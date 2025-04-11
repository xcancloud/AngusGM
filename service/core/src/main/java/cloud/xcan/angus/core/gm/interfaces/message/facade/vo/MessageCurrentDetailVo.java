package cloud.xcan.angus.core.gm.interfaces.message.facade.vo;

import cloud.xcan.angus.remote.NameJoinField;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MessageCurrentDetailVo {

  private Long id;

  private Long messageId;

  private String title;

  private Boolean read;

  private Long userId;

  @NameJoinField(id = "userId", repository = "commonUserBaseRepo")
  private String fullName;

  private LocalDateTime sendDate;

  private String content;

}
