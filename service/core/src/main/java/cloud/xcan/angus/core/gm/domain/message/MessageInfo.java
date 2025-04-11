package cloud.xcan.angus.core.gm.domain.message;


import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DATE_FMT;

import cloud.xcan.angus.api.enums.ReceiveObjectType;
import cloud.xcan.angus.core.gm.domain.SentType;
import cloud.xcan.angus.core.jpa.multitenancy.TenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;


@Entity
@Table(name = "message")
@SQLRestriction("deleted = 0")
@EntityListeners({AuditingEntityListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class MessageInfo extends TenantEntity<MessageInfo, Long> {

  @Id
  private Long id;

  private String title;

  @Column(name = "receive_type")
  @Enumerated(EnumType.STRING)
  private MessageReceiveType receiveType;

  @Column(name = "send_type")
  @Enumerated(EnumType.STRING)
  private SentType sendType;

  @Column(name = "timing_date")
  private LocalDateTime timingDate;

  @Enumerated(EnumType.STRING)
  private MessageStatus status;

  @Column(name = "failure_reason")
  private String failureReason;

  @Column(name = "sent_num")
  private Integer sentNum;

  @Column(name = "read_num")
  private Integer readNum;

  @Column(name = "send_date")
  @DateTimeFormat(pattern = DATE_FMT)
  private LocalDateTime sendDate;

  @Column(name = "receive_tenant_id")
  private Long receiveTenantId;

  @Column(name = "receive_object_type")
  @Enumerated(EnumType.STRING)
  private ReceiveObjectType receiveObjectType;

  private Boolean deleted;

  @Column(name = "deleted_date")
  @DateTimeFormat(pattern = DATE_FMT)
  private LocalDateTime deletedDate;

  @CreatedBy
  @Column(name = "created_by")
  private Long createdBy;

  @Column(name = "created_by_name")
  private String createdByName;

  @CreatedDate
  @DateTimeFormat(pattern = DATE_FMT)
  @Column(name = "created_date", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
  protected LocalDateTime createdDate;

  @Override
  public Long identity() {
    return this.id;
  }
}
