package cloud.xcan.angus.api.manager.impl;

import static cloud.xcan.angus.api.manager.UCManagerMessage.USER_EMAIL_NOT_BIND;
import static cloud.xcan.angus.api.manager.UCManagerMessage.USER_EMAIL_NOT_BIND_CODE;
import static cloud.xcan.angus.api.manager.UCManagerMessage.USER_MOBILE_NOT_BIND;
import static cloud.xcan.angus.api.manager.UCManagerMessage.USER_MOBILE_NOT_BIND_CODE;
import static cloud.xcan.angus.api.manager.UCManagerMessage.USER_NOT_EXISTED_T;
import static cloud.xcan.angus.core.biz.BizAssert.assertNotEmpty;
import static cloud.xcan.angus.core.utils.BeanFieldUtils.setPropertyValue;
import static cloud.xcan.angus.remote.message.CommProtocolException.M.USER_DISABLED_KEY;
import static cloud.xcan.angus.remote.message.CommProtocolException.M.USER_DISABLED_T;
import static cloud.xcan.angus.remote.message.CommProtocolException.M.USER_EXPIRED_KEY;
import static cloud.xcan.angus.remote.message.CommProtocolException.M.USER_EXPIRED_T;
import static cloud.xcan.angus.remote.message.CommProtocolException.M.USER_LOCKED_KEY;
import static cloud.xcan.angus.remote.message.CommProtocolException.M.USER_LOCKED_T;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singleton;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.mcenter.MessageCenterOnlineRepo;
import cloud.xcan.angus.api.commonlink.tag.OrgTargetType;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.UserBase;
import cloud.xcan.angus.api.commonlink.user.UserBaseRepo;
import cloud.xcan.angus.api.commonlink.user.UserRepo;
import cloud.xcan.angus.api.commonlink.user.dept.DeptUserRepo;
import cloud.xcan.angus.api.commonlink.user.group.GroupUserRepo;
import cloud.xcan.angus.api.enums.AuthObjectType;
import cloud.xcan.angus.api.manager.DeptManager;
import cloud.xcan.angus.api.manager.GroupManager;
import cloud.xcan.angus.api.manager.UserManager;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.jpa.entity.projection.IdAndName;
import cloud.xcan.angus.core.utils.BeanFieldUtils;
import cloud.xcan.angus.remote.message.CommProtocolException;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import java.math.BigInteger;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Biz
public class UserManagerImpl implements UserManager {

  @Autowired(required = false)
  private UserRepo userRepo;

  @Autowired(required = false)
  private UserBaseRepo userBaseRepo;

  @Autowired(required = false)
  private GroupUserRepo groupUserRepo;

  @Autowired(required = false)
  private DeptUserRepo deptUserRepo;

  @Autowired(required = false)
  private GroupManager groupManager;

  @Autowired(required = false)
  private DeptManager deptManager;

  @Autowired(required = false)
  private MessageCenterOnlineRepo commonMsgCenterOnlineRepo;

  @Override
  public User findUser(Long userId) {
    return userRepo.findUserByUserId(userId);
  }

  @Override
  public UserBase findUserBase(Long userId) {
    return userBaseRepo.findById(userId).orElse(null);
  }

  @Override
  public List<User> findUsers(Collection<Long> userIds) {
    return userRepo.findByIdIn(userIds);
  }

  @Override
  public List<UserBase> findUserBases(Collection<Long> userIds) {
    return userBaseRepo.findByIdIn(userIds);
  }

  @Override
  public List<User> findByTenantId(Long tenantId) {
    return userRepo.findAllByTenantId(tenantId);
  }

  @Override
  public List<User> findValidByTenantId(Long tenantId) {
    return userRepo.findValidByTenantId(tenantId);
  }

  @Override
  public List<User> findByTenantIdAndDirectoryId(Long tenantId, Long directoryId) {
    return userRepo.findByTenantIdAndDirectoryId(tenantId, directoryId);
  }

  @Override
  public List<User> findValidSysAdminByTenantId(Long tenantId) {
    return userRepo.findValidSysAdminByTenantId(tenantId);
  }

