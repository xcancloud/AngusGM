import { useState } from 'react';
import { Search, Plus, MessageSquare, CheckCircle, XCircle, Send, Settings, Eye, Trash2, MoreVertical, Activity, Edit } from 'lucide-react';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Input } from '@/components/ui/input';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from '@/components/ui/dialog';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { ConfirmDialog } from '@/components/gm/ConfirmDialog';
import { SendSMSPage } from '@/pages/smsMessages/SendSMSPage';
import { Pagination } from '@/components/gm/Pagination';
import { toast } from 'sonner';

interface SMSMessage {
  id: string;
  phone: string;
  content: string;
  channel: string;
  status: '成功' | '失败' | '发送中';
  errorMsg?: string;
  sendTime: string;
  cost: number;
}

interface SMSChannel {
  id: string;
  name: string;
  type: '阿里云' | '腾讯云' | '华为云' | '自定义';
  accessKey: string;
  secretKey: string;
  signature: string;
  status: '启用' | '禁用';
  priority: number;
  totalSent: number;
  successRate: string;
}

interface SMSTemplate {
  id: string;
  name: string;
  code: string;
  thirdPartyId: string;
  channel: string;
  channelName: string;
  signature: string;
  content: string;
  variables: string[];
  createdAt: string;
  updatedAt: string;
  usageCount: number;
}

