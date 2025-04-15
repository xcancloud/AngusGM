package cloud.xcan.angus.api.commonlink.app.func;

import static cloud.xcan.angus.spec.experimental.BizConstant.DEFAULT_ROOT_PID;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.api.Api;
import cloud.xcan.angus.api.commonlink.app.tag.WebTag;
import cloud.xcan.angus.api.commonlink.app.tag.WebTagTarget;
import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import cloud.xcan.angus.remote.NameJoinField;
import cloud.xcan.angus.spec.annotations.DoInFuture;
import cloud.xcan.angus.spec.experimental.Resources;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.LinkedHashSet;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;


@Entity
@Table(name = "app_func")
@Setter
@Getter
@Accessors(chain = true)
public class AppFunc extends TenantAuditingEntity<AppFunc, Long> implements
    Resources<Long> {

  @Id
  private Long id;

  private String code;

  private String name;

  @Column(name = "show_name")
  private String showName;

  private Long pid;

  private String icon;

  @Enumerated(EnumType.STRING)
  private AppFuncType type;

  private String description;

  @Column(name = "auth_ctrl")
  private Boolean authCtrl;

  private Boolean enabled;

  private String url;

  @Column(name = "app_id")
  private Long appId;

  /**
   * Sorting value, the smaller the value, the higher
   */
  private Integer sequence;

  /**
   * May include disabled api
   */
  @DoInFuture("Use authority table instead")
  @Type(JsonType.class)
  @Column(name = "api_ids", columnDefinition = "json")
  private LinkedHashSet<Long> apiIds;

  @Column(name = "client_id")
  private String clientId;

  @Transient
  private Boolean auth;
  @Transient
  @NameJoinField(id = "createdBy", repository = "commonUserBaseRepo")
  private String createdByName;
  @Transient
  private List<Api> apis;
  @Transient
  private List<WebTag> tags;
  @Transient
  private LinkedHashSet<Long> tagIds;
  @Transient
  private List<WebTagTarget> tagTargets;
  @Transient
  private Boolean hit = false;
  @Transient
  private Boolean hasAuth;

  public boolean hasTags(String name) {
    if (isEmpty(this.tags)) {
      return false;
    }
    for (WebTag tag : this.tags) {
      // Fix null tag when there is incorrect relationship data
      if (isNull(tag)) {
        return false;
      }
      if (tag.equalName(name)) {
        return true;
      }
    }
    return false;
  }

  public boolean isMenu() {
    return nonNull(type) && type.equals(AppFuncType.MENU);
  }

  public boolean isButton() {
    return nonNull(type) && type.equals(AppFuncType.BUTTON);
  }

  public boolean isPanel() {
    return nonNull(type) && type.equals(AppFuncType.PANEL);
  }

  public boolean hasParent() {
    return nonNull(pid) && !pid.equals(DEFAULT_ROOT_PID);
  }

  @Override
  public Long identity() {
    return this.id;
  }
}
