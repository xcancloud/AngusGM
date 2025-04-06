package cloud.xcan.angus.core.gm.domain.event.template.receiver;

import cloud.xcan.angus.api.enums.NoticeType;
import cloud.xcan.angus.api.enums.ReceiverType;
import cloud.xcan.angus.core.jpa.multitenancy.TenantEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "event_template_receiver")
@Setter
@Getter
@Accessors(chain = true)
@DynamicUpdate
public class EventTemplateReceiver extends TenantEntity<EventTemplateReceiver, Long> {

  @Id
  private Long id;

  @Column(name = "template_id")
  private Long templateId;

  /**
   * At least one of receiverTypes and receiverIds is required, notification will also be sent when
   * both exist.
   */
  @Type(JsonType.class)
  @Column(name = "receiver_type_data", columnDefinition = "json")
  private Set<ReceiverType> receiverTypes;

  /**
   * At least one of receiverTypes and receiverIds is required, notification will also be sent when
   * both exist.
   */
  @Type(JsonType.class)
  @Column(name = "receiver_ids_data", columnDefinition = "json")
  private Set<Long> receiverIds;

  @Type(JsonType.class)
  @Column(name = "notice_type_data", columnDefinition = "json")
  private Set<NoticeType> noticeTypes;

  @Override
  public Long identity() {
    return this.id;
  }
}
