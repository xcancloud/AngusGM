package cloud.xcan.angus.core.gm.application.query.tag;

import cloud.xcan.angus.api.commonlink.app.func.AppFunc;
import cloud.xcan.angus.api.commonlink.app.tag.WebTagTarget;
import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface WebTagTargetQuery {

  Page<WebTagTarget> findTagTarget(GenericSpecification<WebTagTarget> spec,
      PageRequest pageable);

  Page<WebTagTarget> findTargetTag(GenericSpecification<WebTagTarget> spec,
      PageRequest pageable);

  List<App> checkAppAndDeduplication(Set<WebTagTarget> newTagTargets,
      List<WebTagTarget> tagTargets, Long tagId);

  List<AppFunc> checkAppFuncAndDeduplication(Set<WebTagTarget> newTagTargets,
      List<WebTagTarget> tagTargets, Long tagId);

  Map<Long, List<WebTagTarget>> findTargetTagByTargetId(Collection<Long> targetIds);
}
