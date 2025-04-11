package cloud.xcan.angus.core.gm.application.query.tenant.impl;

import static cloud.xcan.angus.api.commonlink.UCConstant.TOP_TENANT_ADMIN;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.hasToRole;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isTenantClient;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.tenant.TenantCertAuditQuery;
import cloud.xcan.angus.core.gm.domain.tenant.audit.TenantCertAudit;
import cloud.xcan.angus.core.gm.domain.tenant.audit.TenantCertAuditRepo;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.spec.jackson.desensitized.DesensitizedUtils;
import jakarta.annotation.Resource;

@Biz
public class TenantCertAuditQueryImpl implements TenantCertAuditQuery {

  @Resource
  private TenantCertAuditRepo tenantCertAuditRepo;

  @Override
  public TenantCertAudit detail() {
    return new BizTemplate<TenantCertAudit>() {

      @Override
      protected TenantCertAudit process() {
        return findByTenantId(getOptTenantId());
      }
    }.execute();
  }

  @Override
  public TenantCertAudit find0(Long id) {
    return tenantCertAuditRepo.findById(id).orElse(null);
  }

  @Override
  public TenantCertAudit checkAndFind(Long id) {
    TenantCertAudit certAudit = tenantCertAuditRepo.findById(id)
        .orElseThrow(() -> ResourceNotFound.of(id, "TenantCertAudit"));
    desensitizationCert(certAudit);
    return certAudit;
  }

  @Override
  public TenantCertAudit checkAndFindByTenantId(Long tenantId) {
    TenantCertAudit certAudit = tenantCertAuditRepo.findByTenantId(tenantId)
        .orElseThrow(() -> ResourceNotFound.of(tenantId, "TenantCertAudit"));
    // Forbid non tenant operation administrators to view tenant cert information
    desensitizationCert(certAudit);
    return certAudit;
  }

  @Override
  public TenantCertAudit findByTenantId(Long tenantId) {
    TenantCertAudit certAudit = tenantCertAuditRepo.findByTenantId(tenantId).orElse(null);
    if (isNull(certAudit)) {
      return null;
    }
    // Forbid non tenant operation administrators to view tenant cert information
    desensitizationCert(certAudit);
    return certAudit;
  }

  private void desensitizationCert(TenantCertAudit certAudit) {
    // Forbid non tenant operation administrators to view tenant cert information
    if (certAudit.isRealNamePassed() && (isTenantClient() || !hasToRole(TOP_TENANT_ADMIN))) {
      switch (certAudit.getType()) {
        case PERSONAL:
          certAudit.getPersonalCertData().setCertBackPicUrl(null).setCertFrontPicUrl(null)
              .setCertNo(DesensitizedUtils.carNumber(certAudit.getPersonalCertData().getCertNo()));
          break;
        case ENTERPRISE:
          certAudit.getEnterpriseCertData().setBusinessLicensePicUrl(null)
              .setCreditCode(DesensitizedUtils.carNumber(certAudit.getEnterpriseCertData()
                  .getCreditCode()));
          certAudit.getEnterpriseLegalPersonCertData().setCertBackPicUrl(null)
              .setCertFrontPicUrl(null).setCertNo(DesensitizedUtils
                  .carNumber(certAudit.getEnterpriseLegalPersonCertData().getCertNo()));
          break;
        case GOVERNMENT:
          certAudit.getGovernmentCertData().setOrgCertPicUrl(null)
              .setOrgCode(DesensitizedUtils.carNumber(certAudit.getGovernmentCertData()
                  .getOrgCode()));
          break;
        default: /* UNKNOWN: **/
          // NOOP
      }
    }
  }
}
