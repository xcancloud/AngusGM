# 后端模块实现指南 (Backend Module Implementation Guide)

## 概览 (Overview)

本指南提供了实现22个后端API模块的详细步骤和模板。每个模块遵循DDD分层架构和统一的代码规范。

## 已完成模块 (Completed Modules)

1. **02-租户管理模块** - 已优化 ✅
2. **12-消息通知模块** - 已实现 ✅  
3. **01-认证授权模块** - 部分实现 ⚠️
4. **03-用户管理模块** - 基础结构已创建 🚧

## 实现标准 (Implementation Standards)

### 1. 枚举类规范
- ✅ 枚举必须定义成enum类
- ❌ 不需要枚举属性字段
- ❌ 不需要Message属性

```java
// ✅ 正确示例
public enum TenantStatus {
  ENABLED,    // 已启用
  DISABLED    // 已禁用
}

// ❌ 错误示例 - 不要添加额外属性
public enum TenantStatus {
  ENABLED("enabled", "已启用"),  // ❌ 不需要
  DISABLED("disabled", "已禁用"); // ❌ 不需要
  
  private String code;    // ❌ 不需要
  private String message; // ❌ 不需要
}
```

### 2. REST方法顺序规范
必须按以下顺序组织REST控制器方法：

1. **创建 (Create)** - `POST /api/v1/xxx`
2. **更新 (Update)** - `PATCH /api/v1/xxx/{id}`
3. **修改状态 (Change Status)** - `POST /api/v1/xxx/{id}/enable|disable|status`
4. **删除 (Delete)** - `DELETE /api/v1/xxx/{id}`
5. **查询详细 (Get Detail)** - `GET /api/v1/xxx/{id}`
6. **查询列表 (List)** - `GET /api/v1/xxx`
7. **查询统计 (Get Stats)** - `GET /api/v1/xxx/stats`

### 3. JPA JSON字段规范
- ✅ 使用对象类型
- ✅ 使用 `@Type(JsonType.class)`
- ❌ 不要手动编写序列化代码

```java
// ✅ 正确示例
@Type(JsonType.class)
@Column(name = "tags", columnDefinition = "json")
private List<String> tags;

// ✅ 正确示例 - 复杂对象
@Type(JsonType.class)
@Column(name = "config", columnDefinition = "json")
private TenantConfig config;

// ❌ 错误示例 - 不要使用String并手动序列化
private String tags; // ❌
```

### 4. DTO校验注解规范
- ✅ 使用Jakarta Validation注解
- ❌ 不需要添加message属性

```java
// ✅ 正确示例
@NotBlank
@Size(max = 100)
@Schema(description = "租户名称", requiredMode = RequiredMode.REQUIRED)
private String name;

// ❌ 错误示例 - 不要添加message
@NotBlank(message = "名称不能为空")  // ❌ 不需要message
@Size(max = 100, message = "名称长度不能超过100") // ❌ 不需要message
private String name;
```

## 实现步骤模板 (Implementation Template)

### 步骤1: 创建领域层 (Domain Layer)

#### 1.1 创建枚举类

文件路径: `service/core/src/main/java/cloud/xcan/angus/core/gm/domain/xxx/enums/`

```java
package cloud.xcan.angus.core.gm.domain.xxx.enums;

/**
 * Xxx status enum
 */
public enum XxxStatus {
  /**
   * 已启用
   */
  ENABLED,
  
  /**
   * 已禁用
   */
  DISABLED
}
```

#### 1.2 创建领域实体

文件路径: `service/core/src/main/java/cloud/xcan/angus/core/gm/domain/xxx/Xxx.java`

```java
package cloud.xcan.angus.core.gm.domain.xxx;

import cloud.xcan.angus.core.gm.domain.xxx.enums.XxxStatus;
import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Objects;

@Setter
@Getter
@Entity
@Table(name = "gm_xxx")
public class Xxx extends TenantAuditingEntity<Xxx, Long> {

  @Id
  private Long id;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "code", nullable = false, length = 50, unique = true)
  private String code;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 20)
  private XxxStatus status;

  // Transient fields for associated data
  @Transient
  private Long someCount;

  @Override
  public Long identity() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Xxx xxx)) return false;
    return Objects.equals(id, xxx.id) && Objects.equals(code, xxx.code);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, code);
  }
}
```

