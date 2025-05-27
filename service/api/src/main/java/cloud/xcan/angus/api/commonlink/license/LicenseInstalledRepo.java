package cloud.xcan.angus.api.commonlink.license;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.jpa.repository.NameJoinRepository;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository("commonLicenseInstalled")
public interface LicenseInstalledRepo extends NameJoinRepository<LicenseInstalled, Long>,
    BaseRepository<LicenseInstalled, Long> {

  List<LicenseInstalled> findByGoodsCode(String goodsCode);
}
