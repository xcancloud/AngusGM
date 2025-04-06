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
public class AuthAppOrgSearchDto extends PageQuery {

  @Schema(description = "Organization (user, group or department) id.")
  private Long id;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Organization (user, group or department) name.",
      maxLength = MAX_NAME_LENGTH)
  private String name;

}
