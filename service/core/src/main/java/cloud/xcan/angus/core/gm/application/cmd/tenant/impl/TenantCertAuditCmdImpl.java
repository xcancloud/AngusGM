package cloud.xcan.angus.core.gm.application.cmd.tenant.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.domain.UserMessage.NOTICE_REAL_NAME_AUTH;
import static cloud.xcan.angus.core.gm.domain.UserMessage.NOTICE_REAL_NAME_AUTH_FAILURE_REASON;
import static cloud.xcan.angus.core.gm.domain.UserMessage.TENANT_AUDIT_FAILURE_REASON_MISSING;
import static cloud.xcan.angus.core.gm.domain.UserMessage.TENANT_CERT_MISSING_T;
import static cloud.xcan.angus.core.gm.domain.UserMessage.TENANT_CERT_SUMMITED;
import static cloud.xcan.angus.core.gm.domain.UserMessage.TENANT_LEGAL_PERSON_BUSINESS_INCONSISTENT;
import static cloud.xcan.angus.core.gm.domain.UserMessage.TENANT_NAME_BUSINESS_INCONSISTENT;
import static cloud.xcan.angus.core.gm.domain.UserMessage.TENANT_NAME_ID_CARD_INCONSISTENT;
import static cloud.xcan.angus.core.gm.domain.UserMessage.TENANT_REAL_NAME_PASSED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.TENANT;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.SUBMIT_TENANT_AUDIT;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.TENANT_AUDIT;
import static cloud.xcan.angus.core.utils.CoreUtils.copyPropertiesIgnoreNull;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.spec.locale.MessageHolder.message;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.email.EmailBizKey;
import cloud.xcan.angus.api.commonlink.sms.SmsBizKey;
import cloud.xcan.angus.api.commonlink.tenant.Tenant;
import cloud.xcan.angus.api.commonlink.tenant.TenantRepo;
import cloud.xcan.angus.api.commonlink.user.UserRepo;
import cloud.xcan.angus.api.enums.NoticeType;
import cloud.xcan.angus.api.enums.ReceiveObjectType;
import cloud.xcan.angus.api.enums.TenantRealNameStatus;
import cloud.xcan.angus.api.gm.notice.dto.SendNoticeDto;
import cloud.xcan.angus.api.manager.NoticeManager;
import cloud.xcan.angus.api.manager.converter.NoticeConverter;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizAssert;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.auth.AuthUserCmd;
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.tenant.TenantCertAuditCmd;
import cloud.xcan.angus.core.gm.application.query.tenant.TenantCertAuditQuery;
import cloud.xcan.angus.core.gm.application.query.tenant.TenantCertRecognizeQuery;
import cloud.xcan.angus.core.gm.application.query.tenant.TenantQuery;
import cloud.xcan.angus.core.gm.domain.tenant.audit.AuditRecordData;
import cloud.xcan.angus.core.gm.domain.tenant.audit.TenantCertAudit;
import cloud.xcan.angus.core.gm.domain.tenant.audit.TenantCertAuditRepo;
import cloud.xcan.angus.core.gm.domain.tenant.cert.BusinessRecognize;
import cloud.xcan.angus.core.gm.domain.tenant.cert.IdCardRecognize;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.remote.message.SysException;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * Implementation of tenant certificate audit command operations.
 * </p>
 * <p>
 * Manages tenant real-name authentication including certificate submission,
 * automatic and manual audit processes, and notification management.
 * </p>
 * <p>
 * Supports personal and enterprise certificate validation with OCR recognition,
 * automatic audit for eligible certificates, and comprehensive audit logging.
 * </p>
 */
