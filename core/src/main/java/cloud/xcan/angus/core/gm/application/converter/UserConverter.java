package cloud.xcan.angus.core.gm.application.converter;

import static cloud.xcan.angus.api.commonlink.UCConstant.BID_USERNAME;
import static cloud.xcan.angus.api.commonlink.UCConstant.PASSWORD_PROXY_ENCRYP_TYPE;
import static cloud.xcan.angus.core.spring.SpringContextHolder.getBidGenerator;
import static cloud.xcan.angus.core.spring.SpringContextHolder.getCachedUidGenerator;
import static cloud.xcan.angus.core.utils.CoreUtils.calcPasswordStrength;
import static cloud.xcan.angus.spec.SpecConstant.DEFAULT_ENCODING;
import static cloud.xcan.angus.spec.experimental.BizConstant.EMPTY_NUMBER;
import static cloud.xcan.angus.spec.experimental.BizConstant.EMPTY_STR;
import static cloud.xcan.angus.spec.utils.ObjectUtils.convert;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;
import static cloud.xcan.angus.spec.utils.ObjectUtils.stringSafe;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.apache.commons.lang3.RandomUtils.nextLong;

import cloud.xcan.angus.api.commonlink.UCConstant;
import cloud.xcan.angus.api.commonlink.authuser.AuthUser;
import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.api.commonlink.tenant.Tenant;
import cloud.xcan.angus.api.commonlink.tenant.TenantRealNameStatus;
import cloud.xcan.angus.api.commonlink.user.SignupType;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.dept.DeptUser;
import cloud.xcan.angus.api.commonlink.user.group.GroupUser;
import cloud.xcan.angus.api.enums.Gender;
import cloud.xcan.angus.api.enums.PassdEncoderType;
import cloud.xcan.angus.api.enums.PasswordStrength;
import cloud.xcan.angus.api.enums.UserSource;
import cloud.xcan.angus.core.gm.domain.user.directory.UserDirectory;
import cloud.xcan.angus.core.gm.domain.user.directory.model.DirectoryUserSchema;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserConverter {

  public static AuthUser replaceToAuthUser(User user, Tenant tenantDb) {
    return AuthUser.username(user.getUsername())
        .password(user.getPassword())
        .disabled(user.getEnabled() || user.getDeleted())
        .accountExpired(user.getExpired())
        .accountLocked(nullSafe(user.getLocked(), false))
        .credentialsExpired((nullSafe(user.getPasswordExpired(), false)))
        .id(user.getId().toString())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .fullName(user.getFullname())
        .passwordStrength(calcPasswordStrength(stringSafe(user.getPassword())).getValue())
        .sysAdmin(user.getSysAdmin())
        .toUser(false)
        .mobile(user.getMobile())
        .email(user.getEmail())
        .mainDeptId(nonNull(user.getMainDeptId()) ? user.getMainDeptId().toString() : null)
        .passwordExpiredDate(nonNull(user.getPasswordExpiredDate())
            ? Instant.from(user.getPasswordExpiredDate()) : null)
        .lastModifiedPasswordDate(null)
        .expiredDate(nonNull(user.getExpiredDate()) ? Instant.from(user.getExpiredDate()) : null)
        .deleted(nullSafe(user.getDeleted(), false))
        .tenantId(tenantDb.getId().toString())
        .tenantName(tenantDb.getName())
        .tenantRealNameStatus(nonNull(tenantDb.getRealNameStatus())
            ? tenantDb.getRealNameStatus().getValue() : null)
        .directoryId(nonNull(user.getDirectoryId()) ? user.getDirectoryId().toString() : null)
        .defaultLanguage(null)
        .defaultTimeZone(null)
        .build();
  }

  public static void setUserMainDeptAndHead(List<DeptUser> deptUsers, User user) {
    if (isNotEmpty(deptUsers)) {
      user.setMainDeptId(deptUsers.stream().filter(DeptUser::getMainDept)
          .findFirst().orElse(deptUsers.get(0)).getDeptId());
      user.setDeptHead(deptUsers.stream().anyMatch(DeptUser::getDeptHead));
    } else {
      user.setDeptHead(false);
    }
  }

  public static void assembleUserInfo(User userDb, User user) {
    userDb.setFirstName(user.getFirstName())
        .setLastName(user.getLastName())
        .setFullname(user.getFullname())
        .setUsername(user.getUsername())
        .setTitle(user.getTitle())
        .setGender(user.getGender())
        .setItc(user.getItc())
        .setLandline(user.getLandline())
        .setAvatar(user.getAvatar())
        .setCountry(user.getCountry())
        .setMobile(user.getMobile())
        .setEmail(user.getEmail())
        .setAddress(user.getAddress());
  }

  public static void assembleOauthUserInfo(User userDb, AuthUser authUser) {
    userDb.setPasswordStrength(nonNull(authUser.getPasswordStrength()) ? PasswordStrength.valueOf(
            authUser.getPasswordStrength()) : null)
        .setPasswordExpired(authUser.isPasswordExpired())
        .setPasswordExpiredDate(nonNull(authUser.getPasswordExpiredDate()) ? LocalDateTime.from(
            authUser.getPasswordExpiredDate()) : null)
        .setTenantRealNameStatus(
            nonNull(authUser.getTenantRealNameStatus()) ? TenantRealNameStatus.valueOf(
                authUser.getTenantRealNameStatus()) : null);
  }

  public static DeptUser objectArrToDeptUser(Object[] objects) {
    DeptUser deptUser = new DeptUser()
        .setId(convert(objects[0], Long.class))
        .setDeptHead(convert(objects[2], Boolean.class))
        .setMainDept(convert(objects[3], Boolean.class))
        .setCreatedBy(convert(objects[4], Long.class))
        .setCreatedDate(convert(objects[5], LocalDateTime.class))
        .setDeptId(convert(objects[6], Long.class))
        .setDeptName(convert(objects[7], String.class))
        .setDeptCode(convert(objects[8], String.class))
        .setUserId(convert(objects[9], Long.class))
        .setFullname(convert(objects[10], String.class))
        .setAvatar(convert(objects[11], String.class))
        .setMobile(convert(objects[12], String.class));
    deptUser.setTenantId(convert(objects[1], Long.class));
    return deptUser;
  }

  public static GroupUser objectArrToGroupUser(Object[] objects) {
    GroupUser groupUser = new GroupUser()
        .setId(convert(objects[0], Long.class))
        .setCreatedBy(convert(objects[2], Long.class))
        .setCreatedDate(convert(objects[3], LocalDateTime.class))
        .setGroupId(convert(objects[4], Long.class))
        .setGroupName(convert(objects[5], String.class))
        .setGroupCode(convert(objects[6], String.class))
        .setGroupEnabled(convert(objects[7], Boolean.class))
        .setGroupRemark(convert(objects[8], String.class))
        .setUserId(convert(objects[9], Long.class))
        .setFullname(convert(objects[10], String.class))
        .setAvatar(convert(objects[11], String.class))
        .setMobile(convert(objects[12], String.class));
    groupUser.setTenantId(convert(objects[1], Long.class));
    return groupUser;
  }

  public static User ldapToUser(Attributes attrs, UserDirectory ldap) throws NamingException {
    DirectoryUserSchema userMode = ldap.getUserSchemaData();
    User user = new User().setId(getCachedUidGenerator().getUID())
        .setUsername(convertIfAbsent(attrs, userMode.getUsernameAttribute()))
        .setFullname(convertIfAbsent(attrs, userMode.getDisplayNameAttribute()))
        .setFirstName(convertIfAbsent(attrs, userMode.getFirstNameAttribute()))
        .setLastName(convertIfAbsent(attrs, userMode.getLastNameAttribute()))
        .setPassword(generatePassword(ldap.getUserSchemaData().getPassdEncoderType(),
            convertIfAbsent(attrs, userMode.getPassdAttribute())))
        .setSource(UserSource.LDAP_SYNCHRONIZE)
        .setMobile(nullSafe(convertIfAbsent(attrs, userMode.getMobileAttribute()), ""))
        .setEmail(nullSafe(convertIfAbsent(attrs, userMode.getEmailAttribute()), ""))
        .setItc(EMPTY_STR)
        .setCountry(EMPTY_STR)
        .setLandline(EMPTY_STR)
        .setTitle(EMPTY_STR)
        .setEnabled(true)
        .setGender(Gender.UNKNOWN)
        .setAddress(null)
        .setAvatar(EMPTY_STR)
        .setDisableReason(EMPTY_STR)
        .setLocked(false)
        .setDeptHead(false)
        .setSysAdmin(false)
        .setDeleted(false)
        .setExpired(false)
        .setMainDeptId(EMPTY_NUMBER);
    user.setDirectoryId(ldap.getId());
    user.setSignupAccount("");
    user.setSignupAccountType(SignupType.NOOP);
    return user;
  }

  public static GroupUser ldapToMemberUser(Attributes attrs, UserDirectory ldap, Group group)
      throws NamingException {
    DirectoryUserSchema userMode = ldap.getUserSchemaData();
    return new GroupUser().setId(getCachedUidGenerator().getUID())
        .setDirectoryId(ldap.getId())
        .setGroupId(group.getId())
        .setUsername(convertIfAbsent(attrs, userMode.getUsernameAttribute()))
        .setGidNumber(convertIfAbsent(attrs, ldap.getMembershipSchemaData()
            .getMemberGroupAttribute()));
  }

  public static String convertIfAbsent(Attributes attributes, String attrName)
      throws NamingException {
    if (nonNull(attributes) && isNotEmpty(attrName)) {
      Attribute attribute = attributes.get(attrName);
      if (nonNull(attribute)) {
        if (attribute.get() instanceof byte[]) {
          try {
            return new String((byte[]) attribute.get(), DEFAULT_ENCODING);
          } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
          }
        } else {
          return attribute.get().toString();
        }
      }
    }
    return "";
  }

  public static String generatePassword(PassdEncoderType encoderType, String password) {
    // Warn:: The format of Spring security MD5 and LDAP MD5 is inconsistent.
    //    if (password.startsWith(PREFIX)) {
    //      return password;
    //    }
    //    if (Objects.isNull(encoderType)) {
    //      return PREFIX + PassdEncoderType.PLAINTEXT.getValue() + SUFFIX + password;
    //    }
    //    return PREFIX + encoderType.getValue() + SUFFIX + password;
    if (password.contains(UCConstant.PASSWORD_ENCRYP_TYPE_PREFIX)
        && password.contains(UCConstant.PASSWORD_ENCRYP_TYPE_SUFFIX)) {
      return UCConstant.PASSWORD_ENCRYP_TYPE_PREFIX + PASSWORD_PROXY_ENCRYP_TYPE
          + UCConstant.PASSWORD_ENCRYP_TYPE_SUFFIX + password
          .split(UCConstant.PASSWORD_ENCRYP_TYPE_SUFFIX)[1];
    }
    return UCConstant.PASSWORD_ENCRYP_TYPE_PREFIX + PASSWORD_PROXY_ENCRYP_TYPE
        + UCConstant.PASSWORD_ENCRYP_TYPE_SUFFIX + password;
  }

  public static String genUsername() {
    return getBidGenerator().getId(BID_USERNAME) + randomNumeric(3);
  }

  public static String genFullname() {
    return "User" + nextLong(10000, 99999);
  }
}