  @Override
  public Set<Long> findUserIdsByOrgIds(Collection<Long> orgIds) {
    Set<Long> userIds = new HashSet<>();
    if (isNotEmpty(orgIds)) {
      userIds.addAll(userRepo.findUserIdsByIdIn(orgIds));
      userIds.addAll(getUserIdsByDeptIds(orgIds));
      userIds.addAll(getUserIdsByGroupIds(orgIds));
    }
    return userIds;
  }

  @Override
  public Set<Long> findValidUserIdsByOrgIds(Collection<Long> orgIds) {
    Set<Long> userIds = new HashSet<>();
    if (isNotEmpty(orgIds)) {
      userIds.addAll(userRepo.findValidUserIdsByIdIn(orgIds));
      userIds.addAll(getValidUserIdsByDeptIds(orgIds));
      userIds.addAll(getValidUserIdsByGroupIds(orgIds));
    }
    return userIds;
  }

  @Override
  public List<Long> findValidSysAdminIdsByTenantId(Long tenantId) {
    return userRepo.findValidSysAdminIdsByTenantId(tenantId);
  }

  @Override
  public List<User> findSysAdminByTenantId(Long tenantId) {
    return userRepo.findByTenantIdAndSysAdmin(tenantId, true);
  }

  @Override
  public List<Long> findUserIdsByIdIn(Collection<Long> userIds) {
    return userRepo.findUserIdsByIdIn(userIds);
  }

  @Override
  public User checkAndFind(Long userId) {
    return userRepo.findById(userId).orElseThrow(()
        -> ResourceNotFound.of(USER_NOT_EXISTED_T, new Object[]{userId}));
  }

  @Override
  public void checkExists(Long userId) {
    if (!userRepo.existsById(userId)) {
      throw ResourceNotFound.of(USER_NOT_EXISTED_T, new Object[]{userId});
    }
  }

  @Override
  public List<User> checkAndFind(Collection<Long> userIds) {
    if (isEmpty(userIds)) {
      return emptyList();
    }
    List<User> userInfos = userRepo.findByIdIn(userIds);
    if (isEmpty(userInfos)) {
      throw ResourceNotFound.of(USER_NOT_EXISTED_T, new Object[]{userIds.iterator().next()});
    }
    if (userIds.size() != userInfos.size()) {
      Collection<Long> idsDb = userInfos.stream().map(User::getId).collect(Collectors.toSet());
      userIds.removeAll(idsDb);
      throw ResourceNotFound.of(USER_NOT_EXISTED_T, new Object[]{userIds.iterator().next()});
    }
    return userInfos;
  }

  @Override
  public void checkExists(Collection<Long> userIds) {
    if (isEmpty(userIds)) {
      return;
    }
    List<Long> userIdsDb = userRepo.findIdsByIdIn(userIds);
    if (isEmpty(userIdsDb)) {
      throw ResourceNotFound.of(USER_NOT_EXISTED_T, new Object[]{userIds.iterator().next()});
    }
    if (userIds.size() != userIdsDb.size()) {
      userIds.removeAll(userIdsDb);
      throw ResourceNotFound.of(USER_NOT_EXISTED_T, new Object[]{userIds.iterator().next()});
    }
  }

  @Override
  public User checkValidAndFind(Long userId) {
    if (isNull(userId)) {
      return null;
    }
    Optional<User> user = userRepo.findById(userId);
    if (user.isEmpty()) {
      throw ResourceNotFound.of(USER_NOT_EXISTED_T, new Object[]{userId});
    }
    checkUserValid(user.get());
    return user.get();
  }

  @Override
  public List<User> checkValidAndFind(Collection<Long> userIds) {
    if (isEmpty(userIds)) {
      return emptyList();
    }
    List<User> userInfos = userRepo.findByIdIn(userIds);
    if (isEmpty(userInfos)) {
      throw ResourceNotFound.of(USER_NOT_EXISTED_T, new Object[]{userIds.iterator().next()});
    }
    if (userIds.size() != userInfos.size()) {
      Collection<Long> idsDb = userInfos.stream().map(User::getId).collect(Collectors.toSet());
      userIds.removeAll(idsDb);
      throw ResourceNotFound.of(USER_NOT_EXISTED_T, new Object[]{userIds.iterator().next()});
    }
    for (User user : userInfos) {
      checkUserValid(user);
    }
    return userInfos;
  }

