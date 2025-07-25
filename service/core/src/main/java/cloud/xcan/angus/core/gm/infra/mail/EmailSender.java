package cloud.xcan.angus.core.gm.infra.mail;


import static cloud.xcan.angus.spec.SpecConstant.DEFAULT_ENCODING;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static java.util.Collections.emptyMap;
import static org.springframework.ui.freemarker.FreeMarkerTemplateUtils.processTemplateIntoString;

import cloud.xcan.angus.api.pojo.Attachment;
import cloud.xcan.angus.core.gm.domain.email.Email;
import cloud.xcan.angus.core.gm.domain.email.server.EmailServer;
import cloud.xcan.angus.core.gm.domain.email.template.EmailTemplate;
import cloud.xcan.angus.core.spring.boot.ApplicationInfo;
import cloud.xcan.angus.spec.annotations.NonNullable;
import cloud.xcan.angus.spec.experimental.Assert;
import cloud.xcan.angus.spec.locale.SupportedLanguage;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeUtility;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileUrlResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailSender {

  private final StringTemplateLoader templateLoader = new StringTemplateLoader();
  private final Configuration allTemplateConfig = new Configuration(Configuration.VERSION_2_3_22);
  private final String BASE_TEMPLATE_KEY = "email_template";
  private final String EVENT_NOTICE_TEMPLATE_KEY = "event_notice_template";

  private final ApplicationInfo applicationInfo;

  private volatile JavaMailSender mailSender;
  private volatile EmailServer emailServer;

  public EmailSender(ApplicationInfo applicationInfo) throws IOException {
    this.applicationInfo = applicationInfo;
    initAndRefreshTemplateConfiguration();
  }

  public void sendMessage(EmailServer emailServer, EmailTemplate emailTemplate,
      Email email) throws Exception {
    MimeMessageHelper helper = getMimeMessageHelper(emailServer, email);
    helper.setTo(email.getActualToAddrData().toArray(new String[]{}));
    if (email.isTemplateEmail()) {
      Map<String, String> addrParams = emptyMap();
      if (isNotEmpty(email.getTemplateParamData())) {
        addrParams = email.getTemplateParamData().values().stream()
            .findFirst().orElse(emptyMap());
      }

      Template bizTemplate = getAndCachedTemplate(email.getBizKey().isEventNotice()
          ? email.getContent() : emailTemplate.getContent(), email);
      Template baseTemplate = allTemplateConfig.getTemplate(
          getBaseTemplateKey(email.getLanguage(), email.getBizKey().isEventNotice()));

      String bizContent = processTemplateIntoString(bizTemplate, addrParams);
      String finalContent = processTemplateIntoString(baseTemplate, Map.of("content", bizContent));

      helper.setText(finalContent, true);
    } else {
      helper.setText(email.getContent(), email.getHtml());
    }
    mailSender.send(helper.getMimeMessage());
  }

  public void sendBatchMessage(EmailServer emailServer, EmailTemplate emailTemplate,
      Email email) throws Exception {
    MimeMessageHelper helper = getMimeMessageHelper(emailServer, email);
    Template bizTemplate = null;
    Template baseTemplate = null;
    if (email.isTemplateEmail()) {
      bizTemplate = getAndCachedTemplate(email.getBizKey().isEventNotice()
          ? email.getContent() : emailTemplate.getContent(), email);
      baseTemplate = allTemplateConfig.getTemplate(
          getBaseTemplateKey(email.getLanguage(), email.getBizKey().isEventNotice()));
    }

    for (String toAddr : email.getActualToAddrData()) {
      helper.setTo(toAddr);
      if (email.isTemplateEmail()) {
        if (Objects.isNull(bizTemplate) || Objects.isNull(baseTemplate)) {
          log.error("Failed to send email to {}: template is null (bizTemplate={}, baseTemplate={})", 
              toAddr, bizTemplate != null, baseTemplate != null);
          throw new IllegalStateException("Email template is not available for sending");
        }
        Map<String, String> addrParams = email.getTemplateParamData()
            .getOrDefault(toAddr, emptyMap());
        String bizContent = processTemplateIntoString(bizTemplate, addrParams);
        String finalContent = processTemplateIntoString(baseTemplate,
            Map.of("content", bizContent));

        helper.setText(finalContent, true);
        mailSender.send(helper.getMimeMessage());
      } else {
        helper.setText(email.getContent(), email.getHtml());
      }
    }
  }

  private Template getAndCachedTemplate(String content, Email email)
      throws Exception {
    String bizTemplateKey = getBizTemplateKey(email);
    // Cache biz template
    templateLoader.putTemplate(bizTemplateKey, content);
    // Return base template
    return allTemplateConfig.getTemplate(bizTemplateKey);
  }

  private MimeMessageHelper getMimeMessageHelper(EmailServer emailServer,
      Email email) throws Exception {
    MimeMessage message = setupMailSender(emailServer).createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true, DEFAULT_ENCODING);
    helper.setFrom(email.getFromAddr());
    helper.setSubject(email.getSubject());
    if (isNotEmpty(email.getCcAddrData())) {
      helper.setCc(email.getCcAddrData().toArray(new String[]{}));
    }
    if (isNotEmpty(email.getAttachmentData())) {
      for (Attachment attachment : email.getAttachmentData()) {
        helper.addAttachment(MimeUtility.encodeWord(attachment.getName()),
            new FileUrlResource(new java.net.URL(attachment.getUrl())));
      }
    }
    return helper;
  }

  public synchronized void initAndRefreshTemplateConfiguration() throws IOException {
    allTemplateConfig.setDefaultEncoding(DEFAULT_ENCODING);
    allTemplateConfig.setTemplateLoader(templateLoader);
    // Fix:: File inside jar is not visible for spring: cannot be resolved to absolute file path because it does not reside in the file system: jar:file:/data/apps/bs-dev_xcan-email-proxy-boot/xcan-email-proxy.boot-1.0.0.jar!/BOOT-INF/classes!/templates/xcan_email_template-cloudservice-zh_CN.html
    // Make sure you are using resource.getInputStream() not resource.getFile() when loading from inside a jar file.
    String zhCnTemplatePath = getBaseZhCnTemplatePath();
    InputStream zhCnTemplate = new ClassPathResource(zhCnTemplatePath).getInputStream();
    templateLoader.putTemplate(getBaseTemplateKey(SupportedLanguage.zh_CN, false),
        IOUtils.toString(zhCnTemplate, DEFAULT_ENCODING));
    String enTemplatePath = getBaseEnTemplatePath();
    InputStream enTemplate = new ClassPathResource(enTemplatePath).getInputStream();
    templateLoader.putTemplate(getBaseTemplateKey(SupportedLanguage.en, false),
        IOUtils.toString(enTemplate, DEFAULT_ENCODING));
    log.info("Initialize or refresh template configuration, template : {}", BASE_TEMPLATE_KEY);

    String eventZhCnTemplatePath = getEventNoticeZhCnTemplatePath();
    InputStream eventZhCnTemplate = new ClassPathResource(eventZhCnTemplatePath).getInputStream();
    templateLoader.putTemplate(getEventNoticeTemplateKey(SupportedLanguage.zh_CN),
        IOUtils.toString(eventZhCnTemplate, DEFAULT_ENCODING));
    String eventEnTemplatePath = getEventNoticeEnTemplatePath();
    InputStream eventEnTemplate = new ClassPathResource(eventEnTemplatePath).getInputStream();
    templateLoader.putTemplate(getEventNoticeTemplateKey(SupportedLanguage.en),
        IOUtils.toString(eventEnTemplate, DEFAULT_ENCODING));
    log.info("Initialize or refresh template configuration, template : {}",
        EVENT_NOTICE_TEMPLATE_KEY);
  }

  public synchronized JavaMailSender setupMailSender(EmailServer emailServer) {
    Assert.assertNotNull(emailServer, "EmailServer is required");
    if (Objects.isNull(mailSender) || Objects.isNull(this.emailServer)
        || !emailServer.equals(this.emailServer)) {
      this.mailSender = initOrRefreshEmailSender(emailServer);
      this.emailServer = emailServer;
    }
    return mailSender;
  }

  public synchronized JavaMailSender initOrRefreshEmailSender(EmailServer emailServer) {
    log.info("Initialize or refresh email sender, emailServer {}", emailServer.getName());
    JavaMailSenderImpl sender = new JavaMailSenderImpl();
    sender.setSession(setupServerConfig(emailServer, 6 * 1000, 2 * 60 * 1000));
    log.info("Initialize or refresh email sender complete, emailServer {}", emailServer.getName());
    return sender;
  }

  /**
   * @see `SendEmailWithSslTest`
   * @see `SendEmailWithStarttlsTest`
   */
  public static Session setupServerConfig(EmailServer emailServer, int connectionTimeout,
      int writeTimeout) {
    Properties properties = System.getProperties();
    properties.put("mail.transport.protocol", "smtp");
    properties.put("mail.smtp.socketFactory.fallback", "false");
    properties.put("mail.smtp.quitwait", "false");
    properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
    properties.put("mail.smtp.ssl.trust", "*");
    properties.put("mail.smtp.starttls.enable",
        String.valueOf(emailServer.getStartTlsEnabled())); // True or False oK!!!!
    // properties.put("mail.smtp.timeout", String.valueOf(timeout));  // 5 * 60s
    properties.put("mail.smtp.connectiontimeout", String.valueOf(connectionTimeout));
    properties.put("mail.smtp.writetimeout", String.valueOf(writeTimeout));
    // Enable authentication
    properties.put("mail.smtp.auth", emailServer.getAuthEnabled());

    properties.put("mail.smtp.host", emailServer.getHost());
    properties.put("mail.smtp.port", emailServer.getPort());

    // Enable debug
    properties.put("mail.debug", "true");

    // SSL Factory Config!!!
    if (emailServer.getSslEnabled()) {
      properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
    }
    return Session.getDefaultInstance(properties,
        new jakarta.mail.Authenticator() {
          // override the getPasswordAuthentication
          @Override
          protected PasswordAuthentication getPasswordAuthentication() {
            if (Objects.nonNull(emailServer.getAuthAccountData()) && 
                Objects.nonNull(emailServer.getAuthAccountData().getAccount()) &&
                Objects.nonNull(emailServer.getAuthAccountData().getPassword())) {
              return new PasswordAuthentication(emailServer.getAuthAccountData().getAccount(),
                  emailServer.getAuthAccountData().getPassword());
            }
            return null;
          }
        });
  }

  @NonNullable
  private String getBizTemplateKey(Email email) {
    return email.getTemplateCode() + "-" + email.getLanguage();
  }

  @NonNullable
  private String getBaseTemplateKey(SupportedLanguage language, boolean isEventNotice) {
    if (isEventNotice) {
      return getEventNoticeTemplateKey(language);
    }
    return BASE_TEMPLATE_KEY + "-" + (applicationInfo.isPrivateEdition()
        ? "privatization" : "cloudservice") + "-" + language.getValue();
  }

  @NonNullable
  private String getBaseZhCnTemplatePath() {
    return "templates/" + BASE_TEMPLATE_KEY + "-" + (applicationInfo.isPrivateEdition()
        ? "privatization" : "cloudservice") + "-zh_CN.html";
  }

  @NonNullable
  private String getBaseEnTemplatePath() {
    return "templates/" + BASE_TEMPLATE_KEY + "-" + (applicationInfo.isPrivateEdition()
        ? "privatization" : "cloudservice") + "-en.html";
  }

  @NonNullable
  private String getEventNoticeTemplateKey(SupportedLanguage language) {
    return EVENT_NOTICE_TEMPLATE_KEY + "-" + language.getValue();
  }

  @NonNullable
  private String getEventNoticeZhCnTemplatePath() {
    return "templates/" + EVENT_NOTICE_TEMPLATE_KEY + "-zh_CN.html";
  }

  @NonNullable
  private String getEventNoticeEnTemplatePath() {
    return "templates/" + EVENT_NOTICE_TEMPLATE_KEY + "-en.html";
  }
}
