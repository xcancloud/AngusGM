package cloud.xcan.angus.core.gm.interfaces.app.facade.dto.tag;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DATE_FMT;

import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;


@Getter
@Setter
@Accessors(chain = true)
public class AppTagSearchDto extends PageQuery {

  @Schema(description = "Application tag id")
  private Long id;

  @Schema(description = "Application tag name")
  private String name;

  @DateTimeFormat(pattern = DATE_FMT)
  @Schema(description = "Application tag created date")
  private LocalDateTime createdDate;

}
