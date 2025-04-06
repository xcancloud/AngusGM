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
  @Schema(description = "Username, unique identification.", example = "U33730000",
      maxLength = MAX_NAME_LENGTH)
  private String username;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "User first name.", example = "James", maxLength = MAX_NAME_LENGTH)
  private String firstName;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "User last name.", example = "Jones", maxLength = MAX_NAME_LENGTH)
  private String lastName;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "User full name.", example = "James Jones", maxLength = MAX_NAME_LENGTH)
  private String fullname;

  @Length(max = MAX_LANDLINE_LENGTH)
  @Schema(description = "User landline.", example = "010-88287890", maxLength = MAX_NAME_LENGTH)
  private String landline;

  @Length(max = MAX_URL_LENGTH_X2)
  @Schema(description = "User avatar", example = "http://prod-apis.xcan.cloud/storage/pubapi/v1/file/logo.png",
      maxLength = MAX_NAME_LENGTH)
  private String avatar;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "User title.", example = "CTO", maxLength = MAX_NAME_LENGTH)
  private String title;

  @Schema(description = "User gender.", example = "MALE", defaultValue = "UNKNOWN")
  private Gender gender;

  //  @Valid
  //  @Schema(description = "User contact address")
  //  private AddressTo address;
  private String address;

}
