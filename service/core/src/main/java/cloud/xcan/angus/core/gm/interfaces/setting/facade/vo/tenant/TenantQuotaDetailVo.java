package cloud.xcan.angus.core.gm.interfaces.setting.facade.vo.tenant;

import cloud.xcan.angus.api.commonlink.setting.Setting;
import cloud.xcan.angus.api.commonlink.setting.quota.QuotaResource;
import cloud.xcan.angus.api.commonlink.setting.tenant.SettingTenant;
import cloud.xcan.angus.remote.NameJoinField;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class TenantQuotaDetailVo implements Serializable {

  private Long id;

  private String appCode;

  @Schema(hidden = true)
  private String serviceCode;

  private QuotaResource name;

  private Boolean allowChange;

  private Boolean calcRemaining;

  /**
   * @see SettingTenant#getQuotaData()
   */
  @Schema(description = "Tenant quota value")
  private long quota;

  /**
   * @see Setting#getQuota() defalut value
   */
  private long defaults;

  private long min;

  private long max;

  private Long tenantId;

  @NameJoinField(id = "tenantId", repository = "commonTenantRepo")
  private String tenantName;

}
