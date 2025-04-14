package cloud.xcan.angus.core.gm.interfaces.message;

import cloud.xcan.angus.api.gm.message.dto.MessageCenterPushDto;
import cloud.xcan.angus.core.gm.interfaces.message.facade.MessageCenterFacade;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "MessageCenter", description = "Message center unified message dispatch. "
    + "Enables centralized message distribution across multiple users from a single api.")
@Validated
@RestController
@RequestMapping("/api/v1/message/center")
public class MessageCenterRest {

  @Resource
  private MessageCenterFacade messageCenterFacade;

  @Operation(description = "Send the messages of message center.", operationId = "message:center:push")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Successfully sent")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/push")
  public ApiLocaleResult<?> push(@Valid @RequestBody MessageCenterPushDto dto) {
    messageCenterFacade.push(dto);
    return ApiLocaleResult.success();
  }

}
