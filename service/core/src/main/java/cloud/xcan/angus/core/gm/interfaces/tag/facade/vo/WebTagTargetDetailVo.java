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
public class WebTagTargetDetailVo {

  private Long id;

  private WebTagTargetType targetType;

  private Long targetId;

  private String targetName;

  private Long tagId;

  private String tagName;

  private LocalDateTime createdDate;

  private Long createdBy;

  @NameJoinField(id = "createdBy", repository = "commonUserBaseRepo")
  private String createdByName;

  private LocalDateTime targetCreatedDate;

  private Long targetCreatedBy;

  @NameJoinField(id = "createdBy", repository = "commonUserBaseRepo")
  private String targetCreatedByName;

}
