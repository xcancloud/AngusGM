package cloud.xcan.angus.core.gm.application.cmd.tenant;


import java.util.Set;

public interface TenantSignCmd {

  void cancelSignInvoke();

  void signCancelExpire(Set<Long> tenantIds);

  void signCancelSmsSend();

  void signCancelSmsConfirm(String verificationCode);

}
