package cloud.xcan.angus.core.gm.application.query.tenant.impl;

import static cloud.xcan.angus.api.commonlink.UCConstant.TOP_TENANT_ADMIN;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.hasToRole;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isTenantClient;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;


import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.tenant.TenantCertAuditQuery;
import cloud.xcan.angus.core.gm.domain.tenant.audit.TenantCertAudit;
import cloud.xcan.angus.core.gm.domain.tenant.audit.TenantCertAuditRepo;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.spec.jackson.desensitized.DesensitizedUtils;
import jakarta.annotation.Resource;

/**
 * <p>
 * Implementation of tenant certificate audit query operations.
 * </p>
 * <p>
 * Manages tenant certificate audit retrieval, validation, and data desensitization. Provides
 * comprehensive tenant certificate audit querying with security controls.
 * </p>
 * <p>
 * Supports tenant certificate audit detail retrieval, validation, desensitization, and access
 * control for comprehensive tenant certificate audit administration.
 * </p>
 */
@org.springframework.stereotype.Service
public class TenantCertAuditQueryImpl implements TenantCertAuditQuery {

  @Resource
  private TenantCertAuditRepo tenantCertAuditRepo;

  /**
   * <p>
   * Retrieves detailed tenant certificate audit information for current tenant.
   * </p>
   * <p>
   * Fetches certificate audit record for the current tenant context. Applies desensitization based
   * on user permissions.
   * </p>
   */
  @Override
  public TenantCertAudit detail() {
    return new BizTemplate<TenantCertAudit>() {

      @Override
      protected TenantCertAudit process() {
        return findByTenantId(getOptTenantId());
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves tenant certificate audit by ID without validation.
   * </p>
   * <p>
   * Returns certificate audit without existence validation. Returns null if certificate audit does
   * not exist.
   * </p>
   */
  @Override
  public TenantCertAudit find0(Long id) {
    return tenantCertAuditRepo.findById(id).orElse(null);
  }

  /**
   * <p>
   * Validates and retrieves tenant certificate audit by ID.
   * </p>
   * <p>
   * Returns certificate audit with existence validation and desensitization. Throws
   * ResourceNotFound if certificate audit does not exist.
   * </p>
   */
  @Override
  public TenantCertAudit checkAndFind(Long id) {
    TenantCertAudit certAudit = tenantCertAuditRepo.findById(id)
        .orElseThrow(() -> ResourceNotFound.of(id, "TenantCertAudit"));
    desensitizationCert(certAudit);
    return certAudit;
  }

  /**
   * <p>
   * Validates and retrieves tenant certificate audit by tenant ID.
   * </p>
   * <p>
   * Returns certificate audit with existence validation and desensitization. Throws
   * ResourceNotFound if certificate audit does not exist.
   * </p>
   */
  @Override
  public TenantCertAudit checkAndFindByTenantId(Long tenantId) {
    TenantCertAudit certAudit = tenantCertAuditRepo.findByTenantId(tenantId)
        .orElseThrow(() -> ResourceNotFound.of(tenantId, "TenantCertAudit"));
    // Forbid non tenant operation administrators to view tenant cert information
    desensitizationCert(certAudit);
    return certAudit;
  }

  /**
   * <p>
   * Retrieves tenant certificate audit by tenant ID.
   * </p>
   * <p>
   * Returns certificate audit with desensitization based on permissions. Returns null if
   * certificate audit does not exist.
   * </p>
   */
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

  /**
   * <p>
   * Applies data desensitization to certificate audit information.
   * </p>
   * <p>
   * Removes sensitive certificate data based on user permissions and certificate type. Applies
   * different desensitization rules for personal, enterprise, and government certificates.
   * </p>
   */
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
