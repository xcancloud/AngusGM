package cloud.xcan.angus.core.gm.infra.persistence.postgres.wpush;

import cloud.xcan.angus.core.gm.domain.message.center.MessageCenterOnlineRepo;
import org.springframework.stereotype.Repository;

@Repository("messageCenterOnlineRepo")
public interface MessageCenterOnlineRepoPostgres extends MessageCenterOnlineRepo {

}
