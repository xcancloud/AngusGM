package cloud.xcan.angus.core.gm.application.query.email.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.email.EmailQuery;
import cloud.xcan.angus.core.gm.domain.email.Email;
import cloud.xcan.angus.core.gm.domain.email.EmailRepo;
import cloud.xcan.angus.core.gm.domain.email.EmailStatus;
import cloud.xcan.angus.core.gm.domain.email.EmailType;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Biz
public class EmailQueryImpl implements EmailQuery {
    
    @Resource
    private EmailRepo emailRepo;
    
    @Override
    public Optional<Email> findById(Long id) {
        return new BizTemplate<Optional<Email>>() {
            @Override
            protected Optional<Email> process() {
                return emailRepo.findById(id);
            }
        }.execute();
    }

    @Override
    public Email findAndCheck(Long id) {
        return new BizTemplate<Email>() {
            @Override
            protected Email process() {
                return emailRepo.findById(id)
                    .orElseThrow(() -> ResourceNotFound.of("邮件记录未找到", new Object[]{}));
            }
        }.execute();
    }

    @Override
    public Page<Email> find(GenericSpecification<Email> spec, PageRequest pageable) {
        return new BizTemplate<Page<Email>>() {
            @Override
            protected Page<Email> process() {
                return emailRepo.findAll(spec, pageable);
            }
        }.execute();
    }
    
    @Override
    public List<Email> findAll() {
        return new BizTemplate<List<Email>>() {
            @Override
            protected List<Email> process() {
                return emailRepo.findAll();
            }
        }.execute();
    }
    
    @Override
    public List<Email> findByStatus(EmailStatus status) {
        return new BizTemplate<List<Email>>() {
            @Override
            protected List<Email> process() {
                return emailRepo.findByStatus(status);
            }
        }.execute();
    }
    
    @Override
    public List<Email> findByType(EmailType type) {
        return new BizTemplate<List<Email>>() {
            @Override
            protected List<Email> process() {
                return emailRepo.findByType(type);
            }
        }.execute();
    }
    
    @Override
    public List<Email> findByRecipient(String recipient) {
        return new BizTemplate<List<Email>>() {
            @Override
            protected List<Email> process() {
                return emailRepo.findByToRecipient(recipient);
            }
        }.execute();
    }
    
    @Override
    public List<Email> findBySubject(String subject) {
        return new BizTemplate<List<Email>>() {
            @Override
            protected List<Email> process() {
                return emailRepo.findBySubjectContaining(subject);
            }
        }.execute();
    }
    
    @Override
    public Map<String, Object> getStatistics() {
        return new BizTemplate<Map<String, Object>>() {
            @Override
            protected Map<String, Object> process() {
                Map<String, Object> stats = new HashMap<>();
                
                long total = emailRepo.count();
                stats.put("totalSent", total);
                
                long successCount = emailRepo.countByStatus(EmailStatus.SENT);
                stats.put("successCount", successCount);
                
                long failedCount = emailRepo.countByStatus(EmailStatus.FAILED);
                stats.put("failedCount", failedCount);
                
                // Today sent count
                LocalDateTime todayStart = LocalDateTime.now()
                    .withHour(0).withMinute(0).withSecond(0).withNano(0);
                // TODO: Implement countBySendTimeAfter in EmailRepo
                stats.put("todaySent", 0L);
                
                // This month sent count
                LocalDateTime firstDayOfMonth = LocalDateTime.now()
                    .with(TemporalAdjusters.firstDayOfMonth())
                    .withHour(0).withMinute(0).withSecond(0).withNano(0);
                // TODO: Implement countBySendTimeAfter in EmailRepo
                stats.put("thisMonthSent", 0L);
                
                // TODO: Calculate open rate and click rate from tracking data
                stats.put("openRate", 0.0);
                stats.put("clickRate", 0.0);
                
                return stats;
            }
        }.execute();
    }
}
