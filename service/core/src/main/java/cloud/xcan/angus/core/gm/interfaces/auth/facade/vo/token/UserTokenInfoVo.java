package cloud.xcan.angus.core.gm.interfaces.auth.facade.vo.token;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class UserTokenInfoVo {

  @Schema(description = "User access token id")
  private Long id;

  @Schema(description = "User access token name")
  private String name;

  @Schema(description = "User access token expired date")
  private LocalDateTime expiredDate;

  @Schema(description = "User access token creator")
  private Long createdBy;

  @Schema(description = "User access token creation date")
  private LocalDateTime createdDate;

}
