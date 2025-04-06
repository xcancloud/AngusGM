package cloud.xcan.angus.core.gm.application.converter;

import static cloud.xcan.angus.api.commonlink.AASConstant.SIGN_RESOURCE_NAME;
import static cloud.xcan.angus.core.gm.application.converter.UserConverter.genFullname;
import static cloud.xcan.angus.core.gm.application.converter.UserConverter.genUsername;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;
import static cloud.xcan.angus.spec.utils.ObjectUtils.stringSafe;

import cloud.xcan.angus.api.commonlink.AASConstant;
import cloud.xcan.angus.api.commonlink.authuser.AuthUser;
import cloud.xcan.angus.api.commonlink.user.SignupType;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.enums.Gender;
import cloud.xcan.angus.api.enums.SignInType;
import cloud.xcan.angus.api.enums.UserSource;
import cloud.xcan.angus.core.event.OperationEvent;
import cloud.xcan.angus.core.event.source.UserOperation;
import cloud.xcan.angus.core.gm.domain.SignOperationKey;
import cloud.xcan.angus.remote.message.CommProtocolException;
import cloud.xcan.angus.security.authentication.email.EmailCodeAuthenticationConverter;
import cloud.xcan.angus.security.authentication.email.EmailCodeAuthenticationToken;
import cloud.xcan.angus.security.authentication.password.OAuth2PasswordAuthenticationConverter;
import cloud.xcan.angus.security.authentication.password.OAuth2PasswordAuthenticationToken;
import cloud.xcan.angus.security.authentication.sms.SmsCodeAuthenticationConverter;
import cloud.xcan.angus.security.authentication.sms.SmsCodeAuthenticationToken;
import cloud.xcan.angus.security.client.CustomOAuth2RegisteredClient;
import cloud.xcan.angus.spec.locale.MessageHolder;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Set;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2RefreshTokenAuthenticationToken;


public class UserSignConverter {

  public static User signupToAddUser(AuthUser user, String invitationCode) {
    return new User().setCountry(user.getCountry())
        .setPassword(user.getPassword())
        .setItc(user.getItc())
        .setMobile(stringSafe(user.getMobile()))
        .setEmail(stringSafe(user.getEmail()))
        .setSysAdmin(user.isSysAdmin())
        .setFirstName("").setLastName("")
        .setFullname(genFullname()).setUsername(genUsername())
        .setGender(Gender.UNKNOWN)
        .setSignupAccount(SignupType.valueOf(user.getSignupType()).isEmail()
            ? user.getEmail() : user.getMobile())
        .setSignupAccountType(SignupType.valueOf(user.getSignupType()))
        .setSignupDeviceId(user.getSignupDeviceId())
        .setInvitationCode(invitationCode)
        .setSource(UserSource.PLATFORM_SIGNUP)
        .setExpired(false)
        .setEnabled(true)
        .setDeleted(false)
        .setLocked(false)
        .setCreatedBy(-1L)
        .setCreatedDate(LocalDateTime.now())
        .setLastModifiedBy(-1L)
        .setLastModifiedDate(LocalDateTime.now());
  }

  public static OAuth2ClientAuthenticationToken convertClientSuccessAuthentication(
      CustomOAuth2RegisteredClient client) {
    OAuth2ClientAuthenticationToken clientAuthenticationToken
        = new OAuth2ClientAuthenticationToken(client,
        client.getClientAuthenticationMethods().iterator().next(), client.getClientSecret());
    clientAuthenticationToken.setAuthenticated(true);
    // clientAuthenticationToken.setDetails(client);
    return clientAuthenticationToken;
  }

  /**
   * @see OAuth2PasswordAuthenticationConverter#convert(HttpServletRequest)
   * @see SmsCodeAuthenticationConverter#convert(HttpServletRequest)
   * @see EmailCodeAuthenticationConverter#convert(HttpServletRequest)
   */
  public static Authentication convertUserSignInAuthentication(
      SignInType signinType, Long userId, String account, String password,
      Set<String> requestedScopes, OAuth2ClientAuthenticationToken clientAuthenticationToken) {
    return switch (signinType) {
      case ACCOUNT_PASSWORD -> new OAuth2PasswordAuthenticationToken(userId.toString(),
          account, password, clientAuthenticationToken, requestedScopes, new HashMap<>());
      case SMS_CODE -> new SmsCodeAuthenticationToken(userId.toString(),
          account, password, clientAuthenticationToken, requestedScopes, new HashMap<>());
      case EMAIL_CODE -> new EmailCodeAuthenticationToken(userId.toString(), account, password,
          clientAuthenticationToken, requestedScopes, new HashMap<>());
      default -> throw CommProtocolException.of("Unsupported sign-in type: " + signinType);
    };
  }

  public static Authentication convertUserRenewAuthentication(String refreshToken,
      Set<String> requestedScopes, OAuth2ClientAuthenticationToken clientAuthenticationToken) {
    return new OAuth2RefreshTokenAuthenticationToken(refreshToken, clientAuthenticationToken,
        requestedScopes, new HashMap<>());
  }

  public static OperationEvent getSignupOperationEvent(String clientId, AuthUser user) {
    return new OperationEvent(
        UserOperation.newBuilder().success(true)
            .resourceName(SIGN_RESOURCE_NAME)
            .clientId(clientId)
            .requestId(PrincipalContext.getRequestId())
            .userId(Long.valueOf(user.getId()))
            .fullname(user.getFullName())
            .tenantId(Long.valueOf(user.getTenantId()))
            .tenantName(user.getTenantName())
            .description(MessageHolder
                .message(SignOperationKey.SIGN_IN, new Object[]{user.getFullName()}))
            .operationDate(PrincipalContext.getRequestAcceptTime()).build());
  }

  public static OperationEvent getSignoutOperationEvent(String clientId, AuthUser user) {
    if (isNull(user)) {
      return null;
    }
    return new OperationEvent(
        UserOperation.newBuilder()
            .success(true)
            .resourceName(AASConstant.SIGN_RESOURCE_NAME)
            .clientId(clientId)
            .requestId(PrincipalContext.getRequestId())
            .userId(Long.valueOf(user.getId()))
            .fullname(user.getFullName())
            .tenantId(Long.valueOf(user.getTenantId()))
            .tenantName(null)
            .description(
                MessageHolder.message(SignOperationKey.SIGN_OUT, new Object[]{user.getFullName()}))
            .operationDate(PrincipalContext.getRequestAcceptTime()).build()
    );
  }
}
