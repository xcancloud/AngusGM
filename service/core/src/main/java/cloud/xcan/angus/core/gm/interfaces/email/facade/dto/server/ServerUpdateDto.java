package cloud.xcan.angus.core.gm.interfaces.email.facade.dto.server;

import static cloud.xcan.angus.api.commonlink.EmailConstant.MAX_SUBJECT_PREFIX_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_HOST_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_REMARK_LENGTH;

import cloud.xcan.angus.core.gm.domain.email.server.EmailProtocol;
import cloud.xcan.angus.core.gm.interfaces.email.facade.to.AuthAccountTo;
import cloud.xcan.angus.spec.annotations.DoInFuture;
import cloud.xcan.angus.validator.EnumPart;
import cloud.xcan.angus.validator.Port;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;


@Setter
@Getter
@Accessors(chain = true)
public class ServerUpdateDto implements Serializable {

  @NotNull
  @Schema(description = "Mail server id.", requiredMode = RequiredMode.REQUIRED)
  private Long id;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Mail server name.", example = "XCan Email Server", maxLength = MAX_NAME_LENGTH)
  private String name;

  @DoInFuture("Support the receiving protocol: POP3, IMAP.")
  @EnumPart(enumClass = EmailProtocol.class, allowableValues = {"SMTP"})
  @Schema(description = "Mail server protocol, only `SMTP` is supported.", allowableValues = {
      "SMTP"})
  private EmailProtocol protocol;

  @Length(max = MAX_REMARK_LENGTH)
  @Schema(description = "Email server remark.", maxLength = MAX_NAME_LENGTH)
  private String remark;

  @Length(max = MAX_HOST_LENGTH)
  @Schema(description = "Email server hostname, ip or domain.", example = "smtp.xcancloud.com", maxLength = MAX_HOST_LENGTH)
  private String host;

  @Port
  @Schema(description = "Email server port.", example = "465", minimum = "1", maximum = "65535")
  private Integer port;

  @Schema(description = "Whether to enable starttls when connecting to the mail server.")
  private Boolean startTlsEnabled;

  @Schema(description = "Whether to enable socket when communicating with the mail server.")
  private Boolean sslEnabled;

  @Schema(description = "Whether mail server authentication is required.")
  private Boolean authEnabled;

  @Valid
  @Schema(description = "Email server authentication account information.")
  private AuthAccountTo authAccount;

  @Length(max = MAX_SUBJECT_PREFIX_LENGTH)
  @Schema(description = "Email subject prefix.", maxLength = MAX_SUBJECT_PREFIX_LENGTH)
  private String subjectPrefix;

}
