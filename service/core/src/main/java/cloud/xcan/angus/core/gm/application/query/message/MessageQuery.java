package cloud.xcan.angus.core.gm.application.query.message;

import cloud.xcan.angus.core.gm.domain.message.Message;
import cloud.xcan.angus.core.gm.domain.message.MessageInfo;
import cloud.xcan.angus.core.gm.domain.message.MessageReceiveType;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


public interface MessageQuery {

  Message detail(Long id);

  Page<MessageInfo> find(GenericSpecification<MessageInfo> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);

  List<Message> getPendingMessage(MessageReceiveType receiveType, int size);

}
