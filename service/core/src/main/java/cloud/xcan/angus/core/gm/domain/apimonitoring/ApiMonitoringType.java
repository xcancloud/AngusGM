package cloud.xcan.angus.core.gm.domain.apimonitoring;

/**
 * API监控类型枚举
 */
public enum ApiMonitoringType {
    /**
     * 请求监控
     */
    REQUEST,

    /**
     * 响应监控
     */
    RESPONSE,

    /**
     * 性能监控
     */
    PERFORMANCE,

    /**
     * 错误监控
     */
    ERROR,

    /**
     * 安全监控
     */
    SECURITY,

    /**
     * 限流监控
     */
    RATE_LIMIT
}