  @Override
  public UserBase checkValidAndFindUserBase(Long userId) {
    if (isNull(userId)) {
      return null;
    }
    Optional<UserBase> userBase = userBaseRepo.findById(userId);
    if (userBase.isEmpty()) {
      throw ResourceNotFound.of(USER_NOT_EXISTED_T, new Object[]{userId});
    }
    checkUserValid(userBase.get());
    return userBase.get();
  }

  @Override
  public List<UserBase> checkValidAndFindUserBase(Collection<Long> userIds) {
    if (isEmpty(userIds)) {
      return emptyList();
    }
    List<UserBase> userBases = userBaseRepo.findByIdIn(userIds);
    if (isEmpty(userBases)) {
      throw ResourceNotFound.of(USER_NOT_EXISTED_T, new Object[]{userIds.iterator().next()});
    }
    if (userIds.size() != userBases.size()) {
      Collection<Long> idsDb = userBases.stream().map(UserBase::getId).collect(Collectors.toSet());
      userIds.removeAll(idsDb);
      throw ResourceNotFound.of(USER_NOT_EXISTED_T, new Object[]{userIds.iterator().next()});
    }
    for (UserBase user : userBases) {
      checkUserValid(user);
    }
    return userBases;
  }

  @Override
  public Map<String, List<UserBase>> checkValidAndFindUserBasesByName(Collection<String> names) {
    if (isEmpty(names)) {
      return emptyMap();
    }
    List<UserBase> userBases = userBaseRepo.findByFullnameIn(names);
    if (isEmpty(userBases)) {
      throw ResourceNotFound.of(USER_NOT_EXISTED_T, new Object[]{names.iterator().next()});
    }
    if (names.size() != userBases.size()) {
      Collection<String> namesDb = userBases.stream()
          .map(UserBase::getFullname).collect(Collectors.toSet());
      names.removeAll(namesDb);
      throw ResourceNotFound.of(USER_NOT_EXISTED_T, new Object[]{names.iterator().next()});
    }
    for (UserBase user : userBases) {
      checkUserValid(user);
    }
    return userBases.stream().collect(Collectors.groupingBy(UserBase::getFullname));
  }

  @Override
  public void checkUserValid(User user) {
    if (!user.getEnabled()) {
      throw CommProtocolException
          .of(USER_DISABLED_T, USER_DISABLED_KEY, new Object[]{user.getFullname()});
    }
    if (user.getLocked()) {
      throw CommProtocolException
          .of(USER_LOCKED_T, USER_LOCKED_KEY, new Object[]{user.getFullname()});
    }
    if (user.getExpired()) {
      throw CommProtocolException
          .of(USER_EXPIRED_T, USER_EXPIRED_KEY, new Object[]{user.getFullname()});
    }
  }

  @Override
  public void checkUserValid(UserBase userBase) {
    if (!userBase.getEnabled()) {
      throw CommProtocolException
          .of(USER_DISABLED_T, USER_DISABLED_KEY, new Object[]{userBase.getFullname()});
    }
    if (userBase.getLocked()) {
      throw CommProtocolException
          .of(USER_LOCKED_T, USER_LOCKED_KEY, new Object[]{userBase.getFullname()});
    }
    if (userBase.getExpired()) {
      throw CommProtocolException
          .of(USER_EXPIRED_T, USER_EXPIRED_KEY, new Object[]{userBase.getFullname()});
    }
  }

  @Override
  public Map<Long, User> checkValidAndGetUserMap(Collection<Long> userIds) {
    List<User> userInfos = checkValidAndFind(userIds);
    return isEmpty(userInfos) ? null
        : userInfos.stream().collect(Collectors.toMap(User::getId, x -> x));
  }

