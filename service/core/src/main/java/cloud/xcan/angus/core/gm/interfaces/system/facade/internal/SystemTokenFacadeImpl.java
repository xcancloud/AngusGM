package cloud.xcan.angus.core.gm.interfaces.system.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.system.facade.internal.assembler.SystemTokenAssembler.addDtoToDomain;
import static cloud.xcan.angus.core.gm.interfaces.system.facade.internal.assembler.SystemTokenAssembler.addDtoToResourceDomain;
import static cloud.xcan.angus.core.gm.interfaces.system.facade.internal.assembler.SystemTokenAssembler.toSysTokenGrantVo;
import static cloud.xcan.angus.core.gm.interfaces.system.facade.internal.assembler.SystemTokenAssembler.toTokenValueVo;

import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.system.SystemTokenCmd;
import cloud.xcan.angus.core.gm.application.query.system.SystemTokenQuery;
import cloud.xcan.angus.core.gm.domain.system.SystemToken;
import cloud.xcan.angus.core.gm.interfaces.system.facade.SystemTokenFacade;
import cloud.xcan.angus.core.gm.interfaces.system.facade.dto.SystemTokenAddDto;
import cloud.xcan.angus.core.gm.interfaces.system.facade.internal.assembler.SystemTokenAssembler;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.SystemTokenDetailVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.SystemTokenInfoVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.SystemTokenValueVo;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;


@Component
public class SystemTokenFacadeImpl implements SystemTokenFacade {

  @Resource
  private SystemTokenCmd systemTokenCmd;

  @Resource
  private SystemTokenQuery systemTokenQuery;

  @Override
  public SystemTokenValueVo add(SystemTokenAddDto dto) {
    SystemToken systemToken = systemTokenCmd.add(addDtoToDomain(dto), addDtoToResourceDomain(dto));
    return toTokenValueVo(systemToken);
  }

  @Override
  public void delete(HashSet<Long> ids) {
    systemTokenCmd.delete(ids);
  }

  @NameJoin
  @Override
  public SystemTokenDetailVo auth(Long id) {
    return toSysTokenGrantVo(systemTokenQuery.auth(id));
  }

  @Override
  public SystemTokenValueVo value(Long id) {
    return toTokenValueVo(systemTokenQuery.value(id));
  }

  @NameJoin
  @Override
  public List<SystemTokenInfoVo> list() {
    return systemTokenQuery.list().stream()
        .map(SystemTokenAssembler::toTokenInfoVo).collect(Collectors.toList());
  }
}
