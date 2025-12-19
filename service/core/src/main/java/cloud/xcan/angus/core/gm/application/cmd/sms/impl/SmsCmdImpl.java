package cloud.xcan.angus.core.gm.application.cmd.sms.impl;

import cloud.xcan.angus.common.exception.ResourceNotFound;
import cloud.xcan.angus.common.template.BizTemplate;
import cloud.xcan.angus.core.gm.application.cmd.sms.SmsCmd;
import cloud.xcan.angus.core.gm.domain.sms.Sms;
import cloud.xcan.angus.core.gm.domain.sms.SmsRepo;
import cloud.xcan.angus.core.gm.domain.sms.SmsStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SmsCmdImpl implements SmsCmd {

    private final SmsRepo smsRepo;
    private final BizTemplate bizTemplate;

    @Override
    @Transactional
    public Sms create(Sms sms) {
        return bizTemplate.execute(() -> {
            if (sms.getStatus() == null) {
                sms.setStatus(SmsStatus.PENDING);
            }
            if (sms.getRetryCount() == null) {
                sms.setRetryCount(0);
            }
            if (sms.getMaxRetry() == null) {
                sms.setMaxRetry(3);
            }
            return smsRepo.save(sms);
        });
    }

    @Override
    @Transactional
    public Sms update(Sms sms) {
        return bizTemplate.execute(() -> {
            Sms existing = smsRepo.findById(sms.getId())
                    .orElseThrow(() -> new ResourceNotFound("SMS not found"));
            existing.setPhone(sms.getPhone());
            existing.setContent(sms.getContent());
            existing.setTemplateCode(sms.getTemplateCode());
            existing.setTemplateParams(sms.getTemplateParams());
            existing.setType(sms.getType());
            existing.setProvider(sms.getProvider());
            return smsRepo.save(existing);
        });
    }

    @Override
    @Transactional
    public Sms send(Sms sms) {
        return bizTemplate.execute(() -> {
            sms.setStatus(SmsStatus.SENDING);
            sms.setSendTime(LocalDateTime.now());
            return smsRepo.save(sms);
        });
    }

    @Override
    @Transactional
    public Sms retry(Long id) {
        return bizTemplate.execute(() -> {
            Sms sms = smsRepo.findById(id)
                    .orElseThrow(() -> new ResourceNotFound("SMS not found"));
            if (sms.getRetryCount() >= sms.getMaxRetry()) {
                throw new IllegalStateException("Max retry count reached");
            }
            sms.setRetryCount(sms.getRetryCount() + 1);
            sms.setStatus(SmsStatus.PENDING);
            sms.setErrorCode(null);
            sms.setErrorMessage(null);
            return smsRepo.save(sms);
        });
    }

    @Override
    @Transactional
    public void cancel(Long id) {
        bizTemplate.execute(() -> {
            Sms sms = smsRepo.findById(id)
                    .orElseThrow(() -> new ResourceNotFound("SMS not found"));
            if (sms.getStatus() != SmsStatus.PENDING && sms.getStatus() != SmsStatus.SENDING) {
                throw new IllegalStateException("Cannot cancel SMS in current status");
            }
            sms.setStatus(SmsStatus.CANCELLED);
            smsRepo.save(sms);
            return null;
        });
    }

    @Override
    @Transactional
    public void delete(Long id) {
        bizTemplate.execute(() -> {
            if (!smsRepo.existsById(id)) {
                throw new ResourceNotFound("SMS not found");
            }
            smsRepo.deleteById(id);
            return null;
        });
    }
}
