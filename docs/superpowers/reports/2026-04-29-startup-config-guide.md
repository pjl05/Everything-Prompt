# Everything-Prompt 启动与配置指南

**创建时间：** 2026-04-29
**项目版本：** v1.0.0
**状态：** 完整指南

---

## 一、项目概述

Everything-Prompt 是一个提示词分享与 AI 对话平台，包含以下功能模块：

| 模块 | 说明 |
|------|------|
| 模板广场 | 提示词模板浏览、分类、VIP 专享 |
| AI 对话 | MiniMax AI 实时对话、SSE 流式响应 |
| 案例墙 | 用户分享提示词与 AI 结果、点赞互动 |
| 后台管理 | 用户、模板、分类管理 |

**技术栈：**

- 后端：Spring Boot 3.2 + MyBatis-Plus + MySQL + Redis + JWT
- 前端：Vue 3 + Vite + Element Plus + Axios
- AI：MiniMax Chat API (abab6.5s-chat)

---

## 二、环境要求

### 2.1 后端环境

| 要求 | 版本 | 说明 |
|------|------|------|
| JDK | 17+ | Spring Boot 3.2 要求 |
| Maven | 3.6+ | 打包工具 |
| MySQL | 8.0+ | 主数据库 |
| Redis | 6.0+ | 缓存与会话存储 |

### 2.2 前端环境

| 要求 | 版本 | 说明 |
|------|------|------|
| Node.js | 18+ | 运行时 |
| pnpm | 8.0+ | 包管理器（推荐） |

### 2.3 第三方服务

| 服务 | 必需 | 说明 |
|------|------|------|
| MiniMax API | 是 | AI 对话功能，需要申请 API Key |

---

## 三、环境变量配置

### 3.1 后端环境变量

在运行后端前，需要配置以下环境变量：

```bash
# 数据库密码（必须）
export DB_PASSWORD=your_mysql_password

# JWT 密钥（生产环境必须更换）
export JWT_SECRET=YourSuperSecretKeyMustBeAtLeast32Characters!

# Redis 配置（可选，有默认值）
export REDIS_HOST=localhost
export REDIS_PORT=6379
export REDIS_PASSWORD=

# MiniMax AI API Key（必需，否则 AI 对话不可用）
export MINIMAX_API_KEY=your_minimax_api_key
```

**Windows PowerShell：**

```powershell
$env:DB_PASSWORD="your_mysql_password"
$env:JWT_SECRET="YourSuperSecretKeyMustBeAtLeast32Characters!"
$env:MINIMAX_API_KEY="your_minimax_api_key"
```

**Windows CMD：**

```cmd
set DB_PASSWORD=your_mysql_password
set JWT_SECRET=YourSuperSecretKeyMustBeAtLeast32Characters!
set MINIMAX_API_KEY=your_minimax_api_key
```

### 3.2 MiniMax API Key 申请

