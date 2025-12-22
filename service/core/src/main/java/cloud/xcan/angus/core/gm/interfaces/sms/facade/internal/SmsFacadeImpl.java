package cloud.xcan.angus.core.gm.interfaces.sms.facade.internal;

import cloud.xcan.angus.core.gm.application.cmd.sms.SmsCmd;
import cloud.xcan.angus.core.gm.application.query.sms.SmsQuery;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.SmsFacade;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.*;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>SMS facade implementation</p>
 */
@Service
public class SmsFacadeImpl implements SmsFacade {
    
    @Resource
    private SmsCmd smsCmd;
    
    @Resource
    private SmsQuery smsQuery;

    // ==================== 短信模板管理 ====================

    @Override
    public SmsTemplateVo createTemplate(SmsTemplateCreateDto dto) {
        return smsCmd.createTemplate(dto);
    }
    
    @Override
    public SmsTemplateVo updateTemplate(Long id, SmsTemplateUpdateDto dto) {
        return smsCmd.updateTemplate(id, dto);
    }
    
    @Override
    public SmsTemplateStatusVo updateTemplateStatus(Long id, SmsTemplateStatusDto dto) {
        return smsCmd.updateTemplateStatus(id, dto);
    }
    
    @Override
    public void deleteTemplate(Long id) {
        smsCmd.deleteTemplate(id);
    }

    // ==================== 服务商配置 ====================

    @Override
    public SmsProviderVo createProvider(SmsProviderCreateDto dto) {
        return smsCmd.createProvider(dto);
    }

    // ==================== 短信发送 ====================

    @Override
    public SmsSendVo send(SmsSendDto dto) {
        return smsCmd.send(dto);
    }
    
    @Override
    public SmsSendBatchVo sendBatch(SmsSendBatchDto dto) {
        return smsCmd.sendBatch(dto);
    }
    
    @Override
    public SmsTestVo test(SmsTestDto dto) {
        return smsCmd.test(dto);
    }

    // ==================== 查询 ====================

    @Override
    public SmsStatsVo getStats() {
        return smsQuery.getStats();
    }
    
    @Override
    public PageResult<SmsRecordVo> listRecords(SmsRecordFindDto dto) {
        return smsQuery.listRecords(dto);
    }
    
    @Override
    public PageResult<SmsTemplateVo> listTemplates(SmsTemplateFindDto dto) {
        return smsQuery.listTemplates(dto);
    }
    
    @Override
    public List<SmsProviderVo> listProviders() {
        return smsQuery.listProviders();
    }
}
