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

/**
 * <p>
 * Implementation of tenant certificate recognition query operations.
 * </p>
 * <p>
 * Manages tenant certificate recognition using Alibaba Cloud OCR services.
 * Provides comprehensive certificate recognition with business license and ID card support.
 * </p>
 * <p>
 * Supports business license recognition, ID card recognition, and error handling
 * for comprehensive tenant certificate recognition administration.
 * </p>
 */
@Biz
public class TenantCertRecognizeQueryImpl implements TenantCertRecognizeQuery {

  @Autowired(required = false) // Valid only in the cloud service version
  private AliYunRecognizeClient aliyunRecognizeClient;

  /**
   * <p>
   * Recognizes business license information from image URL.
   * </p>
   * <p>
   * Uses Alibaba Cloud OCR service to extract business license data.
   * Handles recognition errors and returns structured business information.
   * </p>
   */
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

  /**
   * <p>
   * Recognizes ID card information from front and back image URLs.
   * </p>
   * <p>
   * Uses Alibaba Cloud OCR service to extract ID card data.
   * Handles recognition errors and returns structured ID card information.
   * </p>
   */
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
