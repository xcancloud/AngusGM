package cloud.xcan.angus.core.gm.domain.api.log;

import cloud.xcan.angus.api.enums.ApiType;
import cloud.xcan.angus.core.jpa.multitenancy.TenantEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
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
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.LinkedMultiValueMap;

@Entity
@Table(name = "api_log")
@Setter
@Getter
@Accessors(chain = true)
public class ApiLog extends TenantEntity<ApiLog, Long> {

  @Id
  private Long id;

  @Column(name = "client_id")
  private String clientId;

  @Column(name = "client_source")
  private String clientSource;

  @Column
  private String remote;

  @Column(name = "api_type")
  @Enumerated(EnumType.STRING)
  private ApiType apiType;

  @Column(name = "tenant_name")
  private String tenantName;

  @Column(name = "user_id")
  private Long userId;

  private String fullName;

  @Column(name = "request_id")
  private String requestId;

  @Column(name = "service_code")
  private String serviceCode;

  @Column(name = "service_name")
  private String serviceName;

  @Column(name = "instance_id")
  private String instanceId;

  @Column(name = "api_code")
  private String apiCode;

  @Column(name = "api_name")
  private String apiName;

  @Column(name = "resource_name")
  private String resourceName;

  @Column
  private String method;

  @Column
  private String uri;

  @Column(name = "request_date")
  private LocalDateTime requestDate;

  @Column
  private Integer status;

  @Column(name = "query_param")
  private String queryParam;

  @Column(name = "request_headers", columnDefinition = "json")
  @Type(JsonType.class)
  private LinkedMultiValueMap<String, String> requestHeaders;

  @Column(name = "request_body")
  private String requestBody;

  @Column(name = "response_date")
  private LocalDateTime responseDate;

  @Column(name = "response_size")
  private Integer responseSize;

  @Column(name = "response_headers", columnDefinition = "json")
  @Type(JsonType.class)
  private LinkedMultiValueMap<String, String> responseHeaders;

  @Column(name = "request_size")
  private Integer requestSize;

  @Column(name = "response_body")
  private String responseBody;

  private Boolean success;

  @Column(name = "elapsed_millis")
  private Long elapsedMillis;

  @CreatedDate
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Column(name = "created_date", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
  private LocalDateTime createdDate;

  @Override
  public Long identity() {
    return this.id;
  }
}
