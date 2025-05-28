package cloud.xcan.angus.core.gm.interfaces.event;

import cloud.xcan.angus.core.gm.interfaces.event.facade.EventTemplateFacade;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.template.EventTemplateAddDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.template.EventTemplateChannelReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.template.EventTemplateFindDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.template.EventTemplateReceiverDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.template.EventTemplateReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.template.EventTemplateSearchDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.vo.template.EventTemplateCurrentDetailVo;
import cloud.xcan.angus.core.gm.interfaces.event.facade.vo.template.EventTemplateVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "EventTemplate", description = "This category handles event template configurations, "
    + "including predefined settings for receivers, notification channels.")
@Validated
@RestController
@RequestMapping("/api/v1/event/template")
public class EventTemplateRest {

  @Resource
  private EventTemplateFacade eventTemplateFacade;

  @Operation(summary = "Add event template.", operationId = "event:template:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Created successfully")})
  @PostMapping
  public ApiLocaleResult<IdKey<Long, Object>> add(@Valid @RequestBody EventTemplateAddDto dto) {
    return ApiLocaleResult.success(eventTemplateFacade.add(dto));
  }

  @Operation(summary = "Replace event template.", operationId = "event:template:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @PutMapping
  public ApiLocaleResult<IdKey<Long, Object>> replace(@RequestBody EventTemplateReplaceDto dto) {
    return ApiLocaleResult.success(eventTemplateFacade.replace(dto));
  }

  @Operation(summary = "Replace the receive channel of event template.", operationId = "event:template:channel:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @PutMapping("/channel")
  public ApiLocaleResult<?> channelReplace(@Valid @RequestBody EventTemplateChannelReplaceDto dto) {
    eventTemplateFacade.channelReplace(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Replace the receiver of event template.", operationId = "event:template:receiver:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully")})
  @PutMapping("/receiver")
  public ApiLocaleResult<?> receiverReplace(@Valid @RequestBody EventTemplateReceiverDto dto) {
    eventTemplateFacade.receiverReplace(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Delete event template.", operationId = "event:template:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{id}")
  public void delete(
      @Parameter(name = "id", description = "Template id", required = true) @PathVariable("id") Long id) {
    eventTemplateFacade.delete(id);
  }

  @Operation(summary = "Query the detail of event template.", operationId = "event:template:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/{id}")
  public ApiLocaleResult<EventTemplateVo> detail(
      @Parameter(name = "id", description = "Template id", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(eventTemplateFacade.detail(id));
  }

  @Operation(summary = "Query the detail of current tenant event template and receive setting.", operationId = "event:template:current:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/{id}/current")
  public ApiLocaleResult<EventTemplateCurrentDetailVo> currentDetail(
      @Parameter(name = "id", description = "Template id", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(eventTemplateFacade.currentDetail(id));
  }

  @Operation(summary = "Query the list of event template.", operationId = "event:template:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<PageResult<EventTemplateVo>> list(@Valid @ParameterObject EventTemplateFindDto dto) {
    return ApiLocaleResult.success(eventTemplateFacade.list(dto));
  }

  @Operation(summary = "Query the list of current tenant event template and receive setting.", operationId = "event:template:current:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/current")
  public ApiLocaleResult<PageResult<EventTemplateCurrentDetailVo>> currentList(
      @Valid @ParameterObject EventTemplateFindDto dto) {
    return ApiLocaleResult.success(eventTemplateFacade.currentList(dto));
  }

  @Operation(summary = "Fulltext search the list of event template.", operationId = "event:template:search")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/search")
  public ApiLocaleResult<PageResult<EventTemplateVo>> search(@Valid @ParameterObject EventTemplateSearchDto dto) {
    return ApiLocaleResult.success(eventTemplateFacade.search(dto));
  }

  @Operation(summary = "Fulltext search the list of current tenant event template and receive setting.", operationId = "event:template:current:search")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/search/current")
  public ApiLocaleResult<PageResult<EventTemplateCurrentDetailVo>> currentSearch(
      @Valid @ParameterObject EventTemplateSearchDto dto) {
    return ApiLocaleResult.success(eventTemplateFacade.currentSearch(dto));
  }

}
