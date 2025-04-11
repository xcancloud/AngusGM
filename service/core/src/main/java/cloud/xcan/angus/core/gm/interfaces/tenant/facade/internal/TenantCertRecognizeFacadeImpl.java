package cloud.xcan.angus.core.gm.interfaces.tenant.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.tenant.facade.internal.assembler.TenantCertRecognizeAssembler.dataToBusinessRecognizeVo;
import static cloud.xcan.angus.core.gm.interfaces.tenant.facade.internal.assembler.TenantCertRecognizeAssembler.dataToIdCardRecognizeVo;

import cloud.xcan.angus.api.gm.tenant.dto.cert.BusinessRecognizeDto;
import cloud.xcan.angus.api.gm.tenant.dto.cert.IdCardRecognizeDto;
import cloud.xcan.angus.api.gm.tenant.vo.cert.BusinessRecognizeVo;
import cloud.xcan.angus.api.gm.tenant.vo.cert.IdCardRecognizeVo;
import cloud.xcan.angus.core.gm.application.query.tenant.TenantCertRecognizeQuery;
import cloud.xcan.angus.core.gm.domain.tenant.cert.BusinessRecognize;
import cloud.xcan.angus.core.gm.domain.tenant.cert.IdCardRecognize;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.TenantCertRecognizeFacade;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class TenantCertRecognizeFacadeImpl implements TenantCertRecognizeFacade {

  @Resource
  private TenantCertRecognizeQuery certRecognizeQuery;

  @Override
  public BusinessRecognizeVo businessRecognize(BusinessRecognizeDto dto) {
    BusinessRecognize data = certRecognizeQuery.businessRecognize(dto.getBusinessLicensePicUrl());
    return dataToBusinessRecognizeVo(data);
  }

  @Override
  public IdCardRecognizeVo idcardRecognize(IdCardRecognizeDto dto) {
    IdCardRecognize data = certRecognizeQuery.idcardRecognize(
        dto.getFacePicUrl(), dto.getBackPicUrl());
    return dataToIdCardRecognizeVo(data);
  }
}
