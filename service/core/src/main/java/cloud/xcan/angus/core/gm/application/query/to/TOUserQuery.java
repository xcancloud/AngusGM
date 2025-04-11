package cloud.xcan.angus.core.gm.application.query.to;

import cloud.xcan.angus.api.commonlink.to.TOUser;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface TOUserQuery {

  TOUser detail(Long userId);

  Page<TOUser> find(Specification<TOUser> spec, Pageable pageable);

  TOUser checkAndFind(Long userId);

  List<TOUser> checkAndFind(Collection<Long> userIds);

  void checkExists(Collection<Long> userIds);
}
