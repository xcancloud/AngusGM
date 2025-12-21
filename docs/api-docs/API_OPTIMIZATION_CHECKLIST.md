# API接口优化检查清单

## 概述
本文档基于《API接口实现优化TODO》的17个优化步骤，对所有22个模块接口进行系统性检查和优化建议。

---

## 通用优化检查项（适用于所有模块）

### ✅ 第1步：接口文档与实现对比
- [ ] 接口路径与文档一致（如 `/api/v1/users`）
- [ ] HTTP方法正确（GET/POST/PUT/PATCH/DELETE）
- [ ] 请求参数与文档一致
- [ ] 响应结构与文档一致
- [ ] 路径参数、查询参数、请求体参数完整

### ✅ 第2步：HTTP方法和状态码规范
- [ ] **创建操作**：`POST` + `@ResponseStatus(HttpStatus.CREATED)` → 201
- [ ] **全量更新**：`PUT` + `@ResponseStatus(HttpStatus.OK)` → 200
- [ ] **部分更新/状态修改**：`PATCH` + `@ResponseStatus(HttpStatus.OK)` → 200
- [ ] **删除操作**：`DELETE` + `@ResponseStatus(HttpStatus.NO_CONTENT)` → 204，**无响应体**

### ✅ 第3步：Swagger注解规范
- [ ] 控制器类使用 `@Tag` 定义API分组
- [ ] 方法使用 `@Operation`（含 `operationId`, `summary`, `description`）
- [ ] 使用 `@ApiResponses` 定义响应状态码说明
- [ ] 路径参数使用 `@Parameter` 描述
- [ ] 查询参数对象使用 `@ParameterObject` 注解

### ✅ 第4步：DTO/VO参数检查
- [ ] DTO参数与接口文档请求体一致
- [ ] VO参数与接口文档响应体一致
- [ ] 补充缺失的DTO类（状态更新、批量操作等）
- [ ] 补充缺失的VO类（操作响应、统计信息等）
- [ ] 使用 `@Schema` 注解描述所有字段
- [ ] 使用Bean Validation注解进行参数校验，**不需要添加message属性**

### ✅ 第5步：DTO继承规范
- [ ] **分页查询DTO** 继承 `PageQuery` 类
- [ ] **创建/更新DTO** 直接定义，不继承
- [ ] PageQuery包含字段：pageNo、pageSize、infoScope、keyword、tenantId、createdBy、createdDate、modifiedBy、modifiedDate、filters

### ✅ 第6步：VO继承规范
- [ ] **详情VO** 继承 `TenantAuditingVo`，包含审计字段
- [ ] **列表VO** 可继承详情VO或单独定义
- [ ] 使用 `@NameJoinField` 自动填充关联名称

### ✅ 第7步：日期字段规范
- [ ] 所有日期字段使用 `LocalDateTime` 类型
- [ ] 不需要手动格式化（框架自动处理）
- [ ] 检查VO中的日期字段类型

### ✅ 第8步：枚举类规范
- [ ] 检查接口文档中的枚举值
- [ ] 将枚举值定义为枚举类，**不需要考虑枚举属性字段和Message**
- [ ] 枚举类放到对应Entity所在目录（`domain/xxx/enums/`）

### ✅ 第9步：门面层规范
- [ ] 门面层注入 `XXXCmd`（命令服务）和 `XXXQuery`（查询服务）
- [ ] 门面层查询**必须**使用 `XXXQuery` 类方法，**禁止**直接使用 `XXXRepo`
- [ ] 使用 `Assembler` 进行 DTO → Domain → VO 转换
- [ ] 使用 `buildVoPageResult()` 构建分页结果

**全文检索在门面层的使用**：

