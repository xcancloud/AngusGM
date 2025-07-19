package cloud.xcan.angus.core.gm.application.query.to;

import cloud.xcan.angus.api.commonlink.to.TORole;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


public interface TORoleQuery {

  TORole detail(String code);

  Page<TORole> list(GenericSpecification<TORole> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);

  TORole find0(Long policyId);

  List<TORole> findAllById(Set<Long> ids);

  TORole checkAndFind(Long appId, boolean checkEnabled);

  List<TORole> checkAndFind(Collection<Long> policyIds, boolean checkEnabled);

  void checkDuplicateInParam(List<TORole> apps);

  void checkUniqueCodeAndName(List<TORole> policies);

}
