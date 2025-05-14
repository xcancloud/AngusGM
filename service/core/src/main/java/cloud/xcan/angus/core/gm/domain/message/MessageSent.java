package cloud.xcan.angus.core.gm.domain.message;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DATE_FMT;

import cloud.xcan.angus.spec.experimental.EntitySupport;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;


@Entity
@Table(name = "message_sent")
@SQLRestriction("deleted = 0")
@EntityListeners({AuditingEntityListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class MessageSent extends EntitySupport<MessageSent, Long> {

  @Id
  private Long id;

  private Long messageId;

  private Long receiveTenantId;

  private Long receiveUserId;

  @Column(name = "`read`")
  private Boolean read;

  @DateTimeFormat(pattern = DATE_FMT)
  private LocalDateTime sentDate;

  @DateTimeFormat(pattern = DATE_FMT)
  private LocalDateTime readDate;

  private Boolean deleted;

  @DateTimeFormat(pattern = DATE_FMT)
  private LocalDateTime deletedDate;

  @CreatedDate
  @DateTimeFormat(pattern = DATE_FMT)
  private LocalDateTime createdDate;

  @Transient
  private Message message;
  @Transient
  private MessageInfo messageInfo;

  @Override
  public Long identity() {
    return this.id;
  }
}
