import { useState } from 'react';
import { Shield, Lock, Key, Clock, AlertTriangle, CheckCircle2, Smartphone, Mail, Globe, RefreshCw } from 'lucide-react';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Switch } from '@/components/ui/switch';
import { Slider } from '@/components/ui/slider';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { toast } from 'sonner';

export function SecuritySettings() {
  const [settings, setSettings] = useState({
    // 密码策略
    passwordMinLength: 8,
    passwordRequireUppercase: true,
    passwordRequireLowercase: true,
    passwordRequireNumber: true,
    passwordRequireSpecialChar: true,
    passwordExpiryDays: 90,
    passwordHistoryCount: 5,
    
    // 登录安全
    maxLoginAttempts: 5,
    lockoutDuration: 30,
    enableTwoFactor: true,
    twoFactorMethod: 'email',
    sessionTimeout: 30,
    enableCaptcha: true,
    
    // IP白名单
    enableIpWhitelist: false,
    ipWhitelist: '',
    
    // API安全
    enableApiRateLimit: true,
    apiRateLimit: 1000,
    apiRateLimitWindow: 60,
    
    // 安全通知
    notifyPasswordChange: true,
    notifyLoginFromNewDevice: true,
    notifyApiKeyUsage: false,
  });

  const [hasChanges, setHasChanges] = useState(false);

  const updateSetting = (key: string, value: any) => {
    setSettings(prev => ({ ...prev, [key]: value }));
    setHasChanges(true);
  };

  const handleSave = () => {
    toast.success('安全设置已保存');
    setHasChanges(false);
  };

  const handleReset = () => {
    toast.info('已重置为默认设置');
    setHasChanges(false);
  };

  return (
    <div className="space-y-6">
      {/* 页面标题 */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl dark:text-white mb-2">安全设置</h2>
          <p className="text-sm text-gray-500 dark:text-gray-400">
            配置系统安全策略和访问控制
          </p>
        </div>
        <div className="flex items-center gap-3">
          <Button
            variant="outline"
            onClick={handleReset}
            className="dark:border-gray-600 dark:hover:bg-gray-700"
          >
            <RefreshCw className="w-4 h-4 mr-2" />
            重置默认
          </Button>
          <Button
            onClick={handleSave}
            disabled={!hasChanges}
            className="bg-blue-600 hover:bg-blue-700"
          >
            <CheckCircle2 className="w-4 h-4 mr-2" />
            保存设置
          </Button>
        </div>
      </div>

      {/* 密码策略 */}
      <Card className="dark:bg-gray-800 dark:border-gray-700">
        <div className="p-6 border-b dark:border-gray-700">
          <div className="flex items-center gap-2">
            <Lock className="w-5 h-5 text-blue-500" />
            <h3 className="text-lg dark:text-white">密码策略</h3>
          </div>
          <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
            设置用户密码的安全要求
          </p>
        </div>

        <div className="p-6 space-y-6">
          <div className="space-y-4">
            <div>
              <Label className="dark:text-gray-300">最小密码长度</Label>
              <div className="flex items-center gap-4 mt-2">
                <Slider
                  value={[settings.passwordMinLength]}
                  onValueChange={(value) => updateSetting('passwordMinLength', value[0])}
                  min={6}
                  max={20}
                  step={1}
                  className="flex-1"
                />
                <span className="text-sm dark:text-white w-12 text-right">
                  {settings.passwordMinLength} 位
                </span>
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="flex items-center justify-between p-4 border dark:border-gray-700 rounded-lg">
                <Label className="dark:text-gray-300">必须包含大写字母</Label>
                <Switch
                  checked={settings.passwordRequireUppercase}
                  onCheckedChange={(checked) => updateSetting('passwordRequireUppercase', checked)}
                />
              </div>

              <div className="flex items-center justify-between p-4 border dark:border-gray-700 rounded-lg">
                <Label className="dark:text-gray-300">必须包含小写字母</Label>
                <Switch
                  checked={settings.passwordRequireLowercase}
                  onCheckedChange={(checked) => updateSetting('passwordRequireLowercase', checked)}
                />
              </div>

              <div className="flex items-center justify-between p-4 border dark:border-gray-700 rounded-lg">
                <Label className="dark:text-gray-300">必须包含数字</Label>
                <Switch
                  checked={settings.passwordRequireNumber}
                  onCheckedChange={(checked) => updateSetting('passwordRequireNumber', checked)}
                />
              </div>

              <div className="flex items-center justify-between p-4 border dark:border-gray-700 rounded-lg">
                <Label className="dark:text-gray-300">必须包含特殊字符</Label>
                <Switch
                  checked={settings.passwordRequireSpecialChar}
                  onCheckedChange={(checked) => updateSetting('passwordRequireSpecialChar', checked)}
                />
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <Label className="dark:text-gray-300">密码过期天数</Label>
                <Input
                  type="number"
                  value={settings.passwordExpiryDays}
                  onChange={(e) => updateSetting('passwordExpiryDays', parseInt(e.target.value))}
                  className="mt-2 dark:bg-gray-900 dark:border-gray-700"
                />
                <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">
                  设置为 0 表示永不过期
                </p>
              </div>

              <div>
                <Label className="dark:text-gray-300">密码历史记录数</Label>
                <Input
                  type="number"
                  value={settings.passwordHistoryCount}
                  onChange={(e) => updateSetting('passwordHistoryCount', parseInt(e.target.value))}
                  className="mt-2 dark:bg-gray-900 dark:border-gray-700"
                />
                <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">
                  防止重复使用最近N次的密码
                </p>
              </div>
            </div>
          </div>
        </div>
      </Card>

      {/* 登录安全 */}
      <Card className="dark:bg-gray-800 dark:border-gray-700">
        <div className="p-6 border-b dark:border-gray-700">
          <div className="flex items-center gap-2">
            <Shield className="w-5 h-5 text-blue-500" />
            <h3 className="text-lg dark:text-white">登录安全</h3>
          </div>
          <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
            控制用户登录的安全措施
          </p>
        </div>

        <div className="p-6 space-y-6">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <Label className="dark:text-gray-300">最大登录尝试次数</Label>
              <Input
                type="number"
                value={settings.maxLoginAttempts}
                onChange={(e) => updateSetting('maxLoginAttempts', parseInt(e.target.value))}
                className="mt-2 dark:bg-gray-900 dark:border-gray-700"
              />
            </div>

            <div>
              <Label className="dark:text-gray-300">账户锁定时长（分钟）</Label>
              <Input
                type="number"
                value={settings.lockoutDuration}
                onChange={(e) => updateSetting('lockoutDuration', parseInt(e.target.value))}
                className="mt-2 dark:bg-gray-900 dark:border-gray-700"
              />
            </div>

            <div>
              <Label className="dark:text-gray-300">会话超时时间（分钟）</Label>
              <Input
                type="number"
                value={settings.sessionTimeout}
                onChange={(e) => updateSetting('sessionTimeout', parseInt(e.target.value))}
                className="mt-2 dark:bg-gray-900 dark:border-gray-700"
              />
            </div>

            <div>
              <Label className="dark:text-gray-300">双因素认证方式</Label>
              <Select
                value={settings.twoFactorMethod}
                onValueChange={(value) => updateSetting('twoFactorMethod', value)}
              >
                <SelectTrigger className="mt-2 dark:bg-gray-900 dark:border-gray-700">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="email">
                    <div className="flex items-center gap-2">
                      <Mail className="w-4 h-4" />
                      邮箱验证码
                    </div>
                  </SelectItem>
                  <SelectItem value="sms">
                    <div className="flex items-center gap-2">
                      <Smartphone className="w-4 h-4" />
                      短信验证码
                    </div>
                  </SelectItem>
                  <SelectItem value="authenticator">
                    <div className="flex items-center gap-2">
                      <Key className="w-4 h-4" />
                      身份验证器
                    </div>
                  </SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>

          <div className="space-y-4">
            <div className="flex items-center justify-between p-4 border dark:border-gray-700 rounded-lg">
              <div>
                <Label className="dark:text-gray-300">启用双因素认证</Label>
                <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
                  为所有用户启用双因素认证
                </p>
              </div>
              <Switch
                checked={settings.enableTwoFactor}
                onCheckedChange={(checked) => updateSetting('enableTwoFactor', checked)}
              />
            </div>

            <div className="flex items-center justify-between p-4 border dark:border-gray-700 rounded-lg">
              <div>
                <Label className="dark:text-gray-300">启用图形验证码</Label>
                <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
                  登录时需要输入验证码
                </p>
              </div>
              <Switch
                checked={settings.enableCaptcha}
                onCheckedChange={(checked) => updateSetting('enableCaptcha', checked)}
              />
            </div>
          </div>
        </div>
      </Card>

      {/* IP访问控制 */}
      <Card className="dark:bg-gray-800 dark:border-gray-700">
        <div className="p-6 border-b dark:border-gray-700">
          <div className="flex items-center gap-2">
            <Globe className="w-5 h-5 text-blue-500" />
            <h3 className="text-lg dark:text-white">IP访问控制</h3>
          </div>
          <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
            限制允许访问系统的IP地址
          </p>
        </div>

        <div className="p-6 space-y-4">
          <div className="flex items-center justify-between p-4 border dark:border-gray-700 rounded-lg">
            <div>
              <Label className="dark:text-gray-300">启用IP白名单</Label>
              <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
                仅允许白名单内的IP访问
              </p>
            </div>
            <Switch
              checked={settings.enableIpWhitelist}
              onCheckedChange={(checked) => updateSetting('enableIpWhitelist', checked)}
            />
          </div>

          {settings.enableIpWhitelist && (
            <div>
              <Label className="dark:text-gray-300">IP白名单</Label>
              <textarea
                value={settings.ipWhitelist}
                onChange={(e) => updateSetting('ipWhitelist', e.target.value)}
                placeholder="每行一个IP地址或CIDR范围，例如：&#10;192.168.1.1&#10;10.0.0.0/8"
                className="w-full mt-2 p-3 border dark:border-gray-700 rounded-lg dark:bg-gray-900 dark:text-white"
                rows={6}
              />
              <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">
                支持单个IP或CIDR格式，每行一个
              </p>
            </div>
          )}
        </div>
      </Card>

      {/* API安全 */}
      <Card className="dark:bg-gray-800 dark:border-gray-700">
        <div className="p-6 border-b dark:border-gray-700">
          <div className="flex items-center gap-2">
            <Key className="w-5 h-5 text-blue-500" />
            <h3 className="text-lg dark:text-white">API安全</h3>
          </div>
          <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
            API接口的安全限制
          </p>
        </div>

        <div className="p-6 space-y-4">
          <div className="flex items-center justify-between p-4 border dark:border-gray-700 rounded-lg">
            <div>
              <Label className="dark:text-gray-300">启用API速率限制</Label>
              <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
                防止API滥用和DDoS攻击
              </p>
            </div>
            <Switch
              checked={settings.enableApiRateLimit}
              onCheckedChange={(checked) => updateSetting('enableApiRateLimit', checked)}
            />
          </div>

          {settings.enableApiRateLimit && (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <Label className="dark:text-gray-300">请求限制数量</Label>
                <Input
                  type="number"
                  value={settings.apiRateLimit}
                  onChange={(e) => updateSetting('apiRateLimit', parseInt(e.target.value))}
                  className="mt-2 dark:bg-gray-900 dark:border-gray-700"
                />
              </div>

              <div>
                <Label className="dark:text-gray-300">时间窗口（秒）</Label>
                <Input
                  type="number"
                  value={settings.apiRateLimitWindow}
                  onChange={(e) => updateSetting('apiRateLimitWindow', parseInt(e.target.value))}
                  className="mt-2 dark:bg-gray-900 dark:border-gray-700"
                />
              </div>
            </div>
          )}
        </div>
      </Card>

      {/* 安全通知 */}
      <Card className="dark:bg-gray-800 dark:border-gray-700">
        <div className="p-6 border-b dark:border-gray-700">
          <div className="flex items-center gap-2">
            <AlertTriangle className="w-5 h-5 text-blue-500" />
            <h3 className="text-lg dark:text-white">安全通知</h3>
          </div>
          <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
            配置安全事件的通知设置
          </p>
        </div>

        <div className="p-6 space-y-4">
          <div className="flex items-center justify-between p-4 border dark:border-gray-700 rounded-lg">
            <div>
              <Label className="dark:text-gray-300">密码修改通知</Label>
              <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
                密码修改时发送通知
              </p>
            </div>
            <Switch
              checked={settings.notifyPasswordChange}
              onCheckedChange={(checked) => updateSetting('notifyPasswordChange', checked)}
            />
          </div>

          <div className="flex items-center justify-between p-4 border dark:border-gray-700 rounded-lg">
            <div>
              <Label className="dark:text-gray-300">新设备登录通知</Label>
              <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
                从新设备登录时发送通知
              </p>
            </div>
            <Switch
              checked={settings.notifyLoginFromNewDevice}
              onCheckedChange={(checked) => updateSetting('notifyLoginFromNewDevice', checked)}
            />
          </div>

          <div className="flex items-center justify-between p-4 border dark:border-gray-700 rounded-lg">
            <div>
              <Label className="dark:text-gray-300">API密钥使用通知</Label>
              <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
                API密钥被使用时发送通知
              </p>
            </div>
            <Switch
              checked={settings.notifyApiKeyUsage}
              onCheckedChange={(checked) => updateSetting('notifyApiKeyUsage', checked)}
            />
          </div>
        </div>
      </Card>
    </div>
  );
}
