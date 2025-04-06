package cloud.xcan.angus.core.gm.application.converter;

import static cloud.xcan.angus.api.commonlink.UCConstant.LDAP_GROUP_CODE_PREFIX;
import static cloud.xcan.angus.core.gm.application.converter.UserConverter.convertIfAbsent;
import static cloud.xcan.angus.core.spring.SpringContextHolder.getCachedUidGenerator;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_DESC_LENGTH;
import static cloud.xcan.angus.spec.utils.ObjectUtils.lengthSafe;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.api.commonlink.group.GroupSource;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.group.GroupUser;
import cloud.xcan.angus.core.gm.domain.user.directory.UserDirectory;
import java.util.ArrayList;
import java.util.List;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GroupConverter {

  public static GroupUser toGroupUser(Long groupId, User userDb) {
    GroupUser groupUser = new GroupUser()
        .setUserId(userDb.getId())
        .setGroupId(groupId);
    groupUser.setTenantId(userDb.getTenantId());
    return groupUser;
  }

  public static Group ldapToGroup(Attributes attrs, UserDirectory ldap) throws NamingException {
    return new Group().setId(getCachedUidGenerator().getUID())
        .setCode(LDAP_GROUP_CODE_PREFIX + convertIfAbsent(attrs,
            ldap.getMembershipSchemaData().getMemberGroupAttribute()))
        .setName(convertIfAbsent(attrs, ldap.getGroupSchemaData().getNameAttribute()))
        .setSource(GroupSource.LDAP_SYNCHRONIZE)
        .setEnabled(true)
        .setRemark(lengthSafe(convertIfAbsent(attrs,
            ldap.getGroupSchemaData().getDescriptionAttribute()), MAX_DESC_LENGTH))
        .setMembers(convertMember(attrs, ldap.getMembershipSchemaData().getGroupMemberAttribute()))
        .setDirectoryGidNumber(
            convertIfAbsent(attrs, ldap.getMembershipSchemaData().getMemberGroupAttribute()))
        .setDirectoryId(ldap.getId());
  }

  private static List<String> convertMember(Attributes attrs, String attrName)
      throws NamingException {
    List<String> result = new ArrayList<>();
    if (nonNull(attrs) && isNotEmpty(attrName)) {
      Attribute attribute = attrs.get(attrName);
      if (nonNull(attribute)) {
        NamingEnumeration<?> list = attribute.getAll();
        while (list.hasMoreElements()) {
          result.add(list.nextElement().toString());
        }
      }
    }
    return result;
  }
}
