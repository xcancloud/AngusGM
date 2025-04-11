package cloud.xcan.angus.core.gm.interfaces.setting.facade.to.tenant;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_URL_LENGTH;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
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
public class TenantServerApiProxyTo implements Serializable {

  @NotNull
  @Schema(description = "Whether or not enabled flag.", example = "true", requiredMode = RequiredMode.REQUIRED)
  private Boolean enabled;

  @NotEmpty
  @Length(max = MAX_URL_LENGTH)
  @Schema(description = "The api proxy url of server client.", example = "ws://localhost:6806",
      maxLength = MAX_URL_LENGTH, requiredMode = RequiredMode.REQUIRED)
  private String url;

}
