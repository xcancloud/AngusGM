package cloud.xcan.angus.core.gm.interfaces.app.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.app.facade.internal.assembler.AppOrgAuthAssembler.getAuthDeptSpecification;
import static cloud.xcan.angus.core.gm.interfaces.app.facade.internal.assembler.AppOrgAuthAssembler.getAuthGroupSpecification;
import static cloud.xcan.angus.core.gm.interfaces.app.facade.internal.assembler.AppOrgAuthAssembler.getAuthPolicySpecification;
import static cloud.xcan.angus.core.gm.interfaces.app.facade.internal.assembler.AppOrgAuthAssembler.getAuthTenantSpecification;
import static cloud.xcan.angus.core.gm.interfaces.app.facade.internal.assembler.AppOrgAuthAssembler.getAuthUserSpecification;
import static cloud.xcan.angus.core.gm.interfaces.app.facade.internal.assembler.AppOrgAuthAssembler.getUnAuthDeptSpecification;
import static cloud.xcan.angus.core.gm.interfaces.app.facade.internal.assembler.AppOrgAuthAssembler.getUnAuthGroupSpecification;
import static cloud.xcan.angus.core.gm.interfaces.app.facade.internal.assembler.AppOrgAuthAssembler.getUnAuthPolicySpecification;
import static cloud.xcan.angus.core.gm.interfaces.app.facade.internal.assembler.AppOrgAuthAssembler.getUnAuthTenantSpecification;
import static cloud.xcan.angus.core.gm.interfaces.app.facade.internal.assembler.AppOrgAuthAssembler.getUnAuthUserSpecification;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.api.commonlink.AuthOrgType;
import cloud.xcan.angus.api.commonlink.dept.Dept;
import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.api.commonlink.tenant.Tenant;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.gm.app.vo.OrgAppAuthVo;
import cloud.xcan.angus.core.biz.MessageJoin;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.app.AppOrgAuthCmd;
import cloud.xcan.angus.core.gm.application.query.app.AppOrgAuthQuery;
import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.gm.domain.policy.AuthPolicy;
import cloud.xcan.angus.core.gm.interfaces.app.facade.AppOrgAuthFacade;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.org.AuthAppOrgFindDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.org.AuthAppOrgSearchDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.org.AuthAppOrgUserSearchDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.org.AuthOrgPolicyAuthDeleteDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.org.AuthOrgPolicyAuthDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.org.UnAuthDeptFindDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.org.UnAuthFindDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.org.UnAuthGroupFindDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.org.UnAuthUserFindDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.internal.assembler.AppOrgAuthAssembler;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org.AppAuthDeptVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org.AppAuthGroupVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org.AppAuthPolicyVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org.AppAuthTenantVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org.AppAuthUserVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org.AppUnauthDeptVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org.AppUnauthGroupVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org.AppUnauthTenantVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org.AppUnauthUserVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class AppOrgAuthFacadeImpl implements AppOrgAuthFacade {

  @Resource
  private AppOrgAuthQuery appOrgAuthQuery;

  @Resource
  private AppOrgAuthCmd appOrgAuthCmd;

  @Override
  public PageResult<AppAuthTenantVo> appAuthTenant(Long appId, AuthAppOrgFindDto dto) {
    dto.setOrderBy("tenant_id");
    Page<Tenant> page = appOrgAuthQuery.appAuthTenant(appId,
        getAuthTenantSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, AppOrgAuthAssembler::toAuthTenantVo);
  }

  @Override
  public PageResult<AppAuthUserVo> appAuthUser(Long appId, AuthAppOrgUserSearchDto dto) {
    Page<User> page = appOrgAuthQuery.appAuthUser(appId,
        getAuthUserSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, AppOrgAuthAssembler::toAuthUserVo);
  }

  @Override
  public PageResult<AppAuthDeptVo> appAuthDept(Long appId, AuthAppOrgSearchDto dto) {
    Page<Dept> page = appOrgAuthQuery.appAuthDept(appId,
        getAuthDeptSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, AppOrgAuthAssembler::toAuthDeptVo);
  }

  @Override
  public PageResult<AppAuthGroupVo> appAuthGroup(Long appId, AuthAppOrgSearchDto dto) {
    Page<Group> page = appOrgAuthQuery.appAuthGroup(appId,
        getAuthGroupSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, AppOrgAuthAssembler::toAuthGroupVo);
  }

  @Override
  public List<AppAuthPolicyVo> appAuthGlobal(Long appId) {
    List<AuthPolicy> authorities = appOrgAuthQuery.appAuthGlobal(appId);
    return isEmpty(authorities) ? null : authorities.stream()
        .map(AppOrgAuthAssembler::toAuthPolicyVo).collect(Collectors.toList());
  }

  @Override
  public PageResult<AppAuthPolicyVo> appAuthPolicy(Long appId, AuthOrgType orgType, Long orgId,
      AuthAppOrgSearchDto dto) {
    Page<AuthPolicy> page = appOrgAuthQuery.orgAuthPolicy(appId, orgType, orgId,
        getAuthPolicySpecification(dto), dto.tranPage());
    return buildVoPageResult(page, AppOrgAuthAssembler::toAuthPolicyVo);
  }

  @Override
  public Boolean appAuthOrgCheck(Long appId, AuthOrgType orgType, Long orgId) {
    return appOrgAuthQuery.appAuthOrgCheck(appId, orgType, orgId);
  }

  @MessageJoin
  @Override
  public List<OrgAppAuthVo> orgAuthApp(AuthOrgType orgType, Long orgId, Boolean joinPolicy) {
    List<App> authApps = appOrgAuthQuery.orgAuthApp(orgType, orgId, nullSafe(joinPolicy, false));
    return authApps.stream().map(AppOrgAuthAssembler::toOrgAppAuthVo).collect(Collectors.toList());
  }

  @Override
  public Boolean orgAuthAppCheck(AuthOrgType orgType, Long orgId, Long appId) {
    return appOrgAuthQuery.orgAuthAppCheck(orgType, orgId, appId);
  }

  @NameJoin
  @Override
  public PageResult<AppUnauthTenantVo> appUnauthTenant(Long appId, UnAuthFindDto dto) {
    Page<Tenant> page = appOrgAuthQuery.appUnauthTenant(appId,
        getUnAuthTenantSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, AppOrgAuthAssembler::toUnAuthTenantVo);
  }

  @Override
  public PageResult<AppUnauthUserVo> appUnauthUser(Long appId, UnAuthUserFindDto dto) {
    Page<User> page = appOrgAuthQuery.appUnauthUser(appId,
        getUnAuthUserSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, AppOrgAuthAssembler::toUnAuthUserVo);
  }

  @Override
  public PageResult<AppUnauthDeptVo> appUnauthDept(Long appId, UnAuthDeptFindDto dto) {
    Page<Dept> page = appOrgAuthQuery.appUnauthDept(appId,
        getUnAuthDeptSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, AppOrgAuthAssembler::toUnAuthDeptVo);
  }

  @Override
  public PageResult<AppUnauthGroupVo> appUnauthGroup(Long appId, UnAuthGroupFindDto dto) {
    Page<Group> page = appOrgAuthQuery.appUnauthGroup(appId,
        getUnAuthGroupSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, AppOrgAuthAssembler::toUnAuthGroupVo);
  }

  @Override
  public PageResult<AppAuthPolicyVo> appUnauthPolicy(Long appId, AuthOrgType orgType,
      Long orgId, UnAuthFindDto dto) {
    Page<AuthPolicy> page = appOrgAuthQuery.orgUnauthPolicy(appId, orgType, orgId,
        getUnAuthPolicySpecification(dto), dto.tranPage());
    return buildVoPageResult(page, AppOrgAuthAssembler::toAuthPolicyVo);
  }

  @Override
  public List<IdKey<Long, Object>> authUserPolicy(Long appId, AuthOrgPolicyAuthDto dto) {
    return appOrgAuthCmd.authUserPolicy(appId, dto.getOrgIds(), dto.getPolicyIds());
  }

  @Override
  public List<IdKey<Long, Object>> authDeptPolicy(Long appId, AuthOrgPolicyAuthDto dto) {
    return appOrgAuthCmd.authDeptPolicy(appId, dto.getOrgIds(), dto.getPolicyIds());
  }

  @Override
  public List<IdKey<Long, Object>> authGroupPolicy(Long appId, AuthOrgPolicyAuthDto dto) {
    return appOrgAuthCmd.authGroupPolicy(appId, dto.getOrgIds(), dto.getPolicyIds());
  }

  @Override
  public void authUserDelete(Long appId, AuthOrgPolicyAuthDeleteDto dto) {
    appOrgAuthCmd.authUserPolicyDelete(appId, dto.getOrgIds(), dto.getPolicyIds());
  }

  @Override
  public void authDeptDelete(Long appId, AuthOrgPolicyAuthDeleteDto dto) {
    appOrgAuthCmd.authDeptPolicyDelete(appId, dto.getOrgIds(), dto.getPolicyIds());
  }

  @Override
  public void authGroupDelete(Long appId, AuthOrgPolicyAuthDeleteDto dto) {
    appOrgAuthCmd.authGroupPolicyDelete(appId, dto.getOrgIds(), dto.getPolicyIds());
  }
}
