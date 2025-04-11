package cloud.xcan.angus.extension.sms.plugin;


import cloud.xcan.angus.plugin.core.Plugin;
import cloud.xcan.angus.plugin.core.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AliYunSmsPlugin extends Plugin {

  public final Logger log = LoggerFactory.getLogger(AliYunSmsPlugin.class);

  public AliYunSmsPlugin(PluginWrapper wrapper) {
    super(wrapper);
  }

  @Override
  public void start() {
    log.info("AliYunSmsPlugin is started");
  }

  @Override
  public void stop() {
    log.info("AliYunSmsPlugin is stopped");
  }

}
