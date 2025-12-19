package cloud.xcan.angus.core.gm.application.query.ldap.impl;

import cloud.xcan.angus.core.gm.application.query.ldap.LdapQuery;
import cloud.xcan.angus.core.gm.domain.ldap.Ldap;
import cloud.xcan.angus.core.gm.domain.ldap.LdapRepo;
import cloud.xcan.angus.core.gm.domain.ldap.LdapStatus;
import cloud.xcan.angus.core.gm.domain.ldap.LdapType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LdapQueryImpl implements LdapQuery {
    
    private final LdapRepo ldapRepo;
    
    @Override
    public Optional<Ldap> findById(Long id) {
        return ldapRepo.findById(id);
    }
    
    @Override
    public Optional<Ldap> findByName(String name) {
        return ldapRepo.findByName(name);
    }
    
    @Override
    public List<Ldap> findByType(LdapType type) {
        return ldapRepo.findByType(type);
    }
    
    @Override
    public List<Ldap> findByStatus(LdapStatus status) {
        return ldapRepo.findByStatus(status);
    }
    
    @Override
    public List<Ldap> findAll() {
        return ldapRepo.findAll();
    }
}
