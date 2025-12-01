package cloud.xcan.angus.core.gm.interfaces.user.facade;

import cloud.xcan.angus.api.enums.UserSource;
import cloud.xcan.angus.api.gm.user.dto.UserFindDto;
import cloud.xcan.angus.api.gm.user.vo.UserDetailVo;
import cloud.xcan.angus.api.gm.user.vo.UserListVo;
import cloud.xcan.angus.api.gm.user.dto.UserAddDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserLockedDto;
import cloud.xcan.angus.api.gm.user.dto.UserReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserSysAdminSetDto;
import cloud.xcan.angus.api.gm.user.dto.UserUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserSysAdminVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UsernameCheckVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.remote.dto.EnabledOrDisabledDto;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public interface UserFacade {

  IdKey<Long, Object> add(UserAddDto dto, UserSource userSource);

  void update(UserUpdateDto dto);

  IdKey<Long, Object> replace(UserReplaceDto dto);

  void delete(HashSet<Long> ids);

  void enabled(Set<EnabledOrDisabledDto> dto);

  void locked(UserLockedDto dto);

  void sysAdminSet(UserSysAdminSetDto dto);

  List<UserSysAdminVo> sysAdminList();

  UsernameCheckVo checkUsername(String username);

  UserDetailVo detail(Long id);

  PageResult<UserListVo> list(UserFindDto findDto);

}
