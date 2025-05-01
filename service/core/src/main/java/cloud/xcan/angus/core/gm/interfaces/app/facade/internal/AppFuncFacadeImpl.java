package cloud.xcan.angus.core.gm.interfaces.app.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.app.facade.internal.assembler.AppFuncAssembler.addDtoToDomain;
import static cloud.xcan.angus.core.gm.interfaces.app.facade.internal.assembler.AppFuncAssembler.getSearchCriteria;
import static cloud.xcan.angus.core.gm.interfaces.app.facade.internal.assembler.AppFuncAssembler.getSpecification;
import static cloud.xcan.angus.core.gm.interfaces.app.facade.internal.assembler.AppFuncAssembler.replaceDtoToDomain;
import static cloud.xcan.angus.core.gm.interfaces.app.facade.internal.assembler.AppFuncAssembler.toAppFuncDetailVo;
import static cloud.xcan.angus.core.gm.interfaces.app.facade.internal.assembler.AppFuncAssembler.toTree;
import static cloud.xcan.angus.core.gm.interfaces.app.facade.internal.assembler.AppFuncAssembler.updateDtoToDomain;
import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;

import cloud.xcan.angus.api.commonlink.app.func.AppFunc;
import cloud.xcan.angus.core.biz.JoinSupplier;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.app.AppFuncCmd;
import cloud.xcan.angus.core.gm.application.query.app.AppFuncQuery;
import cloud.xcan.angus.core.gm.application.query.app.AppFuncSearch;
import cloud.xcan.angus.core.gm.interfaces.app.facade.AppFuncFacade;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.func.AppFuncAddDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.func.AppFuncFindDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.func.AppFuncReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.func.AppFuncUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.internal.assembler.AppFuncAssembler;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.func.AppFuncDetailVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.func.AppFuncTreeVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.func.AppFuncVo;
import cloud.xcan.angus.remote.dto.EnabledOrDisabledDto;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;


@Component
public class AppFuncFacadeImpl implements AppFuncFacade {

  @Resource
  private AppFuncCmd appFuncCmd;

  @Resource
  private AppFuncQuery appFuncQuery;

  @Resource
  private AppFuncSearch appFuncSearch;

  @Resource
  private JoinSupplier joinSupplier;

  @Override
  public List<IdKey<Long, Object>> add(Long appId, List<AppFuncAddDto> dto) {
    List<AppFunc> appFunctions = dto.stream().map(x -> addDtoToDomain(appId, x))
        .collect(Collectors.toList());
    return appFuncCmd.add(appId, appFunctions);
  }

  @Override
  public void update(Long appId, List<AppFuncUpdateDto> dto) {
    List<AppFunc> appFunctions = dto.stream().map(x -> updateDtoToDomain(appId, x))
        .collect(Collectors.toList());
    appFuncCmd.update(appId, appFunctions);
  }

  @Override
  public void replace(Long appId, List<AppFuncReplaceDto> dto) {
    List<AppFunc> appFunctions = dto.stream().map(x -> replaceDtoToDomain(appId, x))
        .collect(Collectors.toList());
    appFuncCmd.replace(appId, appFunctions);
  }

  @Override
  public void delete(Long appId, HashSet<Long> ids) {
    appFuncCmd.delete(appId, ids);
  }

  @Override
  public void enabled(Long appId, List<EnabledOrDisabledDto> dto) {
    List<AppFunc> appFunctions = dto.stream().map(AppFuncAssembler::enabledDtoToDomain)
        .collect(Collectors.toList());
    appFuncCmd.enabled(appId, appFunctions);
  }

  @NameJoin
  @Override
  public AppFuncDetailVo detail(Long id) {
    AppFunc appFunctions = appFuncQuery.detail(id);
    return toAppFuncDetailVo(appFunctions);
  }

  @NameJoin
  @Override
  public List<AppFuncVo> list(Long appId, AppFuncFindDto dto) {
    List<AppFunc> appFunctions = appFuncQuery.list(getSpecification(appId, dto));
    return appFunctions.stream().map(AppFuncAssembler::toAppFuncVo).collect(Collectors.toList());
  }

  @NameJoin
  @Override
  public List<AppFuncVo> search(Long appId, AppFuncFindDto dto) {
    List<AppFunc> appFunctions = appFuncSearch.search(getSearchCriteria(appId, dto)
        , AppFunc.class, getMatchSearchFields(dto.getClass()));
    return appFunctions.stream().map(AppFuncAssembler::toAppFuncVo).collect(Collectors.toList());
  }

  @Override
  public List<AppFuncTreeVo> tree(Long appId, AppFuncFindDto dto) {
    List<AppFunc> appFunctions = appFuncQuery.list(getSpecification(appId, dto));
    joinSupplier.execute(() -> appFunctions);
    return toTree(appFunctions);
  }

  @Override
  public List<AppFuncTreeVo> treeSearch(Long appId, AppFuncFindDto dto) {
    List<AppFunc> appFunctions = appFuncSearch.search(getSearchCriteria(appId, dto)
        , AppFunc.class, getMatchSearchFields(dto.getClass()));
    joinSupplier.execute(() -> appFunctions);
    return toTree(appFunctions);
  }

}
