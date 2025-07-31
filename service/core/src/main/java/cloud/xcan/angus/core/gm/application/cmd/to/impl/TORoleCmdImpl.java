package cloud.xcan.angus.core.gm.application.cmd.to.impl;

import static cloud.xcan.angus.core.utils.CoreUtils.copyPropertiesIgnoreTenantAuditing;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.to.TORole;
import cloud.xcan.angus.api.commonlink.to.TORoleRepo;
import cloud.xcan.angus.api.commonlink.to.TORoleUserRepo;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.to.TORoleCmd;
import cloud.xcan.angus.core.gm.application.query.app.AppQuery;
import cloud.xcan.angus.core.gm.application.query.to.TORoleQuery;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * Implementation of tenant operation role command operations.
 * </p>
 * <p>
 * Manages tenant operation role lifecycle including creation, updates, deletion,
 * and status management.
 * </p>
 * <p>
 * Supports role management with application association, duplicate validation,
 * and comprehensive role-user relationship management.
 * </p>
 */
@Biz
public class TORoleCmdImpl extends CommCmd<TORole, Long> implements TORoleCmd {

  @Resource
  private TORoleRepo toRoleRepo;
  @Resource
  private TORoleQuery toRoleQuery;
  @Resource
  private TORoleUserRepo toRoleUserRepo;
  @Resource
  private AppQuery appQuery;

  /**
   * <p>
   * Creates tenant operation roles with validation.
   * </p>
   * <p>
   * Validates role code and name uniqueness, checks for duplicates in parameters
   * and database, and verifies associated applications exist.
   * </p>
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public List<IdKey<Long, Object>> add(List<TORole> roles) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {

      @Override
      protected void checkParams() {
        // Verify code and name uniqueness in parameters
        toRoleQuery.checkDuplicateInParam(roles);

        // Verify code and name uniqueness in database
        toRoleQuery.checkUniqueCodeAndName(roles);

        // Verify associated applications exist
        appQuery.checkAndFind(roles.stream().map(TORole::getAppId).collect(Collectors.toSet()),
            true);
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        return batchInsert(roles, "code");
      }
    }.execute();
  }

  /**
   * <p>
   * Updates tenant operation roles with validation.
   * </p>
   * <p>
   * Validates role existence, checks for duplicates in parameters and database,
   * and updates role information.
   * </p>
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void update(List<TORole> roles) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        // Verify code and name uniqueness in parameters
        toRoleQuery.checkDuplicateInParam(roles);

        // Verify roles exist
        toRoleQuery.checkAndFind(roles.stream().map(TORole::getId)
            .collect(Collectors.toSet()), false);

        // Verify code and name uniqueness in database
        toRoleQuery.checkUniqueCodeAndName(roles);
      }

      @Override
      protected Void process() {
        batchUpdateOrNotFound(roles);
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Replaces tenant operation roles with comprehensive validation.
   * </p>
   * <p>
   * Handles both new role creation and existing role updates in a single operation.
   * Validates all roles and ensures proper data consistency.
   * </p>
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public List<IdKey<Long, Object>> replace(List<TORole> roles) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      List<TORole> replaceTORoles;
      Set<Long> replaceTORoleIds;
      List<TORole> replaceTORoleDb;

      @Override
      protected void checkParams() {
        // Verify code and name uniqueness in parameters
        toRoleQuery.checkDuplicateInParam(roles);
        // Verify updated roles exist
        replaceTORoles = roles.stream().filter(role -> nonNull(role.getId()))
            .collect(Collectors.toList());
        replaceTORoleIds = replaceTORoles.stream().map(TORole::getId)
            .collect(Collectors.toSet());
        replaceTORoleDb = toRoleQuery.checkAndFind(replaceTORoleIds, false);
        // Verify code and name uniqueness in database
        toRoleQuery.checkUniqueCodeAndName(roles);
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        List<IdKey<Long, Object>> idKeys = new ArrayList<>();

        // Create new roles
        List<TORole> addTORoles = roles.stream().filter(role -> isNull(role.getId()))
            .collect(Collectors.toList());
        if (isNotEmpty(addTORoles)) {
          idKeys.addAll(add(addTORoles));
        }

        // Update existing roles
        if (isNotEmpty(replaceTORoleDb)) {
          Map<Long, TORole> toRoleDbMap = replaceTORoleDb.stream()
              .collect(Collectors.toMap(TORole::getId, x -> x));
          toRoleRepo.saveAll(replaceTORoles.stream()
              .map(x -> copyPropertiesIgnoreTenantAuditing(x, toRoleDbMap.get(x.getId()),
                  "appId", "code", "enabled"))
              .collect(Collectors.toList()));

          idKeys.addAll(replaceTORoleDb.stream().map(x ->
              IdKey.of(x.getId(), x.getName())).toList());
        }
        return idKeys;
      }
    }.execute();
  }

  /**
   * <p>
   * Deletes tenant operation roles and associated user assignments.
   * </p>
   * <p>
   * Removes roles and cascades deletion to associated role-user relationships.
   * </p>
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(HashSet<Long> roleIds) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        // Delete roles
        toRoleRepo.deleteByIdIn(roleIds);
        // Delete associated role-user assignments
        toRoleUserRepo.deleteByToRoleIdIn(roleIds);
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Enables or disables tenant operation roles.
   * </p>
   * <p>
   * Validates role existence and updates role status accordingly.
   * </p>
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void enabled(List<TORole> roles) {
    new BizTemplate<Void>() {
      List<Long> roleIds;

      @Override
      protected void checkParams() {
        // Verify roles exist
        roleIds = roles.stream().map(TORole::getId).collect(Collectors.toList());
        toRoleQuery.checkAndFind(roleIds, false);
      }

      @Override
      protected Void process() {
        batchUpdateOrNotFound(roles);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<TORole, Long> getRepository() {
    return this.toRoleRepo;
  }
}
