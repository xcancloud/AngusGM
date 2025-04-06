package cloud.xcan.angus.core.gm.infra.persistence.mysql.sms;

import cloud.xcan.angus.core.gm.domain.channel.SmsChannelRepo;
import org.springframework.stereotype.Repository;


@Repository("smsChannelRepo")
public interface SmsChannelRepoMysql extends SmsChannelRepo {

}
