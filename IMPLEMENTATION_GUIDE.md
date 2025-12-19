# Backend Module Implementation Guide

This guide provides a step-by-step template for implementing the remaining 20 backend API modules following the established DDD architecture pattern.

## Reference Implementation

The **Tenant Management Module** (02) serves as the complete reference implementation. All code has been created following DDD principles and can be found in:
- `service/core/src/main/java/cloud/xcan/angus/core/gm/domain/tenant/`
- `service/core/src/main/java/cloud/xcan/angus/core/gm/application/{cmd,query}/tenant/`
- `service/core/src/main/java/cloud/xcan/angus/core/gm/infra/persistence/{mysql,postgres}/tenant/`
- `service/core/src/main/java/cloud/xcan/angus/core/gm/interfaces/tenant/`

## Implementation Steps for Each Module

### Step 1: Create Domain Layer

#### 1.1 Create Entity (`domain/{module}/{Entity}.java`)

```java
@Setter
@Getter
@Entity
@Table(name = "gm_{table_name}")
public class {Entity} extends TenantAuditingEntity<{Entity}, Long> {

  @Id
  private Long id;

  // Add entity fields based on API documentation
  @Column(name = "field_name", nullable = false, length = 100)
  private String fieldName;

  // Non-persistent fields for associated data
  @Transient
  private Long associatedCount;

  @Override
  public Long identity() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof {Entity} entity)) return false;
    return Objects.equals(id, entity.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
```

#### 1.2 Create Repository Interface (`domain/{module}/{Entity}Repo.java`)

```java
@NoRepositoryBean
public interface {Entity}Repo extends BaseRepository<{Entity}, Long> {
  
  // Standard query methods
  boolean existsByFieldName(String fieldName);
  boolean existsByFieldNameAndIdNot(String fieldName, Long id);
  long countByStatus(String status);
  
  // Add custom queries as needed
}
```

### Step 2: Create Infrastructure Layer

#### 2.1 MySQL Repository (`infra/persistence/mysql/{module}/{Entity}RepoMysql.java`)

```java
@Repository
public interface {Entity}RepoMysql extends {Entity}Repo {
  // Spring Data JPA auto-implements all methods
}
```

#### 2.2 PostgreSQL Repository (`infra/persistence/postgres/{module}/{Entity}RepoPostgres.java`)

```java
@Repository
public interface {Entity}RepoPostgres extends {Entity}Repo {
  // Spring Data JPA auto-implements all methods
}
```

### Step 3: Create Interface Layer - DTOs and VOs

#### 3.1 Create DTO (`interfaces/{module}/facade/dto/{Entity}CreateDto.java`)

```java
@Data
@Schema(description = "创建{中文名}请求参数")
public class {Entity}CreateDto {

  @NotBlank
  @Size(max = 100)
  @Schema(description = "字段描述", requiredMode = RequiredMode.REQUIRED)
  private String fieldName;

  // Add all fields from API documentation
}
```

#### 3.2 Update DTO (`interfaces/{module}/facade/dto/{Entity}UpdateDto.java`)

```java
@Data
@Schema(description = "更新{中文名}请求参数")
public class {Entity}UpdateDto {
  
  @Size(max = 100)
  @Schema(description = "字段描述")
  private String fieldName;

  // Add updateable fields
}
```

#### 3.3 Find DTO (`interfaces/{module}/facade/dto/{Entity}FindDto.java`)

```java
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "查询{中文名}请求参数")
public class {Entity}FindDto extends PageQuery {

  @Schema(description = "搜索关键词")
  private String keyword;

  @Schema(description = "状态筛选")
  private String status;

  @Schema(description = "排序字段")
  private String orderBy = "createdDate";
}
```

#### 3.4 Detail VO (`interfaces/{module}/facade/vo/{Entity}DetailVo.java`)

```java
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "{中文名}详情")
public class {Entity}DetailVo extends TenantAuditingVo {

  @Schema(description = "ID")
  private Long id;

  // Add all fields from API documentation
}
```

#### 3.5 List VO (`interfaces/{module}/facade/vo/{Entity}ListVo.java`)

```java
@Data
@Schema(description = "{中文名}列表项")
public class {Entity}ListVo extends {Entity}DetailVo {
  // Inherits all fields or define subset
}
```

#### 3.6 Stats VO (if needed) (`interfaces/{module}/facade/vo/{Entity}StatsVo.java`)

```java
@Data
@Schema(description = "{中文名}统计数据")
public class {Entity}StatsVo {
  
  @Schema(description = "总数")
  private Long total;

  // Add statistical fields
}
```

### Step 4: Create Assembler

