package cloud.xcan.angus.core.gm.interfaces.app;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_RELATION_QUOTA;

import cloud.xcan.angus.core.gm.interfaces.app.facade.AppFuncTagFacade;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.tag.AppTargetTagFindDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.tag.AppTagTargetVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.annotations.OperationClient;
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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "AppFuncTag", description = "Provides tags management operation of application function (including application, menus, button and panel).")
@Validated
@RestController
@RequestMapping("/api/v1/app/func")
public class AppFuncTagRest {

  @Resource
  private AppFuncTagFacade appFuncTagFacade;

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(summary = "Add the tags of application function.", operationId = "app:func:tag:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/{id}/tag")
  public ApiLocaleResult<List<IdKey<Long, Object>>> funcTagAdd(
      @Parameter(name = "id", description = "Function id", required = true) @PathVariable("id") Long funcId,
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestBody LinkedHashSet<Long> tagIds) {
    return ApiLocaleResult.success(appFuncTagFacade.funcTagAdd(funcId, tagIds));
  }

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(summary = "Replace the tags of application function.", operationId = "app:func:tag:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @PutMapping("/{id}/tag")
  public ApiLocaleResult<?> funcTagReplace(
      @Parameter(name = "id", description = "Function id", required = true) @PathVariable("id") Long funcId,
      @Valid @Size(max = MAX_RELATION_QUOTA) @RequestBody LinkedHashSet<Long> tagIds) {
    appFuncTagFacade.funcTagReplace(funcId, tagIds);
    return ApiLocaleResult.success();
  }

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(summary = "Delete the tags of application function.", operationId = "app:func:tag:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{id}/tag")
  public void funcTagDelete(
      @Parameter(name = "id", description = "Function id", required = true) @PathVariable("id") Long funcId,
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @RequestParam("tagIds") HashSet<Long> tagIds) {
    appFuncTagFacade.funcTagDelete(funcId, tagIds);
  }

  @Operation(summary = "Query the tags list of application function.", operationId = "app:func:tag:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/{id}/tag")
  public ApiLocaleResult<PageResult<AppTagTargetVo>> funcTagList(
      @Parameter(name = "id", description = "Function id", required = true) @PathVariable("id") Long funcId,
      @Valid @ParameterObject AppTargetTagFindDto dto) {
    return ApiLocaleResult.success(appFuncTagFacade.funcTagList(funcId, dto));
  }

}
