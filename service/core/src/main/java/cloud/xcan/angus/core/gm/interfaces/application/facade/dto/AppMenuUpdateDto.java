package cloud.xcan.angus.core.gm.interfaces.application.facade.dto;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Application menu update DTO
 */
@Getter
@Setter
@Accessors(chain = true)
public class AppMenuUpdateDto implements Serializable {

  private String name;

  private String icon;

  private String path;

  private Integer sortOrder;

  private Boolean isVisible;

  private String permission;
}
