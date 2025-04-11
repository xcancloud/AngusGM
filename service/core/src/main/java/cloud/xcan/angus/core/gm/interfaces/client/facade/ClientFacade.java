package cloud.xcan.angus.core.gm.interfaces.client.facade;

import cloud.xcan.angus.api.gm.client.dto.ClientAddDto;
import cloud.xcan.angus.api.gm.client.dto.ClientReplaceDto;
import cloud.xcan.angus.api.gm.client.dto.ClientUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.client.facade.dto.ClientFindDto;
import cloud.xcan.angus.core.gm.interfaces.client.facade.vo.ClientDetailVo;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.List;

public interface ClientFacade {

  IdKey<String, Object> add(ClientAddDto dto);

  void update(ClientUpdateDto dto);

  IdKey<String, Object> replace(ClientReplaceDto dto);

  void delete(HashSet<String> clientIds);

  ClientDetailVo detail(String id);

  List<ClientDetailVo> list(ClientFindDto dto);

}
