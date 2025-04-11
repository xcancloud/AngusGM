package cloud.xcan.angus.core.gm.interfaces.authuser.facade.internal.assembler;


import static cloud.xcan.angus.core.gm.interfaces.system.facade.internal.assembler.SystemTokenAssembler.safeExpiredDate;

import cloud.xcan.angus.core.gm.domain.authuser.AuthUserToken;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.dto.token.UserTokenAddDto;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.vo.token.UserTokenInfoVo;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.vo.token.UserTokenValueVo;


public class AuthUserTokenAssembler {

  public static AuthUserToken addDtoToDomain(UserTokenAddDto dto) {
    return new AuthUserToken().setName(dto.getName())
        .setExpiredDate(safeExpiredDate(dto.getExpiredDate()))
        .setPassword(dto.getPassword());
  }

  public static UserTokenValueVo toTokenValueVo(AuthUserToken userToken) {
    return new UserTokenValueVo().setId(userToken.getId())
        .setValue(userToken.getDecryptedValue())
        .setExpiredDate(userToken.getExpiredDate());
  }

  public static UserTokenInfoVo toTokenInfoVo(AuthUserToken userToken) {
    return new UserTokenInfoVo().setId(userToken.getId())
        .setName(userToken.getName())
        .setExpiredDate(userToken.getExpiredDate())
        .setCreatedBy(userToken.getCreatedBy())
        .setCreatedDate(userToken.getCreatedDate());
  }
}
