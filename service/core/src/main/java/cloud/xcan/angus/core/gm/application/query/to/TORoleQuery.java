package cloud.xcan.angus.core.gm.application.query.to;

import cloud.xcan.angus.api.commonlink.to.TORole;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;


public interface TORoleQuery {

  TORole detail(String code);

  Page<TORole> list(Specification<TORole> spec, PageRequest pageable);

  TORole find0(Long policyId);

  List<TORole> findAllById(Set<Long> ids);

  TORole checkAndFind(Long appId, boolean checkEnabled);

  List<TORole> checkAndFind(Collection<Long> policyIds, boolean checkEnabled);

  void checkDuplicateInParam(List<TORole> apps);

  void checkUniqueCodeAndName(List<TORole> policies);
}
