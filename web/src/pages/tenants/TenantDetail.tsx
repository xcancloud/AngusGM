import { useState } from 'react';
import { ChevronRight, Edit, Trash2, Building2, Users, Calendar, Clock, MapPin, Mail, Phone, Activity, Crown, UserPlus, Shield, Lock, FileText, TrendingUp, TrendingDown, Percent, Award, FolderKanban, GitBranch, Target } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { toast } from 'sonner';
import { TenantDialog } from '@/pages/tenants/TenantDialog';
import { ConfirmDialog } from '@/components/gm/ConfirmDialog';
import { AssignRoleDialog } from '@/components/gm/AssignRoleDialog';
import { BarChart, Bar, PieChart, Pie, Cell, LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

interface Tenant {
  id: string;
  name: string;
  code: string;
  type: '个人' | '企业' | '未知';
  accountType: 'main' | 'sub';
  adminName: string;
  adminEmail: string;
  adminPhone: string;
  userCount: number;
  departmentCount: number;
  status: '已启用' | '已禁用';
  address: string;
  createdAt: string;
  expireDate: string;
}

interface TenantDetailProps {
  tenant: Tenant;
  onBack: () => void;
  onViewUser?: (userId: string) => void;
  onViewDepartment?: (departmentId: string) => void;
}

export function TenantDetail({ tenant, onBack, onViewUser, onViewDepartment }: TenantDetailProps) {
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [toggleStatusDialogOpen, setToggleStatusDialogOpen] = useState(false);
  const [assignRoleDialogOpen, setAssignRoleDialogOpen] = useState(false);
  const [revokeRoleDialogOpen, setRevokeRoleDialogOpen] = useState(false);
  const [selectedRole, setSelectedRole] = useState<{ id: string; name: string } | null>(null);

  const getTypeColor = (type: string) => {
    switch (type) {
      case '企业':
        return 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400';
      case '个人':
        return 'bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-400';
      default:
        return 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-400';
    }
  };

  // Mock users data
  const users = [
    {
      id: 'U001',
      name: '张三',
      avatar: 'https://images.unsplash.com/photo-1652795385761-7ac287d0cd03?w=100',
      email: 'zhangsan@liaobangong.com',
      department: '技术部',
      role: '管理员',
      status: '在线',
      joinDate: '2024-01-15',
    },
    {
      id: 'U002',
      name: '李四',
      avatar: 'https://images.unsplash.com/photo-1652795385761-7ac287d0cd03?w=100',
      email: 'lisi@liaobangong.com',
      department: '产品部',
      role: '成员',
      status: '离线',
      joinDate: '2024-02-20',
    },
    {
      id: 'U003',
      name: '王五',
      avatar: 'https://images.unsplash.com/photo-1652795385761-7ac287d0cd03?w=100',
      email: 'wangwu@liaobangong.com',
      department: '技术部',
      role: '成员',
      status: '在线',
      joinDate: '2024-03-10',
    },
    {
      id: 'U004',
      name: '赵六',
      avatar: 'https://images.unsplash.com/photo-1652795385761-7ac287d0cd03?w=100',
      email: 'zhaoliu@liaobangong.com',
      department: '设计部',
      role: '成员',
      status: '忙碌',
      joinDate: '2024-04-05',
    },
  ];

  // Mock departments data
  const departments = [
    {
      id: 'D001',
      name: '技术部',
      code: 'TECH',
      parentName: '总部',
      leader: '张三',
      memberCount: 45,
      subDepartmentCount: 3,
      createdAt: '2024-01-15',
    },
    {
      id: 'D002',
      name: '产品部',
      code: 'PRODUCT',
      parentName: '总部',
      leader: '李四',
      memberCount: 28,
      subDepartmentCount: 2,
      createdAt: '2024-01-20',
    },
    {
      id: 'D003',
      name: '设计部',
      code: 'DESIGN',
      parentName: '总部',
      leader: '王五',
      memberCount: 18,
      subDepartmentCount: 1,
      createdAt: '2024-02-01',
    },
  ];

  // Mock authorized roles data
  const authorizedRoles = [
    {
      id: 'R001',
      name: '租户管理员',
      description: '租户的最高管理权限',
      type: '管理角色',
      permissions: ['用户管理', '部门管理', '应用管理', '系统设置'],
      grantedAt: '2024-01-15',
      grantedBy: '系统管理员',
    },
    {
      id: 'R002',
      name: '部门管理员',
      description: '负责部门的管理工作',
      type: '管理角色',
      permissions: ['查看部门', '编辑部门', '管理成员'],
      grantedAt: '2024-02-01',
      grantedBy: '张三',
    },
    {
      id: 'R003',
      name: '普通用户',
      description: '基础用户权限',
      type: '业务角色',
      permissions: ['查看应用', '使用基础功能'],
      grantedAt: '2024-01-15',
      grantedBy: '系统管理员',
    },
  ];

  // User statistics data
  const userStats = [
    {
      label: '总用户数',
      value: tenant.userCount.toString(),
      change: '+12',
      changeType: 'up' as const,
      icon: Users,
      color: 'blue',
    },
    {
      label: '活跃用户',
      value: '286',
      change: '+8.5%',
      changeType: 'up' as const,
      icon: TrendingUp,
      color: 'green',
    },
    {
      label: '管理员',
      value: '15',
      change: '+2',
      changeType: 'up' as const,
      icon: Shield,
      color: 'purple',
    },
    {
      label: '本月新增',
      value: '24',
      change: '+18%',
      changeType: 'up' as const,
      icon: UserPlus,
      color: 'orange',
    },
  ];

  const userDepartmentData = [
    { name: '技术部', value: 98, color: '#3b82f6' },
    { name: '产品部', value: 76, color: '#8b5cf6' },
    { name: '设计部', value: 54, color: '#ec4899' },
    { name: '运营部', value: 43, color: '#f59e0b' },
    { name: '其他', value: 57, color: '#6b7280' },
  ];

  const userGrowthData = [
    { month: '1月', count: 245 },
    { month: '2月', count: 265 },
    { month: '3月', count: 278 },
    { month: '4月', count: 289 },
    { month: '5月', count: 301 },
    { month: '6月', count: 328 },
  ];

  // Department statistics data
  const departmentStats = [
    {
      label: '总部门数',
      value: tenant.departmentCount.toString(),
      change: '+3',
      changeType: 'up' as const,
      icon: Building2,
      color: 'blue',
    },
    {
      label: '一级部门',
      value: '8',
      change: '+1',
      changeType: 'up' as const,
      icon: Building2,
      color: 'green',
    },
    {
      label: '平均人数',
      value: '32',
      change: '+2.3%',
      changeType: 'up' as const,
      icon: Users,
      color: 'purple',
    },
    {
      label: '本月新建',
      value: '3',
      change: '持平',
      changeType: 'neutral' as const,
      icon: TrendingUp,
      color: 'orange',
    },
  ];

  const departmentSizeData = [
    { name: '50人以上', value: 5, color: '#3b82f6' },
    { name: '30-50人', value: 12, color: '#8b5cf6' },
    { name: '10-30人', value: 18, color: '#ec4899' },
    { name: '10人以下', value: 10, color: '#f59e0b' },
  ];

  const departmentLevelData = [
    { level: '一级部门', count: 8 },
    { level: '二级部门', count: 18 },
    { level: '三级部门', count: 15 },
    { level: '四级部门', count: 4 },
  ];

  // Role statistics data
  const roleStats = [
    {
      label: '总角色数',
      value: '12',
      change: '+2',
      changeType: 'up' as const,
      icon: Crown,
      color: 'blue',
    },
    {
      label: '管理角色',
      value: '5',
      change: '+1',
      changeType: 'up' as const,
      icon: Shield,
      color: 'purple',
    },
    {
      label: '业务角色',
      value: '7',
      change: '+1',
      changeType: 'up' as const,
      icon: Award,
      color: 'green',
    },
    {
      label: '授权用户',
      value: '285',
      change: '+15',
      changeType: 'up' as const,
      icon: Users,
      color: 'orange',
    },
  ];

  const roleDistributionData = [
    { name: '管理角色', value: 42, color: '#8b5cf6' },
    { name: '业务角色', value: 58, color: '#3b82f6' },
  ];

  const roleUsageData = [
    { name: '租户管理员', users: 15, permissions: 28 },
    { name: '部门管理员', users: 45, permissions: 18 },
    { name: '项目管理员', users: 38, permissions: 22 },
    { name: '开发人员', users: 98, permissions: 15 },
    { name: '普通用户', users: 132, permissions: 8 },
  ];

  // Group statistics data
  const groupStats = [
    {
      label: '总组数',
      value: '18',
      change: '+4',
      changeType: 'up' as const,
      icon: FolderKanban,
      color: 'blue',
    },
    {
      label: '项目组',
      value: '12',
      change: '+3',
      changeType: 'up' as const,
      icon: Target,
      color: 'green',
    },
    {
      label: '跨部门组',
      value: '6',
      change: '+1',
      changeType: 'up' as const,
      icon: GitBranch,
      color: 'purple',
    },
    {
      label: '组成员',
      value: '245',
      change: '+18',
      changeType: 'up' as const,
      icon: Users,
      color: 'orange',
    },
  ];

  const groupTypeData = [
    { name: '项目组', value: 12, color: '#3b82f6' },
    { name: '部门组', value: 6, color: '#8b5cf6' },
    { name: '临时组', value: 5, color: '#ec4899' },
  ];

  const groupMemberData = [
    { name: '开发组', members: 45 },
    { name: '测试组', members: 32 },
    { name: '产品组', members: 28 },
    { name: '设计组', members: 24 },
    { name: '运营组', members: 18 },
  ];

  // Activity statistics data
  const activityStats = [
    {
      label: '今日活动',
      value: '156',
      change: '+23',
      changeType: 'up' as const,
      icon: Activity,
      color: 'blue',
    },
    {
      label: '用户操作',
      value: '89',
      change: '+12',
      changeType: 'up' as const,
      icon: Users,
      color: 'green',
    },
    {
      label: '系统事件',
      value: '45',
      change: '+8',
      changeType: 'up' as const,
      icon: Shield,
      color: 'purple',
    },
    {
      label: '异常告警',
      value: '3',
      change: '-2',
      changeType: 'down' as const,
      icon: TrendingDown,
      color: 'red',
    },
  ];

  const activityTrendData = [
    { day: '周一', count: 128 },
    { day: '周二', count: 145 },
    { day: '周三', count: 132 },
    { day: '周四', count: 156 },
    { day: '周五', count: 168 },
    { day: '周六', count: 92 },
    { day: '周日', count: 85 },
  ];

  const activityTypeData = [
    { name: '用户管理', value: 45, color: '#3b82f6' },
    { name: '权限变更', value: 32, color: '#8b5cf6' },
    { name: '部门操作', value: 28, color: '#ec4899' },
    { name: '系统配置', value: 18, color: '#f59e0b' },
    { name: '其他', value: 33, color: '#6b7280' },
  ];

  // Mock activity logs
  const activityLogs = [
    { id: 'L001', user: '张三', action: '添加了新用户', target: '李四', time: '2024-12-03 14:30' },
    { id: 'L002', user: '李四', action: '创建了新部门', target: '产品部', time: '2024-12-02 10:15' },
    { id: 'L003', user: '王五', action: '更新了租户信息', target: '', time: '2024-12-01 16:45' },
    { id: 'L004', user: '张三', action: '删除了部门', target: '临时部门', time: '2024-11-30 09:20' },
  ];

  const getRoleTypeColor = (type: string) => {
    switch (type) {
      case '管理角色':
        return 'bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-400';
      case '业务角色':
        return 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400';
      default:
        return 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-400';
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case '在线':
        return 'bg-green-500';
      case '忙碌':
        return 'bg-yellow-500';
      case '离线':
        return 'bg-gray-400';
      default:
        return 'bg-gray-400';
    }
  };

  return (
    <div className="space-y-6">
      {/* Breadcrumb */}
      <div className="flex items-center gap-2 text-sm">
        <button
          onClick={onBack}
          className="text-gray-600 dark:text-gray-400 hover:text-blue-600 dark:hover:text-blue-400"
        >
          租户管理
        </button>
        <ChevronRight className="w-4 h-4 text-gray-400" />
        <span className="text-gray-900 dark:text-white">详情</span>
      </div>

      {/* Tenant Header Card */}
      <Card className="dark:bg-gray-800 dark:border-gray-700">
        <div className="p-6">
          <div className="flex items-start justify-between">
            {/* Left: Tenant Info */}
            <div className="flex items-start gap-6">
              <div className="w-24 h-24 rounded-xl bg-gradient-to-br from-blue-500 to-blue-600 flex items-center justify-center">
                <Building2 className="w-12 h-12 text-white" />
              </div>
              <div>
                <div className="flex items-center gap-3 mb-2">
                  <h1 className="text-2xl dark:text-white">{tenant.name}</h1>
                  <Badge className={`${getTypeColor(tenant.type)} border-0`}>
                    {tenant.type}
                  </Badge>
                  <Badge
                    className={`text-xs ${
                      tenant.accountType === 'main'
                        ? 'bg-amber-100 text-amber-700 dark:bg-amber-900/30 dark:text-amber-400'
                        : 'bg-gray-100 text-gray-700 dark:bg-gray-700 dark:text-gray-400'
                    } border-0`}
                  >
                    {tenant.accountType === 'main' ? '主账号' : '子账号'}
                  </Badge>
                  <Badge
                    variant={tenant.status === '已启用' ? 'default' : 'secondary'}
                    className={tenant.status === '已启用' ? 'bg-green-500 hover:bg-green-600' : 'bg-red-500 hover:bg-red-600'}
                  >
                    {tenant.status}
                  </Badge>
                </div>
                <div className="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400 mb-3">
                  <span className="font-mono text-gray-700 dark:text-gray-300">编码: {tenant.code}</span>
                </div>
                <div className="grid grid-cols-2 gap-x-6 gap-y-2 text-sm">
                  <div className="flex items-center gap-2">
                    <MapPin className="w-4 h-4 text-gray-400" />
                    <span className="text-gray-600 dark:text-gray-400">{tenant.address}</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <Users className="w-4 h-4 text-gray-400" />
                    <span className="text-gray-600 dark:text-gray-400">
                      <span className="dark:text-white">{tenant.userCount}</span> 名用户
                    </span>
                  </div>
                  <div className="flex items-center gap-2">
                    <Mail className="w-4 h-4 text-gray-400" />
                    <span className="text-gray-600 dark:text-gray-400">{tenant.adminEmail}</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <Building2 className="w-4 h-4 text-gray-400" />
                    <span className="text-gray-600 dark:text-gray-400">
                      <span className="dark:text-white">{tenant.departmentCount}</span> 个部门
                    </span>
                  </div>
                  <div className="flex items-center gap-2">
                    <Phone className="w-4 h-4 text-gray-400" />
                    <span className="text-gray-600 dark:text-gray-400">{tenant.adminPhone}</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <Calendar className="w-4 h-4 text-gray-400" />
                    <span className="text-gray-600 dark:text-gray-400">创建于 {tenant.createdAt}</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <Clock className="w-4 h-4 text-gray-400" />
                    <span className="text-gray-600 dark:text-gray-400">到期时间 {tenant.expireDate}</span>
                  </div>
                </div>
              </div>
            </div>

            {/* Right: Action Buttons */}
            <div className="flex gap-2">
              <Button
                variant="outline"
                onClick={() => setEditDialogOpen(true)}
                className="dark:border-gray-600 dark:text-gray-300 dark:hover:bg-gray-700"
              >
                <Edit className="w-4 h-4 mr-2" />
                编辑
              </Button>
              <Button
                variant="outline"
                onClick={() => setToggleStatusDialogOpen(true)}
                className="dark:border-gray-600 dark:text-gray-300 dark:hover:bg-gray-700"
              >
                <Lock className="w-4 h-4 mr-2" />
                {tenant.status === '已启用' ? '禁用' : '启用'}
              </Button>
              {tenant.accountType === 'sub' && (
                <Button
                  variant="outline"
                  onClick={() => setDeleteDialogOpen(true)}
                  className="border-red-200 text-red-600 hover:bg-red-50 dark:border-red-800 dark:text-red-400 dark:hover:bg-red-900/20"
                >
                  <Trash2 className="w-4 h-4 mr-2" />
                  删除
                </Button>
              )}
            </div>
          </div>
        </div>
      </Card>

      {/* Tabs Section */}
      <Tabs defaultValue="users" className="space-y-6">
        <TabsList className="dark:bg-gray-800">
          <TabsTrigger value="users">
            <Users className="w-4 h-4 mr-2" />
            用户统计
          </TabsTrigger>
          <TabsTrigger value="departments">
            <Building2 className="w-4 h-4 mr-2" />
            部门统计
          </TabsTrigger>
          <TabsTrigger value="groups">
            <FolderKanban className="w-4 h-4 mr-2" />
            组统计
          </TabsTrigger>
          <TabsTrigger value="activity">
            <Activity className="w-4 h-4 mr-2" />
            活动统计
          </TabsTrigger>
        </TabsList>

        {/* Users Tab */}
        <TabsContent value="users" className="space-y-6">
          {/* Statistics Cards */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            {userStats.map((stat, index) => {
              const iconBg = `bg-${stat.color}-500`;
              return (
                <Card key={index} className="dark:bg-gray-800 dark:border-gray-700">
                  <div className="p-6">
                    <div className="flex items-center justify-between mb-4">
                      <div className={`p-3 rounded-xl bg-${stat.color}-500/10`}>
                        <stat.icon className={`w-6 h-6 text-${stat.color}-600 dark:text-${stat.color}-400`} />
                      </div>
                      <div className={`flex items-center gap-1 text-sm ${
                        stat.changeType === 'up' ? 'text-green-600 dark:text-green-400' : 
                        stat.changeType === 'down' ? 'text-red-600 dark:text-red-400' : 
                        'text-gray-600 dark:text-gray-400'
                      }`}>
                        {stat.changeType !== 'neutral' && (
                          <TrendingUp className={`w-4 h-4 ${stat.changeType === 'down' ? 'rotate-180' : ''}`} />
                        )}
                        <span>{stat.change}</span>
                      </div>
                    </div>
                    <div className={`text-3xl mb-2 text-${stat.color}-600 dark:text-${stat.color}-400`}>
                      {stat.value}
                    </div>
                    <div className="text-sm text-gray-600 dark:text-gray-400">
                      {stat.label}
                    </div>
                  </div>
                </Card>
              );
            })}
          </div>

          {/* Charts Row */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {/* Department Distribution */}
            <Card className="dark:bg-gray-800 dark:border-gray-700">
              <div className="p-6">
                <h3 className="text-base dark:text-white mb-4">部门人员分布</h3>
                <ResponsiveContainer width="100%" height={300}>
                  <PieChart>
                    <Pie
                      data={userDepartmentData}
                      cx="50%"
                      cy="50%"
                      labelLine={false}
                      label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                      outerRadius={100}
                      fill="#8884d8"
                      dataKey="value"
                    >
                      {userDepartmentData.map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={entry.color} />
                      ))}
                    </Pie>
                    <Tooltip />
                  </PieChart>
                </ResponsiveContainer>
              </div>
            </Card>

            {/* User Growth Trend */}
            <Card className="dark:bg-gray-800 dark:border-gray-700">
              <div className="p-6">
                <h3 className="text-base dark:text-white mb-4">用户增长趋势</h3>
                <ResponsiveContainer width="100%" height={300}>
                  <LineChart data={userGrowthData}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
                    <XAxis dataKey="month" stroke="#9ca3af" />
                    <YAxis stroke="#9ca3af" />
                    <Tooltip 
                      contentStyle={{ 
                        backgroundColor: '#1f2937', 
                        border: '1px solid #374151',
                        borderRadius: '8px'
                      }}
                    />
                    <Line type="monotone" dataKey="count" stroke="#3b82f6" strokeWidth={2} dot={{ fill: '#3b82f6' }} />
                  </LineChart>
                </ResponsiveContainer>
              </div>
            </Card>
          </div>
        </TabsContent>

        {/* Departments Tab */}
        <TabsContent value="departments" className="space-y-6">
          {/* Statistics Cards */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            {departmentStats.map((stat, index) => (
              <Card key={index} className="dark:bg-gray-800 dark:border-gray-700">
                <div className="p-6">
                  <div className="flex items-center justify-between mb-4">
                    <div className={`p-3 rounded-xl bg-${stat.color}-500/10`}>
                      <stat.icon className={`w-6 h-6 text-${stat.color}-600 dark:text-${stat.color}-400`} />
                    </div>
                    <div className={`flex items-center gap-1 text-sm ${
                      stat.changeType === 'up' ? 'text-green-600 dark:text-green-400' : 
                      stat.changeType === 'down' ? 'text-red-600 dark:text-red-400' : 
                      'text-gray-600 dark:text-gray-400'
                    }`}>
                      {stat.changeType !== 'neutral' && (
                        <TrendingUp className={`w-4 h-4 ${stat.changeType === 'down' ? 'rotate-180' : ''}`} />
                      )}
                      <span>{stat.change}</span>
                    </div>
                  </div>
                  <div className={`text-3xl mb-2 text-${stat.color}-600 dark:text-${stat.color}-400`}>
                    {stat.value}
                  </div>
                  <div className="text-sm text-gray-600 dark:text-gray-400">
                    {stat.label}
                  </div>
                </div>
              </Card>
            ))}
          </div>

          {/* Charts Row */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {/* Department Size Distribution */}
            <Card className="dark:bg-gray-800 dark:border-gray-700">
              <div className="p-6">
                <h3 className="text-base dark:text-white mb-4">部门规模分布</h3>
                <ResponsiveContainer width="100%" height={300}>
                  <PieChart>
                    <Pie
                      data={departmentSizeData}
                      cx="50%"
                      cy="50%"
                      labelLine={false}
                      label={({ name, value }) => `${name}: ${value}`}
                      outerRadius={100}
                      fill="#8884d8"
                      dataKey="value"
                    >
                      {departmentSizeData.map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={entry.color} />
                      ))}
                    </Pie>
                    <Tooltip />
                  </PieChart>
                </ResponsiveContainer>
              </div>
            </Card>

            {/* Department Level Distribution */}
            <Card className="dark:bg-gray-800 dark:border-gray-700">
              <div className="p-6">
                <h3 className="text-base dark:text-white mb-4">部门层级分布</h3>
                <ResponsiveContainer width="100%" height={300}>
                  <BarChart data={departmentLevelData}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
                    <XAxis dataKey="level" stroke="#9ca3af" />
                    <YAxis stroke="#9ca3af" />
                    <Tooltip 
                      contentStyle={{ 
                        backgroundColor: '#1f2937', 
                        border: '1px solid #374151',
                        borderRadius: '8px'
                      }}
                    />
                    <Bar dataKey="count" fill="#3b82f6" radius={[8, 8, 0, 0]} />
                  </BarChart>
                </ResponsiveContainer>
              </div>
            </Card>
          </div>
        </TabsContent>

        {/* Groups Tab */}
        <TabsContent value="groups" className="space-y-6">
          {/* Statistics Cards */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            {groupStats.map((stat, index) => (
              <Card key={index} className="dark:bg-gray-800 dark:border-gray-700">
                <div className="p-6">
                  <div className="flex items-center justify-between mb-4">
                    <div className={`p-3 rounded-xl bg-${stat.color}-500/10`}>
                      <stat.icon className={`w-6 h-6 text-${stat.color}-600 dark:text-${stat.color}-400`} />
                    </div>
                    <div className={`flex items-center gap-1 text-sm ${
                      stat.changeType === 'up' ? 'text-green-600 dark:text-green-400' : 
                      stat.changeType === 'down' ? 'text-red-600 dark:text-red-400' : 
                      'text-gray-600 dark:text-gray-400'
                    }`}>
                      {stat.changeType !== 'neutral' && (
                        <TrendingUp className={`w-4 h-4 ${stat.changeType === 'down' ? 'rotate-180' : ''}`} />
                      )}
                      <span>{stat.change}</span>
                    </div>
                  </div>
                  <div className={`text-3xl mb-2 text-${stat.color}-600 dark:text-${stat.color}-400`}>
                    {stat.value}
                  </div>
                  <div className="text-sm text-gray-600 dark:text-gray-400">
                    {stat.label}
                  </div>
                </div>
              </Card>
            ))}
          </div>

          {/* Charts Row */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {/* Group Type Distribution */}
            <Card className="dark:bg-gray-800 dark:border-gray-700">
              <div className="p-6">
                <h3 className="text-base dark:text-white mb-4">组类型分布</h3>
                <ResponsiveContainer width="100%" height={300}>
                  <PieChart>
                    <Pie
                      data={groupTypeData}
                      cx="50%"
                      cy="50%"
                      labelLine={false}
                      label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                      outerRadius={100}
                      fill="#8884d8"
                      dataKey="value"
                    >
                      {groupTypeData.map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={entry.color} />
                      ))}
                    </Pie>
                    <Tooltip />
                  </PieChart>
                </ResponsiveContainer>
              </div>
            </Card>

            {/* Group Member Distribution */}
            <Card className="dark:bg-gray-800 dark:border-gray-700">
              <div className="p-6">
                <h3 className="text-base dark:text-white mb-4">组成员分布</h3>
                <ResponsiveContainer width="100%" height={300}>
                  <BarChart data={groupMemberData}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
                    <XAxis dataKey="name" stroke="#9ca3af" angle={-45} textAnchor="end" height={80} />
                    <YAxis stroke="#9ca3af" />
                    <Tooltip 
                      contentStyle={{ 
                        backgroundColor: '#1f2937', 
                        border: '1px solid #374151',
                        borderRadius: '8px'
                      }}
                    />
                    <Bar dataKey="members" fill="#3b82f6" radius={[8, 8, 0, 0]} />
                  </BarChart>
                </ResponsiveContainer>
              </div>
            </Card>
          </div>
        </TabsContent>

        {/* Activity Tab */}
        <TabsContent value="activity" className="space-y-6">
          {/* Statistics Cards */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            {activityStats.map((stat, index) => (
              <Card key={index} className="dark:bg-gray-800 dark:border-gray-700">
                <div className="p-6">
                  <div className="flex items-center justify-between mb-4">
                    <div className={`p-3 rounded-xl bg-${stat.color}-500/10`}>
                      <stat.icon className={`w-6 h-6 text-${stat.color}-600 dark:text-${stat.color}-400`} />
                    </div>
                    <div className={`flex items-center gap-1 text-sm ${
                      stat.changeType === 'up' ? 'text-green-600 dark:text-green-400' : 
                      stat.changeType === 'down' ? 'text-red-600 dark:text-red-400' : 
                      'text-gray-600 dark:text-gray-400'
                    }`}>
                      {stat.changeType !== 'neutral' && (
                        <TrendingUp className={`w-4 h-4 ${stat.changeType === 'down' ? 'rotate-180' : ''}`} />
                      )}
                      <span>{stat.change}</span>
                    </div>
                  </div>
                  <div className={`text-3xl mb-2 text-${stat.color}-600 dark:text-${stat.color}-400`}>
                    {stat.value}
                  </div>
                  <div className="text-sm text-gray-600 dark:text-gray-400">
                    {stat.label}
                  </div>
                </div>
              </Card>
            ))}
          </div>

          {/* Charts Row */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {/* Activity Trend */}
            <Card className="dark:bg-gray-800 dark:border-gray-700">
              <div className="p-6">
                <h3 className="text-base dark:text-white mb-4">活动趋势</h3>
                <ResponsiveContainer width="100%" height={300}>
                  <LineChart data={activityTrendData}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
                    <XAxis dataKey="day" stroke="#9ca3af" />
                    <YAxis stroke="#9ca3af" />
                    <Tooltip 
                      contentStyle={{ 
                        backgroundColor: '#1f2937', 
                        border: '1px solid #374151',
                        borderRadius: '8px'
                      }}
                    />
                    <Line type="monotone" dataKey="count" stroke="#3b82f6" strokeWidth={2} dot={{ fill: '#3b82f6' }} />
                  </LineChart>
                </ResponsiveContainer>
              </div>
            </Card>

            {/* Activity Type Distribution */}
            <Card className="dark:bg-gray-800 dark:border-gray-700">
              <div className="p-6">
                <h3 className="text-base dark:text-white mb-4">活动类型分布</h3>
                <ResponsiveContainer width="100%" height={300}>
                  <PieChart>
                    <Pie
                      data={activityTypeData}
                      cx="50%"
                      cy="50%"
                      labelLine={false}
                      label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                      outerRadius={100}
                      fill="#8884d8"
                      dataKey="value"
                    >
                      {activityTypeData.map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={entry.color} />
                      ))}
                    </Pie>
                    <Tooltip />
                  </PieChart>
                </ResponsiveContainer>
              </div>
            </Card>
          </div>
        </TabsContent>
      </Tabs>

      {/* Dialogs */}
      <TenantDialog
        open={editDialogOpen}
        onOpenChange={setEditDialogOpen}
        mode="edit"
        tenant={tenant}
      />

      <ConfirmDialog
        open={deleteDialogOpen}
        onOpenChange={setDeleteDialogOpen}
        title="确认删除租户"
        description={`确定要删除租户 "${tenant.name}" 吗？此操作不可撤销，租户下的所有用户、部门和数据都将被永久删除。`}
        onConfirm={() => {
          toast.success(`已删除租户: ${tenant.name}`);
          setDeleteDialogOpen(false);
          setTimeout(() => onBack(), 500);
        }}
        confirmText="删除"
        variant="danger"
      />

      <ConfirmDialog
        open={toggleStatusDialogOpen}
        onOpenChange={setToggleStatusDialogOpen}
        title={tenant.status === '已启用' ? '确认禁用租户' : '确认启用租户'}
        description={
          tenant.status === '已启用'
            ? `确定要禁用租户 "${tenant.name}" 吗？禁用后该租户下的所有用户将无法登录系统。`
            : `确定要启用租户 "${tenant.name}" 吗？启用后该租户下的用户将可以正常登录系统。`
        }
        onConfirm={() => {
          const action = tenant.status === '已启用' ? '禁用' : '启用';
          toast.success(`已${action}租户: ${tenant.name}`);
          setToggleStatusDialogOpen(false);
        }}
        confirmText={tenant.status === '已启用' ? '禁用' : '启用'}
        variant={tenant.status === '已启用' ? 'danger' : 'default'}
      />

      <AssignRoleDialog
        open={assignRoleDialogOpen}
        onOpenChange={setAssignRoleDialogOpen}
        targetType="group"
        targetName={tenant.name}
      />

      <ConfirmDialog
        open={revokeRoleDialogOpen}
        onOpenChange={setRevokeRoleDialogOpen}
        title="确认撤销角色"
        description={`确定要撤销租户 "${tenant.name}" 的 "${selectedRole?.name}" 角色吗？撤销后该租户将失去该角色的所有权限。`}
        onConfirm={() => {
          toast.success(`已撤销角色: ${selectedRole?.name}`);
          setRevokeRoleDialogOpen(false);
          setSelectedRole(null);
        }}
        confirmText="撤销"
        variant="danger"
      />
    </div>
  );
}