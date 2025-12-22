package cloud.xcan.angus.core.gm.application.query.sms;

import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.*;
import cloud.xcan.angus.remote.PageResult;

import java.util.List;

/**
 * <p>SMS query service interface</p>
 */
public interface SmsQuery {
    
    /**
     * <p>Get SMS statistics</p>
     */
    SmsStatsVo getStats();
    
    /**
     * <p>List SMS records</p>
     */
    PageResult<SmsRecordVo> listRecords(SmsRecordFindDto dto);
    
    /**
     * <p>List SMS templates</p>
     */
    PageResult<SmsTemplateVo> listTemplates(SmsTemplateFindDto dto);
    
    /**
     * <p>List SMS providers</p>
     */
    List<SmsProviderVo> listProviders();
}
