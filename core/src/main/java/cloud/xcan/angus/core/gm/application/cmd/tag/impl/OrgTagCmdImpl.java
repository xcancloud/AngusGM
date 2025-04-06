package cloud.xcan.angus.core.gm.application.cmd.tag.impl;

import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;

import cloud.xcan.angus.api.commonlink.tag.OrgTag;
import cloud.xcan.angus.api.commonlink.tag.OrgTagRepo;
import cloud.xcan.angus.api.commonlink.tag.OrgTagTargetRepo;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.tag.OrgTagCmd;
import cloud.xcan.angus.core.gm.application.query.tag.OrgTagQuery;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;


@Biz
public class OrgTagCmdImpl extends CommCmd<OrgTag, Long> implements OrgTagCmd {

  @Resource
  private OrgTagRepo orgTagRepo;

  @Resource
  private OrgTagTargetRepo orgTagTargetRepo;

  @Resource
  private OrgTagQuery orgTagQuery;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> add(List<OrgTag> tags) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      final Long tenantId = getOptTenantId();

      @Override
      protected void checkParams() {
        orgTagQuery.checkNameInParam(tags);
        orgTagQuery.checkAddTagName(tenantId, tags);
        orgTagQuery.checkQuota(tenantId, tags.size());
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        return batchInsert(tags, "name");
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void update(List<OrgTag> tags) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        orgTagQuery.checkNameInParam(tags);
        orgTagQuery.checkUpdateTagName(getOptTenantId(), tags);
      }

      @Override
      protected Void process() {
        batchUpdateOrNotFound(tags);
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(HashSet<Long> ids) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        orgTagRepo.deleteByIdIn(ids);
        orgTagTargetRepo.deleteByTagIdIn(ids);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<OrgTag, Long> getRepository() {
    return this.orgTagRepo;
  }
}
