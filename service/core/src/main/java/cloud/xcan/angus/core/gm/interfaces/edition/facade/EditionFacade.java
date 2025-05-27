package cloud.xcan.angus.core.gm.interfaces.edition.facade;

import cloud.xcan.angus.core.gm.infra.remote.edition.InstalledEditionVo;
import cloud.xcan.angus.core.gm.infra.remote.edition.LatestEditionVo;
import java.util.List;

public interface EditionFacade {

  InstalledEditionVo installed(String goodsCode);

  List<LatestEditionVo> upgradeable(String goodsCode, Long goodsId);
}
