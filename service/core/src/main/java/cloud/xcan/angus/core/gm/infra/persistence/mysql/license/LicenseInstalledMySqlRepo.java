package cloud.xcan.angus.core.gm.infra.persistence.mysql.license;

import cloud.xcan.angus.api.commonlink.license.LicenseInstalledRepo;
import org.springframework.stereotype.Repository;

@Repository("licenseInstalledRepo")
public interface LicenseInstalledMySqlRepo extends LicenseInstalledRepo {

}
