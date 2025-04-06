package cloud.xcan.angus.core.gm.application.query.authuser;

import cloud.xcan.angus.api.commonlink.authuser.AuthUser;
import cloud.xcan.angus.api.commonlink.email.EmailBizKey;
import cloud.xcan.angus.api.commonlink.setting.tenant.SettingTenant;
import cloud.xcan.angus.api.commonlink.sms.SmsBizKey;
import java.util.List;


public interface AuthUserSignQuery {

  List<AuthUser> checkEmail(String email, EmailBizKey bizKey, String verificationCode);

  List<AuthUser> checkSms(String mobile, SmsBizKey bizKey, String verificationCode);

  void checkMinPassdLengthByTenantSetting(Long tenantId, String passd);

  SettingTenant checkAndFindSettingTenant(Long tenantId);
}
