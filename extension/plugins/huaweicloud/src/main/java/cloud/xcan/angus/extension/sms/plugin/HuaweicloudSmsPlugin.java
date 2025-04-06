package cloud.xcan.angus.extension.sms.plugin;


import cloud.xcan.angus.plugin.core.Plugin;
import cloud.xcan.angus.plugin.core.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HuaweicloudSmsPlugin extends Plugin {

  public final Logger log = LoggerFactory.getLogger(HuaweicloudSmsPlugin.class);

  public HuaweicloudSmsPlugin(PluginWrapper wrapper) {
    super(wrapper);
  }

  @Override
  public void start() {
    log.info("HuaweicloudSmsPlugin is started");
  }

  @Override
  public void stop() {
    log.info("HuaweicloudSmsPlugin is stopped");
  }

}
