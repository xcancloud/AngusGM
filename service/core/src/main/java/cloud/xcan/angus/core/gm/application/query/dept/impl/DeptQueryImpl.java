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
import cloud.xcan.angus.core.biz.Biz;
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


@Biz
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

  @Override
  public Page<Dept> list(GenericSpecification<Dept> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<Dept>>(true, true) {

      @Override
      protected Page<Dept> process() {
        Page<Dept> page = fullTextSearch
            ? deptSearchRepo.find(spec.getCriteria(), pageable,  Dept.class, match)
            : deptListRepo.find(spec.getCriteria(), pageable, Dept.class, null);
        setHasSubDept(page.getContent());
        return page;
      }
    }.execute();
  }

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

  @Override
  public List<Dept> findByIdIn(Collection<Long> ids) {
    return deptRepo.findByIdIn(ids);
  }

  @Override
  public Dept checkAndFind(Long id) {
    return deptManager.checkAndFind(id);
  }

  @Override
  public List<Dept> checkAndFind(Collection<Long> ids) {
    return deptManager.checkAndFind(ids);
  }

  @Override
  public List<Dept> checkAndGetParent(Long tenantId, List<Dept> dept) {
    return deptManager.checkAndGetParent(tenantId, dept);
  }

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

  @Override
  public void checkAddDeptCode(Long tenantId, List<Dept> dept) {
    List<Dept> deptDbs = deptRepo.findAllByTenantIdAndCodeIn(tenantId, dept.stream()
        .filter(ObjectUtils::isNotEmpty).map(Dept::getCode).collect(Collectors.toSet()));
    assertResourceExisted(isEmpty(deptDbs), isNotEmpty(deptDbs)
        ? deptDbs.get(0).getCode() : null, "Department");
  }

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

  @Override
  public void checkDeptQuota(Long tenantId, long incr) {
    if (incr > 0) {
      long num = deptRepo.countByTenantId(tenantId);
      settingTenantQuotaManager.checkTenantQuota(QuotaResource.Dept, null, num + incr);
    }
  }

  @Override
  public void checkDeptLevelQuota(Long optTenantId, List<Dept> dept, Map<Long, Dept> deptsDbMap,
      Map<Long, Dept> parentDeptsDbMap, boolean add) {
    if (isEmpty(dept) || isEmpty(parentDeptsDbMap)) {
      return;
    }

    Dept maxLevelParent = parentDeptsDbMap.values().stream()
        .max(Comparator.comparing(Dept::getLevel)).orElse(null);
    if (isNull(maxLevelParent)) {
      return;
    }

    if (add) {
      settingTenantQuotaManager.checkTenantQuota(QuotaResource.DeptLevel,
          dept.stream().filter(d -> parentDeptsDbMap.containsKey(d.getPid()))
              .map(Dept::getName).collect(Collectors.toSet()), maxLevelParent.getLevel() + 1L);
      return;
    }

    // Check the level of the moved sub dept
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
          maxLevelSubDept = subDept.stream().max(Comparator.comparing(Dept::getLevel)).orElse(null);
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

  @Override
  public void checkTagQuota(Long optTenantId, List<Dept> dept) {
    // Check tags quota
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
