package cloud.xcan.angus.core.gm.interfaces.setting.facade.dto.user;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_URL_LENGTH;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Accessors(chain = true)
public class UserApiClientProxyUpdateDto implements Serializable {

  @NotEmpty
  @Length(max = MAX_URL_LENGTH)
  @Schema(description = "User API client proxy server access URL for external service communication",
     example = "ws://localhost:6806/angusProxy", requiredMode = RequiredMode.REQUIRED)
  private String url;

}
