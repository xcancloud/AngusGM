package cloud.xcan.angus.core.gm.infra.search;

import cloud.xcan.angus.core.gm.domain.message.MessageInfo;
import cloud.xcan.angus.core.gm.domain.message.MessageInfoSearchRepo;
import cloud.xcan.angus.core.jpa.repository.SimpleSearchRepository;
import org.springframework.stereotype.Repository;


@Repository
public class MessageSearchRepoMySql extends SimpleSearchRepository<MessageInfo> implements
    MessageInfoSearchRepo {

}
