# Phase 3：社交分享功能

**更新时间：** 2026-04-27
**预计工期：** 1-2周
**阶段目标：** 用户可以分享自己的提示词和 AI 生成结果

---

## 一、任务概述

本阶段需要：
1. 用户分享提示词功能
2. 分享展示和点赞
3. 用户案例墙
4. 优质内容推荐

---

## 二、具体任务

### 2.1 后端任务

| 任务 | 文件 | 说明 |
|------|------|------|
| 实体类 | `entity/PromptShare.java` | 分享记录 |
| 实体类 | `entity/ShareLike.java` | 点赞记录 |
| Mapper | `mapper/PromptShareMapper.java` | 分享数据访问 |
| Mapper | `mapper/ShareLikeMapper.java` | 点赞数据访问 |
| Service | `service/ShareService.java` | 分享业务逻辑 |
| Controller | `controller/ShareController.java` | 分享API接口 |

### 2.2 前端任务

| 任务 | 文件 | 说明 |
|------|------|------|
| 分享页面 | `views/share/index.vue` | 分享列表页 |
| 分享详情 | `views/share/detail.vue` | 分享详情页 |
| 我的分享 | `views/share/mine.vue` | 我的分享管理 |
| API封装 | `api/share.js` | 前端API调用 |

### 2.3 数据库任务

```sql
-- 提示词分享表
CREATE TABLE prompt_share (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '分享用户ID',
    template_id BIGINT COMMENT '关联模板ID',
    prompt_title VARCHAR(100) COMMENT '提示词标题',
    prompt_content TEXT COMMENT '分享的提示词内容',
    ai_result TEXT COMMENT 'AI生成结果示例',
    description TEXT COMMENT '分享描述',
    usage_count INT DEFAULT 0 COMMENT '使用次数',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    status TINYINT DEFAULT 1 COMMENT '状态：0隐藏 1展示',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_template_id (template_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 分享点赞表
CREATE TABLE share_like (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    share_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_share_user (share_id, user_id),
    INDEX idx_share_id (share_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

---

## 三、功能流程

### 3.1 分享流程

1. 用户使用模板生成提示词
2. 用户点击"分享"按钮
3. 填写分享描述（可选）
4. 提交分享
5. 分享展示在案例墙

### 3.2 点赞流程

1. 用户浏览案例墙
2. 点击点赞按钮
3. 点赞数+1
4. 点赞状态保存

### 3.3 推荐算法

| 推荐维度 | 说明 |
|----------|------|
| 点赞数 | 点赞越多越靠前 |
| 使用次数 | 使用越多越靠前 |
| 发布时间 | 最新优先 |
| 模板类型 | 可按分类筛选 |

---

## 四、Git 提交

```bash
git add .
git commit -m "feat: Phase 3 - 用户分享社交功能

- 用户分享提示词功能
- 分享展示和点赞
- 用户案例墙
- 优质内容推荐
- 新建prompt_share和share_like表"

git push origin master
```

---

## 五、验收标准

- [ ] 用户可以分享提示词
- [ ] 分享展示在案例墙
- [ ] 用户可以点赞/取消点赞
- [ ] 点赞数正确更新
- [ ] 前端页面交互正常
- [ ] Git 已提交并推送
