package cloud.xcan.angus.core.gm.application.query.email;

import cloud.xcan.angus.core.gm.domain.email.server.EmailProtocol;
import cloud.xcan.angus.core.gm.domain.email.server.EmailServer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

public interface EmailServerQuery {

  EmailServer detail(Long id);

  Page<EmailServer> list(Specification<EmailServer> spec, PageRequest pageable);

  void checkEnable(EmailProtocol protocol);

  Boolean checkHealth(EmailProtocol protocol);

  EmailServer checkAndFind(Long id);

  EmailServer findEnabled(EmailProtocol protocol);

  EmailServer findEnabled0(EmailProtocol protocol);

  void checkQuota(int incr);

  void checkAddName(EmailServer emailServer);

  void checkUpdateName(EmailServer emailServer);
}
