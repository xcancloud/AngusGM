package cloud.xcan.angus.core.gm.interfaces.user.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserSysAdminVo implements Serializable {

  private Long id;

  private String fullName;

  private String firstName;

  private String lastName;

  private String itc;

  private String country;

  private String mobile;

  private String email;

  @Schema(description = "The first administrator from register or add in the background")
  private Boolean firstSysAdmin;

  private LocalDateTime createdDate;

}
