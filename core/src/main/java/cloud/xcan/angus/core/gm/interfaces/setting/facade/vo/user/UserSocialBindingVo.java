package cloud.xcan.angus.core.gm.interfaces.setting.facade.vo.user;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class UserSocialBindingVo implements Serializable {

  private String wechatUserId;

  private LocalDateTime wechatUserBindDate;

  private String githubUserId;

  private LocalDateTime githubUserBindDate;

  private String googleUserId;

  private LocalDateTime googleUserBindDate;

}
