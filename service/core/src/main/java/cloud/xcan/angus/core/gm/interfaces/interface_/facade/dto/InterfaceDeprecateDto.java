package cloud.xcan.angus.core.gm.interfaces.interface_.facade.dto;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Interface deprecate DTO
 */
@Getter
@Setter
@Accessors(chain = true)
public class InterfaceDeprecateDto implements Serializable {

  private Boolean deprecated;

  private String deprecationNote;
}
