package cloud.xcan.angus.core.gm.interfaces.dept;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import cloud.xcan.angus.api.gm.dept.dto.DeptFindDto;
import cloud.xcan.angus.api.gm.dept.vo.DeptDetailVo;
import cloud.xcan.angus.api.gm.dept.vo.DeptListVo;
import cloud.xcan.angus.core.gm.domain.dept.DeptSubCount;
import cloud.xcan.angus.core.gm.interfaces.dept.facade.DeptFacade;
import cloud.xcan.angus.core.gm.interfaces.dept.facade.dto.DeptAddDto;
import cloud.xcan.angus.core.gm.interfaces.dept.facade.dto.DeptReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.dept.facade.dto.DeptUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.dept.facade.vo.DeptNavigationTreeVo;
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
import java.util.HashSet;
import java.util.List;
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

@Tag(name = "Department", description = "Manages department organizational unit creation, modification, and hierarchical structure maintenance, etc")
@Validated
@RestController
@RequestMapping("/api/v1/dept")
public class DeptRest {

  @Resource
  private DeptFacade deptFacade;

  @Operation(summary = "Add departments", operationId = "dept:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<List<IdKey<Long, Object>>> add(
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestBody List<DeptAddDto> dto) {
    return ApiLocaleResult.success(deptFacade.add(dto));
  }

  @Operation(summary = "Update departments", operationId = "dept:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Updated successfully")})
  @ApiResponse(responseCode = "404", description = "Resource not found")
  @PatchMapping
  public ApiLocaleResult<?> update(
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestBody List<DeptUpdateDto> dto) {
    deptFacade.update(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Replace departments", operationId = "dept:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully")})
  @PutMapping
  public ApiLocaleResult<List<IdKey<Long, Object>>> replace(
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestBody List<DeptReplaceDto> dto) {
    return ApiLocaleResult.success(deptFacade.replace(dto));
  }

  @Operation(summary = "Delete departments", operationId = "dept:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted successfully")})
  @DeleteMapping
  public ApiLocaleResult<?> delete(
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @RequestParam("ids")
      @Parameter(name = "ids", description = "Department ids", required = true) HashSet<Long> ids) {
    deptFacade.delete(ids);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Query the navigation position of department tree", operationId = "dept:navigation")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/{id}/navigation")
  public ApiLocaleResult<DeptNavigationTreeVo> navigation(
      @Parameter(name = "id", description = "Department id", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(deptFacade.navigation(id));
  }

  @Operation(summary = "Query the detail of department", operationId = "dept:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/{id}")
  public ApiLocaleResult<DeptDetailVo> detail(
      @Parameter(name = "id", description = "Department id", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(deptFacade.detail(id));
  }

  @Operation(summary = "Query the list of department", operationId = "dept:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<PageResult<DeptListVo>> list(@Valid @ParameterObject DeptFindDto dto) {
    return ApiLocaleResult.success(deptFacade.list(dto));
  }

  @Operation(summary = "Query the number of sub departments and department users number", operationId = "dept:sub:count")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/{id}/count")
  public ApiLocaleResult<DeptSubCount> subCount(
      @Parameter(name = "id", description = "Department id", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(deptFacade.subCount(id));
  }
}
