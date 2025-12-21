package cloud.xcan.angus.core.gm.interfaces.authentication.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.authentication.facade.internal.assembler.AuthUserSignAssembler.signInToVo;
import static cloud.xcan.angus.core.gm.interfaces.authentication.facade.internal.assembler.AuthUserSignAssembler.signupToOauthUser;
import static cloud.xcan.angus.spec.utils.ObjectUtils.emptySafe;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static java.util.Collections.emptyList;

import cloud.xcan.angus.api.commonlink.authuser.AuthUser;
import cloud.xcan.angus.core.gm.application.cmd.authentication.AuthUserSignCmd;
import cloud.xcan.angus.core.gm.application.cmd.email.EmailCmd;
import cloud.xcan.angus.core.gm.application.cmd.sms.SmsCmd;
import cloud.xcan.angus.core.gm.application.query.authentication.AuthUserQuery;
import cloud.xcan.angus.core.gm.application.query.authentication.AuthUserSignQuery;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.AuthUserSignFacade;
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
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.internal.assembler.AuthUserSignAssembler;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.vo.sign.AccountVo;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.vo.sign.SignVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.internal.assembler.EmailAssembler;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.internal.assembler.SmsAssembler;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;


@Component
public class AuthUserSignFacadeImpl implements AuthUserSignFacade {

  @Resource
  private AuthUserSignCmd authUserSignCmd;

  @Resource
  private AuthUserSignQuery authUserSignQuery;

  @Resource
  private AuthUserQuery authUserQuery;

  @Resource
  private SmsCmd smsCmd;

  @Resource
  private EmailCmd emailCmd;

  @Override
  public IdKey<Long, Object> signup(String deviceId, SignupDto dto) {
    return authUserSignCmd.signup(signupToOauthUser(deviceId, dto));
  }

  @Override
  public SignVo signin(String deviceId, SignInDto dto) {
    Map<String, String> result = authUserSignCmd.signin(dto.getClientId(),
        dto.getClientSecret(), dto.getSigninType(), dto.getUserId(), dto.getAccount(),
        dto.getPassword(), dto.getScope(), emptySafe(deviceId, dto.getDeviceId()));
    return signInToVo(result);
  }

  @Override
  public SignVo renew(RenewDto dto) {
    Map<String, String> result = authUserSignCmd.renew(dto.getClientId(), dto.getClientSecret(),
        dto.getRefreshToken());
    return signInToVo(result);
  }

  @Override
  public void signout(SignoutDto dto) {
    authUserSignCmd.signout(dto.getClientId(), dto.getClientSecret(), dto.getAccessToken());
  }

  @Override
  public void forgetPassword(ForgetPasswordDto dto) {
    authUserSignCmd.forgetPassword(dto.getId(), dto.getNewPassword(), dto.getLinkSecret());
  }

  @Override
  public List<AccountVo> tenantAccount(AccountQueryDto dto) {
    List<AuthUser> authUsers = authUserQuery.findByAccountAndPassword(
        dto.getAccount(), dto.getPassword());
    return isEmpty(authUsers) ? null : authUsers.stream()
        .map(AuthUserSignAssembler::userToAccountVo).collect(Collectors.toList());
  }

  @Override
  public void sendSignSms(SignSmsSendDto dto) {
    smsCmd.send(SmsAssembler.signToDomain(dto), false);
  }

  @Override
  public List<AccountVo> checkSignSms(SignSmsCheckDto dto) {
    List<AuthUser> users = authUserSignQuery
        .checkSms(dto.getMobile(), dto.getBizKey(), dto.getVerificationCode());
    return isEmpty(users) ? emptyList() : users.stream()
        .map(AuthUserSignAssembler::userToAccountVo).collect(Collectors.toList());
  }

  @Override
  public void sendSignEmail(SignEmailSendDto dto) {
    emailCmd.send(EmailAssembler.signToDomain(dto), false);
  }

  @Override
  public List<AccountVo> checkSignEmail(SignEmailCheckDto dto) {
    List<AuthUser> users = authUserSignQuery.checkEmail(dto.getEmail(),
        dto.getBizKey(), dto.getVerificationCode());
    return isEmpty(users) ? emptyList() : users.stream()
        .map(AuthUserSignAssembler::userToAccountVo).collect(Collectors.toList());
  }

}
