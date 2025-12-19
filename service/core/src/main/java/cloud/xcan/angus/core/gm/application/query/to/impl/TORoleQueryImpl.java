package cloud.xcan.angus.core.gm.application.query.to.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceExisted;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.domain.AuthMessage.TO_POLICY_IS_DISABLED_T;
import static cloud.xcan.angus.remote.message.ProtocolException.M.PARAM_VALUE_DUPLICATE_T;
import static cloud.xcan.angus.remote.message.http.ResourceExisted.M.RESOURCE_ALREADY_EXISTS_T2;
import static cloud.xcan.angus.spec.utils.ObjectUtils.duplicateByKey;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.to.TORole;
import cloud.xcan.angus.api.commonlink.to.TORoleRepo;
import cloud.xcan.angus.api.commonlink.to.TORoleUser;
import cloud.xcan.angus.api.commonlink.to.TORoleUserRepo;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.UserRepo;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.to.TORoleQuery;
import cloud.xcan.angus.core.gm.domain.to.TORoleSearchRepo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * <p>
 * Implementation of TO (Tenant Operation) role query operations.
 * </p>
 * <p>
 * Manages TO role retrieval, validation, and user association. Provides comprehensive TO role
 * querying with full-text search support.
 * </p>
 * <p>
 * Supports TO role detail retrieval, paginated listing, validation, and user association for
 * comprehensive TO role administration.
 * </p>
 */
@org.springframework.stereotype.Service
public class TORoleQueryImpl implements TORoleQuery {

  @Resource
  private TORoleRepo toRoleRepo;
  @Resource
  private TORoleSearchRepo toRoleSearchRepo;
  @Resource
  private TORoleUserRepo toRoleUserRepo;
  @Resource
  private UserRepo userRepo;

