package cloud.xcan.angus.core.gm.interfaces.to;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import cloud.xcan.angus.core.gm.interfaces.to.facade.TOUserRoleFacade;
import cloud.xcan.angus.core.spring.condition.CloudServiceEditionCondition;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.spec.annotations.CloudServiceEdition;
import cloud.xcan.angus.spec.annotations.OperationClient;
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
import org.springframework.context.annotation.Conditional;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@OperationClient
@PreAuthorize("@PPS.isOpClient()")
@CloudServiceEdition
@Conditional(value = CloudServiceEditionCondition.class)
@Tag(name = "TOUserRole", description = "Operational user-role relationship management. Provides unified operations for querying and authorizing user-role associations in cloud service environments")
@Validated
@RestController
@RequestMapping("/api/v1/to")
public class TOUserRoleRest {

  @Resource
  private TOUserRoleFacade toUserRoleFacade;

  @Operation(summary = "Assign operational roles to user", operationId = "to:user:role:auth")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Operational roles assigned successfully"),
      @ApiResponse(responseCode = "404", description = "User or role not found")
  })
  @PatchMapping("/user/{id}/role/auth")
  public ApiLocaleResult<?> userRoleAuth(
      @Parameter(name = "id", description = "Operational user account identifier", required = true) @PathVariable("id") Long userId,
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestBody HashSet<Long> roleIds) {
    toUserRoleFacade.userRoleAuth(userId, roleIds);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Remove operational roles from user", operationId = "to:user:role:delete")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Operational roles removed successfully")})
  @DeleteMapping("/user/{id}/role")
  public ApiLocaleResult<?> userRoleDelete(
      @Parameter(name = "id", description = "Operational user account identifier", required = true) @PathVariable("id") Long userId,
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @RequestParam("roleIds") HashSet<Long> roleIds) {
    toUserRoleFacade.userRoleDelete(userId, roleIds);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Assign operational role to multiple users", operationId = "to:role:user:auth")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Operational role assigned to users successfully"),
      @ApiResponse(responseCode = "404", description = "Role or user not found")
  })
  @PatchMapping("/role/{id}/auth")
  public ApiLocaleResult<?> roleUserAuth(
      @Parameter(name = "id", description = "Operational role identifier", required = true) @PathVariable("id") Long roleId,
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @RequestParam("userIds") HashSet<Long> userIds) {
    toUserRoleFacade.roleUserAuth(roleId, userIds);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Remove operational role from multiple users", operationId = "to:role:user:delete")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Operational role removed from users successfully")})
  @DeleteMapping("/role/{id}/user")
  public ApiLocaleResult<?> roleUserDelete(
      @Parameter(name = "id", description = "Operational role identifier", required = true) @PathVariable("id") Long roleId,
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @RequestParam("userIds") HashSet<Long> userIds) {
    toUserRoleFacade.roleUserDelete(roleId, userIds);
    return ApiLocaleResult.success();
  }

}
