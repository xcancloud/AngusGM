import { useState } from 'react';
import { Search, Plus, Users, TrendingUp, Edit, Trash2, MoreVertical, UserPlus, Shield, Tag, Calendar } from 'lucide-react';
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
import { GroupDialog, AddGroupMemberDialog } from '@/pages/groups/GroupDialog';
import { GroupDetail } from '@/pages/groups/GroupDetail';
import { ConfirmDialog } from '@/components/gm/ConfirmDialog';
import { toast } from 'sonner';

interface Group {
  id: string;
  name: string;
  description: string;
  type: '项目组' | '职能组' | '临时组';
  ownerName: string;
  ownerAvatar: string;
  memberCount: number;
  status: '活跃' | '归档';
  tags: string[];
  createdAt: string;
  lastActive: string;
}

export function GroupManagement() {
  const [searchQuery, setSearchQuery] = useState('');
  const [typeFilter, setTypeFilter] = useState<string>('all');
  const [statusFilter, setStatusFilter] = useState<string>('all');
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 3; // 大屏默认展示三条
  const [groupDialogOpen, setGroupDialogOpen] = useState(false);
  const [memberDialogOpen, setMemberDialogOpen] = useState(false);
  const [showDetailPage, setShowDetailPage] = useState(false);
  const [confirmDialogOpen, setConfirmDialogOpen] = useState(false);
  const [dialogMode, setDialogMode] = useState<'create' | 'edit'>('create');
  const [selectedGroup, setSelectedGroup] = useState<Group | undefined>();
  const [groupToDelete, setGroupToDelete] = useState<string>('');

  const stats = [
    {
      label: '总组数',
      value: '89',
      subtext: '本月新增 12 个',
      icon: Users,
      iconBg: 'bg-blue-500',
      valueColor: 'text-blue-600 dark:text-blue-400',
      trend: '+15%',
      trendDirection: 'up',
    },
    {
      label: '项目组',
      value: '42',
      subtext: '占比 47.2%',
      icon: Tag,
      iconBg: 'bg-purple-500',
      valueColor: 'text-purple-600 dark:text-purple-400',
      trend: '+8',
      trendDirection: 'up',
    },
    {
      label: '职能组',
      value: '35',
      subtext: '占比 39.3%',
      icon: Shield,
      iconBg: 'bg-green-500',
      valueColor: 'text-green-600 dark:text-green-400',
      trend: '+3',
      trendDirection: 'up',
    },
    {
      label: '活跃成员',
      value: '567',
      subtext: '平均 6.4 人/组',
      icon: UserPlus,
      iconBg: 'bg-orange-500',
      valueColor: 'text-orange-600 dark:text-orange-400',
      trend: '+18%',
      trendDirection: 'up',
    },
  ];

  const groups: Group[] = [
    {
      id: 'G001',
      name: 'AngusGM项目组',
      description: 'AngusGM全局管理平台核心开发团队',
      type: '项目组',
      ownerName: '张三',
      ownerAvatar: 'https://images.unsplash.com/photo-1652795385761-7ac287d0cd03?w=100',
      memberCount: 15,
      status: '活跃',
      tags: ['开发', '核心', '高优先级'],
      createdAt: '2024-01-15',
      lastActive: '2025-10-22 14:30',
    },
    {
      id: 'G002',
      name: '前端技术委员会',
      description: '前端技术标准制定与技术选型',
      type: '职能组',
      ownerName: '李四',
      ownerAvatar: 'https://images.unsplash.com/photo-1652795385761-7ac287d0cd03?w=100',
      memberCount: 8,
      status: '活跃',
      tags: ['技术', '标准'],
      createdAt: '2024-02-20',
      lastActive: '2025-10-22 10:15',
    },
    {
      id: 'G003',
      name: '年会筹备组',
      description: '2025年公司年会活动策划与执行',
      type: '临时组',
      ownerName: '王五',
      ownerAvatar: 'https://images.unsplash.com/photo-1652795385761-7ac287d0cd03?w=100',
      memberCount: 12,
      status: '活跃',
      tags: ['活动', '临时'],
      createdAt: '2024-10-01',
      lastActive: '2025-10-21 16:45',
    },
    {
      id: 'G004',
      name: '安全审计组',
      description: '系统安全审计与漏洞修复',
      type: '职能组',
      ownerName: '赵六',
      ownerAvatar: 'https://images.unsplash.com/photo-1652795385761-7ac287d0cd03?w=100',
      memberCount: 6,
      status: '活跃',
      tags: ['安全', '审计'],
      createdAt: '2024-03-10',
      lastActive: '2025-10-22 09:20',
    },
    {
      id: 'G005',
      name: '移动端重构项目',
      description: '移动端应用架构重构',
      type: '项目组',
      ownerName: '孙七',
      ownerAvatar: 'https://images.unsplash.com/photo-1652795385761-7ac287d0cd03?w=100',
      memberCount: 18,
      status: '活跃',
      tags: ['开发', '重构', 'Mobile'],
      createdAt: '2024-05-20',
      lastActive: '2025-10-22 13:40',
    },
    {
      id: 'G006',
      name: '用户体验优化组',
      description: '产品用户体验研究与优化',
      type: '职能组',
      ownerName: '周八',
      ownerAvatar: 'https://images.unsplash.com/photo-1652795385761-7ac287d0cd03?w=100',
      memberCount: 9,
      status: '活跃',
      tags: ['UX', '优化'],
      createdAt: '2024-04-15',
      lastActive: '2025-10-21 15:30',
    },
    {
      id: 'G007',
      name: '数据迁移专项组',
      description: '旧系统数据迁移项目',
      type: '项目组',
      ownerName: '吴九',
      ownerAvatar: 'https://images.unsplash.com/photo-1652795385761-7ac287d0cd03?w=100',
      memberCount: 10,
      status: '归档',
      tags: ['数据', '迁移', '已完成'],
      createdAt: '2024-01-10',
      lastActive: '2024-09-30 18:00',
    },
    {
      id: 'G008',
      name: 'API网关建设组',
      description: '统一API网关平台建设',
      type: '项目组',
      ownerName: '郑十',
      ownerAvatar: 'https://images.unsplash.com/photo-1652795385761-7ac287d0cd03?w=100',
      memberCount: 12,
      status: '活跃',
      tags: ['基础设施', 'API'],
      createdAt: '2024-06-01',
      lastActive: '2025-10-22 11:25',
    },
    {
      id: 'G009',
      name: '新员工导师组',
      description: '新员工入职培训与辅导',
      type: '职能组',
      ownerName: '钱一',
      ownerAvatar: 'https://images.unsplash.com/photo-1652795385761-7ac287d0cd03?w=100',
      memberCount: 15,
      status: '活跃',
      tags: ['培训', 'HR'],
      createdAt: '2024-01-20',
      lastActive: '2025-10-20 14:10',
    },
    {
      id: 'G010',
      name: 'Q3营销活动组',
      description: '第三季度营销活动策划',
      type: '临时组',
      ownerName: '刘二',
      ownerAvatar: 'https://images.unsplash.com/photo-1652795385761-7ac287d0cd03?w=100',
      memberCount: 8,
      status: '归档',
      tags: ['营销', '已完成'],
      createdAt: '2024-07-01',
      lastActive: '2024-09-30 17:00',
    },
  ];

  const filteredGroups = groups.filter(group => {
    const matchesSearch = group.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
                         group.description.toLowerCase().includes(searchQuery.toLowerCase()) ||
                         group.ownerName.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesType = typeFilter === 'all' || group.type === typeFilter;
    const matchesStatus = statusFilter === 'all' || group.status === statusFilter;
    return matchesSearch && matchesType && matchesStatus;
  });

  const totalPages = Math.ceil(filteredGroups.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const currentGroups = filteredGroups.slice(startIndex, startIndex + itemsPerPage);

  const handleCreateGroup = () => {
    setDialogMode('create');
    setSelectedGroup(undefined);
    setGroupDialogOpen(true);
  };

  const handleEditGroup = (group: Group) => {
    setDialogMode('edit');
    setSelectedGroup(group);
    setGroupDialogOpen(true);
  };

  const handleAddMember = (group: Group) => {
    setSelectedGroup(group);
    setMemberDialogOpen(true);
  };

  const handleViewDetail = (group: Group) => {
    setSelectedGroup(group);
    setShowDetailPage(true);
  };

  const handleDeleteGroup = (groupName: string) => {
    setGroupToDelete(groupName);
    setConfirmDialogOpen(true);
  };

  const confirmDelete = () => {
    toast.success(`删除组: ${groupToDelete}`);
    setGroupToDelete('');
  };

  const getTypeColor = (type: string) => {
    switch (type) {
      case '项目组':
        return 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400';
      case '职能组':
        return 'bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-400';
      case '临时组':
        return 'bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-400';
      default:
        return 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-400';
    }
  };

  // 如果显示详情页面，则渲染GroupDetail组件
  if (showDetailPage && selectedGroup) {
    return (
      <GroupDetail 
        group={selectedGroup} 
        onBack={() => setShowDetailPage(false)} 
      />
    );
  }

  return (
    <div className="space-y-6">
      {/* 统计卡片 */}
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
          );
        })}
      </div>

      {/* 搜索和筛选 */}
      <Card className="p-6 dark:bg-gray-800 dark:border-gray-700">
        <div className="flex flex-col sm:flex-row gap-4 sm:justify-between">
          <div className="relative w-full sm:w-[390px]">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
            <Input
              placeholder="搜索组名称、描述或负责人..."
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
                <SelectItem value="项目组">项目组</SelectItem>
                <SelectItem value="职能组">职能组</SelectItem>
                <SelectItem value="临时组">临时组</SelectItem>
              </SelectContent>
            </Select>
            <Select value={statusFilter} onValueChange={setStatusFilter}>
              <SelectTrigger className="w-[180px] dark:bg-gray-900 dark:border-gray-700 dark:text-white">
                <SelectValue placeholder="选择状态" />
              </SelectTrigger>
              <SelectContent className="dark:bg-gray-900 dark:border-gray-700">
                <SelectItem value="all">全部状态</SelectItem>
                <SelectItem value="活跃">活跃</SelectItem>
                <SelectItem value="归档">归档</SelectItem>
              </SelectContent>
            </Select>
            <Button 
              className="bg-blue-500 hover:bg-blue-600 text-white"
              onClick={handleCreateGroup}
            >
              <Plus className="w-4 h-4 mr-2" />
              创建组
            </Button>
          </div>
        </div>
      </Card>

      {/* 组列表 */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {currentGroups.map((group) => (
          <Card key={group.id} className="dark:bg-gray-800 dark:border-gray-700 hover:shadow-lg transition-shadow">
            <div className="p-6">
              {/* 头部 */}
              <div className="flex items-start justify-between mb-4">
                <div className="flex-1">
                  <div className="flex items-center gap-2 mb-2">
                    <button
                      onClick={() => handleViewDetail(group)}
                      className="dark:text-white hover:text-blue-600 dark:hover:text-blue-400 cursor-pointer transition-colors"
                    >
                      {group.name}
                    </button>
                    <Badge className={`text-xs ${getTypeColor(group.type)} border-0`}>
                      {group.type}
                    </Badge>
                  </div>
                  <p className="text-sm text-gray-600 dark:text-gray-400 line-clamp-2">
                    {group.description}
                  </p>
                </div>
                <DropdownMenu>
                  <DropdownMenuTrigger asChild>
                    <button className="p-1.5 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg">
                      <MoreVertical className="w-4 h-4 text-gray-600 dark:text-gray-400" />
                    </button>
                  </DropdownMenuTrigger>
                  <DropdownMenuContent align="end" className="dark:bg-gray-900 dark:border-gray-700">
                    <DropdownMenuItem 
                      onClick={() => handleViewDetail(group)}
                      className="dark:text-gray-300 dark:hover:bg-gray-800"
                    >
                      <Shield className="w-4 h-4 mr-2" />
                      查看详情
                    </DropdownMenuItem>
                    <DropdownMenuItem 
                      onClick={() => handleEditGroup(group)}
                      className="dark:text-gray-300 dark:hover:bg-gray-800"
                    >
                      <Edit className="w-4 h-4 mr-2" />
                      编辑
                    </DropdownMenuItem>
                    <DropdownMenuItem 
                      onClick={() => handleAddMember(group)}
                      className="dark:text-gray-300 dark:hover:bg-gray-800"
                    >
                      <UserPlus className="w-4 h-4 mr-2" />
                      添���成员
                    </DropdownMenuItem>
                    <DropdownMenuItem 
                      onClick={() => handleDeleteGroup(group.name)}
                      className="text-red-600 dark:text-red-400 dark:hover:bg-gray-800"
                    >
                      <Trash2 className="w-4 h-4 mr-2" />
                      删除
                    </DropdownMenuItem>
                  </DropdownMenuContent>
                </DropdownMenu>
              </div>

              {/* 标签 */}
              {group.tags.length > 0 && (
                <div className="flex flex-wrap gap-2 mb-4">
                  {group.tags.map((tag, index) => (
                    <Badge key={index} variant="outline" className="text-xs dark:border-gray-600 dark:text-gray-400">
                      {tag}
                    </Badge>
                  ))}
                </div>
              )}

              {/* 负责人和成员数 */}
              <div className="flex items-center justify-between mb-4 pb-4 border-b dark:border-gray-700">
                <div className="flex items-center gap-2">
                  <Avatar className="w-8 h-8 rounded-full overflow-hidden">
                    <AvatarImage src={group.ownerAvatar} alt={group.ownerName} className="object-cover" />
                    <AvatarFallback className="bg-blue-500 text-white text-xs">
                      {group.ownerName.slice(0, 2)}
                    </AvatarFallback>
                  </Avatar>
                  <div>
                    <div className="text-xs text-gray-500 dark:text-gray-400">负责人</div>
                    <div className="text-sm dark:text-white">{group.ownerName}</div>
                  </div>
                </div>
                <div className="text-right">
                  <div className="text-xs text-gray-500 dark:text-gray-400">成员数</div>
                  <div className="text-sm dark:text-white flex items-center gap-1">
                    <Users className="w-3.5 h-3.5" />
                    {group.memberCount}
                  </div>
                </div>
              </div>

              {/* 底部信息 */}
              <div className="flex items-center justify-between text-xs text-gray-500 dark:text-gray-400">
                <div className="flex items-center gap-1">
                  <Calendar className="w-3.5 h-3.5" />
                  创建于 {group.createdAt}
                </div>
                <div className="flex items-center gap-2">
                  <div className={`w-2 h-2 rounded-full ${group.status === '活跃' ? 'bg-green-500' : 'bg-gray-400'}`}></div>
                  <span>最后活跃: {group.lastActive}</span>
                </div>
              </div>
            </div>
          </Card>
        ))}
      </div>

      {/* 分页 */}
      {filteredGroups.length > itemsPerPage && (
        <Card className="p-4 dark:bg-gray-800 dark:border-gray-700">
          <div className="flex items-center justify-between">
            <div className="text-sm text-gray-600 dark:text-gray-400">
              显示 {startIndex + 1} 到 {Math.min(startIndex + itemsPerPage, filteredGroups.length)} 条，共 {filteredGroups.length} 条
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
      )}

      {/* 对话框 */}
      <GroupDialog
        open={groupDialogOpen}
        onOpenChange={setGroupDialogOpen}
        mode={dialogMode}
        group={selectedGroup}
      />

      <AddGroupMemberDialog
        open={memberDialogOpen}
        onOpenChange={setMemberDialogOpen}
        groupName={selectedGroup?.name || ''}
      />

      <ConfirmDialog
        open={confirmDialogOpen}
        onOpenChange={setConfirmDialogOpen}
        title="确认删除"
        description={`确定要删除组 "${groupToDelete}" 吗？此操作不可撤销。`}
        onConfirm={confirmDelete}
        confirmText="删除"
        variant="danger"
      />
    </div>
  );
}