#### 1.3 创建仓储接口

文件路径: `service/core/src/main/java/cloud/xcan/angus/core/gm/domain/xxx/XxxRepo.java`

```java
package cloud.xcan.angus.core.gm.domain.xxx;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface XxxRepo extends BaseRepository<Xxx, Long> {
  
  boolean existsByCode(String code);
  
  boolean existsByCodeAndIdNot(String code, Long id);
  
  long countByStatus(cloud.xcan.angus.core.gm.domain.xxx.enums.XxxStatus status);
}
```

### 步骤2: 创建基础设施层 (Infrastructure Layer)

#### 2.1 MySQL实现

文件路径: `service/core/src/main/java/cloud/xcan/angus/core/gm/infra/persistence/mysql/xxx/XxxRepoMysql.java`

```java
package cloud.xcan.angus.core.gm.infra.persistence.mysql.xxx;

import cloud.xcan.angus.core.gm.domain.xxx.Xxx;
import cloud.xcan.angus.core.gm.domain.xxx.XxxRepo;
import org.springframework.stereotype.Repository;

@Repository
public interface XxxRepoMysql extends XxxRepo {
  // Spring Data JPA will implement methods automatically
}
```

#### 2.2 PostgreSQL实现

文件路径: `service/core/src/main/java/cloud/xcan/angus/core/gm/infra/persistence/postgres/xxx/XxxRepoPostgres.java`

```java
package cloud.xcan.angus.core.gm.infra.persistence.postgres.xxx;

import cloud.xcan.angus.core.gm.domain.xxx.Xxx;
import cloud.xcan.angus.core.gm.domain.xxx.XxxRepo;
import org.springframework.stereotype.Repository;

@Repository
public interface XxxRepoPostgres extends XxxRepo {
  // Spring Data JPA will implement methods automatically
}
```

### 步骤3: 创建应用层 (Application Layer)

#### 3.1 命令服务 (Command Service)

文件路径: `service/core/src/main/java/cloud/xcan/angus/core/gm/application/cmd/xxx/XxxCmd.java`

```java
package cloud.xcan.angus.core.gm.application.cmd.xxx;

import cloud.xcan.angus.core.gm.domain.xxx.Xxx;

public interface XxxCmd {
  Xxx create(Xxx xxx);
  Xxx update(Xxx xxx);
  void delete(Long id);
  void enable(Long id);
  void disable(Long id);
}
```

文件路径: `service/core/src/main/java/cloud/xcan/angus/core/gm/application/cmd/xxx/impl/XxxCmdImpl.java`

```java
package cloud.xcan.angus.core.gm.application.cmd.xxx.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.xxx.XxxCmd;
import cloud.xcan.angus.core.gm.application.query.xxx.XxxQuery;
import cloud.xcan.angus.core.gm.domain.xxx.Xxx;
import cloud.xcan.angus.core.gm.domain.xxx.XxxRepo;
import cloud.xcan.angus.core.gm.domain.xxx.enums.XxxStatus;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.remote.message.http.ResourceExisted;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;

@Biz
public class XxxCmdImpl extends CommCmd<Xxx, Long> implements XxxCmd {

  @Resource
  private XxxRepo xxxRepo;

  @Resource
  private XxxQuery xxxQuery;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Xxx create(Xxx xxx) {
    return new BizTemplate<Xxx>() {
      @Override
      protected void checkParams() {
        if (xxxRepo.existsByCode(xxx.getCode())) {
          throw ResourceExisted.of("编码「{0}」已存在", new Object[]{xxx.getCode()});
        }
      }

      @Override
      protected Xxx process() {
        if (xxx.getStatus() == null) {
          xxx.setStatus(XxxStatus.ENABLED);
        }
        insert(xxx);
        return xxx;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Xxx update(Xxx xxx) {
    return new BizTemplate<Xxx>() {
      Xxx xxxDb;

      @Override
      protected void checkParams() {
        xxxDb = xxxQuery.findAndCheck(xxx.getId());
        if (xxx.getCode() != null && !xxx.getCode().equals(xxxDb.getCode())) {
          if (xxxRepo.existsByCodeAndIdNot(xxx.getCode(), xxx.getId())) {
            throw ResourceExisted.of("编码「{0}」已存在", new Object[]{xxx.getCode()});
          }
        }
      }

      @Override
      protected Xxx process() {
        update(xxx, xxxDb);
        return xxxDb;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        xxxQuery.findAndCheck(id);
      }

      @Override
      protected Void process() {
        xxxRepo.deleteById(id);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void enable(Long id) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        xxxQuery.findAndCheck(id);
      }

      @Override
      protected Void process() {
        Xxx xxx = new Xxx();
        xxx.setId(id);
        xxx.setStatus(XxxStatus.ENABLED);
        xxxRepo.save(xxx);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void disable(Long id) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        xxxQuery.findAndCheck(id);
      }

      @Override
      protected Void process() {
        Xxx xxx = new Xxx();
        xxx.setId(id);
        xxx.setStatus(XxxStatus.DISABLED);
        xxxRepo.save(xxx);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<Xxx, Long> getRepository() {
    return xxxRepo;
  }
}
```

