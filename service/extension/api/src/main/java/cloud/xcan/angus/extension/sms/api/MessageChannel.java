package cloud.xcan.angus.extension.sms.api;

import java.io.Serializable;

/**
 * <p>
 * SMS service provider information that contains configuration details
 * for connecting to and authenticating with third-party SMS services.
 * </p>
 * <p>
 * This class encapsulates all necessary connection parameters including
 * authentication credentials, service endpoints, and provider-specific
 * channel identifiers required for SMS delivery.
 * </p>
 *
 * @author XiaoLong Liu
 * @since 1.0.0
 */
public class MessageChannel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * <p>
     * Display name of the SMS service provider.
     * </p>
     * This is used for identification purposes in user interfaces
     * and administrative displays.
     */
    private String name;

    /**
     * <p>
     * Logo URL or path for the SMS service provider.
     * </p>
     * Used for visual representation in administrative interfaces
     * and configuration screens.
     */
    private String logo;

    /**
     * <p>
     * Service endpoint URL for the SMS provider's API.
     * </p>
     * This is the base URL where SMS API requests will be sent.
     * Should include protocol (http/https) and may include port if non-standard.
     */
    private String endpoint;

    /**
     * <p>
     * Access key identifier for authentication with the SMS service provider.
     * </p>
     * This is typically provided by the SMS service provider upon account creation
     * and is used for API authentication along with the access key secret.
     */
    private String accessKeyId;

    /**
     * <p>
     * Secret access key for authentication with the SMS service provider.
     * </p>
     * This sensitive credential should be kept secure and is used in conjunction
     * with the access key ID for API authentication.
     */
    private String accessKeySecret;

    /**
     * <p>
     * Third-party SMS channel identifier or sender number.
     * </p>
     * Provider-specific channel identifier such as Huawei Cloud sender number,
     * Alibaba Cloud sign name, or other provider-specific routing identifiers.
     */
    private String thirdChannelNo;

    /**
     * <p>
     * Default constructor for framework compatibility and deserialization.
     * </p>
     */
    public MessageChannel() {
    }

    /**
     * <p>
     * Constructor with all required parameters for creating a complete MessageChannel object.
     * </p>
     *
     * @param name Display name of the SMS service provider
     * @param logo Logo URL or path for the provider
     * @param endpoint Service endpoint URL for API requests
     * @param accessKeyId Access key identifier for authentication
     * @param accessKeySecret Secret access key for authentication
     * @param thirdChannelNo Provider-specific channel identifier
     */
    public MessageChannel(String name, String logo, String endpoint, String accessKeyId,
                         String accessKeySecret, String thirdChannelNo) {
        this.name = name;
        this.logo = logo;
        this.endpoint = endpoint;
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        this.thirdChannelNo = thirdChannelNo;
    }

    /**
     * <p>
     * Gets the provider logo URL or path.
     * </p>
     *
     * @return the logo URL or path string
     */
    public String getLogo() {
        return logo;
    }

    /**
     * <p>
     * Sets the provider logo and returns this instance for method chaining.
     * </p>
     *
     * @param logo the logo URL or path to set
     * @return this MessageChannel instance for fluent API usage
     */
    public MessageChannel setLogo(String logo) {
        this.logo = logo;
        return this;
    }

    /**
     * <p>
     * Gets the SMS service provider name.
     * </p>
     *
     * @return the provider name string
     */
    public String getName() {
        return name;
    }

    /**
     * <p>
     * Sets the provider name and returns this instance for method chaining.
     * </p>
     *
     * @param name the provider name to set
     * @return this MessageChannel instance for fluent API usage
     */
    public MessageChannel setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * <p>
     * Gets the service endpoint URL.
     * </p>
     *
     * @return the endpoint URL string
     */
    public String getEndpoint() {
        return endpoint;
    }

    /**
     * <p>
     * Sets the service endpoint URL and returns this instance for method chaining.
     * </p>
     *
     * @param endpoint the endpoint URL to set
     * @return this MessageChannel instance for fluent API usage
     */
    public MessageChannel setEndpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    /**
     * <p>
     * Gets the access key identifier.
     * </p>
     *
     * @return the access key ID string
     */
    public String getAccessKeyId() {
        return accessKeyId;
    }

    /**
     * <p>
     * Sets the access key identifier and returns this instance for method chaining.
     * </p>
     *
     * @param accessKeyId the access key ID to set
     * @return this MessageChannel instance for fluent API usage
     */
    public MessageChannel setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
        return this;
    }

    /**
     * <p>
     * Gets the secret access key.
     * </p>
     *
     * @return the access key secret string
     */
    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    /**
     * <p>
     * Sets the secret access key and returns this instance for method chaining.
     * </p>
     *
     * @param accessKeySecret the access key secret to set
     * @return this MessageChannel instance for fluent API usage
     */
    public MessageChannel setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
        return this;
    }

    /**
     * <p>
     * Gets the third-party channel identifier.
     * </p>
     *
     * @return the third-party channel number or identifier string
     */
    public String getThirdChannelNo() {
        return thirdChannelNo;
    }

    /**
     * <p>
     * Sets the third-party channel identifier and returns this instance for method chaining.
     * </p>
     *
     * @param thirdChannelNo the third-party channel identifier to set
     * @return this MessageChannel instance for fluent API usage
     */
    public MessageChannel setThirdChannelNo(String thirdChannelNo) {
        this.thirdChannelNo = thirdChannelNo;
        return this;
    }
}
