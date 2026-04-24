# Phase 2: 会员完整版详细开发计划

**开发周期**: 2-3 周
**目标**: 完成付费功能 + 安全防护（变现核心）
**Git 远程**: https://github.com/pjl05/Everything-Prompt.git

---

## 一、功能清单

### 1.1 VIP 权限体系

| 功能 | 游客 | 普通用户 | VIP |
|------|------|---------|-----|
| 查看免费模板 | ✅ | ✅ | ✅ |
| 使用基础生成器 | 5次/日 | 无限 | 无限 |
| AI优化 | ❌ | 10次/日 | 无限 |
| 查看付费模板 | ❌ | ❌ | ✅ |
| 私有词库 | ❌ | ❌ | ✅ |
| 批量生成 | ❌ | ❌ | ✅ |

### 1.2 核心功能
- **深度 AI 优化**: 逻辑补全、专业术语补充、指令拆分、约束强化、语气风格定制
- **私有词库**: 新增/编辑/删除词条、分组管理、云端同步
- **收藏夹**: 收藏/取消收藏、一键复用、云端同步
- **批量生成**: 多版本提示词批量生成、TXT/Markdown 导出

---

## 二、后端新增代码

### 2.1 实体类

**UserWord.java** (私有词库):
```java
package com.everything.prompt.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_word")
public class UserWord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private String title;
    private String content;
    private String groupName;  // 分组
    private Integer isShared;  // 0私有 1公开
    private Integer status;    // 0删除 1正常

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
```

**UserFavorite.java** (收藏):
```java
package com.everything.prompt.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_favorite")
public class UserFavorite {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private Long templateId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
```

**AiCallLog.java** (AI调用记录):
```java
package com.everything.prompt.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("ai_call_log")
public class AiCallLog {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private String callType;   // OPTIMIZE, GENERATE, BATCH
    private String inputContent;
    private String outputContent;
    private String model;
    private Integer tokensUsed;
    private java.math.BigDecimal cost;
    private String ipAddress;
    private Integer status;   // 0失败 1成功
    private String errorMessage;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
```

### 2.2 Mapper

**UserWordMapper.java**:
```java
package com.everything.prompt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.everything.prompt.entity.UserWord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserWordMapper extends BaseMapper<UserWord> {
    // 使用 BaseMapper 默认方法
}
```

**UserFavoriteMapper.java**:
```java
package com.everything.prompt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.everything.prompt.entity.UserFavorite;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserFavoriteMapper extends BaseMapper<UserFavorite> {
}
```

**AiCallLogMapper.java**:
```java
package com.everything.prompt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.everything.prompt.entity.AiCallLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AiCallLogMapper extends BaseMapper<AiCallLog> {
}
```

### 2.3 服务类

**VipService.java** (VIP服务):
```java
package com.everything.prompt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.everything.prompt.entity.SysUser;
import com.everything.prompt.entity.PromptTemplate;
import com.everything.prompt.exception.BusinessException;
import com.everything.prompt.mapper.PromptTemplateMapper;
import com.everything.prompt.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VipService {

    private final SysUserMapper userMapper;
    private final PromptTemplateMapper templateMapper;

    public void upgradeVip(Long userId, Integer durationDays) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireTime = user.getVipExpireTime();

        // 如果已过期或当前时间已过，从现在开始计算
        if (expireTime == null || expireTime.isBefore(now)) {
            expireTime = now.plusDays(durationDays);
        } else {
            // 否则累加
            expireTime = expireTime.plusDays(durationDays);
        }

        user.setRole("VIP");
        user.setVipExpireTime(expireTime);
        userMapper.updateById(user);
    }

    public boolean isVipValid(SysUser user) {
        if (user == null || !"VIP".equals(user.getRole())) {
            return false;
        }
        LocalDateTime expireTime = user.getVipExpireTime();
        return expireTime == null || expireTime.isAfter(LocalDateTime.now());
    }
}
```

