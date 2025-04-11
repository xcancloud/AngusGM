package cloud.xcan.angus.core.gm.domain.event.template.channel;

import cloud.xcan.angus.core.gm.domain.event.ReceiveChannelType;
import cloud.xcan.angus.core.jpa.multitenancy.TenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "event_template_channel")
@DynamicUpdate
@Setter
@Getter
@Accessors(chain = true)
public class EventTemplateChannel extends TenantEntity<EventTemplateChannel, Long> {

  @Id
  private Long id;

  @Column(name = "template_id")
  private Long templateId;

  @Column(name = "channel_id")
  private Long channelId;

  @Column(name = "channel_type")
  @Enumerated(EnumType.STRING)
  private ReceiveChannelType channelType;

  @Override
  public Long identity() {
    return this.id;
  }
}
