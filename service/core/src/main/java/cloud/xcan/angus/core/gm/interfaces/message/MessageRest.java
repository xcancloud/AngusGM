package cloud.xcan.angus.core.gm.interfaces.message;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import cloud.xcan.angus.core.gm.interfaces.message.facade.MessageFacade;
import cloud.xcan.angus.core.gm.interfaces.message.facade.dto.MessageAddDto;
import cloud.xcan.angus.core.gm.interfaces.message.facade.dto.MessageFindDto;
import cloud.xcan.angus.core.gm.interfaces.message.facade.dto.MessageSearchDto;
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


@Tag(name = "Message", description = "Handles the lifecycle (send, delete, query) and targeted distribution of messages to diverse organizational entities")
@Validated
@RestController
@RequestMapping("/api/v1/message")
public class MessageRest {

  @Resource
  private MessageFacade messageFacade;

  @Operation(summary = "Add message", operationId = "message:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created successfully")})
  @PostMapping
  public ApiLocaleResult<IdKey<Long, Object>> add(@Valid @RequestBody MessageAddDto dto) {
    return ApiLocaleResult.success(messageFacade.add(dto));
  }

  @Operation(summary = "Delete messages", operationId = "message:delete")
  @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Deleted successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping
  public void delete(
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @RequestParam("ids") Set<Long> ids) {
    messageFacade.delete(ids);
  }

  @Operation(summary = "Query the detail of message", operationId = "message:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping("/{id}")
  public ApiLocaleResult<MessageDetailVo> detail(@PathVariable Long id) {
    return ApiLocaleResult.success(messageFacade.detail(id));
  }

  @Operation(summary = "Query the list of message", operationId = "message:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<PageResult<MessageVo>> list(@Valid @ParameterObject MessageFindDto dto) {
    return ApiLocaleResult.success(messageFacade.list(dto));
  }

  @Operation(summary = "Fulltext search the list of message", operationId = "message:search")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/search")
  public ApiLocaleResult<PageResult<MessageVo>> search(@Valid @ParameterObject MessageSearchDto dto) {
    return ApiLocaleResult.success(messageFacade.search(dto));
  }
}
