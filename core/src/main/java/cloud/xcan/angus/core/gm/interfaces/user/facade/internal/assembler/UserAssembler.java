package cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler;

import static cloud.xcan.angus.core.gm.application.converter.UserConverter.genFullname;
import static cloud.xcan.angus.core.gm.application.converter.UserConverter.genUsername;
import static cloud.xcan.angus.core.spring.SpringContextHolder.getCachedUidGenerator;
import static cloud.xcan.angus.core.utils.CoreUtils.calcPasswordStrength;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;
import static cloud.xcan.angus.spec.utils.ObjectUtils.stringSafe;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;

import cloud.xcan.angus.api.commonlink.tag.OrgTagTarget;
import cloud.xcan.angus.api.commonlink.tag.OrgTargetType;
import cloud.xcan.angus.api.commonlink.user.SignupType;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.dept.DeptUser;
import cloud.xcan.angus.api.commonlink.user.group.GroupUser;
import cloud.xcan.angus.api.enums.Gender;
import cloud.xcan.angus.api.enums.PasswordStrength;
import cloud.xcan.angus.api.enums.UserSource;
import cloud.xcan.angus.api.gm.tenant.dto.TenantAddByMobileDto;
import cloud.xcan.angus.api.gm.tenant.dto.TenantAddDto;
import cloud.xcan.angus.api.gm.user.dto.UserFindDto;
import cloud.xcan.angus.api.gm.user.dto.UserSearchDto;
import cloud.xcan.angus.api.gm.user.to.UserDeptTo;
import cloud.xcan.angus.api.gm.user.to.UserGroupTo;
import cloud.xcan.angus.api.gm.user.vo.UserDetailVo;
import cloud.xcan.angus.api.gm.user.vo.UserListVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserAddDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserSysAdminVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.dto.EnabledOrDisabledDto;
import cloud.xcan.angus.remote.info.IdAndName;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UserAssembler {

  public static User addDtoToDomain(UserAddDto dto, UserSource source) {
    Long userId = nullSafe(dto.getId(), getCachedUidGenerator().getUID());
    String username = isEmpty(dto.getUsername()) ? genUsername() : dto.getUsername();
    dto.setId(userId);
    dto.setUsername(username);

    User user = new User().setId(userId)
        .setUsername(username)
        .setFirstName(stringSafe(dto.getFirstName()))
        .setLastName(stringSafe(dto.getLastName()))
        .setFullName(stringSafe(dto.getFullName(), genFullname()))
        .setItc(dto.getItc())
        .setCountry(dto.getCountry())
        .setMobile(stringSafe(dto.getMobile()))
        .setEmail(stringSafe(dto.getEmail()))
        .setLandline(dto.getLandline())
        .setAvatar(dto.getAvatar())
        .setTitle(dto.getTitle())
        .setGender(nullSafe(dto.getGender(), Gender.UNKNOWN))
        .setAddress(dto.getAddress())
        .setSource(source)
        .setSysAdmin(nullSafe(dto.getSysAdmin(), false))
        .setPassword(dto.getPassword())
        .setPasswordStrength(isEmpty(dto.getPassword()) ? PasswordStrength.UNKNOWN
            : calcPasswordStrength(dto.getPassword()))
        .setEnabled(nullSafe(dto.getEnabled(), true))
        .setDisableReason("")
        .setLocked(false)
        .setInvitationCode(stringSafe(dto.getInvitationCode()))
        .setPasswordExpired(false)
        .setExpired(false)
        .setDeleted(false)
        .setMainDeptId(judgeMainDeptId(dto.getDepts()));
    if (UserSource.BACKGROUND_ADDED.equals(source)) {
      user.setSignupAccount(null);
      user.setSignupAccountType(SignupType.NOOP);
      user.setCreatedBy(getUserId()).setLastModifiedBy(getUserId());
    } else if (UserSource.PLATFORM_SIGNUP.equals(source)
        || UserSource.INVITATION_CODE_SIGNUP.equals(source)) {
      user.setInvitationCode(dto.getInvitationCode());
      user.setSignupAccountType(
          isBlank(dto.getMobile()) ? SignupType.EMAIL : SignupType.MOBILE);
      user.setSignupAccount(
          isBlank(dto.getMobile()) ? dto.getEmail() : dto.getMobile());
      user.setCreatedBy(getUserId()).setLastModifiedBy(getUserId());
    } else if (UserSource.BACKGROUND_SIGNUP.equals(source)) {
      // @see UserAssembler#addTenantToUserAddDto()
      user.setSignupAccountType(dto.getSignupType());
      user.setSignupAccount(dto.getSignupAccount());
      user.setCreatedBy(getUserId()).setLastModifiedBy(getUserId());
    } else if (UserSource.LDAP_SYNCHRONIZE.equals(source)) {
      user.setSignupAccount(null);
      user.setSignupAccountType(SignupType.NOOP);
      user.setCreatedBy(-1L).setLastModifiedBy(-1L);
    } else if (UserSource.THIRD_PARTY_LOGIN.equals(source)) {
      user.setSignupAccount(null);
      user.setSignupAccountType(SignupType.NOOP);
      user.setCreatedBy(-1L).setLastModifiedBy(-1L);
    }
    return user;
  }

  public static UserAddDto addTenantToUserAddDto(TenantAddDto dto) {
    return new UserAddDto().setFullName(stringSafe(dto.getFullName()))
        .setFirstName(dto.getFirstName())
        .setLastName(dto.getLastName())
        .setEmail(dto.getEmail())
        .setMobile(dto.getMobile())
        .setItc(dto.getItc())
        .setCountry(dto.getCountry())
        .setPassword(dto.getPassword())
        .setTitle(dto.getTitle())
        .setSysAdmin(true)
        .setSignupType(isNotEmpty(dto.getMobile()) ? SignupType.MOBILE : SignupType.EMAIL)
        .setSignupAccount(isNotEmpty(dto.getMobile()) ? dto.getMobile() : dto.getEmail());
  }

  public static UserAddDto addTenantToUserAddDto(TenantAddByMobileDto dto) {
    return new UserAddDto().setFullName(stringSafe(dto.getFullName()))
        .setFirstName(dto.getFirstName())
        .setLastName(dto.getLastName())
        .setEmail(dto.getEmail())
        .setMobile(dto.getMobile())
        .setItc(dto.getItc())
        .setCountry(dto.getCountry())
        .setPassword(dto.getPassword())
        .setSysAdmin(true)
        .setSignupType(isNotEmpty(dto.getMobile()) ? SignupType.MOBILE : SignupType.EMAIL)
        .setSignupAccount(isNotEmpty(dto.getMobile()) ? dto.getMobile() : dto.getEmail());
  }

  public static User updateDtoToDomain(UserUpdateDto dto) {
    return new User()
        .setId(dto.getId())
        .setUsername(dto.getUsername())
        .setFirstName(dto.getFirstName())
        .setLastName(dto.getLastName())
        .setFullName(dto.getFullName())
        .setItc(dto.getItc())
        .setCountry(dto.getCountry())
        .setMobile(dto.getMobile())
        .setEmail(dto.getEmail())
        .setLandline(dto.getLandline())
        .setAvatar(dto.getAvatar())
        .setTitle(dto.getTitle())
        .setGender(nullSafe(dto.getGender(), Gender.UNKNOWN))
        .setAddress(dto.getAddress())
        .setPassword(dto.getPassword())
        .setPasswordStrength(isEmpty(dto.getPassword()) ? PasswordStrength.UNKNOWN
            : calcPasswordStrength(dto.getPassword()))
        .setMainDeptId(judgeMainDeptId(dto.getDepts()));
  }

  public static User replaceDtoToDomain(UserReplaceDto dto) {
    return new User()
        .setId(dto.getId())
        .setUsername(stringSafe(dto.getUsername()))
        .setFirstName(stringSafe(dto.getFirstName()))
        .setLastName(stringSafe(dto.getLastName()))
        .setFullName(stringSafe(dto.getFullName()))
        .setItc(dto.getItc())
        .setCountry(dto.getCountry())
        .setMobile(stringSafe(dto.getMobile()))
        .setEmail(stringSafe(dto.getEmail()))
        .setLandline(dto.getLandline())
        .setAvatar(dto.getAvatar())
        .setTitle(dto.getTitle())
        .setGender(nullSafe(dto.getGender(), Gender.UNKNOWN))
        .setAddress(dto.getAddress())
        .setPassword(dto.getPassword())
        .setPasswordStrength(isEmpty(dto.getPassword()) ? PasswordStrength.UNKNOWN
            : calcPasswordStrength(dto.getPassword()))
        .setEnabled(isNull(dto.getId()) ? true : null)
        .setDisableReason(isNull(dto.getId()) ? "" : null)
        .setLocked(isNull(dto.getId()) ? false : null)
        .setInvitationCode(isNull(dto.getId()) ? stringSafe(dto.getInvitationCode()) : null)
        .setPasswordExpired(isNull(dto.getId()) ? false : null)
        .setExpired(isNull(dto.getId()) ? false : null)
        .setDeleted(isNull(dto.getId()) ? false : null)
        .setMainDeptId(judgeMainDeptId(dto.getDepts()))
        // BACKGROUND_ADDED
        .setSignupAccount(null)
        .setSignupAccountType(isNull(dto.getId()) ? SignupType.NOOP : null)
        .setCreatedBy(isNull(dto.getId()) ? getUserId() : null)
        .setLastModifiedBy(isNull(dto.getId()) ? getUserId() : null);
  }

  public static UserDetailVo toDetailVo(User user) {
    return new UserDetailVo()
        .setId(user.getId())
        .setUsername(user.getUsername())
        .setFullName(user.getFullName())
        .setFirstName(user.getFirstName())
        .setPasswordStrength(user.getPasswordStrength())
        .setLastName(user.getLastName())
        .setItc(user.getItc())
        .setCountry(user.getCountry())
        .setMobile(user.getMobile())
        .setEmail(user.getEmail())
        .setLandline(user.getLandline())
        .setAvatar(user.getAvatar())
        .setTitle(user.getTitle())
        .setGender(user.getGender())
        .setAddress(user.getAddress())
        .setSysAdmin(user.getSysAdmin())
        .setDeptHead(user.getDeptHead())
        .setMainDeptId(user.getMainDeptId())
        .setOnline(user.getOnline())
        .setOnlineDate(user.getOnlineDate())
        .setOfflineDate(user.getOfflineDate())
        .setEnabled(user.getEnabled())
        .setCreatedDate(user.getCreatedDate())
        .setSource(user.getSource())
        .setLocked(user.getLocked())
        .setLockStartDate(user.getLockStartDate())
        .setLockEndDate(user.getLockEndDate())
        .setTenantId(user.getTenantId())
        .setTenantName(user.getTenantName())
        .setCreatedBy(user.getCreatedBy())
        .setCreatedDate(user.getCreatedDate())
        .setLastModifiedBy(user.getLastModifiedBy())
        .setLastModifiedDate(user.getLastModifiedDate())
        .setPasswordStrength(user.getPasswordStrength())
        .setPasswordExpired(user.getPasswordExpired())
        .setPasswordExpiredDate(user.getPasswordExpiredDate())
        .setTags(isEmpty(user.getTags()) ? null : user.getTags().stream()
            .map(tag -> new IdAndName().setId(tag.getTag().getId()).setName(tag.getTag().getName()))
            .collect(Collectors.toList()))
        .setDepts(isEmpty(user.getDepts()) ? null : user.getDepts().stream()
            .map(dept -> new UserDeptTo().setId(dept.getDeptId())
                /*.setName(dept.getDept().getName())*/.setMainDept(dept.getMainDept())
                .setDeptHead(dept.getDeptHead()))
            .collect(Collectors.toList()))
        .setGroups(isEmpty(user.getGroups()) ? null : user.getGroups().stream()
            .map(group -> new UserGroupTo().setId(group.getGroupId())
                /*.setName(group.getGroup().getName())*/)
            .collect(Collectors.toList()));
  }

  public static UserListVo toListVo(User user) {
    return new UserListVo().setId(user.getId())
        .setId(user.getId())
        .setUsername(user.getUsername())
        .setFullName(user.getFullName())
        .setFirstName(user.getFirstName())
        .setLastName(user.getLastName())
        .setItc(user.getItc())
        .setCountry(user.getCountry())
        .setMobile(user.getMobile())
        .setEmail(user.getEmail())
        .setLandline(user.getLandline())
        .setAvatar(user.getAvatar())
        .setTitle(user.getTitle())
        .setGender(user.getGender())
        .setAddress(user.getAddress())
        .setSysAdmin(user.getSysAdmin())
        .setDeptHead(user.getDeptHead())
        .setMainDeptId(user.getMainDeptId())
        .setOnline(user.getOnline())
        .setOnlineDate(user.getOnlineDate())
        .setOfflineDate(user.getOfflineDate())
        .setEnabled(user.getEnabled())
        .setCreatedDate(user.getCreatedDate())
        .setSource(user.getSource())
        .setLocked(user.getLocked())
        .setLockStartDate(user.getLockStartDate())
        .setLockEndDate(user.getLockEndDate())
        .setTenantId(user.getTenantId())
        .setTenantName(user.getTenantName())
        .setCreatedBy(user.getCreatedBy())
        .setCreatedDate(user.getCreatedDate())
        .setLastModifiedBy(user.getLastModifiedBy())
        .setLastModifiedDate(user.getLastModifiedDate());
  }

  public static User enabledDtoToDomain(EnabledOrDisabledDto dto) {
    return new User().setId(dto.getId()).setEnabled(dto.getEnabled());
  }

  public static List<UserSysAdminVo> toAdminListVo(List<User> users) {
    if (isNotEmpty(users)) {
      return users.stream()
          .map(x -> new UserSysAdminVo()
              .setId(x.getId())
              .setFullName(x.getFullName())
              .setFirstName(x.getFirstName())
              .setLastName(x.getLastName())
              .setCountry(x.getCountry())
              .setItc(x.getItc())
              .setMobile(x.getMobile())
              .setEmail(x.getEmail())
              .setFirstSysAdmin(x.getFirstSysAdmin())
              .setCreatedDate(x.getCreatedDate()))
          .collect(Collectors.toList());
    }
    return null;
  }

  public static List<OrgTagTarget> dtoToUserTagDomain(Set<Long> userTagIds, Long userId) {
    return isEmpty(userTagIds) ? new ArrayList<>() :
        userTagIds.stream()
            .map(tagId -> new OrgTagTarget()
                .setId(getCachedUidGenerator().getUID())
                .setTargetType(OrgTargetType.USER)
                .setTargetId(userId)
                .setTagId(tagId).setCreatedBy(-1L))
            .collect(Collectors.toList());
  }

  public static List<DeptUser> dtoToUserDeptDomain(Set<UserDeptTo> userDeptTos, Long userId) {
    return isEmpty(userDeptTos) ? new ArrayList<>() :
        userDeptTos.stream()
            .map(uDept -> new DeptUser()
                .setId(getCachedUidGenerator().getUID())
                .setUserId(userId).setDeptId(uDept.getId())
                .setMainDept(nullSafe(uDept.getMainDept(), false))
                .setDeptHead(nullSafe(uDept.getDeptHead(), false))
                .setCreatedBy(-1L))
            .collect(Collectors.toList());
  }

  public static List<GroupUser> dtoToUserGroupDomain(Set<Long> userGroupIds, Long userId) {
    return isEmpty(userGroupIds) ? new ArrayList<>() :
        userGroupIds.stream()
            .map(groupId -> new GroupUser()
                .setId(getCachedUidGenerator().getUID())
                .setUserId(userId)
                .setGroupId(groupId)
                .setCreatedBy(-1L))
            .collect(Collectors.toList());
  }

  private static Long judgeMainDeptId(Set<UserDeptTo> deptUsers) {
    if (isNotEmpty(deptUsers)) {
      for (UserDeptTo ud : deptUsers) {
        if (ud.getMainDept()) {
          return ud.getId();
        }
      }
      // Default first
      return deptUsers.iterator().next().getId();
    }
    return null;
  }

  public static GenericSpecification<User> getSpecification(UserFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate")
        .matchSearchFields("fullName", "mobile", "title", "username")
        .orderByFields("id", "fullName", "createdDate")
        //.inAndNotFields("tagId")
        .build();
    return new GenericSpecification<>(filters);
  }

  public static Set<SearchCriteria> getSearchCriteria(UserSearchDto dto) {
    // Build the final filters
    return new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate")
        .matchSearchFields("fullName", "mobile", "title", "username")
        .orderByFields("id", "fullName", "createdDate")
        //.inAndNotFields("tagId")
        .build();
  }
}
