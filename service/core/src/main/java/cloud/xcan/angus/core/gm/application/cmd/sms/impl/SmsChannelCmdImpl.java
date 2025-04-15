package cloud.xcan.angus.core.gm.application.cmd.sms.impl;

import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.SMS_CHANNEL;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.DELETED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.DISABLED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.ENABLED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.UPDATED;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.sms.SmsChannelCmd;
import cloud.xcan.angus.core.gm.application.cmd.sms.SmsTemplateCmd;
import cloud.xcan.angus.core.gm.application.query.sms.SmsChannelQuery;
import cloud.xcan.angus.core.gm.domain.sms.channel.SmsChannel;
import cloud.xcan.angus.core.gm.domain.sms.channel.SmsChannelRepo;
import cloud.xcan.angus.core.gm.infra.plugin.SmsPluginStateListener;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.spec.annotations.DoInFuture;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;


@Biz
public class SmsChannelCmdImpl extends CommCmd<SmsChannel, Long> implements SmsChannelCmd {

  @Resource
  private SmsChannelRepo smsChannelRepo;

  @Resource
  private SmsChannelQuery smsChannelQuery;

  @Resource
  private SmsTemplateCmd smsTemplateCmd;

  @Resource
  private OperationLogCmd operationLogCmd;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Void update(SmsChannel channel) {
    return new BizTemplate<Void>() {
      @Override
      protected Void process() {
        SmsChannel channelDb = updateOrNotFound(channel);
        operationLogCmd.add(SMS_CHANNEL, channelDb, UPDATED);
        return null;
      }
    }.execute();
  }

  /**
   * Used by {@link SmsPluginStateListener}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void replace(List<SmsChannel> channels) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        batchReplaceOrInsert(channels);
        smsTemplateCmd.init(channels);
        return null;
      }
    }.execute();
  }

  @DoInFuture("Remove the channel after uninstalling the plug-in")
  @Override
  @Transactional(rollbackFor = Exception.class)
  public Void delete(List<Long> ids) {
    return new BizTemplate<Void>() {
      @Override
      protected Void process() {
        List<SmsChannel> channels = smsChannelRepo.findAllById(ids);
        if (!channels.isEmpty()){
          smsChannelRepo.deleteByIdIn(ids);
          operationLogCmd.addAll(SMS_CHANNEL, channels, DELETED);
        }
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Void enabled(Long id, Boolean enabled) {
    return new BizTemplate<Void>() {
      SmsChannel channelDb;

      @Override
      protected void checkParams() {
        channelDb = smsChannelQuery.detail(id);
      }

      @Override
      protected Void process() {
        if (channelDb.getEnabled().equals(enabled)) {
          return null;
        }
        if (enabled) {
          List<SmsChannel> enabledSmsChannelDb = smsChannelRepo.findByEnabled(true);
          if (isNotEmpty(enabledSmsChannelDb)) {
            smsChannelRepo.saveAll(enabledSmsChannelDb.stream()
                .peek(x -> x.setEnabled(false)).collect(Collectors.toList()));
          }
          channelDb.setEnabled(true);
          smsChannelRepo.save(channelDb);

          operationLogCmd.add(SMS_CHANNEL, channelDb, ENABLED);
          return null;
        }

        channelDb.setEnabled(false);
        smsChannelRepo.save(channelDb);
        operationLogCmd.add(SMS_CHANNEL, channelDb, DISABLED);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<SmsChannel, Long> getRepository() {
    return this.smsChannelRepo;
  }
}