#### 需要全文检索时的写法
```java
@Service
public class UserFacadeImpl implements UserFacade {
    @Resource
    private UserCmd userCmd;
    @Resource
    private UserQuery userQuery;
    
    @Override
    public PageResult<UserListVo> list(UserFindDto dto) {
        // DTO有name或description字段，需要全文检索时
        GenericSpecification<User> spec = UserAssembler.getSpecification(dto);
        Page<User> page = userQuery.find(
            spec, 
            dto.tranPage(),
            dto.fullTextSearch,                    // 是否使用全文检索
            getMatchSearchFields(dto.getClass())   // 获取全文检索字段数组
        );
        return buildVoPageResult(page, UserAssembler::toListVo);
    }
    
    /**
     * 获取全文检索字段数组
     * 从Assembler的getSpecification中配置的matchSearchFields获取
     */
    private String[] getMatchSearchFields(Class<?> dtoClass) {
        // 根据DTO类型返回对应的全文检索字段
        // 例如：UserFindDto -> ["username", "email", "name"]
        // 这些字段应该与Assembler中matchSearchFields配置的字段一致
        return new String[]{"username", "email", "name"};
    }
}
```

#### 不需要全文检索时的写法
```java
@Service
public class TagFacadeImpl implements TagFacade {
    @Resource
    private TagCmd tagCmd;
    @Resource
    private TagQuery tagQuery;
    
    @Override
    public PageResult<TagListVo> list(TagFindDto dto) {
        // DTO没有name或description字段，不需要全文检索时
        GenericSpecification<Tag> spec = TagAssembler.getSpecification(dto);
        Page<Tag> page = tagQuery.find(spec, dto.tranPage());  // 只传两个参数
        return buildVoPageResult(page, TagAssembler::toListVo);
    }
}
```

**判断逻辑**：
1. **检查DTO字段**：如果DTO包含 `name` 或 `description` 字段，通常需要全文检索
2. **检查Assembler配置**：如果 `getSpecification()` 中配置了 `matchSearchFields()`，则需要全文检索
3. **检查业务需求**：如果需要对文本字段进行模糊搜索，建议使用全文检索

**关键点**：
- `dto.fullTextSearch` 字段来自 `PageQuery` 基类，用于控制是否启用全文检索
- `getMatchSearchFields()` 方法返回的字段数组必须与Assembler中配置的 `matchSearchFields` 一致
- 如果Query接口只提供了一个 `find()` 方法（两个参数），则不需要全文检索
- 如果Query接口提供了两个 `find()` 方法（三个参数和两个参数），则根据DTO情况选择调用

### ✅ 第10步：应用层Cmd服务规范
- [ ] Cmd接口定义写操作方法，参数和返回值使用Domain对象
- [ ] Cmd实现类继承 `CommCmd<Entity, Long>` 获得基础CRUD能力
- [ ] 使用 `@Transactional` 保证事务性
- [ ] 使用 `BizTemplate` 进行业务处理（checkParams + process）

### ✅ 第11步：应用层Query服务规范
- [ ] Query接口定义读操作方法
- [ ] 使用 `BizTemplate` 进行业务处理
- [ ] 查询后批量设置关联数据（避免N+1问题）
- [ ] 支持全文搜索和标准查询两种模式

**全文检索判断方式和关键写法**：

#### 判断是否需要全文检索
1. **需要全文检索的情况**：
   - DTO中包含 `name` 或 `description` 字段
   - 需要对这些字段进行模糊搜索
   - 数据量大，需要全文索引优化性能

2. **不需要全文检索的情况**：
   - DTO中没有 `name` 或 `description` 字段
   - 只需要精确匹配或范围查询
   - 数据量小，使用数据库索引即可

#### Query接口定义
```java
public interface UserQuery {
    // 需要支持全文检索时 - 提供两个重载方法
    Page<User> find(GenericSpecification<User> spec, PageRequest pageable,
        boolean fullTextSearch, String[] match);
    
    // 不需要支持全文检索时 - 只提供一个方法
    Page<User> find(GenericSpecification<User> spec, PageRequest pageable);
}
```

#### Query实现类写法
```java
@Service
public class UserQueryImpl implements UserQuery {
    @Resource
    private UserRepo userRepo;
    @Resource
    private UserSearchRepo userSearchRepo;  // 全文搜索仓储
    
    // 需要支持全文检索时
    @Override
    public Page<User> find(GenericSpecification<User> spec, PageRequest pageable,
        boolean fullTextSearch, String[] match) {
        return new BizTemplate<Page<User>>() {
            @Override
            protected Page<User> process() {
                return fullTextSearch
                    ? userSearchRepo.find(spec.getCriteria(), pageable, User.class, match)
                    : userRepo.findAll(spec, pageable);
            }
        }.execute();
    }
    
    // 不需要支持全文检索时
    @Override
    public Page<User> find(GenericSpecification<User> spec, PageRequest pageable) {
        return new BizTemplate<Page<User>>() {
            @Override
            protected Page<User> process() {
                return userRepo.findAll(spec, pageable);
            }
        }.execute();
    }
}
```

