package cloud.xcan.angus.core.gm.interfaces.application.facade.internal.assembler;

import cloud.xcan.angus.core.gm.domain.application.Application;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationCreateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.ApplicationDetailVo;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.ApplicationListVo;

/**
 * Application Assembler
 */
public class ApplicationAssembler {

    public static Application toEntity(ApplicationCreateDto dto) {
        Application application = new Application();
        application.setName(dto.getName());
        application.setCode(dto.getCode());
        application.setType(dto.getType());
        application.setDescription(dto.getDescription());
        application.setHomeUrl(dto.getHomeUrl());
        application.setRedirectUrl(dto.getRedirectUrl());
        application.setOwnerId(dto.getOwnerId());
        return application;
    }

    public static Application toEntity(ApplicationUpdateDto dto, Application existing) {
        if (dto.getName() != null) {
            existing.setName(dto.getName());
        }
        if (dto.getType() != null) {
            existing.setType(dto.getType());
        }
        if (dto.getDescription() != null) {
            existing.setDescription(dto.getDescription());
        }
        if (dto.getHomeUrl() != null) {
            existing.setHomeUrl(dto.getHomeUrl());
        }
        if (dto.getRedirectUrl() != null) {
            existing.setRedirectUrl(dto.getRedirectUrl());
        }
        if (dto.getOwnerId() != null) {
            existing.setOwnerId(dto.getOwnerId());
        }
        return existing;
    }

    public static ApplicationDetailVo toDetailVo(Application application) {
        ApplicationDetailVo vo = new ApplicationDetailVo();
        vo.setId(application.getId());
        vo.setName(application.getName());
        vo.setCode(application.getCode());
        vo.setType(application.getType());
        vo.setStatus(application.getStatus());
        vo.setDescription(application.getDescription());
        vo.setClientId(application.getClientId());
        vo.setClientSecret(application.getClientSecret());
        vo.setHomeUrl(application.getHomeUrl());
        vo.setRedirectUrl(application.getRedirectUrl());
        vo.setOwnerId(application.getOwnerId());
        vo.setOwnerName(application.getOwnerName());
        vo.setServiceCount(application.getServiceCount());
        vo.setCreatedAt(application.getCreatedAt());
        vo.setUpdatedAt(application.getUpdatedAt());
        vo.setCreatedBy(application.getCreatedBy());
        vo.setUpdatedBy(application.getUpdatedBy());
        return vo;
    }

    public static ApplicationListVo toListVo(Application application) {
        ApplicationListVo vo = new ApplicationListVo();
        vo.setId(application.getId());
        vo.setName(application.getName());
        vo.setCode(application.getCode());
        vo.setType(application.getType());
        vo.setStatus(application.getStatus());
        vo.setDescription(application.getDescription());
        vo.setOwnerId(application.getOwnerId());
        vo.setOwnerName(application.getOwnerName());
        vo.setServiceCount(application.getServiceCount());
        vo.setCreatedAt(application.getCreatedAt());
        vo.setUpdatedAt(application.getUpdatedAt());
        return vo;
    }
}
