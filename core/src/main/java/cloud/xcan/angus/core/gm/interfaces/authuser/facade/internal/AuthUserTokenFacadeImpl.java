package cloud.xcan.angus.core.gm.interfaces.authuser.facade.internal;

import cloud.xcan.angus.core.gm.application.cmd.authuser.AuthUserTokenCmd;
import cloud.xcan.angus.core.gm.application.query.authuser.AuthUserTokenQuery;
import cloud.xcan.angus.core.gm.domain.authuser.AuthUserToken;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.AuthUserTokenFacade;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.dto.token.UserTokenAddDto;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.internal.assembler.AuthUserTokenAssembler;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.vo.token.UserTokenInfoVo;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.vo.token.UserTokenValueVo;
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
    AuthUserToken userToken = authUserTokenCmd.add(AuthUserTokenAssembler.addDtoToDomain(dto));
    return AuthUserTokenAssembler.toTokenValueVo(userToken);
  }

  @Override
  public void delete(HashSet<Long> ids) {
    authUserTokenCmd.delete(ids);
  }

  @Override
  public UserTokenValueVo value(Long id) {
    AuthUserToken userToken = authUserTokenQuery.value(id);
    return AuthUserTokenAssembler.toTokenValueVo(userToken);
  }

  @Override
  public List<UserTokenInfoVo> list() {
    return authUserTokenQuery.list().stream().map(AuthUserTokenAssembler::toTokenInfoVo)
        .collect(Collectors.toList());
  }

}
