package cloud.xcan.angus.core.gm.application.query.message;

import cloud.xcan.angus.core.gm.domain.message.center.MessageCenterOnline;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface MessageCenterOnlineQuery {

  MessageCenterOnline detail(Long id);

  Page<MessageCenterOnline> list(GenericSpecification<MessageCenterOnline> spec,
      PageRequest pageable, boolean fullTextSearch, String[] match);

}
