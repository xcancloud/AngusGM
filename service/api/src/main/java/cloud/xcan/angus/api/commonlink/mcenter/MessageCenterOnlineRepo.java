package cloud.xcan.angus.api.commonlink.mcenter;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository("commonMsgCenterOnlineRepo")
public interface MessageCenterOnlineRepo extends BaseRepository<MessageCenterOnline, Long> {

  @Override
  Page<MessageCenterOnline> findAll(Specification<MessageCenterOnline> spc, Pageable pageable);

  List<MessageCenterOnline> findAllByUserIdIn(Collection<Long> userIds);

  @Modifying
  @Query("update MessageCenterOnline a set a.online = true, a.onlineDate = now() where a.userId in ?1")
  void updateOnlineStatus(List<Long> userId);

  @Modifying
  @Query("update MessageCenterOnline a set a.online = false, a.offlineDate = now() where a.userId in ?1")
  void updateOfflineStatus(List<Long> userId);

}
