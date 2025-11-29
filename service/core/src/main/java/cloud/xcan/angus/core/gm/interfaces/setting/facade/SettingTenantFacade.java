package cloud.xcan.angus.core.gm.interfaces.setting.facade;


import cloud.xcan.angus.core.gm.interfaces.setting.facade.dto.tenant.TenantLocaleReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.to.locale.LocaleTo;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.to.security.SecurityTo;

public interface SettingTenantFacade {

  void localeReplace(TenantLocaleReplaceDto dto);

  LocaleTo localeDetail();

  void securityReplace(SecurityTo dto);

  String invitationCodeGen();

  SecurityTo securityDetail();

}
