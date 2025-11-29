package cloud.xcan.angus.core.gm.interfaces.setting.facade.vo;

import cloud.xcan.angus.api.commonlink.setting.SettingKey;
import cloud.xcan.angus.api.commonlink.setting.aiagent.AIAgent;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.to.locale.LocaleTo;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.to.quota.QuotaTo;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.to.security.SecurityTo;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.to.social.SocialTo;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.vo.tenant.TenantHealthCheckVo;
import cloud.xcan.angus.core.log.ApiLogProperties;
import cloud.xcan.angus.core.log.OperationLogProperties;
import cloud.xcan.angus.core.log.SystemLogProperties;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class SettingValueVo implements Serializable {

  private SettingKey key;

  private Boolean globalDefault;

  private LocaleTo locale;

  private SecurityTo security;

  private SocialTo social;

  private List<QuotaTo> quota;

  private AIAgent aiAgent;

  private TenantHealthCheckVo healthCheck;

  private ApiLogProperties apiLog;

  private OperationLogProperties operationLog;

  private SystemLogProperties systemLog;

  private Integer maxResourceActivities;

  private Integer maxMetricsDays;
}
