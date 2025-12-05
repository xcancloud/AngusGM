import { useState } from 'react';
import { 
  User, Shield, Bell, Palette, Eye,
  Upload, Trash2, Github, Twitter, Linkedin, Monitor, Sun, Moon, 
  Globe, ChevronRight, ArrowLeft, Key, Fingerprint, KeyRound, Copy, Plus, MoreVertical, Clock as ClockIcon, Search, EyeOff, AlertCircle
} from 'lucide-react';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';  
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import { Switch } from '@/components/ui/switch';
import { Separator } from '@/components/ui/separator';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group';
import { Slider } from '@/components/ui/slider';
import { toast } from 'sonner';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { Checkbox } from '@/components/ui/checkbox';

type SettingsTab = 'profile' | 'security' | 'notifications' | 'appearance' | 'privacy' | 'tokens';

interface UserSettingsProps {
  onClose?: () => void;
  initialTab?: SettingsTab;
}

export function UserSettings({ onClose, initialTab }: UserSettingsProps = {}) {
  const [activeTab, setActiveTab] = useState<SettingsTab>(initialTab || 'profile');
  
  // Security states
  const [authenticatorEnabled, setAuthenticatorEnabled] = useState(false);

  // Token states
  const [showCreateTokenDialog, setShowCreateTokenDialog] = useState(false);
  const [showTokenDialog, setShowTokenDialog] = useState(false);
  const [showRevokeDialog, setShowRevokeDialog] = useState(false);
  const [showDeleteDialog, setShowDeleteDialog] = useState(false);
  const [selectedTokenId, setSelectedTokenId] = useState<string | null>(null);
  const [newTokenName, setNewTokenName] = useState('');
  const [newTokenApp, setNewTokenApp] = useState<string>('');
  const [appPermissions, setAppPermissions] = useState<string[]>([]);
  const [tokenQuota] = useState({ used: 4, total: 10 });
  const [visibleTokens, setVisibleTokens] = useState<Record<string, boolean>>({});
  const [createdTokenValue, setCreatedTokenValue] = useState('');
  const [newTokenDescription, setNewTokenDescription] = useState('');
  const [newTokenExpires, setNewTokenExpires] = useState('365');
  
  // Notification states
  const [emailNotifications, setEmailNotifications] = useState({
    comments: true,
    mentions: true,
    updates: false,
    productNews: true,
  });
  const [pushNotifications, setPushNotifications] = useState({
    comments: true,
    mentions: true,
    updates: false,
  });
  const [desktopNotifications, setDesktopNotifications] = useState(true);
  const [notificationSound, setNotificationSound] = useState(false);
  
  // Appearance states
  const [theme, setTheme] = useState('light');
  const [language, setLanguage] = useState('zh-CN');
  const [fontSize, setFontSize] = useState([14]);
  
  // Privacy states
  const [profileVisibility, setProfileVisibility] = useState('public');
  const [activityVisibility, setActivityVisibility] = useState('team');
  const [emailVisibility, setEmailVisibility] = useState('private');
  const [showOnlineStatus, setShowOnlineStatus] = useState(true);
  const [allowSearchEngineIndexing, setAllowSearchEngineIndexing] = useState(false);

  const menuItems = [
    { id: 'profile' as SettingsTab, icon: User, label: '个人信息' },
    { id: 'security' as SettingsTab, icon: Shield, label: '账号安全' },
    { id: 'notifications' as SettingsTab, icon: Bell, label: '通知设置' },
    { id: 'appearance' as SettingsTab, icon: Palette, label: '外观设置' },
    { id: 'privacy' as SettingsTab, icon: Eye, label: '隐私设置' },
    { id: 'tokens' as SettingsTab, icon: KeyRound, label: '用户令牌' },
  ];

  // Token data
  const [userTokens, setUserTokens] = useState([
    {
      id: '1',
      name: 'API访问令牌',
      app: 'AngusGM',
      appIcon: 'G',
      token: 'agm_1a2b3c4d5e6f7g8h9i0j',
      createdAt: '2024-11-20',
      lastUsed: '2小时前',
      status: 'active' as const,
      authorizedApps: [
        { name: 'AngusGM', permissions: ['管理', '读权限', '写权限'] },
      ],
      scopes: ['AngusGM'],
    },
    {
      id: '2',
      name: '数据同步令牌',
      app: 'DataSync Pro',
      appIcon: 'D',
      token: 'dsp_9z8y7x6w5v4u3t2s1r0q',
      createdAt: '2024-11-15',
      lastUsed: '1天前',
      status: 'active' as const,
      authorizedApps: [
        { name: 'DataSync Pro', permissions: ['读权限', '写权限'] },
      ],
      scopes: ['DataSync Pro'],
    },
    {
      id: '3',
      name: 'CI/CD部署令牌',
      app: 'Jenkins',
      appIcon: 'J',
      token: 'jnk_p1o2n3m4l5k6j7h8g9f0',
      createdAt: '2024-10-28',
      lastUsed: '未使用',
      status: 'active' as const,
      authorizedApps: [
        { name: 'Jenkins', permissions: ['管理', '读权限', '写权限'] },
      ],
      scopes: ['Jenkins'],
    },
    {
      id: '4',
      name: '测试环境令牌',
      app: 'AngusGM',
      appIcon: 'G',
      token: 'agm_e5d4c3b2a1z9y8x7w6v5',
      createdAt: '2024-09-10',
      lastUsed: '30天前',
      status: 'expired' as const,
      authorizedApps: [
        { name: 'AngusGM', permissions: ['读权限'] },
      ],
      scopes: ['AngusGM'],
    },
  ]);

  const availableApps = [
    { id: 'AngusGM', name: 'AngusGM', icon: 'G', description: '全局管理平台' },
    { id: 'DataSync', name: 'DataSync Pro', icon: 'D', description: '数据同步服务' },
    { id: 'Jenkins', name: 'Jenkins', icon: 'J', description: 'CI/CD平台' },
    { id: 'Monitor', name: 'Monitor Center', icon: 'M', description: '监控中心' },
    { id: 'Analytics', name: 'Analytics Hub', icon: 'A', description: '数据分析平台' },
  ];

  const handleSaveProfile = () => {
    toast.success('个人信息已保存');
  };

  const handleChangePassword = () => {
    toast.success('密码已更新');
  };

  const handleCopyToken = (token: string) => {
    navigator.clipboard.writeText(token);
    toast.success('令牌已复制到剪贴板');
  };

  const toggleTokenVisibility = (tokenId: string) => {
    setVisibleTokens(prev => ({
      ...prev,
      [tokenId]: !prev[tokenId]
    }));
  };

  const handleOpenRevokeDialog = (tokenId: string) => {
    setSelectedTokenId(tokenId);
    setShowRevokeDialog(true);
  };

  const handleConfirmRevoke = () => {
    if (selectedTokenId) {
      setUserTokens(tokens => 
        tokens.map(token => 
          token.id === selectedTokenId 
            ? { ...token, status: 'expired' as const, lastUsed: '已撤销' }
            : token
        )
      );
      toast.success('令牌已撤销');
      setShowRevokeDialog(false);
      setSelectedTokenId(null);
    }
  };

  const handleOpenDeleteDialog = (tokenId: string) => {
    setSelectedTokenId(tokenId);
    setShowDeleteDialog(true);
  };

  const handleConfirmDelete = () => {
    if (selectedTokenId) {
      setUserTokens(tokens => tokens.filter(token => token.id !== selectedTokenId));
      toast.success('令牌已删除');
      setShowDeleteDialog(false);
      setSelectedTokenId(null);
    }
  };

  const handleOpenCreateDialog = () => {
    if (userTokens.length >= tokenQuota.total) {
      toast.error('已达到令牌配额上限');
      return;
    }
    setNewTokenName('');
    setNewTokenDescription('');
    setNewTokenExpires('365');
    setNewTokenApp('');
    setAppPermissions([]);
    setShowCreateTokenDialog(true);
  };

  const handleConfirmCreate = () => {
    if (!newTokenName.trim()) {
      toast.error('请输入令牌名称');
      return;
    }

    if (!newTokenApp) {
      toast.error('请选择一个应用');
      return;
    }

    if (appPermissions.length === 0) {
      toast.error('请至少选择一个权限');
      return;
    }

    const selectedApp = availableApps.find(a => a.id === newTokenApp);
    
    const authorizedApps = [{
      name: selectedApp?.name || newTokenApp,
      permissions: appPermissions
    }];

    const tokenValue = `agm_${Math.random().toString(36).substring(2, 15)}${Math.random().toString(36).substring(2, 15)}`;
    
    const newToken = {
      id: Date.now().toString(),
      name: newTokenName,
      app: selectedApp?.name || newTokenApp,
      appIcon: selectedApp?.icon || 'G',
      token: tokenValue,
      createdAt: new Date().toISOString().split('T')[0],
      lastUsed: '未使用',
      status: 'active' as const,
      authorizedApps: authorizedApps,
      scopes: [selectedApp?.name || newTokenApp],
    };

    setUserTokens([newToken, ...userTokens]);
    setCreatedTokenValue(tokenValue);
    setShowCreateTokenDialog(false);
    setShowTokenDialog(true);
    
    // 重置表单
    setNewTokenName('');
    setNewTokenDescription('');
    setNewTokenExpires('365');
    setNewTokenApp('');
    setAppPermissions([]);
  };

  const selectApp = (appId: string) => {
    // 选择新应用时，清空之前的权限选择
    setNewTokenApp(appId);
    setAppPermissions([]);
  };

  const togglePermission = (permission: string) => {
    setAppPermissions(prev => 
      prev.includes(permission)
        ? prev.filter(p => p !== permission)
        : [...prev, permission]
    );
  };



  const handleExportData = () => {
    toast.success('数据导出任务已创建，完成后将发送下载链接到您的邮箱');
  };

  return (
    <div className="flex h-screen w-full bg-gray-50 dark:bg-gray-900">
      {/* Left Navigation */}
      <aside className="w-64 bg-white dark:bg-gray-800 border-r border-gray-200 dark:border-gray-700 overflow-y-auto">
        <div className="p-6">
          <div className="flex items-center gap-3 mb-4">
            {onClose && (
              <button
                onClick={onClose}
                className="p-1.5 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors"
              >
                <ArrowLeft className="w-5 h-5 text-gray-600 dark:text-gray-400" />
              </button>
            )}
            <h2 className="text-xl font-semibold dark:text-white">个人设置</h2>
          </div>
          <p className="text-sm text-gray-600 dark:text-gray-400">管理您的个人信息、偏好设置和安全选项</p>
        </div>
        <nav className="px-3 pb-6">
          {menuItems.map((item) => (
            <button
              key={item.id}
              onClick={() => setActiveTab(item.id)}
              className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-lg mb-1 transition-colors ${
                activeTab === item.id
                  ? 'bg-blue-50 dark:bg-blue-900/20 text-blue-600 dark:text-blue-400'
                  : 'text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700'
              }`}
            >
              <item.icon className="w-4 h-4" />
              <span className="text-sm">{item.label}</span>
            </button>
          ))}
        </nav>
      </aside>

      {/* Main Content */}
      <main className="flex-1 overflow-y-auto">
        <div className="w-full p-8">
          {/* Profile Tab */}
          {activeTab === 'profile' && (
            <div className="space-y-6">
              <div>
                <h1 className="text-2xl dark:text-white mb-2">个人信息</h1>
                <p className="text-gray-600 dark:text-gray-400">更新您的个人资料和公开信息</p>
              </div>

              {/* Avatar */}
              <Card className="p-6 dark:bg-gray-800 dark:border-gray-700">
                <h3 className="text-lg mb-4 dark:text-white">个人头像</h3>
                <p className="text-sm text-gray-600 dark:text-gray-400 mb-4">推荐尺寸 400x400px，支持 JPG、PNG 格式</p>
                <div className="flex items-center gap-4">
                  <Avatar className="w-24 h-24">
                    <AvatarImage src="https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=400&h=400&fit=crop" />
                    <AvatarFallback className="bg-gradient-to-br from-blue-500 to-blue-600 text-white text-2xl">
                      AC
                    </AvatarFallback>
                  </Avatar>
                  <div className="flex gap-2">
                    <Button variant="outline" className="dark:bg-gray-900 dark:border-gray-700 dark:text-gray-300">
                      <Upload className="w-4 h-4 mr-2" />
                      上传新头像
                    </Button>
                    <Button variant="outline" className="dark:bg-gray-900 dark:border-gray-700 dark:text-gray-300">
                      <Trash2 className="w-4 h-4 mr-2" />
                      删除头像
                    </Button>
                  </div>
                </div>
              </Card>

              {/* Basic Info */}
              <Card className="p-6 dark:bg-gray-800 dark:border-gray-700">
                <h3 className="text-lg mb-4 dark:text-white">基本信息</h3>
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <Label htmlFor="name" className="dark:text-gray-300">姓名</Label>
                    <Input id="name" defaultValue="Alex Chen" className="mt-1.5 dark:bg-gray-900 dark:border-gray-700 dark:text-white" />
                  </div>
                  <div>
                    <Label htmlFor="email" className="dark:text-gray-300">邮箱</Label>
                    <Input id="email" type="email" defaultValue="alex.chen@example.com" className="mt-1.5 dark:bg-gray-900 dark:border-gray-700 dark:text-white" />
                  </div>
                  <div className="col-span-2">
                    <Label htmlFor="bio" className="dark:text-gray-300">个人简介</Label>
                    <Textarea id="bio" rows={3} defaultValue="产品经理，专注于AI和知识管理领域" className="mt-1.5 dark:bg-gray-900 dark:border-gray-700 dark:text-white" />
                  </div>
                  <div>
                    <Label htmlFor="position" className="dark:text-gray-300">职位</Label>
                    <Input id="position" defaultValue="产品经理" className="mt-1.5 dark:bg-gray-900 dark:border-gray-700 dark:text-white" />
                  </div>
                  <div>
                    <Label htmlFor="department" className="dark:text-gray-300">部门</Label>
                    <Input id="department" defaultValue="产品部" className="mt-1.5 dark:bg-gray-900 dark:border-gray-700 dark:text-white" />
                  </div>
                  <div>
                    <Label htmlFor="location" className="dark:text-gray-300">地点</Label>
                    <Input id="location" defaultValue="北京, 中国" className="mt-1.5 dark:bg-gray-900 dark:border-gray-700 dark:text-white" />
                  </div>
                  <div>
                    <Label htmlFor="phone" className="dark:text-gray-300">电话</Label>
                    <Input id="phone" defaultValue="+86 138 0000 0000" className="mt-1.5 dark:bg-gray-900 dark:border-gray-700 dark:text-white" />
                  </div>
                  <div className="col-span-2">
                    <Label htmlFor="website" className="dark:text-gray-300">个人网站</Label>
                    <Input id="website" type="url" placeholder="https://yourwebsite.com" className="mt-1.5 dark:bg-gray-900 dark:border-gray-700 dark:text-white" />
                  </div>
                </div>
              </Card>

              {/* Social Media */}
              <Card className="p-6 dark:bg-gray-800 dark:border-gray-700">
                <h3 className="text-lg mb-4 dark:text-white">社交媒体</h3>
                <div className="space-y-4">
                  <div>
                    <Label htmlFor="github" className="dark:text-gray-300 flex items-center gap-2">
                      <Github className="w-4 h-4" />
                      GitHub
                    </Label>
                    <Input id="github" placeholder="username" className="mt-1.5 dark:bg-gray-900 dark:border-gray-700 dark:text-white" />
                  </div>
                  <div>
                    <Label htmlFor="twitter" className="dark:text-gray-300 flex items-center gap-2">
                      <Twitter className="w-4 h-4" />
                      Twitter
                    </Label>
                    <Input id="twitter" placeholder="@username" className="mt-1.5 dark:bg-gray-900 dark:border-gray-700 dark:text-white" />
                  </div>
                  <div>
                    <Label htmlFor="linkedin" className="dark:text-gray-300 flex items-center gap-2">
                      <Linkedin className="w-4 h-4" />
                      LinkedIn
                    </Label>
                    <Input id="linkedin" placeholder="username" className="mt-1.5 dark:bg-gray-900 dark:border-gray-700 dark:text-white" />
                  </div>
                </div>
              </Card>

              <div className="flex justify-end">
                <Button onClick={handleSaveProfile} className="bg-blue-500 hover:bg-blue-600 text-white">
                  保存更改
                </Button>
              </div>
            </div>
          )}

          {/* Security Tab */}
          {activeTab === 'security' && (
            <div className="space-y-6">
              <div>
                <h1 className="text-2xl dark:text-white mb-2">账号安全</h1>
                <p className="text-gray-600 dark:text-gray-400">管理您的密码和安全设置</p>
              </div>

              {/* Password */}
              <Card className="p-6 dark:bg-gray-800 dark:border-gray-700">
                <h3 className="text-lg mb-1 dark:text-white">密码管理</h3>
                <p className="text-sm text-gray-600 dark:text-gray-400 mb-4">上次修改时间: 2024-01-15</p>
                <div className="space-y-3">
                  <div>
                    <Label htmlFor="current-password" className="dark:text-gray-300 text-sm">当前密码</Label>
                    <Input 
                      id="current-password" 
                      type="password" 
                      placeholder="输入当前密码"
                      className="mt-1.5 dark:bg-gray-900 dark:border-gray-700 dark:text-white" 
                    />
                  </div>
                  <div>
                    <Label htmlFor="new-password" className="dark:text-gray-300 text-sm">新密码</Label>
                    <Input 
                      id="new-password" 
                      type="password"
                      placeholder="输入新密码"
                      className="mt-1.5 dark:bg-gray-900 dark:border-gray-700 dark:text-white" 
                    />
                  </div>
                  <div>
                    <Label htmlFor="confirm-password" className="dark:text-gray-300 text-sm">确认新密码</Label>
                    <Input 
                      id="confirm-password" 
                      type="password"
                      placeholder="再次输入新密码"
                      className="mt-1.5 dark:bg-gray-900 dark:border-gray-700 dark:text-white" 
                    />
                  </div>
                  <Button onClick={handleChangePassword} className="bg-gray-900 hover:bg-gray-800 text-white dark:bg-gray-950 dark:hover:bg-gray-900 mt-2">
                    <Key className="w-4 h-4 mr-2" />
                    修改密码
                  </Button>
                </div>
              </Card>

              {/* Two Factor */}
              <Card className="p-6 dark:bg-gray-800 dark:border-gray-700">
                <div className="flex items-start justify-between mb-2">
                  <h3 className="text-lg dark:text-white">两步验证</h3>
                  <Badge className="bg-gray-100 text-gray-700 dark:bg-gray-700 dark:text-gray-400 border-0">
                    未启用
                  </Badge>
                </div>
                <p className="text-sm text-gray-600 dark:text-gray-400 mb-6">为您的账号添加额外的安全保护</p>
                
                <div className="flex items-center justify-between p-4 bg-gray-50 dark:bg-gray-900 rounded-lg">
                  <div className="flex items-center gap-3">
                    <div className="w-10 h-10 rounded-lg bg-blue-100 dark:bg-blue-900/30 flex items-center justify-center">
                      <Fingerprint className="w-5 h-5 text-blue-600 dark:text-blue-400" />
                    </div>
                    <div>
                      <p className="text-sm dark:text-white">验证器应用</p>
                      <p className="text-xs text-gray-500 dark:text-gray-400">使用 Google Authenticator 或类似应用</p>
                    </div>
                  </div>
                  <Switch checked={authenticatorEnabled} onCheckedChange={setAuthenticatorEnabled} />
                </div>
              </Card>


            </div>
          )}

          {/* Notifications Tab */}
          {activeTab === 'notifications' && (
            <div className="space-y-6">
              <div>
                <h1 className="text-2xl dark:text-white mb-2">通知设置</h1>
                <p className="text-gray-600 dark:text-gray-400">管理您如何接收通知</p>
              </div>

              {/* Email Notifications */}
              <Card className="p-6 dark:bg-gray-800 dark:border-gray-700">
                <h3 className="text-lg mb-4 dark:text-white">邮件通知</h3>
                <div className="space-y-4">
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-sm dark:text-white">评论通知</p>
                      <p className="text-xs text-gray-500 dark:text-gray-400">当有人评论您的内容时</p>
                    </div>
                    <Switch
                      checked={emailNotifications.comments}
                      onCheckedChange={(checked) => setEmailNotifications({ ...emailNotifications, comments: checked })}
                    />
                  </div>
                  <Separator className="dark:bg-gray-700" />
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-sm dark:text-white">提及通知</p>
                      <p className="text-xs text-gray-500 dark:text-gray-400">当有人 @提及 您时</p>
                    </div>
                    <Switch
                      checked={emailNotifications.mentions}
                      onCheckedChange={(checked) => setEmailNotifications({ ...emailNotifications, mentions: checked })}
                    />
                  </div>
                  <Separator className="dark:bg-gray-700" />
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-sm dark:text-white">更新通知</p>
                      <p className="text-xs text-gray-500 dark:text-gray-400">关注的文档有更新时</p>
                    </div>
                    <Switch
                      checked={emailNotifications.updates}
                      onCheckedChange={(checked) => setEmailNotifications({ ...emailNotifications, updates: checked })}
                    />
                  </div>
                  <Separator className="dark:bg-gray-700" />
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-sm dark:text-white">产品动态</p>
                      <p className="text-xs text-gray-500 dark:text-gray-400">产品更新和新闻</p>
                    </div>
                    <Switch
                      checked={emailNotifications.productNews}
                      onCheckedChange={(checked) => setEmailNotifications({ ...emailNotifications, productNews: checked })}
                    />
                  </div>
                </div>
              </Card>

              {/* Push Notifications */}
              <Card className="p-6 dark:bg-gray-800 dark:border-gray-700">
                <h3 className="text-lg mb-4 dark:text-white">推送通知</h3>
                <div className="space-y-4">
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-sm dark:text-white">评论通知</p>
                      <p className="text-xs text-gray-500 dark:text-gray-400">当有人评论您的内容时</p>
                    </div>
                    <Switch
                      checked={pushNotifications.comments}
                      onCheckedChange={(checked) => setPushNotifications({ ...pushNotifications, comments: checked })}
                    />
                  </div>
                  <Separator className="dark:bg-gray-700" />
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-sm dark:text-white">提及通知</p>
                      <p className="text-xs text-gray-500 dark:text-gray-400">当有人 @提及 您时</p>
                    </div>
                    <Switch
                      checked={pushNotifications.mentions}
                      onCheckedChange={(checked) => setPushNotifications({ ...pushNotifications, mentions: checked })}
                    />
                  </div>
                  <Separator className="dark:bg-gray-700" />
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-sm dark:text-white">更新通知</p>
                      <p className="text-xs text-gray-500 dark:text-gray-400">关注的文档有更新时</p>
                    </div>
                    <Switch
                      checked={pushNotifications.updates}
                      onCheckedChange={(checked) => setPushNotifications({ ...pushNotifications, updates: checked })}
                    />
                  </div>
                </div>
              </Card>

              {/* Desktop Notifications */}
              <Card className="p-6 dark:bg-gray-800 dark:border-gray-700">
                <h3 className="text-lg mb-4 dark:text-white">桌面通知</h3>
                <div className="space-y-4">
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-sm dark:text-white">启用桌面通知</p>
                      <p className="text-xs text-gray-500 dark:text-gray-400">在桌面显示通知弹窗</p>
                    </div>
                    <Switch checked={desktopNotifications} onCheckedChange={setDesktopNotifications} />
                  </div>
                  <Separator className="dark:bg-gray-700" />
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-sm dark:text-white">通知声音</p>
                      <p className="text-xs text-gray-500 dark:text-gray-400">收到通知时播放提示音</p>
                    </div>
                    <Switch checked={notificationSound} onCheckedChange={setNotificationSound} />
                  </div>
                </div>
              </Card>
            </div>
          )}

          {/* Appearance Tab */}
          {activeTab === 'appearance' && (
            <div className="space-y-6">
              <div>
                <h1 className="text-2xl dark:text-white mb-2">外观设置</h1>
                <p className="text-gray-600 dark:text-gray-400">自定义应用的外观和感觉</p>
              </div>

              {/* Theme */}
              <Card className="p-6 dark:bg-gray-800 dark:border-gray-700">
                <h3 className="text-lg mb-4 dark:text-white">主题模式</h3>
                <RadioGroup value={theme} onValueChange={setTheme} className="grid grid-cols-3 gap-4">
                  <div>
                    <RadioGroupItem value="light" id="light" className="peer sr-only" />
                    <Label
                      htmlFor="light"
                      className="flex flex-col items-center justify-center rounded-lg border-2 border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-900 p-4 hover:border-blue-500 peer-data-[state=checked]:border-blue-500 peer-data-[state=checked]:bg-blue-50 dark:peer-data-[state=checked]:bg-blue-900/20 cursor-pointer transition-all"
                    >
                      <Sun className="w-8 h-8 mb-2 text-gray-600 dark:text-gray-400" />
                      <span className="text-sm dark:text-white">浅色模式</span>
                    </Label>
                  </div>
                  <div>
                    <RadioGroupItem value="dark" id="dark" className="peer sr-only" />
                    <Label
                      htmlFor="dark"
                      className="flex flex-col items-center justify-center rounded-lg border-2 border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-900 p-4 hover:border-blue-500 peer-data-[state=checked]:border-blue-500 peer-data-[state=checked]:bg-blue-50 dark:peer-data-[state=checked]:bg-blue-900/20 cursor-pointer transition-all"
                    >
                      <Moon className="w-8 h-8 mb-2 text-gray-600 dark:text-gray-400" />
                      <span className="text-sm dark:text-white">深色模式</span>
                    </Label>
                  </div>
                  <div>
                    <RadioGroupItem value="system" id="system" className="peer sr-only" />
                    <Label
                      htmlFor="system"
                      className="flex flex-col items-center justify-center rounded-lg border-2 border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-900 p-4 hover:border-blue-500 peer-data-[state=checked]:border-blue-500 peer-data-[state=checked]:bg-blue-50 dark:peer-data-[state=checked]:bg-blue-900/20 cursor-pointer transition-all"
                    >
                      <Monitor className="w-8 h-8 mb-2 text-gray-600 dark:text-gray-400" />
                      <span className="text-sm dark:text-white">跟随系统</span>
                    </Label>
                  </div>
                </RadioGroup>
              </Card>

              {/* Language */}
              <Card className="p-6 dark:bg-gray-800 dark:border-gray-700">
                <h3 className="text-lg mb-4 dark:text-white">语言设置</h3>
                <div className="flex items-center gap-2">
                  <Globe className="w-4 h-4 text-gray-500 dark:text-gray-400" />
                  <Select value={language} onValueChange={setLanguage}>
                    <SelectTrigger className="w-full dark:bg-gray-900 dark:border-gray-700 dark:text-white">
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent className="dark:bg-gray-900 dark:border-gray-700">
                      <SelectItem value="zh-CN">简体中文</SelectItem>
                      <SelectItem value="en">English</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
              </Card>

              {/* Font Size */}
              <Card className="p-6 dark:bg-gray-800 dark:border-gray-700">
                <h3 className="text-lg mb-4 dark:text-white">字体大小</h3>
                <div className="space-y-4">
                  <div className="flex items-center justify-between">
                    <span className="text-sm text-gray-600 dark:text-gray-400">当前大小</span>
                    <span className="text-sm dark:text-white">{fontSize[0]}px</span>
                  </div>
                  <Slider
                    value={fontSize}
                    onValueChange={setFontSize}
                    min={12}
                    max={18}
                    step={2}
                    className="w-full"
                  />
                  <div className="flex justify-between text-xs text-gray-500 dark:text-gray-400">
                    <span>小</span>
                    <span>中</span>
                    <span>大</span>
                  </div>
                </div>
              </Card>
            </div>
          )}

          {/* Privacy Tab */}
          {activeTab === 'privacy' && (
            <div className="space-y-6">
              <div>
                <h1 className="text-2xl dark:text-white mb-2">隐私设置</h1>
                <p className="text-gray-600 dark:text-gray-400">控制您的隐私和数据可见性</p>
              </div>

              {/* Visibility Settings */}
              <Card className="p-6 dark:bg-gray-800 dark:border-gray-700">
                <h3 className="text-lg mb-4 dark:text-white">可见性设置</h3>
                <div className="space-y-4">
                  <div>
                    <Label htmlFor="profile-visibility" className="dark:text-gray-300">个人资料可见性</Label>
                    <Select value={profileVisibility} onValueChange={setProfileVisibility}>
                      <SelectTrigger id="profile-visibility" className="mt-1.5 dark:bg-gray-900 dark:border-gray-700 dark:text-white">
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent className="dark:bg-gray-900 dark:border-gray-700">
                        <SelectItem value="public">公开</SelectItem>
                        <SelectItem value="team">团队可见</SelectItem>
                        <SelectItem value="private">仅自己</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                  <div>
                    <Label htmlFor="activity-visibility" className="dark:text-gray-300">活动记录可见性</Label>
                    <Select value={activityVisibility} onValueChange={setActivityVisibility}>
                      <SelectTrigger id="activity-visibility" className="mt-1.5 dark:bg-gray-900 dark:border-gray-700 dark:text-white">
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent className="dark:bg-gray-900 dark:border-gray-700">
                        <SelectItem value="public">公开</SelectItem>
                        <SelectItem value="team">团队可见</SelectItem>
                        <SelectItem value="private">仅自己</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                  <div>
                    <Label htmlFor="email-visibility" className="dark:text-gray-300">邮箱可见性</Label>
                    <Select value={emailVisibility} onValueChange={setEmailVisibility}>
                      <SelectTrigger id="email-visibility" className="mt-1.5 dark:bg-gray-900 dark:border-gray-700 dark:text-white">
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent className="dark:bg-gray-900 dark:border-gray-700">
                        <SelectItem value="public">公开</SelectItem>
                        <SelectItem value="team">团队可见</SelectItem>
                        <SelectItem value="private">仅自己</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                </div>
              </Card>

              {/* Other Privacy Settings */}
              <Card className="p-6 dark:bg-gray-800 dark:border-gray-700">
                <h3 className="text-lg mb-4 dark:text-white">其他隐私设置</h3>
                <div className="space-y-4">
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-sm dark:text-white">显示在线状态</p>
                      <p className="text-xs text-gray-500 dark:text-gray-400">让其他人看到您是否在线</p>
                    </div>
                    <Switch checked={showOnlineStatus} onCheckedChange={setShowOnlineStatus} />
                  </div>
                  <Separator className="dark:bg-gray-700" />
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-sm dark:text-white">允许搜索引擎索引</p>
                      <p className="text-xs text-gray-500 dark:text-gray-400">允许搜索引擎索引您的公开资料</p>
                    </div>
                    <Switch checked={allowSearchEngineIndexing} onCheckedChange={setAllowSearchEngineIndexing} />
                  </div>
                </div>
              </Card>
            </div>
          )}

          {/* Tokens Tab */}
          {activeTab === 'tokens' && (
            <div className="space-y-6">
              {/* 页面标题 */}
              <div className="flex items-center justify-between">
                <div>
                  <h2 className="text-2xl dark:text-white mb-2">用户令牌</h2>
                  <p className="text-sm text-gray-500 dark:text-gray-400">
                    管理您的API访问令牌和应用授权，最多可创建 {tokenQuota.total} 个令牌
                  </p>
                </div>
                <Button 
                  onClick={handleOpenCreateDialog}
                  disabled={userTokens.length >= tokenQuota.total}
                  className="bg-blue-600 hover:bg-blue-700"
                >
                  <Plus className="w-4 h-4 mr-2" />
                  创建令牌
                </Button>
              </div>

              {/* 令牌统计 */}
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <Card className="p-4 dark:bg-gray-800 dark:border-gray-700">
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-sm text-gray-500 dark:text-gray-400">已创建令牌</p>
                      <p className="text-2xl dark:text-white mt-1">{userTokens.length} / {tokenQuota.total}</p>
                    </div>
                    <div className="w-12 h-12 bg-blue-100 dark:bg-blue-900/30 rounded-lg flex items-center justify-center">
                      <KeyRound className="w-6 h-6 text-blue-600 dark:text-blue-400" />
                    </div>
                  </div>
                </Card>

                <Card className="p-4 dark:bg-gray-800 dark:border-gray-700">
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-sm text-gray-500 dark:text-gray-400">有效令牌</p>
                      <p className="text-2xl dark:text-white mt-1">
                        {userTokens.filter(t => t.status === 'active').length}
                      </p>
                    </div>
                    <div className="w-12 h-12 bg-green-100 dark:bg-green-900/30 rounded-lg flex items-center justify-center">
                      <KeyRound className="w-6 h-6 text-green-600 dark:text-green-400" />
                    </div>
                  </div>
                </Card>

                <Card className="p-4 dark:bg-gray-800 dark:border-gray-700">
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-sm text-gray-500 dark:text-gray-400">剩余配额</p>
                      <p className="text-2xl dark:text-white mt-1">{tokenQuota.total - userTokens.length}</p>
                    </div>
                    <div className="w-12 h-12 bg-purple-100 dark:bg-purple-900/30 rounded-lg flex items-center justify-center">
                      <KeyRound className="w-6 h-6 text-purple-600 dark:text-purple-400" />
                    </div>
                  </div>
                </Card>
              </div>

              {/* 令牌列表 */}
              <div className="space-y-4">
                {userTokens.map((token) => (
                  <Card key={token.id} className="p-6 dark:bg-gray-800 dark:border-gray-700">
                    <div className="space-y-4">
                      <div className="flex items-start justify-between">
                        <div className="flex-1">
                          <div className="flex items-center gap-3 mb-2">
                            <h3 className="text-lg dark:text-white">{token.name}</h3>
                            <Badge className={`border-0 ${token.status === 'active' 
                              ? 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400' 
                              : 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400'
                            }`}>
                              {token.status === 'active' ? '有效' : '已过期'}
                            </Badge>
                          </div>
                          <div className="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400">
                            <div className="w-6 h-6 rounded bg-gradient-to-br from-blue-500 to-blue-600 dark:from-blue-600 dark:to-blue-700 flex items-center justify-center flex-shrink-0">
                              <span className="text-white text-xs">{token.appIcon}</span>
                            </div>
                            <span>{token.app}</span>
                          </div>
                        </div>
                      </div>

                      <div className="space-y-3">
                        <div>
                          <Label className="text-gray-600 dark:text-gray-400">令牌</Label>
                          <div className="flex items-center gap-2 mt-1.5">
                            <div className="flex-1 bg-gray-50 dark:bg-gray-900 border dark:border-gray-700 rounded px-3 py-2 font-mono text-sm dark:text-gray-300">
                              {visibleTokens[token.id] ? token.token : '••••••••••••••••••••••••••••••••'}
                            </div>
                            <Button
                              variant="outline"
                              size="sm"
                              onClick={() => toggleTokenVisibility(token.id)}
                              disabled={token.status === 'expired'}
                              className="dark:bg-gray-900 dark:border-gray-700 dark:text-gray-300"
                            >
                              {visibleTokens[token.id] ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                            </Button>
                            <Button
                              variant="outline"
                              size="sm"
                              onClick={() => handleCopyToken(token.token)}
                              disabled={token.status === 'expired'}
                              className="dark:bg-gray-900 dark:border-gray-700 dark:text-gray-300"
                            >
                              <Copy className="w-4 h-4 mr-2" />
                              复制
                            </Button>
                          </div>
                        </div>

                        {/* 关联应用和权限 */}
                        <div>
                          <Label className="text-gray-600 dark:text-gray-400">关联应用和权限</Label>
                          <div className="mt-1.5 space-y-2">
                            {token.authorizedApps.map((authApp, index) => (
                              <div key={index} className="flex items-center gap-2 text-sm">
                                <span className="dark:text-gray-300">{authApp.name}</span>
                                <span className="text-gray-400 dark:text-gray-500">-</span>
                                <div className="flex flex-wrap gap-1.5">
                                  {authApp.permissions.map((perm, permIndex) => (
                                    <Badge key={permIndex} variant="outline" className="text-xs dark:border-gray-700 dark:text-gray-400">
                                      {perm}
                                    </Badge>
                                  ))}
                                </div>
                              </div>
                            ))}
                          </div>
                        </div>

                        <div className="grid grid-cols-2 gap-4 text-sm">
                          <div>
                            <span className="text-gray-600 dark:text-gray-400">创建时间：</span>
                            <span className="dark:text-white">{token.createdAt}</span>
                          </div>
                          <div>
                            <span className="text-gray-600 dark:text-gray-400">最后使用：</span>
                            <span className="dark:text-white">{token.lastUsed}</span>
                          </div>
                        </div>
                      </div>

                      <div className="flex gap-2 pt-2 border-t dark:border-gray-700">
                        {token.status === 'active' && (
                          <Button
                            variant="outline"
                            size="sm"
                            onClick={() => handleOpenRevokeDialog(token.id)}
                            className="dark:bg-gray-900 dark:border-gray-700 dark:text-gray-300"
                          >
                            撤销令牌
                          </Button>
                        )}
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={() => handleOpenDeleteDialog(token.id)}
                          className="border-red-300 dark:border-red-800 text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/20"
                        >
                          <Trash2 className="w-4 h-4 mr-2" />
                          删除令牌
                        </Button>
                      </div>
                    </div>
                  </Card>
                ))}
              </div>

              {/* 安全提示 */}
              <Card className="p-4 bg-blue-50 dark:bg-blue-900/20 border-blue-200 dark:border-blue-800">
                <div className="flex gap-3">
                  <AlertCircle className="w-5 h-5 text-blue-600 dark:text-blue-400 flex-shrink-0 mt-0.5" />
                  <div>
                    <h3 className="text-sm text-blue-900 dark:text-blue-300 mb-1">安全提示</h3>
                    <p className="text-xs text-blue-800 dark:text-blue-400">
                      请妥善保管您的令牌，不要与他人分享。定期轮换令牌以提高安全性，如果令牌泄露请立即撤销并创建新令牌。令牌创建后仅显示一次，请务必安全保存。
                    </p>
                  </div>
                </div>
              </Card>
            </div>
          )}

          {/* Create Token Dialog */}
          <Dialog open={showCreateTokenDialog} onOpenChange={setShowCreateTokenDialog}>
            <DialogContent className="dark:bg-gray-800 dark:border-gray-700 max-w-2xl">
              <DialogHeader>
                <DialogTitle className="dark:text-white">创建新令牌</DialogTitle>
                <DialogDescription className="dark:text-gray-400">
                  创建一个新的API访问令牌，用于应用程序访问您的账户
                </DialogDescription>
              </DialogHeader>
              <div className="space-y-4 py-4">
                <div>
                  <Label htmlFor="token-name" className="dark:text-gray-300">令牌名称</Label>
                  <Input
                    id="token-name"
                    placeholder="例如：生产环境API令牌"
                    value={newTokenName}
                    onChange={(e) => setNewTokenName(e.target.value)}
                    className="mt-1.5 dark:bg-gray-900 dark:border-gray-700 dark:text-white"
                  />
                </div>
                
                <div>
                  <Label htmlFor="token-description" className="dark:text-gray-300">描述</Label>
                  <Textarea
                    id="token-description"
                    placeholder="令牌的用途说明..."
                    value={newTokenDescription}
                    onChange={(e) => setNewTokenDescription(e.target.value)}
                    className="mt-1.5 dark:bg-gray-900 dark:border-gray-700 dark:text-white"
                    rows={3}
                  />
                </div>

                <div>
                  <Label htmlFor="token-expires" className="dark:text-gray-300">过期时间</Label>
                  <Input
                    id="token-expires"
                    type="number"
                    placeholder="365"
                    value={newTokenExpires}
                    onChange={(e) => setNewTokenExpires(e.target.value)}
                    className="mt-1.5 dark:bg-gray-900 dark:border-gray-700 dark:text-white"
                  />
                  <p className="text-xs text-gray-500 dark:text-gray-400 mt-1.5">
                    设置令牌的有效天数，留空表示永久有效
                  </p>
                </div>

                <div className="p-3 bg-blue-50 dark:bg-blue-900/20 rounded-lg border border-blue-200 dark:border-blue-800">
                  <p className="text-sm text-blue-700 dark:text-blue-300 flex items-start gap-2">
                    <AlertCircle className="w-4 h-4 mt-0.5 flex-shrink-0" />
                    <span>令牌创建后将只显示一次，请妥善保管</span>
                  </p>
                </div>

                <div>
                  <Label htmlFor="token-app" className="dark:text-gray-300">选择应用</Label>
                  <Select value={newTokenApp} onValueChange={selectApp}>
                    <SelectTrigger className="mt-1.5 dark:bg-gray-900 dark:border-gray-700 dark:text-white">
                      <SelectValue placeholder="请选择一个应用" />
                    </SelectTrigger>
                    <SelectContent className="dark:bg-gray-800 dark:border-gray-700">
                      {availableApps.map((app) => (
                        <SelectItem key={app.id} value={app.id} className="dark:text-gray-300 dark:focus:bg-gray-700">
                          <div className="flex items-center gap-2">
                            <div className="w-6 h-6 rounded bg-gradient-to-br from-blue-500 to-blue-600 dark:from-blue-600 dark:to-blue-700 flex items-center justify-center flex-shrink-0">
                              <span className="text-white text-xs">{app.icon}</span>
                            </div>
                            <span>{app.name}</span>
                          </div>
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                  <p className="text-xs text-gray-500 dark:text-gray-400 mt-1.5">
                    令牌只能关联一个应用
                  </p>
                </div>

                {/* Permissions section - shown when app is selected */}
                {newTokenApp && (
                  <div className="p-4 border dark:border-gray-700 rounded-lg dark:bg-gray-900">
                    <Label className="dark:text-gray-300 mb-3 block">授权权限</Label>
                    <div className="space-y-2">
                      {['管理', '读权限', '写权限'].map((permission) => (
                        <div key={permission} className="flex items-center space-x-2">
                          <Checkbox
                            id={`perm-${permission}`}
                            checked={appPermissions.includes(permission)}
                            onCheckedChange={() => togglePermission(permission)}
                          />
                          <label
                            htmlFor={`perm-${permission}`}
                            className="text-sm dark:text-gray-300 cursor-pointer"
                          >
                            {permission}
                          </label>
                        </div>
                      ))}
                    </div>
                    {appPermissions.length > 0 && (
                      <div className="mt-3 pt-3 border-t dark:border-gray-700">
                        <p className="text-xs text-gray-600 dark:text-gray-400 mb-2">已选择权限：</p>
                        <div className="flex flex-wrap gap-1">
                          {appPermissions.map((perm, index) => (
                            <Badge key={index} variant="outline" className="text-xs dark:border-gray-700 dark:text-gray-300">
                              {perm}
                            </Badge>
                          ))}
                        </div>
                      </div>
                    )}
                  </div>
                )}
              </div>
              <DialogFooter>
                <Button
                  variant="outline"
                  onClick={() => setShowCreateTokenDialog(false)}
                  className="dark:bg-gray-900 dark:border-gray-700 dark:text-gray-300"
                >
                  取消
                </Button>
                <Button
                  onClick={handleConfirmCreate}
                  className="bg-blue-500 hover:bg-blue-600 text-white"
                >
                  创建令牌
                </Button>
              </DialogFooter>
            </DialogContent>
          </Dialog>

          {/* 显示新令牌对话框 */}
          <Dialog open={showTokenDialog} onOpenChange={setShowTokenDialog}>
            <DialogContent className="dark:bg-gray-800 dark:border-gray-700" aria-describedby={undefined}>
              <DialogHeader>
                <DialogTitle className="dark:text-white">令牌已创建</DialogTitle>
              </DialogHeader>
              
              <div className="space-y-4">
                <div className="p-4 bg-green-50 dark:bg-green-900/20 rounded-lg border border-green-200 dark:border-green-900/50">
                  <p className="text-sm text-green-700 dark:text-green-400 mb-2">
                    令牌创建成功！请立即复制并妥善保管，此令牌将不会再次显示。
                  </p>
                </div>

                <div>
                  <Label className="dark:text-gray-300">用户令牌</Label>
                  <div className="flex items-center gap-2 mt-2">
                    <code className="flex-1 px-3 py-2 bg-gray-50 dark:bg-gray-900 border dark:border-gray-700 rounded text-sm font-mono dark:text-gray-300 break-all">
                      {createdTokenValue}
                    </code>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => handleCopyToken(createdTokenValue)}
                      className="dark:border-gray-600 dark:hover:bg-gray-700"
                    >
                      <Copy className="w-4 h-4" />
                    </Button>
                  </div>
                </div>

                <div className="p-4 bg-orange-50 dark:bg-orange-900/20 rounded-lg border border-orange-200 dark:border-orange-900/50">
                  <p className="text-sm text-orange-700 dark:text-orange-400">
                    <AlertCircle className="w-4 h-4 inline mr-1" />
                    关闭此窗口后将无法再次查看完整令牌
                  </p>
                </div>
              </div>

              <DialogFooter>
                <Button onClick={() => setShowTokenDialog(false)} className="bg-blue-600 hover:bg-blue-700">
                  我已复制
                </Button>
              </DialogFooter>
            </DialogContent>
          </Dialog>

          {/* Revoke Token Dialog */}
          <Dialog open={showRevokeDialog} onOpenChange={setShowRevokeDialog}>
            <DialogContent className="dark:bg-gray-800 dark:border-gray-700" aria-describedby={undefined}>
              <DialogHeader>
                <DialogTitle className="dark:text-white">撤销令牌</DialogTitle>
              </DialogHeader>
              
              <div className="space-y-4">
                <p className="text-sm text-gray-600 dark:text-gray-400">
                  确认要撤销令牌 <strong className="dark:text-white">
                    {userTokens.find(t => t.id === selectedTokenId)?.name}
                  </strong> 吗？
                </p>
                
                <div className="p-4 bg-orange-50 dark:bg-orange-900/20 rounded-lg border border-orange-200 dark:border-orange-900/50">
                  <p className="text-sm text-orange-700 dark:text-orange-400">
                    <AlertCircle className="w-4 h-4 inline mr-1" />
                    撤销后，使用此令牌的应用将无法访问您的账户，此操作不可恢复
                  </p>
                </div>
              </div>

              <DialogFooter>
                <Button
                  variant="outline"
                  onClick={() => setShowRevokeDialog(false)}
                  className="dark:border-gray-600 dark:hover:bg-gray-700"
                >
                  取消
                </Button>
                <Button 
                  onClick={handleConfirmRevoke}
                  className="bg-orange-600 hover:bg-orange-700"
                >
                  撤销令牌
                </Button>
              </DialogFooter>
            </DialogContent>
          </Dialog>

          {/* Delete Token Dialog */}
          <Dialog open={showDeleteDialog} onOpenChange={setShowDeleteDialog}>
            <DialogContent className="dark:bg-gray-800 dark:border-gray-700" aria-describedby={undefined}>
              <DialogHeader>
                <DialogTitle className="dark:text-white">删除令牌</DialogTitle>
              </DialogHeader>
              
              <div className="space-y-4">
                <p className="text-sm text-gray-600 dark:text-gray-400">
                  确认要删除令牌 <strong className="dark:text-white">
                    {userTokens.find(t => t.id === selectedTokenId)?.name}
                  </strong> 吗？
                </p>
                
                <div className="p-4 bg-red-50 dark:bg-red-900/20 rounded-lg border border-red-200 dark:border-red-900/50">
                  <p className="text-sm text-red-700 dark:text-red-400">
                    <AlertCircle className="w-4 h-4 inline mr-1" />
                    删除后，使用此令牌的应用将无法访问您的账户
                  </p>
                </div>
              </div>

              <DialogFooter>
                <Button
                  variant="outline"
                  onClick={() => setShowDeleteDialog(false)}
                  className="dark:border-gray-600 dark:hover:bg-gray-700"
                >
                  取消
                </Button>
                <Button 
                  onClick={handleConfirmDelete}
                  className="bg-red-600 hover:bg-red-700"
                >
                  删除令牌
                </Button>
              </DialogFooter>
            </DialogContent>
          </Dialog>

        </div>
      </main>
    </div>
  );
}
