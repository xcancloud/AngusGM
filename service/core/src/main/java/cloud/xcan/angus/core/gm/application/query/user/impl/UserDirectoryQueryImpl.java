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


@Biz
public class UserDirectoryQueryImpl implements UserDirectoryQuery {

  @Resource
  private UserDirectoryRepo userDirectoryRepo;

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

  @Override
  public UserDirectory find(Long id) {
    return userDirectoryRepo.findById(id).orElse(null);
  }

  @Override
  public UserDirectory checkAndFind(Long id) {
    return userDirectoryRepo.findById(id)
        .orElseThrow(() -> ResourceNotFound.of(id, "UserDirectory"));
  }

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

  @Override
  public void checkNameExisted(UserDirectory directory) {
    long count = Objects.isNull(directory.getId())
        ? userDirectoryRepo.countByName(directory.getName())
        : userDirectoryRepo.countByNameAndIdNot(directory.getName(), directory.getId());
    if (count > 0) {
      throw ResourceExisted.of(directory.getName(), "UserDirectory");
    }
  }

  @Override
  public void checkQuota(int incr) {
    long count = userDirectoryRepo.count();
    if (count + incr > MAX_USER_DIRECTORY_NUM) {
      throw QuotaException
          .of(QUOTA_OVER_LIMIT_T2, new Object[]{"UserDirectory", MAX_USER_DIRECTORY_NUM});
    }
  }

}
