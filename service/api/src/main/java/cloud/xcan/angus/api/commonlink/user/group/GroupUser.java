package cloud.xcan.angus.api.commonlink.user.group;


import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DATE_FMT;

import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.core.jpa.multitenancy.TenantAware;
import cloud.xcan.angus.core.jpa.multitenancy.TenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "group_user")
@EntityListeners({AuditingEntityListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class GroupUser extends TenantEntity<GroupUser, Long> implements TenantAware {

  @Id
  private Long id;

  @Column(name = "group_id")
  private Long groupId;

  @Column(name = "user_id")
  private Long userId;

  @Column(name = "directory_id")
  private Long directoryId; // LDAP;

  @CreatedDate
  @DateTimeFormat(pattern = DATE_FMT)
  @Column(name = "created_date", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
  private LocalDateTime createdDate;

  @CreatedBy
  @Column(name = "created_by", nullable = false, updatable = false)
  private Long createdBy;

  @Transient
  private User user;
  @Transient
  private Group group;

  @Transient
  private String username;
  @Transient
  private String fullName;
  @Transient
  private String avatar;
  @Transient
  private String mobile;
  @Transient
  private String groupName;
  @Transient
  private String groupCode;
  @Transient
  private Boolean groupEnabled;
  @Transient
  private String groupRemark;
  @Transient
  private String gidNumber;

  @Override
  public boolean sameIdentityAs(GroupUser o) {
    if (this == o) {
      return true;
    }
    if (o == null || groupId == null || userId == null || getClass() != o.getClass()) {
      return false;
    }
    return groupId.equals(o.groupId) && userId.equals(o.userId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(groupId, userId);
  }

  @Override
  public Long identity() {
    return this.id;
  }

}
