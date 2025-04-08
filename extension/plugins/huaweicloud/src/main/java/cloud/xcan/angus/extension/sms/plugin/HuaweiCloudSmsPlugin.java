package cloud.xcan.angus.extension.sms.plugin;


import cloud.xcan.angus.plugin.core.Plugin;
import cloud.xcan.angus.plugin.core.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HuaweiCloudSmsPlugin extends Plugin {

  public final Logger log = LoggerFactory.getLogger(HuaweiCloudSmsPlugin.class);

  public HuaweiCloudSmsPlugin(PluginWrapper wrapper) {
    super(wrapper);
  }

  @Override
  public void start() {
    log.info("HuaweiCloudSmsPlugin is started");
  }

  @Override
  public void stop() {
    log.info("HuaweiCloudSmsPlugin is stopped");
  }

}
