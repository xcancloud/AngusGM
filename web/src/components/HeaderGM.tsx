import { Bell, HelpCircle, Languages, Sun, Moon, User, Shield, Key, MessageSquare, Copy, Check, LogOut, FileText, AlertCircle, CheckCircle, Info, ExternalLink, BookOpen, Video, FileQuestion, Database, Lock, Mail, Palette, Eye, KeyRound, ChevronRight } from 'lucide-react';
import { Button } from './ui/button';
import { Avatar, AvatarFallback, AvatarImage } from './ui/avatar';
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from './ui/dropdown-menu';
import { Popover, PopoverContent, PopoverTrigger } from './ui/popover';
import { Separator } from './ui/separator';
import { Badge } from './ui/badge';
import { useState } from 'react';
import { useTheme } from './ThemeProvider';
import { toast } from 'sonner';

interface HeaderGMProps {
  onOpenSettings?: (tab?: string) => void;
  onNavigate?: (page: string) => void;
  onLogout?: () => void;
}

export function HeaderGM({ onOpenSettings, onNavigate, onLogout }: HeaderGMProps = {}) {
  const [language, setLanguage] = useState<'zh' | 'en'>('zh');
  const { theme, setTheme } = useTheme();
  const [copied, setCopied] = useState(false);
  const [userPopoverOpen, setUserPopoverOpen] = useState(false);
  const [notificationOpen, setNotificationOpen] = useState(false);
  const [helpOpen, setHelpOpen] = useState(false);

  const userInfo = {
    name: 'Alex',
    id: 'ADMIN001',
    avatar: 'https://images.unsplash.com/photo-1652795385761-7ac287d0cd03?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxwcm9mZXNzaW9uYWwlMjBhdmF0YXIlMjBjYXJ0b29ufGVufDF8fHx8MTc2MTEwMTExNXww&ixlib=rb-4.1.0&q=80&w=1080',
    verified: true,
    role: '超级管理员',
  };

  // 获取时间问候语
  const getTimeGreeting = () => {
    const hour = new Date().getHours();
    if (hour >= 5 && hour < 12) return '早上好';
    if (hour >= 12 && hour < 18) return '下午好';
    return '晚上好';
  };

  // 获取格式化日期
  const getFormattedDate = () => {
    const now = new Date();
    const year = now.getFullYear();
    const month = now.getMonth() + 1;
    const day = now.getDate();
    const weekDays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];
    const weekDay = weekDays[now.getDay()];
    return `${year}年${month}月${day}日${weekDay}`;
  };

  const notifications = [
    {
      id: 1,
      type: 'warning',
      icon: AlertCircle,
      title: '系统登录异常',
      description: '检测到IP地址 192.168.1.100 尝试多次登录失败',
      time: '5分钟前',
      read: false,
      page: 'notifications',
    },
    {
      id: 2,
      type: 'success',
      icon: CheckCircle,
      title: '用户审批完成',
      description: '新用户"张三"的账号已通过审核并激活',
      time: '1小时前',
      read: false,
      page: 'notifications',
    },
    {
      id: 3,
      type: 'info',
      icon: Info,
      title: '系统维护通知',
      description: '系统将于今晚22:00进行例行维护，预计1小时',
      time: '3小时前',
      read: true,
      page: 'notifications',
    },
    {
      id: 4,
      type: 'warning',
      icon: AlertCircle,
      title: '资源配额预警',
      description: '租户"某某科技"的存储空间使用已达90%',
      time: '1天前',
      read: true,
      page: 'notifications',
    },
    {
      id: 5,
      type: 'info',
      icon: Info,
      title: 'LDAP同步完成',
      description: 'LDAP用户同步已完成，新增32个用户',
      time: '2天前',
      read: true,
      page: 'notifications',
    },
  ];

  const helpItems = [
    {
      icon: BookOpen,
      title: '管理员指南',
      description: '了解系统管理的最佳实践',
      link: '#',
    },
    {
      icon: Video,
      title: '功能演示',
      description: '观看系统功能的详细演示',
      link: '#',
    },
    {
      icon: FileText,
      title: 'API文档',
      description: '查看系统集成API文档',
      link: '#',
    },
    {
      icon: FileQuestion,
      title: '常见问题',
      description: '管理员常见问题解答',
      link: '#',
    },
    {
      icon: MessageSquare,
      title: '技术支持',
      description: '联系技术支持团队',
      link: '#',
    },
  ];



  const handleCopyId = () => {
    navigator.clipboard.writeText(userInfo.id);
    setCopied(true);
    toast.success('ID 已复制到剪贴板');
    setTimeout(() => setCopied(false), 2000);
  };

  const handleLogout = () => {
    toast.success('已退出登录');
    setUserPopoverOpen(false);
    // 调用传入的登出回调函数，通常会跳转到登录页
    onLogout?.();
  };

  const handleNotificationClick = (notification: any) => {
    if (notification.page && onNavigate) {
      onNavigate(notification.page);
      setNotificationOpen(false);
      toast.success(`正在跳转到${notification.title}相关页面`);
    } else {
      toast.success('已标记为已读');
    }
  };

  const handleClearAll = () => {
    toast.success('已清空所有通知');
    setNotificationOpen(false);
  };

  const getNotificationColor = (type: string) => {
    switch (type) {
      case 'success':
        return 'text-green-500';
      case 'warning':
        return 'text-yellow-500';
      case 'info':
        return 'text-blue-500';
      default:
        return 'text-gray-500';
    }
  };

  const getTypeIcon = (type: string) => {
    switch (type) {
      case 'users':
        return Users;
      case 'tenants':
        return Database;
      case 'permissions':
        return Lock;
      case 'security':
        return Shield;
      default:
        return Settings;
    }
  };



  return (
    <header className="h-[57px] bg-white dark:bg-gray-800 border-b border-gray-200 dark:border-gray-700 px-6 flex items-center">
      <div className="flex items-center justify-between w-full">
        {/* Welcome Message */}
        <div className="flex items-center gap-2 text-sm">
          <span className="text-gray-600 dark:text-gray-300">{getTimeGreeting()}，</span>
          <span className="text-blue-600 dark:text-blue-400">{userInfo.name}</span>
          <span>👋</span>
          <span className="text-gray-400 dark:text-gray-500 ml-2">今天是 {getFormattedDate()}</span>
        </div>

        {/* Right Section */}
        <div className="flex items-center gap-4">
          {/* Notification */}
          <Popover open={notificationOpen} onOpenChange={setNotificationOpen}>
            <PopoverTrigger asChild>
              <button className="relative p-2 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg">
                <Bell className="w-5 h-5 text-gray-600 dark:text-gray-300" />
                {notifications.filter(n => !n.read).length > 0 && (
                  <span className="absolute top-1 right-1 w-2 h-2 bg-red-500 rounded-full"></span>
                )}
              </button>
            </PopoverTrigger>
            <PopoverContent className="w-96 p-0 dark:bg-gray-800 dark:border-gray-700" align="end">
              {/* Header */}
              <div className="p-4 border-b dark:border-gray-700 flex items-center justify-between">
                <div>
                  <h3 className="dark:text-white">系统通知</h3>
                  {notifications.filter(n => !n.read).length > 0 && (
                    <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
                      {notifications.filter(n => !n.read).length} 条未读通知
                    </p>
                  )}
                </div>
                <button
                  onClick={handleClearAll}
                  className="text-sm text-blue-500 hover:text-blue-600 dark:text-blue-400 dark:hover:text-blue-300"
                >
                  清空全部
                </button>
              </div>

              {/* Notifications List */}
              <div className="max-h-96 overflow-y-auto">
                {notifications.map((notification) => {
                  const Icon = notification.icon;
                  return (
                    <div
                      key={notification.id}
                      onClick={() => handleNotificationClick(notification)}
                      className={`p-4 border-b dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-700 cursor-pointer transition-colors ${
                        !notification.read ? 'bg-blue-50/50 dark:bg-blue-900/10' : ''
                      }`}
                    >
                      <div className="flex gap-3">
                        <div className={`flex-shrink-0 ${getNotificationColor(notification.type)}`}>
                          <Icon className="w-5 h-5" />
                        </div>
                        <div className="flex-1 min-w-0">
                          <div className="flex items-start justify-between gap-2 mb-1">
                            <span className="dark:text-white">{notification.title}</span>
                            {!notification.read && (
                              <span className="w-2 h-2 bg-blue-500 rounded-full flex-shrink-0 mt-2"></span>
                            )}
                          </div>
                          <p className="text-sm text-gray-600 dark:text-gray-400 mb-2">
                            {notification.description}
                          </p>
                          <span className="text-xs text-gray-500 dark:text-gray-500">
                            {notification.time}
                          </span>
                        </div>
                      </div>
                    </div>
                  );
                })}
              </div>

              {/* Footer */}
              <div className="p-3 border-t dark:border-gray-700 text-center">
                <button className="text-sm text-blue-500 hover:text-blue-600 dark:text-blue-400 dark:hover:text-blue-300">
                  查看全部通知
                </button>
              </div>
            </PopoverContent>
          </Popover>

          {/* Help */}
          <Popover open={helpOpen} onOpenChange={setHelpOpen}>
            <PopoverTrigger asChild>
              <button className="p-2 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg">
                <HelpCircle className="w-5 h-5 text-gray-600 dark:text-gray-300" />
              </button>
            </PopoverTrigger>
            <PopoverContent className="w-80 p-0 dark:bg-gray-800 dark:border-gray-700" align="end">
              {/* Header */}
              <div className="p-4 border-b dark:border-gray-700">
                <h3 className="dark:text-white">帮助中心</h3>
                <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
                  管理员支持文档
                </p>
              </div>

              {/* Help Items */}
              <div className="p-2">
                {helpItems.map((item, index) => {
                  const Icon = item.icon;
                  return (
                    <a
                      key={index}
                      href={item.link}
                      className="flex items-start gap-3 p-3 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors group"
                      onClick={(e) => {
                        e.preventDefault();
                        toast.success(`正在打开 ${item.title}`);
                      }}
                    >
                      <div className="flex-shrink-0 text-blue-500 group-hover:text-blue-600 dark:text-blue-400 dark:group-hover:text-blue-300">
                        <Icon className="w-5 h-5" />
                      </div>
                      <div className="flex-1 min-w-0">
                        <div className="flex items-center gap-2 mb-1">
                          <span className="dark:text-white group-hover:text-blue-600 dark:group-hover:text-blue-400 transition-colors">
                            {item.title}
                          </span>
                          <ExternalLink className="w-3 h-3 text-gray-400 opacity-0 group-hover:opacity-100 transition-opacity" />
                        </div>
                        <p className="text-sm text-gray-600 dark:text-gray-400">
                          {item.description}
                        </p>
                      </div>
                    </a>
                  );
                })}
              </div>

              {/* Footer */}
              <div className="p-4 border-t dark:border-gray-700 bg-gray-50 dark:bg-gray-750">
                <div className="text-sm text-gray-600 dark:text-gray-400 mb-2">
                  需要技术支持？
                </div>
                <div className="flex gap-2">
                  <button className="flex-1 px-3 py-2 text-sm bg-white dark:bg-gray-700 border border-gray-200 dark:border-gray-600 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-600 transition-colors dark:text-gray-300">
                    在线客服
                  </button>
                  <button className="flex-1 px-3 py-2 text-sm bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors">
                    提交工单
                  </button>
                </div>
              </div>
            </PopoverContent>
          </Popover>

          {/* Language Switch */}
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <button className="flex items-center gap-2 p-2 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg">
                <Languages className="w-5 h-5 text-gray-600 dark:text-gray-300" />
                <span className="text-sm text-gray-600 dark:text-gray-300">{language === 'zh' ? '中文' : 'EN'}</span>
              </button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end">
              <DropdownMenuItem onClick={() => setLanguage('zh')} className={language === 'zh' ? 'bg-blue-50 dark:bg-blue-900' : ''}>
                中文
              </DropdownMenuItem>
              <DropdownMenuItem onClick={() => setLanguage('en')} className={language === 'en' ? 'bg-blue-50 dark:bg-blue-900' : ''}>
                English
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>

          {/* Theme Switch */}
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <button className="flex items-center gap-2 p-2 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg">
                {theme === 'light' ? (
                  <Sun className="w-5 h-5 text-gray-600 dark:text-gray-300" />
                ) : (
                  <Moon className="w-5 h-5 text-gray-600 dark:text-gray-300" />
                )}
              </button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end">
              <DropdownMenuItem onClick={() => setTheme('light')} className={theme === 'light' ? 'bg-blue-50 dark:bg-blue-900' : ''}>
                <Sun className="w-4 h-4 mr-2" />
                浅色模式
              </DropdownMenuItem>
              <DropdownMenuItem onClick={() => setTheme('dark')} className={theme === 'dark' ? 'bg-blue-50 dark:bg-blue-900' : ''}>
                <Moon className="w-4 h-4 mr-2" />
                深色模式
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>

          {/* User */}
          <Popover open={userPopoverOpen} onOpenChange={setUserPopoverOpen}>
            <PopoverTrigger asChild>
              <button className="p-1 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors">
                <Avatar className="w-8 h-8 ring-2 ring-blue-500 ring-offset-2 dark:ring-offset-gray-800">
                  <AvatarImage src={userInfo.avatar} alt={userInfo.name} />
                  <AvatarFallback className="bg-blue-500 text-white">
                    {userInfo.name.slice(0, 2)}
                  </AvatarFallback>
                </Avatar>
              </button>
            </PopoverTrigger>
            <PopoverContent className="w-64 p-0 dark:bg-gray-800 dark:border-gray-700" align="end">
              {/* Header */}
              <div className="p-4 border-b dark:border-gray-700">
                <h3 className="text-sm text-gray-500 dark:text-gray-400 mb-3">管理员账户</h3>
                <div className="flex items-start gap-3">
                  <Avatar className="w-12 h-12 ring-2 ring-blue-500">
                    <AvatarImage src={userInfo.avatar} alt={userInfo.name} />
                    <AvatarFallback className="bg-blue-500 text-white">
                      {userInfo.name.slice(0, 2)}
                    </AvatarFallback>
                  </Avatar>
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-2 mb-1">
                      <span className="dark:text-white truncate">{userInfo.name}</span>
                    </div>
                    <div className="flex items-center gap-2 text-sm text-gray-500 dark:text-gray-400 mb-1">
                      <span className="truncate">ID: {userInfo.id}</span>
                      <button
                        onClick={handleCopyId}
                        className="p-1 hover:bg-gray-100 dark:hover:bg-gray-700 rounded transition-colors shrink-0"
                      >
                        {copied ? (
                          <Check className="w-3.5 h-3.5 text-blue-600" />
                        ) : (
                          <Copy className="w-3.5 h-3.5" />
                        )}
                      </button>
                    </div>
                    <div className="flex items-center gap-1 text-xs">
                      <Badge className="bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400 border-0 px-1.5 py-0">
                        {userInfo.role}
                      </Badge>
                    </div>
                  </div>
                </div>
              </div>

              {/* Quick Access Menu */}
              <div className="py-1">
                <div className="px-3 py-2">
                  <p className="text-xs text-gray-500 dark:text-gray-400">快速访问</p>
                </div>
                <button 
                  onClick={() => {
                    onOpenSettings?.('profile');
                    setUserPopoverOpen(false);
                  }}
                  className="w-full flex items-center justify-between gap-3 px-4 py-2 text-sm hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors text-left dark:text-gray-300"
                >
                  <div className="flex items-center gap-3">
                    <User className="w-4 h-4" />
                    <span>个人信息</span>
                  </div>
                  <ChevronRight className="w-3.5 h-3.5 text-gray-400" />
                </button>
                <button 
                  onClick={() => {
                    onOpenSettings?.('security');
                    setUserPopoverOpen(false);
                  }}
                  className="w-full flex items-center justify-between gap-3 px-4 py-2 text-sm hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors text-left dark:text-gray-300"
                >
                  <div className="flex items-center gap-3">
                    <Shield className="w-4 h-4" />
                    <span>账号安全</span>
                  </div>
                  <ChevronRight className="w-3.5 h-3.5 text-gray-400" />
                </button>
                <button 
                  onClick={() => {
                    onOpenSettings?.('notifications');
                    setUserPopoverOpen(false);
                  }}
                  className="w-full flex items-center justify-between gap-3 px-4 py-2 text-sm hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors text-left dark:text-gray-300"
                >
                  <div className="flex items-center gap-3">
                    <Bell className="w-4 h-4" />
                    <span>通知设置</span>
                  </div>
                  <ChevronRight className="w-3.5 h-3.5 text-gray-400" />
                </button>
                <button 
                  onClick={() => {
                    onOpenSettings?.('appearance');
                    setUserPopoverOpen(false);
                  }}
                  className="w-full flex items-center justify-between gap-3 px-4 py-2 text-sm hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors text-left dark:text-gray-300"
                >
                  <div className="flex items-center gap-3">
                    <Palette className="w-4 h-4" />
                    <span>外观设置</span>
                  </div>
                  <ChevronRight className="w-3.5 h-3.5 text-gray-400" />
                </button>
                <button 
                  onClick={() => {
                    onOpenSettings?.('privacy');
                    setUserPopoverOpen(false);
                  }}
                  className="w-full flex items-center justify-between gap-3 px-4 py-2 text-sm hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors text-left dark:text-gray-300"
                >
                  <div className="flex items-center gap-3">
                    <Eye className="w-4 h-4" />
                    <span>隐私设置</span>
                  </div>
                  <ChevronRight className="w-3.5 h-3.5 text-gray-400" />
                </button>
                <button 
                  onClick={() => {
                    onOpenSettings?.('tokens');
                    setUserPopoverOpen(false);
                  }}
                  className="w-full flex items-center justify-between gap-3 px-4 py-2 text-sm hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors text-left dark:text-gray-300"
                >
                  <div className="flex items-center gap-3">
                    <KeyRound className="w-4 h-4" />
                    <span>用户令牌</span>
                  </div>
                  <ChevronRight className="w-3.5 h-3.5 text-gray-400" />
                </button>
              </div>

              <Separator className="dark:bg-gray-700" />

              {/* Logout */}
              <div className="p-2">
                <button
                  onClick={handleLogout}
                  className="w-full flex items-center justify-center gap-2 px-4 py-2 text-sm text-gray-600 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-700 rounded transition-colors"
                >
                  <LogOut className="w-4 h-4" />
                  <span>退出登录</span>
                </button>
              </div>
            </PopoverContent>
          </Popover>
        </div>
      </div>
    </header>
  );
}