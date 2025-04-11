package cloud.xcan.angus.core.gm.domain.user.directory.model;


import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH_X2;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH_X5;

import cloud.xcan.angus.api.enums.PassdEncoderType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;


@Getter
@Setter
@Accessors(chain = true)
public class DirectoryUserSchema implements Serializable {

  @NotBlank
  @Length(max = MAX_CODE_LENGTH_X5)
  @Schema(description = "The LDAP user object class type to use when loading users", example = "posixAccount",
      maxLength = MAX_CODE_LENGTH_X5, requiredMode = RequiredMode.REQUIRED)
  private String objectClass;

  @NotBlank
  @Length(max = MAX_CODE_LENGTH_X5)
  @Schema(description = "The filter to use when searching user objects", example = "(objectclass=posixAccount)",
      maxLength = MAX_CODE_LENGTH_X5, requiredMode = RequiredMode.REQUIRED)
  private String objectFilter;

  //Warning:: Not found in OPENLDAP query result, this may be a wrong idea.
  //@DoInFuture("After the user is modified, judge the user's unique flag when synchronously modifying the directory")
  //@Length(max = DEFAULT_CODE_LENGTH_X2)
  //@Schema(description = "The attribute field to use for tracking user identity across user renames", example = "entryUUID")
  //private String entryIdAttribute;

  @NotBlank
  @Length(max = MAX_CODE_LENGTH_X2)
  @Schema(description = "Attribute field corresponding to user unique name", example = "uid",
      maxLength = MAX_CODE_LENGTH_X2, requiredMode = RequiredMode.REQUIRED)
  private String usernameAttribute;

  @NotBlank
  @Length(max = MAX_CODE_LENGTH_X2)
  @Schema(description = "The attribute field to use when loading the user first name", example = "givenName",
      maxLength = MAX_CODE_LENGTH_X2, requiredMode = RequiredMode.REQUIRED)
  private String firstNameAttribute;

  @NotBlank
  @Length(max = MAX_CODE_LENGTH_X2)
  @Schema(description = "The attribute field to use when loading the user last name", example = "sn",
      maxLength = MAX_CODE_LENGTH_X2, requiredMode = RequiredMode.REQUIRED)
  private String lastNameAttribute;

  @NotBlank
  @Length(max = MAX_CODE_LENGTH_X2)
  @Schema(description = "The attribute field to use when loading the user full name (Fullname)", example = "cn",
      maxLength = MAX_CODE_LENGTH_X2, requiredMode = RequiredMode.REQUIRED)
  private String displayNameAttribute;

  @NotBlank
  @Length(max = MAX_CODE_LENGTH_X2)
  @Schema(description = "The attribute field to use when loading the user email", example = "mail",
      maxLength = MAX_CODE_LENGTH_X2, requiredMode = RequiredMode.REQUIRED)
  private String emailAttribute;

  @Length(max = MAX_CODE_LENGTH_X2)
  @Schema(description = "The attribute field to use when loading the user mobile", example = "mobile",
      maxLength = MAX_CODE_LENGTH_X2)
  private String mobileAttribute;

  @NotBlank
  @Length(max = MAX_CODE_LENGTH_X2)
  @Schema(description = "The attribute field to use when manipulating a user password", example = "userPassword",
      maxLength = MAX_CODE_LENGTH_X2, requiredMode = RequiredMode.REQUIRED)
  private String passwordAttribute;

  /**
   * The OPENLDAP password format of x is the same as that of AAS PasswordEncoder
   */
  @Schema(description = "Choose the encryption algorithm used for passwords on your directory")
  private PassdEncoderType passwordEncoderType;

  @NotNull
  @Schema(description = "Force ignore the same identity(unique name, email, mobile) user", requiredMode = RequiredMode.REQUIRED)
  private Boolean ignoreSameIdentityUser = true;
}
