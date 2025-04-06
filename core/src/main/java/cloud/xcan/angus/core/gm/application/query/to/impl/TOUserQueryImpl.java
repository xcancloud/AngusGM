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
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@Biz
public class TOUserQueryImpl implements TOUserQuery {

  @Resource
  private TOUserRepo toUserRepo;

  @Resource
  private TORoleRepo toRoleRepo;

  @Resource
  private TORoleUserRepo toRoleUserRepo;

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

  @Override
  public Page<TOUser> find(Specification<TOUser> spec, Pageable pageable) {
    return new BizTemplate<Page<TOUser>>() {

      @Override
      protected Page<TOUser> process() {
        return toUserRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public TOUser checkAndFind(Long userId) {
    return checkAndFind(List.of(userId)).get(0);
  }

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

  @Override
  public void checkExists(Collection<Long> userIds) {
    if (isEmpty(userIds)) {
      return;
    }
    List<TOUser> toUsers = toUserRepo.findAllByUserIdIn(userIds);
    assertResourceExisted(toUsers, userIds.iterator().next(), "TOUser");
  }

  private void setUserPolicy(TOUser toUser) {
    List<TORoleUser> tpu = toRoleUserRepo.findAllByUserIdIn(
        Collections.singletonList(toUser.getUserId()));
    List<TORole> topPolices = toRoleRepo
        .findAllById(tpu.stream().map(TORoleUser::getToRoleId).collect(Collectors.toSet()));
    toUser.setToPolicies(topPolices);
  }
}
