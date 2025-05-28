package cloud.xcan.angus.core.gm.application.converter;

import static cloud.xcan.angus.core.spring.SpringContextHolder.isCloudService;
import static cloud.xcan.angus.spec.experimental.BizConstant.OWNER_TENANT_ID;
import static cloud.xcan.angus.spec.experimental.BizConstant.PRODUCT_ISSUER;
import static cloud.xcan.angus.spec.experimental.BizConstant.PRODUCT_PROVIDE;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getTenantId;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.license.LicenseInstalled;
import cloud.xcan.angus.api.enums.GoodsType;
import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.gm.infra.remote.edition.InstalledEditionVo;


public class EditionConverter {

  public static InstalledEditionVo toInstalledVo(LicenseInstalled licenseInstalled) {
    return new InstalledEditionVo()
        .setGoodsId(nonNull(licenseInstalled.getGoodsId())
            ? Long.valueOf(licenseInstalled.getGoodsId()) : null)
        .setGoodsType(licenseInstalled.getGoodsType())
        .setGoodsCode(licenseInstalled.getGoodsCode())
        .setGoodsName(licenseInstalled.getGoodsName())
        .setGoodsVersion(licenseInstalled.getGoodsVersion())
        .setEditionType(licenseInstalled.getGoodsEditionType())
        .setProvider(licenseInstalled.getProvider())
        .setIssuer(licenseInstalled.getIssuer())
        .setHolderId(licenseInstalled.getHolderId())
        .setHolder(licenseInstalled.getHolder())
        .setLicenseNo(licenseInstalled.getLicenseNo())
        .setInfo(licenseInstalled.getInfo())
        .setSignature(licenseInstalled.getSignature())
        .setIssuedDate(licenseInstalled.getIssuedDate())
        .setExpiredDate(licenseInstalled.getExpiredDate());
  }

  public static InstalledEditionVo toInstalledVo(App app) {
    return new InstalledEditionVo()
        .setGoodsType(GoodsType.APPLICATION)
        .setGoodsCode(app.getCode())
        .setGoodsName(app.getName())
        .setGoodsVersion(app.getVersion())
        .setEditionType(app.getEditionType())
        .setProvider(PRODUCT_PROVIDE)
        .setIssuer(PRODUCT_ISSUER)
        .setHolderId(isCloudService() ? OWNER_TENANT_ID : getTenantId())
        .setHolder(isCloudService() ? PRODUCT_ISSUER : "<Privatization Tenant>")
        .setLicenseNo("<Uninstalled>");
  }

}
