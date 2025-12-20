package cloud.xcan.angus.core.gm.application.cmd.group.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.group.GroupCmd;
import cloud.xcan.angus.core.gm.application.query.group.GroupQuery;
import cloud.xcan.angus.core.gm.domain.group.Group;
import cloud.xcan.angus.core.gm.domain.group.GroupRepo;
import cloud.xcan.angus.core.gm.domain.group.enums.GroupStatus;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.remote.message.http.ResourceExisted;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of group command service
 */
@Biz
public class GroupCmdImpl extends CommCmd<Group, Long> implements GroupCmd {

  @Resource
  private GroupRepo groupRepo;

  @Resource
  private GroupQuery groupQuery;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Group create(Group group) {
    return new BizTemplate<Group>() {
      @Override
      protected void checkParams() {
        if (groupRepo.existsByCode(group.getCode())) {
          throw ResourceExisted.of("组编码「{0}」已存在", new Object[]{group.getCode()});
        }
      }

      @Override
      protected Group process() {
        if (group.getStatus() == null) {
          group.setStatus(GroupStatus.ENABLED);
        }
        insert(group);
        return group;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Group update(Group group) {
    return new BizTemplate<Group>() {
      Group groupDb;

      @Override
      protected void checkParams() {
        groupDb = groupQuery.findAndCheck(group.getId());
        
        if (group.getCode() != null && !group.getCode().equals(groupDb.getCode())) {
          if (groupRepo.existsByCodeAndIdNot(group.getCode(), group.getId())) {
            throw ResourceExisted.of("组编码「{0}」已存在", new Object[]{group.getCode()});
          }
        }
      }

      @Override
      protected Group process() {
        update(group, groupDb);
        return groupDb;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        groupQuery.findAndCheck(id);
      }

      @Override
      protected Void process() {
        groupRepo.deleteById(id);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void enable(Long id) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        groupQuery.findAndCheck(id);
      }

      @Override
      protected Void process() {
        Group group = new Group();
        group.setId(id);
        group.setStatus(GroupStatus.ENABLED);
        groupRepo.save(group);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void disable(Long id) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        groupQuery.findAndCheck(id);
      }

      @Override
      protected Void process() {
        Group group = new Group();
        group.setId(id);
        group.setStatus(GroupStatus.DISABLED);
        groupRepo.save(group);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<Group, Long> getRepository() {
    return groupRepo;
  }
}
