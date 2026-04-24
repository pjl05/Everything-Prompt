# Phase 1: MVP 基础版详细开发计划

**开发周期**: 2-3 周
**目标**: 完成公共模块 + 账号体系 + 核心免费功能
**Git 远程**: https://github.com/pjl05/Everything-Prompt.git

---

## 一、项目初始化

### 1.1 项目结构

```
Everything-Prompt/
├── backend/                    # SpringBoot 后端
│   └── src/main/java/com/everything/prompt/
│       ├── config/            # 配置类
│       ├── controller/        # 控制器
│       ├── service/           # 服务层
│       ├── mapper/            # 数据访问层
│       ├── entity/            # 实体类
│       ├── dto/               # 数据传输对象
│       ├── annotation/        # 自定义注解 (AOP)
│       ├── aspect/            # 切面类
│       ├── filter/            # 过滤器
│       ├── interceptor/       # 拦截器
│       └── exception/         # 异常处理
├── frontend/                  # Vue3 前端
│   └── src/
│       ├── api/               # API 请求
│       ├── views/             # 页面
│       ├── components/        # 组件
│       ├── router/            # 路由
│       └── stores/            # 状态管理
└── docs/                      # 文档
```

### 1.2 后端初始化 [代码生成后提交]

**提交节点**: `feat: initialize SpringBoot backend project`

```bash
# 创建后端项目
mkdir -p backend && cd backend
# 创建 pom.xml
```

**pom.xml 完整内容**:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.3</version>
    </parent>

    <groupId>com.everything</groupId>
    <artifactId>everything-prompt-backend</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <properties>
        <java.version>17</java.version>
        <mybatis-plus.version>3.5.5</mybatis-plus.version>
        <jwt.version>0.11.5</jwt.version>
        <hutool.version>5.8.25</hutool.version>
    </properties>

    <dependencies>
        <!-- SpringBoot Web -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- SpringBoot Security -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>

        <!-- JWT -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>${jwt.version}</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>${jwt.version}</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>${jwt.version}</version>
            <scope>runtime</scope>
        </dependency>

        <!-- MyBatis-Plus -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
            <version>${mybatis-plus.version}</version>
        </dependency>

        <!-- Redis -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-pool2</artifactId>
        </dependency>

        <!-- Caffeine 二级缓存 -->
        <dependency>
            <groupId>com.github.ben-manes.caffeine</groupId>
            <artifactId>caffeine</artifactId>
        </dependency>

        <!-- MySQL -->
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
        </dependency>

        <!-- Hutool 工具库 -->
        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
            <version>${hutool.version}</version>
        </dependency>

        <!-- Validation -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- AOP -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

**application.yml**:
```yaml
server:
  port: 8080

spring:
  application:
    name: everything-prompt
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/everything_prompt?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: ${DB_PASSWORD:password}
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD:}
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0

mybatis-plus:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.everything.prompt.entity
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

jwt:
  secret: ${JWT_SECRET:EverythingPrompt2024SecretKeyMustBe32CharsMin!}
  expiration: 604800000  # 7天

ai:
  minimax:
    api-key: ${MINIMAX_API_KEY:}
    base-url: https://api.minimax.chat
    model: abab6.5s-chat
    timeout: 30

logging:
  level:
    com.everything.prompt: debug
```

### 1.3 前端初始化 [代码生成后提交]

**提交节点**: `feat: initialize Vue3 frontend project`

```bash
# 创建前端项目
npm create vite@latest frontend -- --template vue
cd frontend
npm install
npm install vue-router@4 pinia axios element-plus @element-plus/icons-vue
```

**前端设计风格** (参考 art-design-pro, Ares Admin):

```css
/* src/assets/styles/variables.css - 全局样式变量 */
:root {
  /* 品牌色 - 渐变紫 */
  --color-primary: #667EEA;
  --color-primary-light: #818CF8;
  --color-primary-dark: #5A67D8;

  /* 渐变背景 */
  --gradient-primary: linear-gradient(135deg, #667EEA 0%, #764BA2 100%);
  --gradient-hero: linear-gradient(135deg, #667EEA 0%, #764BA2 50%, #F093FB 100%);
  --gradient-card: linear-gradient(180deg, rgba(255,255,255,0.1) 0%, rgba(255,255,255,0) 100%);

  /* 玻璃态效果 */
  --glass-bg: rgba(255, 255, 255, 0.8);
  --glass-border: rgba(255, 255, 255, 0.3);
  --glass-shadow: 0 8px 32px rgba(102, 126, 234, 0.15);

  /* 暗色文字 */
  --text-primary: #1A202C;
  --text-secondary: #4A5568;
  --text-muted: #A0AEC0;

  /* 间距 */
  --spacing-xs: 4px;
  --spacing-sm: 8px;
  --spacing-md: 16px;
  --spacing-lg: 24px;
  --spacing-xl: 32px;
  --spacing-2xl: 48px;

  /* 圆角 */
  --radius-sm: 8px;
  --radius-md: 12px;
  --radius-lg: 16px;
  --radius-xl: 24px;

  /* 阴影 */
  --shadow-sm: 0 2px 8px rgba(0, 0, 0, 0.08);
  --shadow-md: 0 4px 16px rgba(0, 0, 0, 0.12);
  --shadow-lg: 0 8px 32px rgba(0, 0, 0, 0.16);
}
```

---

## 二、后端核心代码

### 2.1 启动类

**文件**: `backend/src/main/java/com/everything/prompt/EverythingPromptApplication.java`

