package cloud.xcan.angus.core.gm.interfaces.message;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import cloud.xcan.angus.core.gm.interfaces.message.facade.MessageFacade;
import cloud.xcan.angus.core.gm.interfaces.message.facade.dto.MessageAddDto;
import cloud.xcan.angus.core.gm.interfaces.message.facade.dto.MessageFindDto;
import cloud.xcan.angus.core.gm.interfaces.message.facade.vo.MessageDetailVo;
import cloud.xcan.angus.core.gm.interfaces.message.facade.vo.MessageVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.Set;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Message", description = "REST API endpoints for message lifecycle management and targeted distribution to diverse organizational entities")
@Validated
@RestController
@RequestMapping("/api/v1/message")
public class MessageRest {

  @Resource
  private MessageFacade messageFacade;

  @Operation(summary = "Create new message", operationId = "message:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Message created successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<IdKey<Long, Object>> add(@Valid @RequestBody MessageAddDto dto) {
    return ApiLocaleResult.success(messageFacade.add(dto));
  }

  @Operation(summary = "Delete multiple messages", operationId = "message:delete")
  @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Messages deleted successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping
  public void delete(
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @RequestParam("ids") Set<Long> ids) {
    messageFacade.delete(ids);
  }

  @Operation(summary = "Retrieve detailed information about a specific message", operationId = "message:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Message details retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Message not found")})
  @GetMapping("/{id}")
  public ApiLocaleResult<MessageDetailVo> detail(@PathVariable Long id) {
    return ApiLocaleResult.success(messageFacade.detail(id));
  }

  @Operation(summary = "Search and retrieve message list with pagination", operationId = "message:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Message list retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<PageResult<MessageVo>> list(@Valid @ParameterObject MessageFindDto dto) {
    return ApiLocaleResult.success(messageFacade.list(dto));
  }

}
