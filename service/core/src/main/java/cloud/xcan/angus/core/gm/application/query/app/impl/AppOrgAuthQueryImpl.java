package cloud.xcan.angus.core.gm.application.query.app.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.jpa.criteria.CriteriaUtils.findAllIdInAndEqualValues;
import static cloud.xcan.angus.core.jpa.criteria.CriteriaUtils.findMatchAndEqualValue;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getClientId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.emptySafe;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;

import cloud.xcan.angus.api.commonlink.AuthOrgType;
import cloud.xcan.angus.api.commonlink.dept.Dept;
import cloud.xcan.angus.api.commonlink.dept.DeptRepo;
import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.api.commonlink.group.GroupRepo;
import cloud.xcan.angus.api.commonlink.tenant.Tenant;
import cloud.xcan.angus.api.commonlink.tenant.TenantRepo;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.UserRepo;
import cloud.xcan.angus.api.manager.UserManager;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.app.AppOpenQuery;
import cloud.xcan.angus.core.gm.application.query.app.AppOrgAuthQuery;
import cloud.xcan.angus.core.gm.application.query.tag.WebTagQuery;
import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.gm.domain.app.AppRepo;
import cloud.xcan.angus.core.gm.domain.policy.AuthPolicy;
import cloud.xcan.angus.core.gm.domain.policy.AuthPolicyRepo;
import cloud.xcan.angus.core.gm.domain.policy.org.AuthOrgPolicyP;
import cloud.xcan.angus.core.gm.domain.policy.org.AuthPolicyOrgRepo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import jakarta.annotation.Resource;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@Biz
public class AppOrgAuthQueryImpl implements AppOrgAuthQuery {

  @Resource
  private TenantRepo tenantRepo;

  @Resource
  private UserRepo userRepo;

  @Resource
  private DeptRepo deptRepo;

  @Resource
  private GroupRepo groupRepo;

  @Resource
  private AppRepo appRepo;

  @Resource
  private AuthPolicyRepo authPolicyRepo;

  @Resource
  private UserManager userManager;

  @Resource
  private AuthPolicyOrgRepo authPolicyOrgRepo;

  @Resource
  private AppOpenQuery appOpenQuery;

  @Resource
  private WebTagQuery webTagQuery;

  @Override
  public Page<Tenant> appAuthTenant(Long appId, GenericSpecification<Tenant> spec,
      PageRequest pageable) {
    return new BizTemplate<Page<Tenant>>(false) {

      @Override
      protected void checkParams() {
        // NOOP:: appOpenQuery.checkAndFind(appId, getOptTenantId());
      }

      @Override
      protected Page<Tenant> process() {
        Set<String> allIds = findAllIdInAndEqualValues(spec.getCriteria(),
            "id", true);
        Page<BigInteger> tenantIdsPage = authPolicyOrgRepo
            .findAuthTenantIdsByAuthPolices(appId, allIds, pageable);
        if (!tenantIdsPage.hasContent()) {
          return Page.empty();
        }
        return new PageImpl<>(tenantRepo.findAllById(tenantIdsPage.getContent().stream().map(
            BigInteger::longValue).collect(Collectors.toList())), pageable,
            tenantIdsPage.getTotalElements());
      }
    }.execute();
  }

  @Override
  public Page<User> appAuthUser(Long appId, GenericSpecification<User> spec,
      PageRequest pageable) {
    return new BizTemplate<Page<User>>(false) {
      final Long tenantId = getOptTenantId();

      @Override
      protected void checkParams() {
        // Check the application is opened
        appOpenQuery.checkAndFind(appId, tenantId, false);
      }

      @Override
      protected Page<User> process() {
        // Whether all tenant users are authorized
        boolean tenantAllUserAuth = authPolicyOrgRepo.existsAuthAllUser(appId, tenantId) > 0;
        PrincipalContext.addExtension("globalAuth", tenantAllUserAuth);

        // Query full tenant users when tenantAllUserAuth = false
        List<Long> allAuthOrgIds = null;
        if (!tenantAllUserAuth) {
          // Query tenant user authorized organizations
          allAuthOrgIds = authPolicyOrgRepo.findAllAuthOrgIdsByAuthPolices(appId, tenantId, null);
          if (isEmpty(allAuthOrgIds)) {
            return Page.empty();
          }
        }

        // Query user pageable information
        Set<String> userIdsFilter = findAllIdInAndEqualValues(
            spec.getCriteria(), "id", true);
        String nameMatch = findMatchAndEqualValue(spec.getCriteria(),
            "fullName", true);

        Page<User> userPage = tenantAllUserAuth || isEmpty(allAuthOrgIds) ?
            userRepo.findAllValidByIdAndName(tenantId,
                emptySafe(userIdsFilter, null), nameMatch, pageable) :
            userRepo.findValidByOrgIdAndIdAndName(tenantId, allAuthOrgIds,
                emptySafe(userIdsFilter, null), nameMatch, pageable);
        if (userPage.isEmpty()) {
          return Page.empty();
        }

        // Query user authorized polices
        setUserAuthPolicy(appId, tenantId, userPage.getContent());
        return userPage;
      }
    }.execute();
  }

