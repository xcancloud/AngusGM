package cloud.xcan.angus.core.gm.interfaces.dept.facade.internal.assembler;

import static cloud.xcan.angus.spec.experimental.BizConstant.DEFAULT_ROOT_PID;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.pidSafe;

import cloud.xcan.angus.api.commonlink.dept.Dept;
import cloud.xcan.angus.api.gm.dept.dto.DeptFindDto;
import cloud.xcan.angus.api.gm.dept.dto.DeptSearchDto;
import cloud.xcan.angus.api.gm.dept.vo.DeptDetailVo;
import cloud.xcan.angus.api.gm.dept.vo.DeptListVo;
import cloud.xcan.angus.core.gm.interfaces.dept.facade.dto.DeptAddDto;
import cloud.xcan.angus.core.gm.interfaces.dept.facade.dto.DeptReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.dept.facade.dto.DeptUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.dept.facade.vo.DeptNavigationTreeVo;
import cloud.xcan.angus.core.gm.interfaces.dept.facade.vo.DeptNavigationVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.info.IdAndName;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


public class DeptAssembler {

  public static Dept addDtoToDomain(DeptAddDto dto) {
    return new Dept().setCode(dto.getCode())
        .setName(dto.getName())
        .setPid(Objects.isNull(dto.getPid()) || dto.getPid() < DEFAULT_ROOT_PID
            ? DEFAULT_ROOT_PID : dto.getPid())
        .setTagIds(dto.getTagIds());
  }

  public static Dept updateDtoToDomain(DeptUpdateDto dto) {
    return new Dept().setId(dto.getId())
        .setName(dto.getName())
        //.setPid(pidSafe(dto.getPid())) <- Bug
        .setPid(dto.getPid())
        .setTagIds(dto.getTagIds());
  }

  public static Dept replaceDtoToDomain(DeptReplaceDto dto) {
    return new Dept().setId(dto.getId())
        .setCode(dto.getCode())
        .setName(dto.getName())
        .setPid(pidSafe(dto.getPid()))
        .setTagIds(dto.getTagIds());
  }

  public static DeptNavigationTreeVo toNavigationVo(Dept navigation) {
    return new DeptNavigationTreeVo()
        .setCurrent(toNavigationTo(navigation))
        .setParentChain(isEmpty(navigation.getParentChain()) ? null :
            navigation.getParentChain().stream().map(DeptAssembler::toNavigationTo)
                .collect(Collectors.toList()));
  }

  public static DeptNavigationVo toNavigationTo(Dept navigation) {
    return new DeptNavigationVo().setId(navigation.getId())
        .setName(navigation.getName()).setCode(navigation.getCode())
        .setPid(navigation.getPid()).setLevel(navigation.getLevel());
  }

  public static DeptDetailVo toDetailVo(Dept dept) {
    return new DeptDetailVo().setId(dept.getId())
        .setCode(dept.getCode())
        .setName(dept.getName())
        .setPid(pidSafe(dept.getPid()))
        .setLevel(dept.getLevel())
        .setHasSubDept(dept.getHasSubDept())
        .setTenantId(dept.getTenantId())
        .setCreatedBy(dept.getCreatedBy())
        .setCreatedDate(dept.getCreatedDate())
        .setLastModifiedBy(dept.getLastModifiedBy())
        .setLastModifiedDate(dept.getLastModifiedDate())
        .setTags(isEmpty(dept.getTags()) ? null : dept.getTags().stream()
            .map(tag -> new IdAndName().setId(tag.getTag().getId()).setName(tag.getTag().getName()))
            .collect(Collectors.toList()));
  }

  public static DeptListVo toListVo(Dept dept) {
    return new DeptListVo().setId(dept.getId())
        .setCode(dept.getCode())
        .setName(dept.getName())
        .setPid(pidSafe(dept.getPid()))
        .setLevel(dept.getLevel())
        .setHasSubDept(dept.getHasSubDept())
        .setTenantId(dept.getTenantId())
        .setCreatedBy(dept.getCreatedBy())
        .setCreatedDate(dept.getCreatedDate())
        .setLastModifiedBy(dept.getLastModifiedBy())
        .setLastModifiedDate(dept.getLastModifiedDate());
  }

  public static GenericSpecification<Dept> getSpecification(DeptFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate")
        .matchSearchFields("name", "code")
        .orderByFields("id", "name", "createdDate")
        .build();
    return new GenericSpecification<>(filters);
  }

  public static Set<SearchCriteria> getSearchCriteria(DeptSearchDto dto) {
    // Build the final filters
    return new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate")
        .matchSearchFields("name", "code")
        .orderByFields("id", "name", "createdDate")
        .build();
  }

}
