package cloud.xcan.angus.api.commonlink.user;

import cloud.xcan.angus.core.biz.ResourceName;
import cloud.xcan.angus.core.jpa.multitenancy.TenantEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Note: user is mysql keyword. Renaming to avoid providing separate repo implementations for
 * postgres.
 */
@Entity
@Table(name = "user0")
@Setter
@Getter
@Accessors(chain = true)
public class UserBase extends TenantEntity<UserBase, Long> {

  @Id
  private Long id;

  private String username;

  @ResourceName
  private String fullname;

  private String mobile;

  private String email;

  private String avatar;

  private Boolean expired;

  private Boolean enabled;

  private Boolean deleted;

  private Boolean locked;

  public UserInfo toUserInfo() {
    return new UserInfo().setId(id).setFullname(fullname).setAvatar(avatar)
        .setEmail(email).setMobile(mobile);
  }

  @Override
  public Long identity() {
    return this.id;
  }
}
