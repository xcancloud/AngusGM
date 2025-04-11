package cloud.xcan.angus.api.commonlink.service;

import cloud.xcan.angus.core.jpa.auditor.AuditingEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Entity
@Table(name = "service")
@Setter
@Getter
@Accessors(chain = true)
public class Service extends AuditingEntity<Service, Long> {

  @Id
  private Long id;

  private String name;

  private String code;

  private String description;

  @Enumerated(EnumType.STRING)
  private ServiceSource source;

  private Boolean enabled;

  @Column(name = "route_path")
  private String routePath;

  private String url;

  @Column(name = "health_url")
  private String healthUrl;

  @Column(name = "api_doc_url")
  private String apiDocUrl;

  @Transient
  private Long apiNum;
  @Transient
  private List<ServiceResource> resources;
  @Transient
  private List<ServiceResourceApi> resourceApis;

  @Override
  public Long identity() {
    return this.id;
  }
}
