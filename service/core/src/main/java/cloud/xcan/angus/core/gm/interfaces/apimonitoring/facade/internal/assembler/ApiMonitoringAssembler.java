package cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.internal.assembler;

import cloud.xcan.angus.core.gm.domain.apimonitoring.ApiMonitoring;
import cloud.xcan.angus.core.gm.domain.apimonitoring.ApiMonitoringInfo;
import cloud.xcan.angus.core.gm.domain.apimonitoring.ApiMonitoringStatus;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.dto.ErrorRequestFindDto;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.dto.InterfaceStatsFindDto;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.dto.SlowRequestFindDto;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.vo.*;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * API监控装配器
 * </p>
 */
public class ApiMonitoringAssembler {
    
    /**
     * 转换为慢请求VO
     */
    public static SlowRequestVo toSlowRequestVo(ApiMonitoring entity) {
        SlowRequestVo vo = new SlowRequestVo();
        vo.setId(entity.getId());
        vo.setTraceId(entity.getTraceId());
        vo.setServiceName(entity.getServiceName());
        vo.setPath(entity.getPath());
        vo.setMethod(entity.getMethod());
        vo.setRequestTime(entity.getRequestTime());
        vo.setDuration(entity.getDuration() != null ? entity.getDuration().intValue() : null);
        vo.setStatusCode(entity.getStatusCode());
        vo.setIpAddress(entity.getIpAddress());
        vo.setUserId(entity.getUserId());
        
        // 设置查询参数（已经是Map类型，直接使用）
        vo.setParameters(entity.getQueryParameters() != null ? entity.getQueryParameters() : new HashMap<>());
        
        // 设置审计字段（TenantAuditingVo会自动处理）
        
        return vo;
    }
    
    /**
     * 转换为慢请求详情VO
     */
    public static SlowRequestDetailVo toSlowRequestDetailVo(ApiMonitoring entity) {
        SlowRequestDetailVo vo = new SlowRequestDetailVo();
        vo.setId(entity.getId());
        vo.setTraceId(entity.getTraceId());
        vo.setServiceName(entity.getServiceName());
        vo.setPath(entity.getPath());
        vo.setMethod(entity.getMethod());
        vo.setRequestTime(entity.getRequestTime());
        vo.setDuration(entity.getDuration() != null ? entity.getDuration().intValue() : null);
        vo.setStatusCode(entity.getStatusCode());
        vo.setIpAddress(entity.getIpAddress());
        vo.setUserId(entity.getUserId());
        vo.setUserName(entity.getUserName() != null ? entity.getUserName() : "");
        vo.setRequestBody(entity.getRequestBody());
        vo.setResponseBody(entity.getResponseBody());
        
        // 设置请求头（已经是Map类型，直接使用）
        vo.setRequestHeaders(entity.getRequestHeaders() != null ? entity.getRequestHeaders() : new HashMap<>());
        
        // 设置耗时分解（已经是Map类型，直接使用）
        vo.setBreakdown(entity.getBreakdown() != null ? entity.getBreakdown() : new HashMap<>());
        
        // 设置SQL语句列表
        if (entity.getSqlStatements() != null && !entity.getSqlStatements().isEmpty()) {
            List<SlowRequestDetailVo.SqlStatement> sqlStatements = new ArrayList<>();
            for (Map<String, Object> sqlMap : entity.getSqlStatements()) {
                SlowRequestDetailVo.SqlStatement sql = new SlowRequestDetailVo.SqlStatement();
                sql.setSql((String) sqlMap.get("sql"));
                if (sqlMap.get("duration") != null) {
                    sql.setDuration(((Number) sqlMap.get("duration")).intValue());
                }
                if (sqlMap.get("rows") != null) {
                    sql.setRows(((Number) sqlMap.get("rows")).intValue());
                }
                sqlStatements.add(sql);
            }
            vo.setSqlStatements(sqlStatements);
        } else {
            vo.setSqlStatements(new ArrayList<>());
        }
        
        return vo;
    }
    
