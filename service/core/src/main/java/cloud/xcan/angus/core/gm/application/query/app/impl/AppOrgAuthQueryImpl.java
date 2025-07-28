package cloud.xcan.angus.core.gm.application.query.app.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.jpa.criteria.CriteriaUtils.findAllIdInAndEqualValues;
import static cloud.xcan.angus.core.jpa.criteria.CriteriaUtils.getNameFilterValue;
import static cloud.xcan.angus.core.utils.PaginationUtils.paginate;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getClientId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;
import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;

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
import cloud.xcan.angus.api.commonlink.policy.AuthOrgPolicyP;
import cloud.xcan.angus.core.gm.domain.policy.org.AuthPolicyIdP;
import cloud.xcan.angus.core.gm.domain.policy.org.AuthPolicyOrg;
import cloud.xcan.angus.core.gm.domain.policy.org.AuthPolicyOrgP;
import cloud.xcan.angus.core.gm.domain.policy.org.AuthPolicyOrgRepo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.entity.projection.TenantId;
import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

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
        Set<String> allIds = findAllIdInAndEqualValues(spec.getCriteria(), "id", true);

        Set<SearchCriteria> criteria = SearchCriteria.criteria(
            SearchCriteria.equal("appId", appId), SearchCriteria.equal("openAuth", true),
            SearchCriteria.in("tenantId", allIds));
        Page<TenantId> tenantIdsPage = authPolicyOrgRepo.findProjectionByFilters(
            AuthPolicyOrg.class, TenantId.class, new GenericSpecification<>(criteria, true),
            pageable);

        if (!tenantIdsPage.hasContent()) {
          return Page.empty();
        }

        List<Tenant> pageTenants = tenantRepo.findAllById(tenantIdsPage.getContent()
            .stream().map(TenantId::getTenantId).collect(Collectors.toList()));
        return new PageImpl<>(pageTenants, pageable, tenantIdsPage.getTotalElements());
      }
    }.execute();
  }

  @Override
  public Page<User> appAuthUser(Long appId, GenericSpecification<User> spec, PageRequest pageable) {
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
        boolean allTenantUserAuth = authPolicyOrgRepo.existsAuthAllUser(appId, tenantId) > 0;
        PrincipalContext.addExtension("globalAuth", allTenantUserAuth);

        // Query full tenant users when allTenantUserAuth = false
        List<Long> authOrgIds = null;
        if (!allTenantUserAuth) {
          // Query tenant user authorized organizations
          Set<String> idsFilter = findAllIdInAndEqualValues(spec.getCriteria(), "id", true);

          authOrgIds = getAuthOrgIds(tenantId, appId, AuthOrgType.USER, idsFilter);

          if (isEmpty(authOrgIds)) {
            return Page.empty();
          }
        }

        // Query user page
        spec.getCriteria().addAll(SearchCriteria.criteria(
            SearchCriteria.equal("tenantId", tenantId), SearchCriteria.equal("enabled", true),
            SearchCriteria.equal("deleted", false)));
        if (!allTenantUserAuth) {
          spec.getCriteria().add(SearchCriteria.in("id", authOrgIds));
        }
        Page<User> userPage = userRepo.findAll(spec, pageable);

        // Query user authorized polices
        setUserAuthPolicy(appId, tenantId, userPage.getContent());
        return userPage;
      }
    }.execute();
  }

  @Override
  public Page<Dept> appAuthDept(Long appId, GenericSpecification<Dept> spec, PageRequest pageable) {
    return new BizTemplate<Page<Dept>>(false) {
      final Long tenantId = getOptTenantId();

      @Override
      protected void checkParams() {
        // Check the application is opened
        appOpenQuery.checkAndFind(appId, tenantId, true);
      }

      @Override
      protected Page<Dept> process() {
        Set<String> idsFilter = findAllIdInAndEqualValues(spec.getCriteria(), "id", true);
        List<Long> authOrgIds = getAuthOrgIds(tenantId, appId, AuthOrgType.DEPT, idsFilter);
        if (isEmpty(authOrgIds)) {
          return Page.empty();
        }

        // Query department page
        spec.getCriteria().addAll(SearchCriteria.criteria(
            SearchCriteria.equal("tenantId", tenantId), SearchCriteria.in("id", authOrgIds)));
        Page<Dept> deptPage = deptRepo.findAll(spec, pageable);

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
        Set<String> idsFilter = findAllIdInAndEqualValues(spec.getCriteria(), "id", true);
        List<Long> authOrgIds = getAuthOrgIds(tenantId, appId, AuthOrgType.GROUP, idsFilter);
        if (isEmpty(authOrgIds)) {
          return Page.empty();
        }

        // Query  information
        spec.getCriteria().addAll(SearchCriteria.criteria(
            SearchCriteria.equal("tenantId", tenantId), SearchCriteria.in("id", authOrgIds),
            SearchCriteria.equal("enabled", true)));
        Page<Group> groupPage = groupRepo.findAll(spec, pageable);

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
        return authPolicyRepo.findPoliciesOfAuthAllUser(appId, tenantId);
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
            boolean allTenantUserAuth = authPolicyOrgRepo.existsAuthAllUser(appId, tenantId) > 0;
            if (allTenantUserAuth) {
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
        assertTrue(!orgType.equals(AuthOrgType.TENANT) || orgId.equals(tenantId),
            "The optTenantId is missing or incorrect");
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
            authAppIds = authPolicyOrgRepo.findAuthAppIdsByTenantIdAndOrgIdAndOrgType(
                tenantId, orgId, orgType.getValue());
        }

        if (isEmpty(authAppIds)) {
          return emptyList();
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
  public Page<AuthPolicy> orgAuthPolicy(Long appId, AuthOrgType orgType, Long orgId,
      GenericSpecification<AuthPolicy> spec, PageRequest pageable) {
    return new BizTemplate<Page<AuthPolicy>>() {
      final Long tenantId = getOptTenantId();
      Object orgDb;

      @Override
      protected void checkParams() {
        // Check the org existed
        assertTrue(!orgType.equals(AuthOrgType.TENANT) || orgId.equals(tenantId),
            "The optTenantId is missing or incorrect");
        orgDb = userManager.checkOrgAndFind(orgType.toOrgTargetType(), orgId);
        // Check the application is opened
        appOpenQuery.checkAndFind(appId, tenantId, false);
      }

      @Override
      protected Page<AuthPolicy> process() {
        closeMultiTenantCtrl();

        // Query authorized policy
        Set<String> idsFilter = findAllIdInAndEqualValues(spec.getCriteria(), "id", true);
        Page<AuthPolicyIdP> policyIdPage = getAuthPolicyIds(tenantId, appId, orgType,
            Set.of(orgId.toString()), idsFilter, pageable);

        if (isNull(policyIdPage)) {
          return Page.empty();
        }

        List<AuthPolicy> pagePolicies = authPolicyRepo.findAllById(
            policyIdPage.getContent().stream().map(AuthPolicyIdP::getPolicyId)
                .collect(Collectors.toList()));
        return new PageImpl<>(pagePolicies, pageable, policyIdPage.getTotalElements());
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
        Set<String> idsFilter = findAllIdInAndEqualValues(spec.getCriteria(), "id", true);
        List<Long> authIds = authPolicyOrgRepo.findOpenAuthTenantIdByAppId(appId);

        spec.getCriteria().addAll(SearchCriteria.criteria(
            SearchCriteria.in("tenantId", idsFilter),
            SearchCriteria.notIn("tenantId", authIds)));
        return tenantRepo.findAll(spec, pageable);
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
        boolean allTenantUserAuth = authPolicyOrgRepo.existsAuthAllUser(appId, tenantId) > 0;
        if (allTenantUserAuth) {
          return Page.empty();
        }

        // Query tenant user authorized organizations
        Set<String> idsFilter = findAllIdInAndEqualValues(spec.getCriteria(), "id", true);
        List<Long> authOrgIds = getAuthOrgIds(tenantId, appId, null, null);
        Set<Long> authUserIds =
            isNotEmpty(authOrgIds) ? userManager.findUserIdsByOrgIds(authOrgIds) : null;

        // Query user page
        spec.getCriteria().addAll(SearchCriteria.criteria(
            SearchCriteria.equal("tenantId", tenantId), SearchCriteria.equal("enabled", true),
            SearchCriteria.equal("deleted", false), SearchCriteria.in("id", idsFilter),
            SearchCriteria.notIn("id", authUserIds)));
        return userRepo.findAll(spec, pageable);
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
        Set<String> idsFilter = findAllIdInAndEqualValues(spec.getCriteria(), "id", true);
        List<Long> authOrgIds = getAuthOrgIds(tenantId, appId, AuthOrgType.DEPT, null);

        // Query department page
        spec.getCriteria().addAll(SearchCriteria.criteria(
            SearchCriteria.equal("tenantId", tenantId), SearchCriteria.in("id", idsFilter),
            SearchCriteria.notIn("id", authOrgIds)));
        return deptRepo.findAll(spec, pageable);
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
        Set<String> idsFilter = findAllIdInAndEqualValues(spec.getCriteria(), "id", true);
        List<Long> authOrgIds = getAuthOrgIds(tenantId, appId, AuthOrgType.GROUP, null);

        // Query group page
        spec.getCriteria().addAll(SearchCriteria.criteria(
            SearchCriteria.equal("tenantId", tenantId), SearchCriteria.equal("enabled", true),
            SearchCriteria.in("id", idsFilter), SearchCriteria.notIn("id", authOrgIds)));
        return groupRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public Page<AuthPolicy> orgUnauthPolicy(Long appId, AuthOrgType orgType, Long orgId,
      GenericSpecification<AuthPolicy> spec, PageRequest pageable) {
    return new BizTemplate<Page<AuthPolicy>>() {
      final Long tenantId = getOptTenantId();
      Object orgDb;

      @Override
      protected void checkParams() {
        // Check the org existed
        assertTrue(!orgType.equals(AuthOrgType.TENANT) || orgId.equals(tenantId),
            "The optTenantId is missing or incorrect");
        orgDb = userManager.checkOrgAndFind(orgType.toOrgTargetType(), orgId);
        // Check the application is opened
        appOpenQuery.checkAndFind(appId, tenantId, false);
      }

      @Override
      protected Page<AuthPolicy> process() {
        closeMultiTenantCtrl();

        // Query authorized policy
        List<Long> authPolicyIds = getAuthPolicyIds(tenantId, appId, orgType,
            Set.of(orgId.toString()), null);

        Set<String> idsFilter = findAllIdInAndEqualValues(spec.getCriteria(), "id", true);
        String nameFilter = getNameFilterValue(spec.getCriteria());

        List<AuthPolicy> policies = authPolicyRepo.findAllPolicyByTenantId(tenantId).stream()
            .filter(x -> x.getAppId().equals(appId)).filter(x -> !authPolicyIds.contains(x.getId()))
            .filter(x -> idsFilter.isEmpty() || idsFilter.contains(x.getId().toString()))
            .filter(x -> nameFilter.isEmpty() || x.getName().contains(nameFilter))
            .toList();

        return paginate(policies, pageable);
      }
    }.execute();
  }

  @Override
  public List<Long> getAuthOrgIds(Long tenantId, Long appId, @Nullable AuthOrgType orgType,
      @Nullable Set<String> idsFilter) {
    Set<SearchCriteria> criteria = getOrgSearchCriteria(tenantId, appId, orgType, idsFilter);
    return authPolicyOrgRepo.findProjectionByFilters(AuthPolicyOrg.class,
            AuthPolicyOrgP.class, new GenericSpecification<>(criteria))
        .stream().map(AuthPolicyOrgP::getOrgId).distinct().toList();
  }

  @Override
  public List<Long> getAuthPolicyIds(Long tenantId, Long appId, @Nullable AuthOrgType orgType,
      @Nullable Set<String> orgIdsFilter, @Nullable Set<String> policyIdsFilter) {
    Set<SearchCriteria> criteria = getOrgSearchCriteria(tenantId, appId, orgType, orgIdsFilter);
    if (isNotEmpty(policyIdsFilter)) {
      criteria.add(SearchCriteria.in("policyId", policyIdsFilter));
    }
    return authPolicyOrgRepo.findProjectionByFilters(AuthPolicyOrg.class,
            AuthPolicyOrgP.class, new GenericSpecification<>(criteria))
        .stream().map(AuthPolicyOrgP::getPolicyId).distinct().toList();
  }

  @Override
  public Page<AuthPolicyIdP> getAuthPolicyIds(Long tenantId, Long appId,
      @Nullable AuthOrgType orgType, @Nullable Set<String> orgIdsFilter,
      @Nullable Set<String> policyIdsFilter, Pageable pageable) {
    Set<SearchCriteria> criteria = getOrgSearchCriteria(tenantId, appId, orgType, orgIdsFilter);
    if (isNotEmpty(policyIdsFilter)) {
      criteria.add(SearchCriteria.in("policyId", policyIdsFilter));
    }
    return authPolicyOrgRepo.findProjectionByFilters(AuthPolicyOrg.class,
        AuthPolicyIdP.class, new GenericSpecification<>(criteria), pageable);
  }

  private static @NotNull Set<SearchCriteria> getOrgSearchCriteria(Long tenantId, Long appId,
      AuthOrgType orgType, Set<String> orgIdsFilter) {
    Set<SearchCriteria> criteria = SearchCriteria.criteria(
        SearchCriteria.equal("tenantId", tenantId), SearchCriteria.equal("appId", appId));
    if (nonNull(orgType)) {
      criteria.add(SearchCriteria.equal("orgType", orgType.getValue()));
    }
    if (isNotEmpty(orgIdsFilter)) {
      criteria.add(SearchCriteria.in("orgId", orgIdsFilter));
    }
    return criteria;
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
