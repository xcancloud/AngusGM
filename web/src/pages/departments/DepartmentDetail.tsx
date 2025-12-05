import { ChevronRight, Edit, Trash2, UserPlus, Building2, Tag, Calendar, Users, Clock, Activity, FolderTree, User, Crown } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
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

interface DepartmentDetailProps {
  department: Department;
  onBack: () => void;
}

export function DepartmentDetail({ department, onBack }: DepartmentDetailProps) {
  const handleAction = (action: string) => {
    toast.success(`${action}操作已执行`);
  };

  // Mock members data
  const members = [
    {
      id: 'U001',
      name: '张三',
      avatar: 'https://images.unsplash.com/photo-1652795385761-7ac287d0cd03?w=100',
      position: '部门经理',
      role: '管理员',
      joinDate: '2024-01-15',
      email: 'zhangsan@company.com',
    },
    {
      id: 'U002',
      name: '李四',
      avatar: 'https://images.unsplash.com/photo-1652795385761-7ac287d0cd03?w=100',
      position: '高级工程师',
      role: '员工',
      joinDate: '2024-01-20',
      email: 'lisi@company.com',
    },
    {
      id: 'U003',
      name: '王五',
      avatar: 'https://images.unsplash.com/photo-1652795385761-7ac287d0cd03?w=100',
      position: '工程师',
      role: '员工',
      joinDate: '2024-02-01',
      email: 'wangwu@company.com',
    },
    {
      id: 'U004',
      name: '赵六',
      avatar: 'https://images.unsplash.com/photo-1652795385761-7ac287d0cd03?w=100',
      position: '初级工程师',
      role: '员工',
      joinDate: '2024-02-15',
      email: 'zhaoliu@company.com',
    },
  ];

  // Mock sub-departments data
  const subDepartments = [
    {
      id: 'D001-1',
      name: '前端开发组',
      code: 'TECH-FE',
      managerName: '李四',
      userCount: 45,
      status: '已启用',
      createdAt: '2024-01-20',
    },
    {
      id: 'D001-2',
      name: '后端开发组',
      code: 'TECH-BE',
      managerName: '王五',
      userCount: 52,
      status: '已启用',
      createdAt: '2024-01-20',
    },
    {
      id: 'D001-3',
      name: '测试组',
      code: 'TECH-QA',
      managerName: '赵六',
      userCount: 28,
      status: '已启用',
      createdAt: '2024-01-25',
    },
  ];

  // Mock activity logs
  const activityLogs = [
    { id: 'L001', user: '张三', action: '添加了新成员', target: '李四', time: '2024-12-03 14:30' },
    { id: 'L002', user: '李四', action: '更新了部门描述', target: '', time: '2024-12-02 10:15' },
    { id: 'L003', user: '王五', action: '创建了子部门', target: '前端开发组', time: '2024-12-01 16:45' },
    { id: 'L004', user: '张三', action: '调整了部门负责人', target: '李四', time: '2024-11-30 09:20' },
  ];

  // Mock authorized roles data
  const authorizedRoles = [
    { 
      id: 'R001', 
      name: '项目管理员', 
      description: '负责项目的整体管理和协调',
      type: '管理角色',
      permissions: ['查看项目', '编辑项目', '管理成员', '发布公告'],
      grantedAt: '2024-01-15',
      grantedBy: '系统管理员'
    },
    { 
      id: 'R002', 
      name: '开发人员', 
      description: '负责项目开发工作',
      type: '业务角色',
      permissions: ['查看项目', '编辑代码', '提交变更', '查看文档'],
      grantedAt: '2024-01-20',
      grantedBy: '张三'
    },
    { 
      id: 'R003', 
      name: '文档管理员', 
      description: '负责项目文档的编写和维护',
      type: '业务角色',
      permissions: ['查看文档', '编辑文档', '发布文档'],
      grantedAt: '2024-02-01',
      grantedBy: '张三'
    },
  ];

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
    <div className="space-y-6">
      {/* Breadcrumb */}
      <div className="flex items-center gap-2 text-sm">
        <button 
          onClick={onBack}
          className="text-gray-600 dark:text-gray-400 hover:text-blue-600 dark:hover:text-blue-400"
        >
          部门管理
        </button>
        <ChevronRight className="w-4 h-4 text-gray-400" />
        <span className="text-gray-900 dark:text-white">详情</span>
      </div>

      {/* Department Header Card */}
      <Card className="dark:bg-gray-800 dark:border-gray-700">
        <div className="p-6">
          <div className="flex items-start justify-between">
            {/* Left: Department Info */}
            <div className="flex items-start gap-6">
              <div className="w-24 h-24 rounded-xl bg-gradient-to-br from-blue-500 to-blue-600 flex items-center justify-center">
                <Building2 className="w-12 h-12 text-white" />
              </div>
              <div>
                <div className="flex items-center gap-3 mb-2">
                  <h1 className="text-2xl dark:text-white">{department.name}</h1>
                  <Badge 
                    variant={department.status === '已启用' ? 'default' : 'secondary'}
                    className={department.status === '已启用' ? 'bg-green-500 hover:bg-green-600' : ''}
                  >
                    {department.status}
                  </Badge>
                  <Badge variant="outline" className="dark:border-gray-600 dark:text-gray-400">
                    {department.code}
                  </Badge>
                </div>
                <div className="flex items-center gap-6 text-sm mb-4">
                  <div className="flex items-center gap-2">
                    <User className="w-4 h-4 text-gray-400" />
                    <span className="text-gray-600 dark:text-gray-400">
                      负责人: <span className="dark:text-white">{department.managerName}</span>
                    </span>
                  </div>
                  <div className="flex items-center gap-2">
                    <FolderTree className="w-4 h-4 text-gray-400" />
                    <span className="text-gray-600 dark:text-gray-400">
                      层级: <span className="dark:text-white">第 {department.level} 级</span>
                    </span>
                  </div>
                </div>
                <div className="flex items-center gap-6 text-sm">
                  <div className="flex items-center gap-2">
                    <Users className="w-4 h-4 text-gray-400" />
                    <span className="text-gray-600 dark:text-gray-400">
                      <span className="dark:text-white">{department.userCount}</span> 名成员
                    </span>
                  </div>
                  <div className="flex items-center gap-2">
                    <Building2 className="w-4 h-4 text-gray-400" />
                    <span className="text-gray-600 dark:text-gray-400">
                      <span className="dark:text-white">{department.children?.length || 0}</span> 个子部门
                    </span>
                  </div>
                  <div className="flex items-center gap-2">
                    <Calendar className="w-4 h-4 text-gray-400" />
                    <span className="text-gray-600 dark:text-gray-400">
                      创建于 {department.createdAt}
                    </span>
                  </div>
                </div>
              </div>
            </div>

            {/* Right: Action Buttons */}
            <div className="flex gap-2">
              <Button
                variant="outline"
                onClick={() => handleAction('编辑部门')}
                className="dark:border-gray-600 dark:text-gray-300 dark:hover:bg-gray-700"
              >
                <Edit className="w-4 h-4 mr-2" />
                编辑
              </Button>
              <Button
                variant="outline"
                onClick={() => handleAction('删除部门')}
                className="border-red-200 text-red-600 hover:bg-red-50 dark:border-red-800 dark:text-red-400 dark:hover:bg-red-900/20"
              >
                <Trash2 className="w-4 h-4 mr-2" />
                删除
              </Button>
            </div>
          </div>
        </div>
      </Card>

      {/* Tabs Section */}
      <Tabs defaultValue="members" className="space-y-6">
        <TabsList className="dark:bg-gray-800">
          <TabsTrigger value="members">
            <Users className="w-4 h-4 mr-2" />
            成员列表
          </TabsTrigger>
          <TabsTrigger value="subdepartments">
            <Building2 className="w-4 h-4 mr-2" />
            子部门
          </TabsTrigger>
          <TabsTrigger value="roles">
            <Crown className="w-4 h-4 mr-2" />
            授权角色
          </TabsTrigger>
          <TabsTrigger value="activity">
            <Activity className="w-4 h-4 mr-2" />
            活动记录
          </TabsTrigger>
        </TabsList>

        {/* Members Tab */}
        <TabsContent value="members">
          <Card className="dark:bg-gray-800 dark:border-gray-700">
            <div className="p-6">
              <div className="flex items-center justify-between mb-6">
                <div>
                  <h3 className="text-base dark:text-white mb-1">部门成员</h3>
                  <p className="text-sm text-gray-600 dark:text-gray-400">该部门的所有成员列表</p>
                </div>
                <Button
                  size="sm"
                  onClick={() => handleAction('添加成员')}
                  className="bg-blue-600 hover:bg-blue-700 text-white"
                >
                  <UserPlus className="w-4 h-4 mr-2" />
                  添加成员
                </Button>
              </div>
              <div className="space-y-3">
                {members.map((member) => (
                  <div
                    key={member.id}
                    className="flex items-center justify-between p-4 rounded-lg border dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-750"
                  >
                    <div className="flex items-center gap-4">
                      <Avatar className="w-12 h-12">
                        <AvatarImage src={member.avatar} alt={member.name} />
                        <AvatarFallback className="bg-blue-500 text-white">
                          {member.name.slice(0, 2)}
                        </AvatarFallback>
                      </Avatar>
                      <div>
                        <div className="flex items-center gap-2">
                          <span className="dark:text-white">{member.name}</span>
                          {member.role === '管理员' && (
                            <Badge className="bg-gradient-to-r from-blue-500 to-blue-600 text-white border-0">
                              {member.role}
                            </Badge>
                          )}
                        </div>
                        <div className="flex items-center gap-4 text-sm text-gray-600 dark:text-gray-400 mt-1">
                          <span>{member.position}</span>
                          <span>•</span>
                          <span>{member.email}</span>
                          <span>•</span>
                          <span>加入于 {member.joinDate}</span>
                        </div>
                      </div>
                    </div>
                    {member.role !== '管理员' && (
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => handleAction(`移除成员 ${member.name}`)}
                        className="text-red-600 hover:text-red-700 hover:bg-red-50 dark:text-red-400 dark:hover:bg-red-900/20"
                      >
                        <Trash2 className="w-4 h-4 mr-2" />
                        移除
                      </Button>
                    )}
                  </div>
                ))}
              </div>
            </div>
          </Card>
        </TabsContent>

        {/* Sub-departments Tab */}
        <TabsContent value="subdepartments">
          <Card className="dark:bg-gray-800 dark:border-gray-700">
            <div className="p-6">
              <div className="flex items-center justify-between mb-6">
                <div>
                  <h3 className="text-base dark:text-white mb-1">子部门</h3>
                  <p className="text-sm text-gray-600 dark:text-gray-400">该部门的所有子部门列表</p>
                </div>
                <Button
                  size="sm"
                  onClick={() => handleAction('添加子部门')}
                  className="bg-blue-600 hover:bg-blue-700 text-white"
                >
                  <Building2 className="w-4 h-4 mr-2" />
                  添加子部门
                </Button>
              </div>
              <div className="space-y-3">
                {subDepartments.length > 0 ? (
                  subDepartments.map((subDept) => (
                    <div
                      key={subDept.id}
                      className="flex items-center justify-between p-4 rounded-lg border dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-750"
                    >
                      <div className="flex items-center gap-4">
                        <div className="w-10 h-10 rounded-lg bg-blue-100 dark:bg-blue-900/30 flex items-center justify-center">
                          <Building2 className="w-5 h-5 text-blue-600 dark:text-blue-400" />
                        </div>
                        <div>
                          <div className="flex items-center gap-2">
                            <span className="dark:text-white">{subDept.name}</span>
                            <Badge variant="outline" className="dark:border-gray-600 dark:text-gray-400">
                              {subDept.code}
                            </Badge>
                            <Badge 
                              variant={subDept.status === '已启用' ? 'default' : 'secondary'}
                              className={subDept.status === '已启用' ? 'bg-green-500 hover:bg-green-600 text-xs' : 'text-xs'}
                            >
                              {subDept.status}
                            </Badge>
                          </div>
                          <div className="flex items-center gap-4 text-sm text-gray-600 dark:text-gray-400 mt-1">
                            <span>负责人: {subDept.managerName}</span>
                            <span>•</span>
                            <span>{subDept.userCount} 人</span>
                            <span>•</span>
                            <span>创建于 {subDept.createdAt}</span>
                          </div>
                        </div>
                      </div>
                      <div className="flex gap-2">
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => handleAction(`查看部门 ${subDept.name}`)}
                          className="dark:text-gray-300 dark:hover:bg-gray-700"
                        >
                          查看
                        </Button>
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => handleAction(`删除部门 ${subDept.name}`)}
                          className="text-red-600 hover:text-red-700 hover:bg-red-50 dark:text-red-400 dark:hover:bg-red-900/20"
                        >
                          <Trash2 className="w-4 h-4 mr-2" />
                          删除
                        </Button>
                      </div>
                    </div>
                  ))
                ) : (
                  <div className="text-center py-12">
                    <Building2 className="w-12 h-12 text-gray-400 mx-auto mb-3" />
                    <p className="text-gray-600 dark:text-gray-400">暂无子部门</p>
                  </div>
                )}
              </div>
            </div>
          </Card>
        </TabsContent>

        {/* Roles Tab */}
        <TabsContent value="roles">
          <Card className="dark:bg-gray-800 dark:border-gray-700">
            <div className="p-6">
              <div className="flex items-center justify-between mb-6">
                <div>
                  <h3 className="text-base dark:text-white mb-1">授权角色</h3>
                  <p className="text-sm text-gray-600 dark:text-gray-400">该部门拥有的授权角色列表</p>
                </div>
                <Button
                  size="sm"
                  onClick={() => handleAction('分配角色')}
                  className="bg-blue-600 hover:bg-blue-700 text-white"
                >
                  <Crown className="w-4 h-4 mr-2" />
                  分配角色
                </Button>
              </div>
              <div className="space-y-3">
                {authorizedRoles.map((role) => (
                  <div
                    key={role.id}
                    className="flex items-center justify-between p-4 rounded-lg border dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-750"
                  >
                    <div className="flex items-center gap-4">
                      <div className="w-10 h-10 rounded-lg bg-blue-100 dark:bg-blue-900/30 flex items-center justify-center">
                        <Crown className="w-5 h-5 text-blue-600 dark:text-blue-400" />
                      </div>
                      <div>
                        <div className="dark:text-white">{role.name}</div>
                        <div className="flex items-center gap-4 text-sm text-gray-600 dark:text-gray-400 mt-1">
                          <span>类型: {role.type}</span>
                          <span>•</span>
                          <span>授予于 {role.grantedAt}</span>
                        </div>
                      </div>
                    </div>
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={() => handleAction(`撤销角色 ${role.name}`)}
                      className="text-red-600 hover:text-red-700 hover:bg-red-50 dark:text-red-400 dark:hover:bg-red-900/20"
                    >
                      撤销
                    </Button>
                  </div>
                ))}
              </div>
            </div>
          </Card>
        </TabsContent>

        {/* Activity Tab */}
        <TabsContent value="activity">
          <Card className="dark:bg-gray-800 dark:border-gray-700">
            <div className="p-6">
              <div className="mb-6">
                <h3 className="text-base dark:text-white mb-1">活动记录</h3>
                <p className="text-sm text-gray-600 dark:text-gray-400">该部门的最近活动记录</p>
              </div>
              <div className="space-y-4">
                {activityLogs.map((log, index) => (
                  <div key={log.id} className="flex gap-4">
                    <div className="flex flex-col items-center">
                      <div className="w-8 h-8 rounded-full bg-blue-100 dark:bg-blue-900/30 flex items-center justify-center">
                        <Activity className="w-4 h-4 text-blue-600 dark:text-blue-400" />
                      </div>
                      {index < activityLogs.length - 1 && (
                        <div className="w-px flex-1 bg-gray-200 dark:bg-gray-700 mt-2"></div>
                      )}
                    </div>
                    <div className="flex-1 pb-4">
                      <div className="flex items-center gap-2 mb-1">
                        <span className="dark:text-white">{log.user}</span>
                        <span className="text-gray-600 dark:text-gray-400">{log.action}</span>
                        {log.target && (
                          <>
                            <span className="text-gray-600 dark:text-gray-400">:</span>
                            <span className="text-blue-600 dark:text-blue-400">{log.target}</span>
                          </>
                        )}
                      </div>
                      <div className="text-sm text-gray-500 dark:text-gray-500">
                        {log.time}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  );
}