package cloud.xcan.angus.core.gm.domain.backup;

/**
 * 备份状态枚举
 */
public enum BackupStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    FAILED,
    CANCELLED,
    RESTORING
}
