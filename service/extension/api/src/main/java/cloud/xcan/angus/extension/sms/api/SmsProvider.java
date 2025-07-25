package cloud.xcan.angus.extension.sms.api;

import static cloud.xcan.angus.spec.SpecConstant.UTF8;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;

import cloud.xcan.angus.plugin.api.ExtensionPersistencePoint;
import cloud.xcan.angus.spec.experimental.SimpleResult;
import cloud.xcan.angus.spec.utils.IOUtils;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public interface SmsProvider extends ExtensionPersistencePoint {

  /**
   * Maximum 500 SMS messages be distributed by business.
   */
  SimpleResult sendSms(Sms sms, MessageChannel channel);

  MessageChannel getInstallationChannel();

  @Override
  default String getInstallSqlScript(String installVersion) {
    if (isEmpty(installVersion)) {
      return null;
    }
    try {
      String sql = String.format("sql/SmsProxyPlugin-install-%s.sql", installVersion);
      InputStream inputStream = getClass().getClassLoader().getResourceAsStream(sql);
      if (isNull(inputStream)) {
        log.warn("Installing SMS plugin, script {} not found", sql);
        return null;
      }
      return IOUtils.toString(inputStream, UTF8);
    } catch (Exception e) {
      log.error("Installing SMS plugin, exception: {}", e.getMessage());
    }
    return null;
  }

  @Override
  default String getUpgradedSqlScript(String upgradedFromVersion) {
    if (isEmpty(upgradedFromVersion)) {
      return null;
    }
    try {
      InputStream inputStream = getClass().getClassLoader()
          .getResourceAsStream("plugin.properties");
      if (Objects.isNull(inputStream)) {
        log.warn("Upgrade SMS plugin, plugin.properties not found");
        return null;
      }
      Properties properties = new Properties();
      properties.load(inputStream);
      String currentVersion = properties.getProperty("plugin.version");
      if (isEmpty(currentVersion)) {
        log.warn("Upgrade SMS plugin, current version information is missing");
        return null;
      }
      String sql = String.format("sql/SmsProxyPlugin-upgrade-%s-to-%s.sql", upgradedFromVersion,
          currentVersion);
      inputStream = getClass().getClassLoader().getResourceAsStream(sql);
      if (isNull(inputStream)) {
        log.warn("Upgrade SMS plugin, script {} not found", sql);
        return null;
      }
      return IOUtils.toString(inputStream, UTF8);
    } catch (Exception e) {
      log.error("Upgrade SMS plugin, exception: {}", e.getMessage());
    }
    return null;
  }

  @Override
  default String getUnInstallSqlScript(String uninstallVersion) {
    if (isEmpty(uninstallVersion)) {
      return null;
    }
    try {
      String sql = String.format("sql/SmsProxyPlugin-uninstall-%s.sql", uninstallVersion);
      InputStream inputStream = getClass().getClassLoader().getResourceAsStream(sql);
      if (Objects.isNull(inputStream)) {
        log.warn("Uninstalling SMS plugin, script {} not found", sql);
        return null;
      }
      return IOUtils.toString(inputStream, UTF8);
    } catch (Exception e) {
      log.error("Uninstalling SMS plugin, exception: {}", e.getMessage());
    }
    return null;
  }
}
