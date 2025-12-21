package cloud.xcan.angus.core.gm.interfaces.systemversion.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * 系统环境信息VO
 */
@Data
@Schema(description = "系统环境信息")
public class EnvironmentVo {
    
    @Schema(description = "操作系统信息")
    private OsInfo os;
    
    @Schema(description = "Java信息")
    private JavaInfo java;
    
    @Schema(description = "内存信息")
    private MemoryInfo memory;
    
    @Schema(description = "CPU信息")
    private CpuInfo cpu;
    
    @Schema(description = "磁盘信息")
    private DiskInfo disk;
    
    /**
     * 操作系统信息
     */
    @Data
    @Schema(description = "操作系统信息")
    public static class OsInfo {
        @Schema(description = "名称", example = "Linux")
        private String name;
        
        @Schema(description = "版本", example = "Ubuntu 22.04.3 LTS")
        private String version;
        
        @Schema(description = "架构", example = "x86_64")
        private String arch;
    }
    
    /**
     * Java信息
     */
    @Data
    @Schema(description = "Java信息")
    public static class JavaInfo {
        @Schema(description = "版本", example = "17.0.9")
        private String version;
        
        @Schema(description = "厂商", example = "Oracle Corporation")
        private String vendor;
        
        @Schema(description = "安装路径", example = "/usr/lib/jvm/java-17-openjdk")
        private String home;
    }
    
    /**
     * 内存信息
     */
    @Data
    @Schema(description = "内存信息")
    public static class MemoryInfo {
        @Schema(description = "总内存", example = "32 GB")
        private String total;
        
        @Schema(description = "最大可用", example = "28 GB")
        private String max;
        
        @Schema(description = "空闲内存", example = "8 GB")
        private String free;
        
        @Schema(description = "已用内存", example = "20 GB")
        private String used;
    }
    
    /**
     * CPU信息
     */
    @Data
    @Schema(description = "CPU信息")
    public static class CpuInfo {
        @Schema(description = "核心数", example = "8")
        private Integer cores;
        
        @Schema(description = "处理器数", example = "16")
        private Integer processors;
        
        @Schema(description = "型号", example = "Intel(R) Xeon(R) CPU E5-2680 v4 @ 2.40GHz")
        private String model;
    }
    
    /**
     * 磁盘信息
     */
    @Data
    @Schema(description = "磁盘信息")
    public static class DiskInfo {
        @Schema(description = "总容量", example = "2 TB")
        private String total;
        
        @Schema(description = "可用容量", example = "856 GB")
        private String free;
        
        @Schema(description = "可用空间", example = "856 GB")
        private String usable;
    }
}
