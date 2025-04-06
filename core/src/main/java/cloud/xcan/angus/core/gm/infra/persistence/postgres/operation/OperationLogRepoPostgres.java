package cloud.xcan.angus.core.gm.infra.persistence.postgres.operation;

import cloud.xcan.angus.core.gm.domain.operation.OperationLogRepo;
import org.springframework.stereotype.Repository;


@Repository("operationLogRepo")
public interface OperationLogRepoPostgres extends OperationLogRepo {

}
