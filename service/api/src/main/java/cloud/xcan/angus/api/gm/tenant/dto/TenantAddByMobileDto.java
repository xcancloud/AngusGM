package cloud.xcan.angus.api.gm.tenant.dto;

import static cloud.xcan.angus.api.commonlink.CommonConstant.MAX_PASSWORD_LENGTH;
import static cloud.xcan.angus.api.commonlink.CommonConstant.MIN_PASSWORD_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_COUNTRY_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_EMAIL_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_ITC_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_MOBILE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import cloud.xcan.angus.validator.Mobile;
import cloud.xcan.angus.validator.Password;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;


@Getter
@Setter
@Accessors(chain = true)
public class TenantAddByMobileDto implements Serializable {

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Tenant name for identification and display. Used for tenant management and organization", example = "XCan Company")
  private String name;

  //@NotEmpty // Registered user firstName is empty
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "System administrator first name for user identification. Used for user profile and contact information", example = "James")
  private String firstName;

  //@NotEmpty // Registered user firstName is empty
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "System administrator last name for user identification. Used for user profile and contact information", example = "Jones")
  private String lastName;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "System administrator full name for user identification. Used for user profile and contact information", example = "James Jones")
  private String fullName;

  @Length(max = MAX_ITC_LENGTH)
  @Schema(description = "International telephone area code for contact information. Used for phone number formatting and international calling",
      example = "86")
  private String itc;

  //@NotBlank
  @Length(max = MAX_COUNTRY_LENGTH)
  @Schema(description = "User country code for regional settings. Used for localization and regional compliance",
      example = "CN")
  private String country;

  @NotBlank
  @Mobile
  @Length(max = MAX_MOBILE_LENGTH)
  @Schema(description = "System administrator mobile number for contact and verification. At least one mobile or email address is required",
      example = "13813000000", requiredMode = RequiredMode.REQUIRED)
  private String mobile;

  @Length(max = MAX_EMAIL_LENGTH)
  @Email
  @Schema(description = "System administrator email address for contact and verification. At least one mobile or email address is required",
      example = "james@xcan.cloud")
  private String email;

  @Password(allowNull = true)
  @Schema(description = "System administrator password for account security. Used for account authentication and access control", example = "Password@123",
      minLength = MIN_PASSWORD_LENGTH, maxLength = MAX_PASSWORD_LENGTH)
  private String password;

}
