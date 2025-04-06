package cloud.xcan.angus.api.gm.user.vo;

import cloud.xcan.angus.api.commonlink.tenant.TenantRealNameStatus;
import cloud.xcan.angus.api.enums.Gender;
import cloud.xcan.angus.api.enums.PasswordStrength;
import cloud.xcan.angus.api.enums.UserSource;
import cloud.xcan.angus.api.gm.setting.vo.UserPreferenceVo;
import cloud.xcan.angus.remote.NameJoinField;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class UserCurrentDetailVo implements Serializable {

  private Long id;

  private String username;

  private String fullname;

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

  private PasswordStrength passdStrength;

  private Boolean passwordExpired;

  private LocalDateTime passwordExpiredDate;

  private TenantRealNameStatus tenantRealNameStatus;

  private Boolean online;

  private LocalDateTime onlineDate;

  private LocalDateTime offlineDate;

  private UserPreferenceVo preference;

}


