package cloud.xcan.angus.core.gm.interfaces.user.facade.dto.current;

import static cloud.xcan.angus.api.commonlink.EmailConstant.MAX_EMAIL_ADDRESS;

import cloud.xcan.angus.api.commonlink.email.EmailBizKey;
import cloud.xcan.angus.validator.EnumPart;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class CurrentEmailSendDto implements Serializable {

  @NotNull
  @EnumPart(enumClass = EmailBizKey.class, allowableValues = {
      "PAASD_UPDATE", "MODIFY_EMAIL", "BIND_EMAIL"
  })
  @Schema(description = "Email business type key.", example = "BIND_EMAIL",
      allowableValues = {
          "PAASD_UPDATE", "MODIFY_EMAIL", "BIND_EMAIL"
      }, requiredMode = RequiredMode.REQUIRED)
  private EmailBizKey bizKey;

  @NotNull
  @Size(max = MAX_EMAIL_ADDRESS)
  @Schema(description = "List of email receiving addresses, supporting up to `500` addresses.", type = "array",
      example = "[\"james@xcancloud.com\"]", requiredMode = RequiredMode.REQUIRED)
  private Set<String> toAddress;

}
