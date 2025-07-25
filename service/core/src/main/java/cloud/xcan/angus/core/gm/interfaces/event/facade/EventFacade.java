package cloud.xcan.angus.core.gm.interfaces.event.facade;

import cloud.xcan.angus.core.event.source.EventContent;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.EventFindDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.vo.EventDetailVo;
import cloud.xcan.angus.core.gm.interfaces.event.facade.vo.EventReceiveChannelVo;
import cloud.xcan.angus.core.gm.interfaces.event.facade.vo.EventVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.List;

/**
 * <p>
 * Event management facade interface providing business operations for event entities.
 * </p>
 * <p>
 * This facade manages the event notification system including event creation,
 * channel configuration, and event delivery tracking. It coordinates between
 * event domain services, notification channels, and delivery mechanisms to
 * provide a comprehensive event management solution.
 * </p>
 * <p>
 * The facade supports multiple notification channels (email, SMS, webhook, etc.)
 * and provides event lifecycle management from creation to delivery confirmation.
 * </p>
 *
 * @author System
 * @since 1.0.0
 */
public interface EventFacade {

    /**
     * <p>
     * Creates multiple new events in the system for processing and delivery.
     * </p>
     * <p>
     * This method handles batch creation of event entities from event content
     * objects. Events are validated, processed, and queued for delivery through
     * appropriate notification channels. The operation ensures proper event
     * routing and delivery scheduling.
     * </p>
     *
     * @param eventContents List of event content objects containing event data and delivery instructions
     * @return List of IdKey objects containing the newly created event identifiers and metadata
     */
    List<IdKey<Long, Object>> add(List<EventContent> eventContents);

    /**
     * <p>
     * Retrieves comprehensive detailed information about a specific event.
     * </p>
     * <p>
     * This method returns complete event information including event content,
     * delivery status, channel configurations, recipient information, and
     * delivery tracking data. The data provides a full audit trail of the
     * event's lifecycle from creation to delivery.
     * </p>
     *
     * @param id The unique identifier of the event to retrieve
     * @return Detailed event information object containing comprehensive event data
     */
    EventDetailVo detail(Long id);

    /**
     * <p>
     * Retrieves available receive channels for a specific event code.
     * </p>
     * <p>
     * This method returns the list of notification channels that are configured
     * and available for delivering events with the specified event code. It
     * includes channel capabilities, configuration status, and delivery options
     * for each available channel.
     * </p>
     *
     * @param eventCode The event code for which to retrieve available receive channels
     * @return List of event receive channel information objects showing available delivery options
     */
    List<EventReceiveChannelVo> receiveChannel(String eventCode);

    /**
     * <p>
     * Retrieves a paginated list of events with filtering and search capabilities.
     * </p>
     * <p>
     * This method supports complex query operations including text search,
     * filtering by event type, status, delivery channel, time range, and other
     * criteria. Results are paginated for efficient data handling and include
     * essential event information for administrative and monitoring purposes.
     * </p>
     *
     * @param dto Event search and filter criteria including pagination parameters
     * @return Paginated result containing event list with metadata and navigation information
     */
    PageResult<EventVo> list(EventFindDto dto);
}
