package cloud.xcan.angus.core.gm.interfaces.policy.facade.internal.assembler;

import static cloud.xcan.angus.core.gm.application.cmd.policy.impl.AuthPolicyCmdImpl.genPolicyCode;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isTenantClient;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;
import static cloud.xcan.angus.spec.utils.ObjectUtils.stringSafe;

import cloud.xcan.angus.api.commonlink.policy.PolicyGrantStage;
import cloud.xcan.angus.api.commonlink.policy.PolicyType;
import cloud.xcan.angus.core.gm.domain.policy.AuthPolicy;
import cloud.xcan.angus.core.gm.domain.policy.func.AuthPolicyFunc;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.AuthPolicyAddDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.AuthPolicyAssociatedFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.AuthPolicyFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.AuthPolicyReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.AuthPolicyUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.UnAuthPolicyAssociatedFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.UnAuthPolicyFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.AuthPolicyAssociatedVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.AuthPolicyDetailVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.AuthPolicyVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.PolicyUnauthVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.dto.EnabledOrDisabledDto;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


public class AuthPolicyAssembler {

  public static AuthPolicy addDtoToDomain(AuthPolicyAddDto dto) {
    AuthPolicy policy = new AuthPolicy()
        .setName(dto.getName())
        .setAppId(dto.getAppId())
        .setDescription(dto.getDescription())
        .setEnabled(true)
        .setCode(stringSafe(dto.getCode(), genPolicyCode()))
        .setDefault0(dto.getDefault0());
    if (isNotEmpty(dto.getFuncIds())) {
      policy.setPolicyFunc(
          dto.getFuncIds().stream().map(
                  funcId -> new AuthPolicyFunc().setAppId(dto.getAppId()).setFuncId(funcId))
              .collect(Collectors.toList()));
    }
    if (isTenantClient()) {
      policy.setGrantStage(PolicyGrantStage.MANUAL)
          .setType(PolicyType.USER_DEFINED);
    } else {
      policy.setGrantStage(dto.getType().equals(PolicyType.USER_DEFINED)
              ? PolicyGrantStage.MANUAL : PolicyGrantStage.SIGNUP_SUCCESS)
          .setType(dto.getType());
    }
    return policy;
  }

  public static AuthPolicy updateDtoToDomain(AuthPolicyUpdateDto dto) {
    AuthPolicy policy = new AuthPolicy().setId(dto.getId())
        .setName(dto.getName()).setDescription(dto.getDescription())
        .setDefault0(dto.getDefault0());
    if (isNotEmpty(dto.getFuncIds())) {
      policy.setPolicyFunc(dto.getFuncIds().stream().map(
          funcId -> new AuthPolicyFunc().setFuncId(funcId)).collect(Collectors.toList()));
    }
    return policy;
  }

  public static AuthPolicy replaceDtoToDomain(AuthPolicyReplaceDto dto) {
    AuthPolicy policy = new AuthPolicy()
        .setId(dto.getId())
        .setName(dto.getName())
        .setAppId(dto.getAppId())
        .setDescription(dto.getDescription())
        // Insert
        .setEnabled(Objects.isNull(dto.getId()) ? true : null)
        .setCode(stringSafe(dto.getCode(), genPolicyCode()))
        .setDefault0(nullSafe(dto.getDefault0(), dto.isDefaultType()));
    if (isNotEmpty(dto.getFuncIds())) {
      policy.setPolicyFunc(
          dto.getFuncIds().stream().map(
                  funcId -> new AuthPolicyFunc().setAppId(dto.getAppId()).setFuncId(funcId))
              .collect(Collectors.toList()));
    }
    if (isNull(dto.getId())) {
      if (isTenantClient()) {
        policy.setGrantStage(PolicyGrantStage.MANUAL)
            .setType(PolicyType.USER_DEFINED);
      } else {
        policy.setGrantStage(dto.getType().equals(PolicyType.USER_DEFINED)
                ? PolicyGrantStage.MANUAL : PolicyGrantStage.SIGNUP_SUCCESS)
            .setType(dto.getType());
      }
    }
    return policy;
  }

  public static AuthPolicy enabledDtoToDomain(EnabledOrDisabledDto dto) {
    return new AuthPolicy().setId(dto.getId())
        .setEnabled(dto.getEnabled());
  }

