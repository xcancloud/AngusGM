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
        System.out.printf("Installing SMS plugin, script %s not found%n", sql);
        return null;
      }
      return IOUtils.toString(inputStream, UTF8);
    } catch (Exception e) {
      System.out.println("Installing SMS plugin, exception: " + e.getMessage());
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
        System.out.printf("Upgrade SMS plugin, plugin.properties not found%n");
        return null;
      }
      Properties properties = new Properties();
      properties.load(inputStream);
      String currentVersion = properties.getProperty("plugin.version");
      if (isEmpty(currentVersion)) {
        System.out.println("Upgrade SMS plugin, current version information is missing");
        return null;
      }
      String sql = String.format("sql/SmsProxyPlugin-upgrade-%s-to-%s.sql", upgradedFromVersion,
          currentVersion);
      inputStream = getClass().getClassLoader().getResourceAsStream(sql);
      if (isNull(inputStream)) {
        System.out.printf("Upgrade SMS plugin, script %s not found%n", sql);
        return null;
      }
      return IOUtils.toString(inputStream, UTF8);
    } catch (Exception e) {
      System.out.println("Upgrade SMS plugin, exception: " + e.getMessage());
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
        System.out.printf("Uninstalling SMS plugin, script %s not found%n", sql);
        return null;
      }
      return IOUtils.toString(inputStream, UTF8);
    } catch (Exception e) {
      System.out.println("Uninstalling SMS plugin, exception: " + e.getMessage());
    }
    return null;
  }
}
