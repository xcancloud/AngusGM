package cloud.xcan.angus.api.commonlink.app;

import cloud.xcan.angus.api.enums.AppType;
import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.api.enums.OpenStage;
import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import cloud.xcan.angus.core.jpa.multitenancy.TenantListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Entity
@Table(name = "app")
@EntityListeners({TenantListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class AppInfo extends TenantAuditingEntity<AppInfo, Long> {

  @Id
  private Long id;

  private String code;

  private String name;

  @Column(name = "show_name")
  private String showName;

  private String icon;

  @Enumerated(EnumType.STRING)
  private AppType type;

  @Enumerated(EnumType.STRING)
  @Column(name = "edition_type")
  private EditionType editionType;

  private String description;

  @Column(name = "auth_ctrl")
  private Boolean authCtrl;

  private Boolean enabled;

  private String url;

  /**
   * Sorting value, the smaller the value, the higher
   */
  private Integer sequence;

  private String version;

  @Enumerated(EnumType.STRING)
  @Column(name = "open_stage")
  private OpenStage openStage;

  @Column(name = "client_id")
  private String clientId;

  public boolean isCloudApp() {
    return Objects.nonNull(type) && type.equals(AppType.CLOUD_APP);
  }

  public boolean isBaseApp() {
    return Objects.nonNull(type) && type.equals(AppType.BASE_APP);
  }

  public boolean isOpApp() {
    return Objects.nonNull(type) && type.equals(AppType.OP_APP);
  }

  public boolean isTenantApp() {
    return isCloudApp() || isBaseApp();
  }

  @Override
  public Long identity() {
    return this.id;
  }
}
