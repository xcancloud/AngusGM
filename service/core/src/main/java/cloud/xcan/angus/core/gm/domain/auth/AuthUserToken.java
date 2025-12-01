package cloud.xcan.angus.core.gm.domain.auth;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DATE_FMT;

import cloud.xcan.angus.core.jpa.multitenancy.TenantEntity;
import cloud.xcan.angus.spec.experimental.Resources;
import io.swagger.v3.oas.annotations.media.Schema;
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
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;


@Setter
@Getter
@Entity
@Table(name = "auth_user_token")
@Accessors(chain = true)
@EntityListeners(AuditingEntityListener.class)
public class AuthUserToken extends TenantEntity<AuthUserToken, Long> implements Resources<Long> {

  @Id
  private Long id;

  private String name;

  private String value;

  @Column(name = "expired_date")
  private LocalDateTime expiredDate;

  @Column(name = "generate_app_code")
  private String generateAppCode;

  @CreatedBy
  @Column(name = "created_by", nullable = false, updatable = false)
  protected Long createdBy;

  @CreatedDate
  @DateTimeFormat(pattern = DATE_FMT)
  @Column(name = "created_date", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
  protected LocalDateTime createdDate;

  @Transient
  private String decryptedValue;
  @Transient
  private String password;

  @Override
  public Long identity() {
    return this.id;
  }
}