#### 3.2 查询服务 (Query Service)

文件路径: `service/core/src/main/java/cloud/xcan/angus/core/gm/application/query/xxx/XxxQuery.java`

```java
package cloud.xcan.angus.core.gm.application.query.xxx;

import cloud.xcan.angus.core.gm.domain.xxx.Xxx;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface XxxQuery {
  Xxx findAndCheck(Long id);
  Page<Xxx> find(GenericSpecification<Xxx> spec, PageRequest pageable, 
                 boolean fullTextSearch, String[] match);
  boolean existsByCode(String code);
}
```

文件路径: `service/core/src/main/java/cloud/xcan/angus/core/gm/application/query/xxx/impl/XxxQueryImpl.java`

```java
package cloud.xcan.angus.core.gm.application.query.xxx.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.xxx.XxxQuery;
import cloud.xcan.angus.core.gm.domain.xxx.Xxx;
import cloud.xcan.angus.core.gm.domain.xxx.XxxRepo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Biz
public class XxxQueryImpl implements XxxQuery {

  @Resource
  private XxxRepo xxxRepo;

  @Override
  public Xxx findAndCheck(Long id) {
    return new BizTemplate<Xxx>() {
      @Override
      protected Xxx process() {
        return xxxRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of("资源未找到", new Object[]{}));
      }
    }.execute();
  }

  @Override
  public Page<Xxx> find(GenericSpecification<Xxx> spec, PageRequest pageable,
                        boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<Xxx>>() {
      @Override
      protected Page<Xxx> process() {
        return xxxRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public boolean existsByCode(String code) {
    return xxxRepo.existsByCode(code);
  }
}
```

### 步骤4: 创建接口层 (Interface Layer)

#### 4.1 创建DTOs

文件路径: `service/core/src/main/java/cloud/xcan/angus/core/gm/interfaces/xxx/facade/dto/XxxCreateDto.java`

```java
package cloud.xcan.angus.core.gm.interfaces.xxx.facade.dto;

import cloud.xcan.angus.core.gm.domain.xxx.enums.XxxStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "创建Xxx请求参数")
public class XxxCreateDto {

  @NotBlank
  @Size(max = 100)
  @Schema(description = "名称", requiredMode = RequiredMode.REQUIRED)
  private String name;

  @NotBlank
  @Size(max = 50)
  @Schema(description = "编码", requiredMode = RequiredMode.REQUIRED)
  private String code;

  @Schema(description = "状态")
  private XxxStatus status;
}
```

文件路径: `service/core/src/main/java/cloud/xcan/angus/core/gm/interfaces/xxx/facade/dto/XxxUpdateDto.java`

```java
package cloud.xcan.angus.core.gm.interfaces.xxx.facade.dto;

import cloud.xcan.angus.core.gm.domain.xxx.enums.XxxStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "更新Xxx请求参数")
public class XxxUpdateDto {

  @Size(max = 100)
  @Schema(description = "名称")
  private String name;

  @Schema(description = "状态")
  private XxxStatus status;
}
```

