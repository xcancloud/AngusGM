package cloud.xcan.angus.core.gm.infra.authentication.config;

import static cloud.xcan.angus.security.authentication.password.OAuth2PasswordAuthenticationProviderUtils.DEFAULT_ENCODING_ID;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getExtension;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

import cloud.xcan.angus.core.gm.domain.user.directory.UserDirectory;
import cloud.xcan.angus.core.gm.domain.user.directory.model.DirectorySchema;
import cloud.xcan.angus.core.gm.infra.authentication.checker.CustomUserPreAuthenticationChecks;
import cloud.xcan.angus.core.gm.infra.authentication.service.JdbcUserAuthoritiesLazyServiceImpl;
import cloud.xcan.angus.core.gm.infra.authentication.service.RedisLinkSecretService;
import cloud.xcan.angus.security.authentication.service.LinkSecretService;
import cloud.xcan.angus.security.repository.JdbcUserAuthoritiesLazyService;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

@Configuration
public class OAuth2AuthorizationServerConfig {

  /**
   * Support login with email verification code and SMS verification code.
   */
  @Bean
  public LinkSecretService linkSecretService() {
    return new RedisLinkSecretService();
  }

  /**
   * Support lazy initialization and loading of authorization policies, resource, and operational
   * role permissions.
   */
  @Bean
  public JdbcUserAuthoritiesLazyService jdbcUserAuthoritiesLazyService() {
    return new JdbcUserAuthoritiesLazyServiceImpl();
  }

  /**
   * Support for allowing customers to customize checks.
   */
  @Bean
  public CustomUserPreAuthenticationChecks defaultPreAuthenticationChecks() {
    return new CustomUserPreAuthenticationChecks();
  }

  /**
   * Extend LDAP login support.
   */
  @Bean
  @Primary
  public PasswordEncoder passwordEncoder() {
    return createDelegatingPasswordEncoder();
  }

  /**
   * Creates a {@link DelegatingPasswordEncoder} with default mappings. Additional mappings may be
   * added and the encoding will be updated to conform with best practices. However, due to the
   * nature of {@link DelegatingPasswordEncoder} the updates should not impact users. The mappings
   * current are:
   *
   * <ul>
   * <li>bcrypt - {@link BCryptPasswordEncoder} (Also used for encoding)</li>
   * <li>ldap -
   * {@link org.springframework.security.crypto.password.LdapShaPasswordEncoder}</li>
   * <li>MD4 -
   * {@link org.springframework.security.crypto.password.Md4PasswordEncoder}</li>
   * <li>MD5 - {@code new MessageDigestPasswordEncoder("MD5")}</li>
   * <li>noop -
   * {@link org.springframework.security.crypto.password.NoOpPasswordEncoder}</li>
   * <li>pbkdf2 - {@link Pbkdf2PasswordEncoder}</li>
   * <li>scrypt - {@link SCryptPasswordEncoder}</li>
   * <li>SHA-1 - {@code new MessageDigestPasswordEncoder("SHA-1")}</li>
   * <li>SHA-256 - {@code new MessageDigestPasswordEncoder("SHA-256")}</li>
   * <li>sha256 -
   * {@link org.springframework.security.crypto.password.StandardPasswordEncoder}</li>
   * <li>argon2 - {@link Argon2PasswordEncoder}</li>
   * </ul>
   *
   * @return the {@link PasswordEncoder} to use
   */
  @SuppressWarnings("deprecation")
  public static PasswordEncoder createDelegatingPasswordEncoder() {
    Map<String, PasswordEncoder> encoders = new HashMap<>();
    encoders.put(DEFAULT_ENCODING_ID, new BCryptPasswordEncoder());
    //encoders.put("ldap", new org.springframework.security.crypto.password.LdapShaPasswordEncoder());
    encoders.put("SHA", new org.springframework.security.crypto.password.LdapShaPasswordEncoder());
    encoders.put("MD4", new org.springframework.security.crypto.password.Md4PasswordEncoder());
    encoders.put("MD5",
        new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("MD5"));
    encoders.put("noop",
        org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance());
    encoders.put("pbkdf2", Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_5());
    encoders.put("pbkdf2@SpringSecurity_v5_8",
        Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8());
    encoders.put("scrypt", SCryptPasswordEncoder.defaultsForSpringSecurity_v4_1());
    encoders.put("scrypt@SpringSecurity_v5_8",
        SCryptPasswordEncoder.defaultsForSpringSecurity_v5_8());
    encoders.put("SHA-1",
        new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("SHA-1"));
    encoders.put("SHA-256",
        new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("SHA-256"));
    encoders
        .put("sha256", new org.springframework.security.crypto.password.StandardPasswordEncoder());
    //encoders.put("argon2", new Argon2PasswordEncoder());
    encoders.put("LDAP-PROXY", LdapPasswordConnection.getInstance());
    return new DelegatingPasswordEncoder(DEFAULT_ENCODING_ID, encoders);
  }
}

@Slf4j
final class LdapPasswordConnection implements PasswordEncoder {

  private static final LdapPasswordConnection INSTANCE = new LdapPasswordConnection();

  private LdapPasswordConnection() {
  }

  @Override
  public String encode(CharSequence rawPassword) {
    return rawPassword.toString();
  }

  @Override
  public boolean matches(CharSequence rawPassword, String encodedPassword) {
    // return rawPassword.toString().equals(encodedPassword);
    Object ou = getExtension("fullName"); // CN
    if (isNull(ou)) {
      log.warn("Directory full name not found in context");
      return false;
    }
    Object ldap = getExtension("userDirectory");
    if (Objects.isNull(ldap)) {
      log.warn("Directory not found in context");
      return false;
    }
    return tryConnectLdapServer(ou.toString(), rawPassword, (UserDirectory) ldap);
  }

  public static LdapPasswordConnection getInstance() {
    return INSTANCE;
  }

  private boolean tryConnectLdapServer(String ou, CharSequence rawPassword, UserDirectory ldap) {
    LDAPConnection ldapConnection = null;
    try {
      DirectorySchema schema = ldap.getSchemaData();
      String userDn = isNotEmpty(schema.getAdditionalUserDn())
          ? "cn=" + ou + "," + ldap.getSchemaData().getAdditionalUserDn() + "," + schema.getBaseDn()
          : "cn=" + ou + "," + schema.getBaseDn();
      ldapConnection = new LDAPConnection(ldap.getServerData().getHost(),
          ldap.getServerData().getPort(), userDn, rawPassword.toString());
    } catch (LDAPException e) {
      return false;
    } finally {
      if (nonNull(ldapConnection) && ldapConnection.isConnected()) {
        ldapConnection.close();
      }
    }
    return true;
  }

}
