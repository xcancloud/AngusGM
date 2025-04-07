package cloud.xcan.angus.api.gm.tenant.dto;

import static cloud.xcan.angus.api.commonlink.CommonConstant.MAX_PASSWORD_LENGTH;
import static cloud.xcan.angus.api.commonlink.CommonConstant.MIN_PASSWORD_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_COUNTRY_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_EMAIL_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_ITC_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_MOBILE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import cloud.xcan.angus.validator.Mobile;
import cloud.xcan.angus.validator.Passd;
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
  @Schema(description = "Tenant name.", example = "XCan Company", maxLength = MAX_NAME_LENGTH)
  private String name;

  //@NotEmpty // Registered user firstName is empty
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "System administer first name.", example = "James", maxLength = MAX_NAME_LENGTH)
  private String firstName;

  //@NotEmpty // Registered user firstName is empty
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "System administer last name.", example = "Jones", maxLength = MAX_NAME_LENGTH)
  private String lastName;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "System administer full name.", example = "James Jones", maxLength = MAX_NAME_LENGTH)
  private String fullName;

  @Length(max = MAX_ITC_LENGTH)
  @Schema(description = "International telephone area code.",
      example = "86", maxLength = MAX_ITC_LENGTH)
  private String itc;

  //@NotBlank
  @Length(max = MAX_COUNTRY_LENGTH)
  @Schema(description = "User country code",
      example = "CN", maxLength = MAX_COUNTRY_LENGTH /*, requiredMode = RequiredMode.REQUIRED*/)
  private String country;

  @NotBlank
  @Mobile
  @Length(max = MAX_MOBILE_LENGTH)
  @Schema(description = "System administer mobile. There must be one mobile and email address.",
      example = "13813000000", maxLength = MAX_MOBILE_LENGTH, requiredMode = RequiredMode.REQUIRED)
  private String mobile;

  @Length(max = MAX_EMAIL_LENGTH)
  @Email
  @Schema(description = "System administer email. There must be one mobile and email address.",
      example = "james@xcan.cloud", maxLength = MAX_EMAIL_LENGTH)
  private String email;

  @Passd(allowNull = true)
  @Schema(description = "System administer password.", example = "Passd@123",
      minLength = MIN_PASSWORD_LENGTH, maxLength = MAX_PASSWORD_LENGTH)
  private String passd;

}
