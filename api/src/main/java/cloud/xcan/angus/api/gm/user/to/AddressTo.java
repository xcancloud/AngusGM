package cloud.xcan.angus.api.gm.user.to;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH_X4;

import cloud.xcan.angus.spec.experimental.ValueObjectSupport;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Valid
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class AddressTo extends ValueObjectSupport<AddressTo> {

  @Length(max = MAX_CODE_LENGTH)
  @Schema(example = "110000", maxLength = MAX_CODE_LENGTH)
  private String provinceCode;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(example = "Beijing", maxLength = MAX_NAME_LENGTH)
  private String provinceName;

  @Length(max = MAX_CODE_LENGTH)
  @Schema(example = "110100", maxLength = MAX_CODE_LENGTH)
  private String cityCode;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(example = "Beijing", maxLength = MAX_NAME_LENGTH)
  private String cityName;

  @Length(max = MAX_NAME_LENGTH_X4)
  @Schema(example = "1809, Unit 2, Floor 15, Building 3, Yard 1, Beiqing Road, Changping District", maxLength = MAX_NAME_LENGTH_X4)
  private String street;

}