#### Assembler中配置全文检索字段
```java
public class UserAssembler {
    // DTO -> 查询条件
    public static GenericSpecification<User> getSpecification(UserFindDto dto) {
        Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
            .rangeSearchFields("id", "createdDate")           // 范围查询字段
            .orderByFields("id", "createdDate", "username")    // 排序字段
            .matchSearchFields("username", "email", "name")    // 全文检索字段（重要！）
            .build();
        return new GenericSpecification<>(filters);
    }
}
```

**关键点**：
- `matchSearchFields()` 方法配置的字段会用于全文检索
- 如果DTO中没有name或description字段，可以不配置matchSearchFields
- 全文检索字段通常包括：name、description、title、content等文本字段

### ✅ 第12步：Assembler组装器规范
- [ ] Assembler是静态工具类，提供静态方法进行转换
- [ ] `toCreateDomain()`: DTO → Domain（创建）
- [ ] `toUpdateDomain()`: DTO → Domain（更新）
- [ ] `toDetailVo()`: Domain → 详情VO
- [ ] `toListVo()`: Domain → 列表VO
- [ ] `getSpecification()`: DTO → 查询条件

### ✅ 第13步：方法顺序规范
**接口层、门面层、业务层统一**按以下顺序排列方法：
1. **创建**（create, add, insert）
2. **更新**（update, modify, edit）
3. **修改状态**（enable/disable, lock/unlock, activate, resetPassword等）
4. **删除**（delete, remove, batchDelete）
5. **查询详细**（getDetail, findById, get）
6. **查询列表**（list, find, search）
7. **查询统计**（getStats, count, statistics）

### ✅ 第14步：代码注释规范
- [ ] 不使用分组注释分隔不同类型的方法
- [ ] 代码注释使用英文（根据用户偏好）

### ✅ 第15步：JPA JSON字段规范
- [ ] JSON字段使用对象类型，不要手动编写序列化
- [ ] 使用 `@Type(JsonType.class)` 注解

### ✅ 第16步：异常处理规范
- [ ] 使用框架提供的统一异常类
- [ ] 资源不存在：`ResourceNotFound.of()`
- [ ] 资源已存在：`ResourceExisted.of()`
- [ ] 协议异常：`ProtocolException.of()`

### ✅ 第17步：分页响应规范
- [ ] 分页查询返回 `PageResult<T>` 类型
- [ ] 使用 `buildVoPageResult()` 构建分页结果
- [ ] 分页字段：`total`（总数）、`list`（数据列表）

---

## 各模块专项检查

### 01-认证授权模块 (`/api/v1/auth`)
**检查项**：
- [ ] 登录接口返回的token结构正确（accessToken, refreshToken, tokenType, expiresIn）
- [ ] 验证码接口返回base64图片格式正确
- [ ] 第三方登录支持wechat/github/google三种provider
- [ ] 短信/邮箱验证码发送接口返回codeKey和expireTime
- [ ] 邀请码验证接口返回valid、tenantName、inviterName、expireDate

**特殊注意**：
- 登录接口需要支持多种登录方式（account/sms/email）
- 验证码相关接口需要返回codeKey用于后续验证
- Token刷新接口需要返回新的accessToken和refreshToken

---

### 02-租户管理模块 (`/api/v1/tenants`)
**检查项**：
- [ ] 租户列表查询支持多条件筛选（keyword, status, type, accountType）
- [ ] 租户详情包含config和usage信息
- [ ] 租户配置更新接口独立（`/tenants/{id}/config`）
- [ ] 租户使用统计接口独立（`/tenants/{id}/usage`）
- [ ] 状态更新使用PATCH方法

**特殊注意**：
- 租户详情需要包含config（maxUsers, maxStorage, features）和usage（currentUsers, currentStorage）
- 租户配置和使用统计是独立的子资源

---

