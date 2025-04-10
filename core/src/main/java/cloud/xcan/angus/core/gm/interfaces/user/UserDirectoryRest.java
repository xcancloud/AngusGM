package cloud.xcan.angus.core.gm.interfaces.user;


import cloud.xcan.angus.core.gm.interfaces.user.facade.UserDirectoryFacade;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.directory.UserDirectoryAddDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.directory.UserDirectoryReorderDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.directory.UserDirectoryReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.directory.UserDirectoryTestDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.directory.UserDirectoryDetailVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.directory.UserDirectorySyncVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.dto.EnabledOrDisabledDto;
import cloud.xcan.angus.spec.experimental.IdKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Set;
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


@Tag(name = "UserDirectory", description = "User directory service management for system LDAP unified authentication login.")
@Validated
@RestController
@RequestMapping("/api/v1/user/directory")
public class UserDirectoryRest {

  @Resource
  private UserDirectoryFacade userDirectoryFacade;

  @PreAuthorize("@PPS.isCloudTenantSecurity()")
  @Operation(description = "Add user directory.", operationId = "user:directory:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<IdKey<Long, Object>> add(@Valid @RequestBody UserDirectoryAddDto dto) {
    return ApiLocaleResult.success(userDirectoryFacade.add(dto));
  }

  @PreAuthorize("@PPS.isCloudTenantSecurity()")
  @Operation(description = "Replace user directory.", operationId = "user:directory:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")
  })
  @ApiResponse(responseCode = "404", description = "Resource not found")
  @PutMapping
  public ApiLocaleResult<?> replace(@Valid @RequestBody UserDirectoryReplaceDto dto) {
    userDirectoryFacade.replace(dto);
    return ApiLocaleResult.success();
  }

  @PreAuthorize("@PPS.isCloudTenantSecurity()")
  @Operation(description = "Update the sequence value and change the user directory synchronization order.", operationId = "user:directory:reorder")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Updated successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")
  })
  @PatchMapping("/reorder")
  public ApiLocaleResult<?> reorder(@Valid @RequestBody UserDirectoryReorderDto dto) {
    userDirectoryFacade.reorder(dto);
    return ApiLocaleResult.success();
  }

  @PreAuthorize("@PPS.isCloudTenantSecurity()")
  @Operation(description = "Enable or disable user directory.", operationId = "user:directory:enabled")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Enabled or disabled successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")
  })
  @PatchMapping("/enabled")
  public ApiLocaleResult<?> enabled(@Valid @RequestBody EnabledOrDisabledDto dto) {
    userDirectoryFacade.enabled(dto);
    return ApiLocaleResult.success();
  }

  @PreAuthorize("@PPS.isCloudTenantSecurity()")
  @Operation(description = "Synchronize the users and groups from user directories.", operationId = "user:directories:sync")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Synchronize successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @ResponseStatus(HttpStatus.CREATED)
  @PutMapping("/sync")
  public ApiLocaleResult<Map<String, UserDirectorySyncVo>> sync() {
    return ApiLocaleResult.success(userDirectoryFacade.sync());
  }

  @PreAuthorize("@PPS.isCloudTenantSecurity()")
  @Operation(description = "Synchronize the users and groups from user directory.", operationId = "user:directory:sync")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Synchronize successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @ResponseStatus(HttpStatus.CREATED)
  @PutMapping("/{id}/sync")
  public ApiLocaleResult<UserDirectorySyncVo> sync(
      @Parameter(name = "id", description = "Directory id", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(userDirectoryFacade.sync(id));
  }

  @PreAuthorize("@PPS.isCloudTenantSecurity()")
  @Operation(description = "Test user directory configuration.", operationId = "user:directory:test")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Test successfully")})
  @PostMapping("/test")
  public ApiLocaleResult<UserDirectorySyncVo> test(@Valid @RequestBody UserDirectoryTestDto dto) {
    return ApiLocaleResult.success(userDirectoryFacade.test(dto));
  }

  @PreAuthorize("@PPS.isCloudTenantSecurity()")
  @Operation(description = "Delete user directory.", operationId = "user:directory:delete")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Deleted successfully")})
  @DeleteMapping("/{id}")
  public void delete(
      @Parameter(name = "id", description = "Directory id", required = true) @PathVariable("id") Long id,
      @Parameter(name = "deleteSync", description = "Delete synchronization data flag", required = true)
      @Valid @NotNull @RequestParam("deleteSync") Boolean deleteSync) {
    userDirectoryFacade.delete(id, deleteSync);
  }

  @PreAuthorize("@PPS.isCloudTenantSecurity()")
  @Operation(description = "Query the detail of user directory.", operationId = "user:directory:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/{id}")
  public ApiLocaleResult<UserDirectoryDetailVo> detail(
      @Parameter(name = "id", description = "Directory id", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(userDirectoryFacade.detail(id));
  }

  @PreAuthorize("@PPS.isCloudTenantSecurity()")
  @Operation(description = "Query the list of user directory.", operationId = "user:directory:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<List<UserDirectoryDetailVo>> list() {
    return ApiLocaleResult.success(userDirectoryFacade.list());
  }

}