```java
package com.everything.prompt;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan("com.everything.prompt.mapper")
@EnableAsync
public class EverythingPromptApplication {
    public static void main(String[] args) {
        SpringApplication.run(EverythingPromptApplication.class, args);
    }
}
```

### 2.2 实体类

**SysUser.java**:
```java
package com.everything.prompt.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class SysUser {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;
    private String password;
    private String email;
    private String phone;
    private String nickname;
    private String avatar;

    @TableField("`role`")
    private String role;  // GUEST, USER, VIP, ADMIN

    private Integer status;  // 0禁用 1正常

    private LocalDateTime lastLoginTime;
    private String lastLoginIp;
    private Integer totalAiCalls;
    private LocalDateTime vipExpireTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
```

**TemplateCategory.java**:
```java
package com.everything.prompt.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("template_category")
public class TemplateCategory {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private String code;
    private String icon;
    private String description;
    private Integer sortOrder;
    private Integer isFree;  // 0付费 1免费
    private Integer status;  // 0禁用 1启用

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
```

**PromptTemplate.java**:
```java
package com.everything.prompt.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("prompt_template")
public class PromptTemplate {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;
    private String description;
    private String content;
    private Long categoryId;
    private String tags;
    private Integer isPremium;  // 0免费 1付费
    private Integer isOfficial; // 0用户 1官方
    private Integer usageCount;
    private Double rating;
    private Integer status;    // 0下架 1上架
    private Long createUserId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
```

### 2.3 Mapper 层

**SysUserMapper.java**:
```java
package com.everything.prompt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.everything.prompt.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
```

**TemplateCategoryMapper.java**:
```java
package com.everything.prompt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.everything.prompt.entity.TemplateCategory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TemplateCategoryMapper extends BaseMapper<TemplateCategory> {
}
```

**PromptTemplateMapper.java**:
```java
package com.everything.prompt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.everything.prompt.entity.PromptTemplate;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PromptTemplateMapper extends BaseMapper<PromptTemplate> {
}
```

### 2.4 AOP 注解设计

**@RequireRole 注解**:
```java
package com.everything.prompt.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireRole {
    String[] value();  // 允许的角色数组
}
```

**@RateLimit 注解**:
```java
package com.everything.prompt.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {
    int limit() default 10;           // 限制次数
    int period() default 60;        // 时间周期(秒)
    String type() default "user";    // 限流类型: user/ip/guest
}
```

**@Logged 注解** (记录操作日志):
```java
package com.everything.prompt.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Logged {
    String value() default "";  // 操作描述
}
```

### 2.5 切面类

**RoleCheckAspect.java** (角色校验切面):
```java
package com.everything.prompt.aspect;

import com.everything.prompt.annotation.RequireRole;
import com.everything.prompt.entity.SysUser;
import com.everything.prompt.exception.BusinessException;
import com.everything.prompt.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class RoleCheckAspect {

    @Before("@annotation(requireRole)")
    public void checkRole(JoinPoint point, RequireRole requireRole) {
        SysUser currentUser = SecurityUtil.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException(401, "请先登录");
        }

        String userRole = currentUser.getRole();
        boolean hasRole = Arrays.asList(requireRole.value()).contains(userRole);

        if (!hasRole && !userRole.equals("ADMIN")) {
            log.warn("用户 {} 权限不足，角色: {}，需要: {}", currentUser.getUsername(), userRole, Arrays.toString(requireRole.value()));
            throw new BusinessException(403, "权限不足");
        }
    }
}
```

**RateLimitAspect.java** (限流切面):
```java
package com.everything.prompt.aspect;

import com.everything.prompt.annotation.RateLimit;
import com.everything.prompt.exception.BusinessException;
import com.everything.prompt.util.IpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class RateLimitAspect {

    private final StringRedisTemplate redisTemplate;

    @Before("@annotation(rateLimit)")
    public void checkRateLimit(JoinPoint point, RateLimit rateLimit) {
        HttpServletRequest request = getRequest();
        String key = generateKey(request, rateLimit.type());

        Long current = redisTemplate.opsForValue().increment(key);
        if (current == null) {
            current = 1L;
            redisTemplate.opsForValue().set(key, "1", rateLimit.period(), TimeUnit.SECONDS);
        }

        if (current > rateLimit.limit()) {
            log.warn("触发限流: key={}, current={}, limit={}", key, current, rateLimit.limit());
            throw new BusinessException(429, "请求过于频繁，请稍后再试");
        }
    }

    private String generateKey(HttpServletRequest request, String type) {
        String value = switch (type) {
            case "ip" -> IpUtil.getIpAddress(request);
            case "user" -> request.getHeader("X-User-Id");
            default -> IpUtil.getIpAddress(request);
        };
        return String.format("ratelimit:%s:%s", type, value);
    }

    private HttpServletRequest getRequest() {
        return ((HttpServletRequest) org.springframework.web.context.request.RequestContextHolder.getRequestAttributes()).resolveRequestAttribute("javax.servlet.http.HttpServletRequest");
    }
}
```

### 2.6 核心服务类

