import { useState } from 'react';
import { Tag as TagIcon, Search, Plus, Edit2, Trash2, X, Clock, Shield, User } from 'lucide-react';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { toast } from 'sonner';

interface TagItem {
  id: string;
  name: string;
  description: string;
  creator: string;
  createTime: string;
  updateTime: string;
  isSystem: boolean; // 是否为系统定义标签
}

export function TagManagement() {
  const [tags, setTags] = useState<TagItem[]>([
    {
      id: '1',
      name: 'HEADER_MENU_POPOVER',
      description: '头部菜单自定义下拉菜单',
      creator: '系统',
      createTime: '2024-11-15 10:30:00',
      updateTime: '2024-11-15 10:30:00',
      isSystem: true,
    },
    {
      id: '2',
      name: 'DYNAMIC_POSITION',
      description: '标识动态位置显示功能',
      creator: '系统',
      createTime: '2024-11-16 09:20:00',
      updateTime: '2024-11-16 09:20:00',
      isSystem: true,
    },
    {
      id: '3',
      name: 'FIXED_POSITION',
      description: '标识固定位置显示功能',
      creator: '系统',
      createTime: '2024-11-16 09:25:00',
      updateTime: '2024-11-16 09:25:00',
      isSystem: true,
    },
    {
      id: '4',
      name: 'CLOUD_SERVICE',
      description: '标识云服务应用',
      creator: '系统',
      createTime: '2024-11-17 14:10:00',
      updateTime: '2024-11-17 14:10:00',
      isSystem: true,
    },
    {
      id: '5',
      name: 'ENTERPRISE',
      description: '标识企业版应用',
      creator: '系统',
      createTime: '2024-11-17 14:15:00',
      updateTime: '2024-11-17 14:15:00',
      isSystem: true,
    },
    {
      id: '6',
      name: 'DATACENTER',
      description: '标识数据中心版应用',
      creator: '系统',
      createTime: '2024-11-17 14:20:00',
      updateTime: '2024-11-17 14:20:00',
      isSystem: true,
    },
    {
      id: '7',
      name: 'COMMUNITY',
      description: '标识社区版应用',
      creator: '系统',
      createTime: '2024-11-17 14:25:00',
      updateTime: '2024-11-17 14:25:00',
      isSystem: true,
    },
    {
      id: '8',
      name: 'DISPLAY_ON_NAVIGATOR',
      description: '控制在导航栏展示',
      creator: '系统',
      createTime: '2024-11-18 11:00:00',
      updateTime: '2024-11-18 11:00:00',
      isSystem: true,
    },
    {
      id: '9',
      name: 'DISPLAY_ON_MENU',
      description: '控制在菜单栏展示',
      creator: '系统',
      createTime: '2024-11-18 11:05:00',
      updateTime: '2024-11-18 11:05:00',
      isSystem: true,
    },
    {
      id: '10',
      name: 'HIGH_PRIORITY',
      description: '标识高优先级功能',
      creator: '张伟',
      createTime: '2024-11-20 10:00:00',
      updateTime: '2024-11-20 10:00:00',
      isSystem: false,
    },
    {
      id: '11',
      name: 'BETA_FEATURE',
      description: '标识测试版功能',
      creator: '王芳',
      createTime: '2024-11-21 15:30:00',
      updateTime: '2024-11-21 15:30:00',
      isSystem: false,
    },
    {
      id: '12',
      name: 'EXPERIMENTAL',
      description: '标识实验性功能',
      creator: '李明',
      createTime: '2024-11-22 09:00:00',
      updateTime: '2024-11-22 09:00:00',
      isSystem: false,
    },
  ]);

  const [searchQuery, setSearchQuery] = useState('');
  const [showAddDialog, setShowAddDialog] = useState(false);
  const [showEditDialog, setShowEditDialog] = useState(false);
  const [showDeleteDialog, setShowDeleteDialog] = useState(false);
  const [currentTag, setCurrentTag] = useState<TagItem | null>(null);
  const [formData, setFormData] = useState({
    name: '',
    description: '',
  });
  const [currentPage, setCurrentPage] = useState(1);
  const pageSize = 10;

  // 筛选标签
  const filteredTags = tags.filter((tag) =>
    tag.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
    tag.description.toLowerCase().includes(searchQuery.toLowerCase()) ||
    tag.creator.toLowerCase().includes(searchQuery.toLowerCase())
  );

  // 分页
  const totalPages = Math.ceil(filteredTags.length / pageSize);
  const paginatedTags = filteredTags.slice(
    (currentPage - 1) * pageSize,
    currentPage * pageSize
  );

  // 打开新增对话框
  const handleAdd = () => {
    setFormData({ name: '', description: '' });
    setShowAddDialog(true);
  };

  // 打开编辑对话框
  const handleEdit = (tag: TagItem) => {
    if (tag.isSystem) {
      toast.error('系统定义标签不允许编辑');
      return;
    }
    setCurrentTag(tag);
    setFormData({
      name: tag.name,
      description: tag.description,
    });
    setShowEditDialog(true);
  };

  // 打开删除对话框
  const handleDelete = (tag: TagItem) => {
    if (tag.isSystem) {
      toast.error('系统定义标签不允许删除');
      return;
    }
    setCurrentTag(tag);
    setShowDeleteDialog(true);
  };

  // 确认新增
  const handleConfirmAdd = () => {
    if (!formData.name.trim()) {
      toast.error('请输入标签名称');
      return;
    }
    if (!formData.description.trim()) {
      toast.error('请输入标签描述');
      return;
    }

    // 检查标签名称是否已存在
    if (tags.some((tag) => tag.name === formData.name)) {
      toast.error('标签名称已存在');
      return;
    }

    const newTag: TagItem = {
      id: String(Date.now()),
      name: formData.name.trim(),
      description: formData.description.trim(),
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
      isSystem: false,
    };

    setTags([newTag, ...tags]);
    setShowAddDialog(false);
    toast.success('标签创建成功');
  };

  // 确认编辑
  const handleConfirmEdit = () => {
    if (!currentTag) return;

    if (!formData.name.trim()) {
      toast.error('请输入标签名称');
      return;
    }
    if (!formData.description.trim()) {
      toast.error('请输入标签描述');
      return;
    }

    // 检查标签名称是否与其他标签重复
    if (tags.some((tag) => tag.id !== currentTag.id && tag.name === formData.name)) {
      toast.error('标签名称已存在');
      return;
    }

    setTags(
      tags.map((tag) =>
        tag.id === currentTag.id
          ? {
              ...tag,
              name: formData.name.trim(),
              description: formData.description.trim(),
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
          : tag
      )
    );
    setShowEditDialog(false);
    setCurrentTag(null);
    toast.success('标签更新成功');
  };

  // 确认删除
  const handleConfirmDelete = () => {
    if (!currentTag) return;

    setTags(tags.filter((tag) => tag.id !== currentTag.id));
    setShowDeleteDialog(false);
    setCurrentTag(null);
    toast.success('标签删除成功');
  };

  return (
    <div className="space-y-6">
      {/* 页面标题 */}
      <div className="flex items-center justify-between">
        <div>
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-gradient-to-br from-blue-500 to-blue-600 rounded-lg flex items-center justify-center">
              <TagIcon className="w-6 h-6 text-white" />
            </div>
            <div>
              <h1 className="text-2xl dark:text-white">标签管理</h1>
              <p className="text-sm text-gray-500 dark:text-gray-400">管理应用服务的功能标签</p>
            </div>
          </div>
        </div>
      </div>

      {/* 统计卡片 */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card className="dark:bg-gray-800 dark:border-gray-700 p-4">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-500 dark:text-gray-400">总标签数</p>
              <p className="text-2xl mt-2 dark:text-white">{tags.length}</p>
            </div>
            <div className="w-12 h-12 bg-blue-100 dark:bg-blue-900/30 rounded-lg flex items-center justify-center">
              <TagIcon className="w-6 h-6 text-blue-600 dark:text-blue-400" />
            </div>
          </div>
        </Card>

        <Card className="dark:bg-gray-800 dark:border-gray-700 p-4">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-500 dark:text-gray-400">系统定义</p>
              <p className="text-2xl mt-2 dark:text-white">
                {tags.filter((t) => t.isSystem).length}
              </p>
            </div>
            <div className="w-12 h-12 bg-purple-100 dark:bg-purple-900/30 rounded-lg flex items-center justify-center">
              <Shield className="w-6 h-6 text-purple-600 dark:text-purple-400" />
            </div>
          </div>
        </Card>

        <Card className="dark:bg-gray-800 dark:border-gray-700 p-4">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-500 dark:text-gray-400">自定义</p>
              <p className="text-2xl mt-2 dark:text-white">
                {tags.filter((t) => !t.isSystem).length}
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
              <p className="text-sm text-gray-500 dark:text-gray-400">本周新增</p>
              <p className="text-2xl mt-2 dark:text-white">3</p>
            </div>
            <div className="w-12 h-12 bg-orange-100 dark:bg-orange-900/30 rounded-lg flex items-center justify-center">
              <Plus className="w-6 h-6 text-orange-600 dark:text-orange-400" />
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
              placeholder="搜索标签名称、描述、创建人..."
              value={searchQuery}
              onChange={(e) => {
                setSearchQuery(e.target.value);
                setCurrentPage(1);
              }}
              className="pl-10 dark:bg-gray-900 dark:border-gray-700"
            />
          </div>
          <Button onClick={handleAdd} className="bg-blue-600 hover:bg-blue-700">
            <Plus className="w-4 h-4 mr-2" />
            新增标签
          </Button>
        </div>
      </Card>

      {/* 标签列表 */}
      <Card className="dark:bg-gray-800 dark:border-gray-700">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-gray-50 dark:bg-gray-900/50 border-b dark:border-gray-700">
              <tr>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">标签名</th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">类型</th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">描述</th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">创建人</th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">创建时间</th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">更新时间</th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">操作</th>
              </tr>
            </thead>
            <tbody className="divide-y dark:divide-gray-700">
              {paginatedTags.length > 0 ? (
                paginatedTags.map((tag) => (
                  <tr
                    key={tag.id}
                    className="hover:bg-gray-50 dark:hover:bg-gray-700/50 transition-colors"
                  >
                    <td className="p-4">
                      <Badge className="bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400 border-0 font-mono">
                        {tag.name}
                      </Badge>
                    </td>
                    <td className="p-4">
                      {tag.isSystem ? (
                        <Badge className="bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-400 border-0 flex items-center gap-1 w-fit">
                          <Shield className="w-3 h-3" />
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
                      <span className="text-sm dark:text-gray-300">{tag.description}</span>
                    </td>
                    <td className="p-4">
                      <span className="text-sm dark:text-gray-300">{tag.creator}</span>
                    </td>
                    <td className="p-4">
                      <span className="text-sm text-gray-500 dark:text-gray-400">{tag.createTime}</span>
                    </td>
                    <td className="p-4">
                      <span className="text-sm text-gray-500 dark:text-gray-400">{tag.updateTime}</span>
                    </td>
                    <td className="p-4">
                      <div className="flex items-center gap-2">
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => handleEdit(tag)}
                          disabled={tag.isSystem}
                          className={tag.isSystem ? 'opacity-50 cursor-not-allowed' : 'dark:hover:bg-gray-600'}
                        >
                          <Edit2 className="w-4 h-4" />
                        </Button>
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => handleDelete(tag)}
                          disabled={tag.isSystem}
                          className={tag.isSystem ? 'opacity-50 cursor-not-allowed text-gray-400' : 'dark:hover:bg-gray-600 text-red-600 hover:text-red-700'}
                        >
                          <Trash2 className="w-4 h-4" />
                        </Button>
                      </div>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={7} className="p-12 text-center">
                    <div className="text-gray-500 dark:text-gray-400">
                      <TagIcon className="w-12 h-12 mx-auto mb-3 opacity-50" />
                      <p>未找到匹配的标签</p>
                    </div>
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        {/* 分页 */}
        {totalPages > 1 && (
          <div className="p-4 border-t dark:border-gray-700 flex items-center justify-between">
            <div className="text-sm text-gray-500 dark:text-gray-400">
              显示 {(currentPage - 1) * pageSize + 1} 到{' '}
              {Math.min(currentPage * pageSize, filteredTags.length)} 条，共{' '}
              {filteredTags.length} 条
            </div>
            <div className="flex items-center gap-2">
              <Button
                variant="outline"
                size="sm"
                onClick={() => setCurrentPage((p) => Math.max(1, p - 1))}
                disabled={currentPage === 1}
                className="dark:border-gray-600 dark:hover:bg-gray-700"
              >
                上一页
              </Button>
              {Array.from({ length: totalPages }, (_, i) => i + 1).map((page) => (
                <Button
                  key={page}
                  variant={currentPage === page ? 'default' : 'outline'}
                  size="sm"
                  onClick={() => setCurrentPage(page)}
                  className={
                    currentPage === page
                      ? 'bg-blue-600 hover:bg-blue-700'
                      : 'dark:border-gray-600 dark:hover:bg-gray-700'
                  }
                >
                  {page}
                </Button>
              ))}
              <Button
                variant="outline"
                size="sm"
                onClick={() => setCurrentPage((p) => Math.min(totalPages, p + 1))}
                disabled={currentPage === totalPages}
                className="dark:border-gray-600 dark:hover:bg-gray-700"
              >
                下一页
              </Button>
            </div>
          </div>
        )}
      </Card>

      {/* 新增标签对话框 */}
      <Dialog open={showAddDialog} onOpenChange={setShowAddDialog}>
        <DialogContent className="dark:bg-gray-800 dark:border-gray-700" aria-describedby={undefined}>
          <DialogHeader>
            <DialogTitle className="dark:text-white">新增标签</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <div>
              <Label htmlFor="add-name" className="dark:text-gray-300">
                标签名称 <span className="text-red-500">*</span>
              </Label>
              <Input
                id="add-name"
                placeholder="请输入标签名称（如：CLOUD_SERVICE）"
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                className="mt-2 dark:bg-gray-900 dark:border-gray-700"
              />
              <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">
                建议使用大写字母和下划线组合
              </p>
            </div>
            <div>
              <Label htmlFor="add-description" className="dark:text-gray-300">
                标签描述 <span className="text-red-500">*</span>
              </Label>
              <Textarea
                id="add-description"
                placeholder="请输入标签描述"
                value={formData.description}
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                className="mt-2 dark:bg-gray-900 dark:border-gray-700"
                rows={3}
              />
            </div>
          </div>
          <DialogFooter>
            <Button
              variant="outline"
              onClick={() => setShowAddDialog(false)}
              className="dark:border-gray-600 dark:hover:bg-gray-700"
            >
              取消
            </Button>
            <Button onClick={handleConfirmAdd} className="bg-blue-600 hover:bg-blue-700">
              确认
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* 编辑标签对话框 */}
      <Dialog open={showEditDialog} onOpenChange={setShowEditDialog}>
        <DialogContent className="dark:bg-gray-800 dark:border-gray-700" aria-describedby={undefined}>
          <DialogHeader>
            <DialogTitle className="dark:text-white">编辑标签</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <div>
              <Label htmlFor="edit-name" className="dark:text-gray-300">
                标签名称 <span className="text-red-500">*</span>
              </Label>
              <Input
                id="edit-name"
                placeholder="请输入标签名称"
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                className="mt-2 dark:bg-gray-900 dark:border-gray-700"
              />
              <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">
                建议使用大写字母和下划线组合
              </p>
            </div>
            <div>
              <Label htmlFor="edit-description" className="dark:text-gray-300">
                标签描述 <span className="text-red-500">*</span>
              </Label>
              <Textarea
                id="edit-description"
                placeholder="请输入标签描述"
                value={formData.description}
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                className="mt-2 dark:bg-gray-900 dark:border-gray-700"
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
            <Button onClick={handleConfirmEdit} className="bg-blue-600 hover:bg-blue-700">
              确认
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* 删除确认对话框 */}
      <Dialog open={showDeleteDialog} onOpenChange={setShowDeleteDialog}>
        <DialogContent className="dark:bg-gray-800 dark:border-gray-700" aria-describedby={undefined}>
          <DialogHeader>
            <DialogTitle className="dark:text-white">删除标签</DialogTitle>
          </DialogHeader>
          <div className="py-4">
            <p className="text-sm text-gray-600 dark:text-gray-300">
              确定要删除标签 <span className="font-mono text-blue-600 dark:text-blue-400">{currentTag?.name}</span> 吗？
            </p>
            <p className="text-sm text-gray-500 dark:text-gray-400 mt-2">
              此操作不可恢复，删除后可能影响使用此标签的应用服务。
            </p>
          </div>
          <DialogFooter>
            <Button
              variant="outline"
              onClick={() => setShowDeleteDialog(false)}
              className="dark:border-gray-600 dark:hover:bg-gray-700"
            >
              取消
            </Button>
            <Button
              onClick={handleConfirmDelete}
              className="bg-red-600 hover:bg-red-700 text-white"
            >
              确认删除
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
