package cloud.xcan.angus.api.manager;

import cloud.xcan.angus.api.commonlink.tag.OrgTargetType;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.UserBase;
import cloud.xcan.angus.api.enums.AuthObjectType;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserManager {

  User findUser(Long userId);

  UserBase findUserBase(Long userId);

  List<User> findUsers(Collection<Long> userIds);

  List<UserBase> findUserBases(Collection<Long> userIds);

  List<User> findByTenantId(Long tenantId);

  List<User> findValidByTenantId(Long tenantId);

  List<User> findByTenantIdAndDirectoryId(Long tenantId, Long directoryId);

  List<User> findValidSysAdminByTenantId(Long tenantId);

  Set<Long> findUserIdsByOrgIds(Collection<Long> orgIds);

  Set<Long> findValidUserIdsByOrgIds(Collection<Long> orgIds);

  List<Long> findValidSysAdminIdsByTenantId(Long tenantId);

  List<User> findSysAdminByTenantId(Long tenantId);

  List<Long> findUserIdsByIdIn(Collection<Long> userIds);

  User checkAndFind(Long userId);

  void checkExists(Long userId);

  List<User> checkAndFind(Collection<Long> userIds);

  void checkExists(Collection<Long> userIds);

  User checkValidAndFind(Long userId);

  List<User> checkValidAndFind(Collection<Long> userIds);

  UserBase checkAndFindUserBase(Long userId);

  UserBase checkValidAndFindUserBase(Long userId);

  List<UserBase> checkValidAndFindUserBase(Collection<Long> userIds);

  Map<String, List<UserBase>> checkValidAndFindUserBasesByName(Collection<String> names);

  void checkUserValid(User userInfo);

  void checkUserValid(UserBase userBase);

  Map<Long, User> checkValidAndGetUserMap(Collection<Long> userIds);

  void checkOrgExists(OrgTargetType type, Long orgId);

  List<?> findOrgs(OrgTargetType type, Collection<Long> orgIds);

  void checkOrgExists(OrgTargetType type, Collection<Long> orgIds);

  Object checkOrgAndFind(OrgTargetType type, Long orgId);

  List<?> checkOrgAndFind(OrgTargetType type, Collection<Long> orgIds);

  void checkOrgValid(OrgTargetType type, Long orgId);

  void checkOrgValid(OrgTargetType type, Collection<Long> orgIds);

  Object checkOrgValidAndFind(OrgTargetType type, Long orgId);

  List<?> checkOrgValidAndFind(OrgTargetType type, Collection<Long> orgIds);

  void checkUserMobileExists(List<User> users);

  void checkUserEmailExists(List<User> users);

  Set<Long> getUserIdByOrgType0(AuthObjectType creatorObjectType, Long creatorObjectId);

  Set<Long> getUserIdByOrgType(AuthObjectType creatorObjectType,
      Long creatorObjectId);

  Set<Long> getUserIdByOrgTypeIn0(AuthObjectType orgType, Collection<Long> orgIds);

  Set<Long> getAllValidUserIds();

  Set<Long> getAllValidUserIdsByTenantId(Long tenantId);

  Set<Long> getValidUserIdsByGroupIds(Collection<Long> groupIds);

  Set<Long> getValidUserIdsByGroupIds(Long tenantId, Collection<Long> groupIds);

  Set<Long> getUserIdsByGroupIds(Collection<Long> deptIds);

  Set<Long> getValidUserIdsByDeptIds(Collection<Long> deptIds);

  Set<Long> getValidUserIdsByDeptIds(Long tenantId, Collection<Long> deptIds);

  Set<Long> getUserIdsByDeptIds(Collection<Long> deptIds);

  List<Long> getValidOrgAndUserIds();

  List<Long> getValidOrgAndUserIds(Long userId);

  List<Long> getOrgAndUserIds();

  List<Long> getOrgAndUserIds(Long userId);

  Map<Long, String> getOrgNameByIds(Collection<Long> orgIds);

  Map<Long, User> getValidUserInfoMap(Collection<Long> userIds);

  Map<Long, User> getUserInfoMap(Collection<Long> userIds);

  Map<Long, UserBase> getValidUserBaseMap(Collection<Long> userIds);

  Map<Long, UserBase> getUserBaseMap(Collection<Long> userIds);

  void setUserNameAndAvatar(Collection<?> targets, String userIdField);

  void setUserNameAndAvatar(Collection<?> targets, String userIdField, String userNameField,
      String userAvatarField);

}