**AuthService.java**:
```java
package com.everything.prompt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.everything.prompt.entity.SysUser;
import com.everything.prompt.exception.BusinessException;
import com.everything.prompt.mapper.SysUserMapper;
import com.everything.prompt.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final SysUserMapper userMapper;
    private final StringRedisTemplate redisTemplate;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Map<String, Object> register(String username, String password, String email) {
        // 检查用户名是否存在
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(400, "用户名已存在");
        }

        // 创建用户
        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setRole("USER");
        user.setStatus(1);
        userMapper.insert(user);

        return generateToken(user);
    }

    public Map<String, Object> login(String username, String password) {
        SysUser user = userMapper.selectOne(
            new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username)
        );

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        if (user.getStatus() == 0) {
            throw new BusinessException(401, "账号已被禁用");
        }

        return generateToken(user);
    }

    public void logout(String token) {
        // 将token加入黑名单
        redisTemplate.opsForValue().set("jwt:blacklist:" + token, "1", 7, TimeUnit.DAYS);
    }

    public SysUser getCurrentUser(String token) {
        Long userId = JwtUtil.getUserIdFromToken(token);
        return userMapper.selectById(userId);
    }

    private Map<String, Object> generateToken(SysUser user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("username", user.getUsername());
        claims.put("role", user.getRole());

        String token = JwtUtil.generateToken(claims);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        return result;
    }
}
```

**TemplateService.java**:
```java
package com.everything.prompt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.everything.prompt.entity.PromptTemplate;
import com.everything.prompt.entity.TemplateCategory;
import com.everything.prompt.exception.BusinessException;
import com.everything.prompt.mapper.PromptTemplateMapper;
import com.everything.prompt.mapper.TemplateCategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private final PromptTemplateMapper templateMapper;
    private final TemplateCategoryMapper categoryMapper;

    @Cacheable(value = "categories", key = "'all'")
    public List<TemplateCategory> getAllCategories() {
        return categoryMapper.selectList(
            new LambdaQueryWrapper<TemplateCategory>()
                .eq(TemplateCategory::getStatus, 1)
                .orderByAsc(TemplateCategory::getSortOrder)
        );
    }

    public Page<PromptTemplate> getTemplates(Long categoryId, String keyword, int page, int size) {
        Page<PromptTemplate> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<PromptTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PromptTemplate::getStatus, 1);

        if (categoryId != null) {
            wrapper.eq(PromptTemplate::getCategoryId, categoryId);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(PromptTemplate::getTitle, keyword)
                   .or()
                   .like(PromptTemplate::getDescription, keyword);
        }

        wrapper.orderByDesc(PromptTemplate::getUsageCount);
        return templateMapper.selectPage(pageParam, wrapper);
    }

    public PromptTemplate getTemplateById(Long id) {
        PromptTemplate template = templateMapper.selectById(id);
        if (template == null) {
            throw new BusinessException(404, "模板不存在");
        }
        // 增加使用次数
        templateMapper.update(null,
            new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<PromptTemplate>()
                .eq(PromptTemplate::getId, id)
                .setSql("usage_count = usage_count + 1")
        );
        return template;
    }
}
```

### 2.7 控制器

**AuthController.java**:
```java
package com.everything.prompt.controller;

import com.everything.prompt.annotation.Logged;
import com.everything.prompt.service.AuthService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Logged("用户注册")
    public ApiResponse<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        return ApiResponse.success(authService.register(
            request.getUsername(),
            request.getPassword(),
            request.getEmail()
        ));
    }

    @PostMapping("/login")
    @Logged("用户登录")
    public ApiResponse<Map<String, Object>> login(@RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request.getUsername(), request.getPassword()));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token.replace("Bearer ", ""));
        return ApiResponse.success();
    }

    @GetMapping("/me")
    public ApiResponse<Object> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return ApiResponse.success(null);
        }
        return ApiResponse.success(authService.getCurrentUser(token.replace("Bearer ", "")));
    }

    @Data
    public static class RegisterRequest {
        private String username;
        private String password;
        private String email;
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }
}
```

**TemplateController.java**:
```java
package com.everything.prompt.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.everything.prompt.entity.PromptTemplate;
import com.everything.prompt.entity.TemplateCategory;
import com.everything.prompt.service.TemplateService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateService templateService;

    @GetMapping("/categories")
    public ApiResponse<List<TemplateCategory>> getCategories() {
        return ApiResponse.success(templateService.getAllCategories());
    }

    @GetMapping("/templates")
    public ApiResponse<Page<PromptTemplate>> getTemplates(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(templateService.getTemplates(categoryId, keyword, page, size));
    }

    @GetMapping("/templates/{id}")
    public ApiResponse<PromptTemplate> getTemplate(@PathVariable Long id) {
        return ApiResponse.success(templateService.getTemplateById(id));
    }
}
```

### 2.8 统一响应和异常处理

**ApiResponse.java**:
```java
package com.everything.prompt.controller;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(200);
        response.setMessage("success");
        response.setData(data);
        return response;
    }

    public static <T> ApiResponse<T> success() {
        return success(null);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }
}
```

**GlobalExceptionHandler.java**:
```java
package com.everything.prompt.exception;

import com.everything.prompt.controller.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return ApiResponse.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return ApiResponse.error(500, "系统繁忙，请稍后再试");
    }
}
```

**BusinessException.java**:
```java
package com.everything.prompt.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
}
```

### 2.9 工具类

**JwtUtil.java**:
```java
package com.everything.prompt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return Long.valueOf(claims.get("userId").toString());
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}
```

**SecurityUtil.java**:
```java
package com.everything.prompt.util;

import com.everything.prompt.entity.SysUser;

public class SecurityUtil {

    private static final ThreadLocal<SysUser> userThreadLocal = new ThreadLocal<>();

    public static void setCurrentUser(SysUser user) {
        userThreadLocal.set(user);
    }

    public static SysUser getCurrentUser() {
        return userThreadLocal.get();
    }

    public static void remove() {
        userThreadLocal.remove();
    }
}
```

