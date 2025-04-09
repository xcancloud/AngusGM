package cloud.xcan.angus.api.commonlink.api;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;

import cloud.xcan.angus.api.commonlink.operation.OperationResource;
import cloud.xcan.angus.api.enums.ApiType;
import cloud.xcan.angus.core.jpa.auditor.AuditingEntity;
import cloud.xcan.angus.spec.http.HttpMethod;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Table(name = "api")
@EntityListeners({AuditingEntityListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class Api extends AuditingEntity<Api, Long> implements OperationResource<Long> {

  @Id
  private Long id;

  private String name;

  /**
   * OSA3 operationId
   */
  @Column(name ="operation_id")
  private String operationId;

  /**
   * OSA3 SecurityRequirement
   */
  @Type(JsonType.class)
  @Column(columnDefinition = "json", name = "scopes")
  private Set<String> scopes;

  private String uri;

  @Enumerated(EnumType.STRING)
  private HttpMethod method;

  @Enumerated(EnumType.STRING)
  private ApiType type;

  private Boolean enabled;

  private String description;

  @Column(name = "resource_name")
  private String resourceName;

  @Column(name = "resource_description")
  private String resourceDescription;

  @Column(name = "service_id")
  private Long serviceId;

  @Column(name = "service_code")
  private String serviceCode;

  @Column(name = "service_name")
  private String serviceName;

  @Column(name = "service_enabled")
  private Boolean serviceEnabled;

  private Boolean sync;

  @Column(name = "swagger_deleted")
  private Boolean swaggerDeleted;

  @Override
  public Long identity() {
    return this.id;
  }

  @Override
  public boolean sameIdentityAs(Api other) {
    if (Objects.isNull(other)) {
      return false;
    }
    if (isEmpty(other.getMethod()) || isEmpty(other.getUri())) {
      return false;
    }
    return other.getMethod().equals(method) && other.getUri().equalsIgnoreCase(uri);
  }
}
