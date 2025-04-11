package cloud.xcan.angus.core.gm.interfaces.app.facade;

import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.api.enums.ExportFileType;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.AppAddDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.AppExportDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.AppFindDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.AppReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.AppSearchDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.AppSiteInfoUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.AppUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.AppDetailVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.AppVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.remote.dto.EnabledOrDisabledDto;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;


public interface AppFacade {

  IdKey<Long, Object> add(AppAddDto dto);

  void update(AppUpdateDto dto);

  IdKey<Long, Object> replace(AppReplaceDto dto);

  void siteUpdate(AppSiteInfoUpdateDto dto);

  void delete(HashSet<Long> ids);

  void enabled(List<EnabledOrDisabledDto> dtos);

  void importApp(ExportFileType exportType, MultipartFile file);

  void export(AppExportDto dto, HttpServletResponse response);

  AppDetailVo detail(Long id);

  AppDetailVo detail(String code, EditionType editionType);

  PageResult<AppVo> list(AppFindDto dto);

  PageResult<AppVo> search(AppSearchDto dto);

}