  @Override
  public Page<Dept> appAuthDept(Long appId, GenericSpecification<Dept> spec,
      PageRequest pageable) {
    return new BizTemplate<Page<Dept>>(false) {
      final Long tenantId = getOptTenantId();

      @Override
      protected void checkParams() {
        // Check the application is opened
        appOpenQuery.checkAndFind(appId, tenantId, true);
      }

      @Override
      protected Page<Dept> process() {
        Set<String> deptIdsFilter = findAllIdInAndEqualValues(
            spec.getCriteria(), "id", true);

        // Query authorized department
        List<Long> authDeptIds = authPolicyOrgRepo.findAllAuthDeptIdsByAuthPolices(
            appId, tenantId, emptySafe(deptIdsFilter, null));
        if (isEmpty(authDeptIds)) {
          return Page.empty();
        }

        // Query department information
        String nameMatch = findMatchAndEqualValue(spec.getCriteria(),
            "name", true);
        Page<Dept> deptPage = deptRepo.findByOrgIdAndIdAndName(tenantId,
            emptySafe(authDeptIds, null), emptySafe(deptIdsFilter, null), nameMatch, pageable);
        if (deptPage.isEmpty()) {
          return Page.empty();
        }

        // Query dept authorized polices
        setDeptAuthPolicy(appId, tenantId, deptPage.getContent());
        return deptPage;
      }
    }.execute();
  }

  @Override
  public Page<Group> appAuthGroup(Long appId, GenericSpecification<Group> spec,
      PageRequest pageable) {
    return new BizTemplate<Page<Group>>(false) {
      final Long tenantId = getOptTenantId();

      @Override
      protected void checkParams() {
        // Check the application is opened
        appOpenQuery.checkAndFind(appId, tenantId, true);
      }

      @Override
      protected Page<Group> process() {
        Set<String> groupIdsFilter = findAllIdInAndEqualValues(spec.getCriteria(), "id", true);

        // Query authorized department
        List<Long> authGroupIds = authPolicyOrgRepo.findAllAuthGroupIdsByAuthPolices(
            appId, tenantId, emptySafe(groupIdsFilter, null));
        if (isEmpty(authGroupIds)) {
          return Page.empty();
        }

        // Query department information
        String nameMatch = findMatchAndEqualValue(spec.getCriteria(), "name", true);
        Page<Group> groupPage = groupRepo.findValidByOrgIdAndIdAndName(tenantId,
            emptySafe(authGroupIds, null), emptySafe(groupIdsFilter, null), nameMatch, pageable);
        if (groupPage.isEmpty()) {
          return Page.empty();
        }

        // Query group authorized polices
        setGroupAuthPolicy(appId, tenantId, groupPage.getContent());
        return groupPage;
      }
    }.execute();
  }

  @Override
  public List<AuthPolicy> appAuthGlobal(Long appId) {
    return new BizTemplate<List<AuthPolicy>>() {
      final Long tenantId = getOptTenantId();

      @Override
      protected void checkParams() {
        // Check the application is opened
        appOpenQuery.checkAndFind(appId, tenantId, false);
      }

      @Override
      protected List<AuthPolicy> process() {
        return authPolicyOrgRepo.findPoliciesOfAuthAllUser(appId, tenantId);
      }
    }.execute();
  }

