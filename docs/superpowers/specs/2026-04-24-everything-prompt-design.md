# Everything-Prompt 提示词平台设计文档

## 一、项目概述

### 1.1 项目定位
帮用户低成本写出专业 AI 提问：提示词一键生成 + 行业模板库 + 劣质 Prompt 智能优化 + 个人词库 + 会员权限体系 + 安全限流防护

### 1.2 技术栈
| 层级 | 技术选型 | 说明 |
|------|---------|------|
| 后端 | SpringBoot 3.x + MyBatis-Plus | 快速 CRUD 开发 |
| 数据库 | MySQL 8.0 | 结构化数据存储 |
| 缓存 | Redis + Caffeine | 双缓存体系 |
| 安全 | SpringSecurity + JWT | 无状态认证 |
| 前端 | Vue3 + Vite + Element Plus | 快速落地 |
| AI | MiniMax API | HTTP 调用 |
| 部署 | 前后端分离 + Nginx | 反向代理 |

### 1.3 三阶段开发计划
- **Phase 1 (2-3周)**: MVP 基础版 - 公共模块 + 账号体系 + 核心免费功能
- **Phase 2 (2-3周)**: 会员完整版 - 付费功能 + 安全防护
- **Phase 3 (1-2周)**: 管理后台 - 运营支撑

---

## 二、数据库设计

### 2.1 表结构

#### sys_user (用户表)
```sql
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    nickname VARCHAR(50),
    avatar VARCHAR(255) DEFAULT '/static/default-avatar.png',
    role ENUM('GUEST', 'USER', 'VIP', 'ADMIN') DEFAULT 'USER',
    status TINYINT DEFAULT 1,
    last_login_time DATETIME,
    last_login_ip VARCHAR(50),
    total_ai_calls INT DEFAULT 0,
    vip_expire_time DATETIME,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### template_category (模板分类表)
```sql
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
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### prompt_template (提示词模板表)
```sql
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
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### user_word (私有词库表)
```sql
CREATE TABLE user_word (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    group_name VARCHAR(50) DEFAULT '默认分组',
    is_shared TINYINT DEFAULT 0,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### user_favorite (收藏夹表)
```sql
CREATE TABLE user_favorite (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    template_id BIGINT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_template (user_id, template_id)
);
```

#### ai_call_log (AI调用记录表)
```sql
CREATE TABLE ai_call_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    call_type ENUM('OPTIMIZE', 'GENERATE', 'BATCH') NOT NULL,
    input_content TEXT,
    output_content TEXT,
    model VARCHAR(50),
    tokens_used INT,
    cost DECIMAL(10,4),
    ip_address VARCHAR(50),
    status TINYINT DEFAULT 1,
    error_message VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

#### sys_config (系统配置表)
```sql
CREATE TABLE sys_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value TEXT,
    description VARCHAR(255),
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### visit_log (访问日志表)
```sql
CREATE TABLE visit_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    ip_address VARCHAR(50),
    user_agent VARCHAR(500),
    visit_path VARCHAR(255),
    referer VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

### 2.2 Redis 缓存设计

```
template:list:category:{categoryId}  -> 模板列表
template:detail:{templateId}          -> 模板详情
category:all                          -> 全部分类
user:ratelimit:{userId}:{date}       -> 限流计数
dedup:{md5}                          -> 防重提交
jwt:blacklist:{token}                -> JWT黑名单
```

---

## 三、安全设计

### 3.1 限流规则
| 用户类型 | AI调用/日 |
|---------|----------|
| 游客 | 5 |
| 普通用户 | 50 |
| VIP | 500 |

### 3.2 JWT 认证
- Token 有效期 7 天
- Redis 维护黑名单
- 防重提交 60 秒有效期

---

## 四、MiniMax AI 接入

- 超时时间 30 秒
- @Async 异步处理
- 超时降级返回友好提示

---

## 五、部署架构

```
Nginx (80/443)
  ├── /        -> Vue3 静态资源
  └── /api     -> SpringBoot (8080)
                      ├── MySQL (3306)
                      └── Redis (6379)
```

---

## 六、Phase 1 开发任务

### 后端
- [ ] 项目初始化
- [ ] 数据库配置 + MyBatis-Plus
- [ ] Redis + Caffeine 配置
- [ ] 实体类 + Mapper
- [ ] User Service + Auth Controller
- [ ] Category/Template Service + Controller
- [ ] 全局异常处理
- [ ] 限流拦截器
- [ ] JWT 认证过滤器

### 前端
- [ ] 项目初始化 (Vite + Vue3 + Element Plus)
- [ ] Axios 封装
- [ ] Pinia 状态管理
- [ ] 首页 + 模板广场
- [ ] 登录/注册页
- [ ] Prompt 生成器

### 部署
- [ ] 后端打包
- [ ] 前端打包
- [ ] Nginx 配置
