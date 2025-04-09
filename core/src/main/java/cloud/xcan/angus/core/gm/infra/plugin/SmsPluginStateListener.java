package cloud.xcan.angus.core.gm.infra.plugin;


import static cloud.xcan.angus.core.gm.domain.SmsMessage.SMS_NO_PLUGIN_CHANNEL_ERROR;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;

import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.api.enums.EventType;
import cloud.xcan.angus.core.disruptor.DisruptorQueueManager;
import cloud.xcan.angus.core.event.CommonEvent;
import cloud.xcan.angus.core.event.source.EventContent;
import cloud.xcan.angus.core.gm.application.cmd.sms.SmsChannelCmd;
import cloud.xcan.angus.core.gm.domain.channel.SmsChannel;
import cloud.xcan.angus.core.gm.domain.channel.SmsChannelRepo;
import cloud.xcan.angus.core.spring.SpringContextHolder;
import cloud.xcan.angus.core.spring.boot.ApplicationInfo;
import cloud.xcan.angus.core.utils.BeanFieldUtils;
import cloud.xcan.angus.extension.sms.api.MessageChannel;
import cloud.xcan.angus.extension.sms.api.SmsProvider;
import cloud.xcan.angus.idgen.uid.impl.CachedUidGenerator;
import cloud.xcan.angus.plugin.core.PluginState;
import cloud.xcan.angus.plugin.core.PluginStateEvent;
import cloud.xcan.angus.plugin.core.PluginStateListener;
import cloud.xcan.angus.remote.ExceptionLevel;
import cloud.xcan.angus.spec.locale.MessageHolder;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@DependsOn("applicationContextProvider")
public class SmsPluginStateListener implements PluginStateListener {

  @Resource
  private DisruptorQueueManager<CommonEvent> exceptionEventDisruptorQueue;

  @Resource
  private ApplicationInfo applicationInfo;

  @Resource
  private SmsChannelRepo smsChannelRepo;

  @Resource
  private SmsChannelCmd smsChannelCmd;

  /**
   * Collection of processed plug-in IDs
   */
  private final List<String> pluginIds = new ArrayList<>();
  /**
   * All extended channels of all plug-ins, channel names cannot be repeated
   */
  private final List<MessageChannel> channels = new ArrayList<>();

  @Override
  public void pluginStateChanged(PluginStateEvent pluginStateEvent) {
    try {
      if (pluginStateEvent.getPluginState().equals(PluginState.STARTED)) {
        String pluginId = pluginStateEvent.getPlugin().getPluginId();
        if (!pluginIds.contains(pluginId)) {
          pluginIds.add(pluginStateEvent.getPlugin().getPluginId());
          // Get all channels of the plugin
          List<SmsProvider> smsProviders = pluginStateEvent.getPlugin().getPluginManager()
              .getExtensions(SmsProvider.class, pluginId);
          if (isEmpty(smsProviders)) {
            pushExceptionEvent(pluginId);
          }
          smsProviders.forEach(provider -> {
            if (Objects.nonNull(provider)) {
              if (!channels.stream().map(MessageChannel::getName).collect(Collectors.toSet())
                  .contains(provider.getInstallationChannel().getName())) {
                channels.add(provider.getInstallationChannel());
              }
            }
          });
          if (isEmpty(channels)) {
            pushExceptionEvent(pluginId);
          }

          // Obtain plug-in channel information and convert to domain channel，The first channel is set to enable by default
          List<SmsChannel> smsChannels = channels.stream()
              .map(channel -> toInitInstallChannel(channel, applicationInfo))
              .collect(Collectors.toList());
          List<SmsChannel> smsChannelDbs = smsChannelRepo.findAll();

          List<String> channelDbNames = smsChannelDbs.stream().map(SmsChannel::getName)
              .collect(Collectors.toList());
          Map<String, SmsChannel> smsChannelNamesAndIdMap = smsChannelDbs.stream()
              .collect(Collectors.toMap(SmsChannel::getName, o -> o));

          // Add or update or delete channel information
          smsChannels.forEach(channel -> {
            if (!channelDbNames.contains(channel.getName())) {
              channel.setEnabled(false);
              channel.setId(getCachedUidGenerator().getUID());
            } else {
              // Importantly, after installation, you must use the database configuration status: enabled status, authentication key, etc.
              SmsChannel channelDb = smsChannelNamesAndIdMap.get(channel.getName());
              BeanUtils.copyProperties(channelDb, channel,
                  BeanFieldUtils.getNullPropertyNames(channelDb));
            }
          });
          smsChannelCmd.replace(smsChannels);
        }
      }
    } catch (Exception e) {
      //Failed to get the plug-in channel to push abnormal events
      log.error("Get plugin error:", e);
    }
  }

  /**
   * Failed to get the plug-in channel to push abnormal events
   */
  public void pushExceptionEvent(String pluginId) {
    CommonEvent event = new CommonEvent(
        EventContent.newBuilder().type(EventType.SYSTEM.getValue())
            .description(SMS_NO_PLUGIN_CHANNEL_ERROR)
            .cause(MessageHolder.message(SMS_NO_PLUGIN_CHANNEL_ERROR, new Object[]{pluginId}))
            .level(ExceptionLevel.ERROR)
            .instanceId(applicationInfo.getInstanceId())
            .serviceCode(applicationInfo.getArtifactId())
            .serviceName(applicationInfo.getName())
            .timestamp(LocalDateTime.now()).build());
    exceptionEventDisruptorQueue.add(event);
  }

  /**
   * Obtain plug-in channel information and convert to domain channel
   */
  public static SmsChannel toInitInstallChannel(MessageChannel installChannel,
      ApplicationInfo applicationInfo) {
    if (StringUtils.equals(EditionType.CLOUD_SERVICE.name(), applicationInfo.getEditionType())) {
      return new SmsChannel().setLogo(installChannel.getLogo())
          .setName(installChannel.getName())
          .setEndpoint(installChannel.getEndpoint())
          .setAccessKeyId(installChannel.getAccessKeyId())
          .setAccessKeySecret(installChannel.getAccessKeySecret())
          .setThirdChannelNo(installChannel.getThirdChannelNo());
    }
    // Private version requires tenants to configure their own SMS channel information,
    return new SmsChannel().setLogo(installChannel.getLogo()).setName(installChannel.getName());
  }

  public static CachedUidGenerator getCachedUidGenerator() {
    return SpringContextHolder.getBean(CachedUidGenerator.class);
  }

}
