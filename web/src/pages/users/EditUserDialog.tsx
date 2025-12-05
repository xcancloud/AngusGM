import { useState, useRef } from 'react';
import { Upload, User } from 'lucide-react';
import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Label } from '@/components/ui/label';
import { Input } from '@/components/ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Textarea } from '@/components/ui/textarea';
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';

interface EditUserDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  user?: {
    name: string;
    username: string;
    phone: string;
    email: string;
    position: string;
    gender: string;
    landline?: string;
    address?: string;
    department?: string;
    defaultRole?: string;
    avatar?: string;
  };
  onSave: (userData: any) => void;
  mode?: 'create' | 'edit';
}

export function EditUserDialog({ open, onOpenChange, user, onSave, mode = 'edit' }: EditUserDialogProps) {
  const [formData, setFormData] = useState({
    name: user?.name || '',
    username: user?.username || '',
    phone: user?.phone || '',
    email: user?.email || '',
    position: user?.position || '',
    gender: user?.gender || '男',
    password: '',
    confirmPassword: '',
    landline: user?.landline || '',
    address: user?.address || '',
    department: user?.department || '',
    defaultRole: user?.defaultRole || '一般用户',
    avatar: user?.avatar || '',
  });

  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleAvatarClick = () => {
    fileInputRef.current?.click();
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setFormData({ ...formData, avatar: reader.result as string });
      };
      reader.readAsDataURL(file);
    }
  };

  const handleSave = () => {
    onSave(formData);
    onOpenChange(false);
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="dark:bg-gray-800 dark:border-gray-700 sm:max-w-[600px] max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle className="dark:text-white">
            {mode === 'create' ? '创建用户' : '编辑用户'}
          </DialogTitle>
          <DialogDescription className="dark:text-gray-400">
            {mode === 'create' ? '填写用户的基本信息' : '修改用户的基本信息'}
          </DialogDescription>
        </DialogHeader>
        <div className="space-y-6 py-4">
          {/* Avatar Upload */}
          <div className="flex justify-center">
            <div className="relative">
              <Avatar className="w-24 h-24 cursor-pointer" onClick={handleAvatarClick}>
                <AvatarImage src={formData.avatar} alt="用户头像" />
                <AvatarFallback className="bg-blue-500 text-white text-2xl">
                  {formData.name ? formData.name.slice(0, 2) : <User className="w-12 h-12" />}
                </AvatarFallback>
              </Avatar>
              <button
                type="button"
                onClick={handleAvatarClick}
                className="absolute bottom-0 right-0 w-8 h-8 rounded-full bg-blue-600 hover:bg-blue-700 text-white flex items-center justify-center"
              >
                <Upload className="w-4 h-4" />
              </button>
              <input
                ref={fileInputRef}
                type="file"
                accept="image/*"
                onChange={handleFileChange}
                className="hidden"
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="name" className="dark:text-gray-300">
                姓名 <span className="text-red-500">*</span>
              </Label>
              <Input
                id="name"
                placeholder="请输入姓名"
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
              />
            </div>
            
            <div className="space-y-2">
              <Label className="dark:text-gray-300">性别</Label>
              <RadioGroup 
                value={formData.gender} 
                onValueChange={(value) => setFormData({ ...formData, gender: value })}
                className="flex gap-4 mt-2"
              >
                <div className="flex items-center space-x-2">
                  <RadioGroupItem value="女" id="female" />
                  <Label htmlFor="female" className="dark:text-gray-300 cursor-pointer">女</Label>
                </div>
                <div className="flex items-center space-x-2">
                  <RadioGroupItem value="男" id="male" />
                  <Label htmlFor="male" className="dark:text-gray-300 cursor-pointer">男</Label>
                </div>
              </RadioGroup>
            </div>

            <div className="space-y-2">
              <Label htmlFor="username" className="dark:text-gray-300">
                账号 <span className="text-red-500">*</span>
              </Label>
              <Input
                id="username"
                placeholder="请输入账号"
                value={formData.username}
                onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
                disabled={mode === 'edit'}
              />
            </div>
            
            <div className="space-y-2">
              <Label htmlFor="email" className="dark:text-gray-300">邮箱</Label>
              <Input
                id="email"
                type="email"
                placeholder="请输入邮箱"
                value={formData.email}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
              />
            </div>

            {mode === 'create' && (
              <>
                <div className="space-y-2">
                  <Label htmlFor="password" className="dark:text-gray-300">
                    密码 <span className="text-red-500">*</span>
                  </Label>
                  <Input
                    id="password"
                    type="password"
                    placeholder="请输入密码"
                    value={formData.password}
                    onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                    className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
                  />
                </div>
                
                <div className="space-y-2">
                  <Label htmlFor="confirmPassword" className="dark:text-gray-300">
                    确认密码 <span className="text-red-500">*</span>
                  </Label>
                  <Input
                    id="confirmPassword"
                    type="password"
                    placeholder="请再次输入密码"
                    value={formData.confirmPassword}
                    onChange={(e) => setFormData({ ...formData, confirmPassword: e.target.value })}
                    className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
                  />
                </div>
              </>
            )}

            <div className="space-y-2">
              <Label htmlFor="phone" className="dark:text-gray-300">手机号</Label>
              <Input
                id="phone"
                placeholder="请输入手机号"
                value={formData.phone}
                onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="landline" className="dark:text-gray-300">座机号</Label>
              <Input
                id="landline"
                placeholder="请输入座机号"
                value={formData.landline}
                onChange={(e) => setFormData({ ...formData, landline: e.target.value })}
                className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="position" className="dark:text-gray-300">职务</Label>
              <Input
                id="position"
                placeholder="请输入职务"
                value={formData.position}
                onChange={(e) => setFormData({ ...formData, position: e.target.value })}
                className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="department" className="dark:text-gray-300">部门</Label>
              <Select value={formData.department} onValueChange={(value) => setFormData({ ...formData, department: value })}>
                <SelectTrigger className="dark:bg-gray-900 dark:border-gray-700 dark:text-white">
                  <SelectValue placeholder="请选择部门" />
                </SelectTrigger>
                <SelectContent className="dark:bg-gray-800 dark:border-gray-700">
                  <SelectItem value="技术部" className="dark:text-white dark:focus:bg-gray-700">技术部</SelectItem>
                  <SelectItem value="产品部" className="dark:text-white dark:focus:bg-gray-700">产品部</SelectItem>
                  <SelectItem value="设计部" className="dark:text-white dark:focus:bg-gray-700">设计部</SelectItem>
                  <SelectItem value="市场部" className="dark:text-white dark:focus:bg-gray-700">市场部</SelectItem>
                  <SelectItem value="运营部" className="dark:text-white dark:focus:bg-gray-700">运营部</SelectItem>
                  <SelectItem value="财务部" className="dark:text-white dark:focus:bg-gray-700">财务部</SelectItem>
                  <SelectItem value="人事部" className="dark:text-white dark:focus:bg-gray-700">人事部</SelectItem>
                </SelectContent>
              </Select>
            </div>

            <div className="col-span-2 space-y-2">
              <Label htmlFor="address" className="dark:text-gray-300">通讯地址</Label>
              <Textarea
                id="address"
                placeholder="请输入通讯地址"
                value={formData.address}
                onChange={(e) => setFormData({ ...formData, address: e.target.value })}
                className="dark:bg-gray-900 dark:border-gray-700 dark:text-white resize-none"
                rows={2}
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="defaultRole" className="dark:text-gray-300">默认角色</Label>
              <Select value={formData.defaultRole} onValueChange={(value) => setFormData({ ...formData, defaultRole: value })}>
                <SelectTrigger className="dark:bg-gray-900 dark:border-gray-700 dark:text-white">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent className="dark:bg-gray-800 dark:border-gray-700">
                  <SelectItem value="一般用户" className="dark:text-white dark:focus:bg-gray-700">一般用户</SelectItem>
                  <SelectItem value="管理员" className="dark:text-white dark:focus:bg-gray-700">管理员</SelectItem>
                  <SelectItem value="审核员" className="dark:text-white dark:focus:bg-gray-700">审核员</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>
        </div>
        <DialogFooter>
          <Button
            variant="outline"
            onClick={() => onOpenChange(false)}
            className="dark:border-gray-600 dark:text-gray-300 dark:hover:bg-gray-700"
          >
            取消
          </Button>
          <Button onClick={handleSave} className="bg-blue-600 hover:bg-blue-700 text-white">
            {mode === 'create' ? '创建' : '保存'}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}