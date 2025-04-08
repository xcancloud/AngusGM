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
import cloud.xcan.angus.api.enums.UserSource;
import cloud.xcan.angus.core.event.OperationEvent;
import cloud.xcan.angus.core.event.source.UserOperation;
import cloud.xcan.angus.core.gm.domain.SignOperationKey;
import cloud.xcan.angus.spec.locale.MessageHolder;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import java.time.LocalDateTime;


public class UserSignConverter {

  public static User signupToAddUser(AuthUser user, String invitationCode) {
    return new User().setCountry(user.getCountry())
        .setPassword(user.getPassword())
        .setItc(user.getItc())
        .setMobile(stringSafe(user.getMobile()))
        .setEmail(stringSafe(user.getEmail()))
        .setSysAdmin(user.isSysAdmin())
        .setFirstName("").setLastName("")
        .setFullName(genFullname()).setUsername(genUsername())
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

  public static OperationEvent getSignupOperationEvent(String clientId, AuthUser user) {
    return new OperationEvent(
        UserOperation.newBuilder().success(true)
            .resourceName(SIGN_RESOURCE_NAME)
            .clientId(clientId)
            .requestId(PrincipalContext.getRequestId())
            .userId(Long.valueOf(user.getId()))
            .fullName(user.getFullName())
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
            .fullName(user.getFullName())
            .tenantId(Long.valueOf(user.getTenantId()))
            .tenantName(null)
            .description(
                MessageHolder.message(SignOperationKey.SIGN_OUT, new Object[]{user.getFullName()}))
            .operationDate(PrincipalContext.getRequestAcceptTime()).build()
    );
  }
}
