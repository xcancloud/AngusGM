package cloud.xcan.angus.core.gm.interfaces.setting.facade.internal.assembler;

import cloud.xcan.angus.api.commonlink.setting.user.apiproxy.UserApiProxy;
import cloud.xcan.angus.api.commonlink.setting.user.preference.Preference;
import cloud.xcan.angus.api.commonlink.setting.user.socialbinding.SocialBinding;
import cloud.xcan.angus.api.gm.setting.dto.UserSocialUpdateDto;
import cloud.xcan.angus.api.gm.setting.vo.UserApiProxyVo;
import cloud.xcan.angus.api.gm.setting.vo.UserPreferenceVo;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.dto.user.UserPreferenceUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.vo.user.UserSocialBindingVo;


public class SettingUserAssembler {

  public static Preference updateDtoToPreference(UserPreferenceUpdateDto dto) {
    return new Preference().setLanguage(dto.getLanguage());
  }

  public static UserPreferenceVo toPreferenceTo(Preference preference) {
    return new UserPreferenceVo().setLanguage(preference.getLanguage())
        .setThemeCode(preference.getThemeCode())
        .setDefaultTimeZone(preference.getDefaultTimeZone());
  }

  public static UserApiProxyVo toApiProxyVo(UserApiProxy apiProxy) {
    return new UserApiProxyVo().setNoProxy(apiProxy.getNoProxy())
        .setClientProxy(apiProxy.getClientProxy())
        .setServerProxy(apiProxy.getServerProxy())
        .setCloudProxy(apiProxy.getCloudProxy());
  }

  public static SocialBinding updateDtoToSocialBinding(UserSocialUpdateDto dto) {
    return new SocialBinding().setGithubUserId(dto.getGithubUserId())
        .setWechatUserId(dto.getWechatUserId())
        .setGithubUserId(dto.getGithubUserId());
  }

  public static UserSocialBindingVo toSocialBindingVo(SocialBinding binding) {
    return new UserSocialBindingVo()
        .setGithubUserId(binding.getGithubUserId())
        .setGithubUserBindDate(binding.getGithubUserBindDate())
        .setWechatUserId(binding.getWechatUserId())
        .setWechatUserBindDate(binding.getWechatUserBindDate())
        .setGithubUserId(binding.getGithubUserId())
        .setGoogleUserBindDate(binding.getGoogleUserBindDate());
  }
}
