package cloud.xcan.angus.core.gm.interfaces.tenant.facade.internal;

import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.gm.application.cmd.tenant.TenantCmd;
import cloud.xcan.angus.core.gm.application.query.tenant.TenantQuery;
import cloud.xcan.angus.core.gm.domain.tenant.Tenant;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.TenantFacade;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.dto.TenantCreateDto;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.dto.TenantFindDto;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.dto.TenantUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.internal.assembler.TenantAssembler;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo.TenantDetailVo;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo.TenantListVo;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo.TenantStatsVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * Implementation of tenant facade
 */
@Service
public class TenantFacadeImpl implements TenantFacade {

  @Resource
  private TenantCmd tenantCmd;

  @Resource
  private TenantQuery tenantQuery;

  @Override
  public TenantDetailVo create(TenantCreateDto dto) {
    // DTO -> Domain
    Tenant tenant = TenantAssembler.toCreateDomain(dto);
    // Create tenant
    Tenant saved = tenantCmd.create(tenant);
    // Domain -> VO
    return TenantAssembler.toDetailVo(saved);
  }

  @Override
  public TenantDetailVo update(Long id, TenantUpdateDto dto) {
    // DTO -> Domain
    Tenant tenant = TenantAssembler.toUpdateDomain(id, dto);
    // Update tenant
    Tenant saved = tenantCmd.update(tenant);
    // Domain -> VO
    return TenantAssembler.toDetailVo(saved);
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
    // DTO -> Specification
    GenericSpecification<Tenant> spec = TenantAssembler.getSpecification(dto);
    // Query data
    Page<Tenant> page = tenantQuery.find(spec, dto.tranPage());
    // Domain -> VO (with pagination)
    return buildVoPageResult(page, TenantAssembler::toListVo);
  }

  @Override
  public TenantStatsVo getStats() {
    return tenantQuery.getStats();
  }

  @Override
  public void enable(Long id) {
    tenantCmd.enable(id);
  }

  @Override
  public void disable(Long id) {
    tenantCmd.disable(id);
  }
}