文件路径: `service/core/src/main/java/cloud/xcan/angus/core/gm/interfaces/xxx/facade/dto/XxxFindDto.java`

```java
package cloud.xcan.angus.core.gm.interfaces.xxx.facade.dto;

import cloud.xcan.angus.core.gm.domain.xxx.enums.XxxStatus;
import cloud.xcan.angus.spec.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "查询Xxx请求参数")
public class XxxFindDto extends PageQuery {

  @Schema(description = "搜索关键词")
  private String keyword;

  @Schema(description = "状态筛选")
  private XxxStatus status;

  @Schema(description = "排序字段", allowableValues = {"id", "createdDate", "modifiedDate", "name"})
  private String orderBy = "createdDate";
}
```

#### 4.2 创建VOs

文件路径: `service/core/src/main/java/cloud/xcan/angus/core/gm/interfaces/xxx/facade/vo/XxxDetailVo.java`

```java
package cloud.xcan.angus.core.gm.interfaces.xxx.facade.vo;

import cloud.xcan.angus.core.gm.domain.xxx.enums.XxxStatus;
import cloud.xcan.angus.spec.TenantAuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "Xxx详情")
public class XxxDetailVo extends TenantAuditingVo {

  @Schema(description = "ID")
  private Long id;

  @Schema(description = "名称")
  private String name;

  @Schema(description = "编码")
  private String code;

  @Schema(description = "状态")
  private XxxStatus status;
}
```

文件路径: `service/core/src/main/java/cloud/xcan/angus/core/gm/interfaces/xxx/facade/vo/XxxListVo.java`

```java
package cloud.xcan.angus.core.gm.interfaces.xxx.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Xxx列表项")
public class XxxListVo extends XxxDetailVo {
  // Inherits from XxxDetailVo
}
```

文件路径: `service/core/src/main/java/cloud/xcan/angus/core/gm/interfaces/xxx/facade/vo/XxxStatsVo.java`

```java
package cloud.xcan.angus.core.gm.interfaces.xxx.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Xxx统计数据")
public class XxxStatsVo {

  @Schema(description = "总数")
  private Long total;

  @Schema(description = "已启用数量")
  private Long enabled;

  @Schema(description = "已禁用数量")
  private Long disabled;
}
```

#### 4.3 创建Assembler

文件路径: `service/core/src/main/java/cloud/xcan/angus/core/gm/interfaces/xxx/facade/internal/assembler/XxxAssembler.java`

