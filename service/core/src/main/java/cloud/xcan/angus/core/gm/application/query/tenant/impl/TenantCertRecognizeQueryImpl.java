package cloud.xcan.angus.core.gm.application.query.tenant.impl;


import static cloud.xcan.angus.core.gm.domain.ThirdMessage.CERT_RECOGNIZE_ERROR;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.tenant.TenantCertRecognizeQuery;
import cloud.xcan.angus.core.gm.domain.tenant.cert.BusinessRecognize;
import cloud.xcan.angus.core.gm.domain.tenant.cert.IdCardRecognize;
import cloud.xcan.angus.core.gm.infra.config.AliYunRecognizeClient;
import cloud.xcan.angus.remote.message.ProtocolException;
import org.springframework.beans.factory.annotation.Autowired;

@Biz
public class TenantCertRecognizeQueryImpl implements TenantCertRecognizeQuery {

  @Autowired(required = false) // Valid only in the cloud service version
  private AliYunRecognizeClient aliyunRecognizeClient;

  @Override
  public BusinessRecognize businessRecognize(String businessLicensePicUrl) {
    return new BizTemplate<BusinessRecognize>() {

      @Override
      protected BusinessRecognize process() {
        try {
          return aliyunRecognizeClient.recognizeBusinessLicense(businessLicensePicUrl);
        } catch (Exception e) {
          throw ProtocolException.of(CERT_RECOGNIZE_ERROR, new Object[]{e.getMessage()});
        }
      }
    }.execute();
  }

  @Override
  public IdCardRecognize idcardRecognize(String facePicUrl, String backPicUrl) {
    return new BizTemplate<IdCardRecognize>() {

      @Override
      protected IdCardRecognize process() {
        try {
          return aliyunRecognizeClient.recognizeIDCard(facePicUrl, backPicUrl);
        } catch (Exception e) {
          throw ProtocolException.of(CERT_RECOGNIZE_ERROR, new Object[]{e.getMessage()});
        }
      }
    }.execute();
  }
}
