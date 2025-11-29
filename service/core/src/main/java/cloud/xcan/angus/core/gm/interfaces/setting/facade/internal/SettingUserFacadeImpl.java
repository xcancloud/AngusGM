package cloud.xcan.angus.core.gm.interfaces.setting.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.setting.facade.internal.assembler.SettingUserAssembler.toPreferenceTo;
import static cloud.xcan.angus.core.gm.interfaces.setting.facade.internal.assembler.SettingUserAssembler.toSocialBindingVo;
import static cloud.xcan.angus.core.gm.interfaces.setting.facade.internal.assembler.SettingUserAssembler.updateDtoToPreference;
import static cloud.xcan.angus.core.gm.interfaces.setting.facade.internal.assembler.SettingUserAssembler.updateDtoToSocialBinding;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;

import cloud.xcan.angus.api.commonlink.setting.user.preference.Preference;
import cloud.xcan.angus.api.commonlink.setting.user.socialbinding.SocialBinding;
import cloud.xcan.angus.api.enums.SocialType;
import cloud.xcan.angus.api.gm.setting.dto.UserSocialUpdateDto;
import cloud.xcan.angus.api.gm.setting.vo.UserPreferenceVo;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.gm.application.cmd.setting.SettingUserCmd;
import cloud.xcan.angus.core.gm.application.query.setting.SettingUserQuery;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.SettingUserFacade;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.dto.user.UserPreferenceUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.vo.user.UserSocialBindingVo;
import jakarta.annotation.Resource;

@Biz
public class SettingUserFacadeImpl implements SettingUserFacade {

  @Resource
  private SettingUserQuery settingUserQuery;

  @Resource
  private SettingUserCmd settingUserCmd;

  @Override
  public void preferenceUpdate(UserPreferenceUpdateDto dto) {
    settingUserCmd.preferenceUpdate(updateDtoToPreference(dto));
  }

  @Override
  public UserPreferenceVo preferenceDetail() {
    Preference preference = settingUserCmd.findAndInit(getUserId()).getPreference();
    return toPreferenceTo(preference);
  }

  @Override
  public void socialBindingUpdate(UserSocialUpdateDto dto) {
    settingUserCmd.socialBindingUpdate(dto.getType(), updateDtoToSocialBinding(dto));
  }

  @Override
  public void socialUnbind(SocialType type) {
    settingUserCmd.socialUnbind(type);
  }

  @Override
  public UserSocialBindingVo socialBindDetail() {
    SocialBinding binding = settingUserCmd.findAndInit(getUserId()).getSocialBind();
    return toSocialBindingVo(binding);
  }
}
