package cloud.xcan.angus.core.gm.infra.persistence.postgres.wpush;

import cloud.xcan.angus.api.commonlink.mcenter.MessageCenterOnlineRepo;
import org.springframework.stereotype.Repository;

@Repository("messageCenterOnlineRepo")
public interface MessageCenterOnlineRepoPostgres extends MessageCenterOnlineRepo {

}
