package cloud.xcan.angus.core.gm.interfaces.email.facade;

import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.template.EmailTemplateFindDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.template.EmailTemplateUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.template.EmailTemplateDetailVo;
import cloud.xcan.angus.remote.PageResult;

public interface EmailTemplateFacade {

  void update(Long id, EmailTemplateUpdateDto dto);

  EmailTemplateDetailVo detail(Long id);

  PageResult<EmailTemplateDetailVo> list(EmailTemplateFindDto dto);

}
