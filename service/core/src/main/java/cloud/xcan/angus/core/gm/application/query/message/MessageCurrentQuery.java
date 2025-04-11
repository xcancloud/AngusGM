package cloud.xcan.angus.core.gm.application.query.message;

import cloud.xcan.angus.core.gm.domain.message.MessageSent;
import cloud.xcan.angus.core.gm.interfaces.message.facade.vo.MessageStatusCountVo;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;


public interface MessageCurrentQuery {

  Page<MessageSent> list(Specification<MessageSent> spec, Pageable pageable);

  MessageSent detail(Long id);

  List<MessageStatusCountVo> statusCount(Long userid);
}
