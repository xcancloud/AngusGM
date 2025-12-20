package cloud.xcan.angus.core.gm.domain.monitoring;

/**
 * 监控状态枚举
 */
public enum MonitoringStatus {
    /**
     * 健康
     */
    HEALTHY,

    /**
     * 警告
     */
    WARNING,

    /**
     * 错误
     */
    ERROR,

    /**
     * 严重
     */
    CRITICAL,

    /**
     * 未知
     */
    UNKNOWN
}
