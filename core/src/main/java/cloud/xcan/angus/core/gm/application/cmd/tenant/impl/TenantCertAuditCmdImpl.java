package cloud.xcan.angus.core.gm.application.cmd.tenant.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.domain.UCCoreMessage.NOTICE_REAL_NAME_AUTH;
import static cloud.xcan.angus.core.gm.domain.UCCoreMessage.NOTICE_REAL_NAME_AUTH_FAILURE_REASON;
import static cloud.xcan.angus.core.gm.domain.UCCoreMessage.TENANT_AUDIT_FAILURE_REASON_MISSING;
import static cloud.xcan.angus.core.gm.domain.UCCoreMessage.TENANT_CERT_MISSING_T;
import static cloud.xcan.angus.core.gm.domain.UCCoreMessage.TENANT_CERT_SUMMITED;
import static cloud.xcan.angus.core.gm.domain.UCCoreMessage.TENANT_LEGAL_PERSON_BUSINESS_INCONSISTENT;
import static cloud.xcan.angus.core.gm.domain.UCCoreMessage.TENANT_NAME_BUSINESS_INCONSISTENT;
import static cloud.xcan.angus.core.gm.domain.UCCoreMessage.TENANT_NAME_ID_CARD_INCONSISTENT;
import static cloud.xcan.angus.core.gm.domain.UCCoreMessage.TENANT_REAL_NAME_PASSED;
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
import cloud.xcan.angus.api.commonlink.tenant.TenantRealNameStatus;
import cloud.xcan.angus.api.commonlink.tenant.TenantRepo;
import cloud.xcan.angus.api.commonlink.user.UserRepo;
import cloud.xcan.angus.api.enums.NoticeType;
import cloud.xcan.angus.api.enums.ReceiveObjectType;
import cloud.xcan.angus.api.gm.notice.dto.SendNoticeDto;
import cloud.xcan.angus.api.manager.NoticeManager;
import cloud.xcan.angus.api.manager.converter.NoticeConverter;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizAssert;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.authuser.AuthUserCmd;
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

  @Value("${xcan.tenant.enableAutoAudit:false}")
  private boolean enableAutoAudit;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Void submit(TenantCertAudit tenantAudit) {
    return new BizTemplate<Void>() {
      TenantCertAudit tenantAuditDb;
      // final Long tenantId = getOptTenantId(); <- Fix:: Cannot get the modified value in tenantCmd#add()
      Long tenantId;

      @Override
      protected void checkParams() {
        tenantId = getOptTenantId();
        tenantAuditDb = checkAndGetTenantCertAudit(tenantId, tenantAudit);
      }

      @Override
      protected Void process() {
        // The first submission
        tenantAuditDb = initTenantAudit(tenantAuditDb, tenantAudit);

        // The government does not support automatic audit
        if (isAutoAudit()) {
          try {
            checkCertValid();
            updateStatusPassed();
            updateUserTenantName();
            sendRealNameAuthPassedNotice(tenantAuditDb.getTenantId());
          } catch (Exception e) {
            updateStatusFailed(e);
            sendRealNameAuthFailureNotice(tenantAuditDb.getTenantId());
          }
        } else {
          // Waiting for manual audit
          // TODO 发送审核提醒消息
          updateStatusInAuditing();
        }

        // Update tenant real-name status
        updateTenantRealName();

        // Update tenant audit
        tenantCertAuditRepo.save(tenantAuditDb);
        return null;
      }

      private boolean isAutoAudit() {
        return enableAutoAudit && (tenantAuditDb.getType().isPersonal()
            || tenantAuditDb.getType().isEnterprise());
      }

      private void checkCertValid() {
        if (tenantAuditDb.getType().isPersonal()) {
          checkPersonalCert(tenantAuditDb);
        } else {
          checkEnterpriseCert(tenantAuditDb);
        }
      }

      private void updateTenantRealName() {
        Tenant tenantDb = tenantQuery.checkAndFind(tenantId);
        tenantDb.setType(tenantAuditDb.getType());
        tenantDb.setRealNameStatus(tenantAuditDb.getStatus());
        tenantDb.setName(tenantAuditDb.getTenantName());
        tenantRepo.save(tenantDb);
      }

      private void updateStatusInAuditing() {
        tenantAuditDb.setStatus(TenantRealNameStatus.AUDITING).setAutoAudit(false);
        authUserCmd.realName(tenantId, TenantRealNameStatus.AUDITING);
      }

      private void updateStatusFailed(Exception e) {
        tenantAuditDb.setStatus(TenantRealNameStatus.FAILED_AUDIT).setAutoAudit(true)
            .setAuditRecordData(new AuditRecordData().setAuditUserId(null)
                .setAuditUserName(null).setAuditDate(LocalDateTime.now())
                .setReason(e.getMessage()));
        authUserCmd.realName(tenantId, TenantRealNameStatus.FAILED_AUDIT);
      }

      private void updateStatusPassed() {
        tenantAuditDb.setStatus(TenantRealNameStatus.AUDITED).setAutoAudit(true)
            .setAuditRecordData(new AuditRecordData().setAuditUserId(null)
                .setAuditUserName(null).setAuditDate(LocalDateTime.now()));
      }

      private void updateUserTenantName() {
        // Update the tenant name of user when audited passed
        userRepo.updateTenantNameByTenantId(tenantId, tenantAuditDb.getTenantName());
        // Set the tenant real-name status of user
        authUserCmd.realName(tenantId, TenantRealNameStatus.AUDITED);
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void audit(TenantCertAudit tenantAudit) {
    new BizTemplate<Void>(false) {
      TenantCertAudit tenantAuditDb;

      @Override
      protected void checkParams() {
        // Check the cert is submitted
        tenantAuditDb = tenantCertAuditQuery.checkAndFind(tenantAudit.getId());

        // Check the auditing status
        assertTrue(!tenantAuditDb.isRealNamePassed(), TENANT_REAL_NAME_PASSED);

        // Check the reason for failure is required
        assertTrue(!tenantAudit.isRealNameFailed()
                || isNotEmpty(tenantAudit.getAuditRecordData().getReason()),
            TENANT_AUDIT_FAILURE_REASON_MISSING);

        // Check the real-name cert information is required
        assertTrue(tenantAuditDb.isCertSubmitted(), TENANT_CERT_MISSING_T,
            new Object[]{tenantAuditDb.getType()});
      }

      @Override
      protected Void process() {
        Tenant tenantDb = tenantQuery.checkAndFind(tenantAuditDb.getTenantId());

        if (tenantAudit.isRealNamePassed()) {
          updateAuditStatus(TenantRealNameStatus.AUDITED);
          // Update the tenant name of user when audited passed
          updateUserTenantName(tenantDb);
          sendRealNameAuthPassedNotice(tenantAuditDb.getTenantId());
        } else {
          updateAuditStatus(TenantRealNameStatus.FAILED_AUDIT);
          sendRealNameAuthFailureNotice(tenantAuditDb.getTenantId());
        }

        // Update tenant real-name status
        updateTenantRealName(tenantDb);

        // Update tenant audit
        tenantAuditDb.setStatus(tenantAudit.getStatus()).setAutoAudit(false)
            .setAuditRecordData(tenantAudit.getAuditRecordData());
        tenantCertAuditRepo.save(tenantAuditDb);
        return null;
      }

      private void updateTenantRealName(Tenant tenantDb) {
        tenantDb.setType(tenantAuditDb.getType());
        tenantDb.setRealNameStatus(tenantAudit.getStatus());
        tenantRepo.save(tenantDb);
      }

      private void updateUserTenantName(Tenant tenantDb) {
        userRepo.updateTenantNameByTenantId(tenantAuditDb.getTenantId(),
            tenantAuditDb.getTenantName());
        tenantDb.setName(tenantAuditDb.getTenantName());
      }

      private void updateAuditStatus(TenantRealNameStatus audited) {
        authUserCmd.realName(tenantAuditDb.getTenantId(), audited);
      }
    }.execute();
  }

  @Override
  public void check() {
    new BizTemplate<Void>() {
      TenantCertAudit tenantAuditDb;

      @Override
      protected void checkParams() {
        // Check the cert is submitted
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

  private TenantCertAudit checkAndGetTenantCertAudit(Long tenantId, TenantCertAudit tenantAudit) {
    // Check the duplicate submissions
    TenantCertAudit tenantAuditDb = tenantCertAuditQuery.findByTenantId(tenantId);
    if (nonNull(tenantAuditDb)) {
      BizAssert.assertTrue(!tenantAuditDb.isRealNameAuditing()
          && !tenantAuditDb.isRealNamePassed(), TENANT_CERT_SUMMITED);
    }

    // Check whether the submitted information is completed
    assertTrue(tenantAudit.isCertSubmitted(), TENANT_CERT_MISSING_T,
        new Object[]{tenantAudit.getType()});
    return tenantAuditDb;
  }

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

  private void checkEnterpriseCert(TenantCertAudit tenantAuditDb) {
    BusinessRecognize businessRecognize = tenantCertRecognizeQuery.businessRecognize(
        tenantAuditDb.getEnterpriseCertData().getBusinessLicensePicUrl());
    // Fix 三方接口BUG :: 晓蚕科技（北京）有限公司 识别成 晓蚕科技(北京)有限公司
    businessRecognize.setCompanyName(
        businessRecognize.getCompanyName().replaceAll("\\(", "（").replaceAll("\\)", "）"));
    assertTrue(tenantAuditDb.getEnterpriseCertData().getName()
        .equals(businessRecognize.getCompanyName()), TENANT_NAME_BUSINESS_INCONSISTENT);
    // Check enterprise name must be consistent
    checkEnterpriseLegalCert(tenantAuditDb, businessRecognize);
  }

  private void checkEnterpriseLegalCert(TenantCertAudit tenantAuditDb,
      BusinessRecognize businessRecognize) {
    IdCardRecognize idCardRecognize = tenantCertRecognizeQuery.idcardRecognize(
        tenantAuditDb.getEnterpriseLegalPersonCertData().getCertBackPicUrl(),
        tenantAuditDb.getEnterpriseLegalPersonCertData().getCertFrontPicUrl()
    );
    // Check personal name must be consistent
    assertTrue(tenantAuditDb.getEnterpriseLegalPersonCertData().getName()
        .equals(idCardRecognize.getName()), TENANT_NAME_ID_CARD_INCONSISTENT);
    assertTrue(tenantAuditDb.getEnterpriseLegalPersonCertData().getName()
            .equals(businessRecognize.getLegalPerson()),
        TENANT_LEGAL_PERSON_BUSINESS_INCONSISTENT);
  }

  private void checkPersonalCert(TenantCertAudit tenantAuditDb) {
    IdCardRecognize idCardRecognize = tenantCertRecognizeQuery.idcardRecognize(
        tenantAuditDb.getPersonalCertData().getCertBackPicUrl(),
        tenantAuditDb.getPersonalCertData().getCertFrontPicUrl()
    );
    // Check personal name must be consistent
    assertTrue(tenantAuditDb.getPersonalCertData().getName()
        .equals(idCardRecognize.getName()), TENANT_NAME_ID_CARD_INCONSISTENT);
  }

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
      // Allow to send failure
      log.error("Send real-name auth handle notice exception: ", e);
    }
  }

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
      // Allow send failure
      log.error("Send {} notice exception: {}", EmailBizKey.REALNAME_AUTH_PASSED.getValue(),
          e.getMessage());
    }
  }

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
      // Allow to send failure
      log.error("Send {} notice exception: {}", EmailBizKey.REALNAME_AUDIT_FAILURE.getValue(),
          e.getMessage());
    }
  }

  @Override
  protected BaseRepository<TenantCertAudit, Long> getRepository() {
    return this.tenantCertAuditRepo;
  }
}
