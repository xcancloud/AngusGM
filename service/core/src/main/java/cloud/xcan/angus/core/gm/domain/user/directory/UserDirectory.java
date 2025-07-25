package cloud.xcan.angus.core.gm.domain.user.directory;


import cloud.xcan.angus.core.gm.domain.user.directory.model.DirectoryGroupSchema;
import cloud.xcan.angus.core.gm.domain.user.directory.model.DirectoryMembershipSchema;
import cloud.xcan.angus.core.gm.domain.user.directory.model.DirectorySchema;
import cloud.xcan.angus.core.gm.domain.user.directory.model.DirectoryServer;
import cloud.xcan.angus.core.gm.domain.user.directory.model.DirectoryUserSchema;
import cloud.xcan.angus.core.jpa.auditor.AuditingEntity;
import cloud.xcan.angus.spec.experimental.Resources;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;


@Entity
@Table(name = "user_directory")
@Setter
@Getter
@Accessors(chain = true)
public class UserDirectory extends AuditingEntity<UserDirectory, Long>
    implements Resources<Long>, Comparable<UserDirectory>, Serializable {

  @Id
  private Long id;

  /**
   * Server name.
   */
  private String name;

  /**
   * Sorting value, the synchronization priority is high if the value is small
   */
  private Integer sequence;

  private Boolean enabled;

  @Type(JsonType.class)
  @Column(name = "server_data", columnDefinition = "json")
  private DirectoryServer serverData;

  @Type(JsonType.class)
  @Column(name = "schema_data", columnDefinition = "json")
  private DirectorySchema schemaData;

  @Type(JsonType.class)
  @Column(name = "user_schema_data", columnDefinition = "json")
  private DirectoryUserSchema userSchemaData;

  @Type(JsonType.class)
  @Column(name = "group_schema_data", columnDefinition = "json")
  private DirectoryGroupSchema groupSchemaData;

  @Type(JsonType.class)
  @Column(name = "membership_schema_data", columnDefinition = "json")
  private DirectoryMembershipSchema membershipSchemaData;

  @Override
  public Long identity() {
    return this.id;
  }

  @Override
  public int compareTo(UserDirectory o) {
    if (o == null) {
      return 1;
    }
    if (this.getSequence() == null && o.getSequence() == null) {
      return 0;
    }
    if (this.getSequence() == null) {
      return -1;
    }
    if (o.getSequence() == null) {
      return 1;
    }
    return this.getSequence().compareTo(o.getSequence());
  }
}
