package cloud.xcan.angus.core.gm.application.query.service;

import cloud.xcan.angus.api.commonlink.api.Api;
import cloud.xcan.angus.api.commonlink.service.Service;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface ServiceQuery {

  Service detail(Long id);

  Page<Service> list(GenericSpecification<Service> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);

  List<Api> apiList(Long id);

  List<Service> resourceList(String serviceCode, Boolean auth);

  Service resourceApiList(String serviceCode, String resourceName, Boolean auth);

  Service checkAndFind(Long id);

  List<Service> checkAndFind(Collection<String> serviceCodes);

  List<Service> checkAndFind(Collection<Long> ids, boolean checkEnabled);

  void checkAddServiceCode(Service service);

  void checkUpdateServiceCode(Service service);

  void setApiNum(List<Service> services);

}
