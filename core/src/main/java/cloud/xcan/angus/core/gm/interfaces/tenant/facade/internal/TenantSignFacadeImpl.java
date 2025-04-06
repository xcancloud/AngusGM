package cloud.xcan.angus.core.gm.interfaces.tenant.facade.internal;

import cloud.xcan.angus.core.gm.application.cmd.tenant.TenantSignCmd;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.TenantSignFacade;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.dto.SignCancelSmsConfirmDto;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;


@Component
public class TenantSignFacadeImpl implements TenantSignFacade {

  @Resource
  private TenantSignCmd tenantSignCmd;

  @Override
  public void cancelSignInvoke() {
    tenantSignCmd.cancelSignInvoke();
  }

  @Override
  public void signCancelSmsSend() {
    tenantSignCmd.signCancelSmsSend();
  }

  @Override
  public void signCancelSmsConfirm(SignCancelSmsConfirmDto dto) {
    tenantSignCmd.signCancelSmsConfirm(dto.getVerificationCode());
  }

}
