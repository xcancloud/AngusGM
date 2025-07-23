package cloud.xcan.angus.api.commonlink.app.open;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DATE_FMT;

import cloud.xcan.angus.api.enums.AppType;
import cloud.xcan.angus.spec.experimental.EntitySupport;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "app_open")
@Setter
@Getter
@Accessors(chain = true)
public class AppAuth extends EntitySupport<AppOpen, Long> implements
    cloud.xcan.angus.core.jpa.repository.app.AppAuth {

  @Id
  private Long id;

  @Column(name = "app_id")
  private Long appId;

  @Column(name = "app_code")
  private String appCode;

  @Column(name = "app_type")
  @Enumerated(EnumType.STRING)
  private AppType appType;

  private String version;

  @Column(name = "client_id")
  private String clientId;

  @Column(name = "tenant_id")
  protected Long tenantId;

  @DateTimeFormat(pattern = DATE_FMT)
  @Column(name = "open_date", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
  protected LocalDateTime openDate;

  @DateTimeFormat(pattern = DATE_FMT)
  @Column(name = "expiration_date", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
  protected LocalDateTime expirationDate;

  @Override
  public Long identity() {
    return id;
  }
}
