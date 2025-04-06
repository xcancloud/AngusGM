package cloud.xcan.angus.core.gm.application.cmd.sms.impl;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.sms.SmsChannelCmd;
import cloud.xcan.angus.core.gm.application.cmd.sms.SmsTemplateCmd;
import cloud.xcan.angus.core.gm.application.query.sms.SmsChannelQuery;
import cloud.xcan.angus.core.gm.domain.channel.SmsChannel;
import cloud.xcan.angus.core.gm.domain.channel.SmsChannelRepo;
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

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Void update(List<SmsChannel> channels) {
    return new BizTemplate<Void>() {
      @Override
      protected Void process() {
        batchUpdateOrNotFound(channels);
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
        smsChannelRepo.deleteByIdIn(ids);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Void enabled(Long id, Boolean enabled) {
    return new BizTemplate<Void>() {
      SmsChannel smsChannelDb;

      @Override
      protected void checkParams() {
        smsChannelDb = smsChannelQuery.detail(id);
      }

      @Override
      protected Void process() {
        if (smsChannelDb.getEnabled().equals(enabled)) {
          return null;
        }
        if (enabled) {
          List<SmsChannel> enabledSmsChannelDb = smsChannelRepo.findByEnabled(true);
          if (isNotEmpty(enabledSmsChannelDb)) {
            smsChannelRepo.saveAll(enabledSmsChannelDb.stream().peek(x -> x.setEnabled(false))
                .collect(Collectors.toList()));
          }
          smsChannelDb.setEnabled(true);
          smsChannelRepo.save(smsChannelDb);
          return null;
        }

        smsChannelDb.setEnabled(false);
        smsChannelRepo.save(smsChannelDb);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<SmsChannel, Long> getRepository() {
    return this.smsChannelRepo;
  }
}
