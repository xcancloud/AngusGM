package cloud.xcan.angus.core.gm.domain.user.directory.model;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH_X5;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;


@Getter
@Setter
@Accessors(chain = true)
public class DirectorySchema {

  @NotBlank
  @Length(max = MAX_CODE_LENGTH_X5)
  @Schema(description = "Base DN, Root node in LDAP from which to search for users and groups.",
      example = "dc=example,dc=org", maxLength = MAX_CODE_LENGTH_X5, requiredMode = RequiredMode.REQUIRED)
  private String baseDn;

  @Length(max = MAX_CODE_LENGTH_X5)
  @Schema(description = "Additional User DN, Prepended to the base DN to limit the scope when searching for users.",
      example = "ou=user", maxLength = MAX_CODE_LENGTH_X5)
  private String additionalUserDn;

  @Length(max = MAX_CODE_LENGTH_X5)
  @Schema(description = "Additional Group DN, Prepended to the base DN to limit the scope when searching for groups.",
      example = "ou=group", maxLength = MAX_CODE_LENGTH_X5)
  private String additionalGroupDn;

}
