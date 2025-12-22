package cloud.xcan.angus.core.gm.application.cmd.ldap.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.cmd.ldap.LdapCmd;
import cloud.xcan.angus.core.gm.application.query.ldap.LdapQuery;
import cloud.xcan.angus.core.gm.domain.ldap.Ldap;
import cloud.xcan.angus.core.gm.domain.ldap.LdapRepo;
import cloud.xcan.angus.core.gm.domain.ldap.LdapStatus;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of LDAP command service
 */
@Biz
@SuppressWarnings("unchecked")
public class LdapCmdImpl implements LdapCmd {
    
    @Resource
    private LdapRepo ldapRepo;
    
    @Resource
    private LdapQuery ldapQuery;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Ldap create(Ldap ldap) {
        return new BizTemplate<Ldap>() {
            @Override
            protected void checkParams() {
                // Add validation if needed
            }

            @Override
            protected Ldap process() {
                ldap.setStatus(LdapStatus.DISCONNECTED);
                return ((JpaRepository<Ldap, Long>) ldapRepo).save(ldap);
            }
        }.execute();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Ldap update(Ldap ldap) {
        return new BizTemplate<Ldap>() {
            Ldap ldapDb;

            @Override
            protected void checkParams() {
                // Use reflection to get id since BaseEntity may not expose getId() directly
                Long id;
                try {
                    java.lang.reflect.Method getIdMethod = ldap.getClass().getMethod("getId");
                    Object idObj = getIdMethod.invoke(ldap);
                    id = idObj != null ? (Long) idObj : null;
                } catch (Exception e) {
                    throw ResourceNotFound.of("无法获取LDAP配置ID", new Object[]{});
                }
                if (id == null) {
                    throw ResourceNotFound.of("LDAP配置ID不能为空", new Object[]{});
                }
                ldapDb = ldapQuery.findById(id)
                    .orElseThrow(() -> ResourceNotFound.of("LDAP配置未找到", new Object[]{}));
            }

            @Override
            protected Ldap process() {
                // Update fields from ldap to ldapDb
                if (ldap.getName() != null) {
                    ldapDb.setName(ldap.getName());
                }
                if (ldap.getType() != null) {
                    ldapDb.setType(ldap.getType());
                }
                if (ldap.getStatus() != null) {
                    ldapDb.setStatus(ldap.getStatus());
                }
                if (ldap.getServerUrl() != null) {
                    ldapDb.setServerUrl(ldap.getServerUrl());
                }
                if (ldap.getBaseDn() != null) {
                    ldapDb.setBaseDn(ldap.getBaseDn());
                }
                if (ldap.getBindDn() != null) {
                    ldapDb.setBindDn(ldap.getBindDn());
                }
                if (ldap.getBindPassword() != null) {
                    ldapDb.setBindPassword(ldap.getBindPassword());
                }
                if (ldap.getUserFilter() != null) {
                    ldapDb.setUserFilter(ldap.getUserFilter());
                }
                if (ldap.getGroupFilter() != null) {
                    ldapDb.setGroupFilter(ldap.getGroupFilter());
                }
                if (ldap.getSyncEnabled() != null) {
                    ldapDb.setSyncEnabled(ldap.getSyncEnabled());
                }
                if (ldap.getEnabled() != null) {
                    ldapDb.setEnabled(ldap.getEnabled());
                }
                if (ldap.getDescription() != null) {
                    ldapDb.setDescription(ldap.getDescription());
                }
                return ((JpaRepository<Ldap, Long>) ldapRepo).save(ldapDb);
            }
        }.execute();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        new BizTemplate<Void>() {
            @Override
            protected void checkParams() {
                ldapQuery.findById(id)
                    .orElseThrow(() -> ResourceNotFound.of("LDAP配置未找到", new Object[]{}));
            }

            @Override
            protected Void process() {
                ((JpaRepository<Ldap, Long>) ldapRepo).deleteById(id);
                return null;
            }
        }.execute();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enable(Long id) {
        new BizTemplate<Void>() {
            Ldap ldapDb;

            @Override
            protected void checkParams() {
                ldapDb = ldapQuery.findById(id)
                    .orElseThrow(() -> ResourceNotFound.of("LDAP配置未找到", new Object[]{}));
            }

            @Override
            protected Void process() {
                ldapDb.setEnabled(true);
                ((JpaRepository<Ldap, Long>) ldapRepo).save(ldapDb);
                return null;
            }
        }.execute();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disable(Long id) {
        new BizTemplate<Void>() {
            Ldap ldapDb;

            @Override
            protected void checkParams() {
                ldapDb = ldapQuery.findById(id)
                    .orElseThrow(() -> ResourceNotFound.of("LDAP配置未找到", new Object[]{}));
            }

            @Override
            protected Void process() {
                ldapDb.setEnabled(false);
                ((JpaRepository<Ldap, Long>) ldapRepo).save(ldapDb);
                return null;
            }
        }.execute();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void testConnection(Long id) {
        new BizTemplate<Void>() {
            Ldap ldapDb;

            @Override
            protected void checkParams() {
                ldapDb = ldapQuery.findById(id)
                    .orElseThrow(() -> ResourceNotFound.of("LDAP配置未找到", new Object[]{}));
            }

            @Override
            protected Void process() {
                ldapDb.setStatus(LdapStatus.AUTHENTICATING);
                ((JpaRepository<Ldap, Long>) ldapRepo).save(ldapDb);
                return null;
            }
        }.execute();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncUsers(Long id) {
        new BizTemplate<Void>() {
            Ldap ldapDb;

            @Override
            protected void checkParams() {
                ldapDb = ldapQuery.findById(id)
                    .orElseThrow(() -> ResourceNotFound.of("LDAP配置未找到", new Object[]{}));
            }

            @Override
            protected Void process() {
                ldapDb.setSyncEnabled(true);
                ((JpaRepository<Ldap, Long>) ldapRepo).save(ldapDb);
                return null;
            }
        }.execute();
    }
}
