package cloud.xcan.angus.core.gm.interfaces.monitoring.facade.vo;

import lombok.Data;

import java.util.List;

@Data
public class DiskUsageVo {
    private List<DiskInfo> disks;
    
    @Data
    public static class DiskInfo {
        private String device;
        private String mountPoint;
        private String fileSystem;
        private String total;
        private String used;
        private String free;
        private Double usagePercent;
    }
}
