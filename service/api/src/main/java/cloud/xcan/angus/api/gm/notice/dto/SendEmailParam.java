package cloud.xcan.angus.api.gm.notice.dto;

import static cloud.xcan.angus.api.commonlink.EmailConstant.MAX_ATTACHMENT_NUM;
import static cloud.xcan.angus.api.commonlink.EmailConstant.MAX_EMAIL_ADDRESS;
import static cloud.xcan.angus.api.commonlink.EmailConstant.MAX_EMAIL_CONTEXT_SIZE;
import static cloud.xcan.angus.api.commonlink.EmailConstant.MAX_SEND_OBJECT_SIZE;
import static cloud.xcan.angus.api.commonlink.EmailConstant.MAX_SUBJECT_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_LANGUAGE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_OUT_ID_LENGTH;

import cloud.xcan.angus.api.commonlink.EmailConstant;
import cloud.xcan.angus.api.commonlink.SmsConstants;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@ToString
public class SendEmailParam implements Serializable {

  @Length(max = MAX_OUT_ID_LENGTH)
  @Schema(description = "External business identifier for establishing associations with external systems. Used for cross-system integration and tracking")
  private String outId;

  @Schema(description = "Email business key for template-based email sending. Required when type=TEMPLATE for proper template selection", example = "SIGNUP")
  private EmailBizKey bizKey;

  @Length(max = MAX_LANGUAGE_LENGTH)
  @Schema(description = "Language code for email content localization. Used for multi-language email template selection", defaultValue = "zh_CN")
  private String language;

  @NotNull
  @Schema(description = "Email type for determining sending method. CUSTOM for custom content, TEMPLATE for template-based sending", requiredMode = RequiredMode.REQUIRED, example = "CUSTOM")
  private EmailType type;

  @Length(max = MAX_SUBJECT_LENGTH)
  @Schema(description = "Email subject line for recipient identification. Used for email categorization and search", example = "subject")
  private String subject;

  //  @Schema("Default false")
  //  private Boolean urgent = false;

  @Size(max = MAX_EMAIL_ADDRESS)
  @Schema(description = "List of primary recipient email addresses. Takes precedence over receiveObjectIds when both are provided",
      type = "array", example = "[\"user1@xcancloud.com\",\"user2@xcancloud.com\"]")
  private Set<String> toAddress;

  @Size(max = MAX_EMAIL_ADDRESS)
  @Schema(description = "List of carbon copy recipient email addresses. Supports up to 500 addresses for additional recipients", type = "array", example = "[\"demo@xcancloud.com\"]")
  private Set<String> ccAddress;

  @NotNull
  @Schema(description = "Flag indicating whether this is a verification code email. Used for special handling of verification emails", requiredMode = RequiredMode.REQUIRED)
  private Boolean verificationCode;

  @Max(value = EmailConstant.MAX_VC_VALID_SECOND)
  @Schema(description = "Verification code validity period in seconds. Required when verificationCode=true for proper expiration handling", defaultValue = "300")
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
  @Schema(description = "Email template parameters for template-based sending. Required when type=TEMPLATE and template has variables. Key is email address, value is parameter map")
  private Map<String, Map<String, String>> templateParams;

  @Valid
  @Size(max = MAX_ATTACHMENT_NUM)
  @Schema(description = "Email attachments for file sharing. Supports maximum of 10 attachments for comprehensive file delivery")
  private Set<Attachment> attachments;

  @Schema(description = "Email recipient object type for system-based recipient selection. Required when using receiveObjectIds for recipient identification")
  private ReceiveObjectType receiveObjectType;

  @Size(max = EmailConstant.MAX_SEND_OBJECT_SIZE)
  @Schema(description = "Email recipient object identifiers for system-based recipient selection. Secondary to toAddress when both are provided", type = "array")
  private List<Long> receiveObjectIds;

  @Size(max = MAX_SEND_OBJECT_SIZE)
  @Schema(description = "Email recipient TOP policy codes for policy-based recipient selection", type = "array")
  private List<String> receivePolicyCodes;

  @Schema(description = "Flag indicating batch sending mode. When true, multiple recipients are displayed in the email", defaultValue = "true")
  private Boolean batch;

  public SendEmailParam(Builder builder) {
    setOutId(builder.outId);
    setBizKey(builder.bizKey);
    setLanguage(builder.language);
    setType(builder.type);
    setSubject(builder.subject);
    setToAddress(builder.toAddress);
    setCcAddress(builder.ccAddress);
    setVerificationCode(builder.verificationCode);
    setVerificationCodeValidSecond(builder.verificationCodeValidSecond);
    setSendTenantId(builder.sendTenantId);
    setSendUserId(builder.sendUserId);
    setHtml(builder.html);
    setContent(builder.content);
    setTemplateParams(builder.templateParams);
    setAttachments(builder.attachments);
    setReceiveObjectType(builder.receiveObjectType);
    setReceiveObjectIds(builder.receiveObjectIds);
    setReceivePolicyCodes(builder.receivePolicyCodes);
    setBatch(builder.batch);
  }

