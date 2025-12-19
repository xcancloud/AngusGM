package cloud.xcan.angus.core.gm.interfaces.tenant.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.tenant.facade.internal.assembler.TenantAssembler.addDtoToDomain;
import static cloud.xcan.angus.core.gm.interfaces.tenant.facade.internal.assembler.TenantAssembler.addDtoToTenantAudit;
import static cloud.xcan.angus.core.gm.interfaces.tenant.facade.internal.assembler.TenantAssembler.getSpecification;
import static cloud.xcan.angus.core.gm.interfaces.tenant.facade.internal.assembler.TenantAssembler.replaceDtoToDomain;
import static cloud.xcan.angus.core.gm.interfaces.tenant.facade.internal.assembler.TenantAssembler.toDetailVo;
import static cloud.xcan.angus.core.gm.interfaces.tenant.facade.internal.assembler.TenantAssembler.updateDtoToDomain;
import static cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler.UserAssembler.addTenantUserDto;
import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.api.commonlink.tenant.Tenant;
import cloud.xcan.angus.api.enums.UserSource;
import cloud.xcan.angus.api.gm.tenant.dto.TenantAddByMobileDto;
import cloud.xcan.angus.api.gm.tenant.dto.TenantAddDto;
import cloud.xcan.angus.api.gm.tenant.dto.TenantFindDto;
import cloud.xcan.angus.api.gm.tenant.dto.TenantLockedDto;
import cloud.xcan.angus.api.gm.tenant.dto.TenantReplaceDto;
import cloud.xcan.angus.api.gm.tenant.dto.TenantUpdateDto;
import cloud.xcan.angus.api.gm.tenant.vo.TenantDetailVo;
import cloud.xcan.angus.api.gm.tenant.vo.TenantVo;
import cloud.xcan.angus.api.gm.user.dto.UserAddDto;
import cloud.xcan.angus.api.gm.user.dto.UserUpdateDto;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.tenant.TenantCmd;
import cloud.xcan.angus.core.gm.application.query.tenant.TenantQuery;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.TenantFacade;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.internal.assembler.TenantAssembler;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.internal.assembler.TenantAuditAssembler;
import cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler.UserAssembler;
import cloud.xcan.angus.core.utils.CoreUtils;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.remote.dto.EnabledOrDisabledDto;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;


@Component
public class TenantFacadeImpl implements TenantFacade {

  @Resource
  private TenantCmd tenantCmd;

  @Resource
  private TenantQuery tenantQuery;

  @Override
  public IdKey<Long, Object> add(TenantAddDto dto) {
    return tenantCmd.add(addDtoToDomain(dto), addDtoToTenantAudit(dto),
        UserAssembler.addDtoToDomain(UserAssembler.addTenantUserDto(dto),
            UserSource.BACKGROUND_SIGNUP),
        UserSource.BACKGROUND_SIGNUP);
  }

  @Override
  public IdKey<Long, Object> signupByMobile(TenantAddByMobileDto dto) {
    UserAddDto userAddDto = addTenantUserDto(dto);
    return tenantCmd.add(addDtoToDomain(), addDtoToTenantAudit(dto),
        UserAssembler.addDtoToDomain(userAddDto, UserSource.BACKGROUND_SIGNUP),
        UserSource.BACKGROUND_SIGNUP);
  }

  @Override
  public void update(TenantUpdateDto dto) {
    tenantCmd.update(updateDtoToDomain(dto),
        dto.hasModifyCert() ? TenantAuditAssembler.updateDtoToAudit(dto) : null,
        dto.hasModifyUser() ? UserAssembler.updateDtoToDomain(
            CoreUtils.copyPropertiesIgnoreNull(dto, new UserUpdateDto())) : null);
  }

  @Override
  public void replace(TenantReplaceDto dto) {
    tenantCmd.replace(replaceDtoToDomain(dto),
        dto.hasModifyCert() ? TenantAuditAssembler.replaceDtoToAudit(dto) : null,
        dto.hasModifyUser() ? UserAssembler.updateDtoToDomain(
            CoreUtils.copyPropertiesIgnoreNull(dto, new UserUpdateDto())) : null);
  }

  @Override
  public void enabled(EnabledOrDisabledDto dto) {
    tenantCmd.enabled(dto.getId(), dto.getEnabled());
  }

  @Override
  public void locked(TenantLockedDto dto) {
    tenantCmd.locked(dto.getId(), dto.getLocked(), dto.getLockStartDate(), dto.getLockEndDate());
  }

  @NameJoin
  @Override
  public TenantDetailVo detail(Long id) {
    return toDetailVo(tenantQuery.detail(id));
  }

  @NameJoin
  @Override
  public PageResult<TenantVo> list(TenantFindDto dto) {
    Page<Tenant> page = tenantQuery.list(getSpecification(dto), dto.tranPage(),
        dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, TenantAssembler::toVo);
  }

}
