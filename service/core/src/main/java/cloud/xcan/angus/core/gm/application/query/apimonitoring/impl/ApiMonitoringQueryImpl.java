package cloud.xcan.angus.core.gm.application.query.apimonitoring.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.apimonitoring.ApiMonitoringQuery;
import cloud.xcan.angus.core.gm.domain.apimonitoring.ApiMonitoring;
import cloud.xcan.angus.core.gm.domain.apimonitoring.ApiMonitoringInfo;
import cloud.xcan.angus.core.gm.domain.apimonitoring.ApiMonitoringInfoRepo;
import cloud.xcan.angus.core.gm.domain.apimonitoring.ApiMonitoringInfoSearchRepo;
import cloud.xcan.angus.core.gm.domain.apimonitoring.ApiMonitoringRepo;
import cloud.xcan.angus.core.gm.domain.apimonitoring.ApiMonitoringStatus;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * API监控查询服务实现
 * </p>
 */
@Biz
@Transactional(readOnly = true)
public class ApiMonitoringQueryImpl implements ApiMonitoringQuery {
    
    @Resource
    private ApiMonitoringRepo apiMonitoringRepo;
    
    @Resource
    private ApiMonitoringInfoRepo apiMonitoringInfoRepo;
    
    @Resource
    private ApiMonitoringInfoSearchRepo apiMonitoringInfoSearchRepo;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * <p>
     * 根据ID查找完整监控记录（包含大文本字段）
     * </p>
     */
    @Override
    public Optional<ApiMonitoring> findById(Long id) {
        return new BizTemplate<Optional<ApiMonitoring>>() {
            @Override
            protected Optional<ApiMonitoring> process() {
                return apiMonitoringRepo.findById(id);
            }
        }.execute();
    }
    
    
    /**
     * <p>
     * 获取接口监控概览统计
     * </p>
     */
    @Override
    public Map<String, Object> getOverview() {
        return new BizTemplate<Map<String, Object>>() {
            @Override
            protected Map<String, Object> process() {
                Map<String, Object> result = new HashMap<>();
                
                // 使用ApiMonitoringInfo进行统计，避免加载大文本字段
                long totalRequests = apiMonitoringInfoRepo.count();
                
                // 统计成功和失败请求数
                List<ApiMonitoringInfo> all = apiMonitoringInfoRepo.findAll();
                long successRequests = all.stream()
                    .filter(m -> m.getStatus() == ApiMonitoringStatus.SUCCESS)
                    .count();
                long failedRequests = totalRequests - successRequests;
                
                // 计算平均响应时间
                OptionalDouble avgDuration = all.stream()
                    .filter(m -> m.getDuration() != null)
                    .mapToLong(ApiMonitoringInfo::getDuration)
                    .average();
                
                // 计算QPS（最近1分钟的请求数）
                LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
                long recentRequests = all.stream()
                    .filter(m -> m.getRequestTime() != null && m.getRequestTime().isAfter(oneMinuteAgo))
                    .count();
                int qps = (int) (recentRequests / 60.0);
                
                // 计算错误率
                double errorRate = totalRequests > 0 ? (failedRequests * 100.0 / totalRequests) : 0.0;
                
                // 计算慢请求数量（假设超过1000ms为慢请求）
                long slowRequestCount = all.stream()
                    .filter(m -> m.getDuration() != null && m.getDuration() > 1000)
                    .count();
                
                result.put("totalRequests", totalRequests);
                result.put("successRequests", successRequests);
                result.put("failedRequests", failedRequests);
                result.put("avgResponseTime", avgDuration.isPresent() ? (int) avgDuration.getAsDouble() : 0);
                result.put("qps", qps);
                result.put("errorRate", errorRate);
                result.put("slowRequestCount", (int) slowRequestCount);
                
                return result;
            }
        }.execute();
    }
    