  @Override
  public void checkOrgExists(OrgTargetType type, Long orgId) {
    switch (type) {
      case USER:
        checkAndFind(orgId);
        break;
      case GROUP:
        groupManager.checkExists(List.of(orgId));
        break;
      case DEPT:
        deptManager.checkExists(List.of(orgId));
        break;
      default:
        // NOOP
    }
  }

  @Override
  public List<?> findOrgs(OrgTargetType type, Collection<Long> orgIds) {
    return switch (type) {
      case USER -> userBaseRepo.findByIdIn(orgIds);
      case GROUP -> groupManager.find(orgIds);
      case DEPT -> deptManager.find(orgIds);
    };
  }

  @Override
  public void checkOrgExists(OrgTargetType type, Collection<Long> orgIds) {
    switch (type) {
      case USER:
        checkExists(orgIds);
        break;
      case GROUP:
        groupManager.checkExists(orgIds);
        break;
      case DEPT:
        deptManager.checkExists(orgIds);
        break;
      default:
        // NOOP
    }
  }

  @Override
  public Object checkOrgAndFind(OrgTargetType type, Long orgId) {
    return switch (type) {
      case USER -> checkAndFind(orgId);
      case GROUP -> groupManager.checkAndFind(orgId);
      case DEPT -> deptManager.checkAndFind(orgId);
    };
  }

  @Override
  public List<?> checkOrgAndFind(OrgTargetType type, Collection<Long> orgIds) {
    return switch (type) {
      case USER -> checkAndFind(orgIds);
      case GROUP -> groupManager.checkAndFind(orgIds);
      case DEPT -> deptManager.checkAndFind(orgIds);
    };
  }

  @Override
  public void checkOrgValid(OrgTargetType type, Long orgId) {
    switch (type) {
      case USER:
        checkValidAndFindUserBase(orgId);
        break;
      case GROUP:
        groupManager.checkValidAndFind(orgId);
        break;
      case DEPT:
        deptManager.checkAndFind(orgId);
        break;
      default:
        // NOOP
    }
  }

  @Override
  public void checkOrgValid(OrgTargetType type, Collection<Long> orgIds) {
    switch (type) {
      case USER:
        checkValidAndFindUserBase(orgIds);
        break;
      case GROUP:
        groupManager.checkValidAndFind(orgIds);
        break;
      case DEPT:
        deptManager.checkAndFind(orgIds);
        break;
      default:
        // NOOP
    }
  }

  @Override
  public Object checkOrgValidAndFind(OrgTargetType type, Long orgId) {
    return switch (type) {
      case USER -> checkValidAndFindUserBase(orgId);
      case GROUP -> groupManager.checkValidAndFind(orgId);
      case DEPT -> deptManager.checkAndFind(orgId);
    };
  }

  @Override
  public List<?> checkOrgValidAndFind(OrgTargetType type, Collection<Long> orgIds) {
    return switch (type) {
      case USER -> checkValidAndFindUserBase(orgIds);
      case GROUP -> groupManager.checkValidAndFind(orgIds);
      case DEPT -> deptManager.checkAndFind(orgIds);
    };
  }

  @Override
  public void checkUserMobileExists(List<User> users) {
    if (isNotEmpty(users)) {
      for (User user : users) {
        assertNotEmpty(user.getMobile(), USER_MOBILE_NOT_BIND_CODE, USER_MOBILE_NOT_BIND);
      }
    }
  }

  @Override
  public void checkUserEmailExists(List<User> users) {
    if (isNotEmpty(users)) {
      for (User user : users) {
        assertNotEmpty(user.getEmail(), USER_EMAIL_NOT_BIND_CODE, USER_EMAIL_NOT_BIND);
      }
    }
  }

  @Override
  public Set<Long> getUserIdByOrgType0(AuthObjectType orgType, Long orgId) {
    Set<Long> userIds = new HashSet<>();
    switch (orgType) {
      case DEPT:
        assert orgId != null;
        userIds.addAll(getUserIdsByDeptIds(singleton(orgId)));
        break;
      case GROUP:
        assert orgId != null;
        userIds.addAll(getUserIdsByGroupIds(singleton(orgId)));
        break;
      default:
        // By USER
        if (nonNull(orgId)) {
          userIds.add(orgId);
        }
    }
    return userIds;
  }

