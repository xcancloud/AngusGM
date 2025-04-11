package cloud.xcan.angus.core.gm.interfaces.service.facade.vo;

import cloud.xcan.angus.core.gm.interfaces.api.facade.vo.ApiBaseVo;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class ResourceApiVo {

  private String resourceName;

  // TODO i18n message
  private String resourceDesc;

  private List<ApiBaseVo> apis;

}
