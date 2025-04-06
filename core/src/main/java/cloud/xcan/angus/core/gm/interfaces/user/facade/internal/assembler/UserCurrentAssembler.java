package cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler;

import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.gm.user.dto.UserCurrentUpdateDto;
import cloud.xcan.angus.api.gm.user.vo.UserCurrentDetailVo;


public class UserCurrentAssembler {

  public static User updateDtoToDomain(UserCurrentUpdateDto dto) {
    return new User()
        .setUsername(dto.getUsername())
        .setFirstName(dto.getFirstName())
        .setLastName(dto.getLastName())
        .setFullname(dto.getFullname())
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
        .setFullname(user.getFullname())
        .setFirstName(user.getFirstName())
        .setPassdStrength(user.getPasswordStrength())
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
        .setPassdStrength(user.getPasswordStrength())
        .setPasswordExpired(user.getPasswordExpired())
        .setPasswordExpiredDate(user.getPasswordExpiredDate());
  }

}
