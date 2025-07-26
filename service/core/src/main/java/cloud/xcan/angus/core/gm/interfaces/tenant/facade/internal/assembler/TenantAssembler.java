package cloud.xcan.angus.core.gm.interfaces.tenant.facade.internal.assembler;

import static cloud.xcan.angus.api.enums.TenantSource.BACKGROUND_SIGNUP;
import static cloud.xcan.angus.api.enums.TenantSource.PLATFORM_SIGNUP;
import static cloud.xcan.angus.core.gm.application.query.tenant.impl.TenantQueryImpl.genTenantNo;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;
import static cloud.xcan.angus.spec.utils.ObjectUtils.stringSafe;

import cloud.xcan.angus.api.commonlink.tenant.Tenant;
import cloud.xcan.angus.api.enums.TenantRealNameStatus;
import cloud.xcan.angus.api.enums.TenantStatus;
import cloud.xcan.angus.api.enums.TenantType;
import cloud.xcan.angus.api.gm.tenant.dto.TenantAddByMobileDto;
import cloud.xcan.angus.api.gm.tenant.dto.TenantAddDto;
import cloud.xcan.angus.api.gm.tenant.dto.TenantFindDto;
import cloud.xcan.angus.api.gm.tenant.dto.TenantReplaceDto;
import cloud.xcan.angus.api.gm.tenant.dto.TenantUpdateDto;
import cloud.xcan.angus.api.gm.tenant.vo.TenantDetailVo;
import cloud.xcan.angus.api.gm.tenant.vo.TenantVo;
import cloud.xcan.angus.core.gm.domain.tenant.audit.TenantCertAudit;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;


public class TenantAssembler {

  public static Tenant addDtoToDomain(TenantAddDto dto) {
    return new Tenant().setName(dto.getName())
        .setNo(genTenantNo())
        .setAddress("")
        .setType(nullSafe(dto.getType(), TenantType.UNKNOWN))
        .setSource(BACKGROUND_SIGNUP)
        .setStatus(TenantStatus.ENABLED)
        .setRemark(stringSafe(dto.getRemark()))
        .setAddress(dto.getAddress())
        .setRealNameStatus(dto.isCertSubmitted() ? TenantRealNameStatus.AUDITING
            : TenantRealNameStatus.NOT_SUBMITTED)
        .setLocked(false);
  }

  public static Tenant addDtoToDomain() {
    String no = genTenantNo();
    return new Tenant().setName(no)
        .setNo(no)
        .setAddress("")
        .setType(TenantType.UNKNOWN)
        .setSource(PLATFORM_SIGNUP)
        .setStatus(TenantStatus.ENABLED)
        .setRemark("")
        .setRealNameStatus(TenantRealNameStatus.NOT_SUBMITTED)
        .setLocked(false);
  }

  public static TenantCertAudit addDtoToTenantAudit(TenantAddDto dto) {
    return new TenantCertAudit().setStatus(
            dto.isCertSubmitted() ? TenantRealNameStatus.AUDITING : TenantRealNameStatus.NOT_SUBMITTED)
        .setType(nullSafe(dto.getType(), TenantType.UNKNOWN))
        .setEnterpriseCertData(dto.getEnterpriseCert())
        .setEnterpriseLegalPersonCertData(dto.getEnterpriseLegalPersonCert())
        .setPersonalCertData(dto.getPersonalCert())
        .setGovernmentCertData(dto.getGovernmentCert());
  }

  public static TenantCertAudit addDtoToTenantAudit(TenantAddByMobileDto dto) {
    return new TenantCertAudit().setStatus(TenantRealNameStatus.NOT_SUBMITTED)
        .setType(TenantType.UNKNOWN);
  }

  public static TenantDetailVo toDetailVo(Tenant tenant) {
    return new TenantDetailVo()
        .setId(tenant.getId())
        .setNo(tenant.getNo())
        .setName(tenant.getName())
        .setType(tenant.getType())
        .setSource(tenant.getSource())
        .setStatus(tenant.getStatus())
        .setRealNameStatus(tenant.getRealNameStatus())
        .setAddress(tenant.getAddress())
        .setUserCount(tenant.getUserCount())
        .setLocked(tenant.getLocked())
        .setLastLockDate(tenant.getLastLockDate())
        .setLockStartDate(tenant.getLockStartDate())
        .setLockEndDate(tenant.getLockEndDate())
        .setCreatedBy(tenant.getCreatedBy())
        .setCreatedDate(tenant.getCreatedDate())
        .setLastModifiedBy(tenant.getLastModifiedBy())
        .setLastModifiedDate(tenant.getLastModifiedDate())
        .setRemark(tenant.getRemark());
  }

  public static TenantVo toVo(Tenant tenant) {
    return new TenantVo()
        .setId(tenant.getId())
        .setNo(tenant.getNo())
        .setName(tenant.getName())
        .setType(tenant.getType())
        .setSource(tenant.getSource())
        .setStatus(tenant.getStatus())
        .setRealNameStatus(tenant.getRealNameStatus())
        .setAddress(tenant.getAddress())
        .setUserCount(tenant.getUserCount())
        .setLocked(tenant.getLocked())
        .setLastLockDate(tenant.getLastLockDate())
        .setLockStartDate(tenant.getLockStartDate())
        .setLockEndDate(tenant.getLockEndDate())
        .setRemark(tenant.getRemark())
        .setCreatedBy(tenant.getCreatedBy())
        .setCreatedDate(tenant.getCreatedDate())
        .setLastModifiedBy(tenant.getLastModifiedBy())
        .setLastModifiedDate(tenant.getLastModifiedDate());
  }

  public static Tenant replaceDtoToDomain(TenantReplaceDto dto) {
    return new Tenant().setId(dto.getId())
        .setName(dto.getName())
        //.setNo(getTenantNo()) // Init when insert
        .setType(nullSafe(dto.getType(), TenantType.UNKNOWN)) // Update when not audited
        .setSource(BACKGROUND_SIGNUP) // Update when not audited
        //.setStatus(TenantStatus.ENABLED)
        .setRemark(dto.getRemark())
        .setAddress(dto.getAddress())
        .setRealNameStatus(dto.isCertSubmitted()
            ? TenantRealNameStatus.AUDITING : TenantRealNameStatus.NOT_SUBMITTED)
        /*.setUserCount(0)   // Init when insert
        .setLocked(false)*/;
  }

  public static Tenant updateDtoToDomain(TenantUpdateDto dto) {
    return new Tenant().setId(dto.getId())
        .setName(dto.getName())
        //.setNo(getTenantNo())
        .setType(dto.getType()) // Update when not audited
        //.setSource(BACKGROUND_SIGNUP)
        //.setStatus(TenantStatus.ENABLED)
        .setRemark(dto.getRemark())
        .setAddress(dto.getAddress())
        .setRealNameStatus(dto.isCertSubmitted()
            ? TenantRealNameStatus.AUDITING : TenantRealNameStatus.NOT_SUBMITTED)
        /*.setUserCount(0)   // Init when insert
        .setLocked(false)*/;
  }

  public static GenericSpecification<Tenant> getSpecification(TenantFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate", "applyCancelDate",
            "lastLockDate", "lockStartDate", "lockEndDate")
        .orderByFields("id", "name", "createdDate")
        .build();
    return new GenericSpecification<>(filters);
  }

}
