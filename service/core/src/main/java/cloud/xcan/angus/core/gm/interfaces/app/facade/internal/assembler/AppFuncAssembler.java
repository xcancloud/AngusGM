package cloud.xcan.angus.core.gm.interfaces.app.facade.internal.assembler;


import static cloud.xcan.angus.spec.experimental.BizConstant.DEFAULT_SEQUENCE;
import static cloud.xcan.angus.spec.utils.ObjectUtils.integerSafe;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.pidSafe;
import static cloud.xcan.angus.spec.utils.ObjectUtils.stringSafe;

import cloud.xcan.angus.api.commonlink.api.Api;
import cloud.xcan.angus.api.commonlink.app.func.AppFunc;
import cloud.xcan.angus.api.commonlink.app.tag.WebTag;
import cloud.xcan.angus.core.biz.ProtocolAssert;
import cloud.xcan.angus.core.gm.application.converter.WebTagConverter;
import cloud.xcan.angus.core.gm.interfaces.api.facade.internal.assembler.ApiAssembler;
import cloud.xcan.angus.api.gm.app.vo.ApiInfoVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.func.AppFuncAddDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.func.AppFuncFindDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.func.AppFuncReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.func.AppFuncUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.func.AppFuncDetailVo;
import cloud.xcan.angus.api.gm.app.vo.AppFuncTreeVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.func.AppFuncVo;
import cloud.xcan.angus.api.gm.app.vo.AppTagInfoVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.dto.EnabledOrDisabledDto;
import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.spec.utils.TreeUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


public class AppFuncAssembler {

  public static AppFunc addDtoToDomain(Long appId, AppFuncAddDto dto) {
    AppFunc appFunc = new AppFunc()
        .setAppId(appId)
        .setCode(dto.getCode())
        .setName(dto.getName())
        .setShowName(dto.getShowName())
        .setPid(pidSafe(dto.getPid()))
        .setIcon(dto.getIcon())
        .setType(dto.getType())
        .setAuthCtrl(dto.getAuthCtrl())
        .setDescription(dto.getDescription())
        .setEnabled(true)
        .setUrl(stringSafe(dto.getUrl()))
        //.setClientId(dto.getClientId()) // Consistent with application
        .setSequence(integerSafe(dto.getSequence(), DEFAULT_SEQUENCE))
        .setApiIds(dto.getApiIds())
        .setTagIds(dto.getTagIds());
    if (isNotEmpty(dto.getTagIds())) {
      appFunc.setTagTargets(WebTagConverter.assembleAppOrFuncTags(null, null, dto.getTagIds()));
    }
    return appFunc;
  }

  public static AppFunc updateDtoToDomain(Long appId, AppFuncUpdateDto dto) {
    AppFunc appFunc = new AppFunc().setId(dto.getId())
        .setAppId(appId)
        .setCode(dto.getCode())
        .setName(dto.getName())
        .setShowName(dto.getShowName())
        .setPid(dto.getPid())
        .setIcon(dto.getIcon())
        .setAuthCtrl(dto.getAuthCtrl())
        .setDescription(dto.getDescription())
        .setEnabled(true)
        .setUrl(dto.getUrl())
        //.setClientId(dto.getClientId()) // Consistent with application
        .setSequence(integerSafe(dto.getSequence(), DEFAULT_SEQUENCE))
        .setApiIds(dto.getApiIds())
        .setTagIds(dto.getTagIds());
    if (isNotEmpty(dto.getTagIds())) {
      appFunc.setTagTargets(WebTagConverter.assembleAppOrFuncTags(null, null, dto.getTagIds()));
    }
    return appFunc;
  }

  public static AppFunc replaceDtoToDomain(Long appId, AppFuncReplaceDto dto) {
    AppFunc appFunc = new AppFunc().setId(dto.getId())
        .setAppId(appId)
        .setCode(dto.getCode())
        .setName(dto.getName())
        .setShowName(dto.getShowName())
        .setPid(pidSafe(dto.getPid()))
        .setIcon(dto.getIcon())
        .setType(dto.getType())
        .setAuthCtrl(dto.getAuthCtrl())
        .setDescription(dto.getDescription())
        .setUrl(stringSafe(dto.getUrl()))
        //.setClientId(dto.getClientId())
        .setSequence(integerSafe(dto.getSequence(), DEFAULT_SEQUENCE))
        .setApiIds(dto.getApiIds())
        .setEnabled(true);
    if (isNotEmpty(dto.getTagIds())) {
      ProtocolAssert.assertTrue(Objects.isNull(dto.getId())
          || Objects.nonNull(dto.getType()), "type is required");
      appFunc.setTagTargets(WebTagConverter.assembleAppOrFuncTags(null,
          Objects.nonNull(dto.getId()) ? dto.getType().toTagTargetType() : null, dto.getTagIds()));
    }
    return appFunc;
  }

