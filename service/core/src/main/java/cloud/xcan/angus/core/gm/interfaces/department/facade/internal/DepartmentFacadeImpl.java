package cloud.xcan.angus.core.gm.interfaces.department.facade.internal;

import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;
import static cloud.xcan.angus.core.utils.CoreUtils.getMatchSearchFields;

import cloud.xcan.angus.core.gm.application.cmd.department.DepartmentCmd;
import cloud.xcan.angus.core.gm.application.query.department.DepartmentQuery;
import cloud.xcan.angus.core.gm.domain.department.Department;
import cloud.xcan.angus.core.gm.interfaces.department.facade.DepartmentFacade;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentCreateDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentFindDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentManagerUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentMemberAddDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentMemberFindDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentMemberRemoveDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentMemberTransferDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.internal.assembler.DepartmentAssembler;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentDetailVo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentListVo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentManagerUpdateVo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentMemberAddVo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentMemberTransferVo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentMemberVo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentPathVo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentStatsVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class DepartmentFacadeImpl implements DepartmentFacade {

  @Resource
  private DepartmentCmd departmentCmd;

  @Resource
  private DepartmentQuery departmentQuery;

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
    return departmentQuery.getStats();
  }

  @Override
  public List<DepartmentDetailVo> getTree(Long parentId, String status, Boolean includeUsers) {
    List<Department> departments = departmentQuery.findTree(parentId, status);
    return departments.stream()
        .map(DepartmentAssembler::toDetailVo)
        .collect(Collectors.toList());
  }

  @Override
  public PageResult<DepartmentMemberVo> listMembers(Long id, DepartmentMemberFindDto dto) {
    // TODO: Implement list department members
    return new PageResult<>(0L, new ArrayList<>());
  }

  @Override
  public DepartmentMemberAddVo addMembers(Long id, DepartmentMemberAddDto dto) {
    // TODO: Implement add department members
    DepartmentMemberAddVo vo = new DepartmentMemberAddVo();
    vo.setDepartmentId(id);
    vo.setAddedCount(dto.getUserIds().size());
    vo.setAddedUsers(new ArrayList<>());
    return vo;
  }

  @Override
  public void removeMember(Long id, Long userId) {
    // TODO: Implement remove department member
  }

  @Override
  public void removeMembers(Long id, DepartmentMemberRemoveDto dto) {
    // TODO: Implement batch remove department members
  }

  @Override
  public DepartmentMemberTransferVo transferMembers(Long id, DepartmentMemberTransferDto dto) {
    // TODO: Implement transfer department members
    DepartmentMemberTransferVo vo = new DepartmentMemberTransferVo();
    vo.setSourceDepartmentId(id);
    vo.setTargetDepartmentId(dto.getTargetDepartmentId());
    vo.setTransferredCount(dto.getUserIds().size());
    return vo;
  }

  @Override
  public DepartmentManagerUpdateVo updateManager(Long id, DepartmentManagerUpdateDto dto) {
    // TODO: Implement update department manager
    DepartmentManagerUpdateVo vo = new DepartmentManagerUpdateVo();
    vo.setDepartmentId(id);
    vo.setManagerId(dto.getManagerId());
    vo.setModifiedDate(LocalDateTime.now());
    return vo;
  }

  @Override
  public DepartmentPathVo getPath(Long id) {
    // TODO: Implement get department path
    return new DepartmentPathVo();
  }

  @Override
  public List<DepartmentListVo> getChildren(Long id, Boolean recursive) {
    // TODO: Implement get children departments
    return new ArrayList<>();
  }
}
