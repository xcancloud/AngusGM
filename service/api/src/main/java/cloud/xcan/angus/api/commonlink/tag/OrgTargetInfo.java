package cloud.xcan.angus.api.commonlink.tag;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class OrgTargetInfo {

  @Schema(description = "Org object type")
  private OrgTargetType type;

  @Schema(description = "Org object id")
  private Long id;

  @Schema(description = "Org object name")
  private String name;

  @Schema(description = "Org object avatar")
  private String avatar;

}
