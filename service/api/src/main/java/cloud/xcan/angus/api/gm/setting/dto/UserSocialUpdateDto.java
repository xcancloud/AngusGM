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
  @Schema(description = "Bind social type.", requiredMode = RequiredMode.REQUIRED)
  private SocialType type;

  @Length(max = MAX_SOCIAL_USER_ID_LENGTH)
  @Schema(description = "Bind WeChat user id.", maxLength = MAX_SOCIAL_USER_ID_LENGTH)
  private String wechatUserId;

  @Length(max = MAX_SOCIAL_USER_ID_LENGTH)
  @Schema(description = "Bind github user id.", maxLength = MAX_SOCIAL_USER_ID_LENGTH)
  private String githubUserId;

  @Length(max = MAX_SOCIAL_USER_ID_LENGTH)
  @Schema(description = "Bind google user id.", maxLength = MAX_SOCIAL_USER_ID_LENGTH)
  private String googleUserId;

}
