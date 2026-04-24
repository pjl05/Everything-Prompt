# Phase 3: 管理后台详细开发计划

**开发周期**: 1-2 周
**目标**: 完成后台完整功能（运营支撑）
**Git 远程**: https://github.com/pjl05/Everything-Prompt.git

---

## 一、功能清单

### 1.1 模块划分

| 模块 | 功能 |
|------|------|
| 模板管理 | 增删改查、上下架、批量操作 |
| 分类管理 | 增删改查、拖拽排序、启用禁用 |
| 用户管理 | 列表、详情、VIP授权、账号禁用 |
| 权限配置 | 功能白名单、会员权益配置 |
| 数据统计 | 访问量、AI调用、会员统计 |

---

## 二、后端新增代码

### 2.1 实体类

**AdminUser VO (扩展)**:
```java
// 使用现有的 SysUser 实体，通过 Mapper 查询时 JOIN ai_call_log 等表获取额外统计
```

### 2.2 Mapper

**AdminMapper.java** (后台管理专用):
```java
package com.everything.prompt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.everything.prompt.entity.SysUser;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdminMapper extends BaseMapper<SysUser> {

    @Select("""
        SELECT u.*,
               (SELECT COUNT(*) FROM ai_call_log WHERE user_id = u.id) as ai_call_count,
               (SELECT MAX(create_time) FROM ai_call_log WHERE user_id = u.id) as last_call_time
        FROM sys_user u
        WHERE #{keyword} IS NULL OR u.username LIKE CONCAT('%', #{keyword}, '%')
        ORDER BY u.create_time DESC
        """)
    Page<SysUser> selectUserPageWithStats(Page<SysUser> page, @Param("keyword") String keyword);
}
```

**StatsMapper.java** (统计数据):
```java
package com.everything.prompt.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface StatsMapper {

    @Select("""
        SELECT
            COUNT(DISTINCT id) as total_users,
            COUNT(CASE WHEN role = 'VIP' THEN 1 END) as vip_users,
            COUNT(CASE WHEN role = 'USER' THEN 1 END) as normal_users,
            COUNT(CASE WHEN create_time >= CURDATE() THEN 1 END) as today_new_users
        FROM sys_user
        WHERE deleted = 0
        """)
    Map<String, Object> getUserStats();

    @Select("""
        SELECT
            COUNT(*) as total_visits,
            COUNT(DISTINCT ip_address) as unique_visitors,
            COUNT(CASE WHEN create_time >= CURDATE() THEN 1 END) as today_visits
        FROM visit_log
        WHERE create_time >= DATE_SUB(NOW(), INTERVAL 7 DAY)
        """)
    Map<String, Object> getVisitStats();

    @Select("""
        SELECT
            COUNT(*) as total_calls,
            SUM(tokens_used) as total_tokens,
            COUNT(CASE WHEN create_time >= CURDATE() THEN 1 END) as today_calls
        FROM ai_call_log
        WHERE create_time >= DATE_SUB(NOW(), INTERVAL 7 DAY)
        """)
    Map<String, Object> getAiStats();

    @Select("""
        SELECT
            DATE(create_time) as date,
            COUNT(*) as count
        FROM visit_log
        WHERE create_time >= DATE_SUB(NOW(), INTERVAL 30 DAY)
        GROUP BY DATE(create_time)
        ORDER BY date
        """)
    java.util.List<Map<String, Object>> getVisitTrend();

    @Select("""
        SELECT
            t.title,
            t.usage_count as count
        FROM prompt_template t
        WHERE t.status = 1
        ORDER BY t.usage_count DESC
        LIMIT 10
        """)
    java.util.List<Map<String, Object>> getTopTemplates();
}
```

### 2.3 服务类

