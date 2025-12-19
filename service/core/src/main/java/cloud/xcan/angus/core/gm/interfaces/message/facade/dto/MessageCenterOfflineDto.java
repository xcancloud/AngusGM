package cloud.xcan.angus.core.gm.interfaces.message.facade.dto;

import static cloud.xcan.angus.api.commonlink.MessageCenterConstant.MAX_PUSH_OBJECT_NUM;

import cloud.xcan.angus.api.enums.ReceiveObjectType;
import cloud.xcan.angus.validator.EnumPart;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MessageCenterOfflineDto implements Serializable {

  @Schema(description = "Whether to broadcast offline command to other service instances", requiredMode = RequiredMode.REQUIRED)
  private boolean broadcast = true;

  @NotNull
  @EnumPart(enumClass = ReceiveObjectType.class, allowableValues = {"TENANT", "USER"})
  @Schema(description = "Target object type for forced offline operation", allowableValues = {
      "TENANT", "USER"})
  private ReceiveObjectType receiveObjectType;

  @Size(max = MAX_PUSH_OBJECT_NUM)
  @Schema(description = "Target object identifiers for forced offline")
  private List<Long> receiveObjectIds;

}
