package cloud.xcan.angus.core.gm.application.query.group;

import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GroupQuery {

  Group detail(Long id);

  Page<Group> find(GenericSpecification<Group> spec, Pageable pageable);

  List<Group> findByIdIn(HashSet<Long> ids);

  Group checkAndFind(Long id);

  List<Group> checkAndFind(Collection<Long> ids);

  Group checkValidAndFind(Long id);

  List<Group> checkValidAndFind(Collection<Long> ids);

  void checkAddGroupCode(Long tenantId, List<Group> groups);

  void checkUpdateDeptCode(Long tenantId, List<Group> groups);

  void checkQuota(Long optTenantId, long incr);

  void setUserNum(List<Group> groups);

}
