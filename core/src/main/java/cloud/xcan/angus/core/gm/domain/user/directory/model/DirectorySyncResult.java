package cloud.xcan.angus.core.gm.domain.user.directory.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class DirectorySyncResult implements Serializable {

  @Schema(description = "Synchronization success flag")
  private Boolean success;

  @Schema(description = "Connect server success flag")
  private Boolean connectSuccess;

  @Schema(description = "User synchronization success flag")
  private Boolean userSuccess;

  @Schema(description = "Group synchronization success flag")
  private Boolean groupSuccess;

  @Schema(description = "Membership synchronization success flag")
  private Boolean membershipSuccess;

  @Schema(description = "Reason for synchronization failure")
  private String errorMessage;

  @Schema(description = "Number of total users in the directory")
  private int totalUserNum;

  @Schema(description = "Number of new users after synchronization")
  private int addUserNum;

  @Schema(description = "Modify the number of users after synchronization")
  private int updateUserNum;

  @Schema(description = "Delete the number of users that do not exist in the current directory after synchronization")
  private int deleteUserNum;

  @Schema(description = "Ignore the number of users that already exist in other directories after synchronization")
  private int ignoreUserNum;

  @Schema(description = "Number of total groups in the directory")
  private int totalGroupNum;

  @Schema(description = "Number of new groups after synchronization")
  private int addGroupNum;

  @Schema(description = "Modify the number of groups after synchronization")
  private int updateGroupNum;

  @Schema(description = "Delete the number of groups that do not exist in the current directory after synchronization")
  private int deleteGroupNum;

  @Schema(description = "Ignore the number of groups that already exist in other directories after synchronization")
  private int ignoreGroupNum;

  @Schema(description = "Number of new user group membership after synchronization")
  private int addMembershipNum;

  @Schema(description = "Delete the number of new user group membership after synchronization", hidden = true /*The delete statement does not return the deleted quantity*/)
  private int deleteMembershipNum;

  @Override
  public String toString() {
    return "DirectorySyncResult{" +
        "success=" + success +
        ", connectSuccess=" + connectSuccess +
        ", userSuccess=" + userSuccess +
        ", groupSuccess=" + groupSuccess +
        ", membershipSuccess=" + membershipSuccess +
        ", errorMessage='" + errorMessage + '\'' +
        ", addUserNum=" + addUserNum +
        ", updateUserNum=" + updateUserNum +
        ", deleteUserNum=" + deleteUserNum +
        ", ignoreUserNum=" + ignoreUserNum +
        ", addGroupNum=" + addGroupNum +
        ", updateGroupNum=" + updateGroupNum +
        ", deleteGroupNum=" + deleteGroupNum +
        ", ignoreGroupNum=" + ignoreGroupNum +
        ", addMembershipNum=" + addMembershipNum +
        ", deleteMembershipNum=" + deleteMembershipNum +
        '}';
  }
}
