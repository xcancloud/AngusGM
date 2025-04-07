package cloud.xcan.angus.core.gm.domain.user.directory.model;


import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH_X2;

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
public class DirectoryMembershipSchema {

  /**
   * Attribute field name in group, OPENLDAP group attribute and user lists:
   * <pre>
   * memberUid
   *  dev.03
   *  dev.04-sha
   * </pre>
   */
  @NotBlank
  @Length(max = MAX_CODE_LENGTH_X2)
  @Schema(description = "The attribute field to use when loading the group members from the group",
      example = "memberUid", maxLength = MAX_CODE_LENGTH_X2, requiredMode = RequiredMode.REQUIRED)
  private String groupMemberAttribute;

  /**
   * Attribute field name in member(user)
   */
  @NotBlank
  @Length(max = MAX_CODE_LENGTH_X2)
  @Schema(description = "The attribute field to use when loading a user's groups",
      example = "gidNumber", maxLength = MAX_CODE_LENGTH_X2, requiredMode = RequiredMode.REQUIRED)
  private String memberGroupAttribute;

}
