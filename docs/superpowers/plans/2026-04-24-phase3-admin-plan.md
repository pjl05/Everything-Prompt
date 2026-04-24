# Phase 3: 管理后台开发计划

**开发周期**: 1-2 周
**目标**: 完成后台完整功能（运营支撑）

---

## 1. 功能模块

### 1.1 模板管理
- 模板列表（分页展示）
- 新增/编辑/删除模板
- 上下架快速切换
- 批量操作

### 1.2 分类管理
- 分类列表（拖拽排序）
- 新增/编辑/删除分类
- 启用/禁用状态

### 1.3 用户管理
- 用户列表（分页/搜索）
- 用户详情（信息/调用记录）
- VIP手动授权/续期
- 异常账号禁用

### 1.4 权限配置
- 功能白名单配置
- 会员权益配置

### 1.5 数据统计
- 访问量趋势（日/周/月）
- AI调用统计
- 会员统计
- 模板热度排行

---

## 2. 后端新增接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/admin/templates | 模板列表 |
| POST | /api/admin/templates | 新增模板 |
| PUT | /api/admin/templates/{id} | 更新模板 |
| DELETE | /api/admin/templates/{id} | 删除模板 |
| PUT | /api/admin/templates/{id}/status | 切换状态 |
| GET | /api/admin/categories | 分类列表 |
| POST | /api/admin/categories | 新增分类 |
| PUT | /api/admin/categories/{id} | 更新分类 |
| DELETE | /api/admin/categories/{id} | 删除分类 |
| GET | /api/admin/users | 用户列表 |
| GET | /api/admin/users/{id} | 用户详情 |
| PUT | /api/admin/users/{id}/vip | VIP授权 |
| PUT | /api/admin/users/{id}/status | 账号状态 |
| GET | /api/admin/stats/visit | 访问统计 |
| GET | /api/admin/stats/ai | AI调用统计 |
| GET | /api/admin/stats/vip | 会员统计 |

---

## 3. 前端新增页面

| 路径 | 页面 |
|------|------|
| /admin | 管理后台首页 |
| /admin/templates | 模板管理 |
| /admin/categories | 分类管理 |
| /admin/users | 用户管理 |
| /admin/config | 权限配置 |
| /admin/stats | 数据统计 |

---

## 4. 验收标准

- [ ] 模板CRUD正常
- [ ] 分类管理正常
- [ ] 用户管理正常
- [ ] VIP授权正常
- [ ] 数据统计准确
- [ ] 权限控制严格
