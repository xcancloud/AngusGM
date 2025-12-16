import { useState } from 'react';
import { ArrowLeft, Settings, Bell, Search, Plus, Mail, MessageSquare, Webhook, ToggleLeft, TestTube, Edit2, Trash2, Clock, Calendar, TrendingUp, CheckCircle, XCircle } from 'lucide-react';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Switch } from '@/components/ui/switch';
import { Tabs, TabsList, TabsTrigger, TabsContent } from '@/components/ui/tabs';
import { toast } from 'sonner';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Label } from '@/components/ui/label';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Textarea } from '@/components/ui/textarea';

interface Channel {
  id: string;
  name: string;
  type: 'email' | 'slack' | 'webhook' | 'sms';
  isEnabled: boolean;
  stats: {
    sent: number;
    lastSent: string;
  };
  config: {
    [key: string]: string;
  };
  verified: boolean;
}

interface NotificationRule {
  id: string;
  name: string;
  event: string;
  channels: string[];
  conditions: string;
  isEnabled: boolean;
  createdAt: string;
}

interface NotificationTemplate {
  id: string;
  name: string;
  type: string;
  subject: string;
  content: string;
  variables: string[];
  createdAt: string;
  updatedAt: string;
}

interface NotificationHistory {
  id: string;
  title: string;
  channel: string;
  recipient: string;
  status: 'success' | 'failed' | 'pending';
  sentAt: string;
  error?: string;
}

