package cloud.xcan.angus.core.gm.interfaces.app.facade.dto.func;

import static cloud.xcan.angus.api.commonlink.AuthConstant.APP_MAX_TAG;
import static cloud.xcan.angus.api.commonlink.AuthConstant.MAX_APP_FUNC_API_NUM_AP;
import static cloud.xcan.angus.api.commonlink.AuthConstant.MAX_APP_FUNC_SHORT_NAME_LENGTH;
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
  @Schema(description = "Application function identifier to update", example = "1", requiredMode = RequiredMode.REQUIRED)
  private Long id;

  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "Application function unique code identifier", example = "script:add")
  private String code;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Application function display name", example = "Add angus script")
  private String name;

  @Length(max = MAX_APP_FUNC_SHORT_NAME_LENGTH)
  @Schema(description = "Application function short display name for UI", example = "Add script")
  private String showName;

  @Min(DEFAULT_ROOT_PID)
  @Schema(description = "Parent function identifier for hierarchical structure")
  private Long pid;

  @Length(max = MAX_URL_LENGTH)
  @Schema(description = "Function icon URL for UI display", example = "http://dev-files.xcan.cloud/storage/api/v1/file/Menu01.icon")
  private String icon;

  //Modification not allowed
  //private AppFuncType type;

  @Length(max = MAX_DESC_LENGTH)
  @Schema(description = "Application function description", example = "Add angus script")
  private String description;

  @Schema(description = "Enable authorization control for this function")
  private Boolean authCtrl;

  @Length(max = MAX_URL_LENGTH)
  @Schema(description = "Function access URL", example = "http://dev-tester.xcan.cloud/scripts")
  private String url;

  @Positive
  @Schema(description = "Function display sequence order", example = DEFAULT_SEQUENCE + "")
  private Integer sequence;

  @Size(max = MAX_APP_FUNC_API_NUM_AP)
  @Schema(description = "Associated API identifiers for function access control")
  private LinkedHashSet<Long> apiIds;

  @Size(max = APP_MAX_TAG)
  @Schema(description = "Function tag identifiers for categorization")
  private LinkedHashSet<Long> tagIds;

}
