package cloud.xcan.angus.core.gm.domain.event;

import static cloud.xcan.angus.api.commonlink.EventConstant.EVENT_DUPLICATE_MAX_KEY_LENGTH;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.enums.EventType;
import cloud.xcan.angus.core.event.source.EventContent;
import cloud.xcan.angus.core.gm.domain.event.push.EventPushStatus;
import cloud.xcan.angus.core.jpa.multitenancy.TenantEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;


@Setter
@Getter
@Entity
@Table(name = "event")
@Accessors(chain = true)
@ToString(exclude = {"sourceData"})
@EntityListeners({AuditingEntityListener.class})
public class Event extends TenantEntity<Event, Long> {

  @Id
  private Long id;

  private String code;

  /**
   * Non-template event name is null.
   */
  private String name;

  @Enumerated(EnumType.STRING)
  private EventType type;

  @Column(name = "e_key")
  private String eKey;

  @Column
  private String description;

  @Column(name = "tenant_name")
  private String tenantName;

  @Column(name = "user_id")
  private Long userId;

  @Column(name = "full_name")
  private String fullName;

  @Column(name = "target_type")
  private String targetType;

  @Column(name = "target_id")
  private String targetId;

  @Column(name = "target_name")
  private String targetName;

  @Type(JsonType.class)
  @Column(name = "source_data", columnDefinition = "json")
  private EventContent sourceData;

  @Column(name = "app_code")
  private String appCode;

  @Column(name = "service_code")
  private String serviceCode;

  @Column(name = "push_status")
  @Enumerated(EnumType.STRING)
  private EventPushStatus pushStatus;

  @Column(name = "push_msg")
  private String pushMsg;

  @CreatedDate
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Column(name = "created_date", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
  private LocalDateTime createdDate;

  /**
   * @return Do not remove duplication if it is null.
   */
  public String getDuplicateKey() {
    if (Objects.isNull(type)) {
      return null;
    }
    switch (type) {
      case NOTICE:
      case BUSINESS:
        return nonNull(targetId) ? targetId  // Unique
            : tenantId + ":" + (nonNull(code) ? code : name);
      // Exception part
      case SECURITY:
      case QUOTA:
      case SYSTEM:
      case OPERATION:
      case PROTOCOL:
      case API:
        // Event type based on exception mechanism
        if (isNotEmpty(pushMsg)) {
          return type.getValue() + ":" + tenantId + ":" + code + ":" + eKey + ":"
              + StringUtils.substring(pushMsg, 0, EVENT_DUPLICATE_MAX_KEY_LENGTH);
        }
        if (isNotEmpty(description)) {
          return type.getValue() + ":" + tenantId + ":" + code + ":" + eKey + ":"
              + StringUtils.substring(description, 0, EVENT_DUPLICATE_MAX_KEY_LENGTH);
        }
        // Exception part
      case OTHER:
      default:
        return type.getValue() + ":" + tenantId + ":" + code;
    }
  }

  @Override
  public Long identity() {
    return this.id;
  }
}
