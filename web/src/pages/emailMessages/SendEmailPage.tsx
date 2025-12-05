import { useState } from 'react';
import { ArrowLeft, Send } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { toast } from 'sonner';

interface EmailTemplate {
  id: string;
  name: string;
  category: string;
  subject: string;
  content: string;
}

interface EmailServer {
  id: string;
  name: string;
  fromEmail: string;
  status: string;
}

interface SendEmailPageProps {
  onBack: () => void;
  servers: EmailServer[];
  templates: EmailTemplate[];
}

export function SendEmailPage({ onBack, servers, templates }: SendEmailPageProps) {
  const [formData, setFormData] = useState({
    template: '',
    recipients: '',
    subject: '',
    content: '',
    server: servers.find(s => s.status === '启用')?.id || '',
  });

  const handleTemplateChange = (templateId: string) => {
    const template = templates.find(t => t.id === templateId);
    if (template) {
      setFormData({
        ...formData,
        template: templateId,
        subject: template.subject,
        content: template.content,
      });
    } else {
      setFormData({
        ...formData,
        template: '',
      });
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!formData.recipients.trim()) {
      toast.error('请输入收件人邮箱');
      return;
    }
    if (!formData.subject.trim()) {
      toast.error('请输入邮件主题');
      return;
    }
    if (!formData.content.trim()) {
      toast.error('请输入邮件内容');
      return;
    }
    if (!formData.server) {
      toast.error('请选择邮件服务器');
      return;
    }

    toast.success('邮件发送成功');
    onBack();
  };

  return (
    <div className="space-y-6">
      {/* 顶部返回按钮 */}
      <div className="flex items-center gap-4">
        <Button
          variant="ghost"
          onClick={onBack}
          className="dark:text-gray-300 dark:hover:bg-gray-800"
        >
          <ArrowLeft className="w-4 h-4 mr-2" />
          返回
        </Button>
        <div>
          <h2 className="text-2xl dark:text-white">发送邮件</h2>
          <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
            选择模板或自定义邮件内容发送
          </p>
        </div>
      </div>

      {/* 发送表单 */}
      <Card className="dark:bg-gray-800 dark:border-gray-700">
        <CardHeader>
          <CardTitle className="dark:text-white">邮件信息</CardTitle>
          <CardDescription className="dark:text-gray-400">
            填写邮件信息并选择邮件服务器
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-6">
            {/* 选择模板 */}
            <div className="space-y-2">
              <Label className="dark:text-gray-300">选择模板（可选）</Label>
              <Select
                value={formData.template}
                onValueChange={handleTemplateChange}
              >
                <SelectTrigger className="dark:bg-gray-900 dark:border-gray-700 dark:text-white">
                  <SelectValue placeholder="不使用模板，自定义邮件内容" />
                </SelectTrigger>
                <SelectContent className="dark:bg-gray-900 dark:border-gray-700">
                  <SelectItem value="">不使用模板</SelectItem>
                  {templates.map((template) => (
                    <SelectItem key={template.id} value={template.id}>
                      {template.name} - {template.category}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
              <p className="text-xs text-gray-500 dark:text-gray-400">
                选择模板后将自动填充邮件主题和内容，您可以继续编辑
              </p>
            </div>

            {/* 收件人 */}
            <div className="space-y-2">
              <Label className="dark:text-gray-300">
                收件人 <span className="text-red-500">*</span>
              </Label>
              <Textarea
                value={formData.recipients}
                onChange={(e) => setFormData({ ...formData, recipients: e.target.value })}
                placeholder="请输入收件人邮箱地址，多个邮箱用逗号或换行分隔&#10;例如：user1@example.com, user2@example.com"
                rows={3}
                className="dark:bg-gray-900 dark:border-gray-700 dark:text-white resize-none"
              />
            </div>

            {/* 邮件主题 */}
            <div className="space-y-2">
              <Label className="dark:text-gray-300">
                邮件主题 <span className="text-red-500">*</span>
              </Label>
              <Input
                value={formData.subject}
                onChange={(e) => setFormData({ ...formData, subject: e.target.value })}
                placeholder="请输入邮件主题"
                className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
              />
            </div>

            {/* 邮件内容 */}
            <div className="space-y-2">
              <Label className="dark:text-gray-300">
                邮件内容 <span className="text-red-500">*</span>
              </Label>
              <Textarea
                value={formData.content}
                onChange={(e) => setFormData({ ...formData, content: e.target.value })}
                placeholder="请输入邮件内容，支持HTML格式"
                rows={12}
                className="dark:bg-gray-900 dark:border-gray-700 dark:text-white resize-none font-mono text-sm"
              />
            </div>

            {/* 邮件服务器 */}
            <div className="space-y-2">
              <Label className="dark:text-gray-300">
                邮件服务器 <span className="text-red-500">*</span>
              </Label>
              <Select
                value={formData.server}
                onValueChange={(value) => setFormData({ ...formData, server: value })}
              >
                <SelectTrigger className="dark:bg-gray-900 dark:border-gray-700 dark:text-white">
                  <SelectValue placeholder="请选择邮件服务器" />
                </SelectTrigger>
                <SelectContent className="dark:bg-gray-900 dark:border-gray-700">
                  {servers.filter(s => s.status === '启用').map((server) => (
                    <SelectItem key={server.id} value={server.id}>
                      {server.name} - {server.fromEmail}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            {/* 操作按钮 */}
            <div className="flex justify-end gap-3 pt-4 border-t dark:border-gray-700">
              <Button
                type="button"
                variant="outline"
                onClick={onBack}
                className="dark:bg-gray-900 dark:border-gray-700 dark:text-gray-300"
              >
                取消
              </Button>
              <Button
                type="submit"
                className="bg-blue-600 hover:bg-blue-700 text-white"
              >
                <Send className="w-4 h-4 mr-2" />
                发送
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
