package cloud.xcan.angus.core.gm.domain.user;

import cloud.xcan.angus.core.gm.domain.user.enums.EnableStatus;
import cloud.xcan.angus.core.gm.domain.user.enums.UserAccountType;
import cloud.xcan.angus.core.gm.domain.user.enums.UserStatus;
import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

/**
 * User domain entity
 */
@Setter
@Getter
@Entity
@Table(name = "gm_user")
public class User extends TenantAuditingEntity<User, Long> {

  @Id
  private Long id;

  @Column(name = "username", nullable = false, length = 50, unique = true)
  private String username;

  @Column(name = "name", nullable = false, length = 50)
  private String name;

  @Column(name = "email", length = 100)
  private String email;

  @Column(name = "phone", length = 20)
  private String phone;

  @Column(name = "password", nullable = false, length = 255)
  private String password;

  @Column(name = "avatar", length = 500)
  private String avatar;

  @Column(name = "department_id")
  private Long departmentId;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 20)
  private UserStatus status;

  @Enumerated(EnumType.STRING)
  @Column(name = "enable_status", length = 20)
  private EnableStatus enableStatus;

  @Enumerated(EnumType.STRING)
  @Column(name = "account_type", length = 20)
  private UserAccountType accountType;

  @Column(name = "is_locked")
  private Boolean isLocked;

  @Column(name = "last_login")
  private LocalDateTime lastLogin;

  // Non-persistent fields - for temporary associated data
  @Transient
  private String role;

  @Transient
  private List<Long> roleIds;

  @Transient
  private String department;

  @Transient
  private Boolean isOnline;

  @Override
  public Long identity() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof User user)) {
      return false;
    }
    return Objects.equals(id, user.id)
        && Objects.equals(username, user.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, username);
  }
}
