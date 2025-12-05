import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { AppWindow, Plus, Search, Edit, Trash2, X, Download, Settings, Eye } from 'lucide-react';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Badge } from '@/components/ui/badge';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover';
import { Checkbox } from '@/components/ui/checkbox';
import { toast } from 'sonner';
import { Textarea } from '@/components/ui/textarea';

interface Application {
  id: string;
  code: string;
  name: string;
  displayName: string;
  type: '基础应用' | '业务应用' | '系统应用';
  version: string;
  versionType: '云服务版' | '本地版' | '混合版';
  isDefault: boolean;
  shopStatus: string;
  sortOrder: number;
  status: '启用' | '禁用';
  tags: string[]; // 改为标签数组
  url: string;
  groupId?: string;
  tenantName?: string;
  creator: string;
  createTime: string;
  description: string;
  isInstalled: boolean; // 是否为安装应用
}

interface AppManagementProps {
  onViewDetail?: (app: Application) => void;
  onViewMenu?: (app: Application) => void;
}

export function AppManagement({ onViewDetail, onViewMenu }: AppManagementProps) {
  const navigate = useNavigate();
  // 可用标签列表
  const availableTags = [
    'HEADER_MENU_POPOVER',
    'DYNAMIC_POSITION',
    'FIXED_POSITION',
    'CLOUD_SERVICE',
    'ENTERPRISE',
    'DATACENTER',
    'COMMUNITY',
    'DISPLAY_ON_NAVIGATOR',
    'DISPLAY_ON_MENU',
  ];

  const [applications, setApplications] = useState<Application[]>([
    {
      id: '100002',
      code: 'AngusGM',
      name: 'AngusGM',
      displayName: 'AngusGM',
      type: '基础应用',
      version: '1.0.0',
      versionType: '云服务版',
      isDefault: true,
      shopStatus: '注册成功',
      sortOrder: 2,
      status: '启用',
      tags: ['DISPLAY_ON_NAVIGATOR', 'CLOUD_SERVICE'],
      url: 'https://gm.xcan.cloud',
      tenantName: '联普云（北京）科技有限公司',
      creator: '柳小龙',
      createTime: '2024-01-02 11:22:39',
      description: '云服务器主机管理',
      isInstalled: true,
    },
    {
      id: '100003',
      code: 'OrganizationUser',
      name: '组织人员',
      displayName: '组织人员管理',
      type: '基础应用',
      version: '1.0.0',
      versionType: '云服务版',
      isDefault: false,
      shopStatus: '注册成功',
      sortOrder: 1,
      status: '启用',
      tags: ['DYNAMIC_POSITION', 'DISPLAY_ON_MENU'],
      url: '/organization',
      creator: '柳小龙',
      createTime: '2024-01-01 10:00:00',
      description: '组织架构和人员管理系统',
      isInstalled: true,
    },
    {
      id: '100004',
      code: 'CustomApp1',
      name: '自定义应用1',
      displayName: '业务系统',
      type: '业务应用',
      version: '2.1.0',
      versionType: '本地版',
      isDefault: false,
      shopStatus: '未注册',
      sortOrder: 3,
      status: '启用',
      tags: ['FIXED_POSITION', 'ENTERPRISE'],
      url: 'https://business.example.com',
      creator: '张三',
      createTime: '2024-02-15 14:30:00',
      description: '企业业务管理系统',
      isInstalled: false,
    },
    {
      id: '100005',
      code: 'System',
      name: '系统管理',
      displayName: '系统配置',
      type: '系统应用',
      version: '1.5.0',
      versionType: '云服务版',
      isDefault: false,
      shopStatus: '注册成功',
      sortOrder: 5,
      status: '启用',
      tags: ['DYNAMIC_POSITION'],
      url: '/system',
      creator: '柳小龙',
      createTime: '2024-01-01 10:00:00',
      description: '系统配置和管理',
      isInstalled: true,
    },
  ]);

  const [searchQuery, setSearchQuery] = useState('');
  const [typeFilter, setTypeFilter] = useState('all');
  const [statusFilter, setStatusFilter] = useState('all');
  const [sourceFilter, setSourceFilter] = useState('all'); // 来源筛选：all/installed/custom
  const [showEditDialog, setShowEditDialog] = useState(false);
  const [selectedApp, setSelectedApp] = useState<Application | null>(null);
  const [editFormData, setEditFormData] = useState<Partial<Application>>({});

  const filteredApplications = applications.filter(app => {
    const matchSearch = app.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
                       app.code.toLowerCase().includes(searchQuery.toLowerCase()) ||
                       app.displayName.toLowerCase().includes(searchQuery.toLowerCase());
    const matchType = typeFilter === 'all' || app.type === typeFilter;
    const matchStatus = statusFilter === 'all' || app.status === statusFilter;
    const matchSource = sourceFilter === 'all' || 
                       (sourceFilter === 'installed' && app.isInstalled) ||
                       (sourceFilter === 'custom' && !app.isInstalled);
    return matchSearch && matchType && matchStatus && matchSource;
  });

  const handleViewDetail = (app: Application) => {
    setSelectedApp(app);
    if (onViewDetail) {
      onViewDetail(app);
    } else {
      navigate(`/app-management/${app.id}`, { state: { app } });
    }
  };

  const handleEdit = (app: Application) => {
    if (app.isInstalled) {
      toast.error('安装应用不允许编辑');
      return;
    }
    setSelectedApp(app);
    setEditFormData(app);
    setShowEditDialog(true);
  };

  const handleDelete = (app: Application) => {
    if (app.isInstalled) {
      toast.error('安装应用不允许删除');
      return;
    }
    if (confirm(`确定要删除应用"${app.name}"吗？`)) {
      setApplications(applications.filter(a => a.id !== app.id));
      toast.success('应用已删除');
    }
  };

  const handleSaveEdit = () => {
    if (!selectedApp) return;

    setApplications(applications.map(app =>
      app.id === selectedApp.id ? { ...app, ...editFormData } : app
    ));
    setShowEditDialog(false);
    toast.success('应用已更新');
  };

  const handleAddApp = () => {
    setSelectedApp(null);
    setEditFormData({
      code: '',
      name: '',
      displayName: '',
      type: '业务应用',
      version: '1.0.0',
      versionType: '云服务版',
      isDefault: false,
      shopStatus: '未注册',
      sortOrder: applications.length + 1,
      status: '启用',
      tags: [],
      url: '',
      creator: '当前用户',
      createTime: new Date().toISOString().slice(0, 19).replace('T', ' '),
      description: '',
      isInstalled: false,
    });
    setShowEditDialog(true);
  };

  const handleSaveNew = () => {
    const { id, ...formDataWithoutId } = editFormData as Application;
    const newApp: Application = {
      ...formDataWithoutId,
      id: `${100000 + applications.length + 1}`,
    };
    setApplications([...applications, newApp]);
    setShowEditDialog(false);
    toast.success('应用已添加');
  };

  const getTypeBadge = (type: string) => {
    const configs = {
      '基础应用': 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400',
      '业务应用': 'bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-400',
      '系统应用': 'bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-400',
    };
    return <Badge className={`${configs[type as keyof typeof configs]} border-0`}>{type}</Badge>;
  };

  const getStatusBadge = (status: string) => {
    return status === '启用' ? (
      <Badge className="bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400 border-0">
        启用
      </Badge>
    ) : (
      <Badge className="bg-gray-100 text-gray-700 dark:bg-gray-700 dark:text-gray-400 border-0">
        禁用
      </Badge>
    );
  };

  const getSourceBadge = (isInstalled: boolean) => {
    return isInstalled ? (
      <Badge className="bg-cyan-100 text-cyan-700 dark:bg-cyan-900/30 dark:text-cyan-400 border-0">
        <Download className="w-3 h-3 mr-1" />
        安装应用
      </Badge>
    ) : (
      <Badge className="bg-indigo-100 text-indigo-700 dark:bg-indigo-900/30 dark:text-indigo-400 border-0">
        <Settings className="w-3 h-3 mr-1" />
        自定义应用
      </Badge>
    );
  };

  const renderTags = (tags: string[]) => {
    if (!tags || tags.length === 0) {
      return <span className="text-xs text-gray-400 dark:text-gray-500">无</span>;
    }
    return (
      <div className="flex flex-wrap gap-1">
        {tags.map((tag, index) => (
          <Badge
            key={index}
            className="bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400 border-0 text-xs"
          >
            {tag}
          </Badge>
        ))}
      </div>
    );
  };

  // 统计数据
  const stats = {
    total: applications.length,
    installed: applications.filter(a => a.isInstalled).length,
    custom: applications.filter(a => !a.isInstalled).length,
    enabled: applications.filter(a => a.status === '启用').length,
  };

  return (
    <div className="space-y-6">
      {/* 页面标题 */}
      <div>
        <h2 className="text-2xl dark:text-white mb-2">应用管理</h2>
        <p className="text-sm text-gray-500 dark:text-gray-400">
          管理系统应用和自定义应用
        </p>
      </div>

      {/* 统计卡片 */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <Card className="relative overflow-hidden dark:bg-gray-800 dark:border-gray-700">
          <div className="absolute top-0 right-0 w-32 h-32 opacity-10">
            <div className="w-full h-full bg-blue-500 rounded-full blur-3xl transform translate-x-8 -translate-y-8"></div>
          </div>
          <div className="p-6 relative">
            <div className="flex items-center justify-between mb-4">
              <div className="p-3 rounded-xl bg-blue-500/10">
                <AppWindow className="w-6 h-6 text-blue-500" />
              </div>
            </div>
            <div className="text-3xl mb-2 text-blue-600 dark:text-blue-400">
              {stats.total}
            </div>
            <div className="text-sm text-gray-600 dark:text-gray-400 mb-1">
              应用总数
            </div>
            <div className="text-xs text-gray-500 dark:text-gray-500">
              系统中所有应用
            </div>
          </div>
        </Card>

        <Card className="relative overflow-hidden dark:bg-gray-800 dark:border-gray-700">
          <div className="absolute top-0 right-0 w-32 h-32 opacity-10">
            <div className="w-full h-full bg-cyan-500 rounded-full blur-3xl transform translate-x-8 -translate-y-8"></div>
          </div>
          <div className="p-6 relative">
            <div className="flex items-center justify-between mb-4">
              <div className="p-3 rounded-xl bg-cyan-500/10">
                <Download className="w-6 h-6 text-cyan-500" />
              </div>
            </div>
            <div className="text-3xl mb-2 text-cyan-600 dark:text-cyan-400">
              {stats.installed}
            </div>
            <div className="text-sm text-gray-600 dark:text-gray-400 mb-1">
              安装应用
            </div>
            <div className="text-xs text-gray-500 dark:text-gray-500">
              系统预装应用
            </div>
          </div>
        </Card>

        <Card className="relative overflow-hidden dark:bg-gray-800 dark:border-gray-700">
          <div className="absolute top-0 right-0 w-32 h-32 opacity-10">
            <div className="w-full h-full bg-indigo-500 rounded-full blur-3xl transform translate-x-8 -translate-y-8"></div>
          </div>
          <div className="p-6 relative">
            <div className="flex items-center justify-between mb-4">
              <div className="p-3 rounded-xl bg-indigo-500/10">
                <Settings className="w-6 h-6 text-indigo-500" />
              </div>
            </div>
            <div className="text-3xl mb-2 text-indigo-600 dark:text-indigo-400">
              {stats.custom}
            </div>
            <div className="text-sm text-gray-600 dark:text-gray-400 mb-1">
              自定义应用
            </div>
            <div className="text-xs text-gray-500 dark:text-gray-500">
              用户创建的应用
            </div>
          </div>
        </Card>

        <Card className="relative overflow-hidden dark:bg-gray-800 dark:border-gray-700">
          <div className="absolute top-0 right-0 w-32 h-32 opacity-10">
            <div className="w-full h-full bg-green-500 rounded-full blur-3xl transform translate-x-8 -translate-y-8"></div>
          </div>
          <div className="p-6 relative">
            <div className="flex items-center justify-between mb-4">
              <div className="p-3 rounded-xl bg-green-500/10">
                <AppWindow className="w-6 h-6 text-green-500" />
              </div>
            </div>
            <div className="text-3xl mb-2 text-green-600 dark:text-green-400">
              {stats.enabled}
            </div>
            <div className="text-sm text-gray-600 dark:text-gray-400 mb-1">
              启用应用
            </div>
            <div className="text-xs text-gray-500 dark:text-gray-500">
              当前可用的应用
            </div>
          </div>
        </Card>
      </div>

      {/* 筛选和搜索 */}
      <Card className="p-4 dark:bg-gray-800 dark:border-gray-700">
        <div className="flex items-center justify-between gap-3">
          <div className="flex items-center gap-3 flex-1">
            {/* 搜索框 */}
            <div className="relative w-[300px]">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400 dark:text-gray-500" />
              <Input
                type="text"
                placeholder="搜索应用名称、编码..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="pl-10 pr-10 dark:bg-gray-900 dark:border-gray-700 dark:text-gray-100"
              />
              {searchQuery && (
                <button
                  onClick={() => setSearchQuery('')}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300"
                >
                  <X className="w-4 h-4" />
                </button>
              )}
            </div>

            {/* 筛选器 */}
            <Select value={sourceFilter} onValueChange={setSourceFilter}>
              <SelectTrigger className="w-[150px] dark:bg-gray-900 dark:border-gray-700">
                <SelectValue placeholder="应用来源" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">全部来源</SelectItem>
                <SelectItem value="installed">安装应用</SelectItem>
                <SelectItem value="custom">自定义应用</SelectItem>
              </SelectContent>
            </Select>

            <Select value={typeFilter} onValueChange={setTypeFilter}>
              <SelectTrigger className="w-[150px] dark:bg-gray-900 dark:border-gray-700">
                <SelectValue placeholder="应用类型" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">全部类型</SelectItem>
                <SelectItem value="基础应用">基础应用</SelectItem>
                <SelectItem value="业务应用">业务应用</SelectItem>
                <SelectItem value="系统应用">系统应用</SelectItem>
              </SelectContent>
            </Select>

            <Select value={statusFilter} onValueChange={setStatusFilter}>
              <SelectTrigger className="w-[150px] dark:bg-gray-900 dark:border-gray-700">
                <SelectValue placeholder="应用状态" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">全部状态</SelectItem>
                <SelectItem value="启用">启用</SelectItem>
                <SelectItem value="禁用">禁用</SelectItem>
              </SelectContent>
            </Select>
          </div>

          {/* 添加应用按钮 */}
          <Button 
            size="sm" 
            className="bg-blue-500 hover:bg-blue-600 text-white"
            onClick={handleAddApp}
          >
            <Plus className="w-4 h-4 mr-2" />
            添加应用
          </Button>
        </div>
      </Card>

      {/* 应用列表 */}
      <Card className="dark:bg-gray-800 dark:border-gray-700">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="border-b dark:border-gray-700">
              <tr>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">名称</th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">编码</th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">来源</th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">类型</th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">版本</th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">排序</th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">状态</th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">创建人</th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">标签</th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">操作</th>
              </tr>
            </thead>
            <tbody>
              {filteredApplications.map((app) => (
                <tr 
                  key={app.id} 
                  className="border-b dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-700/50"
                >
                  <td className="p-4">
                    <div>
                      <button
                        onClick={() => {
                          if (onViewMenu) {
                            onViewMenu(app);
                          } else {
                            navigate(`/app-management/${app.id}/menu`, { state: { app } });
                          }
                        }}
                        className="text-blue-600 dark:text-blue-400 hover:text-blue-700 dark:hover:text-blue-300 hover:underline"
                      >
                        {app.name}
                      </button>
                      <div className="text-xs text-gray-500 dark:text-gray-400">{app.displayName}</div>
                    </div>
                  </td>
                  <td className="p-4">
                    <code className="text-sm text-gray-600 dark:text-gray-400">{app.code}</code>
                  </td>
                  <td className="p-4">
                    {getSourceBadge(app.isInstalled)}
                  </td>
                  <td className="p-4">
                    {getTypeBadge(app.type)}
                  </td>
                  <td className="p-4">
                    <span className="text-sm dark:text-gray-300">{app.version}</span>
                  </td>
                  <td className="p-4">
                    <span className="text-sm dark:text-gray-300">{app.sortOrder}</span>
                  </td>
                  <td className="p-4">
                    {getStatusBadge(app.status)}
                  </td>
                  <td className="p-4">
                    <span className="text-sm dark:text-gray-300">{app.creator}</span>
                  </td>
                  <td className="p-4">
                    {renderTags(app.tags)}
                  </td>
                  <td className="p-4">
                    <div className="flex items-center gap-2">
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => handleViewDetail(app)}
                        className="dark:hover:bg-gray-600"
                      >
                        <Eye className="w-4 h-4" />
                      </Button>
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => handleEdit(app)}
                        className={`dark:hover:bg-gray-600 ${
                          app.isInstalled ? 'opacity-50 cursor-not-allowed' : ''
                        }`}
                        disabled={app.isInstalled}
                      >
                        <Edit className="w-4 h-4" />
                      </Button>
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => handleDelete(app)}
                        className={`dark:hover:bg-gray-600 text-red-600 dark:text-red-400 ${
                          app.isInstalled ? 'opacity-50 cursor-not-allowed' : ''
                        }`}
                        disabled={app.isInstalled}
                      >
                        <Trash2 className="w-4 h-4" />
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </Card>

      {/* 编辑/新增对话框 */}
      <Dialog open={showEditDialog} onOpenChange={setShowEditDialog}>
        <DialogContent className="max-w-3xl dark:bg-gray-800 dark:border-gray-700" aria-describedby={undefined}>
          <DialogHeader>
            <DialogTitle className="dark:text-white">
              {selectedApp ? '编辑应用' : '添加应用'}
            </DialogTitle>
          </DialogHeader>

          <div className="grid grid-cols-2 gap-4 max-h-[60vh] overflow-y-auto">
            <div>
              <Label className="dark:text-gray-300">名称 *</Label>
              <Input
                value={editFormData.name || ''}
                onChange={(e) => setEditFormData({ ...editFormData, name: e.target.value })}
                className="mt-2 dark:bg-gray-900 dark:border-gray-700"
                placeholder="应用名称"
              />
            </div>
            <div>
              <Label className="dark:text-gray-300">编码 *</Label>
              <Input
                value={editFormData.code || ''}
                onChange={(e) => setEditFormData({ ...editFormData, code: e.target.value })}
                className="mt-2 dark:bg-gray-900 dark:border-gray-700"
                placeholder="应用编码"
              />
            </div>
            <div>
              <Label className="dark:text-gray-300">显示标题 *</Label>
              <Input
                value={editFormData.displayName || ''}
                onChange={(e) => setEditFormData({ ...editFormData, displayName: e.target.value })}
                className="mt-2 dark:bg-gray-900 dark:border-gray-700"
                placeholder="显示标题"
              />
            </div>
            <div>
              <Label className="dark:text-gray-300">类型</Label>
              <Select
                value={editFormData.type}
                onValueChange={(value) => setEditFormData({ ...editFormData, type: value as any })}
              >
                <SelectTrigger className="mt-2 dark:bg-gray-900 dark:border-gray-700">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="基础应用">基础应用</SelectItem>
                  <SelectItem value="业务应用">业务应用</SelectItem>
                  <SelectItem value="系统应用">系统应用</SelectItem>
                </SelectContent>
              </Select>
            </div>
            <div>
              <Label className="dark:text-gray-300">版本</Label>
              <Input
                value={editFormData.version || ''}
                onChange={(e) => setEditFormData({ ...editFormData, version: e.target.value })}
                className="mt-2 dark:bg-gray-900 dark:border-gray-700"
                placeholder="1.0.0"
              />
            </div>
            <div>
              <Label className="dark:text-gray-300">版本类型</Label>
              <Select
                value={editFormData.versionType}
                onValueChange={(value) => setEditFormData({ ...editFormData, versionType: value as any })}
              >
                <SelectTrigger className="mt-2 dark:bg-gray-900 dark:border-gray-700">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="云服务版">云服务版</SelectItem>
                  <SelectItem value="本地版">本地版</SelectItem>
                  <SelectItem value="混合版">混合版</SelectItem>
                </SelectContent>
              </Select>
            </div>
            <div>
              <Label className="dark:text-gray-300">排序</Label>
              <Input
                type="number"
                value={editFormData.sortOrder || 0}
                onChange={(e) => setEditFormData({ ...editFormData, sortOrder: parseInt(e.target.value) })}
                className="mt-2 dark:bg-gray-900 dark:border-gray-700"
              />
            </div>
            <div>
              <Label className="dark:text-gray-300">状态</Label>
              <Select
                value={editFormData.status}
                onValueChange={(value) => setEditFormData({ ...editFormData, status: value as any })}
              >
                <SelectTrigger className="mt-2 dark:bg-gray-900 dark:border-gray-700">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="启用">启用</SelectItem>
                  <SelectItem value="禁用">禁用</SelectItem>
                </SelectContent>
              </Select>
            </div>
            <div>
              <Label className="dark:text-gray-300">标签</Label>
              <Popover>
                <PopoverTrigger asChild>
                  <Button
                    variant="outline"
                    className="w-full mt-2 justify-between dark:bg-gray-900 dark:border-gray-700 dark:hover:bg-gray-800"
                  >
                    <span className="truncate">
                      {editFormData.tags && editFormData.tags.length > 0
                        ? `已选择 ${editFormData.tags.length} 个标签`
                        : '请选择标签'}
                    </span>
                    <X className="w-4 h-4 opacity-50" />
                  </Button>
                </PopoverTrigger>
                <PopoverContent className="w-80 p-0 dark:bg-gray-800 dark:border-gray-700" align="start">
                  <div className="p-4 border-b dark:border-gray-700">
                    <h4 className="font-medium dark:text-white">选择标签</h4>
                    <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">
                      可以选择多个标签
                    </p>
                  </div>
                  <div className="max-h-[300px] overflow-y-auto p-2">
                    {availableTags.map((tag) => (
                      <div
                        key={tag}
                        className="flex items-center gap-2 px-3 py-2 hover:bg-gray-100 dark:hover:bg-gray-700 rounded cursor-pointer"
                        onClick={() => {
                          const currentTags = editFormData.tags || [];
                          const newTags = currentTags.includes(tag)
                            ? currentTags.filter(t => t !== tag)
                            : [...currentTags, tag];
                          setEditFormData({ ...editFormData, tags: newTags });
                        }}
                      >
                        <Checkbox
                          checked={editFormData.tags?.includes(tag) || false}
                          onCheckedChange={(checked) => {
                            const currentTags = editFormData.tags || [];
                            const newTags = checked
                              ? [...currentTags, tag]
                              : currentTags.filter(t => t !== tag);
                            setEditFormData({ ...editFormData, tags: newTags });
                          }}
                        />
                        <span className="text-sm dark:text-gray-200">{tag}</span>
                      </div>
                    ))}
                  </div>
                  {editFormData.tags && editFormData.tags.length > 0 && (
                    <div className="p-3 border-t dark:border-gray-700">
                      <div className="flex flex-wrap gap-1">
                        {editFormData.tags.map((tag) => (
                          <Badge
                            key={tag}
                            variant="secondary"
                            className="text-xs bg-blue-100 text-blue-600 dark:bg-blue-900/30 dark:text-blue-400"
                          >
                            {tag}
                            <X
                              className="w-3 h-3 ml-1 cursor-pointer"
                              onClick={(e) => {
                                e.stopPropagation();
                                const newTags = editFormData.tags?.filter(t => t !== tag) || [];
                                setEditFormData({ ...editFormData, tags: newTags });
                              }}
                            />
                          </Badge>
                        ))}
                      </div>
                    </div>
                  )}
                </PopoverContent>
              </Popover>
            </div>
            <div>
              <Label className="dark:text-gray-300">URL *</Label>
              <Input
                value={editFormData.url || ''}
                onChange={(e) => setEditFormData({ ...editFormData, url: e.target.value })}
                className="mt-2 dark:bg-gray-900 dark:border-gray-700"
                placeholder="https://example.com"
              />
            </div>
            <div className="col-span-2">
              <Label className="dark:text-gray-300">描述</Label>
              <Textarea
                value={editFormData.description || ''}
                onChange={(e) => setEditFormData({ ...editFormData, description: e.target.value })}
                className="mt-2 dark:bg-gray-900 dark:border-gray-700"
                placeholder="应用描述"
                rows={3}
              />
            </div>
          </div>

          <DialogFooter>
            <Button
              variant="outline"
              onClick={() => setShowEditDialog(false)}
              className="dark:border-gray-600 dark:hover:bg-gray-700"
            >
              取消
            </Button>
            <Button
              onClick={selectedApp ? handleSaveEdit : handleSaveNew}
              className="bg-blue-600 hover:bg-blue-700"
            >
              保存
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}