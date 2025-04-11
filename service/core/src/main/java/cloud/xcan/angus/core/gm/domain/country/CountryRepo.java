package cloud.xcan.angus.core.gm.domain.country;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface CountryRepo extends BaseRepository<Country, Long> {

}