  @Override
  public Boolean appAuthOrgCheck(Long appId, AuthOrgType orgType, Long orgId) {
    return new BizTemplate<Boolean>() {
      final Long tenantId = getOptTenantId();
      Object orgDb;

      @Override
      protected void checkParams() {
        // Check the org existed
        assertTrue(!orgType.equals(AuthOrgType.TENANT)
            || orgId.equals(tenantId), "The optTenantId is missing or incorrect");
        userManager.checkOrgExists(orgType.toOrgTargetType(), orgId);
        // Check the application is opened
        orgDb = userManager.checkOrgAndFind(orgType.toOrgTargetType(), orgId);
      }

      @Override
      protected Boolean process() {
        closeMultiTenantCtrl();

        switch (orgType) {
          case TENANT:
            // When appOpenQuery.checkAndFind(appId, tenantId) passed
            return true;
          case USER:
            if (((User) orgDb).getSysAdmin()) {
              return true;
            }
            // Whether all tenant users are authorized
            boolean tenantAllUserAuth = authPolicyOrgRepo.existsAuthAllUser(appId, tenantId) > 0;
            if (tenantAllUserAuth) {
              return true;
            }
            // Whether all tenant users are authorized
            List<Long> orgIds = userManager.getOrgAndUserIds(orgId);
            orgIds.add(tenantId);
            // Whether all tenant users are authorized
            List<Long> authAppIds = authPolicyOrgRepo.findAuthAppIdsOfNonSysAdminUser(orgIds);
            return authAppIds.contains(appId);
          case DEPT:
            // Downward execute
          case GROUP:
            // Downward execute
          default:
            // Whether all tenant users are authorized
            // tenantAllUserAuth = authPolicyOrgRepo.existsAuthAllUser(appId, tenantId) > 0;
            // if (tenantAllUserAuth) {
            //  return true;
            // }
            return !authPolicyOrgRepo.findByAppIdAndOrgIdAndOrgType(appId, orgId, orgType)
                .isEmpty();
        }
      }
    }.execute();
  }

  @Override
  public List<App> orgAuthApp(AuthOrgType orgType, Long orgId, boolean joinPolicy) {
    return new BizTemplate<List<App>>(false) {
      final Long tenantId = getOptTenantId();
      Object orgDb;

      @Override
      protected void checkParams() {
        // Check the org existed
        assertTrue(!orgType.equals(AuthOrgType.TENANT)
            || orgId.equals(tenantId), "The optTenantId is missing or incorrect");
        orgDb = userManager.checkOrgAndFind(orgType.toOrgTargetType(), orgId);
      }

      @Override
      protected List<App> process() {
        closeMultiTenantCtrl();

        List<Long> authAppIds;
        switch (orgType) {
          case TENANT:
            // When appOpenQuery.checkAndFind(appId, tenantId) passed
            authAppIds = authPolicyOrgRepo.findAuthAppIdsByTenantId(tenantId);
            break;
          case USER:
            if (((User) orgDb).getSysAdmin()) {
              authAppIds = authPolicyOrgRepo.findAuthAppIdsByTenantId(tenantId);
              break;
            }
            List<Long> orgIds = userManager.getOrgAndUserIds(orgId);
            orgIds.add(tenantId);
            // Whether all tenant users are authorized
            authAppIds = authPolicyOrgRepo.findAuthAppIdsOfNonSysAdminUser(orgIds);
            break;
          case DEPT:
            // Downward execute
          case GROUP:
            // Downward execute
          default:
            // Whether all tenant org are authorized
            authAppIds = authPolicyOrgRepo.findAuthAppIdsByTenantIdAndOrgIdAndOrgType(tenantId,
                orgId, orgType.getValue());
        }

        if (isEmpty(authAppIds)) {
          return Collections.emptyList();
        }

        List<App> apps = appRepo.findAllById(authAppIds).stream().filter(
            x -> getClientId().equals(x.getClientId())).collect(Collectors.toList());
        if (joinPolicy) {
          setAppAuthPolicy(tenantId, orgType, orgId, orgDb, apps);
        }
        webTagQuery.setAppTags(apps);
        return apps;
      }
    }.execute();
  }

