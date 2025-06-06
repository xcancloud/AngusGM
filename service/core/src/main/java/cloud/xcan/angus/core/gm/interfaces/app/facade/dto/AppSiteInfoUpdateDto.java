package cloud.xcan.angus.core.gm.interfaces.app.facade.dto;

import static cloud.xcan.angus.api.commonlink.AuthConstant.MAX_APP_FUNC_SHORT_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_URL_LENGTH;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;


@Getter
@Setter
@Accessors(chain = true)
public class AppSiteInfoUpdateDto {

  @NotNull
  @Schema(description = "Application id.", requiredMode = RequiredMode.REQUIRED)
  private Long id;

  @Length(max = MAX_APP_FUNC_SHORT_NAME_LENGTH)
  @Schema(description = "Application short name for web display.", example = "AngusTester", maxLength = MAX_APP_FUNC_SHORT_NAME_LENGTH)
  private String showName;

  @Length(max = MAX_URL_LENGTH)
  @Schema(description = "Application icon.", example = "http://dev-files.xcan.cloud/storage/api/v1/file/AngusTester.icon", maxLength = MAX_URL_LENGTH)
  private String icon;

  @Length(max = MAX_URL_LENGTH)
  @Schema(description = "Application access url. The URL used to access the application.", example = "http://dev-tester.xcan.cloud/")
  private String url;

}
