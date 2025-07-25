package cloud.xcan.angus.core.gm.infra.config;

import cloud.xcan.angus.core.gm.application.cmd.event.EventChannelPushCmd;
import cloud.xcan.angus.core.gm.domain.event.ReceiveChannelType;
import java.util.HashMap;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * Spring configuration class for event system components and services.
 * </p>
 * <p>
 * This configuration class sets up the infrastructure for the event notification
 * system including push service mappings, template caching, and channel caching.
 * It provides centralized configuration for event-related beans and services
 * used throughout the application.
 * </p>
 *
 * @author System
 * @since 1.0.0
 */
@Configuration
public class EventConfig {

    /**
     * <p>
     * Creates a mapping of receive channel types to their corresponding push command implementations.
     * </p>
     * <p>
     * This bean provides a strategy pattern implementation for handling different types
     * of notification channels (email, SMS, webhook, etc.). Each channel type is mapped
     * to its specific push command implementation, allowing for polymorphic message delivery.
     * </p>
     * <p>
     * The mapping is automatically populated by Spring's dependency injection, collecting
     * all available EventChannelPushCmd implementations and organizing them by their
     * primary key (channel type).
     * </p>
     *
     * @param eventChannelPushCmd List of all available event channel push command implementations
     * @return HashMap mapping receive channel types to their corresponding push commands
     */
    @Bean("pushServiceMap")
    public HashMap<ReceiveChannelType, EventChannelPushCmd> getPushServiceMap(
            List<EventChannelPushCmd> eventChannelPushCmd) {
        HashMap<ReceiveChannelType, EventChannelPushCmd> pushServiceMap = 
                new HashMap<>(eventChannelPushCmd.size());
        
        for (EventChannelPushCmd eventPushCmd : eventChannelPushCmd) {
            pushServiceMap.put(eventPushCmd.getPkey(), eventPushCmd);
        }
        
        return pushServiceMap;
    }

    /**
     * <p>
     * Creates and configures the event template cache for improved performance.
     * </p>
     * <p>
     * This cache stores frequently accessed event templates to reduce database
     * queries and improve system response times. Templates are cached based on
     * their identifiers and automatically refreshed when templates are modified.
     * </p>
     *
     * @return EventTemplateCache instance for template caching operations
     */
    @Bean
    public EventTemplateCache getEventTemplateCache() {
        return new EventTemplateCache();
    }

    /**
     * <p>
     * Creates and configures the event channel cache for optimized channel lookups.
     * </p>
     * <p>
     * This cache stores event channel configurations to minimize database access
     * during message delivery operations. Channel configurations include routing
     * information, credentials, and delivery preferences for various notification
     * channels.
     * </p>
     *
     * @return EventChannelCache instance for channel configuration caching
     */
    @Bean
    public EventChannelCache getEventChannelCache() {
        return new EventChannelCache();
    }
}