**IpUtil.java**:
```java
package com.everything.prompt.util;

import jakarta.servlet.http.HttpServletRequest;

public class IpUtil {

    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }
}
```

---

## 三、前端核心代码

### 3.1 主入口和样式

**main.js**:
```javascript
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import './assets/styles/index.css'

const app = createApp(App)

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

app.use(createPinia())
app.use(router)
app.use(ElementPlus, { locale: zhCn })

app.mount('#app')
```

**src/assets/styles/index.css**:
```css
/* 全局样式 */
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

:root {
    /* 品牌渐变色 */
    --color-primary: #667EEA;
    --color-primary-light: #818CF8;
    --color-primary-dark: #5A67D8;

    /* 渐变背景 */
    --gradient-brand: linear-gradient(135deg, #667EEA 0%, #764BA2 100%);
    --gradient-hero: linear-gradient(135deg, #667EEA 0%, #764BA2 50%, #F093FB 100%);
    --gradient-card: linear-gradient(180deg, rgba(255,255,255,0.95) 0%, rgba(255,255,255,0.8) 100%);

    /* 玻璃态 */
    --glass-bg: rgba(255, 255, 255, 0.85);
    --glass-border: rgba(255, 255, 255, 0.4);
    --glass-shadow: 0 8px 32px rgba(102, 126, 234, 0.2);

    /* 文字 */
    --text-primary: #1A202C;
    --text-secondary: #4A5568;
    --text-muted: #A0AEC0;

    /* 圆角 */
    --radius-sm: 8px;
    --radius-md: 12px;
    --radius-lg: 16px;
    --radius-xl: 24px;

    /* 阴影 */
    --shadow-sm: 0 2px 8px rgba(0, 0, 0, 0.06);
    --shadow-md: 0 4px 16px rgba(0, 0, 0, 0.1);
    --shadow-lg: 0 8px 32px rgba(0, 0, 0, 0.12);
    --shadow-glow: 0 0 40px rgba(102, 126, 234, 0.3);
}

body {
    font-family: 'Inter', 'PingFang SC', 'Microsoft YaHei', sans-serif;
    background: linear-gradient(135deg, #F7FAFC 0%, #EDF2F7 100%);
    min-height: 100vh;
    color: var(--text-primary);
}

/* 玻璃态卡片 */
.glass-card {
    background: var(--glass-bg);
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
    border: 1px solid var(--glass-border);
    border-radius: var(--radius-lg);
    box-shadow: var(--glass-shadow);
}

/* 渐变按钮 */
.btn-gradient {
    background: var(--gradient-brand);
    color: white;
    border: none;
    padding: 12px 24px;
    border-radius: var(--radius-md);
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.btn-gradient:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
}

/* 渐变文字 */
.text-gradient {
    background: var(--gradient-brand);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
}

/* 容器 */
.container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 24px;
}
```

### 3.2 API 封装

**src/api/request.js**:
```javascript
import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'
import { useUserStore } from '../stores/user'

const request = axios.create({
    baseURL: '/api',
    timeout: 30000
})

// 请求拦截器
request.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token')
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`
        }
        return config
    },
    error => {
        return Promise.reject(error)
    }
)

// 响应拦截器
request.interceptors.response.use(
    response => {
        const res = response.data
        if (res.code !== 200) {
            ElMessage.error(res.message || '请求失败')
            if (res.code === 401) {
                localStorage.removeItem('token')
                router.push('/login')
            }
            return Promise.reject(new Error(res.message))
        }
        return res.data
    },
    error => {
        ElMessage.error(error.message || '网络错误')
        return Promise.reject(error)
    }
)

export default request
```

**src/api/auth.js**:
```javascript
import request from './request'

export const authApi = {
    register(data) {
        return request.post('/auth/register', data)
    },
    login(data) {
        return request.post('/auth/login', data)
    },
    logout() {
        return request.post('/auth/logout')
    },
    getCurrentUser() {
        return request.get('/auth/me')
    }
}
```

**src/api/template.js**:
```javascript
import request from './request'

export const templateApi = {
    getCategories() {
        return request.get('/categories')
    },
    getTemplates(params) {
        return request.get('/templates', { params })
    },
    getTemplateById(id) {
        return request.get(`/templates/${id}`)
    }
}
```

### 3.3 状态管理

**src/stores/user.js**:
```javascript
import { defineStore } from 'pinia'
import { authApi } from '../api/auth'

