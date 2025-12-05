import { ChevronRight, Edit, Trash2, UserPlus, Shield, Tag, Calendar, Users, Clock, FileText, Activity, Crown } from 'lucide-react';
import { Button } from '@/components/ui/button';  
import { Card } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { toast } from 'sonner';
import { useState } from 'react';
import { GroupDialog, AddGroupMemberDialog } from '@/pages/groups/GroupDialog';
import { ConfirmDialog } from '@/components/gm/ConfirmDialog';
import { AssignRoleDialog } from '@/components/gm/AssignRoleDialog';

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

interface GroupDetailProps {
  group: Group;
  onBack: () => void;
  onViewUser?: (userId: string) => void;
}

export function GroupDetail({ group, onBack, onViewUser }: GroupDetailProps) {
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [addMemberDialogOpen, setAddMemberDialogOpen] = useState(false);
  const [removeMemberDialogOpen, setRemoveMemberDialogOpen] = useState(false);
  const [assignRoleDialogOpen, setAssignRoleDialogOpen] = useState(false);
  const [revokeRoleDialogOpen, setRevokeRoleDialogOpen] = useState(false);
  const [selectedMember, setSelectedMember] = useState<{ id: string; name: string } | null>(null);
  const [selectedRole, setSelectedRole] = useState<{ id: string; name: string } | null>(null);

  const handleAction = (action: string) => {
    toast.success(`${action}操作已执行`);
  };

  const getTypeColor = (type: string) => {
    switch (type) {
      case '项目组':
        return 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400';
      case '职能组':
        return 'bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-400';
      case '临时组':
        return 'bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-400';
      default:
        return 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-400';
    }
  };

  // Mock members data
  const members = [
    {
      id: 'U001',
      name: '张三',
      avatar: 'https://images.unsplash.com/photo-1652795385761-7ac287d0cd03?w=100',
      role: '负责人',
      department: '技术部',
      joinDate: '2024-01-15',
    },
    {
      id: 'U002',
      name: '李四',
      avatar: 'https://images.unsplash.com/photo-1652795385761-7ac287d0cd03?w=100',
      role: '成员',
      department: '产品部',
      joinDate: '2024-01-20',
    },
    {
      id: 'U003',
      name: '王五',
      avatar: 'https://images.unsplash.com/photo-1652795385761-7ac287d0cd03?w=100',
      role: '成员',
      department: '技术部',
      joinDate: '2024-02-01',
    },
    {
      id: 'U004',
      name: '赵六',
      avatar: 'https://images.unsplash.com/photo-1652795385761-7ac287d0cd03?w=100',
      role: '成员',
      department: '设计部',
      joinDate: '2024-02-15',
    },
  ];

  // Mock permissions data
  const permissions = [
    { id: 'P001', name: '查看项目文档', scope: '全部', grantedAt: '2024-01-15' },
    { id: 'P002', name: '编辑项目文档', scope: '全部', grantedAt: '2024-01-15' },
    { id: 'P003', name: '管理组成员', scope: '负责人', grantedAt: '2024-01-15' },
    { id: 'P004', name: '发布公告', scope: '全部', grantedAt: '2024-01-20' },
  ];

  // Mock activity logs
  const activityLogs = [
    { id: 'L001', user: '张三', action: '添加了新成员', target: '李四', time: '2024-12-03 14:30' },
    { id: 'L002', user: '李四', action: '更新了组描述', target: '', time: '2024-12-02 10:15' },
    { id: 'L003', user: '王五', action: '发布了公告', target: '项目进度更新', time: '2024-12-01 16:45' },
    { id: 'L004', user: '张三', action: '移除了成员', target: '孙七', time: '2024-11-30 09:20' },
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
          组管理
        </button>
        <ChevronRight className="w-4 h-4 text-gray-400" />
        <span className="text-gray-900 dark:text-white">详情</span>
      </div>

      {/* Group Header Card */}
      <Card className="dark:bg-gray-800 dark:border-gray-700">
        <div className="p-6">
          <div className="flex items-start justify-between">
            {/* Left: Group Info */}
            <div className="flex items-start gap-6">
              <div className="w-24 h-24 rounded-xl bg-gradient-to-br from-blue-500 to-blue-600 flex items-center justify-center">
                <Users className="w-12 h-12 text-white" />
              </div>
              <div>
                <div className="flex items-center gap-3 mb-2">
                  <h1 className="text-2xl dark:text-white">{group.name}</h1>
                  <Badge className={`${getTypeColor(group.type)} border-0`}>
                    {group.type}
                  </Badge>
                  <Badge 
                    variant={group.status === '活跃' ? 'default' : 'secondary'}
                    className={group.status === '活跃' ? 'bg-green-500 hover:bg-green-600' : ''}
                  >
                    {group.status}
                  </Badge>
                </div>
                <p className="text-gray-600 dark:text-gray-400 mb-4">
                  {group.description}
                </p>
                <div className="flex items-center gap-6 text-sm">
                  <div className="flex items-center gap-2">
                    <Users className="w-4 h-4 text-gray-400" />
                    <span className="text-gray-600 dark:text-gray-400">
                      <span className="dark:text-white">{group.memberCount}</span> 名成员
                    </span>
                  </div>
                  <div className="flex items-center gap-2">
                    <Calendar className="w-4 h-4 text-gray-400" />
                    <span className="text-gray-600 dark:text-gray-400">
                      创建于 {group.createdAt}
                    </span>
                  </div>
                  <div className="flex items-center gap-2">
                    <Clock className="w-4 h-4 text-gray-400" />
                    <span className="text-gray-600 dark:text-gray-400">
                      最后活跃 {group.lastActive}
                    </span>
                  </div>
                </div>
              </div>
            </div>

            {/* Right: Action Buttons */}
            <div className="flex gap-2">
              <Button
                variant="outline"
                onClick={() => setEditDialogOpen(true)}
                className="dark:border-gray-600 dark:text-gray-300 dark:hover:bg-gray-700"
              >
                <Edit className="w-4 h-4 mr-2" />
                ��辑
              </Button>
              <Button
                variant="outline"
                onClick={() => setDeleteDialogOpen(true)}
                className="border-red-200 text-red-600 hover:bg-red-50 dark:border-red-800 dark:text-red-400 dark:hover:bg-red-900/20"
              >
                <Trash2 className="w-4 h-4 mr-2" />
                删除
              </Button>
            </div>
          </div>

          {/* Tags */}
          {group.tags.length > 0 && (
            <div className="mt-4 pt-4 border-t dark:border-gray-700">
              <div className="flex items-center gap-2">
                <Tag className="w-4 h-4 text-gray-400" />
                <span className="text-sm text-gray-600 dark:text-gray-400">标签:</span>
                <div className="flex flex-wrap gap-2">
                  {group.tags.map((tag, index) => (
                    <Badge key={index} variant="outline" className="dark:border-gray-600 dark:text-gray-400">
                      {tag}
                    </Badge>
                  ))}
                </div>
              </div>
            </div>
          )}
        </div>
      </Card>

      {/* Tabs Section */}
      <Tabs defaultValue="members" className="space-y-6">
        <TabsList className="dark:bg-gray-800">
          <TabsTrigger value="members">
            <Users className="w-4 h-4 mr-2" />
            成员列表
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
                  <h3 className="text-base dark:text-white mb-1">组成员</h3>
                  <p className="text-sm text-gray-600 dark:text-gray-400">该组的所有成员列表</p>
                </div>
                <Button
                  size="sm"
                  onClick={() => setAddMemberDialogOpen(true)}
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
                          <button
                            onClick={() => onViewUser?.(member.id)}
                            className="dark:text-white hover:text-blue-600 dark:hover:text-blue-400 transition-colors"
                          >
                            {member.name}
                          </button>
                          {member.role === '负责人' && (
                            <Badge className="bg-gradient-to-r from-blue-500 to-blue-600 text-white border-0">
                              负责人
                            </Badge>
                          )}
                        </div>
                        <div className="flex items-center gap-4 text-sm text-gray-600 dark:text-gray-400 mt-1">
                          <span>{member.department}</span>
                          <span>•</span>
                          <span>加入于 {member.joinDate}</span>
                        </div>
                      </div>
                    </div>
                    {member.role !== '负责人' && (
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => {
                          setSelectedMember({ id: member.id, name: member.name });
                          setRemoveMemberDialogOpen(true);
                        }}
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

        {/* Roles Tab */}
        <TabsContent value="roles">
          <Card className="dark:bg-gray-800 dark:border-gray-700">
            <div className="p-6">
              <div className="flex items-center justify-between mb-6">
                <div>
                  <h3 className="text-base dark:text-white mb-1">授权角色</h3>
                  <p className="text-sm text-gray-600 dark:text-gray-400">该组拥有的授权角色列表</p>
                </div>
                <Button
                  size="sm"
                  onClick={() => setAssignRoleDialogOpen(true)}
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
                      onClick={() => {
                        setSelectedRole({ id: role.id, name: role.name });
                        setRevokeRoleDialogOpen(true);
                      }}
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
                <p className="text-sm text-gray-600 dark:text-gray-400">该组的最近活动记录</p>
              </div>
              <div className="space-y-4">
                {activityLogs.map((log) => (
                  <div key={log.id} className="flex gap-4">
                    <div className="flex flex-col items-center">
                      <div className="w-8 h-8 rounded-full bg-blue-100 dark:bg-blue-900/30 flex items-center justify-center">
                        <Activity className="w-4 h-4 text-blue-600 dark:text-blue-400" />
                      </div>
                      <div className="w-px flex-1 bg-gray-200 dark:bg-gray-700 mt-2"></div>
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

      {/* Dialogs */}
      <GroupDialog
        open={editDialogOpen}
        onOpenChange={setEditDialogOpen}
        mode="edit"
        group={group}
      />
      
      <ConfirmDialog
        open={deleteDialogOpen}
        onOpenChange={setDeleteDialogOpen}
        title="确认删除组"
        description={`确定要删除组 "${group.name}" 吗？此操作不可撤销，组内所有成员关系也将被清除。`}
        onConfirm={() => {
          toast.success(`已删除组: ${group.name}`);
          setDeleteDialogOpen(false);
          setTimeout(() => onBack(), 500);
        }}
        confirmText="删除"
        variant="danger"
      />
      
      <AddGroupMemberDialog
        open={addMemberDialogOpen}
        onOpenChange={setAddMemberDialogOpen}
        groupName={group.name}
      />
      
      <ConfirmDialog
        open={removeMemberDialogOpen}
        onOpenChange={setRemoveMemberDialogOpen}
        title="确认移除成员"
        description={`确定要将 "${selectedMember?.name}" 从组 "${group.name}" 中移除吗？该成员将失去组内的所有权限。`}
        onConfirm={() => {
          toast.success(`已移除成员: ${selectedMember?.name}`);
          setRemoveMemberDialogOpen(false);
          setSelectedMember(null);
        }}
        confirmText="移除"
        variant="danger"
      />
      
      <AssignRoleDialog
        open={assignRoleDialogOpen}
        onOpenChange={setAssignRoleDialogOpen}
        targetType="group"
        targetName={group.name}
      />
      
      <ConfirmDialog
        open={revokeRoleDialogOpen}
        onOpenChange={setRevokeRoleDialogOpen}
        title="确认撤销角色"
        description={`确定要撤销组 "${group.name}" 的 "${selectedRole?.name}" 角色吗？撤销后该组将失去该角色的所有权限。`}
        onConfirm={() => {
          toast.success(`已撤销角色: ${selectedRole?.name}`);
          setRevokeRoleDialogOpen(false);
          setSelectedRole(null);
        }}
        confirmText="撤销"
        variant="danger"
      />
    </div>
  );
}