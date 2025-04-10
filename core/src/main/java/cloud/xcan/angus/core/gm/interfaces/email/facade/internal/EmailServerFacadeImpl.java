package cloud.xcan.angus.core.gm.interfaces.email.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.email.facade.internal.assembler.ServerAssembler.addDtoToDomain;
import static cloud.xcan.angus.core.gm.interfaces.email.facade.internal.assembler.ServerAssembler.replaceDtoToDomain;
import static cloud.xcan.angus.core.gm.interfaces.email.facade.internal.assembler.ServerAssembler.updateDtoToDomain;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.gm.application.cmd.email.EmailServerCmd;
import cloud.xcan.angus.core.gm.application.query.email.EmailServerQuery;
import cloud.xcan.angus.core.gm.domain.email.server.EmailServer;
import cloud.xcan.angus.core.gm.interfaces.email.facade.EmailServerFacade;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.server.ServerAddDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.server.ServerEnabledCheckDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.server.ServerFindDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.server.ServerReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.server.ServerUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.internal.assembler.ServerAssembler;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.server.ServerDetailVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.remote.dto.EnabledOrDisabledDto;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.HashSet;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class EmailServerFacadeImpl implements EmailServerFacade {

  @Resource
  private EmailServerCmd emailServerCmd;

  @Resource
  private EmailServerQuery emailServerQuery;

  @Override
  public IdKey<Long, Object> add(ServerAddDto dto) {
    return emailServerCmd.add(addDtoToDomain(dto));
  }

  @Override
  public void update(ServerUpdateDto dto) {
    emailServerCmd.update(updateDtoToDomain(dto));
  }

  @Override
  public IdKey<Long, Object> replace(ServerReplaceDto dto) {
    return emailServerCmd.replace(replaceDtoToDomain(dto));
  }

  @Override
  public void delete(HashSet<Long> ids) {
    emailServerCmd.delete(ids);
  }

  @Override
  public void enabled(EnabledOrDisabledDto dto) {
    emailServerCmd.enabled(dto.getId(), dto.getEnabled());
  }

  @Override
  public void checkEnable(ServerEnabledCheckDto dto) {
    emailServerQuery.checkEnable(dto.getProtocol());
  }

  @Override
  public ServerDetailVo detail(Long id) {
    EmailServer emailServer = emailServerQuery.detail(id);
    return ServerAssembler.toDetail(emailServer);
  }

  @Override
  public PageResult<ServerDetailVo> list(ServerFindDto dto) {
    Page<EmailServer> serverPage = emailServerQuery
        .find(ServerAssembler.getSpecification(dto), dto.tranPage());
    return buildVoPageResult(serverPage, ServerAssembler::toDetail);
  }

}