export const useUserStore = defineStore('user', {
    state: () => ({
        token: localStorage.getItem('token') || '',
        userInfo: null
    }),

    getters: {
        isLoggedIn: state => !!state.token,
        isVip: state => state.userInfo?.role === 'VIP',
        isAdmin: state => state.userInfo?.role === 'ADMIN'
    },

    actions: {
        async login(credentials) {
            const result = await authApi.login(credentials)
            this.token = result.token
            this.userInfo = result.user
            localStorage.setItem('token', this.token)
            return result
        },

        async register(data) {
            const result = await authApi.register(data)
            this.token = result.token
            this.userInfo = result.user
            localStorage.setItem('token', this.token)
            return result
        },

        async fetchUserInfo() {
            if (!this.token) return
            try {
                this.userInfo = await authApi.getCurrentUser()
            } catch (e) {
                this.logout()
            }
        },

        logout() {
            this.token = ''
            this.userInfo = null
            localStorage.removeItem('token')
        }
    }
})
```

### 3.4 路由配置

**src/router/index.js**:
```javascript
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
    {
        path: '/',
        component: () => import('../views/home/index.vue')
    },
    {
        path: '/templates',
        component: () => import('../views/template/list.vue')
    },
    {
        path: '/templates/:id',
        component: () => import('../views/template/detail.vue')
    },
    {
        path: '/generator',
        component: () => import('../views/generator/index.vue')
    },
    {
        path: '/login',
        component: () => import('../views/auth/login.vue')
    },
    {
        path: '/register',
        component: () => import('../views/auth/register.vue')
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
```

### 3.5 页面组件

**App.vue**:
```vue
<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup>
</script>

<style>
#app {
  min-height: 100vh;
}
</style>
```

**src/views/home/index.vue** (首页 - 渐变玻璃态设计):
```vue
<template>
  <div class="home">
    <!-- 导航栏 -->
    <nav class="navbar glass-card">
      <div class="nav-container">
        <div class="logo">
          <span class="logo-icon">✨</span>
          <span class="logo-text text-gradient">Everything-Prompt</span>
        </div>
        <div class="nav-links">
          <a href="/" class="nav-link">首页</a>
          <a href="/templates" class="nav-link">模板广场</a>
          <a href="/generator" class="nav-link">Prompt生成器</a>
        </div>
        <div class="nav-auth">
          <el-button v-if="!userStore.isLoggedIn" @click="$router.push('/login')" link>登录</el-button>
          <el-button v-if="!userStore.isLoggedIn" type="primary" @click="$router.push('/register')">注册</el-button>
          <el-dropdown v-else>
            <span class="user-avatar">
              <el-avatar :src="userStore.userInfo?.avatar || '/default-avatar.png'" />
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>个人中心</el-dropdown-item>
                <el-dropdown-item @click="userStore.logout()">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </nav>

    <!-- Hero 区域 -->
    <section class="hero">
      <div class="hero-bg"></div>
      <div class="hero-content container">
        <h1 class="hero-title">
          释放 <span class="text-gradient">AI</span> 的全部潜力
        </h1>
        <p class="hero-subtitle">
          专业提示词一键生成 · 行业模板库 · 智能优化 · 个人词库
        </p>
        <div class="hero-actions">
          <button class="btn-gradient btn-lg" @click="$router.push('/generator')">
            立即开始 →
          </button>
          <button class="btn-outline" @click="$router.push('/templates')">
            浏览模板
          </button>
        </div>
      </div>
    </section>

    <!-- 分类卡片 -->
    <section class="categories container">
      <h2 class="section-title">探索分类</h2>
      <div class="category-grid">
        <div v-for="cat in categories" :key="cat.id" class="category-card glass-card" @click="$router.push(`/templates?category=${cat.id}`)">
          <div class="category-icon">{{ cat.icon }}</div>
          <h3>{{ cat.name }}</h3>
          <p>{{ cat.description }}</p>
        </div>
      </div>
    </section>

    <!-- 功能特性 -->
    <section class="features">
      <div class="container">
        <h2 class="section-title">为什么选择我们</h2>
        <div class="feature-grid">
          <div class="feature-card glass-card">
            <div class="feature-icon">🎯</div>
            <h3>精准生成</h3>
            <p>基于场景、角色、要求的结构化输入，生成专业的AI提示词</p>
          </div>
          <div class="feature-card glass-card">
            <div class="feature-icon">📚</div>
            <h3>丰富模板</h3>
            <p>涵盖编程、办公、写作、绘画等20+行业的专业模板库</p>
          </div>
          <div class="feature-card glass-card">
            <div class="feature-icon">🤖</div>
            <h3>AI优化</h3>
            <p>智能优化你的原始提示词，让AI理解更准确</p>
          </div>
          <div class="feature-card glass-card">
            <div class="feature-icon">💾</div>
            <h3>个人词库</h3>
            <p>创建和管理你的专属提示词，随时复用</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 页脚 -->
    <footer class="footer">
      <p>© 2024 Everything-Prompt. 让AI提问更简单.</p>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '../../stores/user'
import { templateApi } from '../../api/template'

const userStore = useUserStore()
const categories = ref([])

onMounted(async () => {
  if (userStore.isLoggedIn) {
    await userStore.fetchUserInfo()
  }
  categories.value = await templateApi.getCategories()
})
</script>

<style scoped>
/* 导航栏 */
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  padding: 16px 0;
}

.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 20px;
  font-weight: 700;
}

.nav-links {
  display: flex;
  gap: 32px;
}

.nav-link {
  color: var(--text-secondary);
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s;
}

.nav-link:hover {
  color: var(--color-primary);
}

/* Hero */
.hero {
  position: relative;
  padding: 160px 0 100px;
  overflow: hidden;
}

.hero-bg {
  position: absolute;
  inset: 0;
  background: var(--gradient-hero);
  opacity: 0.05;
  filter: blur(100px);
}

.hero-content {
  text-align: center;
  position: relative;
}

.hero-title {
  font-size: clamp(40px, 6vw, 72px);
  font-weight: 800;
  line-height: 1.1;
  margin-bottom: 24px;
}

.hero-subtitle {
  font-size: 20px;
  color: var(--text-secondary);
  margin-bottom: 40px;
}

.hero-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.btn-lg {
  padding: 16px 32px;
  font-size: 16px;
}

.btn-outline {
  background: transparent;
  border: 2px solid var(--color-primary);
  color: var(--color-primary);
  padding: 14px 30px;
  border-radius: var(--radius-md);
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-outline:hover {
  background: var(--color-primary);
  color: white;
}

/* 分类 */
.categories {
  padding: 80px 0;
}

.section-title {
  font-size: 32px;
  font-weight: 700;
  text-align: center;
  margin-bottom: 48px;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.category-card {
  padding: 24px;
  cursor: pointer;
  transition: all 0.3s;
}

.category-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-glow);
}

