package cloud.xcan.angus.core.gm.interfaces.edition.facade.internal;

import static cloud.xcan.angus.core.spring.SpringContextHolder.isCloudService;
import static java.util.Collections.emptyList;

import cloud.xcan.angus.core.gm.application.query.edition.EditionQuery;
import cloud.xcan.angus.core.gm.infra.remote.edition.EditionOpen2pRemote;
import cloud.xcan.angus.core.gm.infra.remote.edition.InstalledEditionVo;
import cloud.xcan.angus.core.gm.infra.remote.edition.LatestEditionVo;
import cloud.xcan.angus.core.gm.interfaces.edition.facade.EditionFacade;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class EditionFacadeImpl implements EditionFacade {

  @Resource
  private EditionQuery editionQuery;

  @Resource
  private EditionOpen2pRemote editionOpen2pRemote;

  @Override
  public InstalledEditionVo installed(String goodsCode) {
    return editionQuery.installed(goodsCode);
  }

  @Override
  public List<LatestEditionVo> upgradeable(String goodsCode, Long goodsId) {
    return isCloudService() ? emptyList()
        : editionOpen2pRemote.latest(goodsCode, goodsId).orElseContentThrow();
  }
}
