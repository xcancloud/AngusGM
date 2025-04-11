package cloud.xcan.angus.api.commonlink.setting.security;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;

import cloud.xcan.angus.spec.experimental.ValueObjectSupport;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

/**
 * @author XiaoLong Liu
 */
@Setter
@Getter
@Accessors(chain = true)
public class SignupAllow extends ValueObjectSupport<SignupAllow> {

  @Schema(description = "Enable signup limit.", defaultValue = "false")
  private Boolean enabled = false;

  @Length(max = MAX_CODE_LENGTH)
  @Schema(example = "IC000101819Sqi")
  private String invitationCode;

}
