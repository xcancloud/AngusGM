package cloud.xcan.angus.core.gm.application.cmd.dept.impl;

import static cloud.xcan.angus.core.utils.CoreUtils.copyPropertiesIgnoreNull;
import static cloud.xcan.angus.core.utils.CoreUtils.copyPropertiesIgnoreTenantAuditing;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.spec.experimental.BizConstant.DEFAULT_ROOT_PID;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.dept.Dept;
import cloud.xcan.angus.api.commonlink.dept.DeptRepo;
import cloud.xcan.angus.api.commonlink.tag.OrgTagTarget;
import cloud.xcan.angus.api.commonlink.tag.OrgTargetType;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.dept.DeptCmd;
import cloud.xcan.angus.core.gm.application.cmd.dept.DeptUserCmd;
import cloud.xcan.angus.core.gm.application.cmd.policy.AuthPolicyDeptCmd;
import cloud.xcan.angus.core.gm.application.cmd.tag.OrgTagTargetCmd;
import cloud.xcan.angus.core.gm.application.cmd.user.UserCmd;
import cloud.xcan.angus.core.gm.application.converter.DeptTagConverter;
import cloud.xcan.angus.core.gm.application.query.dept.DeptQuery;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Biz
@Slf4j
public class DeptCmdImpl extends CommCmd<Dept, Long> implements DeptCmd {

  @Resource
  private DeptRepo deptRepo;

  @Resource
  private DeptQuery deptQuery;

  @Resource
  private UserCmd userCmd;

  @Resource
  private OrgTagTargetCmd orgTagTargetCmd;

  @Resource
  private DeptUserCmd userDeptCmd;

  @Resource
  private AuthPolicyDeptCmd authPolicyDeptCmd;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> add(List<Dept> departments) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      final Long optTenantId = getOptTenantId();
      List<Dept> parentDepartmentsDb;
      Map<Long, Dept> parentDepartmentsDbMap;