  @Override
  public Page<AuthPolicy> appAuthPolicy(Long appId, AuthOrgType orgType, Long orgId,
      GenericSpecification<AuthPolicy> spec, PageRequest pageable) {
    return new BizTemplate<Page<AuthPolicy>>() {
      final Long tenantId = getOptTenantId();
      Object orgDb;

      @Override
      protected void checkParams() {
        // Check the org existed
        assertTrue(!orgType.equals(AuthOrgType.TENANT)
            || orgId.equals(tenantId), "The optTenantId is missing or incorrect");
        orgDb = userManager.checkOrgAndFind(orgType.toOrgTargetType(), orgId);
        // Check the application is opened
        appOpenQuery.checkAndFind(appId, tenantId, false);
      }

      @Override
      protected Page<AuthPolicy> process() {
        closeMultiTenantCtrl();

        Set<String> policyIdsFilter = findAllIdInAndEqualValues(spec.getCriteria(), "id", true);

        // Query authorized policy
        Page<BigInteger> authPolicyIdsPage;
        switch (orgType) {
          case USER:
            // Query tenant user authorized organizations
            List<Long> allAuthOrgIds = authPolicyOrgRepo
                .findAllAuthOrgIdsByAuthPolices(appId, tenantId, null);
            if (isEmpty(allAuthOrgIds)) {
              return Page.empty();
            }
            authPolicyIdsPage = authPolicyOrgRepo
                .findAuthPolicyIdsByAppIdAndTenantIdAndOrgIdIn(appId, tenantId,
                    allAuthOrgIds, policyIdsFilter, pageable);

            break;
          case TENANT:
            // Downward execute
          case GROUP:
            // Downward execute
          case DEPT:
            // Downward execute
          default: {
            authPolicyIdsPage = authPolicyOrgRepo
                .findAuthPolicyIdsByAppIdAndTenantIdAndOrgTypeAndOrgId(appId, tenantId,
                    orgType.getValue(), orgId, policyIdsFilter, pageable);
          }
        }

        if (isNull(authPolicyIdsPage)) {
          return Page.empty();
        }
        return new PageImpl<>(authPolicyRepo.findAllById(
            authPolicyIdsPage.getContent().stream().map(BigInteger::longValue).collect(
                Collectors.toList())), pageable, authPolicyIdsPage.getTotalElements());
      }
    }.execute();
  }

  @Override
  public Boolean orgAuthAppCheck(AuthOrgType orgType, Long orgId, Long appId) {
    return new BizTemplate<Boolean>() {
      final Long tenantId = getOptTenantId();

      @Override
      protected void checkParams() {
        // Check the org existed
        assertTrue(!orgType.equals(AuthOrgType.TENANT)
            || orgId.equals(tenantId), "The optTenantId is missing or incorrect");
        userManager.checkOrgExists(orgType.toOrgTargetType(), orgId);
        // Check the application is opened
        appOpenQuery.checkAndFind(appId, tenantId, false);
      }

      @Override
      protected Boolean process() {
        return orgAuthApp(orgType, orgId, false).stream().anyMatch(x -> x.getId().equals(appId));
      }
    }.execute();
  }

  @Override
  public Page<Tenant> appUnauthTenant(Long appId, GenericSpecification<Tenant> spec,
      PageRequest pageable) {
    return new BizTemplate<Page<Tenant>>(false) {

      @Override
      protected void checkParams() {
        // NOOP:: appOpenQuery.checkAndFind(appId, getOptTenantId());
      }

      @Override
      protected Page<Tenant> process() {
        Set<String> allIds = findAllIdInAndEqualValues(spec.getCriteria(),
            "id", true);
        Page<BigInteger> tenantIdsPage = authPolicyOrgRepo
            .findUnAuthTenantIdsBysByAuthPolices(appId, allIds, pageable);
        if (!tenantIdsPage.hasContent()) {
          return Page.empty();
        }
        return new PageImpl<>(tenantRepo.findAllById(tenantIdsPage.getContent().stream()
            .map(BigInteger::longValue).collect(Collectors.toList())), pageable,
            tenantIdsPage.getTotalElements());
      }
    }.execute();
  }

