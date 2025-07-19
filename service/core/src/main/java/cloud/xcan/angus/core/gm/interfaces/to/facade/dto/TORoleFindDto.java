package cloud.xcan.angus.core.gm.interfaces.to.facade.dto;


import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class TORoleFindDto extends PageQuery {

  private Long id;

  private String name;

  private String code;

  private Long appId;

  private Boolean enabled;

}
