package cloud.xcan.angus.core.gm.interfaces.tenant.facade.internal.assembler;

import cloud.xcan.angus.api.gm.tenant.vo.cert.BusinessRecognizeVo;
import cloud.xcan.angus.api.gm.tenant.vo.cert.IdCardRecognizeVo;
import cloud.xcan.angus.core.gm.domain.tenant.cert.BusinessRecognize;
import cloud.xcan.angus.core.gm.domain.tenant.cert.IdCardRecognize;
import cloud.xcan.angus.core.utils.CoreUtils;

public class TenantCertRecognizeAssembler {

  public static BusinessRecognizeVo dataToBusinessRecognizeVo(BusinessRecognize data) {
    BusinessRecognizeVo vo = CoreUtils.copyProperties(data, new BusinessRecognizeVo());
    vo.setRegistrationDate(data.getRegistrationDate());
    return vo;
  }

  public static IdCardRecognizeVo dataToIdCardRecognizeVo(IdCardRecognize data) {
    return CoreUtils.copyProperties(data, new IdCardRecognizeVo());
  }
}