  public static AppFunc enabledDtoToDomain(EnabledOrDisabledDto dto) {
    return new AppFunc().setId(dto.getId()).setEnabled(dto.getEnabled());
  }

  public static AppFuncDetailVo toAppFuncDetailVo(AppFunc appFunc) {
    return new AppFuncDetailVo().setId(appFunc.getId())
        .setCode(appFunc.getCode())
        .setName(appFunc.getName())
        .setShowName(appFunc.getShowName())
        .setPid(appFunc.getPid())
        .setIcon(appFunc.getIcon())
        .setType(appFunc.getType())
        .setDescription(appFunc.getDescription())
        .setAuthCtrl(appFunc.getAuthCtrl())
        .setEnabled(appFunc.getEnabled())
        .setUrl(appFunc.getUrl())
        .setAppId(appFunc.getAppId())
        .setSequence(appFunc.getSequence())
        .setTags(toTagVos(appFunc.getTags()))
        .setApis(toApiVos(appFunc.getApis()))
        .setClientId(appFunc.getClientId())
        .setCreatedBy(appFunc.getCreatedBy())
        .setCreatedDate(appFunc.getCreatedDate())
        .setTenantId(appFunc.getTenantId());
  }

  public static AppFuncVo toAppFuncVo(AppFunc appFunc) {
    return Objects.isNull(appFunc) ? null : new AppFuncVo().setId(appFunc.getId())
        .setCode(appFunc.getCode())
        .setName(appFunc.getName())
        .setShowName(appFunc.getShowName())
        .setPid(appFunc.getPid())
        .setIcon(appFunc.getIcon())
        .setType(appFunc.getType())
        .setAuthCtrl(appFunc.getAuthCtrl())
        .setEnabled(appFunc.getEnabled())
        .setUrl(appFunc.getUrl())
        .setAppId(appFunc.getAppId())
        .setSequence(appFunc.getSequence())
        .setCreatedBy(appFunc.getCreatedBy())
        .setCreatedByName(appFunc.getCreatedByName())
        .setTags(toTagVos(appFunc.getTags()));
  }

  public static AppFuncTreeVo toAppFuncTreeVo(AppFunc appFunc) {
    return Objects.isNull(appFunc) ? null : new AppFuncTreeVo().setId(appFunc.getId())
        .setCode(appFunc.getCode())
        .setName(appFunc.getName())
        .setShowName(appFunc.getShowName())
        .setPid(appFunc.getPid())
        .setIcon(appFunc.getIcon())
        .setType(appFunc.getType())
        .setAuthCtrl(appFunc.getAuthCtrl())
        .setEnabled(appFunc.getEnabled())
        .setUrl(appFunc.getUrl())
        .setAppId(appFunc.getAppId())
        .setSequence(appFunc.getSequence())
        .setHit(appFunc.getHit())
        .setCreatedBy(appFunc.getCreatedBy())
        .setCreatedByName(appFunc.getCreatedByName())
        .setTags(toTargetTagVos(appFunc));
  }

  public static List<AppFuncTreeVo> toTree(List<AppFunc> appFunc) {
    if (isEmpty(appFunc)) {
      return null;
    }
    List<AppFuncTreeVo> vos = new ArrayList<>();
    for (AppFunc func : appFunc) {
      vos.add(toAppFuncTreeVo(func));
    }
    // Use database sort
    return TreeUtils.toTree(vos, true);
  }

  public static List<AppTagInfoVo> toTagVos(List<WebTag> tags) {
    if (isEmpty(tags)) {
      return null;
    }
    return tags.stream()
        .map(tag -> new AppTagInfoVo().setId(tag.getId()).setName(tag.getName()))
        .collect(Collectors.toList());
  }

  public static List<AppTagInfoVo> toTargetTagVos(AppFunc appFunc) {
    if (isEmpty(appFunc.getTagTargets())) {
      return null;
    }
    return appFunc.getTagTargets().stream()
        .map(tagTarget -> new AppTagInfoVo().setId(tagTarget.getTagId())
            .setName(tagTarget.getTagName()))
        .collect(Collectors.toList());
  }

  public static List<ApiInfoVo> toApiVos(List<Api> apis) {
    if (isEmpty(apis)) {
      return null;
    }
    return apis.stream().map(ApiAssembler::toInfo).collect(Collectors.toList());
  }

  public static GenericSpecification<AppFunc> getSpecification(Long appId, AppFuncFindDto dto) {
    dto.setAppId(appId);
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "code", "createdDate")
        .matchSearchFields("name", "code", "description")
        .inAndNotFields("id", "code", "pid", "apiId", "tagId")
        .orderByFields("sequence")
        .build();
    return new GenericSpecification<>(filters);
  }

}
