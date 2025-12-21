package cloud.xcan.angus.core.gm.interfaces.authentication.facade;

import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.sign.AccountQueryDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.sign.ForgetPasswordDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.sign.RenewDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.sign.SignEmailCheckDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.sign.SignEmailSendDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.sign.SignInDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.sign.SignSmsCheckDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.sign.SignSmsSendDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.sign.SignoutDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.sign.SignupDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.vo.sign.AccountVo;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.vo.sign.SignVo;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.List;


public interface AuthUserSignFacade {

  IdKey<Long, Object> signup(String deviceId, SignupDto dto);

  SignVo signin(String deviceId, SignInDto dto);

  SignVo renew(RenewDto dto);

  void signout(SignoutDto dto);

  void forgetPassword(ForgetPasswordDto dto);

  List<AccountVo> tenantAccount(AccountQueryDto dto);

  void sendSignSms(SignSmsSendDto dto);

  List<AccountVo> checkSignSms(SignSmsCheckDto dto);

  void sendSignEmail(SignEmailSendDto dto);

  List<AccountVo> checkSignEmail(SignEmailCheckDto dto);

}
