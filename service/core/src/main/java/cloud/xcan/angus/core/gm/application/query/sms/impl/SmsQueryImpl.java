package cloud.xcan.angus.core.gm.application.query.sms.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.sms.SmsQuery;
import cloud.xcan.angus.core.gm.domain.sms.*;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.*;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>Implementation of SMS query service</p>
 */
@Biz
@Service
@Transactional(readOnly = true)
public class SmsQueryImpl implements SmsQuery {
    
    @Resource
    private SmsRepo smsRepo;
    
    @Resource
    private SmsTemplateRepo smsTemplateRepo;
    
    @Resource
    private SmsProviderRepo smsProviderRepo;
    
    @Override
    public SmsStatsVo getStats() {
        return new BizTemplate<SmsStatsVo>() {
            @Override
            protected SmsStatsVo process() {
                long totalSent = smsRepo.count();
                long successCount = smsRepo.countByStatus(SmsStatus.SENT) + smsRepo.countByStatus(SmsStatus.DELIVERED);
                long failedCount = smsRepo.countByStatus(SmsStatus.FAILED);
                
                // Today's count
                LocalDateTime todayStart = LocalDate.now().atStartOfDay();
                LocalDateTime todayEnd = LocalDate.now().atTime(23, 59, 59);
                long todaySent = smsRepo.findByStatusAndSendTimeBetween(SmsStatus.SENT, todayStart, todayEnd).size()
                    + smsRepo.findByStatusAndSendTimeBetween(SmsStatus.DELIVERED, todayStart, todayEnd).size();
                
                // This month's count
                LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
                long thisMonthSent = smsRepo.findByStatusAndSendTimeBetween(SmsStatus.SENT, monthStart, todayEnd).size()
                    + smsRepo.findByStatusAndSendTimeBetween(SmsStatus.DELIVERED, monthStart, todayEnd).size();
                
                // Get balance from default provider
                long balance = smsProviderRepo.findByIsDefaultTrue()
                    .map(SmsProvider::getBalance)
                    .orElse(0L);
                
                SmsStatsVo vo = new SmsStatsVo();
                vo.setTotalSent(totalSent);
                vo.setSuccessCount(successCount);
                vo.setFailedCount(failedCount);
                vo.setTodaySent(todaySent);
                vo.setThisMonthSent(thisMonthSent);
                vo.setBalance(balance);
                
                return vo;
            }
        }.execute();
    }
    
