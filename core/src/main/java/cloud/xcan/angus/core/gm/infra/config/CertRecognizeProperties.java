package cloud.xcan.angus.core.gm.infra.config;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@Accessors(chain = true)
@ConfigurationProperties(prefix = "xcan.cert-recognize", ignoreUnknownFields = false)
public class CertRecognizeProperties {

  private String endpoint;
  private String ak;
  private String sk;

}
