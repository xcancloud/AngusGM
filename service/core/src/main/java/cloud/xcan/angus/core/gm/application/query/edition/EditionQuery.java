package cloud.xcan.angus.core.gm.application.query.edition;

import cloud.xcan.angus.core.gm.infra.remote.edition.InstalledEditionVo;

public interface EditionQuery {

  InstalledEditionVo installed(String goodsCode);
}
