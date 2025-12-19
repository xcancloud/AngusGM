package cloud.xcan.angus.core.gm.application.cmd.sms;

import cloud.xcan.angus.core.gm.domain.sms.Sms;

public interface SmsCmd {

    Sms create(Sms sms);

    Sms update(Sms sms);

    Sms send(Sms sms);

    Sms retry(Long id);

    void cancel(Long id);

    void delete(Long id);
}
