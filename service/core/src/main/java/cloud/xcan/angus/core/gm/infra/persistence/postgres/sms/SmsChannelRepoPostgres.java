package cloud.xcan.angus.core.gm.infra.persistence.postgres.sms;

import cloud.xcan.angus.core.gm.domain.sms.channel.SmsChannelRepo;
import org.springframework.stereotype.Repository;


@Repository("smsChannelRepo")
public interface SmsChannelRepoPostgres extends SmsChannelRepo {

}
