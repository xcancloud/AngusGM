package cloud.xcan.angus.core.gm.interfaces.email.facade.dto.server;

import cloud.xcan.angus.core.gm.domain.email.server.EmailProtocol;
import cloud.xcan.angus.validator.EnumPart;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class ServerEnabledCheckDto implements Serializable {

  @EnumPart(enumClass = EmailProtocol.class, allowableValues = {"SMTP"})
  @Schema(description = "Mail server protocol, only `SMTP` is supported", allowableValues = {
      "SMTP"})
  private EmailProtocol protocol;

}
