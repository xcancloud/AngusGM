package cloud.xcan.angus.core.gm.interfaces.sms.facade.internal;

import cloud.xcan.angus.core.gm.application.cmd.sms.SmsCmd;
import cloud.xcan.angus.core.gm.application.query.sms.SmsQuery;
import cloud.xcan.angus.core.gm.domain.sms.Sms;
import cloud.xcan.angus.core.gm.domain.sms.SmsStatus;
import cloud.xcan.angus.core.gm.domain.sms.SmsType;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.SmsFacade;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.internal.assembler.SmsAssembler;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SmsFacadeImpl implements SmsFacade {
    private final SmsCmd smsCmd;
    private final SmsQuery smsQuery;

    // ==================== 短信模板管理 ====================

    @Override
    public SmsTemplateVo createTemplate(SmsTemplateCreateDto dto) {
        // TODO: 实现创建短信模板
        return null;
    }
    
    @Override
    public SmsTemplateVo updateTemplate(Long id, SmsTemplateUpdateDto dto) {
        // TODO: 实现更新短信模板
        return null;
    }
    
    @Override
    public SmsTemplateStatusVo updateTemplateStatus(Long id, SmsTemplateStatusDto dto) {
        // TODO: 实现更新短信模板状态
        return null;
    }
    
    @Override
    public void deleteTemplate(Long id) {
        // TODO: 实现删除短信模板
    }

    // ==================== 服务商配置 ====================

    @Override
    public SmsProviderVo createProvider(SmsProviderCreateDto dto) {
        // TODO: 实现创建服务商配置
        return null;
    }

    // ==================== 短信发送 ====================

    @Override
    public SmsSendVo send(SmsSendDto dto) {
        // TODO: 实现发送单条短信
        return null;
    }
    
    @Override
    public SmsSendBatchVo sendBatch(SmsSendBatchDto dto) {
        // TODO: 实现批量发送短信
        return null;
    }
    
    @Override
    public SmsTestVo test(SmsTestDto dto) {
        // TODO: 实现测试短信发送
        return null;
    }

    // ==================== 查询 ====================

    @Override
    public SmsStatsVo getStats() {
        SmsStatsVo stats = new SmsStatsVo();
        stats.setTotal(smsQuery.countTotal());
        
        Map<SmsStatus, Long> statusDist = new HashMap<>();
        for (SmsStatus status : SmsStatus.values()) {
            statusDist.put(status, smsQuery.countByStatus(status));
        }
        stats.setStatusDistribution(statusDist);
        
        Map<SmsType, Long> typeDist = new HashMap<>();
        for (SmsType type : SmsType.values()) {
            typeDist.put(type, smsQuery.countByType(type));
        }
        stats.setTypeDistribution(typeDist);
        
        // Calculate success rate
        long total = stats.getTotal();
        if (total > 0) {
            long successful = smsQuery.countByStatus(SmsStatus.DELIVERED);
            stats.setSuccessRate((double) successful / total * 100);
        } else {
            stats.setSuccessRate(0.0);
        }
        
        return stats;
    }
    
    @Override
    public PageResult<SmsRecordVo> listRecords(SmsRecordFindDto dto) {
        // TODO: 实现分页查询短信记录列表
        return null;
    }
    
    @Override
    public PageResult<SmsTemplateVo> listTemplates(SmsTemplateFindDto dto) {
        // TODO: 实现分页查询短信模板列表
        return null;
    }
    
    @Override
    public List<SmsProviderVo> listProviders() {
        // TODO: 实现获取短信服务商配置列表
        return null;
    }
    
    // ==================== 旧方法（需要重构）====================
    
    @Deprecated
    public SmsDetailVo create(SmsCreateDto dto) {
        Sms sms = SmsAssembler.toEntity(dto);
        Sms created = smsCmd.create(sms);
        return SmsAssembler.toDetailVo(created);
    }
    
    @Deprecated
    public SmsDetailVo update(SmsUpdateDto dto) {
        Sms sms = SmsAssembler.toEntity(dto);
        Sms updated = smsCmd.update(sms);
        return SmsAssembler.toDetailVo(updated);
    }
    
    @Deprecated
    public void retry(Long id) {
        smsCmd.retry(id);
    }
    
    @Deprecated
    public void cancel(Long id) {
        smsCmd.cancel(id);
    }
    
    @Deprecated
    public void delete(Long id) {
        smsCmd.delete(id);
    }
    
    @Deprecated
    public SmsDetailVo findById(Long id) {
        Sms sms = smsQuery.findById(id);
        return SmsAssembler.toDetailVo(sms);
    }
    
    @Deprecated
    public SmsDetailVo send(SmsCreateDto dto) {
        Sms sms = SmsAssembler.toEntity(dto);
        Sms created = smsCmd.create(sms);
        Sms sent = smsCmd.send(created);
        return SmsAssembler.toDetailVo(sent);
    }
    
    @Deprecated
    public Page<SmsListVo> findAll(SmsFindDto dto, Pageable pageable) {
        return smsQuery.findAll(dto.getStatus(), dto.getType(), dto.getPhone(), dto.getTemplateCode(), pageable)
                .map(SmsAssembler::toListVo);
    }
}
