package cloud.xcan.angus.core.gm.interfaces.event;

import cloud.xcan.angus.core.gm.domain.event.ReceiveChannelType;
import cloud.xcan.angus.core.gm.interfaces.event.facade.EventChannelFacade;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.channel.EventChannelAddDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.channel.EventChannelReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.channel.EventChannelTestDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.vo.channel.EventChannelVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "EventChannel", description = "Manage event notification channels such as Webhooks, WeChat robots, and DingTalk robots.")
@Validated
@RestController
@RequestMapping("/api/v1/event/channel")
public class EventChannelRest {

  @Resource
  private EventChannelFacade eventChannelFacade;

  @Operation(description = "Add receive event channel.", operationId = "event:channel:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Retrieved successfully")})
  @PostMapping
  public ApiLocaleResult<IdKey<Long, Object>> add(@Valid @RequestBody EventChannelAddDto dto) {
    return ApiLocaleResult.success(eventChannelFacade.add(dto));
  }

  @Operation(description = "Replace receive event channel.", operationId = "event:channel:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @PutMapping
  public ApiLocaleResult<IdKey<Long, Object>> replace(@RequestBody EventChannelReplaceDto dto) {
    return ApiLocaleResult.success(eventChannelFacade.replace(dto));
  }

  @Operation(description = "Delete receive event channel.", operationId = "event:channel:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Delete successfully")})
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(
      @Parameter(name = "id", description = "Event channel id", required = true) @PathVariable("id") Long id) {
    eventChannelFacade.delete(id);
  }

  @Operation(description = "Query the list of receive event channel.", operationId = "event:channel:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/channel/{channelType}")
  public ApiLocaleResult<List<EventChannelVo>> channelList(
      @Parameter(name = "channelType", description = "Receive channel type", required = true) @PathVariable("channelType") ReceiveChannelType channelType) {
    return ApiLocaleResult.success(eventChannelFacade.channelList(channelType));
  }

  @Operation(description = "Test receive event channel.", operationId = "event:channel:test")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Test successfully")})
  @PostMapping("/test")
  public ApiLocaleResult<?> channelTest(@Valid @RequestBody EventChannelTestDto dto) {
    eventChannelFacade.channelTest(dto);
    return ApiLocaleResult.success();
  }

}
