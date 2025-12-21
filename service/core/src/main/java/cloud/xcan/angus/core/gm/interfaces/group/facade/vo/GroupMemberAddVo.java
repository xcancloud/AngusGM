package cloud.xcan.angus.core.gm.interfaces.group.facade.vo;

import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Group member add response VO
 */
@Getter
@Setter
@Accessors(chain = true)
public class GroupMemberAddVo implements Serializable {

  private Long groupId;

  private Integer addedCount;

  private List<AddedUserVo> addedUsers;

  @Getter
  @Setter
  @Accessors(chain = true)
  public static class AddedUserVo implements Serializable {
    private Long id;
    private String name;
  }
}
