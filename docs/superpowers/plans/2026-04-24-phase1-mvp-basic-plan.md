# Phase 1: MVP 基础版开发计划

**开发周期**: 2-3 周
**目标**: 完成公共模块 + 账号体系 + 核心免费功能

---

## 1. 项目结构

### 1.1 后端 SpringBoot 项目

```
everything-prompt-backend/
├── pom.xml
├── src/main/java/com/everything/prompt/
│   ├── EverythingPromptApplication.java
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   ├── RedisConfig.java
│   │   └── AsyncConfig.java
│   ├── controller/
│   │   ├── auth/AuthController.java
│   │   ├── template/TemplateController.java
│   │   └── category/CategoryController.java
│   ├── service/
│   ├── mapper/
│   ├── entity/
│   ├── dto/
│   ├── filter/JwtAuthenticationFilter.java
│   ├── interceptor/RateLimitInterceptor.java
│   └── exception/GlobalExceptionHandler.java
└── src/main/resources/
    ├── application.yml
    └── mapper/
```

### 1.2 前端 Vue3 项目

```
everything-prompt-frontend/
├── package.json
├── vite.config.js
├── src/
│   ├── main.js
│   ├── App.vue
│   ├── api/
│   ├── views/
│   │   ├── home/index.vue
│   │   ├── template/list.vue, detail.vue
│   │   ├── generator/index.vue
│   │   └── auth/login.vue, register.vue
│   ├── router/index.js
│   ├── stores/user.js
│   └── assets/
```

---

## 2. 后端任务清单

### 2.1 依赖 (pom.xml)
- spring-boot-starter-web
- spring-boot-starter-security
- jjwt-api (0.11.5)
- mybatis-plus-spring-boot3-starter (3.5.5)
- spring-boot-starter-data-redis
- caffeine
- mysql-connector-j
- hutool-all (5.8.25)
- spring-boot-starter-validation

### 2.2 配置文件 (application.yml)
```yaml
server:
  port: 8080
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/everything_prompt
    username: root
    password: password
  redis:
    host: localhost
    port: 6379
    password: password
mybatis-plus:
  mapper-locations: classpath:mapper/*.xml
jwt:
  secret: your-256-bit-secret-key-here-min-32-chars
  expiration: 604800000
ai:
  minimax:
    api-key: your_api_key
    base-url: https://api.minimax.chat
    model: abab6.5s-chat
    timeout: 30
```

### 2.3 实体类
| 类 | 说明 |
|----|------|
| SysUser | 用户 (username, password, role, status, vip_expire_time) |
| TemplateCategory | 分类 (name, code, icon, sort_order, is_free) |
| PromptTemplate | 模板 (title, content, category_id, tags, is_premium) |
| UserWord | 私有词库 |
| UserFavorite | 收藏 |

### 2.4 接口设计

**认证接口**
| POST | /api/auth/register | {username, password, email} |
| POST | /api/auth/login | {username, password} |
| POST | /api/auth/logout | - |
| GET | /api/auth/me | - |

**分类接口**
| GET | /api/categories | 全部分类 |

**模板接口**
| GET | /api/templates | 列表?categoryId&keyword |
| GET | /api/templates/{id} | 详情 |

**AI接口**
| POST | /api/ai/generate | {scene, role, requirement, format, constraint} |
| POST | /api/ai/optimize | {content} |

---

## 3. 前端任务清单

### 3.1 依赖
```json
{
  "vue": "^3.4.0",
  "vue-router": "^4.2.0",
  "pinia": "^2.1.0",
  "axios": "^1.6.0",
  "element-plus": "^2.5.0",
  "@element-plus/icons-vue": "^2.3.0"
}
```

### 3.2 页面
| 路径 | 页面 |
|------|------|
| / | 首页 |
| /templates | 模板广场 |
| /templates/:id | 模板详情 |
| /generator | Prompt生成器 |
| /login | 登录 |
| /register | 注册 |

---

## 4. 部署

### 4.1 后端打包
```bash
mvn clean package -DskipTests
# 输出: target/everything-prompt-backend-1.0.0.jar
```

### 4.2 前端打包
```bash
npm install && npm run build
# 输出: dist/
```

### 4.3 Nginx
```nginx
server {
    listen 80;
    location / {
        root /var/www/dist;
        try_files $uri $uri/ /index.html;
    }
    location /api {
        proxy_pass http://localhost:8080;
    }
}
```

---

## 5. 验收标准

- [ ] 注册/登录/登出正常
- [ ] JWT认证正常
- [ ] 模板分类浏览
- [ ] 模板搜索
- [ ] Prompt生成器可用
- [ ] 限流正常
- [ ] 部署通过