.category-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.category-card h3 {
  margin-bottom: 8px;
}

.category-card p {
  color: var(--text-muted);
  font-size: 14px;
}

/* 功能 */
.features {
  padding: 80px 0;
  background: linear-gradient(180deg, transparent, rgba(102, 126, 234, 0.03));
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 24px;
}

.feature-card {
  padding: 32px;
  text-align: center;
}

.feature-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

/* 页脚 */
.footer {
  text-align: center;
  padding: 40px 0;
  color: var(--text-muted);
}
</style>
```

**src/views/auth/login.vue** (登录页):
```vue
<template>
  <div class="login-page">
    <div class="login-container glass-card">
      <div class="login-header">
        <h1 class="text-gradient">欢迎回来</h1>
        <p>登录到 Everything-Prompt</p>
      </div>

      <el-form :model="form" :rules="rules" ref="formRef" class="login-form">
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="用户名"
            size="large"
            :prefix-icon="User"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            size="large"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <button type="submit" class="btn-gradient btn-block" @click.prevent="handleLogin">
            登录
          </button>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        还没有账号?
        <router-link to="/register" class="text-gradient">立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const form = ref({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  try {
    await formRef.value.validate()
    await userStore.login(form.value)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (e) {
    // 验证失败
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background: linear-gradient(135deg, #F7FAFC 0%, #EDF2F7 100%);
}

.login-container {
  width: 100%;
  max-width: 420px;
  padding: 48px;
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-header h1 {
  font-size: 28px;
  margin-bottom: 8px;
}

.login-header p {
  color: var(--text-secondary);
}

.login-form {
  margin-bottom: 24px;
}

.btn-block {
  width: 100%;
  height: 48px;
  font-size: 16px;
}

.login-footer {
  text-align: center;
  color: var(--text-secondary);
}

.login-footer a {
  font-weight: 600;
  text-decoration: none;
}
</style>
```

**src/views/template/list.vue** (模板列表):
```vue
<template>
  <div class="template-list-page">
    <nav class="navbar glass-card">
      <!-- 复用首页导航 -->
    </nav>

    <div class="container main-content">
      <div class="page-header">
        <h1>模板广场</h1>
        <p>发现最优质的AI提示词模板</p>
      </div>

      <!-- 搜索和筛选 -->
      <div class="filter-bar glass-card">
        <el-select v-model="selectedCategory" placeholder="全部分类" clearable>
          <el-option
            v-for="cat in categories"
            :key="cat.id"
            :label="cat.name"
            :value="cat.id"
          />
        </el-select>
        <el-input
          v-model="keyword"
          placeholder="搜索模板..."
          :prefix-icon="Search"
          clearable
          @keyup.enter="searchTemplates"
        />
        <button class="btn-gradient" @click="searchTemplates">搜索</button>
      </div>

      <!-- 模板网格 -->
      <div class="template-grid">
        <div
          v-for="template in templates"
          :key="template.id"
          class="template-card glass-card"
          @click="$router.push(`/templates/${template.id}`)"
        >
          <div class="template-header">
            <span class="template-category">{{ template.categoryName }}</span>
            <el-tag v-if="template.isPremium" type="warning" size="small">VIP</el-tag>
          </div>
          <h3>{{ template.title }}</h3>
          <p class="template-desc">{{ template.description }}</p>
          <div class="template-footer">
            <span class="usage-count">使用 {{ template.usageCount }} 次</span>
            <el-rate v-model="template.rating" disabled size="small" />
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="page"
          :page-size="size"
          :total="total"
          layout="prev, pager, next"
          @current-change="fetchTemplates"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { templateApi } from '../../api/template'
import { Search } from '@element-plus/icons-vue'

const templates = ref([])
const categories = ref([])
const selectedCategory = ref(null)
const keyword = ref('')
const page = ref(1)
const size = ref(12)
const total = ref(0)

const fetchTemplates = async () => {
  const result = await templateApi.getTemplates({
    categoryId: selectedCategory.value,
    keyword: keyword.value,
    page: page.value,
    size: size.value
  })
  templates.value = result.records
  total.value = result.total
}

const searchTemplates = () => {
  page.value = 1
  fetchTemplates()
}

onMounted(async () => {
  categories.value = await templateApi.getCategories()
  await fetchTemplates()
})
</script>

<style scoped>
.main-content {
  padding-top: 100px;
  padding-bottom: 60px;
}

.page-header {
  margin-bottom: 32px;
}

.page-header h1 {
  font-size: 36px;
  margin-bottom: 8px;
}

.filter-bar {
  display: flex;
  gap: 16px;
  padding: 20px;
  margin-bottom: 32px;
}

.filter-bar .el-select,
.filter-bar .el-input {
  width: 200px;
}

.template-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}

.template-card {
  padding: 24px;
  cursor: pointer;
  transition: all 0.3s;
}

.template-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-glow);
}

.template-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.template-category {
  font-size: 12px;
  color: var(--color-primary);
  font-weight: 600;
}

.template-card h3 {
  margin-bottom: 8px;
  font-size: 18px;
}

.template-desc {
  color: var(--text-secondary);
  font-size: 14px;
  margin-bottom: 16px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.template-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.usage-count {
  font-size: 12px;
  color: var(--text-muted);
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}
</style>
```

**src/views/generator/index.vue** (Prompt生成器):
```vue
<template>
  <div class="generator-page">
    <nav class="navbar glass-card">
      <!-- 复用首页导航 -->
    </nav>

    <div class="container main-content">
      <div class="page-header">
        <h1>Prompt <span class="text-gradient">生成器</span></h1>
        <p>通过可视化表单生成专业的AI提示词</p>
      </div>

      <div class="generator-layout">
        <!-- 左侧：表单 -->
        <div class="generator-form glass-card">
          <h3>基础信息</h3>
          <el-form label-position="top">
            <el-form-item label="场景">
              <el-select v-model="form.scene" placeholder="选择使用场景">
                <el-option label="代码生成" value="code" />
                <el-option label="文章写作" value="writing" />
                <el-option label="数据分析" value="analysis" />
                <el-option label="图片生成" value="image" />
                <el-option label="问答咨询" value="qa" />
              </el-select>
            </el-form-item>

            <el-form-item label="角色">
              <el-select v-model="form.role" placeholder="选择AI角色">
                <el-option label="技术专家" value="expert" />
                <el-option label="文案助手" value="copywriter" />
                <el-option label="数据分析员" value="analyst" />
                <el-option label="创意设计师" value="designer" />
                <el-option label="私人顾问" value="advisor" />
              </el-select>
            </el-form-item>

            <el-form-item label="你的要求">
              <el-input
                v-model="form.requirement"
                type="textarea"
                :rows="4"
                placeholder="描述你希望AI完成的具体任务..."
              />
            </el-form-item>

            <el-form-item label="输出格式">
              <el-select v-model="form.format" placeholder="选择输出格式">
                <el-option label="Markdown" value="markdown" />
                <el-option label="JSON" value="json" />
                <el-option label="纯文本" value="text" />
                <el-option label="代码块" value="code" />
                <el-option label="列表" value="list" />
              </el-select>
            </el-form-item>

            <el-form-item label="约束条件">
              <el-input
                v-model="form.constraint"
                type="textarea"
                :rows="3"
                placeholder="添加任何特殊要求或限制..."
              />
            </el-form-item>

            <el-button type="primary" class="btn-gradient btn-block" @click="generate">
              生成 Prompt
            </el-button>
          </el-form>
        </div>

        <!-- 右侧：结果 -->
        <div class="generator-result glass-card">
          <div class="result-header">
            <h3>生成的 Prompt</h3>
            <el-button v-if="result" :icon="DocumentCopy" @click="copyResult">
              一键复制
            </el-button>
          </div>
          <div class="result-content">
            <div v-if="!result" class="result-placeholder">
              <p>填写左侧表单，点击"生成 Prompt"按钮</p>
            </div>
            <div v-else class="result-text">
              <pre>{{ result }}</pre>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { DocumentCopy } from '@element-plus/icons-vue'

const form = ref({
  scene: '',
  role: '',
  requirement: '',
  format: 'markdown',
  constraint: ''
})

const result = ref('')

const generate = () => {
  // 简单的前端拼接，实际应由后端AI生成
  let prompt = `【角色】你是一个${form.value.role === 'expert' ? '技术专家' : form.value.role === 'copywriter' ? '文案助手' : 'AI助手'}。\n\n`
  prompt += `【任务】${form.value.requirement}\n\n`
  prompt += `【输出格式】请使用 ${form.value.format} 格式输出。\n\n`
  if (form.value.constraint) {
    prompt += `【约束条件】${form.value.constraint}`
  }
  result.value = prompt
}

const copyResult = async () => {
  try {
    await navigator.clipboard.writeText(result.value)
    ElMessage.success('已复制到剪贴板')
  } catch {
    ElMessage.error('复制失败')
  }
}
</script>

<style scoped>
.main-content {
  padding-top: 100px;
  padding-bottom: 60px;
}

.page-header {
  margin-bottom: 40px;
}

.page-header h1 {
  font-size: 40px;
  margin-bottom: 8px;
}

.generator-layout {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 32px;
}

.generator-form {
  padding: 32px;
}

.generator-form h3 {
  margin-bottom: 24px;
  font-size: 18px;
}

.generator-result {
  padding: 32px;
  display: flex;
  flex-direction: column;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.result-content {
  flex: 1;
  min-height: 300px;
}

.result-placeholder {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-muted);
}

.result-text pre {
  background: #f8f9fa;
  padding: 16px;
  border-radius: var(--radius-md);
  white-space: pre-wrap;
  word-break: break-all;
  font-size: 14px;
  line-height: 1.6;
}

.btn-block {
  width: 100%;
  margin-top: 16px;
}

@media (max-width: 768px) {
  .generator-layout {
    grid-template-columns: 1fr;
  }
}
</style>
```

---

## 四、Git 提交规范

### 提交节点规划

| 节点 | 提交信息 | 说明 |
|------|----------|------|
| 1 | `feat: initialize SpringBoot backend project` | 后端项目初始化 |
| 2 | `feat: add entity classes (SysUser, TemplateCategory, PromptTemplate)` | 实体类 |
| 3 | `feat: add mapper interfaces` | Mapper层 |
| 4 | `feat: add AOP annotations and aspects (@RequireRole, @RateLimit, @Logged)` | AOP注解和切面 |
| 5 | `feat: add AuthService with JWT authentication` | 认证服务 |
| 6 | `feat: add TemplateService with caching` | 模板服务 |
| 7 | `feat: add REST controllers (Auth, Template, Category)` | 控制器 |
| 8 | `feat: add global exception handler` | 全局异常处理 |
| 9 | `feat: initialize Vue3 frontend project` | 前端项目初始化 |
| 10 | `feat: add API clients and Pinia store` | API封装和状态管理 |
| 11 | `feat: add home page with hero section and categories` | 首页 |
| 12 | `feat: add login and register pages` | 登录注册页 |
| 13 | `feat: add template list page with filters` | 模板列表页 |
| 14 | `feat: add prompt generator page` | Prompt生成器 |
| 15 | `docs: update README with setup instructions` | 更新README |

### 完整提交脚本

```bash
# 1. 后端项目初始化
git add -A && git commit -m "feat: initialize SpringBoot backend project"

# 2. 实体类
git add -A && git commit -m "feat: add entity classes (SysUser, TemplateCategory, PromptTemplate)"

# 3. Mapper层
git add -A && git commit -m "feat: add mapper interfaces"

# 4. AOP
git add -A && git commit -m "feat: add AOP annotations and aspects (@RequireRole, @RateLimit, @Logged)"

# 5. 认证服务
git add -A && git commit -m "feat: add AuthService with JWT authentication"

# 6. 模板服务
git add -A && git commit -m "feat: add TemplateService with caching"

# 7. 控制器
git add -A && git commit -m "feat: add REST controllers (Auth, Template, Category)"

# 8. 异常处理
git add -A && git commit -m "feat: add global exception handler"

# 9. 前端初始化
git add -A && git commit -m "feat: initialize Vue3 frontend project"

# 10. API封装
git add -A && git commit -m "feat: add API clients and Pinia store"

# 11. 首页
git add -A && git commit -m "feat: add home page with hero section and categories"

# 12. 登录注册
git add -A && git commit -m "feat: add login and register pages"

# 13. 模板列表
git add -A && git commit -m "feat: add template list page with filters"

# 14. 生成器
git add -A && git commit -m "feat: add prompt generator page"

# 推送到远程
git push -u origin main
```

---

## 五、数据库初始化 SQL

```sql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS everything_prompt DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE everything_prompt;

-- 用户表
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    nickname VARCHAR(50),
    avatar VARCHAR(255) DEFAULT '/static/default-avatar.png',
    `role` ENUM('GUEST', 'USER', 'VIP', 'ADMIN') DEFAULT 'USER',
    status TINYINT DEFAULT 1,
    last_login_time DATETIME,
    last_login_ip VARCHAR(50),
    total_ai_calls INT DEFAULT 0,
    vip_expire_time DATETIME,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_username (username),
    INDEX idx_role (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 模板分类表
CREATE TABLE template_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    icon VARCHAR(100),
    description VARCHAR(255),
    sort_order INT DEFAULT 0,
    is_free TINYINT DEFAULT 1,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 提示词模板表
CREATE TABLE prompt_template (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    content TEXT NOT NULL,
    category_id BIGINT NOT NULL,
    tags VARCHAR(255),
    is_premium TINYINT DEFAULT 0,
    is_official TINYINT DEFAULT 0,
    usage_count INT DEFAULT 0,
    rating DECIMAL(3,2) DEFAULT 5.00,
    status TINYINT DEFAULT 1,
    create_user_id BIGINT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category_id),
    FULLTEXT idx_search (title, description)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 初始分类数据
INSERT INTO template_category (name, code, icon, description, sort_order, is_free) VALUES
('编程开发', 'code', '💻', 'Java, Python, 前端等开发模板', 1, 1),
('职场办公', 'office', '💼', 'PPT, 邮件, 报告等办公模板', 2, 1),
('论文写作', 'paper', '📝', '论文, 报告, 文献等写作模板', 3, 1),
('AI绘画', 'image', '🎨', 'Midjourney, Stable Diffusion 提示词', 4, 1),
('短视频文案', 'video', '🎬', '抖音, 快手, 视频号文案模板', 5, 1),
('学习备考', 'study', '📚', '考试, 面试, 学习计划模板', 6, 1),
('Java开发', 'java', '☕', 'Java高级工程师专属模板', 7, 0),
('数据分析', 'data', '📊', '数据分析, BI, 可视化模板', 8, 0);

-- 初始管理员账户 (密码: admin123)
INSERT INTO sys_user (username, password, email, `role`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'admin@example.com', 'ADMIN');
```

---

## 六、部署配置

### Nginx 配置

```nginx
server {
    listen 80;
    server_name your-domain.com;

    # 前端静态资源
    location / {
        root /var/www/everything-prompt/frontend/dist;
        try_files $uri $uri/ /index.html;
    }

    # 后端 API 代理
    location /api {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    # 静态资源缓存
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
        root /var/www/everything-prompt/frontend/dist;
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
```

### Docker 快速部署

```yaml
# docker-compose.yml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: password
      MYSQL_DATABASE: everything_prompt
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"

  backend:
    build: ./backend
    ports:
      - "8080:8080"
    depends_on:
      - mysql
      - redis

  frontend:
    image: nginx:alpine
    ports:
      - "80:80"
    volumes:
      - ./frontend/dist:/usr/share/nginx/html
      - ./nginx.conf:/etc/nginx/conf.d/default.conf

volumes:
  mysql_data:
```

---

## 七、验收标准

- [ ] 后端项目可正常启动 (8080端口)
- [ ] 前端项目可正常启动 (5173端口)
- [ ] 用户可注册新账号
- [ ] 用户可登录并获取 JWT Token
- [ ] 可浏览模板分类
- [ ] 可搜索和查看模板详情
- [ ] Prompt 生成器可正常使用
- [ ] 限流注解正常工作
- [ ] 前后端联调通过
- [ ] 代码推送到 GitHub 仓库
