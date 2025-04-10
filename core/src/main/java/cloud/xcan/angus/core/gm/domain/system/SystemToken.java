package cloud.xcan.angus.core.gm.domain.system;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DATE_FMT;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.enums.ResourceAuthType;
import cloud.xcan.angus.core.gm.domain.system.resource.SystemTokenResource;
import cloud.xcan.angus.core.jpa.multitenancy.TenantEntity;
import cloud.xcan.angus.spec.experimental.Resources;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;


@Setter
@Getter
@Entity
@Table(name = "system_token")
@Accessors(chain = true)
@EntityListeners(AuditingEntityListener.class)
public class SystemToken extends TenantEntity<SystemToken, Long> implements Resources<Long> {

  @Id
  private Long id;

  private String name;

  private String value;

  @Column(name = "expired_date")
  private LocalDateTime expiredDate;

  @Column(name = "auth_type")
  private ResourceAuthType authType;

  @CreatedDate
  @DateTimeFormat(pattern = DATE_FMT)
  @Column(name = "created_date", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
  protected LocalDateTime createdDate;

  @CreatedBy
  @Column(name = "created_by", nullable = false, updatable = false)
  protected Long createdBy;

  @Transient
  private List<SystemTokenResource> resources;
  @Transient
  private String decryptedValue;

  public boolean isApiAuth() {
    return nonNull(authType) && ResourceAuthType.API.equals(authType);
  }

  @Override
  public Long identity() {
    return this.id;
  }

}
