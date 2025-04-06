package cloud.xcan.angus.core.gm.interfaces.service.facade.dto;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DATE_FMT;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import cloud.xcan.angus.api.commonlink.service.ServiceSource;
import cloud.xcan.angus.remote.PageQuery;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;


@Getter
@Setter
@Accessors(chain = true)
public class ServiceFindDto extends PageQuery {

  @Id
  private Long id;

  @Length(max = MAX_NAME_LENGTH)
  private String name;

  @Length(max = MAX_CODE_LENGTH)
  private String code;

  private Boolean enabled;

  private ServiceSource source;

  @DateTimeFormat(pattern = DATE_FMT)
  private LocalDateTime createdDate;

}
