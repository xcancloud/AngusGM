package cloud.xcan.angus.api.gm.user.vo;

import cloud.xcan.angus.api.enums.TenantRealNameStatus;
import cloud.xcan.angus.api.enums.Gender;
import cloud.xcan.angus.api.enums.PasswordStrength;
import cloud.xcan.angus.api.enums.UserSource;
import cloud.xcan.angus.api.gm.user.to.UserDeptTo;
import cloud.xcan.angus.api.gm.user.to.UserGroupTo;
import cloud.xcan.angus.remote.NameJoinField;
import cloud.xcan.angus.remote.info.IdAndName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class UserDetailVo implements Serializable {

  private Long id;

  private String username;

  private String fullName;

  private String firstName;

  private String lastName;

  private String itc;

  private String country;

  private String mobile;

  private String email;

  private String landline;

  private String avatar;

  private String title;

  private Gender gender;

  private String address;

  private Boolean sysAdmin;

  private Boolean deptHead;

  private Long mainDeptId;

  private Boolean enabled;

  private UserSource source;

  private Boolean locked;

  private LocalDateTime lockStartDate;

  private LocalDateTime lockEndDate;

  private Long tenantId;

  @NameJoinField(id = "tenantId", repository = "tenantRepo")
  private String tenantName;

  private Long createdBy;

  @NameJoinField(id = "createdBy", repository = "commonUserBaseRepo")
  private String createdByName;

  private LocalDateTime createdDate;

  private Long lastModifiedBy;

  @NameJoinField(id = "lastModifiedBy", repository = "commonUserBaseRepo")
  private String lastModifiedByName;

  private LocalDateTime lastModifiedDate;

  private PasswordStrength passwordStrength;

  private Boolean passwordExpired;

  private LocalDateTime passwordExpiredDate;

  private TenantRealNameStatus tenantRealNameStatus;

  private Boolean online;

  private LocalDateTime onlineDate;

  private LocalDateTime offlineDate;

  private List<IdAndName> tags;

  private List<UserDeptTo> depts;

  private List<UserGroupTo> groups;

}


