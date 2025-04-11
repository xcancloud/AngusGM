package cloud.xcan.angus.core.gm.domain.event.channel;

import cloud.xcan.angus.core.gm.domain.event.ReceiveChannelType;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

@NoRepositoryBean
public interface EventChannelRepo extends BaseRepository<EventChannel, Long> {

  List<EventChannel> findAllByType(ReceiveChannelType channelType);

  Long countByName(String name);

  Long countByNameAndIdNot(String name, Long id);

  Long countByType(ReceiveChannelType channelType);

  @Query(value = "SELECT rs.* FROM event_channel rs INNER JOIN event_template_channel ts "
      + "ON rs.id=ts.channel_id AND ts.tenant_id= :tenantId AND rs.tenant_id= :tenantId AND ts.template_id= :templateId", nativeQuery = true)
  List<EventChannel> findByTemplateId(@Param(value = "tenantId") Long tenantId,
      @Param(value = "templateId") Long templateId);

}
