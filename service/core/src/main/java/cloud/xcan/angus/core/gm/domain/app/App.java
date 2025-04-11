package cloud.xcan.angus.core.gm.domain.app;

import cloud.xcan.angus.api.commonlink.api.Api;
import cloud.xcan.angus.api.commonlink.app.AppType;
import cloud.xcan.angus.api.commonlink.app.OpenStage;
import cloud.xcan.angus.api.commonlink.app.func.AppFunc;
import cloud.xcan.angus.api.commonlink.app.tag.WebTag;
import cloud.xcan.angus.spec.experimental.Resources;
import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.core.gm.domain.policy.AuthPolicy;
import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import cloud.xcan.angus.core.jpa.multitenancy.TenantListener;
import cloud.xcan.angus.spec.annotations.DoInFuture;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;


@Table(name = "app")
@EntityListeners({TenantListener.class})
@Setter
@Getter
@Entity
@Accessors(chain = true)
public class App extends TenantAuditingEntity<App, Long> implements Resources<Long> {

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

  /**
   * May include disabled api
   */
  @DoInFuture("Use authority table instead")
  @Type(JsonType.class)
  @Column(columnDefinition = "json", name = "api_ids")
  private LinkedHashSet<Long> apiIds;

  private String version;

  @Enumerated(EnumType.STRING)
  @Column(name = "open_stage")
  private OpenStage openStage;

  @Column(name = "client_id")
  private String clientId;

  @Transient
  private List<AppFunc> appFunc;
  @Transient
  private List<Api> apis;
  @Transient
  private List<WebTag> tags;
  @Transient
  private List<AuthPolicy> policies;
  @Transient
  private LinkedHashSet<Long> tagIds;

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

  @Override
  public boolean sameIdentityAs(App other) {
    return Objects.equals(this.id, other.id) || (Objects.equals(this.code, other.code)
        && Objects.equals(this.editionType, other.editionType)
        && Objects.equals(this.version, other.version));
  }
}
