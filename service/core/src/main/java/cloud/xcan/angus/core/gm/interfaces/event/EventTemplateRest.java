package cloud.xcan.angus.core.gm.interfaces.event;

import cloud.xcan.angus.core.gm.interfaces.event.facade.EventTemplateFacade;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.template.EventTemplateAddDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.template.EventTemplateChannelReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.template.EventTemplateFindDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.template.EventTemplateReceiverDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.template.EventTemplateReplaceDto;
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


@Tag(name = "Event Template", description = "REST API endpoints for managing event template configurations including predefined receiver settings and notification channels")
@Validated
@RestController
@RequestMapping("/api/v1/event/template")
public class EventTemplateRest {

  @Resource
  private EventTemplateFacade eventTemplateFacade;

  @Operation(summary = "Create new event template", operationId = "event:template:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Event template created successfully")})
  @PostMapping
  public ApiLocaleResult<IdKey<Long, Object>> add(@Valid @RequestBody EventTemplateAddDto dto) {
    return ApiLocaleResult.success(eventTemplateFacade.add(dto));
  }

  @Operation(summary = "Replace event template configuration", operationId = "event:template:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Event template replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Event template not found")})
  @PutMapping
  public ApiLocaleResult<IdKey<Long, Object>> replace(@RequestBody EventTemplateReplaceDto dto) {
    return ApiLocaleResult.success(eventTemplateFacade.replace(dto));
  }

  @Operation(summary = "Replace notification channels for event template",
      operationId = "event:template:channel:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Event template channels replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Event template not found")})
  @PutMapping("/channel")
  public ApiLocaleResult<?> channelReplace(@Valid @RequestBody EventTemplateChannelReplaceDto dto) {
    eventTemplateFacade.channelReplace(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Replace receivers for event template",
      operationId = "event:template:receiver:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Event template receivers replaced successfully")})
  @PutMapping("/receiver")
  public ApiLocaleResult<?> receiverReplace(@Valid @RequestBody EventTemplateReceiverDto dto) {
    eventTemplateFacade.receiverReplace(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Delete event template", operationId = "event:template:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Event template deleted successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{id}")
  public void delete(
      @Parameter(name = "id", description = "Event template identifier", required = true) @PathVariable("id") Long id) {
    eventTemplateFacade.delete(id);
  }

  @Operation(summary = "Retrieve detailed information about event template", operationId = "event:template:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Event template details retrieved successfully")})
  @GetMapping("/{id}")
  public ApiLocaleResult<EventTemplateVo> detail(
      @Parameter(name = "id", description = "Event template identifier", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(eventTemplateFacade.detail(id));
  }

  @Operation(summary = "Retrieve current tenant event template with receive settings",
      operationId = "event:template:current:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Current tenant event template retrieved successfully")})
  @GetMapping("/{id}/current")
  public ApiLocaleResult<EventTemplateCurrentDetailVo> currentDetail(
      @Parameter(name = "id", description = "Event template identifier", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(eventTemplateFacade.currentDetail(id));
  }

  @Operation(summary = "Search and retrieve event template list with pagination", operationId = "event:template:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Event template list retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<PageResult<EventTemplateVo>> list(
      @Valid @ParameterObject EventTemplateFindDto dto) {
    return ApiLocaleResult.success(eventTemplateFacade.list(dto));
  }

  @Operation(summary = "Search and retrieve current tenant event templates with receive settings",
      operationId = "event:template:current:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Current tenant event templates retrieved successfully")})
  @GetMapping("/current")
  public ApiLocaleResult<PageResult<EventTemplateCurrentDetailVo>> currentList(
      @Valid @ParameterObject EventTemplateFindDto dto) {
    return ApiLocaleResult.success(eventTemplateFacade.currentList(dto));
  }

}
