package cloud.xcan.angus.core.gm.application.query.tag;

import cloud.xcan.angus.api.commonlink.app.tag.WebTag;
import cloud.xcan.angus.api.commonlink.app.tag.WebTagTarget;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface WebTagTargetQuery {

  Page<WebTagTarget> findTagTarget(GenericSpecification<WebTagTarget> spec,
      PageRequest pageable);

  Page<WebTagTarget> findTargetTag(GenericSpecification<WebTagTarget> spec,
      PageRequest pageable);

  List<WebTag> checkAndFind(Collection<Long> tagIds);

  Map<Long, List<WebTagTarget>> findTargetTagByTargetId(Collection<Long> targetIds);
}
