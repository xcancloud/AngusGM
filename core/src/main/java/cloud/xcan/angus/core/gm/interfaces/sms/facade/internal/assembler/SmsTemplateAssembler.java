package cloud.xcan.angus.core.gm.interfaces.sms.facade.internal.assembler;

import cloud.xcan.angus.core.gm.domain.sms.template.SmsTemplate;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.template.SmsTemplateFindDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.template.SmsTemplateUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.template.SmsTemplateDetailVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.data.jpa.domain.Specification;

public class SmsTemplateAssembler {

  public static SmsTemplate updateDtoToDomain(Long id, SmsTemplateUpdateDto dto) {
    return new SmsTemplate().setId(id)
        .setName(dto.getName())
        .setContent(dto.getContent())
        .setVerificationCode(dto.getVerificationCode())
        .setVerificationCodeValidSecond(dto.getVerificationCodeValidSecond())
        .setThirdCode(dto.getThirdCode())
        .setLanguage(dto.getLanguage());
  }

  public static SmsTemplateDetailVo toVo(SmsTemplate smsTemplate) {
    return new SmsTemplateDetailVo().setId(smsTemplate.getId())
        .setName(smsTemplate.getName())
        .setChannelId(smsTemplate.getChannelId())
        .setSignature(smsTemplate.getSignature())
        .setCode(smsTemplate.getCode())
        .setVerificationCode(smsTemplate.getVerificationCode())
        .setVerificationCodeValidSecond(smsTemplate.getVerificationCodeValidSecond())
        .setThirdCode(smsTemplate.getThirdCode())
        .setContent(smsTemplate.getContent())
        .setLanguage(smsTemplate.getLanguage());
  }

  public static Specification<SmsTemplate> getSpecification(SmsTemplateFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate")
        .orderByFields("id", "createdDate")
        .build();
    return new GenericSpecification<>(filters);
  }

}
