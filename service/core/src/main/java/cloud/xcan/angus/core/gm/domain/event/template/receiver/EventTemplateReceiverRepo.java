package cloud.xcan.angus.core.gm.domain.event.template.receiver;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;


@NoRepositoryBean
public interface EventTemplateReceiverRepo extends BaseRepository<EventTemplateReceiver, Long> {

  @Query(value = "SELECT * FROM event_template_receiver WHERE tenant_id= ?1 AND template_id= ?2 limit 1", nativeQuery = true)
  EventTemplateReceiver findByTenantIdAndTemplateId(Long tenantId, Long templateId);

  @Query(value = "SELECT * FROM event_template_receiver WHERE tenant_id= ?1 AND template_id IN ?2 limit 1", nativeQuery = true)
  List<EventTemplateReceiver> findByTenantIdAndTemplateIdIn(Long tenantId,
      Collection<Long> templateIds);

  @Modifying
  @Query(value = "DELETE FROM event_template_receiver WHERE template_id = ?1", nativeQuery = true)
  void deleteAllByTemplateId(Long id);
}
