package cloud.xcan.angus.api.gm.dept.vo;

import cloud.xcan.angus.remote.NameJoinField;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class DeptListVo implements Serializable {

  private Long id;

  private String code;

  private String name;

  private Long pid;

  @NameJoinField(id = "pid", repository = "commonDeptRepo")
  private String parentName;

  private Integer level;

  private Boolean hasSubDept;

  private Long tenantId;

  @NameJoinField(id = "tenantId", repository = "commonTenantRepo")
  private String tenantName;

  private Long createdBy;

  @NameJoinField(id = "createdBy", repository = "commonUserBaseRepo")
  private String creator;

  private LocalDateTime createdDate;

  private Long modifiedBy;

  @NameJoinField(id = "modifiedBy", repository = "commonUserBaseRepo")
  private String modifier;

  private LocalDateTime modifiedDate;
}
