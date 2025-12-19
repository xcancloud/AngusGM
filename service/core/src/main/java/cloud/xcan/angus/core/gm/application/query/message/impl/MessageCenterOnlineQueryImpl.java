package cloud.xcan.angus.core.gm.application.query.message.impl;

import cloud.xcan.angus.api.manager.UserManager;

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

/**
 * <p>
 * Implementation of message center online query operations.
 * </p>
 * <p>
 * Manages message center online retrieval and user information association. Provides comprehensive
 * message center online querying with full-text search support.
 * </p>
 * <p>
 * Supports message center online detail retrieval, paginated listing, and user information
 * enrichment for comprehensive message center management.
 * </p>
 */
@org.springframework.stereotype.Service
public class MessageCenterOnlineQueryImpl implements MessageCenterOnlineQuery {

  @Resource
  private MessageCenterOnlineRepo messageCenterOnlineRepo;
  @Resource
  private MessageCenterOnlineSearchRepo messageCenterOnlineSearchRepo;
  @Resource
  private UserManager userManager;

  /**
   * <p>
   * Retrieves detailed message center online information by ID.
   * </p>
   * <p>
   * Fetches complete message center online record with all associated information. Throws
   * ResourceNotFound exception if record does not exist.
   * </p>
   */
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

  /**
   * <p>
   * Retrieves message center online records with optional filtering and search capabilities.
   * </p>
   * <p>
   * Supports full-text search and specification-based filtering. Enriches results with user
   * information for comprehensive display.
   * </p>
   */
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
