package cloud.xcan.angus.core.gm.domain.channel;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface SmsChannelRepo extends BaseRepository<SmsChannel, Long> {

  List<SmsChannel> findByEnabled(Boolean enable);

  @Query(value = "SELECT * FROM sms_channel LIMIT 1", nativeQuery = true)
  SmsChannel findFirst();

  @Modifying
  void deleteByIdIn(Collection<Long> ids);
}
