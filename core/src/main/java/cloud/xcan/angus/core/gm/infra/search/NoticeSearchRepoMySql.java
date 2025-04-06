package cloud.xcan.angus.core.gm.infra.search;

import cloud.xcan.angus.core.gm.domain.notice.Notice;
import cloud.xcan.angus.core.gm.domain.notice.NoticeSearchRepo;
import cloud.xcan.angus.core.jpa.repository.SimpleSearchRepository;
import org.springframework.stereotype.Repository;


@Repository
public class NoticeSearchRepoMySql extends SimpleSearchRepository<Notice> implements
    NoticeSearchRepo {

}
