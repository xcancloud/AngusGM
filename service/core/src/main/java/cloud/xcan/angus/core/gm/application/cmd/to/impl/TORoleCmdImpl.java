package cloud.xcan.angus.core.gm.application.cmd.to.impl;

import static cloud.xcan.angus.core.utils.CoreUtils.copyPropertiesIgnoreTenantAuditing;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.to.TORole;
import cloud.xcan.angus.api.commonlink.to.TORoleRepo;
import cloud.xcan.angus.api.commonlink.to.TORoleUserRepo;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.to.TORoleCmd;
import cloud.xcan.angus.core.gm.application.query.app.AppQuery;
import cloud.xcan.angus.core.gm.application.query.to.TORoleQuery;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;


@Biz
public class TORoleCmdImpl extends CommCmd<TORole, Long> implements TORoleCmd {

  @Resource
  private TORoleRepo toRoleRepo;

  @Resource
  private TORoleQuery toRoleQuery;

  @Resource
  private TORoleUserRepo toRoleUserRepo;

  @Resource
  private AppQuery appQuery;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public List<IdKey<Long, Object>> add(List<TORole> roles) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {

      @Override
      protected void checkParams() {
        // Check the code and name duplication in params
        toRoleQuery.checkDuplicateInParam(roles);

        // Check the code and name duplication in db
        toRoleQuery.checkUniqueCodeAndName(roles);

        // Check the app existed
        appQuery.checkAndFind(roles.stream().map(TORole::getAppId).collect(Collectors.toSet()),
            true);
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        return batchInsert(roles, "code");
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void update(List<TORole> roles) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        // Check the code and name duplication in params
        toRoleQuery.checkDuplicateInParam(roles);

        // Check the roles existed
        toRoleQuery.checkAndFind(roles.stream().map(TORole::getId)
            .collect(Collectors.toSet()), false);

        // Check the code and name duplication in db
        toRoleQuery.checkUniqueCodeAndName(roles);
      }

      @Override
      protected Void process() {
        batchUpdateOrNotFound(roles);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public List<IdKey<Long, Object>> replace(List<TORole> roles) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      List<TORole> replaceTORoles;
      Set<Long> replaceTORoleIds;
      List<TORole> replaceTORoleDb;

      @Override
      protected void checkParams() {
        // Check the code and name duplication in params
        toRoleQuery.checkDuplicateInParam(roles);
        // Check the updated policies existed
        replaceTORoles = roles.stream().filter(dept -> nonNull(dept.getId()))
            .collect(Collectors.toList());
        replaceTORoleIds = replaceTORoles.stream().map(TORole::getId)
            .collect(Collectors.toSet());
        replaceTORoleDb = toRoleQuery.checkAndFind(replaceTORoleIds, false);
        // Check the code and name duplication in db
        toRoleQuery.checkUniqueCodeAndName(roles);
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        List<IdKey<Long, Object>> idKeys = new ArrayList<>();

        List<TORole> addTORoles = roles.stream().filter(dept -> isNull(dept.getId()))
            .collect(Collectors.toList());
        if (isNotEmpty(addTORoles)) {
          idKeys.addAll(add(addTORoles));
        }

        if (isNotEmpty(replaceTORoleDb)) {
          Map<Long, TORole> toRoleDbMap = replaceTORoleDb.stream()
              .collect(Collectors.toMap(TORole::getId, x -> x));
          toRoleRepo.saveAll(replaceTORoles.stream()
              .map(x -> copyPropertiesIgnoreTenantAuditing(x, toRoleDbMap.get(x.getId()),
                  "appId", "code", "enabled"))
              .collect(Collectors.toList()));

          idKeys.addAll(replaceTORoleDb.stream().map(x ->
              IdKey.of(x.getId(), x.getName())).toList());
        }
        return idKeys;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(HashSet<Long> roleIds) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        toRoleRepo.deleteByIdIn(roleIds);
        toRoleUserRepo.deleteByToRoleIdIn(roleIds);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void enabled(List<TORole> roles) {
    new BizTemplate<Void>() {
      List<Long> roleIds;

      @Override
      protected void checkParams() {
        // Check the roles existed
        roleIds = roles.stream().map(TORole::getId).collect(Collectors.toList());
        toRoleQuery.checkAndFind(roleIds, false);
      }

      @Override
      protected Void process() {
        batchUpdateOrNotFound(roles);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<TORole, Long> getRepository() {
    return this.toRoleRepo;
  }
}
