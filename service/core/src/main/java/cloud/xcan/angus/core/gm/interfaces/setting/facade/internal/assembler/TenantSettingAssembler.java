package cloud.xcan.angus.core.gm.interfaces.setting.facade.internal.assembler;

import cloud.xcan.angus.api.commonlink.setting.locale.Locale;
import cloud.xcan.angus.api.commonlink.setting.security.Security;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.dto.tenant.TenantLocaleReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.to.locale.LocaleTo;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.to.security.SecurityTo;

public class TenantSettingAssembler {

  public static Locale updateToLocale(TenantLocaleReplaceDto dto) {
    return new Locale().setDefaultLanguage(dto.getDefaultLanguage());
  }

  public static LocaleTo toLocaleTo(Locale data) {
    return new LocaleTo().setDefaultLanguage(data.getDefaultLanguage())
        .setDefaultTimeZone(data.getDefaultTimeZone());
  }

  public static Security updateToSecurity(SecurityTo dto) {
    return new Security().setSigninLimit(dto.getSigninLimit())
        .setPasswordPolicy(dto.getPasswordPolicy())
        .setSignupAllow(dto.getSignupAllow()).setAlarm(dto.getAlarm());
  }

  public static SecurityTo toSecurityTo(Security data) {
    return new SecurityTo().setSigninLimit(data.getSigninLimit())
        .setPasswordPolicy(data.getPasswordPolicy())
        .setSignupAllow(data.getSignupAllow()).setAlarm(data.getAlarm());
  }

}