  /**
   * <p>
   * Retrieves detailed TO role information by ID or code.
   * </p>
   * <p>
   * Fetches TO role by ID or code with user association. Supports both numeric ID and string code
   * lookup.
   * </p>
   */
  @Override
  public TORole detail(String idOrCode) {
    return new BizTemplate<TORole>() {

      @Override
      protected TORole process() {
        TORole toPolicyDb = null;
        if (NumberUtils.isDigits(idOrCode)) {
          toPolicyDb = find0(Long.parseLong(idOrCode));
        }
        if (Objects.isNull(toPolicyDb)) {
          toPolicyDb = toRoleRepo.findByCode(idOrCode).orElse(null);
        }

        assertResourceNotFound(toPolicyDb, idOrCode, "TORole");

        // Join policy users
        setPolicyUser(toPolicyDb);
        return toPolicyDb;
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves TO roles with optional filtering and search capabilities.
   * </p>
   * <p>
   * Supports full-text search and specification-based filtering. Returns paginated TO role
   * results.
   * </p>
   */
  @Override
  public Page<TORole> list(GenericSpecification<TORole> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<TORole>>() {

      @Override
      protected Page<TORole> process() {
        return fullTextSearch
            ? toRoleSearchRepo.find(spec.getCriteria(), pageable, TORole.class, match)
            : toRoleRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves TO role by ID without validation.
   * </p>
   * <p>
   * Returns TO role without existence validation. Returns null if TO role does not exist.
   * </p>
   */
  @Override
  public TORole find0(Long policyId) {
    return toRoleRepo.findById(policyId).orElse(null);
  }

  /**
   * <p>
   * Retrieves TO roles by IDs.
   * </p>
   * <p>
   * Returns TO roles for the specified role IDs. Returns empty list if no roles found.
   * </p>
   */
  @Override
  public List<TORole> findAllById(Set<Long> ids) {
    return toRoleRepo.findAllByIdIn(ids);
  }

  /**
   * <p>
   * Validates and retrieves TO role by ID.
   * </p>
   * <p>
   * Returns TO role with existence validation and optional enabled check. Throws ResourceNotFound
   * if TO role does not exist.
   * </p>
   */
  @Override
  public TORole checkAndFind(Long policyId, boolean checkEnabled) {
    return checkAndFind(List.of(policyId), checkEnabled).get(0);
  }

  /**
   * <p>
   * Validates and retrieves TO roles by IDs.
   * </p>
   * <p>
   * Returns TO roles with existence validation and optional enabled check. Validates that all
   * requested TO role IDs exist.
   * </p>
   */
  @Override
  public List<TORole> checkAndFind(Collection<Long> policyIds, boolean checkEnabled) {
    if (isEmpty(policyIds)) {
      return null;
    }
    List<TORole> policies = toRoleRepo.findAllByIdIn(policyIds);
    assertResourceNotFound(isNotEmpty(policies), policyIds.iterator().next(), "TORole");
    if (policyIds.size() != policies.size()) {
      for (TORole policy : policies) {
        assertResourceNotFound(policyIds.contains(policy.getId()), policy.getId(), "TORole");
      }
    }
    if (checkEnabled) {
      for (TORole policy : policies) {
        assertTrue(policy.getEnabled(), TO_POLICY_IS_DISABLED_T, new Object[]{policy.getName()});
      }
    }
    return policies;
  }

  /**
   * <p>
   * Validates duplicate TO roles in parameters.
   * </p>
   * <p>
   * Checks for duplicate code and name within the provided role list. Throws ProtocolException if
   * duplicates are found.
   * </p>
   */
  @Override
  public void checkDuplicateInParam(List<TORole> apps) {
    if (isEmpty(apps)) {
      return;
    }
    List<TORole> duplicateCodePolicy = apps.stream().filter(x -> nonNull(x.getCode()))
        .filter(duplicateByKey(TORole::getCode)).collect(Collectors.toList());
    assertTrue(isEmpty(duplicateCodePolicy), PARAM_VALUE_DUPLICATE_T, new Object[]{
        "code", isEmpty(duplicateCodePolicy) ? null : duplicateCodePolicy.get(0).getCode()});
    List<TORole> duplicateNamePolicy = apps.stream().filter(x -> nonNull(x.getName()))
        .filter(duplicateByKey(TORole::getName)).collect(Collectors.toList());
    assertTrue(isEmpty(duplicateNamePolicy), PARAM_VALUE_DUPLICATE_T, new Object[]{
        "name", isEmpty(duplicateNamePolicy) ? null : duplicateNamePolicy.get(0).getName()});
  }

  /**
   * <p>
   * Validates TO role code and name uniqueness.
   * </p>
   * <p>
   * Ensures TO role code and name are unique across the system. Handles both insert and update
   * scenarios.
   * </p>
   */
  @Override
  public void checkUniqueCodeAndName(List<TORole> policies) {
    if (isEmpty(policies)) {
      return;
    }
    for (TORole policy : policies) {
      if (nonNull(policy.getId())) {
        // Update or replace
        assertResourceExisted(isEmpty(policy.getCode()) ||
            toRoleRepo.existsByCodeAndIdNot(policy.getCode(),
                policy.getId()), RESOURCE_ALREADY_EXISTS_T2, policy.getCode());
        assertResourceExisted(isEmpty(policy.getCode()) ||
            !toRoleRepo.existsByNameAndIdNot(policy.getName(),
                policy.getId()), RESOURCE_ALREADY_EXISTS_T2, policy.getName());
      } else {
        // Insert
        assertResourceExisted(!toRoleRepo.existsByCode(policy.getCode()),
            RESOURCE_ALREADY_EXISTS_T2, policy.getCode());
        assertResourceExisted(!toRoleRepo.existsByName(policy.getName()),
            RESOURCE_ALREADY_EXISTS_T2, policy.getName());
      }
    }
  }

  /**
   * <p>
   * Sets user association for TO role.
   * </p>
   * <p>
   * Associates users with the specified TO role. Enriches TO role with user information for
   * complete data.
   * </p>
   */
  private void setPolicyUser(TORole toPolicyDb) {
    List<TORoleUser> tpu = toRoleUserRepo.findAllByToRoleIdIn(
        Collections.singletonList(toPolicyDb.getId()));
    List<User> topUsers = userRepo
        .findAllById(tpu.stream().map(TORoleUser::getUserId).collect(Collectors.toSet()));
    toPolicyDb.setUsers(topUsers);
  }
}
