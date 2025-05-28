package cloud.xcan.angus.core.gm.interfaces.message;

import cloud.xcan.angus.api.gm.message.dto.MessageCenterPushDto;
import cloud.xcan.angus.core.gm.interfaces.message.facade.MessageCenterFacade;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@PreAuthorize("hasAuthority('SCOPE_inner_api_trust')")
@Tag(name = "MessageCenterInner", description = "Supports a unified inter-system calling method to send messages.")
@Validated
@RestController
@RequestMapping("/innerapi/v1/message/center")
public class MessageCenterDoorRest {

  @Resource
  private MessageCenterFacade messageCenterFacade;

  @Operation(summary = "Send messages.", operationId = "message:center:push:inner")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Successfully sent")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/push")
  public ApiLocaleResult<?> push(@RequestBody MessageCenterPushDto dto) {
    messageCenterFacade.push(dto);
    return ApiLocaleResult.success();
  }

}
