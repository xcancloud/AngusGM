package cloud.xcan.angus.core.gm.interfaces.api.facade.dto;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DATE_FMT;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import cloud.xcan.angus.api.enums.ApiType;
import cloud.xcan.angus.remote.PageQuery;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;


@Getter
@Setter
@Accessors(chain = true)
public class ApiFindDto extends PageQuery {

  private Long id;

  @Length(max = MAX_NAME_LENGTH)
  private String name;

  @Length(max = MAX_CODE_LENGTH)
  private String code;

  private Long serviceId;

  @Length(max = MAX_NAME_LENGTH)
  private String serviceName;

  private Boolean enabled;

  private ApiType type;

  @DateTimeFormat(pattern = DATE_FMT)
  private LocalDateTime createdDate;

}
