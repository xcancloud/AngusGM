import { useState } from 'react';
import { ArrowLeft, Send, Plus, X } from 'lucide-react';
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

interface SMSTemplate {
  id: string;
  name: string;
  code: string;
  thirdPartyId: string;
  channel: string;
  signature: string;
  content: string;
  variables: string[];
}

interface SMSChannel {
  id: string;
  name: string;
  signature: string;
  status: string;
}

interface SendSMSPageProps {
  onBack: () => void;
  channels: SMSChannel[];
  templates: SMSTemplate[];
}

export function SendSMSPage({ onBack, channels, templates }: SendSMSPageProps) {
  const [selectedChannel, setSelectedChannel] = useState('');
  const [selectedTemplate, setSelectedTemplate] = useState('');
  const [phones, setPhones] = useState('');
  const [customContent, setCustomContent] = useState('');
  const [templateParams, setTemplateParams] = useState<{ [key: string]: string }>({});

  const currentTemplate = templates.find(t => t.id === selectedTemplate);
  const filteredTemplates = selectedChannel 
    ? templates.filter(t => t.channel === selectedChannel)
    : [];

  const handleTemplateChange = (templateId: string) => {
    const template = templates.find(t => t.id === templateId);
    setSelectedTemplate(templateId);
    if (template) {
      // 初始化模板参数
      const params: { [key: string]: string } = {};
      template.variables.forEach(v => {
        params[v] = '';
      });
      setTemplateParams(params);
      setCustomContent('');
    } else {
      setTemplateParams({});
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!phones.trim()) {
      toast.error('请输入手机号码');
      return;
    }
    
    if (!selectedChannel) {
      toast.error('请选择短信通道');
      return;
    }

    if (selectedTemplate) {
      // 使用模板发送，检查模板参数
      const template = templates.find(t => t.id === selectedTemplate);
      if (template) {
        const missingParams = template.variables.filter(v => !templateParams[v]?.trim());
        if (missingParams.length > 0) {
          toast.error(`请填写模板参数: ${missingParams.join(', ')}`);
          return;
        }
      }
    } else {
      // 自定义内容发送
      if (!customContent.trim()) {
        toast.error('请输入短信内容');
        return;
      }
    }

    toast.success('短信发送成功');
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
          <h2 className="text-2xl dark:text-white">发送短信</h2>
          <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
            选择短信通道和模板发送短信
          </p>
        </div>
      </div>

      {/* 发送表单 */}
      <Card className="dark:bg-gray-800 dark:border-gray-700">
        <CardHeader>
          <CardTitle className="dark:text-white">短信信息</CardTitle>
          <CardDescription className="dark:text-gray-400">
            填写短信信息并选择发送通道
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-6">
            {/* 选择通道 */}
            <div className="space-y-2">
              <Label className="dark:text-gray-300">
                短信通道 <span className="text-red-500">*</span>
              </Label>
              <Select
                value={selectedChannel}
                onValueChange={(value) => {
                  setSelectedChannel(value);
                  setSelectedTemplate('');
                  setTemplateParams({});
                }}
              >
                <SelectTrigger className="dark:bg-gray-900 dark:border-gray-700 dark:text-white">
                  <SelectValue placeholder="请选择短信通道" />
                </SelectTrigger>
                <SelectContent className="dark:bg-gray-900 dark:border-gray-700">
                  {channels.filter(c => c.status === '启用').map((channel) => (
                    <SelectItem key={channel.id} value={channel.id}>
                      {channel.name} - {channel.signature}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            {/* 选择模板 */}
            {selectedChannel && (
              <div className="space-y-2">
                <Label className="dark:text-gray-300">选择模板（可选）</Label>
                <Select
                  value={selectedTemplate}
                  onValueChange={handleTemplateChange}
                >
                  <SelectTrigger className="dark:bg-gray-900 dark:border-gray-700 dark:text-white">
                    <SelectValue placeholder="不使用模板，自定义短信内容" />
                  </SelectTrigger>
                  <SelectContent className="dark:bg-gray-900 dark:border-gray-700">
                    <SelectItem value="">不使用模板</SelectItem>
                    {filteredTemplates.map((template) => (
                      <SelectItem key={template.id} value={template.id}>
                        {template.name} - {template.code}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
                <p className="text-xs text-gray-500 dark:text-gray-400">
                  选择模板后需要填写对应的模板参数
                </p>
              </div>
            )}

            {/* 手机号码 */}
            <div className="space-y-2">
              <Label className="dark:text-gray-300">
                手机号码 <span className="text-red-500">*</span>
              </Label>
              <Textarea
                value={phones}
                onChange={(e) => setPhones(e.target.value)}
                placeholder="请输入手机号码，多个号码用逗号或换行分隔&#10;例如：13800138000, 13900139000"
                rows={3}
                className="dark:bg-gray-900 dark:border-gray-700 dark:text-white resize-none"
              />
            </div>

            {/* 模板参数 */}
            {currentTemplate && currentTemplate.variables.length > 0 && (
              <div className="space-y-4">
                <div className="flex items-center justify-between">
                  <Label className="dark:text-gray-300">
                    模板参数 <span className="text-red-500">*</span>
                  </Label>
                  <div className="text-xs text-gray-500 dark:text-gray-400">
                    根据模板要求填写参数值
                  </div>
                </div>
                <div className="bg-gray-50 dark:bg-gray-900 rounded-lg p-4 space-y-3">
                  <div className="text-sm text-gray-600 dark:text-gray-400 mb-3">
                    模板内容：{currentTemplate.content}
                  </div>
                  {currentTemplate.variables.map((variable, index) => (
                    <div key={index} className="space-y-1">
                      <Label className="text-xs dark:text-gray-400">
                        {variable} <span className="text-red-500">*</span>
                      </Label>
                      <Input
                        value={templateParams[variable] || ''}
                        onChange={(e) => setTemplateParams({
                          ...templateParams,
                          [variable]: e.target.value
                        })}
                        placeholder={`请输入${variable}的值`}
                        className="dark:bg-gray-800 dark:border-gray-700 dark:text-white"
                      />
                    </div>
                  ))}
                </div>
              </div>
            )}

            {/* 自定义内容 */}
            {!selectedTemplate && selectedChannel && (
              <div className="space-y-2">
                <Label className="dark:text-gray-300">
                  短信内容 <span className="text-red-500">*</span>
                </Label>
                <Textarea
                  value={customContent}
                  onChange={(e) => setCustomContent(e.target.value)}
                  placeholder="请输入短信内容"
                  rows={5}
                  className="dark:bg-gray-900 dark:border-gray-700 dark:text-white resize-none"
                />
                <div className="flex justify-between text-xs text-gray-500 dark:text-gray-400">
                  <span>短信长度：{customContent.length} 字符</span>
                  <span>预计条数：{Math.ceil(customContent.length / 70) || 1} 条</span>
                </div>
              </div>
            )}

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