@Slf4j
@Biz
public class TenantCertAuditCmdImpl extends CommCmd<TenantCertAudit, Long> implements
    TenantCertAuditCmd {

  @Resource
  private TenantCertAuditRepo tenantCertAuditRepo;
  @Resource
  private TenantCertAuditQuery tenantCertAuditQuery;
  @Resource
  private TenantCertRecognizeQuery tenantCertRecognizeQuery;
  @Resource
  private TenantRepo tenantRepo;
  @Resource
  private TenantQuery tenantQuery;
  @Resource
  private UserRepo userRepo;
  @Resource
  private NoticeManager noticeManager;
  @Resource
  private AuthUserCmd authUserCmd;
  @Resource
  private OperationLogCmd operationLogCmd;
  @Value("${xcan.tenant.enableAutoAudit:false}")
  private boolean enableAutoAudit;

  /**
   * <p>
   * Submits tenant certificate for audit.
   * </p>
   * <p>
   * Validates certificate submission, performs automatic audit if enabled,
   * and updates tenant real-name status accordingly.
   * </p>
   * <p>
   * Supports both personal and enterprise certificate types with
   * OCR-based validation and notification management.
   * </p>
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public Void submit(TenantCertAudit tenantAudit) {
    return new BizTemplate<Void>() {
      TenantCertAudit tenantAuditDb;
      Tenant tenantDb;
      // Note: Cannot get the modified value in tenantCmd#add()
      Long tenantId;

      @Override
      protected void checkParams() {
        tenantId = getOptTenantId();
        tenantDb = tenantQuery.checkAndFind(tenantId);
        tenantAuditDb = checkAndGetTenantCertAudit(tenantId, tenantAudit);
      }

      @Override
      protected Void process() {
        // Initialize tenant audit for first submission
        tenantAuditDb = initTenantAudit(tenantAuditDb, tenantAudit);

        // Perform automatic audit if enabled
        if (isAutoAudit()) {
          doAutoAudit();
        } else {
          // Wait for manual audit
          // TODO: Send audit reminder message
          updateStatusInAuditing();
        }

        // Update tenant real-name status
        updateTenantRealName();

        // Save tenant audit
        tenantCertAuditRepo.save(tenantAuditDb);

        // Log operation for audit
        operationLogCmd.add(TENANT, tenantDb, SUBMIT_TENANT_AUDIT);
        return null;
      }

      /**
       * <p>
       * Performs automatic certificate audit.
       * </p>
       * <p>
       * Validates certificate information and updates status accordingly.
       * Sends notifications for both success and failure cases.
       * </p>
       */
      private void doAutoAudit() {
        try {
          checkCertValid();
          updateStatusPassed();
          updateUserTenantName();
          sendRealNameAuthPassedNotice(tenantAuditDb.getTenantId());
        } catch (Exception e) {
          updateStatusFailed(e);
          sendRealNameAuthFailureNotice(tenantAuditDb.getTenantId());
        }
      }

      /**
       * <p>
       * Determines if automatic audit should be performed.
       * </p>
       * <p>
       * Checks if auto audit is enabled and certificate type is supported.
       * </p>
       */
      private boolean isAutoAudit() {
        return enableAutoAudit && (tenantAuditDb.getType().isPersonal()
            || tenantAuditDb.getType().isEnterprise());
      }

      /**
       * <p>
       * Validates certificate information based on type.
       * </p>
       * <p>
       * Performs different validation for personal and enterprise certificates.
       * </p>
       */
      private void checkCertValid() {
        if (tenantAuditDb.getType().isPersonal()) {
          checkPersonalCert(tenantAuditDb);
        } else {
          checkEnterpriseCert(tenantAuditDb);
        }
      }

      /**
       * <p>
       * Updates tenant real-name status.
       * </p>
       * <p>
       * Updates tenant type, real-name status, and name in tenant record.
       * </p>
       */
      private void updateTenantRealName() {
        Tenant tenantDb = tenantQuery.checkAndFind(tenantId);
        tenantDb.setType(tenantAuditDb.getType());
        tenantDb.setRealNameStatus(tenantAuditDb.getStatus());
        tenantDb.setName(tenantAuditDb.getTenantName());
        tenantRepo.save(tenantDb);
      }

      /**
       * <p>
       * Updates audit status to auditing.
       * </p>
       * <p>
       * Sets status to AUDITING and disables auto audit for manual review.
       * </p>
       */
      private void updateStatusInAuditing() {
        tenantAuditDb.setStatus(TenantRealNameStatus.AUDITING).setAutoAudit(false);
        authUserCmd.realName(tenantId, TenantRealNameStatus.AUDITING);
      }

      /**
       * <p>
       * Updates audit status to failed.
       * </p>
       * <p>
       * Records failure reason and updates user real-name status.
       * </p>
       */
      private void updateStatusFailed(Exception e) {
        tenantAuditDb.setStatus(TenantRealNameStatus.FAILED_AUDIT).setAutoAudit(true)
            .setAuditRecordData(new AuditRecordData().setAuditUserId(null)
                .setAuditUserName(null).setAuditDate(LocalDateTime.now())
                .setReason(e.getMessage()));
        authUserCmd.realName(tenantId, TenantRealNameStatus.FAILED_AUDIT);
      }

      /**
       * <p>
       * Updates audit status to passed.
       * </p>
       * <p>
       * Records successful audit with current timestamp.
       * </p>
       */
      private void updateStatusPassed() {
        tenantAuditDb.setStatus(TenantRealNameStatus.AUDITED).setAutoAudit(true)
            .setAuditRecordData(new AuditRecordData().setAuditUserId(null)
                .setAuditUserName(null).setAuditDate(LocalDateTime.now()));
      }

      /**
       * <p>
       * Updates user tenant name after successful audit.
       * </p>
       * <p>
       * Updates tenant name for all users and sets real-name status.
       * </p>
       */
      private void updateUserTenantName() {
        // Update tenant name for all users when audit passes
        userRepo.updateTenantNameByTenantId(tenantId, tenantAuditDb.getTenantName());
        // Set user real-name status
        authUserCmd.realName(tenantId, TenantRealNameStatus.AUDITED);
      }
    }.execute();
  }

  /**
   * <p>
   * Performs manual audit of tenant certificate.
   * </p>
   * <p>
   * Validates audit requirements and updates status based on audit result.
   * Sends appropriate notifications and updates tenant information.
   * </p>
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void audit(TenantCertAudit tenantAudit) {
    new BizTemplate<Void>(false) {
      TenantCertAudit tenantAuditDb;

      @Override
      protected void checkParams() {
        // Verify certificate is submitted
        tenantAuditDb = tenantCertAuditQuery.checkAndFind(tenantAudit.getId());

        // Verify not already passed
        assertTrue(!tenantAuditDb.isRealNamePassed(), TENANT_REAL_NAME_PASSED);

        // Verify failure reason is provided when audit fails
        assertTrue(!tenantAudit.isRealNameFailed()
                || isNotEmpty(tenantAudit.getAuditRecordData().getReason()),
            TENANT_AUDIT_FAILURE_REASON_MISSING);

        // Verify certificate information is submitted
        assertTrue(tenantAuditDb.isCertSubmitted(), TENANT_CERT_MISSING_T,
            new Object[]{tenantAuditDb.getType()});
      }

      @Override
      protected Void process() {
        Tenant tenantDb = tenantQuery.checkAndFind(tenantAuditDb.getTenantId());

        if (tenantAudit.isRealNamePassed()) {
          updateAuditStatus(TenantRealNameStatus.AUDITED);
          // Update tenant name for all users when audit passes
          updateUserTenantName(tenantDb);
          sendRealNameAuthPassedNotice(tenantAuditDb.getTenantId());
        } else {
          updateAuditStatus(TenantRealNameStatus.FAILED_AUDIT);
          sendRealNameAuthFailureNotice(tenantAuditDb.getTenantId());
        }

        // Update tenant real-name status
        updateTenantRealName(tenantDb);

        // Update tenant audit record
        tenantAuditDb.setStatus(tenantAudit.getStatus()).setAutoAudit(false)
            .setAuditRecordData(tenantAudit.getAuditRecordData());
        tenantCertAuditRepo.save(tenantAuditDb);

        // Log operation for audit
        operationLogCmd.add(TENANT, tenantDb, TENANT_AUDIT, tenantDb.getStatus());
        return null;
      }

      /**
       * <p>
       * Updates tenant real-name status.
       * </p>
       * <p>
       * Updates tenant type and real-name status in tenant record.
       * </p>
       */
      private void updateTenantRealName(Tenant tenantDb) {
        tenantDb.setType(tenantAuditDb.getType());
        tenantDb.setRealNameStatus(tenantAudit.getStatus());
        tenantRepo.save(tenantDb);
      }

      /**
       * <p>
       * Updates user tenant name after successful audit.
       * </p>
       * <p>
       * Updates tenant name for all users and in tenant record.
       * </p>
       */
      private void updateUserTenantName(Tenant tenantDb) {
        userRepo.updateTenantNameByTenantId(tenantAuditDb.getTenantId(),
            tenantAuditDb.getTenantName());
        tenantDb.setName(tenantAuditDb.getTenantName());
      }

      /**
       * <p>
       * Updates user real-name status.
       * </p>
       * <p>
       * Sets real-name status for all users in the tenant.
       * </p>
       */
      private void updateAuditStatus(TenantRealNameStatus audited) {
        authUserCmd.realName(tenantAuditDb.getTenantId(), audited);
      }
    }.execute();
  }

  /**
   * <p>
   * Checks certificate validity.
   * </p>
   * <p>
   * Validates certificate information based on type (personal or enterprise).
   * Government type certificates are not supported for automatic checking.
   * </p>
   */
  @Override
  public void check() {
    new BizTemplate<Void>() {
      TenantCertAudit tenantAuditDb;

      @Override
      protected void checkParams() {
        // Verify certificate is submitted
        tenantAuditDb = tenantCertAuditQuery.checkAndFindByTenantId(getOptTenantId());
      }

      @Override
      protected Void process() {
        if (tenantAuditDb.getType().isPersonal()) {
          checkPersonalCert(tenantAuditDb);
        } else if (tenantAuditDb.getType().isEnterprise()) {
          checkEnterpriseCert(tenantAuditDb);
        } else {
          throw SysException.of("Government type license audit check is not supported");
        }
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Validates and retrieves tenant certificate audit.
   * </p>
   * <p>
   * Checks for duplicate submissions and validates certificate completeness.
   * </p>
   */
  private TenantCertAudit checkAndGetTenantCertAudit(Long tenantId, TenantCertAudit tenantAudit) {
    // Check for duplicate submissions
    TenantCertAudit tenantAuditDb = tenantCertAuditQuery.findByTenantId(tenantId);
    if (nonNull(tenantAuditDb)) {
      BizAssert.assertTrue(!tenantAuditDb.isRealNameAuditing()
          && !tenantAuditDb.isRealNamePassed(), TENANT_CERT_SUMMITED);
    }

    // Verify submitted information is complete
    assertTrue(tenantAudit.isCertSubmitted(), TENANT_CERT_MISSING_T,
        new Object[]{tenantAudit.getType()});
    return tenantAuditDb;
  }

  /**
   * <p>
   * Initializes tenant audit record.
   * </p>
   * <p>
   * Creates new audit record or updates existing one with new information.
   * </p>
   */
  private TenantCertAudit initTenantAudit(TenantCertAudit auditDb, TenantCertAudit audit) {
    if (isNull(auditDb)) {
      audit.setId(uidGenerator.getUID())
          .setStatus(TenantRealNameStatus.AUDITING).setAutoAudit(enableAutoAudit);
      tenantCertAuditRepo.save(audit);
      auditDb = audit;
    } else {
      copyPropertiesIgnoreNull(audit, auditDb);
    }
    return auditDb;
  }

  /**
   * <p>
   * Validates enterprise certificate information.
   * </p>
   * <p>
   * Uses OCR to recognize business license and validates company name consistency.
   * </p>
   */
  private void checkEnterpriseCert(TenantCertAudit tenantAuditDb) {
    BusinessRecognize businessRecognize = tenantCertRecognizeQuery.businessRecognize(
        tenantAuditDb.getEnterpriseCertData().getBusinessLicensePicUrl());
    // Fix third-party API bug: 晓蚕科技（北京）有限公司 recognized as 晓蚕科技(北京)有限公司
    businessRecognize.setCompanyName(
        businessRecognize.getCompanyName().replaceAll("\\(", "（").replaceAll("\\)", "）"));
    assertTrue(tenantAuditDb.getEnterpriseCertData().getName()
        .equals(businessRecognize.getCompanyName()), TENANT_NAME_BUSINESS_INCONSISTENT);
    // Verify enterprise name consistency
    checkEnterpriseLegalCert(tenantAuditDb, businessRecognize);
  }

  /**
   * <p>
   * Validates enterprise legal person certificate.
   * </p>
   * <p>
   * Uses OCR to recognize ID card and validates legal person name consistency.
   * </p>
   */
  private void checkEnterpriseLegalCert(TenantCertAudit tenantAuditDb,
      BusinessRecognize businessRecognize) {
    IdCardRecognize idCardRecognize = tenantCertRecognizeQuery.idcardRecognize(
        tenantAuditDb.getEnterpriseLegalPersonCertData().getCertBackPicUrl(),
        tenantAuditDb.getEnterpriseLegalPersonCertData().getCertFrontPicUrl()
    );
    // Verify personal name consistency
    assertTrue(tenantAuditDb.getEnterpriseLegalPersonCertData().getName()
        .equals(idCardRecognize.getName()), TENANT_NAME_ID_CARD_INCONSISTENT);
    assertTrue(tenantAuditDb.getEnterpriseLegalPersonCertData().getName()
            .equals(businessRecognize.getLegalPerson()),
        TENANT_LEGAL_PERSON_BUSINESS_INCONSISTENT);
  }

  /**
   * <p>
   * Validates personal certificate information.
   * </p>
   * <p>
   * Uses OCR to recognize ID card and validates personal name consistency.
   * </p>
   */
  private void checkPersonalCert(TenantCertAudit tenantAuditDb) {
    IdCardRecognize idCardRecognize = tenantCertRecognizeQuery.idcardRecognize(
        tenantAuditDb.getPersonalCertData().getCertBackPicUrl(),
        tenantAuditDb.getPersonalCertData().getCertFrontPicUrl()
    );
    // Verify personal name consistency
    assertTrue(tenantAuditDb.getPersonalCertData().getName()
        .equals(idCardRecognize.getName()), TENANT_NAME_ID_CARD_INCONSISTENT);
  }

  /**
   * <p>
   * Sends real-name authentication handling notice.
   * </p>
   * <p>
   * Sends SMS and email notifications to specified users about authentication status.
   * </p>
   */
  private void sendRealNameAuthHandleNotice(List<Long> receiveUserIds, String outId,
      String fullName) {
    try {
      if (isEmpty(receiveUserIds)) {
        return;
      }
      Map<String, String> templateParams = Map.of("companyOrUserMobile", fullName,
          "serviceType", message(NOTICE_REAL_NAME_AUTH), "noOrId", outId);
      SendNoticeDto noticeDto = new SendNoticeDto();
      noticeDto.setNoticeTypes(List.of(NoticeType.SMS, NoticeType.EMAIL));
      noticeDto.setSendEmailParam(
          NoticeConverter.toSendTemplateEmailParam(EmailBizKey.TODO_REALNAME_AUTH,
              outId, ReceiveObjectType.USER, receiveUserIds, Map.of("", templateParams)));
      noticeDto.setSendSmsParam(
          NoticeConverter.toSendTemplateSmsParam(SmsBizKey.TODO_REALNAME_AUTH,
              outId, ReceiveObjectType.USER, receiveUserIds, templateParams));
      noticeManager.send(noticeDto);
    } catch (Exception e) {
      // Allow notification sending to fail
      log.error("Send real-name auth handle notice exception: ", e);
    }
  }

  /**
   * <p>
   * Sends real-name authentication passed notice.
   * </p>
   * <p>
   * Sends SMS and email notifications to system administrators about successful authentication.
   * </p>
   */
  private void sendRealNameAuthPassedNotice(Long tenantId) {
    try {
      List<Long> receiveUserIds = userRepo.findIdsSysAdminUser(tenantId);
      if (isEmpty(receiveUserIds)) {
        return;
      }
      Map<String, String> templateParams = Map.of("object", message(NOTICE_REAL_NAME_AUTH));
      SendNoticeDto noticeDto = new SendNoticeDto();
      noticeDto.setNoticeTypes(List.of(NoticeType.SMS, NoticeType.EMAIL));
      noticeDto.setSendEmailParam(NoticeConverter.toSendTemplateEmailParam(
          EmailBizKey.REALNAME_AUTH_PASSED, String.valueOf(tenantId),
          ReceiveObjectType.USER, receiveUserIds, Map.of("", templateParams)));
      noticeDto.setSendSmsParam(NoticeConverter.toSendTemplateSmsParam(
          SmsBizKey.REALNAME_AUTH_PASSED, String.valueOf(tenantId),
          ReceiveObjectType.USER, receiveUserIds, templateParams));
      noticeManager.send(noticeDto);
    } catch (Exception e) {
      // Allow notification sending to fail
      log.error("Send {} notice exception: {}", EmailBizKey.REALNAME_AUTH_PASSED.getValue(),
          e.getMessage());
    }
  }

  /**
   * <p>
   * Sends real-name authentication failure notice.
   * </p>
   * <p>
   * Sends SMS and email notifications to system administrators about failed authentication.
   * </p>
   */
  private void sendRealNameAuthFailureNotice(Long tenantId) {
    try {
      List<Long> receiveUserIds = userRepo.findIdsSysAdminUser(tenantId);
      if (isEmpty(receiveUserIds)) {
        return;
      }
      Map<String, String> templateParams = Map.of("object", message(NOTICE_REAL_NAME_AUTH),
          "failureReason", message(NOTICE_REAL_NAME_AUTH_FAILURE_REASON));
      SendNoticeDto noticeDto = new SendNoticeDto();
      noticeDto.setNoticeTypes(List.of(NoticeType.SMS, NoticeType.EMAIL));
      noticeDto.setSendEmailParam(NoticeConverter.toSendTemplateEmailParam(
          EmailBizKey.REALNAME_AUDIT_FAILURE, String.valueOf(tenantId), ReceiveObjectType.USER,
          receiveUserIds, Map.of("", templateParams)));
      noticeDto.setSendSmsParam(NoticeConverter.toSendTemplateSmsParam(
          SmsBizKey.REALNAME_AUDIT_FAILURE, String.valueOf(tenantId), ReceiveObjectType.USER,
          receiveUserIds, templateParams));
      noticeManager.send(noticeDto);
    } catch (Exception e) {
      // Allow notification sending to fail
      log.error("Send {} notice exception: {}", EmailBizKey.REALNAME_AUDIT_FAILURE.getValue(),
          e.getMessage());
    }
  }

  @Override
  protected BaseRepository<TenantCertAudit, Long> getRepository() {
    return this.tenantCertAuditRepo;
  }
}
