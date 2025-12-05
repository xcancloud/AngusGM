import { useState } from 'react';
import { X, Building2, Hash, User, Users, MapPin, Calendar } from 'lucide-react';
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
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import { toast } from 'sonner';

interface Department {
  id: string;
  name: string;
  code: string;
  parentId: string | null;
  managerName: string;
  userCount: number;
  level: number;
  status: '已启用' | '已禁用';
  createdAt: string;
}

interface DepartmentDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  mode: 'add' | 'edit' | 'addChild';
  department?: Department;
  parentDepartment?: Department;
  allDepartments: Department[];
  onSubmit: (data: {
    name: string;
    code: string;
    managerName: string;
    parentId: string | null;
    status: '已启用' | '已禁用';
    description: string;
  }) => void;
}

export function DepartmentDialog({ 
  open, 
  onOpenChange, 
  mode, 
  department,
  parentDepartment,
  allDepartments,
  onSubmit 
}: DepartmentDialogProps) {
  const [formData, setFormData] = useState({
    name: department?.name || '',
    code: department?.code || '',
    managerName: department?.managerName || '',
    parentId: mode === 'addChild' ? parentDepartment?.id : department?.parentId || '',
    status: department?.status || '已启用',
    description: '',
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!formData.name.trim()) {
      toast.error('请输入部门名称');
      return;
    }
    if (!formData.code.trim()) {
      toast.error('请输入部门编码');
      return;
    }
    if (!formData.managerName.trim()) {
      toast.error('请输入负责人');
      return;
    }

    // 检查层级限制
    if (mode === 'addChild' && parentDepartment && parentDepartment.level >= 6) {
      toast.error('已达到最大层级限制（6级），无法继续添加子部门');
      return;
    }

    // 调用父组件的提交函数
    onSubmit({
      name: formData.name.trim(),
      code: formData.code.trim(),
      managerName: formData.managerName.trim(),
      parentId: formData.parentId === 'none' || !formData.parentId ? null : formData.parentId,
      status: formData.status as '已启用' | '已禁用',
      description: formData.description,
    });

    const actionText = mode === 'add' || mode === 'addChild' ? '创建' : '更新';
    toast.success(`${actionText}部门成功`);
    onOpenChange(false);
  };

  const getTitle = () => {
    if (mode === 'add') return '添加部门';
    if (mode === 'addChild') return `添加子部门 - ${parentDepartment?.name}`;
    return '编辑部门';
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-2xl dark:bg-gray-800 dark:border-gray-700">
        <DialogHeader>
          <DialogTitle className="dark:text-white">{getTitle()}</DialogTitle>
          <DialogDescription className="dark:text-gray-400">
            {mode === 'add' ? '创建新的部门' : mode === 'addChild' ? '为当前部门添加子部门' : '编辑部门信息'}
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="space-y-6">
          {/* 基本信息 */}
          <div className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label className="dark:text-gray-300">
                  部门名称 <span className="text-red-500">*</span>
                </Label>
                <Input
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  placeholder="请输入部门名称"
                  className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
                />
              </div>

              <div className="space-y-2">
                <Label className="dark:text-gray-300">
                  部门编码 <span className="text-red-500">*</span>
                </Label>
                <Input
                  value={formData.code}
                  onChange={(e) => setFormData({ ...formData, code: e.target.value })}
                  placeholder="请输入部门编码"
                  className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
                />
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label className="dark:text-gray-300">
                  负责人 <span className="text-red-500">*</span>
                </Label>
                <Input
                  value={formData.managerName}
                  onChange={(e) => setFormData({ ...formData, managerName: e.target.value })}
                  placeholder="请输入负责人姓名"
                  className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
                />
              </div>

              <div className="space-y-2">
                <Label className="dark:text-gray-300">上级部门</Label>
                <Select 
                  value={formData.parentId} 
                  onValueChange={(value) => setFormData({ ...formData, parentId: value })}
                  disabled={mode === 'addChild'}
                >
                  <SelectTrigger className="dark:bg-gray-900 dark:border-gray-700 dark:text-white">
                    <SelectValue placeholder="选择上级部门" />
                  </SelectTrigger>
                  <SelectContent className="dark:bg-gray-900 dark:border-gray-700">
                    <SelectItem value="none">无（顶级部门）</SelectItem>
                    {allDepartments
                      .filter(dept => dept.id !== department?.id)
                      .map(dept => (
                        <SelectItem key={dept.id} value={dept.id}>
                          {dept.name}
                        </SelectItem>
                      ))}
                  </SelectContent>
                </Select>
              </div>
            </div>

            <div className="space-y-2">
              <Label className="dark:text-gray-300">部门描述</Label>
              <Textarea
                value={formData.description}
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                placeholder="请输入部门描述（可选）"
                rows={3}
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
                  <SelectItem value="已启用">已启用</SelectItem>
                  <SelectItem value="已禁用">已禁用</SelectItem>
                </SelectContent>
              </Select>
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
              {mode === 'edit' ? '保存' : '创建'}
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
}

interface AddMemberDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  departmentName: string;
}

export function AddMemberDialog({ open, onOpenChange, departmentName }: AddMemberDialogProps) {
  const [selectedUsers, setSelectedUsers] = useState<string[]>([]);
  const [searchQuery, setSearchQuery] = useState('');

  const availableUsers = [
    { id: 'U001', name: '张三', email: 'zhangsan@company.com', department: '技术部' },
    { id: 'U002', name: '李四', email: 'lisi@company.com', department: '市场部' },
    { id: 'U003', name: '王五', email: 'wangwu@company.com', department: '产品部' },
    { id: 'U004', name: '赵六', email: 'zhaoliu@company.com', department: '人力资源部' },
    { id: 'U005', name: '孙七', email: 'sunqi@company.com', department: '财务部' },
  ];

  const filteredUsers = availableUsers.filter(user =>
    user.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
    user.email.toLowerCase().includes(searchQuery.toLowerCase())
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
    toast.success(`成功添加 ${selectedUsers.length} 名成员到 ${departmentName}`);
    onOpenChange(false);
    setSelectedUsers([]);
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-2xl dark:bg-gray-800 dark:border-gray-700">
        <DialogHeader>
          <DialogTitle className="dark:text-white">添加成员 - {departmentName}</DialogTitle>
          <DialogDescription className="dark:text-gray-400">
            选择要添加到该部门的成员
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-4">
          {/* 搜索框 */}
          <div>
            <Input
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="搜索用户名称或邮箱..."
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
                <Badge variant="outline" className="dark:border-gray-600 dark:text-gray-400">
                  {user.department}
                </Badge>
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
              确定添加
            </Button>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  );
}
