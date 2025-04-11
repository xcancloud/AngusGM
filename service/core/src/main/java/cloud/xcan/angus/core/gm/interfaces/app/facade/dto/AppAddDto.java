package cloud.xcan.angus.core.gm.interfaces.app.facade.dto;

import static cloud.xcan.angus.api.commonlink.AASConstant.APP_MAX_TAG;
import static cloud.xcan.angus.api.commonlink.AASConstant.MAX_APP_FUNC_API_NUM_AP;
import static cloud.xcan.angus.api.commonlink.AASConstant.MAX_APP_FUNC_SHORT_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.DEFAULT_SEQUENCE;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_DESC_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_URL_LENGTH;

import cloud.xcan.angus.api.commonlink.app.AppType;
import cloud.xcan.angus.api.commonlink.app.OpenStage;
import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.validator.Version;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.LinkedHashSet;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;


@Valid
@Getter
@Setter
@Accessors(chain = true)
public class AppAddDto {

  @NotEmpty
  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "Application code.", example = "AngusTester",
      maxLength = MAX_CODE_LENGTH, requiredMode = RequiredMode.REQUIRED)
  private String code;

  @NotEmpty
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Application name.", example = "AngusTester",
      maxLength = MAX_NAME_LENGTH, requiredMode = RequiredMode.REQUIRED)
  private String name;

  @NotEmpty
  @Length(max = MAX_APP_FUNC_SHORT_NAME_LENGTH)
  @Schema(description = "Application short name for web display.", example = "AngusTester",
      maxLength = MAX_APP_FUNC_SHORT_NAME_LENGTH, requiredMode = RequiredMode.REQUIRED)
  private String showName;

  @NotEmpty
  @Length(max = MAX_URL_LENGTH)
  @Schema(description = "Application icon.", example = "http://dev-files.xcan.cloud/storage/api/v1/file/AngusTester.icon",
      maxLength = MAX_URL_LENGTH, requiredMode = RequiredMode.REQUIRED)
  private String icon;

  @NotNull
  @Schema(description = """
      Application type.\s
      ***CLOUD_APP (Cloud Applications)***: Deliver scalable, on-demand services via cloud infrastructure, enabling remote access and elastic resource allocation.
      ***BASE_APP (Core Base Applications)***: Provide essential system functionalities and foundational services (e.g., authentication, data storage, system management) critical for platform operations.
      ***OP_APP (Operational Applications)***: The operations platform centralizes and orchestrates business processes, real-time analytics, and system monitoring to enhance operational efficiency.""", requiredMode = RequiredMode.REQUIRED)
  private AppType type;

  @NotNull
  @Schema(description = "Application edition type.", requiredMode = RequiredMode.REQUIRED)
  private EditionType editionType;

  @Length(max = MAX_DESC_LENGTH)
  @Schema(description = "Application detailed introduction.", maxLength = MAX_DESC_LENGTH)
  private String description;

  @NotNull
  @Schema(description = "Authorization control for applications. If enabled, users must be granted "
      + "corresponding access policies to use the application.", requiredMode = RequiredMode.REQUIRED)
  private Boolean authCtrl;

  @NotEmpty
  @Length(max = MAX_URL_LENGTH)
  @Schema(description = "Application access url. The URL used to access the application.",
      example = "http://dev-at.xcan.cloud/", requiredMode = RequiredMode.REQUIRED)
  private String url;

  @Positive
  @Schema(description = "Application sorting sequence value. Supports positive integers, "
      + "smaller numbers appear earlier in the list.", defaultValue = "1000",
      minimum = "1", example = "1000")
  private Integer sequence = DEFAULT_SEQUENCE;

  @Size(max = MAX_APP_FUNC_API_NUM_AP)
  @Schema(description = "Authorization apis for application homepage access. The API endpoint "
      + "invoked when accessing the application to validate permissions. Supports a maximum quantity of `50`.")
  private LinkedHashSet<Long> apiIds;

  @NotNull
  @Version
  @Schema(description = "Application software version.", requiredMode = RequiredMode.REQUIRED)
  private String version;

  @NotNull
  @Schema(description = """
      Application open or activation stage:

      ***SIGNUP***: Automatically activates the application upon successful user registration.
      ***AUTH_PASSED***: Activates the application after real-name authentication is verified.
      ***OPEN_SUCCESS***: Activates the application once manually enabled by the user.""", requiredMode = RequiredMode.REQUIRED)
  private OpenStage openStage;

  @NotEmpty
  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "OAuh2 client id.", example = "xcan_tp",
      maxLength = MAX_CODE_LENGTH, requiredMode = RequiredMode.REQUIRED)
  private String clientId;

  @Size(max = APP_MAX_TAG)
  @Schema(description = "Application tag ids. Use tags to organize applications and functions. Supports a maximum quantity of `10`.")
  private LinkedHashSet<Long> tagIds;

}
