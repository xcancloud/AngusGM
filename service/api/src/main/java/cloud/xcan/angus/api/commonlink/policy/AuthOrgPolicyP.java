package cloud.xcan.angus.api.commonlink.policy;

import cloud.xcan.angus.api.commonlink.AuthOrgType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;

public interface AuthOrgPolicyP {

  Long getPolicyId();

  @Enumerated(EnumType.STRING)
  AuthOrgType getOrgType();

  Long getOrgId();

  @Value("#{target.default0 == 1}")
  boolean getDefault0(); // Projection type must be an interface!

  LocalDateTime getDate();

}
