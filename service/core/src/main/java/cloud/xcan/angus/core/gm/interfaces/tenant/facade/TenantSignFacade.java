package cloud.xcan.angus.core.gm.interfaces.tenant.facade;

import cloud.xcan.angus.core.gm.interfaces.tenant.facade.dto.SignCancelSmsConfirmDto;


public interface TenantSignFacade {

  void signCancelSmsSend();

  void signCancelSmsConfirm(SignCancelSmsConfirmDto dto);

  void cancelSignInvoke();


}
