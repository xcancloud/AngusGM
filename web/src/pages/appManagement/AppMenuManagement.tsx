import { useState } from 'react';
import { ArrowLeft, Plus, Edit, Trash2, ChevronRight, ChevronDown, Menu, MousePointer, BarChart3, FolderTree } from 'lucide-react';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Badge } from '@/components/ui/badge';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { toast } from 'sonner';
import { Textarea } from '@/components/ui/textarea';

interface Application {
  id: string;
  code: string;
  name: string;
  displayName: string;
}

interface TreeNode {
  id: string;
  name: string;
  code: string;
  type: '菜单' | '按钮' | '看板';
  parentId: string | null;
  sortOrder: number;
  icon?: string;
  url?: string;
  status: '启用' | '禁用';
  description?: string;
  config?: string; // 看板配置
  children?: TreeNode[];
  expanded?: boolean;
}

interface AppMenuManagementProps {
  app: Application;
  onBack: () => void;
}

export function AppMenuManagement({ app, onBack }: AppMenuManagementProps) {
  const [treeData, setTreeData] = useState<TreeNode[]>([
    {
      id: 'n1',
      name: '首页',
      code: 'HOME',
      type: '菜单',
      parentId: null,
      sortOrder: 1,
      icon: 'Home',
      url: '/home',
      status: '启用',
      description: '应用首页',
      expanded: true,
      children: [
        {
          id: 'n1-1',
          name: '数据概览',
          code: 'HOME_OVERVIEW',
          type: '菜单',
          parentId: 'n1',
          sortOrder: 1,
          icon: 'BarChart',
          url: '/home/overview',
          status: '启用',
          description: '数据统计概览',
          expanded: true,
          children: [
            {
              id: 'n1-1-1',
              name: '新增数据',
              code: 'HOME_OVERVIEW_ADD',
              type: '按钮',
              parentId: 'n1-1',
              sortOrder: 1,
              status: '启用',
              description: '新增数据按钮',
            },
            {
              id: 'n1-1-2',
              name: '导出报表',
              code: 'HOME_OVERVIEW_EXPORT',
              type: '按钮',
              parentId: 'n1-1',
              sortOrder: 2,
              status: '启用',
              description: '导出报表按钮',
            },
            {
              id: 'n1-1-3',
              name: '用户统计看板',
              code: 'HOME_OVERVIEW_USER_STATS',
              type: '看板',
              parentId: 'n1-1',
              sortOrder: 3,
              status: '启用',
              description: '展示用户统计数据',
              config: '{"chart":"bar","data":"users"}',
            },
          ],
        },
        {
          id: 'n1-2',
          name: '快速操作',
          code: 'HOME_QUICK',
          type: '菜单',
          parentId: 'n1',
          sortOrder: 2,
          icon: 'Zap',
          url: '/home/quick',
          status: '启用',
          description: '常用快捷操作',
        },
      ],
    },
    {
      id: 'n2',
      name: '用户管理',
      code: 'USER',
      type: '菜单',
      parentId: null,
      sortOrder: 2,
      icon: 'Users',
      url: '/users',
      status: '启用',
      description: '用户信息管理',
      expanded: true,
      children: [
        {
          id: 'n2-1',
          name: '用户列表',
          code: 'USER_LIST',
          type: '菜单',
          parentId: 'n2',
          sortOrder: 1,
          icon: 'List',
          url: '/users/list',
          status: '启用',
          description: '查看用户列表',
          children: [
            {
              id: 'n2-1-1',
              name: '新增用户',
              code: 'USER_ADD',
              type: '按钮',
              parentId: 'n2-1',
              sortOrder: 1,
              status: '启用',
              description: '创建新用户',
            },
            {
              id: 'n2-1-2',
              name: '编辑用户',
              code: 'USER_EDIT',
              type: '按钮',
              parentId: 'n2-1',
              sortOrder: 2,
              status: '启用',
              description: '编辑用户信息',
            },
            {
              id: 'n2-1-3',
              name: '删除用户',
              code: 'USER_DELETE',
              type: '按钮',
              parentId: 'n2-1',
              sortOrder: 3,
              status: '启用',
              description: '删除用户',
            },
            {
              id: 'n2-1-4',
              name: '活跃用户看板',
              code: 'USER_ACTIVE_BOARD',
              type: '看板',
              parentId: 'n2-1',
              sortOrder: 4,
              status: '启用',
              description: '展示活跃用户数据',
              config: '{"chart":"line","data":"active_users"}',
            },
          ],
        },
        {
          id: 'n2-2',
          name: '角色管理',
          code: 'USER_ROLE',
          type: '菜单',
          parentId: 'n2',
          sortOrder: 2,
          icon: 'Shield',
          url: '/users/roles',
          status: '启用',
          description: '管理用户角色',
        },
      ],
    },
    {
      id: 'n3',
      name: '系统设置',
      code: 'SYSTEM',
      type: '菜单',
      parentId: null,
      sortOrder: 3,
      icon: 'Settings',
      url: '/system',
      status: '启用',
      description: '系统配置管理',
    },
  ]);

  const [showEditDialog, setShowEditDialog] = useState(false);
  const [editingNode, setEditingNode] = useState<TreeNode | null>(null);
  const [parentNode, setParentNode] = useState<TreeNode | null>(null);
  const [formData, setFormData] = useState<Partial<TreeNode>>({});

  // 获取节点类型图标
  const getTypeIcon = (type: string) => {
    switch (type) {
      case '菜单':
        return <Menu className="w-4 h-4 text-blue-500" />;
      case '按钮':
        return <MousePointer className="w-4 h-4 text-purple-500" />;
      case '看板':
        return <BarChart3 className="w-4 h-4 text-green-500" />;
      default:
        return <Menu className="w-4 h-4 text-gray-500" />;
    }
  };

  // 获取节点类型标签
  const getTypeBadge = (type: string) => {
    const configs = {
      '菜单': 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400',
      '按钮': 'bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-400',
      '看板': 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400',
    };
    return <Badge className={`${configs[type as keyof typeof configs]} border-0 text-xs`}>{type}</Badge>;
  };

  // 获取状态标签
  const getStatusBadge = (status: string) => {
    return status === '启用' ? (
      <Badge className="bg-emerald-100 text-emerald-700 dark:bg-emerald-900/30 dark:text-emerald-400 border-0 text-xs">
        启用
      </Badge>
    ) : (
      <Badge className="bg-gray-100 text-gray-700 dark:bg-gray-700 dark:text-gray-400 border-0 text-xs">
        禁用
      </Badge>
    );
  };

  // 切换节点展开/收起
  const toggleNode = (nodeId: string, nodes: TreeNode[]): TreeNode[] => {
    return nodes.map(node => {
      if (node.id === nodeId) {
        return { ...node, expanded: !node.expanded };
      }
      if (node.children) {
        return { ...node, children: toggleNode(nodeId, node.children) };
      }
      return node;
    });
  };

  const handleToggle = (nodeId: string) => {
    setTreeData(toggleNode(nodeId, treeData));
  };

  // 添加节点
  const handleAdd = (parent: TreeNode | null) => {
    setEditingNode(null);
    setParentNode(parent);
    setFormData({
      name: '',
      code: '',
      type: parent ? '菜单' : '菜单', // 如果有父节点，可以选择类型；顶层只能是菜单
      status: '启用',
      sortOrder: parent 
        ? (parent.children?.length || 0) + 1 
        : treeData.length + 1,
      parentId: parent?.id || null,
    });
    setShowEditDialog(true);
  };

  // 编辑节点
  const handleEdit = (node: TreeNode, parent: TreeNode | null) => {
    setEditingNode(node);
    setParentNode(parent);
    setFormData({ ...node });
    setShowEditDialog(true);
  };

  // 删除节点
  const handleDelete = (nodeId: string, nodes: TreeNode[]): TreeNode[] => {
    return nodes.filter(node => {
      if (node.id === nodeId) {
        return false;
      }
      if (node.children) {
        node.children = handleDelete(nodeId, node.children);
      }
      return true;
    });
  };

  const confirmDelete = (node: TreeNode) => {
    if (node.children && node.children.length > 0) {
      toast.error('请先删除子节点');
      return;
    }
    if (confirm(`确定要删除"${node.name}"吗？`)) {
      setTreeData(handleDelete(node.id, treeData));
      toast.success('节点已删除');
    }
  };

  // 保存节点
  const handleSave = () => {
    if (!formData.name || !formData.code) {
      toast.error('请填写必填字段');
      return;
    }

    if (editingNode) {
      // 更新节点
      const updateNode = (nodes: TreeNode[]): TreeNode[] => {
        return nodes.map(node => {
          if (node.id === editingNode.id) {
            return { ...node, ...formData } as TreeNode;
          }
          if (node.children) {
            return { ...node, children: updateNode(node.children) };
          }
          return node;
        });
      };
      setTreeData(updateNode(treeData));
      toast.success('节点已更新');
    } else {
      // 添加新节点
      const newNode: TreeNode = {
        id: `n${Date.now()}`,
        ...formData as TreeNode,
        children: formData.type === '菜单' ? [] : undefined,
      };

      if (parentNode) {
        // 添加到父节点
        const addToParent = (nodes: TreeNode[]): TreeNode[] => {
          return nodes.map(node => {
            if (node.id === parentNode.id) {
              return {
                ...node,
                children: [...(node.children || []), newNode],
                expanded: true,
              };
            }
            if (node.children) {
              return { ...node, children: addToParent(node.children) };
            }
            return node;
          });
        };
        setTreeData(addToParent(treeData));
      } else {
        // 添加到根节点
        setTreeData([...treeData, newNode]);
      }
      toast.success('节点已添加');
    }

    setShowEditDialog(false);
    setFormData({});
  };

  // 渲染树节点
  const renderTreeNode = (node: TreeNode, level = 0, parent: TreeNode | null = null) => {
    const hasChildren = node.children && node.children.length > 0;
    const canHaveChildren = node.type === '菜单';

    return (
      <div key={node.id}>
        <div
          className={`flex items-center justify-between p-3 hover:bg-gray-50 dark:hover:bg-gray-700/50 rounded-lg group ${
            level > 0 ? 'ml-6' : ''
          }`}
          style={{ marginLeft: level > 0 ? `${level * 24}px` : '0' }}
        >
          <div className="flex items-center gap-3 flex-1 min-w-0">
            {/* 展开/收起按钮 */}
            {canHaveChildren && (
              <button
                onClick={() => handleToggle(node.id)}
                className="flex-shrink-0 w-5 h-5 flex items-center justify-center hover:bg-gray-200 dark:hover:bg-gray-600 rounded"
              >
                {hasChildren && (
                  node.expanded ? (
                    <ChevronDown className="w-4 h-4 text-gray-500" />
                  ) : (
                    <ChevronRight className="w-4 h-4 text-gray-500" />
                  )
                )}
              </button>
            )}
            {!canHaveChildren && <div className="w-5" />}

            {/* 节点图标 */}
            <div className="flex-shrink-0">
              {getTypeIcon(node.type)}
            </div>

            {/* 节点信息 */}
            <div className="flex-1 min-w-0">
              <div className="flex items-center gap-2 flex-wrap">
                <span className="dark:text-white truncate">{node.name}</span>
                {getTypeBadge(node.type)}
                {getStatusBadge(node.status)}
              </div>
              <div className="text-xs text-gray-500 dark:text-gray-400 mt-1 flex items-center gap-3">
                <span className="truncate">{node.code}</span>
                {node.url && (
                  <>
                    <span>·</span>
                    <span className="truncate">{node.url}</span>
                  </>
                )}
                {node.description && (
                  <>
                    <span>·</span>
                    <span className="truncate">{node.description}</span>
                  </>
                )}
              </div>
            </div>
          </div>

          {/* 操作按钮 */}
          <div className="flex items-center gap-1 flex-shrink-0 opacity-0 group-hover:opacity-100 transition-opacity">
            {canHaveChildren && (
              <Button
                variant="ghost"
                size="sm"
                onClick={() => handleAdd(node)}
                className="dark:hover:bg-gray-600 h-8 px-2"
                title="添加子节点"
              >
                <Plus className="w-4 h-4" />
              </Button>
            )}
            <Button
              variant="ghost"
              size="sm"
              onClick={() => handleEdit(node, parent)}
              className="dark:hover:bg-gray-600 h-8 px-2"
              title="编辑"
            >
              <Edit className="w-4 h-4" />
            </Button>
            <Button
              variant="ghost"
              size="sm"
              onClick={() => confirmDelete(node)}
              className="dark:hover:bg-gray-600 text-red-600 dark:text-red-400 h-8 px-2"
              title="删除"
            >
              <Trash2 className="w-4 h-4" />
            </Button>
          </div>
        </div>

        {/* 子节点 */}
        {hasChildren && node.expanded && (
          <div className="mt-1">
            {node.children!.map(child => renderTreeNode(child, level + 1, node))}
          </div>
        )}
      </div>
    );
  };

  return (
    <div className="space-y-6">
      {/* 页面标题 */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <Button
            variant="ghost"
            size="sm"
            onClick={onBack}
            className="dark:hover:bg-gray-700"
          >
            <ArrowLeft className="w-4 h-4 mr-2" />
            返回
          </Button>
          <div>
            <h2 className="text-2xl dark:text-white">应用配置</h2>
            <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
              {app.name} - 菜单、按钮、看板树形管理
            </p>
          </div>
        </div>

        <Button
          size="sm"
          className="bg-blue-600 hover:bg-blue-700"
          onClick={() => handleAdd(null)}
        >
          <Plus className="w-4 h-4 mr-2" />
          添加顶层菜单
        </Button>
      </div>

      {/* 提示信息 */}
      <div className="bg-blue-50 dark:bg-blue-900/20 border border-blue-200 dark:border-blue-800 rounded-lg p-4">
        <div className="flex items-start gap-3">
          <FolderTree className="w-5 h-5 text-blue-600 dark:text-blue-400 mt-0.5 flex-shrink-0" />
          <div className="flex-1 text-sm text-blue-900 dark:text-blue-300">
            <div className="mb-2">
              <strong>树形结构说明：</strong>
            </div>
            <ul className="space-y-1 list-disc list-inside">
              <li><strong>菜单</strong>：可以包含子节点（菜单、按钮、看板），支持多级嵌套</li>
              <li><strong>按钮</strong>：必须作为菜单的子节点，不能包含子节点，用于权限控制</li>
              <li><strong>看板</strong>：必须作为菜单的子节点，不能包含子节点，用于数据展示</li>
            </ul>
          </div>
        </div>
      </div>

      {/* 树形结构 */}
      <Card className="dark:bg-gray-800 dark:border-gray-700">
        <div className="p-4 border-b dark:border-gray-700">
          <h3 className="dark:text-white flex items-center gap-2">
            <FolderTree className="w-5 h-5" />
            菜单树结构
          </h3>
        </div>
        <div className="p-4">
          {treeData.length === 0 ? (
            <div className="text-center py-12 text-gray-500 dark:text-gray-400">
              <FolderTree className="w-12 h-12 mx-auto mb-3 opacity-50" />
              <p>暂无数据，点击右上角按钮添加菜单</p>
            </div>
          ) : (
            <div className="space-y-1">
              {treeData.map(node => renderTreeNode(node))}
            </div>
          )}
        </div>
      </Card>

      {/* 编辑对话框 */}
      <Dialog open={showEditDialog} onOpenChange={setShowEditDialog}>
        <DialogContent className="max-w-2xl dark:bg-gray-800 dark:border-gray-700" aria-describedby={undefined}>
          <DialogHeader>
            <DialogTitle className="dark:text-white">
              {editingNode ? '编辑节点' : parentNode ? `添加子节点（父节点：${parentNode.name}）` : '添加顶层菜单'}
            </DialogTitle>
          </DialogHeader>

          <div className="grid grid-cols-2 gap-4 max-h-[60vh] overflow-y-auto">
            <div>
              <Label className="dark:text-gray-300">类型 *</Label>
              <Select
                value={formData.type}
                onValueChange={(value) => setFormData({ ...formData, type: value as any })}
                disabled={!parentNode} // 顶层只能是菜单
              >
                <SelectTrigger className="mt-2 dark:bg-gray-900 dark:border-gray-700">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="菜单">
                    <div className="flex items-center gap-2">
                      <Menu className="w-4 h-4 text-blue-500" />
                      菜单
                    </div>
                  </SelectItem>
                  {parentNode && (
                    <>
                      <SelectItem value="按钮">
                        <div className="flex items-center gap-2">
                          <MousePointer className="w-4 h-4 text-purple-500" />
                          按钮
                        </div>
                      </SelectItem>
                      <SelectItem value="看板">
                        <div className="flex items-center gap-2">
                          <BarChart3 className="w-4 h-4 text-green-500" />
                          看板
                        </div>
                      </SelectItem>
                    </>
                  )}
                </SelectContent>
              </Select>
              {!parentNode && (
                <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">
                  顶层节点只能是菜单类型
                </p>
              )}
            </div>

            <div>
              <Label className="dark:text-gray-300">名称 *</Label>
              <Input
                value={formData.name || ''}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                className="mt-2 dark:bg-gray-900 dark:border-gray-700"
                placeholder="请输入名称"
              />
            </div>

            <div>
              <Label className="dark:text-gray-300">编码 *</Label>
              <Input
                value={formData.code || ''}
                onChange={(e) => setFormData({ ...formData, code: e.target.value.toUpperCase() })}
                className="mt-2 dark:bg-gray-900 dark:border-gray-700"
                placeholder="请输入编码（大写）"
              />
            </div>

            <div>
              <Label className="dark:text-gray-300">状态</Label>
              <Select
                value={formData.status}
                onValueChange={(value) => setFormData({ ...formData, status: value as any })}
              >
                <SelectTrigger className="mt-2 dark:bg-gray-900 dark:border-gray-700">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="启用">启用</SelectItem>
                  <SelectItem value="禁用">禁用</SelectItem>
                </SelectContent>
              </Select>
            </div>

            <div>
              <Label className="dark:text-gray-300">排序</Label>
              <Input
                type="number"
                value={formData.sortOrder || 0}
                onChange={(e) => setFormData({ ...formData, sortOrder: parseInt(e.target.value) })}
                className="mt-2 dark:bg-gray-900 dark:border-gray-700"
              />
            </div>

            {formData.type === '菜单' && (
              <>
                <div>
                  <Label className="dark:text-gray-300">图标</Label>
                  <Input
                    value={formData.icon || ''}
                    onChange={(e) => setFormData({ ...formData, icon: e.target.value })}
                    className="mt-2 dark:bg-gray-900 dark:border-gray-700"
                    placeholder="例如: Home, Users, Settings"
                  />
                </div>

                <div>
                  <Label className="dark:text-gray-300">URL</Label>
                  <Input
                    value={formData.url || ''}
                    onChange={(e) => setFormData({ ...formData, url: e.target.value })}
                    className="mt-2 dark:bg-gray-900 dark:border-gray-700"
                    placeholder="/path/to/page"
                  />
                </div>
              </>
            )}

            {formData.type === '看板' && (
              <div className="col-span-2">
                <Label className="dark:text-gray-300">看板配置</Label>
                <Textarea
                  value={formData.config || ''}
                  onChange={(e) => setFormData({ ...formData, config: e.target.value })}
                  className="mt-2 dark:bg-gray-900 dark:border-gray-700"
                  placeholder='{"chart":"bar","data":"users"}'
                  rows={3}
                />
              </div>
            )}

            <div className="col-span-2">
              <Label className="dark:text-gray-300">描述</Label>
              <Textarea
                value={formData.description || ''}
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                className="mt-2 dark:bg-gray-900 dark:border-gray-700"
                placeholder="节点描述说明"
                rows={2}
              />
            </div>
          </div>

          <DialogFooter>
            <Button
              variant="outline"
              onClick={() => {
                setShowEditDialog(false);
                setFormData({});
              }}
              className="dark:border-gray-600 dark:hover:bg-gray-700"
            >
              取消
            </Button>
            <Button
              onClick={handleSave}
              className="bg-blue-600 hover:bg-blue-700"
            >
              保存
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
