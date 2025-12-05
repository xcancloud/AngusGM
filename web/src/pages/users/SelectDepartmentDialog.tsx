import { useState } from 'react';
import { Building2, Search } from 'lucide-react';
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

interface Department {
  id: string;
  name: string;
  level: string;
  parent: string;
  memberCount: number;
}

interface SelectDepartmentDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onConfirm: (selectedDepartments: Department[]) => void;
}

export function SelectDepartmentDialog({ open, onOpenChange, onConfirm }: SelectDepartmentDialogProps) {
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedDepartments, setSelectedDepartments] = useState<string[]>([]);

  // Mock departments data
  const availableDepartments: Department[] = [
    { id: 'D001', name: '技术研发部', level: '二级部门', parent: '技术中心', memberCount: 45 },
    { id: 'D002', name: '前端开发组', level: '三级部门', parent: '技术研发部', memberCount: 15 },
    { id: 'D003', name: '后端开发组', level: '三级部门', parent: '技术研发部', memberCount: 20 },
    { id: 'D004', name: '产品部', level: '二级部门', parent: '总经办', memberCount: 12 },
    { id: 'D005', name: '设计部', level: '二级部门', parent: '总经办', memberCount: 8 },
    { id: 'D006', name: '运维部', level: '二级部门', parent: '技术中心', memberCount: 10 },
  ];

  const filteredDepartments = availableDepartments.filter(dept =>
    dept.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
    dept.parent.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const handleToggle = (deptId: string) => {
    setSelectedDepartments(prev =>
      prev.includes(deptId)
        ? prev.filter(id => id !== deptId)
        : [...prev, deptId]
    );
  };

  const handleConfirm = () => {
    const selected = availableDepartments.filter(dept => selectedDepartments.includes(dept.id));
    onConfirm(selected);
    onOpenChange(false);
    setSelectedDepartments([]);
    setSearchQuery('');
  };

  const getDepartmentLevelColor = (level: string) => {
    switch (level) {
      case '一级部门':
        return 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400';
      case '二级部门':
        return 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400';
      case '三级部门':
        return 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400';
      default:
        return 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-400';
    }
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="dark:bg-gray-800 dark:border-gray-700 max-w-2xl">
        <DialogHeader>
          <DialogTitle className="dark:text-white">选择部门</DialogTitle>
          <DialogDescription className="dark:text-gray-400">
            选择要关联的部门，可以选择多个
          </DialogDescription>
        </DialogHeader>
        <div className="space-y-4">
          {/* Search */}
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
            <Input
              placeholder="搜索部门..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="pl-9 dark:bg-gray-900 dark:border-gray-700 dark:text-white"
            />
          </div>

          {/* Department List */}
          <div className="max-h-96 overflow-y-auto space-y-2">
            {filteredDepartments.map((dept) => (
              <div
                key={dept.id}
                className="flex items-start gap-3 p-3 rounded-lg border dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-750 cursor-pointer"
                onClick={() => handleToggle(dept.id)}
              >
                <Checkbox
                  checked={selectedDepartments.includes(dept.id)}
                  onCheckedChange={() => handleToggle(dept.id)}
                  className="mt-1"
                />
                <div className="flex items-start gap-3 flex-1">
                  <div className="w-10 h-10 rounded-lg bg-blue-100 dark:bg-blue-900/30 flex items-center justify-center shrink-0">
                    <Building2 className="w-5 h-5 text-blue-600 dark:text-blue-400" />
                  </div>
                  <div className="flex-1">
                    <div className="flex items-center gap-2 mb-1">
                      <span className="dark:text-white">{dept.name}</span>
                      <Badge className={`border-0 text-xs ${getDepartmentLevelColor(dept.level)}`}>
                        {dept.level}
                      </Badge>
                    </div>
                    <p className="text-sm text-gray-600 dark:text-gray-400">
                      上级: {dept.parent} · {dept.memberCount} 名成员
                    </p>
                  </div>
                </div>
              </div>
            ))}
          </div>

          {selectedDepartments.length > 0 && (
            <div className="pt-2 border-t dark:border-gray-700">
              <p className="text-sm text-gray-600 dark:text-gray-400">
                已选择 {selectedDepartments.length} 个部门
              </p>
            </div>
          )}
        </div>
        <DialogFooter>
          <Button
            variant="outline"
            onClick={() => {
              onOpenChange(false);
              setSelectedDepartments([]);
              setSearchQuery('');
            }}
            className="dark:border-gray-600 dark:text-gray-300 dark:hover:bg-gray-700"
          >
            取消
          </Button>
          <Button
            onClick={handleConfirm}
            disabled={selectedDepartments.length === 0}
            className="bg-blue-600 hover:bg-blue-700 text-white"
          >
            确定 ({selectedDepartments.length})
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