    /**
     * <p>
     * 获取接口调用统计列表（使用ApiMonitoringInfo，不包含大文本字段）
     * </p>
     */
    @Override
    public Page<Map<String, Object>> listStats(Specification<ApiMonitoringInfo> spec, Pageable pageable) {
        return new BizTemplate<Page<Map<String, Object>>>() {
            @Override
            protected Page<Map<String, Object>> process() {
                Page<ApiMonitoringInfo> page = apiMonitoringInfoRepo.findAll(spec, pageable);
                
                // 按服务名称、路径、方法分组统计
                Map<String, Map<String, Object>> statsMap = page.getContent().stream()
                    .collect(Collectors.groupingBy(
                        m -> m.getServiceName() + "|" + m.getPath() + "|" + m.getMethod(),
                        Collectors.collectingAndThen(
                            Collectors.toList(),
                            list -> {
                                Map<String, Object> stats = new HashMap<>();
                                long totalCalls = list.size();
                                long successCalls = list.stream()
                                    .filter(m -> m.getStatus() == ApiMonitoringStatus.SUCCESS)
                                    .count();
                                long failedCalls = totalCalls - successCalls;
                                
                                OptionalDouble avgDuration = list.stream()
                                    .filter(m -> m.getDuration() != null)
                                    .mapToLong(ApiMonitoringInfo::getDuration)
                                    .average();
                                
                                OptionalLong maxDuration = list.stream()
                                    .filter(m -> m.getDuration() != null)
                                    .mapToLong(ApiMonitoringInfo::getDuration)
                                    .max();
                                
                                OptionalLong minDuration = list.stream()
                                    .filter(m -> m.getDuration() != null)
                                    .mapToLong(ApiMonitoringInfo::getDuration)
                                    .min();
                                
                                double errorRate = totalCalls > 0 ? (failedCalls * 100.0 / totalCalls) : 0.0;
                                
                                stats.put("id", list.get(0).getId());
                                stats.put("serviceName", list.get(0).getServiceName());
                                stats.put("path", list.get(0).getPath());
                                stats.put("method", list.get(0).getMethod());
                                stats.put("totalCalls", totalCalls);
                                stats.put("successCalls", successCalls);
                                stats.put("failedCalls", failedCalls);
                                stats.put("avgResponseTime", avgDuration.isPresent() ? (int) avgDuration.getAsDouble() : 0);
                                stats.put("maxResponseTime", maxDuration.isPresent() ? (int) maxDuration.getAsLong() : 0);
                                stats.put("minResponseTime", minDuration.isPresent() ? (int) minDuration.getAsLong() : 0);
                                stats.put("errorRate", errorRate);
                                stats.put("qps", 0); // TODO: 实现QPS计算
                                
                                return stats;
                            }
                        )
                    ));
                
                List<Map<String, Object>> statsList = new ArrayList<>(statsMap.values());
                return new PageImpl<>(statsList, pageable, statsList.size());
            }
        }.execute();
    }
    
