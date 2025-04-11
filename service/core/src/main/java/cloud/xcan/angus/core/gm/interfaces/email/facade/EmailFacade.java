package cloud.xcan.angus.core.gm.interfaces.email.facade;

import cloud.xcan.angus.api.gm.email.dto.EmailSendDto;
import cloud.xcan.angus.api.gm.email.dto.EmailVerificationCodeCheckDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailFindDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailTestDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailDetailVo;
import cloud.xcan.angus.remote.PageResult;
import java.util.HashSet;

public interface EmailFacade {

  void send(EmailSendDto dto);

  void verificationCodeCheck(EmailVerificationCodeCheckDto dto);

  void delete(HashSet<Long> ids);

  EmailDetailVo detail(Long id);

  PageResult<EmailDetailVo> list(EmailFindDto dto);

  void mailTest(EmailTestDto dto);
}
