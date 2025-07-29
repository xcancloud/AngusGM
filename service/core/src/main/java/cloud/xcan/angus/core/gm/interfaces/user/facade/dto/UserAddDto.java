package cloud.xcan.angus.core.gm.interfaces.user.facade.dto;


import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_ADDRESS_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_COUNTRY_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_EMAIL_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_ITC_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_LANDLINE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_MOBILE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_RELATION_QUOTA;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_URL_LENGTH_X2;

import cloud.xcan.angus.api.commonlink.user.SignupType;
import cloud.xcan.angus.api.enums.Gender;
import cloud.xcan.angus.api.gm.user.to.UserDeptTo;
import cloud.xcan.angus.validator.Mobile;
import cloud.xcan.angus.validator.Password;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.LinkedHashSet;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Setter
@Getter
@Accessors(chain = true)
public class UserAddDto implements Serializable {

  @JsonIgnore
  @Schema(hidden = true)
  private Long id;

  /**
   * Note: Is empty when signup add.
   */
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Unique username for system identification (empty during signup)")
  private String username;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "User's first name", example = "James")
  private String firstName;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "User's last name", example = "Jones")
  private String lastName;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "User's complete full name", example = "James Jones")
  private String fullName;

  @Length(max = MAX_COUNTRY_LENGTH)
  @Schema(description = "User's country code", example = "CN")
  private String country;

  @Length(max = MAX_ITC_LENGTH)
  @Schema(description = "International telephone country code", example = "86")
  private String itc;

  @Mobile
  @Length(max = MAX_MOBILE_LENGTH)
  @Schema(description = "User's mobile phone number", example = "13813000000")
  private String mobile;

  @Email
  @Length(max = MAX_EMAIL_LENGTH)
  @Schema(description = "User's email address", example = "james@xcan.cloud")
  private String email;

  @Length(max = MAX_LANDLINE_LENGTH)
  @Schema(description = "User's landline phone number", example = "010-88287890")
  private String landline;

  @Password
  @Schema(description = "User's login password for system authentication",
      example = "xcan@123", requiredMode = RequiredMode.REQUIRED)
  private String password;

  @Length(max = MAX_URL_LENGTH_X2)
  @Schema(description = "User's profile picture URL", example = "http://prod-files.xcan.cloud/storage/pubapi/v1/file/logo.png")
  private String avatar;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "User's job title or position", example = "CTO")
  private String title;

  @Schema(description = "User's gender identity", example = "MALE", defaultValue = "UNKNOWN")
  private Gender gender = Gender.UNKNOWN;

  @Length(max = MAX_ADDRESS_LENGTH)
  @Schema(description = "User's residential or contact address")
  private String address;

  @Schema(description = "User registration method type", example = "EMAIL")
  private SignupType signupType;

  @Length(max = MAX_EMAIL_LENGTH)
  @Schema(description = "Account used during registration (email or mobile number)", example = "Jams@123@xcan.cloud")
  private String signupAccount;

  @Schema(description = "System administrator privilege flag", defaultValue = "false", example = "false")
  private Boolean sysAdmin = false;

  @Schema(description = "User account activation status", defaultValue = "true", example = "true")
  private Boolean enabled = true;

  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "Invitation code for joining existing tenant account")
  private String invitationCode;

  @Valid
  @Size(max = MAX_RELATION_QUOTA)
  @Schema(description = "User's department assignments (maximum 2000 departments)")
  private LinkedHashSet<UserDeptTo> depts;

  @Size(max = MAX_RELATION_QUOTA)
  @Schema(description = "User's group memberships (maximum 2000 groups)")
  private LinkedHashSet<Long> groupIds;

  @Size(max = MAX_RELATION_QUOTA)
  @Schema(description = "User's associated tag assignments (maximum 2000 tags)")
  private LinkedHashSet<Long> tagIds;

}


