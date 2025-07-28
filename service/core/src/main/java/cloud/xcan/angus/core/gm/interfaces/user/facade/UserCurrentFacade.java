package cloud.xcan.angus.core.gm.interfaces.user.facade;

import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.api.gm.tenant.vo.TenantDetailVo;
import cloud.xcan.angus.api.gm.user.dto.UserCurrentUpdateDto;
import cloud.xcan.angus.api.gm.user.vo.UserCurrentDetailVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.current.CurrentEmailCheckDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.current.CurrentEmailSendDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.current.CurrentEmailUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.current.CurrentMobileCheckDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.current.CurrentMobileUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.current.CurrentSmsSendDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.current.CheckSecretVo;
import cloud.xcan.angus.remote.InfoScope;


public interface UserCurrentFacade {

  TenantDetailVo tenantDetail();

  void currentUpdate(UserCurrentUpdateDto dto);

  UserCurrentDetailVo currentDetail(InfoScope infoScope, String appCode, EditionType editionType);

  void sendSms(CurrentSmsSendDto dto);

  CheckSecretVo checkSms(CurrentMobileCheckDto dto);

  void updateMobile(CurrentMobileUpdateDto dto);

  void sendEmail(CurrentEmailSendDto dto);

  CheckSecretVo checkEmail(CurrentEmailCheckDto dto);

  void updateEmail(CurrentEmailUpdateDto dto);

}
