package cloud.xcan.angus.core.gm.interfaces.app.facade.dto.org;

import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class UnAuthUserFindDto extends PageQuery {

  @Schema(description = "Unauthorized user id.")
  private Long id;

  @Schema(description = "Unauthorized user full name.")
  private String fullname;

}