    @Override
    public PageResult<SmsRecordVo> listRecords(SmsRecordFindDto dto) {
        return new BizTemplate<PageResult<SmsRecordVo>>() {
            @Override
            protected PageResult<SmsRecordVo> process() {
                Pageable pageable = PageRequest.of(dto.getPage() - 1, dto.getSize());
                
                Specification<Sms> spec = (root, query, cb) -> {
                    List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
                    
                    if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
                        String keyword = "%" + dto.getKeyword() + "%";
                        predicates.add(cb.or(
                            cb.like(root.get("phone"), keyword),
                            cb.like(root.get("content"), keyword)
                        ));
                    }
                    
                    if (dto.getStatus() != null && !dto.getStatus().isEmpty()) {
                        SmsStatus status = mapStatus(dto.getStatus());
                        if (status != null) {
                            predicates.add(cb.equal(root.get("status"), status));
                        }
                    }
                    
                    if (dto.getTemplateId() != null) {
                        predicates.add(cb.equal(root.get("templateId"), dto.getTemplateId()));
                    }
                    
                    if (dto.getStartDate() != null) {
                        predicates.add(cb.greaterThanOrEqualTo(root.get("sendTime"), dto.getStartDate().atStartOfDay()));
                    }
                    
                    if (dto.getEndDate() != null) {
                        predicates.add(cb.lessThanOrEqualTo(root.get("sendTime"), dto.getEndDate().atTime(23, 59, 59)));
                    }
                    
                    return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
                };
                
                Page<Sms> page = smsRepo.findAll(spec, pageable);
                
                List<SmsRecordVo> vos = page.getContent().stream()
                    .map(sms -> {
                        SmsRecordVo vo = new SmsRecordVo();
                        vo.setId(sms.getId());
                        vo.setPhone(maskPhone(sms.getPhone()));
                        vo.setContent(sms.getContent());
                        vo.setTemplateId(sms.getTemplateId());
                        
                        // Load template name
                        if (sms.getTemplateId() != null) {
                            smsTemplateRepo.findById(sms.getTemplateId())
                                .ifPresent(template -> vo.setTemplateName(template.getName()));
                        }
                        
                        vo.setStatus(mapStatusToString(sms.getStatus()));
                        vo.setSentTime(sms.getSendTime());
                        vo.setDeliveredTime(sms.getDeliverTime());
                        vo.setProvider(sms.getProvider());
                        vo.setCost(sms.getCost());
                        
                        return vo;
                    })
                    .collect(Collectors.toList());
                
                return PageResult.of(page.getTotalElements(), vos);
            }
        }.execute();
    }
    
    @Override
    public PageResult<SmsTemplateVo> listTemplates(SmsTemplateFindDto dto) {
        return new BizTemplate<PageResult<SmsTemplateVo>>() {
            @Override
            protected PageResult<SmsTemplateVo> process() {
                Pageable pageable = PageRequest.of(dto.getPage() - 1, dto.getSize());
                
                Specification<SmsTemplate> spec = (root, query, cb) -> {
                    List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
                    
                    if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
                        String keyword = "%" + dto.getKeyword() + "%";
                        predicates.add(cb.or(
                            cb.like(root.get("name"), keyword),
                            cb.like(root.get("code"), keyword),
                            cb.like(root.get("content"), keyword)
                        ));
                    }
                    
                    if (dto.getStatus() != null && !dto.getStatus().isEmpty()) {
                        predicates.add(cb.equal(root.get("status"), dto.getStatus()));
                    }
                    
                    if (dto.getType() != null && !dto.getType().isEmpty()) {
                        predicates.add(cb.equal(root.get("type"), dto.getType()));
                    }
                    
                    return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
                };
                
                Page<SmsTemplate> page = smsTemplateRepo.findAll(spec, pageable);
                
                List<SmsTemplateVo> vos = page.getContent().stream()
                    .map(template -> {
                        SmsTemplateVo vo = new SmsTemplateVo();
                        vo.setId(template.getId());
                        vo.setName(template.getName());
                        vo.setCode(template.getCode());
                        vo.setType(template.getType());
                        vo.setContent(template.getContent());
                        vo.setParams(template.getParams());
                        vo.setStatus(template.getStatus());
                        vo.setProvider(template.getProvider());
                        vo.setTemplateCode(template.getTemplateCode());
                        
                        // Load usage count
                        long usageCount = smsRepo.countByTemplateId(template.getId());
                        vo.setUsageCount(usageCount);
                        
                        return vo;
                    })
                    .collect(Collectors.toList());
                
                return PageResult.of(page.getTotalElements(), vos);
            }
        }.execute();
    }
    
    @Override
    public List<SmsProviderVo> listProviders() {
        return new BizTemplate<List<SmsProviderVo>>() {
            @Override
            protected List<SmsProviderVo> process() {
                List<SmsProvider> providers = smsProviderRepo.findAll();
                
                return providers.stream()
                    .map(provider -> {
                        SmsProviderVo vo = new SmsProviderVo();
                        vo.setId(provider.getId());
                        vo.setName(provider.getName());
                        vo.setCode(provider.getCode());
                        vo.setIsDefault(provider.getIsDefault());
                        vo.setIsEnabled(provider.getIsEnabled());
                        vo.setConfig(provider.getConfig());
                        vo.setBalance(provider.getBalance());
                        vo.setMonthlyQuota(provider.getMonthlyQuota());
                        
                        return vo;
                    })
                    .collect(Collectors.toList());
            }
        }.execute();
    }
    
    /**
     * <p>Map status string to SmsStatus enum</p>
     */
    private SmsStatus mapStatus(String status) {
        if (status == null) return null;
        switch (status) {
            case "成功":
            case "已发送":
                return SmsStatus.SENT;
            case "失败":
                return SmsStatus.FAILED;
            case "待发送":
                return SmsStatus.PENDING;
            default:
                return null;
        }
    }
    
    /**
     * <p>Map SmsStatus enum to status string</p>
     */
    private String mapStatusToString(SmsStatus status) {
        if (status == null) return null;
        switch (status) {
            case SENT:
            case DELIVERED:
                return "成功";
            case FAILED:
                return "失败";
            case PENDING:
                return "待发送";
            default:
                return status.name();
        }
    }
    
    /**
     * <p>Mask phone number</p>
     */
    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
}
