package cloud.xcan.angus.core.gm.interfaces.user.facade.internal;

import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.emptySafe;

import cloud.xcan.angus.api.commonlink.setting.user.preference.Preference;
import cloud.xcan.angus.api.gm.tenant.vo.TenantDetailVo;
import cloud.xcan.angus.api.gm.user.dto.UserCurrentUpdateDto;
import cloud.xcan.angus.api.gm.user.vo.UserCurrentDetailVo;
import cloud.xcan.angus.core.gm.application.cmd.setting.SettingUserCmd;
import cloud.xcan.angus.core.gm.application.cmd.user.UserCurrentCmd;
import cloud.xcan.angus.core.gm.application.query.user.UserCurrentQuery;
import cloud.xcan.angus.core.gm.interfaces.email.facade.internal.assembler.EmailAssembler;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.internal.assembler.SettingUserAssembler;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.internal.assembler.SmsAssembler;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.TenantFacade;
import cloud.xcan.angus.core.gm.interfaces.user.facade.UserCurrentFacade;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.current.CurrentEmailCheckDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.current.CurrentEmailSendDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.current.CurrentEmailUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.current.CurrentMobileCheckDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.current.CurrentMobileUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.current.CurrentSmsSendDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler.UserCurrentAssembler;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.current.CheckSecretVo;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;


@Component
public class UserCurrentFacadeImpl implements UserCurrentFacade {

  @Resource
  private UserCurrentCmd userCurrentCmd;

  @Resource
  private UserCurrentQuery userCurrentQuery;

  @Resource
  private TenantFacade tenantFacade;

  @Resource
  private SettingUserCmd settingUserCmd;

  @Override
  public TenantDetailVo tenantDetail() {
    return tenantFacade.detail(PrincipalContext.getTenantId());
  }

  @Override
  public void currentUpdate(UserCurrentUpdateDto dto) {
    userCurrentCmd.updateCurrent(UserCurrentAssembler.updateDtoToDomain(dto));
  }

  @Override
  public UserCurrentDetailVo currentDetail() {
    UserCurrentDetailVo vo = UserCurrentAssembler.toDetailVo(userCurrentQuery.currentDetail());
    Preference preferenceData = settingUserCmd.findAndInit(getUserId()).getPreference();
    vo.setPreference(SettingUserAssembler.toPreferenceTo(preferenceData));
    return vo;
  }

  @Override
  public void sendSms(CurrentSmsSendDto dto) {
    userCurrentQuery.sendSms(SmsAssembler.signToDomain(dto), emptySafe(dto.getCountry(),
        PrincipalContext.getCountry()));
  }

  @Override
  public CheckSecretVo checkSms(CurrentMobileCheckDto dto) {
    return new CheckSecretVo().setLinkSecret(
        userCurrentQuery.checkSms(dto.getBizKey(), dto.getMobile(), dto.getCountry(),
            dto.getVerificationCode()));
  }

  @Override
  public void updateMobile(CurrentMobileUpdateDto dto) {
    userCurrentCmd.mobileUpdate(dto.getMobile(), dto.getCountry(), dto.getItc(),
        dto.getVerificationCode(), dto.getLinkSecret(), dto.getBizKey());
  }

  @Override
  public void sendEmail(CurrentEmailSendDto dto) {
    userCurrentQuery.sendEmail(EmailAssembler.signToDomain(dto));
  }

  @Override
  public CheckSecretVo checkEmail(CurrentEmailCheckDto dto) {
    return new CheckSecretVo().setLinkSecret(userCurrentQuery.checkEmail(dto.getBizKey(),
        dto.getEmail(), dto.getVerificationCode()));
  }

  @Override
  public void updateEmail(CurrentEmailUpdateDto dto) {
    userCurrentCmd.updateEmail(dto.getEmail(), dto.getVerificationCode(),
        dto.getLinkSecret(), dto.getBizKey());
  }
}
