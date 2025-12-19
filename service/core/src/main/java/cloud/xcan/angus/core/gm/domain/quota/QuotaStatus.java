package cloud.xcan.angus.core.gm.domain.quota;

/**
 * 配额状态枚举
 */
public enum QuotaStatus {
    ACTIVE,
    EXCEEDED,
    WARNING,
    SUSPENDED,
    DISABLED
}