  public SendEmailParam() {
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    // @formatter:off
    private @Length(max = MAX_OUT_ID_LENGTH) String outId;
    private @NotNull EmailBizKey bizKey;
    private @Length(max = MAX_LANGUAGE_LENGTH) String language;
    private @NotNull EmailType type;
    private @Length(max = MAX_SUBJECT_LENGTH) String subject;
    private @Size(max = MAX_EMAIL_ADDRESS) Set<String> toAddress;
    private @Size(max = MAX_EMAIL_ADDRESS) Set<String> ccAddress;
    private @NotNull Boolean verificationCode;
    private @Max(value = EmailConstant.MAX_VC_VALID_SECOND) Integer verificationCodeValidSecond;
    private Long sendTenantId;
    private Long sendUserId;
    private @NotNull Boolean html;
    private @Size(max = MAX_EMAIL_CONTEXT_SIZE) String content;
    private @Size(max = MAX_EMAIL_ADDRESS) Map<String, Map<String, String>> templateParams;
    private @Size(max = MAX_ATTACHMENT_NUM) @Valid Set<Attachment> attachments;
    private ReceiveObjectType receiveObjectType;
    private @Size(max = EmailConstant.MAX_SEND_OBJECT_SIZE) List<Long> receiveObjectIds;
    private @Size(max = SmsConstants.MAX_SEND_OBJECT_SIZE) List<String> receivePolicyCodes;
    private Boolean batch;
    // @formatter:on

    private Builder() {
    }

    public Builder outId(@Length(max = MAX_OUT_ID_LENGTH) String outId) {
      this.outId = outId;
      return this;
    }

    public Builder bizKey(@NotNull EmailBizKey bizKey) {
      this.bizKey = bizKey;
      return this;
    }

    public Builder language(@Length(max = MAX_LANGUAGE_LENGTH) String language) {
      this.language = language;
      return this;
    }

    public Builder type(@NotNull EmailType type) {
      this.type = type;
      return this;
    }

    public Builder subject(@Length(max = MAX_SUBJECT_LENGTH) String subject) {
      this.subject = subject;
      return this;
    }

    public Builder toAddress(@Size(max = MAX_EMAIL_ADDRESS) Set<String> toAddress) {
      this.toAddress = toAddress;
      return this;
    }

    public Builder ccAddress(@Size(max = MAX_EMAIL_ADDRESS) Set<String> ccAddress) {
      this.ccAddress = ccAddress;
      return this;
    }

    public Builder verificationCode(@NotNull Boolean verificationCode) {
      this.verificationCode = verificationCode;
      return this;
    }

    public Builder verificationCodeValidSecond(
        @Max(value = EmailConstant.MAX_VC_VALID_SECOND) Integer verificationCodeValidSecond) {
      this.verificationCodeValidSecond = verificationCodeValidSecond;
      return this;
    }

    public Builder sendTenantId(Long sendTenantId) {
      this.sendTenantId = sendTenantId;
      return this;
    }

    public Builder sendUserId(Long sendUserId) {
      this.sendUserId = sendUserId;
      return this;
    }

    public Builder html(@NotNull Boolean html) {
      this.html = html;
      return this;
    }

    public Builder content(@Size(max = MAX_EMAIL_CONTEXT_SIZE) String content) {
      this.content = content;
      return this;
    }

    public Builder templateParams(
        @Size(max = MAX_EMAIL_ADDRESS) Map<String, Map<String, String>> templateParams) {
      this.templateParams = templateParams;
      return this;
    }

    public Builder attachments(@Size(max = MAX_ATTACHMENT_NUM) @Valid Set<Attachment> attachments) {
      this.attachments = attachments;
      return this;
    }

    public Builder receiveObjectType(ReceiveObjectType receiveObjectType) {
      this.receiveObjectType = receiveObjectType;
      return this;
    }

    public Builder receiveObjectIds(
        @Size(max = MAX_SEND_OBJECT_SIZE) List<Long> receiveObjectIds) {
      this.receiveObjectIds = receiveObjectIds;
      return this;
    }

    public Builder receivePolicyCodes(
        @Size(max = MAX_SEND_OBJECT_SIZE) List<String> receivePolicyCodes) {
      this.receivePolicyCodes = receivePolicyCodes;
      return this;
    }

    public Builder batch(Boolean batch) {
      this.batch = batch;
      return this;
    }

    public SendEmailParam build() {
      return new SendEmailParam(this);
    }

  }
}
