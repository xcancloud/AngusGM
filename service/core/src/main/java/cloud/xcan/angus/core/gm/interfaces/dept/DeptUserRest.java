package cloud.xcan.angus.core.gm.interfaces.dept;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_RELATION_QUOTA;

import cloud.xcan.angus.core.gm.interfaces.dept.facade.DeptUserFacade;
import cloud.xcan.angus.core.gm.interfaces.dept.facade.dto.DeptHeadReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.dept.facade.dto.user.DeptUserFindDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.dept.UserDeptVo;
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
import java.util.LinkedHashSet;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Department User", description = "REST API endpoints for managing department-to-user membership mappings to regulate user visibility and access privileges based on organizational departments")
@Validated
@RestController
@RequestMapping("/api/v1/dept")
public class DeptUserRest {

  @Resource
  private DeptUserFacade deptUserFacade;

  @Operation(summary = "Assign users to department", operationId = "dept:user:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Users assigned to department successfully"),
      @ApiResponse(responseCode = "404", description = "Department not found")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/{id}/user")
  public ApiLocaleResult<List<IdKey<Long, Object>>> userAdd(
      @Parameter(name = "id", description = "Department identifier", required = true) @PathVariable("id") Long deptId,
      @Valid @NotEmpty @Size(max = MAX_RELATION_QUOTA) @Parameter(name = "ids", description = "User identifiers", required = true)
      @RequestBody LinkedHashSet<Long> userIds) {
    return ApiLocaleResult.success(deptUserFacade.userAdd(deptId, userIds));
  }

  @Operation(summary = "Remove users from department", operationId = "dept:user:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Users removed from department successfully"),
      @ApiResponse(responseCode = "404", description = "Department not found")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{id}/user")
  public void delete(
      @Parameter(name = "id", description = "Department identifier", required = true) @PathVariable("id") Long deptId,
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @Parameter(name = "ids", description = "User identifiers", required = true)
      @RequestParam("ids") HashSet<Long> userIds) {
    deptUserFacade.userDelete(deptId, userIds);
  }

  @Operation(summary = "Replace department head", description = "There can only be one department head", operationId = "dept:head:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Department head replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Department not found")})
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/{id}/user/head")
  public ApiLocaleResult<?> headReplace(
      @Parameter(name = "id", description = "Department identifier", required = true) @PathVariable("id") Long deptId,
      @Valid @RequestBody DeptHeadReplaceDto dto) {
    deptUserFacade.headReplace(deptId, dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Retrieve department user list with filtering and pagination", operationId = "dept:user:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Department user list retrieved successfully")})
  @ResponseStatus(HttpStatus.OK)
  @GetMapping(value = "/{id}/user")
  public ApiLocaleResult<PageResult<UserDeptVo>> deptUserList(
      @Parameter(name = "id", description = "Department identifier", required = true) @PathVariable("id") Long deptId,
      @Valid @ParameterObject DeptUserFindDto dto) {
    return ApiLocaleResult.success(deptUserFacade.deptUserList(deptId, dto));
  }
}
