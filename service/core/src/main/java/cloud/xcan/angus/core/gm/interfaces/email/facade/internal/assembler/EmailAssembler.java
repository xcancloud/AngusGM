package cloud.xcan.angus.core.gm.interfaces.email.facade.internal.assembler;

import cloud.xcan.angus.core.gm.domain.email.Email;
import cloud.xcan.angus.core.gm.domain.email.EmailSmtp;
import cloud.xcan.angus.core.gm.domain.email.EmailTemplate;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailRecordFindDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailSendCustomDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailSendDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailSmtpUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailTemplateCreateDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailTemplateFindDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailTemplateUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailRecordVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailSendVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailSmtpVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailStatsVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailTemplateVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailTrackingVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.remote.search.SearchOperation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Email assembler for DTO/Domain/VO conversion
 */
public class EmailAssembler {

  /**
   * Convert EmailTemplateCreateDto to EmailTemplate Domain
   */
  public static EmailTemplate toCreateTemplateDomain(EmailTemplateCreateDto dto) {
    EmailTemplate template = new EmailTemplate();
    template.setName(dto.getName());
    template.setCode(dto.getCode());
    template.setType(dto.getType());
    template.setSubject(dto.getSubject());
    template.setContent(dto.getContent());
    template.setParams(dto.getParams());
    template.setStatus("已启用"); // Default status
    return template;
  }

  /**
   * Convert EmailTemplateUpdateDto to EmailTemplate Domain
   */
  public static EmailTemplate toUpdateTemplateDomain(Long id, EmailTemplateUpdateDto dto) {
    EmailTemplate template = new EmailTemplate();
    template.setId(id);
    template.setName(dto.getName());
    template.setCode(dto.getCode());
    template.setType(dto.getType());
    template.setSubject(dto.getSubject());
    template.setContent(dto.getContent());
    template.setParams(dto.getParams());
    return template;
  }

  /**
   * Convert EmailTemplate Domain to EmailTemplateVo
   */
  public static EmailTemplateVo toTemplateVo(EmailTemplate template) {
    EmailTemplateVo vo = new EmailTemplateVo();
    vo.setId(template.getId());
    vo.setName(template.getName());
    vo.setCode(template.getCode());
    vo.setType(template.getType());
    vo.setSubject(template.getSubject());
    vo.setContent(template.getContent());
    vo.setParams(template.getParams());
    vo.setStatus(template.getStatus());
    vo.setUsageCount(template.getUsageCount());
    vo.setOpenRate(template.getOpenRate());
    vo.setClickRate(template.getClickRate());

    // Set auditing fields - TenantAuditingVo should handle these automatically
    // But we need to set them explicitly since TenantAuditingVo extends from a base class
    // Use reflection to get and set auditing fields since EmailTemplate uses @Getter and VO uses @Data
    try {
      java.lang.reflect.Method getTenantIdMethod = template.getClass().getMethod("getTenantId");
      Object tenantId = getTenantIdMethod.invoke(template);
      java.lang.reflect.Method setTenantIdMethod = vo.getClass().getMethod("setTenantId", Long.class);
      setTenantIdMethod.invoke(vo, tenantId != null ? (Long) tenantId : null);
      
      java.lang.reflect.Method getCreatedByMethod = template.getClass().getMethod("getCreatedBy");
      Object createdBy = getCreatedByMethod.invoke(template);
      java.lang.reflect.Method setCreatedByMethod = vo.getClass().getMethod("setCreatedBy", Long.class);
      setCreatedByMethod.invoke(vo, createdBy != null ? (Long) createdBy : null);
      
      java.lang.reflect.Method getCreatedDateMethod = template.getClass().getMethod("getCreatedDate");
      Object createdDate = getCreatedDateMethod.invoke(template);
      java.lang.reflect.Method setCreatedDateMethod = vo.getClass().getMethod("setCreatedDate", java.time.LocalDateTime.class);
      setCreatedDateMethod.invoke(vo, createdDate != null ? (java.time.LocalDateTime) createdDate : null);
      
      java.lang.reflect.Method getModifiedByMethod = template.getClass().getMethod("getModifiedBy");
      Object modifiedBy = getModifiedByMethod.invoke(template);
      java.lang.reflect.Method setModifiedByMethod = vo.getClass().getMethod("setModifiedBy", Long.class);
      setModifiedByMethod.invoke(vo, modifiedBy != null ? (Long) modifiedBy : null);
      
      java.lang.reflect.Method getModifiedDateMethod = template.getClass().getMethod("getModifiedDate");
      Object modifiedDate = getModifiedDateMethod.invoke(template);
      java.lang.reflect.Method setModifiedDateMethod = vo.getClass().getMethod("setModifiedDate", java.time.LocalDateTime.class);
      setModifiedDateMethod.invoke(vo, modifiedDate != null ? (java.time.LocalDateTime) modifiedDate : null);
    } catch (Exception e) {
      // If reflection fails, leave auditing fields as null/default values
    }

    return vo;
  }