**AdminTemplateService.java**:
```java
package com.everything.prompt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.everything.prompt.entity.PromptTemplate;
import com.everything.prompt.exception.BusinessException;
import com.everything.prompt.mapper.PromptTemplateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminTemplateService {

    private final PromptTemplateMapper templateMapper;

    public List<PromptTemplate> getAllTemplates(String keyword, Integer status) {
        LambdaQueryWrapper<PromptTemplate> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(PromptTemplate::getTitle, keyword);
        }
        if (status != null) {
            wrapper.eq(PromptTemplate::getStatus, status);
        }
        return templateMapper.selectList(wrapper);
    }

    @CacheEvict(value = "templates", allEntries = true)
    public PromptTemplate createTemplate(PromptTemplate template) {
        template.setStatus(1);
        template.setIsOfficial(1);  // 官方创建
        templateMapper.insert(template);
        return template;
    }

    @CacheEvict(value = "templates", allEntries = true)
    public PromptTemplate updateTemplate(PromptTemplate template) {
        if (template.getId() == null) {
            throw new BusinessException(400, "模板ID不能为空");
        }
        templateMapper.updateById(template);
        return templateMapper.selectById(template.getId());
    }

    @CacheEvict(value = "templates", allEntries = true)
    public void deleteTemplate(Long id) {
        templateMapper.deleteById(id);
    }

    @CacheEvict(value = "templates", allEntries = true)
    public void updateStatus(Long id, Integer status) {
        templateMapper.update(null,
            new LambdaQueryWrapper<PromptTemplate>()
                .eq(PromptTemplate::getId, id)
                .set(PromptTemplate::getStatus, status)
        );
    }

    @CacheEvict(value = "templates", allEntries = true)
    public void batchDelete(List<Long> ids) {
        templateMapper.deleteBatchIds(ids);
    }
}
```

**AdminCategoryService.java**:
```java
package com.everything.prompt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.everything.prompt.entity.TemplateCategory;
import com.everything.prompt.mapper.TemplateCategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCategoryService {

    private final TemplateCategoryMapper categoryMapper;

    public List<TemplateCategory> getAllCategories() {
        return categoryMapper.selectList(
            new LambdaQueryWrapper<TemplateCategory>()
                .orderByAsc(TemplateCategory::getSortOrder)
        );
    }

    @CacheEvict(value = "categories", allEntries = true)
    public TemplateCategory createCategory(TemplateCategory category) {
        categoryMapper.insert(category);
        return category;
    }

    @CacheEvict(value = "categories", allEntries = true)
    public TemplateCategory updateCategory(TemplateCategory category) {
        categoryMapper.updateById(category);
        return category;
    }

    @CacheEvict(value = "categories", allEntries = true)
    public void deleteCategory(Long id) {
        categoryMapper.deleteById(id);
    }

    @CacheEvict(value = "categories", allEntries = true)
    public void updateSortOrder(Long id, Integer sortOrder) {
        categoryMapper.update(null,
            new LambdaQueryWrapper<TemplateCategory>()
                .eq(TemplateCategory::getId, id)
                .set(TemplateCategory::getSortOrder, sortOrder)
        );
    }
}
```

**AdminUserService.java**:
```java
package com.everything.prompt.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.everything.prompt.entity.SysUser;
import com.everything.prompt.exception.BusinessException;
import com.everything.prompt.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final SysUserMapper userMapper;

    public Page<SysUser> getUserPage(String keyword, int page, int size) {
        Page<SysUser> pageParam = new Page<>(page, size);
        return userMapper.selectPage(pageParam,
            new LambdaQueryWrapper<SysUser>()
                .like(keyword != null, SysUser::getUsername, keyword)
                .orderByDesc(SysUser::getCreateTime)
        );
    }

    public SysUser getUserDetail(Long id) {
        return userMapper.selectById(id);
    }

    public void updateUserStatus(Long id, Integer status) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        user.setStatus(status);
        userMapper.updateById(user);
    }

    public void grantVip(Long id, Integer days) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        user.setRole("VIP");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireTime = user.getVipExpireTime();

        if (expireTime == null || expireTime.isBefore(now)) {
            expireTime = now.plusDays(days);
        } else {
            expireTime = expireTime.plusDays(days);
        }
        user.setVipExpireTime(expireTime);
        userMapper.updateById(user);
    }
}
```

**StatsService.java**:
```java
package com.everything.prompt.service;

import com.everything.prompt.mapper.StatsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final StatsMapper statsMapper;

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("userStats", statsMapper.getUserStats());
        stats.put("visitStats", statsMapper.getVisitStats());
        stats.put("aiStats", statsMapper.getAiStats());
        stats.put("topTemplates", statsMapper.getTopTemplates());
        return stats;
    }

    public Map<String, Object> getVisitTrend() {
        Map<String, Object> result = new HashMap<>();
        result.put("trend", statsMapper.getVisitTrend());
        return result;
    }
}
```

