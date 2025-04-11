package cloud.xcan.angus.core.gm.domain.message;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import org.springframework.data.repository.NoRepositoryBean;


@NoRepositoryBean
public interface MessageInfoRepo extends BaseRepository<MessageInfo, Long> {

}
