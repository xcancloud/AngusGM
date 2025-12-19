package cloud.xcan.angus.core.gm.interfaces.quota.facade;

import cloud.xcan.angus.core.gm.interfaces.quota.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.vo.*;

import java.util.List;

public interface QuotaFacade {
    QuotaDetailVo create(QuotaCreateDto dto);
    QuotaDetailVo update(QuotaUpdateDto dto);
    void delete(Long id);
    QuotaDetailVo findById(Long id);
    List<QuotaListVo> findAll(QuotaFindDto dto);
    QuotaStatsVo getStats();
}
