package cloud.xcan.angus.core.gm.interfaces.app.facade.dto.org;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;


@Getter
@Setter
@Accessors(chain = true)
public class AuthAppOrgUserSearchDto extends PageQuery {

  @Schema(description = "Authorized user id.")
  private Long id;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Authorized user full name.")
  private String fullName;

}
