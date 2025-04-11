package cloud.xcan.angus.core.gm.application.query.tag;

import cloud.xcan.angus.api.commonlink.tag.OrgTag;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;


public interface OrgTagQuery {

  OrgTag detail(Long id);

  Page<OrgTag> list(Specification<OrgTag> spec, Pageable pageable);

  OrgTag checkAndFind(Long id);

  List<OrgTag> checkAndFind(Collection<Long> ids);

  void checkNameInParam(List<OrgTag> tags);

  void checkAddTagName(Long tenantId, List<OrgTag> tags);

  void checkUpdateTagName(Long tenantId, List<OrgTag> tags);

  void checkQuota(Long tenantId, long incr);
}
