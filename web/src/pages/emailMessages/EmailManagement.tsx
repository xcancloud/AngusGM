import { useState } from 'react';
import { Search, Plus, Mail, CheckCircle, XCircle, Send, Server, Eye, Trash2, MoreVertical, Settings, FileText, Edit, Copy, ChevronLeft, ChevronRight } from 'lucide-react';
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
import { SendEmailPage } from '@/pages/emailMessages/SendEmailPage';
import { Pagination } from '@/components/gm/Pagination';
import { toast } from 'sonner';

interface EmailMessage {
  id: string;
  recipient: string;
  subject: string;
  content: string;
  server: string;
  status: '成功' | '失败' | '发送中';
  errorMsg?: string;
  sendTime: string;
  hasAttachment: boolean;
}

interface EmailServer {
  id: string;
  name: string;
  smtpHost: string;
  smtpPort: number;
  encryption: 'SSL' | 'TLS' | '无';
  username: string;
  password: string;
  fromEmail: string;
  fromName: string;
  status: '启用' | '禁用';
  totalSent: number;
  successRate: string;
}

interface EmailTemplate {
  id: string;
  name: string;
  code: string;
  subject: string;
  content: string;
  variables: string[];
  createdAt: string;
  updatedAt: string;
  usageCount: number;
}

