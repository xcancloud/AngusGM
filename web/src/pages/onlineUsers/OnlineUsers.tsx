import { useState } from 'react';
import { Users, Circle, Monitor, Smartphone, Tablet, Search, Filter, LogOut, BarChart3 } from 'lucide-react';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';  
import { Badge } from '@/components/ui/badge';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { toast } from 'sonner';

interface OnlineUser {
  id: string;
  userId: string;
  username: string;
  email: string;
  status: 'online' | 'idle' | 'offline';
  device: 'desktop' | 'mobile' | 'tablet';
  browser: string;
  ip: string;
  location: string;
  loginTime: string;
  lastActivity: string;
  sessionDuration: string;
}

interface LoginRecord {
  id: string;
  userId: string;
  username: string;
  action: 'login' | 'logout';
  device: string;
  ip: string;
  location: string;
  timestamp: string;
  status: 'success' | 'failed';
}

export function OnlineUsers() {
  const [activeTab, setActiveTab] = useState('online');
  
  const [onlineUsers, setOnlineUsers] = useState<OnlineUser[]>([
    {
      id: 'OU001',
      userId: 'U001',
      username: '张三',
      email: 'zhangsan@example.com',
      status: 'online',
      device: 'desktop',
      browser: 'Chrome 118',
      ip: '192.168.1.100',
      location: '上海',
      loginTime: '2025-10-22 09:00:00',
      lastActivity: '2025-10-22 14:35:00',
      sessionDuration: '5小时35分钟',
    },
    {
      id: 'OU002',
      userId: 'U002',
      username: '李四',
      email: 'lisi@example.com',
      status: 'online',
      device: 'mobile',
      browser: 'Safari 17',
      ip: '192.168.1.101',
      location: '北京',
      loginTime: '2025-10-22 10:30:00',
      lastActivity: '2025-10-22 14:30:00',
      sessionDuration: '4小时',
    },
    {
      id: 'OU003',
      userId: 'U003',
      username: '王五',
      email: 'wangwu@example.com',
      status: 'idle',
      device: 'desktop',
      browser: 'Firefox 119',
      ip: '192.168.1.102',
      location: '深圳',
      loginTime: '2025-10-22 08:00:00',
      lastActivity: '2025-10-22 14:00:00',
      sessionDuration: '6小时',
    },
  ]);

  const [loginRecords] = useState<LoginRecord[]>([
    {
      id: 'LR001',
      userId: 'U001',
      username: '张三',
      action: 'login',
      device: 'Chrome/Windows',
      ip: '192.168.1.100',
      location: '上海',
      timestamp: '2025-10-22 09:00:00',
      status: 'success',
    },
    {
      id: 'LR002',
      userId: 'U002',
      username: '李四',
      action: 'login',
      device: 'Safari/iOS',
      ip: '192.168.1.101',
      location: '北京',
      timestamp: '2025-10-22 10:30:00',
      status: 'success',
    },
    {
      id: 'LR003',
      userId: 'U004',
      username: '赵六',
      action: 'login',
      device: 'Chrome/MacOS',
      ip: '192.168.1.200',
      location: '广州',
      timestamp: '2025-10-22 08:00:00',
      status: 'failed',
    },
    {
      id: 'LR004',
      userId: 'U005',
      username: '钱七',
      action: 'logout',
      device: 'Edge/Windows',
      ip: '192.168.1.103',
      location: '杭州',
      timestamp: '2025-10-22 12:00:00',
      status: 'success',
    },
  ]);

  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState('all');
  const [deviceFilter, setDeviceFilter] = useState('all');
  const [actionFilter, setActionFilter] = useState('all');
  const [showKickDialog, setShowKickDialog] = useState(false);
  const [kickingUser, setKickingUser] = useState<OnlineUser | null>(null);

  const stats = {
    total: onlineUsers.length,
    online: onlineUsers.filter(u => u.status === 'online').length,
    idle: onlineUsers.filter(u => u.status === 'idle').length,
    desktop: onlineUsers.filter(u => u.device === 'desktop').length,
    mobile: onlineUsers.filter(u => u.device === 'mobile').length,
  };

  const filteredOnlineUsers = onlineUsers.filter(u => {
    const matchSearch = u.username.toLowerCase().includes(searchQuery.toLowerCase()) ||
                       u.email.toLowerCase().includes(searchQuery.toLowerCase()) ||
                       u.ip.includes(searchQuery);
    const matchStatus = statusFilter === 'all' || u.status === statusFilter;
    const matchDevice = deviceFilter === 'all' || u.device === deviceFilter;
    return matchSearch && matchStatus && matchDevice;
  });

  const filteredLoginRecords = loginRecords.filter(r => {
    const matchSearch = r.username.toLowerCase().includes(searchQuery.toLowerCase()) ||
                       r.ip.includes(searchQuery);
    const matchAction = actionFilter === 'all' || r.action === actionFilter;
    return matchSearch && matchAction;
  });

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'online':
        return <Badge className="bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400 border-0">
          <Circle className="w-2 h-2 mr-1 fill-current" />
          在线
        </Badge>;
      case 'idle':
        return <Badge className="bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-400 border-0">
          <Circle className="w-2 h-2 mr-1 fill-current" />
          空闲
        </Badge>;
      case 'offline':
        return <Badge className="bg-gray-100 text-gray-700 dark:bg-gray-700 dark:text-gray-400 border-0">
          <Circle className="w-2 h-2 mr-1 fill-current" />
          离线
        </Badge>;
      default:
        return null;
    }
  };

  const getDeviceIcon = (device: string) => {
    switch (device) {
      case 'desktop':
        return <Monitor className="w-4 h-4" />;
      case 'mobile':
        return <Smartphone className="w-4 h-4" />;
      case 'tablet':
        return <Tablet className="w-4 h-4" />;
      default:
        return <Monitor className="w-4 h-4" />;
    }
  };

  const getActionBadge = (action: string, status: string) => {
    if (action === 'login') {
      return status === 'success' ? (
        <Badge className="bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400 border-0">
          登录
        </Badge>
      ) : (
        <Badge className="bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400 border-0">
          登录失败
        </Badge>
      );
    }
    return <Badge className="bg-gray-100 text-gray-700 dark:bg-gray-700 dark:text-gray-400 border-0">
      登出
    </Badge>;
  };

  const handleKickUser = (user: OnlineUser) => {
    setKickingUser(user);
    setShowKickDialog(true);
  };

  const handleConfirmKick = () => {
    if (kickingUser) {
      setOnlineUsers(onlineUsers.filter(u => u.id !== kickingUser.id));
      toast.success(`已强制下线用户：${kickingUser.username}`);
      setShowKickDialog(false);
      setKickingUser(null);
    }
  };

  return (
    <div className="space-y-6">
      {/* 页面标题 */}
      <div>
        <h2 className="text-2xl dark:text-white mb-2">在线用户</h2>
        <p className="text-sm text-gray-500 dark:text-gray-400">
          查看当前在线用户状态和登录记录
        </p>
      </div>

      {/* 统计卡片 */}
      <div className="grid grid-cols-1 md:grid-cols-5 gap-4">
        <Card className="p-4 dark:bg-gray-800 dark:border-gray-700">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-500 dark:text-gray-400">总在线数</p>
              <p className="text-2xl dark:text-white mt-1">{stats.total}</p>
            </div>
            <div className="w-12 h-12 bg-blue-100 dark:bg-blue-900/30 rounded-lg flex items-center justify-center">
              <Users className="w-6 h-6 text-blue-600 dark:text-blue-400" />
            </div>
          </div>
        </Card>

        <Card className="p-4 dark:bg-gray-800 dark:border-gray-700">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-500 dark:text-gray-400">活跃用户</p>
              <p className="text-2xl dark:text-white mt-1">{stats.online}</p>
            </div>
            <div className="w-12 h-12 bg-green-100 dark:bg-green-900/30 rounded-lg flex items-center justify-center">
              <Circle className="w-6 h-6 text-green-600 dark:text-green-400 fill-current" />
            </div>
          </div>
        </Card>

        <Card className="p-4 dark:bg-gray-800 dark:border-gray-700">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-500 dark:text-gray-400">空闲用户</p>
              <p className="text-2xl dark:text-white mt-1">{stats.idle}</p>
            </div>
            <div className="w-12 h-12 bg-orange-100 dark:bg-orange-900/30 rounded-lg flex items-center justify-center">
              <Circle className="w-6 h-6 text-orange-600 dark:text-orange-400 fill-current" />
            </div>
          </div>
        </Card>

        <Card className="p-4 dark:bg-gray-800 dark:border-gray-700">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-500 dark:text-gray-400">桌面端</p>
              <p className="text-2xl dark:text-white mt-1">{stats.desktop}</p>
            </div>
            <div className="w-12 h-12 bg-purple-100 dark:bg-purple-900/30 rounded-lg flex items-center justify-center">
              <Monitor className="w-6 h-6 text-purple-600 dark:text-purple-400" />
            </div>
          </div>
        </Card>

        <Card className="p-4 dark:bg-gray-800 dark:border-gray-700">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-500 dark:text-gray-400">移动端</p>
              <p className="text-2xl dark:text-white mt-1">{stats.mobile}</p>
            </div>
            <div className="w-12 h-12 bg-pink-100 dark:bg-pink-900/30 rounded-lg flex items-center justify-center">
              <Smartphone className="w-6 h-6 text-pink-600 dark:text-pink-400" />
            </div>
          </div>
        </Card>
      </div>

      <Tabs value={activeTab} onValueChange={setActiveTab}>
        <TabsList className="dark:bg-gray-800">
          <TabsTrigger value="online" className="dark:data-[state=active]:bg-gray-700">
            <Users className="w-4 h-4 mr-2" />
            在线用户
          </TabsTrigger>
          <TabsTrigger value="records" className="dark:data-[state=active]:bg-gray-700">
            <BarChart3 className="w-4 h-4 mr-2" />
            登录记录
          </TabsTrigger>
        </TabsList>

        {/* 在线用户 */}
        <TabsContent value="online" className="space-y-4">
          <Card className="p-4 dark:bg-gray-800 dark:border-gray-700">
            <div className="flex flex-wrap gap-4">
              <div className="flex-1 min-w-[200px]">
                <div className="relative">
                  <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
                  <Input
                    placeholder="搜索用户名、邮箱或IP..."
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    className="pl-10 dark:bg-gray-900 dark:border-gray-700"
                  />
                </div>
              </div>
              
              <Select value={statusFilter} onValueChange={setStatusFilter}>
                <SelectTrigger className="w-[120px] dark:bg-gray-900 dark:border-gray-700">
                  <SelectValue placeholder="状态" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">全部状态</SelectItem>
                  <SelectItem value="online">在线</SelectItem>
                  <SelectItem value="idle">空闲</SelectItem>
                </SelectContent>
              </Select>

              <Select value={deviceFilter} onValueChange={setDeviceFilter}>
                <SelectTrigger className="w-[120px] dark:bg-gray-900 dark:border-gray-700">
                  <SelectValue placeholder="设备" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">全部设备</SelectItem>
                  <SelectItem value="desktop">桌面端</SelectItem>
                  <SelectItem value="mobile">移动端</SelectItem>
                  <SelectItem value="tablet">平板</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </Card>

          <div className="grid grid-cols-1 gap-4">
            {filteredOnlineUsers.map((user) => (
              <Card key={user.id} className="p-6 dark:bg-gray-800 dark:border-gray-700">
                <div className="flex items-start justify-between">
                  <div className="flex items-start gap-4 flex-1">
                    <Avatar className="w-12 h-12">
                      <AvatarFallback className="bg-gradient-to-br from-blue-500 to-blue-600 text-white">
                        {user.username.substring(0, 1)}
                      </AvatarFallback>
                    </Avatar>
                    
                    <div className="flex-1">
                      <div className="flex items-center gap-3 mb-2">
                        <h4 className="dark:text-white">{user.username}</h4>
                        {getStatusBadge(user.status)}
                        <Badge variant="outline" className="dark:border-gray-600 dark:text-gray-400">
                          {getDeviceIcon(user.device)}
                          <span className="ml-1 capitalize">{user.device}</span>
                        </Badge>
                      </div>
                      
                      <p className="text-sm text-gray-600 dark:text-gray-400 mb-3">
                        {user.email}
                      </p>

                      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
                        <div>
                          <span className="text-gray-500 dark:text-gray-400">浏览器:</span>
                          <span className="ml-2 dark:text-white">{user.browser}</span>
                        </div>
                        <div>
                          <span className="text-gray-500 dark:text-gray-400">IP地址:</span>
                          <span className="ml-2 dark:text-white">{user.ip}</span>
                        </div>
                        <div>
                          <span className="text-gray-500 dark:text-gray-400">位置:</span>
                          <span className="ml-2 dark:text-white">{user.location}</span>
                        </div>
                        <div>
                          <span className="text-gray-500 dark:text-gray-400">会话时长:</span>
                          <span className="ml-2 dark:text-white">{user.sessionDuration}</span>
                        </div>
                        <div>
                          <span className="text-gray-500 dark:text-gray-400">登录时间:</span>
                          <span className="ml-2 dark:text-white">{user.loginTime}</span>
                        </div>
                        <div>
                          <span className="text-gray-500 dark:text-gray-400">最后活动:</span>
                          <span className="ml-2 dark:text-white">{user.lastActivity}</span>
                        </div>
                      </div>
                    </div>
                  </div>

                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => handleKickUser(user)}
                    className="text-red-600 hover:text-red-700 dark:text-red-400 dark:border-gray-600 dark:hover:bg-gray-700"
                  >
                    <LogOut className="w-4 h-4 mr-2" />
                    强制下线
                  </Button>
                </div>
              </Card>
            ))}
          </div>
        </TabsContent>

        {/* 登录记录 */}
        <TabsContent value="records" className="space-y-4">
          <Card className="p-4 dark:bg-gray-800 dark:border-gray-700">
            <div className="flex flex-wrap gap-4">
              <div className="flex-1 min-w-[200px]">
                <div className="relative">
                  <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
                  <Input
                    placeholder="搜索用户名或IP..."
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    className="pl-10 dark:bg-gray-900 dark:border-gray-700"
                  />
                </div>
              </div>
              
              <Select value={actionFilter} onValueChange={setActionFilter}>
                <SelectTrigger className="w-[150px] dark:bg-gray-900 dark:border-gray-700">
                  <SelectValue placeholder="操作类型" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">全部操作</SelectItem>
                  <SelectItem value="login">登录</SelectItem>
                  <SelectItem value="logout">登出</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </Card>

          <Card className="dark:bg-gray-800 dark:border-gray-700">
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="border-b dark:border-gray-700 bg-gray-50 dark:bg-gray-900/50">
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">时间</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">用户</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">操作</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">设备</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">IP地址</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">位置</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredLoginRecords.map((record) => (
                    <tr key={record.id} className="border-b dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-700">
                      <td className="p-4 text-sm dark:text-gray-300">{record.timestamp}</td>
                      <td className="p-4">
                        <div className="flex items-center gap-2">
                          <Avatar className="w-8 h-8">
                            <AvatarFallback className="bg-gradient-to-br from-blue-500 to-blue-600 text-white text-xs">
                              {record.username.substring(0, 1)}
                            </AvatarFallback>
                          </Avatar>
                          <div>
                            <div className="text-sm dark:text-white">{record.username}</div>
                            <div className="text-xs text-gray-500 dark:text-gray-400">{record.userId}</div>
                          </div>
                        </div>
                      </td>
                      <td className="p-4">{getActionBadge(record.action, record.status)}</td>
                      <td className="p-4 text-sm dark:text-gray-300">{record.device}</td>
                      <td className="p-4 text-sm dark:text-gray-300">{record.ip}</td>
                      <td className="p-4 text-sm dark:text-gray-300">{record.location}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </Card>
        </TabsContent>
      </Tabs>

      {/* 强制下线确认对话框 */}
      <Dialog open={showKickDialog} onOpenChange={setShowKickDialog}>
        <DialogContent className="dark:bg-gray-800 dark:border-gray-700" aria-describedby={undefined}>
          <DialogHeader>
            <DialogTitle className="dark:text-white">强制下线</DialogTitle>
          </DialogHeader>
          
          <div className="space-y-4">
            <p className="text-sm text-gray-600 dark:text-gray-400">
              确认要强制下线用户 <strong className="dark:text-white">{kickingUser?.username}</strong> 吗？
            </p>
            
            {kickingUser && (
              <div className="p-4 bg-gray-50 dark:bg-gray-900 rounded-lg">
                <div className="space-y-2 text-sm">
                  <div className="flex justify-between">
                    <span className="text-gray-600 dark:text-gray-400">邮箱:</span>
                    <span className="dark:text-white">{kickingUser.email}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-gray-600 dark:text-gray-400">IP地址:</span>
                    <span className="dark:text-white">{kickingUser.ip}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-gray-600 dark:text-gray-400">登录时间:</span>
                    <span className="dark:text-white">{kickingUser.loginTime}</span>
                  </div>
                </div>
              </div>
            )}

            <div className="p-4 bg-orange-50 dark:bg-orange-900/20 rounded-lg border border-orange-200 dark:border-orange-900/50">
              <p className="text-sm text-orange-700 dark:text-orange-400">
                强制下线后，用户将立即退出登录，需要重新登录才能访问系统
              </p>
            </div>
          </div>

          <DialogFooter>
            <Button
              variant="outline"
              onClick={() => setShowKickDialog(false)}
              className="dark:border-gray-600 dark:hover:bg-gray-700"
            >
              取消
            </Button>
            <Button 
              onClick={handleConfirmKick}
              className="bg-red-600 hover:bg-red-700"
            >
              <LogOut className="w-4 h-4 mr-2" />
              确认下线
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}