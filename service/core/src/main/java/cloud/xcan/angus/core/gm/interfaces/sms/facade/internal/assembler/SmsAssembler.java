package cloud.xcan.angus.core.gm.interfaces.sms.facade.internal.assembler;

import cloud.xcan.angus.core.gm.domain.sms.Sms;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsCreateDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsDetailVo;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsListVo;

public class SmsAssembler {
    public static Sms toEntity(SmsCreateDto dto) {
        Sms sms = new Sms();
        sms.setPhone(dto.getPhone());
        sms.setContent(dto.getContent());
        sms.setTemplateCode(dto.getTemplateCode());
        sms.setTemplateParams(dto.getTemplateParams());
        sms.setType(dto.getType());
        sms.setProvider(dto.getProvider());
        sms.setMaxRetry(dto.getMaxRetry());
        return sms;
    }
    
    public static Sms toEntity(SmsUpdateDto dto) {
        Sms sms = new Sms();
        sms.setId(dto.getId());
        sms.setPhone(dto.getPhone());
        sms.setContent(dto.getContent());
        sms.setTemplateCode(dto.getTemplateCode());
        sms.setTemplateParams(dto.getTemplateParams());
        sms.setType(dto.getType());
        sms.setProvider(dto.getProvider());
        return sms;
    }
    
    public static SmsDetailVo toDetailVo(Sms sms) {
        SmsDetailVo vo = new SmsDetailVo();
        vo.setId(sms.getId());
        vo.setPhone(sms.getPhone());
        vo.setContent(sms.getContent());
        vo.setTemplateCode(sms.getTemplateCode());
        vo.setTemplateParams(sms.getTemplateParams());
        vo.setStatus(sms.getStatus());
        vo.setType(sms.getType());
        vo.setProvider(sms.getProvider());
        vo.setSendTime(sms.getSendTime());
        vo.setDeliverTime(sms.getDeliverTime());
        vo.setErrorCode(sms.getErrorCode());
        vo.setErrorMessage(sms.getErrorMessage());
        vo.setExternalId(sms.getExternalId());
        vo.setRetryCount(sms.getRetryCount());
        vo.setMaxRetry(sms.getMaxRetry());
        vo.setCreatedAt(sms.getCreatedAt());
        vo.setUpdatedAt(sms.getUpdatedAt());
        return vo;
    }
    
    public static SmsListVo toListVo(Sms sms) {
        SmsListVo vo = new SmsListVo();
        vo.setId(sms.getId());
        vo.setPhone(sms.getPhone());
        vo.setContent(sms.getContent());
        vo.setStatus(sms.getStatus());
        vo.setType(sms.getType());
        vo.setProvider(sms.getProvider());
        vo.setSendTime(sms.getSendTime());
        vo.setRetryCount(sms.getRetryCount());
        vo.setCreatedAt(sms.getCreatedAt());
        return vo;
    }
}
