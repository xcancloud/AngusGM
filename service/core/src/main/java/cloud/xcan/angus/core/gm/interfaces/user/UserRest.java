package cloud.xcan.angus.core.gm.interfaces.user;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import cloud.xcan.angus.api.enums.UserSource;
import cloud.xcan.angus.api.gm.user.dto.UserFindDto;
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

/**
 * <p>
 * REST controller providing unified user management APIs for the system.
 * </p>
 * <p>
 * This controller handles all user-related operations including CRUD operations,
 * user status management (enable/disable, lock/unlock), system administrator
 * management, and user data retrieval with pagination and filtering capabilities.
 * </p>
 * <p>
 * All endpoints follow RESTful conventions and include comprehensive validation,
 * error handling, and API documentation through OpenAPI annotations.
 * </p>
 *
 * @author System
 * @since 1.0.0
 */
@Tag(name = "User", description = "Unified user management entry for the system")
@Validated
@RestController
@RequestMapping("/api/v1/user")
public class UserRest {

    @Resource
    private UserFacade userFacade;

    /**
     * <p>
     * Creates a new user in the system with the provided information.
     * </p>
     * <p>
     * This endpoint accepts user details including personal information,
     * department associations, group memberships, and tag assignments.
     * The user source is automatically set to BACKGROUND_ADDED for audit purposes.
     * </p>
     *
     * @param dto User creation data transfer object containing all required user information
     * @return ApiLocaleResult containing the newly created user's ID and metadata
     */
    @Operation(summary = "Add user", operationId = "user:add")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created successfully")})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApiLocaleResult<IdKey<Long, Object>> add(@Valid @RequestBody UserAddDto dto) {
        return ApiLocaleResult.success(userFacade.add(dto, UserSource.BACKGROUND_ADDED));
    }

    /**
     * <p>
     * Updates an existing user's information with partial data.
     * </p>
     * <p>
     * This endpoint performs a partial update, modifying only the fields
     * provided in the request body. Null or missing fields are ignored
     * and existing values are preserved.
     * </p>
     *
     * @param dto User update data transfer object containing fields to be modified
     * @return ApiLocaleResult indicating success or failure of the operation
     */
    @Operation(summary = "Update user", operationId = "user:update")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated successfully"),
            @ApiResponse(responseCode = "404", description = "Resource not found")})
    @PatchMapping
    public ApiLocaleResult<?> update(@Valid @RequestBody UserUpdateDto dto) {
        userFacade.update(dto);
        return ApiLocaleResult.success();
    }

    /**
     * <p>
     * Replaces an existing user's information completely with new data.
     * </p>
     * <p>
     * This endpoint performs a complete replacement of user data,
     * unlike the update operation which is partial. All user fields
     * are replaced with the provided values.
     * </p>
     *
     * @param dto User replacement data transfer object containing complete user information
     * @return ApiLocaleResult containing the updated user's ID and metadata
     */
    @Operation(summary = "Replace user", operationId = "user:replace")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Replaced successfully"),
            @ApiResponse(responseCode = "404", description = "Resource not found")})
    @PutMapping
    public ApiLocaleResult<IdKey<Long, Object>> replace(@Valid @RequestBody UserReplaceDto dto) {
        return ApiLocaleResult.success(userFacade.replace(dto));
    }

    /**
     * <p>
     * Deletes multiple users from the system by their IDs.
     * </p>
     * <p>
     * This operation is irreversible and will permanently remove user data
     * from the system. The operation is performed in batch mode for efficiency
     * and is limited to a maximum number of users per request for safety.
     * </p>
     *
     * @param ids Set of user IDs to be deleted (maximum size enforced by MAX_BATCH_SIZE)
     */
    @Operation(summary = "Delete users", operationId = "user:delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Deleted successfully")})
    @DeleteMapping
    public void delete(
            @Valid @RequestParam("ids") @Size(max = MAX_BATCH_SIZE) HashSet<Long> ids) {
        userFacade.delete(ids);
    }

    /**
     * <p>
     * Enables or disables multiple users in batch mode.
     * </p>
     * <p>
     * This endpoint allows administrators to control user access by enabling
     * or disabling user accounts. Disabled users cannot log in or access
     * system resources until re-enabled.
     * </p>
     *
     * @param dto Set of enable/disable operations specifying user IDs and desired states
     * @return ApiLocaleResult indicating success or failure of the operation
     */
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

    /**
     * <p>
     * Locks or unlocks a user account with optional time constraints.
     * </p>
     * <p>
     * This security feature allows administrators to temporarily or permanently
     * restrict user access. Locked users cannot authenticate until unlocked.
     * Optional start and end dates can be specified for automatic lock/unlock.
     * </p>
     *
     * @param dto User lock operation data including user ID, lock status, and optional time constraints
     * @return ApiLocaleResult indicating success or failure of the operation
     */
    @Operation(summary = "Lock or unlock user", operationId = "user:lock")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Locked or unlocked successfully"),
            @ApiResponse(responseCode = "404", description = "Resource not found")
    })
    @PatchMapping("/locked")
    public ApiLocaleResult<?> locked(@Valid @RequestBody UserLockedDto dto) {
        userFacade.locked(dto);
        return ApiLocaleResult.success();
    }

    /**
     * <p>
     * Grants or revokes system administrator privileges for a user.
     * </p>
     * <p>
     * System administrators have elevated privileges and can perform
     * administrative operations across the entire system. This operation
     * should be performed with caution and proper authorization.
     * </p>
     *
     * @param dto System administrator assignment data including user ID and admin status
     * @return ApiLocaleResult indicating success or failure of the operation
     */
    @Operation(summary = "Set user as system administrator", operationId = "user:admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Set successfully"),
            @ApiResponse(responseCode = "404", description = "Resource not found")})
    @PatchMapping(value = "/sysadmin")
    public ApiLocaleResult<?> sysAdminSet(@Valid @RequestBody UserSysAdminSetDto dto) {
        userFacade.sysAdminSet(dto);
        return ApiLocaleResult.success();
    }

    /**
     * <p>
     * Retrieves a list of all system administrators for the current tenant.
     * </p>
     * <p>
     * This endpoint returns information about users who have been granted
     * system administrator privileges within the current tenant scope.
     * </p>
     *
     * @return ApiLocaleResult containing a list of system administrator information
     */
    @Operation(summary = "Query the system administrators of tenant", operationId = "user:admin:list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
    @GetMapping(value = "/sysadmin")
    public ApiLocaleResult<List<UserSysAdminVo>> sysAdminList() {
        return ApiLocaleResult.success(userFacade.sysAdminList());
    }

    /**
     * <p>
     * Checks whether a username already exists in the system.
     * </p>
     * <p>
     * This utility endpoint helps prevent duplicate usernames during user
     * creation or username changes. It returns both existence status and
     * the associated user ID if the username is found.
     * </p>
     *
     * @param username The username to check for existence (must not be empty, max length enforced)
     * @return ApiLocaleResult containing username check results including existence status and user ID
     */
    @Operation(summary = "Check whether or not username existed", operationId = "user:username:check")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Checked successfully")})
    @GetMapping(value = "/username/check")
    public ApiLocaleResult<UsernameCheckVo> checkUsername(
            @Valid @NotEmpty @Length(max = MAX_NAME_LENGTH) @Parameter(name = "username", description = "username", required = true)
            @RequestParam("username") String username) {
        return ApiLocaleResult.success(userFacade.checkUsername(username));
    }

    /**
     * <p>
     * Retrieves detailed information about a specific user by ID.
     * </p>
     * <p>
     * This endpoint returns comprehensive user information including
     * personal details, department associations, group memberships,
     * tag assignments, and other relevant metadata.
     * </p>
     *
     * @param id The unique identifier of the user to retrieve
     * @return ApiLocaleResult containing detailed user information
     */
    @Operation(summary = "Query the detail of user", operationId = "user:detail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Resource not found")})
    @GetMapping(value = "/{id}")
    public ApiLocaleResult<UserDetailVo> detail(
            @Parameter(name = "id", description = "User id", required = true) @PathVariable("id") Long id) {
        return ApiLocaleResult.success(userFacade.detail(id));
    }

    /**
     * <p>
     * Retrieves a paginated list of users with optional filtering and search capabilities.
     * </p>
     * <p>
     * This endpoint supports various query parameters for filtering users by
     * different criteria such as department, status, role, and text search.
     * Results are returned in paginated format for efficient data handling.
     * </p>
     *
     * @param dto User search and filter criteria including pagination parameters
     * @return ApiLocaleResult containing paginated user list with metadata
     */
    @Operation(summary = "Query the list of user", operationId = "user:list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
    @GetMapping
    public ApiLocaleResult<PageResult<UserListVo>> list(@Valid @ParameterObject UserFindDto dto) {
        return ApiLocaleResult.success(userFacade.list(dto));
    }
}
