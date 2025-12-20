package cloud.xcan.angus.core.gm.application.cmd.ldap.impl;

import cloud.xcan.angus.core.gm.application.cmd.ldap.LdapCmd;
import cloud.xcan.angus.core.gm.domain.ldap.Ldap;
import cloud.xcan.angus.core.gm.domain.ldap.LdapRepo;
import cloud.xcan.angus.core.gm.domain.ldap.LdapStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LdapCmdImpl implements LdapCmd {
    
    private final LdapRepo ldapRepo;
    
    @Override
    @Transactional
    public Ldap create(Ldap ldap) {
        ldap.setStatus(LdapStatus.DISCONNECTED);
        return ldapRepo.save(ldap);
    }
    
    @Override
    @Transactional
    public Ldap update(Ldap ldap) {
        return ldapRepo.save(ldap);
    }
    
    @Override
    @Transactional
    public void delete(Long id) {
        ldapRepo.deleteById(id);
    }
    
    @Override
    @Transactional
    public void enable(Long id) {
        Ldap ldap = ldapRepo.findById(id).orElseThrow();
        ldap.setEnabled(true);
        ldapRepo.save(ldap);
    }
    
    @Override
    @Transactional
    public void disable(Long id) {
        Ldap ldap = ldapRepo.findById(id).orElseThrow();
        ldap.setEnabled(false);
        ldapRepo.save(ldap);
    }
    
    @Override
    @Transactional
    public void testConnection(Long id) {
        Ldap ldap = ldapRepo.findById(id).orElseThrow();
        ldap.setStatus(LdapStatus.AUTHENTICATING);
        ldapRepo.save(ldap);
    }
    
    @Override
    @Transactional
    public void syncUsers(Long id) {
        Ldap ldap = ldapRepo.findById(id).orElseThrow();
        ldap.setSyncEnabled(true);
        ldapRepo.save(ldap);
    }
}
