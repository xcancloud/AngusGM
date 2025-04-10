package cloud.xcan.angus.api.commonlink.group;

import cloud.xcan.angus.api.commonlink.tag.OrgTagTarget;
import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import cloud.xcan.angus.spec.experimental.Resources;
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

/**
 * Note: group and groups(8.0+) is mysql keyword. Renaming to avoid providing separate repo
 * implementations for postgres.
 */
@Entity
@Table(name = "group0")
@Setter
@Getter
@Accessors(chain = true)
public class Group extends TenantAuditingEntity<Group, Long> implements Resources<Long> {

  @Id
  private Long id;

  private String name;

  private String code;

  private Boolean enabled;

  private String remark;

  @Enumerated(EnumType.STRING)
  private GroupSource source;

  @Column(name = "directory_id")
  private Long directoryId;

  //@Column(name = "directory_entry_id")
  //private String directoryGroupEntryId; // LDAP;

  @Column(name = "directory_gid_number")
  private String directoryGidNumber; // LDAP;

  @Transient
  private List<String> members;
  @Transient
  private long userNum = 0;
  @Transient
  private List<Long> tagIds;
  @Transient
  private List<OrgTagTarget> tags;

  @Override
  public Long identity() {
    return this.id;
  }
}