### 03-用户管理模块 (`/api/v1/users`)
**检查项**：
- [ ] 用户列表查询支持多条件筛选（keyword, status, role, department, isLocked, isOnline）
- [ ] 用户详情包含roles、groups、loginHistory等关联信息
- [ ] 批量删除接口使用DELETE方法，请求体包含userIds数组
- [ ] 邀请用户接口返回邀请码和邀请URL
- [ ] 当前用户信息接口（`/users/current`）

**特殊注意**：
- 用户详情需要包含roles数组（完整角色信息）、groups数组、loginHistory数组
- 邀请功能需要返回inviteCode和inviteUrl
- 当前用户相关接口路径为`/users/current`

---

### 04-部门管理模块 (`/api/v1/departments`)
**检查项**：
- [ ] 部门树形结构接口（`/departments/tree`）支持includeUsers参数
- [ ] 部门成员管理接口（`/departments/{id}/members`）
- [ ] 部门成员转移接口（`/departments/{id}/members/transfer`）
- [ ] 部门路径接口（`/departments/{id}/path`）返回path和pathArray
- [ ] 子部门列表接口（`/departments/{id}/children`）支持recursive参数

**特殊注意**：
- 部门树形结构是核心功能，需要递归构建children
- 部门成员管理包含添加、移除、批量移除、转移等操作
- 部门路径需要返回完整的层级路径

---

### 05-组管理模块 (`/api/v1/groups`)
**检查项**：
- [ ] 组列表查询支持type、status、ownerId筛选
- [ ] 组详情包含members数组
- [ ] 组成员管理接口（`/groups/{id}/members`）
- [ ] 组负责人设置接口（`/groups/{id}/owner`）
- [ ] 用户所在组列表接口（`/groups/user/{userId}`）

**特殊注意**：
- 组类型包括：项目组、职能组、临时组
- 组状态包括：活跃、归档
- 归档的组不能添加成员（需要业务校验）

---

### 06-应用管理模块 (`/api/v1/applications`)
**检查项**：
- [ ] 应用列表查询支持type、status、source、tags筛选
- [ ] 应用详情包含menuCount、roleCount、userCount统计
- [ ] 应用菜单管理接口（`/applications/{id}/menus`）
- [ ] 菜单排序调整接口（`/applications/{id}/menus/sort`）
- [ ] 可用标签列表接口（`/applications/available-tags`）

**特殊注意**：
- 应用菜单是树形结构，需要支持parentId
- 菜单排序需要批量更新sortOrder
- 标签管理需要独立的接口获取可用标签

---

### 07-服务管理模块 (`/api/v1/services`)
**检查项**：
- [ ] 服务列表返回服务名称和实例数组
- [ ] 服务实例包含健康检查URL、状态页URL等信息
- [ ] 服务实例健康状态接口（`/services/{serviceName}/instances/{instanceId}/health`）
- [ ] Eureka配置管理接口（`/services/eureka/config`）
- [ ] Eureka连接测试接口（`/services/eureka/test`）
- [ ] 服务调用统计接口（`/services/{serviceName}/stats`）

**特殊注意**：
- 服务管理需要与Eureka集成
- 服务实例状态包括：UP、DOWN、OUT_OF_SERVICE
- 健康检查返回详细的组件状态（diskSpace、db、redis等）

---

### 08-接口管理模块 (`/api/v1/interfaces`)
**检查项**：
- [ ] 接口列表查询支持serviceName、method、tags、deprecated筛选
- [ ] 接口详情包含parameters、responses完整信息
- [ ] 接口同步接口（`/interfaces/sync`、`/interfaces/sync-all`）
- [ ] 按服务获取接口列表（`/interfaces/service/{serviceName}`）
- [ ] 按标签获取接口列表（`/interfaces/tag/{tag}`）
- [ ] 接口废弃标记接口（`/interfaces/{id}/deprecate`）
- [ ] 接口调用统计接口（`/interfaces/{id}/stats`）

**特殊注意**：
- 接口同步需要从服务注册中心获取OpenAPI文档
- 接口详情需要包含完整的参数和响应定义
- 接口调用统计需要支持时间范围查询

---

### 09-标签管理模块 (`/api/v1/tags`)
**检查项**：
- [ ] 标签列表查询支持isSystem筛选
- [ ] 标签详情包含applications关联信息
- [ ] 所有标签列表接口（`/tags/all`）不分页
- [ ] 系统标签不能删除（需要业务校验）

