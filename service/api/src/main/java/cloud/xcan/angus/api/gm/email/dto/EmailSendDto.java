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
  @Schema(description = "External business identifier for establishing associations with external systems. Used for cross-system integration and tracking")
  private String outId;

  @Schema(description = "Email business key for template-based email sending. Required when type=TEMPLATE for proper template selection", example = "SIGNUP")
  private EmailBizKey bizKey;

  @Length(max = MAX_LANGUAGE_LENGTH)
  @Schema(description = "Language code for email content localization. Used for multi-language email template selection")
  private String language;

  @NotNull
  @Schema(description = "Email type for determining sending method. CUSTOM for custom content, TEMPLATE for template-based sending", requiredMode = RequiredMode.REQUIRED)
  private EmailType type;

  @Length(max = MAX_SUBJECT_LENGTH)
  @Schema(description = "Email subject line for recipient identification. Used for email categorization and search")
  private String subject;

  @Length(max = MAX_EMAIL_LENGTH)
  @Schema(description = "Sender email address for email identification. Used for reply-to and sender verification")
  private String fromAddress;

  @Size(max = MAX_EMAIL_ADDRESS)
  @Schema(description = "List of primary recipient email addresses. Takes precedence over receiveObjectIds when both are provided",
      type = "array", example = "[\"user1@xcancloud.com\",\"user2@xcancloud.com\"]")
  private Set<String> toAddress;

  @Size(max = MAX_EMAIL_ADDRESS)
  @Schema(description = "List of carbon copy recipient email addresses. Supports up to 500 addresses for additional recipients",
      type = "array", example = "[\"demo@xcancloud.com\"]")
  private Set<String> ccAddress;

  @NotNull
  @Schema(description = "Flag indicating whether this is a verification code email. Used for special handling of verification emails", requiredMode = RequiredMode.REQUIRED)
  private Boolean verificationCode;

  @Max(value = EmailConstant.MAX_VC_VALID_SECOND)
  @Schema(description = "Verification code validity period in seconds. Required when verificationCode=true for proper expiration handling")
  private Integer verificationCodeValidSecond;

  @Schema(description = "Tenant identifier for email sending context. Required for non-user operations like jobs or internal APIs")
  private Long sendTenantId;

  @Schema(description = "User identifier for email sending context. Required for non-user operations like jobs or internal APIs")
  private Long sendUserId;

  @NotNull
  @Schema(description = "Flag indicating whether email content is HTML format. Used for proper content rendering and display", requiredMode = RequiredMode.REQUIRED)
  private Boolean html;

  @Size(max = MAX_EMAIL_CONTEXT_SIZE)
  @Schema(description = "Email content body. Required when type=CUSTOM for custom email content")
  private String content;

  @Size(max = MAX_EMAIL_ADDRESS)
  @Schema(description =
      "Email template parameters for template-based sending. Required when type=TEMPLATE and template has variables. Key is email address, value is parameter map",
      type = "object"
  )
  private Map<String, Map<String, String>> templateParams;

  @Valid
  @Size(max = MAX_ATTACHMENT_NUM)
  @Schema(description = "Email attachments for file sharing. Supports maximum of 10 attachments for comprehensive file delivery", type = "array")
  private Set<Attachment> attachments;

  @DateTimeFormat(pattern = DATE_FMT)
  @Schema(description = "Expected email sending time for scheduled delivery. Used for delayed email sending")
  private LocalDateTime expectedSendDate;

  @Schema(description = "Flag indicating urgent email priority. Used for priority-based email processing and delivery", requiredMode = RequiredMode.REQUIRED)
  private Boolean urgent = false;

  @Schema(description = "Email recipient object type for system-based recipient selection. Required when using receiveObjectIds for recipient identification")
  private ReceiveObjectType receiveObjectType;

  @Size(max = EmailConstant.MAX_SEND_OBJECT_SIZE)
  @Schema(description = "Email recipient object identifiers for system-based recipient selection. Secondary to toAddress when both are provided", type = "array")
  private List<Long> receiveObjectIds;

  @Size(max = MAX_SEND_OBJECT_SIZE)
  @Schema(description = "Email recipient TOP policy codes for policy-based recipient selection", type = "array")
  private List<String> receivePolicyCodes;

  @Schema(description = "Flag indicating batch sending mode. When true, multiple recipients are displayed in the email", defaultValue = "true")
  private Boolean batch = true;

}