    /**
     * <p>
     * 获取单个接口详细统计
     * </p>
     */
    @Override
    public Map<String, Object> getStatsDetail(String serviceName, String path, String startDate, String endDate, String period) {
        return new BizTemplate<Map<String, Object>>() {
            @Override
            protected Map<String, Object> process() {
                Map<String, Object> result = new HashMap<>();
                
                // 解析时间范围
                LocalDateTime start = parseStartDate(startDate, period);
                LocalDateTime end = parseEndDate(endDate, period);
                
                // 查询指定接口的监控数据
                Specification<ApiMonitoringInfo> spec = (root, query, cb) -> {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(cb.equal(root.get("serviceName"), serviceName));
                    predicates.add(cb.equal(root.get("path"), path));
                    if (start != null) {
                        predicates.add(cb.greaterThanOrEqualTo(root.get("requestTime"), start));
                    }
                    if (end != null) {
                        predicates.add(cb.lessThanOrEqualTo(root.get("requestTime"), end));
                    }
                    return cb.and(predicates.toArray(new Predicate[0]));
                };
                
                List<ApiMonitoringInfo> records = apiMonitoringInfoRepo.findAll(spec);
                
                if (records.isEmpty()) {
                    result.put("serviceName", serviceName);
                    result.put("path", path);
                    result.put("method", "");
                    result.put("startDate", startDate);
                    result.put("endDate", endDate);
                    result.put("summary", createEmptySummary());
                    result.put("timeline", new ArrayList<>());
                    result.put("responseTimeDistribution", new HashMap<>());
                    result.put("statusCodeDistribution", new HashMap<>());
                    return result;
                }
                
                // 获取方法（假设所有记录的方法相同）
                String method = records.get(0).getMethod();
                
                // 计算汇总数据
                long totalCalls = records.size();
                long successCalls = records.stream()
                    .filter(m -> m.getStatus() == ApiMonitoringStatus.SUCCESS)
                    .count();
                long failedCalls = totalCalls - successCalls;
                
                OptionalDouble avgDuration = records.stream()
                    .filter(m -> m.getDuration() != null)
                    .mapToLong(ApiMonitoringInfo::getDuration)
                    .average();
                
                double errorRate = totalCalls > 0 ? (failedCalls * 100.0 / totalCalls) : 0.0;
                
                Map<String, Object> summary = new HashMap<>();
                summary.put("totalCalls", totalCalls);
                summary.put("successCalls", successCalls);
                summary.put("failedCalls", failedCalls);
                summary.put("avgResponseTime", avgDuration.isPresent() ? (int) avgDuration.getAsDouble() : 0);
                summary.put("errorRate", errorRate);
                
                // 生成时间线数据（按小时分组）
                List<Map<String, Object>> timeline = generateTimeline(records, start, end);
                
                // 响应时间分布
                Map<String, Long> responseTimeDistribution = calculateResponseTimeDistribution(records);
                
                // 状态码分布
                Map<String, Long> statusCodeDistribution = calculateStatusCodeDistribution(records);
                
                result.put("serviceName", serviceName);
                result.put("path", path);
                result.put("method", method);
                result.put("startDate", startDate);
                result.put("endDate", endDate);
                result.put("summary", summary);
                result.put("timeline", timeline);
                result.put("responseTimeDistribution", responseTimeDistribution);
                result.put("statusCodeDistribution", statusCodeDistribution);
                
                return result;
            }
        }.execute();
    }
    