export function SMSMessageManagement() {
  const [currentView, setCurrentView] = useState<'list' | 'send'>('list');
  const [searchQuery, setSearchQuery] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [selectedChannel, setSelectedChannel] = useState('CH001'); // 默认选中第一个通道
  const [channelDialogOpen, setChannelDialogOpen] = useState(false);
  const [templateDialogOpen, setTemplateDialogOpen] = useState(false);
  const [viewDialogOpen, setViewDialogOpen] = useState(false);
  const [channelMode, setChannelMode] = useState<'add' | 'edit'>('add');
  const [templateMode, setTemplateMode] = useState<'add' | 'edit'>('add');
  const [selectedMessage, setSelectedMessage] = useState<SMSMessage | undefined>();
  const [selectedChannelEdit, setSelectedChannelEdit] = useState<SMSChannel | undefined>();
  const [selectedTemplate, setSelectedTemplate] = useState<SMSTemplate | undefined>();
  const [confirmDialogOpen, setConfirmDialogOpen] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState<{ type: 'message' | 'channel' | 'template', id: string } | null>(null);

  const [channelFormData, setChannelFormData] = useState({
    name: '',
    type: '阿里云' as SMSChannel['type'],
    accessKey: '',
    secretKey: '',
    signature: '',
    status: '启用' as SMSChannel['status'],
    priority: 1,
  });

  const [templateFormData, setTemplateFormData] = useState({
    name: '',
    code: '',
    thirdPartyId: '',
    signature: '',
    content: '',
    variables: '',
  });

  const stats = [
    {
      label: '总发送数',
      value: '45,678',
      subtext: '本月发送 3,456 条',
      icon: MessageSquare,
      iconBg: 'bg-blue-500',
      valueColor: 'text-blue-600 dark:text-blue-400',
      trend: '+18%',
    },
    {
      label: '发送成功',
      value: '44,235',
      subtext: '成功率 96.8%',
      icon: CheckCircle,
      iconBg: 'bg-green-500',
      valueColor: 'text-green-600 dark:text-green-400',
      trend: '96.8%',
    },
    {
      label: '发送失败',
      value: '1,443',
      subtext: '失败率 3.2%',
      icon: XCircle,
      iconBg: 'bg-red-500',
      valueColor: 'text-red-600 dark:text-red-400',
      trend: '3.2%',
    },
    {
      label: '今日发送',
      value: '156',
      subtext: '较昨日 +12',
      icon: Send,
      iconBg: 'bg-purple-500',
      valueColor: 'text-purple-600 dark:text-purple-400',
      trend: '+8%',
    },
  ];

  const messages: SMSMessage[] = [
    {
      id: 'M001',
      phone: '138****1234',
      content: '您的验证码是：123456，5分钟内有效。',
      channel: '华为云短信',
      status: '成功',
      sendTime: '2024-12-03 14:30:25',
      cost: 0.05,
    },
    {
      id: 'M002',
      phone: '139****5678',
      content: '您的业务申请已受理，我们将尽快处理。',
      channel: '华为云短信',
      status: '成功',
      sendTime: '2024-12-03 13:15:40',
      cost: 0.05,
    },
    {
      id: 'M003',
      phone: '136****9012',
      content: '系统维护通知：本周六2:00-6:00进行系统维护。',
      channel: '阿里云短信',
      status: '发送中',
      sendTime: '2024-12-03 12:45:10',
      cost: 0.05,
    },
  ];

  const channels: SMSChannel[] = [
    {
      id: 'CH001',
      name: '华为云短信',
      type: '华为云',
      accessKey: 'AKIAI***************',
      secretKey: '********************',
      signature: '华为中文',
      status: '启用',
      priority: 1,
      totalSent: 28945,
      successRate: '97.2%',
    },
    {
      id: 'CH002',
      name: '阿里云短信',
      type: '阿里云',
      accessKey: 'LTAI***************',
      secretKey: '********************',
      signature: 'AngusGM',
      status: '启用',
      priority: 2,
      totalSent: 12589,
      successRate: '96.5%',
    },
    {
      id: 'CH003',
      name: '腾讯云短信',
      type: '腾讯云',
      accessKey: 'AKIDz**************',
      secretKey: '********************',
      signature: 'AngusGM系统',
      status: '禁用',
      priority: 3,
      totalSent: 4144,
      successRate: '95.8%',
    },
  ];

  const templates: SMSTemplate[] = [
    {
      id: 'T001',
      name: '验证短信',
      code: 'VERIFICATION_CODE',
      thirdPartyId: '7d02ce037f1434d8a6baf0830a9e8042',
      channel: 'CH001',
      channelName: '华为云短信',
      signature: '华为云',
      content: '您的验证码是${code}，请在${time}分钟内完成验证，请勿泄露给第三方。如非本人操作，请忽略。',
      variables: ['code', 'time'],
      createdAt: '2024-11-20 10:00:00',
      updatedAt: '2024-12-01 14:30:00',
      usageCount: 5623,
    },
    {
      id: 'T002',
      name: '业务受理提交',
      code: 'ACCEPTANCE_SUBMIT',
      thirdPartyId: '9e23f45c7d6b4cd0a2e0d2410fc6ae8',
      channel: 'CH001',
      channelName: '华为云短信',
      signature: '华为云',
      content: '您的${object}已记录提交，我们会在${time}个工作日内联系您。',
      variables: ['object', 'time'],
      createdAt: '2024-11-22 15:20:00',
      updatedAt: '2024-11-22 15:20:00',
      usageCount: 1834,
    },
    {
      id: 'T003',
      name: '业务受理通过',
      code: 'ACCEPTANCE_PASSED',
      thirdPartyId: '41f921b794b4977a4e9e2b719a30d60',
      channel: 'CH001',
      channelName: '华为云短信',
      signature: '华为云',
      content: '您提交的${object}已审核通过，详情请登录系统查看并填报详细信息。',
      variables: ['object'],
      createdAt: '2024-11-25 09:00:00',
      updatedAt: '2024-11-28 11:15:00',
      usageCount: 1456,
    },
    {
      id: 'T004',
      name: '业务受理驳回',
      code: 'ACCEPTANCE_FAILURE',
      thirdPartyId: '7f5af36b04f11ae89f686b6e2e9c82f808',
      channel: 'CH001',
      channelName: '华为云短信',
      signature: '华为云',
      content: '您提交的${object}审核未通过，失败原因为：${failureReason}，请确认后重新提交。',
      variables: ['object', 'failureReason'],
      createdAt: '2024-11-28 10:30:00',
      updatedAt: '2024-11-28 10:30:00',
      usageCount: 892,
    },
    {
      id: 'T005',
      name: '系统异常通知',
      code: 'SYS_EXCEPTION_NOTICE',
      thirdPartyId: 'ea63a2db7e9e40d0fd1daf27c10ffce96d',
      channel: 'CH001',
      channelName: '华为云短信',
      signature: '华为云',
      content: '系统异常（${exception}）监测到异常告警，请及时处理！',
      variables: ['exception'],
      createdAt: '2024-12-01 08:30:00',
      updatedAt: '2024-12-01 08:30:00',
      usageCount: 234,
    },
    {
      id: 'T006',
      name: '系统恢复通知',
      code: 'SYS_RECOVERY_NOTICE',
      thirdPartyId: '7f9867d4f18bd2017f9e760c3b1deb',
      channel: 'CH001',
      channelName: '华为云短信',
      signature: '华为云',
      content: '系统异常（${exception}）的故障已恢复，感谢关注！',
      variables: ['exception'],
      createdAt: '2024-12-01 08:30:00',
      updatedAt: '2024-12-01 08:30:00',
      usageCount: 178,
    },
    {
      id: 'T007',
      name: '系统安全提醒',
      code: 'SYS_SECURITY_NOTICE',
      thirdPartyId: 'ea53a2db7e9e46d0a027d297c20f7de0e3',
      channel: 'CH001',
      channelName: '华为云短信',
      signature: '华为云',
      content: '系统安全提醒！${notice}',
      variables: ['notice'],
      createdAt: '2024-12-02 09:15:00',
      updatedAt: '2024-12-02 09:15:00',
      usageCount: 445,
    },
    {
      id: 'T008',
      name: '通道测试',
      code: 'CHANNEL_TEST',
      thirdPartyId: '9020c3197a079de8039392f18c6d201',
      channel: 'CH001',
      channelName: '华为云短信',
      signature: '华为云',
      content: '【${channelType}】请通道测试短信，测试通过为成功回执。',
      variables: ['channelType'],
      createdAt: '2024-12-03 10:00:00',
      updatedAt: '2024-12-03 10:00:00',
      usageCount: 89,
    },
    {
      id: 'T009',
      name: '操作任务提醒',
      code: 'OPERATION_TASK_REMINDER',
      thirdPartyId: '916f2169e00426f97a01916ff0716442',
      channel: 'CH001',
      channelName: '华为云短信',
      signature: '华为云',
      content: '请${company}于${deadlineTime}之前完成${operation}，超时未完成，将会${riskOfOther}。',
      variables: ['company', 'deadlineTime', 'operation', 'riskOfOther'],
      createdAt: '2024-12-03 11:20:00',
      updatedAt: '2024-12-03 11:20:00',
      usageCount: 567,
    },
  ];

  const filteredMessages = messages.filter(message =>
    message.phone.toLowerCase().includes(searchQuery.toLowerCase()) ||
    message.content.toLowerCase().includes(searchQuery.toLowerCase()) ||
    message.channel.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const filteredTemplates = templates.filter(t => t.channel === selectedChannel);

  // 分页计算
  const pageSize = 10;
  const totalPages = Math.max(1, Math.ceil(filteredMessages.length / pageSize));
  const paginatedMessages = filteredMessages.slice(
    (currentPage - 1) * pageSize,
    currentPage * pageSize
  );

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };

  const handleSendSMS = () => {
    setCurrentView('send');
  };

  const handleAddChannel = () => {
    setChannelMode('add');
    setSelectedChannelEdit(undefined);
    setChannelFormData({
      name: '',
      type: '阿里云',
      accessKey: '',
      secretKey: '',
      signature: '',
      status: '启用',
      priority: 1,
    });
    setChannelDialogOpen(true);
  };

  const handleEditChannel = (channel: SMSChannel) => {
    setChannelMode('edit');
    setSelectedChannelEdit(channel);
    setChannelFormData({
      name: channel.name,
      type: channel.type,
      accessKey: channel.accessKey,
      secretKey: channel.secretKey,
      signature: channel.signature,
      status: channel.status,
      priority: channel.priority,
    });
    setChannelDialogOpen(true);
  };

  const handleAddTemplate = () => {
    setTemplateMode('add');
    setSelectedTemplate(undefined);
    const currentChannel = channels.find(c => c.id === selectedChannel);
    setTemplateFormData({
      name: '',
      code: '',
      thirdPartyId: '',
      signature: currentChannel?.signature || '',
      content: '',
      variables: '',
    });
    setTemplateDialogOpen(true);
  };

  const handleEditTemplate = (template: SMSTemplate) => {
    setTemplateMode('edit');
    setSelectedTemplate(template);
    setTemplateFormData({
      name: template.name,
      code: template.code,
      thirdPartyId: template.thirdPartyId,
      signature: template.signature,
      content: template.content,
      variables: template.variables.join(', '),
    });
    setTemplateDialogOpen(true);
  };

  const handleViewMessage = (message: SMSMessage) => {
    setSelectedMessage(message);
    setViewDialogOpen(true);
  };

  const handleDelete = (type: 'message' | 'channel' | 'template', id: string) => {
    setDeleteTarget({ type, id });
    setConfirmDialogOpen(true);
  };

  const confirmDelete = () => {
    if (deleteTarget) {
      const typeText = deleteTarget.type === 'message' ? '短信记录' : 
                       deleteTarget.type === 'channel' ? '短信通道' : '短信模板';
      toast.success(`删除${typeText}成功`);
      setDeleteTarget(null);
    }
  };

  const handleChannelSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!channelFormData.name.trim()) {
      toast.error('请输入通道名称');
      return;
    }
    if (!channelFormData.accessKey.trim()) {
      toast.error('请输入AccessKey');
      return;
    }
    if (!channelFormData.secretKey.trim()) {
      toast.error('请输入SecretKey');
      return;
    }
    if (!channelFormData.signature.trim()) {
      toast.error('请输入短信签名');
      return;
    }

    const actionText = channelMode === 'add' ? '添加' : '更新';
    toast.success(`${actionText}短信通道成功`);
    setChannelDialogOpen(false);
  };

  const handleTemplateSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!templateFormData.name.trim()) {
      toast.error('请输入模板名称');
      return;
    }
    if (!templateFormData.code.trim()) {
      toast.error('请输入模板编码');
      return;
    }
    if (!templateFormData.thirdPartyId.trim()) {
      toast.error('请输入三方模板ID');
      return;
    }
    if (!templateFormData.content.trim()) {
      toast.error('请输入模板内容');
      return;
    }

    const actionText = templateMode === 'add' ? '添加' : '更新';
    toast.success(`${actionText}短信模板成功`);
    setTemplateDialogOpen(false);
  };

  const getStatusBadge = (status: SMSMessage['status']) => {
    const styles = {
      '成功': 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400',
      '失败': 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400',
      '发送中': 'bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-400',
    };
    return <Badge className={`${styles[status]} border-0`}>{status}</Badge>;
  };

  const getChannelStatusBadge = (status: SMSChannel['status']) => {
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

  if (currentView === 'send') {
    return (
      <SendSMSPage
        onBack={() => setCurrentView('list')}
        channels={channels}
        templates={templates}
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

      {/* 标签页 */}
      <Tabs defaultValue="records" className="space-y-6">
        <TabsList className="dark:bg-gray-800 dark:border-gray-700">
          <TabsTrigger value="records" className="dark:data-[state=active]:bg-gray-700">
            <MessageSquare className="w-4 h-4 mr-2" />
            短信记录
          </TabsTrigger>
          <TabsTrigger value="templates" className="dark:data-[state=active]:bg-gray-700">
            <Activity className="w-4 h-4 mr-2" />
            短信模板
          </TabsTrigger>
          <TabsTrigger value="channels" className="dark:data-[state=active]:bg-gray-700">
            <Settings className="w-4 h-4 mr-2" />
            通道配置
          </TabsTrigger>
        </TabsList>

        {/* 短信记录标签 */}
        <TabsContent value="records" className="space-y-6">
          {/* 搜索和发送 */}
          <Card className="p-6 dark:bg-gray-800 dark:border-gray-700">
            <div className="flex items-center justify-between">
              <div className="relative w-96">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
                <Input
                  placeholder="搜索手机号、内容或通道..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="pl-10 dark:bg-gray-900 dark:border-gray-700 dark:text-white"
                />
              </div>
              <Button
                className="bg-blue-600 hover:bg-blue-700 text-white"
                onClick={handleSendSMS}
              >
                <Plus className="w-4 h-4 mr-2" />
                发送短信
              </Button>
            </div>
          </Card>

          {/* 短信记录列表 */}
          <Card className="dark:bg-gray-800 dark:border-gray-700">
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="border-b dark:border-gray-700 bg-gray-50 dark:bg-gray-900/50">
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">手机号</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">短信内容</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">通道</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">状态</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">费用</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">发送时间</th>
                    <th className="text-right p-4 text-sm text-gray-600 dark:text-gray-400">操作</th>
                  </tr>
                </thead>
                <tbody>
                  {paginatedMessages.map((message) => (
                    <tr
                      key={message.id}
                      className="border-b dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-700"
                    >
                      <td className="p-4 text-sm text-gray-700 dark:text-gray-300">
                        {message.phone}
                      </td>
                      <td className="p-4">
                        <div className="text-sm text-gray-700 dark:text-gray-300 max-w-md truncate">
                          {message.content}
                        </div>
                      </td>
                      <td className="p-4 text-sm text-gray-700 dark:text-gray-300">
                        {message.channel}
                      </td>
                      <td className="p-4">
                        <div className="space-y-1">
                          {getStatusBadge(message.status)}
                          {message.errorMsg && (
                            <div className="text-xs text-red-600 dark:text-red-400">
                              {message.errorMsg}
                            </div>
                          )}
                        </div>
                      </td>
                      <td className="p-4 text-sm text-gray-700 dark:text-gray-300">
                        ¥{message.cost.toFixed(2)}
                      </td>
                      <td className="p-4 text-sm text-gray-700 dark:text-gray-300">
                        {message.sendTime}
                      </td>
                      <td className="p-4">
                        <div className="flex justify-end gap-2">
                          <button
                            onClick={() => handleViewMessage(message)}
                            className="p-1.5 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg"
                          >
                            <Eye className="w-4 h-4 text-gray-600 dark:text-gray-400" />
                          </button>
                          <button
                            onClick={() => handleDelete('message', message.id)}
                            className="p-1.5 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg"
                          >
                            <Trash2 className="w-4 h-4 text-red-600 dark:text-red-400" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
            <Pagination
              currentPage={currentPage}
              totalPages={totalPages}
              onPageChange={handlePageChange}
              totalItems={filteredMessages.length}
              pageSize={pageSize}
            />
          </Card>
        </TabsContent>

        {/* 短信模板标签 */}
        <TabsContent value="templates" className="space-y-6">
          {/* 通道选择和添加模板 */}
          <Card className="p-6 dark:bg-gray-800 dark:border-gray-700">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-4">
                <Select
                  value={selectedChannel}
                  onValueChange={setSelectedChannel}
                >
                  <SelectTrigger className="w-64 dark:bg-gray-900 dark:border-gray-700 dark:text-white">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent className="dark:bg-gray-900 dark:border-gray-700">
                    {channels.map((channel) => (
                      <SelectItem key={channel.id} value={channel.id}>
                        {channel.name}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
                <div className="text-sm text-gray-500 dark:text-gray-400">
                  共 {filteredTemplates.length} 个模板
                </div>
              </div>
              <Button
                className="bg-blue-600 hover:bg-blue-700 text-white"
                onClick={handleAddTemplate}
              >
                <Plus className="w-4 h-4 mr-2" />
                新建模板
              </Button>
            </div>
          </Card>

          {/* 模板列表 */}
          <Card className="dark:bg-gray-800 dark:border-gray-700">
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="border-b dark:border-gray-700 bg-gray-50 dark:bg-gray-900/50">
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">模板名称</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">编码</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400 w-48">三方模版ID</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">模版</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">签名</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400 w-80">模板内容</th>
                    <th className="text-right p-4 text-sm text-gray-600 dark:text-gray-400">操作</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredTemplates.map((template) => (
                    <tr
                      key={template.id}
                      className="border-b dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-700"
                    >
                      <td className="p-4">
                        <div className="dark:text-white">{template.name}</div>
                      </td>
                      <td className="p-4 text-sm text-gray-700 dark:text-gray-300">
                        {template.code}
                      </td>
                      <td className="p-4 text-sm text-gray-700 dark:text-gray-300 font-mono w-48">
                        <div className="break-all">{template.thirdPartyId}</div>
                      </td>
                      <td className="p-4">
                        <Badge
                          variant="outline"
                          className="dark:border-gray-600 dark:text-gray-400"
                        >
                          {template.channelName}
                        </Badge>
                      </td>
                      <td className="p-4 text-sm text-gray-700 dark:text-gray-300">
                        {template.signature}
                      </td>
                      <td className="p-4 text-sm text-gray-700 dark:text-gray-300 w-80">
                        <div className="break-words whitespace-normal">{template.content}</div>
                      </td>
                      <td className="p-4">
                        <div className="flex justify-end gap-2">
                          <button
                            onClick={() => handleEditTemplate(template)}
                            className="p-1.5 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg"
                          >
                            <Edit className="w-4 h-4 text-gray-600 dark:text-gray-400" />
                          </button>
                          <button
                            onClick={() => handleDelete('template', template.id)}
                            className="p-1.5 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg"
                          >
                            <Trash2 className="w-4 h-4 text-red-600 dark:text-red-400" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </Card>
        </TabsContent>

        {/* 通道配置标签 */}
        <TabsContent value="channels" className="space-y-6">
          {/* 添加通道按钮 */}
          <Card className="p-6 dark:bg-gray-800 dark:border-gray-700">
            <div className="flex items-center justify-between">
              <div>
                <h3 className="text-lg dark:text-white mb-1">短信通道配置</h3>
                <p className="text-sm text-gray-500 dark:text-gray-400">
                  配置短信发送通道，支持多通道智能切换
                </p>
              </div>
              <Button
                className="bg-blue-600 hover:bg-blue-700 text-white"
                onClick={handleAddChannel}
              >
                <Plus className="w-4 h-4 mr-2" />
                添加通道
              </Button>
            </div>
          </Card>

          {/* 通道列表 */}
          <Card className="dark:bg-gray-800 dark:border-gray-700">
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="border-b dark:border-gray-700 bg-gray-50 dark:bg-gray-900/50">
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">通道名称</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">类型</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">AccessKey</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">签名</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">优先级</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">状态</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">总发送量</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">成功率</th>
                    <th className="text-right p-4 text-sm text-gray-600 dark:text-gray-400">操作</th>
                  </tr>
                </thead>
                <tbody>
                  {channels.map((channel) => (
                    <tr
                      key={channel.id}
                      className="border-b dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-700"
                    >
                      <td className="p-4">
                        <div className="dark:text-white">{channel.name}</div>
                      </td>
                      <td className="p-4">
                        <Badge
                          variant="outline"
                          className="dark:border-gray-600 dark:text-gray-400"
                        >
                          {channel.type}
                        </Badge>
                      </td>
                      <td className="p-4 text-sm text-gray-700 dark:text-gray-300 font-mono">
                        {channel.accessKey}
                      </td>
                      <td className="p-4 text-sm text-gray-700 dark:text-gray-300">
                        {channel.signature}
                      </td>
                      <td className="p-4 text-sm text-gray-700 dark:text-gray-300">
                        {channel.priority}
                      </td>
                      <td className="p-4">
                        {getChannelStatusBadge(channel.status)}
                      </td>
                      <td className="p-4 text-sm text-gray-700 dark:text-gray-300">
                        {channel.totalSent.toLocaleString()}
                      </td>
                      <td className="p-4 text-sm text-green-600 dark:text-green-400">
                        {channel.successRate}
                      </td>
                      <td className="p-4">
                        <div className="flex justify-end">
                          <DropdownMenu>
                            <DropdownMenuTrigger asChild>
                              <button className="p-1.5 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg">
                                <MoreVertical className="w-4 h-4 text-gray-600 dark:text-gray-400" />
                              </button>
                            </DropdownMenuTrigger>
                            <DropdownMenuContent align="end" className="dark:bg-gray-900 dark:border-gray-700">
                              <DropdownMenuItem
                                onClick={() => handleEditChannel(channel)}
                                className="dark:text-gray-300 dark:hover:bg-gray-800"
                              >
                                <Settings className="w-4 h-4 mr-2" />
                                编辑配置
                              </DropdownMenuItem>
                              <DropdownMenuItem
                                onClick={() => handleDelete('channel', channel.id)}
                                className="text-red-600 dark:text-red-400 dark:hover:bg-gray-800"
                              >
                                <Trash2 className="w-4 h-4 mr-2" />
                                删除
                              </DropdownMenuItem>
                            </DropdownMenuContent>
                          </DropdownMenu>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </Card>
        </TabsContent>
      </Tabs>

      {/* 查看短信对话框 */}
      <Dialog open={viewDialogOpen} onOpenChange={setViewDialogOpen}>
        <DialogContent className="max-w-2xl dark:bg-gray-800 dark:border-gray-700">
          <DialogHeader>
            <DialogTitle className="dark:text-white">短信详情</DialogTitle>
          </DialogHeader>
          {selectedMessage && (
            <div className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <Label className="text-sm text-gray-600 dark:text-gray-400">手机号</Label>
                  <div className="text-sm dark:text-white mt-1">{selectedMessage.phone}</div>
                </div>
                <div>
                  <Label className="text-sm text-gray-600 dark:text-gray-400">通道</Label>
                  <div className="text-sm dark:text-white mt-1">{selectedMessage.channel}</div>
                </div>
                <div>
                  <Label className="text-sm text-gray-600 dark:text-gray-400">状态</Label>
                  <div className="mt-1">{getStatusBadge(selectedMessage.status)}</div>
                </div>
                <div>
                  <Label className="text-sm text-gray-600 dark:text-gray-400">费用</Label>
                  <div className="text-sm dark:text-white mt-1">¥{selectedMessage.cost.toFixed(2)}</div>
                </div>
                <div className="col-span-2">
                  <Label className="text-sm text-gray-600 dark:text-gray-400">发送时间</Label>
                  <div className="text-sm dark:text-white mt-1">{selectedMessage.sendTime}</div>
                </div>
              </div>
              <div>
                <Label className="text-sm text-gray-600 dark:text-gray-400">短信内容</Label>
                <div className="text-sm dark:text-white mt-1 p-4 bg-gray-50 dark:bg-gray-900 rounded-lg">
                  {selectedMessage.content}
                </div>
              </div>
              {selectedMessage.errorMsg && (
                <div className="bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg p-4">
                  <Label className="text-sm text-red-800 dark:text-red-400">错误信息</Label>
                  <div className="text-sm text-red-600 dark:text-red-300 mt-1">{selectedMessage.errorMsg}</div>
                </div>
              )}
            </div>
          )}
        </DialogContent>
      </Dialog>

      {/* 通道配置对话框 */}
      <Dialog open={channelDialogOpen} onOpenChange={setChannelDialogOpen}>
        <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto dark:bg-gray-800 dark:border-gray-700">
          <DialogHeader>
            <DialogTitle className="dark:text-white">
              {channelMode === 'add' ? '添加短信通道' : '编辑短信通道'}
            </DialogTitle>
            <DialogDescription className="dark:text-gray-400">
              配置短信发送通道信息
            </DialogDescription>
          </DialogHeader>

          <form onSubmit={handleChannelSubmit} className="space-y-6">
            <div className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label className="dark:text-gray-300">
                    通道名称 <span className="text-red-500">*</span>
                  </Label>
                  <Input
                    value={channelFormData.name}
                    onChange={(e) => setChannelFormData({ ...channelFormData, name: e.target.value })}
                    placeholder="例如：阿里云短信"
                    className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
                  />
                </div>

                <div className="space-y-2">
                  <Label className="dark:text-gray-300">
                    通道类型 <span className="text-red-500">*</span>
                  </Label>
                  <Select
                    value={channelFormData.type}
                    onValueChange={(value: any) => setChannelFormData({ ...channelFormData, type: value })}
                  >
                    <SelectTrigger className="dark:bg-gray-900 dark:border-gray-700 dark:text-white">
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent className="dark:bg-gray-900 dark:border-gray-700">
                      <SelectItem value="阿里云">阿里云</SelectItem>
                      <SelectItem value="腾讯云">腾讯云</SelectItem>
                      <SelectItem value="华为云">华为云</SelectItem>
                      <SelectItem value="自定义">自定义</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
              </div>

              <div className="space-y-2">
                <Label className="dark:text-gray-300">
                  AccessKey <span className="text-red-500">*</span>
                </Label>
                <Input
                  value={channelFormData.accessKey}
                  onChange={(e) => setChannelFormData({ ...channelFormData, accessKey: e.target.value })}
                  placeholder="请输入AccessKey"
                  className="dark:bg-gray-900 dark:border-gray-700 dark:text-white font-mono"
                />
              </div>

              <div className="space-y-2">
                <Label className="dark:text-gray-300">
                  SecretKey <span className="text-red-500">*</span>
                </Label>
                <Input
                  type="password"
                  value={channelFormData.secretKey}
                  onChange={(e) => setChannelFormData({ ...channelFormData, secretKey: e.target.value })}
                  placeholder="请输入SecretKey"
                  className="dark:bg-gray-900 dark:border-gray-700 dark:text-white font-mono"
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label className="dark:text-gray-300">
                    短信签名 <span className="text-red-500">*</span>
                  </Label>
                  <Input
                    value={channelFormData.signature}
                    onChange={(e) => setChannelFormData({ ...channelFormData, signature: e.target.value })}
                    placeholder="例如：AngusGM"
                    className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
                  />
                </div>

                <div className="space-y-2">
                  <Label className="dark:text-gray-300">优先级</Label>
                  <Input
                    type="number"
                    value={channelFormData.priority}
                    onChange={(e) => setChannelFormData({ ...channelFormData, priority: parseInt(e.target.value) })}
                    placeholder="1"
                    className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
                  />
                  <p className="text-xs text-gray-500 dark:text-gray-400">
                    数字越小优先级越高
                  </p>
                </div>
              </div>

              <div className="space-y-2">
                <Label className="dark:text-gray-300">状态</Label>
                <Select
                  value={channelFormData.status}
                  onValueChange={(value: any) => setChannelFormData({ ...channelFormData, status: value })}
                >
                  <SelectTrigger className="dark:bg-gray-900 dark:border-gray-700 dark:text-white">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent className="dark:bg-gray-900 dark:border-gray-700">
                    <SelectItem value="启用">启用</SelectItem>
                    <SelectItem value="禁用">禁用</SelectItem>
                  </SelectContent>
                </Select>
              </div>
            </div>

            <div className="flex justify-end gap-3 pt-4 border-t dark:border-gray-700">
              <Button
                type="button"
                variant="outline"
                onClick={() => setChannelDialogOpen(false)}
                className="dark:bg-gray-900 dark:border-gray-700 dark:text-gray-300"
              >
                取消
              </Button>
              <Button
                type="submit"
                className="bg-blue-600 hover:bg-blue-700 text-white"
              >
                {channelMode === 'add' ? '添加' : '保存'}
              </Button>
            </div>
          </form>
        </DialogContent>
      </Dialog>

      {/* 模板配置对话框 */}
      <Dialog open={templateDialogOpen} onOpenChange={setTemplateDialogOpen}>
        <DialogContent className="max-w-3xl max-h-[90vh] overflow-y-auto dark:bg-gray-800 dark:border-gray-700">
          <DialogHeader>
            <DialogTitle className="dark:text-white">
              {templateMode === 'add' ? '新建短信模板' : '编辑短信模板'}
            </DialogTitle>
            <DialogDescription className="dark:text-gray-400">
              创建短信模板，使用 ${'{变量名}'} 作为占位符
            </DialogDescription>
          </DialogHeader>

          <form onSubmit={handleTemplateSubmit} className="space-y-6">
            <div className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label className="dark:text-gray-300">
                    模板名称 <span className="text-red-500">*</span>
                  </Label>
                  <Input
                    value={templateFormData.name}
                    onChange={(e) => setTemplateFormData({ ...templateFormData, name: e.target.value })}
                    placeholder="例如：验证短信"
                    className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
                  />
                </div>

                <div className="space-y-2">
                  <Label className="dark:text-gray-300">
                    模板编码 <span className="text-red-500">*</span>
                  </Label>
                  <Input
                    value={templateFormData.code}
                    onChange={(e) => setTemplateFormData({ ...templateFormData, code: e.target.value })}
                    placeholder="例如：VERIFICATION_CODE"
                    className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
                  />
                </div>
              </div>

              <div className="space-y-2">
                <Label className="dark:text-gray-300">
                  三方模板ID <span className="text-red-500">*</span>
                </Label>
                <Input
                  value={templateFormData.thirdPartyId}
                  onChange={(e) => setTemplateFormData({ ...templateFormData, thirdPartyId: e.target.value })}
                  placeholder="请输入云服务商提供的模板ID"
                  className="dark:bg-gray-900 dark:border-gray-700 dark:text-white font-mono"
                />
              </div>

              <div className="space-y-2">
                <Label className="dark:text-gray-300">
                  短信签名 <span className="text-red-500">*</span>
                </Label>
                <Input
                  value={templateFormData.signature}
                  onChange={(e) => setTemplateFormData({ ...templateFormData, signature: e.target.value })}
                  placeholder="例如：华为云"
                  className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
                />
              </div>

              <div className="space-y-2">
                <Label className="dark:text-gray-300">
                  模板内容 <span className="text-red-500">*</span>
                </Label>
                <Textarea
                  value={templateFormData.content}
                  onChange={(e) => setTemplateFormData({ ...templateFormData, content: e.target.value })}
                  placeholder="请输入模板内容，使用 ${变量名} 作为占位符，例如: ${code}, ${time} 等"
                  rows={5}
                  className="dark:bg-gray-900 dark:border-gray-700 dark:text-white resize-none"
                />
              </div>

              <div className="space-y-2">
                <Label className="dark:text-gray-300">变量列表</Label>
                <Input
                  value={templateFormData.variables}
                  onChange={(e) => setTemplateFormData({ ...templateFormData, variables: e.target.value })}
                  placeholder="多个变量用逗号分隔，例如: code, time, userName"
                  className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
                />
                <p className="text-xs text-gray-500 dark:text-gray-400">
                  在模板内容中使用 ${'{变量名}'} 格式引用变量
                </p>
              </div>
            </div>

            <div className="flex justify-end gap-3 pt-4 border-t dark:border-gray-700">
              <Button
                type="button"
                variant="outline"
                onClick={() => setTemplateDialogOpen(false)}
                className="dark:bg-gray-900 dark:border-gray-700 dark:text-gray-300"
              >
                取消
              </Button>
              <Button
                type="submit"
                className="bg-blue-600 hover:bg-blue-700 text-white"
              >
                {templateMode === 'add' ? '创建' : '保存'}
              </Button>
            </div>
          </form>
        </DialogContent>
      </Dialog>

      {/* 确认删除对话框 */}
      <ConfirmDialog
        open={confirmDialogOpen}
        onOpenChange={setConfirmDialogOpen}
        onConfirm={confirmDelete}
        title={`确认删除${
          deleteTarget?.type === 'message' ? '短信记录' :
          deleteTarget?.type === 'channel' ? '短信通道' : '短信模板'
        }`}
        description={`此操作无法撤销，确定要删除这条${
          deleteTarget?.type === 'message' ? '短信记录' :
          deleteTarget?.type === 'channel' ? '短信通道' : '短信模板'
        }吗？`}
      />
    </div>
  );
}