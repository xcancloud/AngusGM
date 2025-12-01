package cloud.xcan.angus.core.gm.interfaces.auth.facade.internal.assembler;


import static cloud.xcan.angus.core.gm.interfaces.system.facade.internal.assembler.SystemTokenAssembler.safeExpiredDate;

import cloud.xcan.angus.api.gm.auth.dto.UserTokenAddDto;
import cloud.xcan.angus.api.gm.auth.vo.UserTokenInfoVo;
import cloud.xcan.angus.api.gm.auth.vo.UserTokenValueVo;
import cloud.xcan.angus.core.gm.domain.auth.AuthUserToken;

public class AuthUserTokenAssembler {

  public static AuthUserToken addDtoToDomain(UserTokenAddDto dto) {
    return new AuthUserToken().setName(dto.getName())
        .setExpiredDate(safeExpiredDate(dto.getExpiredDate()))
        .setPassword(dto.getPassword())
        .setGenerateAppCode(dto.getGenerateAppCode());
  }

  public static UserTokenValueVo toTokenValueVo(AuthUserToken userToken) {
    return new UserTokenValueVo().setId(userToken.getId())
        .setName(userToken.getName())
        .setValue(userToken.getDecryptedValue())
        .setHash(userToken.getHash())
        .setExpiredDate(userToken.getExpiredDate())
        .setGenerateAppCode(userToken.getGenerateAppCode());
  }

  public static UserTokenInfoVo toTokenInfoVo(AuthUserToken userToken) {
    return new UserTokenInfoVo().setId(userToken.getId())
        .setName(userToken.getName())
        .setHash(userToken.getHash())
        .setExpiredDate(userToken.getExpiredDate())
        .setGenerateAppCode(userToken.getGenerateAppCode())
        .setCreatedBy(userToken.getCreatedBy())
        .setCreatedDate(userToken.getCreatedDate());
  }
}
