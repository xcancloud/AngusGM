package cloud.xcan.angus.core.gm.application.query.tenant;

import cloud.xcan.angus.core.gm.domain.tenant.cert.BusinessRecognize;
import cloud.xcan.angus.core.gm.domain.tenant.cert.IdCardRecognize;

public interface TenantCertRecognizeQuery {

  BusinessRecognize businessRecognize(String businessLicensePicUrl);

  IdCardRecognize idcardRecognize(String facePicUrl, String backPicUrl);
}
