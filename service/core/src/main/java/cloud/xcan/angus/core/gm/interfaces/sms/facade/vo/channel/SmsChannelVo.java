package cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.channel;


import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class SmsChannelVo implements Serializable {

  private Long id;

  private String name;

  private Boolean enabled;

  private String logo;

  private String accessKeySecret;

  private String accessKeyId;

  private String endpoint;

  private String thirdChannelNo;

}
