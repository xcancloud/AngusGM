package cloud.xcan.angus.core.gm.application.query.to.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceExisted;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.to.TORole;
import cloud.xcan.angus.api.commonlink.to.TORoleRepo;
import cloud.xcan.angus.api.commonlink.to.TORoleUser;
import cloud.xcan.angus.api.commonlink.to.TORoleUserRepo;
import cloud.xcan.angus.api.commonlink.to.TOUser;
import cloud.xcan.angus.api.commonlink.to.TOUserRepo;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.to.TOUserQuery;
import cloud.xcan.angus.core.gm.domain.to.TOUserSearchRepo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * <p>
 * Implementation of TO (Tenant Operation) user query operations.
 * </p>
 * <p>
 * Manages TO user retrieval, validation, and role association.
 * Provides comprehensive TO user querying with full-text search support.
 * </p>
 * <p>
 * Supports TO user detail retrieval, paginated listing, validation,
 * and role association for comprehensive TO user administration.
 * </p>
 */
@Biz
public class TOUserQueryImpl implements TOUserQuery {

  @Resource
  private TOUserRepo toUserRepo;
  @Resource
  private TOUserSearchRepo toUserSearchRepo;
  @Resource
  private TORoleRepo toRoleRepo;
  @Resource
  private TORoleUserRepo toRoleUserRepo;

  /**
   * <p>
   * Retrieves detailed TO user information by user ID.
   * </p>
   * <p>
   * Fetches TO user record with role association.
   * Throws ResourceNotFound if TO user does not exist.
   * </p>
   */
  @Override
  public TOUser detail(Long userId) {
    return new BizTemplate<TOUser>() {

      @Override
      protected TOUser process() {
        TOUser toUser = toUserRepo.findByUserId(userId)
            .orElseThrow(() -> ResourceNotFound.of(userId, "TOUser"));
        setUserPolicy(toUser);
        return toUser;
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves TO users with optional filtering and search capabilities.
   * </p>
   * <p>
   * Supports full-text search and specification-based filtering.
   * Returns paginated TO user results.
   * </p>
   */
  @Override
  public Page<TOUser> list(GenericSpecification<TOUser> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<TOUser>>() {

      @Override
      protected Page<TOUser> process() {
        return fullTextSearch
            ? toUserSearchRepo.find(spec.getCriteria(), pageable, TOUser.class, match)
            : toUserRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  /**
   * <p>
   * Validates and retrieves TO user by user ID.
   * </p>
   * <p>
   * Returns TO user with existence validation.
   * Throws ResourceNotFound if TO user does not exist.
   * </p>
   */
  @Override
  public TOUser checkAndFind(Long userId) {
    return checkAndFind(List.of(userId)).get(0);
  }

  /**
   * <p>
   * Validates and retrieves TO users by user IDs.
   * </p>
   * <p>
   * Returns TO users with existence validation.
   * Validates that all requested TO user IDs exist.
   * </p>
   */
  @Override
  public List<TOUser> checkAndFind(Collection<Long> userIds) {
    if (isEmpty(userIds)) {
      return null;
    }
    List<TOUser> toUsers = toUserRepo.findAllByUserIdIn(userIds);
    assertResourceNotFound(isNotEmpty(toUsers), userIds.iterator().next(), "TOUser");
    if (userIds.size() != toUsers.size()) {
      for (TOUser user : toUsers) {
        assertResourceNotFound(userIds.contains(user.getId()), user.getId(), "TORole");
      }
    }
    return toUsers;
  }

  /**
   * <p>
   * Validates TO user existence by user IDs.
   * </p>
   * <p>
   * Checks if TO users exist for the specified user IDs.
   * Throws ResourceNotFound if any TO user does not exist.
   * </p>
   */
  @Override
  public void checkExists(Collection<Long> userIds) {
    if (isEmpty(userIds)) {
      return;
    }
    List<TOUser> toUsers = toUserRepo.findAllByUserIdIn(userIds);
    assertResourceExisted(toUsers, userIds.iterator().next(), "TOUser");
  }

  /**
   * <p>
   * Sets role association for TO user.
   * </p>
   * <p>
   * Associates roles with the specified TO user.
   * Enriches TO user with role information for complete data.
   * </p>
   */
  private void setUserPolicy(TOUser toUser) {
    List<TORoleUser> tpu = toRoleUserRepo.findAllByUserIdIn(
        Collections.singletonList(toUser.getUserId()));
    List<TORole> topPolices = toRoleRepo
        .findAllById(tpu.stream().map(TORoleUser::getToRoleId).collect(Collectors.toSet()));
    toUser.setToRoles(topPolices);
  }
}
