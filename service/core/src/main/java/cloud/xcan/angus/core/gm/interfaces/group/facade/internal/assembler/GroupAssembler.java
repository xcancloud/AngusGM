package cloud.xcan.angus.core.gm.interfaces.group.facade.internal.assembler;

import static cloud.xcan.angus.spec.BizConstant.nullSafe;

import cloud.xcan.angus.core.gm.domain.group.Group;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupCreateDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupFindDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupDetailVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupListVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupUserVo;
import cloud.xcan.angus.core.gm.domain.user.User;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupMemberFindDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupMemberVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.remote.search.SearchOperation;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Group assembler for DTO/Domain/VO conversion
 */
public class GroupAssembler {

  /**
   * Convert CreateDto to Domain
   */
  public static Group toCreateDomain(GroupCreateDto dto) {
    Group group = new Group();
    group.setName(dto.getName());
    group.setCode(dto.getCode());
    group.setDescription(dto.getDescription());
    group.setType(dto.getType());
    group.setOwnerId(dto.getOwnerId());
    group.setMemberIds(dto.getMemberIds());
    group.setTags(dto.getTags());
    group.setStatus(dto.getStatus());
    return group;
  }

  /**
   * Convert UpdateDto to Domain
   */
  public static Group toUpdateDomain(Long id, GroupUpdateDto dto) {
    Group group = new Group();
    group.setId(id);
    group.setName(dto.getName());
    group.setDescription(dto.getDescription());
    group.setType(dto.getType());
    group.setOwnerId(dto.getOwnerId());
    group.setMemberIds(dto.getMemberIds());
    group.setTags(dto.getTags());
    group.setStatus(dto.getStatus());
    return group;
  }

  /**
   * Convert Domain to DetailVo
   */
  public static GroupDetailVo toDetailVo(Group group) {
    GroupDetailVo vo = new GroupDetailVo();
    vo.setId(group.getId());
    vo.setName(group.getName());
    vo.setCode(group.getCode());
    vo.setDescription(group.getDescription());
    vo.setType(group.getType());
    vo.setStatus(group.getStatus());
    vo.setOwnerId(group.getOwnerId());
    vo.setOwnerName(group.getOwnerName());
    vo.setOwnerAvatar(group.getOwnerAvatar());
    vo.setMemberCount(nullSafe(group.getMemberCount(), 0L));
    vo.setMemberIds(group.getMemberIds());
    vo.setTags(group.getTags());
    vo.setLastActive(group.getLastActive() != null 
        ? group.getLastActive().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        : null);
    // TODO: Set members list from group-user relation

    // Set auditing fields
    vo.setTenantId(group.getTenantId());
    vo.setCreatedBy(group.getCreatedBy());
    vo.setCreator(group.getCreatedByName());
    vo.setCreatedDate(group.getCreatedDate());
    vo.setModifiedBy(group.getModifiedBy());
    vo.setModifier(group.getModifiedByName());
    vo.setModifiedDate(group.getModifiedDate());

    return vo;
  }

  /**
   * Convert Domain to ListVo
   */
  public static GroupListVo toListVo(Group group) {
    GroupListVo vo = new GroupListVo();
    vo.setId(group.getId());
    vo.setName(group.getName());
    vo.setCode(group.getCode());
    vo.setDescription(group.getDescription());
    vo.setType(group.getType());
    vo.setStatus(group.getStatus());
    vo.setOwnerId(group.getOwnerId());
    vo.setOwnerName(group.getOwnerName());
    vo.setOwnerAvatar(group.getOwnerAvatar());
    vo.setMemberCount(nullSafe(group.getMemberCount(), 0L));
    vo.setMemberIds(group.getMemberIds());
    vo.setTags(group.getTags());
    vo.setLastActive(group.getLastActive() != null 
        ? group.getLastActive().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        : null);

    // Set auditing fields
    vo.setTenantId(group.getTenantId());
    vo.setCreatedBy(group.getCreatedBy());
    vo.setCreator(group.getCreatedByName());
    vo.setCreatedDate(group.getCreatedDate());
    vo.setModifiedBy(group.getModifiedBy());
    vo.setModifier(group.getModifiedByName());
    vo.setModifiedDate(group.getModifiedDate());

    return vo;
  }

