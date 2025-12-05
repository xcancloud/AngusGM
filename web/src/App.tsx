import { Routes, Route, Navigate } from 'react-router-dom';
import { useState } from 'react';
import React from 'react';
import { Layout } from '@/components/layout';
import { Dashboard } from '@/pages/dashboard/Dashboard';
import { UserManagement } from '@/pages/users/UserManagement';
import { TenantManagement } from '@/pages/tenants/TenantManagement';
import { DepartmentManagement } from '@/pages/departments/DepartmentManagement';
import { GroupManagement } from '@/pages/groups/GroupManagement';
import { AnnouncementManagement } from '@/pages/appAnnouncements/AnnouncementManagement';
import { NotificationCenter } from '@/pages/notifications/NotificationCenter';
import { SMSMessageManagement } from '@/pages/smsMessages/SMSMessageManagement';
import { EmailManagement } from '@/pages/emailMessages/EmailManagement';
import { SystemVersion } from '@/pages/systemVersion/SystemVersion';
import { SecuritySettings } from '@/pages/securitySettings/SecuritySettings';
import { LDAPIntegration } from '@/pages/ldapIntegration/LDAPIntegration';
import { ResourceQuota } from '@/pages/resourceQuotas/ResourceQuota';
import { AuditLogs } from '@/pages/auditLogs/AuditLogs';
import { OnlineUsers } from '@/pages/onlineUsers/OnlineUsers';
import { BackupRestore } from '@/pages/backupRestore/BackupRestore';
import { AppManagement } from '@/pages/appManagement/AppManagement';
import { AppDetailWrapper } from '@/pages/appManagement/AppDetailWrapper';
import { AppMenuManagementWrapper } from '@/pages/appManagement/AppMenuManagementWrapper';
import { ServiceManagement } from '@/pages/serviceManagement/ServiceManagement';
import { InterfaceManagement } from '@/pages/interfaceManagement/InterfaceManagement';
import { TagManagement } from '@/pages/tagManagement/TagManagement';
import { PermissionPolicy } from '@/pages/permissionPolicies/PermissionPolicy';
import { AuthorizationManagement } from '@/pages/viewAuthorization/AuthorizationManagement';
import { UnderDevelopment } from '@/components/gm/UnderDevelopment';
import { AuthLayout } from '@/components/auth/AuthLayout';
import { LoginWrapper } from '@/pages/login/LoginWrapper';
import { RegisterWrapper } from '@/pages/signUp/RegisterWrapper';
import { ForgotPasswordWrapper } from '@/pages/resetPassword/ForgotPasswordWrapper';
import { PrivacyPolicy } from '@/pages/privacyPolicy/PrivacyPolicy';
import { UserAgreement } from '@/pages/privacyPolicy/UserAgreement';

// 受保护的路由组件
function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const [isAuthenticated] = useState(true); // TODO: 从认证状态管理获取
  
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }
  
  return <React.Fragment>{children}</React.Fragment>;
}

function App() {
  return (
    <Routes>
      {/* 根路径重定向 */}
      <Route path="/" element={<Navigate to="/dashboard" replace />} />
      
      {/* 认证路由 */}
      <Route element={<AuthLayout />}>
        <Route path="login" element={<LoginWrapper />} />
        <Route path="register" element={<RegisterWrapper />} />
        <Route path="forgot-password" element={<ForgotPasswordWrapper />} />
        <Route path="privacy-policy" element={<PrivacyPolicy />} />
        <Route path="user-agreement" element={<UserAgreement />} />
      </Route>
      
      {/* 主应用路由 */}
      <Route
        path="/*"
        element={
          <ProtectedRoute children={<Layout />} />
        }
      >
        <Route index element={<Navigate to="/dashboard" replace />} />
        <Route path="dashboard" element={<Dashboard />} />
        <Route path="users" element={<UserManagement />} />
        <Route path="tenants" element={<TenantManagement />} />
        <Route path="departments" element={<DepartmentManagement />} />
        <Route path="groups" element={<GroupManagement />} />
        <Route path="app-announcements" element={<AnnouncementManagement />} />
        <Route path="notifications" element={<NotificationCenter />} />
        <Route path="sms-messages" element={<SMSMessageManagement />} />
        <Route path="email-messages" element={<EmailManagement />} />
        <Route path="system-version" element={<SystemVersion />} />
        <Route path="security-settings" element={<SecuritySettings />} />
        <Route path="ldap-integration" element={<LDAPIntegration />} />
        <Route path="resource-quotas" element={<ResourceQuota />} />
        <Route path="audit-logs" element={<AuditLogs />} />
        <Route path="online-users" element={<OnlineUsers />} />
        <Route path="backup-restore" element={<BackupRestore />} />
        <Route path="app-management" element={<AppManagement />} />
        <Route path="app-management/:appId" element={<AppDetailWrapper />} />
        <Route path="app-management/:appId/menu" element={<AppMenuManagementWrapper />} />
        <Route path="service-management" element={<ServiceManagement />} />
        <Route path="interface-management" element={<InterfaceManagement />} />
        <Route path="tag-management" element={<TagManagement />} />
        <Route path="permission-policies" element={<PermissionPolicy />} />
        <Route path="view-authorization" element={<AuthorizationManagement />} />
        <Route path="*" element={<UnderDevelopment title="页面未找到" description="您访问的页面不存在" />} />
      </Route>
    </Routes>
  );
}

export default App;