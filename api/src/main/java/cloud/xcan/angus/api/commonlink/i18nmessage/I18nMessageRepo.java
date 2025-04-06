package cloud.xcan.angus.api.commonlink.i18nmessage;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.jpa.repository.I18nMessageJoinRepository;
import org.springframework.stereotype.Repository;

@Repository("i18nMessageRepo")
public interface I18nMessageRepo extends I18nMessageJoinRepository<I18nMessages>,
    BaseRepository<I18nMessages, Long> {

}
