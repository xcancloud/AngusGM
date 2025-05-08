package cloud.xcan.angus.core.gm.interfaces.app;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.core.gm.interfaces.app.facade.AppFacade;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.AppAddDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.AppExportDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.AppFindDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.AppImportDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.AppReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.AppSearchDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.AppSiteInfoUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.AppUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.AppDetailVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.AppVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.remote.dto.EnabledOrDisabledDto;
import cloud.xcan.angus.spec.annotations.OperationClient;
import cloud.xcan.angus.spec.experimental.IdKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@Tag(name = "App", description = "Manages application configurations and lifecycle processes.")
@Validated
@RestController
@RequestMapping("/api/v1/app")
public class AppRest {

  @Resource
  private AppFacade appFacade;

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(description = "Add application.", operationId = "app:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<IdKey<Long, Object>> add(@Valid @RequestBody AppAddDto dto) {
    return ApiLocaleResult.success(appFacade.add(dto));
  }

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(description = "Update application.", operationId = "app:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Updated successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")
  })
  @PatchMapping
  public ApiLocaleResult<?> update(@Valid @RequestBody AppUpdateDto dto) {
    appFacade.update(dto);
    return ApiLocaleResult.success();
  }

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(description = "Replace application.", operationId = "app:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Updated successfully")})
  @PutMapping
  public ApiLocaleResult<IdKey<Long, Object>> replace(@Valid AppReplaceDto dto) {
    return ApiLocaleResult.success(appFacade.replace(dto));
  }

  @Operation(description = "Update application site information.", operationId = "app:site:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @PatchMapping("/site")
  public ApiLocaleResult<?> siteUpdate(@Valid @RequestBody AppSiteInfoUpdateDto dto) {
    appFacade.siteUpdate(dto);
    return ApiLocaleResult.success();
  }

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(description = "Delete applications.", operationId = "app:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping
  public void delete(
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @RequestParam("ids") HashSet<Long> ids) {
    appFacade.delete(ids);
  }

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(description = "Enable or disable applications.", operationId = "app:enabled")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Enabled or disabled successfully")
  })
  @PatchMapping(value = "/enabled")
  public ApiLocaleResult<?> enabled(@Valid @RequestBody List<EnabledOrDisabledDto> dto) {
    appFacade.enabled(dto);
    return ApiLocaleResult.success();
  }

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(description = "Import application.", operationId = "app:import")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Import successfully")
  })
  @PostMapping(value = "/import")
  public ApiLocaleResult<?> importApp(@Valid AppImportDto dto,
      @RequestPart(value = "file", required = true) MultipartFile file) {
    appFacade.importApp(dto.getImportType(), file);
    return ApiLocaleResult.success();
  }

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(description = "Export application.", operationId = "app:export")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Export successfully")
  })
  @GetMapping(value = "/export")
  public void export(@Valid @ParameterObject AppExportDto dto, HttpServletResponse response) {
    appFacade.export(dto, response);
  }

  @Operation(description = "Query the detail of application.", operationId = "app:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/{id}")
  public ApiLocaleResult<AppDetailVo> detail(
      @Parameter(name = "id", description = "Application id", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(appFacade.detail(id));
  }

  @Operation(description = "Query the detail of application.", operationId = "app:detail:bycode")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/{code}/{editionType}")
  public ApiLocaleResult<AppDetailVo> detailByCode(
      @Parameter(name = "code", description = "Application code", required = true) @PathVariable("code") String code,
      @Parameter(name = "editionType", description = "Application edition type", required = true) @PathVariable("editionType") EditionType editionType) {
    return ApiLocaleResult.success(appFacade.detail(code, editionType));
  }

  @Operation(description = "Query the list of application.", operationId = "app:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<PageResult<AppVo>> list(@Valid @ParameterObject AppFindDto dto) {
    return ApiLocaleResult.success(appFacade.list(dto));
  }

  @Operation(description = "Fulltext search the list of application.", operationId = "app:search")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/search")
  public ApiLocaleResult<PageResult<AppVo>> search(@Valid @ParameterObject AppSearchDto dto) {
    return ApiLocaleResult.success(appFacade.search(dto));
  }

}
