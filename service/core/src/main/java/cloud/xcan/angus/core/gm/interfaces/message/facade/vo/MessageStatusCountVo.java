package cloud.xcan.angus.core.gm.interfaces.message.facade.vo;

import cloud.xcan.angus.core.gm.domain.message.MessageReadTab;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class MessageStatusCountVo implements Serializable {

  private MessageReadTab tab;

  private Boolean read;

  private Long count;
}
