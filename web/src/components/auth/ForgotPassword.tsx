import { useState } from 'react';
import { ArrowLeft, Eye, EyeOff, Shield, Zap, KeyRound, Sparkles } from 'lucide-react';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Card } from '../ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '../ui/tabs';
import { toast } from 'sonner';
import { AngusGMLogo } from '../AngusGMLogo';

interface ForgotPasswordProps {
  onResetSuccess?: () => void;
  onNavigateToLogin?: () => void;
}

export function ForgotPassword({ onResetSuccess, onNavigateToLogin }: ForgotPasswordProps) {
  const [resetType, setResetType] = useState<'phone' | 'email'>('phone');
  const [step, setStep] = useState<1 | 2>(1);
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [loading, setLoading] = useState(false);

  // 手机找回表单
  const [phoneForm, setPhoneForm] = useState({
    phone: '',
    code: '',
    password: '',
    confirmPassword: '',
  });

  // 邮箱找回表单
  const [emailForm, setEmailForm] = useState({
    email: '',
    code: '',
    password: '',
    confirmPassword: '',
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

  // 验证码验证
  const handleVerifyCode = (type: 'phone' | 'email') => {
    const form = type === 'phone' ? phoneForm : emailForm;
    
    if (!form.code) {
      toast.error('请输入验证码');
      return;
    }

    setLoading(true);
    setTimeout(() => {
      setLoading(false);
      toast.success('验证成功！');
      setStep(2);
    }, 1000);
  };

  // 重置密码
  const handleResetPassword = (type: 'phone' | 'email') => {
    const form = type === 'phone' ? phoneForm : emailForm;
    
    if (!form.password || !form.confirmPassword) {
      toast.error('请填写新密码');
      return;
    }

    if (form.password.length < 8) {
      toast.error('密码长度至少为8位');
      return;
    }

    if (form.password !== form.confirmPassword) {
      toast.error('两次输入的密码不一致');
      return;
    }

    setLoading(true);
    setTimeout(() => {
      setLoading(false);
      toast.success('密码重置成功！');
      onResetSuccess?.();
    }, 1000);
  };

  const currentPassword = resetType === 'phone' ? phoneForm.password : emailForm.password;
  const strengthInfo = getPasswordStrengthText(currentPassword);

  return (
    <div className="w-full min-h-screen bg-gradient-to-br from-indigo-50 via-blue-50 to-cyan-50 dark:from-gray-950 dark:via-indigo-950 dark:to-gray-950 relative overflow-hidden">
      {/* 背景装饰 */}
      <div className="absolute inset-0 overflow-hidden pointer-events-none">
        <div className="absolute -top-40 -right-40 w-80 h-80 bg-indigo-400/20 rounded-full blur-3xl animate-pulse"></div>
        <div className="absolute top-1/2 -left-40 w-80 h-80 bg-cyan-400/20 rounded-full blur-3xl animate-pulse" style={{ animationDelay: '1s' }}></div>
        <div className="absolute -bottom-40 right-1/3 w-80 h-80 bg-blue-400/20 rounded-full blur-3xl animate-pulse" style={{ animationDelay: '2s' }}></div>
      </div>

      <div className="relative z-10 min-h-screen flex">
        {/* 左侧品牌区 */}
        <div className="lg:flex lg:w-1/2 flex-col justify-between p-8 xl:p-16 2xl:p-20">
          {/* Logo和标题 */}
          <div>
            <div className="mb-12 xl:mb-16">
              <AngusGMLogo className="h-12 mb-6" />
              <h1 className="text-4xl xl:text-5xl 2xl:text-6xl mb-6 bg-gradient-to-r from-indigo-600 via-blue-600 to-cyan-600 dark:from-indigo-400 dark:via-blue-400 dark:to-cyan-400 bg-clip-text text-transparent leading-tight">
                找回密码
              </h1>
              <p className="text-lg xl:text-xl text-gray-600 dark:text-gray-400">
                通过验证快速重置您的账号密码
              </p>
            </div>

            {/* 特性展示 */}
            <div className="grid gap-4">
              <div className="group flex items-start gap-4 p-4 rounded-2xl bg-white/50 dark:bg-gray-800/50 backdrop-blur-sm border border-gray-200/50 dark:border-gray-700/50 hover:border-indigo-300 dark:hover:border-indigo-600 transition-all duration-300 hover:shadow-lg hover:shadow-indigo-500/10">
                <div className="w-12 h-12 rounded-xl bg-gradient-to-br from-indigo-500 to-indigo-600 flex items-center justify-center flex-shrink-0 group-hover:scale-110 transition-transform duration-300">
                  <Shield className="w-6 h-6 text-white" />
                </div>
                <div>
                  <h3 className="text-base mb-1 text-gray-900 dark:text-white">安全验证</h3>
                  <p className="text-sm text-gray-600 dark:text-gray-400">多重身份验证，保障账号安全</p>
                </div>
              </div>

              <div className="group flex items-start gap-4 p-4 rounded-2xl bg-white/50 dark:bg-gray-800/50 backdrop-blur-sm border border-gray-200/50 dark:border-gray-700/50 hover:border-blue-300 dark:hover:border-blue-600 transition-all duration-300 hover:shadow-lg hover:shadow-blue-500/10">
                <div className="w-12 h-12 rounded-xl bg-gradient-to-br from-blue-500 to-blue-600 flex items-center justify-center flex-shrink-0 group-hover:scale-110 transition-transform duration-300">
                  <Zap className="w-6 h-6 text-white" />
                </div>
                <div>
                  <h3 className="text-base mb-1 text-gray-900 dark:text-white">快速重置</h3>
                  <p className="text-sm text-gray-600 dark:text-gray-400">两步即可完成密码重置</p>
                </div>
              </div>

              <div className="group flex items-start gap-4 p-4 rounded-2xl bg-white/50 dark:bg-gray-800/50 backdrop-blur-sm border border-gray-200/50 dark:border-gray-700/50 hover:border-cyan-300 dark:hover:border-cyan-600 transition-all duration-300 hover:shadow-lg hover:shadow-cyan-500/10">
                <div className="w-12 h-12 rounded-xl bg-gradient-to-br from-cyan-500 to-cyan-600 flex items-center justify-center flex-shrink-0 group-hover:scale-110 transition-transform duration-300">
                  <KeyRound className="w-6 h-6 text-white" />
                </div>
                <div>
                  <h3 className="text-base mb-1 text-gray-900 dark:text-white">24小时服务</h3>
                  <p className="text-sm text-gray-600 dark:text-gray-400">随时为您提供找回服务</p>
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

        {/* 右侧重置表单区 */}
        <div className="w-full lg:w-1/2 flex items-center justify-center p-6 lg:p-12 xl:p-16">
          <div className="w-full max-w-lg">
            <Card className="border-0 shadow-2xl shadow-indigo-500/10 dark:shadow-indigo-500/5 bg-white/80 dark:bg-gray-800/80 backdrop-blur-xl">
              <div className="p-6 sm:p-8 lg:p-10">
                {/* 返回按钮 */}
                <button
                  onClick={onNavigateToLogin}
                  className="inline-flex items-center gap-2 text-sm text-gray-600 hover:text-gray-900 dark:text-gray-400 dark:hover:text-gray-200 mb-6 transition-colors group"
                >
                  <ArrowLeft className="w-4 h-4 group-hover:-translate-x-1 transition-transform" />
                  返回登录
                </button>

                {/* 标题 */}
                <div className="text-center mb-8">
                  <h2 className="text-2xl mb-2 text-gray-900 dark:text-white">
                    {step === 1 ? '验证身份' : '设置新密码'}
                  </h2>
                  <p className="text-sm text-gray-600 dark:text-gray-400">
                    {step === 1 ? '请选择验证方式' : '请设置您的新密码'}
                  </p>
                </div>

                {/* 进度指示器 */}
                <div className="flex items-center justify-center mb-8">
                  <div className="flex items-center gap-3">
                    <div className={`flex items-center justify-center w-10 h-10 rounded-full text-sm transition-all duration-300 ${
                      step >= 1 
                        ? 'bg-gradient-to-br from-indigo-500 to-blue-600 text-white shadow-lg shadow-indigo-500/30' 
                        : 'bg-gray-200 text-gray-600 dark:bg-gray-700 dark:text-gray-400'
                    }`}>
                      {step > 1 ? '✓' : '1'}
                    </div>
                    <div className={`w-20 h-1 rounded-full transition-all duration-300 ${
                      step >= 2 
                        ? 'bg-gradient-to-r from-indigo-500 to-blue-600' 
                        : 'bg-gray-200 dark:bg-gray-700'
                    }`}></div>
                    <div className={`flex items-center justify-center w-10 h-10 rounded-full text-sm transition-all duration-300 ${
                      step >= 2 
                        ? 'bg-gradient-to-br from-indigo-500 to-blue-600 text-white shadow-lg shadow-indigo-500/30' 
                        : 'bg-gray-200 text-gray-600 dark:bg-gray-700 dark:text-gray-400'
                    }`}>
                      2
                    </div>
                  </div>
                </div>

                {/* 重置方式标签 */}
                <Tabs value={resetType} onValueChange={(v) => setResetType(v as any)} className="mb-6">
                  <TabsList className="grid w-full grid-cols-2 bg-gray-100 dark:bg-gray-700/50 p-1 rounded-xl">
                    <TabsTrigger value="phone" className="rounded-lg data-[state=active]:bg-white dark:data-[state=active]:bg-gray-600 data-[state=active]:shadow-sm">
                      手机找回
                    </TabsTrigger>
                    <TabsTrigger value="email" className="rounded-lg data-[state=active]:bg-white dark:data-[state=active]:bg-gray-600 data-[state=active]:shadow-sm">
                      邮箱找回
                    </TabsTrigger>
                  </TabsList>

                  {/* 手机找回 */}
                  <TabsContent value="phone" className="mt-6">
                    {step === 1 ? (
                      <div className="space-y-5">
                        <div>
                          <label className="block text-sm mb-2 text-gray-700 dark:text-gray-300">
                            手机号
                          </label>
                          <Input
                            type="tel"
                            placeholder="请输入手机号"
                            value={phoneForm.phone}
                            onChange={(e) => setPhoneForm({ ...phoneForm, phone: e.target.value })}
                            className="h-12 bg-gray-50 dark:bg-gray-700/50 border-gray-200 dark:border-gray-600 focus:border-indigo-500 dark:focus:border-indigo-500 rounded-xl"
                          />
                        </div>

                        <div>
                          <label className="block text-sm mb-2 text-gray-700 dark:text-gray-300">
                            验证码
                          </label>
                          <div className="flex gap-3">
                            <Input
                              type="text"
                              placeholder="请输入验证码"
                              value={phoneForm.code}
                              onChange={(e) => setPhoneForm({ ...phoneForm, code: e.target.value })}
                              className="flex-1 h-12 bg-gray-50 dark:bg-gray-700/50 border-gray-200 dark:border-gray-600 focus:border-indigo-500 dark:focus:border-indigo-500 rounded-xl"
                            />
                            <Button
                              type="button"
                              variant="outline"
                              onClick={() => handleSendCode('phone')}
                              disabled={countdown > 0}
                              className="h-12 px-6 rounded-xl border-gray-200 dark:border-gray-600 hover:border-indigo-500 dark:hover:border-indigo-500 transition-colors"
                            >
                              {countdown > 0 ? `${countdown}s` : '发送'}
                            </Button>
                          </div>
                        </div>

                        <Button
                          onClick={() => handleVerifyCode('phone')}
                          className="w-full h-12 bg-gradient-to-r from-indigo-600 to-blue-600 hover:from-indigo-700 hover:to-blue-700 text-white rounded-xl shadow-lg shadow-indigo-500/30 hover:shadow-xl hover:shadow-indigo-500/40 transition-all duration-300"
                          disabled={loading}
                        >
                          {loading ? (
                            <span className="flex items-center gap-2">
                              <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
                              验证中...
                            </span>
                          ) : (
                            '下一步'
                          )}
                        </Button>
                      </div>
                    ) : (
                      <div className="space-y-5">
                        <div>
                          <label className="block text-sm mb-2 text-gray-700 dark:text-gray-300">
                            新密码
                          </label>
                          <div className="relative">
                            <Input
                              type={showPassword ? 'text' : 'password'}
                              placeholder="至少8位，包含字母和数字"
                              value={phoneForm.password}
                              onChange={(e) => setPhoneForm({ ...phoneForm, password: e.target.value })}
                              className="h-12 bg-gray-50 dark:bg-gray-700/50 border-gray-200 dark:border-gray-600 focus:border-indigo-500 dark:focus:border-indigo-500 rounded-xl pr-12"
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
                            确认密码
                          </label>
                          <div className="relative">
                            <Input
                              type={showConfirmPassword ? 'text' : 'password'}
                              placeholder="请再次输入新密码"
                              value={phoneForm.confirmPassword}
                              onChange={(e) => setPhoneForm({ ...phoneForm, confirmPassword: e.target.value })}
                              className="h-12 bg-gray-50 dark:bg-gray-700/50 border-gray-200 dark:border-gray-600 focus:border-indigo-500 dark:focus:border-indigo-500 rounded-xl pr-12"
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

                        <div className="flex gap-3 pt-2">
                          <Button
                            variant="outline"
                            onClick={() => setStep(1)}
                            className="flex-1 h-12 rounded-xl border-gray-200 dark:border-gray-600 hover:border-indigo-500 dark:hover:border-indigo-500 transition-colors"
                          >
                            上一步
                          </Button>
                          <Button
                            onClick={() => handleResetPassword('phone')}
                            className="flex-1 h-12 bg-gradient-to-r from-indigo-600 to-blue-600 hover:from-indigo-700 hover:to-blue-700 text-white rounded-xl shadow-lg shadow-indigo-500/30 hover:shadow-xl hover:shadow-indigo-500/40 transition-all duration-300"
                            disabled={loading}
                          >
                            {loading ? (
                              <span className="flex items-center gap-2">
                                <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
                                提交中...
                              </span>
                            ) : (
                              '确认修改'
                            )}
                          </Button>
                        </div>
                      </div>
                    )}
                  </TabsContent>

                  {/* 邮箱找回 */}
                  <TabsContent value="email" className="mt-6">
                    {step === 1 ? (
                      <div className="space-y-5">
                        <div>
                          <label className="block text-sm mb-2 text-gray-700 dark:text-gray-300">
                            邮箱地址
                          </label>
                          <Input
                            type="email"
                            placeholder="请输入邮箱地址"
                            value={emailForm.email}
                            onChange={(e) => setEmailForm({ ...emailForm, email: e.target.value })}
                            className="h-12 bg-gray-50 dark:bg-gray-700/50 border-gray-200 dark:border-gray-600 focus:border-indigo-500 dark:focus:border-indigo-500 rounded-xl"
                          />
                        </div>

                        <div>
                          <label className="block text-sm mb-2 text-gray-700 dark:text-gray-300">
                            验证码
                          </label>
                          <div className="flex gap-3">
                            <Input
                              type="text"
                              placeholder="请输入验证码"
                              value={emailForm.code}
                              onChange={(e) => setEmailForm({ ...emailForm, code: e.target.value })}
                              className="flex-1 h-12 bg-gray-50 dark:bg-gray-700/50 border-gray-200 dark:border-gray-600 focus:border-indigo-500 dark:focus:border-indigo-500 rounded-xl"
                            />
                            <Button
                              type="button"
                              variant="outline"
                              onClick={() => handleSendCode('email')}
                              disabled={countdown > 0}
                              className="h-12 px-6 rounded-xl border-gray-200 dark:border-gray-600 hover:border-indigo-500 dark:hover:border-indigo-500 transition-colors"
                            >
                              {countdown > 0 ? `${countdown}s` : '发送'}
                            </Button>
                          </div>
                        </div>

                        <Button
                          onClick={() => handleVerifyCode('email')}
                          className="w-full h-12 bg-gradient-to-r from-indigo-600 to-blue-600 hover:from-indigo-700 hover:to-blue-700 text-white rounded-xl shadow-lg shadow-indigo-500/30 hover:shadow-xl hover:shadow-indigo-500/40 transition-all duration-300"
                          disabled={loading}
                        >
                          {loading ? (
                            <span className="flex items-center gap-2">
                              <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
                              验证中...
                            </span>
                          ) : (
                            '下一步'
                          )}
                        </Button>
                      </div>
                    ) : (
                      <div className="space-y-5">
                        <div>
                          <label className="block text-sm mb-2 text-gray-700 dark:text-gray-300">
                            新密码
                          </label>
                          <div className="relative">
                            <Input
                              type={showPassword ? 'text' : 'password'}
                              placeholder="至少8位，包含字母和数字"
                              value={emailForm.password}
                              onChange={(e) => setEmailForm({ ...emailForm, password: e.target.value })}
                              className="h-12 bg-gray-50 dark:bg-gray-700/50 border-gray-200 dark:border-gray-600 focus:border-indigo-500 dark:focus:border-indigo-500 rounded-xl pr-12"
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
                            确认密码
                          </label>
                          <div className="relative">
                            <Input
                              type={showConfirmPassword ? 'text' : 'password'}
                              placeholder="请再次输入新密码"
                              value={emailForm.confirmPassword}
                              onChange={(e) => setEmailForm({ ...emailForm, confirmPassword: e.target.value })}
                              className="h-12 bg-gray-50 dark:bg-gray-700/50 border-gray-200 dark:border-gray-600 focus:border-indigo-500 dark:focus:border-indigo-500 rounded-xl pr-12"
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

                        <div className="flex gap-3 pt-2">
                          <Button
                            variant="outline"
                            onClick={() => setStep(1)}
                            className="flex-1 h-12 rounded-xl border-gray-200 dark:border-gray-600 hover:border-indigo-500 dark:hover:border-indigo-500 transition-colors"
                          >
                            上一步
                          </Button>
                          <Button
                            onClick={() => handleResetPassword('email')}
                            className="flex-1 h-12 bg-gradient-to-r from-indigo-600 to-blue-600 hover:from-indigo-700 hover:to-blue-700 text-white rounded-xl shadow-lg shadow-indigo-500/30 hover:shadow-xl hover:shadow-indigo-500/40 transition-all duration-300"
                            disabled={loading}
                          >
                            {loading ? (
                              <span className="flex items-center gap-2">
                                <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
                                提交中...
                              </span>
                            ) : (
                              '确认修改'
                            )}
                          </Button>
                        </div>
                      </div>
                    )}
                  </TabsContent>
                </Tabs>
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