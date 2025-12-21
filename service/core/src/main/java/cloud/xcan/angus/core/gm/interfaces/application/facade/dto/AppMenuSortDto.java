package cloud.xcan.angus.core.gm.interfaces.application.facade.dto;

import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Application menu sort DTO
 */
@Getter
@Setter
@Accessors(chain = true)
public class AppMenuSortDto implements Serializable {

  @NotEmpty
  private List<MenuOrder> menuOrders;

  @Getter
  @Setter
  @Accessors(chain = true)
  public static class MenuOrder implements Serializable {
    private Long id;
    private Integer sortOrder;
  }
}
