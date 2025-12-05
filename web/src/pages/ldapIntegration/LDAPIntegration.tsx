import { useState } from 'react';
import { Server, TestTube, Plus, Edit, Trash2, CheckCircle2, XCircle, RefreshCw, Users } from 'lucide-react';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';  
import { Label } from '@/components/ui/label';
import { Badge } from '@/components/ui/badge';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog';
import { Switch } from '@/components/ui/switch';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { toast } from 'sonner';

interface LDAPConfig {
  id: string;
  name: string;
  enabled: boolean;
  server: string;
  port: number;
  baseDN: string;
  bindDN: string;
  bindPassword: string;
  userFilter: string;
  emailAttribute: string;
  nameAttribute: string;
  groupFilter?: string;
  ssl: boolean;
  lastSync?: string;
  syncedUsers?: number;
  status: 'active' | 'inactive' | 'error';
  errorMessage?: string;
}

export function LDAPIntegration() {
  const [ldapConfigs, setLdapConfigs] = useState<LDAPConfig[]>([
    {
      id: 'ldap-001',
      name: 'Active Directory',
      enabled: true,
      server: 'ldap.example.com',
      port: 389,
      baseDN: 'dc=example,dc=com',
      bindDN: 'cn=admin,dc=example,dc=com',
      bindPassword: '********',
      userFilter: '(objectClass=person)',
      emailAttribute: 'mail',
      nameAttribute: 'cn',
      groupFilter: '(objectClass=group)',
      ssl: false,
      lastSync: '2025-10-22 09:00:00',
      syncedUsers: 156,
      status: 'active',
    },
    {
      id: 'ldap-002',
      name: 'OpenLDAP',
      enabled: false,
      server: 'openldap.company.com',
      port: 636,
      baseDN: 'ou=users,dc=company,dc=com',
      bindDN: 'cn=readonly,dc=company,dc=com',
      bindPassword: '********',
      userFilter: '(objectClass=inetOrgPerson)',
      emailAttribute: 'mail',
      nameAttribute: 'displayName',
      ssl: true,
      status: 'inactive',
    },
  ]);

  const [showConfigDialog, setShowConfigDialog] = useState(false);
  const [editingConfig, setEditingConfig] = useState<LDAPConfig | null>(null);
  const [testingConnection, setTestingConnection] = useState(false);
  const [showSyncDialog, setShowSyncDialog] = useState(false);
  const [selectedConfig, setSelectedConfig] = useState<LDAPConfig | null>(null);

  const [formData, setFormData] = useState<Partial<LDAPConfig>>({
    name: '',
    server: '',
    port: 389,
    baseDN: '',
    bindDN: '',
    bindPassword: '',
    userFilter: '(objectClass=person)',
    emailAttribute: 'mail',
    nameAttribute: 'cn',
    groupFilter: '',
    ssl: false,
    enabled: true,
  });

  const handleAddConfig = () => {
    setEditingConfig(null);
    setFormData({
      name: '',
      server: '',
      port: 389,
      baseDN: '',
      bindDN: '',
      bindPassword: '',
      userFilter: '(objectClass=person)',
      emailAttribute: 'mail',
      nameAttribute: 'cn',
      groupFilter: '',
      ssl: false,
      enabled: true,
    });
    setShowConfigDialog(true);
  };

  const handleEditConfig = (config: LDAPConfig) => {
    setEditingConfig(config);
    setFormData(config);
    setShowConfigDialog(true);
  };

  const handleSaveConfig = () => {
    if (!formData.name || !formData.server || !formData.baseDN || !formData.bindDN) {
      toast.error('请填写必填字段');
      return;
    }

    if (editingConfig) {
      setLdapConfigs(ldapConfigs.map(c => 
        c.id === editingConfig.id ? { ...c, ...formData } : c
      ));
      toast.success('LDAP配置已更新');
    } else {
      const newConfig: LDAPConfig = {
        ...formData as LDAPConfig,
        id: `ldap-${Date.now()}`,
        status: 'inactive',
      };
      setLdapConfigs([...ldapConfigs, newConfig]);
      toast.success('LDAP配置已添加');
    }
    setShowConfigDialog(false);
  };

  const handleDeleteConfig = (id: string) => {
    setLdapConfigs(ldapConfigs.filter(c => c.id !== id));
    toast.success('LDAP配置已删除');
  };

  const handleToggleConfig = (id: string) => {
    setLdapConfigs(ldapConfigs.map(c => 
      c.id === id ? { ...c, enabled: !c.enabled } : c
    ));
    toast.success('状态已更新');
  };

  const handleTestConnection = async () => {
    setTestingConnection(true);
    
    // 模拟测试连接
    setTimeout(() => {
      setTestingConnection(false);
      toast.success('连接测试成功');
    }, 2000);
  };

  const handleSync = (config: LDAPConfig) => {
    setSelectedConfig(config);
    setShowSyncDialog(true);
  };

  const handleConfirmSync = () => {
    toast.success('正在同步用户数据...');
    setShowSyncDialog(false);
    
    // 模拟同步
    setTimeout(() => {
      if (selectedConfig) {
        setLdapConfigs(ldapConfigs.map(c => 
          c.id === selectedConfig.id 
            ? { 
                ...c, 
                lastSync: new Date().toLocaleString('zh-CN'),
                syncedUsers: Math.floor(Math.random() * 200) + 100,
                status: 'active' as const,
              }
            : c
        ));
        toast.success('用户同步完成');
      }
    }, 3000);
  };

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'active':
        return <Badge className="bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400 border-0">
          <CheckCircle2 className="w-3 h-3 mr-1" />
          正常
        </Badge>;
      case 'inactive':
        return <Badge className="bg-gray-100 text-gray-700 dark:bg-gray-700 dark:text-gray-400 border-0">
          未启用
        </Badge>;
      case 'error':
        return <Badge className="bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400 border-0">
          <XCircle className="w-3 h-3 mr-1" />
          错误
        </Badge>;
      default:
        return null;
    }
  };

  return (
    <div className="space-y-6">
      {/* 页面标题 */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl dark:text-white mb-2">LDAP集成</h2>
          <p className="text-sm text-gray-500 dark:text-gray-400">
            配置和管理LDAP服务集成，同步企业用户数据
          </p>
        </div>
        <Button onClick={handleAddConfig} className="bg-blue-600 hover:bg-blue-700">
          <Plus className="w-4 h-4 mr-2" />
          添加LDAP配置
        </Button>
      </div>

      {/* 统计卡片 */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <Card className="p-4 dark:bg-gray-800 dark:border-gray-700">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-500 dark:text-gray-400">总配置数</p>
              <p className="text-2xl dark:text-white mt-1">{ldapConfigs.length}</p>
            </div>
            <div className="w-12 h-12 bg-blue-100 dark:bg-blue-900/30 rounded-lg flex items-center justify-center">
              <Server className="w-6 h-6 text-blue-600 dark:text-blue-400" />
            </div>
          </div>
        </Card>

        <Card className="p-4 dark:bg-gray-800 dark:border-gray-700">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-500 dark:text-gray-400">已启用</p>
              <p className="text-2xl dark:text-white mt-1">
                {ldapConfigs.filter(c => c.enabled).length}
              </p>
            </div>
            <div className="w-12 h-12 bg-green-100 dark:bg-green-900/30 rounded-lg flex items-center justify-center">
              <CheckCircle2 className="w-6 h-6 text-green-600 dark:text-green-400" />
            </div>
          </div>
        </Card>

        <Card className="p-4 dark:bg-gray-800 dark:border-gray-700">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-500 dark:text-gray-400">同步用户</p>
              <p className="text-2xl dark:text-white mt-1">
                {ldapConfigs.reduce((sum, c) => sum + (c.syncedUsers || 0), 0)}
              </p>
            </div>
            <div className="w-12 h-12 bg-purple-100 dark:bg-purple-900/30 rounded-lg flex items-center justify-center">
              <Users className="w-6 h-6 text-purple-600 dark:text-purple-400" />
            </div>
          </div>
        </Card>
      </div>

      {/* LDAP配置列表 */}
      <div className="grid grid-cols-1 gap-4">
        {ldapConfigs.map((config) => (
          <Card key={config.id} className="p-6 dark:bg-gray-800 dark:border-gray-700">
            <div className="flex items-start justify-between mb-4">
              <div className="flex items-center gap-4">
                <div className={`w-12 h-12 rounded-lg flex items-center justify-center ${
                  config.enabled && config.status === 'active'
                    ? 'bg-blue-100 dark:bg-blue-900/30 text-blue-600 dark:text-blue-400'
                    : 'bg-gray-100 dark:bg-gray-700 text-gray-400'
                }`}>
                  <Server className="w-6 h-6" />
                </div>
                <div>
                  <div className="flex items-center gap-3">
                    <h3 className="text-lg dark:text-white">{config.name}</h3>
                    {getStatusBadge(config.status)}
                    {config.ssl && (
                      <Badge variant="outline" className="dark:border-gray-600 dark:text-gray-400">
                        SSL
                      </Badge>
                    )}
                  </div>
                  <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
                    {config.server}:{config.port}
                  </p>
                </div>
              </div>
              <div className="flex items-center gap-2">
                <Switch
                  checked={config.enabled}
                  onCheckedChange={() => handleToggleConfig(config.id)}
                />
              </div>
            </div>

            <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-4">
              <div>
                <Label className="text-gray-600 dark:text-gray-400">Base DN</Label>
                <div className="text-sm dark:text-white mt-1 font-mono">{config.baseDN}</div>
              </div>
              <div>
                <Label className="text-gray-600 dark:text-gray-400">用户过滤器</Label>
                <div className="text-sm dark:text-white mt-1 font-mono">{config.userFilter}</div>
              </div>
              {config.lastSync && (
                <div>
                  <Label className="text-gray-600 dark:text-gray-400">最后同步</Label>
                  <div className="text-sm dark:text-white mt-1">{config.lastSync}</div>
                </div>
              )}
              {config.syncedUsers !== undefined && (
                <div>
                  <Label className="text-gray-600 dark:text-gray-400">同步用户数</Label>
                  <div className="text-sm dark:text-white mt-1">{config.syncedUsers} 人</div>
                </div>
              )}
            </div>

            {config.errorMessage && (
              <div className="mb-4 p-3 bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-900/50 rounded-lg">
                <p className="text-sm text-red-700 dark:text-red-400">{config.errorMessage}</p>
              </div>
            )}

            <div className="flex items-center gap-2 pt-4 border-t dark:border-gray-700">
              <Button
                variant="outline"
                size="sm"
                onClick={() => handleSync(config)}
                disabled={!config.enabled}
                className="dark:border-gray-600 dark:hover:bg-gray-700"
              >
                <RefreshCw className="w-4 h-4 mr-2" />
                同步用户
              </Button>
              <Button
                variant="outline"
                size="sm"
                onClick={() => handleEditConfig(config)}
                className="dark:border-gray-600 dark:hover:bg-gray-700"
              >
                <Edit className="w-4 h-4 mr-2" />
                编辑
              </Button>
              <Button
                variant="outline"
                size="sm"
                onClick={() => handleDeleteConfig(config.id)}
                className="text-red-600 hover:text-red-700 dark:text-red-400 dark:border-gray-600 dark:hover:bg-gray-700"
              >
                <Trash2 className="w-4 h-4 mr-2" />
                删除
              </Button>
            </div>
          </Card>
        ))}

        {ldapConfigs.length === 0 && (
          <Card className="p-12 dark:bg-gray-800 dark:border-gray-700">
            <div className="text-center">
              <Server className="w-16 h-16 text-gray-400 mx-auto mb-4" />
              <h3 className="text-lg dark:text-white mb-2">暂无LDAP配置</h3>
              <p className="text-sm text-gray-500 dark:text-gray-400 mb-4">
                添加LDAP配置以同步企业用户数据
              </p>
              <Button onClick={handleAddConfig} className="bg-blue-600 hover:bg-blue-700">
                <Plus className="w-4 h-4 mr-2" />
                添加配置
              </Button>
            </div>
          </Card>
        )}
      </div>

      {/* 配置对话框 */}
      <Dialog open={showConfigDialog} onOpenChange={setShowConfigDialog}>
        <DialogContent className="max-w-2xl dark:bg-gray-800 dark:border-gray-700 max-h-[90vh] overflow-y-auto">
          <DialogHeader>
            <DialogTitle className="dark:text-white">
              {editingConfig ? '编辑LDAP配置' : '添加LDAP配置'}
            </DialogTitle>
          </DialogHeader>
          
          <Tabs defaultValue="basic" className="mt-4">
            <TabsList className="dark:bg-gray-700">
              <TabsTrigger value="basic" className="dark:data-[state=active]:bg-gray-600">
                基本配置
              </TabsTrigger>
              <TabsTrigger value="advanced" className="dark:data-[state=active]:bg-gray-600">
                高级配置
              </TabsTrigger>
            </TabsList>

            <TabsContent value="basic" className="space-y-4 mt-4">
              <div>
                <Label className="dark:text-gray-300">配置名称 *</Label>
                <Input
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  placeholder="例如：Active Directory"
                  className="mt-2 dark:bg-gray-900 dark:border-gray-700"
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <Label className="dark:text-gray-300">LDAP服务器 *</Label>
                  <Input
                    value={formData.server}
                    onChange={(e) => setFormData({ ...formData, server: e.target.value })}
                    placeholder="ldap.example.com"
                    className="mt-2 dark:bg-gray-900 dark:border-gray-700"
                  />
                </div>
                <div>
                  <Label className="dark:text-gray-300">端口 *</Label>
                  <Input
                    type="number"
                    value={formData.port}
                    onChange={(e) => setFormData({ ...formData, port: parseInt(e.target.value) })}
                    className="mt-2 dark:bg-gray-900 dark:border-gray-700"
                  />
                </div>
              </div>

              <div>
                <Label className="dark:text-gray-300">Base DN *</Label>
                <Input
                  value={formData.baseDN}
                  onChange={(e) => setFormData({ ...formData, baseDN: e.target.value })}
                  placeholder="dc=example,dc=com"
                  className="mt-2 dark:bg-gray-900 dark:border-gray-700"
                />
              </div>

              <div>
                <Label className="dark:text-gray-300">Bind DN *</Label>
                <Input
                  value={formData.bindDN}
                  onChange={(e) => setFormData({ ...formData, bindDN: e.target.value })}
                  placeholder="cn=admin,dc=example,dc=com"
                  className="mt-2 dark:bg-gray-900 dark:border-gray-700"
                />
              </div>

              <div>
                <Label className="dark:text-gray-300">Bind密码 *</Label>
                <Input
                  type="password"
                  value={formData.bindPassword}
                  onChange={(e) => setFormData({ ...formData, bindPassword: e.target.value })}
                  className="mt-2 dark:bg-gray-900 dark:border-gray-700"
                />
              </div>

              <div className="flex items-center justify-between p-4 border dark:border-gray-700 rounded-lg">
                <div>
                  <Label className="dark:text-gray-300">启用SSL/TLS</Label>
                  <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
                    使用加密连接
                  </p>
                </div>
                <Switch
                  checked={formData.ssl}
                  onCheckedChange={(checked) => setFormData({ ...formData, ssl: checked })}
                />
              </div>
            </TabsContent>

            <TabsContent value="advanced" className="space-y-4 mt-4">
              <div>
                <Label className="dark:text-gray-300">用户过滤器</Label>
                <Input
                  value={formData.userFilter}
                  onChange={(e) => setFormData({ ...formData, userFilter: e.target.value })}
                  placeholder="(objectClass=person)"
                  className="mt-2 dark:bg-gray-900 dark:border-gray-700"
                />
                <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">
                  LDAP查询过滤器，用于筛选用户
                </p>
              </div>

              <div>
                <Label className="dark:text-gray-300">组过滤器</Label>
                <Input
                  value={formData.groupFilter}
                  onChange={(e) => setFormData({ ...formData, groupFilter: e.target.value })}
                  placeholder="(objectClass=group)"
                  className="mt-2 dark:bg-gray-900 dark:border-gray-700"
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <Label className="dark:text-gray-300">邮箱属性</Label>
                  <Input
                    value={formData.emailAttribute}
                    onChange={(e) => setFormData({ ...formData, emailAttribute: e.target.value })}
                    placeholder="mail"
                    className="mt-2 dark:bg-gray-900 dark:border-gray-700"
                  />
                </div>
                <div>
                  <Label className="dark:text-gray-300">姓名属性</Label>
                  <Input
                    value={formData.nameAttribute}
                    onChange={(e) => setFormData({ ...formData, nameAttribute: e.target.value })}
                    placeholder="cn"
                    className="mt-2 dark:bg-gray-900 dark:border-gray-700"
                  />
                </div>
              </div>
            </TabsContent>
          </Tabs>

          <DialogFooter className="gap-2">
            <Button
              variant="outline"
              onClick={handleTestConnection}
              disabled={testingConnection}
              className="dark:border-gray-600 dark:hover:bg-gray-700"
            >
              <TestTube className="w-4 h-4 mr-2" />
              {testingConnection ? '测试中...' : '测试连接'}
            </Button>
            <Button
              variant="outline"
              onClick={() => setShowConfigDialog(false)}
              className="dark:border-gray-600 dark:hover:bg-gray-700"
            >
              取消
            </Button>
            <Button onClick={handleSaveConfig} className="bg-blue-600 hover:bg-blue-700">
              保存配置
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* 同步确认对话框 */}
      <Dialog open={showSyncDialog} onOpenChange={setShowSyncDialog}>
        <DialogContent className="dark:bg-gray-800 dark:border-gray-700" aria-describedby={undefined}>
          <DialogHeader>
            <DialogTitle className="dark:text-white">同步用户数据</DialogTitle>
          </DialogHeader>
          
          <div className="space-y-4">
            <p className="text-sm text-gray-600 dark:text-gray-400">
              确认要从LDAP服务器同步用户数据吗？
            </p>
            <div className="p-4 bg-blue-50 dark:bg-blue-900/20 rounded-lg border border-blue-200 dark:border-blue-900/50">
              <p className="text-sm text-blue-700 dark:text-blue-400">
                • 将从LDAP服务器获取最新的用户数据<br/>
                • 新增用户将自动创建账户<br/>
                • 已存在用户的信息将被更新<br/>
                • 同步过程可能需要几分钟时间
              </p>
            </div>
          </div>

          <DialogFooter>
            <Button
              variant="outline"
              onClick={() => setShowSyncDialog(false)}
              className="dark:border-gray-600 dark:hover:bg-gray-700"
            >
              取消
            </Button>
            <Button onClick={handleConfirmSync} className="bg-blue-600 hover:bg-blue-700">
              <RefreshCw className="w-4 h-4 mr-2" />
              开始同步
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}