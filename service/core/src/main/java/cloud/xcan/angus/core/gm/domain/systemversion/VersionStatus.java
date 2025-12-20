package cloud.xcan.angus.core.gm.domain.systemversion;

/**
 * 版本状态枚举
 */
public enum VersionStatus {
    /**
     * 规划中
     */
    PLANNING,

    /**
     * 开发中
     */
    DEVELOPMENT,

    /**
     * 测试中
     */
    TESTING,

    /**
     * 预发布
     */
    STAGING,

    /**
     * 生产环境
     */
    PRODUCTION,

    /**
     * 已废弃
     */
    DEPRECATED
}
