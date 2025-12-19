package cloud.xcan.angus.core.gm.interfaces.application.facade.vo;

import lombok.Data;

/**
 * Application Statistics VO
 */
@Data
public class ApplicationStatsVo {

    private Long totalApplications;

    private Long enabledApplications;

    private Long disabledApplications;

    private Long webApplications;

    private Long mobileApplications;

    private Long desktopApplications;

    private Long apiApplications;
}
