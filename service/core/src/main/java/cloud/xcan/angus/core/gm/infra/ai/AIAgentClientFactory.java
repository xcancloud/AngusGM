package cloud.xcan.angus.core.gm.infra.ai;

import cloud.xcan.angus.core.gm.infra.ai.supplier.AIAgentClient;
import cloud.xcan.angus.core.gm.infra.ai.supplier.ProviderType;
import cloud.xcan.angus.core.gm.infra.ai.supplier.TudouAIAgentClient;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Factory class for creating AI agent client instances based on provider type.
 * </p>
 * <p>
 * This factory implements the Factory Method pattern to provide a centralized
 * way of creating AI agent clients for different providers. It abstracts the
 * instantiation logic and provides a consistent interface for client creation
 * regardless of the underlying AI service provider.
 * </p>
 * <p>
 * The factory supports multiple AI providers and can be easily extended to
 * include additional providers by adding new cases to the factory method.
 * </p>
 *
 * @author System
 * @since 1.0.0
 */
@Slf4j
public final class AIAgentClientFactory {

    /**
     * <p>
     * Private constructor to prevent instantiation of utility class.
     * </p>
     * This class is designed as a utility class with static methods only.
     */
    private AIAgentClientFactory() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * <p>
     * Creates an AI agent client instance based on the specified provider type.
     * </p>
     * <p>
     * This method implements the factory pattern to instantiate the appropriate
     * AI client implementation based on the provider type. If an unsupported
     * provider type is specified, it defaults to the Tudou AI client.
     * </p>
     *
     * @param providerType The type of AI provider for which to create a client
     * @return AIAgentClient instance configured for the specified provider
     * @throws IllegalArgumentException if providerType is null
     */
    public static AIAgentClient create(ProviderType providerType) {
        if (providerType == null) {
            log.warn("Provider type is null, defaulting to Tudou AI client");
            return new TudouAIAgentClient();
        }
        
        switch (providerType) {
            case Tudou:
                log.debug("Creating Tudou AI agent client");
                return new TudouAIAgentClient();
            default:
                log.warn("Unsupported provider type: {}, defaulting to Tudou AI client", providerType);
                return new TudouAIAgentClient();
        }
    }

    /**
     * <p>
     * Demonstration main method showing various AI agent client usage scenarios.
     * </p>
     * <p>
     * This method provides examples of how to use the AI agent client for different
     * chat types including backlog writing, task splitting, test case generation,
     * and performance script creation. It serves as both documentation and a
     * testing utility for the AI integration features.
     * </p>
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Session and agent configuration
        String sessionId = "333ca176-0784-4a06-af9d-8ecab5a85091";
        String aiAgentId = "2137107453692846363";
        
        // Create AI client instance
        AIAgentClient client = create(ProviderType.Tudou);
        
        try {
            // Example 1: Generate product backlog items
            String cmd1 = "写10个Wiki系统的产品Backlog";
            log.info("Executing backlog generation command: {}", cmd1);
            client.chat(AIChatType.WRITE_BACKLOG, cmd1, sessionId, aiAgentId);

            // Example 2: Split functionality into sub-tasks
            String cmd2 = "将用户登录功能拆分成多个子任务";
            log.info("Executing task splitting command: {}", cmd2);
            client.chat(AIChatType.SPLIT_SUB_TASK, cmd2, sessionId, aiAgentId);

            // Example 3: Generate functional test cases
            String cmd3 = "编写用户验证码登录功能测试用例";
            log.info("Executing test case generation command: {}", cmd3);
            client.chat(AIChatType.WRITE_FUNC_CASE, cmd3, sessionId, aiAgentId);

            // Example 4: Generate performance testing script
            String cmd4 = "写一个查询用户性能测试脚本";
            log.info("Executing performance script generation command: {}", cmd4);
            client.chat(AIChatType.WRITE_ANGUS_SCRIPT, cmd4, sessionId, aiAgentId);
            
            log.info("All AI agent demonstrations completed successfully");
            
        } catch (Exception e) {
            log.error("Error during AI agent demonstration: {}", e.getMessage(), e);
        }
    }
}

