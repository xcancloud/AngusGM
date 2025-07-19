package cloud.xcan.angus.core.gm.application.query.message.impl;

import cloud.xcan.angus.api.manager.UserManager;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.message.MessageCenterOnlineQuery;
import cloud.xcan.angus.core.gm.domain.message.center.MessageCenterOnline;
import cloud.xcan.angus.core.gm.domain.message.center.MessageCenterOnlineRepo;
import cloud.xcan.angus.core.gm.domain.message.center.MessageCenterOnlineSearchRepo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Biz
public class MessageCenterOnlineQueryImpl implements MessageCenterOnlineQuery {

  @Resource
  private MessageCenterOnlineRepo messageCenterOnlineRepo;

  @Resource
  private MessageCenterOnlineSearchRepo messageCenterOnlineSearchRepo;

  @Resource
  private UserManager userManager;

  @Override
  public MessageCenterOnline detail(Long id) {
    return new BizTemplate<MessageCenterOnline>() {

      @Override
      protected MessageCenterOnline process() {
        return messageCenterOnlineRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of(id, "MessageCenterOnline"));
      }
    }.execute();
  }

  @Override
  public Page<MessageCenterOnline> list(GenericSpecification<MessageCenterOnline> spec,
      PageRequest pageable, boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<MessageCenterOnline>>() {
      @Override
      protected Page<MessageCenterOnline> process() {
        Page<MessageCenterOnline> page = fullTextSearch
            ? messageCenterOnlineSearchRepo.find(spec.getCriteria(), pageable,
            MessageCenterOnline.class, match)
            : messageCenterOnlineRepo.findAll(spec, pageable);
        if (page.hasContent()) {
          userManager.setUserNameAndAvatar(page.getContent(), "userId", "fullName", "avatar");
        }
        return page;
      }
    }.execute();
  }


}
