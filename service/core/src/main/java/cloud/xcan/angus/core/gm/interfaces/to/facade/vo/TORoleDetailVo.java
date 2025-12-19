package cloud.xcan.angus.core.gm.interfaces.to.facade.vo;

import cloud.xcan.angus.remote.NameJoinField;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class TORoleDetailVo {

  private Long id;

  private String name;

  private String code;

  private Long appId;

  @NameJoinField(id = "appId", repository = "appRepo")
  private String appName;

  private String description;

  private Boolean enabled;

  private LocalDateTime createdDate;

  private Long createdBy;

  @NameJoinField(id = "createdBy", repository = "commonUserBaseRepo")
  private String creator;

  private List<TORoleUserVo> toUsers;

}
