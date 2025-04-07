package cloud.xcan.angus.api.commonlink.user.dept;


import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DATE_FMT;

import cloud.xcan.angus.api.commonlink.dept.Dept;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.core.jpa.multitenancy.TenantAware;
import cloud.xcan.angus.core.jpa.multitenancy.TenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "dept_user")
@EntityListeners({AuditingEntityListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class DeptUser extends TenantEntity<DeptUser, Long> implements TenantAware {

  @Id
  private Long id;

  @Column(name = "dept_id")
  private Long deptId;

  @Column(name = "user_id")
  private Long userId;

  @Column(name = "dept_head")
  private Boolean deptHead;

  @Column(name = "main_dept")
  private Boolean mainDept;

  @CreatedDate
  @DateTimeFormat(pattern = DATE_FMT)
  @Column(name = "created_date", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
  protected LocalDateTime createdDate;

  @CreatedBy
  @Column(name = "created_by", nullable = false, updatable = false)
  protected Long createdBy;

  @Transient
  private User user;
  @Transient
  private Dept dept;

  @Transient
  private String fullName;
  @Transient
  private String avatar;
  @Transient
  private String mobile;
  @Transient
  private String deptName;
  @Transient
  private String deptCode;
  @Transient
  private Boolean hasSubDept;

  @Override
  public Long identity() {
    return this.id;
  }
}
