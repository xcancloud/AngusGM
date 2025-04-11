package cloud.xcan.angus.core.gm.interfaces.sms.facade;

import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.template.SmsTemplateFindDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.template.SmsTemplateUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.template.SmsTemplateDetailVo;
import cloud.xcan.angus.remote.PageResult;

public interface SmsTemplateFacade {

  void update(Long id, SmsTemplateUpdateDto dto);

  SmsTemplateDetailVo detail(Long id);

  PageResult<SmsTemplateDetailVo> list(SmsTemplateFindDto dto);
}
