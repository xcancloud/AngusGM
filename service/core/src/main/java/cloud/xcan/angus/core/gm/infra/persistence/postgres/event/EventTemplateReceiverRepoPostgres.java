package cloud.xcan.angus.core.gm.infra.persistence.postgres.event;

import cloud.xcan.angus.core.gm.domain.event.template.receiver.EventTemplateReceiverRepo;
import org.springframework.stereotype.Repository;


@Repository
public interface EventTemplateReceiverRepoPostgres extends EventTemplateReceiverRepo {

}
