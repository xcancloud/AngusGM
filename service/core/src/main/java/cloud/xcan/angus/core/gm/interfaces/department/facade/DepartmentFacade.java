package cloud.xcan.angus.core.gm.interfaces.department.facade;

import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentCreateDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentFindDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentManagerUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentMemberAddDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentMemberFindDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentMemberRemoveDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentMemberTransferDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentDetailVo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentListVo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentManagerUpdateVo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentMemberAddVo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentMemberTransferVo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentMemberVo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentPathVo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentStatsVo;
import cloud.xcan.angus.remote.PageResult;
import java.util.List;

public interface DepartmentFacade {

  DepartmentDetailVo create(DepartmentCreateDto dto);

  DepartmentDetailVo update(Long id, DepartmentUpdateDto dto);

  DepartmentManagerUpdateVo updateManager(Long id, DepartmentManagerUpdateDto dto);

  void delete(Long id);

  DepartmentDetailVo getDetail(Long id);

  PageResult<DepartmentListVo> list(DepartmentFindDto dto);

  List<DepartmentDetailVo> getTree(Long parentId, String status, Boolean includeUsers);

  DepartmentPathVo getPath(Long id);

  List<DepartmentListVo> getChildren(Long id, Boolean recursive);

  DepartmentStatsVo getStats();

  PageResult<DepartmentMemberVo> listMembers(Long id, DepartmentMemberFindDto dto);

  DepartmentMemberAddVo addMembers(Long id, DepartmentMemberAddDto dto);

  void removeMember(Long id, Long userId);

  void removeMembers(Long id, DepartmentMemberRemoveDto dto);

  DepartmentMemberTransferVo transferMembers(Long id, DepartmentMemberTransferDto dto);
}
