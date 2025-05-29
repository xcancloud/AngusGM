package cloud.xcan.angus.core.gm.domain.message.center;

import cloud.xcan.angus.core.jpa.multitenancy.TenantEntity;
import cloud.xcan.angus.core.jpa.multitenancy.TenantListener;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "message_center_online")
@Setter
@Getter
@Accessors(chain = true)
@EntityListeners({TenantListener.class})
public class MessageCenterOnline extends TenantEntity<MessageCenterOnline, Long> {

  @Id
  private Long id;

  private Long userId;

  private String fullName;

  private String userAgent;

  private String deviceId;

  private String remoteAddress;

  private Boolean online;

  private LocalDateTime onlineDate;

  private LocalDateTime offlineDate;

  @Override
  public Long identity() {
    return this.userId;
  }
}
