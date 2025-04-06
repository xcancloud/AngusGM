package cloud.xcan.angus.core.gm.interfaces.authuser.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.authuser.facade.internal.assembler.AuthUserSignAssembler.signInToVo;
import static cloud.xcan.angus.core.gm.interfaces.authuser.facade.internal.assembler.AuthUserSignAssembler.signupToOauthUser;
import static cloud.xcan.angus.spec.utils.ObjectUtils.emptySafe;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static java.util.Collections.emptyList;

import cloud.xcan.angus.api.commonlink.authuser.AuthUser;
import cloud.xcan.angus.core.gm.application.cmd.authuser.AuthUserSignCmd;
import cloud.xcan.angus.core.gm.application.cmd.email.EmailCmd;
import cloud.xcan.angus.core.gm.application.cmd.sms.SmsCmd;
import cloud.xcan.angus.core.gm.application.query.authuser.AuthUserQuery;
import cloud.xcan.angus.core.gm.application.query.authuser.AuthUserSignQuery;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.AuthUserSignFacade;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.dto.sign.AccountQueryDto;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.dto.sign.ForgetPasswordDto;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.dto.sign.RenewDto;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.dto.sign.SignEmailCheckDto;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.dto.sign.SignEmailSendDto;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.dto.sign.SignInDto;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.dto.sign.SignSmsCheckDto;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.dto.sign.SignSmsSendDto;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.dto.sign.SignoutDto;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.dto.sign.SignupDto;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.internal.assembler.AuthUserSignAssembler;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.vo.sign.AccountVo;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.vo.sign.SignVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.internal.assembler.EmailAssembler;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.internal.assembler.SmsAssembler;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
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
    OAuth2AccessTokenAuthenticationToken accessToken = authUserSignCmd.signin(dto.getClientId(),
        dto.getClientSecret(), new HashSet<>(List.of(dto.getScope().split(","))),
        dto.getSigninType(), dto.getUserId(), dto.getAccount(), dto.getPassword(),
        emptySafe(deviceId, dto.getDeviceId()));
    return signInToVo(accessToken);
  }

  @Override
  public SignVo renew(RenewDto dto) {
    OAuth2AccessTokenAuthenticationToken refreshToken = authUserSignCmd.renew(dto.getClientId(),
        dto.getClientSecret(), dto.getRefreshToken(),
        new HashSet<>(List.of(dto.getScope().split(","))));
    return signInToVo(refreshToken);
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
