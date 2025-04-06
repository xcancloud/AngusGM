package cloud.xcan.angus.core.gm.interfaces.email.facade.internal.assembler;

import cloud.xcan.angus.core.gm.domain.email.template.EmailTemplate;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.template.EmailTemplateFindDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.template.EmailTemplateUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.template.EmailTemplateDetailVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.data.jpa.domain.Specification;

public class EmailTemplateAssembler {

  public static EmailTemplate updateDtoToDomain(Long id, EmailTemplateUpdateDto dto) {
    return new EmailTemplate().setId(id)
        .setName(dto.getName())
        .setContent(dto.getContent())
        .setVerificationCode(dto.getVerificationCode())
        .setVerificationCodeValidSecond(dto.getVerificationCodeValidSecond())
        .setSubject(dto.getSubjectPrefix())
        .setLanguage(dto.getLanguage());
  }

  public static EmailTemplateDetailVo toDetail(EmailTemplate emailTemplate) {
    return new EmailTemplateDetailVo().setId(emailTemplate.getId())
        .setCode(emailTemplate.getCode())
        .setName(emailTemplate.getName())
        .setContent(emailTemplate.getContent())
        .setVerificationCode(emailTemplate.getVerificationCode())
        .setVerificationCodeValidSecond(emailTemplate.getVerificationCodeValidSecond())
        .setRemark(emailTemplate.getSubject())
        .setLanguage(emailTemplate.getLanguage());
  }

  public static Specification<EmailTemplate> getSpecification(EmailTemplateFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .orderByFields("id", "code")
        .build();
    return new GenericSpecification<>(filters);
  }

}