      @Override
      protected void checkParams() {
        // Check the code is not repeated
        deptQuery.checkAddDeptCode(optTenantId, departments);
        // Check the pid exists
        parentDepartmentsDb = deptQuery.checkAndGetParent(optTenantId, departments);
        // Check the department and level quota
        deptQuery.checkDeptQuota(optTenantId, departments.size());
        if (isNotEmpty(parentDepartmentsDb)) {
          parentDepartmentsDbMap = parentDepartmentsDb.stream()
              .collect(Collectors.toMap(Dept::getId, x -> x));
        }
        deptQuery.checkDeptLevelQuota(optTenantId, departments, null, parentDepartmentsDbMap, true);
        // Check tag quotas
        deptQuery.checkTagQuota(optTenantId, departments);
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        // Save departments
        List<Dept> departmentsDb = departments.stream().peek(
                dept -> dept.setLevel(calcLevel(dept, parentDepartmentsDbMap))
                    .setParentLikeId(assembleParentLikeId(dept.getPid(), parentDepartmentsDbMap)))
            .collect(Collectors.toList());
        List<IdKey<Long, Object>> idKeys = batchInsert(departmentsDb);

        // Save department tags
        List<OrgTagTarget> allTags = buildOrgTagTargets(departmentsDb);
        if (isNotEmpty(allTags)) {
          orgTagTargetCmd.add(allTags);
        }
        return idKeys;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void update(List<Dept> departments) {
    new BizTemplate<Void>() {
      final Long optTenantId = getOptTenantId();
      List<Dept> departmentsDb;
      Map<Long, Dept> departmentsDbMap;
      List<Dept> parentDepartmentsDb;
      Map<Long, Dept> parentDepartmentsDbMap;
      Set<Long> updateDeptIds;

      @Override
      protected void checkParams() {
        // Check the code not repeated
        deptQuery.checkUpdateDeptCode(optTenantId, departments);
        // Check the departments exists
        updateDeptIds = departments.stream().map(Dept::getId).collect(Collectors.toSet());
        departmentsDb = deptQuery.checkAndFind(updateDeptIds);
        // Check the nested duplicates
        deptQuery.checkNestedDuplicates(departmentsDb);
        // Check the pid exists
        parentDepartmentsDb = deptQuery.checkAndGetParent(optTenantId, departments);
        // Check the department and level quota
        // deptQuery.checkDeptQuota(tenantId, departments.size());
        if (isNotEmpty(parentDepartmentsDb)) {
          parentDepartmentsDbMap = parentDepartmentsDb.stream()
              .collect(Collectors.toMap(Dept::getId, x -> x));
        }
        departmentsDbMap = departmentsDb.stream().collect(Collectors.toMap(Dept::getId, x -> x));
        deptQuery.checkDeptLevelQuota(optTenantId, departments, departmentsDbMap,
            parentDepartmentsDbMap, false);
        // Check the tag quotas
        deptQuery.checkTagQuota(optTenantId, departments);
      }

      @Override
      protected Void process() {
        // Calculate and update level, parentLikeId.
        setAndUpdateLevelAndParentLikeId(departments, departmentsDbMap, parentDepartmentsDbMap);

        // Update departments.
        departmentsDb = departments.stream().map(department ->
                copyPropertiesIgnoreNull(department, departmentsDbMap.get(department.getId()))
                    .setParentLikeId(department.getParentLikeId())/* Set null when moved to root */)
            .collect(Collectors.toList());
        deptRepo.saveAll(departmentsDb);

        // Replace tags.
        List<OrgTagTarget> allTags = buildOrgTagTargets(departments);
        if (isNotEmpty(allTags)) {
          orgTagTargetCmd.deleteAllByTarget(OrgTargetType.DEPT, updateDeptIds);
          orgTagTargetCmd.add(allTags);
        }
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> replace(List<Dept> departments) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      final Long optTenantId = getOptTenantId();
      List<Dept> replaceDepartments;
      List<Dept> replaceDepartmentsDb;
      Map<Long, Dept> replaceDepartmentsDbMap;
      List<Long> replaceDepartmentIds;
      List<Dept> parentDepartmentsDb = null;
      Map<Long, Dept> parentDeptsDbMap = null;

      @Override
      protected void checkParams() {
        replaceDepartments = departments.stream().filter(dept -> nonNull(dept.getId()))
            .collect(Collectors.toList());
        if (isNotEmpty(replaceDepartments)) {
          // Check the departments exists
          replaceDepartmentsDb = deptQuery.checkAndFind(replaceDepartmentIds);
          replaceDepartmentIds = replaceDepartments.stream().map(Dept::getId)
              .collect(Collectors.toList());
          // Check the code is not repeated
          deptQuery.checkUpdateDeptCode(optTenantId, replaceDepartments);
          // Check nested duplicates
          deptQuery.checkNestedDuplicates(replaceDepartmentsDb);
          // Check pid exists
          parentDepartmentsDb = deptQuery.checkAndGetParent(optTenantId, departments);
          // Check dept and level quota
          // deptQuery.checkDeptQuota(tenantId, departments.size());
          if (isNotEmpty(parentDepartmentsDb)) {
            parentDeptsDbMap = parentDepartmentsDb.stream()
                .collect(Collectors.toMap(Dept::getId, x -> x));
          }
          if (isNotEmpty(replaceDepartments)) {
            replaceDepartmentsDbMap = replaceDepartmentsDb.stream()
                .collect(Collectors.toMap(Dept::getId, x -> x));
          }
          deptQuery.checkDeptLevelQuota(optTenantId, replaceDepartments, replaceDepartmentsDbMap,
              parentDeptsDbMap, false);
          // Check tag quotas
          deptQuery.checkTagQuota(optTenantId, replaceDepartments);
        }
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        List<IdKey<Long, Object>> idKeys = new ArrayList<>();

        List<Dept> addDepartments = departments.stream().filter(dept -> isNull(dept.getId()))
            .collect(Collectors.toList());
        if (isNotEmpty(addDepartments)) {
          idKeys.addAll(add(addDepartments));
        }

        if (isNotEmpty(replaceDepartments)) {
          // Calc and update level, parentLikeId
          setAndUpdateLevelAndParentLikeId(replaceDepartments, replaceDepartmentsDbMap,
              parentDeptsDbMap);

          // Do not replace tenant auditing.
          replaceDepartmentsDb = replaceDepartments.stream()
              .map(dept -> copyPropertiesIgnoreTenantAuditing(dept,
                  replaceDepartmentsDbMap.get(dept.getId())))
              .collect(Collectors.toList());
          deptRepo.saveAll(replaceDepartmentsDb);

          // Replace department tags.
          List<OrgTagTarget> allTags = buildOrgTagTargets(replaceDepartments);
          if (isNotEmpty(allTags)) {
            orgTagTargetCmd.deleteAllByTarget(OrgTargetType.DEPT, replaceDepartmentIds);
            orgTagTargetCmd.add(allTags);
          }

          idKeys.addAll(replaceDepartmentsDb.stream()
              .map(x -> IdKey.of(x.getId(), x.getName())).toList());
        }
        return idKeys;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(Set<Long> ids) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        List<Dept> departmentsDb = deptRepo.findAllById(ids);
        if (isEmpty(departmentsDb)) {
          return null;
        }

        // Delete current and sub departments
        List<Long> allDeletedDeptIds = new ArrayList<>(ids);
        for (Dept dept : departmentsDb) {
          List<Long> subIds = deptRepo.findIdByParentLikeId(
              dept.hasParent() ? dept.getParentLikeId() + "-" + dept.getId()
                  : String.valueOf(dept.getId()));
          if (isNotEmpty(subIds)) {
            allDeletedDeptIds.addAll(subIds);
          }
        }
        deptRepo.deleteByIdIn(allDeletedDeptIds);

        // Delete dept association
        userDeptCmd.deleteAllByDeptId(allDeletedDeptIds);
        orgTagTargetCmd.deleteAllByTarget(OrgTargetType.DEPT, allDeletedDeptIds);

        // Clear user mainDeptId
        userCmd.clearMainDeptByDeptIdIn(ids);

        // Delete dept auth in AAS service
        authPolicyDeptCmd.deptPolicyDeleteBatch(new HashSet<>(ids), null);
        return null;
      }
    }.execute();
  }

  private void setAndUpdateLevelAndParentLikeId(List<Dept> departments, Map<Long, Dept> deptsDbMap,
      Map<Long, Dept> parentDeptsDbMap) {
    for (Dept dept : departments) {
      // Fix:: A null pid means that the parent is not modified
      if (Objects.isNull(dept.getPid())) {
        continue;
      }

      dept.setParentLikeId(assembleParentLikeId(dept.getPid(), parentDeptsDbMap));

      Dept deptDb = deptsDbMap.get(dept.getId());
      // Parent department has not changed
      if (dept.getPid().equals(deptDb.getPid())) {
        // Fix:: replace level is null
        dept.setLevel(deptDb.getLevel()).setParentLikeId(deptDb.getParentLikeId());
        continue;
      }

      // Modify the moved department level, parentLikeId
      Dept parentDeptDb = null;
      if (dept.hasParent()) {
        parentDeptDb = parentDeptsDbMap.get(dept.getPid());
        dept.setLevel(parentDeptDb.getSubLevel());
        dept.setParentLikeId(assembleParentLikeId(dept.getPid(), parentDeptDb));
      } else {
        // No parent department
        dept.setLevel(1).setParentLikeId(null);
      }

      // No sub department
      int subDeptNum = deptRepo.findIdByParentLikeId(deptDb.getSubParentLikeId()).size();
      if (subDeptNum < 1) {
        continue;
      }

      // Modify(replace) sub department of new level and parentLikeId
      String oldSubParentLikeId = deptDb.getSubParentLikeId();
      String newSubParentLikeId = isNull(parentDeptDb) ? "" : dept.getSubParentLikeId();
      int newDiffLevel = newSubParentLikeId.split("-").length
          - oldSubParentLikeId.split("-").length;
      deptRepo.updateSubParentByOldParentLikeId(newDiffLevel, oldSubParentLikeId,
          newSubParentLikeId);
    }
  }

  public int calcLevel(Dept dept, Map<Long, Dept> parentDeptsDbMap) {
    return isEmpty(parentDeptsDbMap) || isNull(parentDeptsDbMap.get(dept.getPid())) ? 1 :
        parentDeptsDbMap.get(dept.getPid()).getLevel() + 1;
  }

  public String assembleParentLikeId(Long pid, Map<Long, Dept> parentDeptsDbMap) {
    if (isEmpty(parentDeptsDbMap) || pid.equals(DEFAULT_ROOT_PID)
        || isNull(parentDeptsDbMap.get(pid))) {
      return null;
    }
    String parentLikeId = parentDeptsDbMap.get(pid).getParentLikeId();
    return isEmpty(parentLikeId) ? String.valueOf(pid) : parentLikeId + "-" + pid;
  }

  public String assembleParentLikeId(Long pid, Dept parentDeptDb) {
    if (isEmpty(parentDeptDb) || pid.equals(DEFAULT_ROOT_PID)) {
      return null;
    }
    String parentLikeId = parentDeptDb.getParentLikeId();
    return isEmpty(parentLikeId) ? String.valueOf(pid) : parentLikeId + "-" + pid;
  }

  private List<OrgTagTarget> buildOrgTagTargets(List<Dept> departments) {
    return departments.stream().filter(x -> isNotEmpty(x.getTagIds()))
        .map(dept -> DeptTagConverter.dtoToDeptTagsDomain(dept.getTagIds(), dept.getId()))
        .flatMap(Collection::stream).collect(Collectors.toList());
  }

  @Override
  protected BaseRepository<Dept, Long> getRepository() {
    return this.deptRepo;
  }
}
