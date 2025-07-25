package cloud.xcan.angus.core.gm.interfaces.user.facade;

import cloud.xcan.angus.api.enums.UserSource;
import cloud.xcan.angus.api.gm.user.dto.UserFindDto;
import cloud.xcan.angus.api.gm.user.vo.UserDetailVo;
import cloud.xcan.angus.api.gm.user.vo.UserListVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserAddDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserLockedDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserSysAdminSetDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserSysAdminVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UsernameCheckVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.remote.dto.EnabledOrDisabledDto;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * User management facade interface providing business logic operations for user entities.
 * </p>
 * <p>
 * This facade implements the facade pattern to provide a simplified interface
 * to the complex user management subsystem. It coordinates between multiple
 * domain services and handles business logic validation, data transformation,
 * and transaction management for user-related operations.
 * </p>
 * <p>
 * The facade supports comprehensive user lifecycle management including creation,
 * modification, deletion, status management, and administrative operations.
 * All operations maintain data consistency and enforce business rules.
 * </p>
 *
 * @author System
 * @since 1.0.0
 */
public interface UserFacade {

    /**
     * <p>
     * Creates a new user in the system with associated organizational relationships.
     * </p>
     * <p>
     * This method handles the complete user creation process including validation,
     * domain object creation, and establishment of relationships with departments,
     * groups, and tags. The operation is transactional to ensure data consistency.
     * </p>
     *
     * @param dto User creation data containing personal information and organizational associations
     * @param userSource Source of user creation for audit and tracking purposes
     * @return IdKey containing the newly created user's unique identifier and metadata
     */
    IdKey<Long, Object> add(UserAddDto dto, UserSource userSource);

    /**
     * <p>
     * Updates an existing user's information with partial data modification.
     * </p>
     * <p>
     * Performs a partial update operation where only the provided fields are modified.
     * The method handles cascading updates to related entities such as department
     * associations, group memberships, and tag assignments.
     * </p>
     *
     * @param dto User update data containing the fields to be modified
     */
    void update(UserUpdateDto dto);

    /**
     * <p>
     * Completely replaces an existing user's information with new data.
     * </p>
     * <p>
     * Unlike the update operation, this method performs a complete replacement
     * of user data including all relationships. This operation is useful for
     * data synchronization scenarios or complete user profile overhauls.
     * </p>
     *
     * @param dto User replacement data containing complete user information
     * @return IdKey containing the updated user's identifier and metadata
     */
    IdKey<Long, Object> replace(UserReplaceDto dto);

    /**
     * <p>
     * Permanently deletes multiple users from the system.
     * </p>
     * <p>
     * This operation removes users and all their associated data including
     * relationships, permissions, and audit trails. The operation is irreversible
     * and should be performed with appropriate authorization and confirmation.
     * </p>
     *
     * @param ids Set of user identifiers to be deleted
     */
    void delete(HashSet<Long> ids);

    /**
     * <p>
     * Enables or disables multiple user accounts in batch mode.
     * </p>
     * <p>
     * This method allows administrators to control user access by modifying
     * the enabled status of user accounts. Disabled users are prevented from
     * authenticating and accessing system resources.
     * </p>
     *
     * @param dto Set of enable/disable operations specifying user IDs and desired states
     */
    void enabled(Set<EnabledOrDisabledDto> dto);

    /**
     * <p>
     * Locks or unlocks a user account with optional time-based constraints.
     * </p>
     * <p>
     * This security feature allows temporary or permanent restriction of user access.
     * Optional start and end dates can be specified for automatic lock management.
     * Locked users cannot authenticate until the lock is removed or expires.
     * </p>
     *
     * @param dto User lock operation data including user ID, lock status, and time constraints
     */
    void locked(UserLockedDto dto);

    /**
     * <p>
     * Grants or revokes system administrator privileges for a user.
     * </p>
     * <p>
     * System administrators have elevated privileges across the entire system.
     * This operation should be performed with appropriate authorization and
     * careful consideration of security implications.
     * </p>
     *
     * @param dto System administrator assignment data including user ID and admin status
     */
    void sysAdminSet(UserSysAdminSetDto dto);

    /**
     * <p>
     * Retrieves a list of all system administrators within the current tenant scope.
     * </p>
     * <p>
     * This method returns information about users who have been granted system
     * administrator privileges, providing visibility into the administrative
     * structure of the tenant.
     * </p>
     *
     * @return List of system administrator information objects
     */
    List<UserSysAdminVo> sysAdminList();

    /**
     * <p>
     * Checks whether a username already exists in the system.
     * </p>
     * <p>
     * This utility method helps prevent duplicate usernames during user creation
     * or username modification operations. It returns both existence status and
     * the associated user ID if found.
     * </p>
     *
     * @param username The username to check for existence
     * @return Username check result containing existence status and user ID if found
     */
    UsernameCheckVo checkUsername(String username);

    /**
     * <p>
     * Retrieves comprehensive detailed information about a specific user.
     * </p>
     * <p>
     * This method returns complete user information including personal details,
     * organizational relationships, permissions, and metadata. The data is
     * enriched with related entity names through the @NameJoin annotation.
     * </p>
     *
     * @param id The unique identifier of the user to retrieve
     * @return Detailed user information object
     */
    UserDetailVo detail(Long id);

    /**
     * <p>
     * Retrieves a paginated list of users with filtering and search capabilities.
     * </p>
     * <p>
     * This method supports complex query operations including text search,
     * filtering by various criteria, and pagination for efficient data handling.
     * The results include enriched data through the @NameJoin annotation.
     * </p>
     *
     * @param findDto User search and filter criteria including pagination parameters
     * @return Paginated result containing user list and metadata
     */
    PageResult<UserListVo> list(UserFindDto findDto);
}
