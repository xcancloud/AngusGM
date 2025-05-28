package cloud.xcan.angus.core.gm.interfaces.group;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_RELATION_QUOTA;

import cloud.xcan.angus.core.gm.interfaces.group.facade.GroupUserFacade;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.user.GroupUserFindDto;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "GroupUser", description = "Controls group-to-user membership mappings to regulate "
    + "user visibility and access privileges based on organizational groups.")
@Validated
@RestController
@RequestMapping("/api/v1/group")
public class GroupUserRest {

  @Resource
  private GroupUserFacade groupUserFacade;

  @Operation(summary = "Add the users to group.", operationId = "group:user:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/{id}/user")
  public ApiLocaleResult<List<IdKey<Long, Object>>> userAdd(
      @Parameter(name = "id", description = "Group id", required = true) @PathVariable("id") Long groupId,
      @Valid @NotEmpty @Size(max = MAX_RELATION_QUOTA) @Parameter(name = "ids", description = "User ids", required = true)
      @RequestBody LinkedHashSet<Long> userIds) {
    return ApiLocaleResult.success(groupUserFacade.userAdd(groupId, userIds));
  }

  @Operation(summary = "Remove the users from group.", operationId = "group:user:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{id}/user")
  public void delete(
      @Parameter(name = "id", description = "Group id", required = true) @PathVariable("id") Long groupId,
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @Parameter(name = "ids", description = "User ids", required = true)
      @RequestParam("ids") HashSet<Long> userIds) {
    groupUserFacade.userDelete(groupId, userIds);
  }

  @Operation(summary = "Query the users list of group.", operationId = "group:user:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping(value = "/{id}/user")
  public ApiLocaleResult<PageResult<UserGroupVo>> groupUserList(
      @Parameter(name = "id", description = "Group id", required = true) @PathVariable("id") Long groupId,
      @Valid @ParameterObject GroupUserFindDto dto) {
    return ApiLocaleResult.success(groupUserFacade.groupUserList(groupId, dto));
  }

}