**WordService.java** (私有词库):
```java
package com.everything.prompt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.everything.prompt.entity.UserWord;
import com.everything.prompt.exception.BusinessException;
import com.everything.prompt.mapper.UserWordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WordService {

    private final UserWordMapper wordMapper;
    private final StringRedisTemplate redisTemplate;

    public List<UserWord> getUserWords(Long userId) {
        return wordMapper.selectList(
            new LambdaQueryWrapper<UserWord>()
                .eq(UserWord::getUserId, userId)
                .eq(UserWord::getStatus, 1)
                .orderByDesc(UserWord::getCreateTime)
        );
    }

    @CacheEvict(value = "userWords", key = "#userId")
    public UserWord addWord(Long userId, String title, String content, String groupName) {
        UserWord word = new UserWord();
        word.setUserId(userId);
        word.setTitle(title);
        word.setContent(content);
        word.setGroupName(groupName != null ? groupName : "默认分组");
        word.setStatus(1);
        wordMapper.insert(word);
        return word;
    }

    @CacheEvict(value = "userWords", key = "#userId")
    public UserWord updateWord(Long userId, Long wordId, String title, String content, String groupName) {
        UserWord word = wordMapper.selectOne(
            new LambdaQueryWrapper<UserWord>()
                .eq(UserWord::getId, wordId)
                .eq(UserWord::getUserId, userId)
        );
        if (word == null) {
            throw new BusinessException(404, "词条不存在");
        }
        word.setTitle(title);
        word.setContent(content);
        if (groupName != null) {
            word.setGroupName(groupName);
        }
        wordMapper.updateById(word);
        return word;
    }

    @CacheEvict(value = "userWords", key = "#userId")
    public void deleteWord(Long userId, Long wordId) {
        wordMapper.delete(
            new LambdaQueryWrapper<UserWord>()
                .eq(UserWord::getId, wordId)
                .eq(UserWord::getUserId, userId)
        );
    }
}
```

**FavoriteService.java** (收藏):
```java
package com.everything.prompt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.everything.prompt.entity.PromptTemplate;
import com.everything.prompt.entity.UserFavorite;
import com.everything.prompt.exception.BusinessException;
import com.everything.prompt.mapper.PromptTemplateMapper;
import com.everything.prompt.mapper.UserFavoriteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final UserFavoriteMapper favoriteMapper;
    private final PromptTemplateMapper templateMapper;

    public List<PromptTemplate> getUserFavorites(Long userId) {
        // 查询用户的收藏ID
        List<Long> templateIds = favoriteMapper.selectList(
            new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, userId)
        ).stream().map(UserFavorite::getTemplateId).toList();

        if (templateIds.isEmpty()) {
            return List.of();
        }

        // 查询模板详情
        return templateMapper.selectList(
            new LambdaQueryWrapper<PromptTemplate>()
                .in(PromptTemplate::getId, templateIds)
                .eq(PromptTemplate::getStatus, 1)
        );
    }

    public void addFavorite(Long userId, Long templateId) {
        // 检查是否已收藏
        Long count = favoriteMapper.selectCount(
            new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, userId)
                .eq(UserFavorite::getTemplateId, templateId)
        );
        if (count > 0) {
            throw new BusinessException(400, "已收藏过该模板");
        }

        UserFavorite favorite = new UserFavorite();
        favorite.setUserId(userId);
        favorite.setTemplateId(templateId);
        favoriteMapper.insert(favorite);
    }

    public void removeFavorite(Long userId, Long templateId) {
        favoriteMapper.delete(
            new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, userId)
                .eq(UserFavorite::getTemplateId, templateId)
        );
    }

    public boolean isFavorited(Long userId, Long templateId) {
        return favoriteMapper.selectCount(
            new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, userId)
                .eq(UserFavorite::getTemplateId, templateId)
        ) > 0;
    }
}
```

