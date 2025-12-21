package cloud.xcan.angus.core.gm.interfaces.message;

import cloud.xcan.angus.core.gm.interfaces.message.facade.NotificationFacade;
import cloud.xcan.angus.core.gm.interfaces.message.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.message.facade.vo.*;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 消息通知REST接口
 * 
 * 系统通知的查看、管理、通知配置
 */
@Tag(name = "Notifications", description = "消息通知 - 系统通知的查看、管理、通知配置")
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationRest {
    
    private final NotificationFacade notificationFacade;
    
    @Operation(summary = "获取通知统计数据", description = "获取通知统计数据")
    @GetMapping("/stats")
    public ApiLocaleResult<NotificationStatsVo> getStats() {
        return ApiLocaleResult.success(notificationFacade.getStats());
    }
    
    @Operation(summary = "获取通知列表", description = "分页获取通知列表")
    @GetMapping
    public ApiLocaleResult<PageResult<NotificationVo>> list(@Valid NotificationFindDto dto) {
        return ApiLocaleResult.success(notificationFacade.list(dto));
    }
    
    @Operation(summary = "获取通知详情", description = "获取通知详情")
    @GetMapping("/{id}")
    public ApiLocaleResult<NotificationDetailVo> getDetail(
            @Parameter(description = "通知ID") @PathVariable String id) {
        return ApiLocaleResult.success(notificationFacade.getDetail(id));
    }
    
    @Operation(summary = "标记通知为已读", description = "标记通知为已读")
    @PatchMapping("/{id}/read")
    public ApiLocaleResult<NotificationReadResultVo> markAsRead(
            @Parameter(description = "通知ID") @PathVariable String id) {
        return ApiLocaleResult.success(notificationFacade.markAsRead(id));
    }
    
    @Operation(summary = "批量标记为已读", description = "批量标记通知为已读")
    @PatchMapping("/read-batch")
    public ApiLocaleResult<NotificationBatchReadResultVo> markBatchAsRead(
            @Valid @RequestBody NotificationBatchReadDto dto) {
        return ApiLocaleResult.success(notificationFacade.markBatchAsRead(dto));
    }
    
    @Operation(summary = "全部标记为已读", description = "将所有未读通知标记为已读")
    @PatchMapping("/read-all")
    public ApiLocaleResult<NotificationBatchReadResultVo> markAllAsRead() {
        return ApiLocaleResult.success(notificationFacade.markAllAsRead());
    }
    
    @Operation(summary = "收藏/取消收藏通知", description = "收藏或取消收藏通知")
    @PatchMapping("/{id}/star")
    public ApiLocaleResult<NotificationStarResultVo> toggleStar(
            @Parameter(description = "通知ID") @PathVariable String id,
            @Valid @RequestBody NotificationStarDto dto) {
        return ApiLocaleResult.success(notificationFacade.toggleStar(id, dto));
    }
    
    @Operation(summary = "归档/取消归档通知", description = "归档或取消归档通知")
    @PatchMapping("/{id}/archive")
    public ApiLocaleResult<NotificationArchiveResultVo> toggleArchive(
            @Parameter(description = "通知ID") @PathVariable String id,
            @Valid @RequestBody NotificationArchiveDto dto) {
        return ApiLocaleResult.success(notificationFacade.toggleArchive(id, dto));
    }
    
    @Operation(summary = "删除通知", description = "删除通知")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Parameter(description = "通知ID") @PathVariable String id) {
        notificationFacade.delete(id);
    }
    
    @Operation(summary = "批量删除通知", description = "批量删除通知")
    @DeleteMapping("/batch")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBatch(@Valid @RequestBody NotificationBatchDeleteDto dto) {
        notificationFacade.deleteBatch(dto);
    }
    
    @Operation(summary = "获取通知渠道列表", description = "获取通知渠道列表")
    @GetMapping("/channels")
    public ApiLocaleResult<List<NotificationChannelVo>> listChannels() {
        return ApiLocaleResult.success(notificationFacade.listChannels());
    }
    
    @Operation(summary = "创建通知渠道", description = "创建通知渠道")
    @PostMapping("/channels")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiLocaleResult<NotificationChannelVo> createChannel(
            @Valid @RequestBody NotificationChannelCreateDto dto) {
        return ApiLocaleResult.success(notificationFacade.createChannel(dto));
    }
    
    @Operation(summary = "更新通知渠道", description = "更新通知渠道")
    @PutMapping("/channels/{id}")
    public ApiLocaleResult<NotificationChannelVo> updateChannel(
            @Parameter(description = "渠道ID") @PathVariable String id,
            @Valid @RequestBody NotificationChannelUpdateDto dto) {
        return ApiLocaleResult.success(notificationFacade.updateChannel(id, dto));
    }
    
    @Operation(summary = "删除通知渠道", description = "删除通知渠道")
    @DeleteMapping("/channels/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteChannel(@Parameter(description = "渠道ID") @PathVariable String id) {
        notificationFacade.deleteChannel(id);
    }
    
    @Operation(summary = "测试通知渠道", description = "测试通知渠道")
    @PostMapping("/channels/{id}/test")
    public ApiLocaleResult<ChannelTestResultVo> testChannel(
            @Parameter(description = "渠道ID") @PathVariable String id) {
        return ApiLocaleResult.success(notificationFacade.testChannel(id));
    }
    
    @Operation(summary = "获取通知规则列表", description = "获取通知规则列表")
    @GetMapping("/rules")
    public ApiLocaleResult<List<NotificationRuleVo>> listRules() {
        return ApiLocaleResult.success(notificationFacade.listRules());
    }
    
    @Operation(summary = "创建通知规则", description = "创建通知规则")
    @PostMapping("/rules")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiLocaleResult<NotificationRuleVo> createRule(
            @Valid @RequestBody NotificationRuleCreateDto dto) {
        return ApiLocaleResult.success(notificationFacade.createRule(dto));
    }
    
    @Operation(summary = "获取通知历史", description = "分页获取通知历史")
    @GetMapping("/history")
    public ApiLocaleResult<PageResult<NotificationHistoryVo>> listHistory(
            @Valid NotificationHistoryFindDto dto) {
        return ApiLocaleResult.success(notificationFacade.listHistory(dto));
    }
}
