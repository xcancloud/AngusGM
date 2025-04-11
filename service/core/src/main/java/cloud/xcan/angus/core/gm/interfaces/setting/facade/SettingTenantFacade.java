package cloud.xcan.angus.core.gm.interfaces.setting.facade;


import cloud.xcan.angus.api.commonlink.setting.tenant.event.TesterEvent;
import cloud.xcan.angus.api.gm.indicator.to.FuncTo;
import cloud.xcan.angus.api.gm.indicator.to.PerfTo;
import cloud.xcan.angus.api.gm.indicator.to.StabilityTo;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.dto.tenant.TenantLocaleReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.to.setting.locale.LocaleTo;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.to.setting.security.SecurityTo;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.to.tenant.TenantServerApiProxyTo;
import java.util.List;

public interface SettingTenantFacade {

  void localeReplace(TenantLocaleReplaceDto dto);

  LocaleTo localeDetail();

  void securityReplace(SecurityTo dto);

  String invitationCodeGen();

  SecurityTo securityDetail();

  void proxyReplace(TenantServerApiProxyTo dto);

  TenantServerApiProxyTo proxyDetail();

  void testerEventReplace(List<TesterEvent> dto);

  List<TesterEvent> testerEventDetail();

  void funcReplace(FuncTo funcTo);

  FuncTo funcDetail();

  void perfReplace(PerfTo perfTo);

  PerfTo perfDetail();

  void stabilityReplace(StabilityTo stability);

  StabilityTo stabilityDetail();

}
