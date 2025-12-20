package cloud.xcan.angus.core.gm.interfaces/backup/facade;

import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.*;

import java.util.List;

public interface BackupFacade {
    BackupDetailVo create(BackupCreateDto dto);
    BackupDetailVo update(BackupUpdateDto dto);
    void delete(Long id);
    BackupDetailVo findById(Long id);
    List<BackupListVo> findAll(BackupFindDto dto);
    BackupStatsVo getStats();
}
