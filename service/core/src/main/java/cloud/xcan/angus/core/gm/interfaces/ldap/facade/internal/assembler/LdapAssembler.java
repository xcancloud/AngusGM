package cloud.xcan.angus.core.gm.interfaces.ldap.facade.internal.assembler;

import cloud.xcan.angus.core.gm.domain.ldap.Ldap;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo.*;
import org.springframework.stereotype.Component;

@Component
public class LdapAssembler {
    
    public Ldap toEntity(LdapCreateDto dto) {
        Ldap ldap = new Ldap();
        ldap.setName(dto.getName());
        ldap.setType(dto.getType());
        ldap.setServerUrl(dto.getServerUrl());
        ldap.setBaseDn(dto.getBaseDn());
        ldap.setBindDn(dto.getBindDn());
        ldap.setBindPassword(dto.getBindPassword());
        ldap.setUserFilter(dto.getUserFilter());
        ldap.setGroupFilter(dto.getGroupFilter());
        ldap.setSyncEnabled(dto.getSyncEnabled());
        ldap.setDescription(dto.getDescription());
        return ldap;
    }
    
    public Ldap toEntity(LdapUpdateDto dto) {
        Ldap ldap = new Ldap();
        ldap.setId(dto.getId());
        ldap.setName(dto.getName());
        ldap.setStatus(dto.getStatus());
        ldap.setServerUrl(dto.getServerUrl());
        ldap.setBaseDn(dto.getBaseDn());
        ldap.setEnabled(dto.getEnabled());
        ldap.setDescription(dto.getDescription());
        return ldap;
    }
    
    public LdapDetailVo toDetailVo(Ldap ldap) {
        LdapDetailVo vo = new LdapDetailVo();
        vo.setId(ldap.getId());
        vo.setName(ldap.getName());
        vo.setType(ldap.getType());
        vo.setStatus(ldap.getStatus());
        vo.setServerUrl(ldap.getServerUrl());
        vo.setBaseDn(ldap.getBaseDn());
        vo.setSyncEnabled(ldap.getSyncEnabled());
        vo.setEnabled(ldap.getEnabled());
        vo.setDescription(ldap.getDescription());
        vo.setCreatedAt(ldap.getCreatedAt());
        return vo;
    }
    
    public LdapListVo toListVo(Ldap ldap) {
        LdapListVo vo = new LdapListVo();
        vo.setId(ldap.getId());
        vo.setName(ldap.getName());
        vo.setType(ldap.getType());
        vo.setStatus(ldap.getStatus());
        vo.setEnabled(ldap.getEnabled());
        return vo;
    }
}