### 2.4 控制器

**AdminTemplateController.java**:
```java
package com.everything.prompt.controller.admin;

import com.everything.prompt.annotation.Logged;
import com.everything.prompt.annotation.RequireRole;
import com.everything.prompt.entity.PromptTemplate;
import com.everything.prompt.service.AdminTemplateService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/templates")
@RequiredArgsConstructor
@RequireRole({"ADMIN"})
public class AdminTemplateController {

    private final AdminTemplateService adminTemplateService;

    @GetMapping
    public ApiResponse<List<PromptTemplate>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return ApiResponse.success(adminTemplateService.getAllTemplates(keyword, status));
    }

    @GetMapping("/{id}")
    public ApiResponse<PromptTemplate> getById(@PathVariable Long id) {
        return ApiResponse.success(adminTemplateService.getAllTemplates(null, null)
                .stream().filter(t -> t.getId().equals(id)).findFirst().orElse(null));
    }

    @PostMapping
    @Logged("创建模板")
    public ApiResponse<PromptTemplate> create(@RequestBody TemplateRequest request) {
        PromptTemplate template = new PromptTemplate();
        template.setTitle(request.getTitle());
        template.setDescription(request.getDescription());
        template.setContent(request.getContent());
        template.setCategoryId(request.getCategoryId());
        template.setTags(request.getTags());
        template.setIsPremium(request.getIsPremium());
        return ApiResponse.success(adminTemplateService.createTemplate(template));
    }

    @PutMapping("/{id}")
    @Logged("更新模板")
    public ApiResponse<PromptTemplate> update(@PathVariable Long id, @RequestBody TemplateRequest request) {
        PromptTemplate template = new PromptTemplate();
        template.setId(id);
        template.setTitle(request.getTitle());
        template.setDescription(request.getDescription());
        template.setContent(request.getContent());
        template.setCategoryId(request.getCategoryId());
        template.setTags(request.getTags());
        template.setIsPremium(request.getIsPremium());
        return ApiResponse.success(adminTemplateService.updateTemplate(template));
    }

    @DeleteMapping("/{id}")
    @Logged("删除模板")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        adminTemplateService.deleteTemplate(id);
        return ApiResponse.success();
    }

    @PutMapping("/{id}/status")
    @Logged("更新模板状态")
    public ApiResponse<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        adminTemplateService.updateStatus(id, status);
        return ApiResponse.success();
    }

    @PostMapping("/batch-delete")
    @Logged("批量删除模板")
    public ApiResponse<Void> batchDelete(@RequestBody List<Long> ids) {
        adminTemplateService.batchDelete(ids);
        return ApiResponse.success();
    }

    @Data
    public static class TemplateRequest {
        private String title;
        private String description;
        private String content;
        private Long categoryId;
        private String tags;
        private Integer isPremium;
    }
}
```

**AdminCategoryController.java**:
```java
package com.everything.prompt.controller.admin;

import com.everything.prompt.annotation.Logged;
import com.everything.prompt.annotation.RequireRole;
import com.everything.prompt.entity.TemplateCategory;
import com.everything.prompt.service.AdminCategoryService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
@RequireRole({"ADMIN"})
public class AdminCategoryController {

    private final AdminCategoryService categoryService;

    @GetMapping
    public ApiResponse<List<TemplateCategory>> getAll() {
        return ApiResponse.success(categoryService.getAllCategories());
    }

    @PostMapping
    @Logged("创建分类")
    public ApiResponse<TemplateCategory> create(@RequestBody CategoryRequest request) {
        TemplateCategory category = new TemplateCategory();
        category.setName(request.getName());
        category.setCode(request.getCode());
        category.setIcon(request.getIcon());
        category.setDescription(request.getDescription());
        category.setSortOrder(request.getSortOrder());
        category.setIsFree(request.getIsFree());
        return ApiResponse.success(categoryService.createCategory(category));
    }

    @PutMapping("/{id}")
    @Logged("更新分类")
    public ApiResponse<TemplateCategory> update(@PathVariable Long id, @RequestBody CategoryRequest request) {
        TemplateCategory category = new TemplateCategory();
        category.setId(id);
        category.setName(request.getName());
        category.setCode(request.getCode());
        category.setIcon(request.getIcon());
        category.setDescription(request.getDescription());
        category.setSortOrder(request.getSortOrder());
        category.setIsFree(request.getIsFree());
        return ApiResponse.success(categoryService.updateCategory(category));
    }

    @DeleteMapping("/{id}")
    @Logged("删除分类")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ApiResponse.success();
    }

    @PutMapping("/{id}/sort")
    @Logged("更新分类排序")
    public ApiResponse<Void> updateSort(@PathVariable Long id, @RequestParam Integer sortOrder) {
        categoryService.updateSortOrder(id, sortOrder);
        return ApiResponse.success();
    }

    @Data
    public static class CategoryRequest {
        private String name;
        private String code;
        private String icon;
        private String description;
        private Integer sortOrder;
        private Integer isFree;
    }
}
```

