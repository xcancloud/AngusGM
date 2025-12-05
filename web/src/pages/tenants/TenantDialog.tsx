import { useState, useEffect } from 'react';
import { X, Building2, Hash, User, Mail, Phone, MapPin, Calendar } from 'lucide-react';
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
import { toast } from 'sonner';

interface Tenant {
  id: string;
  name: string;
  code: string;
  type: '个人' | '企业' | '未知';
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

interface TenantDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  mode: 'create' | 'edit';
  tenant?: Tenant;
}

export function TenantDialog({ 
  open, 
  onOpenChange, 
  mode, 
  tenant 
}: TenantDialogProps) {
  const [formData, setFormData] = useState({
    name: '',
    code: '',
    type: '企业' as '个人' | '企业' | '未知',
    adminName: '',
    adminEmail: '',
    adminPhone: '',
    address: '',
    expireDate: '',
    status: '已启用' as '已启用' | '已禁用',
    description: '',
  });

  // 当 tenant 或 open 变化时，更新表单数据
  useEffect(() => {
    if (open) {
      if (mode === 'edit' && tenant) {
        setFormData({
          name: tenant.name,
          code: tenant.code,
          type: tenant.type,
          adminName: tenant.adminName,
          adminEmail: tenant.adminEmail,
          adminPhone: tenant.adminPhone,
          address: tenant.address,
          expireDate: tenant.expireDate,
          status: tenant.status,
          description: '',
        });
      } else {
        // 创建模式，重置表单
        setFormData({
          name: '',
          code: '',
          type: '企业',
          adminName: '',
          adminEmail: '',
          adminPhone: '',
          address: '',
          expireDate: '',
          status: '已启用',
          description: '',
        });
      }
    }
  }, [open, mode, tenant]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!formData.name.trim()) {
      toast.error('请输入租户名称');
      return;
    }
    if (!formData.code.trim()) {
      toast.error('请输入租户编码');
      return;
    }
    if (!formData.adminName.trim()) {
      toast.error('请输入管理员姓名');
      return;
    }
    if (!formData.adminEmail.trim()) {
      toast.error('请输入管理员邮箱');
      return;
    }
    if (!formData.expireDate) {
      toast.error('请选择到期时间');
      return;
    }

    const actionText = mode === 'create' ? '创建' : '更新';
    toast.success(`${actionText}租户成功`);
    onOpenChange(false);
  };

  const getTitle = () => {
    return mode === 'create' ? '创建租户' : '编辑租户';
  };

  const getDescription = () => {
    return mode === 'create' 
      ? '填写租户信息，创建新的租户账号'
      : '修改租户信息，更新租户账号';
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[872px] max-h-[90vh] overflow-y-auto dark:bg-gray-800 dark:border-gray-700">
        <DialogHeader>
          <DialogTitle className="dark:text-white">{getTitle()}</DialogTitle>
          <DialogDescription className="dark:text-gray-400">
            {getDescription()}
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="space-y-6">
          {/* 基本信息 */}
          <div className="space-y-4">
            <h3 className="text-sm dark:text-white flex items-center gap-2">
              <Building2 className="w-4 h-4" />
              基本信息
            </h3>
            
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="name" className="dark:text-gray-300">
                  租户名称 <span className="text-red-500">*</span>
                </Label>
                <Input
                  id="name"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  placeholder="请输入租户名称"
                  className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="code" className="dark:text-gray-300">
                  租户编码 <span className="text-red-500">*</span>
                </Label>
                <Input
                  id="code"
                  value={formData.code}
                  onChange={(e) => setFormData({ ...formData, code: e.target.value.toUpperCase() })}
                  placeholder="请输入租户编码"
                  className="dark:bg-gray-900 dark:border-gray-700 dark:text-white font-mono"
                />
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="type" className="dark:text-gray-300">
                  租户类型 <span className="text-red-500">*</span>
                </Label>
                <Select value={formData.type} onValueChange={(value) => setFormData({ ...formData, type: value as '个人' | '企业' | '未知' })}>
                  <SelectTrigger className="dark:bg-gray-900 dark:border-gray-700 dark:text-white">
                    <SelectValue placeholder="选择租户类型" />
                  </SelectTrigger>
                  <SelectContent className="dark:bg-gray-900 dark:border-gray-700">
                    <SelectItem value="企业">企业</SelectItem>
                    <SelectItem value="个人">个人</SelectItem>
                    <SelectItem value="未知">未知</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div className="space-y-2">
                <Label htmlFor="address" className="dark:text-gray-300">
                  地址
                </Label>
                <Input
                  id="address"
                  value={formData.address}
                  onChange={(e) => setFormData({ ...formData, address: e.target.value })}
                  placeholder="请输入租户地址"
                  className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
                />
              </div>
            </div>

            <div className="space-y-2">
              <Label htmlFor="description" className="dark:text-gray-300">
                描述
              </Label>
              <Textarea
                id="description"
                value={formData.description}
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                placeholder="请输入租户描述信息"
                className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
                rows={3}
              />
            </div>
          </div>

          {/* 管理员信息 */}
          <div className="space-y-4">
            <h3 className="text-sm dark:text-white flex items-center gap-2">
              <User className="w-4 h-4" />
              管理员信息
            </h3>

            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="adminName" className="dark:text-gray-300">
                  管理员姓名 <span className="text-red-500">*</span>
                </Label>
                <Input
                  id="adminName"
                  value={formData.adminName}
                  onChange={(e) => setFormData({ ...formData, adminName: e.target.value })}
                  placeholder="请输入管理员姓名"
                  className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="adminEmail" className="dark:text-gray-300">
                  管理员邮箱 <span className="text-red-500">*</span>
                </Label>
                <Input
                  id="adminEmail"
                  type="email"
                  value={formData.adminEmail}
                  onChange={(e) => setFormData({ ...formData, adminEmail: e.target.value })}
                  placeholder="请输入管理员邮箱"
                  className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
                />
              </div>
            </div>

            <div className="space-y-2">
              <Label htmlFor="adminPhone" className="dark:text-gray-300">
                管理员电话
              </Label>
              <Input
                id="adminPhone"
                value={formData.adminPhone}
                onChange={(e) => setFormData({ ...formData, adminPhone: e.target.value })}
                placeholder="请输入管理员电话"
                className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
              />
            </div>
          </div>

          {/* 配置信息 */}
          <div className="space-y-4">
            <h3 className="text-sm dark:text-white flex items-center gap-2">
              <Calendar className="w-4 h-4" />
              配置信息
            </h3>

            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="expireDate" className="dark:text-gray-300">
                  到期时间 <span className="text-red-500">*</span>
                </Label>
                <Input
                  id="expireDate"
                  type="date"
                  value={formData.expireDate}
                  onChange={(e) => setFormData({ ...formData, expireDate: e.target.value })}
                  className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="status" className="dark:text-gray-300">
                  状态
                </Label>
                <Select 
                  value={formData.status} 
                  onValueChange={(value: '已启用' | '已禁用') => setFormData({ ...formData, status: value })}
                >
                  <SelectTrigger className="dark:bg-gray-900 dark:border-gray-700 dark:text-white">
                    <SelectValue placeholder="选择状态" />
                  </SelectTrigger>
                  <SelectContent className="dark:bg-gray-900 dark:border-gray-700">
                    <SelectItem value="已启用">已启用</SelectItem>
                    <SelectItem value="已禁用">已禁用</SelectItem>
                  </SelectContent>
                </Select>
              </div>
            </div>
          </div>

          {/* 操作按钮 */}
          <div className="flex justify-end gap-3 pt-4 border-t dark:border-gray-700">
            <Button
              type="button"
              variant="outline"
              onClick={() => onOpenChange(false)}
              className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
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

// 租户详情对话框
interface TenantDetailDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  tenant: Tenant;
}

