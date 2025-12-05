import { useState } from 'react';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription, DialogFooter } from '../ui/dialog';
import { Button } from '@/components/ui/button';
import { Label } from '@/components/ui/label';
import { Input } from '@/components/ui/input';
import { Crown, Search, Check } from 'lucide-react';
import { toast } from 'sonner';
import { Badge } from '@/components/ui/badge';

interface AssignRoleDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  targetType: 'user' | 'group' | 'department';
  targetName: string;
}

export function AssignRoleDialog({ open, onOpenChange, targetType, targetName }: AssignRoleDialogProps) {
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedRoles, setSelectedRoles] = useState<string[]>([]);

  // Mock roles data
  const availableRoles = [
    {
      id: 'R001',
      name: '系统管理员',
      description: '拥有系统的所有权限',
      type: '管理角色',
      permissions: ['系统管理', '用户管理', '权限管理', '审计日志'],
    },
    {
      id: 'R002',
      name: '项目管理员',
      description: '负责项目的整体管理和协调',
      type: '管理角色',
      permissions: ['查看项目', '编辑项目', '管理成员', '发布公告'],
    },
    {
      id: 'R003',
      name: '开发人员',
      description: '负责项目开发工作',
      type: '业务角色',
      permissions: ['查看项目', '编辑代码', '提交变更', '查看文档'],
    },
    {
      id: 'R004',
      name: '测试人员',
      description: '负责项目测试工作',
      type: '业务角色',
      permissions: ['查看项目', '创建Bug', '执行测试', '查看文档'],
    },
    {
      id: 'R005',
      name: '文档管理员',
      description: '负责项目文档的编写和维护',
      type: '业务角色',
      permissions: ['查看文档', '编辑文档', '发布文档'],
    },
    {
      id: 'R006',
      name: '审计员',
      description: '负责系统审计工作',
      type: '管理角色',
      permissions: ['查看审计日志', '导出报告'],
    },
    {
      id: 'R007',
      name: '访客',
      description: '仅拥有查看权限',
      type: '业务角色',
      permissions: ['查看项目', '查看文档'],
    },
  ];

  const filteredRoles = availableRoles.filter(role =>
    role.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
    role.description.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const getRoleTypeColor = (type: string) => {
    switch (type) {
      case '管理角色':
        return 'bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-400';
      case '业务角色':
        return 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400';
      default:
        return 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-400';
    }
  };

  const getTargetTypeText = () => {
    switch (targetType) {
      case 'user':
        return '用户';
      case 'group':
        return '组';
      case 'department':
        return '部门';
      default:
        return '对象';
    }
  };

  const handleToggleRole = (roleId: string) => {
    if (selectedRoles.includes(roleId)) {
      setSelectedRoles(selectedRoles.filter(id => id !== roleId));
    } else {
      setSelectedRoles([...selectedRoles, roleId]);
    }
  };

  const handleSubmit = () => {
    if (selectedRoles.length === 0) {
      toast.error('请至少选择一个角色');
      return;
    }

    const roleNames = availableRoles
      .filter(role => selectedRoles.includes(role.id))
      .map(role => role.name)
      .join('、');

    toast.success(`已为${getTargetTypeText()} "${targetName}" 分配角色: ${roleNames}`);
    onOpenChange(false);
    setSelectedRoles([]);
    setSearchQuery('');
  };

  const handleCancel = () => {
    onOpenChange(false);
    setSelectedRoles([]);
    setSearchQuery('');
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[700px] dark:bg-gray-800 dark:border-gray-700 max-h-[90vh] overflow-hidden flex flex-col">
        <DialogHeader>
          <DialogTitle className="dark:text-white flex items-center gap-2">
            <Crown className="w-5 h-5 text-blue-600 dark:text-blue-400" />
            分配角色
          </DialogTitle>
          <DialogDescription className="dark:text-gray-400">
            为{getTargetTypeText()} "{targetName}" 分配一个或多个角色
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-4 flex-1 overflow-hidden flex flex-col">
          {/* Search */}
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
            <Input
              placeholder="搜索角色名称或描述..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="pl-10 dark:bg-gray-900 dark:border-gray-700 dark:text-white"
            />
          </div>

          {/* Selected Count */}
          {selectedRoles.length > 0 && (
            <div className="text-sm text-gray-600 dark:text-gray-400">
              已选择 <span className="text-blue-600 dark:text-blue-400">{selectedRoles.length}</span> 个角色
            </div>
          )}

          {/* Roles List */}
          <div className="flex-1 overflow-y-auto space-y-2 pr-2">
            {filteredRoles.map((role) => {
              const isSelected = selectedRoles.includes(role.id);
              return (
                <button
                  key={role.id}
                  onClick={() => handleToggleRole(role.id)}
                  className={`w-full p-4 rounded-lg border transition-all text-left ${
                    isSelected
                      ? 'border-blue-500 bg-blue-50 dark:bg-blue-900/20 dark:border-blue-600'
                      : 'border-gray-200 dark:border-gray-700 hover:border-gray-300 dark:hover:border-gray-600 hover:bg-gray-50 dark:hover:bg-gray-750'
                  }`}
                >
                  <div className="flex items-start justify-between gap-4">
                    <div className="flex items-start gap-3 flex-1">
                      <div className={`w-10 h-10 rounded-lg flex items-center justify-center flex-shrink-0 ${
                        isSelected
                          ? 'bg-blue-500 dark:bg-blue-600'
                          : 'bg-gray-100 dark:bg-gray-700'
                      }`}>
                        {isSelected ? (
                          <Check className="w-5 h-5 text-white" />
                        ) : (
                          <Crown className={`w-5 h-5 ${
                            isSelected ? 'text-white' : 'text-gray-400 dark:text-gray-500'
                          }`} />
                        )}
                      </div>
                      <div className="flex-1">
                        <div className="flex items-center gap-2 mb-1">
                          <span className={`font-medium ${
                            isSelected
                              ? 'text-blue-900 dark:text-blue-100'
                              : 'text-gray-900 dark:text-white'
                          }`}>
                            {role.name}
                          </span>
                          <Badge className={`text-xs ${getRoleTypeColor(role.type)} border-0`}>
                            {role.type}
                          </Badge>
                        </div>
                        <p className={`text-sm mb-2 ${
                          isSelected
                            ? 'text-blue-700 dark:text-blue-300'
                            : 'text-gray-600 dark:text-gray-400'
                        }`}>
                          {role.description}
                        </p>
                        <div className="flex flex-wrap gap-1">
                          {role.permissions.slice(0, 3).map((permission, index) => (
                            <span
                              key={index}
                              className={`text-xs px-2 py-0.5 rounded ${
                                isSelected
                                  ? 'bg-blue-100 text-blue-700 dark:bg-blue-900/40 dark:text-blue-300'
                                  : 'bg-gray-100 text-gray-600 dark:bg-gray-800 dark:text-gray-400'
                              }`}
                            >
                              {permission}
                            </span>
                          ))}
                          {role.permissions.length > 3 && (
                            <span className={`text-xs px-2 py-0.5 rounded ${
                              isSelected
                                ? 'bg-blue-100 text-blue-700 dark:bg-blue-900/40 dark:text-blue-300'
                                : 'bg-gray-100 text-gray-600 dark:bg-gray-800 dark:text-gray-400'
                            }`}>
                              +{role.permissions.length - 3}
                            </span>
                          )}
                        </div>
                      </div>
                    </div>
                  </div>
                </button>
              );
            })}
          </div>

          {filteredRoles.length === 0 && (
            <div className="text-center py-8 text-gray-500 dark:text-gray-400">
              <Crown className="w-12 h-12 mx-auto mb-2 opacity-30" />
              <p>未找到匹配的角色</p>
            </div>
          )}
        </div>

        <DialogFooter>
          <Button
            variant="outline"
            onClick={handleCancel}
            className="dark:bg-gray-900 dark:border-gray-700 dark:text-gray-300"
          >
            取消
          </Button>
          <Button
            onClick={handleSubmit}
            disabled={selectedRoles.length === 0}
            className="bg-blue-500 hover:bg-blue-600 text-white disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <Crown className="w-4 h-4 mr-2" />
            分配角色 {selectedRoles.length > 0 && `(${selectedRoles.length})`}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
