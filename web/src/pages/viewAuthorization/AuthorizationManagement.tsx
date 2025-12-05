import { useState } from 'react';
import { UserCog, Search, Shield, Users, User, Plus, Edit2, Trash2, AppWindow, Building2, X } from 'lucide-react';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription, DialogFooter } from '@/components/ui/dialog';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Label } from '@/components/ui/label';
import { Checkbox } from '@/components/ui/checkbox';
import { toast } from 'sonner';

interface AuthorizationRecord {
  id: string;
  targetType: 'user' | 'department' | 'group';
  targetId: string;
  targetName: string;
  targetAvatar?: string;
  targetDepartment?: string;
  targetEmail?: string;
  targetParent?: string;
  targetDescription?: string;
  targetUserCount?: number;
  roles: {
    id: string;
    name: string;
    code: string;
    appId: string;
    appName: string;
  }[];
  createTime: string;
}

interface TargetOption {
  id: string;
  name: string;
  avatar?: string;
  department?: string;
  email?: string;
  parent?: string;
  description?: string;
  userCount?: number;
}

export function AuthorizationManagement() {
  const [viewTab, setViewTab] = useState<'user' | 'department' | 'group'>('user');
  const [searchQuery, setSearchQuery] = useState('');
  const [filterAppId, setFilterAppId] = useState<string>('all');
  const [filterRoleId, setFilterRoleId] = useState<string>('all');
  const [showAuthDialog, setShowAuthDialog] = useState(false);
  const [editMode, setEditMode] = useState<'add' | 'edit'>('add');
  const [currentRecord, setCurrentRecord] = useState<AuthorizationRecord | null>(null);
  const [currentPage, setCurrentPage] = useState(1);
  const pageSize = 10;

  // 表单状态
  const [selectedTarget, setSelectedTarget] = useState<string>('');
  const [selectedRoleIds, setSelectedRoleIds] = useState<string[]>([]);

  // 可用应用列表
  const [applications] = useState([
    { id: 'app-gm', name: 'AngusGM', description: '全局管理平台' },
    { id: 'app-ai', name: 'AngusAI', description: '智能对话平台' },
    { id: 'app-data', name: 'AngusData', description: '数据分析平台' },
    { id: 'app-workflow', name: 'AngusFlow', description: '工作流平台' },
  ]);

  // 可用角色列表
  const [roles] = useState([
    { id: '1', name: '系统管理员', code: 'SYSTEM_ADMIN', appId: 'app-gm', appName: 'AngusGM' },
    { id: '2', name: '租户管理员', code: 'TENANT_ADMIN', appId: 'app-gm', appName: 'AngusGM' },
    { id: '3', name: '部门管理员', code: 'DEPT_ADMIN', appId: 'app-gm', appName: 'AngusGM' },
    { id: '4', name: '普通用户', code: 'NORMAL_USER', appId: 'app-ai', appName: 'AngusAI' },
    { id: '5', name: '项目经理', code: 'PROJECT_MANAGER', appId: 'app-workflow', appName: 'AngusFlow' },
    { id: '6', name: '开发工程师', code: 'DEVELOPER', appId: 'app-ai', appName: 'AngusAI' },
    { id: '7', name: '测试工程师', code: 'TESTER', appId: 'app-data', appName: 'AngusData' },
    { id: '8', name: '运维工程师', code: 'OPS_ENGINEER', appId: 'app-gm', appName: 'AngusGM' },
  ]);

  // 可选用户列表
  const [userOptions] = useState<TargetOption[]>([
    { id: 'user-1', name: '张伟', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Zhang', department: '技术部', email: 'zhangwei@example.com' },
    { id: 'user-2', name: '王芳', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Wang', department: '产品部', email: 'wangfang@example.com' },
    { id: 'user-3', name: '李明', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Li', department: '运维部', email: 'liming@example.com' },
    { id: 'user-4', name: '赵静', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Zhao', department: '市场部', email: 'zhaojing@example.com' },
    { id: 'user-5', name: '刘洋', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Liu', department: '测试部', email: 'liuyang@example.com' },
    { id: 'user-6', name: '陈静', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Chen', department: '设计部', email: 'chenjing@example.com' },
  ]);

  // 可选部门列表
  const [departmentOptions] = useState<TargetOption[]>([
    { id: 'dept-1', name: '技术部', parent: '研发中心', userCount: 45 },
    { id: 'dept-2', name: '产品部', parent: '产品中心', userCount: 28 },
    { id: 'dept-3', name: '测试部', parent: '质量中心', userCount: 18 },
    { id: 'dept-4', name: '设计部', parent: '产品中心', userCount: 15 },
    { id: 'dept-5', name: '运维部', parent: '技术中心', userCount: 12 },
  ]);

  // 可选组列表
  const [groupOptions] = useState<TargetOption[]>([
    { id: 'group-1', name: '前端开发组', description: '负责前端技术开发', userCount: 12 },
    { id: 'group-2', name: '后端开发组', description: '负责后端服务开发', userCount: 18 },
    { id: 'group-3', name: 'UI设计组', description: '负责界面设计', userCount: 8 },
    { id: 'group-4', name: '测试自动化组', description: '负责自动化测试', userCount: 6 },
    { id: 'group-5', name: 'DevOps组', description: '负责运维自动化', userCount: 10 },
  ]);

  // 用户授权记录
  const [userAuthorizations, setUserAuthorizations] = useState<AuthorizationRecord[]>([
    {
      id: '1',
      targetType: 'user',
      targetId: 'user-1',
      targetName: '张伟',
      targetAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Zhang',
      targetDepartment: '技术部',
      targetEmail: 'zhangwei@example.com',
      roles: [
        { id: '1', name: '系统管理员', code: 'SYSTEM_ADMIN', appId: 'app-gm', appName: 'AngusGM' },
        { id: '6', name: '开发工程师', code: 'DEVELOPER', appId: 'app-ai', appName: 'AngusAI' },
      ],
      createTime: '2024-01-15 10:30:00',
    },
    {
      id: '2',
      targetType: 'user',
      targetId: 'user-2',
      targetName: '王芳',
      targetAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Wang',
      targetDepartment: '产品部',
      targetEmail: 'wangfang@example.com',
      roles: [
        { id: '5', name: '项目经理', code: 'PROJECT_MANAGER', appId: 'app-workflow', appName: 'AngusFlow' },
      ],
      createTime: '2024-02-10 14:20:00',
    },
    {
      id: '3',
      targetType: 'user',
      targetId: 'user-3',
      targetName: '李明',
      targetAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Li',
      targetDepartment: '运维部',
      targetEmail: 'liming@example.com',
      roles: [
        { id: '8', name: '运维工程师', code: 'OPS_ENGINEER', appId: 'app-gm', appName: 'AngusGM' },
      ],
      createTime: '2024-03-05 09:15:00',
    },
    {
      id: '4',
      targetType: 'user',
      targetId: 'user-4',
      targetName: '赵静',
      targetAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Zhao',
      targetDepartment: '市场部',
      targetEmail: 'zhaojing@example.com',
      roles: [
        { id: '4', name: '普通用户', code: 'NORMAL_USER', appId: 'app-ai', appName: 'AngusAI' },
      ],
      createTime: '2024-03-12 16:40:00',
    },
    {
      id: '5',
      targetType: 'user',
      targetId: 'user-5',
      targetName: '刘洋',
      targetAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Liu',
      targetDepartment: '测试部',
      targetEmail: 'liuyang@example.com',
      roles: [
        { id: '7', name: '测试工程师', code: 'TESTER', appId: 'app-data', appName: 'AngusData' },
      ],
      createTime: '2024-03-18 11:25:00',
    },
  ]);

  // 部门授权记录
  const [departmentAuthorizations, setDepartmentAuthorizations] = useState<AuthorizationRecord[]>([
    {
      id: '1',
      targetType: 'department',
      targetId: 'dept-1',
      targetName: '技术部',
      targetParent: '研发中心',
      targetUserCount: 45,
      roles: [
        { id: '6', name: '开发工程师', code: 'DEVELOPER', appId: 'app-ai', appName: 'AngusAI' },
        { id: '8', name: '运维工程师', code: 'OPS_ENGINEER', appId: 'app-gm', appName: 'AngusGM' },
      ],
      createTime: '2024-01-05 09:00:00',
    },
    {
      id: '2',
      targetType: 'department',
      targetId: 'dept-2',
      targetName: '产品部',
      targetParent: '产品中心',
      targetUserCount: 28,
      roles: [
        { id: '5', name: '项目经理', code: 'PROJECT_MANAGER', appId: 'app-workflow', appName: 'AngusFlow' },
      ],
      createTime: '2024-01-10 10:30:00',
    },
    {
      id: '3',
      targetType: 'department',
      targetId: 'dept-3',
      targetName: '测试部',
      targetParent: '质量中心',
      targetUserCount: 18,
      roles: [
        { id: '7', name: '测试工程师', code: 'TESTER', appId: 'app-data', appName: 'AngusData' },
      ],
      createTime: '2024-01-12 14:15:00',
    },
  ]);

  // 组授权记录
  const [groupAuthorizations, setGroupAuthorizations] = useState<AuthorizationRecord[]>([
    {
      id: '1',
      targetType: 'group',
      targetId: 'group-1',
      targetName: '前端开发组',
      targetDescription: '负责前端技术开发',
      targetUserCount: 12,
      roles: [
        { id: '6', name: '开发工程师', code: 'DEVELOPER', appId: 'app-ai', appName: 'AngusAI' },
      ],
      createTime: '2024-02-01 10:00:00',
    },
    {
      id: '2',
      targetType: 'group',
      targetId: 'group-2',
      targetName: '后端开发组',
      targetDescription: '负责后端服务开发',
      targetUserCount: 18,
      roles: [
        { id: '6', name: '开发工程师', code: 'DEVELOPER', appId: 'app-ai', appName: 'AngusAI' },
        { id: '8', name: '运维工程师', code: 'OPS_ENGINEER', appId: 'app-gm', appName: 'AngusGM' },
      ],
      createTime: '2024-02-05 11:20:00',
    },
    {
      id: '3',
      targetType: 'group',
      targetId: 'group-3',
      targetName: 'UI设计组',
      targetDescription: '负责界面设计',
      targetUserCount: 8,
      roles: [
        { id: '4', name: '普通用户', code: 'NORMAL_USER', appId: 'app-ai', appName: 'AngusAI' },
      ],
      createTime: '2024-02-08 15:30:00',
    },
  ]);

  // 获取当前tab的数据
  const getCurrentData = () => {
    switch (viewTab) {
      case 'user':
        return userAuthorizations;
      case 'department':
        return departmentAuthorizations;
      case 'group':
        return groupAuthorizations;
      default:
        return [];
    }
  };

  // 更新当前tab的数据
  const setCurrentData = (data: AuthorizationRecord[]) => {
    switch (viewTab) {
      case 'user':
        setUserAuthorizations(data);
        break;
      case 'department':
        setDepartmentAuthorizations(data);
        break;
      case 'group':
        setGroupAuthorizations(data);
        break;
    }
  };

  // 获取当前类型的可选对象列表
  const getCurrentOptions = () => {
    switch (viewTab) {
      case 'user':
        return userOptions;
      case 'department':
        return departmentOptions;
      case 'group':
        return groupOptions;
      default:
        return [];
    }
  };

  // 筛选数据
  const filteredData = getCurrentData().filter((record) => {
    const matchSearch = 
      record.targetName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      (record.targetEmail && record.targetEmail.toLowerCase().includes(searchQuery.toLowerCase())) ||
      (record.targetDepartment && record.targetDepartment.toLowerCase().includes(searchQuery.toLowerCase())) ||
      (record.targetDescription && record.targetDescription.toLowerCase().includes(searchQuery.toLowerCase())) ||
      record.roles.some(role => 
        role.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
        role.code.toLowerCase().includes(searchQuery.toLowerCase())
      );
    
    const matchApp = filterAppId === 'all' || record.roles.some(role => role.appId === filterAppId);
    const matchRole = filterRoleId === 'all' || record.roles.some(role => role.id === filterRoleId);
    
    return matchSearch && matchApp && matchRole;
  });

  // 分页
  const totalPages = Math.ceil(filteredData.length / pageSize);
  const paginatedData = filteredData.slice(
    (currentPage - 1) * pageSize,
    currentPage * pageSize
  );

  // 统计数据
  const stats = {
    totalUsers: userAuthorizations.length,
    totalDepartments: departmentAuthorizations.length,
    totalGroups: groupAuthorizations.length,
    totalAuthorizations: userAuthorizations.length + departmentAuthorizations.length + groupAuthorizations.length,
  };

  // 打开新增授权对话框
  const handleAdd = () => {
    setEditMode('add');
    setSelectedTarget('');
    setSelectedRoleIds([]);
    setCurrentRecord(null);
    setShowAuthDialog(true);
  };

  // 打开编辑授权对话框
  const handleEdit = (record: AuthorizationRecord) => {
    setEditMode('edit');
    setSelectedTarget(record.targetId);
    setSelectedRoleIds(record.roles.map(r => r.id));
    setCurrentRecord(record);
    setShowAuthDialog(true);
  };

  // 删除授权
  const handleDelete = (record: AuthorizationRecord) => {
    if (confirm(`确定要删除 ${record.targetName} 的所有角色授权吗？`)) {
      const currentData = getCurrentData();
      const newData = currentData.filter(r => r.id !== record.id);
      setCurrentData(newData);
      toast.success('删除成功');
    }
  };

  // 切换角色选择
  const toggleRole = (roleId: string) => {
    if (selectedRoleIds.includes(roleId)) {
      setSelectedRoleIds(selectedRoleIds.filter(id => id !== roleId));
    } else {
      setSelectedRoleIds([...selectedRoleIds, roleId]);
    }
  };

  // 保存授权
  const handleSave = () => {
    if (!selectedTarget) {
      toast.error('请选择授权对象');
      return;
    }
    if (selectedRoleIds.length === 0) {
      toast.error('请至少选择一个角色');
      return;
    }

    const currentData = getCurrentData();
    const targetOption = getCurrentOptions().find(opt => opt.id === selectedTarget);
    if (!targetOption) return;

    const selectedRoles = roles.filter(r => selectedRoleIds.includes(r.id));

    if (editMode === 'add') {
      const newRecord: AuthorizationRecord = {
        id: Date.now().toString(),
        targetType: viewTab,
        targetId: selectedTarget,
        targetName: targetOption.name,
        targetAvatar: targetOption.avatar,
        targetDepartment: targetOption.department,
        targetEmail: targetOption.email,
        targetParent: targetOption.parent,
        targetDescription: targetOption.description,
        targetUserCount: targetOption.userCount,
        roles: selectedRoles,
        createTime: new Date().toLocaleString('zh-CN', { 
          year: 'numeric', 
          month: '2-digit', 
          day: '2-digit', 
          hour: '2-digit', 
          minute: '2-digit', 
          second: '2-digit',
          hour12: false 
        }).replace(/\//g, '-'),
      };
      setCurrentData([...currentData, newRecord]);
      toast.success('添加授权成功');
    } else {
      const updatedData = currentData.map(record => {
        if (record.id === currentRecord?.id) {
          return {
            ...record,
            roles: selectedRoles,
          };
        }
        return record;
      });
      setCurrentData(updatedData);
      toast.success('更新授权成功');
    }

    setShowAuthDialog(false);
  };

  return (
    <div className="space-y-6">
      {/* 页面标题 */}
      <div className="flex items-center justify-between">
        <div>
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-gradient-to-br from-blue-500 to-blue-600 rounded-lg flex items-center justify-center">
              <UserCog className="w-6 h-6 text-white" />
            </div>
            <div>
              <h1 className="text-2xl dark:text-white">授权管理</h1>
              <p className="text-sm text-gray-500 dark:text-gray-400">管理用户、部门和组的角色授权</p>
            </div>
          </div>
        </div>
      </div>

      {/* 统计卡片 */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card className="dark:bg-gray-800 dark:border-gray-700 p-4">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-500 dark:text-gray-400">总授权数</p>
              <p className="text-2xl mt-2 dark:text-white">{stats.totalAuthorizations}</p>
            </div>
            <div className="w-12 h-12 bg-blue-100 dark:bg-blue-900/30 rounded-lg flex items-center justify-center">
              <Shield className="w-6 h-6 text-blue-600 dark:text-blue-400" />
            </div>
          </div>
        </Card>

        <Card className="dark:bg-gray-800 dark:border-gray-700 p-4">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-500 dark:text-gray-400">用户授权</p>
              <p className="text-2xl mt-2 dark:text-white">{stats.totalUsers}</p>
            </div>
            <div className="w-12 h-12 bg-green-100 dark:bg-green-900/30 rounded-lg flex items-center justify-center">
              <User className="w-6 h-6 text-green-600 dark:text-green-400" />
            </div>
          </div>
        </Card>

        <Card className="dark:bg-gray-800 dark:border-gray-700 p-4">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-500 dark:text-gray-400">部门授权</p>
              <p className="text-2xl mt-2 dark:text-white">{stats.totalDepartments}</p>
            </div>
            <div className="w-12 h-12 bg-purple-100 dark:bg-purple-900/30 rounded-lg flex items-center justify-center">
              <Building2 className="w-6 h-6 text-purple-600 dark:text-purple-400" />
            </div>
          </div>
        </Card>

        <Card className="dark:bg-gray-800 dark:border-gray-700 p-4">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-500 dark:text-gray-400">组授权</p>
              <p className="text-2xl mt-2 dark:text-white">{stats.totalGroups}</p>
            </div>
            <div className="w-12 h-12 bg-orange-100 dark:bg-orange-900/30 rounded-lg flex items-center justify-center">
              <Users className="w-6 h-6 text-orange-600 dark:text-orange-400" />
            </div>
          </div>
        </Card>
      </div>

      {/* Tab切换和筛选 */}
      <Card className="dark:bg-gray-800 dark:border-gray-700">
        <div className="p-4 space-y-4">
          {/* Tab切换 */}
          <div className="flex gap-2 border-b dark:border-gray-700 pb-4">
            <button
              onClick={() => {
                setViewTab('user');
                setCurrentPage(1);
              }}
              className={`px-4 py-2 text-sm transition-colors rounded-t ${
                viewTab === 'user'
                  ? 'bg-blue-50 dark:bg-blue-900/30 text-blue-600 dark:text-blue-400 border-b-2 border-blue-600 dark:border-blue-400'
                  : 'text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-300'
              }`}
            >
              <div className="flex items-center gap-2">
                <User className="w-4 h-4" />
                <span>用户授权</span>
                <Badge className="bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400 border-0">
                  {stats.totalUsers}
                </Badge>
              </div>
            </button>
            <button
              onClick={() => {
                setViewTab('department');
                setCurrentPage(1);
              }}
              className={`px-4 py-2 text-sm transition-colors rounded-t ${
                viewTab === 'department'
                  ? 'bg-blue-50 dark:bg-blue-900/30 text-blue-600 dark:text-blue-400 border-b-2 border-blue-600 dark:border-blue-400'
                  : 'text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-300'
              }`}
            >
              <div className="flex items-center gap-2">
                <Building2 className="w-4 h-4" />
                <span>部门授权</span>
                <Badge className="bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-400 border-0">
                  {stats.totalDepartments}
                </Badge>
              </div>
            </button>
            <button
              onClick={() => {
                setViewTab('group');
                setCurrentPage(1);
              }}
              className={`px-4 py-2 text-sm transition-colors rounded-t ${
                viewTab === 'group'
                  ? 'bg-blue-50 dark:bg-blue-900/30 text-blue-600 dark:text-blue-400 border-b-2 border-blue-600 dark:border-blue-400'
                  : 'text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-300'
              }`}
            >
              <div className="flex items-center gap-2">
                <Users className="w-4 h-4" />
                <span>组授权</span>
                <Badge className="bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-400 border-0">
                  {stats.totalGroups}
                </Badge>
              </div>
            </button>
          </div>

          {/* 筛选条件 */}
          <div className="flex items-center gap-4">
            <div className="flex-1 relative">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
              <Input
                placeholder={
                  viewTab === 'user' ? '搜索用户姓名、邮箱、部门、角色...' :
                  viewTab === 'department' ? '搜索部门名称、角色...' :
                  '搜索组名称、描述、角色...'
                }
                value={searchQuery}
                onChange={(e) => {
                  setSearchQuery(e.target.value);
                  setCurrentPage(1);
                }}
                className="pl-10 dark:bg-gray-900 dark:border-gray-700"
              />
            </div>
            <div className="w-48">
              <Select value={filterAppId} onValueChange={(value) => { setFilterAppId(value); setCurrentPage(1); }}>
                <SelectTrigger className="dark:bg-gray-900 dark:border-gray-700">
                  <SelectValue placeholder="筛选应用" />
                </SelectTrigger>
                <SelectContent className="dark:bg-gray-800 dark:border-gray-700">
                  <SelectItem value="all" className="dark:text-gray-200 dark:hover:bg-gray-700">
                    全部应用
                  </SelectItem>
                  {applications.map((app) => (
                    <SelectItem key={app.id} value={app.id} className="dark:text-gray-200 dark:hover:bg-gray-700">
                      <div className="flex items-center gap-2">
                        <AppWindow className="w-4 h-4" />
                        <span>{app.name}</span>
                      </div>
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <div className="w-48">
              <Select value={filterRoleId} onValueChange={(value) => { setFilterRoleId(value); setCurrentPage(1); }}>
                <SelectTrigger className="dark:bg-gray-900 dark:border-gray-700">
                  <SelectValue placeholder="筛选角色" />
                </SelectTrigger>
                <SelectContent className="dark:bg-gray-800 dark:border-gray-700">
                  <SelectItem value="all" className="dark:text-gray-200 dark:hover:bg-gray-700">
                    全部角色
                  </SelectItem>
                  {roles.map((role) => (
                    <SelectItem key={role.id} value={role.id} className="dark:text-gray-200 dark:hover:bg-gray-700">
                      <div className="flex items-center gap-2">
                        <Shield className="w-4 h-4" />
                        <span>{role.name}</span>
                      </div>
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <Button onClick={handleAdd} className="bg-blue-600 hover:bg-blue-700 whitespace-nowrap">
              <Plus className="w-4 h-4 mr-2" />
              新增授权
            </Button>
          </div>
        </div>
      </Card>

      {/* 授权列表 */}
      <Card className="dark:bg-gray-800 dark:border-gray-700">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-gray-50 dark:bg-gray-900/50 border-b dark:border-gray-700">
              <tr>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">
                  {viewTab === 'user' ? '用户信息' : viewTab === 'department' ? '部门信息' : '组信息'}
                </th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">已授予角色</th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">涉及应用</th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">授权时间</th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">操作</th>
              </tr>
            </thead>
            <tbody className="divide-y dark:divide-gray-700">
              {paginatedData.length > 0 ? (
                paginatedData.map((record) => (
                  <tr
                    key={record.id}
                    className="hover:bg-gray-50 dark:hover:bg-gray-700/50 transition-colors"
                  >
                    <td className="p-4">
                      {viewTab === 'user' ? (
                        <div className="flex items-center gap-3">
                          <img src={record.targetAvatar} alt={record.targetName} className="w-10 h-10 rounded-full" />
                          <div>
                            <div className="dark:text-white">{record.targetName}</div>
                            <div className="text-sm text-gray-500 dark:text-gray-400">
                              {record.targetDepartment} · {record.targetEmail}
                            </div>
                          </div>
                        </div>
                      ) : viewTab === 'department' ? (
                        <div className="flex items-center gap-3">
                          <div className="w-10 h-10 bg-purple-100 dark:bg-purple-900/30 rounded-lg flex items-center justify-center">
                            <Building2 className="w-5 h-5 text-purple-600 dark:text-purple-400" />
                          </div>
                          <div>
                            <div className="dark:text-white">{record.targetName}</div>
                            <div className="text-sm text-gray-500 dark:text-gray-400">
                              {record.targetParent} · {record.targetUserCount} 人
                            </div>
                          </div>
                        </div>
                      ) : (
                        <div className="flex items-center gap-3">
                          <div className="w-10 h-10 bg-orange-100 dark:bg-orange-900/30 rounded-lg flex items-center justify-center">
                            <Users className="w-5 h-5 text-orange-600 dark:text-orange-400" />
                          </div>
                          <div>
                            <div className="dark:text-white">{record.targetName}</div>
                            <div className="text-sm text-gray-500 dark:text-gray-400">
                              {record.targetDescription} · {record.targetUserCount} 人
                            </div>
                          </div>
                        </div>
                      )}
                    </td>
                    <td className="p-4">
                      <div className="flex flex-wrap gap-2">
                        {record.roles.map((role) => (
                          <Badge
                            key={role.id}
                            className="bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400 border-0 flex items-center gap-1"
                          >
                            <Shield className="w-3 h-3" />
                            {role.name}
                          </Badge>
                        ))}
                      </div>
                    </td>
                    <td className="p-4">
                      <div className="flex flex-wrap gap-2">
                        {Array.from(new Set(record.roles.map(r => r.appName))).map((appName, index) => (
                          <Badge
                            key={index}
                            className="bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400 border-0 flex items-center gap-1"
                          >
                            <AppWindow className="w-3 h-3" />
                            {appName}
                          </Badge>
                        ))}
                      </div>
                    </td>
                    <td className="p-4">
                      <span className="text-sm text-gray-500 dark:text-gray-400">{record.createTime}</span>
                    </td>
                    <td className="p-4">
                      <div className="flex items-center gap-2">
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => handleEdit(record)}
                          className="dark:hover:bg-gray-600 text-blue-600 dark:text-blue-400 hover:text-blue-700"
                        >
                          <Edit2 className="w-4 h-4 mr-1" />
                          编辑
                        </Button>
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => handleDelete(record)}
                          className="dark:hover:bg-gray-600 text-red-600 dark:text-red-400 hover:text-red-700"
                        >
                          <Trash2 className="w-4 h-4 mr-1" />
                          删除
                        </Button>
                      </div>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={5} className="p-12 text-center">
                    <div className="text-gray-500 dark:text-gray-400">
                      <Shield className="w-12 h-12 mx-auto mb-3 opacity-50" />
                      <p>暂无授权数据</p>
                    </div>
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        {/* 分页 */}
        {totalPages > 1 && (
          <div className="border-t dark:border-gray-700 p-4">
            <div className="flex items-center justify-between">
              <div className="text-sm text-gray-500 dark:text-gray-400">
                共 {filteredData.length} 条记录，第 {currentPage} / {totalPages} 页
              </div>
              <div className="flex items-center gap-2">
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => setCurrentPage(currentPage - 1)}
                  disabled={currentPage === 1}
                  className="dark:border-gray-700 dark:hover:bg-gray-700"
                >
                  上一页
                </Button>
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => setCurrentPage(currentPage + 1)}
                  disabled={currentPage === totalPages}
                  className="dark:border-gray-700 dark:hover:bg-gray-700"
                >
                  下一页
                </Button>
              </div>
            </div>
          </div>
        )}
      </Card>

      {/* 授权对话框 */}
      <Dialog open={showAuthDialog} onOpenChange={setShowAuthDialog}>
        <DialogContent className="dark:bg-gray-800 dark:border-gray-700 max-w-2xl max-h-[80vh] overflow-hidden flex flex-col">
          <DialogHeader>
            <DialogTitle className="dark:text-white">
              {editMode === 'add' ? '新增授权' : '编辑授权'}
            </DialogTitle>
            <DialogDescription className="dark:text-gray-400">
              {editMode === 'add' 
                ? `为${viewTab === 'user' ? '用户' : viewTab === 'department' ? '部门' : '组'}分配角色权限`
                : `修改 ${currentRecord?.targetName} 的角色权限`
              }
            </DialogDescription>
          </DialogHeader>
          <div className="flex-1 overflow-y-auto py-4 space-y-6">
            {/* 选择授权对象 */}
            <div className="space-y-2">
              <Label className="dark:text-gray-200">
                授权对象 {viewTab === 'user' ? '（用户）' : viewTab === 'department' ? '（部门）' : '（组）'}
              </Label>
              <Select 
                value={selectedTarget} 
                onValueChange={setSelectedTarget}
                disabled={editMode === 'edit'}
              >
                <SelectTrigger className="dark:bg-gray-900 dark:border-gray-700">
                  <SelectValue placeholder={`请选择${viewTab === 'user' ? '用户' : viewTab === 'department' ? '部门' : '组'}`} />
                </SelectTrigger>
                <SelectContent className="dark:bg-gray-800 dark:border-gray-700">
                  {getCurrentOptions().map((option) => (
                    <SelectItem 
                      key={option.id} 
                      value={option.id}
                      className="dark:text-gray-200 dark:hover:bg-gray-700"
                    >
                      <div className="flex items-center gap-2">
                        {viewTab === 'user' && option.avatar && (
                          <img src={option.avatar} alt={option.name} className="w-6 h-6 rounded-full" />
                        )}
                        {viewTab === 'department' && (
                          <Building2 className="w-4 h-4 text-purple-600 dark:text-purple-400" />
                        )}
                        {viewTab === 'group' && (
                          <Users className="w-4 h-4 text-orange-600 dark:text-orange-400" />
                        )}
                        <div>
                          <div>{option.name}</div>
                          {viewTab === 'user' && (
                            <div className="text-xs text-gray-500 dark:text-gray-400">
                              {option.department} · {option.email}
                            </div>
                          )}
                          {viewTab === 'department' && (
                            <div className="text-xs text-gray-500 dark:text-gray-400">
                              {option.parent} · {option.userCount} 人
                            </div>
                          )}
                          {viewTab === 'group' && (
                            <div className="text-xs text-gray-500 dark:text-gray-400">
                              {option.description}
                            </div>
                          )}
                        </div>
                      </div>
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            {/* 选择角色 */}
            <div className="space-y-3">
              <Label className="dark:text-gray-200">
                分配角色（可多选）
              </Label>
              <div className="space-y-2 max-h-80 overflow-y-auto pr-2">
                {applications.map((app) => {
                  const appRoles = roles.filter(r => r.appId === app.id);
                  if (appRoles.length === 0) return null;
                  
                  return (
                    <div key={app.id} className="space-y-2">
                      <div className="flex items-center gap-2 py-2 border-b dark:border-gray-700">
                        <AppWindow className="w-4 h-4 text-blue-600 dark:text-blue-400" />
                        <span className="text-sm dark:text-gray-200">{app.name}</span>
                        <Badge className="bg-gray-100 text-gray-600 dark:bg-gray-700 dark:text-gray-300 border-0 text-xs">
                          {app.description}
                        </Badge>
                      </div>
                      <div className="space-y-2 pl-6">
                        {appRoles.map((role) => (
                          <div key={role.id} className="flex items-center gap-3 p-2 hover:bg-gray-50 dark:hover:bg-gray-700/50 rounded">
                            <Checkbox
                              id={`role-${role.id}`}
                              checked={selectedRoleIds.includes(role.id)}
                              onCheckedChange={() => toggleRole(role.id)}
                              className="dark:border-gray-600"
                            />
                            <label
                              htmlFor={`role-${role.id}`}
                              className="flex-1 flex items-center gap-2 cursor-pointer"
                            >
                              <Shield className="w-4 h-4 text-blue-600 dark:text-blue-400" />
                              <div className="flex-1">
                                <div className="text-sm dark:text-gray-200">{role.name}</div>
                                <div className="text-xs text-gray-500 dark:text-gray-400">{role.code}</div>
                              </div>
                            </label>
                          </div>
                        ))}
                      </div>
                    </div>
                  );
                })}
              </div>
              {selectedRoleIds.length > 0 && (
                <div className="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400 pt-2 border-t dark:border-gray-700">
                  <span>已选择 {selectedRoleIds.length} 个角色</span>
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={() => setSelectedRoleIds([])}
                    className="h-6 px-2 text-xs dark:hover:bg-gray-700"
                  >
                    <X className="w-3 h-3 mr-1" />
                    清空
                  </Button>
                </div>
              )}
            </div>
          </div>
          <DialogFooter>
            <Button
              variant="outline"
              onClick={() => setShowAuthDialog(false)}
              className="dark:border-gray-700 dark:hover:bg-gray-700"
            >
              取消
            </Button>
            <Button onClick={handleSave} className="bg-blue-600 hover:bg-blue-700">
              {editMode === 'add' ? '确认添加' : '保存修改'}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
