package cloud.xcan.angus.core.gm.interfaces.tenant.facade.internal;

import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.gm.application.cmd.tenant.TenantCmd;
import cloud.xcan.angus.core.gm.application.query.tenant.TenantQuery;
import cloud.xcan.angus.core.gm.domain.tenant.Tenant;
import cloud.xcan.angus.core.gm.domain.tenant.enums.TenantStatus;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.TenantFacade;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.dto.TenantConfigUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.dto.TenantCreateDto;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.dto.TenantFindDto;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.dto.TenantStatusUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.dto.TenantUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.internal.assembler.TenantAssembler;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo.TenantConfigVo;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo.TenantDetailVo;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo.TenantListVo;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo.TenantStatsVo;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo.TenantStatusUpdateVo;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo.TenantUsageVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
public class TenantFacadeImpl implements TenantFacade {

  @Resource
  private TenantCmd tenantCmd;

  @Resource
  private TenantQuery tenantQuery;

  @Override
  public TenantDetailVo create(TenantCreateDto dto) {
    Tenant tenant = TenantAssembler.toCreateDomain(dto);
    Tenant saved = tenantCmd.create(tenant);
    return TenantAssembler.toDetailVo(saved);
  }

  @Override
  public TenantDetailVo update(Long id, TenantUpdateDto dto) {
    Tenant tenant = TenantAssembler.toUpdateDomain(id, dto);
    Tenant saved = tenantCmd.update(tenant);
    return TenantAssembler.toDetailVo(saved);
  }

  @Override
  public TenantStatusUpdateVo updateStatus(Long id, TenantStatusUpdateDto dto) {
    if (TenantStatus.ENABLED.equals(dto.getStatus())) {
      tenantCmd.enable(id);
    } else {
      tenantCmd.disable(id);
    }
    TenantStatusUpdateVo vo = new TenantStatusUpdateVo();
    vo.setId(id);
    vo.setStatus(dto.getStatus());
    vo.setModifiedDate(LocalDateTime.now());
    return vo;
  }

  @Override
  public void delete(Long id) {
    tenantCmd.delete(id);
  }

  @Override
  public TenantDetailVo getDetail(Long id) {
    Tenant tenant = tenantQuery.findAndCheck(id);
    return TenantAssembler.toDetailVo(tenant);
  }

  @Override
  public PageResult<TenantListVo> list(TenantFindDto dto) {
    GenericSpecification<Tenant> spec = TenantAssembler.getSpecification(dto);
    Page<Tenant> page = tenantQuery.find(spec, dto.tranPage(),
        dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, TenantAssembler::toListVo);
  }

  /**
   * Get match search fields for full-text search
   * Returns fields configured in TenantAssembler.getSpecification()
   */
  private String[] getMatchSearchFields(Class<?> dtoClass) {
    if (TenantFindDto.class.equals(dtoClass)) {
      return new String[]{"name", "code"};
    }
    return new String[0];
  }

  @Override
  public TenantStatsVo getStats() {
    return tenantQuery.getStats();
  }

  @Override
  public TenantConfigVo getConfig(Long id) {
    // TODO: Implement get tenant config
    return new TenantConfigVo();
  }

  @Override
  public TenantConfigVo updateConfig(Long id, TenantConfigUpdateDto dto) {
    // TODO: Implement update tenant config
    TenantConfigVo vo = new TenantConfigVo();
    vo.setMaxUsers(dto.getMaxUsers());
    vo.setMaxStorage(dto.getMaxStorage());
    vo.setMaxDepartments(dto.getMaxDepartments());
    vo.setFeatures(dto.getFeatures());
    vo.setCustomDomain(dto.getCustomDomain());
    vo.setModifiedDate(LocalDateTime.now());
    return vo;
  }

  @Override
  public TenantUsageVo getUsage(Long id) {
    // TODO: Implement get tenant usage
    return new TenantUsageVo();
  }
}
