import { useState } from 'react';
import { X, Users, Tag, User, FileText, Calendar, Shield } from 'lucide-react';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription } from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
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

interface GroupDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  mode: 'create' | 'edit';
  group?: Group;
}

export function GroupDialog({ open, onOpenChange, mode, group }: GroupDialogProps) {
  const [formData, setFormData] = useState({
    name: group?.name || '',
    description: group?.description || '',
    type: group?.type || '项目组',
    ownerName: group?.ownerName || '',
    status: group?.status || '活跃',
  });

  const [tags, setTags] = useState<string[]>(group?.tags || []);
  const [tagInput, setTagInput] = useState('');

  const handleAddTag = () => {
    if (tags.length >= 5) {
      toast.error('最多只能添加5个标签');
      return;
    }
    if (tagInput.trim() && !tags.includes(tagInput.trim())) {
      setTags([...tags, tagInput.trim()]);
      setTagInput('');
    }
  };

  const handleRemoveTag = (tag: string) => {
    setTags(tags.filter(t => t !== tag));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!formData.name.trim()) {
      toast.error('请输入组名称');
      return;
    }
    if (!formData.description.trim()) {
      toast.error('请输入组描述');
      return;
    }
    if (!formData.ownerName.trim()) {
      toast.error('请选择负责人');
      return;
    }

    const actionText = mode === 'create' ? '创建' : '更新';
    toast.success(`${actionText}组成功`);
    onOpenChange(false);
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-2xl dark:bg-gray-800 dark:border-gray-700">
        <DialogHeader>
          <DialogTitle className="dark:text-white">
            {mode === 'create' ? '创建组' : '编辑组'}
          </DialogTitle>
          <DialogDescription className="dark:text-gray-400">
            {mode === 'create' ? '创建一个新的工作组' : '编辑组信息'}
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="space-y-6">
          {/* 基本信息 */}
          <div className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label className="dark:text-gray-300">
                  组名称 <span className="text-red-500">*</span>
                </Label>
                <Input
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  placeholder="请输入组名称"
                  className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
                />
              </div>

              <div className="space-y-2">
                <Label className="dark:text-gray-300">
                  组类型 <span className="text-red-500">*</span>
                </Label>
                <Select 
                  value={formData.type} 
                  onValueChange={(value: any) => setFormData({ ...formData, type: value })}
                >
                  <SelectTrigger className="dark:bg-gray-900 dark:border-gray-700 dark:text-white">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent className="dark:bg-gray-900 dark:border-gray-700">
                    <SelectItem value="项目组">项目组</SelectItem>
                    <SelectItem value="职能组">职能组</SelectItem>
                    <SelectItem value="临时组">临时组</SelectItem>
                  </SelectContent>
                </Select>
              </div>
            </div>

            <div className="space-y-2">
              <Label className="dark:text-gray-300">
                组描述 <span className="text-red-500">*</span>
              </Label>
              <Textarea
                value={formData.description}
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                placeholder="请输入组描述"
                rows={3}
                className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
              />
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label className="dark:text-gray-300">
                  负责人 <span className="text-red-500">*</span>
                </Label>
                <Input
                  value={formData.ownerName}
                  onChange={(e) => setFormData({ ...formData, ownerName: e.target.value })}
                  placeholder="请输入负责人姓名"
                  className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
                />
              </div>

              <div className="space-y-2">
                <Label className="dark:text-gray-300">状态</Label>
                <Select 
                  value={formData.status} 
                  onValueChange={(value: any) => setFormData({ ...formData, status: value })}
                >
                  <SelectTrigger className="dark:bg-gray-900 dark:border-gray-700 dark:text-white">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent className="dark:bg-gray-900 dark:border-gray-700">
                    <SelectItem value="活跃">活跃</SelectItem>
                    <SelectItem value="归档">归档</SelectItem>
                  </SelectContent>
                </Select>
              </div>
            </div>

            {/* 标签 */}
            <div className="space-y-2">
              <Label className="dark:text-gray-300">
                标签 <span className="text-sm text-gray-500 dark:text-gray-500">({tags.length}/5)</span>
              </Label>
              <div className="flex gap-2">
                <Input
                  value={tagInput}
                  onChange={(e) => setTagInput(e.target.value)}
                  onKeyDown={(e) => {
                    if (e.key === 'Enter') {
                      e.preventDefault();
                      handleAddTag();
                    }
                  }}
                  placeholder={tags.length >= 5 ? "已达到标签数量上限" : "输入标签后按回车添加"}
                  className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
                  disabled={tags.length >= 5}
                />
                <Button
                  type="button"
                  variant="outline"
                  onClick={handleAddTag}
                  className="dark:bg-gray-900 dark:border-gray-700 dark:text-gray-300"
                  disabled={tags.length >= 5}
                >
                  添加
                </Button>
              </div>
              {tags.length > 0 && (
                <div className="flex flex-wrap gap-2 mt-2">
                  {tags.map((tag, index) => (
                    <Badge key={index} variant="outline" className="dark:border-gray-600 dark:text-gray-400">
                      {tag}
                      <button
                        type="button"
                        onClick={() => handleRemoveTag(tag)}
                        className="ml-1 hover:bg-gray-200 dark:hover:bg-gray-700 rounded-full"
                      >
                        <X className="w-3 h-3" />
                      </button>
                    </Badge>
                  ))}
                </div>
              )}
            </div>
          </div>

          {/* 操作按钮 */}
          <div className="flex justify-end gap-3 pt-4 border-t dark:border-gray-700">
            <Button
              type="button"
              variant="outline"
              onClick={() => onOpenChange(false)}
              className="dark:bg-gray-900 dark:border-gray-700 dark:text-gray-300"
            >
              取消
            </Button>
            <Button
              type="submit"
              className="bg-blue-500 hover:bg-blue-600 text-white"
            >
              {mode === 'create' ? '创建' : '保存'}
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
}

