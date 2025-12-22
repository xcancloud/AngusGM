package cloud.xcan.angus.core.gm.application.cmd.sms;

import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.*;

/**
 * <p>SMS command service interface</p>
 */
public interface SmsCmd {
    
    /**
     * <p>Send SMS</p>
     */
    SmsSendVo send(SmsSendDto dto);
    
    /**
     * <p>Send SMS batch</p>
     */
    SmsSendBatchVo sendBatch(SmsSendBatchDto dto);
    
    /**
     * <p>Test SMS</p>
     */
    SmsTestVo test(SmsTestDto dto);
    
    /**
     * <p>Create SMS template</p>
     */
    SmsTemplateVo createTemplate(SmsTemplateCreateDto dto);
    
    /**
     * <p>Update SMS template</p>
     */
    SmsTemplateVo updateTemplate(Long id, SmsTemplateUpdateDto dto);
    
    /**
     * <p>Update SMS template status</p>
     */
    SmsTemplateStatusVo updateTemplateStatus(Long id, SmsTemplateStatusDto dto);
    
    /**
     * <p>Delete SMS template</p>
     */
    void deleteTemplate(Long id);
    
    /**
     * <p>Create SMS provider</p>
     */
    SmsProviderVo createProvider(SmsProviderCreateDto dto);
}
