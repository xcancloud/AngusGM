package cloud.xcan.angus.core.gm.domain.user.directory.model;


import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH_X2;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH_X5;

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
public class DirectoryGroupSchema {

  @NotBlank
  @Length(max = MAX_CODE_LENGTH_X5)
  @Schema(description = "LDAP attribute objectClass value to search for when loading groups",
      example = "posixGroup", maxLength = MAX_CODE_LENGTH_X5, requiredMode = RequiredMode.REQUIRED)
  private String objectClass;

  @NotBlank
  @Length(max = MAX_CODE_LENGTH_X5)
  @Schema(description = "The filter to use when searching group objects",
      example = "(objectClass=posixGroup)", maxLength = MAX_CODE_LENGTH_X5, requiredMode = RequiredMode.REQUIRED)
  private String objectFilter;

  //Warning:: Not found in OPENLDAP query result, this may be a wrong idea.
  //@DoInFuture("After the group is modified, judge the group's unique flag when synchronously modifying the directory")
  //@Length(max = DEFAULT_CODE_LENGTH_X2)
  //@Schema(description = "The attribute field to group for tracking group identity across group renames", example = "entryUUID")
  //private String entryIdAttribute;

  @NotBlank
  @Length(max = MAX_CODE_LENGTH_X2)
  @Schema(description = "The attribute field to use when loading the group name", example = "cn",
      maxLength = MAX_CODE_LENGTH_X2, requiredMode = RequiredMode.REQUIRED)
  private String nameAttribute;

  @Length(max = MAX_CODE_LENGTH_X2)
  @Schema(description = "The attribute field to use when loading the group description",
      maxLength = MAX_CODE_LENGTH_X2)
  private String descriptionAttribute;

  @NotNull
  @Schema(description = "If true ignore the same name group, otherwise, save the same name group", requiredMode = RequiredMode.REQUIRED)
  private Boolean ignoreSameNameGroup;
}
