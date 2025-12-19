package cloud.xcan.angus.api.gm.group.vo;

import cloud.xcan.angus.api.commonlink.group.GroupSource;
import cloud.xcan.angus.remote.NameJoinField;
import cloud.xcan.angus.remote.info.IdAndName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class GroupDetailVo implements Serializable {

  private Long id;

  private String name;

  private String code;

  private GroupSource source;

  private Long userNum;

  private Boolean enabled;

  private String remark;

  private Long directoryId;

  private String directoryGidNumber;

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

  private List<IdAndName> tags;

}
