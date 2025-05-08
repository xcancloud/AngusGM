package cloud.xcan.angus.core.gm.interfaces.user;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_RELATION_QUOTA;

import cloud.xcan.angus.core.gm.interfaces.user.facade.UserGroupFacade;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.group.UserGroupFindDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.group.UserGroupVo;
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


@Tag(name = "UserGroup", description = "Controls user-to-group membership mappings to regulate "
    + "user visibility and access privileges based on organizational groups.")
@Validated
@RestController
@RequestMapping("/api/v1/user")
public class UserGroupRest {

  @Resource
  private UserGroupFacade userGroupFacade;

  @Operation(description = "Add the user to groups.", operationId = "user:group:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/{id}/group")
  public ApiLocaleResult<List<IdKey<Long, Object>>> groupAppend(
      @Parameter(name = "id", description = "User id", required = true) @PathVariable("id") Long userId,
      @Valid @NotEmpty @Size(max = MAX_RELATION_QUOTA) @Parameter(name = "ids", description = "Group ids")
      @RequestBody LinkedHashSet<Long> groupIds) {
    return ApiLocaleResult.success(userGroupFacade.groupAdd(userId, groupIds));
  }

  @Operation(description = "Replace the groups of user.", operationId = "user:group:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @PutMapping("/{id}/group")
  public ApiLocaleResult<?> groupReplace(
      @Parameter(name = "id", description = "User id", required = true) @PathVariable("id") Long userId,
      @Valid @Size(max = MAX_RELATION_QUOTA) @Parameter(name = "ids", description = "Group ids")
      @RequestBody LinkedHashSet<Long> groupIds) {
    userGroupFacade.groupReplace(userId, groupIds);
    return ApiLocaleResult.success();
  }

  @Operation(description = "Remove the user from groups.", operationId = "user:group:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{id}/group")
  public void groupDelete(
      @Parameter(name = "id", description = "User id", required = true) @PathVariable("id") Long userId,
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @Parameter(name = "groupIds", description = "Group ids", required = true)
      @RequestParam("groupIds") HashSet<Long> groupIds) {
    userGroupFacade.groupDelete(userId, groupIds);
  }

  @Operation(description = "Query the groups list of user.", operationId = "user:group:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping(value = "/{id}/group")
  public ApiLocaleResult<PageResult<UserGroupVo>> groupList(
      @Parameter(name = "id", description = "User id", required = true) @PathVariable("id") Long userId,
      @Valid @ParameterObject UserGroupFindDto dto) {
    return ApiLocaleResult.success(userGroupFacade.groupList(userId, dto));
  }

}
