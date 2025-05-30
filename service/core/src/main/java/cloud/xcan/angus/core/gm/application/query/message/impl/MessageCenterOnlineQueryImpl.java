package cloud.xcan.angus.core.gm.application.query.message.impl;

import cloud.xcan.angus.api.manager.UserManager;
import cloud.xcan.angus.core.gm.domain.message.center.MessageCenterOnline;
import cloud.xcan.angus.core.gm.domain.message.center.MessageCenterOnlineRepo;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.message.MessageCenterOnlineQuery;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@Biz
public class MessageCenterOnlineQueryImpl implements MessageCenterOnlineQuery {

  @Resource
  private MessageCenterOnlineRepo messageCenterOnlineRepo;

  @Resource
  private UserManager userManager;

  @Override
  public MessageCenterOnline find(Long id) {
    return new BizTemplate<MessageCenterOnline>() {

      @Override
      protected MessageCenterOnline process() {
        return messageCenterOnlineRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of(String.valueOf(id), "MessageCenterOnline"));
      }
    }.execute();
  }

  @Override
  public Page<MessageCenterOnline> find(Specification<MessageCenterOnline> spec,
      Pageable pageable) {
    return new BizTemplate<Page<MessageCenterOnline>>() {
      @Override
      protected Page<MessageCenterOnline> process() {
        Page<MessageCenterOnline> page = messageCenterOnlineRepo.findAll(spec, pageable);
        if (page.hasContent()) {
          if (page.hasContent()) {
            userManager.setUserNameAndAvatar(page.getContent(), "userId", "fullName", "avatar");
          }
        }
        return page;
      }
    }.execute();
  }


}
