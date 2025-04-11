package cloud.xcan.angus.core.gm.interfaces.country.facade;

import cloud.xcan.angus.core.gm.interfaces.country.facade.dto.CountryFindDto;
import cloud.xcan.angus.core.gm.interfaces.country.facade.dto.CountrySearchDto;
import cloud.xcan.angus.core.gm.interfaces.country.facade.vo.CountryDetailVo;
import cloud.xcan.angus.remote.PageResult;

public interface CountryFacade {

  CountryDetailVo detail(Long id);

  PageResult<CountryDetailVo> list(CountryFindDto findDto);

  PageResult<CountryDetailVo> search(CountrySearchDto dto);
}
