package cloud.xcan.angus.core.gm.interfaces.tag.facade.vo;

import cloud.xcan.angus.api.commonlink.app.tag.WebTagTargetType;
import cloud.xcan.angus.remote.NameJoinField;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class WebTagTargetVo {

  private Long tagId;

  private String tagName;

  private Long targetId;

  private WebTagTargetType targetType;

  private String targetName;

  private Long createdBy;

  @NameJoinField(id = "createdBy", repository = "commonUserBaseRepo")
  private String createdByName;

  private LocalDateTime createdDate;

  private Long targetCreatedBy;

  @NameJoinField(id = "targetCreatedBy", repository = "commonUserBaseRepo")
  private String targetCreatedByName;

  private LocalDateTime targetCreatedDate;

}