```java
package cloud.xcan.angus.core.gm.interfaces.xxx.facade.internal.assembler;

import static cloud.xcan.angus.spec.BizConstant.nullSafe;

import cloud.xcan.angus.core.gm.domain.xxx.Xxx;
import cloud.xcan.angus.core.gm.interfaces.xxx.facade.dto.XxxCreateDto;
import cloud.xcan.angus.core.gm.interfaces.xxx.facade.dto.XxxFindDto;
import cloud.xcan.angus.core.gm.interfaces.xxx.facade.dto.XxxUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.xxx.facade.vo.XxxDetailVo;
import cloud.xcan.angus.core.gm.interfaces.xxx.facade.vo.XxxListVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;

public class XxxAssembler {

  public static Xxx toCreateDomain(XxxCreateDto dto) {
    Xxx xxx = new Xxx();
    xxx.setName(dto.getName());
    xxx.setCode(dto.getCode());
    xxx.setStatus(dto.getStatus());
    return xxx;
  }

  public static Xxx toUpdateDomain(Long id, XxxUpdateDto dto) {
    Xxx xxx = new Xxx();
    xxx.setId(id);
    xxx.setName(dto.getName());
    xxx.setStatus(dto.getStatus());
    return xxx;
  }

  public static XxxDetailVo toDetailVo(Xxx xxx) {
    XxxDetailVo vo = new XxxDetailVo();
    vo.setId(xxx.getId());
    vo.setName(xxx.getName());
    vo.setCode(xxx.getCode());
    vo.setStatus(xxx.getStatus());
    
    // Set auditing fields
    vo.setTenantId(xxx.getTenantId());
    vo.setCreatedBy(xxx.getCreatedBy());
    vo.setCreator(xxx.getCreatedByName());
    vo.setCreatedDate(xxx.getCreatedDate());
    vo.setModifiedBy(xxx.getModifiedBy());
    vo.setModifier(xxx.getModifiedByName());
    vo.setModifiedDate(xxx.getModifiedDate());
    
    return vo;
  }

  public static XxxListVo toListVo(Xxx xxx) {
    XxxListVo vo = new XxxListVo();
    vo.setId(xxx.getId());
    vo.setName(xxx.getName());
    vo.setCode(xxx.getCode());
    vo.setStatus(xxx.getStatus());
    
    // Set auditing fields
    vo.setTenantId(xxx.getTenantId());
    vo.setCreatedBy(xxx.getCreatedBy());
    vo.setCreator(xxx.getCreatedByName());
    vo.setCreatedDate(xxx.getCreatedDate());
    vo.setModifiedBy(xxx.getModifiedBy());
    vo.setModifier(xxx.getModifiedByName());
    vo.setModifiedDate(xxx.getModifiedDate());
    
    return vo;
  }

  public static GenericSpecification<Xxx> getSpecification(XxxFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate", "modifiedDate")
        .orderByFields("id", "createdDate", "modifiedDate", "name")
        .matchSearchFields("name", "code")
        .build();
    return new GenericSpecification<>(filters);
  }
}
```

#### 4.4 创建Facade

文件路径: `service/core/src/main/java/cloud/xcan/angus/core/gm/interfaces/xxx/facade/XxxFacade.java`

```java
package cloud.xcan.angus.core.gm.interfaces.xxx.facade;

import cloud.xcan.angus.core.gm.interfaces.xxx.facade.dto.XxxCreateDto;
import cloud.xcan.angus.core.gm.interfaces.xxx.facade.dto.XxxFindDto;
import cloud.xcan.angus.core.gm.interfaces.xxx.facade.dto.XxxUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.xxx.facade.vo.XxxDetailVo;
import cloud.xcan.angus.core.gm.interfaces.xxx.facade.vo.XxxListVo;
import cloud.xcan.angus.core.gm.interfaces.xxx.facade.vo.XxxStatsVo;
import cloud.xcan.angus.remote.PageResult;

public interface XxxFacade {

  XxxDetailVo create(XxxCreateDto dto);

  XxxDetailVo update(Long id, XxxUpdateDto dto);

  void enable(Long id);

  void disable(Long id);

  void delete(Long id);

  XxxDetailVo getDetail(Long id);

  PageResult<XxxListVo> list(XxxFindDto dto);

  XxxStatsVo getStats();
}
```

文件路径: `service/core/src/main/java/cloud/xcan/angus/core/gm/interfaces/xxx/facade/internal/XxxFacadeImpl.java`

