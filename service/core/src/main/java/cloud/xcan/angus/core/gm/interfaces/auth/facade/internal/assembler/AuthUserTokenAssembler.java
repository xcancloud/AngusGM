package cloud.xcan.angus.core.gm.interfaces.auth.facade.internal.assembler;


import static cloud.xcan.angus.core.gm.interfaces.system.facade.internal.assembler.SystemTokenAssembler.safeExpiredDate;

import cloud.xcan.angus.core.gm.domain.auth.AuthUserToken;
import cloud.xcan.angus.core.gm.interfaces.auth.facade.dto.token.UserTokenAddDto;
import cloud.xcan.angus.core.gm.interfaces.auth.facade.vo.token.UserTokenInfoVo;
import cloud.xcan.angus.core.gm.interfaces.auth.facade.vo.token.UserTokenValueVo;


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
