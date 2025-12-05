import { useState } from 'react';
import { Search, Plus, Bell, Eye, Edit, Trash2, MoreVertical, Calendar, Users, AlertCircle } from 'lucide-react';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Input } from '@/components/ui/input';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from '@/components/ui/dialog';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group';
import { ConfirmDialog } from '@/components/gm/ConfirmDialog';
import { toast } from 'sonner';

interface Announcement {
  id: string;
  title: string;
  content: string;
  scope: '全局' | '应用';
  appName?: string;
  expiryDate: string;
  sendType: '立即发送' | '定时发送';
  sendTime?: string;
  status: '已发送' | '待发送' | '已过期';
  viewCount: number;
  createdAt: string;
  createdBy: string;
}

export function AnnouncementManagement() {
  const [searchQuery, setSearchQuery] = useState('');
  const [dialogOpen, setDialogOpen] = useState(false);
  const [dialogMode, setDialogMode] = useState<'add' | 'edit' | 'view'>('add');
  const [selectedAnnouncement, setSelectedAnnouncement] = useState<Announcement | undefined>();
  const [confirmDialogOpen, setConfirmDialogOpen] = useState(false);
  const [announcementToDelete, setAnnouncementToDelete] = useState<string>('');

  const [formData, setFormData] = useState({
    title: '',
    content: '',
    scope: '全局',
    appName: '',
    expiryDate: '',
    sendType: '立即发送',
    sendTime: '',
  });

  const stats = [
    {
      label: '总公告数',
      value: '156',
      subtext: '本月新增 12 条',
      icon: Bell,
      iconBg: 'bg-blue-500',
      valueColor: 'text-blue-600 dark:text-blue-400',
      trend: '+8%',
    },
    {
      label: '待发送',
      value: '8',
      subtext: '定时发送任务',
      icon: Calendar,
      iconBg: 'bg-orange-500',
      valueColor: 'text-orange-600 dark:text-orange-400',
      trend: '+2',
    },
    {
      label: '已过期',
      value: '45',
      subtext: '本月过期 15 条',
      icon: AlertCircle,
      iconBg: 'bg-red-500',
      valueColor: 'text-red-600 dark:text-red-400',
      trend: '15',
    },
    {
      label: '总查看量',
      value: '12,456',
      subtext: '平均 79.8 次/条',
      icon: Eye,
      iconBg: 'bg-green-500',
      valueColor: 'text-green-600 dark:text-green-400',
      trend: '+23%',
    },
  ];

  const announcements: Announcement[] = [
    {
      id: 'A001',
      title: '系统维护通知',
      content: '系统将于本周六凌晨2:00-6:00进行维护升级，期间服务将暂时不可用，请提前做好准备。',
      scope: '全局',
      expiryDate: '2025-11-01',
      sendType: '立即发送',
      status: '已发送',
      viewCount: 1234,
      createdAt: '2025-10-15',
      createdBy: '系统管理员',
    },
    {
      id: 'A002',
      title: '新功能上线公告',
      content: 'AngusGM 2.0版本正式上线，新增部门管理、权限策略等功能，欢迎体验！',
      scope: '全局',
      expiryDate: '2025-10-30',
      sendType: '立即发送',
      status: '已发送',
      viewCount: 2156,
      createdAt: '2025-10-10',
      createdBy: '产品经理',
    },
    {
      id: 'A003',
      title: '应用升级通知',
      content: 'AI助手应用将在明天进行功能升级，升级期间可能会影响部分功能使用。',
      scope: '应用',
      appName: 'AI助手',
      expiryDate: '2025-10-25',
      sendType: '定时发送',
      sendTime: '2025-10-23 09:00',
      status: '待发送',
      viewCount: 0,
      createdAt: '2025-10-20',
      createdBy: '技术负责人',
    },
    {
      id: 'A004',
      title: '假期安排通知',
      content: '根据国家规定，国庆节放假时间为10月1日-7日，请各部门提前安排好工作。',
      scope: '全局',
      expiryDate: '2025-09-30',
      sendType: '立即发送',
      status: '已过期',
      viewCount: 3456,
      createdAt: '2025-09-20',
      createdBy: '人力资源部',
    },
  ];

  const filteredAnnouncements = announcements.filter(announcement =>
    announcement.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
    announcement.content.toLowerCase().includes(searchQuery.toLowerCase()) ||
    announcement.createdBy.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const handleAdd = () => {
    setDialogMode('add');
    setSelectedAnnouncement(undefined);
    setFormData({
      title: '',
      content: '',
      scope: '全局',
      appName: '',
      expiryDate: '',
      sendType: '立即发送',
      sendTime: '',
    });
    setDialogOpen(true);
  };

  const handleEdit = (announcement: Announcement) => {
    setDialogMode('edit');
    setSelectedAnnouncement(announcement);
    setFormData({
      title: announcement.title,
      content: announcement.content,
      scope: announcement.scope,
      appName: announcement.appName || '',
      expiryDate: announcement.expiryDate,
      sendType: announcement.sendType,
      sendTime: announcement.sendTime || '',
    });
    setDialogOpen(true);
  };

  const handleView = (announcement: Announcement) => {
    setDialogMode('view');
    setSelectedAnnouncement(announcement);
    setFormData({
      title: announcement.title,
      content: announcement.content,
      scope: announcement.scope,
      appName: announcement.appName || '',
      expiryDate: announcement.expiryDate,
      sendType: announcement.sendType,
      sendTime: announcement.sendTime || '',
    });
    setDialogOpen(true);
  };

  const handleDelete = (id: string) => {
    setAnnouncementToDelete(id);
    setConfirmDialogOpen(true);
  };

  const confirmDelete = () => {
    toast.success('删除公告成功');
    setAnnouncementToDelete('');
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!formData.title.trim()) {
      toast.error('请输入公告标题');
      return;
    }
    if (!formData.content.trim()) {
      toast.error('请输入公告内容');
      return;
    }
    if (!formData.expiryDate) {
      toast.error('请选择过期时间');
      return;
    }
    if (formData.sendType === '定时发送' && !formData.sendTime) {
      toast.error('请选择发送时间');
      return;
    }

    const actionText = dialogMode === 'add' ? '创建' : '更新';
    toast.success(`${actionText}公告成功`);
    setDialogOpen(false);
  };

  const getStatusBadge = (status: Announcement['status']) => {
    const styles = {
      '已发送': 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400',
      '待发送': 'bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-400',
      '已过期': 'bg-gray-100 text-gray-700 dark:bg-gray-700 dark:text-gray-400',
    };
    return <Badge className={`${styles[status]} border-0`}>{status}</Badge>;
  };

  const getScopeBadge = (scope: Announcement['scope']) => {
    return scope === '全局' ? (
      <Badge className="bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400 border-0">
        全局
      </Badge>
    ) : (
      <Badge variant="outline" className="dark:border-gray-600 dark:text-gray-400">
        应用
      </Badge>
    );
  };

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

      {/* 搜索和添加 */}
      <Card className="p-6 dark:bg-gray-800 dark:border-gray-700">
        <div className="flex items-center justify-between">
          <div className="relative w-96">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
            <Input
              placeholder="搜索公告标题、内容或创建人..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="pl-10 dark:bg-gray-900 dark:border-gray-700 dark:text-white"
            />
          </div>
          <Button
            className="bg-blue-500 hover:bg-blue-600 text-white"
            onClick={handleAdd}
          >
            <Plus className="w-4 h-4 mr-2" />
            创建公告
          </Button>
        </div>
      </Card>

      {/* 公告列表 */}
      <Card className="dark:bg-gray-800 dark:border-gray-700">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="border-b dark:border-gray-700 bg-gray-50 dark:bg-gray-900/50">
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">公告标题</th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">公告范围</th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">发送类型</th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">过期时间</th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">状态</th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">查看量</th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">创建人</th>
                <th className="text-right p-4 text-sm text-gray-600 dark:text-gray-400">操作</th>
              </tr>
            </thead>
            <tbody>
              {filteredAnnouncements.map((announcement) => (
                <tr
                  key={announcement.id}
                  className="border-b dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-700"
                >
                  <td className="p-4">
                    <div className="dark:text-white">{announcement.title}</div>
                    <div className="text-sm text-gray-500 dark:text-gray-400 mt-1 line-clamp-1">
                      {announcement.content}
                    </div>
                  </td>
                  <td className="p-4">
                    <div className="flex flex-col gap-1">
                      {getScopeBadge(announcement.scope)}
                      {announcement.appName && (
                        <div className="text-xs text-gray-500 dark:text-gray-400">
                          {announcement.appName}
                        </div>
                      )}
                    </div>
                  </td>
                  <td className="p-4">
                    <div className="text-sm text-gray-700 dark:text-gray-300">
                      {announcement.sendType}
                    </div>
                    {announcement.sendTime && (
                      <div className="text-xs text-gray-500 dark:text-gray-400 mt-1">
                        {announcement.sendTime}
                      </div>
                    )}
                  </td>
                  <td className="p-4 text-sm text-gray-700 dark:text-gray-300">
                    {announcement.expiryDate}
                  </td>
                  <td className="p-4">
                    {getStatusBadge(announcement.status)}
                  </td>
                  <td className="p-4">
                    <div className="flex items-center gap-1 text-sm text-gray-700 dark:text-gray-300">
                      <Eye className="w-3.5 h-3.5" />
                      {announcement.viewCount}
                    </div>
                  </td>
                  <td className="p-4 text-sm text-gray-700 dark:text-gray-300">
                    {announcement.createdBy}
                  </td>
                  <td className="p-4">
                    <div className="flex justify-end">
                      <DropdownMenu>
                        <DropdownMenuTrigger asChild>
                          <button className="p-1.5 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg">
                            <MoreVertical className="w-4 h-4 text-gray-600 dark:text-gray-400" />
                          </button>
                        </DropdownMenuTrigger>
                        <DropdownMenuContent align="end" className="dark:bg-gray-900 dark:border-gray-700">
                          <DropdownMenuItem
                            onClick={() => handleView(announcement)}
                            className="dark:text-gray-300 dark:hover:bg-gray-800"
                          >
                            <Eye className="w-4 h-4 mr-2" />
                            查看详情
                          </DropdownMenuItem>
                          <DropdownMenuItem
                            onClick={() => handleEdit(announcement)}
                            className="dark:text-gray-300 dark:hover:bg-gray-800"
                            disabled={announcement.status === '已过期'}
                          >
                            <Edit className="w-4 h-4 mr-2" />
                            编辑
                          </DropdownMenuItem>
                          <DropdownMenuItem
                            onClick={() => handleDelete(announcement.id)}
                            className="text-red-600 dark:text-red-400 dark:hover:bg-gray-800"
                          >
                            <Trash2 className="w-4 h-4 mr-2" />
                            删除
                          </DropdownMenuItem>
                        </DropdownMenuContent>
                      </DropdownMenu>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </Card>

      {/* 创建/编辑公告对话框 */}
      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent className="max-w-2xl dark:bg-gray-800 dark:border-gray-700" aria-describedby={undefined}>
          <DialogHeader>
            <DialogTitle className="dark:text-white">
              {dialogMode === 'add' ? '创建公告' : dialogMode === 'edit' ? '编辑公告' : '公告详情'}
            </DialogTitle>
            <DialogDescription className="dark:text-gray-400">
              {dialogMode === 'view' ? '查公告详细信息' : '填写公告���息'}
            </DialogDescription>
          </DialogHeader>

          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="space-y-4">
              <div className="space-y-2">
                <Label className="dark:text-gray-300">
                  公告标题 <span className="text-red-500">*</span>
                </Label>
                <Input
                  value={formData.title}
                  onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                  placeholder="请输入公告标题"
                  className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
                  disabled={dialogMode === 'view'}
                />
              </div>

              <div className="space-y-2">
                <Label className="dark:text-gray-300">
                  公告内容 <span className="text-red-500">*</span>
                </Label>
                <Textarea
                  value={formData.content}
                  onChange={(e) => setFormData({ ...formData, content: e.target.value })}
                  placeholder="限制输入200字"
                  rows={5}
                  maxLength={200}
                  className="dark:bg-gray-900 dark:border-gray-700 dark:text-white resize-none"
                  disabled={dialogMode === 'view'}
                />
                <div className="text-xs text-gray-500 dark:text-gray-400 text-right">
                  {formData.content.length}/200
                </div>
              </div>

              <div className="space-y-2">
                <Label className="dark:text-gray-300">
                  公告范围 <span className="text-red-500">*</span>
                </Label>
                <Select
                  value={formData.scope}
                  onValueChange={(value: any) => setFormData({ ...formData, scope: value })}
                  disabled={dialogMode === 'view'}
                >
                  <SelectTrigger className="dark:bg-gray-900 dark:border-gray-700 dark:text-white">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent className="dark:bg-gray-900 dark:border-gray-700">
                    <SelectItem value="全局">全局</SelectItem>
                    <SelectItem value="应用">应用</SelectItem>
                  </SelectContent>
                </Select>
                <div className="flex items-start gap-2 text-xs text-blue-600 dark:text-blue-400 bg-blue-50 dark:bg-blue-900/20 p-3 rounded-lg">
                  <AlertCircle className="w-4 h-4 flex-shrink-0 mt-0.5" />
                  <div>
                    &quot;全局&quot;公告针对所有应用生效，&quot;应用&quot;公告只针对指定应用有效。当应用同时存在&quot;全局&quot;公告和&quot;应用&quot;公告时，优先展示前者的。
                  </div>
                </div>
              </div>

              <div className="space-y-2">
                <Label className="dark:text-gray-300">
                  过期时间 <span className="text-red-500">*</span>
                </Label>
                <Input
                  type="date"
                  value={formData.expiryDate}
                  onChange={(e) => setFormData({ ...formData, expiryDate: e.target.value })}
                  className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
                  disabled={dialogMode === 'view'}
                />
              </div>

              <div className="space-y-2">
                <Label className="dark:text-gray-300">发送类型</Label>
                <RadioGroup
                  value={formData.sendType}
                  onValueChange={(value: any) => setFormData({ ...formData, sendType: value })}
                  disabled={dialogMode === 'view'}
                >
                  <div className="flex items-center space-x-6">
                    <div className="flex items-center space-x-2">
                      <RadioGroupItem value="立即发送" id="immediate" />
                      <Label htmlFor="immediate" className="cursor-pointer dark:text-gray-300">
                        立即发送
                      </Label>
                    </div>
                    <div className="flex items-center space-x-2">
                      <RadioGroupItem value="定时发送" id="scheduled" />
                      <Label htmlFor="scheduled" className="cursor-pointer dark:text-gray-300">
                        定时发送
                      </Label>
                    </div>
                  </div>
                </RadioGroup>
              </div>

              {formData.sendType === '定时发送' && (
                <div className="space-y-2">
                  <Label className="dark:text-gray-300">
                    发送时间 <span className="text-red-500">*</span>
                  </Label>
                  <Input
                    type="datetime-local"
                    value={formData.sendTime}
                    onChange={(e) => setFormData({ ...formData, sendTime: e.target.value })}
                    className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
                    disabled={dialogMode === 'view'}
                  />
                </div>
              )}
            </div>

            {dialogMode !== 'view' && (
              <div className="flex justify-end gap-3 pt-4 border-t dark:border-gray-700">
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => setDialogOpen(false)}
                  className="dark:bg-gray-900 dark:border-gray-700 dark:text-gray-300"
                >
                  取消
                </Button>
                <Button
                  type="submit"
                  className="bg-blue-500 hover:bg-blue-600 text-white"
                >
                  {dialogMode === 'add' ? '确定' : '保存'}
                </Button>
              </div>
            )}
          </form>
        </DialogContent>
      </Dialog>

      {/* 删除确认对话框 */}
      <ConfirmDialog
        open={confirmDialogOpen}
        onOpenChange={setConfirmDialogOpen}
        title="确认删除"
        description="确定要删除该公告吗？此操作不可撤销。"
        onConfirm={confirmDelete}
        confirmText="删除"
        variant="danger"
      />
    </div>
  );
}