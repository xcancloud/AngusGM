package cloud.xcan.angus.core.gm.interfaces.message.facade.internal;

import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.gm.interfaces.message.facade.NotificationFacade;
import cloud.xcan.angus.core.gm.interfaces.message.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.message.facade.vo.*;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 消息通知Facade实现
 */
@Service
public class NotificationFacadeImpl implements NotificationFacade {

  // TODO: 注入相应的Cmd和Query服务

  @Override
  public NotificationStatsVo getStats() {
    // TODO: 实现统计数据查询
    NotificationStatsVo stats = new NotificationStatsVo();
    stats.setTotalNotifications(0);
    stats.setUnreadNotifications(0);
    stats.setStarredNotifications(0);
    stats.setArchivedNotifications(0);
    return stats;
  }

  @Override
  public PageResult<NotificationVo> list(NotificationFindDto dto) {
    // TODO: 实现通知列表查询
    return PageResult.of(new ArrayList<>(), 0L);
  }

  @Override
  public NotificationDetailVo getDetail(String id) {
    // TODO: 实现通知详情查询
    return new NotificationDetailVo();
  }

  @Override
  public NotificationReadResultVo markAsRead(String id) {
    // TODO: 实现标记为已读
    NotificationReadResultVo result = new NotificationReadResultVo();
    result.setId(id);
    result.setIsRead(true);
    return result;
  }

  @Override
  public NotificationBatchReadResultVo markBatchAsRead(NotificationBatchReadDto dto) {
    // TODO: 实现批量标记为已读
    NotificationBatchReadResultVo result = new NotificationBatchReadResultVo();
    result.setSuccessCount(0);
    result.setFailedCount(0);
    return result;
  }

  @Override
  public NotificationBatchReadResultVo markAllAsRead() {
    // TODO: 实现全部标记为已读
    NotificationBatchReadResultVo result = new NotificationBatchReadResultVo();
    result.setSuccessCount(0);
    result.setFailedCount(0);
    return result;
  }

  @Override
  public NotificationStarResultVo toggleStar(String id, NotificationStarDto dto) {
    // TODO: 实现收藏/取消收藏
    NotificationStarResultVo result = new NotificationStarResultVo();
    result.setId(id);
    result.setIsStarred(dto.getIsStarred());
    return result;
  }

  @Override
  public NotificationArchiveResultVo toggleArchive(String id, NotificationArchiveDto dto) {
    // TODO: 实现归档/取消归档
    NotificationArchiveResultVo result = new NotificationArchiveResultVo();
    result.setId(id);
    result.setIsArchived(dto.getIsArchived());
    return result;
  }

  @Override
  public void delete(String id) {
    // TODO: 实现删除通知
  }

  @Override
  public void deleteBatch(NotificationBatchDeleteDto dto) {
    // TODO: 实现批量删除通知
  }

  @Override
  public List<NotificationChannelVo> listChannels() {
    // TODO: 实现通知渠道列表查询
    return new ArrayList<>();
  }

  @Override
  public NotificationChannelVo createChannel(NotificationChannelCreateDto dto) {
    // TODO: 实现创建通知渠道
    return new NotificationChannelVo();
  }

  @Override
  public NotificationChannelVo updateChannel(String id, NotificationChannelUpdateDto dto) {
    // TODO: 实现更新通知渠道
    return new NotificationChannelVo();
  }

  @Override
  public void deleteChannel(String id) {
    // TODO: 实现删除通知渠道
  }

  @Override
  public ChannelTestResultVo testChannel(String id) {
    // TODO: 实现测试通知渠道
    ChannelTestResultVo result = new ChannelTestResultVo();
    result.setSuccess(false);
    result.setMessage("未实现");
    return result;
  }

  @Override
  public List<NotificationRuleVo> listRules() {
    // TODO: 实现通知规则列表查询
    return new ArrayList<>();
  }

  @Override
  public NotificationRuleVo createRule(NotificationRuleCreateDto dto) {
    // TODO: 实现创建通知规则
    return new NotificationRuleVo();
  }

  @Override
  public PageResult<NotificationHistoryVo> listHistory(NotificationHistoryFindDto dto) {
    // TODO: 实现通知历史查询
    return PageResult.of(new ArrayList<>(), 0L);
  }
}
