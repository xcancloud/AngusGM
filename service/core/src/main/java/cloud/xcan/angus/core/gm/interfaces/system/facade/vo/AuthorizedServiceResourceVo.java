package cloud.xcan.angus.core.gm.interfaces.system.facade.vo;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class AuthorizedServiceResourceVo {

  private String serviceCode;

  // TODO 国际化
  private String serviceName;

  private List<AuthorizedResourceVo> resources;

}
