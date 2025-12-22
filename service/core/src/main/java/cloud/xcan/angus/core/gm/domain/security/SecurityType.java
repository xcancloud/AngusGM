package cloud.xcan.angus.core.gm.domain.security;

/**
 * <p>Security type enumeration</p>
 */
public enum SecurityType {
    /**
     * <p>Password policy</p>
     */
    PASSWORD_POLICY,

    /**
     * <p>Two factor authentication</p>
     */
    TWO_FACTOR,

    /**
     * <p>IP whitelist</p>
     */
    IP_WHITELIST,

    /**
     * <p>Session config</p>
     */
    SESSION_CONFIG,

    /**
     * <p>Security event</p>
     */
    SECURITY_EVENT,

    /**
     * <p>Eureka config</p>
     */
    EUREKA_CONFIG,

    /**
     * <p>Session management</p>
     */
    SESSION,

    /**
     * <p>API security</p>
     */
    API_SECURITY,

    /**
     * <p>Data encryption</p>
     */
    DATA_ENCRYPTION,

    /**
     * <p>Access control</p>
     */
    ACCESS_CONTROL
}
