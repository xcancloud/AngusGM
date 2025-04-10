package cloud.xcan.angus.core.gm.interfaces.dept.facade.internal;

import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.api.commonlink.dept.Dept;
import cloud.xcan.angus.api.gm.dept.dto.DeptFindDto;
import cloud.xcan.angus.api.gm.dept.dto.DeptSearchDto;
import cloud.xcan.angus.api.gm.dept.vo.DeptDetailVo;
import cloud.xcan.angus.api.gm.dept.vo.DeptListVo;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.dept.DeptCmd;
import cloud.xcan.angus.core.gm.application.query.dept.DeptQuery;
import cloud.xcan.angus.core.gm.application.query.dept.DeptSearch;
import cloud.xcan.angus.core.gm.domain.dept.DeptSubCount;
import cloud.xcan.angus.core.gm.interfaces.dept.facade.DeptFacade;
import cloud.xcan.angus.core.gm.interfaces.dept.facade.dto.DeptAddDto;
import cloud.xcan.angus.core.gm.interfaces.dept.facade.dto.DeptReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.dept.facade.dto.DeptUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.dept.facade.internal.assembler.DeptAssembler;
import cloud.xcan.angus.core.gm.interfaces.dept.facade.vo.DeptNavigationTreeVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;


@Component
public class DeptFacadeImpl implements DeptFacade {

  @Resource
  private DeptCmd deptCmd;

  @Resource
  private DeptSearch deptSearch;

  @Resource
  private DeptQuery deptQuery;

  @Override
  public List<IdKey<Long, Object>> add(List<DeptAddDto> dto) {
    List<Dept> dept = dto.stream().map(DeptAssembler::addDtoToDomain)
        .collect(Collectors.toList());
    return deptCmd.add(dept);
  }

  @Override
  public void update(List<DeptUpdateDto> dto) {
    List<Dept> dept = dto.stream().map(DeptAssembler::updateDtoToDomain)
        .collect(Collectors.toList());
    deptCmd.update(dept);
  }

  @Override
  public List<IdKey<Long, Object>> replace(List<DeptReplaceDto> dto) {
    List<Dept> dept = dto.stream().map(DeptAssembler::replaceDtoToDomain)
        .collect(Collectors.toList());
    return deptCmd.replace(dept);
  }

  @Override
  public void delete(Set<Long> ids) {
    deptCmd.delete(ids);
  }

  @Override
  public DeptNavigationTreeVo navigation(Long id) {
    return DeptAssembler.toNavigationVo(deptQuery.navigation(id));
  }

  @NameJoin
  @Override
  public DeptDetailVo detail(Long id) {
    return DeptAssembler.toDetailVo(deptQuery.detail(id));
  }

  @NameJoin
  @Override
  public PageResult<DeptListVo> list(DeptFindDto dto) {
    Page<Dept> page = deptQuery.list(DeptAssembler.getSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, DeptAssembler::toListVo);
  }

  @NameJoin
  @Override
  public PageResult<DeptListVo> search(DeptSearchDto dto) {
    Page<Dept> page = deptSearch.search(DeptAssembler.getSearchCriteria(dto), dto.tranPage(),
        Dept.class, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, DeptAssembler::toListVo);
  }

  @Override
  public DeptSubCount subCount(Long id) {
    return deptQuery.subCount(id);
  }

}
