package cloud.xcan.angus.core.gm.interfaces.application.facade.dto;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Application menu create DTO
 */
@Getter
@Setter
@Accessors(chain = true)
public class AppMenuCreateDto implements Serializable {

  @NotBlank
  private String name;

  @NotBlank
  private String code;

  private String icon;

  private String path;

  private Long parentId;

  private Integer sortOrder;

  private Boolean isVisible;

  private String permission;
}
