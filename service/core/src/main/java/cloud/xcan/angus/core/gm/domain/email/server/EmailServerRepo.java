package cloud.xcan.angus.core.gm.domain.email.server;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface EmailServerRepo extends BaseRepository<EmailServer, Long> {

  List<EmailServer> findAllByProtocolAndEnabled(EmailProtocol protocol, Boolean enabled);

  List<EmailServer> findAllByProtocolAndIdNot(EmailProtocol protocol, Long id);

  List<EmailServer> findByName(String name);

  List<EmailServer> findByEnabled(Boolean enabled);

  List<EmailServer> findByNameAndIdNot(String name, Long id);

  @Modifying
  @Query(value = "DELETE FROM email_server WHERE id IN ?1", nativeQuery = true)
  void deleteByIdIn(Collection<Long> ids);

}
