package cloud.xcan.angus.api.commonlink.setting.security;

import static cloud.xcan.angus.api.commonlink.CommonConstant.DEFAULT_PASSWORD_MIN_LENGTH;
import static cloud.xcan.angus.api.commonlink.CommonConstant.MAX_PASSWORD_LENGTH;
import static cloud.xcan.angus.api.commonlink.CommonConstant.MIN_PASSWORD_LENGTH;

import cloud.xcan.angus.spec.experimental.ValueObjectSupport;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

/**
 * @author XiaoLong Liu
 */
@Setter
@Getter
@Accessors(chain = true)
public class PasswordPolicy extends ValueObjectSupport<PasswordPolicy> {

  @Range(min = MIN_PASSWORD_LENGTH, max = MAX_PASSWORD_LENGTH)
  private Integer minLength = DEFAULT_PASSWORD_MIN_LENGTH;

  // private List<PasswordCharType> charTypes;
}
