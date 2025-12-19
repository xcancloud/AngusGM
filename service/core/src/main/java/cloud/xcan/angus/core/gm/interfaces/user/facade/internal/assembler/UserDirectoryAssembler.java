package cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler;

import cloud.xcan.angus.core.gm.domain.user.directory.UserDirectory;
import cloud.xcan.angus.core.gm.domain.user.directory.model.DirectorySyncResult;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.directory.UserDirectoryAddDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.directory.UserDirectoryReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.directory.UserDirectoryTestDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.directory.UserDirectoryDetailVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.directory.UserDirectorySyncVo;
import cloud.xcan.angus.core.utils.CoreUtils;


public class UserDirectoryAssembler {

  public static UserDirectory addDtoToDomain(UserDirectoryAddDto dto) {
    return new UserDirectory()
        .setName(dto.getServer().getName())
        .setSequence(dto.getSequence())
        .setEnabled(true)
        .setServerData(dto.getServer())
        .setSchemaData(dto.getSchema())
        .setUserSchemaData(dto.getUserSchema())
        .setGroupSchemaData(dto.getGroupSchema())
        .setMembershipSchemaData(dto.getMembershipSchema());
  }

  public static UserDirectory replaceDtoToDomain(UserDirectoryReplaceDto dto) {
    return new UserDirectory().setId(dto.getId())
        .setName(dto.getServer().getName())
        .setSequence(dto.getSequence())
        .setServerData(dto.getServer())
        .setSchemaData(dto.getSchema())
        .setUserSchemaData(dto.getUserSchema())
        .setGroupSchemaData(dto.getGroupSchema())
        .setMembershipSchemaData(dto.getMembershipSchema());
  }

  public static UserDirectory testDtoToDomain(UserDirectoryTestDto dto) {
    return new UserDirectory()
        .setName(dto.getServer().getName())
        .setEnabled(true)
        .setSequence(1)
        .setServerData(dto.getServer())
        .setSchemaData(dto.getSchema())
        .setUserSchemaData(dto.getUserSchema())
        .setGroupSchemaData(dto.getGroupSchema())
        .setMembershipSchemaData(dto.getMembershipSchema());
  }

  public static UserDirectorySyncVo toSyncVo(DirectorySyncResult result) {
    return CoreUtils.copyProperties(result, new UserDirectorySyncVo());
  }

  public static UserDirectoryDetailVo toVo(UserDirectory directory) {
    return new UserDirectoryDetailVo().setId(directory.getId())
        .setName(directory.getName())
        .setSequence(directory.getSequence())
        .setEnabled(directory.getEnabled())
        .setServer(directory.getServerData())
        .setSchema(directory.getSchemaData())
        .setUserSchema(directory.getUserSchemaData())
        .setGroupSchema(directory.getGroupSchemaData())
        .setMembershipSchema(directory.getMembershipSchemaData())
        .setCreatedBy(directory.getCreatedBy())
        .setCreatedDate(directory.getCreatedDate())
        .setModifiedBy(directory.getModifiedBy())
        .setModifiedDate(directory.getModifiedDate());
  }

}
