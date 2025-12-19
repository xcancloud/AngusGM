package cloud.xcan.angus.core.gm.domain.backup;

/**
 * 备份类型枚举
 */
public enum BackupType {
    FULL,
    INCREMENTAL,
    DIFFERENTIAL,
    DATABASE,
    FILES,
    CONFIGURATION
}