  /**
   * Convert Domain list to UserVo list
   */
  public static List<GroupUserVo> toUserVoList(List<Group> groups) {
    if (groups == null || groups.isEmpty()) {
      return new ArrayList<>();
    }
    return groups.stream()
        .map(GroupAssembler::toUserVo)
        .collect(Collectors.toList());
  }

  /**
   * Convert Domain to UserVo
   */
  public static GroupUserVo toUserVo(Group group) {
    GroupUserVo vo = new GroupUserVo();
    vo.setId(group.getId());
    vo.setName(group.getName());
    vo.setType(group.getType() != null ? group.getType().name() : null);
    vo.setOwnerName(group.getOwnerName());
    vo.setMemberCount(nullSafe(group.getMemberCount(), 0L));
    vo.setStatus(group.getStatus() != null ? group.getStatus().name() : null);
    // TODO: Set role from group-user relation

    // Set auditing fields
    vo.setTenantId(group.getTenantId());
    vo.setCreatedBy(group.getCreatedBy());
    vo.setCreator(group.getCreatedByName());
    vo.setCreatedDate(group.getCreatedDate());
    vo.setModifiedBy(group.getModifiedBy());
    vo.setModifier(group.getModifiedByName());
    vo.setModifiedDate(group.getModifiedDate());

    return vo;
  }

  /**
   * Build query specification from FindDto
   */
  public static GenericSpecification<Group> getSpecification(GroupFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate", "modifiedDate")
        .orderByFields("id", "createdDate", "modifiedDate", "name")
        .matchSearchFields("name", "code", "description")
        .build();
    
    // Add status filter
    if (dto.getStatus() != null) {
      filters.add(new SearchCriteria("status", dto.getStatus().name(), SearchOperation.EQUAL));
    }
    
    // Add type filter
    if (dto.getType() != null) {
      filters.add(new SearchCriteria("type", dto.getType().name(), SearchOperation.EQUAL));
    }
    
    // Add ownerId filter
    if (dto.getOwnerId() != null) {
      filters.add(new SearchCriteria("ownerId", dto.getOwnerId(), SearchOperation.EQUAL));
    }
    
    return new GenericSpecification<>(filters);
  }

  /**
   * Build query specification for group members
   */
  public static GenericSpecification<User> getMemberSpecification(Long groupId, GroupMemberFindDto dto, List<Long> userIds) {
    Set<SearchCriteria> filters = new HashSet<>();
    
    // Filter by userIds (from group-user relation)
    if (userIds != null && !userIds.isEmpty()) {
      filters.add(new SearchCriteria("id", userIds, SearchOperation.IN));
    }
    
    // Add keyword search if provided
    if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
      filters.add(new SearchCriteria("name", dto.getKeyword(), SearchOperation.MATCH));
    }
    
    return new GenericSpecification<>(filters);
  }

  /**
   * Convert User to GroupMemberVo
   */
  public static GroupMemberVo toMemberVo(User user, Long groupId) {
    GroupMemberVo vo = new GroupMemberVo();
    vo.setId(user.getId());
    vo.setName(user.getName());
    vo.setEmail(user.getEmail());
    vo.setAvatar(user.getAvatar());
    vo.setDepartment(user.getDepartment());
    vo.setDepartmentId(user.getDepartmentId());
    vo.setRole(user.getRole());
    // TODO: Set joinDate from group-user relation createdDate
    // TODO: Set isOwner by comparing userId with group.ownerId
    
    // Set auditing fields
    vo.setTenantId(user.getTenantId());
    vo.setCreatedBy(user.getCreatedBy());
    vo.setCreator(user.getCreatedByName());
    vo.setCreatedDate(user.getCreatedDate());
    vo.setModifiedBy(user.getModifiedBy());
    vo.setModifier(user.getModifiedByName());
    vo.setModifiedDate(user.getModifiedDate());
    
    return vo;
  }
}
