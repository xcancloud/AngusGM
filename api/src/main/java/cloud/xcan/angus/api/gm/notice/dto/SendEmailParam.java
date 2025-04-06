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
  @Schema(description = "Email type.", requiredMode = RequiredMode.REQUIRED, example = "CUSTOM")
  private EmailType type;

  @Length(max = MAX_SUBJECT_LENGTH)
  @Schema(description = "Email subject.", example = "subject", maxLength = MAX_SUBJECT_LENGTH)
  private String subject;

  //  @Schema("Default false")
  //  private Boolean urgent = false;

  @Size(max = MAX_EMAIL_ADDRESS)
  @Schema(description = "List of email receiving addresses, supporting up to `500` addresses. "
      + "Parameter toAddress and objectIds are required to choose one, if both are passed, toAddress is used by default.",
      type = "array", example = "[\"user1@xcancloud.com\",\"user2@xcancloud.com\"]")
  private Set<String> toAddress;

  @Size(max = MAX_EMAIL_ADDRESS)
  @Schema(description = "List of email CC addresses, supporting up to `500` addresses. ", type = "array", example = "[\"demo@xcancloud.com\"]")
  private Set<String> ccAddress;

  @NotNull
  @Schema(description = "Whether or not verification code email flag.", requiredMode = RequiredMode.REQUIRED)
  private Boolean verificationCode;

  @Max(value = EmailConstant.MAX_VC_VALID_SECOND)
  @Schema(description = "Validity period of verification code, in seconds, it is required when verificationCode=true.", defaultValue = "300")
  private Integer verificationCodeValidSecond;

  @Schema(description = "Non user operation (job or doorapi) is required.")
  private Long sendTenantId;

  @Schema(description = "Non user operation (job or doorapi) is required.")
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
          + "Key is email address and value is template parameter, when all email template parameters are the same, only one value is set.")
  private Map<String, Map<String, String>> templateParams;

  @Valid
  @Size(max = MAX_ATTACHMENT_NUM)
  @Schema(description = "The attachments of the email, maximum support for `10`.")
  private Set<Attachment> attachments;

  @Schema(description = "Email recipient object type, the receiveObjectType is required when sending via receiveObjectIds.")
  private ReceiveObjectType receiveObjectType;

  @Size(max = EmailConstant.MAX_SEND_OBJECT_SIZE)
  @Schema(description =
      "Email recipient object ids, maximum support for `500`. Parameter toAddress and receiveObjectIds "
          + "are required to choose one, if both are passed, toAddress is used by default", type = "array")
  private List<Long> receiveObjectIds;

  @Size(max = MAX_SEND_OBJECT_SIZE)
  @Schema(description = "Email recipient TOP policy codes, maximum support for `500`. ", type = "array")
  private List<String> receivePolicyCodes;

  @Schema(description = "Whether or not batch sending flag, multiple receiving addresses will be displayed in the mail.", defaultValue = "true")
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
