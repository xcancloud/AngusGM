package cloud.xcan.angus.core.gm.interfaces.monitoring.facade.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class NetworkUsageVo {
    private List<NetworkInterface> interfaces;
    private List<NetworkHistory> history;
    
    @Data
    public static class NetworkInterface {
        private String name;
        private String ipAddress;
        private String status;
        private String bytesIn;
        private String bytesOut;
        private String currentInRate;
        private String currentOutRate;
    }
    
    @Data
    public static class NetworkHistory {
        private LocalDateTime time;
        private String inRate;
        private String outRate;
    }
}
