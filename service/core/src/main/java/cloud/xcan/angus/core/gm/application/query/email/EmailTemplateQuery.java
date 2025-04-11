package cloud.xcan.angus.core.gm.application.query.email;

import cloud.xcan.angus.core.gm.domain.email.template.EmailTemplate;
import cloud.xcan.angus.spec.locale.SupportedLanguage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

public interface EmailTemplateQuery {

  EmailTemplate detail(Long id);

  Page<EmailTemplate> find(Specification<EmailTemplate> spec, PageRequest pageable);

  EmailTemplate check(SupportedLanguage language, String code);

}