interface AddGroupMemberDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  groupName: string;
}

export function AddGroupMemberDialog({ open, onOpenChange, groupName }: AddGroupMemberDialogProps) {
  const [selectedUsers, setSelectedUsers] = useState<string[]>([]);
  const [searchQuery, setSearchQuery] = useState('');

  const availableUsers = [
    { id: 'U001', name: '张三', email: 'zhangsan@company.com', department: '技术部', role: '开发工程师' },
    { id: 'U002', name: '李四', email: 'lisi@company.com', department: '市场部', role: '市场专员' },
    { id: 'U003', name: '王五', email: 'wangwu@company.com', department: '产品部', role: '产品经理' },
    { id: 'U004', name: '赵六', email: 'zhaoliu@company.com', department: '人力资源部', role: 'HR专员' },
    { id: 'U005', name: '孙七', email: 'sunqi@company.com', department: '财务部', role: '会计' },
    { id: 'U006', name: '周八', email: 'zhouba@company.com', department: '技术部', role: '架构师' },
    { id: 'U007', name: '吴九', email: 'wujiu@company.com', department: '技术部', role: '前端工程师' },
    { id: 'U008', name: '郑十', email: 'zhengshi@company.com', department: '产品部', role: 'UI设计师' },
  ];

  const filteredUsers = availableUsers.filter(user =>
    user.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
    user.email.toLowerCase().includes(searchQuery.toLowerCase()) ||
    user.department.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const toggleUser = (userId: string) => {
    setSelectedUsers(prev =>
      prev.includes(userId)
        ? prev.filter(id => id !== userId)
        : [...prev, userId]
    );
  };

  const handleSubmit = () => {
    if (selectedUsers.length === 0) {
      toast.error('请至少选择一个成员');
      return;
    }
    toast.success(`成功添加 ${selectedUsers.length} 名成员到 ${groupName}`);
    onOpenChange(false);
    setSelectedUsers([]);
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-3xl dark:bg-gray-800 dark:border-gray-700">
        <DialogHeader>
          <DialogTitle className="dark:text-white">添加成员 - {groupName}</DialogTitle>
          <DialogDescription className="dark:text-gray-400">
            选择要添加到该组的成员
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-4">
          {/* 搜索框 */}
          <div>
            <Input
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="搜索用户名称、邮箱或部门..."
              className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
            />
          </div>

          {/* 已选择 */}
          {selectedUsers.length > 0 && (
            <div className="p-3 bg-blue-50 dark:bg-blue-900/20 rounded-lg">
              <div className="text-sm text-blue-700 dark:text-blue-400 mb-2">
                已选择 {selectedUsers.length} 人
              </div>
              <div className="flex flex-wrap gap-2">
                {selectedUsers.map(userId => {
                  const user = availableUsers.find(u => u.id === userId);
                  return (
                    <Badge key={userId} className="bg-blue-500 text-white">
                      {user?.name}
                      <button
                        onClick={() => toggleUser(userId)}
                        className="ml-1 hover:bg-blue-600 rounded-full"
                      >
                        <X className="w-3 h-3" />
                      </button>
                    </Badge>
                  );
                })}
              </div>
            </div>
          )}

          {/* 用户列表 */}
          <div className="border dark:border-gray-700 rounded-lg max-h-96 overflow-y-auto">
            {filteredUsers.map(user => (
              <div
                key={user.id}
                className={`flex items-center gap-3 p-3 border-b dark:border-gray-700 last:border-b-0 cursor-pointer hover:bg-gray-50 dark:hover:bg-gray-700 ${
                  selectedUsers.includes(user.id) ? 'bg-blue-50 dark:bg-blue-900/20' : ''
                }`}
                onClick={() => toggleUser(user.id)}
              >
                <input
                  type="checkbox"
                  checked={selectedUsers.includes(user.id)}
                  onChange={() => toggleUser(user.id)}
                  className="w-4 h-4"
                />
                <Avatar className="w-10 h-10">
                  <AvatarFallback className="bg-gradient-to-br from-blue-500 to-blue-600 text-white">
                    {user.name.slice(0, 2)}
                  </AvatarFallback>
                </Avatar>
                <div className="flex-1">
                  <div className="dark:text-white">{user.name}</div>
                  <div className="text-sm text-gray-500 dark:text-gray-400">{user.email}</div>
                </div>
                <div className="text-right">
                  <Badge variant="outline" className="dark:border-gray-600 dark:text-gray-400 mb-1">
                    {user.department}
                  </Badge>
                  <div className="text-xs text-gray-500 dark:text-gray-400">{user.role}</div>
                </div>
              </div>
            ))}
          </div>

          {/* 操作按钮 */}
          <div className="flex justify-end gap-3 pt-4 border-t dark:border-gray-700">
            <Button
              type="button"
              variant="outline"
              onClick={() => onOpenChange(false)}
              className="dark:bg-gray-900 dark:border-gray-700 dark:text-gray-300"
            >
              取消
            </Button>
            <Button
              onClick={handleSubmit}
              className="bg-blue-500 hover:bg-blue-600 text-white"
            >
              确定添加 ({selectedUsers.length})
            </Button>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  );
}

interface GroupDetailDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  group: Group;
}

