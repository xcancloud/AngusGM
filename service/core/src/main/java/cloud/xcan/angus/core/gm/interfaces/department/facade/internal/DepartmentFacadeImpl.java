package cloud.xcan.angus.core.gm.interfaces.department.facade.internal;

import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.gm.application.cmd.department.DepartmentCmd;
import cloud.xcan.angus.core.gm.application.query.department.DepartmentQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserQuery;
import cloud.xcan.angus.core.gm.domain.department.Department;
import cloud.xcan.angus.core.gm.domain.user.User;
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

  @Resource
  private UserQuery userQuery;

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
        dto.isFullTextSearch(), new String[]{"name", "code"});
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
    // Verify department exists
    departmentQuery.findAndCheck(id);
    
    // Build specification for member query
    GenericSpecification<User> spec = DepartmentAssembler.getMemberSpecification(id, dto);
    
    // Query members with pagination
    Page<User> page = departmentQuery.findMembers(id, spec, dto.tranPage());
    
    // Convert to VO using buildVoPageResult
    return buildVoPageResult(page, user -> DepartmentAssembler.toMemberVo(user, id));
  }

  @Override
  public DepartmentMemberAddVo addMembers(Long id, DepartmentMemberAddDto dto) {
    // Add members to department
    int addedCount = departmentCmd.addMembers(id, dto.getUserIds());
    
    // Build response VO
    DepartmentMemberAddVo vo = new DepartmentMemberAddVo();
    vo.setDepartmentId(id);
    vo.setAddedCount(addedCount);
    
    // Get added users info
    List<DepartmentMemberAddVo.AddedUserVo> addedUsers = dto.getUserIds().stream()
        .map(userId -> {
          try {
            User user = userQuery.findAndCheck(userId);
            DepartmentMemberAddVo.AddedUserVo addedUser = new DepartmentMemberAddVo.AddedUserVo();
            addedUser.setId(user.getId());
            addedUser.setName(user.getName());
            return addedUser;
          } catch (Exception e) {
            // Skip if user not found
            return null;
          }
        })
        .filter(user -> user != null)
        .collect(Collectors.toList());
    
    vo.setAddedUsers(addedUsers);
    return vo;
  }

  @Override
  public void removeMember(Long id, Long userId) {
    departmentCmd.removeMember(id, userId);
  }

  @Override
  public void removeMembers(Long id, DepartmentMemberRemoveDto dto) {
    departmentCmd.removeMembers(id, dto.getUserIds());
  }

  @Override
  public DepartmentMemberTransferVo transferMembers(Long id, DepartmentMemberTransferDto dto) {
    int transferredCount = departmentCmd.transferMembers(id, dto.getTargetDepartmentId(), dto.getUserIds());
    
    DepartmentMemberTransferVo vo = new DepartmentMemberTransferVo();
    vo.setSourceDepartmentId(id);
    vo.setTargetDepartmentId(dto.getTargetDepartmentId());
    vo.setTransferredCount(transferredCount);
    return vo;
  }

  @Override
  public DepartmentManagerUpdateVo updateManager(Long id, DepartmentManagerUpdateDto dto) {
    Department department = departmentCmd.updateManager(id, dto.getManagerId());
    DepartmentManagerUpdateVo vo = new DepartmentManagerUpdateVo();
    vo.setDepartmentId(id);
    vo.setManagerId(dto.getManagerId());
    vo.setManagerName(department.getManagerName());
    // ModifiedDate will be set automatically by JPA auditing, use current time as fallback
    vo.setModifiedDate(LocalDateTime.now());
    return vo;
  }

  @Override
  public DepartmentPathVo getPath(Long id) {
    List<Department> path = departmentQuery.getPath(id);
    DepartmentPathVo vo = new DepartmentPathVo();
    
    // Build path string
    String pathStr = path.stream()
        .map(Department::getName)
        .collect(Collectors.joining("/"));
    vo.setPath(pathStr);
    
    // Build path array
    List<DepartmentPathVo.PathItemVo> pathArray = path.stream()
        .map(dept -> {
          DepartmentPathVo.PathItemVo item = new DepartmentPathVo.PathItemVo();
          item.setId(dept.getId());
          item.setName(dept.getName());
          return item;
        })
        .collect(Collectors.toList());
    vo.setPathArray(pathArray);
    
    return vo;
  }

  @Override
  public List<DepartmentListVo> getChildren(Long id, Boolean recursive) {
    List<Department> children = departmentQuery.findChildren(id, recursive);
    return children.stream()
        .map(DepartmentAssembler::toListVo)
        .collect(Collectors.toList());
  }
}
