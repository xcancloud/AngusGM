package cloud.xcan.angus.core.gm.domain.user.directory.model;


import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH_X5;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_HOST_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH_X2;

import cloud.xcan.angus.validator.Port;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;


@Getter
@Setter
@Accessors(chain = true)
public class DirectoryServer {

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Server name", example = "XCanLdap", requiredMode = RequiredMode.REQUIRED)
  private String name;

  @NotNull
  @Schema(description = "Directory Type", example = "OPENLDAP", requiredMode = RequiredMode.REQUIRED)
  private DirectoryType directoryType;

  @NotBlank
  @Length(max = MAX_HOST_LENGTH)
  @Schema(description = "Hostname of the server running LDAP", example = "ldap.example.com", requiredMode = RequiredMode.REQUIRED)
  private String host;

  @Port
  @NotNull
  @Schema(description = "Server port, default 389 or ssl 636", example = "389", requiredMode = RequiredMode.REQUIRED)
  private Integer port;

  @NotNull
  @Schema(description = "Whether to use SSL flag, default false", example = "false", requiredMode = RequiredMode.REQUIRED)
  private Boolean ssl = false;

  @NotBlank
  @Length(max = MAX_NAME_LENGTH_X2)
  @Schema(description = "Auth username", example = "cn=admin,dc=example,dc=org", requiredMode = RequiredMode.REQUIRED)
  private String username;

  @NotBlank
  @Length(max = MAX_CODE_LENGTH_X5)
  @Schema(description = "Auth password", example = "admin", requiredMode = RequiredMode.REQUIRED)
  private String password;

}
