package cloud.xcan.angus.api.commonlink.user;

import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.operation.OperationResource;
import cloud.xcan.angus.api.commonlink.tag.OrgTagTarget;
import cloud.xcan.angus.api.commonlink.tenant.Tenant;
import cloud.xcan.angus.api.commonlink.tenant.TenantRealNameStatus;
import cloud.xcan.angus.api.commonlink.user.dept.DeptUser;
import cloud.xcan.angus.api.commonlink.user.group.GroupUser;
import cloud.xcan.angus.api.enums.Gender;
import cloud.xcan.angus.api.enums.PassdEncoderType;
import cloud.xcan.angus.api.enums.PasswordStrength;
import cloud.xcan.angus.api.enums.UserSource;
import cloud.xcan.angus.core.biz.ResourceName;
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
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.SQLRestriction;

/**
 * Note: user is mysql keyword. Renaming to avoid providing separate repo implementations for
 * postgres.
 */
@Entity
@Table(name = "user0")
@SQLRestriction("deleted = 0")
@Setter
@Getter
@Accessors(chain = true)
public class User extends TenantAuditingEntity<User, Long> implements OperationResource<Long> {

  @Id
  private Long id;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @ResourceName
  @Column(name = "full_name")
  private String fullName;

  private String username;

  private String itc;

  private String country;

  private String mobile;

  private String email;

  private String landline;

  private String avatar;

  private String title;

  @Enumerated(EnumType.STRING)
  private Gender gender;

  private String address;

  @Enumerated(EnumType.STRING)
  private UserSource source;

  @Column(name = "directory_id")
  private Long directoryId; // LDAP;

  //@Column(name = "directory_entry_id")
  //private String directoryUserEntryId; // LDAP;

  /**
   * The system administrator has all permissions including TOP.
   */
  @Column(name = "sys_admin")
  private Boolean sysAdmin;

  @Column(name = "signup_account")
  private String signupAccount;

  @Enumerated(EnumType.STRING)
  @Column(name = "signup_account_type")
  private SignupType signupAccountType;

  @Column(name = "signup_device_id")
  private String signupDeviceId;

  private Boolean expired;

  @Column(name = "expired_date")
  private LocalDateTime expiredDate;

  @Column(name = "dept_head")
  private Boolean deptHead;

  @Column(name = "main_dept_id")
  private Long mainDeptId;

  private Boolean online;

  @Column(name = "online_date")
  private LocalDateTime onlineDate;

  @Column(name = "offline_date")
  private LocalDateTime offlineDate;

  private Boolean enabled;

  private Boolean deleted;

  @Column(name = "disable_reason")
  private String disableReason;

  private Boolean locked;

  @Column(name = "last_lock_date")
  private LocalDateTime lastLockDate;

  @Column(name = "lock_start_date")
  private LocalDateTime lockStartDate;

  @Column(name = "lock_end_date")
  private LocalDateTime lockEndDate;

  @Column(name = "tenant_name")
  private String tenantName;

  @Transient
  private String invitationCode;
  /**
   * The first administrator from register or add in the background.
   */
  @Transient
  private Boolean firstSysAdmin;

  /////////////OAuthUser///////////
  @Transient
  private String password;
  @Transient
  private PasswordStrength passwordStrength;
  @Transient
  private Boolean passwordExpired;
  @Transient
  private LocalDateTime passwordExpiredDate;
  @Transient
  private TenantRealNameStatus tenantRealNameStatus;
  @Transient
  private PassdEncoderType passwordEncoderType;
  /////////////OAuthUser///////////

  @Transient
  private List<OrgTagTarget> tags;
  @Transient
  private List<GroupUser> groups;
  @Transient
  private List<DeptUser> depts;

  public boolean getSysAdmin() {
    return nonNull(sysAdmin) && sysAdmin;
  }

  public boolean getExpired() {
    return (nonNull(expired) && expired)
        || (nonNull(expiredDate) && expiredDate.isBefore(LocalDateTime.now()));
  }

  public boolean notSameInDirectory(Tenant tenantDb, User user, boolean syncMobile) {
    // firstNameAttribute/lastNameAttribute/displayNameAttribute/emailAttribute/mobileAttribute/passdAttribute/passdEncoderType
    boolean modified = !this.firstName.equals(user.getFirstName())
        || !this.lastName.equals(user.getLastName()) || !this.fullName.equals(user.getFullName())
        || !this.email.equals(
        user.getEmail()) /*|| !this.password.equals(user.getPassd()) <- LDAP-PROXY */
        || !tenantDb.getRealNameStatus().equals(user.getTenantRealNameStatus());
    return modified || (syncMobile && !this.mobile.equals(user.getMobile()));
  }

  public UserInfo toUserInfo() {
    return new UserInfo().setId(id).setFullName(fullName).setAvatar(avatar).setEmail(email)
        .setMobile(mobile);
  }

  @Override
  public Long identity() {
    return this.id;
  }

  @Override
  public String getName() {
    return fullName;
  }
}
