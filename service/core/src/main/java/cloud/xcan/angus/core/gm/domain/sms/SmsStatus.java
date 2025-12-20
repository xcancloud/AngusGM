package cloud.xcan.angus.core.gm.domain.sms;

/**
 * SMS状态枚举
 */
public enum SmsStatus {
    /**
     * 待发送
     */
    PENDING,

    /**
     * 发送中
     */
    SENDING,

    /**
     * 已发送
     */
    SENT,

    /**
     * 已送达
     */
    DELIVERED,

    /**
     * 发送失败
     */
    FAILED,

    /**
     * 已取消
     */
    CANCELLED
}
