package cloud.xcan.angus.core.gm.interfaces.application.facade.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Application menu VO
 */
@Getter
@Setter
@Accessors(chain = true)
public class AppMenuVo implements Serializable {

  private Long tenantId;

  private Long createdBy;

  private String creator;

  private LocalDateTime createdDate;

  private Long modifiedBy;

  private String modifier;

  private LocalDateTime modifiedDate;

  private Long id;

  private Long appId;

  private String name;

  private String code;

  private String icon;

  private String path;

  private Long parentId;

  private Integer sortOrder;

  private Boolean isVisible;

  private String permission;

  private List<AppMenuVo> children;
}
