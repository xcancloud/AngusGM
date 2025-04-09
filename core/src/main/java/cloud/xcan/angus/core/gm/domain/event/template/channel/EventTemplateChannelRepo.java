package cloud.xcan.angus.core.gm.domain.event.template.channel;

import cloud.xcan.angus.core.gm.domain.event.channel.EventChannelP;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;


@NoRepositoryBean
public interface EventTemplateChannelRepo extends BaseRepository<EventTemplateChannel, Long> {

  List<EventTemplateChannel> findAllByChannelId(Long channelId);

  @Query(value =
      "SELECT c.id id, c.type type, c.name name, c.address address, tc.template_id templateId "
          + "FROM event_channel c, event_template_channel tc WHERE c.id = tc.channel_id AND c.tenant_id = ?1 AND tc.template_id = ?2", nativeQuery = true)
  List<EventChannelP> findChannelByTenantIdAndTemplateId(Long tenantId, Long templateId);

  @Query(value =
      "SELECT c.id id, c.type type, c.name name, c.address address, tc.template_id templateId "
          + "FROM event_channel c, event_template_channel tc WHERE c.id = tc.channel_id AND c.tenant_id = ?1 AND tc.template_id IN ?2", nativeQuery = true)
  List<EventChannelP> findChannelByTenantIdAndTemplateIdIn(Long optTenantId,
      Collection<Long> templateIds);

  @Modifying
  @Query(value = "DELETE FROM event_template_channel WHERE template_id = ?1", nativeQuery = true)
  void deleteAllByTemplateId(Long templateId);

  @Modifying
  @Query(value = "DELETE FROM event_template_channel WHERE template_id = ?1 AND channel_id IN ?2", nativeQuery = true)
  void deleteAllByTemplateIdAndChannelIdIn(Long templateId, Collection<Long> channelIds);

  @Modifying
  @Query(value = "DELETE FROM event_template_channel WHERE template_id = ?1 AND channel_type IN ?2", nativeQuery = true)
  void deleteAllByTemplateIdAndChannelTypeIn(Long id, Collection<String> channelTypes);

}
