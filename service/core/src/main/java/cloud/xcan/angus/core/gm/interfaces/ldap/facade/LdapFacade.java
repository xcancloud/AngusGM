package cloud.xcan.angus.core.gm.interfaces.ldap.facade;

import cloud.xcan.angus.core.gm.interfaces.ldap.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo.*;
import cloud.xcan.angus.remote.PageResult;

import java.util.List;
import java.util.Map;

/**
 * LDAP集成门面接口
 */
public interface LdapFacade {
    
    // ==================== LDAP配置 ====================
    
    /**
     * 获取LDAP配置
     */
    LdapConfigVo getConfig();
    
    /**
     * 更新LDAP配置
     */
    LdapConfigVo updateConfig(LdapConfigUpdateDto dto);
    
    // ==================== 连接测试 ====================
    
    /**
     * 测试LDAP连接
     */
    LdapConnectionTestVo testConnection(LdapConnectionTestDto dto);
    
    /**
     * 测试LDAP认证
     */
    LdapAuthTestVo testAuth(LdapAuthTestDto dto);
    
    // ==================== 用户同步 ====================
    
    /**
     * 手动同步LDAP用户
     */
    LdapSyncResultVo syncUsers();
    
    /**
     * 获取同步历史记录
     */
    PageResult<LdapSyncHistoryVo> listSyncHistory(LdapSyncHistoryFindDto dto);
    
    /**
     * 获取同步详情
     */
    LdapSyncDetailVo getSyncDetail(String id);
    
    // ==================== LDAP用户搜索 ====================
    
    /**
     * 搜索LDAP用户
     */
    List<LdapUserVo> searchUsers(LdapUserSearchDto dto);
    
    // ==================== LDAP组管理 ====================
    
    /**
     * 获取LDAP组列表
     */
    List<LdapGroupVo> listGroups();
    
    /**
     * 获取LDAP组成员
     */
    LdapGroupMembersVo getGroupMembers(String groupDN);
    
    // ==================== 字段映射 ====================
    
    /**
     * 获取字段映射配置
     */
    Map<String, String> getFieldMapping();
    
    /**
     * 更新字段映射配置
     */
    Map<String, String> updateFieldMapping(Map<String, String> mapping);
}