**特殊注意**：
- 标签分为系统标签和自定义标签
- 标签使用计数（usageCount）需要实时统计
- 删除标签前需要检查是否正在使用

---

### 10-权限策略模块 (`/api/v1/roles`)
**检查项**：
- [ ] 角色列表查询支持appId、isSystem、isDefault筛选
- [ ] 角色详情包含permissions和users数组
- [ ] 角色权限配置接口（`/roles/{id}/permissions`）
- [ ] 角色用户列表接口（`/roles/{id}/users`）
- [ ] 默认角色设置接口（`/roles/{id}/default`）
- [ ] 可用权限列表接口（`/roles/available-permissions`）

**特殊注意**：
- 角色权限使用resource和actions结构
- 每个应用只能有一个默认角色（需要业务校验）
- 系统角色不能删除（需要业务校验）

---

### 11-授权管理模块 (`/api/v1/authorizations`)
**检查项**：
- [ ] 授权列表查询支持targetType、appId、roleId筛选
- [ ] 授权详情包含roles数组
- [ ] 批量授权接口（`/authorizations/batch`）
- [ ] 批量删除授权接口（`/authorizations/batch`）
- [ ] 指定目标授权信息接口（`/authorizations/target/{targetType}/{targetId}`）
- [ ] 添加/移除角色接口（`/authorizations/{id}/roles`）

**特殊注意**：
- 授权目标类型包括：user、department、group
- 授权可以包含多个角色
- 不能移除最后一个角色（需要业务校验）

---

### 12-消息通知模块 (`/api/v1/notifications`)
**检查项**：
- [ ] 通知列表查询支持category、source、status筛选
- [ ] 通知详情包含metadata信息
- [ ] 批量标记已读接口（`/notifications/read-batch`）
- [ ] 全部标记已读接口（`/notifications/read-all`）
- [ ] 通知渠道管理接口（`/notifications/channels`）
- [ ] 通知规则管理接口（`/notifications/rules`）
- [ ] 通知历史接口（`/notifications/history`）

**特殊注意**：
- 通知分类包括：all、unread、starred、archived
- 通知状态包括：success、warning、error、info
- 通知渠道支持email、slack等多种类型

---

### 13-备份恢复模块 (`/api/v1/backup`)
**检查项**：
- [ ] 备份记录列表查询支持type、status、日期范围筛选
- [ ] 备份详情包含backupContent、canRestore、restoreHistory
- [ ] 备份文件下载接口（`/backup/records/{id}/download`）
- [ ] 恢复备份接口（`/backup/records/{id}/restore`）
- [ ] 恢复进度查询接口（`/backup/restore/{restoreId}/progress`）
- [ ] 备份计划管理接口（`/backup/schedules`）
- [ ] 立即执行备份计划接口（`/backup/schedules/{id}/run`）

**特殊注意**：
- 备份类型包括：完整备份、增量备份、差异备份
- 恢复操作需要确认覆盖（confirmOverwrite）
- 恢复进度需要实时查询

---

### 14-短信消息模块 (`/api/v1/sms`)
**检查项**：
- [ ] 短信记录列表查询支持status、templateId、日期范围筛选
- [ ] 批量发送短信接口（`/sms/send-batch`）
- [ ] 短信模板管理接口（`/sms/templates`）
- [ ] 短信服务商配置接口（`/sms/providers`）
- [ ] 测试短信发送接口（`/sms/test`）

**特殊注意**：
- 短信模板支持参数替换（params）
- 短信服务商配置包含敏感信息（密码需要脱敏显示）
- 短信发送需要记录成本和状态

---

### 15-电子邮件模块 (`/api/v1/email`)
**检查项**：
- [ ] 邮件记录列表查询支持status、templateId、日期范围筛选
- [ ] 批量发送邮件接口（`/email/send-batch`）
- [ ] 自定义邮件发送接口（`/email/send-custom`）
- [ ] 邮件模板管理接口（`/email/templates`）
- [ ] SMTP配置接口（`/email/smtp`）
- [ ] SMTP连接测试接口（`/email/smtp/test`）
- [ ] 邮件打开/点击统计接口（`/email/{id}/stats`）

