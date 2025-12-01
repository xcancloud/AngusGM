package cloud.xcan.angus.core.gm.interfaces.system.facade.vo;

import cloud.xcan.angus.api.enums.ResourceAclType;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class AuthorizedResourceVo {

  private String resource;

  // TODO 国际化
  private String description;

  private List<AuthorizedResourceApiVo> resources;

  private Map<Long, List<ResourceAclType>> acls;

}
