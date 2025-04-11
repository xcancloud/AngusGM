package cloud.xcan.angus.core.gm.application.query.message.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.message.MessageSearch;
import cloud.xcan.angus.core.gm.domain.message.MessageInfo;
import cloud.xcan.angus.core.gm.domain.message.MessageInfoSearchRepo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Biz
public class MessageSearchImpl implements MessageSearch {

  @Resource
  private MessageInfoSearchRepo messageSearchRepo;

  @Override
  public Page<MessageInfo> search(Set<SearchCriteria> criteria, Pageable pageable,
      Class<MessageInfo> clz, String... matches) {
    return new BizTemplate<Page<MessageInfo>>() {

      @Override
      protected Page<MessageInfo> process() {
        return messageSearchRepo.find(criteria, pageable, clz, matches);
      }
    }.execute();
  }

}
