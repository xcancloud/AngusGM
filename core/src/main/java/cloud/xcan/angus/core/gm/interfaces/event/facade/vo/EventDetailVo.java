package cloud.xcan.angus.core.gm.interfaces.event.facade.vo;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;

import cloud.xcan.angus.api.enums.EventType;
import cloud.xcan.angus.core.event.source.EventContent;
import cloud.xcan.angus.core.gm.domain.event.push.EventPushStatus;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class EventDetailVo implements Serializable {

  private Long id;

  private Long userId;

  private String fullname;

  private Long tenantId;

  private String tenantName;

  private String code;

  private String name;

  private String description;

  private String eKey;

  private EventType type;

  private LocalDateTime createdDate;

  private EventContent source;

  private String targetId;

  private String targetType;

  private String targetName;

  private String appCode;

  private String serviceCode;

  private String eventViewUrl;

  private EventPushStatus pushStatus;

  private String pushMsg;

  public String getName() {
    return isEmpty(name) ? "--" : name;
  }
}
