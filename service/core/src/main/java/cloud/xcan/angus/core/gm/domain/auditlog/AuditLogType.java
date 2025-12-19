package cloud.xcan.angus.core.gm.domain.auditlog;

/**
 * 审计日志类型枚举
 */
public enum AuditLogType {
    /**
     * 安全审计
     */
    SECURITY,

    /**
     * 数据审计
     */
    DATA,

    /**
     * 系统审计
     */
    SYSTEM,

    /**
     * 用户审计
     */
    USER,

    /**
     * API审计
     */
    API,

    /**
     * 事务审计
     */
    TRANSACTION
}
