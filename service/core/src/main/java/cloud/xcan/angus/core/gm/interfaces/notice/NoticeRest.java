package cloud.xcan.angus.core.gm.interfaces.notice;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import cloud.xcan.angus.core.gm.interfaces.notice.facade.NoticeFacade;
import cloud.xcan.angus.core.gm.interfaces.notice.facade.dto.NoticeAddDto;
import cloud.xcan.angus.core.gm.interfaces.notice.facade.dto.NoticeFindDto;
import cloud.xcan.angus.core.gm.interfaces.notice.facade.vo.NoticeLatestVo;
import cloud.xcan.angus.core.gm.interfaces.notice.facade.vo.NoticeVo;
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
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
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


@Tag(name = "Notice", description = "REST API endpoints for application notification management and real-time message delivery")
@Validated
@RestController
@RequestMapping("/api/v1/notice")
public class NoticeRest {

  @Resource
  private NoticeFacade noticeFacade;

  @Operation(summary = "Create application notification", operationId = "notice:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Notification created successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<IdKey<Long, Object>> add(@Valid @RequestBody NoticeAddDto dto) {
    return ApiLocaleResult.success(noticeFacade.add(dto));
  }

  @Operation(summary = "Delete multiple application notifications", operationId = "notice:delete")
  @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Notifications deleted successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping
  public void delete(
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @RequestParam("ids") List<Long> ids) {
    noticeFacade.delete(ids);
  }

  @Operation(summary = "Retrieve detailed information about a specific notification", operationId = "notice:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Notification details retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Notification not found")})
  @GetMapping("/{id}")
  public ApiLocaleResult<NoticeVo> detail(
      @Parameter(name = "id", description = "Notification identifier", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(noticeFacade.detail(id));
  }

  @Operation(summary = "Retrieve the latest global notification", operationId = "notice:global:latest")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Latest global notification retrieved successfully")})
  @GetMapping("/global/latest")
  public ApiLocaleResult<NoticeLatestVo> globalLatest() {
    return ApiLocaleResult.success(noticeFacade.globalLatest());
  }

  @Operation(summary = "Retrieve the latest notification for a specific application", operationId = "notice:app:latest")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Latest application notification retrieved successfully")})
  @GetMapping("/app/{appId}/latest")
  public ApiLocaleResult<NoticeLatestVo> appLatest(
      @Parameter(name = "appId", description = "Application identifier", required = true) @PathVariable("appId") Long appId) {
    return ApiLocaleResult.success(noticeFacade.appLatest(appId));
  }

  @Operation(summary = "Search and retrieve application notifications with pagination", operationId = "notice:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Notification list retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<PageResult<NoticeVo>> list(@Valid @ParameterObject NoticeFindDto dto) {
    return ApiLocaleResult.success(noticeFacade.list(dto));
  }

}
