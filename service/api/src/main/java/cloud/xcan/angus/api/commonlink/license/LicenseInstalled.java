package cloud.xcan.angus.api.commonlink.license;

import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.api.enums.GoodsType;
import cloud.xcan.angus.spec.experimental.EntitySupport;
import jakarta.persistence.Column;
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

  /**** Non lcs information **/
  @Id
  private Long id;

  private Boolean main;

  @Enumerated(EnumType.STRING)
  private EditionType installEditionType;

  /**** lcs information **/
  private String licenseNo;

  private String mainLicenseNo;

  private String provider;

  private String issuer;

  private Long holderId;

  private String holder;

  @Enumerated(EnumType.STRING)
  private EditionType goodsEditionType;

  private String goodsId;

  @Enumerated(EnumType.STRING)
  private GoodsType goodsType;

  private String goodsCode;

  private String goodsName;

  private String goodsVersion;

  private String orderNo;

  private String subject;

  private String info;

  /**
   * License signature
   */
  private String signature;

  private Date issuedDate;

  private Date expiredDate;

  /**
   * License file base64 encoding text.
   */
  private String content;

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
