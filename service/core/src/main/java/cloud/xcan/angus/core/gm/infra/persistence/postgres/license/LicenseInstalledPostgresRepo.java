package cloud.xcan.angus.core.gm.infra.persistence.postgres.license;

import cloud.xcan.angus.api.commonlink.license.LicenseInstalledRepo;
import org.springframework.stereotype.Repository;

@Repository("licenseInstalledRepo")
public interface LicenseInstalledPostgresRepo extends LicenseInstalledRepo {

}
