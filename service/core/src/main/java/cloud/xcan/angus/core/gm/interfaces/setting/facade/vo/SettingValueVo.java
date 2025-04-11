package cloud.xcan.angus.core.gm.interfaces.setting.facade.vo;

import cloud.xcan.angus.api.commonlink.setting.SettingKey;
import cloud.xcan.angus.api.commonlink.setting.aiagent.AIAgent;
import cloud.xcan.angus.api.commonlink.setting.tenant.event.TesterEvent;
import cloud.xcan.angus.api.gm.indicator.to.FuncTo;
import cloud.xcan.angus.api.gm.indicator.to.PerfTo;
import cloud.xcan.angus.api.gm.indicator.to.StabilityTo;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.to.setting.locale.LocaleTo;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.to.setting.quota.QuotaTo;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.to.setting.security.SecurityTo;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.to.setting.social.SocialTo;
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

  private FuncTo func;

  private PerfTo perf;

  private StabilityTo stability;

  private List<TesterEvent> testerEvent;

  private AIAgent aiAgent;

  private TenantHealthCheckVo healthCheck;

  private ApiLogProperties apiLog;

  private OperationLogProperties operationLog;

  private SystemLogProperties systemLog;

  private Integer maxResourceActivities;

  private Integer maxMetricsDays;
}
