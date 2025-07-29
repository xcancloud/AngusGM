package cloud.xcan.angus.core.gm.interfaces.group;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import cloud.xcan.angus.api.gm.group.dto.GroupFindDto;
import cloud.xcan.angus.api.gm.group.vo.GroupDetailVo;
import cloud.xcan.angus.api.gm.group.vo.GroupListVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.GroupFacade;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupAddDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupUpdateDto;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.remote.dto.EnabledOrDisabledDto;
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
import java.util.List;
import java.util.Set;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
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

@Tag(name = "Group", description = "REST API endpoints for organizational group management including creation, modification, and status control")
@Validated
@RestController
@RequestMapping("/api/v1/group")
public class GroupRest {

  @Resource
  private GroupFacade groupFacade;

  @Operation(summary = "Create multiple groups", operationId = "group:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Groups created successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<List<IdKey<Long, Object>>> add(
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestBody List<GroupAddDto> dto) {
    return ApiLocaleResult.success(groupFacade.add(dto));
  }

  @Operation(summary = "Update multiple groups", operationId = "group:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Groups updated successfully"),
      @ApiResponse(responseCode = "404", description = "Group not found")})
  @PatchMapping
  public ApiLocaleResult<?> update(
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestBody List<GroupUpdateDto> dto) {
    groupFacade.update(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Replace multiple groups", operationId = "group:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Groups replaced successfully")})
  @PutMapping
  public ApiLocaleResult<List<IdKey<Long, Object>>> replace(
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestBody List<GroupReplaceDto> dto) {
    return ApiLocaleResult.success(groupFacade.replace(dto));
  }

  @Operation(summary = "Delete multiple groups", operationId = "group:delete")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Groups deleted successfully")})
  @DeleteMapping
  public void delete(
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @RequestParam("ids") HashSet<Long> ids) {
    groupFacade.delete(ids);
  }

  @Operation(summary = "Enable or disable multiple groups", operationId = "group:enabled")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Groups enabled or disabled successfully"),
      @ApiResponse(responseCode = "404", description = "Group not found")
  })
  @PatchMapping("/enabled")
  public ApiLocaleResult<?> enabled(
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestBody Set<EnabledOrDisabledDto> dto) {
    groupFacade.enabled(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Retrieve detailed information about a specific group", operationId = "group:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Group details retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Group not found")})
  @GetMapping(value = "/{id}")
  public ApiLocaleResult<GroupDetailVo> detail(
      @Parameter(name = "id", description = "Group identifier", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(groupFacade.detail(id));
  }

  @Operation(summary = "Search and retrieve group list with pagination", operationId = "group:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Group list retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<PageResult<GroupListVo>> list(@Valid @ParameterObject GroupFindDto dto) {
    return ApiLocaleResult.success(groupFacade.list(dto));
  }

}
