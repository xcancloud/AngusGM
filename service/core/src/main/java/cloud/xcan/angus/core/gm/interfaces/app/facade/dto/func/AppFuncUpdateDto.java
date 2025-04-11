package cloud.xcan.angus.core.gm.interfaces.app.facade.dto.func;

import static cloud.xcan.angus.api.commonlink.AASConstant.APP_MAX_TAG;
import static cloud.xcan.angus.api.commonlink.AASConstant.MAX_APP_FUNC_API_NUM_AP;
import static cloud.xcan.angus.api.commonlink.AASConstant.MAX_APP_FUNC_SHORT_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.DEFAULT_ROOT_PID;
import static cloud.xcan.angus.spec.experimental.BizConstant.DEFAULT_SEQUENCE;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_DESC_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_URL_LENGTH;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.LinkedHashSet;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;


@Getter
@Setter
@Accessors(chain = true)
public class AppFuncUpdateDto {

  @NotNull
  @Schema(example = "1", requiredMode = RequiredMode.REQUIRED)
  private Long id;

  @Length(max = MAX_CODE_LENGTH)
  @Schema(example = "script:add")
  private String code;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(example = "Add angus script")
  private String name;

  @Length(max = MAX_APP_FUNC_SHORT_NAME_LENGTH)
  @Schema(example = "Add script")
  private String showName;

  @Min(DEFAULT_ROOT_PID)
  private Long pid;

  @Length(max = MAX_URL_LENGTH)
  @Schema(example = "http://dev-files.xcan.cloud/storage/api/v1/file/Menu01.icon")
  private String icon;

  //Modification not allowed
  //private AppFuncType type;

  @Length(max = MAX_DESC_LENGTH)
  @Schema(example = "Add angus script")
  private String description;

  private Boolean authCtrl;

  @Length(max = MAX_URL_LENGTH)
  @Schema(example = "http://dev-at.xcan.cloud/scripts")
  private String url;

  @Positive
  @Schema(example = DEFAULT_SEQUENCE + "")
  private Integer sequence;

  @Size(max = MAX_APP_FUNC_API_NUM_AP)
  @Schema(description = "Application homepage associated api ids")
  private LinkedHashSet<Long> apiIds;

  @Size(max = APP_MAX_TAG)
  @Schema(description = "Application tag ids")
  private LinkedHashSet<Long> tagIds;

}
