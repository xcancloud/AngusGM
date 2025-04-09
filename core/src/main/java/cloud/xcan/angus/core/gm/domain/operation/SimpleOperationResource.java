package cloud.xcan.angus.core.gm.domain.operation;

import cloud.xcan.angus.api.commonlink.operation.OperationResource;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class SimpleOperationResource implements OperationResource {

  private OperationResourceType resource;

  private Long id;

  private String name;

}
