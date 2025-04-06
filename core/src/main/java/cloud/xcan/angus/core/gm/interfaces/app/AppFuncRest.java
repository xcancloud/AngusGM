package cloud.xcan.angus.core.gm.interfaces.app;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import cloud.xcan.angus.core.gm.interfaces.app.facade.AppFuncFacade;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.func.AppFuncAddDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.func.AppFuncFindDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.func.AppFuncReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.func.AppFuncUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.func.AppFuncDetailVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.func.AppFuncTreeVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.func.AppFuncVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.dto.EnabledOrDisabledDto;
import cloud.xcan.angus.spec.annotations.OperationClient;
import cloud.xcan.angus.spec.experimental.IdKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "AppFunc", description = "Organizes system function (menus, button, panel) and permission assignments.")
@Validated
@RestController
@RequestMapping("/api/v1/app")
public class AppFuncRest {

  @Resource
  private AppFuncFacade appFuncFacade;

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(description = "Add application functions, including menu, button, and panel.", operationId = "app:func:add")
  @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Created successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/{appId}/func")
  public ApiLocaleResult<List<IdKey<Long, Object>>> add(
      @Parameter(name = "appId", description = "Application id", required = true) @PathVariable("appId") Long appId,
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestBody List<AppFuncAddDto> dto) {
    return ApiLocaleResult.success(appFuncFacade.add(appId, dto));
  }

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(description = "Update application menu, button and panel functions.", operationId = "app:func:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Updated successfully"),
      @ApiResponse(responseCode = "404", description = "App function does not existed")})
  @PatchMapping("/{appId}/func")
  public ApiLocaleResult<?> update(
      @Parameter(name = "appId", description = "Application id", required = true) @PathVariable("appId") Long appId,
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestBody List<AppFuncUpdateDto> dto) {
    appFuncFacade.update(appId, dto);
    return ApiLocaleResult.success();
  }

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(description = "Replace application menu, button and panel functions.", operationId = "app:func:replace")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Replaced successfully")})
  @PutMapping("/{appId}/func")
  public ApiLocaleResult<?> replace(
      @Parameter(name = "appId", description = "Application id", required = true) @PathVariable("appId") Long appId,
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestBody List<AppFuncReplaceDto> dto) {
    appFuncFacade.replace(appId, dto);
    return ApiLocaleResult.success();
  }

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(description = "Delete the menu, button and panel functions of application.", operationId = "app:func:delete")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Deleted successfully")})
  @DeleteMapping(value = "/{appId}/func")
  public void delete(
      @Parameter(name = "appId", description = "Application id", required = true) @PathVariable("appId") Long appId,
      @Valid @RequestParam("ids") @Size(max = MAX_BATCH_SIZE) HashSet<Long> ids) {
    appFuncFacade.delete(appId, ids);
  }

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(description = "Enable or disable the menu, button and panel functions of application.", operationId = "app:func:enabled")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Enabled or disabled successfully")})
  @PatchMapping("/{appId}/func/enabled")
  public ApiLocaleResult<?> enabled(
      @Parameter(name = "appId", description = "Application id", required = true) @PathVariable("appId") Long appId,
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestBody List<EnabledOrDisabledDto> dto) {
    appFuncFacade.enabled(appId, dto);
    return ApiLocaleResult.success();
  }

  @Operation(description = "Query the menu, button or panel function detail of application.", operationId = "app:func:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "App function does not existed")})
  @GetMapping(value = "/func/{id}")
  public ApiLocaleResult<AppFuncDetailVo> detail(
      @Parameter(name = "id", description = "Function id", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(appFuncFacade.detail(id));
  }

  @Operation(description = "Query the menu, button and panel functions list of application.", operationId = "app:func:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/{appId}/func")
  public ApiLocaleResult<List<AppFuncVo>> list(
      @Parameter(name = "appId", description = "Application id", required = true) @PathVariable("appId") Long appId,
      @Valid AppFuncFindDto dto) {
    return ApiLocaleResult.success(appFuncFacade.list(appId, dto));
  }

  @Operation(description = "Fulltext search the menu, button and panel functions list of application.", operationId = "app:func:search")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/{appId}/func/search")
  public ApiLocaleResult<List<AppFuncVo>> search(
      @Parameter(name = "appId", description = "Application id", required = true) @PathVariable("appId") Long appId,
      @Valid AppFuncFindDto dto) {
    return ApiLocaleResult.success(appFuncFacade.search(appId, dto));
  }

  @Operation(description = "Query the menu, button and panel functions tree of application.", operationId = "app:func:tree")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/{appId}/func/tree")
  public ApiLocaleResult<List<AppFuncTreeVo>> tree(
      @Parameter(name = "appId", description = "Application id", required = true) @PathVariable("appId") Long appId,
      @Valid AppFuncFindDto dto) {
    return ApiLocaleResult.success(appFuncFacade.tree(appId, dto));
  }

  @Operation(description = "Fulltext search the menu, button and panel functions tree of application.", operationId = "app:func:tree:search")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/{appId}/func/tree/search")
  public ApiLocaleResult<List<AppFuncTreeVo>> treeSearch(
      @Parameter(name = "appId", description = "Application id", required = true) @PathVariable("appId") Long appId,
      @Valid AppFuncFindDto dto) {
    return ApiLocaleResult.success(appFuncFacade.treeSearch(appId, dto));
  }
}
