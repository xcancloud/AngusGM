package cloud.xcan.angus.api.commonlink.setting.tenant.quota;

import cloud.xcan.angus.api.commonlink.setting.Setting;
import cloud.xcan.angus.api.commonlink.setting.quota.QuotaResource;
import cloud.xcan.angus.core.jpa.multitenancy.TenantEntity;
import cloud.xcan.angus.core.jpa.repository.summary.SummaryQueryRegister;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "c_setting_tenant_quota")
@Accessors(chain = true)
public class SettingTenantQuota extends TenantEntity<SettingTenantQuota, Long> {

  @Id
  private Long id;

  @Column(name = "app_code")
  private String appCode;

  /**
   * Original service code, privatization will be merged.
   */
  @Column(name = "service_code")
  private String serviceCode;

  /**
   * It needs to be consistent with the name value in SummaryQueryRegister.
   *
   * @see SummaryQueryRegister
   */
  @Enumerated(EnumType.STRING)
  private QuotaResource name;

  @Column(name = "allow_change")
  private Boolean allowChange;

  /**
   * TODO License initialization quota after purchase
   */
  @Column(name = "license_ctrl")
  private Boolean licenseCtrl;

  /**
   * Calculate remaining quota flag
   */
  @Column(name = "calc_remaining")
  private Boolean calcRemaining;

  /**
   * @see Setting#getQuota()
   */
  private Long quota;

  private Long min;

  private Long max;

  private Long capacity;

  /**
   * @see Setting#getQuota() defalut value
   */
  @Transient
  @Schema(description = "Default quota value")
  private long default0;

  @Transient
  @Schema(description = "Used quota value")
  private long used;

  @Override
  public Long identity() {
    return this.id;
  }
}
