package cloud.xcan.angus.core.gm.interfaces.app;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_RELATION_QUOTA;

import cloud.xcan.angus.core.gm.interfaces.app.facade.AppTagFacade;
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


@Tag(name = "App Tag", description = "Manages application categorization and metadata using tags")
@Validated
@RestController
@RequestMapping("/api/v1/app")
public class AppTagRest {

  @Resource
  private AppTagFacade appTagFacade;

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(summary = "Assign tags to application", operationId = "app:tag:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Tags assigned to application successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/{id}/tag")
  public ApiLocaleResult<List<IdKey<Long, Object>>> appTagAdd(
      @Parameter(name = "id", description = "Application identifier", required = true) @PathVariable("id") Long appId,
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestBody LinkedHashSet<Long> tagIds) {
    return ApiLocaleResult.success(appTagFacade.appTagAdd(appId, tagIds));
  }

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(summary = "Replace tags for application", operationId = "app:tag:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Tags replaced for application successfully"),
      @ApiResponse(responseCode = "404", description = "Application not found")})
  @PutMapping("/{id}/tag")
  public ApiLocaleResult<?> appTagReplace(
      @Parameter(name = "id", description = "Application identifier", required = true) @PathVariable("id") Long appId,
      @Valid @Size(max = MAX_RELATION_QUOTA) @RequestBody LinkedHashSet<Long> tagIds) {
    appTagFacade.appTagReplace(appId, tagIds);
    return ApiLocaleResult.success();
  }

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(summary = "Remove tags from application", operationId = "app:tag:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Tags removed from application successfully"),
      @ApiResponse(responseCode = "404", description = "Application not found")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{id}/tag")
  public void appTagDelete(
      @Parameter(name = "id", description = "Application identifier", required = true) @PathVariable("id") Long appId,
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @RequestParam("tagIds") HashSet<Long> tagIds) {
    appTagFacade.appTagDelete(appId, tagIds);
  }

  @Operation(summary = "Retrieve tags list for application", operationId = "app:tag:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Application tags list retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Application not found")})
  @GetMapping(value = "/{id}/tag")
  public ApiLocaleResult<PageResult<AppTagTargetVo>> appTagList(
      @Parameter(name = "id", description = "Application identifier", required = true) @PathVariable("id") Long appId,
      @Valid @ParameterObject AppTargetTagFindDto dto) {
    return ApiLocaleResult.success(appTagFacade.appTagList(appId, dto));
  }

}
