package cloud.xcan.angus.core.gm.interfaces.message;

import cloud.xcan.angus.core.gm.interfaces.message.facade.MessageCenterOnlineDoorFacade;
import cloud.xcan.angus.core.gm.interfaces.message.facade.dto.MessageCenterOfflineDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@PreAuthorize("hasAuthority('SCOPE_inner_api_trust')")
@Tag(name = "MessageCenterOnlineInner", description = "Supports inter-system calling method to forcibly logout users.")
@Validated
@RestController
@RequestMapping("/innerapi/v1/mcenter/online")
public class MessageCenterOnlineDoorRest {

  @Resource
  private MessageCenterOnlineDoorFacade messageCenterOnlineDoorFacade;

  @Operation(description = "Forced offline and logout users.", operationId = "mcenter:offline:inner")
  @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Successfully offline")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping
  public void offline(@Valid @RequestBody MessageCenterOfflineDto dto) {
    messageCenterOnlineDoorFacade.offline(dto);
  }

}
