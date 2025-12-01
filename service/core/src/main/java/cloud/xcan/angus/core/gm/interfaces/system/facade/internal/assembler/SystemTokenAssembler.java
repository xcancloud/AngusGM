package cloud.xcan.angus.core.gm.interfaces.system.facade.internal.assembler;

import static cloud.xcan.angus.api.commonlink.AuthConstant.DEFAULT_TOKEN_EXPIRE_SECOND;

import cloud.xcan.angus.api.enums.ResourceAclType;
import cloud.xcan.angus.api.enums.ResourceAuthType;
import cloud.xcan.angus.core.gm.domain.system.SystemToken;
import cloud.xcan.angus.core.gm.domain.system.resource.SystemTokenResource;
import cloud.xcan.angus.core.gm.interfaces.system.facade.dto.AuthorizedResourceDto;
import cloud.xcan.angus.core.gm.interfaces.system.facade.dto.SystemTokenAddDto;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.AuthorizedResourceApiVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.AuthorizedResourceVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.AuthorizedServiceResourceVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.SystemTokenDetailVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.SystemTokenInfoVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.SystemTokenValueVo;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


public class SystemTokenAssembler {

  public static SystemToken addDtoToDomain(SystemTokenAddDto dto) {
    return new SystemToken().setName(dto.getName())
        .setExpiredDate(safeExpiredDate(dto.getExpiredDate()))
        .setAuthType(dto.getAuthType());
  }

  public static List<SystemTokenResource> addDtoToResourceDomain(SystemTokenAddDto dto) {
    List<SystemTokenResource> resources = new ArrayList<>();
    for (AuthorizedResourceDto resource : dto.getResources()) {
      for (Long resourceId : resource.getResourceIds()) {
        resources.add(new SystemTokenResource()
            .setResource(resource.getResource())
            .setResourceId(resourceId)
            .setAuthorities(ResourceAuthType.API.equals(dto.getAuthType())
                ? null : resource.getResourceAcls().get(resourceId).stream()
                .map(ResourceAclType::getValue).collect(Collectors.toList()))
        );
      }
    }
    return resources;
  }

  public static LocalDateTime safeExpiredDate(LocalDateTime expiredDate) {
    return Objects.isNull(expiredDate) ? LocalDateTime.now()
        .plusSeconds(DEFAULT_TOKEN_EXPIRE_SECOND) : expiredDate;
  }

  public static SystemTokenValueVo toTokenValueVo(SystemToken systemToken) {
    return new SystemTokenValueVo().setId(systemToken.getId())
        .setValue(systemToken.getDecryptedValue())
        .setExpiredDate(systemToken.getExpiredDate());
  }

  public static SystemTokenInfoVo toTokenInfoVo(SystemToken systemToken) {
    return new SystemTokenInfoVo().setId(systemToken.getId())
        .setName(systemToken.getName())
        .setAuthType(systemToken.getAuthType())
        .setExpiredDate(systemToken.getExpiredDate())
        .setCreatedBy(systemToken.getCreatedBy())
        .setCreatedDate(systemToken.getCreatedDate());
  }

  public static SystemTokenDetailVo toSysTokenGrantVo(SystemToken systemToken) {
    return new SystemTokenDetailVo().setId(systemToken.getId())
        .setName(systemToken.getName())
        .setAuthType(systemToken.getAuthType())
        .setExpiredDate(systemToken.getExpiredDate())
        .setCreatedBy(systemToken.getCreatedBy())
        .setCreatedDate(systemToken.getCreatedDate())
        .setResources(toServiceResourceVos(systemToken));
  }

  public static List<AuthorizedServiceResourceVo> toServiceResourceVos(SystemToken systemToken) {
    Map<String, List<SystemTokenResource>> servicesMap = systemToken.getResources()
        .stream().collect(Collectors.groupingBy(SystemTokenResource::getServiceCode));
    List<AuthorizedServiceResourceVo> serviceResourceVos = new ArrayList<>();
    for (String serviceCode : servicesMap.keySet()) {
      serviceResourceVos.add(new AuthorizedServiceResourceVo()
          .setServiceCode(serviceCode)
          .setServiceName(servicesMap.get(serviceCode).get(0).getServiceName())
          .setResources(toResourceVos(systemToken, servicesMap.get(serviceCode))));
    }
    return serviceResourceVos;
  }

  public static List<AuthorizedResourceVo> toResourceVos(SystemToken systemToken,
      List<SystemTokenResource> resources) {
    List<AuthorizedResourceVo> resourceVos = new ArrayList<>();
    Map<String, List<SystemTokenResource>> resourceMap = resources.stream()
        .collect(Collectors.groupingBy(SystemTokenResource::getResource));
    for (String resource : resourceMap.keySet()) {
      if (systemToken.isApiAuth()) {
        resourceVos.add(new AuthorizedResourceVo()
            .setResource(resource)
            .setResources(resources.stream().map(
                    x -> new AuthorizedResourceApiVo()
                        .setId(x.getResourceId())
                        .setCode(x.getApi().getOperationId())
                        .setName(x.getApi().getName())
                        .setDescription(x.getApi().getDescription()))
                .collect(Collectors.toList()))
            .setAcls(null));
      } else {
        Map<Long, List<ResourceAclType>> acls = new java.util.HashMap<>();
        for (SystemTokenResource res : resourceMap.get(resource)) {
          acls.put(res.getResourceId(), res.getAuthorities().stream()
              .map(ResourceAclType::valueOf).collect(Collectors.toList()));
        }
        resourceVos.add(new AuthorizedResourceVo()
            .setResource(resource)
            .setAcls(acls));
      }
    }
    return resourceVos;
  }

}
