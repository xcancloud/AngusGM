package cloud.xcan.angus.api.commonlink.setting.user.apiproxy;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_URL_LENGTH;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;


@Getter
@Setter
@Accessors(chain = true)
public class ApiProxy implements Serializable {

  @Enumerated(EnumType.STRING)
  @Schema(description = "Enable apis proxy name")
  private ApiProxyType name;

  @Schema(description = "Whether or not the agent is enabled")
  private Boolean enabled;

  @Length(max = MAX_URL_LENGTH)
  @Schema(description = "User client proxy access address url", example = "ws://localhost:6806")
  private String url;

}