    /**
     * 转换为错误请求VO
     */
    public static ErrorRequestVo toErrorRequestVo(ApiMonitoring entity) {
        ErrorRequestVo vo = new ErrorRequestVo();
        vo.setId(entity.getId());
        vo.setTraceId(entity.getTraceId());
        vo.setServiceName(entity.getServiceName());
        vo.setPath(entity.getPath());
        vo.setMethod(entity.getMethod());
        vo.setRequestTime(entity.getRequestTime());
        vo.setDuration(entity.getDuration() != null ? entity.getDuration().intValue() : null);
        vo.setStatusCode(entity.getStatusCode());
        vo.setErrorMessage(entity.getErrorMessage());
        vo.setErrorType(entity.getErrorType());
        vo.setIpAddress(entity.getIpAddress());
        vo.setUserId(entity.getUserId());
        
        // 设置审计字段（TenantAuditingVo会自动处理）
        
        return vo;
    }
    
    /**
     * 转换为错误请求详情VO
     */
    public static ErrorRequestDetailVo toErrorRequestDetailVo(ApiMonitoring entity) {
        ErrorRequestDetailVo vo = new ErrorRequestDetailVo();
        vo.setId(entity.getId());
        vo.setTraceId(entity.getTraceId());
        vo.setServiceName(entity.getServiceName());
        vo.setPath(entity.getPath());
        vo.setMethod(entity.getMethod());
        vo.setRequestTime(entity.getRequestTime());
        vo.setDuration(entity.getDuration() != null ? entity.getDuration().intValue() : null);
        vo.setStatusCode(entity.getStatusCode());
        vo.setErrorMessage(entity.getErrorMessage());
        vo.setErrorType(entity.getErrorType());
        vo.setIpAddress(entity.getIpAddress());
        vo.setUserId(entity.getUserId());
        vo.setUserName(entity.getUserName() != null ? entity.getUserName() : "");
        vo.setRequestBody(entity.getRequestBody());
        vo.setResponseBody(entity.getResponseBody());
        vo.setStackTrace(entity.getStackTrace());
        vo.setResolved(entity.getResolved() != null ? entity.getResolved() : false);
        
        // 设置请求头（已经是Map类型，直接使用）
        vo.setRequestHeaders(entity.getRequestHeaders() != null ? entity.getRequestHeaders() : new HashMap<>());
        
        return vo;
    }
    
    /**
     * 转换为接口统计详情VO
     */
    public static InterfaceStatsDetailVo toInterfaceStatsDetailVo(Map<String, Object> data) {
        InterfaceStatsDetailVo vo = new InterfaceStatsDetailVo();
        vo.setServiceName((String) data.get("serviceName"));
        vo.setPath((String) data.get("path"));
        vo.setMethod((String) data.get("method"));
        
        // 设置周期
        InterfaceStatsDetailVo.Period period = new InterfaceStatsDetailVo.Period();
        period.setStartDate((String) data.get("startDate"));
        period.setEndDate((String) data.get("endDate"));
        vo.setPeriod(period);
        
        // 设置汇总数据
        InterfaceStatsDetailVo.Summary summary = new InterfaceStatsDetailVo.Summary();
        summary.setTotalCalls(((Number) data.getOrDefault("totalCalls", 0L)).longValue());
        summary.setSuccessCalls(((Number) data.getOrDefault("successCalls", 0L)).longValue());
        summary.setFailedCalls(((Number) data.getOrDefault("failedCalls", 0L)).longValue());
        summary.setAvgResponseTime(((Number) data.getOrDefault("avgResponseTime", 0)).intValue());
        summary.setErrorRate(((Number) data.getOrDefault("errorRate", 0.0)).doubleValue());
        vo.setSummary(summary);
        
        // 设置时间线数据
        List<InterfaceStatsDetailVo.TimelineItem> timeline = new ArrayList<>();
        if (data.get("timeline") instanceof List) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> timelineData = (List<Map<String, Object>>) data.get("timeline");
            for (Map<String, Object> item : timelineData) {
                InterfaceStatsDetailVo.TimelineItem timelineItem = new InterfaceStatsDetailVo.TimelineItem();
                timelineItem.setTime((String) item.get("time"));
                timelineItem.setCalls(((Number) item.get("calls")).longValue());
                timelineItem.setAvgResponseTime(((Number) item.get("avgResponseTime")).intValue());
                timelineItem.setErrorRate(((Number) item.get("errorRate")).doubleValue());
                timeline.add(timelineItem);
            }
        }
        vo.setTimeline(timeline);
        
