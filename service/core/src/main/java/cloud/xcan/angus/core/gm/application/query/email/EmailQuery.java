package cloud.xcan.angus.core.gm.application.query.email;

import cloud.xcan.angus.core.gm.domain.email.Email;
import cloud.xcan.angus.core.gm.domain.email.template.EmailTemplate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface EmailQuery {

  Page<Email> find(Specification<Email> spec, Pageable pageable);

  Email checkAndFind(Long id);

  EmailTemplate checkAndFindTemplate(Email email);

  List<Email> findTenantEmailInPending(int count);

  List<Email> findPlatformEmailInPending(int count);

  void checkAttachmentQuota(Email email);

}
