import { SidebarGM } from './components/SidebarGM';
import { HeaderGM } from './components/HeaderGM';
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
import { SecuritySettings } from './pages/securitySettings/SecuritySettings';
import { LDAPIntegration } from './pages/ldapIntegration/LDAPIntegration';
import { ResourceQuota } from './pages/resourceQuotas/ResourceQuota';
import { AuditLogs } from './pages/auditLogs/AuditLogs';
import { OnlineUsers } from './pages/onlineUsers/OnlineUsers';
import { BackupRestore } from './pages/backupRestore/BackupRestore';
import { AppManagement } from './pages/appManagement/AppManagement';
import { AppDetail } from './pages/appManagement/AppDetail';
import { AppMenuManagement } from './pages/appManagement/AppMenuManagement';
import { ServiceManagement } from './pages/serviceManagement/ServiceManagement';
import { InterfaceManagement } from './pages/interfaceManagement/InterfaceManagement';
import { TagManagement } from './pages/tagManagement/TagManagement';
import { PermissionPolicy } from './pages/permissionPolicies/PermissionPolicy';
import { AuthorizationManagement } from './pages/viewAuthorization/AuthorizationManagement';
import { UserSettings } from './pages/userSetting/UserSettings';
import { UnderDevelopment } from './components/gm/UnderDevelopment';
import { AuthContainer } from './components/auth/AuthContainer';
import { ThemeProvider } from './components/ThemeProvider';
import { Toaster } from './components/ui/sonner';
import { useState } from 'react';

export default function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(true);
  const [activePage, setActivePage] = useState('dashboard');
  const [selectedApp, setSelectedApp] = useState<any>(null);
  const [appSubPage, setAppSubPage] = useState<'list' | 'detail' | 'menu'>('list');
  const [showSettings, setShowSettings] = useState(false);
  const [settingsTab, setSettingsTab] = useState<string | undefined>(undefined);

  // 处理登录成功
  const handleAuthSuccess = () => {
    setIsAuthenticated(true);
  };

  // 处理退出登录
  const handleLogout = () => {
    setIsAuthenticated(false);
    setActivePage('dashboard');
  };

  const getPageTitle = (page: string): string => {
    const titles: { [key: string]: string } = {
      'tenants': '租户管理',
      'users': '用户管理',
      'departments': '部门管理',
      'groups': '组管理',
      'permission-policies': '权限策略',
      'view-authorization': '授权管理',
      'notifications': '消息通知',
      'sms-messages': '短信消息',
      'email-messages': '电子邮件',
      'system-version': '系统版本',
      'security-settings': '安全设置',
      'ldap-integration': 'LDAP集成',
      'resource-quotas': '资源配额',
      'audit-logs': '审计日志',
      'online-users': '在线用户',
      'backup-restore': '备份恢复',
      'app-management': '应用管理',
      'service-management': '服务管理',
      'interface-management': '接口管理',
      'tag-management': '标签管理',
    };
    return titles[page] || page;
  };

  return (
    <ThemeProvider>
      <div className="flex h-screen bg-gray-50 dark:bg-gray-900">
        <Toaster richColors position="top-right" />
        
        {!isAuthenticated ? (
          <AuthContainer onAuthSuccess={handleAuthSuccess} />
        ) : showSettings ? (
          <UserSettings 
            onClose={() => {
              setShowSettings(false);
              setSettingsTab(undefined);
            }} 
            initialTab={settingsTab as any}
          />
        ) : (
          <>
            <SidebarGM activePage={activePage} onPageChange={setActivePage} />
            
            <div className="flex-1 flex flex-col overflow-hidden">
              <HeaderGM 
                onOpenSettings={(tab) => {
                  setSettingsTab(tab);
                  setShowSettings(true);
                }}
                onNavigate={(page) => {
                  setActivePage(page);
                }}
                onLogout={handleLogout}
              />
              
              <main className="flex-1 overflow-y-auto hide-scrollbar">
                <div className="px-7 py-6 space-y-6">
              {activePage === 'dashboard' && <Dashboard />}
              {activePage === 'users' && <UserManagement />}
              {activePage === 'tenants' && <TenantManagement />}
              {activePage === 'departments' && <DepartmentManagement />}
              {activePage === 'groups' && <GroupManagement />}
              {activePage === 'app-announcements' && <AnnouncementManagement />}
              {activePage === 'notifications' && <NotificationCenter />}
              {activePage === 'sms-messages' && <SMSMessageManagement />}
              {activePage === 'email-messages' && <EmailManagement />}
              {activePage === 'system-version' && <SystemVersion />}
              {activePage === 'security-settings' && <SecuritySettings />}
              {activePage === 'ldap-integration' && <LDAPIntegration />}
              {activePage === 'resource-quotas' && <ResourceQuota />}
              {activePage === 'audit-logs' && <AuditLogs />}
              {activePage === 'online-users' && <OnlineUsers />}
              {activePage === 'backup-restore' && <BackupRestore />}
              {activePage === 'app-management' && (
                <div>
                  {appSubPage === 'list' && (
                    <AppManagement 
                      onViewDetail={(app) => {
                        setSelectedApp(app);
                        setAppSubPage('detail');
                      }}
                      onViewMenu={(app) => {
                        setSelectedApp(app);
                        setAppSubPage('menu');
                      }}
                    />
                  )}
                  {appSubPage === 'detail' && selectedApp && (
                    <AppDetail 
                      app={selectedApp} 
                      onBack={() => setAppSubPage('list')}
                      onUpdate={(updatedApp) => {
                        setSelectedApp(updatedApp);
                      }}
                    />
                  )}
                  {appSubPage === 'menu' && selectedApp && (
                    <AppMenuManagement 
                      app={selectedApp} 
                      onBack={() => setAppSubPage('list')}
                    />
                  )}
                </div>
              )}
              {activePage === 'service-management' && (
                <ServiceManagement />
              )}
              {activePage === 'interface-management' && (
                <InterfaceManagement />
              )}
              {activePage === 'tag-management' && (
                <TagManagement />
              )}
              {activePage === 'permission-policies' && (
                <PermissionPolicy />
              )}
              {activePage === 'view-authorization' && (
                <AuthorizationManagement />
              )}
              
              {activePage !== 'dashboard' && activePage !== 'users' && activePage !== 'tenants' && activePage !== 'departments' && activePage !== 'groups' && activePage !== 'app-announcements' && activePage !== 'notifications' && activePage !== 'sms-messages' && activePage !== 'email-messages' && activePage !== 'system-version' && activePage !== 'security-settings' && activePage !== 'ldap-integration' && activePage !== 'resource-quotas' && activePage !== 'audit-logs' && activePage !== 'online-users' && activePage !== 'backup-restore' && activePage !== 'app-management' && activePage !== 'service-management' && activePage !== 'interface-management' && activePage !== 'tag-management' && activePage !== 'permission-policies' && activePage !== 'view-authorization' && (
                <UnderDevelopment 
                  title={getPageTitle(activePage)}
                  description={`${getPageTitle(activePage)}功能正在开发中，敬请期待...`}
                />
              )}
            </div>
          </main>
        </div>
          </>
        )}
      </div>
    </ThemeProvider>
  );
}