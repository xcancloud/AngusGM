package cloud.xcan.angus.core.gm.interfaces.app.facade.internal.assembler;


import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isDoorApi;

import cloud.xcan.angus.api.commonlink.app.open.AppOpen;
import cloud.xcan.angus.api.gm.app.dto.AppOpenDto;
import cloud.xcan.angus.api.gm.app.dto.AppOpenRenewDto;
import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.open.AppOpenFindDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.open.AppOpenVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.spec.principal.Principal;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class AppOpenAssembler {

  public static AppOpen openDtoToDomain(AppOpenDto dto) {
    Principal principal = PrincipalContext.get();
    if (isDoorApi(principal)) {
      principal.setUserId(dto.getUserId())
          .setTenantId(dto.getTenantId())
          .setOptTenantId(dto.getTenantId());
    }
    return new AppOpen()/*.setAppId(dto.getAppId())*/
        .setAppCode(dto.getAppCode())
        .setEditionType(dto.getEditionType())
        .setVersion(dto.getVersion())
        .setTenantId(getOptTenantId())
        .setUserId(principal.getUserId())
        .setOpenDate(dto.getOpenDate())
        .setExpirationDate(dto.getExpirationDate());
  }

  public static AppOpen renewDtoToDomain(AppOpenRenewDto dto) {
    Principal principal = PrincipalContext.get();
    if (isDoorApi(principal)) {
      principal.setTenantId(dto.getTenantId())
          .setOptTenantId(dto.getTenantId());
    }
    return new AppOpen()/*.setAppId(dto.getAppId())*/
        .setAppCode(dto.getAppCode())
        .setEditionType(dto.getEditionType())
        .setVersion(dto.getVersion())
        .setTenantId(principal.getOptTenantId())
        .setUserId(principal.getUserId())
        .setOpenDate(dto.getOpenDate())
        .setExpirationDate(dto.getExpirationDate());
  }

  public static AppOpenVo toVo(AppOpen appOpen) {
    AppOpenVo openVo = new AppOpenVo();
    openVo.setId(appOpen.getId())
        .setUserId(appOpen.getUserId())
        .setTenantId(appOpen.getTenantId())
        .setExpirationDeleted(appOpen.getExpirationDeleted())
        .setExpirationDate(appOpen.getExpirationDate())
        .setOpClientOpen(appOpen.getOpClientOpen())
        .setOpenDate(appOpen.getOpenDate())
        .setCreatedDate(appOpen.getCreatedDate());
    Object apps = PrincipalContext.getExtension("apps");
    if (Objects.isNull(apps)) {
      return openVo;
    }
    App app = ((List<App>) apps).stream().filter(x -> x.getId().equals(appOpen.getAppId()))
        .findFirst().orElse(null);
    return Objects.isNull(app) ? openVo : openVo.setAppId(appOpen.getAppId())
        .setAppCode(app.getCode()).setAppName(app.getName()).setAppShowName(app.getShowName())
        .setAppIcon(app.getIcon()).setAppType(app.getType()).setEditionType(app.getEditionType())
        .setAppUrl(app.getUrl()).setAppSequence(app.getSequence())
        .setAppVersion(app.getVersion())
        .setAppTags(AppFuncAssembler.toTagVos(app.getTags()));
  }

  public static GenericSpecification<AppOpen> getSpecification(AppOpenFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .orderByFields("id", "createdDate", "openDate", "expirationDate")
        .build();
    return new GenericSpecification<>(filters);
  }

}
