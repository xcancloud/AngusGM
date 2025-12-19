package cloud.xcan.angus.api.gm.user.vo;

import cloud.xcan.angus.api.enums.Gender;
import cloud.xcan.angus.api.enums.UserSource;
import cloud.xcan.angus.remote.NameJoinField;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class UserListVo implements Serializable {

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

  private Boolean online = false;

  private LocalDateTime onlineDate;

  private LocalDateTime offlineDate;

  private Long tenantId;

  private String tenantName;

  private Long createdBy;

  @NameJoinField(id = "createdBy", repository = "commonUserBaseRepo")
  private String creator;

  private LocalDateTime createdDate;

  private Long modifiedBy;

  @NameJoinField(id = "modifiedBy", repository = "commonUserBaseRepo")
  private String modifier;

  private LocalDateTime modifiedDate;

}


