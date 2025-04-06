package cloud.xcan.angus.core.gm.interfaces.user.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class UsernameCheckVo implements Serializable {

  @Schema(description = "Existing user id")
  private Long userId;

  @Schema(description = "Existing username")
  private String username;

}