1. 访问 [MiniMax 开放平台](https://platform.minimax.chat/)
2. 注册并登录账号
3. 进入控制台 → API Keys → 创建新 Key
4. 复制 Key 并设置为环境变量

---

## 四、数据库初始化

### 4.1 创建数据库

```bash
mysql -u root -p < backend/sql/init.sql
```

或手动执行：

```sql
CREATE DATABASE IF NOT EXISTS everything_prompt DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE everything_prompt;
-- 然后执行 init.sql 中的建表语句
```

### 4.2 初始化数据说明

init.sql 包含以下表结构和初始数据：

| 表名 | 说明 | 初始数据 |
|------|------|----------|
| sys_user | 用户表 | 1 个管理员账号 |
| template_category | 模板分类 | 10 个分类 |
| prompt_template | 提示词模板 | 20 个模板 |
| chat_session | AI 对话会话 | - |
| chat_message | AI 聊天消息 | - |
| prompt_share | 分享记录 | - |
| share_like | 点赞记录 | - |

### 4.3 默认账号

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 管理员 | admin | 123456 | 需修改默认密码 |

---

## 五、项目启动

### 5.1 后端启动

**方式一：Maven 运行**

```bash
cd backend
./mvnw spring-boot:run
```

**方式二：JAR 运行**

```bash
cd backend
./mvnw clean package -DskipTests
java -jar target/everything-prompt-backend-1.0.0.jar
```

**验证后端启动成功：**

```
http://localhost:8080
```

应看到 Spring Boot 默认页面或 404（正常，API 需要认证）。

### 5.2 前端启动

```bash
cd frontend
pnpm install
pnpm dev
```

**验证前端启动成功：**

访问 http://localhost:3000

---

## 六、配置验证清单

### 6.1 Phase 1 - 模板内容验证

- [ ] 访问 http://localhost:3000/templates
- [ ] 查看 10 个分类（编程开发、职场办公、论文写作、AI绘画等）
- [ ] 点击模板查看详情页
- [ ] 验证示例效果、适用场景、使用说明显示正常

### 6.2 Phase 2 - AI 对话验证

- [ ] 访问 http://localhost:3000/ai/chat
- [ ] 创建新对话
- [ ] 发送消息，验证 SSE 流式响应正常
- [ ] 验证对话历史保存和加载

### 6.3 Phase 3 - 社交分享验证

- [ ] 访问 http://localhost:3000/share
- [ ] 查看分享列表（案例墙）
- [ ] 测试点赞/取消点赞功能
- [ ] 创建新分享

### 6.4 Phase 4 - 后台管理验证

- [ ] 访问 http://localhost:3000/admin
- [ ] 使用管理员账号登录
- [ ] 验证用户管理、模板管理、分类管理功能

---

## 七、项目结构

```
Everything-Prompt/
├── backend/
│   ├── src/main/java/com/everything/prompt/
│   │   ├── config/         # 配置类（MiniMax、Security等）
│   │   ├── controller/     # REST API 控制器
│   │   ├── entity/         # 数据实体
│   │   ├── mapper/         # MyBatis 数据访问
│   │   ├── service/        # 业务逻辑
│   │   └── util/           # 工具类
│   ├── src/main/resources/
│   │   └── application.yml # 主配置文件
│   ├── sql/
│   │   └── init.sql        # 数据库初始化脚本
│   └── pom.xml
│
├── frontend/
│   ├── src/
│   │   ├── api/            # API 调用（ai.js, share.js）
│   │   ├── router/         # Vue 路由配置
│   │   ├── views/          # 页面组件
│   │   │   ├── template/   # 模板相关页面
│   │   │   ├── ai/         # AI 对话页面
│   │   │   ├── share/      # 案例墙页面
│   │   │   └── admin/      # 管理后台页面
│   │   └── App.vue
│   ├── index.html
│   └── package.json
│
└── docs/
    └── superpowers/
        ├── plans/          # 开发计划
        └── reports/        # 开发报告
```

---

## 八、常见问题

### 8.1 AI 对话无响应

**原因：** 未配置 MiniMax API Key

**解决：**
```bash
export MINIMAX_API_KEY=your_valid_api_key
# 重启后端服务
```

### 8.2 前端无法连接后端

**原因：** 跨域问题或后端未启动

**解决：**
1. 确认后端运行在 http://localhost:8080
2. 检查前端 API 基础路径配置

### 8.3 数据库连接失败

**原因：** MySQL 未启动或密码错误

**解决：**
1. 确认 MySQL 服务运行中
2. 验证环境变量 `DB_PASSWORD` 正确
3. 检查 MySQL 用户权限

### 8.4 Redis 连接失败

**原因：** Redis 未启动

**解决：**
1. 启动 Redis 服务
2. 或设置 `REDIS_HOST` 指向正确的 Redis 服务器

---

## 九、生产部署注意事项

### 9.1 安全配置

- [ ] 修改 JWT_SECRET 为强密码
- [ ] 配置 MySQL 强密码
- [ ] MiniMax API Key 不要提交到代码仓库
- [ ] 启用 HTTPS

### 9.2 性能优化

- [ ] 配置 Redis 缓存
- [ ] 启用 MySQL 连接池
- [ ] 前端资源 gzip 压缩
- [ ] 静态资源 CDN 加速

### 9.3 环境变量示例（生产环境）

```bash
# 生产环境推荐通过环境变量或配置中心注入
DB_PASSWORD=<生产数据库密码>
JWT_SECRET=<32位以上强密码>
REDIS_HOST=<Redis生产地址>
REDIS_PASSWORD=<Redis密码>
MINIMAX_API_KEY=<MiniMax生产Key>
```

---

## 十、Git 提交记录

| 提交 | 阶段 | 说明 |
|------|------|------|
| 7128957 | Phase 1 | 修复编译错误并添加阶段计划文档 |
| 605f66d | Phase 1 | 填充模板内容和示例展示功能 |
| c00389d | Phase 2 | MiniMax AI 实战功能 |
| c46ba80 | Phase 3 | 用户分享社交功能 |
| 1f61abe | Phase 4 | 推广启动计划和内容规划 |

---

## 十一、联系方式与支持

如有问题，请检查：

1. 开发报告：`docs/superpowers/reports/`
2. 控制台日志输出
3. 浏览器开发者工具 Network/Console 面板
