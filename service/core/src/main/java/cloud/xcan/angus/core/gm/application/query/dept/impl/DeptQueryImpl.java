package cloud.xcan.angus.core.gm.application.query.dept.impl;

import static cloud.xcan.angus.api.commonlink.UCConstant.TOP_TENANT_ADMIN;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceExisted;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.spec.experimental.Assert.assertNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static java.util.Collections.singleton;
import static java.util.Objects.isNull;

import cloud.xcan.angus.api.commonlink.dept.Dept;
import cloud.xcan.angus.api.commonlink.dept.DeptRepo;
import cloud.xcan.angus.api.commonlink.setting.quota.QuotaResource;
import cloud.xcan.angus.api.commonlink.tag.OrgTargetType;
import cloud.xcan.angus.api.commonlink.user.dept.DeptUserRepo;
import cloud.xcan.angus.api.manager.DeptManager;
import cloud.xcan.angus.api.manager.SettingTenantQuotaManager;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.dept.DeptQuery;
import cloud.xcan.angus.core.gm.application.query.tag.OrgTagTargetQuery;
import cloud.xcan.angus.core.gm.domain.dept.DeptListRepo;
import cloud.xcan.angus.core.gm.domain.dept.DeptSearchRepo;
import cloud.xcan.angus.core.gm.domain.dept.DeptSubCount;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.repository.summary.SummaryQueryRegister;
import cloud.xcan.angus.spec.utils.ObjectUtils;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * <p>
 * Implementation of department query operations.
 * </p>
 * <p>
 * Manages department retrieval, validation, hierarchical queries, and quota management. Provides
 * comprehensive department querying with full-text search and summary support.
 * </p>
 * <p>
 * Supports department detail retrieval, navigation queries, hierarchical management, quota
 * validation, and tag management for comprehensive department administration.
 * </p>
 */
@org.springframework.stereotype.Service
@Slf4j
@SummaryQueryRegister(name = "Dept", table = "dept", topAuthority = TOP_TENANT_ADMIN,
    groupByColumns = {"created_date", "level"})
public class DeptQueryImpl implements DeptQuery {

  @Resource
  private DeptRepo deptRepo;
  @Resource
  private DeptManager deptManager;
  @Resource
  private DeptListRepo deptListRepo;
  @Resource
  private DeptSearchRepo deptSearchRepo;
  @Resource
  private DeptUserRepo deptUserRepo;
  @Resource
  private OrgTagTargetQuery orgTagTargetQuery;
  @Resource
  private SettingTenantQuotaManager settingTenantQuotaManager;

