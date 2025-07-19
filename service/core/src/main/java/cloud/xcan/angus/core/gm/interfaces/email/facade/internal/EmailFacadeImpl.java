package cloud.xcan.angus.core.gm.interfaces.email.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.email.facade.internal.assembler.EmailAssembler.getSpecification;
import static cloud.xcan.angus.core.gm.interfaces.email.facade.internal.assembler.EmailAssembler.testToDomain;
import static cloud.xcan.angus.core.gm.interfaces.email.facade.internal.assembler.EmailAssembler.toDetailVo;
import static cloud.xcan.angus.core.gm.interfaces.email.facade.internal.assembler.EmailAssembler.toDomain;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.api.gm.email.dto.EmailSendDto;
import cloud.xcan.angus.api.gm.email.dto.EmailVerificationCodeCheckDto;
import cloud.xcan.angus.core.gm.application.cmd.email.EmailCmd;
import cloud.xcan.angus.core.gm.application.query.email.EmailQuery;
import cloud.xcan.angus.core.gm.domain.email.Email;
import cloud.xcan.angus.core.gm.interfaces.email.facade.EmailFacade;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailFindDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailTestDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.internal.assembler.EmailAssembler;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailDetailVo;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import java.util.HashSet;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class EmailFacadeImpl implements EmailFacade {

  @Resource
  private EmailCmd emailCmd;

  @Resource
  private EmailQuery emailQuery;

  @Override
  public void send(EmailSendDto dto) {
    emailCmd.send(toDomain(dto), false);
  }

  @Override
  public void mailTest(EmailTestDto dto) {
    emailCmd.send(testToDomain(dto), true);
  }

  @Override
  public void delete(HashSet<Long> ids) {
    emailCmd.delete(ids);
  }

  @Override
  public void verificationCodeCheck(EmailVerificationCodeCheckDto dto) {
    emailCmd.checkVerificationCode(dto.getBizKey(), dto.getEmail(), dto.getVerificationCode());
  }

  @Override
  public EmailDetailVo detail(Long id) {
    Email email = emailQuery.detail(id);
    return toDetailVo(email);
  }

  @Override
  public PageResult<EmailDetailVo> list(EmailFindDto dto) {
    Page<Email> page = emailQuery.list(getSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, EmailAssembler::toDetailVo);
  }

}
