package cloud.xcan.angus.core.gm.domain.email.biz;

import cloud.xcan.angus.api.commonlink.email.EmailBizKey;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface EmailTemplateBizRepo extends BaseRepository<EmailTemplateBiz, String> {

  EmailTemplateBiz findByBizKey(EmailBizKey emailBizKey);

}