**AdminUserController.java**:
```java
package com.everything.prompt.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.everything.prompt.annotation.Logged;
import com.everything.prompt.annotation.RequireRole;
import com.everything.prompt.entity.SysUser;
import com.everything.prompt.service.AdminUserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@RequireRole({"ADMIN"})
public class AdminUserController {

    private final AdminUserService userService;

    @GetMapping
    public ApiResponse<Page<SysUser>> getUserPage(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(userService.getUserPage(keyword, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<SysUser> getUserDetail(@PathVariable Long id) {
        return ApiResponse.success(userService.getUserDetail(id));
    }

    @PutMapping("/{id}/status")
    @Logged("更新用户状态")
    public ApiResponse<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateUserStatus(id, status);
        return ApiResponse.success();
    }

    @PutMapping("/{id}/vip")
    @Logged("授权VIP")
    public ApiResponse<Void> grantVip(@PathVariable Long id, @RequestParam Integer days) {
        userService.grantVip(id, days);
        return ApiResponse.success();
    }
}
```

**AdminStatsController.java**:
```java
package com.everything.prompt.controller.admin;

import com.everything.prompt.annotation.RequireRole;
import com.everything.prompt.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
@RequireRole({"ADMIN"})
public class AdminStatsController {

    private final StatsService statsService;

    @GetMapping("/dashboard")
    public ApiResponse<Map<String, Object>> getDashboard() {
        return ApiResponse.success(statsService.getDashboardStats());
    }

    @GetMapping("/visit-trend")
    public ApiResponse<Map<String, Object>> getVisitTrend() {
        return ApiResponse.success(statsService.getVisitTrend());
    }
}
```

---

## 三、前端新增页面

### 3.1 管理后台布局

**src/layout/admin.vue**:
```vue
<template>
  <div class="admin-layout">
    <!-- 侧边栏 -->
    <aside class="admin-sidebar glass-card">
      <div class="sidebar-header">
        <span class="logo-text text-gradient">管理后台</span>
      </div>
      <el-menu :default-active="activeMenu" router>
        <el-menu-item index="/admin">
          <el-icon><DataAnalysis /></el-icon>
          <span>数据概览</span>
        </el-menu-item>
        <el-menu-item index="/admin/templates">
          <el-icon><Document /></el-icon>
          <span>模板管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/categories">
          <el-icon><Folder /></el-icon>
          <span>分类管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/config">
          <el-icon><Setting /></el-icon>
          <span>权限配置</span>
        </el-menu-item>
      </el-menu>
    </aside>

    <!-- 主内容 -->
    <div class="admin-main">
      <!-- 顶部栏 -->
      <header class="admin-header glass-card">
        <div class="header-left">
          <h2>{{ pageTitle }}</h2>
        </div>
        <div class="header-right">
          <el-dropdown>
            <span class="user-info">
              <el-avatar :src="userStore.userInfo?.avatar" />
              <span>{{ userStore.userInfo?.username }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="userStore.logout()">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- 内容区域 -->
      <div class="admin-content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { DataAnalysis, Document, Folder, User, Setting } from '@element-plus/icons-vue'

const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const pageTitle = computed(() => {
  const map = {
    '/admin': '数据概览',
    '/admin/templates': '模板管理',
    '/admin/categories': '分类管理',
    '/admin/users': '用户管理',
    '/admin/config': '权限配置'
  }
  return map[route.path] || '管理后台'
})
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
}

.admin-sidebar {
  width: 240px;
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  padding: 20px 0;
}

.sidebar-header {
  padding: 0 20px 20px;
  border-bottom: 1px solid rgba(0,0,0,0.1);
}

.admin-main {
  flex: 1;
  margin-left: 240px;
}

.admin-header {
  position: sticky;
  top: 0;
  z-index: 100;
  padding: 16px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.admin-content {
  padding: 24px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}
</style>
```

