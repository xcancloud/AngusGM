package cloud.xcan.angus.core.gm.domain.country.district;

import cloud.xcan.angus.core.gm.domain.country.district.model.DistrictLevel;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface DistrictRepo extends BaseRepository<District, Long> {

  List<District> findByCountryCode(String countryCode);

  District findByCountryCodeAndCode(String countryCode, String code);

  List<District> findByCountryCodeAndLevel(String countryCode, DistrictLevel level);

  List<District> findByCountryCodeAndParentCodeAndLevel(String countryCode, String parentCode,
      DistrictLevel level);

  Page<District> findAll(Specification<District> spc, Pageable pageable);

}
