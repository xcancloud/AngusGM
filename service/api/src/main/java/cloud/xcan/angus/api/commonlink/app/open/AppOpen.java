package cloud.xcan.angus.api.commonlink.app.open;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DATE_FMT;

import cloud.xcan.angus.api.enums.AppType;
import cloud.xcan.angus.api.enums.EditionType;
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

/**
 * Internal non multi tenant table and `privatization` is also needed.
 * <p>
 * Base applications should also be included.
 *
 * @author XiaoLong Liu
 */
@Entity
@Table(name = "app_open")
@Setter
@Getter
@Accessors(chain = true)
public class AppOpen extends EntitySupport<AppOpen, Long> {

  @Id
  private Long id;

  @Column(name = "app_id")
  private Long appId;

  @Column(name = "edition_type")
  @Enumerated(EnumType.STRING)
  private EditionType editionType;

  @Column(name = "app_code")
  private String appCode;

  @Column(name = "app_type")
  @Enumerated(EnumType.STRING)
  private AppType appType;

  private String version;

  @Column(name = "client_id")
  private String clientId;

  /**
   * Open to application tenant' user
   */
  @Column(name = "user_id")
  private Long userId;

  @Column(name = "tenant_id")
  protected Long tenantId;

  @DateTimeFormat(pattern = DATE_FMT)
  @Column(name = "open_date", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
  protected LocalDateTime openDate;

  @DateTimeFormat(pattern = DATE_FMT)
  @Column(name = "expiration_date", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
  protected LocalDateTime expirationDate;

  @Column(name = "expiration_deleted")
  private Boolean expirationDeleted;

  @Column(name = "op_client_open")
  private Boolean opClientOpen;

  @DateTimeFormat(pattern = DATE_FMT)
  @Column(name = "created_date", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
  private LocalDateTime createdDate;

  @Override
  public Long identity() {
    return this.id;
  }
}
