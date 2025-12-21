package cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade;

import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.dto.InterfaceStatsFindDto;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.dto.SlowRequestFindDto;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.dto.ErrorRequestFindDto;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.dto.TopRequestFindDto;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.vo.*;
import cloud.xcan.angus.remote.PageResult;

import java.util.List;

/**
 * 接口监控门面接口
 */
public interface ApiMonitoringFacade {
    
    // ==================== 监控概览 ====================
    
    /**
     * 获取接口监控概览
     */
    InterfaceMonitoringOverviewVo getOverview();
    
    // ==================== 接口调用统计 ====================
    
    /**
     * 获取接口调用统计列表
     */
    PageResult<InterfaceStatsVo> listStats(InterfaceStatsFindDto dto);
    
    /**
     * 获取单个接口详细统计
     */
    InterfaceStatsDetailVo getStatsDetail(String serviceName, String path, String startDate, String endDate, String period);
    
    // ==================== 慢请求分析 ====================
    
    /**
     * 获取慢请求列表
     */
    PageResult<SlowRequestVo> listSlowRequests(SlowRequestFindDto dto);
    
    /**
     * 获取慢请求详情
     */
    SlowRequestDetailVo getSlowRequestDetail(String id);
    
    // ==================== 错误请求分析 ====================
    
    /**
     * 获取错误请求列表
     */
    PageResult<ErrorRequestVo> listErrorRequests(ErrorRequestFindDto dto);
    
    /**
     * 获取错误请求详情
     */
    ErrorRequestDetailVo getErrorRequestDetail(String id);
    
    // ==================== 实时监控 ====================
    
    /**
     * 获取实时QPS数据
     */
    RealtimeQpsVo getRealtimeQps();
    
    /**
     * 获取实时响应时间数据
     */
    RealtimeResponseTimeVo getRealtimeResponseTime();
    
    // ==================== TOP排行 ====================
    
    /**
     * 获取调用量TOP接口
     */
    List<TopCallsVo> getTopCalls(TopRequestFindDto dto);
    
    /**
     * 获取响应时间TOP接口
     */
    List<TopSlowVo> getTopSlow(TopRequestFindDto dto);
    
    /**
     * 获取错误率TOP接口
     */
    List<TopErrorsVo> getTopErrors(TopRequestFindDto dto);
}
