package cloud.xcan.angus.core.gm.application.query.interfaces.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.interfaces.InterfaceQuery;
import cloud.xcan.angus.core.gm.application.query.service.ServiceQuery;
import cloud.xcan.angus.core.gm.domain.interfaces.Interface;
import cloud.xcan.angus.core.gm.domain.interfaces.InterfaceRepo;
import cloud.xcan.angus.core.gm.domain.interfaces.enums.InterfaceStatus;
import cloud.xcan.angus.core.gm.domain.service.Service;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.remote.search.SearchOperation;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * Implementation of interface query service
 */
@Biz
public class InterfaceQueryImpl implements InterfaceQuery {

  @Resource
  private InterfaceRepo interfaceRepo;

  @Resource
  private ServiceQuery serviceQuery;

    @Override
  public Interface findAndCheck(Long id) {
    return new BizTemplate<Interface>() {
      @Override
      protected Interface process() {
        return interfaceRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of("接口未找到", new Object[]{}));
      }
    }.execute();
    }

    @Override
  public Page<Interface> find(GenericSpecification<Interface> spec, PageRequest pageable,
                             boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<Interface>>() {
      @Override
      protected Page<Interface> process() {
        Page<Interface> page = interfaceRepo.findAll(spec, pageable);
        
        // Set associated data if needed
        if (page.hasContent()) {
          setServiceNames(page.getContent());
        }
        
        return page;
      }
    }.execute();
  }

  @Override
  public Page<Interface> findByServiceId(Long serviceId, GenericSpecification<Interface> spec,
                                         PageRequest pageable, boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<Interface>>() {
      @Override
      protected Page<Interface> process() {
        // Add serviceId filter to specification
        Set<SearchCriteria> filters = new HashSet<>(spec.getCriteria());
        filters.add(new SearchCriteria("serviceId", serviceId, SearchOperation.EQUAL));
        GenericSpecification<Interface> finalSpec = new GenericSpecification<>(filters);
        
        // Use unified find method
        return find(finalSpec, pageable, fullTextSearch, match);
      }
    }.execute();
  }

  @Override
  public Page<Interface> findByTag(String tag, GenericSpecification<Interface> spec,
                                  PageRequest pageable, boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<Interface>>() {
      @Override
      protected Page<Interface> process() {
        // Add tag filter to specification
        Set<SearchCriteria> filters = new HashSet<>(spec.getCriteria());
        filters.add(new SearchCriteria("tags", tag, SearchOperation.MATCH));
        GenericSpecification<Interface> finalSpec = new GenericSpecification<>(filters);
        
        // Use unified find method
        return find(finalSpec, pageable, fullTextSearch, match);
      }
    }.execute();
  }

  @Override
  public List<Interface> findAllServices() {
    return new BizTemplate<List<Interface>>() {
      @Override
      protected List<Interface> process() {
        // This should return services, not interfaces
        // For now, return empty list as this needs service query
        return new ArrayList<>();
      }
    }.execute();
  }

  @Override
  public List<String> findAllTags() {
    return new BizTemplate<List<String>>() {
      @Override
      protected List<String> process() {
        List<Interface> allInterfaces = interfaceRepo.findAll();
        Set<String> tagSet = new HashSet<>();
        
        for (Interface inter : allInterfaces) {
          if (inter.getTags() != null) {
            tagSet.addAll(inter.getTags());
          }
        }
        
        return new ArrayList<>(tagSet);
      }
    }.execute();
    }

    @Override
    public long countTotal() {
        return interfaceRepo.count();
    }

    @Override
    public long countByStatus(InterfaceStatus status) {
        return interfaceRepo.countByStatus(status);
    }

  @Override
  public long countByServiceId(Long serviceId) {
    return interfaceRepo.countByServiceId(serviceId);
  }

  /**
   * Set service names for interfaces
   */
  private void setServiceNames(List<Interface> interfaces) {
    // Batch load service names
    for (Interface inter : interfaces) {
      Optional<Service> serviceOpt = serviceQuery.findById(inter.getServiceId().toString());
      if (serviceOpt.isPresent()) {
        inter.setServiceName(serviceOpt.get().getName());
      }
    }
    }
}