export function TenantDetailDialog({ open, onOpenChange, tenant }: TenantDetailDialogProps) {
  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[872px] max-h-[90vh] overflow-y-auto dark:bg-gray-800 dark:border-gray-700">
        <DialogHeader>
          <DialogTitle className="dark:text-white">租户详情</DialogTitle>
          <DialogDescription className="dark:text-gray-400">
            查看租户的详细信息
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-6">
          {/* 基本信息 */}
          <div className="space-y-4">
            <h3 className="text-sm dark:text-white flex items-center gap-2">
              <Building2 className="w-4 h-4" />
              基本信息
            </h3>
            <div className="grid grid-cols-2 gap-4 p-4 rounded-lg bg-gray-50 dark:bg-gray-900">
              <div>
                <div className="text-xs text-gray-500 dark:text-gray-400 mb-1">租户名称</div>
                <div className="text-sm dark:text-white">{tenant.name}</div>
              </div>
              <div>
                <div className="text-xs text-gray-500 dark:text-gray-400 mb-1">租户编码</div>
                <div className="text-sm dark:text-white font-mono">{tenant.code}</div>
              </div>
              <div>
                <div className="text-xs text-gray-500 dark:text-gray-400 mb-1">租户类型</div>
                <div className="text-sm dark:text-white">{tenant.type}</div>
              </div>
              <div>
                <div className="text-xs text-gray-500 dark:text-gray-400 mb-1">地址</div>
                <div className="text-sm dark:text-white">{tenant.address}</div>
              </div>
            </div>
          </div>

          {/* 管理员信息 */}
          <div className="space-y-4">
            <h3 className="text-sm dark:text-white flex items-center gap-2">
              <User className="w-4 h-4" />
              管理员信息
            </h3>
            <div className="grid grid-cols-2 gap-4 p-4 rounded-lg bg-gray-50 dark:bg-gray-900">
              <div>
                <div className="text-xs text-gray-500 dark:text-gray-400 mb-1">姓名</div>
                <div className="text-sm dark:text-white">{tenant.adminName}</div>
              </div>
              <div>
                <div className="text-xs text-gray-500 dark:text-gray-400 mb-1">邮箱</div>
                <div className="text-sm dark:text-white">{tenant.adminEmail}</div>
              </div>
              <div className="col-span-2">
                <div className="text-xs text-gray-500 dark:text-gray-400 mb-1">电话</div>
                <div className="text-sm dark:text-white">{tenant.adminPhone}</div>
              </div>
            </div>
          </div>

          {/* 统计信息 */}
          <div className="space-y-4">
            <h3 className="text-sm dark:text-white flex items-center gap-2">
              <Hash className="w-4 h-4" />
              统计信息
            </h3>
            <div className="grid grid-cols-2 gap-4 p-4 rounded-lg bg-gray-50 dark:bg-gray-900">
              <div>
                <div className="text-xs text-gray-500 dark:text-gray-400 mb-1">用户数</div>
                <div className="text-sm dark:text-white">{tenant.userCount} 人</div>
              </div>
              <div>
                <div className="text-xs text-gray-500 dark:text-gray-400 mb-1">部门数</div>
                <div className="text-sm dark:text-white">{tenant.departmentCount} 个</div>
              </div>
              <div>
                <div className="text-xs text-gray-500 dark:text-gray-400 mb-1">创建时间</div>
                <div className="text-sm dark:text-white">{tenant.createdAt}</div>
              </div>
              <div>
                <div className="text-xs text-gray-500 dark:text-gray-400 mb-1">到期时间</div>
                <div className="text-sm dark:text-white">{tenant.expireDate}</div>
              </div>
              <div>
                <div className="text-xs text-gray-500 dark:text-gray-400 mb-1">状态</div>
                <div className="text-sm">
                  <span className={`inline-block px-2 py-1 rounded text-xs ${
                    tenant.status === '已启用' 
                      ? 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400' 
                      : 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400'
                  }`}>
                    {tenant.status}
                  </span>
                </div>
              </div>
            </div>
          </div>

          {/* 关闭按钮 */}
          <div className="flex justify-end pt-4 border-t dark:border-gray-700">
            <Button
              variant="outline"
              onClick={() => onOpenChange(false)}
              className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
            >
              关闭
            </Button>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  );
}