  @Override
  public Page<User> appUnauthUser(Long appId, GenericSpecification<User> spec,
      PageRequest pageable) {
    return new BizTemplate<Page<User>>(false) {
      final Long tenantId = getOptTenantId();

      @Override
      protected void checkParams() {
        // Check the application is opened
        appOpenQuery.checkAndFind(appId, tenantId, false);
      }

      @Override
      protected Page<User> process() {
        // Whether all tenant users are authorized
        boolean tenantAllUserAuth = authPolicyOrgRepo.existsAuthAllUser(appId, tenantId) > 0;
        if (tenantAllUserAuth) {
          return Page.empty();
        }

        // Query tenant user authorized organizations
        Set<String> userIdsFilter = findAllIdInAndEqualValues(
            spec.getCriteria(), "id", true);
        String nameMatch = findMatchAndEqualValue(spec.getCriteria(),
            "fullName", true);
        List<Long> allAuthOrgIds = authPolicyOrgRepo.findAllAuthOrgIdsByAuthPolices(appId,
            tenantId, null);
        if (isEmpty(allAuthOrgIds)) {
          // Query unauthorized from all users when authorized users is empty
          return userRepo.findAllValidByIdAndName(tenantId, emptySafe(userIdsFilter, null),
              nameMatch, pageable);
        }

        // Query unauthorized users
        return userRepo.findValidByOrgIdNotAndIdAndName(tenantId, allAuthOrgIds, null,
            emptySafe(userIdsFilter, null), nameMatch, pageable);
      }
    }.execute();
  }

  @Override
  public Page<Dept> appUnauthDept(Long appId, GenericSpecification<Dept> spec,
      PageRequest pageable) {
    return new BizTemplate<Page<Dept>>(false) {
      final Long tenantId = getOptTenantId();

      @Override
      protected void checkParams() {
        // Check the application is opened
        appOpenQuery.checkAndFind(appId, tenantId, false);
      }

      @Override
      protected Page<Dept> process() {
        // Query authorized department
        List<Long> authDeptIds = authPolicyOrgRepo
            .findAllAuthDeptIdsByAuthPolices(appId, tenantId, null);

        // Query unauthorized department information
        Set<String> deptIdsFilter = findAllIdInAndEqualValues(
            spec.getCriteria(), "id", true);
        String nameMatch = findMatchAndEqualValue(spec.getCriteria(),
            "name", true);

        return deptRepo.findByOrgIdNotAndIdAndName(tenantId, emptySafe(authDeptIds, null),
            emptySafe(deptIdsFilter, null), nameMatch, pageable);
      }
    }.execute();
  }

  @Override
  public Page<Group> appUnauthGroup(Long appId, GenericSpecification<Group> spec,
      PageRequest pageable) {
    return new BizTemplate<Page<Group>>(false) {
      final Long tenantId = getOptTenantId();

      @Override
      protected void checkParams() {
        // Check the application is opened
        appOpenQuery.checkAndFind(appId, tenantId, false);
      }

      @Override
      protected Page<Group> process() {
        // Query authorized group
        List<Long> authGroupIds = authPolicyOrgRepo
            .findAllAuthGroupIdsByAuthPolices(appId, tenantId, null);
        // Query unauthorized group information
        Set<String> groupIdsFilter = findAllIdInAndEqualValues(spec.getCriteria(), "id", true);
        String nameMatch = findMatchAndEqualValue(spec.getCriteria(),
            "name", true);
        return groupRepo.findValidByOrgIdNotAndIdAndName(tenantId, emptySafe(authGroupIds, null),
            emptySafe(groupIdsFilter, null), nameMatch, pageable);
      }
    }.execute();
  }

  @Override
  public Page<AuthPolicy> appUnauthPolicy(Long appId, AuthOrgType orgType, Long orgId,
      GenericSpecification<AuthPolicy> spec, PageRequest pageable) {
    return new BizTemplate<Page<AuthPolicy>>() {
      final Long tenantId = getOptTenantId();
      Object orgDb;

      @Override
      protected void checkParams() {
        // Check the org existed
        assertTrue(!orgType.equals(AuthOrgType.TENANT)
            || orgId.equals(tenantId), "The optTenantId is missing or incorrect");
        orgDb = userManager.checkOrgAndFind(orgType.toOrgTargetType(), orgId);
        // Check the application is opened
        appOpenQuery.checkAndFind(appId, tenantId, false);
      }

      @Override
      protected Page<AuthPolicy> process() {
        closeMultiTenantCtrl();

        Set<?> policyIdsFilter = findAllIdInAndEqualValues(
            spec.getCriteria(), "id", true);

        // Query authorized policy
        Page<BigInteger> authPolicyIdsPage;
        switch (orgType) {
          case USER:
            // Query tenant user authorized organizations
            List<Long> allAuthOrgIds = authPolicyOrgRepo
                .findAllAuthOrgIdsByAuthPolices(appId, tenantId, null);
            if (isEmpty(allAuthOrgIds)) {
              return Page.empty();
            }
            authPolicyIdsPage = authPolicyOrgRepo
                .findAuthPolicyIdsByAppIdAndTenantIdAndOrgIdInNot(appId, tenantId,
                    allAuthOrgIds, policyIdsFilter, pageable);
            break;
          case TENANT:
            // Downward execute
          case GROUP:
            // Downward execute
          case DEPT:
            // Downward execute
          default: {
            authPolicyIdsPage = authPolicyOrgRepo
                .findAuthPolicyIdsByAppIdAndTenantIdAndOrgTypeAndOrgIdNot(appId, tenantId,
                    orgType.getValue(), orgId, policyIdsFilter, pageable);
          }
        }
        if (isNull(authPolicyIdsPage)) {
          return Page.empty();
        }

        return new PageImpl<>(authPolicyRepo.findAllById(authPolicyIdsPage.getContent()
            .stream().map(BigInteger::longValue).collect(Collectors.toList())), pageable,
            authPolicyIdsPage.getTotalElements());
      }
    }.execute();
  }

