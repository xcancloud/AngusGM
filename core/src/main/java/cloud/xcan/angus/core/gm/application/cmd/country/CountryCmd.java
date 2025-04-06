package cloud.xcan.angus.core.gm.application.cmd.country;

import cloud.xcan.angus.core.gm.domain.country.Country;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.List;

public interface CountryCmd {

  List<IdKey<Long, Object>> add(List<Country> countries);

  void update(List<Country> countries);

  void delete(HashSet<Long> ids);

}
