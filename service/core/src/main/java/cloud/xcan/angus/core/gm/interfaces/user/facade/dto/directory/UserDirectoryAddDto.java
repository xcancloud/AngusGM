package cloud.xcan.angus.core.gm.interfaces.user.facade.dto.directory;

import cloud.xcan.angus.core.gm.domain.user.directory.model.DirectoryGroupSchema;
import cloud.xcan.angus.core.gm.domain.user.directory.model.DirectoryMembershipSchema;
import cloud.xcan.angus.core.gm.domain.user.directory.model.DirectorySchema;
import cloud.xcan.angus.core.gm.domain.user.directory.model.DirectoryServer;
import cloud.xcan.angus.core.gm.domain.user.directory.model.DirectoryUserSchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class UserDirectoryAddDto implements Serializable {

  @Schema(description = "Sorting value, the synchronization priority is high if the value is small.")
  private Integer sequence;

  @Valid
  @NotNull
  @Schema(description = "Server Settings.", requiredMode = RequiredMode.REQUIRED)
  private DirectoryServer server;

  @Valid
  @NotNull
  @Schema(description = "LDAP Schema.", requiredMode = RequiredMode.REQUIRED)
  private DirectorySchema schema;

  // LDAP Permissions: @DoInFuture
  // - Read Only: Users, groups and memberships are retrieved from your LDAP server and cannot be modified in UC.
  // - Read Only, with Local Groups: Users, groups and memberships are retrieved from your LDAP server and cannot be modified in Confluence. Users from LDAP can be added to groups maintained in UC's internal directory.
  // - Read/Write: Modifying users, groups and memberships in UC will cause the changes to be applied directly to your LDAP server. Your configured LDAP user will need to have modification permissions on your LDAP server.

  // Advanced Settings: @DoInFuture
  // - Update group memberships when logging in, this ensures the group list is up to date, but can slow down authentication.
  //   - For newly added users only
  //   - Never
  //   - Every time the user logs in
  // - Synchronisation Interval (minutes): Time to wait between directory updates.
  // - Connection Timeout (seconds): Time to wait when opening new server connections, or getting a connection from the connection pool. Value of 0 means wait indefinitely for a pooled connection to become available, or to wait for the default TCP timeout to take effect when creating a new connection.
  // - Read Timeout (seconds): Time to wait for a response to be received. If there is no response within the specified time period, the read attempt will be aborted. Value of 0 means there is no limit.

  @Valid
  @NotNull
  @Schema(description = "LDAP user schema.", requiredMode = RequiredMode.REQUIRED)
  private DirectoryUserSchema userSchema;

  @Valid
  @Schema(description = "LDAP group schema.")
  private DirectoryGroupSchema groupSchema;

  @Valid
  @Schema(description = "LDAP membership schema.")
  private DirectoryMembershipSchema membershipSchema;

}
