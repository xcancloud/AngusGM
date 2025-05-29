package cloud.xcan.angus.core.gm.infra.search;

import cloud.xcan.angus.core.gm.domain.message.center.MessageCenterOnline;
import cloud.xcan.angus.core.gm.domain.message.center.MessageCenterOnlineSearchRepo;
import cloud.xcan.angus.core.jpa.repository.SimpleSearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public class MessageCenterOnlineSearchMysql extends SimpleSearchRepository<MessageCenterOnline>
    implements MessageCenterOnlineSearchRepo {

}
