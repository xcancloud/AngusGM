package cloud.xcan.angus.core.gm.infra.job;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.enums.ProcessStatus;
import cloud.xcan.angus.core.gm.application.cmd.email.EmailCmd;
import cloud.xcan.angus.core.gm.application.query.email.EmailQuery;
import cloud.xcan.angus.core.gm.domain.email.Email;
import cloud.xcan.angus.core.job.JobTemplate;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * <p>
 * Scheduled job for processing and sending email messages in the system.
 * </p>
 * <p>
 * This job implements a distributed email processing system that handles both
 * tenant-scoped and platform-scoped email delivery. It uses distributed locking
 * to ensure only one instance processes emails at a time, preventing duplicate
 * sending and maintaining system consistency.
 * </p>
 * <p>
 * The job processes emails in batches for improved performance and includes
 * comprehensive error handling to mark failed emails appropriately.
 * </p>
 *
 * @author System
 * @since 1.0.0
 */
@Slf4j
@Component
public class EmailSendJob {

    /**
     * Distributed lock key for tenant-scoped email processing to prevent concurrent execution.
     */
    private static final String TENANT_EMAIL_LOCK_KEY = "gm:job:EmailSendJob:sendTenantScopeEmail";
    
    /**
     * Distributed lock key for platform-scoped email processing to prevent concurrent execution.
     */
    private static final String PLATFORM_EMAIL_LOCK_KEY = "gm:job:EmailSendJob:sendPlatformScopeEmail";

    /**
     * Batch size for email processing to optimize performance and memory usage.
     */
    private static final int COUNT = 100;

    @Resource
    private JobTemplate jobTemplate;

    @Resource
    private EmailQuery emailQuery;

    @Resource
    private EmailCmd emailCmd;

    /**
     * <p>
     * Processes and sends tenant-scoped emails in scheduled intervals.
     * </p>
     * <p>
     * This method runs every 10 seconds with an initial delay of 1 second.
     * It uses distributed locking with a 60-minute timeout to ensure only
     * one instance processes tenant emails at a time. Emails are processed
     * in batches for optimal performance.
     * </p>
     * <p>
     * In case of processing failures, affected emails are marked with
     * FAILURE status to prevent infinite retry loops.
     * </p>
     */
    @Scheduled(fixedDelay = 10 * 1000, initialDelay = 1000)
    public void sendTenantScopeEmail() {
        jobTemplate.execute(TENANT_EMAIL_LOCK_KEY, 60, TimeUnit.MINUTES, () -> {
            List<Email> emailInPending = null;
            try {
                emailInPending = emailQuery.findTenantEmailInPending(COUNT);
                while (isNotEmpty(emailInPending)) {
                    // Process each email in the current batch
                    for (Email email : emailInPending) {
                        emailCmd.sendByJob(email);
                    }
                    
                    // Fetch next batch only if current batch was full size
                    emailInPending = emailInPending.size() >= COUNT
                            ? emailQuery.findTenantEmailInPending(COUNT) : null;
                }
            } catch (Exception e) {
                log.error("SendTenantScopeEmail execute fail: {}", e.getMessage(), e);
                
                // Mark failed emails to prevent infinite retry
                if (isNotEmpty(emailInPending)) {
                    emailCmd.update0(emailInPending.stream()
                            .peek(email -> email.setSendStatus(ProcessStatus.FAILURE))
                            .collect(Collectors.toList()));
                }
            }
        });
    }

    /**
     * <p>
     * Processes and sends platform-scoped emails in scheduled intervals.
     * </p>
     * <p>
     * This method runs every 15 seconds with an initial delay of 5 seconds.
     * It uses distributed locking with a 120-minute timeout to ensure only
     * one instance processes platform emails at a time. The longer timeout
     * accommodates potentially larger volumes of platform-wide emails.
     * </p>
     * <p>
     * Platform emails typically have system-wide scope and may include
     * administrative notifications, system alerts, and bulk communications.
     * </p>
     */
    @Scheduled(fixedDelay = 15 * 1000, initialDelay = 5000)
    public void sendPlatformScopeEmail() {
        jobTemplate.execute(PLATFORM_EMAIL_LOCK_KEY, 120, TimeUnit.MINUTES, () -> {
            List<Email> emailInPending = null;
            try {
                emailInPending = emailQuery.findPlatformEmailInPending(COUNT);
                while (isNotEmpty(emailInPending)) {
                    // Process each email in the current batch
                    for (Email email : emailInPending) {
                        emailCmd.sendByJob(email);
                    }
                    
                    // Fixed: Use correct query method for platform emails
                    emailInPending = emailInPending.size() >= COUNT
                            ? emailQuery.findPlatformEmailInPending(COUNT) : null;
                }
            } catch (Exception e) {
                log.error("SendPlatformScopeEmail execute fail: {}", e.getMessage(), e);
                
                // Mark failed emails to prevent infinite retry
                if (isNotEmpty(emailInPending)) {
                    emailCmd.update0(emailInPending.stream()
                            .peek(email -> email.setSendStatus(ProcessStatus.FAILURE))
                            .collect(Collectors.toList()));
                }
            }
        });
    }
}
