package cloud.xcan.angus.core.gm.interfaces.api.facade.dto;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DATE_FMT;

import cloud.xcan.angus.api.enums.ApiType;
import cloud.xcan.angus.remote.PageQuery;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;


@Getter
@Setter
@Accessors(chain = true)
public class ApiSearchDto extends PageQuery {

  private Long id;

  private String name;

  private String operationId;

  private Long serviceId;

  private String serviceName;

  private Boolean enabled;

  private ApiType type;

  @DateTimeFormat(pattern = DATE_FMT)
  private LocalDateTime createdDate;
}
