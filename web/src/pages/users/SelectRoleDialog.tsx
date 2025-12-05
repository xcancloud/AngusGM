import { useState } from 'react';
import { Crown, Search } from 'lucide-react';
import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Checkbox } from '@/components/ui/checkbox';

interface Role {
  id: string;
  name: string;
  type: string;
  description: string;
}

interface SelectRoleDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onConfirm: (selectedRoles: Role[]) => void;
}

export function SelectRoleDialog({ open, onOpenChange, onConfirm }: SelectRoleDialogProps) {
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedRoles, setSelectedRoles] = useState<string[]>([]);

  // Mock roles data
  const availableRoles: Role[] = [
    { id: 'R001', name: '项目管理员', type: '管理角色', description: '负责项目的整体管理和协调' },
    { id: 'R002', name: '开发人员', type: '业务角色', description: '负责项目开发工作' },
    { id: 'R003', name: '文档管理员', type: '业务角色', description: '负责项目文档的编写和维护' },
    { id: 'R004', name: '测试工程师', type: '业务角色', description: '负责软件测试工作' },
    { id: 'R005', name: '运维工程师', type: '业务角色', description: '负责系统运维工作' },
    { id: 'R006', name: '产品经理', type: '管理角色', description: '负责产品规划和管理' },
  ];

  const filteredRoles = availableRoles.filter(role =>
    role.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
    role.description.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const handleToggle = (roleId: string) => {
    setSelectedRoles(prev =>
      prev.includes(roleId)
        ? prev.filter(id => id !== roleId)
        : [...prev, roleId]
    );
  };

  const handleConfirm = () => {
    const selected = availableRoles.filter(role => selectedRoles.includes(role.id));
    onConfirm(selected);
    onOpenChange(false);
    setSelectedRoles([]);
    setSearchQuery('');
  };

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

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="dark:bg-gray-800 dark:border-gray-700 max-w-2xl">
        <DialogHeader>
          <DialogTitle className="dark:text-white">选择角色</DialogTitle>
          <DialogDescription className="dark:text-gray-400">
            选择要分配的角色，可以选择多个
          </DialogDescription>
        </DialogHeader>
        <div className="space-y-4">
          {/* Search */}
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
            <Input
              placeholder="搜索角色..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="pl-9 dark:bg-gray-900 dark:border-gray-700 dark:text-white"
            />
          </div>

          {/* Role List */}
          <div className="max-h-96 overflow-y-auto space-y-2">
            {filteredRoles.map((role) => (
              <div
                key={role.id}
                className="flex items-start gap-3 p-3 rounded-lg border dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-750 cursor-pointer"
                onClick={() => handleToggle(role.id)}
              >
                <Checkbox
                  checked={selectedRoles.includes(role.id)}
                  onCheckedChange={() => handleToggle(role.id)}
                  className="mt-1"
                />
                <div className="flex items-start gap-3 flex-1">
                  <div className="w-10 h-10 rounded-lg bg-blue-100 dark:bg-blue-900/30 flex items-center justify-center shrink-0">
                    <Crown className="w-5 h-5 text-blue-600 dark:text-blue-400" />
                  </div>
                  <div className="flex-1">
                    <div className="flex items-center gap-2 mb-1">
                      <span className="dark:text-white">{role.name}</span>
                      <Badge className={`border-0 text-xs ${getRoleTypeColor(role.type)}`}>
                        {role.type}
                      </Badge>
                    </div>
                    <p className="text-sm text-gray-600 dark:text-gray-400">{role.description}</p>
                  </div>
                </div>
              </div>
            ))}
          </div>

          {selectedRoles.length > 0 && (
            <div className="pt-2 border-t dark:border-gray-700">
              <p className="text-sm text-gray-600 dark:text-gray-400">
                已选择 {selectedRoles.length} 个角色
              </p>
            </div>
          )}
        </div>
        <DialogFooter>
          <Button
            variant="outline"
            onClick={() => {
              onOpenChange(false);
              setSelectedRoles([]);
              setSearchQuery('');
            }}
            className="dark:border-gray-600 dark:text-gray-300 dark:hover:bg-gray-700"
          >
            取消
          </Button>
          <Button
            onClick={handleConfirm}
            disabled={selectedRoles.length === 0}
            className="bg-blue-600 hover:bg-blue-700 text-white"
          >
            确定 ({selectedRoles.length})
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
