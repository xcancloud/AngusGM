package cloud.xcan.angus.api.gm.user.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_LANDLINE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_URL_LENGTH_X2;

import cloud.xcan.angus.api.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Accessors(chain = true)
public class UserCurrentUpdateDto implements Serializable {

  //@ResourceName
  //@NotEmpty Is empty when signup add
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Username for unique user identification. Used for user authentication and system identification", example = "U33730000")
  private String username;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "User first name for personal identification. Used for user profile and contact information", example = "James")
  private String firstName;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "User last name for personal identification. Used for user profile and contact information", example = "Jones")
  private String lastName;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "User full name for personal identification. Used for user profile and contact information", example = "James Jones")
  private String fullName;

  @Length(max = MAX_LANDLINE_LENGTH)
  @Schema(description = "User landline phone number for contact information. Used for office contact and communication", example = "010-88287890")
  private String landline;

  @Length(max = MAX_URL_LENGTH_X2)
  @Schema(description = "User avatar image URL for profile display. Used for user interface and profile visualization", example = "http://prod-apis.xcan.cloud/storage/pubapi/v1/file/logo.png")
  private String avatar;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "User job title for role identification. Used for organizational hierarchy and contact information", example = "CTO")
  private String title;

  @Schema(description = "User gender for demographic information. Used for user profile and statistical analysis", example = "MALE", defaultValue = "UNKNOWN")
  private Gender gender;

  //  @Valid
  //  @Schema(description = "User contact address")
  //  private AddressTo address;
  private String address;

}