    /**
     * <p>
     * 获取慢请求列表（使用ApiMonitoringInfo，不包含大文本字段）
     * </p>
     */
    @Override
    public Page<ApiMonitoring> listSlowRequests(Specification<ApiMonitoringInfo> spec, Pageable pageable) {
        return new BizTemplate<Page<ApiMonitoring>>() {
            @Override
            protected Page<ApiMonitoring> process() {
                Page<ApiMonitoringInfo> page = apiMonitoringInfoRepo.findAll(spec, pageable);
                
                // 转换为ApiMonitoring（不包含大文本字段）
                List<ApiMonitoring> content = page.getContent().stream()
                    .map(ApiMonitoringQueryImpl::convertToApiMonitoring)
                    .collect(Collectors.toList());
                
                return new PageImpl<>(content, pageable, page.getTotalElements());
            }
        }.execute();
    }
    
    
    /**
     * <p>
     * 获取错误请求列表（使用ApiMonitoringInfo，不包含大文本字段）
     * </p>
     */
    @Override
    public Page<ApiMonitoring> listErrorRequests(Specification<ApiMonitoringInfo> spec, Pageable pageable) {
        return new BizTemplate<Page<ApiMonitoring>>() {
            @Override
            protected Page<ApiMonitoring> process() {
                Page<ApiMonitoringInfo> page = apiMonitoringInfoRepo.findAll(spec, pageable);
                
                // 转换为ApiMonitoring（不包含大文本字段）
                List<ApiMonitoring> content = page.getContent().stream()
                    .map(ApiMonitoringQueryImpl::convertToApiMonitoring)
                    .collect(Collectors.toList());
                
                return new PageImpl<>(content, pageable, page.getTotalElements());
            }
        }.execute();
    }
    
    
    /**
     * <p>
     * 获取实时QPS数据
     * </p>
     */
    @Override
    public Map<String, Object> getRealtimeQps() {
        return new BizTemplate<Map<String, Object>>() {
            @Override
            protected Map<String, Object> process() {
                Map<String, Object> result = new HashMap<>();
                
                // 获取最近30分钟的数据
                LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minusMinutes(30);
                Specification<ApiMonitoringInfo> spec = (root, query, cb) -> 
                    cb.greaterThanOrEqualTo(root.get("requestTime"), thirtyMinutesAgo);
                
                List<ApiMonitoringInfo> recentRecords = apiMonitoringInfoRepo.findAll(spec);
                
                // 计算当前QPS（最近1分钟）
                LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
                long currentQps = recentRecords.stream()
                    .filter(m -> m.getRequestTime() != null && m.getRequestTime().isAfter(oneMinuteAgo))
                    .count();
                
                // 计算峰值QPS（最近30分钟内的最大值）
                Map<String, Long> qpsByMinute = recentRecords.stream()
                    .collect(Collectors.groupingBy(
                        m -> m.getRequestTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        Collectors.counting()
                    ));
                long peakQps = qpsByMinute.values().stream()
                    .mapToLong(Long::longValue)
                    .max()
                    .orElse(0L);
                
                // 计算平均QPS
                long averageQps = recentRecords.size() / 30;
                
                // 生成时间线数据（最近30分钟，每分钟一个点）
                List<Map<String, Object>> timeline = new ArrayList<>();
                for (int i = 29; i >= 0; i--) {
                    LocalDateTime minuteTime = LocalDateTime.now().minusMinutes(i);
                    String minuteKey = minuteTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                    long qps = qpsByMinute.getOrDefault(minuteKey, 0L);
                    
                    Map<String, Object> item = new HashMap<>();
                    item.put("time", minuteTime.format(DATE_FORMATTER));
                    item.put("qps", (int) qps);
                    timeline.add(item);
                }
                
                result.put("current", (int) currentQps);
                result.put("peak", (int) peakQps);
                result.put("average", (int) averageQps);
                result.put("timeline", timeline);
                
                return result;
            }
        }.execute();
    }
    
