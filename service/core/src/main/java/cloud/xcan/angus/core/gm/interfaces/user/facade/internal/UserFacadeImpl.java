package cloud.xcan.angus.core.gm.interfaces.user.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler.UserAssembler.addDtoToDomain;
import static cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler.UserAssembler.dtoToUserDeptDomain;
import static cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler.UserAssembler.dtoToUserGroupDomain;
import static cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler.UserAssembler.dtoToUserTagDomain;
import static cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler.UserAssembler.getSpecification;
import static cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler.UserAssembler.replaceDtoToDomain;
import static cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler.UserAssembler.toAdminListVo;
import static cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler.UserAssembler.toDetailVo;
import static cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler.UserAssembler.updateDtoToDomain;
import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.enums.UserSource;
import cloud.xcan.angus.api.gm.user.dto.UserFindDto;
import cloud.xcan.angus.api.gm.user.vo.UserDetailVo;
import cloud.xcan.angus.api.gm.user.vo.UserListVo;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.user.UserCmd;
import cloud.xcan.angus.core.gm.application.query.user.UserQuery;
import cloud.xcan.angus.core.gm.interfaces.user.facade.UserFacade;
import cloud.xcan.angus.api.gm.user.dto.UserAddDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserLockedDto;
import cloud.xcan.angus.api.gm.user.dto.UserReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserSysAdminSetDto;
import cloud.xcan.angus.api.gm.user.dto.UserUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler.UserAssembler;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserSysAdminVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UsernameCheckVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.remote.dto.EnabledOrDisabledDto;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class UserFacadeImpl implements UserFacade {

  @Resource
  private UserCmd userCmd;

  @Resource
  private UserQuery userQuery;

  @Override
  public IdKey<Long, Object> add(UserAddDto dto, UserSource userSource) {
    return userCmd.add(addDtoToDomain(dto, userSource),
        dtoToUserDeptDomain(dto.getDepts(), dto.getId()),
        dtoToUserGroupDomain(dto.getGroupIds(), dto.getId()),
        dtoToUserTagDomain(dto.getTagIds(), dto.getId()), userSource);
  }

  @Override
  public void update(UserUpdateDto dto) {
    userCmd.update(updateDtoToDomain(dto),
        dtoToUserDeptDomain(dto.getDepts(), dto.getId()),
        dtoToUserGroupDomain(dto.getGroupIds(), dto.getId()),
        dtoToUserTagDomain(dto.getTagIds(), dto.getId()));
  }

  @Override
  public IdKey<Long, Object> replace(UserReplaceDto dto) {
    return userCmd.replace(replaceDtoToDomain(dto),
        dtoToUserDeptDomain(dto.getDepts(), dto.getId()),
        dtoToUserGroupDomain(dto.getGroupIds(), dto.getId()),
        dtoToUserTagDomain(dto.getTagIds(), dto.getId()));
  }

  @Override
  public void delete(HashSet<Long> ids) {
    userCmd.delete(ids);
  }

  @Override
  public void enabled(Set<EnabledOrDisabledDto> dto) {
    userCmd.enabled(dto.stream().map(UserAssembler::enabledDtoToDomain)
        .collect(Collectors.toList()));
  }

  @Override
  public void locked(UserLockedDto dto) {
    userCmd.locked(dto.getId(), dto.getLocked(), dto.getLockStartDate(), dto.getLockEndDate());
  }

  @Override
  public void sysAdminSet(UserSysAdminSetDto dto) {
    userCmd.adminSet(dto.getId(), dto.getSysAdmin());
  }

  @Override
  public List<UserSysAdminVo> sysAdminList() {
    return toAdminListVo(userQuery.getSysAdmins());
  }

  @Override
  public UsernameCheckVo checkUsername(String username) {
    return new UsernameCheckVo().setUsername(username)
        .setUserId(userQuery.checkUsername(username));
  }

  @NameJoin
  @Override
  public UserDetailVo detail(Long id) {
    return toDetailVo(userQuery.detail(id, true));
  }

  @NameJoin
  @Override
  public PageResult<UserListVo> list(UserFindDto dto) {
    Page<User> page = userQuery.list(getSpecification(dto), dto.tranPage(),
        dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, UserAssembler::toListVo);
  }

}
