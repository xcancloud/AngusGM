package cloud.xcan.angus.core.gm.interfaces.api.facade;

import cloud.xcan.angus.core.gm.interfaces.api.facade.dto.ApiAddDto;
import cloud.xcan.angus.core.gm.interfaces.api.facade.dto.ApiFindDto;
import cloud.xcan.angus.core.gm.interfaces.api.facade.dto.ApiReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.api.facade.dto.ApiUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.api.facade.vo.ApiDetailVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.remote.dto.EnabledOrDisabledDto;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.List;

/**
 * <p>
 * API management facade interface providing business operations for API entities.
 * </p>
 * <p>
 * This facade manages the complete lifecycle of API definitions within the system,
 * including creation, modification, deletion, and status management. It supports
 * batch operations for efficient processing of multiple API entities and provides
 * comprehensive query capabilities with filtering and pagination.
 * </p>
 * <p>
 * The facade coordinates between API domain services, validation logic, and
 * data persistence layers to ensure consistent API management operations.
 * </p>
 *
 * @author System
 * @since 1.0.0
 */
public interface ApiFacade {

    /**
     * <p>
     * Creates multiple new API definitions in the system.
     * </p>
     * <p>
     * This method handles batch creation of API entities with validation,
     * domain object instantiation, and persistence. All operations are
     * performed within a transactional context to ensure data consistency.
     * </p>
     *
     * @param dto List of API creation data transfer objects containing API definitions
     * @return List of IdKey objects containing the newly created API identifiers and metadata
     */
    List<IdKey<Long, Object>> add(List<ApiAddDto> dto);

    /**
     * <p>
     * Updates multiple existing API definitions with partial data modification.
     * </p>
     * <p>
     * Performs partial updates on API entities where only the provided fields
     * are modified. The operation maintains referential integrity and applies
     * business validation rules during the update process.
     * </p>
     *
     * @param dto List of API update data transfer objects containing fields to be modified
     */
    void update(List<ApiUpdateDto> dto);

    /**
     * <p>
     * Completely replaces multiple existing API definitions with new data.
     * </p>
     * <p>
     * Unlike update operations, this method performs complete replacement of
     * API data. This is useful for synchronization scenarios or when complete
     * API definition overhauls are required.
     * </p>
     *
     * @param dto List of API replacement data transfer objects containing complete API information
     * @return List of IdKey objects containing the updated API identifiers and metadata
     */
    List<IdKey<Long, Object>> replace(List<ApiReplaceDto> dto);

    /**
     * <p>
     * Permanently deletes multiple API definitions from the system.
     * </p>
     * <p>
     * This operation removes API entities and all associated data including
     * permissions, logs, and relationships. The operation is irreversible
     * and should be performed with appropriate authorization.
     * </p>
     *
     * @param ids Set of API identifiers to be deleted
     */
    void delete(HashSet<Long> ids);

    /**
     * <p>
     * Enables or disables multiple API definitions in batch mode.
     * </p>
     * <p>
     * This method allows administrators to control API availability by
     * modifying the enabled status. Disabled APIs are not accessible
     * for execution and may be hidden from API discovery mechanisms.
     * </p>
     *
     * @param dto List of enable/disable operations specifying API IDs and desired states
     */
    void enabled(List<EnabledOrDisabledDto> dto);

    /**
     * <p>
     * Retrieves comprehensive detailed information about a specific API definition.
     * </p>
     * <p>
     * This method returns complete API information including metadata,
     * configuration details, permissions, and usage statistics. The data
     * provides a full view of the API's current state and configuration.
     * </p>
     *
     * @param id The unique identifier of the API to retrieve
     * @return Detailed API information object containing comprehensive API data
     */
    ApiDetailVo detail(Long id);

    /**
     * <p>
     * Retrieves a paginated list of API definitions with filtering and search capabilities.
     * </p>
     * <p>
     * This method supports complex query operations including text search,
     * filtering by various criteria such as status, category, and ownership,
     * and pagination for efficient data handling in user interfaces.
     * </p>
     *
     * @param dto API search and filter criteria including pagination parameters
     * @return Paginated result containing API list with metadata and navigation information
     */
    PageResult<ApiDetailVo> list(ApiFindDto dto);
}
