package cloud.xcan.angus.core.gm.interfaces.user;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import cloud.xcan.angus.api.enums.UserSource;
import cloud.xcan.angus.api.gm.user.dto.UserFindDto;
import cloud.xcan.angus.api.gm.user.dto.UserSearchDto;
import cloud.xcan.angus.api.gm.user.vo.UserDetailVo;
import cloud.xcan.angus.api.gm.user.vo.UserListVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.UserFacade;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserAddDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserLockedDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserSysAdminSetDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserSysAdminVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UsernameCheckVo;
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
import org.hibernate.validator.constraints.Length;
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


@Tag(name = "User", description = "Unified user management entry for the system")
@Validated
@RestController
@RequestMapping("/api/v1/user")
public class UserRest {

  @Resource
  private UserFacade userFacade;

  @Operation(summary = "Add user", operationId = "user:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<IdKey<Long, Object>> add(@Valid @RequestBody UserAddDto dto) {
    return ApiLocaleResult.success(userFacade.add(dto, UserSource.BACKGROUND_ADDED));
  }

  @Operation(summary = "Update user", operationId = "user:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Updated successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @PatchMapping
  public ApiLocaleResult<?> update(@Valid @RequestBody UserUpdateDto dto) {
    userFacade.update(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Replace user", operationId = "user:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @PutMapping
  public ApiLocaleResult<IdKey<Long, Object>> replace(@Valid @RequestBody UserReplaceDto dto) {
    return ApiLocaleResult.success(userFacade.replace(dto));
  }

  @Operation(summary = "Delete users", operationId = "user:delete")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Deleted successfully")})
  @DeleteMapping
  public void delete(
      @Valid @RequestParam("ids") @Size(max = MAX_BATCH_SIZE) HashSet<Long> ids) {
    userFacade.delete(ids);
  }

  @Operation(summary = "Enable or disable users", operationId = "user:enabled")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Enabled or disabled successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")
  })
  @PatchMapping("/enabled")
  public ApiLocaleResult<?> enabled(
      @Valid @RequestBody @Size(max = MAX_BATCH_SIZE) Set<EnabledOrDisabledDto> dto) {
    userFacade.enabled(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "DistributedLock or unlock  user", operationId = "user:lock")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Locked or unlocked successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")
  })
  @PatchMapping("/locked")
  public ApiLocaleResult<?> locked(@Valid @RequestBody UserLockedDto dto) {
    userFacade.locked(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Set user as system administrator", operationId = "user:admin")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Set successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @PatchMapping(value = "/sysadmin")
  public ApiLocaleResult<?> sysAdminSet(@Valid @RequestBody UserSysAdminSetDto dto) {
    userFacade.sysAdminSet(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Query the system administrators of tenant", operationId = "user:admin:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping(value = "/sysadmin")
  public ApiLocaleResult<List<UserSysAdminVo>> sysAdminList() {
    return ApiLocaleResult.success(userFacade.sysAdminList());
  }

  @Operation(summary = "Check whether or not username existed", operationId = "user:username:check")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Checked successfully")})
  @GetMapping(value = "/username/check")
  public ApiLocaleResult<UsernameCheckVo> checkUsername(
      @Valid @NotEmpty @Length(max = MAX_NAME_LENGTH) @Parameter(name = "username", description = "username", required = true)
      @RequestParam("username") String username) {
    return ApiLocaleResult.success(userFacade.checkUsername(username));
  }

  @Operation(summary = "Query the detail of user", operationId = "user:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/{id}")
  public ApiLocaleResult<UserDetailVo> detail(
      @Parameter(name = "id", description = "User id", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(userFacade.detail(id));
  }

  @Operation(summary = "Query the list of user", operationId = "user:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<PageResult<UserListVo>> list(@Valid @ParameterObject UserFindDto dto) {
    return ApiLocaleResult.success(userFacade.list(dto));
  }

  @Operation(summary = "Fulltext search the list of user", operationId = "user:search")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/search")
  public ApiLocaleResult<PageResult<UserListVo>> search(@Valid @ParameterObject UserSearchDto dto) {
    return ApiLocaleResult.success(userFacade.search(dto));
  }
}