#### 4.1 Assembler (`interfaces/{module}/facade/internal/assembler/{Entity}Assembler.java`)

```java
public class {Entity}Assembler {

  public static {Entity} toCreateDomain({Entity}CreateDto dto) {
    {Entity} entity = new {Entity}();
    entity.setFieldName(dto.getFieldName());
    // Map all fields
    return entity;
  }

  public static {Entity} toUpdateDomain(Long id, {Entity}UpdateDto dto) {
    {Entity} entity = new {Entity}();
    entity.setId(id);
    entity.setFieldName(dto.getFieldName());
    // Map updateable fields
    return entity;
  }

  public static {Entity}DetailVo toDetailVo({Entity} entity) {
    {Entity}DetailVo vo = new {Entity}DetailVo();
    vo.setId(entity.getId());
    vo.setFieldName(entity.getFieldName());
    
    // Set auditing fields
    vo.setTenantId(entity.getTenantId());
    vo.setCreatedBy(entity.getCreatedBy());
    vo.setCreator(entity.getCreatedByName());
    vo.setCreatedDate(entity.getCreatedDate());
    vo.setModifiedBy(entity.getModifiedBy());
    vo.setModifier(entity.getModifiedByName());
    vo.setModifiedDate(entity.getModifiedDate());

    return vo;
  }

  public static {Entity}ListVo toListVo({Entity} entity) {
    // Similar to toDetailVo
  }

  public static GenericSpecification<{Entity}> getSpecification({Entity}FindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate", "modifiedDate")
        .orderByFields("id", "createdDate", "modifiedDate")
        .matchSearchFields("fieldName")
        .build();
    return new GenericSpecification<>(filters);
  }
}
```

### Step 5: Create Application Layer

#### 5.1 Command Interface (`application/cmd/{module}/{Entity}Cmd.java`)

```java
public interface {Entity}Cmd {
  {Entity} create({Entity} entity);
  {Entity} update({Entity} entity);
  void delete(Long id);
  // Add other command operations
}
```

#### 5.2 Command Implementation (`application/cmd/{module}/impl/{Entity}CmdImpl.java`)

```java
@Biz
public class {Entity}CmdImpl extends CommCmd<{Entity}, Long> implements {Entity}Cmd {

  @Resource
  private {Entity}Repo {entity}Repo;

  @Resource
  private {Entity}Query {entity}Query;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public {Entity} create({Entity} entity) {
    return new BizTemplate<{Entity}>() {
      @Override
      protected void checkParams() {
        // Validation logic
        if ({entity}Repo.existsByFieldName(entity.getFieldName())) {
          throw ResourceExisted.of("资源已存在", new Object[]{});
        }
      }

      @Override
      protected {Entity} process() {
        // Set defaults
        if (entity.getStatus() == null) {
          entity.setStatus("已启用");
        }
        insert(entity);
        return entity;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public {Entity} update({Entity} entity) {
    return new BizTemplate<{Entity}>() {
      {Entity} entityDb;

      @Override
      protected void checkParams() {
        entityDb = {entity}Query.findAndCheck(entity.getId());
      }

      @Override
      protected {Entity} process() {
        update(entity, entityDb);
        return entityDb;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        {entity}Query.findAndCheck(id);
      }

      @Override
      protected Void process() {
        {entity}Repo.deleteById(id);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<{Entity}, Long> getRepository() {
    return {entity}Repo;
  }
}
```

#### 5.3 Query Interface (`application/query/{module}/{Entity}Query.java`)

```java
public interface {Entity}Query {
  {Entity} findAndCheck(Long id);
  Page<{Entity}> find(GenericSpecification<{Entity}> spec, PageRequest pageable);
  {Entity}StatsVo getStats(); // if needed
}
```

#### 5.4 Query Implementation (`application/query/{module}/impl/{Entity}QueryImpl.java`)

```java
@Biz
public class {Entity}QueryImpl implements {Entity}Query {

  @Resource
  private {Entity}Repo {entity}Repo;

  @Override
  public {Entity} findAndCheck(Long id) {
    return new BizTemplate<{Entity}>() {
      @Override
      protected {Entity} process() {
        {Entity} entity = {entity}Repo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of("资源未找到", new Object[]{}));
        
        // Set associated data
        // TODO: Set related counts/data

        return entity;
      }
    }.execute();
  }

  @Override
  public Page<{Entity}> find(GenericSpecification<{Entity}> spec, PageRequest pageable) {
    return new BizTemplate<Page<{Entity}>>() {
      @Override
      protected Page<{Entity}> process() {
        Page<{Entity}> page = {entity}Repo.findAll(spec, pageable);
        
        // Set associated data for each entity
        if (page.hasContent()) {
          page.getContent().forEach(entity -> {
            // TODO: Batch query for associated data
          });
        }

        return page;
      }
    }.execute();
  }
}
```