### 3.2 数据概览页面

**src/views/admin/dashboard.vue**:
```vue
<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card glass-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #667EEA, #764BA2);">
          <User />
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.userStats?.total_users || 0 }}</span>
          <span class="stat-label">总用户数</span>
        </div>
      </div>
      <div class="stat-card glass-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #F6AD55, #ED8936);">
          <Star />
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.userStats?.vip_users || 0 }}</span>
          <span class="stat-label">VIP会员</span>
        </div>
      </div>
      <div class="stat-card glass-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #48BB78, #38A169);">
          <View />
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.visitStats?.total_visits || 0 }}</span>
          <span class="stat-label">总访问量</span>
        </div>
      </div>
      <div class="stat-card glass-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #F56565, #E53E3E);">
          <ChatDotRound />
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.aiStats?.total_calls || 0 }}</span>
          <span class="stat-label">AI调用次数</span>
        </div>
      </div>
    </div>

    <!-- 趋势图表区域 -->
    <div class="chart-section">
      <div class="chart-card glass-card">
        <h3>访问趋势</h3>
        <div class="chart-placeholder">
          <p>访问趋势图表 (ECharts)</p>
        </div>
      </div>
      <div class="chart-card glass-card">
        <h3>热门模板</h3>
        <div class="top-templates">
          <div v-for="(t, i) in stats.topTemplates" :key="i" class="top-item">
            <span class="rank">{{ i + 1 }}</span>
            <span class="name">{{ t.title }}</span>
            <span class="count">{{ t.count }}次</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { User, Star, View, ChatDotRound } from '@element-plus/icons-vue'

const stats = ref({})

onMounted(async () => {
  // 获取统计数据
  // stats.value = await adminApi.getDashboard()
})
</script>

<style scoped>
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  margin-bottom: 24px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 24px;
  gap: 16px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
}

.stat-value {
  display: block;
  font-size: 28px;
  font-weight: 700;
}

.stat-label {
  color: var(--text-muted);
  font-size: 14px;
}

.chart-section {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
}

.chart-card {
  padding: 24px;
}

.chart-placeholder {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f9fa;
  border-radius: 8px;
}

.top-templates {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.top-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
}

.rank {
  width: 24px;
  height: 24px;
  background: var(--color-primary);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
}

.count {
  margin-left: auto;
  color: var(--text-muted);
}
</style>
```

### 3.3 模板管理页面

**src/views/admin/templates.vue**:
```vue
<template>
  <div class="template-admin">
    <div class="page-toolbar">
      <el-input v-model="keyword" placeholder="搜索模板..." style="width: 200px;" />
      <el-select v-model="status" placeholder="状态" clearable style="width: 120px;">
        <el-option label="上架" :value="1" />
        <el-option label="下架" :value="0" />
      </el-select>
      <el-button type="primary" class="btn-gradient" @click="search">搜索</el-button>
      <el-button type="primary" @click="showAddDialog = true">新增模板</el-button>
    </div>

    <el-table :data="templates" stripe class="glass-card">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="categoryName" label="分类" width="120" />
      <el-table-column prop="isPremium" label="类型" width="100">
        <template #default="{ row }">
          <el-tag :type="row.isPremium ? 'warning' : 'success'" size="small">
            {{ row.isPremium ? '付费' : '免费' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="usageCount" label="使用次数" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="toggleStatus(row)" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="editTemplate(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteTemplate(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="showAddDialog" :title="editingTemplate ? '编辑模板' : '新增模板'" width="600px">
      <el-form :model="templateForm" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="templateForm.title" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="templateForm.description" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="templateForm.content" type="textarea" :rows="6" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="templateForm.categoryId">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="标签">
          <el-input v-model="templateForm.tags" placeholder="多个标签用逗号分隔" />
        </el-form-item>
        <el-form-item label="是否付费">
          <el-switch v-model="templateForm.isPremium" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="saveTemplate">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const templates = ref([])
const categories = ref([])
const keyword = ref('')
const status = ref(null)
const showAddDialog = ref(false)
const editingTemplate = ref(null)

const templateForm = ref({
  title: '',
  description: '',
  content: '',
  categoryId: null,
  tags: '',
  isPremium: 0
})

const search = () => {
  // 调用API搜索
}

const editTemplate = (row) => {
  editingTemplate.value = row
  templateForm.value = { ...row }
  showAddDialog.value = true
}

const saveTemplate = () => {
  // 保存
  showAddDialog.value = false
  ElMessage.success('保存成功')
}

const toggleStatus = (row) => {
  // 更新状态
}

const deleteTemplate = async (id) => {
  await ElMessageBox.confirm('确认删除该模板?', '警告', { type: 'warning' })
  // 调用删除API
}

onMounted(() => {
  // 获取数据
})
</script>

<style scoped>
.page-toolbar {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
}
</style>
```

