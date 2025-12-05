import { useState } from 'react';
import { ArrowLeft, Edit, Save, X } from 'lucide-react';
import { Card } from '@/components/ui/card';  
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Badge } from '@/components/ui/badge';
import { Textarea } from '@/components/ui/textarea';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { toast } from 'sonner';

interface Application {
  id: string;
  code: string;
  name: string;
  displayName: string;
  type: '基础应用' | '业务应用' | '系统应用';
  version: string;
  versionType: '云服务版' | '本地版' | '混合版';
  isDefault: boolean;
  shopStatus: string;
  sortOrder: number;
  status: '启用' | '禁用';
  search: string;
  url: string;
  groupId?: string;
  tenantName?: string;
  creator: string;
  createTime: string;
  description: string;
  isInstalled: boolean;
}

interface AppDetailProps {
  app: Application;
  onBack: () => void;
  onUpdate: (app: Application) => void;
}

export function AppDetail({ app, onBack, onUpdate }: AppDetailProps) {
  const [isEditing, setIsEditing] = useState(false);
  const [editFormData, setEditFormData] = useState<Application>(app);

  const handleSave = () => {
    onUpdate(editFormData);
    setIsEditing(false);
    toast.success('应用信息已更新');
  };

  const handleCancel = () => {
    setEditFormData(app);
    setIsEditing(false);
  };

  const getTypeBadge = (type: string) => {
    const configs = {
      '基础应用': 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400',
      '业务应用': 'bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-400',
      '系统应用': 'bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-400',
    };
    return <Badge className={`${configs[type as keyof typeof configs]} border-0`}>{type}</Badge>;
  };

  const getStatusBadge = (status: string) => {
    return status === '启用' ? (
      <Badge className="bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400 border-0">
        启用
      </Badge>
    ) : (
      <Badge className="bg-gray-100 text-gray-700 dark:bg-gray-700 dark:text-gray-400 border-0">
        禁用
      </Badge>
    );
  };

  const getSourceBadge = (isInstalled: boolean) => {
    return isInstalled ? (
      <Badge className="bg-cyan-100 text-cyan-700 dark:bg-cyan-900/30 dark:text-cyan-400 border-0">
        安装应用
      </Badge>
    ) : (
      <Badge className="bg-indigo-100 text-indigo-700 dark:bg-indigo-900/30 dark:text-indigo-400 border-0">
        自定义应用
      </Badge>
    );
  };

  return (
    <div className="space-y-6">
      {/* 页面标题和操作 */}
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
            <h2 className="text-2xl dark:text-white">应用详情</h2>
            <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
              查看和编辑应用信息
            </p>
          </div>
        </div>
        
        {!isEditing ? (
          <Button
            onClick={() => {
              if (app.isInstalled) {
                toast.error('安装应用不允许编辑');
                return;
              }
              setIsEditing(true);
            }}
            className="bg-blue-600 hover:bg-blue-700"
            disabled={app.isInstalled}
          >
            <Edit className="w-4 h-4 mr-2" />
            编辑
          </Button>
        ) : (
          <div className="flex items-center gap-2">
            <Button
              variant="outline"
              onClick={handleCancel}
              className="dark:border-gray-600 dark:hover:bg-gray-700"
            >
              <X className="w-4 h-4 mr-2" />
              取消
            </Button>
            <Button
              onClick={handleSave}
              className="bg-blue-600 hover:bg-blue-700"
            >
              <Save className="w-4 h-4 mr-2" />
              保存
            </Button>
          </div>
        )}
      </div>

      {/* 详情卡片 */}
      <Card className="dark:bg-gray-800 dark:border-gray-700">
        <div className="p-6">
          <div className="grid grid-cols-2 gap-x-12 gap-y-6">
            {/* 左侧 */}
            <div className="space-y-6">
              <div>
                <Label className="text-gray-600 dark:text-gray-400">名称</Label>
                {isEditing ? (
                  <Input
                    value={editFormData.name}
                    onChange={(e) => setEditFormData({ ...editFormData, name: e.target.value })}
                    className="mt-2 dark:bg-gray-900 dark:border-gray-700"
                  />
                ) : (
                  <div className="dark:text-white mt-2">{app.name}</div>
                )}
              </div>

              <div>
                <Label className="text-gray-600 dark:text-gray-400">显示标题</Label>
                {isEditing ? (
                  <Input
                    value={editFormData.displayName}
                    onChange={(e) => setEditFormData({ ...editFormData, displayName: e.target.value })}
                    className="mt-2 dark:bg-gray-900 dark:border-gray-700"
                  />
                ) : (
                  <div className="dark:text-white mt-2">{app.displayName}</div>
                )}
              </div>

              <div>
                <Label className="text-gray-600 dark:text-gray-400">类型</Label>
                {isEditing ? (
                  <Select
                    value={editFormData.type}
                    onValueChange={(value) => setEditFormData({ ...editFormData, type: value as any })}
                  >
                    <SelectTrigger className="mt-2 dark:bg-gray-900 dark:border-gray-700">
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="基础应用">基础应用</SelectItem>
                      <SelectItem value="业务应用">业务应用</SelectItem>
                      <SelectItem value="系统应用">系统应用</SelectItem>
                    </SelectContent>
                  </Select>
                ) : (
                  <div className="mt-2">{getTypeBadge(app.type)}</div>
                )}
              </div>

              <div>
                <Label className="text-gray-600 dark:text-gray-400">版本</Label>
                {isEditing ? (
                  <Input
                    value={editFormData.version}
                    onChange={(e) => setEditFormData({ ...editFormData, version: e.target.value })}
                    className="mt-2 dark:bg-gray-900 dark:border-gray-700"
                  />
                ) : (
                  <div className="dark:text-white mt-2">{app.version}</div>
                )}
              </div>

              <div>
                <Label className="text-gray-600 dark:text-gray-400">版本类型</Label>
                {isEditing ? (
                  <Select
                    value={editFormData.versionType}
                    onValueChange={(value) => setEditFormData({ ...editFormData, versionType: value as any })}
                  >
                    <SelectTrigger className="mt-2 dark:bg-gray-900 dark:border-gray-700">
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="云服务版">云服务版</SelectItem>
                      <SelectItem value="本地版">本地版</SelectItem>
                      <SelectItem value="混合版">混合版</SelectItem>
                    </SelectContent>
                  </Select>
                ) : (
                  <div className="dark:text-white mt-2">{app.versionType}</div>
                )}
              </div>

              <div>
                <Label className="text-gray-600 dark:text-gray-400">是否默认应用</Label>
                <div className="dark:text-white mt-2">{app.isDefault ? '是' : '否'}</div>
              </div>

              <div>
                <Label className="text-gray-600 dark:text-gray-400">开通商店</Label>
                <div className="dark:text-white mt-2">{app.shopStatus}</div>
              </div>

              <div>
                <Label className="text-gray-600 dark:text-gray-400">排序</Label>
                {isEditing ? (
                  <Input
                    type="number"
                    value={editFormData.sortOrder}
                    onChange={(e) => setEditFormData({ ...editFormData, sortOrder: parseInt(e.target.value) })}
                    className="mt-2 dark:bg-gray-900 dark:border-gray-700"
                  />
                ) : (
                  <div className="dark:text-white mt-2">{app.sortOrder}</div>
                )}
              </div>

              <div>
                <Label className="text-gray-600 dark:text-gray-400">创建时间</Label>
                <div className="dark:text-white mt-2">{app.createTime}</div>
              </div>

              <div>
                <Label className="text-gray-600 dark:text-gray-400">描述</Label>
                {isEditing ? (
                  <Textarea
                    value={editFormData.description}
                    onChange={(e) => setEditFormData({ ...editFormData, description: e.target.value })}
                    className="mt-2 dark:bg-gray-900 dark:border-gray-700"
                    rows={3}
                  />
                ) : (
                  <div className="dark:text-white mt-2">{app.description}</div>
                )}
              </div>
            </div>

            {/* 右侧 */}
            <div className="space-y-6">
              <div>
                <Label className="text-gray-600 dark:text-gray-400">编码</Label>
                {isEditing ? (
                  <Input
                    value={editFormData.code}
                    onChange={(e) => setEditFormData({ ...editFormData, code: e.target.value })}
                    className="mt-2 dark:bg-gray-900 dark:border-gray-700"
                  />
                ) : (
                  <div className="dark:text-white mt-2">{app.code}</div>
                )}
              </div>

              <div>
                <Label className="text-gray-600 dark:text-gray-400">ID</Label>
                <div className="dark:text-white mt-2">{app.id}</div>
              </div>

              <div>
                <Label className="text-gray-600 dark:text-gray-400">所属组</Label>
                {isEditing ? (
                  <Input
                    value={editFormData.groupId || ''}
                    onChange={(e) => setEditFormData({ ...editFormData, groupId: e.target.value })}
                    className="mt-2 dark:bg-gray-900 dark:border-gray-700"
                    placeholder="未设置"
                  />
                ) : (
                  <div className="dark:text-white mt-2">{app.groupId || '--'}</div>
                )}
              </div>

              <div>
                <Label className="text-gray-600 dark:text-gray-400">所属租户</Label>
                <div className="dark:text-white mt-2">{app.tenantName || '--'}</div>
              </div>

              <div>
                <Label className="text-gray-600 dark:text-gray-400">创建人</Label>
                <div className="dark:text-white mt-2">{app.creator}</div>
              </div>

              <div>
                <Label className="text-gray-600 dark:text-gray-400">状态</Label>
                {isEditing ? (
                  <Select
                    value={editFormData.status}
                    onValueChange={(value) => setEditFormData({ ...editFormData, status: value as any })}
                  >
                    <SelectTrigger className="mt-2 dark:bg-gray-900 dark:border-gray-700">
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="启用">启用</SelectItem>
                      <SelectItem value="禁用">禁用</SelectItem>
                    </SelectContent>
                  </Select>
                ) : (
                  <div className="mt-2">{getStatusBadge(app.status)}</div>
                )}
              </div>

              <div>
                <Label className="text-gray-600 dark:text-gray-400">检索</Label>
                {isEditing ? (
                  <Input
                    value={editFormData.search}
                    onChange={(e) => setEditFormData({ ...editFormData, search: e.target.value })}
                    className="mt-2 dark:bg-gray-900 dark:border-gray-700"
                  />
                ) : (
                  <div className="dark:text-white mt-2">{app.search}</div>
                )}
              </div>

              <div>
                <Label className="text-gray-600 dark:text-gray-400">URL</Label>
                {isEditing ? (
                  <Input
                    value={editFormData.url}
                    onChange={(e) => setEditFormData({ ...editFormData, url: e.target.value })}
                    className="mt-2 dark:bg-gray-900 dark:border-gray-700"
                  />
                ) : (
                  <div className="dark:text-white mt-2 text-sm break-all">{app.url}</div>
                )}
              </div>

              <div>
                <Label className="text-gray-600 dark:text-gray-400">应用来源</Label>
                <div className="mt-2">{getSourceBadge(app.isInstalled)}</div>
              </div>
            </div>
          </div>
        </div>
      </Card>
    </div>
  );
}
