package cloud.xcan.angus.core.gm.application.query.message;

import cloud.xcan.angus.core.gm.domain.message.center.MessageCenterOnline;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface MessageCenterOnlineQuery {

  Page<MessageCenterOnline> find(Specification<MessageCenterOnline> specification,
      Pageable pageable);

  MessageCenterOnline find(Long id);

}