**AiService.java** (AI服务 - MiniMax接入):
```java
package com.everything.prompt.service;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.everything.prompt.entity.AiCallLog;
import com.everything.prompt.exception.BusinessException;
import com.everything.prompt.mapper.AiCallLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiService {

    private final AiCallLogMapper aiCallLogMapper;

    @Value("${ai.minimax.api-key}")
    private String apiKey;

    @Value("${ai.minimax.base-url}")
    private String baseUrl;

    @Value("${ai.minimax.model}")
    private String model;

    @Value("${ai.minimax.timeout}")
    private int timeout;

    /**
     * 异步调用MiniMax进行Prompt优化
     */
    @Async("aiTaskExecutor")
    public CompletableFuture<String> optimizeAsync(Long userId, String content, String ipAddress) {
        String prompt = buildOptimizePrompt(content);
        String response = callMiniMax(prompt);

        // 记录日志
        saveCallLog(userId, "OPTIMIZE", content, response, ipAddress);

        return CompletableFuture.completedFuture(response);
    }

    /**
     * 异步调用MiniMax进行批量生成
     */
    @Async("aiTaskExecutor")
    public CompletableFuture<String> batchGenerateAsync(Long userId, String requirement, int count, String ipAddress) {
        String prompt = buildBatchPrompt(requirement, count);
        String response = callMiniMax(prompt);

        // 记录日志
        saveCallLog(userId, "BATCH", requirement, response, ipAddress);

        return CompletableFuture.completedFuture(response);
    }

    private String callMiniMax(String prompt) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", new Object[]{
                Map.of("role", "user", "content", prompt)
            });

            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + apiKey);
            headers.put("Content-Type", "application/json");

            String response = HttpUtil.createPost(baseUrl + "/v1/text/chatcompletion_v2")
                    .headerMap(headers, false)
                    .timeout(timeout * 1000)
                    .body(JSONUtil.toJsonStr(requestBody))
                    .execute()
                    .body();

            JSONObject jsonResponse = JSONUtil.parseObj(response);
            if (jsonResponse.containsKey("choices")) {
                return jsonResponse.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getStr("content");
            }

            throw new BusinessException(500, "MiniMax API 调用失败");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("MiniMax API 调用异常", e);
            throw new BusinessException(500, "AI 服务暂时不可用，请稍后再试");
        }
    }

    private String buildOptimizePrompt(String content) {
        return String.format("""
            请优化以下提示词，使其更加专业、清晰、有效：

            原始提示词：
            %s

            请从以下方面进行优化：
            1. 逻辑补全
            2. 专业术语补充
            3. 指令拆分
            4. 约束强化
            5. 语气风格定制

            直接返回优化后的提示词，不要添加其他解释。
            """, content);
    }

    private String buildBatchPrompt(String requirement, int count) {
        return String.format("""
            根据以下需求，生成%d个不同版本的AI提示词：

            需求：%s

            要求：
            1. 每个版本要有差异化
            2. 涵盖不同的表达方式和角度
            3. 保持核心目标一致

            直接返回%d个版本的提示词，用编号分隔。
            """, count, requirement, count);
    }

    private void saveCallLog(Long userId, String callType, String input, String output, String ip) {
        try {
            AiCallLog log = new AiCallLog();
            log.setUserId(userId);
            log.setCallType(callType);
            log.setInputContent(input);
            log.setOutputContent(output);
            log.setModel(model);
            log.setIpAddress(ip);
            log.setStatus(1);
            aiCallLogMapper.insert(log);
        } catch (Exception e) {
            AiService.log.error("保存AI调用日志失败", e);
        }
    }
}
```

### 2.4 控制器

**VipController.java**:
```java
package com.everything.prompt.controller;

import com.everything.prompt.annotation.RequireRole;
import com.everything.prompt.entity.SysUser;
import com.everything.prompt.service.VipService;
import com.everything.prompt.util.SecurityUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vip")
@RequiredArgsConstructor
public class VipController {

    private final VipService vipService;

    @PostMapping("/upgrade")
    @RequireRole({"USER", "VIP"})
    public ApiResponse<Void> upgradeVip(@RequestBody UpgradeRequest request) {
        SysUser currentUser = SecurityUtil.getCurrentUser();
        vipService.upgradeVip(currentUser.getId(), request.getDays());
        return ApiResponse.success();
    }

    @GetMapping("/status")
    @RequireRole({"USER", "VIP"})
    public ApiResponse<Boolean> checkVipStatus() {
        SysUser currentUser = SecurityUtil.getCurrentUser();
        return ApiResponse.success(vipService.isVipValid(currentUser));
    }

    @Data
    public static class UpgradeRequest {
        private Integer days;
    }
}
```

