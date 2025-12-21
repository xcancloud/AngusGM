package cloud.xcan.angus.core.gm.interfaces.authentication.facade.vo.sign;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class UserSocialVo {

  private Long userId;

  private String linkSecret;

  private String wechatUserId;

  private String githubUserId;
}
