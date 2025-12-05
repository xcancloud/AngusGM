import { useState } from 'react';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription, DialogFooter } from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Label } from '@/components/ui/label';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { toast } from 'sonner';
import { Mail, Link as LinkIcon, Copy, Send } from 'lucide-react';

interface UserInviteDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

export function UserInviteDialog({ open, onOpenChange }: UserInviteDialogProps) {
  const [inviteMethod, setInviteMethod] = useState<'email' | 'link'>('email');
  const [emails, setEmails] = useState('');
  const [role, setRole] = useState('普通用户');
  const [department, setDepartment] = useState('');
  const [message, setMessage] = useState('');
  const [inviteLink, setInviteLink] = useState('');
  const [linkExpiry, setLinkExpiry] = useState('7');

  const handleGenerateLink = () => {
    const randomId = Math.random().toString(36).substring(2, 15);
    const link = `https://angusgm.example.com/invite/${randomId}`;
    setInviteLink(link);
    toast.success('邀请链接已生成');
  };

  const handleCopyLink = () => {
    navigator.clipboard.writeText(inviteLink);
    toast.success('链接已复制到剪贴板');
  };

  const handleSendInvite = () => {
    if (inviteMethod === 'email') {
      if (!emails.trim()) {
        toast.error('请输入邮箱地址');
        return;
      }
      const emailList = emails.split(/[,\n]/).map(e => e.trim()).filter(e => e);
      toast.success(`已向 ${emailList.length} 个邮箱发送邀请`);
    } else {
      if (!inviteLink) {
        toast.error('请先生成邀请链接');
        return;
      }
      toast.success('邀请链接已准备就绪');
    }
    onOpenChange(false);
    resetForm();
  };

  const resetForm = () => {
    setEmails('');
    setRole('普通用户');
    setDepartment('');
    setMessage('');
    setInviteLink('');
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[600px] dark:bg-gray-800 dark:border-gray-700">
        <DialogHeader>
          <DialogTitle className="dark:text-white">邀请用户</DialogTitle>
          <DialogDescription className="dark:text-gray-400">
            通过邮箱或邀请链接邀请新用户加入系统
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-6 py-4">
          {/* 邀请方式选择 */}
          <div className="flex gap-2 p-1 bg-gray-100 dark:bg-gray-900 rounded-lg">
            <button
              onClick={() => setInviteMethod('email')}
              className={`flex-1 flex items-center justify-center gap-2 px-4 py-2 rounded-md transition-colors ${
                inviteMethod === 'email'
                  ? 'bg-white dark:bg-gray-800 text-blue-600 dark:text-blue-400 shadow-sm'
                  : 'text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-gray-200'
              }`}
            >
              <Mail className="w-4 h-4" />
              <span className="text-sm">邮箱邀请</span>
            </button>
            <button
              onClick={() => setInviteMethod('link')}
              className={`flex-1 flex items-center justify-center gap-2 px-4 py-2 rounded-md transition-colors ${
                inviteMethod === 'link'
                  ? 'bg-white dark:bg-gray-800 text-blue-600 dark:text-blue-400 shadow-sm'
                  : 'text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-gray-200'
              }`}
            >
              <LinkIcon className="w-4 h-4" />
              <span className="text-sm">邀请链接</span>
            </button>
          </div>

          {inviteMethod === 'email' ? (
            <>
              {/* 邮箱地址 */}
              <div className="space-y-2">
                <Label htmlFor="emails" className="dark:text-gray-200">
                  邮箱地址 <span className="text-red-500">*</span>
                </Label>
                <Textarea
                  id="emails"
                  placeholder="输入邮箱地址，多个邮箱用逗号或换行分隔&#10;例如：user1@example.com, user2@example.com"
                  value={emails}
                  onChange={(e) => setEmails(e.target.value)}
                  rows={4}
                  className="dark:bg-gray-900 dark:border-gray-700 dark:text-white resize-none"
                />
                <p className="text-xs text-gray-500 dark:text-gray-400">
                  支持批量邀请，用逗号或换行分隔多个邮箱
                </p>
              </div>

              {/* 默认角色 */}
              <div className="space-y-2">
                <Label htmlFor="role" className="dark:text-gray-200">
                  默认角色
                </Label>
                <Select value={role} onValueChange={setRole}>
                  <SelectTrigger className="dark:bg-gray-900 dark:border-gray-700 dark:text-white">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent className="dark:bg-gray-900 dark:border-gray-700">
                    <SelectItem value="管理员">管理员</SelectItem>
                    <SelectItem value="审核员">审核员</SelectItem>
                    <SelectItem value="普通用户">普通用户</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              {/* 所属部门 */}
              <div className="space-y-2">
                <Label htmlFor="department" className="dark:text-gray-200">
                  所属部门
                </Label>
                <Select value={department} onValueChange={setDepartment}>
                  <SelectTrigger className="dark:bg-gray-900 dark:border-gray-700 dark:text-white">
                    <SelectValue placeholder="选择部门（可选）" />
                  </SelectTrigger>
                  <SelectContent className="dark:bg-gray-900 dark:border-gray-700">
                    <SelectItem value="技术部">技术部</SelectItem>
                    <SelectItem value="市场部">市场部</SelectItem>
                    <SelectItem value="运营部">运营部</SelectItem>
                    <SelectItem value="财务部">财务部</SelectItem>
                    <SelectItem value="人事部">人事部</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              {/* 邀请消息 */}
              <div className="space-y-2">
                <Label htmlFor="message" className="dark:text-gray-200">
                  邀请消息（可选）
                </Label>
                <Textarea
                  id="message"
                  placeholder="输入邀请消息，将显示在邀请邮件中..."
                  value={message}
                  onChange={(e) => setMessage(e.target.value)}
                  rows={3}
                  className="dark:bg-gray-900 dark:border-gray-700 dark:text-white resize-none"
                />
              </div>
            </>
          ) : (
            <>
              {/* 链接有效期 */}
              <div className="space-y-2">
                <Label htmlFor="expiry" className="dark:text-gray-200">
                  链接有效期
                </Label>
                <Select value={linkExpiry} onValueChange={setLinkExpiry}>
                  <SelectTrigger className="dark:bg-gray-900 dark:border-gray-700 dark:text-white">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent className="dark:bg-gray-900 dark:border-gray-700">
                    <SelectItem value="1">1 天</SelectItem>
                    <SelectItem value="3">3 天</SelectItem>
                    <SelectItem value="7">7 天</SelectItem>
                    <SelectItem value="30">30 天</SelectItem>
                    <SelectItem value="never">永不过期</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              {/* 默认角色 */}
              <div className="space-y-2">
                <Label htmlFor="role-link" className="dark:text-gray-200">
                  默认角色
                </Label>
                <Select value={role} onValueChange={setRole}>
                  <SelectTrigger className="dark:bg-gray-900 dark:border-gray-700 dark:text-white">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent className="dark:bg-gray-900 dark:border-gray-700">
                    <SelectItem value="管理员">管理员</SelectItem>
                    <SelectItem value="审核员">审核员</SelectItem>
                    <SelectItem value="普通用户">普通用户</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              {/* 所属部门 */}
              <div className="space-y-2">
                <Label htmlFor="department-link" className="dark:text-gray-200">
                  所属部门
                </Label>
                <Select value={department} onValueChange={setDepartment}>
                  <SelectTrigger className="dark:bg-gray-900 dark:border-gray-700 dark:text-white">
                    <SelectValue placeholder="选择部门（可选）" />
                  </SelectTrigger>
                  <SelectContent className="dark:bg-gray-900 dark:border-gray-700">
                    <SelectItem value="技术部">技术部</SelectItem>
                    <SelectItem value="市场部">市场部</SelectItem>
                    <SelectItem value="运营部">运营部</SelectItem>
                    <SelectItem value="财务部">财务部</SelectItem>
                    <SelectItem value="人事部">人事部</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              {/* 生成链接按钮 */}
              {!inviteLink && (
                <Button
                  onClick={handleGenerateLink}
                  className="w-full bg-blue-500 hover:bg-blue-600 text-white"
                >
                  生成邀请链接
                </Button>
              )}

              {/* 显示生成的链接 */}
              {inviteLink && (
                <div className="space-y-2">
                  <Label className="dark:text-gray-200">邀请链接</Label>
                  <div className="flex gap-2">
                    <Input
                      value={inviteLink}
                      readOnly
                      className="dark:bg-gray-900 dark:border-gray-700 dark:text-white"
                    />
                    <Button
                      onClick={handleCopyLink}
                      variant="outline"
                      className="dark:bg-gray-900 dark:border-gray-700 dark:text-gray-300 flex-shrink-0"
                    >
                      <Copy className="w-4 h-4" />
                    </Button>
                  </div>
                  <p className="text-xs text-gray-500 dark:text-gray-400">
                    链接有效期：{linkExpiry === 'never' ? '永不过期' : `${linkExpiry} 天`}
                  </p>
                </div>
              )}
            </>
          )}
        </div>

        <DialogFooter>
          <Button
            variant="outline"
            onClick={() => {
              onOpenChange(false);
              resetForm();
            }}
            className="dark:bg-gray-900 dark:border-gray-700 dark:text-gray-300"
          >
            取消
          </Button>
          <Button onClick={handleSendInvite} className="bg-blue-500 hover:bg-blue-600 text-white">
            <Send className="w-4 h-4 mr-2" />
            {inviteMethod === 'email' ? '发送邀请' : '确定'}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
