package cloud.xcan.angus.core.gm.interfaces.email.facade;

import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.server.ServerAddDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.server.ServerEnabledCheckDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.server.ServerFindDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.server.ServerReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.server.ServerUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.server.ServerDetailVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.remote.dto.EnabledOrDisabledDto;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;

public interface EmailServerFacade {

  IdKey<Long, Object> add(ServerAddDto dto);

  void update(ServerUpdateDto dto);

  IdKey<Long, Object> replace(ServerReplaceDto dto);

  void delete(HashSet<Long> ids);

  void enabled(EnabledOrDisabledDto dto);

  ServerDetailVo detail(Long id);

  void checkEnable(ServerEnabledCheckDto dto);

  PageResult<ServerDetailVo> list(ServerFindDto dto);

}
