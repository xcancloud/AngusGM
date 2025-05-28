package cloud.xcan.angus.api.commonlink.license;

import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.api.enums.GoodsType;
import cloud.xcan.angus.spec.experimental.EntitySupport;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "license_installed")
@Setter
@Getter
@Accessors(chain = true)
public class LicenseInstalled extends EntitySupport<LicenseInstalled, Long> {

  @Id
  private Long id;

  private Boolean main;

  private String licenseNo;

  private String mainLicenseNo;

  private Date issuedDate;

  private Date expiredDate;

  private String info;

  private String content;

  private String provider;

  private String issuer;

  private Long holderId;

  private String holder;

  @Enumerated(EnumType.STRING)
  private EditionType installEditionType;

  @Enumerated(EnumType.STRING)
  private EditionType goodsEditionType;

  private String goodsId;

  @Enumerated(EnumType.STRING)
  private GoodsType goodsType;

  private String goodsCode;

  private String goodsName;

  private String goodsVersion;

  private String signature;

  private String orderNo;

  private String subject;

  @Transient
  public static boolean isModified = false;

  @Override
  public Long identity() {
    return this.id;
  }

  @Override
  public boolean sameIdentityAs(LicenseInstalled other) {
    return this.licenseNo.equals(other.licenseNo);
  }

}
