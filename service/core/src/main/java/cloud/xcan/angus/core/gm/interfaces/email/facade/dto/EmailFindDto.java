package cloud.xcan.angus.core.gm.interfaces.email.facade.dto;

import lombok.Data;

@Data
public class EmailFindDto {
    private String status;
    private String type;
    private String recipient;
    private String subject;
}
