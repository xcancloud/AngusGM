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

/**
 * Implementation of department command operations for managing organizational departments.
 *
 * <p>This class provides comprehensive functionality for department management including:</p>
 * <ul>
 *   <li>Creating, updating, and deleting departments</li>
 *   <li>Managing department hierarchies and parent-child relationships</li>
 *   <li>Handling department tags and organizational structure</li>
 *   <li>Managing department quotas and level constraints</li>
 *   <li>Recording operation logs for audit trails</li>
 * </ul>
 *
 * <p>The implementation ensures proper organizational structure management
 * with hierarchical relationships and data consistency.</p>
 */
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

  /**
   * Creates multiple departments with comprehensive validation and setup.
   *
   * <p>This method performs department creation including:</p>
   * <ul>
   *   <li>Validating department code uniqueness</li>
   *   <li>Checking parent department existence</li>
   *   <li>Validating department and level quotas</li>
   *   <li>Calculating department levels and parent relationships</li>
   *   <li>Creating department tags</li>
   *   <li>Recording creation audit logs</li>
   * </ul>
   *
   * @param dept List of department entities to create
   * @return List of created department identifiers with associated data
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> add(List<Dept> dept) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      final Long optTenantId = getOptTenantId();
      List<Dept> parentDeptDb;
      Map<Long, Dept> parentDeptDbMap;

      @Override
      protected void checkParams() {
        // Validate department code uniqueness
        deptQuery.checkAddDeptCode(optTenantId, dept);
        // Validate parent department existence
        parentDeptDb = deptQuery.checkAndGetParent(optTenantId, dept);
        // Validate department and level quotas
        deptQuery.checkDeptQuota(optTenantId, dept.size());
        if (isNotEmpty(parentDeptDb)) {
          parentDeptDbMap = parentDeptDb.stream().collect(Collectors.toMap(Dept::getId, x -> x));
        }
        deptQuery.checkDeptLevelQuota(optTenantId, dept, null, parentDeptDbMap, true);
        // Validate tag quotas
        deptQuery.checkTagQuota(optTenantId, dept);
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        // Save departments with calculated levels and parent relationships
        List<Dept> departmentsDb = dept.stream().peek(
                dept -> dept.setLevel(calcLevel(dept, parentDeptDbMap))
                    .setParentLikeId(assembleParentLikeId(dept.getPid(), parentDeptDbMap)))
            .collect(Collectors.toList());
        List<IdKey<Long, Object>> idKeys = batchInsert(departmentsDb);

        // Create department tags
        List<OrgTagTarget> allTags = buildOrgTagTargets(departmentsDb);
        if (isNotEmpty(allTags)) {
          orgTagTargetCmd.add(allTags);
        }

        // Record creation audit log
        operationLogCmd.addAll(DEPT, dept, CREATED);
        return idKeys;
      }
    }.execute();
  }

  /**
   * Updates multiple departments with comprehensive validation and hierarchy management.
   *
   * <p>This method performs department updates including:</p>
   * <ul>
   *   <li>Validating department code uniqueness</li>
   *   <li>Checking department existence and nested duplicates</li>
   *   <li>Validating parent department relationships</li>
   *   <li>Updating department levels and parent relationships</li>
   *   <li>Managing department tags</li>
   *   <li>Recording update audit logs</li>
   * </ul>
   *
   * @param dept List of department entities to update
   */
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
        // Validate department code uniqueness
        deptQuery.checkUpdateDeptCode(optTenantId, dept);
        // Validate department existence
        updateDeptIds = dept.stream().map(Dept::getId).collect(Collectors.toSet());
        deptDb = deptQuery.checkAndFind(updateDeptIds);
        // Validate nested duplicates
        deptQuery.checkNestedDuplicates(deptDb);
        // Validate parent department existence
        parentDeptDb = deptQuery.checkAndGetParent(optTenantId, dept);
        // Validate department level quotas
        if (isNotEmpty(parentDeptDb)) {
          parentDeptDbMap = parentDeptDb.stream().collect(Collectors.toMap(Dept::getId, x -> x));
        }
        deptDbMap = deptDb.stream().collect(Collectors.toMap(Dept::getId, x -> x));
        deptQuery.checkDeptLevelQuota(optTenantId, dept, deptDbMap, parentDeptDbMap, false);
        // Validate tag quotas
        deptQuery.checkTagQuota(optTenantId, dept);
      }

      @Override
      protected Void process() {
        // Calculate and update department levels and parent relationships
        setAndUpdateLevelAndParentLikeId(dept, deptDbMap, parentDeptDbMap);

        // Update departments with null-safe property copying
        deptDb = dept.stream().map(dept0 ->
                copyPropertiesIgnoreNull(dept0, deptDbMap.get(dept0.getId()))
                    .setParentLikeId(dept0.getParentLikeId()) /* Set null when moved to root */)
            .collect(Collectors.toList());
        deptRepo.saveAll(deptDb);

        // Replace department tags
        List<OrgTagTarget> allTags = buildOrgTagTargets(dept);
        if (isNotEmpty(allTags)) {
          orgTagTargetCmd.deleteAllByTarget(OrgTargetType.DEPT, updateDeptIds);
          orgTagTargetCmd.add(allTags);
        }

        // Record update audit log
        operationLogCmd.addAll(DEPT, deptDb, UPDATED);
        return null;
      }
    }.execute();
  }

  /**
   * Replaces departments by creating new ones and updating existing ones.
   *
   * <p>This method handles both creation and update scenarios including:</p>
   * <ul>
   *   <li>Creating new departments if no ID is provided</li>
   *   <li>Updating existing departments if ID is provided</li>
   *   <li>Managing department hierarchies and relationships</li>
   *   <li>Handling department tags and audit logs</li>
   * </ul>
   *
   * @param dept List of department entities to replace
   * @return List of department identifiers with associated data
   */
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
          // Validate department existence
          replaceDeptDb = deptQuery.checkAndFind(replaceDepIds);
          replaceDepIds = replaceDept.stream().map(Dept::getId).collect(Collectors.toList());
          // Validate department code uniqueness
          deptQuery.checkUpdateDeptCode(optTenantId, replaceDept);
          // Validate nested duplicates
          deptQuery.checkNestedDuplicates(replaceDeptDb);
          // Validate parent department existence
          parentDeptDb = deptQuery.checkAndGetParent(optTenantId, dept);
          // Validate department level quotas
          if (isNotEmpty(parentDeptDb)) {
            parentDeptDbMap = parentDeptDb.stream().collect(Collectors.toMap(Dept::getId, x -> x));
          }
          if (isNotEmpty(replaceDept)) {
            replaceDeptDbMap = replaceDeptDb.stream()
                .collect(Collectors.toMap(Dept::getId, x -> x));
          }
          deptQuery.checkDeptLevelQuota(optTenantId, replaceDept, replaceDeptDbMap,
              parentDeptDbMap, false);
          // Validate tag quotas
          deptQuery.checkTagQuota(optTenantId, replaceDept);
        }
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        List<IdKey<Long, Object>> idKeys = new ArrayList<>();

        // Create new departments
        List<Dept> addDept = dept.stream().filter(dept -> isNull(dept.getId()))
            .collect(Collectors.toList());
        if (isNotEmpty(addDept)) {
          idKeys.addAll(add(addDept));
        }

        if (isNotEmpty(replaceDept)) {
          // Calculate and update department levels and parent relationships
          setAndUpdateLevelAndParentLikeId(replaceDept, replaceDeptDbMap, parentDeptDbMap);

          // Update departments without replacing tenant auditing
          replaceDeptDb = replaceDept.stream()
              .map(dept -> copyPropertiesIgnoreTenantAuditing(dept,
                  replaceDeptDbMap.get(dept.getId()))).collect(Collectors.toList());
          deptRepo.saveAll(replaceDeptDb);

          // Replace department tags
          List<OrgTagTarget> allTags = buildOrgTagTargets(replaceDept);
          if (isNotEmpty(allTags)) {
            orgTagTargetCmd.deleteAllByTarget(OrgTargetType.DEPT, replaceDepIds);
            orgTagTargetCmd.add(allTags);
          }

          idKeys.addAll(replaceDeptDb.stream().map(x -> IdKey.of(x.getId(), x.getName())).toList());

          // Record update audit log
          operationLogCmd.addAll(DEPT, replaceDeptDb, UPDATED);
        }
        return idKeys;
      }
    }.execute();
  }

  /**
   * Deletes departments and cleans up related data.
   *
   * <p>This method performs comprehensive department deletion including:</p>
   * <ul>
   *   <li>Deleting current and sub-departments</li>
   *   <li>Cleaning up department associations</li>
   *   <li>Clearing user main department references</li>
   *   <li>Removing department authorization policies</li>
   *   <li>Recording deletion audit logs</li>
   * </ul>
   *
   * @param ids Set of department identifiers to delete
   */
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

        // Delete current and sub-departments
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

        // Clean up department associations
        userDeptCmd.deleteAllByDeptId(allDeletedDeptIds);
        orgTagTargetCmd.deleteAllByTarget(OrgTargetType.DEPT, allDeletedDeptIds);

        // Clear user main department references
        userCmd.clearMainDeptByDeptIdIn(ids);

        // Remove department authorization policies
        authPolicyDeptCmd.deptPolicyDeleteBatch(new HashSet<>(ids), null);

        // Record deletion audit log
        operationLogCmd.addAll(DEPT, deptDb, DELETED);
        return null;
      }
    }.execute();
  }

  /**
   * Updates department levels and parent relationships for moved departments.
   *
   * <p>This method handles department hierarchy updates when departments are moved,
   * ensuring proper level calculations and parent relationship maintenance.</p>
   *
   * @param dept List of departments to update
   * @param deptDbMap Map of existing departments
   * @param parentDeptDbMap Map of parent departments
   */
  private void setAndUpdateLevelAndParentLikeId(List<Dept> dept, Map<Long, Dept> deptDbMap,
      Map<Long, Dept> parentDeptDbMap) {
    for (Dept dept0 : dept) {
      // Skip if parent ID is null (parent not modified)
      if (Objects.isNull(dept0.getPid())) {
        continue;
      }

      dept0.setParentLikeId(assembleParentLikeId(dept0.getPid(), parentDeptDbMap));

      Dept deptDb = deptDbMap.get(dept0.getId());
      // Parent department has not changed
      if (dept0.getPid().equals(deptDb.getPid())) {
        // Preserve existing level and parent relationship
        dept0.setLevel(deptDb.getLevel()).setParentLikeId(deptDb.getParentLikeId());
        continue;
      }

      // Update moved department level and parent relationship
      Dept parentDeptDb = null;
      if (dept0.hasParent()) {
        parentDeptDb = parentDeptDbMap.get(dept0.getPid());
        dept0.setLevel(parentDeptDb.getSubLevel());
        dept0.setParentLikeId(assembleParentLikeId(dept0.getPid(), parentDeptDb));
      } else {
        // No parent department
        dept0.setLevel(1).setParentLikeId(null);
      }

      // Skip if no sub-departments
      int subDeptNum = deptRepo.findIdByParentLikeId(deptDb.getSubParentLikeId()).size();
      if (subDeptNum < 1) {
        continue;
      }

      // Update sub-department levels and parent relationships
      String oldSubParentLikeId = deptDb.getSubParentLikeId();
      String newSubParentLikeId = isNull(parentDeptDb) ? "" : dept0.getSubParentLikeId();
      int newDiffLevel = newSubParentLikeId.split("-").length
          - oldSubParentLikeId.split("-").length;
      deptRepo.updateSubParentByOldParentLikeId(newDiffLevel, oldSubParentLikeId,
          newSubParentLikeId);
    }
  }

  /**
   * Calculates department level based on parent department.
   *
   * @param dept Department entity
   * @param parentDeptDbMap Map of parent departments
   * @return Calculated department level
   */
  public int calcLevel(Dept dept, Map<Long, Dept> parentDeptDbMap) {
    return isEmpty(parentDeptDbMap) || isNull(parentDeptDbMap.get(dept.getPid())) ? 1 :
        parentDeptDbMap.get(dept.getPid()).getLevel() + 1;
  }

  /**
   * Assembles parent-like ID for department hierarchy tracking.
   *
   * @param pid Parent department identifier
   * @param parentDeptDbMap Map of parent departments
   * @return Assembled parent-like ID string
   */
  public String assembleParentLikeId(Long pid, Map<Long, Dept> parentDeptDbMap) {
    if (isEmpty(parentDeptDbMap) || pid.equals(DEFAULT_ROOT_PID)
        || isNull(parentDeptDbMap.get(pid))) {
      return null;
    }
    String parentLikeId = parentDeptDbMap.get(pid).getParentLikeId();
    return isEmpty(parentLikeId) ? String.valueOf(pid) : parentLikeId + "-" + pid;
  }

  /**
   * Assembles parent-like ID for single parent department.
   *
   * @param pid Parent department identifier
   * @param parentDeptDb Parent department entity
   * @return Assembled parent-like ID string
   */
  public String assembleParentLikeId(Long pid, Dept parentDeptDb) {
    if (isEmpty(parentDeptDb) || pid.equals(DEFAULT_ROOT_PID)) {
      return null;
    }
    String parentLikeId = parentDeptDb.getParentLikeId();
    return isEmpty(parentLikeId) ? String.valueOf(pid) : parentLikeId + "-" + pid;
  }

  /**
   * Builds organization tag targets for departments.
   *
   * @param departments List of departments
   * @return List of organization tag targets
   */
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
