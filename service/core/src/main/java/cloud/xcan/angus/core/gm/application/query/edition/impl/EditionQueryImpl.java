package cloud.xcan.angus.core.gm.application.query.edition.impl;

import static cloud.xcan.angus.core.gm.application.converter.EditionConverter.toInstalledVo;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getApplicationInfo;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isPrivateEdition;

import cloud.xcan.angus.api.commonlink.license.LicenseInstalled;
import cloud.xcan.angus.api.commonlink.license.LicenseInstalledRepo;
import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.app.AppQuery;
import cloud.xcan.angus.core.gm.application.query.edition.EditionQuery;
import cloud.xcan.angus.core.gm.infra.remote.edition.InstalledEditionVo;
import jakarta.annotation.Resource;
import java.util.List;

@Biz
public class EditionQueryImpl implements EditionQuery {

  @Resource
  private AppQuery appQuery;

  @Resource
  private LicenseInstalledRepo licenseInstalledRepo;

  @Override
  public InstalledEditionVo installed(String goodsCode) {
    return new BizTemplate<InstalledEditionVo>() {
      @Override
      protected InstalledEditionVo process() {
        if (isPrivateEdition()){
          // Privatized edition may not have installed licenses.
          List<LicenseInstalled> licenses = licenseInstalledRepo.findByGoodsCode(goodsCode);
          if (!licenses.isEmpty()) {
            return toInstalledVo(licenses.get(licenses.size() - 1)); // Get last
          }
        }
        return toInstalledVo(appQuery.findLatestByCode(goodsCode,
            EditionType.valueOf(getApplicationInfo().getEditionType())));
      }
    }.execute();
  }
}
