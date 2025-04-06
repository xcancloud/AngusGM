package cloud.xcan.angus.core.gm.interfaces.client.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.client.facade.internal.assembler.ClientAssembler.addDtoToDomain;
import static cloud.xcan.angus.core.gm.interfaces.client.facade.internal.assembler.ClientAssembler.replaceDtoToDomain;
import static cloud.xcan.angus.core.gm.interfaces.client.facade.internal.assembler.ClientAssembler.toDetailVo;
import static cloud.xcan.angus.core.gm.interfaces.client.facade.internal.assembler.ClientAssembler.updateDtoToDomain;

import cloud.xcan.angus.api.gm.client.dto.ClientAddDto;
import cloud.xcan.angus.api.gm.client.dto.ClientReplaceDto;
import cloud.xcan.angus.api.gm.client.dto.ClientUpdateDto;
import cloud.xcan.angus.core.gm.application.cmd.client.ClientCmd;
import cloud.xcan.angus.core.gm.application.query.client.ClientQuery;
import cloud.xcan.angus.core.gm.interfaces.client.facade.ClientFacade;
import cloud.xcan.angus.core.gm.interfaces.client.facade.dto.ClientFindDto;
import cloud.xcan.angus.core.gm.interfaces.client.facade.internal.assembler.ClientAssembler;
import cloud.xcan.angus.core.gm.interfaces.client.facade.vo.ClientDetailVo;
import cloud.xcan.angus.security.client.CustomOAuth2RegisteredClient;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ClientFacadeImpl implements ClientFacade {

  @Resource
  private ClientQuery clientQuery;

  @Resource
  private ClientCmd clientCmd;

  @Override
  public IdKey<String, Object> add(ClientAddDto dto) {
    return clientCmd.add(addDtoToDomain(dto));
  }

  @Override
  public void update(ClientUpdateDto dto) {
    clientCmd.update(updateDtoToDomain(dto));
  }

  @Override
  public IdKey<String, Object> replace(ClientReplaceDto dto) {
    return clientCmd.replace(replaceDtoToDomain(dto));
  }

  @Override
  public void delete(HashSet<String> clientIds) {
    clientCmd.delete(clientIds);
  }

  @Override
  public ClientDetailVo detail(String id) {
    return toDetailVo(clientQuery.detail(id));
  }

  @Override
  public List<ClientDetailVo> list(ClientFindDto dto) {
    List<CustomOAuth2RegisteredClient> clients = clientQuery.find(dto.getId(),
        dto.getClientId(), dto.getTenantId());
    return clients.stream().map(ClientAssembler::toDetailVo).collect(Collectors.toList());
  }

}
