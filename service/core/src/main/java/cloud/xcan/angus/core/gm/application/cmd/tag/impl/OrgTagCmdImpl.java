package cloud.xcan.angus.core.gm.application.cmd.tag.impl;

import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.ORG_TAG;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.CREATED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.DELETED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.UPDATED;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;

import cloud.xcan.angus.api.commonlink.tag.OrgTag;
import cloud.xcan.angus.api.commonlink.tag.OrgTagRepo;
import cloud.xcan.angus.api.commonlink.tag.OrgTagTargetRepo;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.tag.OrgTagCmd;
import cloud.xcan.angus.core.gm.application.query.tag.OrgTagQuery;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * Implementation of organization tag command operations.
 * </p>
 * <p>
 * Manages organization tag lifecycle including creation, updates, and deletion.
 * Provides tag management with quota validation and audit logging.
 * </p>
 * <p>
 * Supports tenant-specific tag management with proper validation and
 * cascading deletion of tag targets.
 * </p>
 */
@Biz
public class OrgTagCmdImpl extends CommCmd<OrgTag, Long> implements OrgTagCmd {

  @Resource
  private OrgTagRepo orgTagRepo;
  @Resource
  private OrgTagTargetRepo orgTagTargetRepo;
  @Resource
  private OrgTagQuery orgTagQuery;
  @Resource
  private OperationLogCmd operationLogCmd;

  /**
   * <p>
   * Creates organization tags with validation.
   * </p>
   * <p>
   * Validates tag names, checks for duplicates, and verifies quota limits.
   * Creates tags and logs the operation for audit purposes.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> add(List<OrgTag> tags) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      final Long tenantId = getOptTenantId();

      @Override
      protected void checkParams() {
        // Verify tag names in parameters
        orgTagQuery.checkNameInParam(tags);
        // Verify tag name uniqueness for tenant
        orgTagQuery.checkAddTagName(tenantId, tags);
        // Verify tag quota for tenant
        orgTagQuery.checkQuota(tenantId, tags.size());
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        List<IdKey<Long, Object>> idKeys = batchInsert(tags, "name");
        operationLogCmd.addAll(ORG_TAG, tags, CREATED);
        return idKeys;
      }
    }.execute();
  }

  /**
   * <p>
   * Updates organization tags with validation.
   * </p>
   * <p>
   * Validates tag names and checks for uniqueness during updates.
   * Updates tags and logs the operation for audit purposes.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void update(List<OrgTag> tags) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        // Verify tag names in parameters
        orgTagQuery.checkNameInParam(tags);
        // Verify tag name uniqueness for tenant during update
        orgTagQuery.checkUpdateTagName(getOptTenantId(), tags);
      }

      @Override
      protected Void process() {
        List<OrgTag> tagsDb = batchUpdateOrNotFound(tags);
        operationLogCmd.addAll(ORG_TAG, tagsDb, UPDATED);
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Deletes organization tags and associated targets.
   * </p>
   * <p>
   * Removes tags and cascades deletion to associated tag targets.
   * Logs the operation for audit purposes.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(HashSet<Long> ids) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        List<OrgTag> tagsDb = orgTagQuery.checkAndFind(ids);
        if (!tagsDb.isEmpty()) {
          // Delete tags and associated targets
          orgTagRepo.deleteByIdIn(ids);
          orgTagTargetRepo.deleteByTagIdIn(ids);

          // Log operation for audit
          operationLogCmd.addAll(ORG_TAG, tagsDb, DELETED);
        }
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<OrgTag, Long> getRepository() {
    return this.orgTagRepo;
  }
}
