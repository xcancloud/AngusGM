package cloud.xcan.angus.core.gm.interfaces.to.facade.vo;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class TOUserRoleVo {

  private Long id;

  private String name;

  private String code;

  private String description;

  private LocalDateTime createdDate;

}
