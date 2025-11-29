package cloud.xcan.angus.api.commonlink.setting.tenant;

import cloud.xcan.angus.api.commonlink.setting.converter.LocaleDataConverter;
import cloud.xcan.angus.api.commonlink.setting.converter.SecurityDataConverter;
import cloud.xcan.angus.api.commonlink.setting.locale.Locale;
import cloud.xcan.angus.api.commonlink.setting.security.Security;
import cloud.xcan.angus.api.commonlink.setting.security.SignupAllow;
import cloud.xcan.angus.core.jpa.multitenancy.TenantEntity;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Except for tree and hierarchy related restrictions, other quota restrictions are not written in
 * the code!! This makes it easy to modify after privatization deployment.
 *
 * @author XiaoLong Liu
 */
@Entity
@Table(name = "c_setting_tenant")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@Setter
@Getter
@Accessors(chain = true)
public class SettingTenant extends TenantEntity<SettingTenant, Long> {

  @Id
  private Long id;

  /**
   * Redundant fields for retrieval.
   *
   * @see SignupAllow#getInvitationCode()
   */
  @Column(name = "invitation_code")
  private String invitationCode;

  ////////////////////////////Automatic initialization//////////////////////////////
  @Column(name = "locale_data")
  @Convert(converter = LocaleDataConverter.class)
  private Locale localeData;

  //  @Column(name = "func_data", columnDefinition = "json")
  //  @Convert(converter = FuncDataConverter.class)
  //  private Func funcData;
  //
  //  @Column(name = "perf_data", columnDefinition = "json")
  //  @Convert(converter = PerfDataConverter.class)
  //  private Perf perfData;
  //
  //  @Column(name = "stability_data")
  //  @Convert(converter = StabilityDataConverter.class)
  //  private Stability stabilityData;

  @Column(name = "security_data")
  @Convert(converter = SecurityDataConverter.class)
  private Security securityData;

  //  @Column(name = "tester_event_data")
  //  @Convert(converter = TesterEventConverter.class)
  //  private List<TesterEvent> testerEventData;
  ////////////////////////////Automatic initialization//////////////////////////////

  //  @Column(name = "server_api_proxy_data")
  //  @Convert(converter = ServerApiProxyDataConverter.class)
  //  private ServerApiProxy serverApiProxyData;

  @Override
  public Long identity() {
    return this.id;
  }
}
