package cloud.xcan.angus.core.gm.domain.operation;

import cloud.xcan.angus.core.jpa.multitenancy.TenantEntity;
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


@Entity
@Table(name = "operation_log")
@Setter
@Getter
@Accessors(chain = true)
public class OperationLog extends TenantEntity<OperationLog, Long> {

  @Id
  private Long id;

  @Column(name = "request_id")
  private String requestId;

  @Column(name = "client_id")
  private String clientId;

  @Enumerated(EnumType.STRING)
  private OperationResourceType resource;

  @Column(name = "resource_name")
  private String resourceName;

  @Column(name = "resource_id")
  private String resourceId;

  @Enumerated(EnumType.STRING)
  private OperationType type;

  @Column(name = "tenant_name")
  private String tenantName;

  @Column(name = "user_id")
  private Long userId;

  @Column(name = "full_name")
  private String fullName;

  @Column(name = "opt_date")
  private LocalDateTime optDate;

  @Column
  private String description;

  @Column
  private String detail;

  private Boolean private0;

  @Override
  public Long identity() {
    return this.id;
  }

}
