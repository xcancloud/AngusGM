package cloud.xcan.angus.core.gm.application.cmd.dept.impl;

import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.DEPT;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.CREATED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.DELETED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.UPDATED;
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
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
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

  @Resource
  private OperationLogCmd operationLogCmd;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> add(List<Dept> dept) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      final Long optTenantId = getOptTenantId();
      List<Dept> parentDeptDb;
      Map<Long, Dept> parentDeptDbMap;

      @Override
      protected void checkParams() {
        // Check the code is not repeated
        deptQuery.checkAddDeptCode(optTenantId, dept);
        // Check the pid existed
        parentDeptDb = deptQuery.checkAndGetParent(optTenantId, dept);
        // Check the department and level quota
        deptQuery.checkDeptQuota(optTenantId, dept.size());
        if (isNotEmpty(parentDeptDb)) {
          parentDeptDbMap = parentDeptDb.stream().collect(Collectors.toMap(Dept::getId, x -> x));
        }
        deptQuery.checkDeptLevelQuota(optTenantId, dept, null, parentDeptDbMap, true);
        // Check tag quotas
        deptQuery.checkTagQuota(optTenantId, dept);
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        // Save departments
        List<Dept> departmentsDb = dept.stream().peek(
                dept -> dept.setLevel(calcLevel(dept, parentDeptDbMap))
                    .setParentLikeId(assembleParentLikeId(dept.getPid(), parentDeptDbMap)))
            .collect(Collectors.toList());
        List<IdKey<Long, Object>> idKeys = batchInsert(departmentsDb);

        // Save department tags
        List<OrgTagTarget> allTags = buildOrgTagTargets(departmentsDb);
        if (isNotEmpty(allTags)) {
          orgTagTargetCmd.add(allTags);
        }

        // Save operation log
        operationLogCmd.addAll(DEPT, dept, CREATED);
        return idKeys;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void update(List<Dept> dept) {
    new BizTemplate<Void>() {
      final Long optTenantId = getOptTenantId();
      List<Dept> deptDb;
      Map<Long, Dept> deptDbMap;
      List<Dept> parentDeptDb;
      Map<Long, Dept> parentDeptDbMap;
      Set<Long> updateDeptIds;

      @Override
      protected void checkParams() {
        // Check the code not repeated
        deptQuery.checkUpdateDeptCode(optTenantId, dept);
        // Check the departments existed
        updateDeptIds = dept.stream().map(Dept::getId).collect(Collectors.toSet());
        deptDb = deptQuery.checkAndFind(updateDeptIds);
        // Check the nested duplicates
        deptQuery.checkNestedDuplicates(deptDb);
        // Check the pid existed
        parentDeptDb = deptQuery.checkAndGetParent(optTenantId, dept);
        // Check the department and level quota
        // deptQuery.checkDeptQuota(tenantId, departments.size());
        if (isNotEmpty(parentDeptDb)) {
          parentDeptDbMap = parentDeptDb.stream().collect(Collectors.toMap(Dept::getId, x -> x));
        }
        deptDbMap = deptDb.stream().collect(Collectors.toMap(Dept::getId, x -> x));
        deptQuery.checkDeptLevelQuota(optTenantId, dept, deptDbMap, parentDeptDbMap, false);
        // Check the tag quotas
        deptQuery.checkTagQuota(optTenantId, dept);
      }

      @Override
      protected Void process() {
        // Calculate and update level, parentLikeId.
        setAndUpdateLevelAndParentLikeId(dept, deptDbMap, parentDeptDbMap);

        // Update departments.
        deptDb = dept.stream().map(dept0 ->
                copyPropertiesIgnoreNull(dept0, deptDbMap.get(dept0.getId()))
                    .setParentLikeId(dept0.getParentLikeId())/* Set null when moved to root */)
            .collect(Collectors.toList());
        deptRepo.saveAll(deptDb);

        // Replace tags
        List<OrgTagTarget> allTags = buildOrgTagTargets(dept);
        if (isNotEmpty(allTags)) {
          orgTagTargetCmd.deleteAllByTarget(OrgTargetType.DEPT, updateDeptIds);
          orgTagTargetCmd.add(allTags);
        }

        // Save operation log
        operationLogCmd.addAll(DEPT, dept, UPDATED);
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> replace(List<Dept> dept) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      final Long optTenantId = getOptTenantId();
      List<Dept> replaceDept;
      List<Dept> replaceDeptDb;
      Map<Long, Dept> replaceDeptDbMap;
      List<Long> replaceDepIds;
      List<Dept> parentDeptDb = null;
      Map<Long, Dept> parentDeptDbMap = null;

      @Override
      protected void checkParams() {
        replaceDept = dept.stream().filter(dept -> nonNull(dept.getId()))
            .collect(Collectors.toList());
        if (isNotEmpty(replaceDept)) {
          // Check the departments existed
          replaceDeptDb = deptQuery.checkAndFind(replaceDepIds);
          replaceDepIds = replaceDept.stream().map(Dept::getId).collect(Collectors.toList());
          // Check the code is not repeated
          deptQuery.checkUpdateDeptCode(optTenantId, replaceDept);
          // Check nested duplicates
          deptQuery.checkNestedDuplicates(replaceDeptDb);
          // Check pid existed
          parentDeptDb = deptQuery.checkAndGetParent(optTenantId, dept);
          // Check dept and level quota
          // deptQuery.checkDeptQuota(tenantId, departments.size());
          if (isNotEmpty(parentDeptDb)) {
            parentDeptDbMap = parentDeptDb.stream().collect(Collectors.toMap(Dept::getId, x -> x));
          }
          if (isNotEmpty(replaceDept)) {
            replaceDeptDbMap = replaceDeptDb.stream()
                .collect(Collectors.toMap(Dept::getId, x -> x));
          }
          deptQuery.checkDeptLevelQuota(optTenantId, replaceDept, replaceDeptDbMap,
              parentDeptDbMap, false);
          // Check tag quotas
          deptQuery.checkTagQuota(optTenantId, replaceDept);
        }
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        List<IdKey<Long, Object>> idKeys = new ArrayList<>();

        List<Dept> addDept = dept.stream().filter(dept -> isNull(dept.getId()))
            .collect(Collectors.toList());
        if (isNotEmpty(addDept)) {
          idKeys.addAll(add(addDept));
        }

        if (isNotEmpty(replaceDept)) {
          // Calc and update level, parentLikeId
          setAndUpdateLevelAndParentLikeId(replaceDept, replaceDeptDbMap, parentDeptDbMap);

          // Do not replace tenant auditing.
          replaceDeptDb = replaceDept.stream()
              .map(dept -> copyPropertiesIgnoreTenantAuditing(dept,
                  replaceDeptDbMap.get(dept.getId()))).collect(Collectors.toList());
          deptRepo.saveAll(replaceDeptDb);

          // Replace department tags.
          List<OrgTagTarget> allTags = buildOrgTagTargets(replaceDept);
          if (isNotEmpty(allTags)) {
            orgTagTargetCmd.deleteAllByTarget(OrgTargetType.DEPT, replaceDepIds);
            orgTagTargetCmd.add(allTags);
          }

          idKeys.addAll(replaceDeptDb.stream().map(x -> IdKey.of(x.getId(), x.getName())).toList());

          // Save operation log
          operationLogCmd.addAll(DEPT, replaceDeptDb, UPDATED);
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
        List<Dept> deptDb = deptRepo.findAllById(ids);
        if (isEmpty(deptDb)) {
          return null;
        }

        // Delete current and sub departments
        List<Long> allDeletedDeptIds = new ArrayList<>(ids);
        for (Dept dept : deptDb) {
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

        // Save operation log
        operationLogCmd.addAll(DEPT, deptDb, DELETED);
        return null;
      }
    }.execute();
  }

  private void setAndUpdateLevelAndParentLikeId(List<Dept> dept, Map<Long, Dept> deptDbMap,
      Map<Long, Dept> parentDeptDbMap) {
    for (Dept dept0 : dept) {
      // Fix:: A null pid means that the parent is not modified
      if (Objects.isNull(dept0.getPid())) {
        continue;
      }

      dept0.setParentLikeId(assembleParentLikeId(dept0.getPid(), parentDeptDbMap));

      Dept deptDb = deptDbMap.get(dept0.getId());
      // Parent department has not changed
      if (dept0.getPid().equals(deptDb.getPid())) {
        // Fix:: replace level is null
        dept0.setLevel(deptDb.getLevel()).setParentLikeId(deptDb.getParentLikeId());
        continue;
      }

      // Modify the moved department level, parentLikeId
      Dept parentDeptDb = null;
      if (dept0.hasParent()) {
        parentDeptDb = parentDeptDbMap.get(dept0.getPid());
        dept0.setLevel(parentDeptDb.getSubLevel());
        dept0.setParentLikeId(assembleParentLikeId(dept0.getPid(), parentDeptDb));
      } else {
        // No parent department
        dept0.setLevel(1).setParentLikeId(null);
      }

      // No sub department
      int subDeptNum = deptRepo.findIdByParentLikeId(deptDb.getSubParentLikeId()).size();
      if (subDeptNum < 1) {
        continue;
      }

      // Modify(replace) sub department of new level and parentLikeId
      String oldSubParentLikeId = deptDb.getSubParentLikeId();
      String newSubParentLikeId = isNull(parentDeptDb) ? "" : dept0.getSubParentLikeId();
      int newDiffLevel = newSubParentLikeId.split("-").length
          - oldSubParentLikeId.split("-").length;
      deptRepo.updateSubParentByOldParentLikeId(newDiffLevel, oldSubParentLikeId,
          newSubParentLikeId);
    }
  }

  public int calcLevel(Dept dept, Map<Long, Dept> parentDeptDbMap) {
    return isEmpty(parentDeptDbMap) || isNull(parentDeptDbMap.get(dept.getPid())) ? 1 :
        parentDeptDbMap.get(dept.getPid()).getLevel() + 1;
  }

  public String assembleParentLikeId(Long pid, Map<Long, Dept> parentDeptDbMap) {
    if (isEmpty(parentDeptDbMap) || pid.equals(DEFAULT_ROOT_PID)
        || isNull(parentDeptDbMap.get(pid))) {
      return null;
    }
    String parentLikeId = parentDeptDbMap.get(pid).getParentLikeId();
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
