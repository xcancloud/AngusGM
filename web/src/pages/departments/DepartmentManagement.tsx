import { useState } from 'react';
import { Search, Plus, Building2, Users, ChevronRight, ChevronDown, TrendingUp, Edit, Trash2, MoreVertical, UserPlus, FolderTree, Eye } from 'lucide-react';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Input } from '@/components/ui/input';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { DepartmentDialog, AddMemberDialog } from '@/pages/departments/DepartmentDialog';
import { DepartmentDetail } from '@/pages/departments/DepartmentDetail';
import { ConfirmDialog } from '@/components/gm/ConfirmDialog';
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
  children?: Department[];
}

// 初始部门数据
const initialDepartments: Department[] = [
  {
    id: 'D001',
    name: '技术部',
    code: 'TECH',
    parentId: null,
    managerName: '张三',
    userCount: 245,
    level: 1,
    status: '已启用',
    createdAt: '2024-01-15',
    children: [
      {
        id: 'D001-1',
        name: '前端开发组',
        code: 'TECH-FE',
        parentId: 'D001',
        managerName: '李四',
        userCount: 45,
        level: 2,
        status: '已启用',
        createdAt: '2024-01-20',
        children: [
          {
            id: 'D001-1-1',
            name: 'React团队',
            code: 'TECH-FE-REACT',
            parentId: 'D001-1',
            managerName: '王五',
            userCount: 18,
            level: 3,
            status: '已启用',
            createdAt: '2024-02-01',
            children: [
              {
                id: 'D001-1-1-1',
                name: 'React核心开发',
                code: 'TECH-FE-REACT-CORE',
                parentId: 'D001-1-1',
                managerName: '刘A',
                userCount: 8,
                level: 4,
                status: '已启用',
                createdAt: '2024-03-01',
                children: [
                  {
                    id: 'D001-1-1-1-1',
                    name: 'React组件库',
                    code: 'TECH-FE-REACT-CORE-LIB',
                    parentId: 'D001-1-1-1',
                    managerName: '张B',
                    userCount: 4,
                    level: 5,
                    status: '已启用',
                    createdAt: '2024-04-01',
                    children: [
                      {
                        id: 'D001-1-1-1-1-1',
                        name: 'UI组件团队',
                        code: 'TECH-FE-REACT-CORE-LIB-UI',
                        parentId: 'D001-1-1-1-1',
                        managerName: '李C',
                        userCount: 2,
                        level: 6,
                        status: '已启用',
                        createdAt: '2024-05-01',
                      },
                    ],
                  },
                ],
              },
              {
                id: 'D001-1-1-2',
                name: 'React测试',
                code: 'TECH-FE-REACT-TEST',
                parentId: 'D001-1-1',
                managerName: '赵D',
                userCount: 6,
                level: 4,
                status: '已启用',
                createdAt: '2024-03-01',
              },
            ],
          },
          {
            id: 'D001-1-2',
            name: 'Vue团队',
            code: 'TECH-FE-VUE',
            parentId: 'D001-1',
            managerName: '赵六',
            userCount: 15,
            level: 3,
            status: '已启用',
            createdAt: '2024-02-01',
          },
          {
            id: 'D001-1-3',
            name: 'UI/UX团队',
            code: 'TECH-FE-UI',
            parentId: 'D001-1',
            managerName: '孙七',
            userCount: 12,
            level: 3,
            status: '已启用',
            createdAt: '2024-02-05',
          },
        ],
      },
      {
        id: 'D001-2',
        name: '后端开发组',
        code: 'TECH-BE',
        parentId: 'D001',
        managerName: '周八',
        userCount: 68,
        level: 2,
        status: '已启用',
        createdAt: '2024-01-20',
        children: [
          {
            id: 'D001-2-1',
            name: 'Java团队',
            code: 'TECH-BE-JAVA',
            parentId: 'D001-2',
            managerName: '吴九',
            userCount: 32,
            level: 3,
            status: '已启用',
            createdAt: '2024-02-01',
            children: [
              {
                id: 'D001-2-1-1',
                name: 'Spring团队',
                code: 'TECH-BE-JAVA-SPRING',
                parentId: 'D001-2-1',
                managerName: '王E',
                userCount: 12,
                level: 4,
                status: '已启用',
                createdAt: '2024-03-05',
                children: [
                  {
                    id: 'D001-2-1-1-1',
                    name: 'Spring Boot',
                    code: 'TECH-BE-JAVA-SPRING-BOOT',
                    parentId: 'D001-2-1-1',
                    managerName: '孙F',
                    userCount: 6,
                    level: 5,
                    status: '已启用',
                    createdAt: '2024-04-10',
                    children: [
                      {
                        id: 'D001-2-1-1-1-1',
                        name: 'Boot核心研发',
                        code: 'TECH-BE-JAVA-SPRING-BOOT-CORE',
                        parentId: 'D001-2-1-1-1',
                        managerName: '周G',
                        userCount: 3,
                        level: 6,
                        status: '已启用',
                        createdAt: '2024-05-15',
                      },
                    ],
                  },
                ],
              },
            ],
          },
          {
            id: 'D001-2-2',
            name: 'Go团队',
            code: 'TECH-BE-GO',
            parentId: 'D001-2',
            managerName: '郑十',
            userCount: 24,
            level: 3,
            status: '已启用',
            createdAt: '2024-02-01',
          },
          {
            id: 'D001-2-3',
            name: '微服务团队',
            code: 'TECH-BE-MS',
            parentId: 'D001-2',
            managerName: '钱一',
            userCount: 12,
            level: 3,
            status: '已启用',
            createdAt: '2024-02-10',
          },
        ],
      },
      {
        id: 'D001-3',
        name: '测试组',
        code: 'TECH-QA',
        parentId: 'D001',
        managerName: '刘二',
        userCount: 35,
        level: 2,
        status: '已启用',
        createdAt: '2024-01-25',
      },
      {
        id: 'D001-4',
        name: '运维组',
        code: 'TECH-OPS',
        parentId: 'D001',
        managerName: '陈三',
        userCount: 28,
        level: 2,
        status: '已启用',
        createdAt: '2024-01-25',
      },
    ],
  },
  {
    id: 'D002',
    name: '市场部',
    code: 'MKT',
    parentId: null,
    managerName: '林四',
    userCount: 128,
    level: 1,
    status: '已启用',
    createdAt: '2024-01-15',
    children: [
      {
        id: 'D002-1',
        name: '品牌营销组',
        code: 'MKT-BRAND',
        parentId: 'D002',
        managerName: '黄五',
        userCount: 42,
        level: 2,
        status: '已启用',
        createdAt: '2024-01-22',
      },
      {
        id: 'D002-2',
        name: '渠道拓展组',
        code: 'MKT-CHANNEL',
        parentId: 'D002',
        managerName: '杨六',
        userCount: 38,
        level: 2,
        status: '已启用',
        createdAt: '2024-01-22',
      },
      {
        id: 'D002-3',
        name: '市场调研组',
        code: 'MKT-RESEARCH',
        parentId: 'D002',
        managerName: '朱七',
        userCount: 28,
        level: 2,
        status: '已启用',
        createdAt: '2024-01-28',
      },
    ],
  },
  {
    id: 'D003',
    name: '产品部',
    code: 'PROD',
    parentId: null,
    managerName: '何八',
    userCount: 85,
    level: 1,
    status: '已启用',
    createdAt: '2024-01-15',
    children: [
      {
        id: 'D003-1',
        name: '产品设计组',
        code: 'PROD-DESIGN',
        parentId: 'D003',
        managerName: '罗九',
        userCount: 32,
        level: 2,
        status: '已启用',
        createdAt: '2024-01-20',
      },
      {
        id: 'D003-2',
        name: '产品运营组',
        code: 'PROD-OPS',
        parentId: 'D003',
        managerName: '谢十',
        userCount: 28,
        level: 2,
        status: '已启用',
        createdAt: '2024-01-20',
      },
      {
        id: 'D003-3',
        name: '数据分析组',
        code: 'PROD-DATA',
        parentId: 'D003',
        managerName: '唐一',
        userCount: 25,
        level: 2,
        status: '已启用',
        createdAt: '2024-01-25',
      },
    ],
  },
  {
    id: 'D004',
    name: '人力资源部',
    code: 'HR',
    parentId: null,
    managerName: '韩二',
    userCount: 32,
    level: 1,
    status: '已启用',
    createdAt: '2024-01-15',
  },
  {
    id: 'D005',
    name: '财务部',
    code: 'FIN',
    parentId: null,
    managerName: '曹三',
    userCount: 28,
    level: 1,
    status: '已启用',
    createdAt: '2024-01-15',
  },
];

