package cloud.xcan.angus.core.gm.interfaces.app.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.app.facade.internal.assembler.AppAssembler.addDtoToDomain;
import static cloud.xcan.angus.core.gm.interfaces.app.facade.internal.assembler.AppAssembler.getSpecification;
import static cloud.xcan.angus.core.gm.interfaces.app.facade.internal.assembler.AppAssembler.replaceDtoToDomain;
import static cloud.xcan.angus.core.gm.interfaces.app.facade.internal.assembler.AppAssembler.siteUpdateDtoToDomain;
import static cloud.xcan.angus.core.gm.interfaces.app.facade.internal.assembler.AppAssembler.toDetailVo;
import static cloud.xcan.angus.core.gm.interfaces.app.facade.internal.assembler.AppAssembler.updateDtoToDomain;
import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.api.enums.ExportFileType;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.app.AppCmd;
import cloud.xcan.angus.core.gm.application.query.app.AppQuery;
import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.gm.interfaces.app.facade.AppFacade;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.AppAddDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.AppExportDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.AppFindDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.AppReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.AppSiteInfoUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.AppUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.internal.assembler.AppAssembler;
import cloud.xcan.angus.api.gm.app.vo.AppDetailVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.AppVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.remote.dto.EnabledOrDisabledDto;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;


@Component
public class AppFacadeImpl implements AppFacade {

  @Resource
  private AppCmd appCmd;

  @Resource
  private AppQuery appQuery;

  @Override
  public IdKey<Long, Object> add(AppAddDto dto) {
    return appCmd.add(addDtoToDomain(dto));
  }

  @Override
  public void update(AppUpdateDto dto) {
    appCmd.update(updateDtoToDomain(dto));
  }

  @Override
  public IdKey<Long, Object> replace(AppReplaceDto dto) {
    return appCmd.replace(replaceDtoToDomain(dto));
  }

  @Override
  public void siteUpdate(AppSiteInfoUpdateDto dto) {
    appCmd.siteUpdate(siteUpdateDtoToDomain(dto));
  }

  @Override
  public void delete(HashSet<Long> ids) {
    appCmd.delete(ids);
  }

  @Override
  public void enabled(List<EnabledOrDisabledDto> dto) {
    appCmd.enabled(dto.stream().map(AppAssembler::enabledDtoToDomain)
        .collect(Collectors.toList()));
  }

  @Override
  public void importApp(ExportFileType exportType, MultipartFile file) {
    //    AppFileCmd handler = appFileHandler.getHandler(exportType);
    //    handler.importApp(exportType, file);
  }

  @Override
  public void export(AppExportDto dto, HttpServletResponse response) {
    //    AppFileCmd handler = appFileHandler.getHandler(dto.getExportType());
    //    handler.export(dto.getId(), dto.getExportType(), response);
  }

  @NameJoin
  @Override
  public AppDetailVo detail(Long id) {
    return toDetailVo(appQuery.detail(id));
  }

  @NameJoin
  @Override
  public AppDetailVo detail(String code, EditionType editionType) {
    return toDetailVo(appQuery.detail(code, editionType));
  }

  @NameJoin
  @Override
  public PageResult<AppVo> list(AppFindDto dto) {
    Page<App> page = appQuery.list(getSpecification(dto), dto.tranPage(),
        dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, AppAssembler::toVo);
  }

}