export function EmailManagement() {
  const [currentView, setCurrentView] = useState<'list' | 'send'>('list');
  const [searchQuery, setSearchQuery] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [serverDialogOpen, setServerDialogOpen] = useState(false);
  const [templateDialogOpen, setTemplateDialogOpen] = useState(false);
  const [viewDialogOpen, setViewDialogOpen] = useState(false);
  const [serverMode, setServerMode] = useState<'add' | 'edit'>('add');
  const [templateMode, setTemplateMode] = useState<'add' | 'edit'>('add');
  const [selectedEmail, setSelectedEmail] = useState<EmailMessage | undefined>();
  const [selectedServer, setSelectedServer] = useState<EmailServer | undefined>();
  const [selectedTemplate, setSelectedTemplate] = useState<EmailTemplate | undefined>();
  const [confirmDialogOpen, setConfirmDialogOpen] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState<{ type: 'email' | 'server' | 'template', id: string } | null>(null);

  const [serverFormData, setServerFormData] = useState({
    name: '',
    smtpHost: '',
    smtpPort: 465,
    encryption: 'SSL',
    username: '',
    password: '',
    fromEmail: '',
    fromName: '',
    status: '启用',
  });

  const [templateFormData, setTemplateFormData] = useState({
    name: '',
    code: '',
    subject: '',
    content: '',
    variables: '',
  });

  const stats = [
    {
      label: '总发送数',
      value: '23,456',
      subtext: '本月发送 1,234 封',
      icon: Mail,
      iconBg: 'bg-blue-500',
      valueColor: 'text-blue-600 dark:text-blue-400',
      trend: '+15%',
    },
    {
      label: '发送成功',
      value: '22,789',
      subtext: '成功率 97.2%',
      icon: CheckCircle,
      iconBg: 'bg-green-500',
      valueColor: 'text-green-600 dark:text-green-400',
      trend: '97.2%',
    },
    {
      label: '发送失败',
      value: '667',
      subtext: '失败率 2.8%',
      icon: XCircle,
      iconBg: 'bg-red-500',
      valueColor: 'text-red-600 dark:text-red-400',
      trend: '2.8%',
    },
    {
      label: '今日发送',
      value: '89',
      subtext: '较昨日 +5',
      icon: Send,
      iconBg: 'bg-purple-500',
      valueColor: 'text-purple-600 dark:text-purple-400',
      trend: '+6%',
    },
  ];

  const emails: EmailMessage[] = [
    {
      id: 'E001',
      recipient: 'zhangsan@example.com',
      subject: '系统维护通知',
      content: '尊敬的用户，系统将于本周六凌晨2:00-6:00进行维护升级...',
      server: '阿里云邮件服务',
      status: '成功',
      sendTime: '2024-12-03 10:30:15',
      hasAttachment: false,
    },
    {
      id: 'E002',
      recipient: 'lisi@example.com',
      subject: '账号安全提醒',
      content: '您的账号在异地登录，请确认是否为本人操作...',
      server: '腾讯企业邮箱',
      status: '成功',
      sendTime: '2024-12-03 09:15:20',
      hasAttachment: true,
    },
    {
      id: 'E003',
      recipient: 'wangwu@example.com',
      subject: '备份任务完成通知',
      content: '系统备份任务已成功完成，备份文件大小: 2.5GB...',
      server: '阿里云邮件服务',
      status: '成功',
      sendTime: '2024-12-03 08:45:10',
      hasAttachment: true,
    },
    {
      id: 'E004',
      recipient: 'zhaoliu@invalid',
      subject: '密码重置通知',
      content: '您的密码已重置，请使用新密码登录...',
      server: '网易企业邮箱',
      status: '失败',
      errorMsg: '邮箱地址格式错误',
      sendTime: '2024-12-02 16:20:30',
      hasAttachment: false,
    },
  ];

  const servers: EmailServer[] = [
    {
      id: 'S001',
      name: '阿里云邮件服务',
      smtpHost: 'smtp.aliyun.com',
      smtpPort: 465,
      encryption: 'SSL',
      username: 'noreply@angus.com',
      password: '********************',
      fromEmail: 'noreply@angus.com',
      fromName: 'AngusGM系统',
      status: '启用',
      totalSent: 15678,
      successRate: '97.5%',
    },
    {
      id: 'S002',
      name: '腾讯企业邮箱',
      smtpHost: 'smtp.exmail.qq.com',
      smtpPort: 465,
      encryption: 'SSL',
      username: 'service@angus.com',
      password: '********************',
      fromEmail: 'service@angus.com',
      fromName: 'AngusGM客服',
      status: '启用',
      totalSent: 6234,
      successRate: '96.8%',
    },
    {
      id: 'S003',
      name: '网易企业邮箱',
      smtpHost: 'smtp.ym.163.com',
      smtpPort: 465,
      encryption: 'SSL',
      username: 'admin@angus.com',
      password: '********************',
      fromEmail: 'admin@angus.com',
      fromName: 'AngusGM管理员',
      status: '禁用',
      totalSent: 1544,
      successRate: '95.2%',
    },
  ];

  const templates: EmailTemplate[] = [
    {
      id: 'T001',
      name: '登录验证码',
      code: 'login_verification',
      subject: '【AngusGM】您的登录验证码',
      content: `<div style="font-family: Arial, sans-serif; padding: 20px; max-width: 600px; margin: 0 auto;">
  <h2 style="color: #2563eb;">登录验证码</h2>
  <p>您好，</p>
  <p>您正在登录 AngusGM 系统，验证码为：</p>
  <div style="background: #f3f4f6; padding: 15px; border-radius: 8px; text-align: center; margin: 20px 0;">
    <span style="font-size: 32px; font-weight: bold; color: #2563eb; letter-spacing: 5px;">{{code}}</span>
  </div>
  <p>该验证码将在 <strong>5分钟</strong> 后失效，请尽快使用。</p>
  <p style="color: #ef4444;">如果这不是您的操作，请忽略此邮件。</p>
  <hr style="margin: 30px 0; border: none; border-top: 1px solid #e5e7eb;">
  <p style="color: #6b7280; font-size: 12px;">此邮件由系统自动发送，请勿回复。</p>
</div>`,
      variables: ['code'],
      createdAt: '2024-11-20 10:00:00',
      updatedAt: '2024-12-01 14:30:00',
      usageCount: 1256,
    },
    {
      id: 'T002',
      name: '注册验证码',
      code: 'registration_verification',
      subject: '【AngusGM】欢迎注册',
      content: `<div style="font-family: Arial, sans-serif; padding: 20px; max-width: 600px; margin: 0 auto;">
  <h2 style="color: #2563eb;">欢迎注册 AngusGM</h2>
  <p>您好，</p>
  <p>感谢您注册 AngusGM 全局管理平台，您的验证码为：</p>
  <div style="background: #f3f4f6; padding: 15px; border-radius: 8px; text-align: center; margin: 20px 0;">
    <span style="font-size: 32px; font-weight: bold; color: #2563eb; letter-spacing: 5px;">{{code}}</span>
  </div>
  <p>验证码有效期为 <strong>10分钟</strong>。</p>
  <hr style="margin: 30px 0; border: none; border-top: 1px solid #e5e7eb;">
  <p style="color: #6b7280; font-size: 12px;">此邮件由系统自动发送，请勿回复。</p>
</div>`,
      variables: ['code'],
      createdAt: '2024-11-20 10:00:00',
      updatedAt: '2024-11-20 10:00:00',
      usageCount: 845,
    },
    {
      id: 'T003',
      name: '邀请用户注册',
      code: 'user_invitation',
      subject: '【AngusGM】您收到了一个系统邀请',
      content: `<div style="font-family: Arial, sans-serif; padding: 20px; max-width: 600px; margin: 0 auto;">
  <h2 style="color: #2563eb;">系统邀请</h2>
  <p>您好，{{userName}}</p>
  <p>管理员 <strong>{{inviterName}}</strong> 邀请您加入 <strong>{{tenantName}}</strong> 租户。</p>
  <p>角色: <strong>{{roleName}}</strong></p>
  <p>点击下方按钮完成注册：</p>
  <div style="text-align: center; margin: 30px 0;">
    <a href="{{inviteLink}}" style="background: #2563eb; color: white; padding: 12px 30px; text-decoration: none; border-radius: 6px; display: inline-block;">
      接受邀请
    </a>
  </div>
  <p style="color: #6b7280; font-size: 14px;">链接有效期为 7 天。</p>
  <hr style="margin: 30px 0; border: none; border-top: 1px solid #e5e7eb;">
  <p style="color: #6b7280; font-size: 12px;">此邮件由系统自动发送，请勿回复。</p>
</div>`,
      variables: ['userName', 'inviterName', 'tenantName', 'roleName', 'inviteLink'],
      createdAt: '2024-11-22 15:20:00',
      updatedAt: '2024-11-22 15:20:00',
      usageCount: 423,
    },
    {
      id: 'T004',
      name: '系统告警通知',
      code: 'system_alert',
      subject: '【AngusGM】系统告警 - {{alertLevel}}',
      content: `<div style="font-family: Arial, sans-serif; padding: 20px; max-width: 600px; margin: 0 auto;">
  <h2 style="color: #ef4444;">⚠️ 系统告警</h2>
  <div style="background: #fef2f2; border-left: 4px solid #ef4444; padding: 15px; margin: 20px 0;">
    <p style="margin: 0;"><strong>告警级别:</strong> <span style="color: #ef4444;">{{alertLevel}}</span></p>
    <p style="margin: 10px 0 0 0;"><strong>告警内容:</strong> {{alertMessage}}</p>
  </div>
  <p><strong>发生时间:</strong> {{alertTime}}</p>
  <p><strong>影响范围:</strong> {{affectedScope}}</p>
  <p><strong>建议操作:</strong> {{suggestedAction}}</p>
  <hr style="margin: 30px 0; border: none; border-top: 1px solid #e5e7eb;">
  <p style="color: #6b7280; font-size: 12px;">此邮件由系统自动发送，请勿回复。</p>
</div>`,
      variables: ['alertLevel', 'alertMessage', 'alertTime', 'affectedScope', 'suggestedAction'],
      createdAt: '2024-11-25 09:00:00',
      updatedAt: '2024-11-28 11:15:00',
      usageCount: 187,
    },
    {
      id: 'T005',
      name: '备份任务完成',
      code: 'backup_completion',
      subject: '【AngusGM】备份任务完成通知',
      content: `<div style="font-family: Arial, sans-serif; padding: 20px; max-width: 600px; margin: 0 auto;">
  <h2 style="color: #10b981;">✓ 备份任务完成</h2>
  <p>系统备份任务已成功完成。</p>
  <div style="background: #f0fdf4; border-left: 4px solid #10b981; padding: 15px; margin: 20px 0;">
    <p style="margin: 0;"><strong>备份名称:</strong> {{backupName}}</p>
    <p style="margin: 10px 0 0 0;"><strong>备份类型:</strong> {{backupType}}</p>
    <p style="margin: 10px 0 0 0;"><strong>备份大小:</strong> {{backupSize}}</p>
    <p style="margin: 10px 0 0 0;"><strong>完成时间:</strong> {{completeTime}}</p>
    <p style="margin: 10px 0 0 0;"><strong>耗时:</strong> {{duration}}</p>
  </div>
  <p><strong>存储路径:</strong> {{storagePath}}</p>
  <hr style="margin: 30px 0; border: none; border-top: 1px solid #e5e7eb;">
  <p style="color: #6b7280; font-size: 12px;">此邮件由系统自动发送，请勿回复。</p>
</div>`,
      variables: ['backupName', 'backupType', 'backupSize', 'completeTime', 'duration', 'storagePath'],
      createdAt: '2024-12-01 08:30:00',
      updatedAt: '2024-12-01 08:30:00',
      usageCount: 234,
    },
    {
      id: 'T006',
      name: '数据恢复完成',
      code: 'data_recovery_completion',
      subject: '【AngusGM】数据恢复完成通知',
      content: `<div style="font-family: Arial, sans-serif; padding: 20px; max-width: 600px; margin: 0 auto;">
  <h2 style="color: #10b981;">✓ 数据恢复完成</h2>
  <p>系统数据恢复任务已成功完成。</p>
  <div style="background: #f0fdf4; border-left: 4px solid #10b981; padding: 15px; margin: 20px 0;">
    <p style="margin: 0;"><strong>恢复来源:</strong> {{restoreSource}}</p>
    <p style="margin: 10px 0 0 0;"><strong>恢复内容:</strong> {{restoreContent}}</p>
    <p style="margin: 10px 0 0 0;"><strong>完成时间:</strong> {{completeTime}}</p>
    <p style="margin: 10px 0 0 0;"><strong>耗时:</strong> {{duration}}</p>
  </div>
  <p><strong>操作人员:</strong> {{operator}}</p>
  <p style="color: #059669;">系统已自动重启相关服务，请验证数据完整性。</p>
  <hr style="margin: 30px 0; border: none; border-top: 1px solid #e5e7eb;">
  <p style="color: #6b7280; font-size: 12px;">此邮件由系统自动发送，请勿回复。</p>
</div>`,
      variables: ['restoreSource', 'restoreContent', 'completeTime', 'duration', 'operator'],
      createdAt: '2024-12-01 08:30:00',
      updatedAt: '2024-12-01 08:30:00',
      usageCount: 89,
    },
  ];

  const filteredEmails = emails.filter(email =>
    email.recipient.toLowerCase().includes(searchQuery.toLowerCase()) ||
    email.subject.toLowerCase().includes(searchQuery.toLowerCase()) ||
    email.content.toLowerCase().includes(searchQuery.toLowerCase())
  );

  // 分页计算
  const pageSize = 10;
  const totalPages = Math.max(1, Math.ceil(filteredEmails.length / pageSize));
  const paginatedEmails = filteredEmails.slice(
    (currentPage - 1) * pageSize,
    currentPage * pageSize
  );

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };

  const handleSendEmail = () => {
    setCurrentView('send');
  };

  const handleAddServer = () => {
    setServerMode('add');
    setSelectedServer(undefined);
    setServerFormData({
      name: '',
      smtpHost: '',
      smtpPort: 465,
      encryption: 'SSL',
      username: '',
      password: '',
      fromEmail: '',
      fromName: '',
      status: '启用',
    });
    setServerDialogOpen(true);
  };

  const handleEditServer = (server: EmailServer) => {
    setServerMode('edit');
    setSelectedServer(server);
    setServerFormData({
      name: server.name,
      smtpHost: server.smtpHost,
      smtpPort: server.smtpPort,
      encryption: server.encryption,
      username: server.username,
      password: server.password,
      fromEmail: server.fromEmail,
      fromName: server.fromName,
      status: server.status,
    });
    setServerDialogOpen(true);
  };

  const handleAddTemplate = () => {
    setTemplateMode('add');
    setSelectedTemplate(undefined);
    setTemplateFormData({
      name: '',
      code: '',
      subject: '',
      content: '',
      variables: '',
    });
    setTemplateDialogOpen(true);
  };

  const handleEditTemplate = (template: EmailTemplate) => {
    setTemplateMode('edit');
    setSelectedTemplate(template);
    setTemplateFormData({
      name: template.name,
      code: template.code,
      subject: template.subject,
      content: template.content,
      variables: template.variables.join(', '),
    });
    setTemplateDialogOpen(true);
  };

  const handleViewEmail = (email: EmailMessage) => {
    setSelectedEmail(email);
    setViewDialogOpen(true);
  };

  const handleDelete = (type: 'email' | 'server' | 'template', id: string) => {
    setDeleteTarget({ type, id });
    setConfirmDialogOpen(true);
  };

  const confirmDelete = () => {
    if (deleteTarget) {
      const typeText = deleteTarget.type === 'email' ? '邮件记录' : deleteTarget.type === 'server' ? '服务器配置' : '邮件模板';
      toast.success(`删除${typeText}成功`);
      setDeleteTarget(null);
    }
  };

  const handleServerSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!serverFormData.name.trim()) {
      toast.error('请输入服务器名称');
      return;
    }
    if (!serverFormData.smtpHost.trim()) {
      toast.error('请输入SMTP地址');
      return;
    }
    if (!serverFormData.username.trim()) {
      toast.error('请输入用户名');
      return;
    }
    if (!serverFormData.password.trim()) {
      toast.error('请输入密码');
      return;
    }
    if (!serverFormData.fromEmail.trim()) {
      toast.error('请输入发件人邮箱');
      return;
    }

    const actionText = serverMode === 'add' ? '添加' : '更新';
    toast.success(`${actionText}服务器配置成功`);
    setServerDialogOpen(false);
  };

  const handleTemplateSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!templateFormData.name.trim()) {
      toast.error('请输入模板名称');
      return;
    }
    if (!templateFormData.subject.trim()) {
      toast.error('请输入邮件主题');
      return;
    }
    if (!templateFormData.content.trim()) {
      toast.error('请输入邮件内容');
      return;
    }

    const actionText = templateMode === 'add' ? '添加' : '更新';
    toast.success(`${actionText}邮件模板成功`);
    setTemplateDialogOpen(false);
  };

  const getStatusBadge = (status: EmailMessage['status']) => {
    const styles = {
      '成功': 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400',
      '失败': 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400',
      '发送中': 'bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-400',
    };
    return <Badge className={`${styles[status]} border-0`}>{status}</Badge>;
  };

  const getServerStatusBadge = (status: EmailServer['status']) => {
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

  const getCategoryBadge = (category: EmailTemplate['category']) => {
    const colors = {
      '邮件验证码': 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400',
      '邀请注册': 'bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-400',
      '告警通知': 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400',
      '备份通知': 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400',
      '恢复通知': 'bg-cyan-100 text-cyan-700 dark:bg-cyan-900/30 dark:text-cyan-400',
      '其他': 'bg-gray-100 text-gray-700 dark:bg-gray-700 dark:text-gray-400',
    };
    return <Badge className={`${colors[category]} border-0`}>{category}</Badge>;
  };

  if (currentView === 'send') {
    return (
      <SendEmailPage
        onBack={() => setCurrentView('list')}
        servers={servers}
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
            <Mail className="w-4 h-4 mr-2" />
            邮件记录
          </TabsTrigger>
          <TabsTrigger value="templates" className="dark:data-[state=active]:bg-gray-700">
            <FileText className="w-4 h-4 mr-2" />
            邮件模板
          </TabsTrigger>
          <TabsTrigger value="servers" className="dark:data-[state=active]:bg-gray-700">
            <Server className="w-4 h-4 mr-2" />
            服务器配置
          </TabsTrigger>
        </TabsList>

        {/* 邮件记录标签 */}
        <TabsContent value="records" className="space-y-6">
          {/* 搜索和发送 */}
          <Card className="p-6 dark:bg-gray-800 dark:border-gray-700">
            <div className="flex items-center justify-between">
              <div className="relative w-96">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
                <Input
                  placeholder="搜索邮箱、主题或内容..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="pl-10 dark:bg-gray-900 dark:border-gray-700 dark:text-white"
                />
              </div>
              <Button
                className="bg-blue-600 hover:bg-blue-700 text-white"
                onClick={handleSendEmail}
              >
                <Plus className="w-4 h-4 mr-2" />
                发送邮件
              </Button>
            </div>
          </Card>

          {/* 邮件记录列表 */}
          <Card className="dark:bg-gray-800 dark:border-gray-700">
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="border-b dark:border-gray-700 bg-gray-50 dark:bg-gray-900/50">
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">收件人</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">邮件主题</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">服务器</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">状态</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">发送时间</th>
                    <th className="text-right p-4 text-sm text-gray-600 dark:text-gray-400">操作</th>
                  </tr>
                </thead>
                <tbody>
                  {paginatedEmails.map((email) => (
                    <tr
                      key={email.id}
                      className="border-b dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-700"
                    >
                      <td className="p-4 text-sm text-gray-700 dark:text-gray-300">
                        {email.recipient}
                      </td>
                      <td className="p-4">
                        <div className="flex items-center gap-2">
                          <div className="text-sm text-gray-700 dark:text-gray-300 line-clamp-1">
                            {email.subject}
                          </div>
                          {email.hasAttachment && (
                            <FileText className="w-4 h-4 text-gray-400 flex-shrink-0" />
                          )}
                        </div>
                        <div className="text-xs text-gray-500 dark:text-gray-400 mt-1 line-clamp-1">
                          {email.content}
                        </div>
                      </td>
                      <td className="p-4 text-sm text-gray-700 dark:text-gray-300">
                        {email.server}
                      </td>
                      <td className="p-4">
                        <div className="space-y-1">
                          {getStatusBadge(email.status)}
                          {email.errorMsg && (
                            <div className="text-xs text-red-600 dark:text-red-400">
                              {email.errorMsg}
                            </div>
                          )}
                        </div>
                      </td>
                      <td className="p-4 text-sm text-gray-700 dark:text-gray-300">
                        {email.sendTime}
                      </td>
                      <td className="p-4">
                        <div className="flex justify-end gap-2">
                          <button
                            onClick={() => handleViewEmail(email)}
                            className="p-1.5 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg"
                          >
                            <Eye className="w-4 h-4 text-gray-600 dark:text-gray-400" />
                          </button>
                          <button
                            onClick={() => handleDelete('email', email.id)}
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
              totalItems={filteredEmails.length}
              pageSize={pageSize}
            />
          </Card>
        </TabsContent>

        {/* 邮件模板标签 */}
        <TabsContent value="templates" className="space-y-6">
          {/* 添加模板按钮 */}
          <Card className="p-6 dark:bg-gray-800 dark:border-gray-700">
            <div className="flex items-center justify-between">
              <div>
                <h3 className="text-lg dark:text-white mb-1">邮件模板管理</h3>
                <p className="text-sm text-gray-500 dark:text-gray-400">
                  创建和管理邮件模板，支持变量占位符
                </p>
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
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">模板编码</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">邮件主题</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">变量</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">使用次数</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">更新时间</th>
                    <th className="text-right p-4 text-sm text-gray-600 dark:text-gray-400">操作</th>
                  </tr>
                </thead>
                <tbody>
                  {templates.map((template) => (
                    <tr
                      key={template.id}
                      className="border-b dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-700"
                    >
                      <td className="p-4">
                        <div className="dark:text-white">{template.name}</div>
                      </td>
                      <td className="p-4">
                        <div className="text-sm text-gray-700 dark:text-gray-300">{template.code}</div>
                      </td>
                      <td className="p-4 text-sm text-gray-700 dark:text-gray-300 max-w-xs truncate">
                        {template.subject}
                      </td>
                      <td className="p-4">
                        <div className="flex flex-wrap gap-1">
                          {template.variables.slice(0, 3).map((variable, index) => (
                            <Badge
                              key={index}
                              variant="outline"
                              className="text-xs dark:border-gray-600 dark:text-gray-400"
                            >
                              {`{{${variable}}}`}
                            </Badge>
                          ))}
                          {template.variables.length > 3 && (
                            <Badge
                              variant="outline"
                              className="text-xs dark:border-gray-600 dark:text-gray-400"
                            >
                              +{template.variables.length - 3}
                            </Badge>
                          )}
                        </div>
                      </td>
                      <td className="p-4 text-sm text-gray-700 dark:text-gray-300">
                        {template.usageCount.toLocaleString()}
                      </td>
                      <td className="p-4 text-sm text-gray-700 dark:text-gray-300">
                        {template.updatedAt}
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
                                onClick={() => handleEditTemplate(template)}
                                className="dark:text-gray-300 dark:hover:bg-gray-800"
                              >
                                <Edit className="w-4 h-4 mr-2" />
                                编辑模板
                              </DropdownMenuItem>
                              <DropdownMenuItem
                                onClick={() => {
                                  navigator.clipboard.writeText(template.content);
                                  toast.success('模板内容已复制到剪贴板');
                                }}
                                className="dark:text-gray-300 dark:hover:bg-gray-800"
                              >
                                <Copy className="w-4 h-4 mr-2" />
                                复制内容
                              </DropdownMenuItem>
                              <DropdownMenuItem
                                onClick={() => handleDelete('template', template.id)}
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

        {/* 服务器配置标签 */}
        <TabsContent value="servers" className="space-y-6">
          {/* 添加服务器按钮 */}
          <Card className="p-6 dark:bg-gray-800 dark:border-gray-700">
            <div className="flex items-center justify-between">
              <div>
                <h3 className="text-lg dark:text-white mb-1">邮件服务器配置</h3>
                <p className="text-sm text-gray-500 dark:text-gray-400">
                  配置SMTP邮件服务器，支持多服务器负载均衡
                </p>
              </div>
              <Button
                className="bg-blue-600 hover:bg-blue-700 text-white"
                onClick={handleAddServer}
              >
                <Plus className="w-4 h-4 mr-2" />
                添加服务器
              </Button>
            </div>
          </Card>

          {/* 服务器列表 */}
          <Card className="dark:bg-gray-800 dark:border-gray-700">
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="border-b dark:border-gray-700 bg-gray-50 dark:bg-gray-900/50">
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">服务器名称</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">SMTP地址</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">端口/加密</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">发件人</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">状态</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">总发送量</th>
                    <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">成功率</th>
                    <th className="text-right p-4 text-sm text-gray-600 dark:text-gray-400">操作</th>
                  </tr>
                </thead>
                <tbody>
                  {servers.map((server) => (
                    <tr
                      key={server.id}
                      className="border-b dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-700"
                    >
                      <td className="p-4">
                        <div className="dark:text-white">{server.name}</div>
                      </td>
                      <td className="p-4 text-sm text-gray-700 dark:text-gray-300">
                        {server.smtpHost}
                      </td>
                      <td className="p-4">
                        <div className="text-sm text-gray-700 dark:text-gray-300">
                          {server.smtpPort}
                        </div>
                        <Badge
                          variant="outline"
                          className="text-xs dark:border-gray-600 dark:text-gray-400 mt-1"
                        >
                          {server.encryption}
                        </Badge>
                      </td>
                      <td className="p-4">
                        <div className="text-sm text-gray-700 dark:text-gray-300">
                          {server.fromEmail}
                        </div>
                        <div className="text-xs text-gray-500 dark:text-gray-400 mt-1">
                          {server.fromName}
                        </div>
                      </td>
                      <td className="p-4">
                        {getServerStatusBadge(server.status)}
                      </td>
                      <td className="p-4 text-sm text-gray-700 dark:text-gray-300">
                        {server.totalSent.toLocaleString()}
                      </td>
                      <td className="p-4 text-sm text-green-600 dark:text-green-400">
                        {server.successRate}
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
                                onClick={() => handleEditServer(server)}
                                className="dark:text-gray-300 dark:hover:bg-gray-800"
                              >
                                <Settings className="w-4 h-4 mr-2" />
                                编辑配置
                              </DropdownMenuItem>
                              <DropdownMenuItem
                                onClick={() => handleDelete('server', server.id)}
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

      {/* 查看邮件对话框 */}
      <Dialog open={viewDialogOpen} onOpenChange={setViewDialogOpen}>
        <DialogContent className="max-w-2xl dark:bg-gray-800 dark:border-gray-700">
          <DialogHeader>
            <DialogTitle className="dark:text-white">邮件详情</DialogTitle>
            <DialogDescription className="dark:text-gray-400">
              查看邮件的详细信息
            </DialogDescription>
          </DialogHeader>
          {selectedEmail && (
            <div className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <Label className="text-sm text-gray-600 dark:text-gray-400">收件人</Label>
                  <div className="text-sm dark:text-white mt-1">{selectedEmail.recipient}</div>
                </div>
                <div>
                  <Label className="text-sm text-gray-600 dark:text-gray-400">服务器</Label>
                  <div className="text-sm dark:text-white mt-1">{selectedEmail.server}</div>
                </div>
                <div>
                  <Label className="text-sm text-gray-600 dark:text-gray-400">状态</Label>
                  <div className="mt-1">{getStatusBadge(selectedEmail.status)}</div>
                </div>
                <div>
                  <Label className="text-sm text-gray-600 dark:text-gray-400">发送时间</Label>
                  <div className="text-sm dark:text-white mt-1">{selectedEmail.sendTime}</div>
                </div>
              </div>
              <div>
                <Label className="text-sm text-gray-600 dark:text-gray-400">邮件主题</Label>
                <div className="text-sm dark:text-white mt-1">{selectedEmail.subject}</div>
              </div>
              <div>
                <Label className="text-sm text-gray-600 dark:text-gray-400">邮件内容</Label>
                <div className="text-sm dark:text-white mt-1 p-4 bg-gray-50 dark:bg-gray-900 rounded-lg whitespace-pre-wrap">
                  {selectedEmail.content}
                </div>
              </div>
              {selectedEmail.errorMsg && (
                <div className="bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg p-4">
                  <Label className="text-sm text-red-800 dark:text-red-400">错误信息</Label>
                  <div className="text-sm text-red-600 dark:text-red-300 mt-1">{selectedEmail.errorMsg}</div>
                </div>
              )}
            </div>
          )}
        </DialogContent>
      </Dialog>

      {/* 服务器配置对话框 */}
      <Dialog open={serverDialogOpen} onOpenChange={setServerDialogOpen}>
        <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto dark:bg-gray-800 dark:border-gray-700">
          <DialogHeader>
            <DialogTitle className="dark:text-white">
              {serverMode === 'add' ? '添加邮件服务器' : '编辑邮件服务器'}
            </DialogTitle>
            <DialogDescription className="dark:text-gray-400">
              配置SMTP邮件服务器信息
            </DialogDescription>
          </DialogHeader>

          <form onSubmit={handleServerSubmit} className="space-y-6">
            <div className="space-y-4">
              <div className="space-y-2">
                <Label className="dark:text-gray-300">
                  服务器名称 <span className="text-red-500">*</span>
                </Label>
                <Input
                  value={serverFormData.name}
                  onChange={(e) => setServerFormData({ ...serverFormData, name: e.target.value })}
                  placeholder="例如：阿里云邮件服务"
                  className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label className="dark:text-gray-300">
                    SMTP地址 <span className="text-red-500">*</span>
                  </Label>
                  <Input
                    value={serverFormData.smtpHost}
                    onChange={(e) => setServerFormData({ ...serverFormData, smtpHost: e.target.value })}
                    placeholder="smtp.example.com"
                    className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
                  />
                </div>

                <div className="space-y-2">
                  <Label className="dark:text-gray-300">
                    端口 <span className="text-red-500">*</span>
                  </Label>
                  <Input
                    type="number"
                    value={serverFormData.smtpPort}
                    onChange={(e) => setServerFormData({ ...serverFormData, smtpPort: parseInt(e.target.value) })}
                    placeholder="465"
                    className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
                  />
                </div>
              </div>

              <div className="space-y-2">
                <Label className="dark:text-gray-300">加密方式</Label>
                <Select
                  value={serverFormData.encryption}
                  onValueChange={(value: any) => setServerFormData({ ...serverFormData, encryption: value })}
                >
                  <SelectTrigger className="dark:bg-gray-900 dark:border-gray-700 dark:text-white">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent className="dark:bg-gray-900 dark:border-gray-700">
                    <SelectItem value="SSL">SSL</SelectItem>
                    <SelectItem value="TLS">TLS</SelectItem>
                    <SelectItem value="无">无</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div className="space-y-2">
                <Label className="dark:text-gray-300">
                  用户名 <span className="text-red-500">*</span>
                </Label>
                <Input
                  value={serverFormData.username}
                  onChange={(e) => setServerFormData({ ...serverFormData, username: e.target.value })}
                  placeholder="user@example.com"
                  className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
                />
              </div>

              <div className="space-y-2">
                <Label className="dark:text-gray-300">
                  密码 <span className="text-red-500">*</span>
                </Label>
                <Input
                  type="password"
                  value={serverFormData.password}
                  onChange={(e) => setServerFormData({ ...serverFormData, password: e.target.value })}
                  placeholder="••••••••"
                  className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label className="dark:text-gray-300">
                    发件人邮箱 <span className="text-red-500">*</span>
                  </Label>
                  <Input
                    value={serverFormData.fromEmail}
                    onChange={(e) => setServerFormData({ ...serverFormData, fromEmail: e.target.value })}
                    placeholder="noreply@example.com"
                    className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
                  />
                </div>

                <div className="space-y-2">
                  <Label className="dark:text-gray-300">发件人名称</Label>
                  <Input
                    value={serverFormData.fromName}
                    onChange={(e) => setServerFormData({ ...serverFormData, fromName: e.target.value })}
                    placeholder="AngusGM系统"
                    className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
                  />
                </div>
              </div>

              <div className="space-y-2">
                <Label className="dark:text-gray-300">状态</Label>
                <Select
                  value={serverFormData.status}
                  onValueChange={(value: any) => setServerFormData({ ...serverFormData, status: value })}
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
                onClick={() => setServerDialogOpen(false)}
                className="dark:bg-gray-900 dark:border-gray-700 dark:text-gray-300"
              >
                取消
              </Button>
              <Button
                type="submit"
                className="bg-blue-600 hover:bg-blue-700 text-white"
              >
                {serverMode === 'add' ? '添加' : '保存'}
              </Button>
            </div>
          </form>
        </DialogContent>
      </Dialog>

      {/* 模板配置对话框 */}
      <Dialog open={templateDialogOpen} onOpenChange={setTemplateDialogOpen}>
        <DialogContent className="max-w-5xl max-h-[90vh] overflow-y-auto dark:bg-gray-800 dark:border-gray-700">
          <DialogHeader>
            <DialogTitle className="dark:text-white">
              {templateMode === 'add' ? '新建邮件模板' : '编辑邮件模板'}
            </DialogTitle>
            <DialogDescription className="dark:text-gray-400">
              创建邮件模板，使用 {`{{变量名}}`} 作为占位符
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
                    placeholder="例如：登录验证码"
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
                    placeholder="例如：login_verification"
                    className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
                  />
                </div>
              </div>

              <div className="space-y-2">
                <Label className="dark:text-gray-300">
                  邮件主题 <span className="text-red-500">*</span>
                </Label>
                <Input
                  value={templateFormData.subject}
                  onChange={(e) => setTemplateFormData({ ...templateFormData, subject: e.target.value })}
                  placeholder="例如：【AngusGM】您的登录验证码"
                  className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
                />
              </div>

              <div className="space-y-2">
                <Label className="dark:text-gray-300">
                  邮件内容 <span className="text-red-500">*</span>
                </Label>
                <Textarea
                  value={templateFormData.content}
                  onChange={(e) => setTemplateFormData({ ...templateFormData, content: e.target.value })}
                  placeholder="请输入邮件内容，支持HTML格式。使用 {{变量名}} 作为占位符，例如: {{code}}, {{userName}} 等"
                  rows={15}
                  className="dark:bg-gray-900 dark:border-gray-700 dark:text-white resize-none font-mono text-sm"
                />
              </div>

              <div className="space-y-2">
                <Label className="dark:text-gray-300">变量列表</Label>
                <Input
                  value={templateFormData.variables}
                  onChange={(e) => setTemplateFormData({ ...templateFormData, variables: e.target.value })}
                  placeholder="多个变量用逗号分隔，例如: code, userName, companyName"
                  className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
                />
                <p className="text-xs text-gray-500 dark:text-gray-400">
                  在邮件内容中使用 {`{{变量名}}`} 格式引用变量
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
          deleteTarget?.type === 'email' ? '邮件记录' :
          deleteTarget?.type === 'server' ? '服务器配置' : '邮件模板'
        }`}
        description={`此操作无法撤销，确定要删除这条${
          deleteTarget?.type === 'email' ? '邮件记录' :
          deleteTarget?.type === 'server' ? '服务器配置' : '邮件模板'
        }吗？`}
      />
    </div>
  );
}