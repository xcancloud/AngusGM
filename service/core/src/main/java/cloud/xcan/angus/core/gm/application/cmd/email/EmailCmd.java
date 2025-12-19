package cloud.xcan.angus.core.gm.application.cmd.email;

import cloud.xcan.angus.core.gm.domain.email.Email;

public interface EmailCmd {
    
    Email create(Email email);
    
    Email update(Long id, Email email);
    
    void send(Long id);
    
    void retry(Long id);
    
    void cancel(Long id);
    
    void delete(Long id);
}
