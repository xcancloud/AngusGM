package cloud.xcan.angus.core.gm.interfaces.setting.facade.to.setting.social;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;
import static org.apache.commons.lang3.StringUtils.isAnyBlank;

import cloud.xcan.angus.api.commonlink.setting.social.GitHubSocial;
import cloud.xcan.angus.api.commonlink.setting.social.GoogleSocial;
import cloud.xcan.angus.api.commonlink.setting.social.WeChatSocial;
import cloud.xcan.angus.spec.experimental.ValueObjectSupport;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class SocialTo extends ValueObjectSupport<SocialTo> {

  private WeChatSocial weChatSocial;

  private GitHubSocial gitHubSocial;

  private GoogleSocial googleSocial;

  private String loginRedirectUrl;

  private String loginBindRedirectUrl;

  private String bindRedirectUrl;

  public boolean check() {
    return isAnyBlank(loginRedirectUrl, loginBindRedirectUrl, bindRedirectUrl)
        || isNull(weChatSocial) || isNull(gitHubSocial)
        || weChatSocial.check() || gitHubSocial.check();
  }
}
