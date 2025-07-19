package cloud.xcan.angus.core.gm.application.query.tag;

import cloud.xcan.angus.api.commonlink.app.tag.WebTag;
import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


public interface WebTagQuery {

  WebTag detail(Long id);

  Page<WebTag> list(GenericSpecification<WebTag> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);

  List<WebTag> findAllById(Collection<Long> tagIdSet);

  List<WebTag> findByTargetId(Long targetId);

  Map<Long, List<WebTag>> findByTargetIdIn(Collection<Long> targetIds);

  WebTag checkAndFind(Long id);

  List<WebTag> checkAndFind(Collection<Long> tagIds);

  void checkAddTagNameExist(List<String> names);

  void checkUpdateTagNameExist(List<WebTag> webTags);

  void checkRepeatedTagNameInParams(List<String> names);

  void setAppTags(Collection<Long> appIds);

  void setAppTags(List<App> apps);

}
