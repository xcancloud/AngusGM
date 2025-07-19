package cloud.xcan.angus.core.gm.application.query.group;

import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface GroupQuery {

  Group detail(Long id);

  Page<Group> list(GenericSpecification<Group> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);

  List<Group> findByIdIn(Collection<Long> ids);

  Group checkAndFind(Long id);

  List<Group> checkAndFind(Collection<Long> ids);

  Group checkValidAndFind(Long id);

  List<Group> checkValidAndFind(Collection<Long> ids);

  void checkAddGroupCode(Long tenantId, List<Group> groups);

  void checkUpdateDeptCode(Long tenantId, List<Group> groups);

  void checkQuota(Long optTenantId, long incr);

  void setUserNum(List<Group> groups);

}