**WordController.java**:
```java
package com.everything.prompt.controller;

import com.everything.prompt.annotation.Logged;
import com.everything.prompt.annotation.RequireRole;
import com.everything.prompt.entity.SysUser;
import com.everything.prompt.entity.UserWord;
import com.everything.prompt.service.WordService;
import com.everything.prompt.util.SecurityUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/words")
@RequiredArgsConstructor
public class WordController {

    private final WordService wordService;

    @GetMapping
    @RequireRole({"VIP", "ADMIN"})
    public ApiResponse<List<UserWord>> getMyWords() {
        SysUser user = SecurityUtil.getCurrentUser();
        return ApiResponse.success(wordService.getUserWords(user.getId()));
    }

    @PostMapping
    @RequireRole({"VIP", "ADMIN"})
    @Logged("添加词条")
    public ApiResponse<UserWord> addWord(@RequestBody WordRequest request) {
        SysUser user = SecurityUtil.getCurrentUser();
        return ApiResponse.success(wordService.addWord(user.getId(), request.getTitle(), request.getContent(), request.getGroupName()));
    }

    @PutMapping("/{id}")
    @RequireRole({"VIP", "ADMIN"})
    @Logged("更新词条")
    public ApiResponse<UserWord> updateWord(@PathVariable Long id, @RequestBody WordRequest request) {
        SysUser user = SecurityUtil.getCurrentUser();
        return ApiResponse.success(wordService.updateWord(user.getId(), id, request.getTitle(), request.getContent(), request.getGroupName()));
    }

    @DeleteMapping("/{id}")
    @RequireRole({"VIP", "ADMIN"})
    @Logged("删除词条")
    public ApiResponse<Void> deleteWord(@PathVariable Long id) {
        SysUser user = SecurityUtil.getCurrentUser();
        wordService.deleteWord(user.getId(), id);
        return ApiResponse.success();
    }

    @Data
    public static class WordRequest {
        private String title;
        private String content;
        private String groupName;
    }
}
```

**FavoriteController.java**:
```java
package com.everything.prompt.controller;

import com.everything.prompt.annotation.Logged;
import com.everything.prompt.annotation.RequireRole;
import com.everything.prompt.entity.PromptTemplate;
import com.everything.prompt.entity.SysUser;
import com.everything.prompt.service.FavoriteService;
import com.everything.prompt.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping
    @RequireRole({"USER", "VIP", "ADMIN"})
    public ApiResponse<List<PromptTemplate>> getMyFavorites() {
        SysUser user = SecurityUtil.getCurrentUser();
        return ApiResponse.success(favoriteService.getUserFavorites(user.getId()));
    }

    @PostMapping("/{templateId}")
    @RequireRole({"USER", "VIP", "ADMIN"})
    @Logged("添加收藏")
    public ApiResponse<Void> addFavorite(@PathVariable Long templateId) {
        SysUser user = SecurityUtil.getCurrentUser();
        favoriteService.addFavorite(user.getId(), templateId);
        return ApiResponse.success();
    }

    @DeleteMapping("/{templateId}")
    @RequireRole({"USER", "VIP", "ADMIN"})
    @Logged("取消收藏")
    public ApiResponse<Void> removeFavorite(@PathVariable Long templateId) {
        SysUser user = SecurityUtil.getCurrentUser();
        favoriteService.removeFavorite(user.getId(), templateId);
        return ApiResponse.success();
    }
}
```

**AiController.java** (AI 优化和批量生成):
```java
package com.everything.prompt.controller;

import com.everything.prompt.annotation.Logged;
import com.everything.prompt.annotation.RequireRole;
import com.everything.prompt.annotation.RateLimit;
import com.everything.prompt.entity.SysUser;
import com.everything.prompt.service.AiService;
import com.everything.prompt.service.VipService;
import com.everything.prompt.util.IpUtil;
import com.everything.prompt.util.SecurityUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;
    private final VipService vipService;

    @PostMapping("/optimize")
    @RequireRole({"USER", "VIP", "ADMIN"})
    @RateLimit(limit = 10, period = 60, type = "user")
    @Logged("AI优化")
    public ApiResponse<CompletableFuture<String>> optimize(@RequestBody OptimizeRequest request, HttpServletRequest httpRequest) {
        SysUser user = SecurityUtil.getCurrentUser();
        String ip = IpUtil.getIpAddress(httpRequest);

        // VIP用户无限制，普通用户10次/分钟
        if (!vipService.isVipValid(user)) {
            // 普通用户限制检查已在@RateLimit中处理
        }

        CompletableFuture<String> result = aiService.optimizeAsync(user.getId(), request.getContent(), ip);
        return ApiResponse.success(result);
    }

    @PostMapping("/batch")
    @RequireRole({"VIP", "ADMIN"})
    @RateLimit(limit = 5, period = 60, type = "user")
    @Logged("批量生成")
    public ApiResponse<CompletableFuture<String>> batchGenerate(@RequestBody BatchRequest request, HttpServletRequest httpRequest) {
        SysUser user = SecurityUtil.getCurrentUser();
        String ip = IpUtil.getIpAddress(httpRequest);

        CompletableFuture<String> result = aiService.batchGenerateAsync(user.getId(), request.getRequirement(), request.getCount(), ip);
        return ApiResponse.success(result);
    }

    @Data
    public static class OptimizeRequest {
        private String content;
    }

    @Data
    public static class BatchRequest {
        private String requirement;
        private Integer count = 5;
    }
}
```

