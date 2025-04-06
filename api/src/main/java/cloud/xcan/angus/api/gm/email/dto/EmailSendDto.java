package cloud.xcan.angus.api.gm.email.dto;


import static cloud.xcan.angus.api.commonlink.EmailConstant.MAX_ATTACHMENT_NUM;
import static cloud.xcan.angus.api.commonlink.EmailConstant.MAX_EMAIL_ADDRESS;
import static cloud.xcan.angus.api.commonlink.EmailConstant.MAX_EMAIL_CONTEXT_SIZE;
import static cloud.xcan.angus.api.commonlink.EmailConstant.MAX_SEND_OBJECT_SIZE;
import static cloud.xcan.angus.api.commonlink.EmailConstant.MAX_SUBJECT_LENGTH;
import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DATE_FMT;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_EMAIL_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_LANGUAGE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_OUT_ID_LENGTH;

import cloud.xcan.angus.api.commonlink.EmailConstant;
import cloud.xcan.angus.api.commonlink.email.EmailBizKey;
import cloud.xcan.angus.api.commonlink.email.EmailType;
import cloud.xcan.angus.api.enums.ReceiveObjectType;
import cloud.xcan.angus.api.pojo.Attachment;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;


@Setter
@Getter
@Accessors(chain = true)
public class EmailSendDto implements Serializable {

  @Length(max = MAX_OUT_ID_LENGTH)
  @Schema(description = "Out business id, used to establish associations with external businesses.",
      maxLength = MAX_OUT_ID_LENGTH)
  private String outId;

  @Schema(description = "Email business key, it is required when type=TEMPLATE.", example = "SIGNUP")
  private EmailBizKey bizKey;

  @Length(max = MAX_LANGUAGE_LENGTH)
  @Schema(description = "Use language to send email content.", defaultValue = "zh_CN",
      example = "zh_CN", maxLength = MAX_LANGUAGE_LENGTH)
  private String language;

  @NotNull
  @Schema(description = "Email type.", example = "CUSTOM", requiredMode = RequiredMode.REQUIRED)
  private EmailType type;

  @Length(max = MAX_SUBJECT_LENGTH)
  @Schema(description = "Email subject.", example = "subject", maxLength = MAX_SUBJECT_LENGTH)
  private String subject;

  @Length(max = MAX_EMAIL_LENGTH)
  @Schema(description = "Email sent address.", example = "cloud@xcan.cloud", maxLength = MAX_EMAIL_LENGTH)
  private String fromAddress;

  @Size(max = MAX_EMAIL_ADDRESS)
  @Schema(description = "List of email receiving addresses, supporting up to `500` addresses. "
      + "Parameter toAddress and objectIds are required to choose one, if both are passed, toAddress is used by default.",
      type = "array", example = "[\"user1@xcancloud.com\",\"user2@xcancloud.com\"]")
  private Set<String> toAddress;

  @Size(max = MAX_EMAIL_ADDRESS)
  @Schema(description = "List of email CC addresses, supporting up to `500` addresses.",
      type = "array", example = "[\"demo@xcancloud.com\"]")
  private Set<String> ccAddress;

  @NotNull
  @Schema(description = "Whether or not verification code email flag.", requiredMode = RequiredMode.REQUIRED)
  private Boolean verificationCode;

  @Max(value = EmailConstant.MAX_VC_VALID_SECOND)
  @Schema(description = "Validity period of verification code, in seconds. It is required when verificationCode=true.", defaultValue = "300")
  private Integer verificationCodeValidSecond;

  @Schema(description = "Send email tenant id. Non user operation (job or doorapi) is required.")
  private Long sendTenantId;

  @Schema(description = "Send email user id. Non user operation (job or doorapi) is required.")
  private Long sendUserId;

  @NotNull
  @Schema(description = "Whether or not html content email flag.", requiredMode = RequiredMode.REQUIRED)
  private Boolean html;

  @Size(max = MAX_EMAIL_CONTEXT_SIZE)
  @Schema(description = "Email content, it is required when type=CUSTOM.", maxLength = MAX_EMAIL_CONTEXT_SIZE)
  private String content;

  @Size(max = MAX_EMAIL_ADDRESS)
  @Schema(description =
      "Email template parameters, it is required when type=TEMPLATE and there are template variables. "
          + "Key is email address and value is template parameter, when all email template parameters are the same, only one value is set.",
      type = "object"
  )
  private Map<String, Map<String, String>> templateParams;

  @Valid
  @Size(max = MAX_ATTACHMENT_NUM)
  @Schema(description = "The attachments of the email, maximum support for `10`.", type = "array")
  private Set<Attachment> attachments;

  @DateTimeFormat(pattern = DATE_FMT)
  @Schema(description = "Expected SMS sending time.")
  private LocalDateTime expectedSendDate;

  @Schema(description = "Whether or not urgent sent flag.", defaultValue = "false", requiredMode = RequiredMode.REQUIRED)
  private Boolean urgent = false;

  @Schema(description = "Email recipient object type, the receiveObjectType is required when sending via receiveObjectIds.")
  private ReceiveObjectType receiveObjectType;

  @Size(max = EmailConstant.MAX_SEND_OBJECT_SIZE)
  @Schema(description =
      "Email recipient object ids, maximum support for `500`. Parameter toAddress and receiveObjectIds "
          + "are required to choose one, if both are passed, toAddress is used by default.", type = "array")
  private List<Long> receiveObjectIds;

  @Size(max = MAX_SEND_OBJECT_SIZE)
  @Schema(description = "Email recipient TOP policy codes, maximum support for `500`.", type = "array")
  private List<String> receivePolicyCodes;

  @Schema(description = "Whether or not batch sending flag, multiple receiving addresses will be displayed in the mail.", defaultValue = "true")
  private Boolean batch = true;

}
