package cloud.xcan.angus.core.gm.application.query.api;

import cloud.xcan.angus.api.commonlink.api.Api;
import cloud.xcan.angus.api.commonlink.service.ServiceResource;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface ApiQuery {

  Api detail(Long id);

  Page<Api> list(Specification<Api> spec, Pageable pageable);

  List<Api> findAllById(Collection<Long> apiIds);

  void setServiceInfo(List<Api> apis);

  Api checkAndFind(Long id);

  List<Api> checkAndFind(Collection<Long> ids, boolean checkEnabled);

  void checkByServiceId(Long serviceId, HashSet<Long> apiIds);

  Map<String, List<ServiceResource>> checkAndFindResource(Collection<String> resources);

}
