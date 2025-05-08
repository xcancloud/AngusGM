package cloud.xcan.angus.core.gm.interfaces.user;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_RELATION_QUOTA;

import cloud.xcan.angus.api.gm.user.to.UserDeptTo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.UserDeptFacade;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.dept.UserDeptFindDto;
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


@Tag(name = "UserDept", description = "Controls user-to-department membership mappings to regulate "
    + "user visibility and access privileges based on organizational departments.")
@Validated
@RestController
@RequestMapping("/api/v1/user")
public class UserDeptRest {

  @Resource
  private UserDeptFacade userDeptFacade;

  @Operation(description = "Add the departments of user.", operationId = "user:dept:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/{id}/dept")
  public ApiLocaleResult<List<IdKey<Long, Object>>> deptAdd(
      @Parameter(name = "id", description = "User id", required = true) @PathVariable("id") Long userId,
      @Valid @NotEmpty @Size(max = MAX_RELATION_QUOTA) @Parameter(name = "deptIds", description = "Department ids")
      @RequestBody LinkedHashSet<Long> deptIds) {
    return ApiLocaleResult.success(userDeptFacade.deptAdd(userId, deptIds));
  }

  @Operation(description = "Replace the departments of user.", operationId = "user:dept:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @PutMapping("/{id}/dept")
  public ApiLocaleResult<?> deptReplace(
      @Parameter(name = "id", description = "User id", required = true) @PathVariable("id") Long userId,
      @Valid @Size(max = MAX_RELATION_QUOTA) @RequestBody LinkedHashSet<UserDeptTo> dto) {
    userDeptFacade.deptReplace(userId, dto);
    return ApiLocaleResult.success();
  }

  @Operation(description = "Remove the departments of user.", operationId = "user:dept:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{id}/dept")
  public void delete(
      @Parameter(name = "id", description = "User id", required = true) @PathVariable("id") Long userId,
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @Parameter(name = "deptIds", description = "Department ids", required = true)
      @RequestParam("deptIds") HashSet<Long> deptIds) {
    userDeptFacade.deptDelete(userId, deptIds);
  }

  @Operation(description = "Query the departments list of user.", operationId = "user:dept:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/{id}/dept")
  public ApiLocaleResult<PageResult<UserDeptVo>> deptList(
      @Parameter(name = "id", description = "User id", required = true) @PathVariable("id") Long userId,
      @Valid @ParameterObject UserDeptFindDto dto) {
    return ApiLocaleResult.success(userDeptFacade.deptList(userId, dto));
  }

}
