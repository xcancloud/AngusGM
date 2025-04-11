package cloud.xcan.angus.core.gm.application.query.tag;

import cloud.xcan.angus.api.commonlink.dept.Dept;
import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.api.commonlink.tag.OrgTagTarget;
import cloud.xcan.angus.api.commonlink.tag.OrgTargetType;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


public interface OrgTagTargetQuery {

  Page<OrgTagTarget> findTagTarget(GenericSpecification<OrgTagTarget> spec,
      PageRequest pageable);

  Page<OrgTagTarget> findTargetTag(GenericSpecification<OrgTagTarget> spec,
      PageRequest pageable);

  void checkTargetAppendTagQuota(Long optTenantId, long incr, Long orgId);

  void checkTargetTagQuota(Long optTenantId, List<OrgTagTarget> tagTargets);

  void checkTargetAppendTagQuota(Long optTenantId, List<OrgTagTarget> tagTargets);

  List<OrgTagTarget> findAllByTarget(OrgTargetType targetType, Long targetId);

  void checkTargetTagQuota(Long optTenantId, long incr, Long userId);

  List<User> checkUserAndDeduplication(Set<OrgTagTarget> newTagTargets,
      List<OrgTagTarget> tagTargets, Long tagId);

  List<Dept> checkDeptAndDeduplication(Set<OrgTagTarget> newTagTargets,
      List<OrgTagTarget> tagTargets, Long tagId);

  List<Group> checkGroupAndDeduplication(Set<OrgTagTarget> newTagTargets,
      List<OrgTagTarget> tagTargets, Long tagId);
}
