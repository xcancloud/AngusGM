package cloud.xcan.angus.core.gm.interfaces.backup.facade.dto;

import lombok.Data;

@Data
public class BackupRestoreDto {
    private Boolean restoreDatabase;
    private Boolean restoreFiles;
    private Boolean restoreConfigurations;
    private Boolean confirmOverwrite;
}