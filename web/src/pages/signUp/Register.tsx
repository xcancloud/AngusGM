import { useState } from 'react';
import { Eye, EyeOff, Check, Shield, Zap, Globe, Sparkles, CheckCircle2, Languages } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Card } from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { toast } from 'sonner';
import { AngusGMLogo } from '@/components/AngusGMLogo';
import { useLanguage } from '@/components/auth/LanguageContext';

interface RegisterProps {
  onRegisterSuccess?: () => void;
  onNavigateToLogin?: () => void;
  onNavigateToAgreement?: () => void;
  onNavigateToPrivacy?: () => void;
}

export function Register({ onRegisterSuccess, onNavigateToLogin, onNavigateToAgreement, onNavigateToPrivacy }: RegisterProps) {
  const { language, setLanguage, t } = useLanguage();
  const [registerType, setRegisterType] = useState<'phone' | 'email'>('phone');
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [loading, setLoading] = useState(false);

  // 手机注册表单
  const [phoneForm, setPhoneForm] = useState({
    phone: '',
    code: '',
    password: '',
    confirmPassword: '',
    inviteCode: '',
    agree: false,
  });

  // 邮箱注册表单
  const [emailForm, setEmailForm] = useState({
    email: '',
    code: '',
    password: '',
    confirmPassword: '',
    inviteCode: '',
    agree: false,
  });

  const [countdown, setCountdown] = useState(0);

  // 密码强度检测
  const checkPasswordStrength = (password: string) => {
    let strength = 0;
    if (password.length >= 8) strength++;
    if (/[a-z]/.test(password) && /[A-Z]/.test(password)) strength++;
    if (/\d/.test(password)) strength++;
    if (/[!@#$%^&*(),.?":{}|<>]/.test(password)) strength++;
    return strength;
  };

  const getPasswordStrengthText = (password: string) => {
    const strength = checkPasswordStrength(password);
    if (!password) return { text: '', color: '', width: '0%' };
    if (strength <= 1) return { text: '弱', color: 'bg-red-500', width: '25%' };
    if (strength === 2) return { text: '中', color: 'bg-yellow-500', width: '50%' };
    if (strength === 3) return { text: '强', color: 'bg-green-500', width: '75%' };
    return { text: '很强', color: 'bg-blue-500', width: '100%' };
  };

  // 发送验证码
  const handleSendCode = (type: 'phone' | 'email') => {
    const value = type === 'phone' ? phoneForm.phone : emailForm.email;
    if (!value) {
      toast.error(`请输入${type === 'phone' ? '手机号' : '邮箱地址'}`);
      return;
    }

    // 验证格式
    if (type === 'phone' && !/^1[3-9]\d{9}$/.test(value)) {
      toast.error('请输入正确的手机号');
      return;
    }
    if (type === 'email' && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
      toast.error('请输入正确的邮箱地址');
      return;
    }

    toast.success(`验证码已发送至${type === 'phone' ? '手机' : '邮箱'}`);
    setCountdown(60);
    const timer = setInterval(() => {
      setCountdown((prev) => {
        if (prev <= 1) {
          clearInterval(timer);
          return 0;
        }
        return prev - 1;
      });
    }, 1000);
  };

  // 手机注册
  const handlePhoneRegister = (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!phoneForm.phone || !phoneForm.code || !phoneForm.password || !phoneForm.confirmPassword) {
      toast.error('请填写所有必填项');
      return;
    }

    if (!/^1[3-9]\d{9}$/.test(phoneForm.phone)) {
      toast.error('请输入正确的手机号');
      return;
    }

    if (phoneForm.password.length < 8) {
      toast.error('密码长度至少为8位');
      return;
    }

    if (phoneForm.password !== phoneForm.confirmPassword) {
      toast.error('两次输入的密码不一致');
      return;
    }

    if (!phoneForm.agree) {
      toast.error('请阅读并同意用户协议和隐私政策');
      return;
    }

    setLoading(true);
    setTimeout(() => {
      setLoading(false);
      toast.success('注册成功！');
      onRegisterSuccess?.();
    }, 1000);
  };

  // 邮箱注册
  const handleEmailRegister = (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!emailForm.email || !emailForm.code || !emailForm.password || !emailForm.confirmPassword) {
      toast.error('请填写所有必填项');
      return;
    }

    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(emailForm.email)) {
      toast.error('请输入正确的邮箱地址');
      return;
    }

    if (emailForm.password.length < 8) {
      toast.error('密码长度至少为8位');
      return;
    }

    if (emailForm.password !== emailForm.confirmPassword) {
      toast.error('两次输入的密码不一致');
      return;
    }

    if (!emailForm.agree) {
      toast.error('请阅读并同意用户协议和隐私政策');
      return;
    }

    setLoading(true);
    setTimeout(() => {
      setLoading(false);
      toast.success('注册成功！');
      onRegisterSuccess?.();
    }, 1000);
  };

  const currentForm = registerType === 'phone' ? phoneForm : emailForm;
  const currentPassword = registerType === 'phone' ? phoneForm.password : emailForm.password;
  const strengthInfo = getPasswordStrengthText(currentPassword);

  return (
    <div className="w-full min-h-screen bg-gradient-to-br from-purple-50 via-blue-50 to-indigo-50 dark:from-gray-950 dark:via-purple-950 dark:to-gray-950 relative overflow-hidden">
      {/* 背景装饰 */}
      <div className="absolute inset-0 overflow-hidden pointer-events-none">
        <div className="absolute -top-40 -right-40 w-80 h-80 bg-purple-400/20 rounded-full blur-3xl animate-pulse"></div>
        <div className="absolute top-1/2 -left-40 w-80 h-80 bg-blue-400/20 rounded-full blur-3xl animate-pulse" style={{ animationDelay: '1s' }}></div>
        <div className="absolute -bottom-40 right-1/3 w-80 h-80 bg-indigo-400/20 rounded-full blur-3xl animate-pulse" style={{ animationDelay: '2s' }}></div>
      </div>

      <div className="relative z-10 min-h-screen flex">
        {/* 左侧品牌区 */}
        <div className="lg:flex lg:w-1/2 flex-col justify-between p-8 xl:p-16 2xl:p-20">
          {/* Logo和标题 */}
          <div>
            <div className="mb-12 xl:mb-16">
              <AngusGMLogo className="h-12 mb-6" />
              <h1 className="text-4xl xl:text-5xl 2xl:text-6xl mb-6 bg-gradient-to-r from-purple-600 via-blue-600 to-indigo-600 dark:from-purple-400 dark:via-blue-400 dark:to-indigo-400 bg-clip-text text-transparent leading-tight">
                开启管理新篇章
              </h1>
              <p className="text-lg xl:text-xl text-gray-600 dark:text-gray-400">
                注册 AngusGM 账号，体验专业的企业级管理平台
              </p>
            </div>

            {/* 优势展示 */}
            <div className="grid gap-4">
              <div className="group flex items-start gap-4 p-4 rounded-2xl bg-white/50 dark:bg-gray-800/50 backdrop-blur-sm border border-gray-200/50 dark:border-gray-700/50 hover:border-purple-300 dark:hover:border-purple-600 transition-all duration-300 hover:shadow-lg hover:shadow-purple-500/10">
                <div className="w-12 h-12 rounded-xl bg-gradient-to-br from-purple-500 to-purple-600 flex items-center justify-center flex-shrink-0 group-hover:scale-110 transition-transform duration-300">
                  <CheckCircle2 className="w-6 h-6 text-white" />
                </div>
                <div>
                  <h3 className="text-base mb-1 text-gray-900 dark:text-white">快速部署</h3>
                  <p className="text-sm text-gray-600 dark:text-gray-400">5分钟即可完成注册并开始使用</p>
                </div>
              </div>

              <div className="group flex items-start gap-4 p-4 rounded-2xl bg-white/50 dark:bg-gray-800/50 backdrop-blur-sm border border-gray-200/50 dark:border-gray-700/50 hover:border-blue-300 dark:hover:border-blue-600 transition-all duration-300 hover:shadow-lg hover:shadow-blue-500/10">
                <div className="w-12 h-12 rounded-xl bg-gradient-to-br from-blue-500 to-blue-600 flex items-center justify-center flex-shrink-0 group-hover:scale-110 transition-transform duration-300">
                  <Shield className="w-6 h-6 text-white" />
                </div>
                <div>
                  <h3 className="text-base mb-1 text-gray-900 dark:text-white">安全保障</h3>
                  <p className="text-sm text-gray-600 dark:text-gray-400">企业级加密，数据安全无忧</p>
                </div>
              </div>

              <div className="group flex items-start gap-4 p-4 rounded-2xl bg-white/50 dark:bg-gray-800/50 backdrop-blur-sm border border-gray-200/50 dark:border-gray-700/50 hover:border-indigo-300 dark:hover:border-indigo-600 transition-all duration-300 hover:shadow-lg hover:shadow-indigo-500/10">
                <div className="w-12 h-12 rounded-xl bg-gradient-to-br from-indigo-500 to-indigo-600 flex items-center justify-center flex-shrink-0 group-hover:scale-110 transition-transform duration-300">
                  <Zap className="w-6 h-6 text-white" />
                </div>
                <div>
                  <h3 className="text-base mb-1 text-gray-900 dark:text-white">专业支持</h3>
                  <p className="text-sm text-gray-600 dark:text-gray-400">7×24小时技术支持团队</p>
                </div>
              </div>
            </div>
          </div>

          {/* 底部信息 */}
          <div className="mt-auto pt-8">
            <p className="text-sm text-gray-500 dark:text-gray-500">
              © 2024 AngusGM. All rights reserved.
            </p>
          </div>
        </div>

        {/* 右侧注册表单区 */}
        <div className="w-full lg:w-1/2 flex items-center justify-center p-6 py-8 lg:px-12 xl:px-16">
          <div className="w-full max-w-lg">
            <Card className="border-0 shadow-2xl shadow-purple-500/10 dark:shadow-purple-500/5 bg-white/80 dark:bg-gray-800/80 backdrop-blur-xl">
              <div className="p-6 sm:p-8 lg:p-10">
                {/* 标题 */}
                <div className="text-center mb-3">
                  <h2 className="text-2xl mb-2 text-gray-900 dark:text-white">创建账号</h2>
                </div>

                {/* 注册方式标签 */}
                <Tabs value={registerType} onValueChange={(v) => setRegisterType(v as any)} className="mb-6">
                  <TabsList className="grid w-full grid-cols-2 bg-gray-100 dark:bg-gray-700/50 p-1 rounded-xl">
                    <TabsTrigger value="phone" className="rounded-lg data-[state=active]:bg-white dark:data-[state=active]:bg-gray-600 data-[state=active]:shadow-sm">
                      手机注册
                    </TabsTrigger>
                    <TabsTrigger value="email" className="rounded-lg data-[state=active]:bg-white dark:data-[state=active]:bg-gray-600 data-[state=active]:shadow-sm">
                      邮箱注册
                    </TabsTrigger>
                  </TabsList>

                  {/* 手机注册 */}
                  <TabsContent value="phone" className="mt-6">
                    <form onSubmit={handlePhoneRegister} className="space-y-4">
                      <div>
                        <label className="block text-sm mb-2 text-gray-700 dark:text-gray-300">
                          手机号 <span className="text-red-500">*</span>
                        </label>
                        <Input
                          type="tel"
                          placeholder="请输入手机号"
                          value={phoneForm.phone}
                          onChange={(e) => setPhoneForm({ ...phoneForm, phone: e.target.value })}
                          className="h-12 bg-gray-50 dark:bg-gray-700/50 border-gray-200 dark:border-gray-600 focus:border-purple-500 dark:focus:border-purple-500 rounded-xl"
                        />
                      </div>

                      <div>
                        <label className="block text-sm mb-2 text-gray-700 dark:text-gray-300">
                          验证码 <span className="text-red-500">*</span>
                        </label>
                        <div className="flex gap-3">
                          <Input
                            type="text"
                            placeholder="请输入验证码"
                            value={phoneForm.code}
                            onChange={(e) => setPhoneForm({ ...phoneForm, code: e.target.value })}
                            className="flex-1 h-12 bg-gray-50 dark:bg-gray-700/50 border-gray-200 dark:border-gray-600 focus:border-purple-500 dark:focus:border-purple-500 rounded-xl"
                          />
                          <Button
                            type="button"
                            variant="outline"
                            onClick={() => handleSendCode('phone')}
                            disabled={countdown > 0}
                            className="h-12 px-6 rounded-xl border-gray-200 dark:border-gray-600 hover:border-purple-500 dark:hover:border-purple-500 transition-colors"
                          >
                            {countdown > 0 ? `${countdown}s` : '发送'}
                          </Button>
                        </div>
                      </div>

                      <div>
                        <label className="block text-sm mb-2 text-gray-700 dark:text-gray-300">
                          密码 <span className="text-red-500">*</span>
                        </label>
                        <div className="relative">
                          <Input
                            type={showPassword ? 'text' : 'password'}
                            placeholder="至少8位，包含字母和数字"
                            value={phoneForm.password}
                            onChange={(e) => setPhoneForm({ ...phoneForm, password: e.target.value })}
                            className="h-12 bg-gray-50 dark:bg-gray-700/50 border-gray-200 dark:border-gray-600 focus:border-purple-500 dark:focus:border-purple-500 rounded-xl pr-12"
                          />
                          <button
                            type="button"
                            onClick={() => setShowPassword(!showPassword)}
                            className="absolute right-4 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors"
                          >
                            {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                          </button>
                        </div>
                        {phoneForm.password && (
                          <div className="mt-2">
                            <div className="flex items-center justify-between mb-1">
                              <span className="text-xs text-gray-500 dark:text-gray-400">密码强度</span>
                              <span className={`text-xs ${strengthInfo.color.replace('bg-', 'text-')}`}>
                                {strengthInfo.text}
                              </span>
                            </div>
                            <div className="h-1.5 bg-gray-200 dark:bg-gray-700 rounded-full overflow-hidden">
                              <div 
                                className={`h-full ${strengthInfo.color} transition-all duration-300`}
                                style={{ width: strengthInfo.width }}
                              ></div>
                            </div>
                          </div>
                        )}
                      </div>

                      <div>
                        <label className="block text-sm mb-2 text-gray-700 dark:text-gray-300">
                          确认密码 <span className="text-red-500">*</span>
                        </label>
                        <div className="relative">
                          <Input
                            type={showConfirmPassword ? 'text' : 'password'}
                            placeholder="请再次输入密码"
                            value={phoneForm.confirmPassword}
                            onChange={(e) => setPhoneForm({ ...phoneForm, confirmPassword: e.target.value })}
                            className="h-12 bg-gray-50 dark:bg-gray-700/50 border-gray-200 dark:border-gray-600 focus:border-purple-500 dark:focus:border-purple-500 rounded-xl pr-12"
                          />
                          <button
                            type="button"
                            onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                            className="absolute right-4 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors"
                          >
                            {showConfirmPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                          </button>
                        </div>
                      </div>

                      <div>
                        <label className="block text-sm mb-2 text-gray-700 dark:text-gray-300">
                          邀请码（可选）
                        </label>
                        <Input
                          type="text"
                          placeholder="如有邀请码请输入"
                          value={phoneForm.inviteCode}
                          onChange={(e) => setPhoneForm({ ...phoneForm, inviteCode: e.target.value })}
                          className="h-12 bg-gray-50 dark:bg-gray-700/50 border-gray-200 dark:border-gray-600 focus:border-purple-500 dark:focus:border-purple-500 rounded-xl"
                        />
                      </div>

                      <div className="flex items-start gap-3 pt-2">
                        <input
                          type="checkbox"
                          id="phone-agree"
                          checked={phoneForm.agree}
                          onChange={(e) => setPhoneForm({ ...phoneForm, agree: e.target.checked })}
                          className="mt-1 w-4 h-4 rounded border-gray-300 text-purple-600 focus:ring-purple-500 focus:ring-offset-0 transition-colors"
                        />
                        <label htmlFor="phone-agree" className="text-sm text-gray-600 dark:text-gray-400 leading-relaxed">
                          我已阅读并同意
                          <a href="/user-agreement" className="text-purple-600 hover:text-purple-700 dark:text-purple-400 dark:hover:text-purple-300 mx-1 transition-colors" onClick={onNavigateToAgreement}>
                            《用户协议》
                          </a>
                          和
                          <a href="/privacy-policy" className="text-purple-600 hover:text-purple-700 dark:text-purple-400 dark:hover:text-purple-300 mx-1 transition-colors" onClick={onNavigateToPrivacy}>
                            《隐私政策》
                          </a>
                        </label>
                      </div>

                      <Button
                        type="submit"
                        className="w-full h-12 bg-gradient-to-r from-purple-600 to-indigo-600 hover:from-purple-700 hover:to-indigo-700 text-white rounded-xl shadow-lg shadow-purple-500/30 hover:shadow-xl hover:shadow-purple-500/40 transition-all duration-300 mt-2"
                        disabled={loading}
                      >
                        {loading ? (
                          <span className="flex items-center gap-2">
                            <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
                            注册中...
                          </span>
                        ) : (
                          '立即注册'
                        )}
                      </Button>
                    </form>
                  </TabsContent>

                  {/* 邮箱注册 */}
                  <TabsContent value="email" className="mt-6">
                    <form onSubmit={handleEmailRegister} className="space-y-4">
                      <div>
                        <label className="block text-sm mb-2 text-gray-700 dark:text-gray-300">
                          邮箱地址 <span className="text-red-500">*</span>
                        </label>
                        <Input
                          type="email"
                          placeholder="请输入邮箱地址"
                          value={emailForm.email}
                          onChange={(e) => setEmailForm({ ...emailForm, email: e.target.value })}
                          className="h-12 bg-gray-50 dark:bg-gray-700/50 border-gray-200 dark:border-gray-600 focus:border-purple-500 dark:focus:border-purple-500 rounded-xl"
                        />
                      </div>

                      <div>
                        <label className="block text-sm mb-2 text-gray-700 dark:text-gray-300">
                          验证码 <span className="text-red-500">*</span>
                        </label>
                        <div className="flex gap-3">
                          <Input
                            type="text"
                            placeholder="请输入验证码"
                            value={emailForm.code}
                            onChange={(e) => setEmailForm({ ...emailForm, code: e.target.value })}
                            className="flex-1 h-12 bg-gray-50 dark:bg-gray-700/50 border-gray-200 dark:border-gray-600 focus:border-purple-500 dark:focus:border-purple-500 rounded-xl"
                          />
                          <Button
                            type="button"
                            variant="outline"
                            onClick={() => handleSendCode('email')}
                            disabled={countdown > 0}
                            className="h-12 px-6 rounded-xl border-gray-200 dark:border-gray-600 hover:border-purple-500 dark:hover:border-purple-500 transition-colors"
                          >
                            {countdown > 0 ? `${countdown}s` : '发送'}
                          </Button>
                        </div>
                      </div>

                      <div>
                        <label className="block text-sm mb-2 text-gray-700 dark:text-gray-300">
                          密码 <span className="text-red-500">*</span>
                        </label>
                        <div className="relative">
                          <Input
                            type={showPassword ? 'text' : 'password'}
                            placeholder="至少8位，包含字母和数字"
                            value={emailForm.password}
                            onChange={(e) => setEmailForm({ ...emailForm, password: e.target.value })}
                            className="h-12 bg-gray-50 dark:bg-gray-700/50 border-gray-200 dark:border-gray-600 focus:border-purple-500 dark:focus:border-purple-500 rounded-xl pr-12"
                          />
                          <button
                            type="button"
                            onClick={() => setShowPassword(!showPassword)}
                            className="absolute right-4 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors"
                          >
                            {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                          </button>
                        </div>
                        {emailForm.password && (
                          <div className="mt-2">
                            <div className="flex items-center justify-between mb-1">
                              <span className="text-xs text-gray-500 dark:text-gray-400">密码强度</span>
                              <span className={`text-xs ${strengthInfo.color.replace('bg-', 'text-')}`}>
                                {strengthInfo.text}
                              </span>
                            </div>
                            <div className="h-1.5 bg-gray-200 dark:bg-gray-700 rounded-full overflow-hidden">
                              <div 
                                className={`h-full ${strengthInfo.color} transition-all duration-300`}
                                style={{ width: strengthInfo.width }}
                              ></div>
                            </div>
                          </div>
                        )}
                      </div>

                      <div>
                        <label className="block text-sm mb-2 text-gray-700 dark:text-gray-300">
                          确认密码 <span className="text-red-500">*</span>
                        </label>
                        <div className="relative">
                          <Input
                            type={showConfirmPassword ? 'text' : 'password'}
                            placeholder="请再次输入密码"
                            value={emailForm.confirmPassword}
                            onChange={(e) => setEmailForm({ ...emailForm, confirmPassword: e.target.value })}
                            className="h-12 bg-gray-50 dark:bg-gray-700/50 border-gray-200 dark:border-gray-600 focus:border-purple-500 dark:focus:border-purple-500 rounded-xl pr-12"
                          />
                          <button
                            type="button"
                            onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                            className="absolute right-4 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors"
                          >
                            {showConfirmPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                          </button>
                        </div>
                      </div>

                      <div>
                        <label className="block text-sm mb-2 text-gray-700 dark:text-gray-300">
                          邀请码（可选）
                        </label>
                        <Input
                          type="text"
                          placeholder="如有邀请码请输入"
                          value={emailForm.inviteCode}
                          onChange={(e) => setEmailForm({ ...emailForm, inviteCode: e.target.value })}
                          className="h-12 bg-gray-50 dark:bg-gray-700/50 border-gray-200 dark:border-gray-600 focus:border-purple-500 dark:focus:border-purple-500 rounded-xl"
                        />
                      </div>

                      <div className="flex items-start gap-3 pt-2">
                        <input
                          type="checkbox"
                          id="email-agree"
                          checked={emailForm.agree}
                          onChange={(e) => setEmailForm({ ...emailForm, agree: e.target.checked })}
                          className="mt-1 w-4 h-4 rounded border-gray-300 text-purple-600 focus:ring-purple-500 focus:ring-offset-0 transition-colors"
                        />
                        <label htmlFor="email-agree" className="text-sm text-gray-600 dark:text-gray-400 leading-relaxed">
                          我已阅读并同意
                          <a href="/user-agreement" className="text-purple-600 hover:text-purple-700 dark:text-purple-400 dark:hover:text-purple-300 mx-1 transition-colors" onClick={onNavigateToAgreement}>
                            《用户协议》
                          </a>
                          和
                          <a href="/privacy-policy" className="text-purple-600 hover:text-purple-700 dark:text-purple-400 dark:hover:text-purple-300 mx-1 transition-colors" onClick={onNavigateToPrivacy}>
                            《隐私政策》
                          </a>
                        </label>
                      </div>

                      <Button
                        type="submit"
                        className="w-full h-12 bg-gradient-to-r from-purple-600 to-indigo-600 hover:from-purple-700 hover:to-indigo-700 text-white rounded-xl shadow-lg shadow-purple-500/30 hover:shadow-xl hover:shadow-purple-500/40 transition-all duration-300 mt-2"
                        disabled={loading}
                      >
                        {loading ? (
                          <span className="flex items-center gap-2">
                            <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
                            注册中...
                          </span>
                        ) : (
                          '立即注册'
                        )}
                      </Button>
                    </form>
                  </TabsContent>
                </Tabs>

                {/* 登录链接 */}
                <div className="text-center mt-6">
                  <span className="text-sm text-gray-600 dark:text-gray-400">已有账号？</span>
                  <button
                    onClick={onNavigateToLogin}
                    className="text-sm text-purple-600 hover:text-purple-700 dark:text-purple-400 dark:hover:text-purple-300 ml-2 font-medium transition-colors"
                  >
                    立即登录
                  </button>
                </div>
              </div>
            </Card>

            {/* 移动端底部信息 */}
            <div className="lg:hidden text-center mt-8">
              <p className="text-sm text-gray-500 dark:text-gray-500">
                © 2024 AngusGM. All rights reserved.
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}