  /**
   * <p>
   * Retrieves department navigation information with parent chain.
   * </p>
   * <p>
   * Fetches department details and builds complete parent hierarchy chain. Validates parent
   * relationship data integrity.
   * </p>
   */
  @Override
  public Dept navigation(Long id) {
    return new BizTemplate<Dept>(true, true) {
      Dept deptDb;

      @Override
      protected void checkParams() {
        deptDb = checkAndFind(id);
      }

      @Override
      protected Dept process() {
        if (deptDb.hasParent()) {
          assertNotEmpty(deptDb.getParentLikeId(), "Data exception, parentLikeId is empty!");
          Collection<Dept> parent = deptRepo.findByIdIn(
              Stream.of(deptDb.getParentLikeId().split("-"))
                  .map(Long::parseLong).collect(Collectors.toList()));
          deptDb.setParentChain(parent);
        }
        return deptDb;
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves detailed department information with associated tags.
   * </p>
   * <p>
   * Fetches complete department record with tag associations and sub-department status.
   * </p>
   */
  @Override
  public Dept detail(Long id) {
    return new BizTemplate<Dept>(true, true) {
      Dept deptDb;

      @Override
      protected void checkParams() {
        deptDb = checkAndFind(id);
      }

      @Override
      protected Dept process() {
        deptDb.setTags(orgTagTargetQuery.findAllByTarget(OrgTargetType.DEPT, id));
        setHasSubDept(Collections.singletonList(deptDb));
        return deptDb;
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves departments with optional filtering and search capabilities.
   * </p>
   * <p>
   * Supports full-text search and specification-based filtering. Sets sub-department status for
   * comprehensive department management.
   * </p>
   */
  @Override
  public Page<Dept> list(GenericSpecification<Dept> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<Dept>>(true, true) {

      @Override
      protected Page<Dept> process() {
        Page<Dept> page = fullTextSearch
            ? deptSearchRepo.find(spec.getCriteria(), pageable, Dept.class, match)
            : deptListRepo.find(spec.getCriteria(), pageable, Dept.class, null);
        setHasSubDept(page.getContent());
        return page;
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves department sub-count statistics.
   * </p>
   * <p>
   * Counts sub-departments and users under the specified department. Provides comprehensive
   * statistics for department hierarchy management.
   * </p>
   */
  @Override
  public DeptSubCount subCount(Long id) {
    return new BizTemplate<DeptSubCount>() {
      Dept deptDb;

      @Override
      protected void checkParams() {
        deptDb = checkAndFind(id);
      }

      @Override
      protected DeptSubCount process() {
        DeptSubCount count = new DeptSubCount();

        // Count sub departments
        String subParentLikeId = deptDb.getSubParentLikeId();
        List<Long> subIds = deptRepo.findIdByParentLikeId(subParentLikeId);
        count.setSubDeptNum(subIds.size());

        // Count sub users
        Set<Long> allDeptIds = new HashSet<>();
        allDeptIds.add(id);
        if (isNotEmpty(subIds)) {
          allDeptIds.addAll(subIds);
        }
        long userNum = deptUserRepo.countByTenantIdAndDeptIdIn(getOptTenantId(), allDeptIds);
        count.setSunUserNum(userNum);
        return count;
      }
    }.execute();
  }

  /**
   * <p>
   * Sets sub-department status for department list.
   * </p>
   * <p>
   * Identifies departments that have sub-departments and sets hasSubDept flag.
   * </p>
   */
  @Override
  public void setHasSubDept(List<Dept> deptDb) {
    if (isEmpty(deptDb)) {
      return;
    }
    Set<Long> hasSubDeptIds = deptRepo.findPidBySubPid(
        deptDb.stream().map(Dept::getId).collect(Collectors.toSet()));
    for (Dept dept : deptDb) {
      dept.setHasSubDept(hasSubDeptIds.contains(dept.getId()));
    }
  }

  /**
   * <p>
   * Retrieves departments by IDs without validation.
   * </p>
   * <p>
   * Returns departments for the specified IDs without validation checks.
   * </p>
   */
  @Override
  public List<Dept> findByIdIn(Collection<Long> ids) {
    return deptRepo.findByIdIn(ids);
  }

  /**
   * <p>
   * Validates and retrieves department by ID.
   * </p>
   * <p>
   * Verifies department exists and returns department information. Throws appropriate exception if
   * department does not exist.
   * </p>
   */
  @Override
  public Dept checkAndFind(Long id) {
    return deptManager.checkAndFind(id);
  }

  /**
   * <p>
   * Validates and retrieves multiple departments by IDs.
   * </p>
   * <p>
   * Verifies all departments exist and returns department information. Throws appropriate
   * exceptions for missing departments.
   * </p>
   */
  @Override
  public List<Dept> checkAndFind(Collection<Long> ids) {
    return deptManager.checkAndFind(ids);
  }

  /**
   * <p>
   * Validates and retrieves parent departments for department list.
   * </p>
   * <p>
   * Verifies parent departments exist and returns parent information.
   * </p>
   */
  @Override
  public List<Dept> checkAndGetParent(Long tenantId, List<Dept> dept) {
    return deptManager.checkAndGetParent(tenantId, dept);
  }

  /**
   * <p>
   * Validates department hierarchy for nested duplicates.
   * </p>
   * <p>
   * Checks for circular references and nested duplicate relationships. Throws appropriate exception
   * if nested duplicates are found.
   * </p>
   */
  @Override
  public void checkNestedDuplicates(List<Dept> deptDb) {
    Set<String> parentIds = deptDb.stream().filter(Dept::hasParent)
        .map(Dept::getParentLikeId).flatMap(x -> Stream.of(x.split("-")))
        .collect(Collectors.toSet());
    if (isNotEmpty(parentIds)) {
      Dept hasDuplicate = deptDb.stream().filter(m
          -> parentIds.contains(String.valueOf(m.getId()))).findFirst().orElse(null);
      assertTrue(isNull(hasDuplicate), String.format("Department %s is nested duplicates",
          isNull(hasDuplicate) ? "" : hasDuplicate.getId()));
    }
  }

  /**
   * <p>
   * Validates department code uniqueness for new departments.
   * </p>
   * <p>
   * Checks if department codes already exist within the tenant. Throws ResourceExisted exception if
   * codes are not unique.
   * </p>
   */
  @Override
  public void checkAddDeptCode(Long tenantId, List<Dept> dept) {
    List<Dept> deptDbs = deptRepo.findAllByTenantIdAndCodeIn(tenantId, dept.stream()
        .filter(ObjectUtils::isNotEmpty).map(Dept::getCode).collect(Collectors.toSet()));
    assertResourceExisted(isEmpty(deptDbs), isNotEmpty(deptDbs)
        ? deptDbs.get(0).getCode() : null, "Department");
  }

  /**
   * <p>
   * Validates department code uniqueness for updated departments.
   * </p>
   * <p>
   * Checks if department codes conflict with existing departments. Allows same code for the same
   * department during updates.
   * </p>
   */
  @Override
  public void checkUpdateDeptCode(Long tenantId, List<Dept> dept) {
    if (isEmpty(dept)) {
      return;
    }
    List<Dept> deptDbs = deptRepo.findAllByTenantIdAndCodeIn(tenantId, dept.stream()
        .filter(ObjectUtils::isNotEmpty).map(Dept::getCode).collect(Collectors.toSet()));
    if (isNotEmpty(deptDbs)) {
      Map<String, List<Dept>> codeDeptsMap = deptDbs.stream()
          .collect(Collectors.groupingBy(Dept::getCode));
      for (Dept dept0 : dept) {
        if (isNotEmpty(dept0.getCode())) {
          List<Dept> codeDept = codeDeptsMap.get(dept0.getCode());
          assertResourceExisted(isEmpty(codeDept)
                  || (codeDept.size() == 1 && dept0.getId().equals(codeDept.get(0).getId())),
              deptDbs.get(0).getCode(), "Department");
        }
      }
    }
  }

  /**
   * <p>
   * Validates department quota for tenant.
   * </p>
   * <p>
   * Checks if adding departments would exceed tenant quota limits. Throws appropriate exception if
   * quota would be exceeded.
   * </p>
   */
  @Override
  public void checkDeptQuota(Long tenantId, long incr) {
    if (incr > 0) {
      long num = deptRepo.countByTenantId(tenantId);
      settingTenantQuotaManager.checkTenantQuota(QuotaResource.Dept, null, num + incr);
    }
  }

  /**
   * <p>
   * Validates department level quota for tenant.
   * </p>
   * <p>
   * Checks if department hierarchy levels would exceed tenant quota limits. Supports both add and
   * update operations with different validation logic.
   * </p>
   */
  @Override
  public void checkDeptLevelQuota(Long optTenantId, List<Dept> dept, Map<Long, Dept> deptsDbMap,
      Map<Long, Dept> parentDeptsDbMap, boolean add) {
    if (isEmpty(dept) || isEmpty(parentDeptsDbMap)) {
      return;
    }

    Dept maxLevelParent = parentDeptsDbMap.values().stream()
        .max(Comparator.comparing(Dept::getLevel, Comparator.nullsFirst(Integer::compareTo)))
        .orElse(null);
    if (isNull(maxLevelParent)) {
      return;
    }

    if (add) {
      settingTenantQuotaManager.checkTenantQuota(QuotaResource.DeptLevel,
          dept.stream().filter(d -> parentDeptsDbMap.containsKey(d.getPid()))
              .map(Dept::getName).collect(Collectors.toSet()), maxLevelParent.getLevel() + 1L);
      return;
    }

    // Verify the level of the moved sub dept
    for (Dept dept0 : dept) {
      if (dept0.hasParent()) {
        List<Dept> subDept = deptRepo.findSubDeptsByParentLikeId(optTenantId,
            dept0.getSubParentLikeId());
        int newMaxSubDeptLevel;
        Dept maxLevelSubDept;
        if (isEmpty(subDept)) {
          maxLevelSubDept = dept0;
          newMaxSubDeptLevel = parentDeptsDbMap.get(dept0.getPid()).getLevel() + 1;
        } else {
          maxLevelSubDept = subDept.stream()
              .max(Comparator.comparing(Dept::getLevel, Comparator.nullsFirst(Integer::compareTo)))
              .orElse(null);
          if (isNull(maxLevelSubDept)) {
            continue;
          }
          newMaxSubDeptLevel = maxLevelSubDept.getLevel() + 1
              - deptsDbMap.get(dept0.getId()).getLevel()
              + parentDeptsDbMap.get(dept0.getPid()).getLevel();
        }
        settingTenantQuotaManager.checkTenantQuota(QuotaResource.DeptLevel,
            singleton(maxLevelSubDept.getId()), (long) newMaxSubDeptLevel);
      }
    }
  }

  /**
   * <p>
   * Validates tag quota for departments.
   * </p>
   * <p>
   * Checks if department tag associations would exceed tenant quota limits. Throws appropriate
   * exception if quota would be exceeded.
   * </p>
   */
  @Override
  public void checkTagQuota(Long optTenantId, List<Dept> dept) {
    // Verify tags quota
    if (isEmpty(dept)) {
      return;
    }
    for (Dept dept0 : dept) {
      if (isNotEmpty(dept0.getTagIds())) {
        orgTagTargetQuery.checkTargetTagQuota(optTenantId, dept0.getTagIds().size(), dept0.getId());
      }
    }
  }
}
