package cloud.xcan.angus.core.gm.domain.operation;

import cloud.xcan.angus.spec.experimental.Resources;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class SimpleResources implements Resources {

  private OperationResourceType resource;

  private Long id;

  private String name;

}
