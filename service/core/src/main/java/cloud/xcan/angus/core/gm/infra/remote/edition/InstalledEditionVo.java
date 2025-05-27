package cloud.xcan.angus.core.gm.infra.remote.edition;

import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.api.enums.GoodsType;
import cloud.xcan.angus.spec.annotations.DoInFuture;
import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class InstalledEditionVo implements Serializable {

  private Long goodsId;

  private GoodsType goodsType;

  private String goodsCode;

  @DoInFuture("I18n by goods.getType()")
  private String goodsName;

  private String goodsVersion;

  private EditionType editionType;

  private String provider;

  private String issuer;

  private Long holderId;

  private String holder;

  private String licenseNo;

  private String info;

  /**
   * License signature.
   */
  private String signature;

  private Date issuedDate;

  private Date expiredDate;

}
