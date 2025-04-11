package cloud.xcan.angus.api.commonlink.setting.tenant.apiproxy;

import cloud.xcan.angus.spec.experimental.ValueObjectSupport;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class ServerApiProxy extends ValueObjectSupport<ServerApiProxy> {

  private Boolean enabled;

  private String url;

}
