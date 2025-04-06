package cloud.xcan.angus.core.gm.domain.email.template;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.spec.locale.SupportedLanguage;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface EmailTemplateRepo extends BaseRepository<EmailTemplate, Long> {

  EmailTemplate findByLanguageAndCode(SupportedLanguage language, String code);

}
