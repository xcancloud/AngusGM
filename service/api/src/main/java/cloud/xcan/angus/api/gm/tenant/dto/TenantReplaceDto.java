package cloud.xcan.angus.api.gm.tenant.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_ADDRESS_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_COUNTRY_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_EMAIL_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_ITC_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_MOBILE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_REMARK_LENGTH;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.tenant.cert.EnterpriseCert;
import cloud.xcan.angus.api.commonlink.tenant.cert.EnterpriseLegalPersonCert;
import cloud.xcan.angus.api.commonlink.tenant.cert.GovernmentCert;
import cloud.xcan.angus.api.commonlink.tenant.cert.PersonalCert;
import cloud.xcan.angus.api.enums.TenantType;
import cloud.xcan.angus.validator.Mobile;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Accessors(chain = true)
public class TenantReplaceDto implements Serializable {

  @NotNull
  @Schema(description = "Tenant identifier for replacement operation. Used for identifying the specific tenant for replacement",
      example = "1", requiredMode = RequiredMode.REQUIRED)
  private Long id;

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Tenant name for identification and display. Used for tenant management and organization",
      example = "XCan Company", requiredMode = RequiredMode.REQUIRED)
  private String name;

  @Schema(description = "Tenant type for classification and management. Used for determining tenant category and applicable policies")
  private TenantType type;

  @Length(max = MAX_REMARK_LENGTH)
  @Schema(description = "Tenant remark for additional information. Used for tenant documentation and management notes")
  private String remark;

  @Length(max = MAX_ADDRESS_LENGTH)
  @Schema(description = "Tenant contact address for location information. Used for contact details and regional compliance")
  private String address;

  @NotNull
  //@NotEmpty // Registered user firstName is empty
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "System administrator first name for user identification. Used for user profile and contact information", example = "James",
      requiredMode = RequiredMode.REQUIRED)
  private String firstName;

  @NotNull
  //@NotEmpty // Registered user firstName is empty
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "System administrator last name for user identification. Used for user profile and contact information", example = "Jones",
      requiredMode = RequiredMode.REQUIRED)
  private String lastName;

  @NotNull
  //@NotEmpty // Registered user firstName is empty
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "System administrator full name for user identification. Used for user profile and contact information", example = "James Jones",
      requiredMode = RequiredMode.REQUIRED)
  private String fullName;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "System administrator title for role identification. Used for organizational hierarchy and contact information", example = "CTO")
  private String title;

  @Length(max = MAX_ITC_LENGTH)
  @Schema(description = "International telephone area code for contact information. Used for phone number formatting and international calling", example = "86")
  private String itc;

  //@NotBlank
  @Length(max = MAX_COUNTRY_LENGTH)
  @Schema(description = "User country code for regional settings. Used for localization and regional compliance", example = "CN")
  private String country;

  @NotEmpty
  @Mobile
  @Length(max = MAX_MOBILE_LENGTH)
  @Schema(description = "System administrator mobile number for contact and verification. At least one mobile or email address is required",
      example = "13813000000", requiredMode = RequiredMode.REQUIRED)
  private String mobile;

  @NotEmpty
  @Email
  @Length(max = MAX_EMAIL_LENGTH)
  @Schema(description = "System administrator email address for contact and verification. At least one mobile or email address is required",
      example = "james@xcan.cloud", requiredMode = RequiredMode.REQUIRED)
  private String email;

  @Valid
  private PersonalCert personalCert;
  @Valid
  private EnterpriseCert enterpriseCert;
  @Valid
  private EnterpriseLegalPersonCert enterpriseLegalPersonCert;
  @Valid
  private GovernmentCert governmentCert;

  public boolean isCertSubmitted() {
    if (nonNull(type)) {
      return switch (type) {
        case GOVERNMENT -> nonNull(governmentCert);
        case ENTERPRISE -> nonNull(enterpriseCert) && nonNull(enterpriseLegalPersonCert);
        case PERSONAL -> nonNull(personalCert);
        default -> false;
      };
    }
    return false;
  }

  public boolean hasModifyUser() {
    return isNotEmpty(firstName) || isNotEmpty(lastName) || isNotEmpty(fullName)
        || isNotEmpty(title) || isNotEmpty(itc) || isNotEmpty(country) || isNotEmpty(mobile)
        || isNotEmpty(email);
  }

  public boolean hasModifyCert() {
    return nonNull(type) && (nonNull(governmentCert)
        || nonNull(enterpriseCert) || nonNull(enterpriseLegalPersonCert)
        || nonNull(personalCert));
  }
}
