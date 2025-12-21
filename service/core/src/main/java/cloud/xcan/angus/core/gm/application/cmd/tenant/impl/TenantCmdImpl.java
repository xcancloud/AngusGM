package cloud.xcan.angus.core.gm.application.cmd.tenant.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.tenant.TenantCmd;
import cloud.xcan.angus.core.gm.application.query.tenant.TenantQuery;
import cloud.xcan.angus.core.gm.domain.tenant.Tenant;
import cloud.xcan.angus.core.gm.domain.tenant.TenantRepo;
import cloud.xcan.angus.core.gm.domain.tenant.enums.TenantStatus;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.remote.message.http.ProtocolException;
import cloud.xcan.angus.remote.message.http.ResourceExisted;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of tenant command service
 */
@Biz
public class TenantCmdImpl extends CommCmd<Tenant, Long> implements TenantCmd {

  @Resource
  private TenantRepo tenantRepo;

  @Resource
  private TenantQuery tenantQuery;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Tenant create(Tenant tenant) {
    return new BizTemplate<Tenant>() {
      @Override
      protected void checkParams() {
        // Check if tenant code already exists
        if (tenantRepo.existsByCode(tenant.getCode())) {
          throw ResourceExisted.of("租户编码「{0}」已存在", new Object[]{tenant.getCode()});
        }
      }

      @Override
      protected Tenant process() {
        // Set default status if not provided
        if (tenant.getStatus() == null) {
          tenant.setStatus(TenantStatus.ENABLED);
        }
        // Insert tenant
        insert(tenant);
        return tenant;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Tenant update(Tenant tenant) {
    return new BizTemplate<Tenant>() {
      Tenant tenantDb;

      @Override
      protected void checkParams() {
        // Check if tenant exists
        tenantDb = tenantQuery.findAndCheck(tenant.getId());
        
        // If code is being updated, check for duplicates
        if (tenant.getCode() != null && !tenant.getCode().equals(tenantDb.getCode())) {
          if (tenantRepo.existsByCodeAndIdNot(tenant.getCode(), tenant.getId())) {
            throw ResourceExisted.of("租户编码「{0}」已存在", new Object[]{tenant.getCode()});
          }
        }
      }

      @Override
      protected Tenant process() {
        // Update tenant
        update(tenant, tenantDb);
        return tenantDb;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        // Check if tenant exists
        tenantQuery.findAndCheck(id);
      }

      @Override
      protected Void process() {
        // Delete tenant
        tenantRepo.deleteById(id);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void enable(Long id) {
    new BizTemplate<Void>() {
      Tenant tenantDb;

      @Override
      protected void checkParams() {
        tenantDb = tenantQuery.findAndCheck(id);
      }

      @Override
      protected Void process() {
        tenantDb.setStatus(TenantStatus.ENABLED);
        tenantRepo.save(tenantDb);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void disable(Long id) {
    new BizTemplate<Void>() {
      Tenant tenantDb;

      @Override
      protected void checkParams() {
        tenantDb = tenantQuery.findAndCheck(id);
      }

      @Override
      protected Void process() {
        tenantDb.setStatus(TenantStatus.DISABLED);
        tenantRepo.save(tenantDb);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<Tenant, Long> getRepository() {
    return tenantRepo;
  }
}
