package cloud.xcan.angus.core.gm.application.cmd.tag.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.tag.TagCmd;
import cloud.xcan.angus.core.gm.application.query.tag.TagQuery;
import cloud.xcan.angus.core.gm.domain.tag.Tag;
import cloud.xcan.angus.core.gm.domain.tag.TagRepo;
import cloud.xcan.angus.core.gm.domain.tag.enums.TagStatus;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.remote.message.http.ResourceExisted;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of tag command service
 */
@Biz
public class TagCmdImpl extends CommCmd<Tag, Long> implements TagCmd {

  @Resource
  private TagRepo tagRepo;

  @Resource
  private TagQuery tagQuery;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Tag create(Tag tag) {
    return new BizTemplate<Tag>() {
      @Override
      protected void checkParams() {
        if (tagRepo.existsByName(tag.getName())) {
          throw ResourceExisted.of("标签名称「{0}」已存在", new Object[]{tag.getName()});
        }
      }

      @Override
      protected Tag process() {
        if (tag.getStatus() == null) {
          tag.setStatus(TagStatus.ENABLED);
        }
        if (tag.getSortOrder() == null) {
          tag.setSortOrder(0);
        }
        insert(tag);
        return tag;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Tag update(Tag tag) {
    return new BizTemplate<Tag>() {
      Tag tagDb;

      @Override
      protected void checkParams() {
        tagDb = tagQuery.findAndCheck(tag.getId());
        
        if (tag.getName() != null && !tag.getName().equals(tagDb.getName())) {
          if (tagRepo.existsByNameAndIdNot(tag.getName(), tag.getId())) {
            throw ResourceExisted.of("标签名称「{0}」已存在", new Object[]{tag.getName()});
          }
        }
      }

      @Override
      protected Tag process() {
        update(tag, tagDb);
        return tagDb;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        tagQuery.findAndCheck(id);
      }

      @Override
      protected Void process() {
        tagRepo.deleteById(id);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void enable(Long id) {
    new BizTemplate<Void>() {
      Tag tagDb;

      @Override
      protected void checkParams() {
        tagDb = tagQuery.findAndCheck(id);
      }

      @Override
      protected Void process() {
        tagDb.setStatus(TagStatus.ENABLED);
        tagRepo.save(tagDb);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void disable(Long id) {
    new BizTemplate<Void>() {
      Tag tagDb;

      @Override
      protected void checkParams() {
        tagDb = tagQuery.findAndCheck(id);
      }

      @Override
      protected Void process() {
        tagDb.setStatus(TagStatus.DISABLED);
        tagRepo.save(tagDb);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<Tag, Long> getRepository() {
    return tagRepo;
  }
}
