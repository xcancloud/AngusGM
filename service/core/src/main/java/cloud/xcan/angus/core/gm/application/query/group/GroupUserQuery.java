package cloud.xcan.angus.core.gm.application.query.group;

import cloud.xcan.angus.api.commonlink.user.group.GroupUser;
import cloud.xcan.angus.api.commonlink.user.group.GroupUserNum;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface GroupUserQuery {

  Page<GroupUser> findUserGroup(GenericSpecification<GroupUser> spec, Pageable pageable);

  Page<GroupUser> findGroupUser(GenericSpecification<GroupUser> spec, Pageable pageable);

  List<GroupUser> findAllByUserId(Long userId);

  //statistics dept user count
  List<GroupUserNum> userCount(Set<Long> groupIds);

  void checkUserGroupReplaceQuota(Long optTenantId, long incr, Long userId);

  void checkGroupUserAppendQuota(Long optTenantId, long incr, Long groupId);

  void checkUserGroupAppendQuota(Long optTenantId, long incr, Long userId);
}
