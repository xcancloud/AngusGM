package cloud.xcan.angus.core.gm.interfaces.application.facade.internal.assembler;

import static cloud.xcan.angus.spec.BizConstant.nullSafe;

import cloud.xcan.angus.core.gm.domain.application.Application;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationCreateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationFindDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.ApplicationDetailVo;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.ApplicationListVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.remote.search.SearchOperation;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>
 * Application Assembler
 * </p>
 */
public class ApplicationAssembler {

    /**
     * <p>
     * Convert CreateDto to Domain
     * </p>
     */
    public static Application toEntity(ApplicationCreateDto dto) {
        Application application = new Application();
        application.setCode(dto.getCode());
        application.setName(dto.getName());
        application.setDisplayName(dto.getDisplayName());
        application.setType(dto.getType());
        application.setVersion(dto.getVersion());
        application.setVersionType(dto.getVersionType());
        application.setUrl(dto.getUrl());
        application.setTags(dto.getTags());
        application.setSortOrder(dto.getSortOrder());
        application.setStatus(dto.getStatus());
        application.setDescription(dto.getDescription());
        return application;
    }

    /**
     * <p>
     * Convert UpdateDto to Domain
     * </p>
     */
    public static Application toEntity(ApplicationUpdateDto dto, Application existing) {
        if (dto.getName() != null) {
            existing.setName(dto.getName());
        }
        if (dto.getDisplayName() != null) {
            existing.setDisplayName(dto.getDisplayName());
        }
        if (dto.getType() != null) {
            existing.setType(dto.getType());
        }
        if (dto.getVersion() != null) {
            existing.setVersion(dto.getVersion());
        }
        if (dto.getVersionType() != null) {
            existing.setVersionType(dto.getVersionType());
        }
        if (dto.getUrl() != null) {
            existing.setUrl(dto.getUrl());
        }
        if (dto.getTags() != null) {
            existing.setTags(dto.getTags());
        }
        if (dto.getSortOrder() != null) {
            existing.setSortOrder(dto.getSortOrder());
        }
        if (dto.getStatus() != null) {
            existing.setStatus(dto.getStatus());
        }
        if (dto.getDescription() != null) {
            existing.setDescription(dto.getDescription());
        }
        return existing;
    }

    /**
     * <p>
     * Convert Domain to DetailVo
     * </p>
     */
    public static ApplicationDetailVo toDetailVo(Application application) {
        ApplicationDetailVo vo = new ApplicationDetailVo();
        vo.setId(application.getId());
        vo.setCode(application.getCode());
        vo.setName(application.getName());
        vo.setDisplayName(application.getDisplayName());
        vo.setType(application.getType());
        vo.setVersion(application.getVersion());
        vo.setVersionType(application.getVersionType());
        vo.setIsDefault(nullSafe(application.getIsDefault(), false));
        vo.setShopStatus(application.getShopStatus());
        vo.setSortOrder(application.getSortOrder());
        vo.setStatus(application.getStatus());
        vo.setTags(application.getTags());
        vo.setUrl(application.getUrl());
        vo.setGroupId(application.getGroupId());
        vo.setTenantName(application.getTenantName());
        vo.setDescription(application.getDescription());
        vo.setIsInstalled(nullSafe(application.getIsInstalled(), false));
        vo.setMenuCount(application.getMenuCount());
        vo.setRoleCount(application.getRoleCount());
        vo.setUserCount(application.getUserCount());

        // Set auditing fields (inherited from TenantAuditingVo)
        // Note: TenantAuditingVo fields are set automatically via inheritance
        // If needed, can be set explicitly:
        // vo.setTenantId(application.getTenantId());
        // vo.setCreatedBy(application.getCreatedBy());
        // vo.setCreatedDate(application.getCreatedDate());
        // vo.setModifiedBy(application.getModifiedBy());
        // vo.setModifiedDate(application.getModifiedDate());

        return vo;
    }

    /**
     * <p>
     * Convert Domain to ListVo
     * </p>
     */
    public static ApplicationListVo toListVo(Application application) {
        ApplicationListVo vo = new ApplicationListVo();
        vo.setId(application.getId());
        vo.setCode(application.getCode());
        vo.setName(application.getName());
        vo.setDisplayName(application.getDisplayName());
        vo.setType(application.getType());
        vo.setVersion(application.getVersion());
        vo.setVersionType(application.getVersionType());
        vo.setIsDefault(nullSafe(application.getIsDefault(), false));
        vo.setShopStatus(application.getShopStatus());
        vo.setSortOrder(application.getSortOrder());
        vo.setStatus(application.getStatus());
        vo.setTags(application.getTags());
        vo.setUrl(application.getUrl());
        vo.setGroupId(application.getGroupId());
        vo.setTenantName(application.getTenantName());
        vo.setDescription(application.getDescription());
        vo.setIsInstalled(nullSafe(application.getIsInstalled(), false));

        // Set auditing fields (inherited from TenantAuditingVo)
        // Note: TenantAuditingVo fields are set automatically via inheritance
        // If needed, can be set explicitly:
        // vo.setTenantId(application.getTenantId());
        // vo.setCreatedBy(application.getCreatedBy());
        // vo.setCreatedDate(application.getCreatedDate());
        // vo.setModifiedBy(application.getModifiedBy());
        // vo.setModifiedDate(application.getModifiedDate());

        return vo;
    }

    /**
     * <p>
     * Build query specification from FindDto
     * </p>
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static GenericSpecification<Application> getSpecification(ApplicationFindDto dto) {
        Set<SearchCriteria> filters = new SearchCriteriaBuilder(dto)
            .rangeSearchFields("id", "createdDate", "modifiedDate")
            .orderByFields("id", "createdDate", "modifiedDate", "name", "code", "sortOrder")
            .matchSearchFields("name", "code", "displayName")
            .build();

        // Add status filter
        if (dto.getStatus() != null) {
            filters.add(new SearchCriteria("status", dto.getStatus().name(), SearchOperation.EQUAL));
        }

        // Add type filter
        if (dto.getType() != null) {
            filters.add(new SearchCriteria("type", dto.getType().name(), SearchOperation.EQUAL));
        }

        // Add source filter
        if (dto.getSource() != null) {
            filters.add(new SearchCriteria("source", dto.getSource(), SearchOperation.EQUAL));
        }

        // Add tags filter
        if (dto.getTags() != null && !dto.getTags().isEmpty()) {
            for (String tag : dto.getTags()) {
                filters.add(new SearchCriteria("tags", tag, SearchOperation.MATCH));
            }
        }

        return new GenericSpecification<>(filters);
    }
}
