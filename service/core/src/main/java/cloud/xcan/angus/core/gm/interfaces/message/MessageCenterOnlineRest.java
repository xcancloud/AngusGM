package cloud.xcan.angus.core.gm.interfaces.message;

import cloud.xcan.angus.core.gm.interfaces.message.facade.MessageCenterOnlineFacade;
import cloud.xcan.angus.core.gm.interfaces.message.facade.dto.MessageCenterOfflineDto;
import cloud.xcan.angus.core.gm.interfaces.message.facade.dto.MessageCenterOnlineFindDto;
import cloud.xcan.angus.core.gm.interfaces.message.facade.vo.MessageCenterOnlineVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "MessageCenterOnline", description = "Monitors real-time user online statuses, such as querying online users or forced logouts")
@Validated
@RestController
@RequestMapping("/api/v1/message/center/online")
public class MessageCenterOnlineRest {

  @Resource
  private MessageCenterOnlineFacade messageCenterOnlineFacade;

  @Operation(summary = "Forced offline and logout users", operationId = "message:center:offline")
  @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Successfully offline")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PostMapping("/off")
  public ApiLocaleResult<?> offline(@Valid @RequestBody MessageCenterOfflineDto dto) {
    messageCenterOnlineFacade.offline(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Query the online user information of the message center", operationId = "message:center:online:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Not found resource")})
  @GetMapping(value = "/{userId}")
  public ApiLocaleResult<MessageCenterOnlineVo> detail(
      @Parameter(name = "userId", description = "User ID", required = true) @PathVariable("userId") Long userId) {
    return ApiLocaleResult.success(messageCenterOnlineFacade.detail(userId));
  }

  @Operation(summary = "Query the online user information of the message center", operationId = "message:center:online:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<PageResult<MessageCenterOnlineVo>> list(
      @Valid @ParameterObject MessageCenterOnlineFindDto dto) {
    return ApiLocaleResult.success(messageCenterOnlineFacade.list(dto));
  }


}
