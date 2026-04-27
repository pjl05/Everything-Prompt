# Everything-Prompt

AI 提示词工程平台 - 释放 AI 的全部潜力

---

## 🚀 快速启动

### 前置要求

- JDK 17+
- Node.js 18+
- Docker Desktop (已启动)

### 1. 启动数据库服务 (Docker)

```bash
# 启动 MySQL 和 Redis
docker-compose up -d

# 验证服务状态
docker-compose ps
```

**Navicat 连接配置：**
| 配置项 | 值 |
|--------|-----|
| 主机 | localhost |
| 端口 | 3306 |
| 用户名 | root |
| 密码 | password |

**Redis Desktop Manager 连接：**
| 配置项 | 值 |
|--------|-----|
| 主机 | localhost |
| 端口 | 6379 |

### 2. 初始化数据库

```bash
# 进入后端目录
cd backend

# 初始化数据库表
mysql -uroot -ppassword -hlocalhost < sql/init.sql

# 或在 MySQL 客户端执行 sql/init.sql 文件内容
```

### 3. 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端地址：http://localhost:8080

### 4. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端地址：http://localhost:5173

---

## 📁 项目结构

```
Everything-Prompt/
├── backend/                 # SpringBoot 后端
│   ├── src/main/java/com/everything/prompt/
│   │   ├── annotation/      # 自定义注解 (@RequireRole, @RateLimit, @Logged)
│   │   ├── aspect/          # AOP 切面
│   │   ├── config/          # 配置类
│   │   ├── controller/      # 控制器
│   │   │   └── admin/       # 管理后台控制器
│   │   ├── entity/          # 实体类
│   │   ├── mapper/          # 数据访问层
│   │   ├── service/          # 业务服务
│   │   └── util/             # 工具类
│   └── sql/
│       └── init.sql          # 数据库初始化脚本
├── frontend/                # Vue3 前端
│   └── src/
│       ├── api/              # API 客户端
│       ├── assets/           # 静态资源
│       ├── layout/           # 布局组件
│       ├── router/          # 路由配置
│       ├── stores/           # Pinia 状态管理
│       └── views/            # 页面组件
├── docker-compose.yml        # Docker 配置
└── start.sh                 # 启动脚本
```

---

## 🔐 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |

---

## 📋 功能模块

### Phase 1 - MVP 基础版
- ✅ 用户注册/登录 (JWT)
- ✅ 提示词模板浏览
- ✅ Prompt 生成器
- ✅ Redis 限流

### Phase 2 - VIP 会员版
- ✅ VIP 权限体系
- ✅ 私有词库 (VIP专属)
- ✅ 收藏夹
- ✅ AI 优化 (MiniMax)
- ✅ 批量生成

### Phase 3 - 管理后台
- ✅ 数据统计看板
- ✅ 模板管理 (CRUD)
- ✅ 分类管理
- ✅ 用户管理 / VIP 授权

---

## 🛠️ 技术栈

**后端：**
- SpringBoot 3.2.3
- MyBatis-Plus 3.5.5
- Spring Security + JWT
- Redis + Caffeine (二级缓存)
- MySQL 8.0

**前端：**
- Vue 3 + Vite
- Element Plus
- Pinia (状态管理)
- Axios

---

## ⚙️ 环境变量 (可选)

如需自定义配置，可通过环境变量覆盖：

| 变量名 | 默认值 | 说明 |
|--------|--------|------|
| DB_PASSWORD | password | MySQL root 密码 |
| REDIS_HOST | localhost | Redis 地址 |
| REDIS_PORT | 6379 | Redis 端口 |
| JWT_SECRET | (默认密钥) | JWT 签名密钥 |

---

## 📄 许可证

MIT License
