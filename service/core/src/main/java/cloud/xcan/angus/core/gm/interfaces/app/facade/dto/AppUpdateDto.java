package cloud.xcan.angus.core.gm.interfaces.app.facade.dto;

import static cloud.xcan.angus.api.commonlink.AuthConstant.APP_MAX_TAG;
import static cloud.xcan.angus.api.commonlink.AuthConstant.MAX_APP_FUNC_API_NUM_AP;
import static cloud.xcan.angus.api.commonlink.AuthConstant.MAX_APP_FUNC_SHORT_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.DEFAULT_SEQUENCE;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_DESC_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_URL_LENGTH;

import cloud.xcan.angus.api.enums.AppType;
import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.api.enums.OpenStage;
import cloud.xcan.angus.validator.Version;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
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
public class AppUpdateDto {

  @NotNull
  @Schema(description = "Application identifier to update", requiredMode = RequiredMode.REQUIRED)
  private Long id;

  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "Application unique code identifier", example = "AngusTester")
  private String code;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Application display name", example = "AngusTester")
  private String name;

  @Length(max = MAX_APP_FUNC_SHORT_NAME_LENGTH)
  @Schema(description = "Application short display name for web interface", example = "AngusTester")
  private String showName;

  @Length(max = MAX_URL_LENGTH)
  @Schema(description = "Application icon URL for UI display", example = "http://dev-files.xcan.cloud/storage/api/v1/file/AngusTester.icon")
  private String icon;

  @Schema(description = """
      Application type.\s
      ***CLOUD_APP (Cloud Applications)***: Deliver scalable, on-demand services via cloud infrastructure, enabling remote access and elastic resource allocation.
      ***BASE_APP (Core Base Applications)***: Provide essential system functionalities and foundational services (e.g., authentication, data storage, system management) critical for platform operations.
      ***OP_APP (Operational Applications)***: The operations platform centralizes and orchestrates business processes, real-time analytics, and system monitoring to enhance operational efficiency""")
  private AppType type;

  @Schema(description = "Application edition type")
  private EditionType editionType;

  @Length(max = MAX_DESC_LENGTH)
  @Schema(description = "Application detailed description")
  private String description;

  @Schema(description =
      "Enable authorization control for applications. If enabled, users must be granted "
          + "corresponding access policies to use the application")
  private Boolean authCtrl;

  @Length(max = MAX_URL_LENGTH)
  @Schema(description = "Application access URL for user navigation", example = "http://dev-tester.xcan.cloud/")
  private String url;

  @Positive
  @Schema(description = "Application display sequence order. Supports positive integers, "
      + "smaller numbers appear earlier in the list", defaultValue = "1000", minimum = "1", example = "1000")
  private Integer sequence = DEFAULT_SEQUENCE;

  @Size(max = MAX_APP_FUNC_API_NUM_AP)
  @Schema(description =
      "Authorization API identifiers for application homepage access. The API endpoints "
          + "invoked when accessing the application to validate permissions")
  private LinkedHashSet<Long> apiIds;

  @Version
  @Schema(description = "Application software version")
  private String version;

  @Schema(description = """
      Application activation stage:

      ***SIGNUP***: Automatically activates the application upon successful user registration.
      ***AUTH_PASSED***: Activates the application after real-name authentication is verified.
      ***OPEN_SUCCESS***: Activates the application once manually enabled by the user""")
  private OpenStage openStage;

  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "OAuth2 client identifier", example = "xcan_tp")
  private String clientId;

  @Size(max = APP_MAX_TAG)
  @Schema(description = "Application tag identifiers for categorization. Use tags to organize applications and functions")
  private LinkedHashSet<Long> tagIds;

}
