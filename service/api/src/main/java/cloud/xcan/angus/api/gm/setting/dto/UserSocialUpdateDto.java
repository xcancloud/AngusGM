package cloud.xcan.angus.api.gm.setting.dto;

import static cloud.xcan.angus.api.commonlink.CommonConstant.MAX_SOCIAL_USER_ID_LENGTH;

import cloud.xcan.angus.api.enums.SocialType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Setter
@Getter
@Accessors(chain = true)
public class UserSocialUpdateDto implements Serializable {

  @NotNull
  @Schema(description = "Social platform type for user account binding. Used for identifying the social platform for account linkage", requiredMode = RequiredMode.REQUIRED)
  private SocialType type;

  @Length(max = MAX_SOCIAL_USER_ID_LENGTH)
  @Schema(description = "WeChat user identifier for social account binding. Used for WeChat platform integration and user identification")
  private String wechatUserId;

  @Length(max = MAX_SOCIAL_USER_ID_LENGTH)
  @Schema(description = "GitHub user identifier for social account binding. Used for GitHub platform integration and user identification")
  private String githubUserId;

  @Length(max = MAX_SOCIAL_USER_ID_LENGTH)
  @Schema(description = "Google user identifier for social account binding. Used for Google platform integration and user identification")
  private String googleUserId;

}
