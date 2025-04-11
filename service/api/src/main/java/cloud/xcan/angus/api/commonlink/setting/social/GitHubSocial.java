package cloud.xcan.angus.api.commonlink.setting.social;

import cloud.xcan.angus.spec.experimental.ValueObjectSupport;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GitHubSocial extends ValueObjectSupport<GitHubSocial> {

  private String appId;

  private String secret;

  private String callback;

  private String codeUrl;
  private String tokenUrl;
  private String userInfoUrl;

  @Override
  public GitHubSocial copy() {
    return new GitHubSocial(this.appId, this.secret, this.callback, this.codeUrl,
        this.tokenUrl, this.userInfoUrl);
  }

  public boolean check() {
    return StringUtils.isAnyBlank(appId, secret, callback, codeUrl, userInfoUrl);
  }
}
