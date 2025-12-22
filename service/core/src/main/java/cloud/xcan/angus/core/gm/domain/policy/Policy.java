package cloud.xcan.angus.core.gm.domain.policy;

import cloud.xcan.angus.core.gm.domain.policy.enums.PolicyEffect;
import cloud.xcan.angus.core.gm.domain.policy.enums.PolicyStatus;
import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.List;
import java.util.Objects;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Policy domain entity
 */
@Setter
@Getter
@Entity
@Table(name = "gm_policy")
public class Policy extends TenantAuditingEntity<Policy, Long> {

  @Id
  private Long id;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "code", nullable = false, length = 50, unique = true)
  private String code;

  @Enumerated(EnumType.STRING)
  @Column(name = "effect", length = 20)
  private PolicyEffect effect;

  @Column(name = "description", length = 500)
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 20)
  private PolicyStatus status;

  @Column(name = "resource_ids", columnDefinition = "TEXT")
  private String resourceIds; // JSON array of interface IDs

  @Column(name = "priority")
  private Integer priority;

  @Column(name = "is_system", nullable = false)
  private Boolean isSystem = false;

  @Column(name = "is_default", nullable = false)
  private Boolean isDefault = false;

  @Column(name = "app_id", length = 50)
  private String appId;

  @Column(name = "permissions", columnDefinition = "TEXT")
  private String permissions; // JSON array of permission objects: [{"resource":"users","actions":["create","read"]}]

  // Non-persistent fields
  @Transient
  private List<Long> resourceIdList;

  @Transient
  private Long authorizationCount;

  @Transient
  private Long userCount;

  @Transient
  private String appName;

  @Transient
  private List<PermissionInfo> permissionList;

  @Transient
  private List<UserInfo> userList;

  @Data
  public static class PermissionInfo {
    private String resource;
    private String resourceName;
    private List<String> actions;
    private String description;
  }

  @Data
  public static class UserInfo {
    private Long id;
    private String name;
    private String email;
    private String avatar;
  }

  @Override
  public Long identity() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Policy policy)) {
      return false;
    }
    return Objects.equals(id, policy.id)
        && Objects.equals(code, policy.code);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, code);
  }
}