### 3.4 用户管理页面

**src/views/admin/users.vue**:
```vue
<template>
  <div class="user-admin">
    <div class="page-toolbar">
      <el-input v-model="keyword" placeholder="搜索用户..." style="width: 200px;" />
      <el-button type="primary" @click="search">搜索</el-button>
    </div>

    <el-table :data="users" stripe class="glass-card">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column prop="role" label="角色" width="100">
        <template #default="{ row }">
          <el-tag :type="row.role === 'VIP' ? 'warning' : row.role === 'ADMIN' ? 'danger' : 'info'">
            {{ row.role }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="totalAiCalls" label="AI调用" width="100" />
      <el-table-column prop="vipExpireTime" label="VIP到期" width="120" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="grantVip(row)">授权VIP</el-button>
          <el-button size="small" :type="row.status === 1 ? 'danger' : 'success'" @click="toggleStatus(row)">
            {{ row.status === 1 ? '禁用' : '启用' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="page"
      :page-size="size"
      :total="total"
      layout="prev, pager, next"
      @current-change="fetchUsers"
      style="margin-top: 24px; justify-content: center;"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const users = ref([])
const keyword = ref('')
const page = ref(1)
const size = ref(10)
const total = ref(0)

const grantVip = async (user) => {
  await ElMessageBox.prompt('请输入VIP天数', '授权VIP', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /^\d+$/,
    inputErrorMessage: '请输入数字'
  }).then(({ value }) => {
    // 调用API
    ElMessage.success('授权成功')
  })
}

const toggleStatus = (user) => {
  // 切换状态
}

const search = () => {
  page.value = 1
  fetchUsers()
}

const fetchUsers = () => {
  // 获取用户列表
}

onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
.page-toolbar {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
}
</style>
```

---

## 四、Git 提交节点

| 节点 | 提交信息 | 说明 |
|------|----------|------|
| 1 | `feat: add admin entities and mappers` | 管理后台实体和Mapper |
| 2 | `feat: add AdminTemplateService and controller` | 模板管理服务 |
| 3 | `feat: add AdminCategoryService and controller` | 分类管理服务 |
| 4 | `feat: add AdminUserService and controller` | 用户管理服务 |
| 5 | `feat: add StatsService for dashboard` | 统计服务 |
| 6 | `feat: add admin dashboard page` | 数据概览页 |
| 7 | `feat: add admin template management page` | 模板管理页 |
| 8 | `feat: add admin category management page` | 分类管理页 |
| 9 | `feat: add admin user management page` | 用户管理页 |
| 10 | `feat: add admin config page` | 权限配置页 |

```bash
# 提交示例
git add -A && git commit -m "feat: add admin dashboard page"
git push
```

---

## 五、验收标准

- [ ] 管理后台可正常访问
- [ ] 模板CRUD正常
- [ ] 分类管理正常
- [ ] 用户管理正常
- [ ] VIP授权正常
- [ ] 数据统计准确
- [ ] 权限控制严格
- [ ] 代码推送到GitHub
