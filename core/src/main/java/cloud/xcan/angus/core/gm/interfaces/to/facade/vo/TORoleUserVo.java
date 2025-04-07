package cloud.xcan.angus.core.gm.interfaces.to.facade.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class TORoleUserVo {

  private Long id;

  private String username;

  private String fullName;

  private String avatar;

  private String mobile;

  private String email;
}
