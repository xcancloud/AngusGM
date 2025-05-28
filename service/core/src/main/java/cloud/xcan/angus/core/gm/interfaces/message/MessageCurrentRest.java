package cloud.xcan.angus.core.gm.interfaces.message;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import cloud.xcan.angus.core.gm.interfaces.message.facade.MessageCurrentFacade;
import cloud.xcan.angus.core.gm.interfaces.message.facade.dto.MessageCurrentFindDto;
import cloud.xcan.angus.core.gm.interfaces.message.facade.vo.MessageCurrentDetailVo;
import cloud.xcan.angus.core.gm.interfaces.message.facade.vo.MessageCurrentVo;
import cloud.xcan.angus.core.gm.interfaces.message.facade.vo.MessageStatusCountVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "MessageCurrent", description = "Manages current individual user interactions with messages, including deletion, read status, and retrieval.")
@Validated
@RestController
@RequestMapping("/api/v1/message")
public class MessageCurrentRest {

  @Resource
  private MessageCurrentFacade messageCurrentFacade;

  @Operation(summary = "Delete the messages of current user.", operationId = "message:delete:current")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/current")
  public void delete(
      @RequestParam("ids") @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) Set<Long> ids) {
    messageCurrentFacade.delete(ids);
  }

  @Operation(summary = "Submit the read status of message.", operationId = "message:read:current")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Submit successfully")})
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/read/current")
  public ApiLocaleResult<?> read(
      @RequestParam("ids") @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) Set<Long> ids) {
    messageCurrentFacade.read(ids);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Query the message information of current user.", operationId = "message:detail:current")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping("/{id}/current")
  public ApiLocaleResult<MessageCurrentDetailVo> detail(@PathVariable Long id) {
    return ApiLocaleResult.success(messageCurrentFacade.detail(id));
  }

  @Operation(summary = "Query the message list of current user.", operationId = "message:list:current")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/current")
  public ApiLocaleResult<PageResult<MessageCurrentVo>> list(@Valid MessageCurrentFindDto dto) {
    return ApiLocaleResult.success(messageCurrentFacade.list(dto));
  }

  @Operation(summary = "Message status count.", operationId = "message:status:count")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/status/count")
  public ApiLocaleResult<List<MessageStatusCountVo>> statusCount(@Valid Long userid) {
    return ApiLocaleResult.success(messageCurrentFacade.statusCount(userid));
  }
}
