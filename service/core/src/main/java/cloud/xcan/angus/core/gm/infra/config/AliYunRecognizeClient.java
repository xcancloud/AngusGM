package cloud.xcan.angus.core.gm.infra.config;

import static cloud.xcan.angus.core.utils.CoreUtils.copyPropertiesIgnoreNull;
import static cloud.xcan.angus.core.utils.GsonUtils.fromJson;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getAuthorization;

import cloud.xcan.angus.core.gm.domain.tenant.cert.BusinessRecognize;
import cloud.xcan.angus.core.gm.domain.tenant.cert.IdCardRecognize;
import cloud.xcan.angus.core.spring.condition.CloudServiceEditionCondition;
import cloud.xcan.angus.core.utils.GsonUtils;
import cloud.xcan.angus.spec.utils.download.FileDownloader;
import cloud.xcan.angus.spec.utils.download.SimpleFileDownloader;
import com.aliyun.darabonba.stream.Client;
import com.aliyun.ocr_api20210707.models.RecognizeBusinessLicenseRequest;
import com.aliyun.ocr_api20210707.models.RecognizeBusinessLicenseResponse;
import com.aliyun.ocr_api20210707.models.RecognizeIdcardRequest;
import com.aliyun.ocr_api20210707.models.RecognizeIdcardResponse;
import com.aliyun.teautil.models.RuntimeOptions;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(CloudServiceEditionCondition.class)
@EnableConfigurationProperties({CertRecognizeProperties.class})
public class AliYunRecognizeClient {

  private final com.aliyun.ocr_api20210707.Client client;

  public AliYunRecognizeClient(CertRecognizeProperties properties) throws Exception {
    com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
        .setAccessKeyId(properties.getAk())
        .setAccessKeySecret(properties.getSk());
    config.endpoint = properties.getEndpoint();
    this.client = new com.aliyun.ocr_api20210707.Client(config);
  }

  public BusinessRecognize recognizeBusinessLicense(String businessLicensePicUrl)
      throws Exception {
    String businessLicensePicPath = new SimpleFileDownloader().downloadFile(
            new URL(businessLicensePicUrl), Map.of("Authorization", getAuthorization()))
        .toFile().getAbsolutePath();
    InputStream bodyStream = Client.readFromFilePath(businessLicensePicPath);
    RecognizeBusinessLicenseRequest request = new RecognizeBusinessLicenseRequest()
        .setBody(bodyStream);
    RecognizeBusinessLicenseResponse response = client
        .recognizeBusinessLicenseWithOptions(request, new RuntimeOptions());
    Map<Object, Object> data = fromJson(response.getBody().data, Map.class);
    return fromJson(GsonUtils.toJson(data.get("data")), BusinessRecognize.class);
  }

  public IdCardRecognize recognizeIDCard(String facePicUrl, String backPicUrl)
      throws Exception {
    FileDownloader downloader = new SimpleFileDownloader();
    String facePicPath = downloader.downloadFile(new URL(facePicUrl),
        Map.of("Authorization", getAuthorization())).toFile().getAbsolutePath();
    InputStream bodyStream = Client.readFromFilePath(facePicPath);
    RecognizeIdcardRequest request = new RecognizeIdcardRequest().setBody(bodyStream);
    RecognizeIdcardResponse response = client
        .recognizeIdcardWithOptions(request, new RuntimeOptions());
    Map<Object, Object> data = fromJson(response.getBody().data, Map.class);
    Map<Object, Object> data0 = (Map<Object, Object>) data.get("data");
    Map<Object, Object> faceData = (Map<Object, Object>) data0.get("face");
    IdCardRecognize faceRecognize = fromJson(GsonUtils.toJson(faceData.get("data")),
        IdCardRecognize.class);

    String backPicPath = downloader.downloadFile(new URL(backPicUrl),
        Map.of("Authorization", getAuthorization())).toFile().getAbsolutePath();
    bodyStream = Client.readFromFilePath(backPicPath);
    request = new RecognizeIdcardRequest().setBody(bodyStream);
    response = client.recognizeIdcardWithOptions(request, new RuntimeOptions());
    data = fromJson(response.getBody().data, Map.class);
    data0 = (Map<Object, Object>) data.get("data");
    Map<Object, Object> backData = (Map<Object, Object>) data0.get("back");
    IdCardRecognize backRecognize = fromJson(GsonUtils.toJson(backData.get("data")),
        IdCardRecognize.class);

    return copyPropertiesIgnoreNull(faceRecognize, backRecognize);
  }
}
