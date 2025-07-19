package cloud.xcan.angus.core.gm.application.query.api;

import cloud.xcan.angus.api.commonlink.api.Api;
import cloud.xcan.angus.api.commonlink.service.ServiceResource;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface ApiQuery {

  Api detail(Long id);

  Page<Api> list(GenericSpecification<Api> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);

  List<Api> findAllById(Collection<Long> apiIds);

  void setServiceInfo(List<Api> apis);

  Api checkAndFind(Long id);

  List<Api> checkAndFind(Collection<Long> ids, boolean checkEnabled);

  void checkByServiceId(Long serviceId, HashSet<Long> apiIds);

  Map<String, List<ServiceResource>> checkAndFindResource(Collection<String> resources);

}
