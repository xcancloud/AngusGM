package cloud.xcan.angus.core.gm.domain.authority;

import cloud.xcan.angus.spec.experimental.EntitySupport;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;

@Setter
@Getter
@Accessors(chain = true)
@Entity
@Table(name = "oauth2_api_authority")
public class ApiAuthority extends EntitySupport<ApiAuthority, Long> {

  @Id
  private Long id;

  @Enumerated(EnumType.STRING)
  private ApiAuthoritySource source;

  @Column(name = "source_id")
  private Long sourceId;

  @Column(name = "source_enabled")
  private Boolean sourceEnabled;

  @Column(name = "app_id")
  private Long appId;

  @Column(name = "app_enabled")
  private Boolean appEnabled;

  @Column(name = "api_id")
  private Long apiId;

  /**
   * OSA3 operationId
   */
  @Column(name = "api_operation_id")
  private String apiOperationId;

  /**
   * OSA3 SecurityRequirement
   */
  @Type(JsonType.class)
  @Column(columnDefinition = "json", name = "api_scopes")
  private Set<String> apiScopes;

  @Column(name = "api_enabled")
  private Boolean apiEnabled;

  @Column(name = "service_id")
  private Long serviceId;

  @Column(name = "service_code")
  private String serviceCode;

  @Column(name = "service_enabled")
  private Boolean serviceEnabled;

  @Override
  public Long identity() {
    return id;
  }

}
