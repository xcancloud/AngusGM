package cloud.xcan.angus.core.gm.interfaces.group.facade.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Group member VO
 */
@Getter
@Setter
@Accessors(chain = true)
public class GroupMemberVo implements Serializable {

  private Long tenantId;

  private Long createdBy;

  private String creator;

  private LocalDateTime createdDate;

  private Long modifiedBy;

  private String modifier;

  private LocalDateTime modifiedDate;

  private Long id;

  private String name;

  private String email;

  private String avatar;

  private String department;

  private Long departmentId;

  private String role;

  private LocalDateTime joinDate;

  private Boolean isOwner;
}
