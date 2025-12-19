package cloud.xcan.angus.core.gm.interfaces.user.facade.vo.directory;

import cloud.xcan.angus.core.gm.domain.user.directory.model.DirectoryGroupSchema;
import cloud.xcan.angus.core.gm.domain.user.directory.model.DirectoryMembershipSchema;
import cloud.xcan.angus.core.gm.domain.user.directory.model.DirectorySchema;
import cloud.xcan.angus.core.gm.domain.user.directory.model.DirectoryServer;
import cloud.xcan.angus.core.gm.domain.user.directory.model.DirectoryUserSchema;
import cloud.xcan.angus.remote.NameJoinField;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class UserDirectoryDetailVo implements Serializable {

  private Long id;

  private String name;

  private Integer sequence;

  private Boolean enabled;

  private DirectoryServer server;

  private DirectorySchema schema;

  private DirectoryUserSchema userSchema;

  private DirectoryGroupSchema groupSchema;

  private DirectoryMembershipSchema membershipSchema;

  private Long createdBy;

  @NameJoinField(id = "createdBy", repository = "commonUserBaseRepo")
  private String creator;

  private LocalDateTime createdDate;

  private Long modifiedBy;

  @NameJoinField(id = "modifiedBy", repository = "commonUserBaseRepo")
  private String modifier;

  private LocalDateTime modifiedDate;

}
