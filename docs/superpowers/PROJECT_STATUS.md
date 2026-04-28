# Everything-Prompt 项目现状文档

**更新时间：** 2026-04-27

---

## 一、项目概述

AI 提示词工程平台，帮助用户生成、优化、管理提示词。

**愿景：** 让普通人也能轻松用好 AI

---

## 二、技术栈

| 层级 | 技术 |
|------|------|
| 后端 | SpringBoot 3.2.3 + MyBatis-Plus 3.5.5 |
| 前端 | Vue 3 + Vite + Element Plus |
| 数据库 | MySQL 8.0 (Docker) |
| 缓存 | Redis 7 (Docker) |
| 认证 | JWT + Spring Security |
| AI | MiniMax API (可选) |

---

## 三、功能模块完成状态

| 模块 | 状态 | 说明 |
|------|------|------|
| 用户注册/登录 | ✅ 完成 | JWT 认证 |
| 模板广场 | ✅ 完成 | 公开模板浏览 |
| Prompt生成器 | ✅ 完成 | 结构化输入生成 |
| VIP会员体系 | ✅ 完成 | 付费解锁高级模板 |
| 私有词库 | ✅ 完成 | VIP专属 |
| 收藏夹 | ✅ 完成 | 用户收藏 |
| AI优化 | ⚠️ 待接入 | MiniMax API 未配置 |
| 批量生成 | ✅ 完成 | 批量生成提示词 |
| 管理后台 | ✅ 完成 | 模板/分类/用户/统计 |

---

## 四、当前问题

| 问题 | 状态 | 解决方案 |
|------|------|----------|
| Redis连接失败 | 🔴 待解决 | 检查本地端口6379是否开放 |
| 密码无法登录 | 🔴 待解决 | 执行SQL更新密码哈希 |
| MiniMax API | ⚠️ 未配置 | 需要申请API Key |

---

## 五、启动流程

```bash
# 1. 启动Docker服务
docker-compose up -d

# 2. 初始化数据库
mysql -uroot -ppassword -hlocalhost < backend/sql/init.sql

# 3. 启动后端
cd backend && mvn spring-boot:run

# 4. 启动前端
cd frontend && npm install && npm run dev
```

**前端地址：** http://localhost:5173
**后端地址：** http://localhost:8080

---

## 六、默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |

---

## 七、项目结构

```
Everything-Prompt/
├── backend/                 # SpringBoot 后端
│   └── src/main/java/com/everything/prompt/
│       ├── annotation/      # 自定义注解
│       ├── aspect/          # AOP 切面
│       ├── config/          # 配置类
│       ├── controller/      # 控制器
│       │   └── admin/       # 管理后台控制器
│       ├── entity/          # 实体类
│       ├── mapper/          # 数据访问层
│       ├── service/         # 业务服务
│       └── util/            # 工具类
├── frontend/                # Vue3 前端
│   └── src/
│       ├── api/             # API 客户端
│       ├── layout/          # 布局组件
│       ├── router/          # 路由配置
│       ├── stores/          # Pinia 状态管理
│       └── views/           # 页面组件
├── docker-compose.yml      # Docker 配置
└── sql/init.sql            # 数据库初始化
```

---

## 八、已完成的 Phase

### Phase 1 - MVP 基础版
- ✅ 用户注册/登录 (JWT)
- ✅ 提示词模板浏览
- ✅ Prompt 生成器
- ✅ Redis 限流

### Phase 2 - VIP 会员版
- ✅ VIP 权限体系
- ✅ 私有词库 (VIP专属)
- ✅ 收藏夹
- ✅ AI 优化 (待接入MiniMax)
- ✅ 批量生成

### Phase 3 - 管理后台
- ✅ 数据统计看板
- ✅ 模板管理 (CRUD)
- ✅ 分类管理
- ✅ 用户管理 / VIP 授权
