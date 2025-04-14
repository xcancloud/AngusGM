package cloud.xcan.angus.api.gm.message;

import cloud.xcan.angus.api.gm.message.dto.MessageCenterPushDto;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@FeignClient(name = "${xcan.service.gm:XCAN-ANGUSGM.BOOT}")
public interface MessageCenterDoorRemote {

  @Operation(description = "Send messages", operationId = "message:center:push")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Successfully sent")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/innerapi/v1/message/center/push")
  ApiLocaleResult<?> send(@RequestBody MessageCenterPushDto message);

}
