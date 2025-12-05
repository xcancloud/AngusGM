import { useState } from 'react';
import { Users, Search } from 'lucide-react';
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

interface Group {
  id: string;
  name: string;
  type: string;
  memberCount: number;
  description: string;
}

interface SelectGroupDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onConfirm: (selectedGroups: Group[]) => void;
}

export function SelectGroupDialog({ open, onOpenChange, onConfirm }: SelectGroupDialogProps) {
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedGroups, setSelectedGroups] = useState<string[]>([]);

  // Mock groups data
  const availableGroups: Group[] = [
    { id: 'G001', name: '管理员组', type: '系统组', memberCount: 15, description: '系统管理员用户组' },
    { id: 'G002', name: '技术团队', type: '部门组', memberCount: 28, description: '技术部门工作组' },
    { id: 'G003', name: '项目Alpha小组', type: '项目组', memberCount: 8, description: 'Alpha项目团队' },
    { id: 'G004', name: '产品团队', type: '部门组', memberCount: 12, description: '产品部门工作组' },
    { id: 'G005', name: '设计团队', type: '部门组', memberCount: 10, description: '设计部门工作组' },
    { id: 'G006', name: '项目Beta小组', type: '项目组', memberCount: 6, description: 'Beta项目团队' },
  ];

  const filteredGroups = availableGroups.filter(group =>
    group.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
    group.description.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const handleToggle = (groupId: string) => {
    setSelectedGroups(prev =>
      prev.includes(groupId)
        ? prev.filter(id => id !== groupId)
        : [...prev, groupId]
    );
  };

  const handleConfirm = () => {
    const selected = availableGroups.filter(group => selectedGroups.includes(group.id));
    onConfirm(selected);
    onOpenChange(false);
    setSelectedGroups([]);
    setSearchQuery('');
  };

  const getGroupTypeColor = (type: string) => {
    switch (type) {
      case '系统组':
        return 'bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-400';
      case '部门组':
        return 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400';
      case '项目组':
        return 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400';
      default:
        return 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-400';
    }
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="dark:bg-gray-800 dark:border-gray-700 max-w-2xl">
        <DialogHeader>
          <DialogTitle className="dark:text-white">选择组</DialogTitle>
          <DialogDescription className="dark:text-gray-400">
            选择要加入的组，可以选择多个
          </DialogDescription>
        </DialogHeader>
        <div className="space-y-4">
          {/* Search */}
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
            <Input
              placeholder="搜索组..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="pl-9 dark:bg-gray-900 dark:border-gray-700 dark:text-white"
            />
          </div>

          {/* Group List */}
          <div className="max-h-96 overflow-y-auto space-y-2">
            {filteredGroups.map((group) => (
              <div
                key={group.id}
                className="flex items-start gap-3 p-3 rounded-lg border dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-750 cursor-pointer"
                onClick={() => handleToggle(group.id)}
              >
                <Checkbox
                  checked={selectedGroups.includes(group.id)}
                  onCheckedChange={() => handleToggle(group.id)}
                  className="mt-1"
                />
                <div className="flex items-start gap-3 flex-1">
                  <div className="w-10 h-10 rounded-lg bg-blue-100 dark:bg-blue-900/30 flex items-center justify-center shrink-0">
                    <Users className="w-5 h-5 text-blue-600 dark:text-blue-400" />
                  </div>
                  <div className="flex-1">
                    <div className="flex items-center gap-2 mb-1">
                      <span className="dark:text-white">{group.name}</span>
                      <Badge className={`border-0 text-xs ${getGroupTypeColor(group.type)}`}>
                        {group.type}
                      </Badge>
                    </div>
                    <p className="text-sm text-gray-600 dark:text-gray-400">
                      {group.description} · {group.memberCount} 名成员
                    </p>
                  </div>
                </div>
              </div>
            ))}
          </div>

          {selectedGroups.length > 0 && (
            <div className="pt-2 border-t dark:border-gray-700">
              <p className="text-sm text-gray-600 dark:text-gray-400">
                已选择 {selectedGroups.length} 个组
              </p>
            </div>
          )}
        </div>
        <DialogFooter>
          <Button
            variant="outline"
            onClick={() => {
              onOpenChange(false);
              setSelectedGroups([]);
              setSearchQuery('');
            }}
            className="dark:border-gray-600 dark:text-gray-300 dark:hover:bg-gray-700"
          >
            取消
          </Button>
          <Button
            onClick={handleConfirm}
            disabled={selectedGroups.length === 0}
            className="bg-blue-600 hover:bg-blue-700 text-white"
          >
            确定 ({selectedGroups.length})
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
