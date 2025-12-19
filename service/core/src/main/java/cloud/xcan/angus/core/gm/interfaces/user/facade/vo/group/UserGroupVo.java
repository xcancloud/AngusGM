package cloud.xcan.angus.core.gm.interfaces.user.facade.vo.group;

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
public class UserGroupVo implements Serializable {

  @Id
  private Long id;

  private Long groupId;

  private String groupName;

  private String groupCode;

  private Boolean groupEnabled;

  private String groupRemark;

  private Long userId;

  private String fullName;

  private String mobile;

  private String avatar;

  private LocalDateTime createdDate;

  private Long createdBy;

  private Long tenantId;

  @NameJoinField(id = "createdBy", repository = "commonUserBaseRepo")
  private String creator;

}
