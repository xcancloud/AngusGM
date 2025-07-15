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


@Tag(name = "Notice", description =
    "Application (or system) notice management, used for delivers real-time push and updates across application "
        + "to inform users of critical events, required actions, or information changes")
@Validated
@RestController
@RequestMapping("/api/v1/notice")
public class NoticeRest {

  @Resource
  private NoticeFacade noticeFacade;

  @Operation(summary = "Add application notice", operationId = "notice:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created successfully")})
  @PostMapping
  public ApiLocaleResult<IdKey<Long, Object>> add(@Valid @RequestBody NoticeAddDto dto) {
    return ApiLocaleResult.success(noticeFacade.add(dto));
  }

  @Operation(summary = "Delete application notice", operationId = "notice:delete")
  @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Deleted successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping
  public void delete(
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @RequestParam("ids") List<Long> ids) {
    noticeFacade.delete(ids);
  }

  @Operation(summary = "Query the detail of application notice", operationId = "notice:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping("/{id}")
  public ApiLocaleResult<NoticeVo> detail(
      @Parameter(name = "id", description = "Notice id", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(noticeFacade.detail(id));
  }

  @Operation(summary = "Query the list of application notice", operationId = "notice:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<PageResult<NoticeVo>> list(@Valid @ParameterObject NoticeFindDto dto) {
    return ApiLocaleResult.success(noticeFacade.list(dto));
  }

  @Operation(summary = "Fulltext search the list of application notice", operationId = "notice:search")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/search")
  public ApiLocaleResult<PageResult<NoticeVo>> search(@Valid @ParameterObject NoticeFindDto dto) {
    return ApiLocaleResult.success(noticeFacade.search(dto));
  }

  @Operation(summary = "Query the latest global application notice", operationId = "notice:global:latest")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/global/latest")
  public ApiLocaleResult<NoticeLatestVo> globalLatest() {
    return ApiLocaleResult.success(noticeFacade.globalLatest());
  }

  @Operation(summary = "Query the latest application notice", operationId = "notice:app:latest")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/app/{appId}/latest")
  public ApiLocaleResult<NoticeLatestVo> appLatest(
      @Parameter(name = "appId", description = "App id", required = true) @PathVariable("appId") Long appId) {
    return ApiLocaleResult.success(noticeFacade.appLatest(appId));
  }

}
