package cloud.xcan.angus.core.gm.interfaces.api.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.api.facade.internal.assembler.ApiAssembler.getSearchCriteria;
import static cloud.xcan.angus.core.gm.interfaces.api.facade.internal.assembler.ApiAssembler.getSpecification;
import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.api.commonlink.api.Api;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.api.ApiCmd;
import cloud.xcan.angus.core.gm.application.query.api.ApiQuery;
import cloud.xcan.angus.core.gm.application.query.api.ApiSearch;
import cloud.xcan.angus.core.gm.interfaces.api.facade.ApiFacade;
import cloud.xcan.angus.core.gm.interfaces.api.facade.dto.ApiAddDto;
import cloud.xcan.angus.core.gm.interfaces.api.facade.dto.ApiFindDto;
import cloud.xcan.angus.core.gm.interfaces.api.facade.dto.ApiReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.api.facade.dto.ApiSearchDto;
import cloud.xcan.angus.core.gm.interfaces.api.facade.dto.ApiUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.api.facade.internal.assembler.ApiAssembler;
import cloud.xcan.angus.core.gm.interfaces.api.facade.vo.ApiDetailVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.remote.dto.EnabledOrDisabledDto;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;


@Component
public class ApiFacadeImpl implements ApiFacade {

  @Resource
  private ApiCmd apiCmd;

  @Resource
  private ApiQuery apiQuery;

  @Resource
  private ApiSearch apiSearch;

  @Override
  public List<IdKey<Long, Object>> add(List<ApiAddDto> dto) {
    List<Api> apis = dto.stream().map(ApiAssembler::addDtoToDomain)
        .collect(Collectors.toList());
    return apiCmd.add(apis);
  }

  @Override
  public void update(List<ApiUpdateDto> dto) {
    List<Api> apis = dto.stream().map(ApiAssembler::updateDtoToDomain)
        .collect(Collectors.toList());
    apiCmd.update(apis);
  }

  @Override
  public List<IdKey<Long, Object>> replace(List<ApiReplaceDto> dto) {
    List<Api> apis = dto.stream().map(ApiAssembler::replaceDtoToDomain)
        .collect(Collectors.toList());
    return apiCmd.replace(apis);
  }

  @Override
  public void delete(HashSet<Long> ids) {
    apiCmd.delete(ids);
  }

  @Override
  public void enabled(List<EnabledOrDisabledDto> dto) {
    List<Api> apis = dto.stream().map(ApiAssembler::enabledDtoToDomain)
        .collect(Collectors.toList());
    apiCmd.enabled(apis);
  }

  @NameJoin
  @Override
  public ApiDetailVo detail(Long id) {
    Api api = apiQuery.detail(id);
    return ApiAssembler.toApiDetailVo(api);
  }

  @NameJoin
  @Override
  public PageResult<ApiDetailVo> list(ApiFindDto dto) {
    Page<Api> apiPage = apiQuery.list(getSpecification(dto), dto.tranPage());
    return buildVoPageResult(apiPage, ApiAssembler::toApiDetailVo);
  }

  @NameJoin
  @Override
  public PageResult<ApiDetailVo> search(ApiSearchDto dto) {
    Page<Api> apiPage = apiSearch.search(getSearchCriteria(dto), dto.tranPage(),
        Api.class, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(apiPage, ApiAssembler::toApiDetailVo);
  }

}
