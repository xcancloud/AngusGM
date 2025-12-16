import { useState } from 'react';
import { Tag as TagIcon, Search, Plus, Edit2, Trash2, Shield, User } from 'lucide-react';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter, DialogDescription  } from '@/components/ui/dialog';
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
  isSystem: boolean;
}

export function TagManagement() {
  // 标签数据
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
  
  // 标签相关状态
  const [showAddDialog, setShowAddDialog] = useState(false);
  const [showEditDialog, setShowEditDialog] = useState(false);
  const [showDeleteDialog, setShowDeleteDialog] = useState(false);
  const [currentTag, setCurrentTag] = useState<TagItem | null>(null);
  const [tagFormData, setTagFormData] = useState({
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

  // 标签分页
  const totalPages = Math.ceil(filteredTags.length / pageSize);
  const paginatedTags = filteredTags.slice(
    (currentPage - 1) * pageSize,
    currentPage * pageSize
  );

  // ========== 标签操作 ==========
  const handleAddTag = () => {
    setTagFormData({ name: '', description: '' });
    setShowAddDialog(true);
  };

  const handleEditTag = (tag: TagItem) => {
    if (tag.isSystem) {
      toast.error('系统定义标签不允许编辑');
      return;
    }
    setCurrentTag(tag);
    setTagFormData({
      name: tag.name,
      description: tag.description,
    });
    setShowEditDialog(true);
  };

  const handleDeleteTag = (tag: TagItem) => {
    if (tag.isSystem) {
      toast.error('系统定义标签不允许删除');
      return;
    }
    setCurrentTag(tag);
    setShowDeleteDialog(true);
  };

  const handleConfirmAddTag = () => {
    if (!tagFormData.name.trim()) {
      toast.error('请输入标签名称');
      return;
    }
    if (!tagFormData.description.trim()) {
      toast.error('请输入标签描述');
      return;
    }

    if (tags.some((tag) => tag.name === tagFormData.name)) {
      toast.error('标签名称已存在');
      return;
    }

    const newTag: TagItem = {
      id: String(Date.now()),
      name: tagFormData.name.trim(),
      description: tagFormData.description.trim(),
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

  const handleConfirmEditTag = () => {
    if (!currentTag) return;

    if (!tagFormData.name.trim()) {
      toast.error('请输入标签名称');
      return;
    }
    if (!tagFormData.description.trim()) {
      toast.error('请输入标签描述');
      return;
    }

    if (tags.some((tag) => tag.id !== currentTag.id && tag.name === tagFormData.name)) {
      toast.error('标签名称已存在');
      return;
    }

    setTags(
      tags.map((tag) =>
        tag.id === currentTag.id
          ? {
              ...tag,
              name: tagFormData.name.trim(),
              description: tagFormData.description.trim(),
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

  const handleConfirmDeleteTag = () => {
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
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
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
            <div className="w-12 h-12 bg-green-100 dark:bg-green-900/30 rounded-lg flex items-center justify-center">
              <Shield className="w-6 h-6 text-green-600 dark:text-green-400" />
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
            <div className="w-12 h-12 bg-orange-100 dark:bg-orange-900/30 rounded-lg flex items-center justify-center">
              <User className="w-6 h-6 text-orange-600 dark:text-orange-400" />
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
          <Button onClick={handleAddTag} className="bg-blue-600 hover:bg-blue-700">
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
                      <div className="flex items-center gap-2">
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => handleEditTag(tag)}
                          disabled={tag.isSystem}
                          className={tag.isSystem ? 'opacity-50 cursor-not-allowed' : 'dark:hover:bg-gray-600'}
                        >
                          <Edit2 className="w-4 h-4" />
                        </Button>
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => handleDeleteTag(tag)}
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
                  <td colSpan={6} className="p-12 text-center">
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
                      ? 'bg-blue-600 hover:bg-blue-700 text-white'
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
        <DialogContent className="dark:bg-gray-800 dark:border-gray-700">
          <DialogHeader>
            <DialogTitle className="dark:text-white">新增标签</DialogTitle>
            <DialogDescription className="dark:text-gray-400">
              创建一个新的功能标签
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-4 py-4">
            <div className="space-y-2">
              <Label className="dark:text-gray-200">
                标签名称 <span className="text-red-500">*</span>
              </Label>
              <Input
                placeholder="例如: FEATURE_NAME"
                value={tagFormData.name}
                onChange={(e) => setTagFormData({ ...tagFormData, name: e.target.value })}
                className="dark:bg-gray-900 dark:border-gray-700"
              />
            </div>
            <div className="space-y-2">
              <Label className="dark:text-gray-200">
                标签描述 <span className="text-red-500">*</span>
              </Label>
              <Textarea
                placeholder="请输入标签描述"
                value={tagFormData.description}
                onChange={(e) => setTagFormData({ ...tagFormData, description: e.target.value })}
                className="dark:bg-gray-900 dark:border-gray-700"
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
            <Button onClick={handleConfirmAddTag} className="bg-blue-600 hover:bg-blue-700">
              确认
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* 编辑标签对话框 */}
      <Dialog open={showEditDialog} onOpenChange={setShowEditDialog}>
        <DialogContent className="dark:bg-gray-800 dark:border-gray-700">
          <DialogHeader>
            <DialogTitle className="dark:text-white">编辑标签</DialogTitle>
            <DialogDescription className="dark:text-gray-400">
              修改标签信息
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-4 py-4">
            <div className="space-y-2">
              <Label className="dark:text-gray-200">
                标签名称 <span className="text-red-500">*</span>
              </Label>
              <Input
                placeholder="例如: FEATURE_NAME"
                value={tagFormData.name}
                onChange={(e) => setTagFormData({ ...tagFormData, name: e.target.value })}
                className="dark:bg-gray-900 dark:border-gray-700"
              />
            </div>
            <div className="space-y-2">
              <Label className="dark:text-gray-200">
                标签描述 <span className="text-red-500">*</span>
              </Label>
              <Textarea
                placeholder="请输入标签描述"
                value={tagFormData.description}
                onChange={(e) => setTagFormData({ ...tagFormData, description: e.target.value })}
                className="dark:bg-gray-900 dark:border-gray-700"
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
            <Button onClick={handleConfirmEditTag} className="bg-blue-600 hover:bg-blue-700">
              保存
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* 删除标签确认对话框 */}
      <Dialog open={showDeleteDialog} onOpenChange={setShowDeleteDialog}>
        <DialogContent className="dark:bg-gray-800 dark:border-gray-700">
          <DialogHeader>
            <DialogTitle className="dark:text-white">确认删除</DialogTitle>
            <DialogDescription className="dark:text-gray-400">
              确定要删除标签 &quot;{currentTag?.name}&quot; 吗？此操作无法撤销。
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button
              variant="outline"
              onClick={() => setShowDeleteDialog(false)}
              className="dark:border-gray-600 dark:hover:bg-gray-700"
            >
              取消
            </Button>
            <Button
              onClick={handleConfirmDeleteTag}
              className="bg-red-600 hover:bg-red-700 text-white"
            >
              删除
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
