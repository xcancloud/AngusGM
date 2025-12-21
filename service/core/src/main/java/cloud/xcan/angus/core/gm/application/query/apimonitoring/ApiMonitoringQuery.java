package cloud.xcan.angus.core.gm.application.query.apimonitoring;

import cloud.xcan.angus.core.gm.domain.apimonitoring.ApiMonitoring;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.dto.ErrorRequestFindDto;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.dto.InterfaceStatsFindDto;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.dto.SlowRequestFindDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * <p>
 * API监控查询服务接口
 * </p>
 */
public interface ApiMonitoringQuery {
    
    /**
     * 根据ID查找
     */
    Optional<ApiMonitoring> findById(Long id);
    
    
    /**
     * 获取监控概览统计
     */
    Map<String, Object> getOverview();
    
    /**
     * 获取接口调用统计列表
     */
    Page<Map<String, Object>> listStats(InterfaceStatsFindDto dto, Pageable pageable);
    
    /**
     * 获取单个接口详细统计
     */
    Map<String, Object> getStatsDetail(String serviceName, String path, String startDate, String endDate, String period);
    
    /**
     * 获取慢请求列表
     */
    Page<ApiMonitoring> listSlowRequests(SlowRequestFindDto dto, Pageable pageable);
    
    /**
     * 获取错误请求列表
     */
    Page<ApiMonitoring> listErrorRequests(ErrorRequestFindDto dto, Pageable pageable);
    
    /**
     * 获取实时QPS数据
     */
    Map<String, Object> getRealtimeQps();
    
    /**
     * 获取实时响应时间数据
     */
    Map<String, Object> getRealtimeResponseTime();
    
    /**
     * 获取调用量TOP接口
     */
    List<Map<String, Object>> getTopCalls(Integer limit, String period);
    
    /**
     * 获取响应时间TOP接口
     */
    List<Map<String, Object>> getTopSlow(Integer limit, String period);
    
    /**
     * 获取错误率TOP接口
     */
    List<Map<String, Object>> getTopErrors(Integer limit, String period);
}
