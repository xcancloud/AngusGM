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

import cloud.xcan.angus.api.commonlink.app.func.AppFuncType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
public class AppFuncAddDto {

  @NotBlank
  @Length(max = MAX_CODE_LENGTH)
  @Schema(example = "script:add", requiredMode = RequiredMode.REQUIRED)
  private String code;

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(example = "Add angus script", requiredMode = RequiredMode.REQUIRED)
  private String name;

  @NotBlank
  @Length(max = MAX_APP_FUNC_SHORT_NAME_LENGTH)
  @Schema(example = "Add script", requiredMode = RequiredMode.REQUIRED)
  private String showName;

  @Min(DEFAULT_ROOT_PID)
  private Long pid;

  @Length(max = MAX_URL_LENGTH)
  @Schema(example = "http://dev-files.xcan.cloud/storage/api/v1/file/Menu01.icon", requiredMode = RequiredMode.REQUIRED)
  private String icon;

  @NotNull
  @Schema(requiredMode = RequiredMode.REQUIRED)
  private AppFuncType type;

  @Length(max = MAX_DESC_LENGTH)
  @Schema(example = "Add angus script")
  private String description;

  @NotNull
  @Schema(example = "true")
  private Boolean authCtrl;

  @Length(max = MAX_URL_LENGTH)
  @Schema(example = "http://dev-tester.xcan.cloud/scripts")
  private String url;

  @Positive
  @Schema(example = DEFAULT_SEQUENCE + "")
  private Integer sequence;

  @Size(max = MAX_APP_FUNC_API_NUM_AP)
  @Schema(description = "Function associated api ids")
  private LinkedHashSet<Long> apiIds;

  @Size(max = APP_MAX_TAG)
  @Schema(description = "Function tag ids")
  private LinkedHashSet<Long> tagIds;

}
