package cloud.xcan.angus.api.gm.group.dto;

import cloud.xcan.angus.remote.PageQuery;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class GroupSearchDto extends PageQuery {

  private Long id;

  private String name;

  private String code;

  private Boolean enabled;

  private Long createdBy;

  private LocalDateTime createdDate;

  private Long tagId;

}
