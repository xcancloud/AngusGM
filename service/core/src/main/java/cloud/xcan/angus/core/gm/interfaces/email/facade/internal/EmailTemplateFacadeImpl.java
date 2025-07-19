package cloud.xcan.angus.core.gm.interfaces.email.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.email.facade.internal.assembler.EmailTemplateAssembler.getSpecification;
import static cloud.xcan.angus.core.gm.interfaces.email.facade.internal.assembler.EmailTemplateAssembler.toDetail;
import static cloud.xcan.angus.core.gm.interfaces.email.facade.internal.assembler.EmailTemplateAssembler.updateDtoToDomain;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.gm.application.cmd.email.EmailTemplateCmd;
import cloud.xcan.angus.core.gm.application.query.email.EmailTemplateQuery;
import cloud.xcan.angus.core.gm.domain.email.template.EmailTemplate;
import cloud.xcan.angus.core.gm.interfaces.email.facade.EmailTemplateFacade;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.template.EmailTemplateFindDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.template.EmailTemplateUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.internal.assembler.EmailTemplateAssembler;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.template.EmailTemplateDetailVo;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class EmailTemplateFacadeImpl implements EmailTemplateFacade {

  @Resource
  private EmailTemplateCmd emailTemplateCmd;

  @Resource
  private EmailTemplateQuery emailTemplateQuery;

  @Override
  public void update(Long id, EmailTemplateUpdateDto dto) {
    emailTemplateCmd.update(updateDtoToDomain(id, dto));
  }

  @Override
  public EmailTemplateDetailVo detail(Long id) {
    return toDetail(emailTemplateQuery.detail(id));
  }

  @Override
  public PageResult<EmailTemplateDetailVo> list(EmailTemplateFindDto dto) {
    Page<EmailTemplate> page = emailTemplateQuery.list(getSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, EmailTemplateAssembler::toDetail);
  }

}
