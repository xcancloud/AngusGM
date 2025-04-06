package cloud.xcan.angus.core.gm.interfaces.tenant.facade;

import cloud.xcan.angus.api.gm.tenant.dto.cert.BusinessRecognizeDto;
import cloud.xcan.angus.api.gm.tenant.dto.cert.IdCardRecognizeDto;
import cloud.xcan.angus.api.gm.tenant.vo.cert.BusinessRecognizeVo;
import cloud.xcan.angus.api.gm.tenant.vo.cert.IdCardRecognizeVo;

public interface TenantCertRecognizeFacade {

  BusinessRecognizeVo businessRecognize(BusinessRecognizeDto dto);

  IdCardRecognizeVo idcardRecognize(IdCardRecognizeDto dto);
}
