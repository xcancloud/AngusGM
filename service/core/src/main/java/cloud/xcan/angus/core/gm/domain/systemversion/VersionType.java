package cloud.xcan.angus.core.gm.domain.systemversion;

/**
 * 版本类型枚举
 */
public enum VersionType {
    /**
     * 主版本
     */
    MAJOR,

    /**
     * 次版本
     */
    MINOR,

    /**
     * 补丁版本
     */
    PATCH,

    /**
     * 热修复
     */
    HOTFIX,

    /**
     * 候选发布
     */
    RELEASE_CANDIDATE,

    /**
     * 测试版本
     */
    BETA
}
