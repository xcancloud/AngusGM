package cloud.xcan.angus.core.gm.interfaces.dept.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler.UserDeptAssembler.deptUserAddToDomain;
import static cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler.UserDeptAssembler.getDeptUserSpecification;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.api.commonlink.user.dept.DeptUser;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.dept.DeptUserCmd;
import cloud.xcan.angus.core.gm.application.query.dept.DeptUserQuery;
import cloud.xcan.angus.core.gm.interfaces.dept.facade.DeptUserFacade;
import cloud.xcan.angus.core.gm.interfaces.dept.facade.dto.DeptHeadReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.dept.facade.dto.user.DeptUserFindDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler.UserDeptAssembler;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.dept.UserDeptVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;


@Component
public class DeptUserFacadeImpl implements DeptUserFacade {

  @Resource
  private DeptUserCmd userDeptCmd;

  @Resource
  private DeptUserQuery userDeptQuery;

  @Override
  public List<IdKey<Long, Object>> userAdd(Long deptId, LinkedHashSet<Long> userIds) {
    return userDeptCmd.userAdd(deptId, deptUserAddToDomain(deptId, userIds));
  }

  @Override
  public void userDelete(Long deptId, HashSet<Long> userIds) {
    userDeptCmd.userDelete(deptId, userIds);
  }

  @Override
  public void headReplace(Long deptId, DeptHeadReplaceDto dto) {
    userDeptCmd.headReplace(deptId, dto.getUserId(), dto.getHead());
  }

  @NameJoin
  @Override
  public PageResult<UserDeptVo> deptUserList(Long deptId, DeptUserFindDto dto) {
    dto.setDeptId(deptId);
    Page<DeptUser> userDeptPage = userDeptQuery
        .findDeptUser(getDeptUserSpecification(dto), dto.tranPage());
    return buildVoPageResult(userDeptPage, UserDeptAssembler::toUserDeptVo);
  }
}
