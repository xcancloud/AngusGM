package cloud.xcan.angus.core.gm.domain.monitoring;

/**
 * 监控类型枚举
 */
public enum MonitoringType {
    /**
     * CPU监控
     */
    CPU,

    /**
     * 内存监控
     */
    MEMORY,

    /**
     * 磁盘监控
     */
    DISK,

    /**
     * 网络监控
     */
    NETWORK,

    /**
     * 数据库监控
     */
    DATABASE,

    /**
     * 应用监控
     */
    APPLICATION,

    /**
     * 服务监控
     */
    SERVICE
}
