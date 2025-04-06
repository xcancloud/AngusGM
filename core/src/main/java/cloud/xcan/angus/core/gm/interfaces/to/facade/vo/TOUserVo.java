package cloud.xcan.angus.core.gm.interfaces.to.facade.vo;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DATE_FMT;

import cloud.xcan.angus.remote.NameJoinField;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

@Setter
@Getter
@Accessors(chain = true)
public class TOUserVo {

  private Long id;

  private Long userId;

  @NameJoinField(id = "userId", repository = "commonUserBaseRepo")
  private String userName;

  private Long createdBy;

  @NameJoinField(id = "createdBy", repository = "commonUserBaseRepo")
  private String createdByName;

  @DateTimeFormat(pattern = DATE_FMT)
  private LocalDateTime createdDate;

}