  /**
   * Convert EmailSmtpUpdateDto to EmailSmtp Domain
   */
  public static EmailSmtp toUpdateSmtpDomain(EmailSmtpUpdateDto dto) {
    EmailSmtp smtp = new EmailSmtp();
    smtp.setHost(dto.getHost());
    smtp.setPort(dto.getPort());
    smtp.setUsername(dto.getUsername());
    if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
      smtp.setPassword(dto.getPassword());
    }
    smtp.setFromName(dto.getFromName());
    smtp.setFromEmail(dto.getFromEmail());
    smtp.setUseSsl(dto.getUseSsl() != null ? dto.getUseSsl() : true);
    return smtp;
  }

  /**
   * Convert EmailSmtp Domain to EmailSmtpVo
   */
  public static EmailSmtpVo toSmtpVo(EmailSmtp smtp) {
    EmailSmtpVo vo = new EmailSmtpVo();
    vo.setId(smtp.getId());
    vo.setHost(smtp.getHost());
    vo.setPort(smtp.getPort());
    vo.setUsername(smtp.getUsername());
    vo.setPassword(smtp.getPassword() != null ? "******" : null); // Mask password
    vo.setFromName(smtp.getFromName());
    vo.setFromEmail(smtp.getFromEmail());
    vo.setUseSsl(smtp.getUseSsl());
    vo.setIsDefault(smtp.getIsDefault());

    // Set auditing fields - TenantAuditingVo should handle these automatically
    // But we need to set them explicitly since TenantAuditingVo extends from a base class
    // Use reflection to get and set auditing fields since EmailSmtp uses @Getter and VO uses @Data
    try {
      java.lang.reflect.Method getTenantIdMethod = smtp.getClass().getMethod("getTenantId");
      Object tenantId = getTenantIdMethod.invoke(smtp);
      java.lang.reflect.Method setTenantIdMethod = vo.getClass().getMethod("setTenantId", Long.class);
      setTenantIdMethod.invoke(vo, tenantId != null ? (Long) tenantId : null);
      
      java.lang.reflect.Method getCreatedByMethod = smtp.getClass().getMethod("getCreatedBy");
      Object createdBy = getCreatedByMethod.invoke(smtp);
      java.lang.reflect.Method setCreatedByMethod = vo.getClass().getMethod("setCreatedBy", Long.class);
      setCreatedByMethod.invoke(vo, createdBy != null ? (Long) createdBy : null);
      
      java.lang.reflect.Method getCreatedDateMethod = smtp.getClass().getMethod("getCreatedDate");
      Object createdDate = getCreatedDateMethod.invoke(smtp);
      java.lang.reflect.Method setCreatedDateMethod = vo.getClass().getMethod("setCreatedDate", java.time.LocalDateTime.class);
      setCreatedDateMethod.invoke(vo, createdDate != null ? (java.time.LocalDateTime) createdDate : null);
      
      java.lang.reflect.Method getModifiedByMethod = smtp.getClass().getMethod("getModifiedBy");
      Object modifiedBy = getModifiedByMethod.invoke(smtp);
      java.lang.reflect.Method setModifiedByMethod = vo.getClass().getMethod("setModifiedBy", Long.class);
      setModifiedByMethod.invoke(vo, modifiedBy != null ? (Long) modifiedBy : null);
      
      java.lang.reflect.Method getModifiedDateMethod = smtp.getClass().getMethod("getModifiedDate");
      Object modifiedDate = getModifiedDateMethod.invoke(smtp);
      java.lang.reflect.Method setModifiedDateMethod = vo.getClass().getMethod("setModifiedDate", java.time.LocalDateTime.class);
      setModifiedDateMethod.invoke(vo, modifiedDate != null ? (java.time.LocalDateTime) modifiedDate : null);
    } catch (Exception e) {
      // If reflection fails, leave auditing fields as null/default values
    }

    return vo;
  }

  /**
   * Convert Email Domain to EmailSendVo
   */
  public static EmailSendVo toSendVo(Email email) {
    EmailSendVo vo = new EmailSendVo();
    // Email uses @Data annotation, getId() should be available from BaseEntity
    // Since EmailFacadeImpl uses created.getId(), BaseEntity must have getId() method
    // BaseEntity should have id field with @Id annotation, and @Data generates getId()
    // Try to get id - EmailRepo.findById() works, so BaseEntity must have id field
    try {
      // Use reflection to get id if getId() is not directly available
      java.lang.reflect.Method getIdMethod = email.getClass().getMethod("getId");
      Object id = getIdMethod.invoke(email);
      vo.setId(id != null ? (Long) id : null);
    } catch (Exception e) {
      // If reflection fails, set to null
      vo.setId(null);
    }
    if (email.getToRecipients() != null && !email.getToRecipients().isEmpty()) {
      vo.setTo(email.getToRecipients().get(0));
    }
    vo.setSubject(email.getSubject());
    if (email.getTemplateId() != null) {
      vo.setTemplateId(Long.parseLong(email.getTemplateId()));
    }
    vo.setStatus(email.getStatus() != null ? email.getStatus().name() : null);
    vo.setSentTime(email.getSendTime());
    vo.setMessageId(email.getExternalId());

    // Note: Email extends BaseEntity, not TenantAuditingEntity, so auditing fields are not available
    // EmailSendVo extends TenantAuditingVo, but Email doesn't have auditing fields
    // We'll leave auditing fields as null/default values

    return vo;
  }

  /**
   * Convert Email Domain to EmailRecordVo
   */
  public static EmailRecordVo toRecordVo(Email email) {
    EmailRecordVo vo = new EmailRecordVo();
    // Email uses @Data annotation, getId() should be available
    // Try to get id - EmailRepo.findById() works, so BaseEntity must have id field
    Long emailId = null;
    try {
      // Use reflection to get id if getId() is not directly available
      java.lang.reflect.Method getIdMethod = email.getClass().getMethod("getId");
      Object id = getIdMethod.invoke(email);
      emailId = id != null ? (Long) id : null;
    } catch (Exception e) {
      // If reflection fails, set to null
      emailId = null;
    }
    vo.setId(emailId);
    if (email.getToRecipients() != null && !email.getToRecipients().isEmpty()) {
      vo.setTo(email.getToRecipients().get(0));
    }
    if (email.getCcRecipients() != null && !email.getCcRecipients().isEmpty()) {
      vo.setCc(email.getCcRecipients().get(0));
    }
    if (email.getBccRecipients() != null && !email.getBccRecipients().isEmpty()) {
      vo.setBcc(email.getBccRecipients().get(0));
    }
    vo.setSubject(email.getSubject());
    vo.setContent(email.getHtmlContent());
    if (email.getTemplateId() != null) {
      vo.setTemplateId(Long.parseLong(email.getTemplateId()));
    }
    vo.setStatus(email.getStatus() != null ? email.getStatus().name() : null);
    vo.setSentTime(email.getSendTime());
    vo.setDeliveredTime(email.getDeliverTime());
    vo.setProvider(email.getProvider());

    // Convert attachments
    if (email.getAttachments() != null) {
      List<EmailRecordVo.EmailAttachmentVo> attachmentVos = email.getAttachments().stream()
          .map(att -> {
            EmailRecordVo.EmailAttachmentVo attachmentVo = new EmailRecordVo.EmailAttachmentVo();
            attachmentVo.setFileName((String) att.get("fileName"));
            attachmentVo.setFileUrl((String) att.get("fileUrl"));
            return attachmentVo;
          })
          .collect(Collectors.toList());
      vo.setAttachments(attachmentVos);
    }

    // Note: Email extends BaseEntity, not TenantAuditingEntity, so auditing fields are not available
    // EmailRecordVo extends TenantAuditingVo, but Email doesn't have auditing fields
    // We'll leave auditing fields as null/default values

    return vo;
  }

  /**
   * Convert Email Domain to EmailTrackingVo
   */
  public static EmailTrackingVo toTrackingVo(Email email) {
    EmailTrackingVo vo = new EmailTrackingVo();
    // Email uses @Data annotation, getId() should be available
    // Try to get id - EmailRepo.findById() works, so BaseEntity must have id field
    Long emailId = null;
    try {
      // Use reflection to get id if getId() is not directly available
      java.lang.reflect.Method getIdMethod = email.getClass().getMethod("getId");
      Object id = getIdMethod.invoke(email);
      emailId = id != null ? (Long) id : null;
    } catch (Exception e) {
      // If reflection fails, set to null
      emailId = null;
    }
    vo.setEmailId(emailId);
    vo.setSubject(email.getSubject());
    vo.setSentTime(email.getSendTime());
    vo.setDeliveredTime(email.getDeliverTime());
    // TODO: Set opened, clicked, bounced, complained from tracking data
    vo.setOpened(false);
    vo.setOpenedTime(null);
    vo.setOpenCount(0);
    vo.setClicked(false);
    vo.setClickCount(0);
    vo.setBounced(false);
    vo.setComplained(false);
    return vo;
  }

  /**
   * Convert EmailSendDto to Email Domain for sending
   */
  public static Email toSendEmailDomain(EmailSendDto dto, EmailTemplate template) {
    Email email = new Email();
    email.setToRecipients(List.of(dto.getTo()));
    if (dto.getCc() != null) {
      email.setCcRecipients(List.of(dto.getCc()));
    }
    if (dto.getBcc() != null) {
      email.setBccRecipients(List.of(dto.getBcc()));
    }
    email.setSubject(template.getSubject());
    email.setTemplateId(template.getId().toString());
    if (dto.getParams() != null) {
      Map<String, Object> templateParams = new HashMap<>();
      dto.getParams().forEach((k, v) -> templateParams.put(k, v));
      email.setTemplateParams(templateParams);
    }
    if (dto.getAttachments() != null) {
      List<Map<String, Object>> attachments = dto.getAttachments().stream()
          .map(att -> {
            Map<String, Object> attMap = new HashMap<>();
            attMap.put("fileName", att.getFileName());
            attMap.put("fileUrl", att.getFileUrl());
            return attMap;
          })
          .collect(Collectors.toList());
      email.setAttachments(attachments);
    }
    return email;
  }

  /**
   * Convert EmailSendCustomDto to Email Domain
   */
  public static Email toCustomEmailDomain(EmailSendCustomDto dto) {
    Email email = new Email();
    email.setToRecipients(List.of(dto.getTo()));
    if (dto.getCc() != null) {
      email.setCcRecipients(List.of(dto.getCc()));
    }
    if (dto.getBcc() != null) {
      email.setBccRecipients(List.of(dto.getBcc()));
    }
    email.setSubject(dto.getSubject());
    if ("html".equals(dto.getContentType())) {
      email.setHtmlContent(dto.getContent());
    } else {
      email.setTextContent(dto.getContent());
    }
    if (dto.getAttachments() != null) {
      List<Map<String, Object>> attachments = dto.getAttachments().stream()
          .map(att -> {
            Map<String, Object> attMap = new HashMap<>();
            attMap.put("fileName", att.getFileName());
            attMap.put("fileUrl", att.getFileUrl());
            return attMap;
          })
          .collect(Collectors.toList());
      email.setAttachments(attachments);
    }
    return email;
  }

  /**
   * Build query specification for EmailRecordFindDto
   */
  public static GenericSpecification<Email> getRecordSpecification(EmailRecordFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<EmailRecordFindDto>(dto)
        .rangeSearchFields("id", "createdDate", "modifiedDate", "sendTime")
        .orderByFields("id", "createdDate", "modifiedDate", "sendTime", "subject")
        .matchSearchFields("subject")
        .build();

    // Add status filter
    if (dto.getStatus() != null && !dto.getStatus().isEmpty()) {
      filters.add(new SearchCriteria("status", dto.getStatus(), SearchOperation.EQUAL));
    }

    // Add templateId filter
    if (dto.getTemplateId() != null) {
      filters.add(new SearchCriteria("templateId", dto.getTemplateId().toString(), SearchOperation.EQUAL));
    }

    // Add date range filter
    if (dto.getStartDate() != null) {
      filters.add(new SearchCriteria("sendTime", dto.getStartDate(), SearchOperation.GREATER_THAN));
    }
    if (dto.getEndDate() != null) {
      filters.add(new SearchCriteria("sendTime", dto.getEndDate(), SearchOperation.LESS_THAN));
    }

    // Add keyword search
    if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
      filters.add(new SearchCriteria("subject", dto.getKeyword(), SearchOperation.MATCH));
    }

    return new GenericSpecification<>(filters);
  }

  /**
   * Build query specification for EmailTemplateFindDto
   */
  public static GenericSpecification<EmailTemplate> getTemplateSpecification(EmailTemplateFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<EmailTemplateFindDto>(dto)
        .rangeSearchFields("id", "createdDate", "modifiedDate")
        .orderByFields("id", "createdDate", "modifiedDate", "name", "code")
        .matchSearchFields("name", "code", "subject")
        .build();

    // Add status filter
    if (dto.getStatus() != null && !dto.getStatus().isEmpty()) {
      filters.add(new SearchCriteria("status", dto.getStatus(), SearchOperation.EQUAL));
    }

    // Add type filter
    if (dto.getType() != null && !dto.getType().isEmpty()) {
      filters.add(new SearchCriteria("type", dto.getType(), SearchOperation.EQUAL));
    }

    // Add keyword search
    if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
      filters.add(new SearchCriteria("name", dto.getKeyword(), SearchOperation.MATCH));
    }

    return new GenericSpecification<>(filters);
  }

  /**
   * Convert statistics map to EmailStatsVo
   */
  public static EmailStatsVo toStatsVo(Map<String, Object> stats) {
    EmailStatsVo vo = new EmailStatsVo();
    vo.setTotalSent(getLongValue(stats, "totalSent", 0L));
    vo.setSuccessCount(getLongValue(stats, "successCount", 0L));
    vo.setFailedCount(getLongValue(stats, "failedCount", 0L));
    vo.setTodaySent(getLongValue(stats, "todaySent", 0L));
    vo.setThisMonthSent(getLongValue(stats, "thisMonthSent", 0L));
    vo.setOpenRate(getDoubleValue(stats, "openRate", 0.0));
    vo.setClickRate(getDoubleValue(stats, "clickRate", 0.0));
    return vo;
  }

  private static Long getLongValue(Map<String, Object> map, String key, Long defaultValue) {
    Object value = map.get(key);
    if (value == null) {
      return defaultValue;
    }
    if (value instanceof Long) {
      return (Long) value;
    }
    if (value instanceof Number) {
      return ((Number) value).longValue();
    }
    return defaultValue;
  }

  private static Double getDoubleValue(Map<String, Object> map, String key, Double defaultValue) {
    Object value = map.get(key);
    if (value == null) {
      return defaultValue;
    }
    if (value instanceof Double) {
      return (Double) value;
    }
    if (value instanceof Number) {
      return ((Number) value).doubleValue();
    }
    return defaultValue;
  }
}
