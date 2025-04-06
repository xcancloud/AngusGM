package cloud.xcan.angus.core.gm.interfaces.app.facade.internal.assembler;


import static cloud.xcan.angus.spec.principal.PrincipalContext.getExtension;
import static cloud.xcan.angus.spec.utils.ObjectUtils.objectToList;

import cloud.xcan.angus.api.commonlink.dept.Dept;
import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.api.commonlink.tenant.Tenant;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.gm.domain.policy.AuthPolicy;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.org.AuthAppOrgFindDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.org.AuthAppOrgSearchDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.org.AuthAppOrgUserSearchDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.org.UnAuthDeptFindDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.org.UnAuthFindDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.org.UnAuthGroupFindDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.org.UnAuthUserFindDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org.AppAuthDeptVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org.AppAuthGroupVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org.AppAuthPolicyVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org.AppAuthTenantVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org.AppAuthUserVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org.AppUnauthDeptVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org.AppUnauthGroupVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org.AppUnauthTenantVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org.AppUnauthUserVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org.OrgAppAuthVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.AuthPolicyOrgVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.spec.utils.ObjectUtils;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class AppOrgAuthAssembler {

  public static AppAuthTenantVo toAuthTenantVo(Tenant tenant) {
    return new AppAuthTenantVo().setId(tenant.getId())
        .setName(tenant.getName())
        .setNo(tenant.getNo())
        .setType(tenant.getType())
        .setSource(tenant.getSource())
        .setStatus(tenant.getStatus())
        .setRealNameStatus(tenant.getRealNameStatus())
        .setLocked(tenant.getLocked());
  }

  public static AppAuthUserVo toAuthUserVo(User user) {
    return new AppAuthUserVo().setId(user.getId())
        .setFullname(user.getFullname())
        .setAvatar(user.getAvatar())
        .setGlobalAuth((Boolean) getExtension("globalAuth"))
        .setPolicies(toAuthPolicyOrgVos(getExtension(String.valueOf(user.getId()))));
  }

  public static AppAuthDeptVo toAuthDeptVo(Dept dept) {
    return new AppAuthDeptVo().setId(dept.getId())
        .setCode(dept.getCode())
        .setName(dept.getName())
        .setLevel(dept.getLevel())
        .setPolicies(toAuthPolicyOrgVos(getExtension(String.valueOf(dept.getId()))));
  }

  public static AppAuthGroupVo toAuthGroupVo(Group group) {
    return new AppAuthGroupVo().setId(group.getId())
        .setCode(group.getCode())
        .setName(group.getName())
        .setSource(group.getSource())
        .setEnabled(group.getEnabled())
        .setPolicies(toAuthPolicyOrgVos(getExtension(String.valueOf(group.getId()))));
  }

  private static List<AuthPolicyOrgVo> toAuthPolicyOrgVos(Object extension) {
    if (Objects.isNull(extension)) {
      return null;
    }
    List<AuthPolicy> authPolicies = objectToList(extension, AuthPolicy.class);
    return ObjectUtils.isEmpty(authPolicies) ? null : authPolicies.stream()
        .map(x -> new AuthPolicyOrgVo().setId(x.getId())
            .setCode(x.getCode()).setName(x.getName())
            .setDescription(x.getDescription()).setAuthOrgs(x.getOrgPolicies())
        ).collect(Collectors.toList());
  }

  public static AppAuthPolicyVo toAuthPolicyVo(AuthPolicy policy) {
    return new AppAuthPolicyVo().setId(policy.getId())
        .setName(policy.getName())
        .setCode(policy.getCode())
        .setType(policy.getType())
        .setDefault0(policy.getDefault0())
        .setGrantStage(policy.getGrantStage())
        .setEnabled(policy.getEnabled());
  }

  public static OrgAppAuthVo toOrgAppAuthVo(App app) {
    return new OrgAppAuthVo().setId(app.getId())
        .setCode(app.getCode())
        .setName(app.getName())
        .setEditionType(app.getEditionType())
        .setShowName(app.getShowName())
        .setIcon(app.getIcon())
        .setAuthCtrl(app.getAuthCtrl())
        .setEnabled(app.getEnabled())
        .setUrl(app.getUrl())
        .setType(app.getType())
        .setSequence(app.getSequence())
        .setVersion(app.getVersion())
        .setOpenStage(app.getOpenStage())
        .setPolicies(toAuthPolicyOrgVos(getExtension(String.valueOf(app.getId()))))
        .setTags(AppFuncAssembler.toTagVos(app.getTags()));
  }

  public static AppUnauthTenantVo toUnAuthTenantVo(Tenant tenant) {
    return new AppUnauthTenantVo().setId(tenant.getId())
        .setName(tenant.getName())
        .setNo(tenant.getNo())
        .setType(tenant.getType())
        .setSource(tenant.getSource())
        .setStatus(tenant.getStatus())
        .setRealNameStatus(tenant.getRealNameStatus())
        .setLocked(tenant.getLocked());
  }

  public static AppUnauthUserVo toUnAuthUserVo(User user) {
    return new AppUnauthUserVo().setId(user.getId())
        .setFullname(user.getFullname())
        .setAvatar(user.getAvatar());
  }

  public static AppUnauthDeptVo toUnAuthDeptVo(Dept dept) {
    return new AppUnauthDeptVo().setId(dept.getId())
        .setCode(dept.getCode())
        .setName(dept.getName())
        .setLevel(dept.getLevel());
  }

  public static AppUnauthGroupVo toUnAuthGroupVo(Group group) {
    return new AppUnauthGroupVo().setId(group.getId())
        .setCode(group.getCode())
        .setName(group.getName())
        .setSource(group.getSource())
        .setEnabled(group.getEnabled());
  }

  public static GenericSpecification<Tenant> getAuthTenantSpecification(AuthAppOrgFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .orderByFields("id")
        .build();
    return new GenericSpecification<>(filters);
  }

  public static GenericSpecification<User> getAuthUserSpecification(AuthAppOrgUserSearchDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .inAndNotFields("id")
        .matchSearchFields("fullname")
        .orderByFields("id")
        .build();
    return new GenericSpecification<>(filters);
  }

  public static GenericSpecification<Dept> getAuthDeptSpecification(AuthAppOrgSearchDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .inAndNotFields("id")
        .matchSearchFields("name")
        .orderByFields("id")
        .build();
    return new GenericSpecification<>(filters);
  }

  public static GenericSpecification<Group> getAuthGroupSpecification(AuthAppOrgSearchDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .inAndNotFields("id")
        .matchSearchFields("name")
        .orderByFields("id")
        .build();
    return new GenericSpecification<>(filters);
  }

  public static GenericSpecification<AuthPolicy> getAuthPolicySpecification(
      AuthAppOrgSearchDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .matchSearchFields("name")
        .orderByFields("id")
        .build();
    return new GenericSpecification<>(filters);
  }

  public static GenericSpecification<Tenant> getUnAuthTenantSpecification(UnAuthFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .matchSearchFields("name")
        .build();
    return new GenericSpecification<>(filters);
  }

  public static GenericSpecification<User> getUnAuthUserSpecification(UnAuthUserFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .matchSearchFields("fullname")
        .build();
    return new GenericSpecification<>(filters);
  }

  public static GenericSpecification<Dept> getUnAuthDeptSpecification(UnAuthDeptFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .matchSearchFields("name")
        .build();
    return new GenericSpecification<>(filters);
  }

  public static GenericSpecification<Group> getUnAuthGroupSpecification(UnAuthGroupFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .matchSearchFields("name")
        .build();
    return new GenericSpecification<>(filters);
  }

  public static GenericSpecification<AuthPolicy> getUnAuthPolicySpecification(UnAuthFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .matchSearchFields("name")
        .build();
    return new GenericSpecification<>(filters);
  }

  public static GenericSpecification<App> getUnAuthAppSpecification(UnAuthFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .matchSearchFields("name")
        .build();
    return new GenericSpecification<>(filters);
  }

}
