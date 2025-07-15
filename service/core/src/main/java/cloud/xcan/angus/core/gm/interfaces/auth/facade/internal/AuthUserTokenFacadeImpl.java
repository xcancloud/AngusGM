package cloud.xcan.angus.core.gm.interfaces.auth.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.auth.facade.internal.assembler.AuthUserTokenAssembler.addDtoToDomain;
import static cloud.xcan.angus.core.gm.interfaces.auth.facade.internal.assembler.AuthUserTokenAssembler.toTokenValueVo;

import cloud.xcan.angus.core.gm.application.cmd.auth.AuthUserTokenCmd;
import cloud.xcan.angus.core.gm.application.query.auth.AuthUserTokenQuery;
import cloud.xcan.angus.core.gm.domain.auth.AuthUserToken;
import cloud.xcan.angus.core.gm.interfaces.auth.facade.AuthUserTokenFacade;
import cloud.xcan.angus.core.gm.interfaces.auth.facade.dto.token.UserTokenAddDto;
import cloud.xcan.angus.core.gm.interfaces.auth.facade.internal.assembler.AuthUserTokenAssembler;
import cloud.xcan.angus.core.gm.interfaces.auth.facade.vo.token.UserTokenInfoVo;
import cloud.xcan.angus.core.gm.interfaces.auth.facade.vo.token.UserTokenValueVo;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;


@Component
public class AuthUserTokenFacadeImpl implements AuthUserTokenFacade {

  @Resource
  private AuthUserTokenCmd authUserTokenCmd;

  @Resource
  private AuthUserTokenQuery authUserTokenQuery;

  @Override
  public UserTokenValueVo add(UserTokenAddDto dto) {
    AuthUserToken userToken = authUserTokenCmd.add(addDtoToDomain(dto));
    return toTokenValueVo(userToken);
  }

  @Override
  public void delete(HashSet<Long> ids) {
    authUserTokenCmd.delete(ids);
  }

  @Override
  public UserTokenValueVo value(Long id) {
    AuthUserToken userToken = authUserTokenQuery.value(id);
    return toTokenValueVo(userToken);
  }

  @Override
  public List<UserTokenInfoVo> list() {
    return authUserTokenQuery.list().stream().map(AuthUserTokenAssembler::toTokenInfoVo)
        .collect(Collectors.toList());
  }

}
