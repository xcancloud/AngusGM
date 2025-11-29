package cloud.xcan.angus.core.gm.application.cmd.setting;

import cloud.xcan.angus.api.commonlink.setting.locale.Locale;
import cloud.xcan.angus.api.commonlink.setting.security.Security;


public interface SettingTenantCmd {

  void localeReplace(Locale locale);

  void securityReplace(Security security);

  String invitationCodeGen();

  void init(Long tenantId);

}
