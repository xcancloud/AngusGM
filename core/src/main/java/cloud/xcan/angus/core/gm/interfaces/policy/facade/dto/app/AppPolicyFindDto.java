package cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.app;

import cloud.xcan.angus.api.commonlink.policy.PolicyGrantScope;
import cloud.xcan.angus.api.commonlink.policy.PolicyGrantStage;
import cloud.xcan.angus.api.commonlink.policy.PolicyType;
import cloud.xcan.angus.remote.PageQuery;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class AppPolicyFindDto extends PageQuery {

  private Long id;

  private String name;

  private String code;

  private PolicyType type;

  private Boolean default0;

  private PolicyGrantScope grantScope;

  private PolicyGrantStage grantStage;

  private String description;

  private Long appId;

  private LocalDateTime createdDate;

}
