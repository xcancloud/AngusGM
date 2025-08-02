package cloud.xcan.angus.core.gm.application.query.user.impl;

import static cloud.xcan.angus.api.commonlink.UCConstant.MAX_USER_DIRECTORY_NUM;
import static cloud.xcan.angus.core.biz.exception.QuotaException.M.QUOTA_OVER_LIMIT_T2;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.ProtocolAssert;
import cloud.xcan.angus.core.biz.exception.QuotaException;
import cloud.xcan.angus.core.gm.application.query.user.UserDirectoryQuery;
import cloud.xcan.angus.core.gm.domain.user.directory.UserDirectory;
import cloud.xcan.angus.core.gm.domain.user.directory.UserDirectoryRepo;
import cloud.xcan.angus.remote.message.http.ResourceExisted;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * Implementation of user directory query operations.
 * </p>
 * <p>
 * Manages user directory retrieval, validation, and quota management.
 * Provides comprehensive user directory querying with quota controls.
 * </p>
 * <p>
 * Supports user directory detail retrieval, listing, validation,
 * and quota management for comprehensive user directory administration.
 * </p>
 */
@Biz
public class UserDirectoryQueryImpl implements UserDirectoryQuery {

  @Resource
  private UserDirectoryRepo userDirectoryRepo;

  /**
   * <p>
   * Retrieves detailed user directory information by ID.
   * </p>
   * <p>
   * Fetches complete user directory record with existence validation.
   * Throws ResourceNotFound exception if user directory does not exist.
   * </p>
   */
  @Override
  public UserDirectory detail(Long id) {
    return new BizTemplate<UserDirectory>() {

      @Override
      protected UserDirectory process() {
        return userDirectoryRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of(id, "UserDirectory"));
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves all user directories with sorting.
   * </p>
   * <p>
   * Returns sorted list of all user directories.
   * Uses natural ordering for consistent results.
   * </p>
   */
  @Override
  public List<UserDirectory> list() {
    return new BizTemplate<List<UserDirectory>>() {

      @Override
      protected List<UserDirectory> process() {
        List<UserDirectory> directories = userDirectoryRepo.findAll();
        Collections.sort(directories);
        return directories;
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves user directory by ID without validation.
   * </p>
   * <p>
   * Returns user directory without existence validation.
   * Returns null if user directory does not exist.
   * </p>
   */
  @Override
  public UserDirectory find(Long id) {
    return userDirectoryRepo.findById(id).orElse(null);
  }

  /**
   * <p>
   * Validates and retrieves user directory by ID.
   * </p>
   * <p>
   * Returns user directory with existence validation.
   * Throws ResourceNotFound if user directory does not exist.
   * </p>
   */
  @Override
  public UserDirectory checkAndFind(Long id) {
    return userDirectoryRepo.findById(id)
        .orElseThrow(() -> ResourceNotFound.of(id, "UserDirectory"));
  }

  /**
   * <p>
   * Validates and retrieves user directories by IDs.
   * </p>
   * <p>
   * Returns user directories with existence validation.
   * Validates that all requested user directory IDs exist.
   * </p>
   */
  @Override
  public List<UserDirectory> checkAndFind(Collection<Long> ids) {
    List<UserDirectory> directories = userDirectoryRepo.findAllById(ids);
    ProtocolAssert.assertResourceNotFound(isNotEmpty(directories), ids.iterator().next(),
        "UserDirectory");
    if (ids.size() != directories.size()) {
      for (UserDirectory directory : directories) {
        ProtocolAssert.assertResourceNotFound(ids.contains(directory.getId()), directory.getId(),
            "UserDirectory");
      }
    }
    return directories;
  }

  /**
   * <p>
   * Validates user directory name uniqueness.
   * </p>
   * <p>
   * Ensures user directory name does not already exist.
   * Handles both insert and update scenarios.
   * </p>
   */
  @Override
  public void checkNameExisted(UserDirectory directory) {
    long count = Objects.isNull(directory.getId())
        ? userDirectoryRepo.countByName(directory.getName())
        : userDirectoryRepo.countByNameAndIdNot(directory.getName(), directory.getId());
    if (count > 0) {
      throw ResourceExisted.of(directory.getName(), "UserDirectory");
    }
  }

  /**
   * <p>
   * Validates user directory quota.
   * </p>
   * <p>
   * Checks if adding user directories would exceed quota limits.
   * Throws QuotaException if quota would be exceeded.
   * </p>
   */
  @Override
  public void checkQuota(int incr) {
    long count = userDirectoryRepo.count();
    if (count + incr > MAX_USER_DIRECTORY_NUM) {
      throw QuotaException
          .of(QUOTA_OVER_LIMIT_T2, new Object[]{"UserDirectory", MAX_USER_DIRECTORY_NUM});
    }
  }

}
