package cloud.xcan.angus.core.gm.interfaces.system.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import cloud.xcan.angus.api.commonlink.AuthConstant;
import cloud.xcan.angus.api.enums.ResourceAuthType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;


@Getter
@Setter
@Accessors(chain = true)
public class SystemTokenAddDto {

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "System token display name for identification", requiredMode = RequiredMode.REQUIRED)
  private String name;

  /**
   * Valid within 24 hours by default
   *
   * @see AuthConstant#DEFAULT_TOKEN_EXPIRE_SECOND
   */
  @Future
  @Schema(description = "System token expiration date and time", example = "2025-10-09 03:02:29")
  private LocalDateTime expiredDate;

  @NotNull
  @Schema(description = "Resource authorization type for access control", requiredMode = RequiredMode.REQUIRED)
  private ResourceAuthType authType;

  @Valid
  @NotEmpty
  @Schema(description = "Authorized resource configurations for the token", requiredMode = RequiredMode.REQUIRED)
  private List<AuthorizedResourceDto> resources;

}