  @Override
  public Set<Long> getUserIdByOrgType(AuthObjectType orgType, Long orgId) {
    Set<Long> userIds = new HashSet<>();
    switch (orgType) {
      case DEPT:
        assert orgId != null;
        userIds.addAll(getUserIdsByDeptIds(singleton(orgId)));
        break;
      case GROUP:
        assert orgId != null;
        userIds.addAll(getUserIdsByGroupIds(singleton(orgId)));
        break;
      default:
        // By USER
        userIds.add(nonNull(orgId) ? orgId : getUserId());
    }
    return userIds;
  }

  @Override
  public Set<Long> getUserIdByOrgTypeIn0(AuthObjectType orgType, Collection<Long> orgIds) {
    Set<Long> userIds = new HashSet<>();
    switch (orgType) {
      case DEPT:
        assert orgIds != null;
        userIds.addAll(getUserIdsByDeptIds(orgIds));
        break;
      case GROUP:
        assert orgIds != null;
        userIds.addAll(getUserIdsByGroupIds(orgIds));
        break;
      default:
        // By USER
        if (nonNull(orgIds)) {
          userIds.addAll(orgIds);
        }
    }
    return userIds;
  }

  @Override
  public Set<Long> getAllValidUserIds() {
    int page = -1;
    Set<Long> allUserIds = new HashSet<>();
    Page<BigInteger> userIdPage;
    // Page<Long> userIdPage; -> Fix:: ClassCastException: class java.math.BigInteger cannot be cast to class java.lang.Long (java.math.BigInteger and java.lang.Long are in module java.base of loader 'bootstrap')at com.mysql.jdbc.ConnectionImpl.buildCollationMapping(Connec
    do {
      userIdPage = userRepo.findValidId(PageRequest.of(++page, 2000));
      if (userIdPage.hasContent()) {
        allUserIds.addAll(userIdPage.getContent().stream().map(BigInteger::longValue)
            .collect(Collectors.toSet()));
      }
    } while (userIdPage.hasNext());
    return allUserIds;
  }

  @Override
  public Set<Long> getAllValidUserIdsByTenantId(Long tenantId) {
    int page = -1;
    Set<Long> allUserIds = new HashSet<>();
    Page<BigInteger> userIdPage;
    // Page<Long> userIdPage; -> Fix:: ClassCastException: class java.math.BigInteger cannot be cast to class java.lang.Long (java.math.BigInteger and java.lang.Long are in module java.base of loader 'bootstrap')at com.mysql.jdbc.ConnectionImpl.buildCollationMapping(Connec
    do {
      userIdPage = userRepo.findValidIdByTenantId(tenantId, PageRequest.of(++page, 2000));
      if (userIdPage.hasContent()) {
        allUserIds.addAll(userIdPage.getContent().stream().map(BigInteger::longValue)
            .collect(Collectors.toSet()));
      }
    } while (userIdPage.hasNext());
    return allUserIds;
  }

  @Override
  public Set<Long> getValidUserIdsByGroupIds(Collection<Long> groupIds) {
    return groupUserRepo.findValidUserIdsByGroupIds(groupIds);
  }

  @Override
  public Set<Long> getValidUserIdsByGroupIds(Long tenantId, Collection<Long> groupIds) {
    return groupUserRepo.findValidUserIdsByTenantIdAndGroupIds(tenantId, groupIds);
  }

  @Override
  public Set<Long> getUserIdsByGroupIds(Collection<Long> groupIds) {
    return groupUserRepo.findUserIdsByGroupIds(groupIds);
  }

  @Override
  public Set<Long> getValidUserIdsByDeptIds(Collection<Long> deptIds) {
    return deptUserRepo.findValidUserIdsByDeptIds(deptIds);
  }

  @Override
  public Set<Long> getValidUserIdsByDeptIds(Long tenantId, Collection<Long> deptIds) {
    return deptUserRepo.findValidUserIdsByTenantIdAndDeptIds(tenantId, deptIds);
  }

  @Override
  public Set<Long> getUserIdsByDeptIds(Collection<Long> deptIds) {
    return deptUserRepo.findUserIdsByDeptIds(deptIds);
  }

