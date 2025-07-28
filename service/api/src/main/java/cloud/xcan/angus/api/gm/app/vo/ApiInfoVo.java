package cloud.xcan.angus.api.gm.app.vo;

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
