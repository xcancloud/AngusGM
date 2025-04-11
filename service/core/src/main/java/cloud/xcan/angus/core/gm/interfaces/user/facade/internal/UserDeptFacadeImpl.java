package cloud.xcan.angus.core.gm.interfaces.user.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler.UserDeptAssembler.getUserDeptSpecification;
import static cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler.UserDeptAssembler.replaceToUserDeptDomain;
import static cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler.UserDeptAssembler.userDeptAddToDomain;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.api.commonlink.user.dept.DeptUser;
import cloud.xcan.angus.api.gm.user.to.UserDeptTo;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.dept.DeptUserCmd;
import cloud.xcan.angus.core.gm.application.query.dept.DeptUserQuery;
import cloud.xcan.angus.core.gm.interfaces.user.facade.UserDeptFacade;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.dept.UserDeptFindDto;
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
public class UserDeptFacadeImpl implements UserDeptFacade {

  @Resource
  private DeptUserCmd userDeptCmd;

  @Resource
  private DeptUserQuery userDeptQuery;

  @Override
  public List<IdKey<Long, Object>> deptAdd(Long userId, LinkedHashSet<Long> deptIds) {
    return userDeptCmd.deptAdd(userId, userDeptAddToDomain(userId, deptIds));
  }

  @Override
  public void deptReplace(Long userId, LinkedHashSet<UserDeptTo> dto) {
    userDeptCmd.deptReplace(userId, replaceToUserDeptDomain(userId, dto));
  }

  @Override
  public void deptDelete(Long userId, HashSet<Long> deptIds) {
    userDeptCmd.deptDelete(userId, deptIds);
  }

  @NameJoin
  @Override
  public PageResult<UserDeptVo> deptList(Long userId, UserDeptFindDto dto) {
    dto.setUserId(userId);
    Page<DeptUser> page = userDeptQuery.findUserDept(getUserDeptSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, UserDeptAssembler::toUserDeptVo);
  }

}
