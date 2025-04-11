package cloud.xcan.angus.core.gm.application.cmd.setting;

import cloud.xcan.angus.api.commonlink.setting.indicator.Func;
import cloud.xcan.angus.api.commonlink.setting.indicator.Perf;
import cloud.xcan.angus.api.commonlink.setting.indicator.Stability;
import cloud.xcan.angus.api.commonlink.setting.locale.Locale;
import cloud.xcan.angus.api.commonlink.setting.security.Security;
import cloud.xcan.angus.api.commonlink.setting.tenant.apiproxy.ServerApiProxy;
import cloud.xcan.angus.api.commonlink.setting.tenant.event.TesterEvent;
import java.util.List;


public interface SettingTenantCmd {

  void localeReplace(Locale locale);

  void securityReplace(Security security);

  String invitationCodeGen();

  void proxyReplace(ServerApiProxy apiProxy);

  void testerEventReplace(List<TesterEvent> testerEvent);

  void funcReplace(Func func);

  void perfReplace(Perf perf);

  void stabilityReplace(Stability stability);

  void init(Long tenantId);

}
