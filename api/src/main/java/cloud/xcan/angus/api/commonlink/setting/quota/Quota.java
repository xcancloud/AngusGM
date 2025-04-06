package cloud.xcan.angus.api.commonlink.setting.quota;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.setting.tenant.quota.SettingTenantQuota;
import cloud.xcan.angus.core.jpa.repository.summary.SummaryQueryRegister;
import cloud.xcan.angus.spec.experimental.ValueObjectSupport;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Except for tree and hierarchy related restrictions, other quota restrictions are not written in
 * the code!! This makes it easy to modify after privatization deployment.
 */
@Getter
@Setter
@NoArgsConstructor
public class Quota extends ValueObjectSupport<Quota> {

  /**
   * It needs to be consistent with the name value in SummaryQueryRegister.
   *
   * @see SummaryQueryRegister
   */
  private QuotaResource name;

  private String appCode;

  /**
   * Original service code, privatization will be merged.
   */
  private String serviceCode;

  private Boolean allowChange;

  /**
   * TODO Lcs initialization quota after purchase
   */
  private Boolean lcsCtrl;

  /**
   * Calculate remaining quota flag
   */
  private Boolean calcRemaining;

  private Long quota;

  private Long min;

  private Long max;

  private Long capacity;

  public SettingTenantQuota toTenantQuota(Long tenantId) {
    SettingTenantQuota tenantQuota = new SettingTenantQuota().setId(tenantId)
        .setAppCode(this.appCode).setServiceCode(this.serviceCode)
        .setName(this.name).setAllowChange(this.allowChange)
        .setLcsCtrl(this.lcsCtrl).setCalcRemaining(this.calcRemaining)
        .setMin(this.min).setMax(this.max).setCapacity(this.capacity).setDefaults(this.quota)
        .setQuota(this.quota);
    tenantQuota.setTenantId(tenantId);
    return tenantQuota;
  }

  private Quota(Builder builder) {
    setName(builder.name);
    setAppCode(builder.appCode);
    setServiceCode(builder.serviceCode);
    setAllowChange(builder.allowChange);
    setLcsCtrl(builder.lcsCtrl);
    setCalcRemaining(builder.calcRemaining);
    setQuota(builder.quota);
    setMin(builder.min);
    setMax(builder.max);
    setCapacity(builder.capacity);
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static Builder newBuilder(Quota copy) {
    Builder builder = new Builder();
    builder.name = copy.getName();
    builder.appCode = copy.getAppCode();
    builder.serviceCode = copy.getServiceCode();
    builder.allowChange = copy.getAllowChange();
    builder.lcsCtrl = copy.getLcsCtrl();
    builder.calcRemaining = copy.getCalcRemaining();
    builder.quota = copy.getQuota();
    builder.min = copy.getMin();
    builder.max = copy.getMax();
    builder.capacity = copy.getCapacity();
    return builder;
  }

  /**
   * Rewrite business equal. {@link ValueObjectSupport#equals(Object)}
   */
  @Override
  public boolean sameValueAs(Quota other) {
    return isNotEmpty(name) && nonNull(other) && name.equals(other.getName());
  }

  public static final class Builder {

    private QuotaResource name;
    private String appCode;
    private String serviceCode;
    private Boolean allowChange;
    private Boolean lcsCtrl;
    private Boolean calcRemaining;
    private Long quota;
    private Long min;
    private Long max;
    private Long capacity;

    private Builder() {
    }

    public Builder name(QuotaResource name) {
      this.name = name;
      return this;
    }

    public Builder appCode(String appCode) {
      this.appCode = appCode;
      return this;
    }

    public Builder serviceCode(String serviceCode) {
      this.serviceCode = serviceCode;
      return this;
    }

    public Builder allowChange(Boolean allowChange) {
      this.allowChange = allowChange;
      return this;
    }

    public Builder lcsCtrl(Boolean lcsCtrl) {
      this.lcsCtrl = lcsCtrl;
      return this;
    }

    public Builder calcRemaining(Boolean calcRemaining) {
      this.calcRemaining = calcRemaining;
      return this;
    }

    public Builder quota(Long quota) {
      this.quota = quota;
      return this;
    }

    public Builder min(Long min) {
      this.min = min;
      return this;
    }

    public Builder max(Long max) {
      this.max = max;
      return this;
    }

    public Builder capacity(Long capacity) {
      this.capacity = capacity;
      return this;
    }

    public Quota build() {
      return new Quota(this);
    }
  }

}
