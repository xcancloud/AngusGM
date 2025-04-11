package cloud.xcan.angus.core.gm.interfaces.app.facade;

import cloud.xcan.angus.api.gm.app.dto.AppOpenCancelDto;
import cloud.xcan.angus.api.gm.app.dto.AppOpenDto;
import cloud.xcan.angus.api.gm.app.dto.AppOpenRenewDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.open.AppOpenFindDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.open.AppOpenVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;

public interface AppOpenFacade {

  IdKey<Long, Object> open(AppOpenDto dto);

  void renew(AppOpenRenewDto dto);

  void cancel(AppOpenCancelDto dto);

  PageResult<AppOpenVo> list(AppOpenFindDto dto);
}
