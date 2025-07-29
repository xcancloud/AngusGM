package cloud.xcan.angus.core.gm.interfaces.event;

import cloud.xcan.angus.core.gm.interfaces.event.facade.EventFacade;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.EventFindDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.vo.EventDetailVo;
import cloud.xcan.angus.core.gm.interfaces.event.facade.vo.EventReceiveChannelVo;
import cloud.xcan.angus.core.gm.interfaces.event.facade.vo.EventVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Event", description = "REST API endpoints for retrieving historical event records and associated channel configurations with filtering, sorting, and pagination")
@Validated
@RestController
@RequestMapping("/api/v1/event")
public class EventRest {

  @Resource
  private EventFacade eventFacade;

  @Operation(summary = "Retrieve detailed information about a specific event", operationId = "event:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Event details retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Event not found")})
  @GetMapping(value = "/{id}")
  public ApiLocaleResult<EventDetailVo> detail(
      @Parameter(name = "id", description = "Event identifier", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(eventFacade.detail(id));
  }

  @Operation(summary = "Retrieve notification channels for a specific event", operationId = "event:channel:receive:all")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Event channels retrieved successfully")})
  @GetMapping("/{eventCode}/channel")
  public ApiLocaleResult<List<EventReceiveChannelVo>> receiveChannel(
      @Parameter(name = "eventCode", description = "Event code", required = true) @PathVariable("eventCode") String eventCode) {
    return ApiLocaleResult.success(eventFacade.receiveChannel(eventCode));
  }

  @Operation(summary = "Search and retrieve event list with pagination", operationId = "event:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Event list retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<PageResult<EventVo>> list(@Valid @ParameterObject EventFindDto dto) {
    return ApiLocaleResult.success(eventFacade.list(dto));
  }

}
