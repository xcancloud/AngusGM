package cloud.xcan.angus.core.gm.interfaces.sms.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.sms.facade.internal.assembler.SmsTemplateAssembler.getSpecification;
import static cloud.xcan.angus.core.gm.interfaces.sms.facade.internal.assembler.SmsTemplateAssembler.toVo;
import static cloud.xcan.angus.core.gm.interfaces.sms.facade.internal.assembler.SmsTemplateAssembler.updateDtoToDomain;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.gm.application.cmd.sms.SmsTemplateCmd;
import cloud.xcan.angus.core.gm.application.query.sms.SmsTemplateQuery;
import cloud.xcan.angus.core.gm.domain.sms.template.SmsTemplate;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.SmsTemplateFacade;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.template.SmsTemplateFindDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.template.SmsTemplateUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.internal.assembler.SmsTemplateAssembler;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.template.SmsTemplateDetailVo;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class SmsTemplateFacadeImpl implements SmsTemplateFacade {

  @Resource
  private SmsTemplateCmd smsTemplateCmd;

  @Resource
  private SmsTemplateQuery smsTemplateQuery;

  @Override
  public void update(Long id, SmsTemplateUpdateDto dto) {
    smsTemplateCmd.update(updateDtoToDomain(id, dto));
  }

  @Override
  public SmsTemplateDetailVo detail(Long id) {
    SmsTemplate template = smsTemplateQuery.detail(id);
    return toVo(template);
  }

  @Override
  public PageResult<SmsTemplateDetailVo> list(SmsTemplateFindDto dto) {
    Page<SmsTemplate> page = smsTemplateQuery.find(getSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, SmsTemplateAssembler::toVo);
  }
}
