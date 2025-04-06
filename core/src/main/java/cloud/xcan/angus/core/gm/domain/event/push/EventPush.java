package cloud.xcan.angus.core.gm.domain.event.push;

import cloud.xcan.angus.api.enums.EventType;
import cloud.xcan.angus.core.gm.domain.event.ReceiveChannelType;
import cloud.xcan.angus.spec.experimental.EntitySupport;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Table(name = "event_push")
@Setter
@Getter
@Accessors(chain = true)
@EntityListeners({AuditingEntityListener.class})
public class EventPush extends EntitySupport<EventPush, Long> {

  @Id
  private Long id;

  @Column(name = "event_id")
  private Long eventId;

  /**
   * {@link EventType#NOTICE} type events are not saved
   */
  @Enumerated(EnumType.STRING)
  private EventType type;

  private String name;

  private String content;

  private String secret;

  @Column(name = "channel_type")
  @Enumerated(EnumType.STRING)
  private ReceiveChannelType channelType;

  private String address;

  private Boolean push;

  @Column(name = "push_msg")
  private String pushMsg;

  @Column(name = "retry_times")
  private Long retryTimes;

  @Override
  public Long identity() {
    return this.id;
  }

  @Override
  public String toString() {
    return "EventPush{" +
        "id=" + id +
        ", eventId=" + eventId +
        ", type=" + type +
        ", name='" + name + '\'' +
        ", content='" + content + '\'' +
        ", secret='" + secret + '\'' +
        ", channelType=" + channelType +
        ", address='" + address + '\'' +
        ", push=" + push +
        ", pushFailMessage='" + pushMsg + '\'' +
        ", retryTimes=" + retryTimes +
        '}';
  }
}
