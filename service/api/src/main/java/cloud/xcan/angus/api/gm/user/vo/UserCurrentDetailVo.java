package cloud.xcan.angus.api.gm.user.vo;

import cloud.xcan.angus.api.enums.Gender;
import cloud.xcan.angus.api.enums.PasswordStrength;
import cloud.xcan.angus.api.enums.TenantRealNameStatus;
import cloud.xcan.angus.api.enums.UserSource;
import cloud.xcan.angus.api.gm.app.vo.AppDetailVo;
import cloud.xcan.angus.api.gm.app.vo.OrgAppAuthVo;
import cloud.xcan.angus.api.gm.setting.vo.UserPreferenceVo;
import cloud.xcan.angus.api.gm.tenant.vo.TenantDetailVo;
import cloud.xcan.angus.api.gm.user.to.UserDeptTo;
import cloud.xcan.angus.api.gm.user.to.UserGroupTo;
import cloud.xcan.angus.remote.NameJoinField;
import cloud.xcan.angus.remote.info.IdAndName;
import java.io.Serializable;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class UserCurrentDetailVo implements Serializable {

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

  private Boolean enabled;

  private UserSource source;

  private Boolean locked;

  private LocalDateTime lockStartDate;

  private LocalDateTime lockEndDate;

  private Long tenantId;

  @NameJoinField(id = "tenantId", repository = "tenantRepo")
  private String tenantName;

  private Long createdBy;

  private LocalDateTime createdDate;

  private Long lastModifiedBy;

  private LocalDateTime lastModifiedDate;

  private PasswordStrength passwordStrength;

  private Boolean passwordExpired;

  private LocalDateTime passwordExpiredDate;

  private TenantRealNameStatus tenantRealNameStatus;

  private Boolean online;

  private LocalDateTime onlineDate;

  private LocalDateTime offlineDate;

  private Principal principal;

  private List<IdAndName> tags;

  private List<UserDeptTo> depts;

  private List<UserGroupTo> groups;

  private UserPreferenceVo preference;

  private TenantDetailVo tenant;

  private AppDetailVo accessApp;

  private List<OrgAppAuthVo> authApps;

}


