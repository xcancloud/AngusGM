package cloud.xcan.angus.core.gm.domain.system.resource;

import cloud.xcan.angus.api.commonlink.api.Api;
import cloud.xcan.angus.spec.experimental.EntitySupport;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;


@Setter
@Getter
@Entity
@Table(name = "system_token_resource")
@Accessors(chain = true)
public class SystemTokenResource extends EntitySupport<SystemTokenResource, Long> implements
    Serializable {

  @Id
  private Long id;

  @Column(name = "service_code")
  private String serviceCode;

  private String resource;

  @Column(name = "resource_id")
  private Long resourceId;

  @Type(JsonType.class)
  @Column(columnDefinition = "json", name = "authorities")
  private List<String> authorities;

  @Column(name = "system_token_id")
  private Long systemTokenId;

  @Transient
  private Api api;
  @Transient
  private String serviceName;

  @Override
  public Long identity() {
    return this.id;
  }
}
