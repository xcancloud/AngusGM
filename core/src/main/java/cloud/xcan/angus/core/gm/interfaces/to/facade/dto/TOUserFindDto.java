package cloud.xcan.angus.core.gm.interfaces.to.facade.dto;

import cloud.xcan.angus.remote.PageQuery;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class TOUserFindDto extends PageQuery {

  private Long userId;

  private LocalDateTime createdDate;

}