  private void setUserAuthPolicy(Long appId, Long tenantId, List<User> users) {
    for (User user : users) {
      addUserAuthPolicyToExtension(appId, tenantId, user, String.valueOf(user.getId()));
    }
  }

  private void setDeptAuthPolicy(Long appId, Long tenantId, List<Dept> depts) {
    for (Dept dept : depts) {
      addAuthPolicyToExtension(appId, tenantId, AuthOrgType.DEPT, dept.getId(),
          String.valueOf(dept.getId()));
    }
  }

  private void setGroupAuthPolicy(Long appId, Long tenantId, List<Group> groups) {
    for (Group group : groups) {
      addAuthPolicyToExtension(appId, tenantId, AuthOrgType.GROUP, group.getId(),
          String.valueOf(group.getId()));
    }
  }

  private void setAppAuthPolicy(Long tenantId, AuthOrgType orgType,
      Long orgId, Object orgDb, List<App> apps) {
    for (App app : apps) {
      Long appId = app.getId();
      switch (orgType) {
        case TENANT:
          break;
        case USER:
          addUserAuthPolicyToExtension(appId, tenantId, (User) orgDb, String.valueOf(appId));
          break;
        case DEPT:
          addAuthPolicyToExtension(appId, tenantId, AuthOrgType.DEPT, orgId,
              String.valueOf(appId));
          break;
        case GROUP:
          addAuthPolicyToExtension(appId, tenantId, AuthOrgType.GROUP, orgId,
              String.valueOf(appId));
          break;
        default:
          break;
      }
    }
  }

  private void addUserAuthPolicyToExtension(Long appId, Long tenantId, User user,
      String extendKey) {
    List<Long> allOrgIds = userManager.getOrgAndUserIds(user.getId());
    // Preset policies need to be included
    allOrgIds.add(tenantId);
    List<AuthOrgPolicyP> allAuthOrgPolicies = user.getSysAdmin() ?
        authPolicyOrgRepo.findAuthOfSysAdminUser(appId, tenantId, allOrgIds) :
        authPolicyOrgRepo.findAuthOfNonSysAdminUser(appId, tenantId, allOrgIds);
    if (isNotEmpty(allAuthOrgPolicies)) {
      Set<Long> allAuthPolicyIds = allAuthOrgPolicies.stream().map(AuthOrgPolicyP::getPolicyId)
          .collect(Collectors.toSet());
      Map<Long, List<AuthOrgPolicyP>> policyOrgMap = allAuthOrgPolicies.stream()
          .collect(Collectors.groupingBy(AuthOrgPolicyP::getPolicyId));
      List<AuthPolicy> authPolicies = authPolicyRepo.findByIdIn(allAuthPolicyIds);
      for (AuthPolicy authPolicy : authPolicies) {
        authPolicy.setOrgPolicies(policyOrgMap.get(authPolicy.getId()));
      }
      PrincipalContext.addExtension(extendKey, authPolicies);
    }
  }

  private void addAuthPolicyToExtension(Long appId, Long tenantId, AuthOrgType orgType,
      Long orgId, String extendKey) {
    List<Long> allAuthPolicyIds = authPolicyOrgRepo.findAuthPolicyIdsOfDept(appId, tenantId,
        orgType.getValue(), orgId);
    if (isNotEmpty(allAuthPolicyIds)) {
      PrincipalContext.addExtension(extendKey, authPolicyRepo.findByIdIn(allAuthPolicyIds));
    }
  }
}
