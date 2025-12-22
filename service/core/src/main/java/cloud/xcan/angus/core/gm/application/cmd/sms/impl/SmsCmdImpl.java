package cloud.xcan.angus.core.gm.application.cmd.sms.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.cmd.sms.SmsCmd;
import cloud.xcan.angus.core.gm.application.query.sms.SmsQuery;
import cloud.xcan.angus.core.gm.domain.sms.*;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.*;
import cloud.xcan.angus.remote.message.http.ResourceExisted;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>Implementation of SMS command service</p>
 */
@Biz
public class SmsCmdImpl implements SmsCmd {
    
    @Resource
    private SmsRepo smsRepo;
    
    @Resource
    private SmsTemplateRepo smsTemplateRepo;
    
    @Resource
    private SmsProviderRepo smsProviderRepo;
    
    @Resource
    private SmsQuery smsQuery;
    
    @Resource
    private ObjectMapper objectMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SmsSendVo send(SmsSendDto dto) {
        return new BizTemplate<SmsSendVo>() {
            SmsTemplate templateDb;
            Sms sms;
            
            @Override
            protected void checkParams() {
                templateDb = smsTemplateRepo.findById(dto.getTemplateId())
                    .orElseThrow(() -> ResourceNotFound.of("短信模板未找到", new Object[]{}));
                
                if (!"已启用".equals(templateDb.getStatus())) {
                    throw new IllegalArgumentException("短信模板未启用");
                }
            }
            
            @Override
            protected SmsSendVo process() {
                // Create SMS record
                sms = new Sms();
                sms.setPhone(dto.getPhone());
                sms.setTemplateId(dto.getTemplateId());
                
                // Replace template params
                String content = templateDb.getContent();
                if (dto.getParams() != null) {
                    for (Map.Entry<String, String> entry : dto.getParams().entrySet()) {
                        content = content.replace("{" + entry.getKey() + "}", entry.getValue());
                    }
                }
                sms.setContent(content);
                
                // Set template code and params
                sms.setTemplateCode(templateDb.getTemplateCode());
                try {
                    sms.setTemplateParams(objectMapper.writeValueAsString(dto.getParams()));
                } catch (Exception e) {
                    throw new RuntimeException("Failed to serialize params", e);
                }
                
                sms.setType(SmsType.VERIFICATION);
                sms.setProvider(templateDb.getProvider());
                sms.setStatus(SmsStatus.PENDING);
                sms.setSendTime(LocalDateTime.now());
                
                // TODO: Call SMS provider API to send SMS
                sms.setStatus(SmsStatus.SENT);
                sms.setMessageId("msg-" + System.currentTimeMillis());
                
                Sms saved = smsRepo.save(sms);
                
                SmsSendVo vo = new SmsSendVo();
                vo.setId(saved.getId());
                vo.setPhone(maskPhone(dto.getPhone()));
                vo.setContent(saved.getContent());
                vo.setTemplateId(saved.getTemplateId());
                vo.setStatus("成功");
                vo.setSentTime(saved.getSendTime());
                vo.setMessageId(saved.getMessageId());
                
                return vo;
            }
        }.execute();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SmsSendBatchVo sendBatch(SmsSendBatchDto dto) {
        return new BizTemplate<SmsSendBatchVo>() {
            SmsTemplate templateDb;
            
            @Override
            protected void checkParams() {
                templateDb = smsTemplateRepo.findById(dto.getTemplateId())
                    .orElseThrow(() -> ResourceNotFound.of("短信模板未找到", new Object[]{}));
                
                if (!"已启用".equals(templateDb.getStatus())) {
                    throw new IllegalArgumentException("短信模板未启用");
                }
            }
            
            @Override
            protected SmsSendBatchVo process() {
                List<SmsSendBatchVo.SmsSendResultVo> results = new ArrayList<>();
                int successCount = 0;
                int failedCount = 0;
                
                for (String phone : dto.getPhones()) {
                    try {
                        // Create SMS record
                        Sms sms = new Sms();
                        sms.setPhone(phone);
                        sms.setTemplateId(dto.getTemplateId());
                        
                        // Replace template params
                        String content = templateDb.getContent();
                        if (dto.getParams() != null) {
                            for (Map.Entry<String, String> entry : dto.getParams().entrySet()) {
                                content = content.replace("{" + entry.getKey() + "}", entry.getValue());
                            }
                        }
                        sms.setContent(content);
                        
                        sms.setTemplateCode(templateDb.getTemplateCode());
                        try {
                            sms.setTemplateParams(objectMapper.writeValueAsString(dto.getParams()));
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to serialize params", e);
                        }
                        
                        sms.setType(SmsType.NOTIFICATION);
                        sms.setProvider(templateDb.getProvider());
                        sms.setStatus(SmsStatus.PENDING);
                        sms.setSendTime(LocalDateTime.now());
                        
                        // TODO: Call SMS provider API to send SMS
                        sms.setStatus(SmsStatus.SENT);
                        sms.setMessageId("msg-" + System.currentTimeMillis());
                        
                        smsRepo.save(sms);
                        
                        SmsSendBatchVo.SmsSendResultVo result = new SmsSendBatchVo.SmsSendResultVo();
                        result.setPhone(maskPhone(phone));
                        result.setStatus("成功");
                        result.setMessageId(sms.getMessageId());
                        results.add(result);
                        successCount++;
                    } catch (Exception e) {
                        SmsSendBatchVo.SmsSendResultVo result = new SmsSendBatchVo.SmsSendResultVo();
                        result.setPhone(maskPhone(phone));
                        result.setStatus("失败");
                        results.add(result);
                        failedCount++;
                    }
                }
                
                SmsSendBatchVo vo = new SmsSendBatchVo();
                vo.setTotalCount(dto.getPhones().size());
                vo.setSuccessCount(successCount);
                vo.setFailedCount(failedCount);
                vo.setResults(results);
                
                return vo;
            }
        }.execute();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SmsTestVo test(SmsTestDto dto) {
        return new BizTemplate<SmsTestVo>() {
            @Override
            protected void checkParams() {
                // Validate phone format
                if (dto.getPhone() == null || !dto.getPhone().matches("^1[3-9]\\d{9}$")) {
                    throw new IllegalArgumentException("手机号格式错误");
                }
            }
            
            @Override
            protected SmsTestVo process() {
                // Create test SMS record
                Sms sms = new Sms();
                sms.setPhone(dto.getPhone());
                sms.setContent(dto.getContent());
                sms.setType(SmsType.TEST);
                sms.setStatus(SmsStatus.PENDING);
                sms.setSendTime(LocalDateTime.now());
                
                // TODO: Call SMS provider API to send test SMS
                sms.setStatus(SmsStatus.SENT);
                sms.setMessageId("test-msg-" + System.currentTimeMillis());
                
                smsRepo.save(sms);
                
                SmsTestVo vo = new SmsTestVo();
                vo.setPhone(maskPhone(dto.getPhone()));
                vo.setStatus("成功");
                vo.setMessageId(sms.getMessageId());
                vo.setSentTime(sms.getSendTime());
                
                return vo;
            }
        }.execute();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SmsTemplateVo createTemplate(SmsTemplateCreateDto dto) {
        return new BizTemplate<SmsTemplateVo>() {
            @Override
            protected void checkParams() {
                if (smsTemplateRepo.existsByCode(dto.getCode())) {
                    throw ResourceExisted.of("短信模板编码「{0}」已存在", new Object[]{dto.getCode()});
                }
            }
            
            @Override
            protected SmsTemplateVo process() {
                SmsTemplate template = new SmsTemplate();
                template.setName(dto.getName());
                template.setCode(dto.getCode());
                template.setType(dto.getType());
                template.setContent(dto.getContent());
                template.setParams(dto.getParams());
                template.setProvider(dto.getProvider());
                template.setTemplateCode(dto.getTemplateCode());
                template.setStatus("已启用");
                
                SmsTemplate saved = smsTemplateRepo.save(template);
                
                SmsTemplateVo vo = new SmsTemplateVo();
                vo.setId(saved.getId());
                vo.setName(saved.getName());
                vo.setCode(saved.getCode());
                vo.setType(saved.getType());
                vo.setContent(saved.getContent());
                vo.setParams(saved.getParams());
                vo.setStatus(saved.getStatus());
                vo.setProvider(saved.getProvider());
                vo.setTemplateCode(saved.getTemplateCode());
                vo.setUsageCount(0L);
                
                return vo;
            }
        }.execute();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SmsTemplateVo updateTemplate(Long id, SmsTemplateUpdateDto dto) {
        return new BizTemplate<SmsTemplateVo>() {
            SmsTemplate templateDb;
            
            @Override
            protected void checkParams() {
                templateDb = smsTemplateRepo.findById(id)
                    .orElseThrow(() -> ResourceNotFound.of("短信模板未找到", new Object[]{}));
                
                if (!templateDb.getCode().equals(dto.getCode()) 
                    && smsTemplateRepo.existsByCodeAndIdNot(dto.getCode(), id)) {
                    throw ResourceExisted.of("短信模板编码「{0}」已存在", new Object[]{dto.getCode()});
                }
            }
            
            @Override
            protected SmsTemplateVo process() {
                templateDb.setName(dto.getName());
                templateDb.setCode(dto.getCode());
                templateDb.setType(dto.getType());
                templateDb.setContent(dto.getContent());
                templateDb.setParams(dto.getParams());
                templateDb.setProvider(dto.getProvider());
                templateDb.setTemplateCode(dto.getTemplateCode());
                
                SmsTemplate saved = smsTemplateRepo.save(templateDb);
                
                SmsTemplateVo vo = new SmsTemplateVo();
                vo.setId(saved.getId());
                vo.setName(saved.getName());
                vo.setCode(saved.getCode());
                vo.setType(saved.getType());
                vo.setContent(saved.getContent());
                vo.setParams(saved.getParams());
                vo.setStatus(saved.getStatus());
                vo.setProvider(saved.getProvider());
                vo.setTemplateCode(saved.getTemplateCode());
                
                return vo;
            }
        }.execute();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SmsTemplateStatusVo updateTemplateStatus(Long id, SmsTemplateStatusDto dto) {
        return new BizTemplate<SmsTemplateStatusVo>() {
            SmsTemplate templateDb;
            
            @Override
            protected void checkParams() {
                templateDb = smsTemplateRepo.findById(id)
                    .orElseThrow(() -> ResourceNotFound.of("短信模板未找到", new Object[]{}));
            }
            
            @Override
            protected SmsTemplateStatusVo process() {
                templateDb.setStatus(dto.getStatus());
                SmsTemplate saved = smsTemplateRepo.save(templateDb);
                
                SmsTemplateStatusVo vo = new SmsTemplateStatusVo();
                vo.setId(saved.getId());
                vo.setStatus(saved.getStatus());
                vo.setModifiedDate(saved.getLastModifiedDate());
                
                return vo;
            }
        }.execute();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTemplate(Long id) {
        new BizTemplate<Void>() {
            @Override
            protected void checkParams() {
                smsTemplateRepo.findById(id)
                    .orElseThrow(() -> ResourceNotFound.of("短信模板未找到", new Object[]{}));
            }
            
            @Override
            protected Void process() {
                smsTemplateRepo.deleteById(id);
                return null;
            }
        }.execute();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SmsProviderVo createProvider(SmsProviderCreateDto dto) {
        return new BizTemplate<SmsProviderVo>() {
            @Override
            protected void checkParams() {
                if (smsProviderRepo.existsByCode(dto.getCode())) {
                    throw ResourceExisted.of("服务商编码「{0}」已存在", new Object[]{dto.getCode()});
                }
                
                // If this is set as default, unset other default providers
                if (Boolean.TRUE.equals(dto.getIsDefault())) {
                    smsProviderRepo.findByIsDefaultTrue().ifPresent(provider -> {
                        provider.setIsDefault(false);
                        smsProviderRepo.save(provider);
                    });
                }
            }
            
            @Override
            protected SmsProviderVo process() {
                SmsProvider provider = new SmsProvider();
                provider.setName(dto.getName());
                provider.setCode(dto.getCode());
                provider.setIsDefault(dto.getIsDefault() != null ? dto.getIsDefault() : false);
                provider.setIsEnabled(true);
                provider.setConfig(dto.getConfig());
                
                SmsProvider saved = smsProviderRepo.save(provider);
                
                SmsProviderVo vo = new SmsProviderVo();
                vo.setId(saved.getId());
                vo.setName(saved.getName());
                vo.setCode(saved.getCode());
                vo.setIsDefault(saved.getIsDefault());
                vo.setIsEnabled(saved.getIsEnabled());
                vo.setConfig(saved.getConfig());
                vo.setBalance(0L);
                vo.setMonthlyQuota(0L);
                
                return vo;
            }
        }.execute();
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
