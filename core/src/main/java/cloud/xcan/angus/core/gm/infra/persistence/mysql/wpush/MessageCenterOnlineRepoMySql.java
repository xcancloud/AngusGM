package cloud.xcan.angus.core.gm.infra.persistence.mysql.wpush;

import cloud.xcan.angus.api.commonlink.mcenter.MessageCenterOnlineRepo;
import org.springframework.stereotype.Repository;

@Repository("messageCenterOnlineRepo")
public interface MessageCenterOnlineRepoMySql extends MessageCenterOnlineRepo {

}
