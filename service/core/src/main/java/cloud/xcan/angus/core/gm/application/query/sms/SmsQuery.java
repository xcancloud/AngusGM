package cloud.xcan.angus.core.gm.application.query.sms;

import cloud.xcan.angus.core.gm.domain.sms.Sms;
import cloud.xcan.angus.core.gm.domain.sms.SmsStatus;
import cloud.xcan.angus.core.gm.domain.sms.SmsType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SmsQuery {

    Sms findById(Long id);

    Page<Sms> findAll(SmsStatus status, SmsType type, String phone, String templateCode, Pageable pageable);

    long countTotal();

    long countByStatus(SmsStatus status);

    long countByType(SmsType type);
}
