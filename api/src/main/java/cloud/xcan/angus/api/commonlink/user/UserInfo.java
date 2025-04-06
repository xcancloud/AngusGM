package cloud.xcan.angus.api.commonlink.user;

import java.io.Serializable;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Used by angustester
 */
@Setter
@Getter
@Accessors(chain = true)
public class UserInfo implements Serializable {

  private Long id;

  private String username;

  private String fullname;

  private String mobile;

  private String email;

  private String avatar;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof UserInfo userInfo)) {
      return false;
    }
    return Objects.equals(id, userInfo.id)
        && Objects.equals(username, userInfo.username)
        && Objects.equals(fullname, userInfo.fullname)
        && Objects.equals(mobile, userInfo.mobile)
        && Objects.equals(email, userInfo.email)
        && Objects.equals(avatar, userInfo.avatar);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, username, fullname, mobile, email, avatar);
  }
}
