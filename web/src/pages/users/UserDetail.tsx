import { ChevronRight, Edit, Ban, Trash2, UserX, Shield, ShieldCheck, Lock, LockOpen, Plus, Trash, Building2, Users, Calendar, Eye, FileText, Crown, User, Mail, Phone, MapPin, Briefcase, Clock, Monitor, Smartphone, MapPinned, Activity } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { toast } from 'sonner';
import { useState } from 'react';
import { ConfirmDialog } from '@/components/gm/ConfirmDialog';
import { EditUserDialog } from './EditUserDialog';
import { SelectRoleDialog } from './SelectRoleDialog';
import { SelectDepartmentDialog } from './SelectDepartmentDialog';
import { SelectGroupDialog } from './SelectGroupDialog';

interface UserDetailProps {
  userId: string;
  onBack: () => void;
}

export function UserDetail({ userId, onBack }: UserDetailProps) {
  // Mock user data
  const user = {
    id: '20969481206603142',
    name: '刘健华',
    username: 'd9hua.liu',
    avatar: 'https://images.unsplash.com/photo-1652795385761-7ac287d0cd03?w=200',
    phone: '18620389299',
    email: 'd9hua@kunxin.cloud',
    landline: '010-86287896',
    gender: '男',
    address: '百云路60',
    position: '首府咨询顾问',
    role: '一般用户',
    systemGroup: '--',
    passwordExpiry: '--',
    joinDate: '2024-02-06 16:30:34',
    lastOnline: '2024-12-03 15:30:00',
    status: '已启用',
    isLocked: false,
  };

  const handleAction = (action: string) => {
    toast.success(`${action}操作已执行`);
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

  // Mock permissions data
  const authorizedRoles = [
    { 
      id: 'R001', 
      name: '项目管理员', 
      description: '负责项目的整体管理和协调',
      type: '管理角色',
      permissions: ['查看项目', '编辑项目', '管理成员', '发布公告'],
      grantedAt: '2024-01-15',
      grantedBy: '系统管理员'
    },
    { 
      id: 'R002', 
      name: '开发人员', 
      description: '负责项目开发工作',
      type: '业务角色',
      permissions: ['查看项目', '编辑代码', '提交变更', '查看文档'],
      grantedAt: '2024-01-20',
      grantedBy: '张三'
    },
    { 
      id: 'R003', 
      name: '文档管理员', 
      description: '负责项目文档的编写和维护',
      type: '业务角色',
      permissions: ['查看文档', '编辑文档', '发布文档'],
      grantedAt: '2024-02-01',
      grantedBy: '张三'
    },
  ];

  // Mock departments data
  const departments = [
    {
      id: 'D001',
      name: '技术研发部',
      level: '二级部门',
      parent: '技术中心',
      joinDate: '2024-01-15',
      role: '部门主管'
    },
    {
      id: 'D002',
      name: '前端开发组',
      level: '三级部门',
      parent: '技术研发部',
      joinDate: '2024-02-01',
      role: '普通成员'
    },
  ];

  // Mock groups data
  const groups = [
    {
      id: 'G001',
      name: '管理员组',
      type: '系统组',
      memberCount: 15,
      joinDate: '2024-01-15',
      role: '组长'
    },
    {
      id: 'G002',
      name: '技术团队',
      type: '部门组',
      memberCount: 28,
      joinDate: '2024-01-20',
      role: '普通成员'
    },
    {
      id: 'G003',
      name: '项目Alpha小组',
      type: '项目组',
      memberCount: 8,
      joinDate: '2024-03-01',
      role: '普通成员'
    },
  ];

  // Mock activity logs data (including login records)
  const activityLogs = [
    {
      id: 'A001',
      type: '登录',
      action: '用户登录',
      device: 'Chrome on Windows',
      ip: '192.168.1.100',
      location: '北京市 朝阳区',
      time: '2024-12-03 15:30:00',
      status: '成功',
    },
    {
      id: 'A002',
      type: '操作',
      action: '修改个人资料',
      description: '更新了手机号码',
      device: 'Chrome on Windows',
      ip: '192.168.1.100',
      location: '北京市 朝阳区',
      time: '2024-12-03 14:20:00',
      status: '成功',
    },
    {
      id: 'A003',
      type: '登录',
      action: '用户登录',
      device: 'Safari on iPhone',
      ip: '10.0.2.15',
      location: '北京市 海淀区',
      time: '2024-12-03 09:15:00',
      status: '成功',
    },
    {
      id: 'A004',
      type: '操作',
      action: '创建项目',
      description: '创建了新项目「Web应用重构」',
      device: 'Chrome on Windows',
      ip: '192.168.1.100',
      location: '北京市 朝阳区',
      time: '2024-12-02 16:45:00',
      status: '成功',
    },
    {
      id: 'A005',
      type: '登录',
      action: '用户登录',
      device: 'Chrome on Windows',
      ip: '192.168.1.100',
      location: '北京市 朝阳区',
      time: '2024-12-02 08:30:00',
      status: '成功',
    },
    {
      id: 'A006',
      type: '登录',
      action: '登录失败',
      device: 'Chrome on Windows',
      ip: '203.0.113.45',
      location: '上海市 浦东新区',
      time: '2024-12-01 22:10:00',
      status: '失败',
      reason: '密码错误',
    },
    {
      id: 'A007',
      type: '操作',
      action: '删除文件',
      description: '删除了文件「旧版本设计稿.pdf」',
      device: 'Chrome on Windows',
      ip: '192.168.1.100',
      location: '北京市 朝阳区',
      time: '2024-12-01 15:20:00',
      status: '成功',
    },
    {
      id: 'A008',
      type: '登录',
      action: '用户登录',
      device: 'Edge on Windows',
      ip: '192.168.1.100',
      location: '北京市 朝阳区',
      time: '2024-12-01 09:00:00',
      status: '成功',
    },
  ];

  const getPermissionTypeColor = (type: string) => {
    switch (type) {
      case '管理角色':
        return 'bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-400';
      case '业务角色':
        return 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400';
      default:
        return 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-400';
    }
  };

  const getDepartmentLevelColor = (level: string) => {
    switch (level) {
      case '一级部门':
        return 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400';
      case '二级部门':
        return 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400';
      case '三级部门':
        return 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400';
      default:
        return 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-400';
    }
  };

  const getGroupTypeColor = (type: string) => {
    switch (type) {
      case '系统组':
        return 'bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-400';
      case '部门组':
        return 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400';
      case '项目组':
        return 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400';
      default:
        return 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-400';
    }
  };

  const [isEditUserDialogOpen, setEditUserDialogOpen] = useState(false);
  const [isSelectRoleDialogOpen, setSelectRoleDialogOpen] = useState(false);
  const [isSelectDepartmentDialogOpen, setSelectDepartmentDialogOpen] = useState(false);
  const [isSelectGroupDialogOpen, setSelectGroupDialogOpen] = useState(false);
  const [isConfirmDialogOpen, setConfirmDialogOpen] = useState(false);

  return (
    <div className="space-y-6">
      {/* Breadcrumb */}
      <div className="flex items-center gap-2 text-sm">
        <button 
          onClick={onBack}
          className="text-gray-600 dark:text-gray-400 hover:text-blue-600 dark:hover:text-blue-400"
        >
          用户管理
        </button>
        <ChevronRight className="w-4 h-4 text-gray-400" />
        <span className="text-gray-900 dark:text-white">详情</span>
      </div>

      {/* User Header Card */}
      <Card className="dark:bg-gray-800 dark:border-gray-700">
        <div className="p-6">
          <div className="flex items-start justify-between">
            {/* Left: User Info */}
            <div className="flex items-start gap-6">
              <Avatar className="w-24 h-24 ring-4 ring-blue-500/20">
                <AvatarImage src={user.avatar} alt={user.name} />
                <AvatarFallback className="bg-blue-500 text-white text-2xl">
                  {user.name.slice(0, 2)}
                </AvatarFallback>
              </Avatar>
              <div>
                <div className="flex items-center gap-3 mb-2">
                  <h1 className="text-2xl dark:text-white">{user.name}</h1>
                  <Badge 
                    variant={user.status === '已启用' ? 'default' : 'secondary'}
                    className={user.status === '已启用' ? 'bg-green-500 hover:bg-green-600' : ''}
                  >
                    {user.status}
                  </Badge>
                  {user.isLocked && (
                    <Badge variant="outline" className="border-red-500 text-red-600 dark:text-red-400">
                      <Lock className="w-3 h-3 mr-1" />
                      已锁定
                    </Badge>
                  )}
                  <Badge className={`border-0 ${getRoleBadgeColor(user.role)} ${user.role === '超级管理员' ? 'flex items-center gap-1' : ''}`}>
                    {user.role === '超级管理员' && <Crown className="w-3 h-3" />}
                    {user.role}
                  </Badge>
                </div>
                <div className="text-sm text-gray-600 dark:text-gray-400 mb-4">
                  @{user.username} · ID: {user.id}
                </div>
                <div className="flex items-center gap-6 text-sm">
                  <div className="flex items-center gap-2">
                    <Briefcase className="w-4 h-4 text-gray-400" />
                    <span className="text-gray-600 dark:text-gray-400">{user.position}</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <Calendar className="w-4 h-4 text-gray-400" />
                    <span className="text-gray-600 dark:text-gray-400">
                      加入于 {user.joinDate}
                    </span>
                  </div>
                </div>
              </div>
            </div>

            {/* Right: Action Buttons */}
            <div className="flex gap-2">
              <Button
                variant="outline"
                onClick={() => setEditUserDialogOpen(true)}
                className="dark:border-gray-600 dark:text-gray-300 dark:hover:bg-gray-700"
              >
                <Edit className="w-4 h-4 mr-2" />
                编辑
              </Button>
              <Button
                variant="outline"
                onClick={() => setConfirmDialogOpen(true)}
                className="border-red-200 text-red-600 hover:bg-red-50 dark:border-red-800 dark:text-red-400 dark:hover:bg-red-900/20"
              >
                <Trash2 className="w-4 h-4 mr-2" />
                删除
              </Button>
            </div>
          </div>
        </div>
      </Card>

      {/* Tabs Section */}
      <Tabs defaultValue="basic" className="space-y-6">
        <TabsList className="dark:bg-gray-800">
          <TabsTrigger value="basic">
            <User className="w-4 h-4 mr-2" />
            基本信息
          </TabsTrigger>
          <TabsTrigger value="roles">
            <Crown className="w-4 h-4 mr-2" />
            授权角色
          </TabsTrigger>
          <TabsTrigger value="departments">
            <Building2 className="w-4 h-4 mr-2" />
            关联部门
          </TabsTrigger>
          <TabsTrigger value="groups">
            <Users className="w-4 h-4 mr-2" />
            关联组
          </TabsTrigger>
          <TabsTrigger value="activities">
            <Activity className="w-4 h-4 mr-2" />
            操作记录
          </TabsTrigger>
        </TabsList>

        {/* Basic Info Tab */}
        <TabsContent value="basic">
          <Card className="dark:bg-gray-800 dark:border-gray-700">
            <div className="p-6">
              <div className="mb-6">
                <h3 className="text-base dark:text-white mb-1">基本信息</h3>
                <p className="text-sm text-gray-600 dark:text-gray-400">用户的详细信息</p>
              </div>
              <div className="grid grid-cols-2 gap-8">
                {/* Left Column */}
                <div className="space-y-6">
                  <div className="flex items-start gap-4">
                    <div className="w-10 h-10 rounded-lg bg-blue-100 dark:bg-blue-900/30 flex items-center justify-center shrink-0">
                      <User className="w-5 h-5 text-blue-600 dark:text-blue-400" />
                    </div>
                    <div className="flex-1">
                      <div className="text-sm text-gray-600 dark:text-gray-400 mb-1">用户ID</div>
                      <div className="text-sm text-gray-900 dark:text-white font-mono">{user.id}</div>
                    </div>
                  </div>
                  <div className="flex items-start gap-4">
                    <div className="w-10 h-10 rounded-lg bg-green-100 dark:bg-green-900/30 flex items-center justify-center shrink-0">
                      <Phone className="w-5 h-5 text-green-600 dark:text-green-400" />
                    </div>
                    <div className="flex-1">
                      <div className="text-sm text-gray-600 dark:text-gray-400 mb-1">手机号</div>
                      <div className="text-sm text-gray-900 dark:text-white">{user.phone}</div>
                    </div>
                  </div>
                  <div className="flex items-start gap-4">
                    <div className="w-10 h-10 rounded-lg bg-purple-100 dark:bg-purple-900/30 flex items-center justify-center shrink-0">
                      <Mail className="w-5 h-5 text-purple-600 dark:text-purple-400" />
                    </div>
                    <div className="flex-1">
                      <div className="text-sm text-gray-600 dark:text-gray-400 mb-1">邮箱</div>
                      <div className="text-sm text-gray-900 dark:text-white">{user.email}</div>
                    </div>
                  </div>
                  <div className="flex items-start gap-4">
                    <div className="w-10 h-10 rounded-lg bg-orange-100 dark:bg-orange-900/30 flex items-center justify-center shrink-0">
                      <Phone className="w-5 h-5 text-orange-600 dark:text-orange-400" />
                    </div>
                    <div className="flex-1">
                      <div className="text-sm text-gray-600 dark:text-gray-400 mb-1">座机号</div>
                      <div className="text-sm text-gray-900 dark:text-white">{user.landline}</div>
                    </div>
                  </div>
                  <div className="flex items-start gap-4">
                    <div className="w-10 h-10 rounded-lg bg-pink-100 dark:bg-pink-900/30 flex items-center justify-center shrink-0">
                      <User className="w-5 h-5 text-pink-600 dark:text-pink-400" />
                    </div>
                    <div className="flex-1">
                      <div className="text-sm text-gray-600 dark:text-gray-400 mb-1">性别</div>
                      <div className="text-sm text-gray-900 dark:text-white">{user.gender}</div>
                    </div>
                  </div>
                </div>

                {/* Right Column */}
                <div className="space-y-6">
                  <div className="flex items-start gap-4">
                    <div className="w-10 h-10 rounded-lg bg-cyan-100 dark:bg-cyan-900/30 flex items-center justify-center shrink-0">
                      <Briefcase className="w-5 h-5 text-cyan-600 dark:text-cyan-400" />
                    </div>
                    <div className="flex-1">
                      <div className="text-sm text-gray-600 dark:text-gray-400 mb-1">职位</div>
                      <div className="text-sm text-gray-900 dark:text-white">{user.position}</div>
                    </div>
                  </div>
                  <div className="flex items-start gap-4">
                    <div className="w-10 h-10 rounded-lg bg-yellow-100 dark:bg-yellow-900/30 flex items-center justify-center shrink-0">
                      <Shield className="w-5 h-5 text-yellow-600 dark:text-yellow-400" />
                    </div>
                    <div className="flex-1">
                      <div className="text-sm text-gray-600 dark:text-gray-400 mb-1">系统管理组</div>
                      <div className="text-sm text-gray-900 dark:text-white">{user.systemGroup}</div>
                    </div>
                  </div>
                  <div className="flex items-start gap-4">
                    <div className="w-10 h-10 rounded-lg bg-red-100 dark:bg-red-900/30 flex items-center justify-center shrink-0">
                      <Lock className="w-5 h-5 text-red-600 dark:text-red-400" />
                    </div>
                    <div className="flex-1">
                      <div className="text-sm text-gray-600 dark:text-gray-400 mb-1">密码过期天数</div>
                      <div className="text-sm text-gray-900 dark:text-white">{user.passwordExpiry}</div>
                    </div>
                  </div>
                  <div className="flex items-start gap-4">
                    <div className="w-10 h-10 rounded-lg bg-indigo-100 dark:bg-indigo-900/30 flex items-center justify-center shrink-0">
                      <Calendar className="w-5 h-5 text-indigo-600 dark:text-indigo-400" />
                    </div>
                    <div className="flex-1">
                      <div className="text-sm text-gray-600 dark:text-gray-400 mb-1">最近上线</div>
                      <div className="text-sm text-gray-900 dark:text-white">{user.lastOnline}</div>
                    </div>
                  </div>
                  <div className="flex items-start gap-4">
                    <div className="w-10 h-10 rounded-lg bg-teal-100 dark:bg-teal-900/30 flex items-center justify-center shrink-0">
                      <MapPin className="w-5 h-5 text-teal-600 dark:text-teal-400" />
                    </div>
                    <div className="flex-1">
                      <div className="text-sm text-gray-600 dark:text-gray-400 mb-1">地址</div>
                      <div className="text-sm text-gray-900 dark:text-white">{user.address}</div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </Card>
        </TabsContent>

        {/* Permissions Tab */}
        <TabsContent value="roles">
          <Card className="dark:bg-gray-800 dark:border-gray-700">
            <div className="p-6">
              <div className="flex items-center justify-between mb-6">
                <div>
                  <h3 className="text-base dark:text-white mb-1">授权角色</h3>
                  <p className="text-sm text-gray-600 dark:text-gray-400">该用户已被授予的角色</p>
                </div>
                <Button
                  size="sm"
                  onClick={() => setSelectRoleDialogOpen(true)}
                  className="bg-blue-600 hover:bg-blue-700 text-white"
                >
                  <Plus className="w-4 h-4 mr-2" />
                  添加角色
                </Button>
              </div>
              <div className="space-y-3">
                {authorizedRoles.map((role) => (
                  <div
                    key={role.id}
                    className="flex items-center justify-between p-4 rounded-lg border dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-750"
                  >
                    <div className="flex items-center gap-4">
                      <div className="w-10 h-10 rounded-lg bg-blue-100 dark:bg-blue-900/30 flex items-center justify-center">
                        <Crown className="w-5 h-5 text-blue-600 dark:text-blue-400" />
                      </div>
                      <div>
                        <div className="dark:text-white">{role.name}</div>
                        <div className="flex items-center gap-4 text-sm text-gray-600 dark:text-gray-400 mt-1">
                          <span>类型: {role.type}</span>
                          <span>•</span>
                          <span>授予于 {role.grantedAt}</span>
                        </div>
                      </div>
                    </div>
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={() => handleAction(`撤销角色 ${role.name}`)}
                      className="text-red-600 hover:text-red-700 hover:bg-red-50 dark:text-red-400 dark:hover:bg-red-900/20"
                    >
                      撤销
                    </Button>
                  </div>
                ))}
              </div>
            </div>
          </Card>
        </TabsContent>

        {/* Departments Tab */}
        <TabsContent value="departments">
          <Card className="dark:bg-gray-800 dark:border-gray-700">
            <div className="p-6">
              <div className="flex items-center justify-between mb-6">
                <div>
                  <h3 className="text-base dark:text-white mb-1">关联部门</h3>
                  <p className="text-sm text-gray-600 dark:text-gray-400">该用户所属的组织部门</p>
                </div>
                <Button
                  size="sm"
                  onClick={() => setSelectDepartmentDialogOpen(true)}
                  className="bg-blue-600 hover:bg-blue-700 text-white"
                >
                  <Plus className="w-4 h-4 mr-2" />
                  添加部门
                </Button>
              </div>
              <div className="space-y-3">
                {departments.map((dept) => (
                  <div
                    key={dept.id}
                    className="flex items-center justify-between p-4 rounded-lg border dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-750"
                  >
                    <div className="flex items-center gap-4">
                      <div className="w-10 h-10 rounded-lg bg-blue-100 dark:bg-blue-900/30 flex items-center justify-center">
                        <Building2 className="w-5 h-5 text-blue-600 dark:text-blue-400" />
                      </div>
                      <div>
                        <div className="flex items-center gap-2">
                          <span className="dark:text-white">{dept.name}</span>
                          <Badge className={`border-0 ${getDepartmentLevelColor(dept.level)}`}>
                            {dept.level}
                          </Badge>
                          <Badge variant="outline" className="dark:border-gray-600 dark:text-gray-400 text-xs">
                            {dept.role}
                          </Badge>
                        </div>
                        <div className="flex items-center gap-4 text-sm text-gray-600 dark:text-gray-400 mt-1">
                          <span>上级: {dept.parent}</span>
                          <span>•</span>
                          <span>加入于 {dept.joinDate}</span>
                        </div>
                      </div>
                    </div>
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={() => handleAction(`移除部门 ${dept.name}`)}
                      className="text-red-600 hover:text-red-700 hover:bg-red-50 dark:text-red-400 dark:hover:bg-red-900/20"
                    >
                      <Trash className="w-4 h-4" />
                    </Button>
                  </div>
                ))}
              </div>
            </div>
          </Card>
        </TabsContent>

        {/* Groups Tab */}
        <TabsContent value="groups">
          <Card className="dark:bg-gray-800 dark:border-gray-700">
            <div className="p-6">
              <div className="flex items-center justify-between mb-6">
                <div>
                  <h3 className="text-base dark:text-white mb-1">关联组</h3>
                  <p className="text-sm text-gray-600 dark:text-gray-400">该用户加入的用户组</p>
                </div>
                <Button
                  size="sm"
                  onClick={() => setSelectGroupDialogOpen(true)}
                  className="bg-blue-600 hover:bg-blue-700 text-white"
                >
                  <Plus className="w-4 h-4 mr-2" />
                  添加组
                </Button>
              </div>
              <div className="space-y-3">
                {groups.map((group) => (
                  <div
                    key={group.id}
                    className="flex items-center justify-between p-4 rounded-lg border dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-750"
                  >
                    <div className="flex items-center gap-4">
                      <div className="w-10 h-10 rounded-lg bg-blue-100 dark:bg-blue-900/30 flex items-center justify-center">
                        <Users className="w-5 h-5 text-blue-600 dark:text-blue-400" />
                      </div>
                      <div>
                        <div className="flex items-center gap-2">
                          <span className="dark:text-white">{group.name}</span>
                          <Badge className={`border-0 ${getGroupTypeColor(group.type)}`}>
                            {group.type}
                          </Badge>
                          <Badge variant="outline" className="dark:border-gray-600 dark:text-gray-400 text-xs">
                            {group.role}
                          </Badge>
                        </div>
                        <div className="flex items-center gap-4 text-sm text-gray-600 dark:text-gray-400 mt-1">
                          <span>{group.memberCount} 人</span>
                          <span>•</span>
                          <span>加入于 {group.joinDate}</span>
                        </div>
                      </div>
                    </div>
                    <div className="flex gap-2">
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => handleAction(`查看组 ${group.name}`)}
                        className="dark:text-gray-300 dark:hover:bg-gray-700"
                      >
                        <Eye className="w-4 h-4" />
                      </Button>
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => handleAction(`退出组 ${group.name}`)}
                        className="text-red-600 hover:text-red-700 hover:bg-red-50 dark:text-red-400 dark:hover:bg-red-900/20"
                      >
                        <Trash className="w-4 h-4" />
                      </Button>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </Card>
        </TabsContent>

        {/* Activities Tab */}
        <TabsContent value="activities">
          <Card className="dark:bg-gray-800 dark:border-gray-700">
            <div className="p-6">
              <div className="flex items-center justify-between mb-6">
                <div>
                  <h3 className="text-base dark:text-white mb-1">操作记录</h3>
                  <p className="text-sm text-gray-600 dark:text-gray-400">该用户的操作记录</p>
                </div>
              </div>
              <div className="space-y-3">
                {activityLogs.map((log) => (
                  <div
                    key={log.id}
                    className="flex items-center justify-between p-4 rounded-lg border dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-750"
                  >
                    <div className="flex items-center gap-4">
                      <div className="w-10 h-10 rounded-lg bg-blue-100 dark:bg-blue-900/30 flex items-center justify-center">
                        {log.type === '登录' ? (
                          <LockOpen className="w-5 h-5 text-blue-600 dark:text-blue-400" />
                        ) : (
                          <FileText className="w-5 h-5 text-blue-600 dark:text-blue-400" />
                        )}
                      </div>
                      <div>
                        <div className="dark:text-white">{log.action}</div>
                        <div className="flex items-center gap-4 text-sm text-gray-600 dark:text-gray-400 mt-1">
                          {log.description && <span>{log.description}</span>}
                          <span>•</span>
                          <span>设备: {log.device}</span>
                          <span>•</span>
                          <span>IP: {log.ip}</span>
                          <span>•</span>
                          <span>位置: {log.location}</span>
                          <span>•</span>
                          <span>时间: {log.time}</span>
                          <span>•</span>
                          <span>状态: {log.status}</span>
                          {log.reason && <span>•</span>}
                          {log.reason && <span>原因: {log.reason}</span>}
                        </div>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </Card>
        </TabsContent>
      </Tabs>

      {/* Dialogs */}
      <EditUserDialog
        open={isEditUserDialogOpen}
        onOpenChange={setEditUserDialogOpen}
        user={user}
        onSave={(userData) => {
          toast.success('用户信息已更新');
        }}
      />
      
      <SelectRoleDialog
        open={isSelectRoleDialogOpen}
        onOpenChange={setSelectRoleDialogOpen}
        onConfirm={(selectedRoles) => {
          toast.success(`已添加 ${selectedRoles.length} 个角色`);
        }}
      />
      
      <SelectDepartmentDialog
        open={isSelectDepartmentDialogOpen}
        onOpenChange={setSelectDepartmentDialogOpen}
        onConfirm={(selectedDepartments) => {
          toast.success(`已添加 ${selectedDepartments.length} 个部门`);
        }}
      />
      
      <SelectGroupDialog
        open={isSelectGroupDialogOpen}
        onOpenChange={setSelectGroupDialogOpen}
        onConfirm={(selectedGroups) => {
          toast.success(`已加入 ${selectedGroups.length} 个组`);
        }}
      />
      
      <ConfirmDialog
        open={isConfirmDialogOpen}
        onOpenChange={setConfirmDialogOpen}
        title="删除用户"
        description={`确定要删除用户"${user.name}"吗？此操作不可撤销。`}
        onConfirm={() => {
          toast.success('用户已删除');
          onBack();
        }}
        variant="danger"
        confirmText="删除"
      />
    </div>
  );
}