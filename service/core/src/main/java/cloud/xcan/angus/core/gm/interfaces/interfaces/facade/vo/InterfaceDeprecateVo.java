package cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Interface deprecate response VO
 */
@Getter
@Setter
@Accessors(chain = true)
public class InterfaceDeprecateVo implements Serializable {

  private Long id;

  private Boolean deprecated;

  private String deprecationNote;

  private LocalDateTime modifiedDate;
}
