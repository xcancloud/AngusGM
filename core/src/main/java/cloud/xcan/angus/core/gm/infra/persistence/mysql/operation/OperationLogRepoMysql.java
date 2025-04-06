package cloud.xcan.angus.core.gm.infra.persistence.mysql.operation;

import cloud.xcan.angus.core.gm.domain.operation.OperationLogRepo;
import org.springframework.stereotype.Repository;


@Repository("operationLogRepo")
public interface OperationLogRepoMysql extends OperationLogRepo {

}
