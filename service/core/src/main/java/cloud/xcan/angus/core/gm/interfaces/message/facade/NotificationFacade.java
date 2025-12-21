package cloud.xcan.angus.core.gm.interfaces.message.facade;

import cloud.xcan.angus.core.gm.interfaces.message.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.message.facade.vo.*;
import cloud.xcan.angus.remote.PageResult;

import java.util.List;

/**
 * 消息通知Facade接口
 */
public interface NotificationFacade {
    
    // ==================== 通知管理 ====================
    
    /**
     * 获取通知统计数据
     */
    NotificationStatsVo getStats();
    
    /**
     * 获取通知列表
     */
    PageResult<NotificationVo> list(NotificationFindDto dto);
    
    /**
     * 获取通知详情
     */
    NotificationDetailVo getDetail(String id);
    
    /**
     * 标记通知为已读
     */
    NotificationReadResultVo markAsRead(String id);
    
    /**
     * 批量标记为已读
     */
    NotificationBatchReadResultVo markBatchAsRead(NotificationBatchReadDto dto);
    
    /**
     * 全部标记为已读
     */
    NotificationBatchReadResultVo markAllAsRead();
    
    /**
     * 收藏/取消收藏通知
     */
    NotificationStarResultVo toggleStar(String id, NotificationStarDto dto);
    
    /**
     * 归档/取消归档通知
     */
    NotificationArchiveResultVo toggleArchive(String id, NotificationArchiveDto dto);
    
    /**
     * 删除通知
     */
    void delete(String id);
    
    /**
     * 批量删除通知
     */
    void deleteBatch(NotificationBatchDeleteDto dto);
    
    // ==================== 通知渠道 ====================
    
    /**
     * 获取通知渠道列表
     */
    List<NotificationChannelVo> listChannels();
    
    /**
     * 创建通知渠道
     */
    NotificationChannelVo createChannel(NotificationChannelCreateDto dto);
    
    /**
     * 更新通知渠道
     */
    NotificationChannelVo updateChannel(String id, NotificationChannelUpdateDto dto);
    
    /**
     * 删除通知渠道
     */
    void deleteChannel(String id);
    
    /**
     * 测试通知渠道
     */
    ChannelTestResultVo testChannel(String id);
    
    // ==================== 通知规则 ====================
    
    /**
     * 获取通知规则列表
     */
    List<NotificationRuleVo> listRules();
    
    /**
     * 创建通知规则
     */
    NotificationRuleVo createRule(NotificationRuleCreateDto dto);
    
    // ==================== 通知历史 ====================
    
    /**
     * 获取通知历史
     */
    PageResult<NotificationHistoryVo> listHistory(NotificationHistoryFindDto dto);
}
