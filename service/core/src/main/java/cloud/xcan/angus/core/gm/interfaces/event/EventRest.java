package cloud.xcan.angus.core.gm.interfaces.event;

import cloud.xcan.angus.core.gm.interfaces.event.facade.EventFacade;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.EventFindDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.EventSearchDto;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Event", description =
    "enable retrieval of historical event records and associated channel configurations. Users can filter, sort, "
        + "and paginate event logs, while also querying configured event channels to audit event routing and processing workflows.")
@Validated
@RestController
@RequestMapping("/api/v1/event")
public class EventRest {

  @Resource
  private EventFacade eventFacade;

  @Operation(description = "Query the detail of event.", operationId = "event:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/{id}")
  public ApiLocaleResult<EventDetailVo> detail(
      @Parameter(name = "id", description = "Event id", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(eventFacade.detail(id));
  }

  @Operation(description = "Query the receive channels of event.", operationId = "event:channel:receive:all")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/{eventCode}/channel")
  public ApiLocaleResult<List<EventReceiveChannelVo>> receiveChannel(
      @Parameter(name = "eventCode", description = "Event code", required = true) @PathVariable("eventCode") String eventCode) {
    return ApiLocaleResult.success(eventFacade.receiveChannel(eventCode));
  }

  @Operation(description = "Query the list of event.", operationId = "event:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<PageResult<EventVo>> list(@Valid EventFindDto dto) {
    return ApiLocaleResult.success(eventFacade.list(dto));
  }

  @Operation(description = "Fulltext search the list of event.", operationId = "event:search")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/search")
  public ApiLocaleResult<PageResult<EventVo>> search(@Valid EventSearchDto dto) {
    return ApiLocaleResult.success(eventFacade.search(dto));
  }

}
