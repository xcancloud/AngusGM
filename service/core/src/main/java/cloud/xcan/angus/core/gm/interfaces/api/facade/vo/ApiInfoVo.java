package cloud.xcan.angus.core.gm.interfaces.api.facade.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class ApiInfoVo {

  private Long id;

  private String name;

  private String code;

  private Boolean enabled;

  private String resourceName;

  private String description;

  private String resourceDescription;

}
