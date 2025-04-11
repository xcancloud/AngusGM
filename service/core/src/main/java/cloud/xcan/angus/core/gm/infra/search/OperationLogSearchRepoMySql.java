package cloud.xcan.angus.core.gm.infra.search;

import cloud.xcan.angus.core.gm.domain.operation.OperationLog;
import cloud.xcan.angus.core.gm.domain.operation.OperationLogSearchRepo;
import cloud.xcan.angus.core.jpa.repository.SimpleSearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public class OperationLogSearchRepoMySql extends SimpleSearchRepository<OperationLog>
    implements OperationLogSearchRepo {

}
