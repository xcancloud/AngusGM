package cloud.xcan.angus.core.gm.interfaces.app.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.app.facade.internal.assembler.AppOpenAssembler.getSpecification;
import static cloud.xcan.angus.core.gm.interfaces.app.facade.internal.assembler.AppOpenAssembler.openDtoToDomain;
import static cloud.xcan.angus.core.gm.interfaces.app.facade.internal.assembler.AppOpenAssembler.renewDtoToDomain;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.api.commonlink.app.open.AppOpen;
import cloud.xcan.angus.api.gm.app.dto.AppOpenCancelDto;
import cloud.xcan.angus.api.gm.app.dto.AppOpenDto;
import cloud.xcan.angus.api.gm.app.dto.AppOpenRenewDto;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.app.AppOpenCmd;
import cloud.xcan.angus.core.gm.application.query.app.AppOpenQuery;
import cloud.xcan.angus.core.gm.interfaces.app.facade.AppOpenFacade;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.open.AppOpenFindDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.internal.assembler.AppOpenAssembler;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.open.AppOpenVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class AppOpenFacadeImpl implements AppOpenFacade {

  @Resource
  private AppOpenCmd appOpenCmd;

  @Resource
  private AppOpenQuery appOpenQuery;

  @Override
  public IdKey<Long, Object> open(AppOpenDto dto) {
    return appOpenCmd.open(openDtoToDomain(dto), true);
  }

  @Override
  public void renew(AppOpenRenewDto dto) {
    appOpenCmd.renew(renewDtoToDomain(dto));
  }

  @Override
  public void cancel(AppOpenCancelDto dto) {
    appOpenCmd.cancel(dto.getAppId());
  }

  @NameJoin
  @Override
  public PageResult<AppOpenVo> list(AppOpenFindDto dto) {
    Page<AppOpen> openPage = appOpenQuery.list(getSpecification(dto), dto.tranPage());
    return buildVoPageResult(openPage, AppOpenAssembler::toVo);
  }
}
