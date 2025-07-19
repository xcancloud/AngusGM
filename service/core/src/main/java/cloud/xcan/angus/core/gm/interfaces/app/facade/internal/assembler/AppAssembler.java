package cloud.xcan.angus.core.gm.interfaces.app.facade.internal.assembler;


import static cloud.xcan.angus.spec.experimental.BizConstant.DEFAULT_SEQUENCE;
import static cloud.xcan.angus.spec.utils.ObjectUtils.integerSafe;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.gm.interfaces.api.facade.internal.assembler.ApiAssembler;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.AppAddDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.AppFindDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.AppReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.AppSiteInfoUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.AppUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.AppDetailVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.AppVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.dto.EnabledOrDisabledDto;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


public class AppAssembler {

  public static App addDtoToDomain(AppAddDto dto) {
    return new App()
        .setCode(dto.getCode())
        .setName(dto.getName())
        .setShowName(dto.getShowName())
        .setIcon(dto.getIcon())
        .setType(dto.getType())
        .setEditionType(dto.getEditionType())
        .setDescription(dto.getDescription())
        .setAuthCtrl(dto.getAuthCtrl())
        .setEnabled(true)
        .setUrl(dto.getUrl())
        .setSequence(integerSafe(dto.getSequence(), DEFAULT_SEQUENCE))
        .setOpenStage(dto.getOpenStage())
        .setClientId(dto.getClientId())
        .setVersion(dto.getVersion())
        .setApiIds(dto.getApiIds())
        .setTagIds(dto.getTagIds());
  }

  public static App updateDtoToDomain(AppUpdateDto dto) {
    return new App()
        .setId(dto.getId())
        .setCode(dto.getCode())
        .setName(dto.getName())
        .setShowName(dto.getShowName())
        .setIcon(dto.getIcon())
        .setType(dto.getType())
        .setEditionType(dto.getEditionType())
        .setDescription(dto.getDescription())
        .setAuthCtrl(dto.getAuthCtrl())
        .setUrl(dto.getUrl())
        .setSequence(dto.getSequence())
        .setOpenStage(dto.getOpenStage())
        .setClientId(dto.getClientId())
        .setVersion(dto.getVersion())
        .setApiIds(dto.getApiIds())
        .setTagIds(dto.getTagIds());
  }

  public static App replaceDtoToDomain(AppReplaceDto dto) {
    return new App()
        .setId(dto.getId())
        .setCode(dto.getCode())
        .setName(dto.getName())
        .setShowName(dto.getShowName())
        .setIcon(dto.getIcon())
        .setType(dto.getType())
        .setEditionType(dto.getEditionType())
        .setDescription(dto.getDescription())
        .setAuthCtrl(dto.getAuthCtrl())
        .setEnabled(true)
        .setUrl(dto.getUrl())
        .setSequence(integerSafe(dto.getSequence(), DEFAULT_SEQUENCE))
        .setOpenStage(dto.getOpenStage())
        .setClientId(dto.getClientId())
        .setVersion(dto.getVersion())
        .setApiIds(dto.getApiIds())
        .setTagIds(dto.getTagIds());
  }

  public static App siteUpdateDtoToDomain(AppSiteInfoUpdateDto dto) {
    return new App().setId(dto.getId())
        .setShowName(dto.getShowName())
        .setIcon(dto.getIcon())
        .setUrl(dto.getUrl());
  }

  public static App enabledDtoToDomain(EnabledOrDisabledDto dto) {
    return new App().setId(dto.getId()).setEnabled(dto.getEnabled());
  }

  public static AppVo toVo(App app) {
    return Objects.isNull(app) ? null : new AppVo().setId(app.getId())
        .setCode(app.getCode())
        .setName(app.getName())
        .setShowName(app.getShowName())
        .setIcon(app.getIcon())
        .setType(app.getType())
        .setEditionType(app.getEditionType())
        .setAuthCtrl(app.getAuthCtrl())
        .setEnabled(app.getEnabled())
        .setUrl(app.getUrl())
        .setSequence(app.getSequence())
        .setOpenStage(app.getOpenStage())
        .setCreatedBy(app.getCreatedBy())
        .setCreatedDate(app.getCreatedDate())
        .setVersion(app.getVersion());
  }

  public static AppDetailVo toDetailVo(App app) {
    if (Objects.isNull(app)) {
      return null;
    }
    AppDetailVo appDetailVo = new AppDetailVo()
        .setId(app.getId())
        .setCode(app.getCode())
        .setName(app.getName())
        .setShowName(app.getShowName())
        .setIcon(app.getIcon())
        .setType(app.getType())
        .setEditionType(app.getEditionType())
        .setDescription(app.getDescription())
        .setAuthCtrl(app.getAuthCtrl())
        .setEnabled(app.getEnabled())
        .setUrl(app.getUrl())
        .setSequence(app.getSequence())
        .setOpenStage(app.getOpenStage())
        .setClientId(app.getClientId())
        .setTenantId(app.getTenantId())
        .setCreatedBy(app.getCreatedBy())
        .setCreatedDate(app.getCreatedDate())
        .setVersion(app.getVersion());
    if (isNotEmpty(app.getTags())) {
      appDetailVo.setTags(app.getTags().stream().map(AppTagAssembler::toListVo)
          .collect(Collectors.toList()));
    }
    if (isNotEmpty(app.getApis())) {
      appDetailVo.setApis(app.getApis().stream().map(ApiAssembler::toInfo)
          .collect(Collectors.toList()));
    }
    return appDetailVo;
  }

  public static GenericSpecification<App> getSpecification(AppFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate")
        .matchSearchFields("name", "code", "showName", "description")
        .orderByFields("id", "sequence", "createdDate")
        .build();
    return new GenericSpecification<>(filters);
  }

}
