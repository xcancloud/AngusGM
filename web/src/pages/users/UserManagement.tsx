import { Users, Plus, Search, X, Filter, MoreHorizontal, Eye, Edit, Trash2, UserCheck, UserX, Mail, Phone, Shield, TrendingUp, TrendingDown, Lock, LockOpen, UserPlus, Copy, RefreshCw, Crown } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Input } from '@/components/ui/input';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from '@/components/ui/dropdown-menu';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Pagination, PaginationContent, PaginationItem, PaginationLink, PaginationNext, PaginationPrevious } from '@/components/ui/pagination';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { useState } from 'react';
import { toast } from 'sonner';
import { UserDetail } from './UserDetail';
import { UserInviteDialog } from './UserInviteDialog';
import { EditUserDialog } from './EditUserDialog';

interface User {
  id: string;
  name: string;
  email: string;
  phone: string;
  role: string;
  department: string;
  status: '已激活' | '已禁用' | '待审核';
  avatar: string;
  lastLogin: string;
  createdAt: string;
  isLocked: boolean;
  isOnline: boolean;
  enableStatus: '已启用' | '已禁用';
}

interface UserInvite {
  id: string;
  email: string;
  role: string;
  tenant: string;
  invitedBy: string;
  inviteDate: string;
  expiryDate: string;
  status: '待接受' | '已过期' | '已接受' | '已取消';
}

interface FilterOptions {
  status: string[];
  role: string[];
  department: string[];
  isLocked: 'all' | 'locked' | 'unlocked';
  isOnline: 'all' | 'online' | 'offline';
}

