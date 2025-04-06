package cloud.xcan.angus.api.commonlink.authuser;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.security.model.CustomOAuth2User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;

@Slf4j
@Entity
@Table(name = "oauth2_user")
public class AuthUser extends CustomOAuth2User {
  // Fields -> Do in parent class.

  private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

  /**
   * OAuth2 User fields
   */
  protected String username;

  @Nullable
  protected String password;

  protected boolean enabled;

  @Column(name = "account_non_expired")
  protected boolean accountNonExpired;

  @Column(name = "account_non_locked")
  protected boolean accountNonLocked;

  /**
   * Is password none expired.
   */
  @Column(name = "credentials_non_expired")
  protected boolean credentialsNonExpired;

  @Transient
  protected Set<GrantedAuthority> authorities;

  /**
   * AngusGM User Info.
   */
  @Id
  protected String id;

  @Column(name = "first_name")
  protected String firstName;

  @Column(name = "last_name")
  protected String lastName;

  @Column(name = "full_name")
  protected String fullName;

  @Column(name = "password_strength")
  protected String passwordStrength;

  @Column(name = "sys_admin")
  protected boolean sysAdmin;

  @Column(name = "to_user")
  protected boolean toUser;

  protected String mobile;

  protected String email;

  @Column(name = "main_dept_id")
  protected String mainDeptId;

  @Column(name = "password_expired_date")
  protected Instant passwordExpiredDate;

  @Column(name = "last_modified_password_date")
  protected Instant lastModifiedPasswordDate;

  @Column(name = "expired_date")
  protected Instant expiredDate;

  protected boolean deleted;

  @Column(name = "tenant_id")
  protected String tenantId;

  @Column(name = "tenant_name")
  protected String tenantName;

  @Column(name = "tenant_real_name_status")
  protected String tenantRealNameStatus;

  @Column(name = "directory_id")
  protected String directoryId;

  @Column(name = "default_language")
  protected String defaultLanguage;

  @Column(name = "default_time_zone")
  protected String defaultTimeZone;

  /**
   * Temp filed for signup.
   */
  @Transient
  protected String itc;
  @Transient
  protected String country;
  @JsonIgnore
  @Transient
  private String clientId;
  @JsonIgnore
  @Transient
  private String clientSource;

  /**
   * Temp filed for business.
   */
  @JsonIgnore
  @Transient
  protected String signupType;
  @JsonIgnore
  @Transient
  protected String signupDeviceId;
  @JsonIgnore
  @Transient
  protected String verificationCode;
  @JsonIgnore
  @Transient
  protected String smsBizKey;
  @JsonIgnore
  @Transient
  protected String emailBizKey;
  @JsonIgnore
  @Transient
  protected String linkSecret;
  @JsonIgnore
  @Transient
  protected boolean setPassword;
  @JsonIgnore
  @Transient
  protected String invitationCode;

  public AuthUser() {
  }

  public AuthUser(String username, String password,
      Collection<? extends GrantedAuthority> authorities) {
    super(username, password, authorities);
  }

