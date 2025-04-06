package cloud.xcan.angus.api.commonlink.setting.security;

import static cloud.xcan.angus.api.commonlink.CommonConstant.DEFAULT_LOCKED_DURATION;
import static cloud.xcan.angus.api.commonlink.CommonConstant.DEFAULT_LOCKED_PASSWORD_ERROR_NUM;
import static cloud.xcan.angus.api.commonlink.CommonConstant.DEFAULT_PASSWORD_ERROR_INTERVAL;
import static cloud.xcan.angus.api.commonlink.CommonConstant.DEFAULT_SIGN_OUT_PERIOD;
import static cloud.xcan.angus.api.commonlink.CommonConstant.MAX_LOCKED_PASSWORD_ERROR_NUM;
import static cloud.xcan.angus.api.commonlink.CommonConstant.MAX_PASSWORD_ERROR_INTERVAL;
import static cloud.xcan.angus.api.commonlink.CommonConstant.MAX_SIGN_OUT_PERIOD;
import static cloud.xcan.angus.api.commonlink.CommonConstant.MIN_SIGN_OUT_PERIOD;

import cloud.xcan.angus.spec.experimental.ValueObjectSupport;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class SigninLimit extends ValueObjectSupport<SigninLimit> {

  @Schema(description = "Enable sign-in limit.", defaultValue = "true")
  private Boolean enabled = true;

  @Range(min = MIN_SIGN_OUT_PERIOD, max = MAX_SIGN_OUT_PERIOD)
  @Schema(description = "Automatically signout when the user does not operate for a long time.")
  private Integer signoutPeriodInMinutes = DEFAULT_SIGN_OUT_PERIOD;

  @Range(min = 1, max = MAX_PASSWORD_ERROR_INTERVAL)
  @Schema(description = "Calculate password error interval.")
  private Integer passdErrorIntervalInMinutes = DEFAULT_PASSWORD_ERROR_INTERVAL;

  @Range(min = 1, max = MAX_LOCKED_PASSWORD_ERROR_NUM)
  @Schema(description = "Number of locked account and password errors.")
  private Integer lockedPassdErrorNum = DEFAULT_LOCKED_PASSWORD_ERROR_NUM;

  @Range(min = 1, max = Integer.MAX_VALUE)
  @Schema(description = "Duration of account locking.")
  private Integer lockedDurationInMinutes = DEFAULT_LOCKED_DURATION;
}
