package cloud.xcan.angus.core.gm.domain.event.channel;

import cloud.xcan.angus.core.gm.domain.event.ReceiveChannelType;
import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import cloud.xcan.angus.spec.experimental.Resources;
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
@Table(name = "event_channel")
@Setter
@Getter
@Accessors(chain = true)
@DynamicUpdate
public class EventChannel extends TenantAuditingEntity<EventChannel, Long>
    implements Resources<Long> {

  @Id
  private Long id;

  @Enumerated(EnumType.STRING)
  private ReceiveChannelType type;

  @Column
  private String name;

  @Column
  private String address;

  @Override
  public Long identity() {
    return this.id;
  }
}