  @Override
  public List<Long> getValidOrgAndUserIds() {
    Long currentUserId = PrincipalContext.getUserId();
    List<Long> ids = userRepo.findValidOrgIdsById(currentUserId);
    ids.add(currentUserId);
    return ids;
  }

  @Override
  public List<Long> getValidOrgAndUserIds(Long userId) {
    List<Long> ids = userRepo.findValidOrgIdsById(userId);
    ids.add(userId);
    return ids;
  }

  @Override
  public List<Long> getOrgAndUserIds() {
    Long currentUserId = PrincipalContext.getUserId();
    List<Long> ids = userRepo.findValidOrgIdsById(currentUserId);
    ids.add(currentUserId);
    return ids;
  }

  @Override
  public List<Long> getOrgAndUserIds(Long userId) {
    List<Long> ids = userRepo.findValidOrgIdsById(userId);
    ids.add(userId);
    return ids;
  }

  @Override
  public Map<Long, String> getOrgNameByIds(Collection<Long> orgIds) {
    List<IdAndName> idAndNames = userRepo.findOrgIdAndNameByIds(orgIds);
    return isEmpty(idAndNames) ? emptyMap()
        : idAndNames.stream().collect(Collectors.toMap(IdAndName::getId, IdAndName::getName));
  }

  @Override
  public Map<Long, User> getValidUserInfoMap(Collection<Long> userIds) {
    if (isEmpty(userIds)) {
      return emptyMap();
    }
    return userRepo.findValidByIdIn(userIds).stream()
        .collect(Collectors.toMap(User::getId, o -> o));
  }

  @Override
  public Map<Long, User> getUserInfoMap(Collection<Long> userIds) {
    if (isEmpty(userIds)) {
      return emptyMap();
    }
    return userRepo.findByIdIn(userIds).stream()
        .collect(Collectors.toMap(User::getId, o -> o));
  }

  @Override
  public Map<Long, UserBase> getValidUserBaseMap(Collection<Long> userIds) {
    if (isEmpty(userIds)) {
      return emptyMap();
    }
    return userBaseRepo.findValidByIdIn(userIds).stream()
        .collect(Collectors.toMap(UserBase::getId, o -> o));
  }

  @Override
  public Map<Long, UserBase> getUserBaseMap(Collection<Long> userIds) {
    if (isEmpty(userIds)) {
      return emptyMap();
    }
    return userBaseRepo.findByIdIn(userIds).stream()
        .collect(Collectors.toMap(UserBase::getId, o -> o));
  }

  @SneakyThrows
  @Override
  public void setUserNameAndAvatar(Collection<?> targets, String userIdField) {
    setUserNameAndAvatar(targets, userIdField, "createdByName", "avatar");
  }

  @SneakyThrows
  @Override
  public void setUserNameAndAvatar(Collection<?> targets, String userIdField,
      String fullNameField, String userAvatarField) {
    if (isNotEmpty(targets)) {
      Set<Long> userIds = new HashSet<>();
      for (Object target : targets) {
        Object userId = FieldUtils.readField(target, userIdField, true);
        if (nonNull(userId)) {
          userIds.add(Long.parseLong(userId.toString()));
        }
      }
      Map<Long, UserBase> userDbMap = getUserBaseMap(userIds);
      if (isEmpty(userDbMap)) {
        return;
      }
      Object first = targets.stream().findFirst().orElseThrow();
      boolean hasName = BeanFieldUtils.hasProperty(first, fullNameField);
      boolean hasAvatar = BeanFieldUtils.hasProperty(first, userAvatarField);
      for (Object target : targets) {
        Object userId = FieldUtils.readField(target, userIdField, true);
        if (nonNull(userId)) {
          UserBase user = userDbMap.get(Long.parseLong(userId.toString()));
          if (nonNull(user)) {
            if (hasName) {
              setPropertyValue(target, fullNameField, user.getFullname());
            }
            if (hasAvatar) {
              setPropertyValue(target, userAvatarField, user.getAvatar());
            }
          }
        }
      }
    }
  }

}
