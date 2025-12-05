import { Users, Shield, Bell, Settings, ChevronDown, ChevronRight, Building2, User, Network, FolderTree, Lock, Eye, Megaphone, Mail, MessageSquare, Smartphone, ServerCog, PackageCheck, UserCheck, ShieldAlert, Calendar, Workflow, Boxes, ScrollText, Radio, LayoutDashboard, Check, AppWindow, Server, Link, Tags, Database } from 'lucide-react';
import { Button } from './ui/button';
import { Badge } from './ui/badge';
import { AngusGMLogo } from './AngusGMLogo';
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from './ui/dropdown-menu';
import { useState } from 'react';
import { toast } from 'sonner';

interface SidebarGMProps {
  activePage: string;
  onPageChange: (page: string) => void;
}

interface MenuItem {
  id: string;
  icon: any;
  label: string;
  children?: { id: string; label: string; icon?: any }[];
}

export function SidebarGM({ activePage, onPageChange }: SidebarGMProps) {
  const [expandedMenus, setExpandedMenus] = useState<string[]>([]);
  const [selectedApp, setSelectedApp] = useState('AngusGM');
  const [selectedTenant, setSelectedTenant] = useState('全局租户');
  
  const applications = [
    { id: 'angusgm', name: 'AngusGM', icon: '🔧', description: '全局管理平台' },
    { id: 'angusai', name: 'AngusAI', icon: '🤖', description: 'AI 工作平台' },
    { id: 'monitor', name: '系统监控', icon: '📊', description: '实时监控系统' },
    { id: 'analytics', name: '数据分析', icon: '📈', description: '业务分析平台' },
  ];

  const tenants = [
    { id: 'global', name: '全局租户', type: '系统级', status: '活跃', code: 'GLOBAL-SYS-001', accountType: 'main' as const },
    { id: 'techflow', name: 'TechFlow Inc', type: '企业版', status: '活跃', code: 'TECH-ENT-1024', accountType: 'sub' as const },
    { id: 'startuphub', name: 'StartupHub', type: '标准版', status: '活跃', code: 'START-STD-2048', accountType: 'sub' as const },
    { id: 'cloudnet', name: 'CloudNet Systems', type: '企业版', status: '警告', code: 'CLOUD-ENT-3072', accountType: 'sub' as const },
    { id: 'dataflow', name: 'DataFlow Pro', type: '专业版', status: '活跃', code: 'DATA-PRO-4096', accountType: 'sub' as const },
    { id: 'securenet', name: 'SecureNet', type: '企业版', status: '活跃', code: 'SECURE-ENT-5120', accountType: 'sub' as const },
  ];

  const mainMenuItems: MenuItem[] = [
    {
      id: 'dashboard',
      icon: LayoutDashboard,
      label: '系统概览',
    },
    {
      id: 'tenants',
      icon: Building2,
      label: '租户管理',
    },
    {
      id: 'organization',
      icon: Users,
      label: '组织人员',
      children: [
        { id: 'users', label: '用户', icon: User },
        { id: 'departments', label: '部门', icon: Network },
        { id: 'groups', label: '组', icon: Boxes },
      ],
    },
    {
      id: 'app-services',
      icon: AppWindow,
      label: '应用服务',
      children: [
        { id: 'app-management', label: '应用管理', icon: AppWindow },
        { id: 'service-management', label: '服务管理', icon: Server },
        { id: 'interface-management', label: '接口管理', icon: Link },
        { id: 'tag-management', label: '标签管理', icon: Tags },
      ],
    },
    {
      id: 'permissions',
      icon: Shield,
      label: '用户权限',
      children: [
        { id: 'permission-policies', label: '权限策略', icon: ShieldAlert },
        { id: 'view-authorization', label: '授权管理', icon: Eye },
      ],
    },
    {
      id: 'messages',
      icon: MessageSquare,
      label: '系统消息',
      children: [
        { id: 'notifications', label: '消息通知', icon: Bell },
        { id: 'sms-messages', label: '短信消息', icon: Smartphone },
        { id: 'email-messages', label: '电子邮件', icon: Mail },
      ],
    },
    {
      id: 'security-settings',
      icon: Lock,
      label: '安全设置',
    },
    {
      id: 'ldap-integration',
      icon: Workflow,
      label: 'LDAP集成',
    },
    {
      id: 'resource-quotas',
      icon: PackageCheck,
      label: '资源配额',
    },
    {
      id: 'audit-logs',
      icon: ScrollText,
      label: '审计日志',
    },
    {
      id: 'online-users',
      icon: UserCheck,
      label: '在线用户',
    },
    {
      id: 'backup-restore',
      icon: Database,
      label: '备份恢复',
    },
    {
      id: 'system-version',
      icon: ServerCog,
      label: '系统版本',
    },
  ];

  const toggleMenu = (menuId: string) => {
    setExpandedMenus(prev =>
      prev.includes(menuId)
        ? prev.filter(id => id !== menuId)
        : [...prev, menuId]
    );
  };

  const isMenuExpanded = (menuId: string) => expandedMenus.includes(menuId);

  const isParentActive = (menuId: string, children?: { id: string; label: string }[]) => {
    if (!children) return false;
    return children.some(child => activePage === child.id);
  };

  return (
    <aside className="w-64 bg-white dark:bg-gray-800 border-r border-gray-200 dark:border-gray-700 flex flex-col">
      {/* Logo with App Navigator - 与Header高度一致 */}
      <div className="h-[57px] px-4 border-b border-gray-200 dark:border-gray-700 flex items-center">
        <div className="flex items-center gap-2 flex-1">
          <AngusGMLogo className="w-10 h-10 flex-shrink-0" />
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <button className="flex items-center gap-2 flex-1 min-w-0 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg px-2 py-1.5 transition-colors">
                <div className="flex-1 min-w-0 text-left">
                  <div className="font-semibold dark:text-white truncate">{selectedApp}</div>
                  <div className="text-xs text-gray-500 dark:text-gray-400 truncate">全局管理平台</div>
                </div>
                <ChevronDown className="w-4 h-4 text-gray-400 flex-shrink-0" />
              </button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="start" className="w-64 dark:bg-gray-800 dark:border-gray-700">
              <div className="p-2">
                <div className="text-xs text-gray-500 dark:text-gray-400 px-2 py-1.5 mb-1">切换应用</div>
                {applications.map((app) => (
                  <DropdownMenuItem
                    key={app.id}
                    onClick={() => {
                      setSelectedApp(app.name);
                      toast.success(`已切换到 ${app.name}`);
                    }}
                    className={`flex items-center gap-3 px-2 py-2.5 cursor-pointer ${
                      selectedApp === app.name
                        ? 'bg-blue-50 dark:bg-blue-900/30'
                        : ''
                    }`}
                  >
                    <span className="text-2xl">{app.icon}</span>
                    <div className="flex-1 min-w-0">
                      <div className="flex items-center gap-2">
                        <span className="text-sm dark:text-white">{app.name}</span>
                        {selectedApp === app.name && (
                          <Check className="w-4 h-4 text-blue-500" />
                        )}
                      </div>
                      <div className="text-xs text-gray-500 dark:text-gray-400">{app.description}</div>
                    </div>
                  </DropdownMenuItem>
                ))}
              </div>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>
      </div>

      {/* Main Menu */}
      <nav className="flex-1 overflow-y-auto hide-scrollbar">
        <div className="px-2 py-4 space-y-1">
          {/* Tenant Selector */}
          <div className="mb-3 pb-3 border-b border-gray-200 dark:border-gray-700">
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <button className="w-full flex items-center gap-2 px-3 py-2 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors">
                  <Building2 className="w-4 h-4 text-blue-600 dark:text-blue-400 shrink-0" />
                  <div className="flex-1 min-w-0 text-left">
                    <div className="flex items-center gap-2">
                      <span className="text-sm dark:text-white truncate">{selectedTenant}</span>
                      <Badge className={`text-xs ${
                        tenants.find(t => t.name === selectedTenant)?.accountType === 'main'
                          ? 'bg-amber-100 text-amber-700 dark:bg-amber-900/30 dark:text-amber-400'
                          : 'bg-gray-100 text-gray-700 dark:bg-gray-700 dark:text-gray-400'
                      } border-0 shrink-0`}>
                        {tenants.find(t => t.name === selectedTenant)?.accountType === 'main' ? '主账号' : '子账号'}
                      </Badge>
                    </div>
                  </div>
                  <ChevronDown className="w-4 h-4 text-gray-400 shrink-0" />
                </button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="start" className="w-64 dark:bg-gray-800 dark:border-gray-700">
                <div className="p-2">
                  {tenants.map((tenant) => (
                    <DropdownMenuItem
                      key={tenant.id}
                      onClick={() => {
                        setSelectedTenant(tenant.name);
                        toast.success(`已切换到租户: ${tenant.name}`);
                      }}
                      className={`flex items-center gap-3 px-2 py-2 cursor-pointer ${
                        selectedTenant === tenant.name
                          ? 'bg-blue-50 dark:bg-blue-900/30'
                          : ''
                      }`}
                    >
                      <Building2 className="w-4 h-4 text-gray-400 shrink-0" />
                      <div className="flex-1 min-w-0">
                        <div className="flex items-center gap-2">
                          <span className="text-sm dark:text-white truncate">{tenant.name}</span>
                          <Badge className={`text-xs ${
                            tenant.accountType === 'main'
                              ? 'bg-amber-100 text-amber-700 dark:bg-amber-900/30 dark:text-amber-400'
                              : 'bg-gray-100 text-gray-700 dark:bg-gray-700 dark:text-gray-400'
                          } border-0 shrink-0`}>
                            {tenant.accountType === 'main' ? '主账号' : '子账号'}
                          </Badge>
                          {selectedTenant === tenant.name && (
                            <Check className="w-4 h-4 text-blue-500 shrink-0" />
                          )}
                        </div>
                        <div className="text-xs text-gray-500 dark:text-gray-400 truncate mt-0.5">
                          {tenant.code}
                        </div>
                      </div>
                    </DropdownMenuItem>
                  ))}
                </div>
              </DropdownMenuContent>
            </DropdownMenu>
          </div>

          {mainMenuItems.map((item) => (
            <div key={item.id}>
              {/* Parent Menu Item */}
              <button
                onClick={() => {
                  if (item.children) {
                    toggleMenu(item.id);
                  } else {
                    onPageChange(item.id);
                  }
                }}
                className={`w-full flex items-center gap-3 px-3 py-2 rounded-lg transition-colors ${
                  !item.children && activePage === item.id
                    ? 'bg-blue-50 dark:bg-blue-900/20 text-blue-600 dark:text-blue-400'
                    : isParentActive(item.id, item.children)
                    ? 'bg-blue-50 dark:bg-blue-900/20 text-blue-600 dark:text-blue-400'
                    : 'text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700'
                }`}
              >
                <item.icon className="w-4 h-4 shrink-0" />
                <span className="flex-1 text-left text-sm">{item.label}</span>
                {item.children && (
                  isMenuExpanded(item.id) ? (
                    <ChevronDown className="w-4 h-4 shrink-0" />
                  ) : (
                    <ChevronRight className="w-4 h-4 shrink-0" />
                  )
                )}
              </button>

              {/* Submenu Items */}
              {item.children && isMenuExpanded(item.id) && (
                <div className="ml-7 mt-1 space-y-1">
                  {item.children.map((child) => (
                    <button
                      key={child.id}
                      onClick={() => onPageChange(child.id)}
                      className={`w-full flex items-center gap-2 px-3 py-1.5 rounded-lg text-sm transition-colors ${
                        activePage === child.id
                          ? 'bg-blue-100 dark:bg-blue-900/30 text-blue-700 dark:text-blue-400'
                          : 'text-gray-600 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-700 hover:text-gray-900 dark:hover:text-gray-200'
                      }`}
                    >
                      {child.icon && <child.icon className="w-3.5 h-3.5 shrink-0" />}
                      <span>{child.label}</span>
                    </button>
                  ))}
                </div>
              )}
            </div>
          ))}
        </div>
      </nav>
    </aside>
  );
}