export function GroupDetailDialog({ open, onOpenChange, group }: GroupDetailDialogProps) {
  const members = [
    { id: 'U001', name: '张三', role: '项目负责人', avatar: '', joinDate: '2024-01-15' },
    { id: 'U002', name: '李四', role: '开发工程师', avatar: '', joinDate: '2024-01-20' },
    { id: 'U003', name: '王五', role: '前端工程师', avatar: '', joinDate: '2024-02-01' },
    { id: 'U004', name: '赵六', role: 'UI设计师', avatar: '', joinDate: '2024-02-10' },
    { id: 'U005', name: '孙七', role: '测试工程师', avatar: '', joinDate: '2024-03-01' },
  ];

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-4xl max-h-[90vh] overflow-y-auto dark:bg-gray-800 dark:border-gray-700">
        <DialogHeader>
          <DialogTitle className="dark:text-white">组详情</DialogTitle>
          <DialogDescription className="dark:text-gray-400">
            查看和管理组的详细信息和成员
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-6">
          {/* 基本信息 */}
          <div className="space-y-4">
            <div className="flex items-start justify-between">
              <div className="flex-1">
                <div className="flex items-center gap-2 mb-2">
                  <h3 className="text-xl dark:text-white">{group.name}</h3>
                  <Badge className={`text-xs ${
                    group.type === '项目组' ? 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400' :
                    group.type === '职能组' ? 'bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-400' :
                    'bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-400'
                  } border-0`}>
                    {group.type}
                  </Badge>
                  <Badge className={`text-xs ${
                    group.status === '活跃' ? 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400' :
                    'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-400'
                  } border-0`}>
                    {group.status}
                  </Badge>
                </div>
                <p className="text-gray-600 dark:text-gray-400">{group.description}</p>
              </div>
            </div>

            {/* 标签 */}
            {group.tags.length > 0 && (
              <div className="flex flex-wrap gap-2">
                {group.tags.map((tag, index) => (
                  <Badge key={index} variant="outline" className="dark:border-gray-600 dark:text-gray-400">
                    <Tag className="w-3 h-3 mr-1" />
                    {tag}
                  </Badge>
                ))}
              </div>
            )}

            {/* 信息卡片 */}
            <div className="grid grid-cols-3 gap-4">
              <div className="p-4 bg-gray-50 dark:bg-gray-900/50 rounded-lg">
                <div className="flex items-center gap-2 text-gray-600 dark:text-gray-400 mb-1">
                  <User className="w-4 h-4" />
                  <span className="text-sm">负责人</span>
                </div>
                <div className="dark:text-white">{group.ownerName}</div>
              </div>

              <div className="p-4 bg-gray-50 dark:bg-gray-900/50 rounded-lg">
                <div className="flex items-center gap-2 text-gray-600 dark:text-gray-400 mb-1">
                  <Users className="w-4 h-4" />
                  <span className="text-sm">成员数量</span>
                </div>
                <div className="dark:text-white">{group.memberCount} 人</div>
              </div>

              <div className="p-4 bg-gray-50 dark:bg-gray-900/50 rounded-lg">
                <div className="flex items-center gap-2 text-gray-600 dark:text-gray-400 mb-1">
                  <Calendar className="w-4 h-4" />
                  <span className="text-sm">创建时间</span>
                </div>
                <div className="dark:text-white">{group.createdAt}</div>
              </div>
            </div>
          </div>

          {/* 成员列表 */}
          <div>
            <div className="flex items-center justify-between mb-4">
              <h4 className="dark:text-white">成员列表</h4>
              <Button
                size="sm"
                className="bg-blue-500 hover:bg-blue-600 text-white"
              >
                <Users className="w-4 h-4 mr-2" />
                添加成员
              </Button>
            </div>

            <div className="border dark:border-gray-700 rounded-lg divide-y dark:divide-gray-700">
              {members.map(member => (
                <div key={member.id} className="flex items-center gap-3 p-4 hover:bg-gray-50 dark:hover:bg-gray-700">
                  <Avatar className="w-10 h-10">
                    <AvatarFallback className="bg-gradient-to-br from-blue-500 to-blue-600 text-white">
                      {member.name.slice(0, 2)}
                    </AvatarFallback>
                  </Avatar>
                  <div className="flex-1">
                    <div className="dark:text-white">{member.name}</div>
                    <div className="text-sm text-gray-500 dark:text-gray-400">{member.role}</div>
                  </div>
                  <div className="text-sm text-gray-500 dark:text-gray-400">
                    加入时间: {member.joinDate}
                  </div>
                  <Button
                    variant="ghost"
                    size="sm"
                    className="text-red-600 dark:text-red-400"
                  >
                    移除
                  </Button>
                </div>
              ))}
            </div>
          </div>

          {/* 操作按钮 */}
          <div className="flex justify-end gap-3 pt-4 border-t dark:border-gray-700">
            <Button
              variant="outline"
              onClick={() => onOpenChange(false)}
              className="dark:bg-gray-900 dark:border-gray-700 dark:text-gray-300"
            >
              关闭
            </Button>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  );
}