    /**
     * <p>
     * 获取实时响应时间数据
     * </p>
     */
    @Override
    public Map<String, Object> getRealtimeResponseTime() {
        return new BizTemplate<Map<String, Object>>() {
            @Override
            protected Map<String, Object> process() {
                Map<String, Object> result = new HashMap<>();
                
                // 获取最近30分钟的数据
                LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minusMinutes(30);
                Specification<ApiMonitoringInfo> spec = (root, query, cb) -> 
                    cb.greaterThanOrEqualTo(root.get("requestTime"), thirtyMinutesAgo);
                
                List<ApiMonitoringInfo> recentRecords = apiMonitoringInfoRepo.findAll(spec);
                
                // 获取所有响应时间
                List<Long> durations = recentRecords.stream()
                    .filter(m -> m.getDuration() != null)
                    .map(ApiMonitoringInfo::getDuration)
                    .sorted()
                    .collect(Collectors.toList());
                
                if (durations.isEmpty()) {
                    result.put("current", 0);
                    result.put("p50", 0);
                    result.put("p95", 0);
                    result.put("p99", 0);
                    result.put("max", 0);
                    result.put("timeline", new ArrayList<>());
                    return result;
                }
                
                // 计算当前平均响应时间（最近1分钟）
                LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
                OptionalDouble currentAvg = recentRecords.stream()
                    .filter(m -> m.getRequestTime() != null && m.getRequestTime().isAfter(oneMinuteAgo))
                    .filter(m -> m.getDuration() != null)
                    .mapToLong(ApiMonitoringInfo::getDuration)
                    .average();
                
                // 计算百分位数
                int p50 = getPercentile(durations, 50);
                int p95 = getPercentile(durations, 95);
                int p99 = getPercentile(durations, 99);
                int max = durations.get(durations.size() - 1).intValue();
                
                // 生成时间线数据（最近30分钟，每分钟一个点）
                List<Map<String, Object>> timeline = new ArrayList<>();
                for (int i = 29; i >= 0; i--) {
                    LocalDateTime minuteTime = LocalDateTime.now().minusMinutes(i);
                    LocalDateTime minuteStart = minuteTime.withSecond(0).withNano(0);
                    LocalDateTime minuteEnd = minuteStart.plusMinutes(1);
                    
                    List<Long> minuteDurations = recentRecords.stream()
                        .filter(m -> m.getRequestTime() != null 
                            && m.getRequestTime().isAfter(minuteStart) 
                            && m.getRequestTime().isBefore(minuteEnd))
                        .filter(m -> m.getDuration() != null)
                        .map(ApiMonitoringInfo::getDuration)
                        .sorted()
                        .collect(Collectors.toList());
                    
                    Map<String, Object> item = new HashMap<>();
                    item.put("time", minuteTime.format(DATE_FORMATTER));
                    if (!minuteDurations.isEmpty()) {
                        OptionalDouble avg = minuteDurations.stream().mapToLong(Long::longValue).average();
                        item.put("avg", avg.isPresent() ? (int) avg.getAsDouble() : 0);
                        item.put("p95", getPercentile(minuteDurations, 95));
                    } else {
                        item.put("avg", 0);
                        item.put("p95", 0);
                    }
                    timeline.add(item);
                }
                
                result.put("current", currentAvg.isPresent() ? (int) currentAvg.getAsDouble() : 0);
                result.put("p50", p50);
                result.put("p95", p95);
                result.put("p99", p99);
                result.put("max", max);
                result.put("timeline", timeline);
                
                return result;
            }
        }.execute();
    }
    
    /**
     * <p>
     * 获取调用量TOP接口
     * </p>
     */
    @Override
    public List<Map<String, Object>> getTopCalls(Integer limit, String period) {
        return new BizTemplate<List<Map<String, Object>>>() {
            @Override
            protected List<Map<String, Object>> process() {
                LocalDateTime start = parseStartDate(null, period);
                LocalDateTime end = parseEndDate(null, period);
                
                Specification<ApiMonitoringInfo> spec = buildTimeRangeSpec(start, end);
                List<ApiMonitoringInfo> records = apiMonitoringInfoRepo.findAll(spec);
                
                // 按服务名称、路径、方法分组统计调用量
                Map<String, Map<String, Object>> statsMap = records.stream()
                    .collect(Collectors.groupingBy(
                        m -> m.getServiceName() + "|" + m.getPath() + "|" + m.getMethod(),
                        Collectors.collectingAndThen(
                            Collectors.toList(),
                            list -> {
                                long totalCalls = list.size();
                                long failedCalls = list.stream()
                                    .filter(m -> m.getStatus() != ApiMonitoringStatus.SUCCESS)
                                    .count();
                                double errorRate = totalCalls > 0 ? (failedCalls * 100.0 / totalCalls) : 0.0;
                                
                                Map<String, Object> stats = new HashMap<>();
                                stats.put("serviceName", list.get(0).getServiceName());
                                stats.put("path", list.get(0).getPath());
                                stats.put("method", list.get(0).getMethod());
                                stats.put("calls", totalCalls);
                                stats.put("errorRate", errorRate);
                                return stats;
                            }
                        )
                    ));
                
                // 按调用量排序并取TOP N
                int actualLimit = limit != null ? limit : 10;
                return statsMap.values().stream()
                    .sorted((a, b) -> Long.compare(
                        ((Long) b.get("calls")), 
                        ((Long) a.get("calls"))
                    ))
                    .limit(actualLimit)
                    .collect(Collectors.toList());
            }
        }.execute();
    }
    
