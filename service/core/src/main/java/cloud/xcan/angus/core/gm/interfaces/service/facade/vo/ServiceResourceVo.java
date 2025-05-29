package cloud.xcan.angus.core.gm.interfaces.service.facade.vo;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class ServiceResourceVo {

  private Long serviceId;

  private String serviceCode;

  private String serviceName;

  private String serviceDescription;

  private Boolean serviceEnabled;

  private List<ResourceVo> resources;

}