export function NotificationSettings({ onBack }: { onBack: () => void }) {
  const [searchQuery, setSearchQuery] = useState('');
  const [showChannelDialog, setShowChannelDialog] = useState(false);
  const [showRuleDialog, setShowRuleDialog] = useState(false);
  const [showTemplateDialog, setShowTemplateDialog] = useState(false);
  const [editingChannel, setEditingChannel] = useState<Channel | null>(null);
  const [editingRule, setEditingRule] = useState<NotificationRule | null>(null);
  const [editingTemplate, setEditingTemplate] = useState<NotificationTemplate | null>(null);

  const [channels, setChannels] = useState<Channel[]>([
    {
      id: '1',
      name: 'System Email',
      type: 'email',
      isEnabled: true,
      stats: {
        sent: 12370,
        lastSent: '2024-12-02 10:30:00',
      },
      config: {
        host: 'smtp.gmail.com',
        port: '587',
        from: 'system@angusgm.com',
      },
      verified: true,
    },
    {
      id: '2',
      name: 'DevOps Slack',
      type: 'slack',
      isEnabled: true,
      stats: {
        sent: 856,
        lastSent: '2024-12-02 09:15:00',
      },
      config: {
        webhookUrl: 'https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX',
        channel: '#devops-alerts',
      },
      verified: true,
    },
    {
      id: '3',
      name: 'Monitoring Webhook',
      type: 'webhook',
      isEnabled: false,
      stats: {
        sent: 423,
        lastSent: '2024-12-01 15:20:00',
      },
      config: {
        url: 'https://monitoring.example.com/webhook',
        method: 'POST',
      },
      verified: false,
    },
  ]);

  const [rules, setRules] = useState<NotificationRule[]>([
    {
      id: '1',
      name: '新用户注册通知',
      event: 'user.registered',
      channels: ['1', '2'],
      conditions: '所有新用户',
      isEnabled: true,
      createdAt: '2024-11-15 14:30:00',
    },
    {
      id: '2',
      name: '系统错误告警',
      event: 'system.error',
      channels: ['1', '2', '3'],
      conditions: '严重程度 >= 高',
      isEnabled: true,
      createdAt: '2024-11-10 09:00:00',
    },
    {
      id: '3',
      name: '账单到期提醒',
      event: 'billing.due',
      channels: ['1'],
      conditions: '到期前3天',
      isEnabled: true,
      createdAt: '2024-11-01 10:00:00',
    },
  ]);

  const [templates, setTemplates] = useState<NotificationTemplate[]>([
    {
      id: '1',
      name: '用户注册欢迎邮件',
      type: 'email',
      subject: '欢迎加入 AngusGM - {{tenantName}}',
      content: '尊敬的 {{userName}}，\n\n感谢您注册 AngusGM 平台...',
      variables: ['userName', 'tenantName', 'verificationLink'],
      createdAt: '2024-11-01 10:00:00',
      updatedAt: '2024-11-15 14:30:00',
    },
    {
      id: '2',
      name: '密码重置通知',
      type: 'email',
      subject: '密码重置请求 - AngusGM',
      content: '您好 {{userName}}，\n\n我们收到了您的密码重置请求...',
      variables: ['userName', 'resetLink', 'expireTime'],
      createdAt: '2024-11-05 11:00:00',
      updatedAt: '2024-11-20 16:00:00',
    },
    {
      id: '3',
      name: '系统维护通知',
      type: 'slack',
      subject: '系统维护公告',
      content: '@channel 系统将于 {{maintenanceTime}} 进行维护...',
      variables: ['maintenanceTime', 'duration', 'affectedServices'],
      createdAt: '2024-11-10 15:00:00',
      updatedAt: '2024-11-10 15:00:00',
    },
  ]);

  const [history, setHistory] = useState<NotificationHistory[]>([
    {
      id: '1',
      title: '新用户注册 - John Doe',
      channel: 'System Email',
      recipient: 'john@example.com',
      status: 'success',
      sentAt: '2024-12-02 10:30:00',
    },
    {
      id: '2',
      title: '账单到期提醒',
      channel: 'System Email',
      recipient: 'billing@techflow.com',
      status: 'success',
      sentAt: '2024-12-02 10:15:00',
    },
    {
      id: '3',
      title: '系统错误告警',
      channel: 'DevOps Slack',
      recipient: '#devops-alerts',
      status: 'failed',
      sentAt: '2024-12-02 09:45:00',
      error: 'Webhook URL unreachable',
    },
    {
      id: '4',
      title: '密码重置请求',
      channel: 'System Email',
      recipient: 'user@example.com',
      status: 'success',
      sentAt: '2024-12-02 09:30:00',
    },
    {
      id: '5',
      title: '系统维护通知',
      channel: 'DevOps Slack',
      recipient: '#general',
      status: 'pending',
      sentAt: '2024-12-02 09:00:00',
    },
  ]);

  const getChannelIcon = (type: string) => {
    switch (type) {
      case 'email':
        return <Mail className="w-5 h-5" />;
      case 'slack':
        return <MessageSquare className="w-5 h-5" />;
      case 'webhook':
        return <Webhook className="w-5 h-5" />;
      case 'sms':
        return <MessageSquare className="w-5 h-5" />;
      default:
        return <Bell className="w-5 h-5" />;
    }
  };

  const getChannelIconBg = (type: string) => {
    switch (type) {
      case 'email':
        return 'bg-blue-100 dark:bg-blue-900/30';
      case 'slack':
        return 'bg-purple-100 dark:bg-purple-900/30';
      case 'webhook':
        return 'bg-cyan-100 dark:bg-cyan-900/30';
      case 'sms':
        return 'bg-green-100 dark:bg-green-900/30';
      default:
        return 'bg-gray-100 dark:bg-gray-900/30';
    }
  };

  const getChannelIconColor = (type: string) => {
    switch (type) {
      case 'email':
        return 'text-blue-600 dark:text-blue-400';
      case 'slack':
        return 'text-purple-600 dark:text-purple-400';
      case 'webhook':
        return 'text-cyan-600 dark:text-cyan-400';
      case 'sms':
        return 'text-green-600 dark:text-green-400';
      default:
        return 'text-gray-600 dark:text-gray-400';
    }
  };

  const getChannelTypeName = (type: string) => {
    switch (type) {
      case 'email':
        return '邮件通知';
      case 'slack':
        return 'Slack通知';
      case 'webhook':
        return 'Webhook通知';
      case 'sms':
        return '短信通知';
      default:
        return '未知类型';
    }
  };

  const toggleChannelStatus = (id: string) => {
    setChannels(
      channels.map((c) => (c.id === id ? { ...c, isEnabled: !c.isEnabled } : c))
    );
    toast.success('渠道状态已更新');
  };

  const testChannel = (channel: Channel) => {
    toast.success(`正在测试 ${channel.name}...`);
    setTimeout(() => {
      toast.success(`${channel.name} 测试成功`);
    }, 1500);
  };

  const deleteChannel = (id: string) => {
    setChannels(channels.filter((c) => c.id !== id));
    toast.success('渠道已删除');
  };

  const deleteRule = (id: string) => {
    setRules(rules.filter((r) => r.id !== id));
    toast.success('规则已删除');
  };

  const deleteTemplate = (id: string) => {
    setTemplates(templates.filter((t) => t.id !== id));
    toast.success('模板已删除');
  };

  const toggleRuleStatus = (id: string) => {
    setRules(rules.map((r) => (r.id === id ? { ...r, isEnabled: !r.isEnabled } : r)));
    toast.success('规则状态已更新');
  };

  const handleAddChannel = () => {
    setEditingChannel(null);
    setShowChannelDialog(true);
  };

  const handleEditChannel = (channel: Channel) => {
    setEditingChannel(channel);
    setShowChannelDialog(true);
  };

  const handleAddRule = () => {
    setEditingRule(null);
    setShowRuleDialog(true);
  };

  const handleEditRule = (rule: NotificationRule) => {
    setEditingRule(rule);
    setShowRuleDialog(true);
  };

  const handleAddTemplate = () => {
    setEditingTemplate(null);
    setShowTemplateDialog(true);
  };

  const handleEditTemplate = (template: NotificationTemplate) => {
    setEditingTemplate(template);
    setShowTemplateDialog(true);
  };

  const filteredChannels = channels.filter(
    (channel) =>
      channel.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      channel.type.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const filteredRules = rules.filter(
    (rule) =>
      rule.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      rule.event.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const filteredTemplates = templates.filter(
    (template) =>
      template.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      template.subject.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const filteredHistory = history.filter(
    (item) =>
      item.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
      item.recipient.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-start justify-between">
        <div className="flex items-center gap-3">
          <Button
            variant="ghost"
            size="sm"
            onClick={onBack}
            className="dark:text-gray-300 dark:hover:bg-gray-700"
          >
            <ArrowLeft className="w-4 h-4 mr-1" />
            返回
          </Button>
        </div>
      </div>

      <div className="flex items-start justify-between">
        <div className="flex items-center gap-3">
          <div className="p-2 bg-blue-100 dark:bg-blue-900/30 rounded-lg">
            <Settings className="w-6 h-6 text-blue-600 dark:text-blue-400" />
          </div>
          <div>
            <h1 className="text-2xl dark:text-white">通知配置</h1>
            <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
              配置系统通知渠道和规则
            </p>
          </div>
        </div>
      </div>

      {/* Tabs */}
      <Tabs defaultValue="channels" className="space-y-4">
        <TabsList className="bg-gray-100 dark:bg-gray-800 p-1">
          <TabsTrigger
            value="channels"
            className="data-[state=active]:bg-white dark:data-[state=active]:bg-gray-700"
          >
            <Bell className="w-4 h-4 mr-2" />
            通知渠道
          </TabsTrigger>
          <TabsTrigger
            value="rules"
            className="data-[state=active]:bg-white dark:data-[state=active]:bg-gray-700"
          >
            <ToggleLeft className="w-4 h-4 mr-2" />
            通知规则
          </TabsTrigger>
          <TabsTrigger
            value="templates"
            className="data-[state=active]:bg-white dark:data-[state=active]:bg-gray-700"
          >
            <Mail className="w-4 h-4 mr-2" />
            通知模板
          </TabsTrigger>
          <TabsTrigger
            value="history"
            className="data-[state=active]:bg-white dark:data-[state=active]:bg-gray-700"
          >
            <Clock className="w-4 h-4 mr-2" />
            通知历史
          </TabsTrigger>
          <TabsTrigger
            value="stats"
            className="data-[state=active]:bg-white dark:data-[state=active]:bg-gray-700"
          >
            <TrendingUp className="w-4 h-4 mr-2" />
            统计分析
          </TabsTrigger>
        </TabsList>

        {/* Channels Tab */}
        <TabsContent value="channels" className="space-y-4">
          <Card className="p-4 dark:bg-gray-800 dark:border-gray-700">
            <div className="flex items-center justify-between mb-4">
              <div className="relative flex-1 max-w-md">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
                <Input
                  placeholder="搜索规则、渠道或场景..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="pl-10 dark:bg-gray-700 dark:border-gray-600 dark:text-gray-200 dark:placeholder-gray-400"
                />
              </div>
              <Button
                onClick={handleAddChannel}
                className="ml-4 bg-blue-600 hover:bg-blue-700 text-white"
              >
                <Plus className="w-4 h-4 mr-2" />
                添加渠道
              </Button>
            </div>

            <div className="space-y-3">
              {filteredChannels.map((channel) => (
                <div
                  key={channel.id}
                  className="p-4 rounded-lg border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800/50"
                >
                  <div className="flex items-center gap-4">
                    {/* Icon */}
                    <div
                      className={`p-3 rounded-lg ${getChannelIconBg(
                        channel.type
                      )}`}
                    >
                      <div className={getChannelIconColor(channel.type)}>
                        {getChannelIcon(channel.type)}
                      </div>
                    </div>

                    {/* Info */}
                    <div className="flex-1 min-w-0">
                      <div className="flex items-center gap-2 mb-1">
                        <h4 className="dark:text-white">{channel.name}</h4>
                        {channel.verified && (
                          <CheckCircle className="w-4 h-4 text-green-500" />
                        )}
                      </div>
                      <div className="flex items-center gap-4 text-sm text-gray-500 dark:text-gray-400">
                        <span>✓ {channel.stats.sent.toLocaleString()} 已发送</span>
                        <span className="flex items-center gap-1">
                          <Clock className="w-3 h-3" />
                          {channel.stats.lastSent}
                        </span>
                      </div>
                    </div>

                    {/* Actions */}
                    <div className="flex items-center gap-3">
                      <Badge
                        variant="outline"
                        className="dark:border-gray-600 dark:text-gray-300"
                      >
                        {getChannelTypeName(channel.type)}
                      </Badge>
                      <Switch
                        checked={channel.isEnabled}
                        onCheckedChange={() => toggleChannelStatus(channel.id)}
                      />
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => testChannel(channel)}
                        className="dark:border-gray-600 dark:text-gray-300 dark:hover:bg-gray-700"
                      >
                        <TestTube className="w-4 h-4 mr-1" />
                        测试渠道
                      </Button>
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => handleEditChannel(channel)}
                        className="dark:text-gray-300 dark:hover:bg-gray-700"
                      >
                        <Edit2 className="w-4 h-4" />
                      </Button>
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => deleteChannel(channel.id)}
                        className="text-red-600 hover:text-red-700 hover:bg-red-50 dark:text-red-400 dark:hover:bg-red-900/20"
                      >
                        <Trash2 className="w-4 h-4" />
                      </Button>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </Card>
        </TabsContent>

        {/* Rules Tab */}
        <TabsContent value="rules" className="space-y-4">
          <Card className="p-4 dark:bg-gray-800 dark:border-gray-700">
            <div className="flex items-center justify-between mb-4">
              <div className="relative flex-1 max-w-md">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
                <Input
                  placeholder="搜索规则..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="pl-10 dark:bg-gray-700 dark:border-gray-600 dark:text-gray-200 dark:placeholder-gray-400"
                />
              </div>
              <Button
                onClick={handleAddRule}
                className="ml-4 bg-blue-600 hover:bg-blue-700 text-white"
              >
                <Plus className="w-4 h-4 mr-2" />
                添加规则
              </Button>
            </div>

            <div className="space-y-3">
              {filteredRules.map((rule) => (
                <div
                  key={rule.id}
                  className="p-4 rounded-lg border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800/50"
                >
                  <div className="flex items-center justify-between">
                    <div className="flex-1">
                      <div className="flex items-center gap-2 mb-2">
                        <h4 className="dark:text-white">{rule.name}</h4>
                        <Badge
                          variant="outline"
                          className="dark:border-gray-600 dark:text-gray-300"
                        >
                          {rule.event}
                        </Badge>
                      </div>
                      <div className="flex items-center gap-4 text-sm text-gray-500 dark:text-gray-400">
                        <span>条件: {rule.conditions}</span>
                        <span className="flex items-center gap-1">
                          <Calendar className="w-3 h-3" />
                          创建于 {rule.createdAt}
                        </span>
                      </div>
                      <div className="flex items-center gap-2 mt-2">
                        <span className="text-xs text-gray-500 dark:text-gray-400">
                          通知渠道:
                        </span>
                        {rule.channels.map((channelId) => {
                          const channel = channels.find((c) => c.id === channelId);
                          return channel ? (
                            <Badge
                              key={channelId}
                              variant="secondary"
                              className="text-xs"
                            >
                              {channel.name}
                            </Badge>
                          ) : null;
                        })}
                      </div>
                    </div>
                    <div className="flex items-center gap-2">
                      <Switch
                        checked={rule.isEnabled}
                        onCheckedChange={() => toggleRuleStatus(rule.id)}
                      />
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => handleEditRule(rule)}
                        className="dark:text-gray-300 dark:hover:bg-gray-700"
                      >
                        <Edit2 className="w-4 h-4" />
                      </Button>
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => deleteRule(rule.id)}
                        className="text-red-600 hover:text-red-700 hover:bg-red-50 dark:text-red-400 dark:hover:bg-red-900/20"
                      >
                        <Trash2 className="w-4 h-4" />
                      </Button>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </Card>
        </TabsContent>

        {/* Templates Tab */}
        <TabsContent value="templates" className="space-y-4">
          <Card className="p-4 dark:bg-gray-800 dark:border-gray-700">
            <div className="flex items-center justify-between mb-4">
              <div className="relative flex-1 max-w-md">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
                <Input
                  placeholder="搜索模板..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="pl-10 dark:bg-gray-700 dark:border-gray-600 dark:text-gray-200 dark:placeholder-gray-400"
                />
              </div>
              <Button
                onClick={handleAddTemplate}
                className="ml-4 bg-blue-600 hover:bg-blue-700 text-white"
              >
                <Plus className="w-4 h-4 mr-2" />
                添加模板
              </Button>
            </div>

            <div className="space-y-3">
              {filteredTemplates.map((template) => (
                <div
                  key={template.id}
                  className="p-4 rounded-lg border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800/50"
                >
                  <div className="flex items-start justify-between">
                    <div className="flex-1">
                      <div className="flex items-center gap-2 mb-2">
                        <h4 className="dark:text-white">{template.name}</h4>
                        <Badge
                          variant="outline"
                          className="dark:border-gray-600 dark:text-gray-300"
                        >
                          {template.type}
                        </Badge>
                      </div>
                      <p className="text-sm text-gray-600 dark:text-gray-400 mb-2">
                        主题: {template.subject}
                      </p>
                      <p className="text-sm text-gray-500 dark:text-gray-500 mb-3 line-clamp-2">
                        {template.content}
                      </p>
                      <div className="flex items-center gap-2 flex-wrap">
                        <span className="text-xs text-gray-500 dark:text-gray-400">
                          变量:
                        </span>
                        {template.variables.map((variable) => (
                          <Badge
                            key={variable}
                            variant="secondary"
                            className="text-xs font-mono"
                          >
                            {`{{${variable}}}`}
                          </Badge>
                        ))}
                      </div>
                      <div className="flex items-center gap-4 mt-2 text-xs text-gray-500 dark:text-gray-400">
                        <span>创建: {template.createdAt}</span>
                        <span>更新: {template.updatedAt}</span>
                      </div>
                    </div>
                    <div className="flex items-center gap-2">
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => handleEditTemplate(template)}
                        className="dark:text-gray-300 dark:hover:bg-gray-700"
                      >
                        <Edit2 className="w-4 h-4" />
                      </Button>
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => deleteTemplate(template.id)}
                        className="text-red-600 hover:text-red-700 hover:bg-red-50 dark:text-red-400 dark:hover:bg-red-900/20"
                      >
                        <Trash2 className="w-4 h-4" />
                      </Button>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </Card>
        </TabsContent>

        {/* History Tab */}
        <TabsContent value="history" className="space-y-4">
          <Card className="p-4 dark:bg-gray-800 dark:border-gray-700">
            <div className="flex items-center justify-between mb-4">
              <div className="relative flex-1 max-w-md">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
                <Input
                  placeholder="搜索历史记录..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="pl-10 dark:bg-gray-700 dark:border-gray-600 dark:text-gray-200 dark:placeholder-gray-400"
                />
              </div>
            </div>

            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="border-b border-gray-200 dark:border-gray-700">
                    <th className="text-left py-3 px-4 text-sm text-gray-600 dark:text-gray-400">
                      标题
                    </th>
                    <th className="text-left py-3 px-4 text-sm text-gray-600 dark:text-gray-400">
                      渠道
                    </th>
                    <th className="text-left py-3 px-4 text-sm text-gray-600 dark:text-gray-400">
                      接收者
                    </th>
                    <th className="text-left py-3 px-4 text-sm text-gray-600 dark:text-gray-400">
                      状态
                    </th>
                    <th className="text-left py-3 px-4 text-sm text-gray-600 dark:text-gray-400">
                      发送时间
                    </th>
                  </tr>
                </thead>
                <tbody>
                  {filteredHistory.map((item) => (
                    <tr
                      key={item.id}
                      className="border-b border-gray-100 dark:border-gray-800 hover:bg-gray-50 dark:hover:bg-gray-700/50"
                    >
                      <td className="py-3 px-4">
                        <span className="text-sm dark:text-gray-300">
                          {item.title}
                        </span>
                      </td>
                      <td className="py-3 px-4">
                        <Badge variant="outline" className="text-xs">
                          {item.channel}
                        </Badge>
                      </td>
                      <td className="py-3 px-4">
                        <span className="text-sm text-gray-600 dark:text-gray-400">
                          {item.recipient}
                        </span>
                      </td>
                      <td className="py-3 px-4">
                        {item.status === 'success' && (
                          <Badge className="bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400">
                            <CheckCircle className="w-3 h-3 mr-1" />
                            成功
                          </Badge>
                        )}
                        {item.status === 'failed' && (
                          <Badge className="bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400">
                            <XCircle className="w-3 h-3 mr-1" />
                            失败
                          </Badge>
                        )}
                        {item.status === 'pending' && (
                          <Badge className="bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-400">
                            <Clock className="w-3 h-3 mr-1" />
                            待发送
                          </Badge>
                        )}
                      </td>
                      <td className="py-3 px-4">
                        <span className="text-sm text-gray-600 dark:text-gray-400">
                          {item.sentAt}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </Card>
        </TabsContent>

        {/* Stats Tab */}
        <TabsContent value="stats" className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            <Card className="p-4 dark:bg-gray-800 dark:border-gray-700">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">
                    总发送量
                  </p>
                  <p className="text-3xl dark:text-white">13,649</p>
                  <p className="text-xs text-green-600 dark:text-green-400 mt-1">
                    ↗️ +12.5% 较上月
                  </p>
                </div>
                <div className="p-3 bg-blue-100 dark:bg-blue-900/30 rounded-lg">
                  <TrendingUp className="w-6 h-6 text-blue-600 dark:text-blue-400" />
                </div>
              </div>
            </Card>

            <Card className="p-4 dark:bg-gray-800 dark:border-gray-700">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">
                    成功率
                  </p>
                  <p className="text-3xl dark:text-white">98.7%</p>
                  <p className="text-xs text-green-600 dark:text-green-400 mt-1">
                    ↗️ +0.3% 较上月
                  </p>
                </div>
                <div className="p-3 bg-green-100 dark:bg-green-900/30 rounded-lg">
                  <CheckCircle className="w-6 h-6 text-green-600 dark:text-green-400" />
                </div>
              </div>
            </Card>

            <Card className="p-4 dark:bg-gray-800 dark:border-gray-700">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">
                    失败数量
                  </p>
                  <p className="text-3xl dark:text-white">178</p>
                  <p className="text-xs text-red-600 dark:text-red-400 mt-1">
                    ↗️ +5 较上月
                  </p>
                </div>
                <div className="p-3 bg-red-100 dark:bg-red-900/30 rounded-lg">
                  <XCircle className="w-6 h-6 text-red-600 dark:text-red-400" />
                </div>
              </div>
            </Card>

            <Card className="p-4 dark:bg-gray-800 dark:border-gray-700">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">
                    活跃渠道
                  </p>
                  <p className="text-3xl dark:text-white">2 / 3</p>
                  <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">
                    66.7% 可用率
                  </p>
                </div>
                <div className="p-3 bg-purple-100 dark:bg-purple-900/30 rounded-lg">
                  <Bell className="w-6 h-6 text-purple-600 dark:text-purple-400" />
                </div>
              </div>
            </Card>
          </div>

          <Card className="p-4 dark:bg-gray-800 dark:border-gray-700">
            <h3 className="text-lg dark:text-white mb-4">渠道发送统计</h3>
            <div className="space-y-3">
              {channels.map((channel) => (
                <div key={channel.id} className="space-y-2">
                  <div className="flex items-center justify-between text-sm">
                    <span className="dark:text-gray-300">{channel.name}</span>
                    <span className="text-gray-600 dark:text-gray-400">
                      {channel.stats.sent.toLocaleString()} 次
                    </span>
                  </div>
                  <div className="w-full bg-gray-200 dark:bg-gray-700 rounded-full h-2">
                    <div
                      className="bg-blue-600 h-2 rounded-full"
                      style={{
                        width: `${
                          (channel.stats.sent /
                            Math.max(...channels.map((c) => c.stats.sent))) *
                          100
                        }%`,
                      }}
                    />
                  </div>
                </div>
              ))}
            </div>
          </Card>
        </TabsContent>
      </Tabs>

      {/* Channel Dialog */}
      <Dialog open={showChannelDialog} onOpenChange={setShowChannelDialog}>
        <DialogContent className="dark:bg-gray-800 dark:border-gray-700">
          <DialogHeader>
            <DialogTitle className="dark:text-white">
              {editingChannel ? '编辑渠道' : '添加渠道'}
            </DialogTitle>
            <DialogDescription className="dark:text-gray-400">
              配置通知渠道的基本信息和连接参数
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-4">
            <div>
              <Label htmlFor="channel-name" className="dark:text-gray-300">
                渠道名称
              </Label>
              <Input
                id="channel-name"
                placeholder="例如: System Email"
                defaultValue={editingChannel?.name}
                className="dark:bg-gray-700 dark:border-gray-600 dark:text-gray-200"
              />
            </div>
            <div>
              <Label htmlFor="channel-type" className="dark:text-gray-300">
                渠道类型
              </Label>
              <Select defaultValue={editingChannel?.type || 'email'}>
                <SelectTrigger className="dark:bg-gray-700 dark:border-gray-600 dark:text-gray-200">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent className="dark:bg-gray-700 dark:border-gray-600">
                  <SelectItem value="email">邮件通知</SelectItem>
                  <SelectItem value="slack">Slack通知</SelectItem>
                  <SelectItem value="webhook">Webhook通知</SelectItem>
                  <SelectItem value="sms">短信通知</SelectItem>
                </SelectContent>
              </Select>
            </div>
            <div>
              <Label htmlFor="channel-config" className="dark:text-gray-300">
                配置信息 (JSON)
              </Label>
              <Textarea
                id="channel-config"
                placeholder='{"host": "smtp.gmail.com", "port": "587"}'
                rows={4}
                defaultValue={
                  editingChannel
                    ? JSON.stringify(editingChannel.config, null, 2)
                    : ''
                }
                className="dark:bg-gray-700 dark:border-gray-600 dark:text-gray-200 font-mono text-sm"
              />
            </div>
          </div>
          <DialogFooter>
            <Button
              variant="outline"
              onClick={() => setShowChannelDialog(false)}
              className="dark:border-gray-600 dark:text-gray-300 dark:hover:bg-gray-700"
            >
              取消
            </Button>
            <Button
              onClick={() => {
                toast.success(editingChannel ? '渠道已更新' : '渠道已添加');
                setShowChannelDialog(false);
              }}
              className="bg-blue-600 hover:bg-blue-700 text-white"
            >
              {editingChannel ? '更新' : '添加'}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Rule Dialog */}
      <Dialog open={showRuleDialog} onOpenChange={setShowRuleDialog}>
        <DialogContent className="dark:bg-gray-800 dark:border-gray-700">
          <DialogHeader>
            <DialogTitle className="dark:text-white">
              {editingRule ? '编辑规则' : '添加规则'}
            </DialogTitle>
            <DialogDescription className="dark:text-gray-400">
              配置通知规则的触发条件和渠道
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-4">
            <div>
              <Label htmlFor="rule-name" className="dark:text-gray-300">
                规则名称
              </Label>
              <Input
                id="rule-name"
                placeholder="例如: 新用户注册通知"
                defaultValue={editingRule?.name}
                className="dark:bg-gray-700 dark:border-gray-600 dark:text-gray-200"
              />
            </div>
            <div>
              <Label htmlFor="rule-event" className="dark:text-gray-300">
                触发事件
              </Label>
              <Select defaultValue={editingRule?.event || 'user.registered'}>
                <SelectTrigger className="dark:bg-gray-700 dark:border-gray-600 dark:text-gray-200">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent className="dark:bg-gray-700 dark:border-gray-600">
                  <SelectItem value="user.registered">用户注册</SelectItem>
                  <SelectItem value="system.error">系统错误</SelectItem>
                  <SelectItem value="billing.due">账单到期</SelectItem>
                  <SelectItem value="resource.quota">资源配额</SelectItem>
                </SelectContent>
              </Select>
            </div>
            <div>
              <Label htmlFor="rule-conditions" className="dark:text-gray-300">
                触发条件
              </Label>
              <Input
                id="rule-conditions"
                placeholder="例如: 所有新用户"
                defaultValue={editingRule?.conditions}
                className="dark:bg-gray-700 dark:border-gray-600 dark:text-gray-200"
              />
            </div>
          </div>
          <DialogFooter>
            <Button
              variant="outline"
              onClick={() => setShowRuleDialog(false)}
              className="dark:border-gray-600 dark:text-gray-300 dark:hover:bg-gray-700"
            >
              取消
            </Button>
            <Button
              onClick={() => {
                toast.success(editingRule ? '规则已更新' : '规则已添加');
                setShowRuleDialog(false);
              }}
              className="bg-blue-600 hover:bg-blue-700 text-white"
            >
              {editingRule ? '更新' : '添加'}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Template Dialog */}
      <Dialog open={showTemplateDialog} onOpenChange={setShowTemplateDialog}>
        <DialogContent className="dark:bg-gray-800 dark:border-gray-700 max-w-2xl">
          <DialogHeader>
            <DialogTitle className="dark:text-white">
              {editingTemplate ? '编辑模板' : '添加模板'}
            </DialogTitle>
            <DialogDescription className="dark:text-gray-400">
              创建或编辑通知消息模板
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-4">
            <div>
              <Label htmlFor="template-name" className="dark:text-gray-300">
                模板名称
              </Label>
              <Input
                id="template-name"
                placeholder="例如: 用户注册欢迎邮件"
                defaultValue={editingTemplate?.name}
                className="dark:bg-gray-700 dark:border-gray-600 dark:text-gray-200"
              />
            </div>
            <div>
              <Label htmlFor="template-type" className="dark:text-gray-300">
                模板类型
              </Label>
              <Select defaultValue={editingTemplate?.type || 'email'}>
                <SelectTrigger className="dark:bg-gray-700 dark:border-gray-600 dark:text-gray-200">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent className="dark:bg-gray-700 dark:border-gray-600">
                  <SelectItem value="email">邮件</SelectItem>
                  <SelectItem value="slack">Slack</SelectItem>
                  <SelectItem value="sms">短信</SelectItem>
                </SelectContent>
              </Select>
            </div>
            <div>
              <Label htmlFor="template-subject" className="dark:text-gray-300">
                主题
              </Label>
              <Input
                id="template-subject"
                placeholder="例如: 欢迎加入 AngusGM"
                defaultValue={editingTemplate?.subject}
                className="dark:bg-gray-700 dark:border-gray-600 dark:text-gray-200"
              />
            </div>
            <div>
              <Label htmlFor="template-content" className="dark:text-gray-300">
                内容
              </Label>
              <Textarea
                id="template-content"
                placeholder="使用 {{variableName}} 插入变量"
                rows={6}
                defaultValue={editingTemplate?.content}
                className="dark:bg-gray-700 dark:border-gray-600 dark:text-gray-200"
              />
            </div>
            <div>
              <Label htmlFor="template-variables" className="dark:text-gray-300">
                可用变量 (逗号分隔)
              </Label>
              <Input
                id="template-variables"
                placeholder="例如: userName, email, verificationLink"
                defaultValue={editingTemplate?.variables.join(', ')}
                className="dark:bg-gray-700 dark:border-gray-600 dark:text-gray-200"
              />
            </div>
          </div>
          <DialogFooter>
            <Button
              variant="outline"
              onClick={() => setShowTemplateDialog(false)}
              className="dark:border-gray-600 dark:text-gray-300 dark:hover:bg-gray-700"
            >
              取消
            </Button>
            <Button
              onClick={() => {
                toast.success(editingTemplate ? '模板已更新' : '模板已添加');
                setShowTemplateDialog(false);
              }}
              className="bg-blue-600 hover:bg-blue-700 text-white"
            >
              {editingTemplate ? '更新' : '添加'}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
