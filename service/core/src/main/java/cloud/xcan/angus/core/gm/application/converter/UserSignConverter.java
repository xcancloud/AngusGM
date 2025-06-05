package cloud.xcan.angus.core.gm.application.converter;

import static cloud.xcan.angus.core.gm.application.converter.UserConverter.genFullname;
import static cloud.xcan.angus.core.gm.application.converter.UserConverter.genUsername;
import static cloud.xcan.angus.spec.utils.ObjectUtils.stringSafe;

import cloud.xcan.angus.api.commonlink.authuser.AuthUser;
import cloud.xcan.angus.api.commonlink.user.SignupType;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.enums.Gender;
import cloud.xcan.angus.api.enums.UserSource;
import java.time.LocalDateTime;


public class UserSignConverter {

  public static User signupToAddUser(AuthUser user, String invitationCode) {
    SignupType signupType = SignupType.valueOf(user.getSignupType().toUpperCase());
    return new User().setCountry(user.getCountry())
        .setPassword(user.getPassword())
        .setItc(user.getItc())
        .setMobile(stringSafe(user.getMobile()))
        .setEmail(stringSafe(user.getEmail()))
        .setSysAdmin(user.isSysAdmin())
        .setFirstName("").setLastName("")
        .setFullName(genFullname()).setUsername(genUsername())
        .setGender(Gender.UNKNOWN)
        .setSignupAccount(signupType.isEmail() ? user.getEmail() : user.getMobile())
        .setSignupAccountType(signupType)
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

}
