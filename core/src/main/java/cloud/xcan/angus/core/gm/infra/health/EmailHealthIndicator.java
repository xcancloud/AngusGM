package cloud.xcan.angus.core.gm.infra.health;

import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.core.gm.application.query.email.EmailServerQuery;
import cloud.xcan.angus.core.gm.domain.email.server.EmailProtocol;
import jakarta.annotation.Resource;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

@Component
public class EmailHealthIndicator extends AbstractHealthIndicator {

  @Resource
  private EmailServerQuery emailServerQuery;

  /**
   * return UNKNOWN  {@link Status#UNKNOWN},not configured. If you throw an exception, the status
   * will be DOWN {@link Status#DOWN} with the exception message.
   */
  @Override
  protected void doHealthCheck(Health.Builder builder) {
    try {
      Boolean health = emailServerQuery.checkHealth(EmailProtocol.SMTP);
      if (health) {
        builder.up();
      } else {
        builder.up().withDetail("warning", "Mail service is not configured or enabled");
      }
    } catch (Exception e) {
      // Fix:: builder.down(e); <- The whole service will be offline and RPC and WebUI will not be available
      builder.up().withDetail("exception", nullSafe(e.getMessage(), ""));
    }
  }
}
