package cloud.xcan.angus.core.gm.interfaces.user.facade.vo.dept;

import cloud.xcan.angus.remote.NameJoinField;
import jakarta.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class UserDeptVo implements Serializable {

  @Id
  private Long id;

  private Long deptId;

  private String deptCode;

  private String deptName;

  private Boolean deptHead;

  private Boolean mainDept;

  private Long userId;

  private String fullName;

  private String mobile;

  private String avatar;

  private Boolean hasSubDept;

  private LocalDateTime createdDate;

  private Long createdBy;

  private Long tenantId;

  @NameJoinField(id = "createdBy", repository = "commonUserBaseRepo")
  private String createdByName;

}
