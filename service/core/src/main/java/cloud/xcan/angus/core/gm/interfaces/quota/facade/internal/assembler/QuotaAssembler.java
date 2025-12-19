package cloud.xcan.angus.core.gm.interfaces.quota.facade.internal.assembler;

import cloud.xcan.angus.core.gm.domain.quota.Quota;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.vo.*;
import org.springframework.stereotype.Component;

@Component
public class QuotaAssembler {
    
    public Quota toEntity(QuotaCreateDto dto) {
        Quota quota = new Quota();
        quota.setName(dto.getName());
        quota.setType(dto.getType());
        quota.setTenantId(dto.getTenantId());
        quota.setLimitValue(dto.getLimitValue());
        quota.setWarningThreshold(dto.getWarningThreshold());
        quota.setUnit(dto.getUnit());
        quota.setDescription(dto.getDescription());
        return quota;
    }
    
    public Quota toEntity(QuotaUpdateDto dto) {
        Quota quota = new Quota();
        quota.setId(dto.getId());
        quota.setName(dto.getName());
        quota.setStatus(dto.getStatus());
        quota.setLimitValue(dto.getLimitValue());
        quota.setWarningThreshold(dto.getWarningThreshold());
        quota.setEnabled(dto.getEnabled());
        quota.setDescription(dto.getDescription());
        return quota;
    }
    
    public QuotaDetailVo toDetailVo(Quota quota) {
        QuotaDetailVo vo = new QuotaDetailVo();
        vo.setId(quota.getId());
        vo.setName(quota.getName());
        vo.setType(quota.getType());
        vo.setStatus(quota.getStatus());
        vo.setTenantId(quota.getTenantId());
        vo.setLimitValue(quota.getLimitValue());
        vo.setUsedValue(quota.getUsedValue());
        vo.setWarningThreshold(quota.getWarningThreshold());
        vo.setUnit(quota.getUnit());
        vo.setEnabled(quota.getEnabled());
        vo.setDescription(quota.getDescription());
        vo.setCreatedAt(quota.getCreatedAt());
        return vo;
    }
    
    public QuotaListVo toListVo(Quota quota) {
        QuotaListVo vo = new QuotaListVo();
        vo.setId(quota.getId());
        vo.setName(quota.getName());
        vo.setType(quota.getType());
        vo.setStatus(quota.getStatus());
        vo.setLimitValue(quota.getLimitValue());
        vo.setUsedValue(quota.getUsedValue());
        return vo;
    }
}
