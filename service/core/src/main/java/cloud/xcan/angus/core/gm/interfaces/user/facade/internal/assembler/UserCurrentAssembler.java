package cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;

import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.gm.user.dto.UserCurrentUpdateDto;
import cloud.xcan.angus.api.gm.user.to.UserDeptTo;
import cloud.xcan.angus.api.gm.user.to.UserGroupTo;
import cloud.xcan.angus.api.gm.user.vo.UserCurrentDetailVo;
import cloud.xcan.angus.remote.info.IdAndName;
import java.util.stream.Collectors;


public class UserCurrentAssembler {

  public static User updateDtoToDomain(UserCurrentUpdateDto dto) {
    return new User()
        .setUsername(dto.getUsername())
        .setFirstName(dto.getFirstName())
        .setLastName(dto.getLastName())
        .setFullName(dto.getFullName())
        .setLandline(dto.getLandline())
        .setAvatar(dto.getAvatar())
        .setTitle(dto.getTitle())
        .setGender(dto.getGender())
        //.setAddress(UserAssembler.address2ToDomain(dto.getAddress()))
        .setAddress(dto.getAddress());
  }

  public static UserCurrentDetailVo toDetailVo(User user) {
    return new UserCurrentDetailVo()
        .setId(user.getId())
        .setUsername(user.getUsername())
        .setFullName(user.getFullName())
        .setFirstName(user.getFirstName())
        .setPasswordStrength(user.getPasswordStrength())
        .setLastName(user.getLastName())
        .setItc(user.getItc())
        .setCountry(user.getCountry())
        .setMobile(user.getMobile())
        .setEmail(user.getEmail())
        .setLandline(user.getLandline())
        .setAvatar(user.getAvatar())
        .setTitle(user.getTitle())
        .setGender(user.getGender())
        //.setAddress(addressDomain2To(user.getAddress()))
        .setAddress(user.getAddress())
        .setSysAdmin(user.getSysAdmin())
        .setDeptHead(user.getDeptHead())
        .setOnline(user.getOnline())
        .setOnlineDate(user.getOnlineDate())
        .setOfflineDate(user.getOfflineDate())
        .setEnabled(user.getEnabled())
        .setCreatedDate(user.getCreatedDate())
        .setSource(user.getSource())
        .setLocked(user.getLocked())
        .setLockStartDate(user.getLockStartDate())
        .setLockEndDate(user.getLockEndDate())
        .setTenantId(user.getTenantId())
        .setTenantName(user.getTenantName())
        .setTenantRealNameStatus(user.getTenantRealNameStatus())
        .setCreatedBy(user.getCreatedBy())
        .setCreatedDate(user.getCreatedDate())
        .setLastModifiedBy(user.getLastModifiedBy())
        .setLastModifiedDate(user.getLastModifiedDate())
        .setPasswordStrength(user.getPasswordStrength())
        .setPasswordExpired(user.getPasswordExpired())
        .setPasswordExpiredDate(user.getPasswordExpiredDate())
        .setTags(isEmpty(user.getTags()) ? null : user.getTags().stream()
            .map(tag -> new IdAndName().setId(tag.getTag().getId()).setName(tag.getTag().getName()))
            .collect(Collectors.toList()))
        .setDepts(isEmpty(user.getDepts()) ? null : user.getDepts().stream()
            .map(dept -> new UserDeptTo().setId(dept.getDeptId())
                .setName(dept.getDept().getName()).setMainDept(dept.getMainDept())
                .setDeptHead(dept.getDeptHead()))
            .collect(Collectors.toList()))
        .setGroups(isEmpty(user.getGroups()) ? null : user.getGroups().stream()
            .map(group -> new UserGroupTo().setId(group.getGroupId())
                .setName(group.getGroup().getName()))
            .collect(Collectors.toList()));
  }

}