**特殊注意**：
- 邮件支持附件（attachments）
- 邮件模板支持HTML格式
- 邮件统计包括打开率、点击率等指标

---

### 16-安全设置模块 (`/api/v1/security`)
**检查项**：
- [ ] 安全设置概览接口（`/security/overview`）
- [ ] 密码策略配置接口（`/security/password-policy`）
- [ ] 双因素认证配置接口（`/security/two-factor`）
- [ ] IP白名单管理接口（`/security/ip-whitelist`）
- [ ] 会话配置接口（`/security/session`）
- [ ] 活跃会话列表接口（`/security/sessions/active`）
- [ ] 安全事件列表接口（`/security/events`）
- [ ] 安全审计统计接口（`/security/audit-stats`）

**特殊注意**：
- 密码策略包含复杂度要求、过期时间、锁定策略等
- 双因素认证支持sms、email、totp三种方式
- IP白名单支持单个IP和IP范围
- 安全事件需要支持风险等级筛选

---

### 17-系统监控模块 (`/api/v1/monitoring`)
**检查项**：
- [ ] 系统监控概览接口（`/monitoring/overview`）
- [ ] CPU使用率数据接口（`/monitoring/cpu`）支持period参数
- [ ] 内存使用数据接口（`/monitoring/memory`）支持period参数
- [ ] 磁盘使用数据接口（`/monitoring/disk`）
- [ ] 网络流量数据接口（`/monitoring/network`）支持period参数
- [ ] 进程列表接口（`/monitoring/processes`）
- [ ] 数据库连接池状态接口（`/monitoring/database/pools`）
- [ ] 数据库性能指标接口（`/monitoring/database/performance`）
- [ ] Redis监控数据接口（`/monitoring/cache/redis`）
- [ ] 告警规则管理接口（`/monitoring/alerts/rules`）
- [ ] 告警记录列表接口（`/monitoring/alerts/records`）
- [ ] 系统健康检查接口（`/monitoring/health`）

**特殊注意**：
- 监控数据需要支持时间周期查询（1h、6h、24h、7d、30d）
- 告警规则需要支持多种指标和阈值配置
- 系统健康检查需要检查所有组件状态

---

### 18-接口监控模块 (`/api/v1/interface-monitoring`)
**检查项**：
- [ ] 接口监控概览接口（`/interface-monitoring/overview`）
- [ ] 接口调用统计列表接口（`/interface-monitoring/stats`）
- [ ] 单个接口详细统计接口（`/interface-monitoring/stats/{serviceName}/{path}`）
- [ ] 慢请求列表接口（`/interface-monitoring/slow-requests`）
- [ ] 慢请求详情接口（`/interface-monitoring/slow-requests/{id}`）
- [ ] 错误请求列表接口（`/interface-monitoring/error-requests`）
- [ ] 错误请求详情接口（`/interface-monitoring/error-requests/{id}`）
- [ ] 实时QPS数据接口（`/interface-monitoring/realtime/qps`）
- [ ] 实时响应时间数据接口（`/interface-monitoring/realtime/response-time`）
- [ ] TOP排行接口（`/interface-monitoring/top/calls`、`/top/slow`、`/top/errors`）

**特殊注意**：
- 接口监控需要记录traceId用于链路追踪
- 慢请求详情需要包含SQL语句和执行时间
- 错误请求详情需要包含堆栈信息
- 实时监控数据需要支持WebSocket推送

---

### 19-LDAP集成模块 (`/api/v1/ldap`)
**检查项**：
- [ ] LDAP配置接口（`/ldap/config`）
- [ ] LDAP连接测试接口（`/ldap/test-connection`）
- [ ] LDAP认证测试接口（`/ldap/test-auth`）
- [ ] 手动同步LDAP用户接口（`/ldap/sync-users`）
- [ ] 同步历史记录接口（`/ldap/sync-history`）
- [ ] 同步详情接口（`/ldap/sync-history/{id}`）
- [ ] 搜索LDAP用户接口（`/ldap/search-users`）
- [ ] LDAP组列表接口（`/ldap/groups`）
- [ ] LDAP组成员接口（`/ldap/groups/{groupDN}/members`）
- [ ] 字段映射配置接口（`/ldap/field-mapping`）