### 2.5 配置类

**AsyncConfig.java** (异步配置):
```java
package com.everything.prompt.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig {

    @Bean("aiTaskExecutor")
    public Executor aiTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("ai-async-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
```

**CacheConfig.java** (Caffeine二级缓存):
```java
package com.everything.prompt.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    @Primary
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(500)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .recordStats());
        return cacheManager;
    }
}
```

---

## 三、前端新增页面

### 3.1 VIP 模板页面

**src/views/vip/templates.vue**:
```vue
<template>
  <div class="vip-templates-page">
    <div class="page-header">
      <h1>高级模板</h1>
      <p>解锁专属VIP模板，释放AI全部潜力</p>
    </div>

    <div class="template-grid">
      <div v-for="template in templates" :key="template.id" class="template-card glass-card" @click="viewDetail(template.id)">
        <div class="vip-badge">VIP专属</div>
        <h3>{{ template.title }}</h3>
        <p>{{ template.description }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const templates = ref([])

const viewDetail = (id) => {
  // 导航到详情页
}

onMounted(() => {
  // 获取VIP模板
})
</script>

<style scoped>
.vip-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: linear-gradient(135deg, #F6AD55, #ED8936);
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}
</style>
```

### 3.2 私有词库页面

**src/views/word/list.vue**:
```vue
<template>
  <div class="word-list-page">
    <div class="page-header">
      <h1>私有词库</h1>
      <el-button type="primary" @click="showAddDialog = true">添加词条</el-button>
    </div>

    <!-- 分组标签 -->
    <div class="group-tabs">
      <el-tag v-for="group in groups" :key="group" :type="activeGroup === group ? 'primary' : 'info'" @click="activeGroup = group">
        {{ group }}
      </el-tag>
    </div>

    <!-- 词条列表 -->
    <div class="word-list">
      <div v-for="word in filteredWords" :key="word.id" class="word-card glass-card">
        <h3>{{ word.title }}</h3>
        <p>{{ word.content }}</p>
        <div class="word-actions">
          <el-button size="small" @click="useWord(word)">使用</el-button>
          <el-button size="small" @click="editWord(word)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteWord(word.id)">删除</el-button>
        </div>
      </div>
    </div>

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="showAddDialog" :title="editingWord ? '编辑词条' : '添加词条'">
      <el-form :model="wordForm">
        <el-form-item label="标题">
          <el-input v-model="wordForm.title" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="wordForm.content" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="分组">
          <el-input v-model="wordForm.groupName" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="saveWord">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'

const words = ref([])
const activeGroup = ref('全部')
const showAddDialog = ref(false)
const editingWord = ref(null)

const wordForm = ref({
  title: '',
  content: '',
  groupName: '默认分组'
})

const groups = computed(() => {
  const set = new Set(words.value.map(w => w.groupName))
  return ['全部', ...set]
})

const filteredWords = computed(() => {
  if (activeGroup.value === '全部') return words.value
  return words.value.filter(w => w.groupName === activeGroup.value)
})

const saveWord = () => {
  // 保存词条
  showAddDialog.value = false
  ElMessage.success('保存成功')
}
</script>
```

### 3.3 收藏页面