### Step 6: Create Facade Layer

#### 6.1 Facade Interface (`interfaces/{module}/facade/{Entity}Facade.java`)

```java
public interface {Entity}Facade {
  {Entity}DetailVo create({Entity}CreateDto dto);
  {Entity}DetailVo update(Long id, {Entity}UpdateDto dto);
  void delete(Long id);
  {Entity}DetailVo getDetail(Long id);
  PageResult<{Entity}ListVo> list({Entity}FindDto dto);
  // Add other operations
}
```

#### 6.2 Facade Implementation (`interfaces/{module}/facade/internal/{Entity}FacadeImpl.java`)

```java
@Service
public class {Entity}FacadeImpl implements {Entity}Facade {

  @Resource
  private {Entity}Cmd {entity}Cmd;

  @Resource
  private {Entity}Query {entity}Query;

  @Override
  public {Entity}DetailVo create({Entity}CreateDto dto) {
    {Entity} entity = {Entity}Assembler.toCreateDomain(dto);
    {Entity} saved = {entity}Cmd.create(entity);
    return {Entity}Assembler.toDetailVo(saved);
  }

  @Override
  public {Entity}DetailVo update(Long id, {Entity}UpdateDto dto) {
    {Entity} entity = {Entity}Assembler.toUpdateDomain(id, dto);
    {Entity} saved = {entity}Cmd.update(entity);
    return {Entity}Assembler.toDetailVo(saved);
  }

  @Override
  public void delete(Long id) {
    {entity}Cmd.delete(id);
  }

  @Override
  public {Entity}DetailVo getDetail(Long id) {
    {Entity} entity = {entity}Query.findAndCheck(id);
    return {Entity}Assembler.toDetailVo(entity);
  }

  @Override
  public PageResult<{Entity}ListVo> list({Entity}FindDto dto) {
    GenericSpecification<{Entity}> spec = {Entity}Assembler.getSpecification(dto);
    Page<{Entity}> page = {entity}Query.find(spec, dto.tranPage());
    return buildVoPageResult(page, {Entity}Assembler::toListVo);
  }
}
```

### Step 7: Create REST Controller

#### 7.1 REST Controller (`interfaces/{module}/{Entity}Rest.java`)

```java
@Tag(name = "{Entity}", description = "{中文名}管理 - {功能描述}")
@Validated
@RestController
@RequestMapping("/api/v1/{module-plural}")
public class {Entity}Rest {

  @Resource
  private {Entity}Facade {entity}Facade;

  @Operation(operationId = "get{Entity}List", summary = "获取{中文名}列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "列表获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  public ApiLocaleResult<PageResult<{Entity}ListVo>> list(
      @Valid @ParameterObject {Entity}FindDto dto) {
    return ApiLocaleResult.success({entity}Facade.list(dto));
  }

  @Operation(operationId = "create{Entity}", summary = "创建{中文名}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "创建成功")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<{Entity}DetailVo> create(
      @Valid @RequestBody {Entity}CreateDto dto) {
    return ApiLocaleResult.success({entity}Facade.create(dto));
  }

  @Operation(operationId = "get{Entity}Detail", summary = "获取{中文名}详情")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "详情获取成功"),
      @ApiResponse(responseCode = "404", description = "资源不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{id}")
  public ApiLocaleResult<{Entity}DetailVo> getDetail(
      @Parameter(description = "ID") @PathVariable Long id) {
    return ApiLocaleResult.success({entity}Facade.getDetail(id));
  }

  @Operation(operationId = "update{Entity}", summary = "更新{中文名}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PatchMapping("/{id}")
  public ApiLocaleResult<{Entity}DetailVo> update(
      @Parameter(description = "ID") @PathVariable Long id,
      @Valid @RequestBody {Entity}UpdateDto dto) {
    return ApiLocaleResult.success({entity}Facade.update(id, dto));
  }

  @Operation(operationId = "delete{Entity}", summary = "删除{中文名}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "删除成功")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{id}")
  public void delete(
      @Parameter(description = "ID") @PathVariable Long id) {
    {entity}Facade.delete(id);
  }
}
```

## Module Implementation Checklist

Use this checklist for each module:

