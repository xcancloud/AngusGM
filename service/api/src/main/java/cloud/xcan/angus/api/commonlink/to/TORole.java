package cloud.xcan.angus.api.commonlink.to;

import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.core.jpa.auditor.AuditingEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "to_role")
@EntityListeners({AuditingEntityListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class TORole extends AuditingEntity<TORole, Long> {

  @Id
  private Long id;

  private String name;

  private String code;

  private Boolean enabled;

  private String description;

  @Column(name = "app_id")
  private Long appId;

  @Transient
  private List<User> users;

  @Override
  public Long identity() {
    return this.id;
  }
}
