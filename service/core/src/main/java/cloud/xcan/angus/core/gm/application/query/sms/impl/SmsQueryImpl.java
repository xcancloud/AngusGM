package cloud.xcan.angus.core.gm.application.query.sms.impl;

import cloud.xcan.angus.common.exception.ResourceNotFound;
import cloud.xcan.angus.core.gm.application.query.sms.SmsQuery;
import cloud.xcan.angus.core.gm.domain.sms.Sms;
import cloud.xcan.angus.core.gm.domain.sms.SmsRepo;
import cloud.xcan.angus.core.gm.domain.sms.SmsStatus;
import cloud.xcan.angus.core.gm.domain.sms.SmsType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SmsQueryImpl implements SmsQuery {

    private final SmsRepo smsRepo;

    @Override
    public Sms findById(Long id) {
        return smsRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFound("SMS not found"));
    }

    @Override
    public Page<Sms> findAll(SmsStatus status, SmsType type, String phone, String templateCode, Pageable pageable) {
        return smsRepo.findAll(status, type, phone, templateCode, pageable);
    }

    @Override
    public long countTotal() {
        return smsRepo.count();
    }

    @Override
    public long countByStatus(SmsStatus status) {
        return smsRepo.countByStatus(status);
    }

    @Override
    public long countByType(SmsType type) {
        return smsRepo.countByType(type);
    }
}
