import { useState } from 'react';
import { Search, Plus, Building2, Users, Calendar, TrendingUp, MapPin, Mail, Phone, MoreVertical, Edit, Trash2, Lock, Shield } from 'lucide-react';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Input } from '@/components/ui/input';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { toast } from 'sonner';
import { TenantDialog } from '@/pages/tenants/TenantDialog';
import { ConfirmDialog } from '@/components/gm/ConfirmDialog';
import { TenantDetail } from '@/pages/tenants/TenantDetail';

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

export function TenantManagement() {
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState<string>('all');
  const [typeFilter, setTypeFilter] = useState<string>('all');
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 10;
  const [tenantDialogOpen, setTenantDialogOpen] = useState(false);
  const [showDetailPage, setShowDetailPage] = useState(false);
  const [confirmDialogOpen, setConfirmDialogOpen] = useState(false);
  const [dialogMode, setDialogMode] = useState<'create' | 'edit'>('create');
  const [selectedTenant, setSelectedTenant] = useState<Tenant | undefined>();
  const [tenantToDelete, setTenantToDelete] = useState<string>('');

  const stats = [
    {
      label: '总租户数',
      value: '48',
      subtext: '本月新增 6 个',
      icon: Building2,
      iconBg: 'bg-blue-500',
      valueColor: 'text-blue-600 dark:text-blue-400',
      trend: '+14%',
      trendDirection: 'up',
    },
    {
      label: '已启用',
      value: '42',
      subtext: '占比 87.5%',
      icon: Shield,
      iconBg: 'bg-green-500',
      valueColor: 'text-green-600 dark:text-green-400',
      trend: '+10%',
      trendDirection: 'up',
    },
    {
      label: '已禁用',
      value: '6',
      subtext: '占比 12.5%',
      icon: Lock,
      iconBg: 'bg-red-500',
      valueColor: 'text-red-600 dark:text-red-400',
      trend: '-3%',
      trendDirection: 'down',
    },
    {
      label: '总用户数',
      value: '1,234',
      subtext: '本月新增 45 人',
      icon: Users,
      iconBg: 'bg-purple-500',
      valueColor: 'text-purple-600 dark:text-purple-400',
      trend: '+12%',
      trendDirection: 'up',
    },
  ];

  const tenants: Tenant[] = [
    {
      id: 'T001',
      name: '聊办公集团',
      code: 'LIAOBG',
      type: '企业',
      accountType: 'main',
      adminName: '张三',
      adminEmail: 'zhangsan@liaobangong.com',
      adminPhone: '138****1234',
      userCount: 328,
      departmentCount: 45,
      status: '已启用',
      address: '北京市朝阳区建国路88号',
      createdAt: '2024-01-15',
      expireDate: '2025-01-15',
    },
    {
      id: 'T002',
      name: '科技创新公司',
      code: 'KJCX',
      type: '企业',
      accountType: 'sub',
      adminName: '李四',
      adminEmail: 'lisi@kjcx.com',
      adminPhone: '139****5678',
      userCount: 256,
      departmentCount: 32,
      status: '已启用',
      address: '上海市浦东新区世纪大道100号',
      createdAt: '2024-02-20',
      expireDate: '2025-02-20',
    },
    {
      id: 'T003',
      name: '智慧教育集团',
      code: 'ZHJY',
      type: '企业',
      accountType: 'sub',
      adminName: '王五',
      adminEmail: 'wangwu@zhjy.com',
      adminPhone: '137****9012',
      userCount: 189,
      departmentCount: 28,
      status: '已启用',
      address: '广州市天河区珠江新城',
      createdAt: '2024-03-10',
      expireDate: '2025-03-10',
    },
    {
      id: 'T004',
      name: '金融服务公司',
      code: 'JRFW',
      type: '企业',
      accountType: 'sub',
      adminName: '赵六',
      adminEmail: 'zhaoliu@jrfw.com',
      adminPhone: '136****3456',
      userCount: 145,
      departmentCount: 18,
      status: '已启用',
      address: '深圳市福田区福华路',
      createdAt: '2024-04-05',
      expireDate: '2025-04-05',
    },
    {
      id: 'T005',
      name: '测试租户',
      code: 'TEST',
      type: '个人',
      accountType: 'sub',
      adminName: '孙七',
      adminEmail: 'sunqi@test.com',
      adminPhone: '135****7890',
      userCount: 12,
      departmentCount: 3,
      status: '已禁用',
      address: '杭州市西湖区文一路',
      createdAt: '2024-10-01',
      expireDate: '2024-12-01',
    },
    {
      id: 'T006',
      name: '个人工作室',
      code: 'STUDIO',
      type: '个人',
      accountType: 'sub',
      adminName: '周九',
      adminEmail: 'zhoujiu@studio.com',
      adminPhone: '134****2345',
      userCount: 5,
      departmentCount: 1,
      status: '已启用',
      address: '成都市高新区天府大道',
      createdAt: '2024-06-15',
      expireDate: '2025-06-15',
    },
    {
      id: 'T007',
      name: '临时测试账户',
      code: 'TEMP',
      type: '未知',
      accountType: 'sub',
      adminName: '吴十',
      adminEmail: 'wushi@temp.com',
      adminPhone: '133****6789',
      userCount: 2,
      departmentCount: 1,
      status: '已启用',
      address: '武汉市江汉区',
      createdAt: '2024-10-20',
      expireDate: '2024-11-20',
    },
  ];

  const filteredTenants = tenants.filter(tenant => {
    const matchesSearch = tenant.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
                         tenant.code.toLowerCase().includes(searchQuery.toLowerCase()) ||
                         tenant.adminName.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesStatus = statusFilter === 'all' || tenant.status === statusFilter;
    const matchesType = typeFilter === 'all' || tenant.type === typeFilter;
    return matchesSearch && matchesStatus && matchesType;
  });

  const totalPages = Math.ceil(filteredTenants.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const currentTenants = filteredTenants.slice(startIndex, startIndex + itemsPerPage);

  const handleCreateTenant = () => {
    setDialogMode('create');
    setSelectedTenant(undefined);
    setTenantDialogOpen(true);
  };

  const handleEditTenant = (tenant: Tenant) => {
    setDialogMode('edit');
    setSelectedTenant(tenant);
    setTenantDialogOpen(true);
  };

  const handleViewDetail = (tenant: Tenant) => {
    setSelectedTenant(tenant);
    setShowDetailPage(true);
  };

  const handleToggleStatus = (tenant: Tenant) => {
    const action = tenant.status === '已启用' ? '禁用' : '启用';
    toast.success(`${action}租户: ${tenant.name}`);
  };

  const handleDeleteTenant = (tenant: Tenant) => {
    if (tenant.accountType === 'main') {
      toast.error('主账号不允许删除');
      return;
    }
    setTenantToDelete(tenant.name);
    setConfirmDialogOpen(true);
  };

  const confirmDelete = () => {
    toast.success(`删除租户: ${tenantToDelete}`);
    setTenantToDelete('');
  };

  // 如果显示详情页面，则渲染TenantDetail组件
  if (showDetailPage && selectedTenant) {
    return (
      <TenantDetail
        tenant={selectedTenant}
        onBack={() => setShowDetailPage(false)}
      />
    );
  }

  return (
    <div className="space-y-6">
      {/* 统计卡片 */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {stats.map((stat, index) => (
          <Card key={index} className="relative overflow-hidden dark:bg-gray-800 dark:border-gray-700">
            <div className="absolute top-0 right-0 w-32 h-32 opacity-10">
              <div className={`w-full h-full ${stat.iconBg} rounded-full blur-3xl transform translate-x-8 -translate-y-8`}></div>
            </div>
            <div className="p-6 relative">
              <div className="flex items-center justify-between mb-4">
                <div className={`p-3 rounded-xl ${stat.iconBg}/10`}>
                  <stat.icon className={`w-6 h-6 ${stat.iconBg.replace('bg-', 'text-')}`} />
                </div>
                <div className={`flex items-center gap-1 text-sm ${stat.trendDirection === 'up' ? 'text-green-600 dark:text-green-400' : 'text-red-600 dark:text-red-400'}`}>
                  <TrendingUp className={`w-4 h-4 ${stat.trendDirection === 'down' ? 'rotate-180' : ''}`} />
                  <span>{stat.trend}</span>
                </div>
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
        ))}
      </div>

      {/* 搜索和筛选 */}
      <Card className="p-6 dark:bg-gray-800 dark:border-gray-700">
        <div className="flex flex-col sm:flex-row gap-4 sm:justify-between">
          <div className="relative w-full sm:w-[390px]">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
            <Input
              placeholder="搜索租户名称、编码或管理员..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="pl-10 dark:bg-gray-900 dark:border-gray-700 dark:text-white"
            />
          </div>
          <div className="flex gap-4">
            <Select value={typeFilter} onValueChange={setTypeFilter}>
              <SelectTrigger className="w-[180px] dark:bg-gray-900 dark:border-gray-700 dark:text-white">
                <SelectValue placeholder="选择类型" />
              </SelectTrigger>
              <SelectContent className="dark:bg-gray-900 dark:border-gray-700">
                <SelectItem value="all">全部类型</SelectItem>
                <SelectItem value="企业">企业</SelectItem>
                <SelectItem value="个人">个人</SelectItem>
                <SelectItem value="未知">未知</SelectItem>
              </SelectContent>
            </Select>
            <Select value={statusFilter} onValueChange={setStatusFilter}>
              <SelectTrigger className="w-[180px] dark:bg-gray-900 dark:border-gray-700 dark:text-white">
                <SelectValue placeholder="选择状态" />
              </SelectTrigger>
              <SelectContent className="dark:bg-gray-900 dark:border-gray-700">
                <SelectItem value="all">全部状态</SelectItem>
                <SelectItem value="已启用">已启用</SelectItem>
                <SelectItem value="已禁用">已禁用</SelectItem>
              </SelectContent>
            </Select>
            <Button 
              className="bg-blue-500 hover:bg-blue-600 text-white"
              onClick={handleCreateTenant}
            >
              <Plus className="w-4 h-4 mr-2" />
              添加租户
            </Button>
          </div>
        </div>
      </Card>

      {/* 租户表格 */}
      <Card className="dark:bg-gray-800 dark:border-gray-700">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="border-b dark:border-gray-700">
              <tr>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">租户信息</th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">编码</th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">类型</th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">管理员</th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">规模</th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">状态</th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">到期时间</th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">操作</th>
              </tr>
            </thead>
            <tbody>
              {currentTenants.map((tenant) => (
                <tr key={tenant.id} className="border-b dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-700">
                  <td className="p-4">
                    <div className="flex items-center gap-3">
                      <Avatar className="w-10 h-10 rounded-lg">
                        <AvatarFallback className="bg-gradient-to-br from-blue-500 to-blue-600 text-white rounded-lg">
                          {tenant.name.slice(0, 2)}
                        </AvatarFallback>
                      </Avatar>
                      <div>
                        <div className="flex items-center gap-2">
                          <button
                            onClick={() => handleViewDetail(tenant)}
                            className="dark:text-white hover:text-blue-600 dark:hover:text-blue-400 transition-colors"
                          >
                            {tenant.name}
                          </button>
                          <Badge className={`text-xs ${
                            tenant.accountType === 'main'
                              ? 'bg-amber-100 text-amber-700 dark:bg-amber-900/30 dark:text-amber-400'
                              : 'bg-gray-100 text-gray-700 dark:bg-gray-700 dark:text-gray-400'
                          } border-0`}>
                            {tenant.accountType === 'main' ? '主账号' : '子账号'}
                          </Badge>
                        </div>
                        <div className="flex items-center gap-1 text-xs text-gray-500 dark:text-gray-400 mt-1">
                          <MapPin className="w-3 h-3" />
                          {tenant.address}
                        </div>
                      </div>
                    </div>
                  </td>
                  <td className="p-4">
                    <span className="text-sm text-gray-700 dark:text-gray-300 font-mono">{tenant.code}</span>
                  </td>
                  <td className="p-4">
                    <Badge className={`text-xs ${
                      tenant.type === '企业' 
                        ? 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400' 
                        : tenant.type === '个人'
                        ? 'bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-400'
                        : 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-400'
                    } border-0`}>
                      {tenant.type}
                    </Badge>
                  </td>
                  <td className="p-4">
                    <div>
                      <div className="text-sm text-gray-700 dark:text-gray-300">{tenant.adminName}</div>
                      <div className="flex items-center gap-2 text-xs text-gray-500 dark:text-gray-400 mt-1">
                        <Mail className="w-3 h-3" />
                        {tenant.adminEmail}
                      </div>
                    </div>
                  </td>
                  <td className="p-4">
                    <div className="space-y-1">
                      <div className="flex items-center gap-1 text-sm text-gray-700 dark:text-gray-300">
                        <Users className="w-3.5 h-3.5" />
                        {tenant.userCount} 用户
                      </div>
                      <div className="flex items-center gap-1 text-sm text-gray-700 dark:text-gray-300">
                        <Building2 className="w-3.5 h-3.5" />
                        {tenant.departmentCount} 部门
                      </div>
                    </div>
                  </td>
                  <td className="p-4">
                    <Badge className={`text-xs ${tenant.status === '已启用' ? 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400' : 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400'} border-0`}>
                      {tenant.status}
                    </Badge>
                  </td>
                  <td className="p-4">
                    <div className="flex items-center gap-1 text-sm text-gray-600 dark:text-gray-400">
                      <Calendar className="w-3.5 h-3.5" />
                      {tenant.expireDate}
                    </div>
                  </td>
                  <td className="p-4">
                    <DropdownMenu>
                      <DropdownMenuTrigger asChild>
                        <button className="p-1.5 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg">
                          <MoreVertical className="w-4 h-4 text-gray-600 dark:text-gray-400" />
                        </button>
                      </DropdownMenuTrigger>
                      <DropdownMenuContent align="end" className="dark:bg-gray-900 dark:border-gray-700">
                        <DropdownMenuItem 
                          onClick={() => handleViewDetail(tenant)}
                          className="dark:text-gray-300 dark:hover:bg-gray-800"
                        >
                          <Shield className="w-4 h-4 mr-2" />
                          查看详情
                        </DropdownMenuItem>
                        <DropdownMenuItem 
                          onClick={() => handleEditTenant(tenant)}
                          className="dark:text-gray-300 dark:hover:bg-gray-800"
                        >
                          <Edit className="w-4 h-4 mr-2" />
                          编辑
                        </DropdownMenuItem>
                        <DropdownMenuItem 
                          onClick={() => handleToggleStatus(tenant)}
                          className="dark:text-gray-300 dark:hover:bg-gray-800"
                        >
                          <Lock className="w-4 h-4 mr-2" />
                          {tenant.status === '已启用' ? '禁用' : '启用'}
                        </DropdownMenuItem>
                        <DropdownMenuItem 
                          onClick={() => handleDeleteTenant(tenant)}
                          disabled={tenant.accountType === 'main'}
                          className={`${
                            tenant.accountType === 'main'
                              ? 'opacity-50 cursor-not-allowed'
                              : 'text-red-600 dark:text-red-400'
                          } dark:hover:bg-gray-800`}
                        >
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

        {/* 分页 */}
        <div className="flex items-center justify-between px-6 py-4 border-t dark:border-gray-700">
          <div className="text-sm text-gray-600 dark:text-gray-400">
            显示 {startIndex + 1} 到 {Math.min(startIndex + itemsPerPage, filteredTenants.length)} 条，共 {filteredTenants.length} 条
          </div>
          <div className="flex gap-2">
            <Button
              variant="outline"
              size="sm"
              onClick={() => setCurrentPage(p => Math.max(1, p - 1))}
              disabled={currentPage === 1}
              className="dark:bg-gray-900 dark:border-gray-700 dark:text-gray-300"
            >
              上一页
            </Button>
            <Button
              variant="outline"
              size="sm"
              onClick={() => setCurrentPage(p => Math.min(totalPages, p + 1))}
              disabled={currentPage === totalPages}
              className="dark:bg-gray-900 dark:border-gray-700 dark:text-gray-300"
            >
              下一页
            </Button>
          </div>
        </div>
      </Card>

      {/* 对话框 */}
      <TenantDialog
        open={tenantDialogOpen}
        onOpenChange={setTenantDialogOpen}
        mode={dialogMode}
        tenant={selectedTenant}
      />

      <ConfirmDialog
        open={confirmDialogOpen}
        onOpenChange={setConfirmDialogOpen}
        title="确认删除"
        description={`确定要删除租户"${tenantToDelete}"吗？此操作无法撤销。`}
        onConfirm={confirmDelete}
      />
    </div>
  );
}