    /**
     * <p>
     * 获取响应时间TOP接口
     * </p>
     */
    @Override
    public List<Map<String, Object>> getTopSlow(Integer limit, String period) {
        return new BizTemplate<List<Map<String, Object>>>() {
            @Override
            protected List<Map<String, Object>> process() {
                LocalDateTime start = parseStartDate(null, period);
                LocalDateTime end = parseEndDate(null, period);
                
                Specification<ApiMonitoringInfo> spec = buildTimeRangeSpec(start, end);
                List<ApiMonitoringInfo> records = apiMonitoringInfoRepo.findAll(spec);
                
                // 按服务名称、路径、方法分组统计平均响应时间
                Map<String, Map<String, Object>> statsMap = records.stream()
                    .filter(m -> m.getDuration() != null)
                    .collect(Collectors.groupingBy(
                        m -> m.getServiceName() + "|" + m.getPath() + "|" + m.getMethod(),
                        Collectors.collectingAndThen(
                            Collectors.toList(),
                            list -> {
                                OptionalDouble avgDuration = list.stream()
                                    .mapToLong(ApiMonitoringInfo::getDuration)
                                    .average();
                                
                                Map<String, Object> stats = new HashMap<>();
                                stats.put("serviceName", list.get(0).getServiceName());
                                stats.put("path", list.get(0).getPath());
                                stats.put("method", list.get(0).getMethod());
                                stats.put("avgResponseTime", avgDuration.isPresent() ? (int) avgDuration.getAsDouble() : 0);
                                stats.put("calls", (long) list.size());
                                return stats;
                            }
                        )
                    ));
                
                // 按平均响应时间排序并取TOP N
                int actualLimit = limit != null ? limit : 10;
                return statsMap.values().stream()
                    .sorted((a, b) -> Integer.compare(
                        ((Integer) b.get("avgResponseTime")), 
                        ((Integer) a.get("avgResponseTime"))
                    ))
                    .limit(actualLimit)
                    .collect(Collectors.toList());
            }
        }.execute();
    }
    
