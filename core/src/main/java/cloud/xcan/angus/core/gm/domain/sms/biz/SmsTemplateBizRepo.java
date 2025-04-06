package cloud.xcan.angus.core.gm.domain.sms.biz;


import cloud.xcan.angus.api.commonlink.sms.SmsBizKey;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import org.springframework.data.repository.NoRepositoryBean;


@NoRepositoryBean
public interface SmsTemplateBizRepo extends BaseRepository<SmsTemplateBiz, Long> {

  SmsTemplateBiz findByBizKey(SmsBizKey bizKey);

}
