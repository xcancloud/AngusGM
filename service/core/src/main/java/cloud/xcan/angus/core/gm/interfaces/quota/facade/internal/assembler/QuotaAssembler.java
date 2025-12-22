package cloud.xcan.angus.core.gm.interfaces.quota.facade.internal.assembler;

import cloud.xcan.angus.core.gm.domain.quota.Quota;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.vo.*;

/**
 * <p>Quota assembler for DTO/Domain/VO conversion</p>
 */
public class QuotaAssembler {
    
    /**
     * <p>Convert CreateDto to Domain entity</p>
     */
    public static Quota toEntity(QuotaCreateDto dto) {
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
    
    /**
     * <p>Convert UpdateDto to Domain entity</p>
     */
    public static Quota toEntity(QuotaUpdateDto dto) {
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
    
    /**
     * <p>Convert Domain to DetailVo</p>
     */
    public static QuotaDetailVo toDetailVo(Quota quota) {
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
    
    /**
     * <p>Convert Domain to ListVo</p>
     */
    public static QuotaListVo toListVo(Quota quota) {
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
