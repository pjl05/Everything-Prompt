# Phase 2：MiniMax AI 实战功能

**更新时间：** 2026-04-27
**预计工期：** 1周
**阶段目标：** 用户可在平台内直接与 MiniMax AI 对话实战

---

## 一、任务概述

本阶段需要：
1. 接入 MiniMax 对话 API
2. 实现 SSE 流式输出（打字机效果）
3. 创建 AI 对话页面
4. 支持对话上下文管理
5. 保存对话历史

---

## 二、技术方案

### 2.1 MiniMax API 配置

文件：`backend/src/main/resources/application.yml`

```yaml
minimax:
  api-key: your-api-key
  api-url: https://api.minimax.chat/v1
  group-id: your-group-id
```

### 2.2 核心功能点

| 功能 | 说明 |
|------|------|
| 同步对话 | 简单场景使用 |
| 流式对话（SSE） | 打字机效果，用户体验更好 |
| 对话上下文 | 支持多轮对话 |
| 历史记录 | 保存和加载历史对话 |

---

## 三、具体任务

### 3.1 后端任务

| 任务 | 文件 | 说明 |
|------|------|------|
| MiniMax配置类 | `config/MiniMaxConfig.java` | API配置 |
| MiniMax工具类 | `util/MiniMaxUtil.java` | 对话API调用 |
| 实体类 | `entity/ChatSession.java` | 对话会话 |
| 实体类 | `entity/ChatMessageRecord.java` | 聊天消息 |
| Mapper | `mapper/ChatSessionMapper.java` | 会话数据访问 |
| Mapper | `mapper/ChatMessageRecordMapper.java` | 消息数据访问 |
| Service | `service/ChatService.java` | 对话业务逻辑 |
| Controller | `controller/AiChatController.java` | 对话API接口 |

### 3.2 前端任务

| 任务 | 文件 | 说明 |
|------|------|------|
| 对话页面 | `views/ai/chat.vue` | AI对话主页面 |
| API封装 | `api/chat.js` | 前端API调用 |

### 3.3 数据库任务

```sql
-- 对话会话表
CREATE TABLE chat_session (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(100) DEFAULT '新对话',
    template_id BIGINT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 聊天消息表
CREATE TABLE chat_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL COMMENT 'user/assistant',
    content TEXT NOT NULL,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_session_id (session_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

---

## 四、Git 提交

```bash
git add .
git commit -m "feat: Phase 2 - MiniMax AI 实战功能

- 接入MiniMax对话API
- 实现SSE流式输出
- 创建AI对话页面
- 支持对话上下文管理
- 保存对话历史记录
- 新建chat_session和chat_message表"

git push origin master
```

---

## 五、验收标准

- [ ] MiniMax API 调用成功
- [ ] 流式输出正常（打字机效果）
- [ ] 可以创建新对话
- [ ] 可以发送消息并获取 AI 回复
- [ ] 对话历史正确保存和加载
- [ ] 前端页面交互正常
- [ ] Git 已提交并推送
