package cloud.xcan.angus.core.gm.interfaces.department.facade.internal;

import static cloud.xcan.angus.spec.BizConstant.buildVoPageResult;
import static cloud.xcan.angus.spec.BizConstant.getMatchSearchFields;

import cloud.xcan.angus.core.gm.application.cmd.department.DepartmentCmd;
import cloud.xcan.angus.core.gm.application.query.department.DepartmentQuery;
import cloud.xcan.angus.core.gm.domain.department.Department;
import cloud.xcan.angus.core.gm.domain.department.DepartmentRepo;
import cloud.xcan.angus.core.gm.domain.department.enums.DepartmentStatus;
import cloud.xcan.angus.core.gm.interfaces.department.facade.DepartmentFacade;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentCreateDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentFindDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.internal.assembler.DepartmentAssembler;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentDetailVo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentListVo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentStatsVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * Implementation of department facade
 */
@Service
public class DepartmentFacadeImpl implements DepartmentFacade {

  @Resource
  private DepartmentCmd departmentCmd;

  @Resource
  private DepartmentQuery departmentQuery;

  @Resource
  private DepartmentRepo departmentRepo;

  @Override
  public DepartmentDetailVo create(DepartmentCreateDto dto) {
    Department department = DepartmentAssembler.toCreateDomain(dto);
    Department saved = departmentCmd.create(department);
    return DepartmentAssembler.toDetailVo(saved);
  }

  @Override
  public DepartmentDetailVo update(Long id, DepartmentUpdateDto dto) {
    Department department = DepartmentAssembler.toUpdateDomain(id, dto);
    Department saved = departmentCmd.update(department);
    return DepartmentAssembler.toDetailVo(saved);
  }

  @Override
  public void enable(Long id) {
    departmentCmd.enable(id);
  }

  @Override
  public void disable(Long id) {
    departmentCmd.disable(id);
  }

  @Override
  public void delete(Long id) {
    departmentCmd.delete(id);
  }

  @Override
  public DepartmentDetailVo getDetail(Long id) {
    Department department = departmentQuery.findAndCheck(id);
    return DepartmentAssembler.toDetailVo(department);
  }

  @Override
  public PageResult<DepartmentListVo> list(DepartmentFindDto dto) {
    GenericSpecification<Department> spec = DepartmentAssembler.getSpecification(dto);
    Page<Department> page = departmentQuery.find(spec, dto.tranPage(),
        dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, DepartmentAssembler::toListVo);
  }

  @Override
  public DepartmentStatsVo getStats() {
    DepartmentStatsVo stats = new DepartmentStatsVo();
    
    long totalDepartments = departmentRepo.count();
    long enabledDepartments = departmentRepo.countByStatus(DepartmentStatus.ENABLED);
    long disabledDepartments = departmentRepo.countByStatus(DepartmentStatus.DISABLED);
    List<Department> rootDepartments = departmentRepo.findByParentIdIsNull();
    
    stats.setTotalDepartments(totalDepartments);
    stats.setEnabledDepartments(enabledDepartments);
    stats.setDisabledDepartments(disabledDepartments);
    stats.setRootDepartments((long) rootDepartments.size());
    
    // TODO: Calculate average and max level
    stats.setAverageLevel(0.0);
    stats.setMaxLevel(0);
    
    return stats;
  }

  @Override
  public List<DepartmentDetailVo> getTree(Long parentId) {
    List<Department> departments = departmentQuery.findTree(parentId);
    return departments.stream()
        .map(DepartmentAssembler::toDetailVo)
        .collect(Collectors.toList());
  }
}