export function UserManagement() {
  const [activeTab, setActiveTab] = useState<'users' | 'invites'>('users');
  const [searchQuery, setSearchQuery] = useState('');
  const [inviteSearchQuery, setInviteSearchQuery] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [inviteCurrentPage, setInviteCurrentPage] = useState(1);
  const [selectedUserId, setSelectedUserId] = useState<string | null>(null);
  const [inviteDialogOpen, setInviteDialogOpen] = useState(false);
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [filters, setFilters] = useState<FilterOptions>({
    status: [],
    role: [],
    department: [],
    isLocked: 'all',
    isOnline: 'all',
  });
  const itemsPerPage = 10;

  const users: User[] = [
    {
      id: 'U000',
      name: '系统管理员',
      email: 'admin@angusgm.com',
      phone: '188****0000',
      role: '超级管理员',
      department: '系统管理',
      status: '已激活',
      avatar: 'https://images.unsplash.com/photo-1652795385761-7ac287d0cd03?w=100',
      lastLogin: '2025-12-03 09:15',
      createdAt: '2024-01-01',
      isLocked: false,
      isOnline: true,
      enableStatus: '已启用',
    },
    {
      id: 'U001',
      name: '张三',
      email: 'zhangsan@example.com',
      phone: '138****1234',
      role: '管理员',
      department: '技术部',
      status: '已激活',
      avatar: 'https://images.unsplash.com/photo-1652795385761-7ac287d0cd03?w=100',
      lastLogin: '2025-10-22 14:30',
      createdAt: '2025-01-15',
      isLocked: false,
      isOnline: true,
      enableStatus: '已启用',
    },
    {
      id: 'U002',
      name: '李四',
      email: 'lisi@example.com',
      phone: '139****5678',
      role: '普通用户',
      department: '市场部',
      status: '已激活',
      avatar: 'https://images.unsplash.com/photo-1652795385761-7ac287d0cd03?w=100',
      lastLogin: '2025-10-22 13:15',
      createdAt: '2025-02-20',
      isLocked: false,
      isOnline: false,
      enableStatus: '已启用',
    },
    {
      id: 'U003',
      name: '王五',
      email: 'wangwu@example.com',
      phone: '137****9012',
      role: '审核员',
      department: '运营部',
      status: '已激活',
      avatar: 'https://images.unsplash.com/photo-1652795385761-7ac287d0cd03?w=100',
      lastLogin: '2025-10-22 10:45',
      createdAt: '2025-03-10',
      isLocked: true,
      isOnline: false,
      enableStatus: '已启用',
    },
    {
      id: 'U004',
      name: '赵六',
      email: 'zhaoliu@example.com',
      phone: '136****3456',
      role: '普通用户',
      department: '财务部',
      status: '待审核',
      avatar: 'https://images.unsplash.com/photo-1652795385761-7ac287d0cd03?w=100',
      lastLogin: '-',
      createdAt: '2025-10-22',
      isLocked: false,
      isOnline: false,
      enableStatus: '已启用',
    },
    {
      id: 'U005',
      name: '孙七',
      email: 'sunqi@example.com',
      phone: '135****7890',
      role: '普通用户',
      department: '人事部',
      status: '已禁用',
      avatar: 'https://images.unsplash.com/photo-1652795385761-7ac287d0cd03?w=100',
      lastLogin: '2025-10-20 16:20',
      createdAt: '2025-04-05',
      isLocked: false,
      isOnline: false,
      enableStatus: '已禁用',
    },
  ];

  const invites: UserInvite[] = [
    {
      id: 'I000',
      email: 'super.admin@angusgm.com',
      role: '超级管理员',
      tenant: 'AngusGM Global',
      invitedBy: '系统管理员',
      inviteDate: '2024-12-01',
      expiryDate: '2024-12-08',
      status: '待接受',
    },
    {
      id: 'I001',
      email: 'john.doe@example.com',
      role: '普通用户',
      tenant: 'TechFlow Inc',
      invitedBy: 'Sarah Johnson',
      inviteDate: '2024-06-01',
      expiryDate: '2024-06-08',
      status: '待接受',
    },
    {
      id: 'I002',
      email: 'jane.smith@example.com',
      role: '编辑',
      tenant: 'Innovate Solutions',
      invitedBy: 'Emily Davis',
      inviteDate: '2024-05-28',
      expiryDate: '2024-06-04',
      status: '待接受',
    },
    {
      id: 'I003',
      email: 'alex.brown@example.com',
      role: '管理员',
      tenant: 'CloudScale Systems',
      invitedBy: 'David Kim',
      inviteDate: '2024-05-15',
      expiryDate: '2024-05-22',
      status: '已过期',
    },
  ];

  const stats = [
    {
      label: '总用户数',
      value: '1,234',
      subtext: '本月新增 45 人',
      icon: Users,
      iconBg: 'bg-blue-500',
      valueColor: 'text-blue-600 dark:text-blue-400',
      trend: '+12%',
      trendDirection: 'up',
    },
    {
      label: '已激活',
      value: '1,156',
      subtext: '占比 93.7%',
      icon: UserCheck,
      iconBg: 'bg-green-500',
      valueColor: 'text-green-600 dark:text-green-400',
      trend: '+8%',
      trendDirection: 'up',
    },
    {
      label: '待审核',
      value: '28',
      subtext: '占比 2.3%',
      icon: Eye,
      iconBg: 'bg-orange-500',
      valueColor: 'text-orange-600 dark:text-orange-400',
      trend: '-5%',
      trendDirection: 'down',
    },
    {
      label: '已禁用',
      value: '50',
      subtext: '占比 4.1%',
      icon: UserX,
      iconBg: 'bg-red-500',
      valueColor: 'text-red-600 dark:text-red-400',
      trend: '+2%',
      trendDirection: 'up',
    },
  ];

  const filteredUsers = users.filter(user => {
    // 搜索过滤
    const matchesSearch = 
      user.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      user.email.toLowerCase().includes(searchQuery.toLowerCase()) ||
      user.department.toLowerCase().includes(searchQuery.toLowerCase());
    
    if (!matchesSearch) return false;

    // 状态过滤
    if (filters.status.length > 0 && !filters.status.includes(user.status)) {
      return false;
    }

    // 角色过滤
    if (filters.role.length > 0 && !filters.role.includes(user.role)) {
      return false;
    }

    // 部门过滤
    if (filters.department.length > 0 && !filters.department.includes(user.department)) {
      return false;
    }

    // 锁定状态过滤
    if (filters.isLocked === 'locked' && !user.isLocked) {
      return false;
    }
    if (filters.isLocked === 'unlocked' && user.isLocked) {
      return false;
    }

    // 在线状态过滤
    if (filters.isOnline === 'online' && !user.isOnline) {
      return false;
    }
    if (filters.isOnline === 'offline' && user.isOnline) {
      return false;
    }

    return true;
  });

  const filteredInvites = invites.filter(invite => {
    // 搜索过滤
    const matchesSearch = 
      invite.email.toLowerCase().includes(inviteSearchQuery.toLowerCase()) ||
      invite.role.toLowerCase().includes(inviteSearchQuery.toLowerCase()) ||
      invite.status.toLowerCase().includes(inviteSearchQuery.toLowerCase());
    
    if (!matchesSearch) return false;

    return true;
  });

  const totalPages = Math.ceil(filteredUsers.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const currentUsers = filteredUsers.slice(startIndex, startIndex + itemsPerPage);

  const totalInvitePages = Math.ceil(filteredInvites.length / itemsPerPage);
  const inviteStartIndex = (inviteCurrentPage - 1) * itemsPerPage;
  const currentInvites = filteredInvites.slice(inviteStartIndex, inviteStartIndex + itemsPerPage);

  const handleAction = (action: string, userName: string) => {
    toast.success(`${action}: ${userName}`);
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case '已激活':
        return 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400';
      case '已禁用':
        return 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400';
      case '待审核':
        return 'bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-400';
      default:
        return 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-400';
    }
  };

  const getInviteStatusColor = (status: string) => {
    switch (status) {
      case '待接受':
        return 'bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-400';
      case '已过期':
        return 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-400';
      case '已接受':
        return 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400';
      case '已取消':
        return 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400';
      default:
        return 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-400';
    }
  };

  const getRoleBadgeColor = (role: string) => {
    switch (role) {
      case '超级管理员':
        return 'bg-gradient-to-r from-red-500 to-orange-500 text-white dark:from-red-600 dark:to-orange-600';
      case '管理员':
        return 'bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-400';
      case '审核员':
        return 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400';
      case '编辑':
        return 'bg-cyan-100 text-cyan-700 dark:bg-cyan-900/30 dark:text-cyan-400';
      default:
        return 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-400';
    }
  };

  // Show user detail view if a user is selected
  if (selectedUserId) {
    return <UserDetail userId={selectedUserId} onBack={() => setSelectedUserId(null)} />;
  }

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div>
        <h1 className="text-2xl dark:text-white mb-2">用户管理</h1>
        <p className="text-sm text-gray-600 dark:text-gray-400">
          管理系统用户账号和权限
        </p>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {stats.map((stat, index) => {
          const Icon = stat.icon;
          return (
            <Card key={index} className="relative overflow-hidden dark:bg-gray-800 dark:border-gray-700">
              <div className="absolute top-0 right-0 w-32 h-32 opacity-10">
                <div className={`w-full h-full ${stat.iconBg} rounded-full blur-3xl transform translate-x-8 -translate-y-8`}></div>
              </div>
              <div className="p-6 relative">
                <div className="flex items-center justify-between mb-4">
                  <div className={`p-3 rounded-xl ${stat.iconBg}/10`}>
                    <Icon className={`w-6 h-6 ${stat.iconBg.replace('bg-', 'text-')}`} />
                  </div>
                  {stat.trend && (
                    <div className={`flex items-center gap-1 text-sm ${
                      stat.trendDirection === 'up' ? 'text-green-600 dark:text-green-400' : 'text-red-600 dark:text-red-400'
                    }`}>
                      <TrendingUp className={`w-4 h-4 ${stat.trendDirection === 'down' ? 'rotate-180' : ''}`} />
                      <span>{stat.trend}</span>
                    </div>
                  )}
                </div>
                <div className={`text-3xl mb-2 ${stat.valueColor}`}>
                  {stat.value}
                </div>
                <div className="text-sm text-gray-600 dark:text-gray-400 mb-1">
                  {stat.label}
                </div>
                <div className="text-xs text-gray-500 dark:text-gray-500">
                  {stat.subtext}
                </div>
              </div>
            </Card>
          );
        })}
      </div>

      {/* Tabs */}
      <Tabs defaultValue="users" className="space-y-6">
        <TabsList className="dark:bg-gray-800 dark:border-gray-700">
          <TabsTrigger value="users" className="dark:data-[state=active]:bg-gray-700">
            <Users className="w-4 h-4 mr-2" />
            用户列表
          </TabsTrigger>
          <TabsTrigger value="invites" className="dark:data-[state=active]:bg-gray-700">
            <UserPlus className="w-4 h-4 mr-2" />
            邀请记录
          </TabsTrigger>
        </TabsList>

        {/* Users Table */}
        <TabsContent value="users" className="space-y-6">
          {/* Filters and Actions */}
          <Card className="p-4 dark:bg-gray-800 dark:border-gray-700">
            <div className="flex items-center justify-between gap-3">
              {/* Left: Search and Filters */}
              <div className="flex items-center gap-3">
                <div className="relative w-[390px]">
                  <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400 dark:text-gray-500" />
                  <Input
                    type="text"
                    placeholder="搜索用户名称、邮箱、部门..."
                    value={searchQuery}
                    onChange={(e) => {
                      setSearchQuery(e.target.value);
                      setCurrentPage(1);
                    }}
                    className="pl-10 pr-10 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-100"
                  />
                  {searchQuery && (
                    <button
                      onClick={() => {
                        setSearchQuery('');
                        setCurrentPage(1);
                      }}
                      className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300"
                    >
                      <X className="w-4 h-4" />
                    </button>
                  )}
                </div>

                <Select 
                  value={filters.status.length > 0 ? filters.status[0] : 'all'} 
                  onValueChange={(value) => {
                    if (value === 'all') {
                      setFilters({ ...filters, status: [] });
                    } else {
                      setFilters({ ...filters, status: [value] });
                    }
                    setCurrentPage(1);
                  }}
                >
                  <SelectTrigger className="w-[150px] dark:bg-gray-800 dark:border-gray-700 dark:text-gray-100">
                    <SelectValue placeholder="用户状态" />
                  </SelectTrigger>
                  <SelectContent className="dark:bg-gray-800 dark:border-gray-700">
                    <SelectItem value="all" className="dark:text-white dark:focus:bg-gray-700">全部状态</SelectItem>
                    <SelectItem value="已激活" className="dark:text-white dark:focus:bg-gray-700">已激活</SelectItem>
                    <SelectItem value="已禁用" className="dark:text-white dark:focus:bg-gray-700">已禁用</SelectItem>
                    <SelectItem value="待审核" className="dark:text-white dark:focus:bg-gray-700">待审核</SelectItem>
                  </SelectContent>
                </Select>

                <Select 
                  value={filters.isOnline} 
                  onValueChange={(value: 'all' | 'online' | 'offline') => {
                    setFilters({ ...filters, isOnline: value });
                    setCurrentPage(1);
                  }}
                >
                  <SelectTrigger className="w-[150px] dark:bg-gray-800 dark:border-gray-700 dark:text-gray-100">
                    <SelectValue placeholder="在线状态" />
                  </SelectTrigger>
                  <SelectContent className="dark:bg-gray-800 dark:border-gray-700">
                    <SelectItem value="all" className="dark:text-white dark:focus:bg-gray-700">全部</SelectItem>
                    <SelectItem value="online" className="dark:text-white dark:focus:bg-gray-700">在线</SelectItem>
                    <SelectItem value="offline" className="dark:text-white dark:focus:bg-gray-700">离线</SelectItem>
                  </SelectContent>
                </Select>

                {(filters.status.length > 0 || filters.isOnline !== 'all') && (
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={() => {
                      setFilters({
                        status: [],
                        role: [],
                        department: [],
                        isLocked: 'all',
                        isOnline: 'all',
                      });
                      setCurrentPage(1);
                      toast.success('已清除所有筛选条件');
                    }}
                    className="text-sm text-blue-600 dark:text-blue-400 hover:text-blue-700 dark:hover:text-blue-300"
                  >
                    <X className="w-4 h-4 mr-1" />
                    清除筛选
                  </Button>
                )}
              </div>

              {/* Right: Actions */}
              <div className="flex items-center gap-3">
                <Button 
                  variant="outline" 
                  size="sm" 
                  className="dark:bg-gray-800 dark:border-gray-700 dark:text-gray-300"
                  onClick={() => setInviteDialogOpen(true)}
                >
                  <UserPlus className="w-4 h-4 mr-2" />
                  邀请用户
                </Button>
                <Button 
                  size="sm" 
                  onClick={() => setEditDialogOpen(true)}
                  className="bg-blue-500 hover:bg-blue-600 text-white"
                >
                  <Plus className="w-4 h-4 mr-2" />
                  添加用户
                </Button>
              </div>
            </div>
          </Card>

          <Card className="dark:bg-gray-800 dark:border-gray-700">
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="border-b dark:border-gray-700">
                  <tr>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">ID</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">用户</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">联系方式</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">部门</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">角色</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">状态</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">加入时间</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">最后登录</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">操作</th>
                  </tr>
                </thead>
                <tbody>
                  {currentUsers.map((user) => (
                    <tr key={user.id} className="border-b dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-700">
                      <td className="p-4">
                        <span className="text-sm text-gray-700 dark:text-gray-300 font-mono">{user.id}</span>
                      </td>
                      <td className="p-4">
                        <div className="flex items-center gap-3">
                          <Avatar className="w-10 h-10 rounded-full overflow-hidden">
                            <AvatarImage src={user.avatar} alt={user.name} className="object-cover" />
                            <AvatarFallback className="bg-blue-500 text-white">
                              {user.name.slice(0, 2)}
                            </AvatarFallback>
                          </Avatar>
                          <div>
                            <div className="flex items-center gap-2">
                              <button 
                                onClick={() => setSelectedUserId(user.id)}
                                className="dark:text-white hover:text-blue-600 dark:hover:text-blue-400 cursor-pointer"
                              >
                                {user.name}
                              </button>
                              {user.isLocked && (
                                <Lock className="w-3.5 h-3.5 text-red-500 dark:text-red-400" />
                              )}
                            </div>
                            <div className="flex items-center gap-1 mt-1">
                              <div className={`w-2 h-2 rounded-full ${user.isOnline ? 'bg-green-500' : 'bg-gray-400'}`}></div>
                              <span className="text-xs text-gray-500 dark:text-gray-400">
                                {user.isOnline ? '在线' : '离线'}
                              </span>
                            </div>
                          </div>
                        </div>
                      </td>
                      <td className="p-4">
                        <div className="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400 mb-1">
                          <Mail className="w-3.5 h-3.5" />
                          {user.email}
                        </div>
                        <div className="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400">
                          <Phone className="w-3.5 h-3.5" />
                          {user.phone}
                        </div>
                      </td>
                      <td className="p-4">
                        <span className="text-sm text-gray-700 dark:text-gray-300">{user.department}</span>
                      </td>
                      <td className="p-4">
                        <Badge className={`text-xs ${getRoleBadgeColor(user.role)} border-0 ${user.role === '超级管理员' ? 'flex items-center gap-1 w-fit' : ''}`}>
                          {user.role === '超级管理员' && <Crown className="w-3 h-3" />}
                          {user.role}
                        </Badge>
                      </td>
                      <td className="p-4">
                        <div className="space-y-1.5">
                          <Badge className={`text-xs ${getStatusColor(user.status)} border-0`}>
                            {user.status}
                          </Badge>
                          <Badge className={`text-xs ${user.enableStatus === '已启用' ? 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400' : 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-400'} border-0 block w-fit`}>
                            {user.enableStatus}
                          </Badge>
                        </div>
                      </td>
                      <td className="p-4">
                        <span className="text-sm text-gray-600 dark:text-gray-400">{user.createdAt}</span>
                      </td>
                      <td className="p-4">
                        <span className="text-sm text-gray-600 dark:text-gray-400">{user.lastLogin}</span>
                      </td>
                      <td className="p-4">
                        <DropdownMenu>
                          <DropdownMenuTrigger asChild>
                            <button className="p-1.5 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg">
                              <MoreHorizontal className="w-4 h-4 text-gray-600 dark:text-gray-400" />
                            </button>
                          </DropdownMenuTrigger>
                          <DropdownMenuContent align="end" className="dark:bg-gray-800 dark:border-gray-700">
                            <DropdownMenuItem onClick={() => setSelectedUserId(user.id)} className="dark:text-gray-300">
                              <Eye className="w-4 h-4 mr-2" />
                              查看详情
                            </DropdownMenuItem>
                            <DropdownMenuItem onClick={() => setEditDialogOpen(true)} className="dark:text-gray-300">
                              <Edit className="w-4 h-4 mr-2" />
                              编辑
                            </DropdownMenuItem>
                            <DropdownMenuItem onClick={() => handleAction('重置密码', user.name)} className="dark:text-gray-300">
                              <Shield className="w-4 h-4 mr-2" />
                              重置密码
                            </DropdownMenuItem>
                            <DropdownMenuItem onClick={() => handleAction('删除', user.name)} className="text-red-600 dark:text-red-400">
                              <Trash2 className="w-4 h-4 mr-2" />
                              删除
                            </DropdownMenuItem>
                          </DropdownMenuContent>
                        </DropdownMenu>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {/* Pagination */}
            {totalPages > 1 && (
              <div className="flex items-center justify-center p-4 border-t dark:border-gray-700">
                <Pagination>
                  <PaginationContent>
                    <PaginationItem>
                      <PaginationPrevious 
                        onClick={() => setCurrentPage(prev => Math.max(1, prev - 1))}
                        className={currentPage === 1 ? 'pointer-events-none opacity-50' : 'cursor-pointer'}
                      >
                        上一页
                      </PaginationPrevious>
                    </PaginationItem>
                    {Array.from({ length: totalPages }, (_, i) => i + 1).map((page) => (
                      <PaginationItem key={page}>
                        <PaginationLink
                          onClick={() => setCurrentPage(page)}
                          isActive={currentPage === page}
                          className="cursor-pointer"
                        >
                          {page}
                        </PaginationLink>
                      </PaginationItem>
                    ))}
                    <PaginationItem>
                      <PaginationNext 
                        onClick={() => setCurrentPage(prev => Math.min(totalPages, prev + 1))}
                        className={currentPage === totalPages ? 'pointer-events-none opacity-50' : 'cursor-pointer'}
                      >
                        下一页
                      </PaginationNext>
                    </PaginationItem>
                  </PaginationContent>
                </Pagination>
              </div>
            )}
          </Card>
        </TabsContent>

        {/* Invites Table */}
        <TabsContent value="invites" className="space-y-6">
          {/* Invite Search */}
          <Card className="p-4 dark:bg-gray-800 dark:border-gray-700">
            <div className="relative w-[390px]">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400 dark:text-gray-500" />
              <Input
                type="text"
                placeholder="搜索邀请邮箱..."
                value={inviteSearchQuery}
                onChange={(e) => {
                  setInviteSearchQuery(e.target.value);
                  setInviteCurrentPage(1);
                }}
                className="pl-10 pr-10 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-100"
              />
              {inviteSearchQuery && (
                <button
                  onClick={() => {
                    setInviteSearchQuery('');
                    setInviteCurrentPage(1);
                  }}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300"
                >
                  <X className="w-4 h-4" />
                </button>
              )}
            </div>
          </Card>

          <Card className="dark:bg-gray-800 dark:border-gray-700">
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="border-b dark:border-gray-700">
                  <tr>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">ID</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">邮箱</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">角色</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">租户</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">邀请人</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">邀请日期</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">过期日期</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">状态</th>
                  </tr>
                </thead>
                <tbody>
                  {currentInvites.map((invite) => (
                    <tr key={invite.id} className="border-b dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-700">
                      <td className="p-4">
                        <span className="text-sm text-gray-700 dark:text-gray-300 font-mono">{invite.id}</span>
                      </td>
                      <td className="p-4">
                        <span className="text-sm text-gray-700 dark:text-gray-300">{invite.email}</span>
                      </td>
                      <td className="p-4">
                        <Badge className={`text-xs ${getRoleBadgeColor(invite.role)} border-0 ${invite.role === '超级管理员' ? 'flex items-center gap-1 w-fit' : ''}`}>
                          {invite.role === '超级��理员' && <Crown className="w-3 h-3" />}
                          {invite.role}
                        </Badge>
                      </td>
                      <td className="p-4">
                        <span className="text-sm text-gray-700 dark:text-gray-300">{invite.tenant}</span>
                      </td>
                      <td className="p-4">
                        <span className="text-sm text-gray-700 dark:text-gray-300">{invite.invitedBy}</span>
                      </td>
                      <td className="p-4">
                        <span className="text-sm text-gray-600 dark:text-gray-400">{invite.inviteDate}</span>
                      </td>
                      <td className="p-4">
                        <span className="text-sm text-gray-600 dark:text-gray-400">{invite.expiryDate}</span>
                      </td>
                      <td className="p-4">
                        <Badge className={`text-xs ${getInviteStatusColor(invite.status)} border-0`}>
                          {invite.status}
                        </Badge>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {/* Pagination */}
            {totalInvitePages > 1 && (
              <div className="flex items-center justify-center p-4 border-t dark:border-gray-700">
                <Pagination>
                  <PaginationContent>
                    <PaginationItem>
                      <PaginationPrevious 
                        onClick={() => setInviteCurrentPage(prev => Math.max(1, prev - 1))}
                        className={inviteCurrentPage === 1 ? 'pointer-events-none opacity-50' : 'cursor-pointer'}
                      >
                        上一页
                      </PaginationPrevious>
                    </PaginationItem>
                    {Array.from({ length: totalInvitePages }, (_, i) => i + 1).map((page) => (
                      <PaginationItem key={page}>
                        <PaginationLink
                          onClick={() => setInviteCurrentPage(page)}
                          isActive={inviteCurrentPage === page}
                          className="cursor-pointer"
                        >
                          {page}
                        </PaginationLink>
                      </PaginationItem>
                    ))}
                    <PaginationItem>
                      <PaginationNext 
                        onClick={() => setInviteCurrentPage(prev => Math.min(totalInvitePages, prev + 1))}
                        className={inviteCurrentPage === totalInvitePages ? 'pointer-events-none opacity-50' : 'cursor-pointer'}
                      >
                        下一页
                      </PaginationNext>
                    </PaginationItem>
                  </PaginationContent>
                </Pagination>
              </div>
            )}
          </Card>
        </TabsContent>
      </Tabs>

      {/* Dialogs */}
      <UserInviteDialog
        open={inviteDialogOpen}
        onOpenChange={setInviteDialogOpen}
      />

      <EditUserDialog
        open={editDialogOpen}
        onOpenChange={setEditDialogOpen}
        mode="create"
        onSave={(userData) => {
          toast.success('用户已创建');
        }}
      />
    </div>
  );
}