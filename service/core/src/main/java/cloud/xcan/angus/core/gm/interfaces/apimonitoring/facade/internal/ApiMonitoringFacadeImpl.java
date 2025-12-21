package cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.internal;

import cloud.xcan.angus.core.gm.application.query.apimonitoring.ApiMonitoringQuery;
import cloud.xcan.angus.core.gm.domain.apimonitoring.ApiMonitoring;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.ApiMonitoringFacade;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.dto.ErrorRequestFindDto;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.dto.InterfaceStatsFindDto;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.dto.SlowRequestFindDto;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.internal.assembler.ApiMonitoringAssembler;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.vo.*;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

/**
 * <p>
 * 接口监控门面实现
 * </p>
 */
@Component
public class ApiMonitoringFacadeImpl implements ApiMonitoringFacade {
    
    @Resource
    private ApiMonitoringQuery apiMonitoringQuery;
    
    @Override
    public InterfaceMonitoringOverviewVo getOverview() {
        Map<String, Object> data = apiMonitoringQuery.getOverview();
        InterfaceMonitoringOverviewVo vo = new InterfaceMonitoringOverviewVo();
        vo.setTotalRequests(((Number) data.get("totalRequests")).longValue());
        vo.setSuccessRequests(((Number) data.get("successRequests")).longValue());
        vo.setFailedRequests(((Number) data.get("failedRequests")).longValue());
        vo.setAvgResponseTime((Integer) data.get("avgResponseTime"));
        vo.setQps((Integer) data.get("qps"));
        vo.setErrorRate((Double) data.get("errorRate"));
        vo.setSlowRequestCount((Integer) data.get("slowRequestCount"));
        return vo;
    }
    
    @Override
    public PageResult<InterfaceStatsVo> listStats(InterfaceStatsFindDto dto) {
        PageRequest pageRequest = PageRequest.of(
            dto.getPage() != null ? dto.getPage() - 1 : 0,
            dto.getSize() != null ? dto.getSize() : 20
        );
        
        Page<Map<String, Object>> page = apiMonitoringQuery.listStats(dto, pageRequest);
        List<InterfaceStatsVo> list = page.getContent().stream()
            .map(this::mapToInterfaceStatsVo)
            .collect(Collectors.toList());
        
        return PageResult.of(page.getTotalElements(), list);
    }
    
    @Override
    public InterfaceStatsDetailVo getStatsDetail(String serviceName, String path, String startDate, String endDate, String period) {
        Map<String, Object> data = apiMonitoringQuery.getStatsDetail(serviceName, path, startDate, endDate, period);
        return ApiMonitoringAssembler.toInterfaceStatsDetailVo(data);
    }
    
    @Override
    public PageResult<SlowRequestVo> listSlowRequests(SlowRequestFindDto dto) {
        PageRequest pageRequest = PageRequest.of(
            dto.getPage() != null ? dto.getPage() - 1 : 0,
            dto.getSize() != null ? dto.getSize() : 20
        );
        
        Page<ApiMonitoring> page = apiMonitoringQuery.listSlowRequests(dto, pageRequest);
        return buildVoPageResult(page, ApiMonitoringAssembler::toSlowRequestVo);
    }
    
    @Override
    public SlowRequestDetailVo getSlowRequestDetail(Long id) {
        ApiMonitoring entity = apiMonitoringQuery.findById(id)
            .orElseThrow(() -> ResourceNotFound.of("慢请求记录不存在", new Object[]{}));
        return ApiMonitoringAssembler.toSlowRequestDetailVo(entity);
    }
    
    @Override
    public PageResult<ErrorRequestVo> listErrorRequests(ErrorRequestFindDto dto) {
        PageRequest pageRequest = PageRequest.of(
            dto.getPage() != null ? dto.getPage() - 1 : 0,
            dto.getSize() != null ? dto.getSize() : 20
        );
        
        Page<ApiMonitoring> page = apiMonitoringQuery.listErrorRequests(dto, pageRequest);
        return buildVoPageResult(page, ApiMonitoringAssembler::toErrorRequestVo);
    }
    
    @Override
    public ErrorRequestDetailVo getErrorRequestDetail(Long id) {
        ApiMonitoring entity = apiMonitoringQuery.findById(id)
            .orElseThrow(() -> ResourceNotFound.of("错误请求记录不存在", new Object[]{}));
        return ApiMonitoringAssembler.toErrorRequestDetailVo(entity);
    }
    
    @Override
    public RealtimeQpsVo getRealtimeQps() {
        Map<String, Object> data = apiMonitoringQuery.getRealtimeQps();
        return ApiMonitoringAssembler.toRealtimeQpsVo(data);
    }
    
    @Override
    public RealtimeResponseTimeVo getRealtimeResponseTime() {
        Map<String, Object> data = apiMonitoringQuery.getRealtimeResponseTime();
        return ApiMonitoringAssembler.toRealtimeResponseTimeVo(data);
    }
    
    @Override
    public List<TopCallsVo> getTopCalls(Integer limit, String period) {
        List<Map<String, Object>> data = apiMonitoringQuery.getTopCalls(limit, period);
        return data.stream()
            .map(ApiMonitoringAssembler::toTopCallsVo)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<TopSlowVo> getTopSlow(Integer limit, String period) {
        List<Map<String, Object>> data = apiMonitoringQuery.getTopSlow(limit, period);
        return data.stream()
            .map(ApiMonitoringAssembler::toTopSlowVo)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<TopErrorsVo> getTopErrors(Integer limit, String period) {
        List<Map<String, Object>> data = apiMonitoringQuery.getTopErrors(limit, period);
        return data.stream()
            .map(ApiMonitoringAssembler::toTopErrorsVo)
            .collect(Collectors.toList());
    }
    
    private InterfaceStatsVo mapToInterfaceStatsVo(Map<String, Object> data) {
        InterfaceStatsVo vo = new InterfaceStatsVo();
        Object idObj = data.get("id");
        if (idObj instanceof Long) {
            vo.setId((Long) idObj);
        } else if (idObj instanceof Number) {
            vo.setId(((Number) idObj).longValue());
        } else {
            vo.setId(null);
        }
        vo.setServiceName((String) data.get("serviceName"));
        vo.setPath((String) data.get("path"));
        vo.setMethod((String) data.get("method"));
        vo.setTotalCalls(((Number) data.get("totalCalls")).longValue());
        vo.setSuccessCalls(((Number) data.get("successCalls")).longValue());
        vo.setFailedCalls(((Number) data.get("failedCalls")).longValue());
        vo.setAvgResponseTime((Integer) data.get("avgResponseTime"));
        vo.setMaxResponseTime((Integer) data.get("maxResponseTime"));
        vo.setMinResponseTime((Integer) data.get("minResponseTime"));
        vo.setErrorRate((Double) data.get("errorRate"));
        vo.setQps((Integer) data.get("qps"));
        return vo;
    }
}
