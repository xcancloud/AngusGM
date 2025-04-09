package cloud.xcan.angus.api.commonlink.app.tag;

import cloud.xcan.angus.api.commonlink.operation.OperationResource;
import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Entity
@Table(name = "web_tag")
@Setter
@Getter
@Accessors(chain = true)
public class WebTag extends TenantAuditingEntity<WebTag, Long> implements OperationResource<Long> {

  @Id
  private Long id;

  private String name;

  private String description;

  @Override
  public Long identity() {
    return this.id;
  }

  public boolean equalName(String name) {
    if (name == null) {
      return false;
    }
    return Objects.nonNull(this.name) && this.name.equals(name);
  }
}