**特殊注意**：
- LDAP配置包含敏感信息（bindPassword需要脱敏）
- 同步操作是异步任务，需要返回任务ID
- 字段映射用于LDAP属性与系统字段的对应关系

---

### 20-资源配额模块 (`/api/v1/quotas`)
**检查项**：
- [ ] 资源配额概览接口（`/quotas/overview`）
- [ ] 租户配额列表接口（`/quotas/tenants`）
- [ ] 租户配额详情接口（`/quotas/tenants/{tenantId}`）
- [ ] 更新租户配额接口（`/quotas/tenants/{tenantId}`）
- [ ] 配额模板列表接口（`/quotas/templates`）
- [ ] 创建配额模板接口（`/quotas/templates`）
- [ ] 应用模板到租户接口（`/quotas/tenants/{tenantId}/apply-template`）
- [ ] 配额告警规则接口（`/quotas/alert-rules`）
- [ ] 配额告警记录接口（`/quotas/alerts`）
- [ ] 处理配额告警接口（`/quotas/alerts/{id}/handle`）

**特殊注意**：
- 配额类型包括：users、storage、applications、apiCalls
- 配额详情需要包含趋势数据（trend）
- 配额告警需要支持warning和critical两个级别

---

### 21-审计日志模块 (`/api/v1/audit-logs`)
**检查项**：
- [ ] 审计日志统计接口（`/audit-logs/stats`）
- [ ] 审计日志列表接口（`/audit-logs`）支持多条件筛选
- [ ] 审计日志详情接口（`/audit-logs/{id}`）
- [ ] 导出审计日志接口（`/audit-logs/export`）
- [ ] 导出任务状态接口（`/audit-logs/export/{taskId}`）
- [ ] 用户操作历史接口（`/audit-logs/user/{userId}`）
- [ ] 模块操作统计接口（`/audit-logs/module-stats`）
- [ ] 敏感操作日志接口（`/audit-logs/sensitive`）
- [ ] 失败操作日志接口（`/audit-logs/failures`）
- [ ] 清理审计日志接口（`/audit-logs/cleanup`）
- [ ] 审计日志保留策略接口（`/audit-logs/retention-policy`）

**特殊注意**：
- 审计日志需要记录完整的请求和响应数据
- 日志级别包括：info、warning、error
- 导出操作是异步任务，需要返回任务ID
- 清理操作需要谨慎，支持按日期和级别清理

---

### 22-系统版本模块 (`/api/v1/system-version`)
**检查项**：
- [ ] 当前系统版本信息接口（`/system-version/current`）
- [ ] 版本历史列表接口（`/system-version/history`）
- [ ] 版本详情接口（`/system-version/history/{id}`）
- [ ] 变更日志接口（`/system-version/changelog`）
- [ ] 检查更新接口（`/system-version/check-update`）
- [ ] 系统依赖信息接口（`/system-version/dependencies`）
- [ ] 系统许可证信息接口（`/system-version/license`）
- [ ] 更新许可证接口（`/system-version/license`）
- [ ] 系统环境信息接口（`/system-version/environment`）
- [ ] 版本对比接口（`/system-version/compare`）

**特殊注意**：
- 版本信息需要包含components详细信息
- 变更日志需要支持按版本查询
- 许可证信息包含敏感信息（licenseKey需要脱敏）
- 版本对比需要支持features、breakingChanges、migrations

---

## 优先级分类

### 🔴 高优先级（必须立即处理）
1. **HTTP方法和状态码规范** - 影响API契约
2. **DTO/VO参数检查** - 影响数据一致性
3. **分页响应规范** - 影响前端展示
4. **异常处理规范** - 影响错误处理

### 🟡 中优先级（建议尽快处理）
1. **Swagger注解规范** - 影响API文档
2. **DTO继承规范** - 影响代码复用
3. **VO继承规范** - 影响数据转换
4. **门面层规范** - 影响架构一致性

### 🟢 低优先级（可以逐步优化）
1. **方法顺序规范** - 影响代码可读性
2. **代码注释规范** - 影响代码维护
3. **枚举类规范** - 影响类型安全

---

## 检查执行建议

### 阶段1：基础规范检查（1-2周）
- 完成所有模块的HTTP方法和状态码检查
- 完成所有模块的DTO/VO参数对比
- 完成所有模块的Swagger注解补充

