package cloud.xcan.angus.core.gm.interfaces.auditlog.facade;

import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.dto.AuditLogCreateDto;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.dto.AuditLogFindDto;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.dto.AuditLogUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo.AuditLogDetailVo;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo.AuditLogListVo;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo.AuditLogStatsVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditLogFacade {
    AuditLogDetailVo create(AuditLogCreateDto dto);
    AuditLogDetailVo update(Long id, AuditLogUpdateDto dto);
    void delete(Long id);
    void enable(Long id);
    void disable(Long id);
    AuditLogDetailVo findById(Long id);
    Page<AuditLogListVo> find(AuditLogFindDto dto, Pageable pageable);
    AuditLogStatsVo getStats();
}
