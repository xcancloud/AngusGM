package cloud.xcan.angus.core.gm.interfaces.system.facade.vo;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class SystemTokenValueVo {

  private Long id;

  private String value;

  private LocalDateTime expiredDate;

}
