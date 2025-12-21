package cloud.xcan.angus.core.gm.interfaces.ldap.facade.internal;

import cloud.xcan.angus.core.gm.application.cmd.ldap.LdapCmd;
import cloud.xcan.angus.core.gm.application.query.ldap.LdapQuery;
import cloud.xcan.angus.core.gm.domain.ldap.Ldap;
import cloud.xcan.angus.core.gm.domain.ldap.LdapStatus;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.LdapFacade;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.internal.assembler.LdapAssembler;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LdapFacadeImpl implements LdapFacade {
    
    @Resource
    private LdapCmd ldapCmd;
    
    @Resource
    private LdapQuery ldapQuery;
    
    @Resource
    private LdapAssembler assembler;
    
    @Override
    public LdapDetailVo create(LdapCreateDto dto) {
        Ldap ldap = assembler.toEntity(dto);
        Ldap created = ldapCmd.create(ldap);
        return assembler.toDetailVo(created);
    }
    
    @Override
    public LdapDetailVo update(LdapUpdateDto dto) {
        Ldap ldap = assembler.toEntity(dto);
        Ldap updated = ldapCmd.update(ldap);
        return assembler.toDetailVo(updated);
    }
    
    @Override
    public void delete(Long id) {
        ldapCmd.delete(id);
    }
    
    @Override
    public LdapDetailVo findById(Long id) {
        Ldap ldap = ldapQuery.findById(id).orElseThrow();
        return assembler.toDetailVo(ldap);
    }
    
    @Override
    public List<LdapListVo> findAll(LdapFindDto dto) {
        return ldapQuery.findAll().stream()
                .map(assembler::toListVo)
                .collect(Collectors.toList());
    }
    
    @Override
    public LdapStatsVo getStats() {
        List<Ldap> all = ldapQuery.findAll();
        LdapStatsVo stats = new LdapStatsVo();
        stats.setTotalCount((long) all.size());
        stats.setConnectedCount(all.stream().filter(l -> l.getStatus() == LdapStatus.CONNECTED).count());
        stats.setEnabledCount(all.stream().filter(Ldap::getEnabled).count());
        return stats;
    }
}
