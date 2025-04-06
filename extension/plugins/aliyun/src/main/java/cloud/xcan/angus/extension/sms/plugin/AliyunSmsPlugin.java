package cloud.xcan.angus.extension.sms.plugin;


import cloud.xcan.angus.plugin.core.Plugin;
import cloud.xcan.angus.plugin.core.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AliyunSmsPlugin extends Plugin {

  public final Logger log = LoggerFactory.getLogger(AliyunSmsPlugin.class);

  public AliyunSmsPlugin(PluginWrapper wrapper) {
    super(wrapper);
  }

  @Override
  public void start() {
    log.info("AliyunSmsPlugin is started");
  }

  @Override
  public void stop() {
    log.info("AliyunSmsPlugin is stopped");
  }

}
