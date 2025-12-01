package cloud.xcan.angus.api.gm.auth.vo;

import static cloud.xcan.angus.api.commonlink.UCConstant.GM_APP_CODE;

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

  @Schema(description = "User token hash value")
  private String hash;

  @Schema(description = "User access token expired date")
  private LocalDateTime expiredDate;

  @Schema(description = "Application code for Generating User Tokens", defaultValue = GM_APP_CODE)
  private String generateAppCode = GM_APP_CODE;

  @Schema(description = "User access token creator")
  private Long createdBy;

  @Schema(description = "User access token creation date")
  private LocalDateTime createdDate;

}
