package cloud.xcan.angus.core.gm.interfaces.setting.facade.to.setting.quota;

import cloud.xcan.angus.api.commonlink.setting.Setting;
import cloud.xcan.angus.api.commonlink.setting.quota.QuotaResource;
import cloud.xcan.angus.api.commonlink.setting.tenant.SettingTenant;
import cloud.xcan.angus.core.jpa.repository.summary.SummaryQueryRegister;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class QuotaTo implements Serializable {

  private String appCode;

  /**
   * Original service code, privatization will be merged.
   */
  private String serviceCode;

  /**
   * It needs to be consistent with the name value in SummaryQueryRegister.
   *
   * @see SummaryQueryRegister
   */
  private QuotaResource name;

  private Boolean allowChange;

  /**
   * TODO Lcs initialization quota after purchase
   */
  private Boolean lcsCtrl;

  /**
   * @see SettingTenant#getQuotaData()
   * @see Setting#getQuota() defalut value
   */
  private Long quota;

  private Long min;

  private Long max;

}
