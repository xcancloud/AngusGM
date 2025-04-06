package cloud.xcan.angus.api.commonlink.setting.tenant.event;

import cloud.xcan.angus.api.enums.NoticeType;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class TesterEvent {

  private String eventCode;

  private List<NoticeType> noticeTypes;

}
