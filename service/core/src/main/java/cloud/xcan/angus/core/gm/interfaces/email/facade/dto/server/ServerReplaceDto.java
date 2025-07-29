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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Setter
@Getter
@Accessors(chain = true)
public class ServerReplaceDto implements Serializable {

  @Schema(description = "Email server id. The ID is required when modifying an existing server, "
      + "create a new server when the value is empty")
  private Long id;

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Mail server name", example = "XCan Email Server",
      maxLength = MAX_NAME_LENGTH, requiredMode = RequiredMode.REQUIRED)
  private String name;

  @DoInFuture("Support the receiving protocol: POP3, IMAP")
  @NotNull
  @EnumPart(enumClass = EmailProtocol.class, allowableValues = {"SMTP"})
  @Schema(description = "Mail server protocol, only `SMTP` is supported",
      allowableValues = {"SMTP"}, requiredMode = RequiredMode.REQUIRED)
  private EmailProtocol protocol;

  @Length(max = MAX_REMARK_LENGTH)
  @Schema(description = "Email server remark", maxLength = MAX_NAME_LENGTH)
  private String remark;

  @NotBlank
  @Length(max = MAX_HOST_LENGTH)
  @Schema(description = "Email server hostname, ip or domain", example = "smtp.xcancloud.com",
      maxLength = MAX_HOST_LENGTH, requiredMode = RequiredMode.REQUIRED)
  private String host;

  @NotNull
  @Port
  @Schema(description = "Email server port", example = "465",
      minimum = "1", maximum = "65535", requiredMode = RequiredMode.REQUIRED)
  private Integer port;

  @Schema(description = "Whether to enable starttls when connecting to the mail server", defaultValue = "true")
  private Boolean startTlsEnabled = true;

  @Schema(description = "Whether to enable socket when communicating with the mail server", defaultValue = "true")
  private Boolean sslEnabled = true;

  // TODO new add
  
  @Schema(description = "Whether mail server authentication is required", defaultValue = "true")
  private Boolean authEnabled = true;

  // TODO web modify： sendAccount -> authAccount

  @Valid
  @Schema(description = "Email server authentication account information")
  private AuthAccountTo authAccount;

  @Length(max = MAX_SUBJECT_PREFIX_LENGTH)
  @Schema(description = "Email subject prefix", maxLength = MAX_SUBJECT_PREFIX_LENGTH)
  private String subjectPrefix;

}
