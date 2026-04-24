# Phase 2: 会员完整版开发计划

**开发周期**: 2-3 周
**目标**: 完成付费功能 + 安全防护（变现核心）

---

## 1. 新增功能

### 1.1 VIP 权限体系
| 功能 | 游客 | 普通用户 | VIP |
|------|------|---------|-----|
| 查看免费模板 | ✅ | ✅ | ✅ |
| 使用基础生成器 | 5次/日 | 无限 | 无限 |
| AI优化 | ❌ | 10次/日 | 无限 |
| 查看付费模板 | ❌ | ❌ | ✅ |
| 私有词库 | ❌ | ❌ | ✅ |
| 批量生成 | ❌ | ❌ | ✅ |

### 1.2 深度 AI 优化
- 逻辑补全
- 专业术语补充
- 指令拆分
- 约束强化
- 语气风格定制

### 1.3 私有词库
- 新增/编辑/删除词条
- 分组管理
- 云端同步 (Redis + MySQL)

### 1.4 收藏夹
- 收藏/取消收藏
- 一键复用
- 云端同步

### 1.5 批量生成
- 多版本提示词批量生成
- TXT/Markdown 导出

---

## 2. 安全防护

### 2.1 Redis 全局限流
```java
String key = "ratelimit:" + userId + ":" + LocalDate.now();
```

### 2.2 防重复提交
```java
String dedupKey = "dedup:" + MD5(input + userId + action);
redis.setNX(dedupKey, "1", 60, TimeUnit.SECONDS);
```

### 2.3 接口权限拦截
```java
@RequiresRole({Role.VIP, Role.ADMIN})
public ApiResponse<List<TemplateVO>> getPremiumTemplates() {}
```

### 2.4 热点数据缓存
```java
@Cacheable(value = "templates", key = "#categoryId")
public List<TemplateVO> getTemplates(Long categoryId) {}
```

### 2.5 异步 AI 调用
```java
@Async("aiTaskExecutor")
public CompletableFuture<String> optimizeAsync(String content) {}
```

### 2.6 超时熔断
超时30秒自动降级

---

## 3. 后端新增接口

### VIP 接口
| POST | /api/vip/upgrade | 升级VIP | 用户 |

### 词库接口
| GET | /api/words | 我的词库 | VIP |
| POST | /api/words | 添加词条 | VIP |
| PUT | /api/words/{id} | 更新词条 | VIP |
| DELETE | /api/words/{id} | 删除词条 | VIP |

### 收藏接口
| GET | /api/favorites | 我的收藏 | 用户 |
| POST | /api/favorites/{templateId} | 收藏 | 用户 |
| DELETE | /api/favorites/{templateId} | 取消收藏 | 用户 |

### 批量接口
| POST | /api/ai/batch | 批量生成 | VIP |
| GET | /api/ai/batch/{taskId}/export | 导出 | VIP |

---

## 4. 前端新增页面

| 路径 | 页面 |
|------|------|
| /vip/templates | 付费模板 |
| /words | 私有词库 |
| /favorites | 我的收藏 |
| /batch | 批量生成 |

---

## 5. 验收标准

- [ ] VIP权限区分正常
- [ ] 深度AI优化可用
- [ ] 私有词库CRUD正常
- [ ] 收藏功能正常
- [ ] 批量生成可用
- [ ] Redis限流正常
- [ ] 防重提交正常
- [ ] 超时降级正常
