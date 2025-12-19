package cloud.xcan.angus.core.gm.domain.apimonitoring;

/**
 * API监控状态枚举
 */
public enum ApiMonitoringStatus {
    /**
     * 成功
     */
    SUCCESS,

    /**
     * 失败
     */
    FAILED,

    /**
     * 超时
     */
    TIMEOUT,

    /**
     * 错误
     */
    ERROR,

    /**
     * 被阻止
     */
    BLOCKED
}
