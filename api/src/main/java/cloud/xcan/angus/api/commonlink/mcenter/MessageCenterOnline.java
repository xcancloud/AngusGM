package cloud.xcan.angus.api.commonlink.mcenter;

import cloud.xcan.angus.core.jpa.multitenancy.TenantEntity;
import cloud.xcan.angus.core.jpa.multitenancy.TenantListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "mcenter_online")
@Setter
@Getter
@Accessors(chain = true)
@EntityListeners({TenantListener.class})
public class MessageCenterOnline extends TenantEntity<MessageCenterOnline, Long> {

  @Id
  @Column(name = "user_id")
  private Long userId;

  private String fullname;

  @Column(name = "user_agent")
  private String userAgent;

  @Column(name = "device_id")
  private String deviceId;

  @Column(name = "remote_address")
  private String remoteAddress;

  private Boolean online;

  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Column(name = "online_date", columnDefinition = "TIMESTAMP")
  private LocalDateTime onlineDate;

  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Column(name = "offline_date", columnDefinition = "TIMESTAMP")
  private LocalDateTime offlineDate;

  @Override
  public Long identity() {
    return this.userId;
  }
}
