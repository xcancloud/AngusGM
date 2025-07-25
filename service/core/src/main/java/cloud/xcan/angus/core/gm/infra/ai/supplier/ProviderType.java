package cloud.xcan.angus.core.gm.infra.ai.supplier;

/**
 * <p>
 * Enumeration of supported AI service providers for agent client implementations.
 * </p>
 * <p>
 * This enum defines the available AI service providers that can be used with
 * the AI agent client factory. Each provider type corresponds to a specific
 * AI service implementation with its own client configuration and capabilities.
 * </p>
 * <p>
 * The enum can be extended to support additional AI providers by adding new
 * enum constants and implementing corresponding client classes.
 * </p>
 *
 * @author System
 * @since 1.0.0
 */
public enum ProviderType {
    
    /**
     * <p>
     * Tudou AI service provider.
     * </p>
     * <p>
     * Represents the Tudou AI platform which provides various AI capabilities
     * including natural language processing, code generation, and task automation.
     * This provider supports multiple chat types for different AI-assisted operations.
     * </p>
     */
    Tudou
}
