package cloud.xcan.angus.core.gm.domain.auditlog;

/**
 * 审计日志操作枚举
 */
public enum AuditLogAction {
    /**
     * 创建
     */
    CREATE,

    /**
     * 更新
     */
    UPDATE,

    /**
     * 删除
     */
    DELETE,

    /**
     * 登录
     */
    LOGIN,

    /**
     * 登出
     */
    LOGOUT,

    /**
     * 访问
     */
    ACCESS,

    /**
     * 执行
     */
    EXECUTE
}
