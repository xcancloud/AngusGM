package cloud.xcan.angus.core.gm.interfaces.systemversion.facade;

import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.dto.ChangelogFindDto;
import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.dto.LicenseUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.dto.VersionHistoryFindDto;
import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.vo.*;
import cloud.xcan.angus.remote.PageResult;

/**
 * 系统版本Facade接口
 */
public interface SystemVersionFacade {
    
    // ==================== 版本信息 ====================
    
    /**
     * 获取当前系统版本信息
     */
    CurrentVersionVo getCurrentVersion();
    
    /**
     * 获取版本历史列表
     */
    PageResult<VersionHistoryVo> listVersionHistory(VersionHistoryFindDto dto);
    
    /**
     * 获取版本详情
     */
    VersionDetailVo getVersionDetail(String id);
    
    // ==================== 变更日志 ====================
    
    /**
     * 获取变更日志
     */
    PageResult<ChangelogVo> getChangelog(ChangelogFindDto dto);
    
    // ==================== 更新检查 ====================
    
    /**
     * 检查更新
     */
    UpdateCheckVo checkUpdate();
    
    // ==================== 系统信息 ====================
    
    /**
     * 获取系统依赖信息
     */
    DependenciesVo getDependencies();
    
    /**
     * 获取系统许可证信息
     */
    LicenseVo getLicense();
    
    /**
     * 更新许可证
     */
    LicenseVo updateLicense(LicenseUpdateDto dto);
    
    /**
     * 获取系统环境信息
     */
    EnvironmentVo getEnvironment();
    
    // ==================== 版本对比 ====================
    
    /**
     * 获取版本对比
     */
    VersionCompareVo compareVersions(String fromVersion, String toVersion);
}
