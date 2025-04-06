package cloud.xcan.angus.core.gm.application.query.service;

import cloud.xcan.angus.api.commonlink.service.Service;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ServiceSearch {

  Page<Service> search(Set<SearchCriteria> criteria, Pageable pageable,
      Class<Service> serviceClass, String... matches);
}
