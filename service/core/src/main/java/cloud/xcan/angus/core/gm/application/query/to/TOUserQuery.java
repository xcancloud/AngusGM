package cloud.xcan.angus.core.gm.application.query.to;

import cloud.xcan.angus.api.commonlink.to.TOUser;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface TOUserQuery {

  TOUser detail(Long userId);

  Page<TOUser> list(GenericSpecification<TOUser> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);

  TOUser checkAndFind(Long userId);

  List<TOUser> checkAndFind(Collection<Long> userIds);

  void checkExists(Collection<Long> userIds);
}
