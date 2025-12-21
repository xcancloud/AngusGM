package cloud.xcan.angus.core.gm.interfaces.email.facade.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EmailSmtpTestVo {
    private Boolean connected;
    private LocalDateTime testTime;
    private String message;
}
