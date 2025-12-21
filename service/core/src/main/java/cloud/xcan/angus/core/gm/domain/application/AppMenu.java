package cloud.xcan.angus.core.gm.domain.application;

import cloud.xcan.angus.core.gm.domain.application.enums.AppMenuType;
import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * Application Menu domain entity
 * </p>
 */
@Setter
@Getter
@Entity
@Table(name = "app_func")
public class AppMenu extends TenantAuditingEntity<AppMenu, Long> {

  @Id
  private Long id;

  @Column(name = "app_id", nullable = false)
  private Long appId;

  @Column(name = "code", nullable = false, length = 80)
  private String code;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "show_name", length = 40)
  private String showName;

  @Column(name = "pid")
  private Long parentId;

  @Column(name = "icon", length = 200)
  private String icon;

  @Column(name = "url", length = 200)
  private String path;

  @Column(name = "sequence")
  private Integer sortOrder;

  @Column(name = "enabled")
  private Boolean isVisible;

  @Column(name = "description", length = 200)
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", length = 10)
  private AppMenuType type;

  @Column(name = "auth_ctrl")
  private Boolean authCtrl;

  @Column(name = "permission", length = 200)
  private String permission;

  @Override
  public Long identity() {
    return id;
  }
}

