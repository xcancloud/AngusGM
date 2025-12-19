package cloud.xcan.angus.core.gm.application.query.application.impl;

import cloud.xcan.angus.core.gm.application.query.application.ApplicationQuery;
import cloud.xcan.angus.core.gm.domain.application.Application;
import cloud.xcan.angus.core.gm.domain.application.ApplicationRepo;
import cloud.xcan.angus.core.gm.domain.application.enums.ApplicationStatus;
import cloud.xcan.angus.core.gm.domain.application.enums.ApplicationType;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationFindDto;
import cloud.xcan.grapefruit.infra.exception.ResourceNotFound;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Application Query Service Implementation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationQueryImpl implements ApplicationQuery {

    private final ApplicationRepo applicationRepo;

    @Override
    public Application findById(String id) {
        return applicationRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Application not found"));
    }

    @Override
    public Page<Application> find(ApplicationFindDto dto) {
        PageRequest pageRequest = PageRequest.of(
                dto.getPage() != null ? dto.getPage() : 0,
                dto.getSize() != null ? dto.getSize() : 20
        );

        if (dto.getStatus() != null && dto.getType() != null) {
            return applicationRepo.findByStatusAndType(dto.getStatus(), dto.getType(), pageRequest);
        } else if (dto.getStatus() != null) {
            return applicationRepo.findByStatus(dto.getStatus(), pageRequest);
        } else if (dto.getType() != null) {
            return applicationRepo.findByType(dto.getType(), pageRequest);
        } else if (dto.getOwnerId() != null) {
            return applicationRepo.findByOwnerId(dto.getOwnerId(), pageRequest);
        }

        return applicationRepo.findAll(pageRequest);
    }

    @Override
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalApplications", applicationRepo.count());
        stats.put("enabledApplications", applicationRepo.countByStatus(ApplicationStatus.ENABLED));
        stats.put("disabledApplications", applicationRepo.countByStatus(ApplicationStatus.DISABLED));
        stats.put("webApplications", applicationRepo.countByType(ApplicationType.WEB));
        stats.put("mobileApplications", applicationRepo.countByType(ApplicationType.MOBILE));
        stats.put("desktopApplications", applicationRepo.countByType(ApplicationType.DESKTOP));
        stats.put("apiApplications", applicationRepo.countByType(ApplicationType.API));
        
        return stats;
    }
}
