package cloud.xcan.angus.api.gm.tenant.dto;

import static cloud.xcan.angus.api.commonlink.CommonConstant.MIN_PASSWORD_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_ADDRESS_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_COUNTRY_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_EMAIL_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_ITC_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_MOBILE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_REMARK_LENGTH;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.tenant.TenantType;
import cloud.xcan.angus.api.commonlink.tenant.cert.EnterpriseCert;
import cloud.xcan.angus.api.commonlink.tenant.cert.EnterpriseLegalPersonCert;
import cloud.xcan.angus.api.commonlink.tenant.cert.GovernmentCert;
import cloud.xcan.angus.api.commonlink.tenant.cert.PersonalCert;
import cloud.xcan.angus.validator.Mobile;
import cloud.xcan.angus.validator.Passd;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
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
public class TenantAddDto implements Serializable {

  @NotNull
  @Schema(description = "Tenant type.", requiredMode = RequiredMode.REQUIRED)
  private TenantType type;

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Tenant name.", example = "XCan Company",
      maxLength = MAX_NAME_LENGTH, requiredMode = RequiredMode.REQUIRED)
  private String name;

  @Length(max = MAX_REMARK_LENGTH)
  @Schema(description = "Tenant remark.", maxLength = MAX_REMARK_LENGTH)
  private String remark;

  @Length(max = MAX_ADDRESS_LENGTH)
  @Schema(example = "Tenant contact address.", maxLength = MAX_ADDRESS_LENGTH)
  private String address;

  @NotNull
  //@NotEmpty // Registered user firstName is empty
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "System administer first name.", example = "James",
      maxLength = MAX_NAME_LENGTH, requiredMode = RequiredMode.REQUIRED)
  private String firstName;

  @NotNull
  //@NotEmpty // Registered user firstName is empty
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "System administer last name.", example = "Jones",
      maxLength = MAX_NAME_LENGTH, requiredMode = RequiredMode.REQUIRED)
  private String lastName;

  @NotNull
  //@NotEmpty // Registered user firstName is empty
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "System administer full name.", example = "James Jones",
      maxLength = MAX_NAME_LENGTH, requiredMode = RequiredMode.REQUIRED)
  private String fullName;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "System administer title.", example = "CTO",
      maxLength = MAX_NAME_LENGTH)
  private String title;

  @Length(max = MAX_ITC_LENGTH)
  @Schema(description = "International telephone area code", example = "86",
      maxLength = MAX_ITC_LENGTH)
  private String itc;

  @Length(max = MAX_COUNTRY_LENGTH)
  @Schema(description = "User country code", example = "CN",
      maxLength = MAX_COUNTRY_LENGTH)
  private String country;

  @NotBlank
  @Mobile
  @Length(max = MAX_MOBILE_LENGTH)
  @Schema(description = "System administer mobile. There must be one mobile and email address.",
      example = "13813000000", requiredMode = RequiredMode.REQUIRED)
  private String mobile;

  @NotBlank
  @Email
  @Length(max = MAX_EMAIL_LENGTH)
  @Schema(description = "System administer email. There must be one mobile and email address.",
      example = "james@xcan.cloud", requiredMode = RequiredMode.REQUIRED)
  private String email;

  @Passd(allowNull = true)
  @Schema(description = "System administer password.", example = "Passd@123",
      minLength = MIN_PASSWORD_LENGTH, maxLength = MAX_COUNTRY_LENGTH)
  private String password;

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

}
