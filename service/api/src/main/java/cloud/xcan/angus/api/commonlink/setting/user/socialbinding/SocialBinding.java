package cloud.xcan.angus.api.commonlink.setting.user.socialbinding;

import cloud.xcan.angus.spec.experimental.ValueObjectSupport;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class SocialBinding extends ValueObjectSupport<SocialBinding> {

  @Schema(description = "Bind WeChat user id")
  private String wechatUserId;

  @Schema(description = "Bind WeChat user date")
  private LocalDateTime wechatUserBindDate;

  @Schema(description = "Bind github user id")
  private String githubUserId;

  @Schema(description = "Bind github user date")
  private LocalDateTime githubUserBindDate;

  @Schema(description = "Bind google user id")
  private String googleUserId;

  @Schema(description = "Bind google user date")
  private LocalDateTime googleUserBindDate;

}