export function DepartmentManagement() {
  const [departments, setDepartments] = useState<Department[]>(initialDepartments);
  const [searchQuery, setSearchQuery] = useState('');
  const [expandedDepts, setExpandedDepts] = useState<Set<string>>(new Set(['D001', 'D002', 'D003']));
  const [deptDialogOpen, setDeptDialogOpen] = useState(false);
  const [memberDialogOpen, setMemberDialogOpen] = useState(false);
  const [showDetailPage, setShowDetailPage] = useState(false);
  const [confirmDialogOpen, setConfirmDialogOpen] = useState(false);
  const [dialogMode, setDialogMode] = useState<'add' | 'edit' | 'addChild'>('add');
  const [selectedDepartment, setSelectedDepartment] = useState<Department | undefined>();
  const [selectedParentDept, setSelectedParentDept] = useState<Department | undefined>();
  const [deptToDelete, setDeptToDelete] = useState<Department | undefined>();
  const [matchedDeptIds, setMatchedDeptIds] = useState<Set<string>>(new Set());

  const stats = [
    {
      label: '总部门数',
      value: '156',
      subtext: '本月新增 8 个',
      icon: Building2,
      iconBg: 'bg-blue-500',
      valueColor: 'text-blue-600 dark:text-blue-400',
      trend: '+5%',
      trendDirection: 'up',
    },
    {
      label: '一级部门',
      value: '12',
      subtext: '顶层组织架构',
      icon: FolderTree,
      iconBg: 'bg-purple-500',
      valueColor: 'text-purple-600 dark:text-purple-400',
      trend: '+2',
      trendDirection: 'up',
    },
    {
      label: '平均层级',
      value: '3.2',
      subtext: '最深 6 层',
      icon: ChevronRight,
      iconBg: 'bg-green-500',
      valueColor: 'text-green-600 dark:text-green-400',
      trend: '0',
      trendDirection: 'up',
    },
    {
      label: '部门人数',
      value: '1,234',
      subtext: '平均 7.9 人/部门',
      icon: Users,
      iconBg: 'bg-orange-500',
      valueColor: 'text-orange-600 dark:text-orange-400',
      trend: '+12%',
      trendDirection: 'up',
    },
  ];

  // 生成新部门ID
  const generateDepartmentId = () => {
    const timestamp = Date.now();
    const random = Math.floor(Math.random() * 1000);
    return `D${timestamp}${random}`;
  };

  // 计算部门层级
  const calculateLevel = (parentId: string | null): number => {
    if (!parentId) return 1;
    const allDepts = getAllDepartments(departments);
    const parent = allDepts.find(d => d.id === parentId);
    return parent ? parent.level + 1 : 1;
  };

  // 在树结构中添加部门
  const addDepartmentToTree = (depts: Department[], newDept: Department, parentId: string | null): Department[] => {
    if (!parentId) {
      // 添加为顶级部门
      return [...depts, newDept];
    }

    return depts.map(dept => {
      if (dept.id === parentId) {
        return {
          ...dept,
          children: [...(dept.children || []), newDept],
        };
      }
      if (dept.children) {
        return {
          ...dept,
          children: addDepartmentToTree(dept.children, newDept, parentId),
        };
      }
      return dept;
    });
  };

  // 在树结构中更新部门
  const updateDepartmentInTree = (depts: Department[], deptId: string, updates: Partial<Department>): Department[] => {
    return depts.map(dept => {
      if (dept.id === deptId) {
        return { ...dept, ...updates };
      }
      if (dept.children) {
        return {
          ...dept,
          children: updateDepartmentInTree(dept.children, deptId, updates),
        };
      }
      return dept;
    });
  };

  // 在树结构中删除部门
  const deleteDepartmentFromTree = (depts: Department[], deptId: string): Department[] => {
    return depts.filter(dept => {
      if (dept.id === deptId) {
        return false;
      }
      if (dept.children) {
        dept.children = deleteDepartmentFromTree(dept.children, deptId);
      }
      return true;
    });
  };

  // 处理添加/编辑部门提交
  const handleDepartmentSubmit = (data: {
    name: string;
    code: string;
    managerName: string;
    parentId: string | null;
    status: '已启用' | '已禁用';
    description: string;
  }) => {
    if (dialogMode === 'edit' && selectedDepartment) {
      // 编辑部门
      const updatedDepts = updateDepartmentInTree(departments, selectedDepartment.id, {
        name: data.name,
        code: data.code,
        managerName: data.managerName,
        status: data.status,
      });
      setDepartments(updatedDepts);
    } else {
      // 添加新部门或子部门
      const parentId = dialogMode === 'addChild' && selectedParentDept 
        ? selectedParentDept.id 
        : data.parentId;
      
      const level = calculateLevel(parentId);
      const newDept: Department = {
        id: generateDepartmentId(),
        name: data.name,
        code: data.code,
        managerName: data.managerName,
        parentId,
        userCount: 0,
        level,
        status: data.status,
        createdAt: new Date().toISOString().split('T')[0],
        children: [],
      };

      const updatedDepts = addDepartmentToTree(departments, newDept, parentId);
      setDepartments(updatedDepts);
      
      // 如果是添加子部门，自动展开父部门
      if (parentId) {
        setExpandedDepts(prev => new Set([...prev, parentId]));
      }
    }
  };

  // 确认删除部门
  const confirmDelete = () => {
    if (deptToDelete) {
      const updatedDepts = deleteDepartmentFromTree(departments, deptToDelete.id);
      setDepartments(updatedDepts);
      setDeptToDelete(undefined);
    }
  };

  const handleAddDepartment = () => {
    setDialogMode('add');
    setSelectedDepartment(undefined);
    setSelectedParentDept(undefined);
    setDeptDialogOpen(true);
  };

  const handleEditDepartment = (dept: Department) => {
    setDialogMode('edit');
    setSelectedDepartment(dept);
    setDeptDialogOpen(true);
  };

  const handleAddChild = (parentDept: Department) => {
    setDialogMode('addChild');
    setSelectedParentDept(parentDept);
    setDeptDialogOpen(true);
  };

  const handleAddMember = (dept: Department) => {
    setSelectedDepartment(dept);
    setMemberDialogOpen(true);
  };

  const handleViewDetail = (dept: Department) => {
    setSelectedDepartment(dept);
    setShowDetailPage(true);
  };

  const handleDeleteDepartment = (dept: Department) => {
    setDeptToDelete(dept);
    setConfirmDialogOpen(true);
  };

  const toggleExpand = (deptId: string) => {
    setExpandedDepts(prev => {
      const newSet = new Set(prev);
      if (newSet.has(deptId)) {
        newSet.delete(deptId);
      } else {
        newSet.add(deptId);
      }
      return newSet;
    });
  };

  // 获取所有部门（扁平化）
  const getAllDepartments = (depts: Department[]): Department[] => {
    let result: Department[] = [];
    depts.forEach(dept => {
      result.push(dept);
      if (dept.children) {
        result = result.concat(getAllDepartments(dept.children));
      }
    });
    return result;
  };

  // 检查部门或其子部门是否匹配搜索
  const departmentMatchesSearch = (dept: Department, query: string): boolean => {
    const lowerQuery = query.toLowerCase();
    return (
      dept.name.toLowerCase().includes(lowerQuery) ||
      dept.code.toLowerCase().includes(lowerQuery) ||
      dept.managerName.toLowerCase().includes(lowerQuery)
    );
  };

  // 检查部门树是否包含匹配项（递归检查）
  const hasMatchInTree = (dept: Department, query: string): boolean => {
    if (departmentMatchesSearch(dept, query)) {
      return true;
    }
    if (dept.children) {
      return dept.children.some(child => hasMatchInTree(child, query));
    }
    return false;
  };

  // 收集所有匹配的部门ID和需要展开的父级ID
  const collectMatchedAndParentIds = (depts: Department[], query: string, parentIds: string[] = []): { matched: Set<string>, toExpand: Set<string> } => {
    const matched = new Set<string>();
    const toExpand = new Set<string>();

    depts.forEach(dept => {
      const isMatch = departmentMatchesSearch(dept, query);
      const hasChildMatch = dept.children ? dept.children.some(child => hasMatchInTree(child, query)) : false;

      if (isMatch) {
        matched.add(dept.id);
        // 展开所有父级
        parentIds.forEach(pid => toExpand.add(pid));
      }

      if (hasChildMatch) {
        // 如果子级有匹配，展开当前部门
        toExpand.add(dept.id);
        // 展开所有父级
        parentIds.forEach(pid => toExpand.add(pid));
      }

      if (dept.children) {
        const childResult = collectMatchedAndParentIds(dept.children, query, [...parentIds, dept.id]);
        childResult.matched.forEach(id => matched.add(id));
        childResult.toExpand.forEach(id => toExpand.add(id));
      }
    });

    return { matched, toExpand };
  };

  // 当搜索词变化时，更新匹配和展开状态
  const handleSearchChange = (value: string) => {
    setSearchQuery(value);
    
    if (value.trim()) {
      const { matched, toExpand } = collectMatchedAndParentIds(departments, value);
      setMatchedDeptIds(matched);
      setExpandedDepts(toExpand);
    } else {
      setMatchedDeptIds(new Set());
      setExpandedDepts(new Set(['D001', 'D002', 'D003']));
    }
  };

  // 高亮文本
  const highlightText = (text: string, query: string) => {
    if (!query.trim()) return text;
    
    const parts = text.split(new RegExp(`(${query})`, 'gi'));
    return (
      <>
        {parts.map((part, index) => 
          part.toLowerCase() === query.toLowerCase() ? (
            <span key={index} className="bg-yellow-200 dark:bg-yellow-600/40 text-gray-900 dark:text-white">
              {part}
            </span>
          ) : (
            part
          )
        )}
      </>
    );
  };

  const renderDepartment = (dept: Department, isLast: boolean = false) => {
    const hasChildren = dept.children && dept.children.length > 0;
    const isExpanded = expandedDepts.has(dept.id);
    const indentLevel = dept.level - 1;
    const isMatched = matchedDeptIds.has(dept.id);
    
    // 如果有搜索且当前部门及其子树都不匹配，则不显示
    if (searchQuery.trim() && !hasMatchInTree(dept, searchQuery)) {
      return null;
    }

    return (
      <div key={dept.id}>
        <div className={`flex items-center hover:bg-gray-50 dark:hover:bg-gray-700 ${!isLast ? 'border-b dark:border-gray-700' : ''}`}>
          {/* 缩进线条 */}
          <div className="flex items-center" style={{ paddingLeft: `${indentLevel * 16}px` }}>
            {indentLevel > 0 && (
              <div className="flex items-center">
                {Array.from({ length: indentLevel }).map((_, i) => (
                  <div key={i} className="w-4 flex items-center justify-center">
                    {i === indentLevel - 1 ? (
                      <div className="w-full h-px bg-gray-300 dark:bg-gray-600"></div>
                    ) : (
                      <div className="w-px h-full"></div>
                    )}
                  </div>
                ))}
              </div>
            )}
            {hasChildren ? (
              <button
                onClick={() => toggleExpand(dept.id)}
                className="p-1 hover:bg-gray-200 dark:hover:bg-gray-700 rounded mr-1"
              >
                {isExpanded ? (
                  <ChevronDown className="w-4 h-4 text-gray-600 dark:text-gray-400" />
                ) : (
                  <ChevronRight className="w-4 h-4 text-gray-600 dark:text-gray-400" />
                )}
              </button>
            ) : (
              <div className="w-6 mr-1"></div>
            )}
          </div>

          {/* 部门信息 */}
          <div className={`flex-1 grid grid-cols-8 items-center py-4 pr-4 gap-4 ${isMatched ? 'bg-blue-50/50 dark:bg-blue-900/10' : ''}`}>
            <div className="col-span-2 flex items-center gap-3">
              <div className={`p-2 rounded-lg ${dept.level === 1 ? 'bg-blue-100 dark:bg-blue-900/30' : dept.level === 2 ? 'bg-purple-100 dark:bg-purple-900/30' : 'bg-gray-100 dark:bg-gray-700'}`}>
                <Building2 className={`w-4 h-4 ${dept.level === 1 ? 'text-blue-600 dark:text-blue-400' : dept.level === 2 ? 'text-purple-600 dark:text-purple-400' : 'text-gray-600 dark:text-gray-400'}`} />
              </div>
              <div>
                <div className="dark:text-white">{highlightText(dept.name, searchQuery)}</div>
                <div className="text-xs text-gray-500 dark:text-gray-400 mt-0.5">
                  {highlightText(dept.code, searchQuery)}
                </div>
              </div>
            </div>

            <div className="text-sm text-gray-700 dark:text-gray-300">
              {highlightText(dept.managerName, searchQuery)}
            </div>

            <div className="flex items-center gap-1 text-sm text-gray-700 dark:text-gray-300">
              <Users className="w-3.5 h-3.5" />
              {dept.userCount} 人
            </div>

            <div>
              <Badge className="text-xs bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400 border-0">
                L{dept.level}
              </Badge>
            </div>

            <div>
              <Badge className={`text-xs ${dept.status === '已启用' ? 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400' : 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400'} border-0`}>
                {dept.status}
              </Badge>
            </div>

            <div className="text-sm text-gray-600 dark:text-gray-400">
              {dept.createdAt}
            </div>

            <div className="flex justify-end">
              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <button className="p-1.5 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg">
                    <MoreVertical className="w-4 h-4 text-gray-600 dark:text-gray-400" />
                  </button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end" className="dark:bg-gray-900 dark:border-gray-700">
                  <DropdownMenuItem 
                    onClick={() => handleViewDetail(dept)}
                    className="dark:text-gray-300 dark:hover:bg-gray-800"
                  >
                    <Eye className="w-4 h-4 mr-2" />
                    查看详情
                  </DropdownMenuItem>
                  <DropdownMenuItem 
                    onClick={() => handleAddChild(dept)}
                    className="dark:text-gray-300 dark:hover:bg-gray-800"
                    disabled={dept.level >= 6}
                  >
                    <Plus className="w-4 h-4 mr-2" />
                    添加子部门{dept.level >= 6 && ' (已达最大层级)'}
                  </DropdownMenuItem>
                  <DropdownMenuItem 
                    onClick={() => handleEditDepartment(dept)}
                    className="dark:text-gray-300 dark:hover:bg-gray-800"
                  >
                    <Edit className="w-4 h-4 mr-2" />
                    编辑
                  </DropdownMenuItem>
                  <DropdownMenuItem 
                    onClick={() => handleAddMember(dept)}
                    className="dark:text-gray-300 dark:hover:bg-gray-800"
                  >
                    <UserPlus className="w-4 h-4 mr-2" />
                    添加成员
                  </DropdownMenuItem>
                  <DropdownMenuItem 
                    onClick={() => handleDeleteDepartment(dept)}
                    className="text-red-600 dark:text-red-400 dark:hover:bg-gray-800"
                  >
                    <Trash2 className="w-4 h-4 mr-2" />
                    删除
                  </DropdownMenuItem>
                </DropdownMenuContent>
              </DropdownMenu>
            </div>
          </div>
        </div>

        {/* 递归渲染子部门 */}
        {hasChildren && isExpanded && dept.children!.map((child, index) => 
          renderDepartment(child, index === dept.children!.length - 1)
        )}
      </div>
    );
  };

  // 过滤顶层部门（如果有搜索，只显示包含匹配的树）
  const filteredDepartments = searchQuery.trim()
    ? departments.filter(dept => hasMatchInTree(dept, searchQuery))
    : departments;

  // 如果显示详情页面，则渲染DepartmentDetail组件
  if (showDetailPage && selectedDepartment) {
    return (
      <DepartmentDetail 
        department={selectedDepartment} 
        onBack={() => setShowDetailPage(false)} 
      />
    );
  }

  return (
    <div className="space-y-6">
      {/* 统计卡片 */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {stats.map((stat, index) => {
          const Icon = stat.icon;
          return (
            <Card key={index} className="relative overflow-hidden dark:bg-gray-800 dark:border-gray-700">
              <div className="absolute top-0 right-0 w-32 h-32 opacity-10">
                <div className={`w-full h-full ${stat.iconBg} rounded-full blur-3xl transform translate-x-8 -translate-y-8`}></div>
              </div>
              <div className="p-6 relative">
                <div className="flex items-center justify-between mb-4">
                  <div className={`p-3 rounded-xl ${stat.iconBg}/10`}>
                    <Icon className={`w-6 h-6 ${stat.iconBg.replace('bg-', 'text-')}`} />
                  </div>
                  <div className={`flex items-center gap-1 text-sm ${stat.trendDirection === 'up' ? 'text-green-600 dark:text-green-400' : 'text-red-600 dark:text-red-400'}`}>
                    <TrendingUp className={`w-4 h-4 ${stat.trendDirection === 'down' ? 'rotate-180' : ''}`} />
                    <span>{stat.trend}</span>
                  </div>
                </div>
                <div className={`text-3xl mb-2 ${stat.valueColor}`}>
                  {stat.value}
                </div>
                <div className="text-sm text-gray-600 dark:text-gray-400 mb-1">
                  {stat.label}
                </div>
                <div className="text-xs text-gray-500 dark:text-gray-500">
                  {stat.subtext}
                </div>
              </div>
            </Card>
          );
        })}
      </div>

      {/* 搜索和添加 */}
      <Card className="p-6 dark:bg-gray-800 dark:border-gray-700">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="relative w-[390px]">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
              <Input
                placeholder="搜索部门名称、编码或负责人..."
                value={searchQuery}
                onChange={(e) => handleSearchChange(e.target.value)}
                className="pl-10 dark:bg-gray-900 dark:border-gray-700 dark:text-white"
              />
            </div>
            {searchQuery.trim() && matchedDeptIds.size > 0 && (
              <div className="text-sm text-gray-600 dark:text-gray-400">
                找到 <span className="text-blue-600 dark:text-blue-400">{matchedDeptIds.size}</span> 个匹配结果
              </div>
            )}
          </div>
          <div className="flex items-center gap-2">
            <Button 
              variant="outline"
              className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
              onClick={() => setExpandedDepts(new Set(departments.map(d => d.id)))}
            >
              展开全部
            </Button>
            <Button 
              variant="outline"
              className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
              onClick={() => setExpandedDepts(new Set())}
            >
              收起全部
            </Button>
            <Button 
              className="bg-blue-500 hover:bg-blue-600 text-white"
              onClick={handleAddDepartment}
            >
              <Plus className="w-4 h-4 mr-2" />
              添加部门
            </Button>
          </div>
        </div>
      </Card>

      {/* 部门树形表格 */}
      <Card className="dark:bg-gray-800 dark:border-gray-700">
        <div className="overflow-x-auto">
          {/* 表头 */}
          <div className="grid grid-cols-8 border-b dark:border-gray-700 p-4 gap-4 bg-gray-50 dark:bg-gray-900/50">
            <div className="col-span-2 text-sm text-gray-600 dark:text-gray-400">部门名称</div>
            <div className="text-sm text-gray-600 dark:text-gray-400">负责人</div>
            <div className="text-sm text-gray-600 dark:text-gray-400">人数</div>
            <div className="text-sm text-gray-600 dark:text-gray-400">层级</div>
            <div className="text-sm text-gray-600 dark:text-gray-400">状态</div>
            <div className="text-sm text-gray-600 dark:text-gray-400">创建时间</div>
            <div className="text-sm text-gray-600 dark:text-gray-400 text-right">操作</div>
          </div>

          {/* 树形结构 */}
          <div>
            {filteredDepartments.map((dept, index) => 
              renderDepartment(dept, index === filteredDepartments.length - 1)
            )}
          </div>
        </div>
      </Card>

      {/* 对话框 */}
      <DepartmentDialog
        open={deptDialogOpen}
        onOpenChange={setDeptDialogOpen}
        mode={dialogMode}
        department={selectedDepartment}
        parentDepartment={selectedParentDept}
        allDepartments={getAllDepartments(departments)}
        onSubmit={handleDepartmentSubmit}
      />

      <AddMemberDialog
        open={memberDialogOpen}
        onOpenChange={setMemberDialogOpen}
        departmentName={selectedDepartment?.name || ''}
      />

      <ConfirmDialog
        open={confirmDialogOpen}
        onOpenChange={setConfirmDialogOpen}
        title="确认删除"
        description={`确定要删除部门 "${deptToDelete}" 吗？此操作不可撤销。`}
        onConfirm={confirmDelete}
        confirmText="删除"
        variant="danger"
      />
    </div>
  );
}