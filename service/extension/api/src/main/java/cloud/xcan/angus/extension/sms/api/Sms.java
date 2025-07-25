package cloud.xcan.angus.extension.sms.api;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * SMS information data transfer object that encapsulates all necessary information
 * for sending SMS messages through various SMS service providers.
 * </p>
 * <p>
 * This class follows the builder pattern for fluent API usage and implements
 * Serializable for proper data transfer between distributed components.
 * </p>
 *
 * @author XiaoLong Liu
 * @since 1.0.0
 */
public class Sms implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * <p>
     * SMS signature that identifies the sender organization or service.
     * </p>
     * This signature is typically displayed at the beginning or end of SMS messages
     * and must be registered with the SMS service provider.
     */
    private String sign;

    /**
     * <p>
     * List of mobile phone numbers that will receive the SMS message.
     * </p>
     * Each number should be in valid international format (e.g., +86138xxxxxxxx)
     * or domestic format depending on the SMS provider requirements.
     */
    private List<String> mobiles;

    /**
     * <p>
     * SMS template code generated and provided by the SMS service provider.
     * </p>
     * This code references a pre-approved message template that contains
     * placeholders for dynamic content insertion.
     */
    private String templateCode;

    /**
     * <p>
     * Map containing parameter names and their corresponding values for template substitution.
     * </p>
     * The keys should match the parameter placeholders defined in the SMS template,
     * and values will be substituted into the template before sending.
     */
    private Map<String, String> templateParams;

    /**
     * <p>
     * Default constructor for framework compatibility and deserialization.
     * </p>
     */
    public Sms() {
    }

    /**
     * <p>
     * Constructor with all required parameters for creating a complete SMS object.
     * </p>
     *
     * @param sign SMS signature for sender identification
     * @param mobiles List of recipient mobile phone numbers
     * @param templateCode SMS template code from service provider
     * @param templateParams Parameters for template variable substitution
     */
    public Sms(String sign, List<String> mobiles, String templateCode,
               Map<String, String> templateParams) {
        this.sign = sign;
        this.mobiles = mobiles;
        this.templateCode = templateCode;
        this.templateParams = templateParams;
    }

    /**
     * <p>
     * Gets the SMS template code.
     * </p>
     *
     * @return the template code string
     */
    public String getTemplateCode() {
        return templateCode;
    }

    /**
     * <p>
     * Sets the SMS template code and returns this instance for method chaining.
     * </p>
     *
     * @param templateCode the template code to set
     * @return this Sms instance for fluent API usage
     */
    public Sms setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
        return this;
    }

    /**
     * <p>
     * Gets the template parameters map.
     * </p>
     *
     * @return map of template parameter names to values
     */
    public Map<String, String> getTemplateParams() {
        return templateParams;
    }

    /**
     * <p>
     * Sets the template parameters and returns this instance for method chaining.
     * </p>
     *
     * @param templateParams map of parameter names to values for template substitution
     * @return this Sms instance for fluent API usage
     */
    public Sms setTemplateParams(Map<String, String> templateParams) {
        this.templateParams = templateParams;
        return this;
    }

    /**
     * <p>
     * Gets the SMS signature.
     * </p>
     *
     * @return the SMS signature string
     */
    public String getSign() {
        return sign;
    }

    /**
     * <p>
     * Sets the SMS signature and returns this instance for method chaining.
     * </p>
     *
     * @param sign the SMS signature to set
     * @return this Sms instance for fluent API usage
     */
    public Sms setSign(String sign) {
        this.sign = sign;
        return this;
    }

    /**
     * <p>
     * Gets the list of recipient mobile phone numbers.
     * </p>
     *
     * @return list of mobile phone numbers
     */
    public List<String> getMobiles() {
        return mobiles;
    }

    /**
     * <p>
     * Sets the recipient mobile phone numbers and returns this instance for method chaining.
     * </p>
     *
     * @param mobiles list of mobile phone numbers to receive the SMS
     * @return this Sms instance for fluent API usage
     */
    public Sms setMobiles(List<String> mobiles) {
        this.mobiles = mobiles;
        return this;
    }
}
