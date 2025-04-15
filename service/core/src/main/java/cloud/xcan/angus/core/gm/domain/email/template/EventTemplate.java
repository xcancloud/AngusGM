package cloud.xcan.angus.core.gm.domain.email.template;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.enums.EventType;
import cloud.xcan.angus.core.gm.domain.event.ReceiveChannelType;
import cloud.xcan.angus.core.gm.domain.event.channel.EventChannelP;
import cloud.xcan.angus.core.gm.domain.event.template.receiver.EventTemplateReceiver;
import cloud.xcan.angus.core.jpa.auditor.AuditingEntity;
import cloud.xcan.angus.spec.experimental.Resources;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "event_template")
@Setter
@Getter
@Accessors(chain = true)
@DynamicUpdate
public class EventTemplate extends AuditingEntity<EventTemplate, Long> implements Resources<Long> {

  @Id
  private Long id;

  @Column(name = "event_code")
  private String eventCode;

  @Column(name = "event_name")
  private String eventName;

  @Column(name = "event_type")
  @Enumerated(EnumType.STRING)
  private EventType eventType;

  @Column(name = "e_key")
  private String eKey;

  @Column(name = "target_type")
  private String targetType;

  @Column(name = "app_code")
  private String appCode;

  private Boolean private0;

  /**
   * Allowed tenant configured channel types.
   */
  @Type(JsonType.class)
  @Column(name = "allowed_channel_type_data", columnDefinition = "json")
  private Set<ReceiveChannelType> allowedChannelTypes;

  @Transient
  private EventTemplateReceiver receivers;
  @Transient
  private List<EventChannelP> channels;

  public boolean hasReceiverIds() {
    return isNotEmpty(receivers) && isNotEmpty(receivers.getReceiverIds());
  }

  @Override
  public Long identity() {
    return this.id;
  }

  @Override
  public String getName() {
    return eventName;
  }
}
