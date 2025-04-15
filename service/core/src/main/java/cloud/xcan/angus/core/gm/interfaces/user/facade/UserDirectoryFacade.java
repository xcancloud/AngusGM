package cloud.xcan.angus.core.gm.interfaces.user.facade;

import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.directory.UserDirectoryAddDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.directory.UserDirectoryReorderDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.directory.UserDirectoryReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.directory.UserDirectoryTestDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.directory.UserDirectoryDetailVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.directory.UserDirectorySyncVo;
import cloud.xcan.angus.remote.dto.EnabledOrDisabledDto;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.List;
import java.util.Map;

public interface UserDirectoryFacade {

  IdKey<Long, Object> add(UserDirectoryAddDto dto);

  void replace(UserDirectoryReplaceDto dto);

  void reorder(UserDirectoryReorderDto dto);

  void enabled(EnabledOrDisabledDto dto);

  UserDirectorySyncVo sync(Long id);

  Map<String, UserDirectorySyncVo> sync();

  UserDirectorySyncVo test(UserDirectoryTestDto dto);

  void delete(Long id, Boolean deleteSync);

  UserDirectoryDetailVo detail(Long id);

  List<UserDirectoryDetailVo> list();

}