    /**
     * <p>
     * 获取错误率TOP接口
     * </p>
     */
    @Override
    public List<Map<String, Object>> getTopErrors(Integer limit, String period) {
        return new BizTemplate<List<Map<String, Object>>>() {
            @Override
            protected List<Map<String, Object>> process() {
                LocalDateTime start = parseStartDate(null, period);
                LocalDateTime end = parseEndDate(null, period);
                
                Specification<ApiMonitoringInfo> spec = buildTimeRangeSpec(start, end);
                List<ApiMonitoringInfo> records = apiMonitoringInfoRepo.findAll(spec);
                
                // 按服务名称、路径、方法分组统计错误率
                Map<String, Map<String, Object>> statsMap = records.stream()
                    .collect(Collectors.groupingBy(
                        m -> m.getServiceName() + "|" + m.getPath() + "|" + m.getMethod(),
                        Collectors.collectingAndThen(
                            Collectors.toList(),
                            list -> {
                                long totalCalls = list.size();
                                long failedCalls = list.stream()
                                    .filter(m -> m.getStatus() != ApiMonitoringStatus.SUCCESS)
                                    .count();
                                double errorRate = totalCalls > 0 ? (failedCalls * 100.0 / totalCalls) : 0.0;
                                
                                Map<String, Object> stats = new HashMap<>();
                                stats.put("serviceName", list.get(0).getServiceName());
                                stats.put("path", list.get(0).getPath());
                                stats.put("method", list.get(0).getMethod());
                                stats.put("errorRate", errorRate);
                                stats.put("calls", totalCalls);
                                stats.put("failedCalls", failedCalls);
                                return stats;
                            }
                        )
                    ));
                
                // 按错误率排序并取TOP N
                int actualLimit = limit != null ? limit : 10;
                return statsMap.values().stream()
                    .filter(s -> ((Double) s.get("errorRate")) > 0) // 只返回有错误的接口
                    .sorted((a, b) -> Double.compare(
                        ((Double) b.get("errorRate")), 
                        ((Double) a.get("errorRate"))
                    ))
                    .limit(actualLimit)
                    .collect(Collectors.toList());
            }
        }.execute();
    }
    
    
    /**
     * <p>
     * 将ApiMonitoringInfo转换为ApiMonitoring（不包含大文本字段）
     * </p>
     */
    private static ApiMonitoring convertToApiMonitoring(ApiMonitoringInfo info) {
        ApiMonitoring monitoring = new ApiMonitoring();
        monitoring.setId(info.getId());
        monitoring.setTraceId(info.getTraceId());
        monitoring.setServiceName(info.getServiceName());
        monitoring.setPath(info.getPath());
        monitoring.setMethod(info.getMethod());
        monitoring.setRequestTime(info.getRequestTime());
        monitoring.setDuration(info.getDuration());
        monitoring.setStatusCode(info.getStatusCode());
        monitoring.setType(info.getType());
        monitoring.setStatus(info.getStatus());
        monitoring.setIpAddress(info.getIpAddress());
        monitoring.setUserId(info.getUserId());
        monitoring.setUserName(info.getUserName());
        monitoring.setQueryParameters(info.getQueryParameters());
        monitoring.setRequestHeaders(info.getRequestHeaders());
        monitoring.setResponseHeaders(info.getResponseHeaders());
        monitoring.setErrorMessage(info.getErrorMessage());
        monitoring.setErrorType(info.getErrorType());
        monitoring.setResolved(info.getResolved());
        monitoring.setBreakdown(info.getBreakdown());
        monitoring.setSqlStatements(info.getSqlStatements());
        // 注意：不设置大文本字段 requestBody, responseBody, stackTrace
        return monitoring;
    }
    
    /**
     * <p>
     * 解析开始日期
     * </p>
     */
    private LocalDateTime parseStartDate(String startDate, String period) {
        if (StringUtils.hasText(startDate)) {
            try {
                return LocalDateTime.parse(startDate + " 00:00:00", DATE_FORMATTER);
            } catch (Exception e) {
                // 忽略解析错误
            }
        }
        
        if (StringUtils.hasText(period)) {
            return switch (period) {
                case "1h" -> LocalDateTime.now().minusHours(1);
                case "6h" -> LocalDateTime.now().minusHours(6);
                case "24h" -> LocalDateTime.now().minusHours(24);
                case "7d" -> LocalDateTime.now().minusDays(7);
                case "30d" -> LocalDateTime.now().minusDays(30);
                default -> LocalDateTime.now().minusDays(7);
            };
        }
        
        return LocalDateTime.now().minusDays(7);
    }
    
    /**
     * <p>
     * 解析结束日期
     * </p>
     */
    private LocalDateTime parseEndDate(String endDate, String period) {
        if (StringUtils.hasText(endDate)) {
            try {
                return LocalDateTime.parse(endDate + " 23:59:59", DATE_FORMATTER);
            } catch (Exception e) {
                // 忽略解析错误
            }
        }
        
        return LocalDateTime.now();
    }
    