```java
package cloud.xcan.angus.core.gm.interfaces.xxx.facade.internal;

import static cloud.xcan.angus.spec.BizConstant.buildVoPageResult;
import static cloud.xcan.angus.spec.BizConstant.getMatchSearchFields;

import cloud.xcan.angus.core.gm.application.cmd.xxx.XxxCmd;
import cloud.xcan.angus.core.gm.application.query.xxx.XxxQuery;
import cloud.xcan.angus.core.gm.domain.xxx.Xxx;
import cloud.xcan.angus.core.gm.interfaces.xxx.facade.XxxFacade;
import cloud.xcan.angus.core.gm.interfaces.xxx.facade.dto.XxxCreateDto;
import cloud.xcan.angus.core.gm.interfaces.xxx.facade.dto.XxxFindDto;
import cloud.xcan.angus.core.gm.interfaces.xxx.facade.dto.XxxUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.xxx.facade.internal.assembler.XxxAssembler;
import cloud.xcan.angus.core.gm.interfaces.xxx.facade.vo.XxxDetailVo;
import cloud.xcan.angus.core.gm.interfaces.xxx.facade.vo.XxxListVo;
import cloud.xcan.angus.core.gm.interfaces.xxx.facade.vo.XxxStatsVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class XxxFacadeImpl implements XxxFacade {

  @Resource
  private XxxCmd xxxCmd;

  @Resource
  private XxxQuery xxxQuery;

  @Override
  public XxxDetailVo create(XxxCreateDto dto) {
    Xxx xxx = XxxAssembler.toCreateDomain(dto);
    Xxx saved = xxxCmd.create(xxx);
    return XxxAssembler.toDetailVo(saved);
  }

  @Override
  public XxxDetailVo update(Long id, XxxUpdateDto dto) {
    Xxx xxx = XxxAssembler.toUpdateDomain(id, dto);
    Xxx saved = xxxCmd.update(xxx);
    return XxxAssembler.toDetailVo(saved);
  }

  @Override
  public void enable(Long id) {
    xxxCmd.enable(id);
  }

  @Override
  public void disable(Long id) {
    xxxCmd.disable(id);
  }

  @Override
  public void delete(Long id) {
    xxxCmd.delete(id);
  }

  @Override
  public XxxDetailVo getDetail(Long id) {
    Xxx xxx = xxxQuery.findAndCheck(id);
    return XxxAssembler.toDetailVo(xxx);
  }

  @Override
  public PageResult<XxxListVo> list(XxxFindDto dto) {
    GenericSpecification<Xxx> spec = XxxAssembler.getSpecification(dto);
    Page<Xxx> page = xxxQuery.find(spec, dto.tranPage(),
        dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, XxxAssembler::toListVo);
  }

  @Override
  public XxxStatsVo getStats() {
    XxxStatsVo stats = new XxxStatsVo();
    // TODO: Implement statistics logic
    return stats;
  }
}
```

#### 4.5 创建REST控制器

文件路径: `service/core/src/main/java/cloud/xcan/angus/core/gm/interfaces/xxx/XxxRest.java`

```java
package cloud.xcan.angus.core.gm.interfaces.xxx;

import cloud.xcan.angus.core.gm.interfaces.xxx.facade.XxxFacade;
import cloud.xcan.angus.core.gm.interfaces.xxx.facade.dto.XxxCreateDto;
import cloud.xcan.angus.core.gm.interfaces.xxx.facade.dto.XxxFindDto;
import cloud.xcan.angus.core.gm.interfaces.xxx.facade.dto.XxxUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.xxx.facade.vo.XxxDetailVo;
import cloud.xcan.angus.core.gm.interfaces.xxx.facade.vo.XxxListVo;
import cloud.xcan.angus.core.gm.interfaces.xxx.facade.vo.XxxStatsVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Xxx", description = "Xxx管理 - Xxx的创建、管理、统计等功能")
@Validated
@RestController
@RequestMapping("/api/v1/xxxs")
public class XxxRest {

  @Resource
  private XxxFacade xxxFacade;

  // 创建
  @Operation(operationId = "createXxx", summary = "创建Xxx", description = "创建新Xxx")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Xxx创建成功")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<XxxDetailVo> create(
      @Valid @RequestBody XxxCreateDto dto) {
    return ApiLocaleResult.success(xxxFacade.create(dto));
  }

  // 更新
  @Operation(operationId = "updateXxx", summary = "更新Xxx", description = "更新Xxx基本信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PatchMapping("/{id}")
  public ApiLocaleResult<XxxDetailVo> update(
      @Parameter(description = "Xxx ID") @PathVariable Long id,
      @Valid @RequestBody XxxUpdateDto dto) {
    return ApiLocaleResult.success(xxxFacade.update(id, dto));
  }

  // 修改状态 - 启用
  @Operation(operationId = "enableXxx", summary = "启用Xxx", description = "启用指定Xxx")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "启用成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/{id}/enable")
  public ApiLocaleResult<Void> enable(
      @Parameter(description = "Xxx ID") @PathVariable Long id) {
    xxxFacade.enable(id);
    return ApiLocaleResult.success(null);
  }

  // 修改状态 - 禁用
  @Operation(operationId = "disableXxx", summary = "禁用Xxx", description = "禁用指定Xxx")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "禁用成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/{id}/disable")
  public ApiLocaleResult<Void> disable(
      @Parameter(description = "Xxx ID") @PathVariable Long id) {
    xxxFacade.disable(id);
    return ApiLocaleResult.success(null);
  }

  // 删除
  @Operation(operationId = "deleteXxx", summary = "删除Xxx", description = "删除指定Xxx")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "删除成功")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{id}")
  public void delete(
      @Parameter(description = "Xxx ID") @PathVariable Long id) {
    xxxFacade.delete(id);
  }

  // 查询详细
  @Operation(operationId = "getXxxDetail", summary = "获取Xxx详情", 
      description = "获取指定Xxx的详细信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Xxx详情获取成功"),
      @ApiResponse(responseCode = "404", description = "Xxx不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{id}")
  public ApiLocaleResult<XxxDetailVo> getDetail(
      @Parameter(description = "Xxx ID") @PathVariable Long id) {
    return ApiLocaleResult.success(xxxFacade.getDetail(id));
  }

  // 查询列表
  @Operation(operationId = "getXxxList", summary = "获取Xxx列表", 
      description = "获取Xxx列表，支持分页、搜索和筛选")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Xxx列表获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  public ApiLocaleResult<PageResult<XxxListVo>> list(
      @Valid @ParameterObject XxxFindDto dto) {
    return ApiLocaleResult.success(xxxFacade.list(dto));
  }

  // 查询统计
  @Operation(operationId = "getXxxStats", summary = "获取Xxx统计数据", 
      description = "获取Xxx统计数据，包括总数、启用/禁用数量等")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "统计数据获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/stats")
  public ApiLocaleResult<XxxStatsVo> getStats() {
    return ApiLocaleResult.success(xxxFacade.getStats());
  }
}
```