**src/views/favorite/list.vue**:
```vue
<template>
  <div class="favorite-list-page">
    <div class="page-header">
      <h1>我的收藏</h1>
    </div>

    <div v-if="favorites.length === 0" class="empty-state">
      <p>暂无收藏的模板</p>
      <el-button type="primary" @click="$router.push('/templates')">去逛逛</el-button>
    </div>

    <div v-else class="template-grid">
      <div v-for="template in favorites" :key="template.id" class="template-card glass-card">
        <h3>{{ template.title }}</h3>
        <p>{{ template.description }}</p>
        <div class="card-footer">
          <el-button size="small" type="primary" @click="useTemplate(template)">使用</el-button>
          <el-button size="small" @click="removeFavorite(template.id)">取消收藏</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const favorites = ref([])

const removeFavorite = async (id) => {
  // 调用API取消收藏
}

const useTemplate = (template) => {
  // 导航到使用页面
}

onMounted(() => {
  // 获取收藏列表
})
</script>
```

### 3.4 批量生成页面

**src/views/batch/index.vue**:
```vue
<template>
  <div class="batch-page">
    <div class="page-header">
      <h1>批量生成</h1>
      <p>一次生成多个版本的提示词</p>
    </div>

    <div class="batch-form glass-card">
      <el-form label-position="top">
        <el-form-item label="需求描述">
          <el-input v-model="form.requirement" type="textarea" :rows="4" placeholder="描述你希望AI完成的任务..." />
        </el-form-item>
        <el-form-item label="生成数量">
          <el-slider v-model="form.count" :min="3" :max="10" :marks="{3: '3', 5: '5', 10: '10'}" />
        </el-form-item>
        <el-button type="primary" class="btn-gradient" :loading="loading" @click="generate">
          开始生成
        </el-button>
      </el-form>
    </div>

    <!-- 结果展示 -->
    <div v-if="results.length > 0" class="batch-results">
      <h2>生成结果</h2>
      <div v-for="(result, index) in results" :key="index" class="result-item glass-card">
        <div class="result-header">
          <span>版本 {{ index + 1 }}</span>
          <el-button size="small" @click="copyResult(result)">复制</el-button>
        </div>
        <pre>{{ result }}</pre>
      </div>
      <el-button class="btn-gradient" @click="exportAll">导出全部</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const form = ref({
  requirement: '',
  count: 5
})
const loading = ref(false)
const results = ref([])

const generate = async () => {
  loading.value = true
  try {
    // 调用API
    await new Promise(resolve => setTimeout(resolve, 2000))
    results.value = ['版本1内容...', '版本2内容...', '版本3内容...']
  } finally {
    loading.value = false
  }
}

const copyResult = async (content) => {
  await navigator.clipboard.writeText(content)
  ElMessage.success('已复制')
}

const exportAll = () => {
  // 导出为Markdown
}
</script>
```

---

## 四、Git 提交节点

| 节点 | 提交信息 | 说明 |
|------|----------|------|
| 1 | `feat: add UserWord, UserFavorite, AiCallLog entities` | 实体类 |
| 2 | `feat: add VIP service and controller` | VIP服务 |
| 3 | `feat: add WordService for private word library` | 私有词库服务 |
| 4 | `feat: add FavoriteService for favorites` | 收藏服务 |
| 5 | `feat: add AiService with MiniMax integration` | AI服务 |
| 6 | `feat: add async config and Caffeine cache` | 异步和缓存配置 |
| 7 | `feat: add VIP templates page` | VIP模板页 |
| 8 | `feat: add private word library page` | 私有词库页 |
| 9 | `feat: add favorites page` | 收藏页 |
| 10 | `feat: add batch generation page` | 批量生成页 |
| 11 | `feat: add rate limiting for AI calls` | AI限流 |

```bash
# 提交示例
git add -A && git commit -m "feat: add AiService with MiniMax integration"
git push
```

---

## 五、验收标准

- [ ] VIP用户可解锁高级模板
- [ ] AI优化功能正常工作
- [ ] 私有词库CRUD正常
- [ ] 收藏功能正常
- [ ] 批量生成功能正常
- [ ] Redis限流正常工作
- [ ] 防重提交正常工作
- [ ] 超时降级正常
- [ ] 代码推送到GitHub
