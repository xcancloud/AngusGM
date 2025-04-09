package cloud.xcan.angus.core.gm.domain.sms.channel;


import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DATE_FMT;

import cloud.xcan.angus.core.biz.ResourceName;
import cloud.xcan.angus.spec.experimental.EntitySupport;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

@Setter
@Getter
@Accessors(chain = true)
@EntityListeners({AuditingEntityListener.class})
@Entity
@Table(name = "sms_channel")
public class SmsChannel extends EntitySupport<SmsChannel, Long> implements Serializable {

  @Id
  private Long id;

  @ResourceName
  private String name;

  private Boolean enabled;

  private String logo;

  @Column(name = "endpoint")
  private String endpoint;

  @Column(name = "access_key_secret")
  private String accessKeySecret;

  @Column(name = "access_key_id")
  private String accessKeyId;

  @Column(name = "third_channel_no")
  private String thirdChannelNo;

  @CreatedDate
  @DateTimeFormat(pattern = DATE_FMT)
  @Column(name = "created_date", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
  protected LocalDateTime createdDate;

  @CreatedBy
  @Column(name = "created_by", nullable = false, updatable = false)
  protected Long createdBy;

  @Override
  public Long identity() {
    return this.id;
  }
}
