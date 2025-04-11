package cloud.xcan.angus.core.gm.domain.notice;

import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;


@NoRepositoryBean
public interface NoticeSearchRepo extends CustomBaseRepository<Notice> {

}
