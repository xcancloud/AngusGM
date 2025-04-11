package cloud.xcan.angus.core.gm.application.query.message;

import cloud.xcan.angus.core.gm.domain.message.Message;
import cloud.xcan.angus.core.gm.domain.message.MessageInfo;
import cloud.xcan.angus.core.gm.domain.message.MessageReceiveType;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;


public interface MessageQuery {

  Page<MessageInfo> find(Specification<MessageInfo> spec, Pageable pageable);

  Message findById(Long id);

  List<Message> getPendingMessage(MessageReceiveType receiveType, int size);
}