## 快速检查清单 (Quick Checklist)

完成一个模块后，使用此清单验证：

### 领域层
- [ ] 枚举类已创建（无额外属性）
- [ ] 实体类使用 @Enumerated(EnumType.STRING)
- [ ] 仓储接口继承 BaseRepository
- [ ] 实体重写 identity(), equals(), hashCode()

### 基础设施层
- [ ] MySQL 实现已创建
- [ ] PostgreSQL 实现已创建
- [ ] 搜索仓储（如需要）已创建

### 应用层
- [ ] 命令服务接口和实现
- [ ] 查询服务接口和实现
- [ ] 使用 BizTemplate 进行业务处理
- [ ] 命令方法添加 @Transactional

### 接口层
- [ ] CreateDto, UpdateDto, FindDto 已创建
- [ ] DetailVo, ListVo, StatsVo 已创建
- [ ] Assembler 已创建
- [ ] Facade 接口和实现已创建
- [ ] REST 控制器已创建
- [ ] 方法顺序正确：创建→更新→修改状态→删除→查询详细→查询列表→查询统计

### 验证规范
- [ ] DTO 校验注解无 message 属性
- [ ] 所有枚举为 enum 类
- [ ] JSON 字段使用对象类型
- [ ] REST 方法使用正确的 HTTP 状态码

## 常见问题 (FAQ)

### Q: 何时使用 TenantAuditingEntity vs TenantEntity?
A: 需要审计字段（创建人、创建时间、修改人、修改时间）时使用 TenantAuditingEntity

### Q: 何时需要创建 SearchRepo?
A: 需要全文搜索功能时创建，继承 CustomBaseRepository

### Q: Cmd 和 Query 如何选择?
A: 写操作（增删改）放 Cmd，读操作（查询）放 Query

### Q: 如何处理关联数据?
A: 使用 @Transient 字段，在 Query 层设置，避免 N+1 问题

### Q: 枚举如何映射到数据库?
A: 使用 @Enumerated(EnumType.STRING) 存储枚举名称

## 总结

遵循此模板可以确保：
1. 代码结构统一
2. 符合 DDD 分层架构
3. 满足所有代码规范要求
4. 便于维护和扩展

每个模块估计需要创建 50-80 个文件。建议先完成基础模块（用户、部门、组、标签），再实现其他模块。
