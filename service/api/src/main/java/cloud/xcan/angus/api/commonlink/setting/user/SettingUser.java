package cloud.xcan.angus.api.commonlink.setting.user;

import cloud.xcan.angus.api.commonlink.setting.tenant.SettingTenant;
import cloud.xcan.angus.api.commonlink.setting.user.preference.Preference;
import cloud.xcan.angus.api.commonlink.setting.user.socialbinding.SocialBinding;
import cloud.xcan.angus.core.jpa.multitenancy.TenantEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "c_setting_user")
@Setter
@Getter
@Accessors(chain = true)
public class SettingUser extends TenantEntity<SettingTenant, Long> {

  @Id
  private Long id;

  @Type(JsonType.class)
  @Column(name = "preference", columnDefinition = "json")
  private Preference preference;

  //  /**
  //   * The server proxy may be modified. Use {@link SettingTenant#getServerApiProxyData()} overwrite
  //   * server proxy.
  //   */
  //  @Type(JsonType.class)
  //  @Column(name = "api_proxy", columnDefinition = "json")
  //  private UserApiProxy apiProxy;

  @Type(JsonType.class)
  @Column(name = "social_bind", columnDefinition = "json")
  private SocialBinding socialBind;

  @Override
  public Long identity() {
    return id;
  }

}
