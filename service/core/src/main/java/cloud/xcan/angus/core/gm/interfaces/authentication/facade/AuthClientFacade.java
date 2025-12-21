package cloud.xcan.angus.core.gm.interfaces.authentication.facade;

import cloud.xcan.angus.api.gm.client.dto.AuthClientAddDto;
import cloud.xcan.angus.api.gm.client.dto.AuthClientReplaceDto;
import cloud.xcan.angus.api.gm.client.dto.AuthClientUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.AuthClientFindDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.vo.AuthClientDetailVo;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.List;

public interface AuthClientFacade {

  IdKey<String, Object> add(AuthClientAddDto dto);

  void update(AuthClientUpdateDto dto);

  IdKey<String, Object> replace(AuthClientReplaceDto dto);

  void delete(HashSet<String> clientIds);

  AuthClientDetailVo detail(String id);

  List<AuthClientDetailVo> list(AuthClientFindDto dto);

}
