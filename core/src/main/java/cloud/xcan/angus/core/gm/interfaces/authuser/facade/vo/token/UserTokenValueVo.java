package cloud.xcan.angus.core.gm.interfaces.authuser.facade.vo.token;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class UserTokenValueVo {

  @Schema(description = "User access token id.")
  private Long id;

  @Schema(description = "User access token value.")
  private String value;

  @Schema(description = "User access token expired date.")
  private LocalDateTime expiredDate;

}