  public static AuthPolicyDetailVo toDetailVo(AuthPolicy policy) {
    return new AuthPolicyDetailVo().setId(policy.getId())
        .setName(policy.getName())
        .setCode(policy.getCode())
        .setType(policy.getType())
        .setDefault0(policy.getDefault0())
        .setGrantStage(policy.getGrantStage())
        .setDescription(policy.getDescription())
        .setClientId(policy.getClientId())
        .setAppId(policy.getAppId())
        .setEnabled(policy.getEnabled())
        .setTenantId(policy.getTenantId())
        .setCreatedBy(policy.getCreatedBy())
        .setCreatedDate(policy.getCreatedDate())
        .setLastModifiedBy(policy.getLastModifiedBy())
        .setLastModifiedDate(policy.getLastModifiedDate());
  }

  public static AuthPolicyVo toAuthPolicyVo(AuthPolicy policy) {
    return new AuthPolicyVo().setId(policy.getId())
        .setName(policy.getName())
        .setCode(policy.getCode())
        .setType(policy.getType())
        .setDescription(policy.getDescription())
        .setDefault0(policy.getDefault0())
        .setGrantStage(policy.getGrantStage())
        .setAppId(policy.getAppId())
        .setAppName(policy.getAppName())
        .setAppVersion(policy.getAppVersion())
        .setAppEditionType(policy.getAppEditionType())
        .setEnabled(policy.getEnabled())
        .setAuthBy(policy.getAuthBy())
        .setAuthDate(policy.getAuthDate())
        .setTenantId(policy.getTenantId())
        .setCreatedBy(policy.getCreatedBy())
        .setCreatedDate(policy.getCreatedDate());
  }

  public static PolicyUnauthVo toUnAuthPolicyVo(AuthPolicy policy) {
    return new PolicyUnauthVo().setId(policy.getId())
        .setName(policy.getName())
        .setCode(policy.getCode())
        .setType(policy.getType())
        .setDescription(policy.getDescription())
        .setDefault0(policy.getDefault0())
        .setGrantStage(policy.getGrantStage())
        .setAppId(policy.getAppId())
        .setEnabled(policy.getEnabled())
        .setTenantId(policy.getTenantId())
        .setCreatedBy(policy.getCreatedBy())
        .setCreatedDate(policy.getCreatedDate());
  }

  public static AuthPolicyAssociatedVo toPolicyAssociatedListVo(AuthPolicy policy) {
    return new AuthPolicyAssociatedVo().setId(policy.getId())
        .setName(policy.getName())
        .setCode(policy.getCode())
        .setType(policy.getType())
        .setDescription(policy.getDescription())
        .setDefault0(policy.getDefault0())
        .setCurrentDefault(policy.getCurrentDefault0())
        .setOpenAuth(policy.getOpenAuth())
        .setGrantScope(policy.getGrantScope())
        .setGrantStage(policy.getGrantStage())
        .setAppId(policy.getAppId())
        .setEnabled(policy.getEnabled())
        .setAuthBy(policy.getAuthBy())
        .setAuthDate(policy.getAuthDate())
        .setTenantId(policy.getTenantId())
        .setCreatedBy(policy.getCreatedBy())
        .setCreatedDate(policy.getCreatedDate())
        .setOrgId(policy.getOrgId())
        .setOrgType(policy.getOrgType())
        .setOrgName(policy.getOrgName());
  }

  public static GenericSpecification<AuthPolicy> getSpecification(AuthPolicyFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .matchSearchFields("code", "name", "description")
        .orderByFields("id", "createdDate")
        .build();
    return new GenericSpecification<>(filters);
  }

  public static GenericSpecification<AuthPolicy> getSpecification(AuthPolicyAssociatedFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .matchSearchFields("code", "name", "description")
        .orderByFields("id", "createdDate")
        .build();
    return new GenericSpecification<>(filters);
  }

  public static GenericSpecification<AuthPolicy> getSpecification(
      UnAuthPolicyAssociatedFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .matchSearchFields("code", "name", "description")
        .orderByFields("id", "createdDate")
        .build();
    return new GenericSpecification<>(filters);
  }

  public static GenericSpecification<AuthPolicy> getSpecification(UnAuthPolicyFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .matchSearchFields("code", "name", "description")
        .orderByFields("id", "createdDate")
        .build();
    return new GenericSpecification<>(filters);
  }
}
