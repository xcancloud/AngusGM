package cloud.xcan.angus.core.gm.domain.operation;

import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface OperationLogSearchRepo extends CustomBaseRepository<OperationLog> {

}
