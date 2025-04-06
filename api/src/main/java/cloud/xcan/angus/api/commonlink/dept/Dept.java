package cloud.xcan.angus.api.commonlink.dept;

import static cloud.xcan.angus.spec.experimental.BizConstant.DEFAULT_ROOT_PID;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;

import cloud.xcan.angus.api.commonlink.tag.OrgTagTarget;
import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "dept")
@Setter
@Getter
@Accessors(chain = true)
public class Dept extends TenantAuditingEntity<Dept, Long> {

  @Id
  private Long id;

  private String code;

  @Column(name = "parent_like_id")
  private String parentLikeId;

  private String name;

  private Long pid;

  private Integer level;

  @Transient
  private List<Long> tagIds;
  @Transient
  private List<OrgTagTarget> tags;
  @Transient
  private Boolean main;
  @Transient
  private Boolean deptHead;
  @Transient
  private Collection<Dept> parentChain;
  @Transient
  private Boolean hasSubDept;

  public boolean hasParent() {
    return Objects.nonNull(pid) && !pid.equals(DEFAULT_ROOT_PID);
  }

  public String getSubParentLikeId() {
    return isEmpty(parentLikeId) ? String.valueOf(id) : parentLikeId + "-" + id;
  }

  public Integer getSubLevel() {
    return level + 1;
  }

  @Override
  public Long identity() {
    return this.id;
  }
}
