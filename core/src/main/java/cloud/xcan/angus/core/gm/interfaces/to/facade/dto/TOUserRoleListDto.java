package cloud.xcan.angus.core.gm.interfaces.to.facade.dto;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DATE_FMT;

import cloud.xcan.angus.remote.PageQuery;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;


@Getter
@Setter
@Accessors(chain = true)
public class TOUserRoleListDto extends PageQuery {

  private Long id;

  private Long userId;

  private Long toRoleId;

  @DateTimeFormat(pattern = DATE_FMT)
  private LocalDateTime createdDate;

}
