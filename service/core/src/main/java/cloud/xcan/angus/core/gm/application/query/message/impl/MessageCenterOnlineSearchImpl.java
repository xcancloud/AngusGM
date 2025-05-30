package cloud.xcan.angus.core.gm.application.query.message.impl;

import cloud.xcan.angus.api.manager.UserManager;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.message.MessageCenterOnlineSearch;
import cloud.xcan.angus.core.gm.domain.message.center.MessageCenterOnline;
import cloud.xcan.angus.core.gm.domain.message.center.MessageCenterOnlineSearchRepo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Biz
public class MessageCenterOnlineSearchImpl implements MessageCenterOnlineSearch {

  @Resource
  private MessageCenterOnlineSearchRepo messageCenterOnlineSearchRepo;

  @Resource
  private UserManager userManager;

  @Override
  public Page<MessageCenterOnline> search(Set<SearchCriteria> criteria, Pageable pageable,
      Class<MessageCenterOnline> clz, String... matches) {
    return new BizTemplate<Page<MessageCenterOnline>>() {

      @Override
      protected Page<MessageCenterOnline> process() {
        Page<MessageCenterOnline> page = messageCenterOnlineSearchRepo.find(
            criteria, pageable, clz, matches);
        if (page.hasContent()) {
          userManager.setUserNameAndAvatar(page.getContent(), "userId", "fullName", "avatar");
        }
        return page;
      }
    }.execute();
  }
}