        // 设置响应时间分布
        if (data.get("responseTimeDistribution") instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Long> distribution = (Map<String, Long>) data.get("responseTimeDistribution");
            vo.setResponseTimeDistribution(distribution);
        } else {
            vo.setResponseTimeDistribution(new HashMap<>());
        }
        
        // 设置状态码分布
        if (data.get("statusCodeDistribution") instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Long> distribution = (Map<String, Long>) data.get("statusCodeDistribution");
            vo.setStatusCodeDistribution(distribution);
        } else {
            vo.setStatusCodeDistribution(new HashMap<>());
        }
        
        return vo;
    }
    
    /**
     * 转换为实时QPS VO
     */
    public static RealtimeQpsVo toRealtimeQpsVo(Map<String, Object> data) {
        RealtimeQpsVo vo = new RealtimeQpsVo();
        vo.setCurrent(((Number) data.getOrDefault("current", 0)).intValue());
        vo.setPeak(((Number) data.getOrDefault("peak", 0)).intValue());
        vo.setAverage(((Number) data.getOrDefault("average", 0)).intValue());
        
        List<RealtimeQpsVo.TimelineItem> timeline = new ArrayList<>();
        if (data.get("timeline") instanceof List) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> timelineData = (List<Map<String, Object>>) data.get("timeline");
            for (Map<String, Object> item : timelineData) {
                RealtimeQpsVo.TimelineItem timelineItem = new RealtimeQpsVo.TimelineItem();
                timelineItem.setTime((String) item.get("time"));
                timelineItem.setQps(((Number) item.get("qps")).intValue());
                timeline.add(timelineItem);
            }
        }
        vo.setTimeline(timeline);
        
        return vo;
    }
    
    /**
     * 转换为实时响应时间VO
     */
    public static RealtimeResponseTimeVo toRealtimeResponseTimeVo(Map<String, Object> data) {
        RealtimeResponseTimeVo vo = new RealtimeResponseTimeVo();
        vo.setCurrent(((Number) data.getOrDefault("current", 0)).intValue());
        vo.setP50(((Number) data.getOrDefault("p50", 0)).intValue());
        vo.setP95(((Number) data.getOrDefault("p95", 0)).intValue());
        vo.setP99(((Number) data.getOrDefault("p99", 0)).intValue());
        vo.setMax(((Number) data.getOrDefault("max", 0)).intValue());
        
        List<RealtimeResponseTimeVo.TimelineItem> timeline = new ArrayList<>();
        if (data.get("timeline") instanceof List) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> timelineData = (List<Map<String, Object>>) data.get("timeline");
            for (Map<String, Object> item : timelineData) {
                RealtimeResponseTimeVo.TimelineItem timelineItem = new RealtimeResponseTimeVo.TimelineItem();
                timelineItem.setTime((String) item.get("time"));
                timelineItem.setAvg(((Number) item.get("avg")).intValue());
                timelineItem.setP95(((Number) item.get("p95")).intValue());
                timeline.add(timelineItem);
            }
        }
        vo.setTimeline(timeline);
        
        return vo;
    }
    
    /**
     * 转换为调用量TOP VO
     */
    public static TopCallsVo toTopCallsVo(Map<String, Object> data) {
        TopCallsVo vo = new TopCallsVo();
        vo.setServiceName((String) data.get("serviceName"));
        vo.setPath((String) data.get("path"));
        vo.setMethod((String) data.get("method"));
        vo.setCalls(((Number) data.get("calls")).longValue());
        vo.setErrorRate(((Number) data.get("errorRate")).doubleValue());
        return vo;
    }
    
    /**
     * 转换为响应时间TOP VO
     */
    public static TopSlowVo toTopSlowVo(Map<String, Object> data) {
        TopSlowVo vo = new TopSlowVo();
        vo.setServiceName((String) data.get("serviceName"));
        vo.setPath((String) data.get("path"));
        vo.setMethod((String) data.get("method"));
        vo.setAvgResponseTime(((Number) data.get("avgResponseTime")).intValue());
        vo.setCalls(((Number) data.get("calls")).longValue());
        return vo;
    }
    
    /**
     * 转换为错误率TOP VO
     */
    public static TopErrorsVo toTopErrorsVo(Map<String, Object> data) {
        TopErrorsVo vo = new TopErrorsVo();
        vo.setServiceName((String) data.get("serviceName"));
        vo.setPath((String) data.get("path"));
        vo.setMethod((String) data.get("method"));
        vo.setErrorRate(((Number) data.get("errorRate")).doubleValue());
        vo.setCalls(((Number) data.get("calls")).longValue());
        vo.setFailedCalls(((Number) data.get("failedCalls")).longValue());
        return vo;
    }

    /**
     * <p>
     * Build specification for interface stats query
     * </p>
     */
    public static Specification<ApiMonitoringInfo> buildStatsSpecification(InterfaceStatsFindDto dto) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (StringUtils.hasText(dto.getServiceName())) {
                predicates.add(cb.equal(root.get("serviceName"), dto.getServiceName()));
            }
            if (StringUtils.hasText(dto.getPath())) {
                predicates.add(cb.like(root.get("path"), "%" + dto.getPath() + "%"));
            }
            if (StringUtils.hasText(dto.getMethod())) {
                predicates.add(cb.equal(root.get("method"), dto.getMethod()));
            }
            if (StringUtils.hasText(dto.getStartDate())) {
                LocalDateTime start = LocalDateTime.parse(dto.getStartDate() + " 00:00:00", 
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                predicates.add(cb.greaterThanOrEqualTo(root.get("requestTime"), start));
            }
            if (StringUtils.hasText(dto.getEndDate())) {
                LocalDateTime end = LocalDateTime.parse(dto.getEndDate() + " 23:59:59", 
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                predicates.add(cb.lessThanOrEqualTo(root.get("requestTime"), end));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * <p>
     * Build specification for slow request query
     * </p>
     */
    public static Specification<ApiMonitoringInfo> buildSlowRequestSpecification(SlowRequestFindDto dto) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (StringUtils.hasText(dto.getServiceName())) {
                predicates.add(cb.equal(root.get("serviceName"), dto.getServiceName()));
            }
            if (StringUtils.hasText(dto.getPath())) {
                predicates.add(cb.like(root.get("path"), "%" + dto.getPath() + "%"));
            }
            if (dto.getMinDuration() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("duration"), dto.getMinDuration().longValue()));
            }
            if (StringUtils.hasText(dto.getStartDate())) {
                LocalDateTime start = LocalDateTime.parse(dto.getStartDate() + " 00:00:00", 
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                predicates.add(cb.greaterThanOrEqualTo(root.get("requestTime"), start));
            }
            if (StringUtils.hasText(dto.getEndDate())) {
                LocalDateTime end = LocalDateTime.parse(dto.getEndDate() + " 23:59:59", 
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                predicates.add(cb.lessThanOrEqualTo(root.get("requestTime"), end));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * <p>
     * Build specification for error request query
     * </p>
     */
    public static Specification<ApiMonitoringInfo> buildErrorRequestSpecification(ErrorRequestFindDto dto) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Only query error status records
            predicates.add(cb.notEqual(root.get("status"), ApiMonitoringStatus.SUCCESS));
            
            if (StringUtils.hasText(dto.getServiceName())) {
                predicates.add(cb.equal(root.get("serviceName"), dto.getServiceName()));
            }
            if (StringUtils.hasText(dto.getPath())) {
                predicates.add(cb.like(root.get("path"), "%" + dto.getPath() + "%"));
            }
            if (dto.getStatusCode() != null) {
                predicates.add(cb.equal(root.get("statusCode"), dto.getStatusCode()));
            }
            if (StringUtils.hasText(dto.getStartDate())) {
                LocalDateTime start = LocalDateTime.parse(dto.getStartDate() + " 00:00:00", 
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                predicates.add(cb.greaterThanOrEqualTo(root.get("requestTime"), start));
            }
            if (StringUtils.hasText(dto.getEndDate())) {
                LocalDateTime end = LocalDateTime.parse(dto.getEndDate() + " 23:59:59", 
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                predicates.add(cb.lessThanOrEqualTo(root.get("requestTime"), end));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
