package cloud.xcan.angus.core.gm.domain.service;

import cloud.xcan.angus.api.commonlink.service.Service;
import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ServiceSearchRepo extends CustomBaseRepository<Service> {

}