    /**
     * <p>
     * 构建时间范围查询条件
     * </p>
     */
    private Specification<ApiMonitoringInfo> buildTimeRangeSpec(LocalDateTime start, LocalDateTime end) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (start != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("requestTime"), start));
            }
            if (end != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("requestTime"), end));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
    
    /**
     * <p>
     * 计算百分位数
     * </p>
     */
    private int getPercentile(List<Long> sortedList, int percentile) {
        if (sortedList.isEmpty()) {
            return 0;
        }
        int index = (int) Math.ceil(sortedList.size() * percentile / 100.0) - 1;
        index = Math.max(0, Math.min(index, sortedList.size() - 1));
        return sortedList.get(index).intValue();
    }
    
    /**
     * <p>
     * 生成时间线数据
     * </p>
     */
    private List<Map<String, Object>> generateTimeline(List<ApiMonitoringInfo> records, LocalDateTime start, LocalDateTime end) {
        List<Map<String, Object>> timeline = new ArrayList<>();
        
        if (start == null || end == null || records.isEmpty()) {
            return timeline;
        }
        
        // 按小时分组
        Map<String, List<ApiMonitoringInfo>> groupedByHour = records.stream()
            .collect(Collectors.groupingBy(
                m -> m.getRequestTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00:00"))
            ));
        
        LocalDateTime current = start.withMinute(0).withSecond(0).withNano(0);
        while (!current.isAfter(end)) {
            String hourKey = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00:00"));
            List<ApiMonitoringInfo> hourRecords = groupedByHour.getOrDefault(hourKey, new ArrayList<>());
            
            long calls = hourRecords.size();
            OptionalDouble avgDuration = hourRecords.stream()
                .filter(m -> m.getDuration() != null)
                .mapToLong(ApiMonitoringInfo::getDuration)
                .average();
            
            long failedCalls = hourRecords.stream()
                .filter(m -> m.getStatus() != ApiMonitoringStatus.SUCCESS)
                .count();
            double errorRate = calls > 0 ? (failedCalls * 100.0 / calls) : 0.0;
            
            Map<String, Object> item = new HashMap<>();
            item.put("time", hourKey);
            item.put("calls", calls);
            item.put("avgResponseTime", avgDuration.isPresent() ? (int) avgDuration.getAsDouble() : 0);
            item.put("errorRate", errorRate);
            timeline.add(item);
            
            current = current.plusHours(1);
        }
        
        return timeline;
    }
    
    /**
     * <p>
     * 计算响应时间分布
     * </p>
     */
    private Map<String, Long> calculateResponseTimeDistribution(List<ApiMonitoringInfo> records) {
        Map<String, Long> distribution = new HashMap<>();
        distribution.put("0-100ms", 0L);
        distribution.put("100-500ms", 0L);
        distribution.put("500-1000ms", 0L);
        distribution.put("1000-3000ms", 0L);
        distribution.put("3000ms+", 0L);
        
        records.stream()
            .filter(m -> m.getDuration() != null)
            .forEach(m -> {
                long duration = m.getDuration();
                if (duration < 100) {
                    distribution.put("0-100ms", distribution.get("0-100ms") + 1);
                } else if (duration < 500) {
                    distribution.put("100-500ms", distribution.get("100-500ms") + 1);
                } else if (duration < 1000) {
                    distribution.put("500-1000ms", distribution.get("500-1000ms") + 1);
                } else if (duration < 3000) {
                    distribution.put("1000-3000ms", distribution.get("1000-3000ms") + 1);
                } else {
                    distribution.put("3000ms+", distribution.get("3000ms+") + 1);
                }
            });
        
        return distribution;
    }
    
    /**
     * <p>
     * 计算状态码分布
     * </p>
     */
    private Map<String, Long> calculateStatusCodeDistribution(List<ApiMonitoringInfo> records) {
        return records.stream()
            .filter(m -> m.getStatusCode() != null)
            .collect(Collectors.groupingBy(
                m -> String.valueOf(m.getStatusCode()),
                Collectors.counting()
            ));
    }
    
    /**
     * <p>
     * 创建空的汇总数据
     * </p>
     */
    private Map<String, Object> createEmptySummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalCalls", 0L);
        summary.put("successCalls", 0L);
        summary.put("failedCalls", 0L);
        summary.put("avgResponseTime", 0);
        summary.put("errorRate", 0.0);
        return summary;
    }
}
