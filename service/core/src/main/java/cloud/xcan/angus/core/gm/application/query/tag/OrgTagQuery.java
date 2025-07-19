package cloud.xcan.angus.core.gm.application.query.tag;

import cloud.xcan.angus.api.commonlink.tag.OrgTag;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


public interface OrgTagQuery {

  OrgTag detail(Long id);

  Page<OrgTag> list(GenericSpecification<OrgTag> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);

  OrgTag checkAndFind(Long id);

  List<OrgTag> checkAndFind(Collection<Long> ids);

  void checkNameInParam(List<OrgTag> tags);

  void checkAddTagName(Long tenantId, List<OrgTag> tags);

  void checkUpdateTagName(Long tenantId, List<OrgTag> tags);

  void checkQuota(Long tenantId, long incr);

}