  public AuthUser(String username, String password, boolean enabled, boolean accountNonExpired,
      boolean credentialsNonExpired, boolean accountNonLocked,
      Collection<? extends GrantedAuthority> authorities) {
    super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked,
        authorities);
  }

  public AuthUser(String username, String password, boolean enabled, boolean accountNonExpired,
      boolean credentialsNonExpired, boolean accountNonLocked,
      Collection<? extends GrantedAuthority> authorities, String id, String firstName,
      String lastName, String fullName, String passwordStrength, boolean sysAdmin, boolean toUser,
      String mobile, String email, String mainDeptId, Instant passwordExpiredDate,
      Instant lastModifiedPasswordDate, Instant expiredDate, boolean deleted, String tenantId,
      String tenantName, String tenantRealNameStatus, String directoryId, String defaultLanguage,
      String defaultTimeZone) {
    super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked,
        authorities, id, firstName, lastName, fullName, passwordStrength, sysAdmin, toUser, mobile,
        email, mainDeptId, passwordExpiredDate, lastModifiedPasswordDate, expiredDate, deleted,
        tenantId, tenantName, tenantRealNameStatus, directoryId, defaultLanguage, defaultTimeZone);
  }

  /**
   * Copy a User with AuthUserBuilder
   */
  public static AuthUser with(String username, AuthUser user, List<GrantedAuthority> authorities) {
    return newBuilder().username(username).password(user.password)
        .disabled(!user.enabled).accountExpired(!user.accountNonExpired)
        .accountLocked(!user.accountNonLocked).credentialsExpired(!user.credentialsNonExpired)
        .authorities(authorities).id(user.id).firstName(user.firstName).lastName(user.lastName)
        .fullName(user.fullName).passwordStrength(user.passwordStrength).sysAdmin(user.sysAdmin)
        .toUser(user.toUser).mobile(user.mobile).email(user.email).mainDeptId(user.mainDeptId)
        .passwordExpiredDate(user.passwordExpiredDate)
        .lastModifiedPasswordDate(user.lastModifiedPasswordDate).expiredDate(user.expiredDate)
        .deleted(user.deleted).tenantId(user.tenantId).tenantName(user.tenantName)
        .tenantRealNameStatus(user.tenantRealNameStatus)
        .build();
  }

  /**
   * Creates a AuthUserBuilder with a specified username
   *
   * @param username the username to use
   * @return the AuthUserBuilder
   */
  public static AuthUserBuilder username(String username) {
    return newBuilder().username(username);
  }

  /**
   * Creates a AuthUserBuilder
   *
   * @return the AuthUserBuilder
   */
  public static AuthUserBuilder newBuilder() {
    return new AuthUserBuilder();
  }

  /**
   * <p>
   * <b>WARNING:</b> This method is considered unsafe for production and is only
   * intended for sample applications.
   * </p>
   * <p>
   * Creates a user and automatically encodes the provided password using
   * {@code PasswordEncoderFactories.createDelegatingPasswordEncoder()}. For example:
   * </p>
   *
   * <pre>
   * <code>
   * UserDetails user = AuthUser.withDefaultPasswordEncoder()
   *     .username("user")
   *     .password("password")
   *     .roles("USER")
   *     .build();
   * // outputs {bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG
   * System.out.println(user.getPassword());
   * </code> </pre>
   * <p>
   * This is not safe for production (it is intended for getting started experience) because the
   * password "password" is compiled into the source code and then is included in memory at the time
   * of creation. This means there are still ways to recover the plain text password making it
   * unsafe. It does provide a slight improvement to using plain text passwords since the
   * UserDetails password is securely hashed. This means if the UserDetails password is accidentally
   * exposed, the password is securely stored.
   * <p>
   * In a production setting, it is recommended to hash the password ahead of time. For example:
   *
   * <pre>
   * <code>
   * PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
   * // outputs {bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG
   * // remember the password that is printed out and use in the next step
   * System.out.println(encoder.encode("password"));
   * </code> </pre>
   *
   * <pre>
   * <code>
   * UserDetails user = AuthUser.withUsername("user")
   *     .password("{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG")
   *     .roles("USER")
   *     .build();
   * </code> </pre>
   *
   * @return a AuthUserBuilder that automatically encodes the password with the default
   * PasswordEncoder
   * @deprecated Using this method is not considered safe for production, but is acceptable for
   * demos and getting started. For production purposes, ensure the password is encoded externally.
   * See the method Javadoc for additional details. There are no plans to remove this support. It is
   * deprecated to indicate that this is considered insecure for production purposes.
   */
  @Deprecated
  public static AuthUserBuilder defaultPasswordEncoder() {
    log.warn("AuthUser.withDefaultPasswordEncoder() is considered unsafe for production "
        + "and is only intended for sample applications.");
    PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    return newBuilder().passwordEncoder(encoder::encode);
  }

  public static AuthUserBuilder userDetails(UserDetails userDetails) {
    // @formatter:off
    return username(userDetails.getUsername())
        .password(userDetails.getPassword())
        .accountExpired(!userDetails.isAccountNonExpired())
        .accountLocked(!userDetails.isAccountNonLocked())
        .authorities(userDetails.getAuthorities())
        .credentialsExpired(!userDetails.isCredentialsNonExpired())
        .disabled(!userDetails.isEnabled());
    // @formatter:on
  }

  private static class AuthorityComparator implements Comparator<GrantedAuthority>, Serializable {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    @Override
    public int compare(GrantedAuthority g1, GrantedAuthority g2) {
      // Neither should ever be null as each entry is checked before adding it to
      // the set. If the authority is null, it is a custom authority and should
      // precede others.
      if (g2.getAuthority() == null) {
        return -1;
      }
      if (g1.getAuthority() == null) {
        return 1;
      }
      return g1.getAuthority().compareTo(g2.getAuthority());
    }

  }

  /**
   * Builds the user to be added. At minimum the username, password, and authorities should
   * provided. The remaining attributes have reasonable defaults.
   */
  public static final class AuthUserBuilder {

    /**
     * OAuth2 User fields
     */
    private String username;
    private String password;
    private boolean disabled = false;
    private boolean accountExpired = false;
    private boolean accountLocked = false;
    private boolean credentialsExpired = false;

    private List<GrantedAuthority> authorities = new ArrayList<>();
    private Function<String, String> passwordEncoder = (password) -> password;

    /**
     * AngusGM User Info.
     */
    private String id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String passwordStrength;
    private boolean sysAdmin = false;
    private boolean toUser = false;
    private String mobile;
    private String email;
    private String mainDeptId;
    private Instant passwordExpiredDate;
    private Instant lastModifiedPasswordDate;
    private Instant expiredDate;
    private boolean deleted = false;
    private String tenantId;
    private String tenantName;
    private String tenantRealNameStatus;

    private String directoryId;
    private String defaultLanguage;
    private String defaultTimeZone;

    /**
     * Creates a new instance
     */
    private AuthUserBuilder() {
    }

    /**
     * Populates the username. This attribute is required.
     *
     * @param username the username. Cannot be null.
     * @return the {@link AuthUserBuilder} for method chaining (i.e. to populate additional
     * attributes for this user)
     */
    public AuthUserBuilder username(String username) {
      Assert.notNull(username, "username cannot be null");
      this.username = username;
      return this;
    }

    /**
     * Populates the password. This attribute is not required.
     *
     * @param password the password. Can be null.
     * @return the {@link AuthUserBuilder} for method chaining (i.e. to populate additional
     * attributes for this user)
     */
    public AuthUserBuilder password(String password) {
      this.password = password;
      return this;
    }

    /**
     * Defines if the account is disabled or not. Default is false.
     *
     * @param disabled true if the account is disabled, false otherwise
     * @return the {@link AuthUserBuilder} for method chaining (i.e. to populate additional
     * attributes for this user)
     */
    public AuthUserBuilder disabled(boolean disabled) {
      this.disabled = disabled;
      return this;
    }

    /**
     * Encodes the current password (if non-null) and any future passwords supplied to
     * {@link #password(String)}.
     *
     * @param encoder the encoder to use
     * @return the {@link AuthUserBuilder} for method chaining (i.e. to populate additional
     * attributes for this user)
     */
    public AuthUserBuilder passwordEncoder(Function<String, String> encoder) {
      Assert.notNull(encoder, "encoder cannot be null");
      this.passwordEncoder = encoder;
      return this;
    }

    /**
     * Defines if the account is expired or not. Default is false.
     *
     * @param accountExpired true if the account is expired, false otherwise
     * @return the {@link AuthUserBuilder} for method chaining (i.e. to populate additional
     * attributes for this user)
     */
    public AuthUserBuilder accountExpired(boolean accountExpired) {
      this.accountExpired = accountExpired;
      return this;
    }

    /**
     * Defines if the account is locked or not. Default is false.
     *
     * @param accountLocked true if the account is locked, false otherwise
     * @return the {@link AuthUserBuilder} for method chaining (i.e. to populate additional
     * attributes for this user)
     */
    public AuthUserBuilder accountLocked(boolean accountLocked) {
      this.accountLocked = accountLocked;
      return this;
    }

    /**
     * Defines if the credentials are expired or not. Default is false.
     *
     * @param credentialsExpired true if the credentials are expired, false otherwise
     * @return the {@link AuthUserBuilder} for method chaining (i.e. to populate additional
     * attributes for this user)
     */
    public AuthUserBuilder credentialsExpired(boolean credentialsExpired) {
      this.credentialsExpired = credentialsExpired;
      return this;
    }

    /**
     * Populates the roles. This method is a shortcut for calling {@link #authorities(String...)},
     * but automatically prefixes each entry with "ROLE_". This means the following:
     *
     * <code>
     * builder.roles("USER","ADMIN");
     * </code>
     * <p>
     * is equivalent to
     *
     * <code>
     * builder.authorities("ROLE_USER","ROLE_ADMIN");
     * </code>
     *
     * <p>
     * This attribute is required, but can also be populated with {@link #authorities(String...)}.
     * </p>
     *
     * @param roles the roles for this user (i.e. USER, ADMIN, etc). Cannot be null, contain null
     *              values or start with "ROLE_"
     * @return the {@link AuthUserBuilder} for method chaining (i.e. to populate additional
     * attributes for this user)
     */
    public AuthUserBuilder roles(String... roles) {
      List<GrantedAuthority> authorities = new ArrayList<>(roles.length);
      for (String role : roles) {
        Assert.isTrue(!role.startsWith("ROLE_"),
            () -> role + " cannot start with ROLE_ (it is automatically added)");
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
      }
      return authorities(authorities);
    }

    /**
     * Populates the authorities. This attribute is required.
     *
     * @param authorities the authorities for this user. Cannot be null, or contain null values
     * @return the {@link AuthUserBuilder} for method chaining (i.e. to populate additional
     * attributes for this user)
     * @see #roles(String...)
     */
    public AuthUserBuilder authorities(GrantedAuthority... authorities) {
      Assert.notNull(authorities, "authorities cannot be null");
      return authorities(Arrays.asList(authorities));
    }

    /**
     * Populates the authorities. This attribute is required.
     *
     * @param authorities the authorities for this user. Cannot be null, or contain null values
     * @return the {@link AuthUserBuilder} for method chaining (i.e. to populate additional
     * attributes for this user)
     * @see #roles(String...)
     */
    public AuthUserBuilder authorities(
        Collection<? extends GrantedAuthority> authorities) {
      Assert.notNull(authorities, "authorities cannot be null");
      this.authorities = new ArrayList<>(authorities);
      return this;
    }

    /**
     * Populates the authorities. This attribute is required.
     *
     * @param authorities the authorities for this user (i.e. ROLE_USER, ROLE_ADMIN, etc). Cannot be
     *                    null, or contain null values
     * @return the {@link AuthUserBuilder} for method chaining (i.e. to populate additional
     * attributes for this user)
     * @see #roles(String...)
     */
    public AuthUserBuilder authorities(String... authorities) {
      Assert.notNull(authorities, "authorities cannot be null");
      return authorities(AuthorityUtils.createAuthorityList(authorities));
    }

    public AuthUserBuilder id(String id) {
      this.id = id;
      return this;
    }

    public AuthUserBuilder firstName(String firstName) {
      this.firstName = firstName;
      return this;
    }

    public AuthUserBuilder lastName(String lastName) {
      this.lastName = lastName;
      return this;
    }

    public AuthUserBuilder fullName(String fullName) {
      this.fullName = fullName;
      return this;
    }

    public AuthUserBuilder passwordStrength(String passwordStrength) {
      this.passwordStrength = passwordStrength;
      return this;
    }

    public AuthUserBuilder sysAdmin(boolean sysAdmin) {
      this.sysAdmin = sysAdmin;
      return this;
    }

    public AuthUserBuilder toUser(boolean toUser) {
      this.toUser = toUser;
      return this;
    }

    public AuthUserBuilder mobile(String mobile) {
      this.mobile = mobile;
      return this;
    }

    public AuthUserBuilder email(String email) {
      this.email = email;
      return this;
    }

    public AuthUserBuilder mainDeptId(String mainDeptId) {
      this.mainDeptId = mainDeptId;
      return this;
    }

    public AuthUserBuilder passwordExpiredDate(Instant passwordExpiredDate) {
      this.passwordExpiredDate = passwordExpiredDate;
      return this;
    }

    public AuthUserBuilder lastModifiedPasswordDate(Instant lastModifiedPasswordDate) {
      this.lastModifiedPasswordDate = lastModifiedPasswordDate;
      return this;
    }

    public AuthUserBuilder expiredDate(Instant expiredDate) {
      this.expiredDate = expiredDate;
      return this;
    }

    public AuthUserBuilder deleted(boolean deleted) {
      this.deleted = deleted;
      return this;
    }

    public AuthUserBuilder tenantId(String tenantId) {
      this.tenantId = tenantId;
      return this;
    }

    public AuthUserBuilder tenantName(String tenantName) {
      this.tenantName = tenantName;
      return this;
    }

    public AuthUserBuilder tenantRealNameStatus(String tenantRealNameStatus) {
      this.tenantRealNameStatus = tenantRealNameStatus;
      return this;
    }

    public AuthUserBuilder directoryId(String directoryId) {
      this.directoryId = directoryId;
      return this;
    }

    public AuthUserBuilder defaultLanguage(String defaultLanguage) {
      this.defaultLanguage = defaultLanguage;
      return this;
    }

    public AuthUserBuilder defaultTimeZone(String defaultTimeZone) {
      this.defaultTimeZone = defaultTimeZone;
      return this;
    }

    public AuthUser build() {
      String encodedPassword = isNotEmpty(this.password)
          ? this.passwordEncoder.apply(this.password) : null;
      return new AuthUser(this.username, encodedPassword, !this.disabled,
          !this.accountExpired, !this.credentialsExpired, !this.accountLocked, this.authorities,
          this.id, this.firstName, this.lastName, this.fullName, this.passwordStrength,
          this.sysAdmin, this.toUser, this.mobile, this.email, this.mainDeptId,
          this.passwordExpiredDate, this.lastModifiedPasswordDate, this.expiredDate,
          this.deleted, this.tenantId, this.tenantName, this.tenantRealNameStatus,
          this.directoryId, this.defaultLanguage, this.defaultTimeZone);
    }
  }
}