- [ ] Step 1: Create Entity and Repository in domain layer
- [ ] Step 2: Create MySQL and PostgreSQL repository implementations
- [ ] Step 3: Create all DTOs and VOs
- [ ] Step 4: Create Assembler
- [ ] Step 5: Create Command service interface and implementation
- [ ] Step 6: Create Query service interface and implementation
- [ ] Step 7: Create Facade interface and implementation
- [ ] Step 8: Create REST controller
- [ ] Step 9: Test compilation
- [ ] Step 10: Create database migration scripts (if needed)

## Remaining Modules to Implement

1. 01-认证授权模块 (Authentication & Authorization) - **Partial - needs completion**
2. ~~02-租户管理模块~~ (Tenant Management) - **COMPLETED ✓**
3. 03-用户管理模块 (User Management)
4. 04-部门管理模块 (Department Management)
5. 05-组管理模块 (Group Management)
6. 06-应用管理模块 (Application Management)
7. 07-服务管理模块 (Service Management)
8. 08-接口管理模块 (API Management)
9. 09-标签管理模块 (Tag Management)
10. 10-权限策略模块 (Permission Policy)
11. 11-授权管理模块 (Authorization Management)
12. 12-消息通知模块 (Message Notification) - **Partial - exists**
13. 13-备份恢复模块 (Backup & Recovery)
14. 14-短信消息模块 (SMS Message)
15. 15-电子邮件模块 (Email Module)
16. 16-安全设置模块 (Security Settings)
17. 17-系统监控模块 (System Monitoring)
18. 18-接口监控模块 (API Monitoring)
19. 19-LDAP集成模块 (LDAP Integration)
20. 20-资源配额模块 (Resource Quota)
21. 21-审计日志模块 (Audit Log)
22. 22-系统版本模块 (System Version)

## Tips and Best Practices

1. **Always start with the API documentation** for each module to understand fields and operations
2. **Follow the naming conventions** strictly for consistency
3. **Use the Tenant module as reference** when in doubt
4. **Test compilation** after completing each layer
5. **Implement modules in order of dependency** (foundation modules first)
6. **Reuse patterns** - the structure is the same for all modules
7. **Add TODOs** for business logic that needs cross-module data
8. **Document any deviations** from the standard pattern

## File Naming Conventions

- Domain Entity: `{Entity}.java`
- Repository: `{Entity}Repo.java`
- Repository MySQL: `{Entity}RepoMysql.java`
- Repository PostgreSQL: `{Entity}RepoPostgres.java`
- Create DTO: `{Entity}CreateDto.java`
- Update DTO: `{Entity}UpdateDto.java`
- Find DTO: `{Entity}FindDto.java`
- Detail VO: `{Entity}DetailVo.java`
- List VO: `{Entity}ListVo.java`
- Stats VO: `{Entity}StatsVo.java`
- Assembler: `{Entity}Assembler.java`
- Command: `{Entity}Cmd.java`
- Command Impl: `{Entity}CmdImpl.java`
- Query: `{Entity}Query.java`
- Query Impl: `{Entity}QueryImpl.java`
- Facade: `{Entity}Facade.java`
- Facade Impl: `{Entity}FacadeImpl.java`
- REST: `{Entity}Rest.java`

## Package Structure

```
cloud.xcan.angus.core.gm
├── domain
│   └── {module}
│       ├── {Entity}.java
│       └── {Entity}Repo.java
├── application
│   ├── cmd
│   │   └── {module}
│   │       ├── {Entity}Cmd.java
│   │       └── impl
│   │           └── {Entity}CmdImpl.java
│   └── query
│       └── {module}
│           ├── {Entity}Query.java
│           └── impl
│               └── {Entity}QueryImpl.java
├── infra
│   └── persistence
│       ├── mysql
│       │   └── {module}
│       │       └── {Entity}RepoMysql.java
│       └── postgres
│           └── {module}
│               └── {Entity}RepoPostgres.java
└── interfaces
    └── {module}
        ├── {Entity}Rest.java
        └── facade
            ├── {Entity}Facade.java
            ├── dto
            │   ├── {Entity}CreateDto.java
            │   ├── {Entity}UpdateDto.java
            │   └── {Entity}FindDto.java
            ├── vo
            │   ├── {Entity}DetailVo.java
            │   ├── {Entity}ListVo.java
            │   └── {Entity}StatsVo.java
            └── internal
                ├── {Entity}FacadeImpl.java
                └── assembler
                    └── {Entity}Assembler.java
```

## Next Steps

1. Review this guide thoroughly
2. Start with foundation modules (User, Department, Group)
3. Follow the step-by-step template for each module
4. Refer to Tenant module implementation for any clarifications
5. Test each module after completion
6. Move to next phase of modules after foundation is complete

---

*This guide is based on the completed Tenant Management Module implementation and follows DDD architecture principles.*
