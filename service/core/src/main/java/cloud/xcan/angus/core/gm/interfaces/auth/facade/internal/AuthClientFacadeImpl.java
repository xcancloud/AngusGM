package cloud.xcan.angus.core.gm.interfaces.auth.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.auth.facade.internal.assembler.AuthClientAssembler.addDtoToDomain;
import static cloud.xcan.angus.core.gm.interfaces.auth.facade.internal.assembler.AuthClientAssembler.replaceDtoToDomain;
import static cloud.xcan.angus.core.gm.interfaces.auth.facade.internal.assembler.AuthClientAssembler.toDetailVo;
import static cloud.xcan.angus.core.gm.interfaces.auth.facade.internal.assembler.AuthClientAssembler.updateDtoToDomain;

import cloud.xcan.angus.api.gm.client.dto.AuthClientAddDto;
import cloud.xcan.angus.api.gm.client.dto.AuthClientReplaceDto;
import cloud.xcan.angus.api.gm.client.dto.AuthClientUpdateDto;
import cloud.xcan.angus.core.gm.application.cmd.auth.AuthClientCmd;
import cloud.xcan.angus.core.gm.application.query.auth.AuthClientQuery;
import cloud.xcan.angus.core.gm.interfaces.auth.facade.AuthClientFacade;
import cloud.xcan.angus.core.gm.interfaces.auth.facade.dto.AuthClientFindDto;
import cloud.xcan.angus.core.gm.interfaces.auth.facade.internal.assembler.AuthClientAssembler;
import cloud.xcan.angus.core.gm.interfaces.auth.facade.vo.AuthClientDetailVo;
import cloud.xcan.angus.security.client.CustomOAuth2RegisteredClient;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class AuthClientFacadeImpl implements AuthClientFacade {

  @Resource
  private AuthClientQuery authClientQuery;

  @Resource
  private AuthClientCmd authClientCmd;

  @Override
  public IdKey<String, Object> add(AuthClientAddDto dto) {
    return authClientCmd.add(addDtoToDomain(dto));
  }

  @Override
  public void update(AuthClientUpdateDto dto) {
    authClientCmd.update(updateDtoToDomain(dto));
  }

  @Override
  public IdKey<String, Object> replace(AuthClientReplaceDto dto) {
    return authClientCmd.replace(replaceDtoToDomain(dto));
  }

  @Override
  public void delete(HashSet<String> clientIds) {
    authClientCmd.delete(clientIds);
  }

  @Override
  public AuthClientDetailVo detail(String id) {
    return toDetailVo(authClientQuery.detail(id));
  }

  @Override
  public List<AuthClientDetailVo> list(AuthClientFindDto dto) {
    List<CustomOAuth2RegisteredClient> clients = authClientQuery.find(dto.getId(),
        dto.getClientId(), dto.getTenantId());
    return clients.stream().map(AuthClientAssembler::toDetailVo).collect(Collectors.toList());
  }

}
