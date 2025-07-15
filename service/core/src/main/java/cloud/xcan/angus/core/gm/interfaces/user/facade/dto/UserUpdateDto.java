package cloud.xcan.angus.core.gm.interfaces.user.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_ADDRESS_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_COUNTRY_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_EMAIL_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_ITC_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_LANDLINE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_MOBILE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_RELATION_QUOTA;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_URL_LENGTH_X2;

import cloud.xcan.angus.api.enums.Gender;
import cloud.xcan.angus.api.gm.user.to.UserDeptTo;
import cloud.xcan.angus.core.biz.ResourceName;
import cloud.xcan.angus.validator.Mobile;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
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
public class UserUpdateDto implements Serializable {

  @NotNull
  @Schema(description = "User id", requiredMode = RequiredMode.REQUIRED)
  private Long id;

  /**
   * Note: Is empty when signup add.
   */
  @ResourceName
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Username, unique user identification", maxLength = MAX_NAME_LENGTH)
  private String username;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "User first name", example = "James", maxLength = MAX_NAME_LENGTH)
  private String firstName;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "User last name", example = "Jones", maxLength = MAX_NAME_LENGTH)
  private String lastName;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "User full name", example = "James Jones", maxLength = MAX_NAME_LENGTH)
  private String fullName;

  @Length(max = MAX_COUNTRY_LENGTH)
  @Schema(description = "User country code", example = "CN", maxLength = MAX_COUNTRY_LENGTH)
  private String country;

  @Length(max = MAX_ITC_LENGTH)
  @Schema(description = "International telephone area code", example = "86", maxLength = MAX_ITC_LENGTH)
  private String itc;

  @Mobile
  @Length(max = MAX_MOBILE_LENGTH)
  @Schema(description = "User mobile", example = "13813000000", maxLength = MAX_MOBILE_LENGTH)
  private String mobile;

  @Email
  @Length(max = MAX_EMAIL_LENGTH)
  @Schema(description = "User e-mail", example = "james@xcan.cloud", maxLength = MAX_EMAIL_LENGTH)
  private String email;

  @Length(max = MAX_LANDLINE_LENGTH)
  @Schema(description = "User landline", example = "010-88287890", maxLength = MAX_LANDLINE_LENGTH)
  private String landline;

  //  @Password(allowNull = true)
  //  @Schema(description = "User signup password, used to log in to the system through the password", example = "xcan@123")
  //  private String password;

  @Length(max = MAX_URL_LENGTH_X2)
  @Schema(description = "User avatar", example = "http://prod-files.xcan.cloud/storage/pubapi/v1/file/logo.png",
      maxLength = MAX_URL_LENGTH_X2)
  private String avatar;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "User title", example = "CTO", maxLength = MAX_NAME_LENGTH)
  private String title;

  @Schema(description = "User gender", example = "MALE", defaultValue = "UNKNOWN")
  private Gender gender = Gender.UNKNOWN;

  @Length(max = MAX_ADDRESS_LENGTH)
  @Schema(example = "User's residence or contact address", maxLength = MAX_ADDRESS_LENGTH)
  private String address;

  @Valid
  @Size(max = MAX_RELATION_QUOTA)
  @Schema(description = "User's departments information, the maximum support is `2000`")
  private LinkedHashSet<UserDeptTo> depts;

  @Size(max = MAX_RELATION_QUOTA)
  @Schema(description = "User's group ids, the maximum support is `2000`")
  private LinkedHashSet<Long> groupIds;

  @Size(max = MAX_RELATION_QUOTA)
  @Schema(description = "User association tag ids, the maximum support is `2000`")
  private LinkedHashSet<Long> tagIds;

}