### 阶段2：架构规范检查（2-3周）
- 完成所有模块的DTO继承规范
- 完成所有模块的VO继承规范
- 完成所有模块的门面层规范检查

### 阶段3：业务规范检查（3-4周）
- 完成所有模块的Assembler规范
- 完成所有模块的Cmd/Query服务规范
- 完成所有模块的异常处理规范

### 阶段4：优化完善（持续）
- 根据实际使用情况持续优化
- 补充缺失的功能接口
- 优化性能瓶颈

---

## 注意事项

1. **保持一致性**：所有模块遵循相同的规范和结构
2. **先查后改**：修改前先充分了解现有实现
3. **逐层修改**：按 REST → Facade → Cmd/Query 顺序修改
4. **编译验证**：每次修改后确保编译通过
5. **保留TODO**：未实现的业务逻辑用TODO标记
6. **文档同步**：代码修改后同步更新接口文档

---

---

## 📚 全文检索快速参考

### 判断是否需要全文检索

| 情况 | 是否需要全文检索 | 说明 |
|------|----------------|------|
| DTO包含 `name` 字段 | ✅ 需要 | 通常需要对名称进行模糊搜索 |
| DTO包含 `description` 字段 | ✅ 需要 | 通常需要对描述进行模糊搜索 |
| DTO包含 `title` 字段 | ✅ 需要 | 通常需要对标题进行模糊搜索 |
| DTO包含 `content` 字段 | ✅ 需要 | 通常需要对内容进行模糊搜索 |
| 只有精确匹配字段（id、status等） | ❌ 不需要 | 使用数据库索引即可 |
| 只有范围查询字段（日期、金额等） | ❌ 不需要 | 使用数据库索引即可 |

### 代码模板

#### 模板1：需要全文检索
```java
// 1. Query接口定义
Page<User> find(GenericSpecification<User> spec, PageRequest pageable,
    boolean fullTextSearch, String[] match);

// 2. Query实现
@Override
public Page<User> find(GenericSpecification<User> spec, PageRequest pageable,
    boolean fullTextSearch, String[] match) {
    return fullTextSearch
        ? userSearchRepo.find(spec.getCriteria(), pageable, User.class, match)
        : userRepo.findAll(spec, pageable);
}

// 3. Assembler配置
.matchSearchFields("username", "email", "name")  // 配置全文检索字段

// 4. Facade调用
Page<User> page = userQuery.find(
    spec, 
    dto.tranPage(),
    dto.fullTextSearch,
    getMatchSearchFields(dto.getClass())
);
```

#### 模板2：不需要全文检索
```java
// 1. Query接口定义
Page<Tag> find(GenericSpecification<Tag> spec, PageRequest pageable);

// 2. Query实现
@Override
public Page<Tag> find(GenericSpecification<Tag> spec, PageRequest pageable) {
    return tagRepo.findAll(spec, pageable);
}

// 3. Assembler配置（不配置matchSearchFields）
.rangeSearchFields("id", "createdDate")
.orderByFields("id", "createdDate")
// 不调用 .matchSearchFields()

// 4. Facade调用
Page<Tag> page = tagQuery.find(spec, dto.tranPage());
```

### 常见字段映射

| DTO字段 | 是否全文检索 | Assembler配置示例 |
|---------|------------|------------------|
| `name` | ✅ | `.matchSearchFields("name")` |
| `description` | ✅ | `.matchSearchFields("description")` |
| `title` | ✅ | `.matchSearchFields("title")` |
| `content` | ✅ | `.matchSearchFields("content")` |
| `keyword` | ✅ | `.matchSearchFields("name", "description")` |
| `username` | ✅ | `.matchSearchFields("username")` |
| `email` | ✅ | `.matchSearchFields("email")` |
| `id` | ❌ | `.rangeSearchFields("id")` |
| `status` | ❌ | 精确匹配，不需要配置 |
| `createdDate` | ❌ | `.rangeSearchFields("createdDate")` |

---

## 更新记录

- **2025-12-19**: 初始版本，基于API_IMPLEMENTATION_TODO.md生成
- **2025-12-19**: 补充全文检索和非全文检索的判断方式和关键写法
- 后续根据检查结果持续更新

