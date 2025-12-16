import { useState } from 'react';
import { Shield, Search, Plus, Edit2, Trash2, Settings, Users, CheckCircle2, User, UserPlus, CheckSquare, Layout, MousePointer, Eye, AppWindow } from 'lucide-react';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription, DialogFooter } from '@/components/ui/dialog';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { Checkbox } from '@/components/ui/checkbox';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { toast } from 'sonner';

interface Role {
  id: string;
  name: string;
  code: string;
  description: string;
  isSystem: boolean;
  userCount: number;
  creator: string;
  createTime: string;
  updateTime: string;
  isDefault: boolean; // 是否为新用户默认角色
  appId: string; // 所属应用ID
  appName: string; // 所属应用名称
}

interface PermissionItem {
  id: string;
  name: string;
  checked: boolean;
  children?: PermissionItem[];
}

interface UserAssignment {
  id: string;
  name: string;
  avatar: string;
  department: string;
  email: string;
}

export function PermissionPolicy() {
  // 可用应用列表
  const [applications] = useState([
    { id: 'app-gm', name: 'AngusGM', description: '全局管理平台' },
    { id: 'app-ai', name: 'AngusAI', description: '智能对话平台' },
    { id: 'app-data', name: 'AngusData', description: '数据分析平台' },
    { id: 'app-workflow', name: 'AngusFlow', description: '工作流平台' },
  ]);

  const [roles, setRoles] = useState<Role[]>([
    {
      id: '1',
      name: '系统管理员',
      code: 'SYSTEM_ADMIN',
      description: '系统最高权限，拥有所有管理功能',
      isSystem: true,
      userCount: 3,
      creator: '系统',
      createTime: '2024-01-01 00:00:00',
      updateTime: '2024-01-01 00:00:00',
      isDefault: false,
      appId: 'app-gm',
      appName: 'AngusGM',
    },
    {
      id: '2',
      name: '租户管理员',
      code: 'TENANT_ADMIN',
      description: '租户级别管理员，管理租户内所有资源',
      isSystem: true,
      userCount: 5,
      creator: '系统',
      createTime: '2024-01-01 00:00:00',
      updateTime: '2024-01-01 00:00:00',
      isDefault: false,
      appId: 'app-gm',
      appName: 'AngusGM',
    },
    {
      id: '3',
      name: '部门管理员',
      code: 'DEPT_ADMIN',
      description: '部门管理员，管理部门成员和资源',
      isSystem: true,
      userCount: 12,
      creator: '系统',
      createTime: '2024-01-01 00:00:00',
      updateTime: '2024-01-01 00:00:00',
      isDefault: false,
      appId: 'app-gm',
      appName: 'AngusGM',
    },
    {
      id: '4',
      name: '普通用户',
      code: 'NORMAL_USER',
      description: '普通用户，基本访问权限',
      isSystem: true,
      userCount: 156,
      creator: '系统',
      createTime: '2024-01-01 00:00:00',
      updateTime: '2024-01-01 00:00:00',
      isDefault: true,
      appId: 'app-ai',
      appName: 'AngusAI',
    },
    {
      id: '5',
      name: '项目经理',
      code: 'PROJECT_MANAGER',
      description: '项目管理权限，管理项目和团队',
      isSystem: false,
      userCount: 8,
      creator: '柳小龙',
      createTime: '2024-11-15 10:30:00',
      updateTime: '2024-11-20 14:20:00',
      isDefault: false,
      appId: 'app-workflow',
      appName: 'AngusFlow',
    },
    {
      id: '6',
      name: '开发工程师',
      code: 'DEVELOPER',
      description: '开发人员角色，访问开发相关功能',
      isSystem: false,
      userCount: 45,
      creator: '张伟',
      createTime: '2024-11-18 09:15:00',
      updateTime: '2024-11-22 16:40:00',
      isDefault: false,
      appId: 'app-ai',
      appName: 'AngusAI',
    },
    {
      id: '7',
      name: '测试工程师',
      code: 'TESTER',
      description: '测试人员角色，访问测试相关功能',
      isSystem: false,
      userCount: 28,
      creator: '王芳',
      createTime: '2024-11-19 11:20:00',
      updateTime: '2024-11-23 10:15:00',
      isDefault: false,
      appId: 'app-data',
      appName: 'AngusData',
    },
    {
      id: '8',
      name: '运维工程师',
      code: 'OPS_ENGINEER',
      description: '运维人员角色，管理系统运维',
      isSystem: false,
      userCount: 15,
      creator: '李明',
      createTime: '2024-11-20 15:45:00',
      updateTime: '2024-11-24 09:30:00',
      isDefault: false,
      appId: 'app-gm',
      appName: 'AngusGM',
    },
  ]);

  const [searchQuery, setSearchQuery] = useState('');
  const [filterAppId, setFilterAppId] = useState<string>('all');
  const [showAddDialog, setShowAddDialog] = useState(false);
  const [showEditDialog, setShowEditDialog] = useState(false);
  const [showDeleteDialog, setShowDeleteDialog] = useState(false);
  const [showPermissionDialog, setShowPermissionDialog] = useState(false);
  const [showUserAssignDialog, setShowUserAssignDialog] = useState(false);
  const [currentRole, setCurrentRole] = useState<Role | null>(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [showDetail, setShowDetail] = useState(false);
  const [detailRole, setDetailRole] = useState<Role | null>(null);
  const pageSize = 8;

  const [formData, setFormData] = useState({
    name: '',
    code: '',
    description: '',
    isDefault: false,
    appId: '',
  });

  // 不同应用的权限树数据（包含菜单、按钮和看板，统一在一个树中）
  const appPermissionsMap: Record<string, PermissionItem[]> = {
    'app-gm': [
      {
        id: 'menu-gm-1',
        name: '租户管理',
        checked: false,
        children: [
          { id: 'btn-gm-1-1', name: '新增租户', checked: false },
          { id: 'btn-gm-1-2', name: '编辑租户', checked: false },
          { id: 'btn-gm-1-3', name: '删除租户', checked: false },
          { id: 'btn-gm-1-4', name: '查看详情', checked: false },
        ],
      },
      {
        id: 'menu-gm-2',
        name: '用户管理',
        checked: false,
        children: [
          { id: 'btn-gm-2-1', name: '新增用户', checked: false },
          { id: 'btn-gm-2-2', name: '编辑用户', checked: false },
          { id: 'btn-gm-2-3', name: '删除用户', checked: false },
          { id: 'btn-gm-2-4', name: '重置密码', checked: false },
        ],
      },
      {
        id: 'menu-gm-3',
        name: '应用管理',
        checked: false,
        children: [
          { id: 'btn-gm-3-1', name: '新增应用', checked: false },
          { id: 'btn-gm-3-2', name: '编辑应用', checked: false },
          { id: 'btn-gm-3-3', name: '删除应用', checked: false },
        ],
      },
      {
        id: 'dash-gm-1',
        name: '系统概览看板',
        checked: false,
      },
      {
        id: 'dash-gm-2',
        name: '租户统计看板',
        checked: false,
      },
      {
        id: 'dash-gm-3',
        name: '用户分析看板',
        checked: false,
      },
    ],
    'app-ai': [
      {
        id: 'menu-ai-1',
        name: '智能对话',
        checked: false,
        children: [
          { id: 'btn-ai-1-1', name: '发送消息', checked: false },
          { id: 'btn-ai-1-2', name: '清空对话', checked: false },
          { id: 'btn-ai-1-3', name: '导出对话', checked: false },
        ],
      },
      {
        id: 'menu-ai-2',
        name: '知识库',
        checked: false,
        children: [
          { id: 'btn-ai-2-1', name: '创建知识库', checked: false },
          { id: 'btn-ai-2-2', name: '编辑知识库', checked: false },
          { id: 'btn-ai-2-3', name: '删除知识库', checked: false },
          { id: 'btn-ai-2-4', name: '上传文档', checked: false },
        ],
      },
      {
        id: 'dash-ai-1',
        name: '对话统计看板',
        checked: false,
      },
      {
        id: 'dash-ai-2',
        name: '知识库分析看板',
        checked: false,
      },
    ],
    'app-data': [
      {
        id: 'menu-data-1',
        name: '数据分析',
        checked: false,
        children: [
          { id: 'btn-data-1-1', name: '创建报表', checked: false },
          { id: 'btn-data-1-2', name: '编辑报表', checked: false },
          { id: 'btn-data-1-3', name: '导出数据', checked: false },
        ],
      },
      {
        id: 'menu-data-2',
        name: '数据源管理',
        checked: false,
        children: [
          { id: 'btn-data-2-1', name: '添加数据源', checked: false },
          { id: 'btn-data-2-2', name: '测试连接', checked: false },
          { id: 'btn-data-2-3', name: '删除数据源', checked: false },
        ],
      },
      {
        id: 'dash-data-1',
        name: '业务数据看板',
        checked: false,
      },
      {
        id: 'dash-data-2',
        name: '实时监控看板',
        checked: false,
      },
    ],
    'app-workflow': [
      {
        id: 'menu-flow-1',
        name: '流程管理',
        checked: false,
        children: [
          { id: 'btn-flow-1-1', name: '创建流程', checked: false },
          { id: 'btn-flow-1-2', name: '编辑流程', checked: false },
          { id: 'btn-flow-1-3', name: '发布流程', checked: false },
        ],
      },
      {
        id: 'menu-flow-2',
        name: '审批管理',
        checked: false,
        children: [
          { id: 'btn-flow-2-1', name: '发起审批', checked: false },
          { id: 'btn-flow-2-2', name: '审批操作', checked: false },
          { id: 'btn-flow-2-3', name: '撤销审批', checked: false },
        ],
      },
      {
        id: 'dash-flow-1',
        name: '流程统计看板',
        checked: false,
      },
      {
        id: 'dash-flow-2',
        name: '审批效率看板',
        checked: false,
      },
    ],
  };

  const [permissions, setPermissions] = useState<PermissionItem[]>([]);



  // 可分配用户列表
  const [availableUsers] = useState<UserAssignment[]>([
    {
      id: '1',
      name: '张伟',
      avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Zhang',
      department: '技术部',
      email: 'zhangwei@example.com',
    },
    {
      id: '2',
      name: '王芳',
      avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Wang',
      department: '产品部',
      email: 'wangfang@example.com',
    },
    {
      id: '3',
      name: '李明',
      avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Li',
      department: '运维部',
      email: 'liming@example.com',
    },
    {
      id: '4',
      name: '赵静',
      avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Zhao',
      department: '市场部',
      email: 'zhaojing@example.com',
    },
  ]);

  const [selectedUsers, setSelectedUsers] = useState<string[]>([]);
  const [selectedDepartments, setSelectedDepartments] = useState<string[]>([]);
  const [selectedGroups, setSelectedGroups] = useState<string[]>([]);
  const [assignTab, setAssignTab] = useState<'user' | 'department' | 'group'>('user');

  // 可分配部门列表
  const [availableDepartments] = useState([
    { id: 'dept-1', name: '技术部', parentName: '研发中心', userCount: 45 },
    { id: 'dept-2', name: '产品部', parentName: '产品中心', userCount: 28 },
    { id: 'dept-3', name: '运维部', parentName: '技术中心', userCount: 15 },
    { id: 'dept-4', name: '市场部', parentName: '市场中心', userCount: 32 },
  ]);

  // 可分配组列表
  const [availableGroups] = useState([
    { id: 'group-1', name: '前端开发组', description: '负责前端技术开发', userCount: 12 },
    { id: 'group-2', name: '后端开发组', description: '负责后端服务开发', userCount: 18 },
    { id: 'group-3', name: '测试组', description: '负责质量保障', userCount: 10 },
    { id: 'group-4', name: 'UI设计组', description: '负责界面设计', userCount: 8 },
  ]);

  // 筛选角色
  const filteredRoles = roles.filter((role) => {
    const matchSearch = role.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      role.code.toLowerCase().includes(searchQuery.toLowerCase()) ||
      role.description.toLowerCase().includes(searchQuery.toLowerCase()) ||
      role.appName.toLowerCase().includes(searchQuery.toLowerCase());
    
    const matchApp = filterAppId === 'all' || role.appId === filterAppId;
    
    return matchSearch && matchApp;
  });

  // 分页
  const totalPages = Math.ceil(filteredRoles.length / pageSize);
  const paginatedRoles = filteredRoles.slice(
    (currentPage - 1) * pageSize,
    currentPage * pageSize
  );

  // 打开新增对话框
  const handleAdd = () => {
    setFormData({
      name: '',
      code: '',
      description: '',
      isDefault: false,
      appId: '',
    });
    setPermissions([]);
    setShowAddDialog(true);
  };

  // 打开编辑对话框
  const handleEdit = (role: Role) => {
    setCurrentRole(role);
    setFormData({
      name: role.name,
      code: role.code,
      description: role.description,
      isDefault: role.isDefault,
      appId: role.appId,
    });
    // 加载该应用的权限树
    if (role.appId && appPermissionsMap[role.appId]) {
      setPermissions(JSON.parse(JSON.stringify(appPermissionsMap[role.appId])));
    }
    setShowEditDialog(true);
  };

  // 打开删除对话框
  const handleDelete = (role: Role) => {
    if (role.isSystem) {
      toast.error('系统定义角色不允许删除');
      return;
    }
    setCurrentRole(role);
    setShowDeleteDialog(true);
  };

  // 打开权限配置对话框
  const handleConfigPermission = (role: Role) => {
    setCurrentRole(role);
    // 只加载当前角色所属应用的权限树
    if (role.appId && appPermissionsMap[role.appId]) {
      setPermissions(JSON.parse(JSON.stringify(appPermissionsMap[role.appId])));
    } else {
      setPermissions([]);
    }
    
    setShowPermissionDialog(true);
  };

  // 打开用户分配对话框
  const handleAssignUsers = (role: Role) => {
    setCurrentRole(role);
    setSelectedUsers([]);
    setSelectedDepartments([]);
    setSelectedGroups([]);
    setAssignTab('user');
    setShowUserAssignDialog(true);
  };

  // 查看角色详情
  const handleViewDetail = (role: Role) => {
    setDetailRole(role);
    setShowDetail(true);
  };

  // 从详情页返回列表
  const handleBackToList = () => {
    setShowDetail(false);
    setDetailRole(null);
  };

  // 保存新增
  const handleSaveAdd = () => {
    if (!formData.name.trim()) {
      toast.error('请输入角色名称');
      return;
    }
    if (!formData.code.trim()) {
      toast.error('请输入角色编码');
      return;
    }
    if (!formData.appId) {
      toast.error('请选择所属应用');
      return;
    }

    const selectedApp = applications.find(app => app.id === formData.appId);
    if (!selectedApp) {
      toast.error('所选应用不存在');
      return;
    }

    const newRole: Role = {
      id: String(Date.now()),
      name: formData.name.trim(),
      code: formData.code.trim(),
      description: formData.description.trim(),
      isSystem: false,
      userCount: 0,
      creator: '当前用户',
      createTime: new Date().toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        hour12: false,
      }).replace(/\//g, '-'),
      updateTime: new Date().toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        hour12: false,
      }).replace(/\//g, '-'),
      isDefault: formData.isDefault,
      appId: formData.appId,
      appName: selectedApp.name,
    };

    setRoles([...roles, newRole]);
    setShowAddDialog(false);
    toast.success('角色创建成功');
  };

  // 保存编辑
  const handleSaveEdit = () => {
    if (!currentRole) return;
    if (!formData.name.trim()) {
      toast.error('请输入角色名称');
      return;
    }
    if (!formData.code.trim()) {
      toast.error('请输入角色编码');
      return;
    }
    if (!formData.appId) {
      toast.error('请选择所属应用');
      return;
    }

    const selectedApp = applications.find(app => app.id === formData.appId);
    if (!selectedApp) {
      toast.error('所选应用不存在');
      return;
    }

    setRoles(
      roles.map((role) =>
        role.id === currentRole.id
          ? {
              ...role,
              name: formData.name.trim(),
              code: formData.code.trim(),
              description: formData.description.trim(),
              isDefault: formData.isDefault,
              appId: formData.appId,
              appName: selectedApp.name,
              updateTime: new Date().toLocaleString('zh-CN', {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit',
                hour: '2-digit',
                minute: '2-digit',
                second: '2-digit',
                hour12: false,
              }).replace(/\//g, '-'),
            }
          : role
      )
    );
    setShowEditDialog(false);
    toast.success('角色更新成功');
  };

  // 确认删除
  const handleConfirmDelete = () => {
    if (!currentRole) return;
    setRoles(roles.filter((role) => role.id !== currentRole.id));
    setShowDeleteDialog(false);
    toast.success('角色删除成功');
  };

  // 保存权限配置
  const handleSavePermissions = () => {
    toast.success('权限配置保存成功');
    setShowPermissionDialog(false);
  };

  // 保存用户分配
  const handleSaveUserAssignment = () => {
    const totalSelected = selectedUsers.length + selectedDepartments.length + selectedGroups.length;
    if (totalSelected === 0) {
      toast.error('请至少选择一个用户、部门或组');
      return;
    }
    
    let message = '成功授权：';
    const messages = [];
    if (selectedUsers.length > 0) messages.push(`${selectedUsers.length} 个用户`);
    if (selectedDepartments.length > 0) messages.push(`${selectedDepartments.length} 个部门`);
    if (selectedGroups.length > 0) messages.push(`${selectedGroups.length} 个组`);
    
    toast.success(message + messages.join('、'));
    setShowUserAssignDialog(false);
  };

  // 切换权限项选中状态
  const togglePermission = (id: string, parentIds: string[] = []) => {
    const updatePermissions = (items: PermissionItem[], path: string[] = []): PermissionItem[] => {
      return items.map((item) => {
        if (item.id === id) {
          const newChecked = !item.checked;
          // 更新自己和所有子项
          const updateChildren = (children?: PermissionItem[]): PermissionItem[] | undefined => {
            if (!children) return undefined;
            return children.map((child) => ({
              ...child,
              checked: newChecked,
              children: updateChildren(child.children),
            }));
          };
          return {
            ...item,
            checked: newChecked,
            children: updateChildren(item.children),
          };
        }
        if (item.children) {
          return {
            ...item,
            children: updatePermissions(item.children, [...path, item.id]),
          };
        }
        return item;
      });
    };
    setPermissions(updatePermissions(permissions));
  };

  // 如果正在查看详情，显示详情页面
  if (showDetail && detailRole) {
    return (
      <RoleDetail
        role={detailRole}
        onBack={handleBackToList}
        onEdit={(role) => {
          setShowDetail(false);
          handleEdit(role);
        }}
        onConfigPermission={(role) => {
          setShowDetail(false);
          handleConfigPermission(role);
        }}
        onAssignUsers={(role) => {
          setShowDetail(false);
          handleAssignUsers(role);
        }}
      />
    );
  }

  return (
    <div className="space-y-6">
      {/* 页面标题 */}
      <div className="flex items-center justify-between">
        <div>
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-gradient-to-br from-blue-500 to-blue-600 rounded-lg flex items-center justify-center">
              <Shield className="w-6 h-6 text-white" />
            </div>
            <div>
              <h1 className="text-2xl dark:text-white">权限策略</h1>
              <p className="text-sm text-gray-500 dark:text-gray-400">管理角色定义和权限分配</p>
            </div>
          </div>
        </div>
      </div>

      {/* 统计卡片 */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card className="dark:bg-gray-800 dark:border-gray-700 p-4">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-500 dark:text-gray-400">总角色数</p>
              <p className="text-2xl mt-2 dark:text-white">{roles.length}</p>
            </div>
            <div className="w-12 h-12 bg-blue-100 dark:bg-blue-900/30 rounded-lg flex items-center justify-center">
              <Shield className="w-6 h-6 text-blue-600 dark:text-blue-400" />
            </div>
          </div>
        </Card>

        <Card className="dark:bg-gray-800 dark:border-gray-700 p-4">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-500 dark:text-gray-400">系统角色</p>
              <p className="text-2xl mt-2 dark:text-white">
                {roles.filter((r) => r.isSystem).length}
              </p>
            </div>
            <div className="w-12 h-12 bg-purple-100 dark:bg-purple-900/30 rounded-lg flex items-center justify-center">
              <Settings className="w-6 h-6 text-purple-600 dark:text-purple-400" />
            </div>
          </div>
        </Card>

        <Card className="dark:bg-gray-800 dark:border-gray-700 p-4">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-500 dark:text-gray-400">自定义角色</p>
              <p className="text-2xl mt-2 dark:text-white">
                {roles.filter((r) => !r.isSystem).length}
              </p>
            </div>
            <div className="w-12 h-12 bg-green-100 dark:bg-green-900/30 rounded-lg flex items-center justify-center">
              <User className="w-6 h-6 text-green-600 dark:text-green-400" />
            </div>
          </div>
        </Card>

        <Card className="dark:bg-gray-800 dark:border-gray-700 p-4">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-500 dark:text-gray-400">已授权用户</p>
              <p className="text-2xl mt-2 dark:text-white">
                {roles.reduce((sum, r) => sum + r.userCount, 0)}
              </p>
            </div>
            <div className="w-12 h-12 bg-orange-100 dark:bg-orange-900/30 rounded-lg flex items-center justify-center">
              <Users className="w-6 h-6 text-orange-600 dark:text-orange-400" />
            </div>
          </div>
        </Card>
      </div>

      {/* 搜索栏 */}
      <Card className="dark:bg-gray-800 dark:border-gray-700 p-4">
        <div className="flex items-center gap-4">
          <div className="flex-1 relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
            <Input
              placeholder="搜索角色名称、编码、描述..."
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
          <Button onClick={handleAdd} className="bg-blue-600 hover:bg-blue-700">
            <Plus className="w-4 h-4 mr-2" />
            新增角色
          </Button>
        </div>
      </Card>

      {/* 角色列表 */}
      <Card className="dark:bg-gray-800 dark:border-gray-700">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-gray-50 dark:bg-gray-900/50 border-b dark:border-gray-700">
              <tr>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">角色信息</th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">所属应用</th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">类型</th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">描述</th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">已授权用户</th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">默认角色</th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">创建时间</th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">操作</th>
              </tr>
            </thead>
            <tbody className="divide-y dark:divide-gray-700">
              {paginatedRoles.length > 0 ? (
                paginatedRoles.map((role) => (
                  <tr
                    key={role.id}
                    className="hover:bg-gray-50 dark:hover:bg-gray-700/50 transition-colors"
                  >
                    <td className="p-4">
                      <div className="flex flex-col gap-1">
                        <div className="flex items-center gap-2">
                          <Shield className="w-4 h-4 text-blue-600 dark:text-blue-400" />
                          <button
                            onClick={() => handleViewDetail(role)}
                            className="dark:text-gray-200 hover:text-blue-600 dark:hover:text-blue-400 transition-colors cursor-pointer text-left"
                          >
                            {role.name}
                          </button>
                        </div>
                        <Badge className="bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400 border-0 font-mono text-xs w-fit">
                          {role.code}
                        </Badge>
                      </div>
                    </td>
                    <td className="p-4">
                      <div className="flex items-center gap-2">
                        <AppWindow className="w-4 h-4 text-blue-600 dark:text-blue-400" />
                        <span className="dark:text-gray-200">{role.appName}</span>
                      </div>
                    </td>
                    <td className="p-4">
                      {role.isSystem ? (
                        <Badge className="bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-400 border-0 flex items-center gap-1 w-fit">
                          <Settings className="w-3 h-3" />
                          系统定义
                        </Badge>
                      ) : (
                        <Badge className="bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400 border-0 flex items-center gap-1 w-fit">
                          <User className="w-3 h-3" />
                          自定义
                        </Badge>
                      )}
                    </td>
                    <td className="p-4">
                      <span className="text-sm text-gray-600 dark:text-gray-400">{role.description}</span>
                    </td>
                    <td className="p-4">
                      <div className="flex items-center gap-2">
                        <Users className="w-4 h-4 text-gray-400" />
                        <span className="text-sm dark:text-gray-300">{role.userCount}</span>
                      </div>
                    </td>
                    <td className="p-4">
                      {role.isDefault ? (
                        <Badge className="bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400 border-0 flex items-center gap-1 w-fit">
                          <CheckCircle2 className="w-3 h-3" />
                          是
                        </Badge>
                      ) : (
                        <span className="text-sm text-gray-400">-</span>
                      )}
                    </td>
                    <td className="p-4">
                      <span className="text-sm text-gray-500 dark:text-gray-400">{role.createTime}</span>
                    </td>
                    <td className="p-4">
                      <div className="flex items-center gap-2">
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => handleConfigPermission(role)}
                          className="dark:hover:bg-gray-600"
                          title="配置权限"
                        >
                          <Settings className="w-4 h-4" />
                        </Button>
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => handleAssignUsers(role)}
                          className="dark:hover:bg-gray-600"
                          title="分配用户"
                        >
                          <UserPlus className="w-4 h-4" />
                        </Button>
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => handleEdit(role)}
                          className="dark:hover:bg-gray-600"
                          title="编辑"
                        >
                          <Edit2 className="w-4 h-4" />
                        </Button>
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => handleDelete(role)}
                          disabled={role.isSystem}
                          className={role.isSystem ? 'opacity-50 cursor-not-allowed text-gray-400' : 'dark:hover:bg-gray-600 text-red-600 hover:text-red-700'}
                          title="删除"
                        >
                          <Trash2 className="w-4 h-4" />
                        </Button>
                      </div>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={8} className="p-12 text-center">
                    <div className="text-gray-500 dark:text-gray-400">
                      <Shield className="w-12 h-12 mx-auto mb-3 opacity-50" />
                      <p>暂无角色数据</p>
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
                共 {filteredRoles.length} 条记录，第 {currentPage} / {totalPages} 页
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

      {/* 新增角色对话框 */}
      <Dialog open={showAddDialog} onOpenChange={setShowAddDialog}>
        <DialogContent className="dark:bg-gray-800 dark:border-gray-700 max-w-2xl">
          <DialogHeader>
            <DialogTitle className="dark:text-white">新增角色</DialogTitle>
            <DialogDescription className="dark:text-gray-400">
              创建新的角色并配置相关信息
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-4 py-4">
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label className="dark:text-gray-300">
                  角色名称 <span className="text-red-500">*</span>
                </Label>
                <Input
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  placeholder="请输入角色名称"
                  className="dark:bg-gray-900 dark:border-gray-700"
                />
              </div>
              <div className="space-y-2">
                <Label className="dark:text-gray-300">
                  角色编码 <span className="text-red-500">*</span>
                </Label>
                <Input
                  value={formData.code}
                  onChange={(e) => setFormData({ ...formData, code: e.target.value.toUpperCase() })}
                  placeholder="请输入角色编码（英文大写）"
                  className="dark:bg-gray-900 dark:border-gray-700 font-mono"
                />
              </div>
            </div>
            <div className="space-y-2">
              <Label className="dark:text-gray-300">
                所属应用 <span className="text-red-500">*</span>
              </Label>
              <Select 
                value={formData.appId} 
                onValueChange={(value) => {
                  setFormData({ ...formData, appId: value });
                  // 加载该应用的权限树
                  if (appPermissionsMap[value]) {
                    setPermissions(JSON.parse(JSON.stringify(appPermissionsMap[value])));
                  } else {
                    setPermissions([]);
                  }
                }}
              >
                <SelectTrigger className="dark:bg-gray-900 dark:border-gray-700">
                  <SelectValue placeholder="请选择所属应用" />
                </SelectTrigger>
                <SelectContent className="dark:bg-gray-800 dark:border-gray-700">
                  {applications.map((app) => (
                    <SelectItem key={app.id} value={app.id} className="dark:text-gray-200 dark:hover:bg-gray-700">
                      <div className="flex items-center gap-2">
                        <AppWindow className="w-4 h-4" />
                        <span>{app.name}</span>
                        <span className="text-xs text-gray-500 dark:text-gray-400">({app.description})</span>
                      </div>
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
              <p className="text-xs text-gray-500 dark:text-gray-400">
                角色只能属于一个应用，不能跨应用使用
              </p>
            </div>
            
            <div className="space-y-2">
              <Label className="dark:text-gray-300">角色描述</Label>
              <Textarea
                value={formData.description}
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                placeholder="请输入角色描述"
                rows={3}
                className="dark:bg-gray-900 dark:border-gray-700"
              />
            </div>
            <div className="flex items-center space-x-2">
              <Checkbox
                id="isDefault"
                checked={formData.isDefault}
                onCheckedChange={(checked) => setFormData({ ...formData, isDefault: checked as boolean })}
                className="dark:border-gray-600"
              />
              <Label htmlFor="isDefault" className="dark:text-gray-300 cursor-pointer">
                设为新用户默认角色
              </Label>
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setShowAddDialog(false)} className="dark:border-gray-700 dark:hover:bg-gray-700">
              取消
            </Button>
            <Button onClick={handleSaveAdd} className="bg-blue-600 hover:bg-blue-700">
              保存
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* 编辑角色对话框 */}
      <Dialog open={showEditDialog} onOpenChange={setShowEditDialog}>
        <DialogContent className="dark:bg-gray-800 dark:border-gray-700 max-w-2xl">
          <DialogHeader>
            <DialogTitle className="dark:text-white">编辑角色</DialogTitle>
            <DialogDescription className="dark:text-gray-400">
              修改角色的基本信息和配置
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-4 py-4">
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label className="dark:text-gray-300">
                  角色名称 <span className="text-red-500">*</span>
                </Label>
                <Input
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  placeholder="请输入角色名称"
                  disabled={currentRole?.isSystem}
                  className="dark:bg-gray-900 dark:border-gray-700"
                />
              </div>
              <div className="space-y-2">
                <Label className="dark:text-gray-300">
                  角色编码 <span className="text-red-500">*</span>
                </Label>
                <Input
                  value={formData.code}
                  onChange={(e) => setFormData({ ...formData, code: e.target.value.toUpperCase() })}
                  placeholder="请输入角色编码（英文大写）"
                  disabled={currentRole?.isSystem}
                  className="dark:bg-gray-900 dark:border-gray-700 font-mono"
                />
              </div>
            </div>
            <div className="space-y-2">
              <Label className="dark:text-gray-300">
                所属应用 <span className="text-red-500">*</span>
              </Label>
              <Select 
                value={formData.appId} 
                onValueChange={(value) => {
                  setFormData({ ...formData, appId: value });
                  // 加载该应用的权限树
                  if (appPermissionsMap[value]) {
                    setPermissions(JSON.parse(JSON.stringify(appPermissionsMap[value])));
                  } else {
                    setPermissions([]);
                  }
                }}
                disabled={currentRole?.isSystem}
              >
                <SelectTrigger className="dark:bg-gray-900 dark:border-gray-700">
                  <SelectValue placeholder="请选择所属应用" />
                </SelectTrigger>
                <SelectContent className="dark:bg-gray-800 dark:border-gray-700">
                  {applications.map((app) => (
                    <SelectItem key={app.id} value={app.id} className="dark:text-gray-200 dark:hover:bg-gray-700">
                      <div className="flex items-center gap-2">
                        <AppWindow className="w-4 h-4" />
                        <span>{app.name}</span>
                        <span className="text-xs text-gray-500 dark:text-gray-400">({app.description})</span>
                      </div>
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
              <p className="text-xs text-gray-500 dark:text-gray-400">
                角色只能属于一个应用，不能跨应用使用
              </p>
            </div>
            
            <div className="space-y-2">
              <Label className="dark:text-gray-300">角色描述</Label>
              <Textarea
                value={formData.description}
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                placeholder="请输入角色描述"
                rows={3}
                className="dark:bg-gray-900 dark:border-gray-700"
              />
            </div>
            <div className="flex items-center space-x-2">
              <Checkbox
                id="isDefaultEdit"
                checked={formData.isDefault}
                onCheckedChange={(checked) => setFormData({ ...formData, isDefault: checked as boolean })}
                className="dark:border-gray-600"
              />
              <Label htmlFor="isDefaultEdit" className="dark:text-gray-300 cursor-pointer">
                设为新用户默认角色
              </Label>
            </div>
            {currentRole?.isSystem && (
              <div className="bg-yellow-50 dark:bg-yellow-900/20 border border-yellow-200 dark:border-yellow-800 rounded-lg p-3">
                <p className="text-sm text-yellow-800 dark:text-yellow-200">
                  系统角色的名称、编码和所属应用不可修改，仅可修改描述和默认角色设置
                </p>
              </div>
            )}
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setShowEditDialog(false)} className="dark:border-gray-700 dark:hover:bg-gray-700">
              取消
            </Button>
            <Button onClick={handleSaveEdit} className="bg-blue-600 hover:bg-blue-700">
              保存
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* 删除确认对话框 */}
      <Dialog open={showDeleteDialog} onOpenChange={setShowDeleteDialog}>
        <DialogContent className="dark:bg-gray-800 dark:border-gray-700">
          <DialogHeader>
            <DialogTitle className="dark:text-white">确认删除</DialogTitle>
            <DialogDescription className="dark:text-gray-400">
              此操作不可恢复，请确认是否继续
            </DialogDescription>
          </DialogHeader>
          <div className="py-4">
            <p className="dark:text-gray-300">
              确定要删除角色 <span className="font-semibold text-red-600">{currentRole?.name}</span> 吗？
            </p>
            <p className="text-sm text-gray-500 dark:text-gray-400 mt-2">
              删除后，已分配此角色的用户将失去对应权限。此操作不可恢复。
            </p>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setShowDeleteDialog(false)} className="dark:border-gray-700 dark:hover:bg-gray-700">
              取消
            </Button>
            <Button onClick={handleConfirmDelete} className="bg-red-600 hover:bg-red-700">
              确认删除
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* 权限配置对话框 */}
      <Dialog open={showPermissionDialog} onOpenChange={setShowPermissionDialog}>
        <DialogContent className="dark:bg-gray-800 dark:border-gray-700 max-w-4xl max-h-[80vh] overflow-hidden flex flex-col">
          <DialogHeader>
            <DialogTitle className="dark:text-white">
              配置权限 - {currentRole?.name}
            </DialogTitle>
            <DialogDescription className="dark:text-gray-400">
              为角色配置 {currentRole?.appName} 应用的菜单、按钮和看板访问权限
            </DialogDescription>
          </DialogHeader>
          <div className="flex-1 overflow-y-auto py-4">
            <div className="space-y-6">
              {/* 权限树（包含菜单、按钮和看板） */}
              <div>
                <div className="flex items-center gap-2 mb-3">
                  <Layout className="w-5 h-5 text-blue-600 dark:text-blue-400" />
                  <h3 className="dark:text-white">权限配置</h3>
                  <span className="text-xs text-gray-500 dark:text-gray-400">（菜单、按钮和看板）</span>
                </div>
                <Card className="dark:bg-gray-900 dark:border-gray-700 p-4">
                  <div className="space-y-3">
                    {permissions.map((item) => (
                      <div key={item.id} className="space-y-2">
                        {/* 菜单/看板级别 */}
                        <div className="flex items-center space-x-2 p-2 rounded hover:bg-gray-100 dark:hover:bg-gray-800">
                          <Checkbox
                            id={item.id}
                            checked={item.checked}
                            onCheckedChange={() => togglePermission(item.id)}
                            className="dark:border-gray-600"
                          />
                          {item.children && item.children.length > 0 ? (
                            <Layout className="w-4 h-4 text-blue-600 dark:text-blue-400" />
                          ) : (
                            <Eye className="w-4 h-4 text-purple-600 dark:text-purple-400" />
                          )}
                          <Label htmlFor={item.id} className="flex-1 cursor-pointer dark:text-gray-200">
                            {item.name}
                          </Label>
                        </div>
                        {/* 按钮级别 */}
                        {item.children && (
                          <div className="ml-6 space-y-1">
                            {item.children.map((btn) => (
                              <div key={btn.id} className="flex items-center space-x-2 p-2 rounded hover:bg-gray-100 dark:hover:bg-gray-800">
                                <Checkbox
                                  id={btn.id}
                                  checked={btn.checked}
                                  onCheckedChange={() => togglePermission(btn.id)}
                                  className="dark:border-gray-600"
                                />
                                <MousePointer className="w-3 h-3 text-gray-400" />
                                <Label htmlFor={btn.id} className="flex-1 cursor-pointer text-sm dark:text-gray-400">
                                  {btn.name}
                                </Label>
                              </div>
                            ))}
                          </div>
                        )}
                      </div>
                    ))}
                  </div>
                </Card>
              </div>
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setShowPermissionDialog(false)} className="dark:border-gray-700 dark:hover:bg-gray-700">
              取消
            </Button>
            <Button onClick={handleSavePermissions} className="bg-blue-600 hover:bg-blue-700">
              保存权限
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* 用户分配对话框 */}
      <Dialog open={showUserAssignDialog} onOpenChange={setShowUserAssignDialog}>
        <DialogContent className="dark:bg-gray-800 dark:border-gray-700 max-w-3xl">
          <DialogHeader>
            <DialogTitle className="dark:text-white">
              角色授权 - {currentRole?.name}
            </DialogTitle>
            <DialogDescription className="dark:text-gray-400">
              选择要授予此角色权限的用户、部门或组
            </DialogDescription>
          </DialogHeader>
          <div className="py-4">
            {/* Tab 切换 */}
            <div className="flex gap-2 mb-4 border-b dark:border-gray-700">
              <button
                onClick={() => setAssignTab('user')}
                className={`px-4 py-2 text-sm transition-colors ${
                  assignTab === 'user'
                    ? 'text-blue-600 dark:text-blue-400 border-b-2 border-blue-600 dark:border-blue-400'
                    : 'text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-300'
                }`}
              >
                <div className="flex items-center gap-2">
                  <User className="w-4 h-4" />
                  <span>用户</span>
                </div>
              </button>
              <button
                onClick={() => setAssignTab('department')}
                className={`px-4 py-2 text-sm transition-colors ${
                  assignTab === 'department'
                    ? 'text-blue-600 dark:text-blue-400 border-b-2 border-blue-600 dark:border-blue-400'
                    : 'text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-300'
                }`}
              >
                <div className="flex items-center gap-2">
                  <Users className="w-4 h-4" />
                  <span>部门</span>
                </div>
              </button>
              <button
                onClick={() => setAssignTab('group')}
                className={`px-4 py-2 text-sm transition-colors ${
                  assignTab === 'group'
                    ? 'text-blue-600 dark:text-blue-400 border-b-2 border-blue-600 dark:border-blue-400'
                    : 'text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-300'
                }`}
              >
                <div className="flex items-center gap-2">
                  <Users className="w-4 h-4" />
                  <span>组</span>
                </div>
              </button>
            </div>

            <div className="mb-4">
              <div className="relative">
                <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
                <Input
                  placeholder={
                    assignTab === 'user' ? '搜索用户姓名、邮箱、部门...' :
                    assignTab === 'department' ? '搜索部门名称...' :
                    '搜索组名称、描述...'
                  }
                  className="pl-10 dark:bg-gray-900 dark:border-gray-700"
                />
              </div>
            </div>

            {/* 用户列表 */}
            {assignTab === 'user' && (
              <div className="border dark:border-gray-700 rounded-lg max-h-96 overflow-y-auto">
                {availableUsers.map((user) => (
                  <div
                    key={user.id}
                    className="flex items-center gap-3 p-3 hover:bg-gray-50 dark:hover:bg-gray-700/50 border-b dark:border-gray-700 last:border-0"
                  >
                    <Checkbox
                      id={`user-${user.id}`}
                      checked={selectedUsers.includes(user.id)}
                      onCheckedChange={(checked) => {
                        if (checked) {
                          setSelectedUsers([...selectedUsers, user.id]);
                        } else {
                          setSelectedUsers(selectedUsers.filter((id) => id !== user.id));
                        }
                      }}
                      className="dark:border-gray-600"
                    />
                    <img src={user.avatar} alt={user.name} className="w-10 h-10 rounded-full" />
                    <div className="flex-1">
                      <div className="dark:text-white">{user.name}</div>
                      <div className="text-sm text-gray-500 dark:text-gray-400">
                        {user.department} · {user.email}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}

            {/* 部门列表 */}
            {assignTab === 'department' && (
              <div className="border dark:border-gray-700 rounded-lg max-h-96 overflow-y-auto">
                {availableDepartments.map((dept) => (
                  <div
                    key={dept.id}
                    className="flex items-center gap-3 p-3 hover:bg-gray-50 dark:hover:bg-gray-700/50 border-b dark:border-gray-700 last:border-0"
                  >
                    <Checkbox
                      id={`dept-${dept.id}`}
                      checked={selectedDepartments.includes(dept.id)}
                      onCheckedChange={(checked) => {
                        if (checked) {
                          setSelectedDepartments([...selectedDepartments, dept.id]);
                        } else {
                          setSelectedDepartments(selectedDepartments.filter((id) => id !== dept.id));
                        }
                      }}
                      className="dark:border-gray-600"
                    />
                    <div className="w-10 h-10 bg-blue-100 dark:bg-blue-900/30 rounded-lg flex items-center justify-center">
                      <Users className="w-5 h-5 text-blue-600 dark:text-blue-400" />
                    </div>
                    <div className="flex-1">
                      <div className="dark:text-white">{dept.name}</div>
                      <div className="text-sm text-gray-500 dark:text-gray-400">
                        {dept.parentName} · {dept.userCount} 人
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}

            {/* 组列表 */}
            {assignTab === 'group' && (
              <div className="border dark:border-gray-700 rounded-lg max-h-96 overflow-y-auto">
                {availableGroups.map((group) => (
                  <div
                    key={group.id}
                    className="flex items-center gap-3 p-3 hover:bg-gray-50 dark:hover:bg-gray-700/50 border-b dark:border-gray-700 last:border-0"
                  >
                    <Checkbox
                      id={`group-${group.id}`}
                      checked={selectedGroups.includes(group.id)}
                      onCheckedChange={(checked) => {
                        if (checked) {
                          setSelectedGroups([...selectedGroups, group.id]);
                        } else {
                          setSelectedGroups(selectedGroups.filter((id) => id !== group.id));
                        }
                      }}
                      className="dark:border-gray-600"
                    />
                    <div className="w-10 h-10 bg-green-100 dark:bg-green-900/30 rounded-lg flex items-center justify-center">
                      <Users className="w-5 h-5 text-green-600 dark:text-green-400" />
                    </div>
                    <div className="flex-1">
                      <div className="dark:text-white">{group.name}</div>
                      <div className="text-sm text-gray-500 dark:text-gray-400">
                        {group.description} · {group.userCount} 人
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}

            {/* 选择提示 */}
            {(
              (assignTab === 'user' && selectedUsers.length > 0) ||
              (assignTab === 'department' && selectedDepartments.length > 0) ||
              (assignTab === 'group' && selectedGroups.length > 0)
            ) && (
              <div className="mt-4 p-3 bg-blue-50 dark:bg-blue-900/20 border border-blue-200 dark:border-blue-800 rounded-lg">
                <p className="text-sm text-blue-800 dark:text-blue-200">
                  {assignTab === 'user' && `已选择 ${selectedUsers.length} 个用户`}
                  {assignTab === 'department' && `已选择 ${selectedDepartments.length} 个部门`}
                  {assignTab === 'group' && `已选择 ${selectedGroups.length} 个组`}
                </p>
              </div>
            )}
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setShowUserAssignDialog(false)} className="dark:border-gray-700 dark:hover:bg-gray-700">
              取消
            </Button>
            <Button onClick={handleSaveUserAssignment} className="bg-blue-600 hover:bg-blue-700">
              确认授权
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
