package cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;

import cloud.xcan.angus.api.commonlink.user.dept.DeptUser;
import cloud.xcan.angus.api.gm.user.to.UserDeptTo;
import cloud.xcan.angus.core.gm.interfaces.dept.facade.dto.user.DeptUserFindDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.dept.UserDeptFindDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.dept.UserDeptVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UserDeptAssembler {

  public static List<DeptUser> replaceToUserDeptDomain(Long userId, LinkedHashSet<UserDeptTo> dto) {
    return isEmpty(dto) ? null : dto.stream().map(x ->
        new DeptUser().setUserId(userId).setDeptId(x.getId())
            .setDeptHead(x.getDeptHead())
            .setMainDept(x.getMainDept())).collect(Collectors.toList());
  }

  public static UserDeptVo toUserDeptVo(DeptUser deptUser) {
    return new UserDeptVo().setId(deptUser.getId())
        .setDeptId(deptUser.getDeptId())
        .setDeptName(deptUser.getDeptName())
        .setDeptCode(deptUser.getDeptCode())
        .setMainDept(deptUser.getMainDept())
        .setDeptHead(deptUser.getDeptHead())
        .setHasSubDept(deptUser.getHasSubDept())
        .setUserId(deptUser.getUserId())
        .setFullname(deptUser.getFullname())
        .setCreatedBy(deptUser.getCreatedBy())
        .setCreatedDate(deptUser.getCreatedDate())
        .setAvatar(deptUser.getAvatar())
        .setMobile(deptUser.getMobile())
        .setTenantId(deptUser.getTenantId());
  }

  public static List<DeptUser> userDeptAddToDomain(Long userId, LinkedHashSet<Long> deptIds) {
    return deptIds.stream().map(did -> new DeptUser()
        .setDeptId(did)
        .setDeptHead(false)
        .setMainDept(false)
        .setUserId(userId)).collect(Collectors.toList());
  }

  public static List<DeptUser> deptUserAddToDomain(Long deptId, LinkedHashSet<Long> userIds) {
    return userIds.stream().map(uid -> new DeptUser()
        .setDeptId(deptId)
        .setDeptHead(false)
        .setMainDept(false)
        .setUserId(uid)).collect(Collectors.toList());
  }

  public static GenericSpecification<DeptUser> getDeptUserSpecification(DeptUserFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("createdDate")
        .orderByFields("createdDate")
        .matchSearchFields("fullname")
        .build();
    return new GenericSpecification<>(filters);
  }

  public static GenericSpecification<DeptUser> getUserDeptSpecification(UserDeptFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("createdDate")
        .orderByFields("createdDate")
        .matchSearchFields("deptName")
        .build();
    return new GenericSpecification<>(filters);
  }
}
