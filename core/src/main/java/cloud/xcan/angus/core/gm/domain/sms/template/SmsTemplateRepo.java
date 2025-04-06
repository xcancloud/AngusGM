package cloud.xcan.angus.core.gm.domain.sms.template;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.spec.locale.SupportedLanguage;
import java.util.List;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SmsTemplateRepo extends BaseRepository<SmsTemplate, Long> {

  SmsTemplate findByCodeAndLanguageAndChannelId(String code, SupportedLanguage language,
      Long channelId);

  List<SmsTemplate> findByChannelId(Long channelId);

  Long countByChannelId(Long channelId);
}
