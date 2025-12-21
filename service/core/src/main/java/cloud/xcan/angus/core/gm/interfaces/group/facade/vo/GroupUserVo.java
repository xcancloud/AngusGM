package cloud.xcan.angus.core.gm.interfaces.group.facade.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * User's group list VO
 */
@Getter
@Setter
@Accessors(chain = true)
public class GroupUserVo implements Serializable {

  private Long tenantId;

  private Long createdBy;

  private String creator;

  private LocalDateTime createdDate;

  private Long modifiedBy;

  private String modifier;

  private LocalDateTime modifiedDate;

  private Long id;

  private String name;

  private String type;

  private String ownerName;

  private Integer memberCount;

  private String status;

  private String role;
}
