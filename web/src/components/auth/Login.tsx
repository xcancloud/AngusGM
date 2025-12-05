import { useState } from 'react';
import { Eye, EyeOff, Github, Shield, Zap, Globe, Sparkles, Languages, User } from 'lucide-react';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Card } from '../ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '../ui/tabs';
import { Separator } from '../ui/separator';
import { toast } from 'sonner';
import { AngusGMLogo } from '../AngusGMLogo';
import { useLanguage } from './LanguageContext';

interface LoginProps {
  onLoginSuccess?: () => void;
  onNavigateToRegister?: () => void;
  onNavigateToForgotPassword?: () => void;
}

// 模拟多账号数据
interface Account {
  id: string;
  name: string;
  avatar?: string;
  lastLogin?: string;
}

export function Login({ onLoginSuccess, onNavigateToRegister, onNavigateToForgotPassword }: LoginProps) {
  const { language, setLanguage, t } = useLanguage();
  const [loginType, setLoginType] = useState<'account' | 'phone' | 'email'>('account');
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [showAccountSelector, setShowAccountSelector] = useState(false);
  const [availableAccounts, setAvailableAccounts] = useState<Account[]>([]);

  // 账号登录表单
  const [accountForm, setAccountForm] = useState({
    account: '',
    password: '',
    remember: false,
  });

  // 短信登录表单
  const [phoneForm, setPhoneForm] = useState({
    phone: '',
    code: '',
  });

  // 邮箱登录表单
  const [emailForm, setEmailForm] = useState({
    email: '',
    code: '',
  });

  const [countdown, setCountdown] = useState(0);

  // 发送验证码
  const handleSendCode = (type: 'phone' | 'email') => {
    const value = type === 'phone' ? phoneForm.phone : emailForm.email;
    if (!value) {
      toast.error(type === 'phone' ? '请输入手机号' : '请输入邮箱地址');
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

  // 账号登录
  const handleAccountLogin = (e: React.FormEvent) => {
    e.preventDefault();
    if (!accountForm.account || !accountForm.password) {
      toast.error('请输入账号和密码');
      return;
    }

    setLoading(true);
    setTimeout(() => {
      setLoading(false);
      toast.success('登录成功！');
      onLoginSuccess?.();
    }, 1000);
  };

  // 短信登录
  const handlePhoneLogin = (e: React.FormEvent) => {
    e.preventDefault();
    if (!phoneForm.phone || !phoneForm.code) {
      toast.error('请输入手机号和验证码');
      return;
    }

    setLoading(true);
    // 模拟检测多账号
    setTimeout(() => {
      setLoading(false);
      
      // 模拟：该手机号绑定了多个账号
      const mockAccounts: Account[] = [
        { id: '1', name: '主账号 - 张三', lastLogin: '2024-12-04 10:30' },
        { id: '2', name: '子账号 - 张三（销售部）', lastLogin: '2024-12-03 15:20' },
        { id: '3', name: '子账号 - 张三（技术部）', lastLogin: '2024-12-02 09:15' },
      ];
      
      // 如果有多个账号，显示选择界面
      if (mockAccounts.length > 1) {
        setAvailableAccounts(mockAccounts);
        setShowAccountSelector(true);
      } else {
        toast.success('登录成功！');
        onLoginSuccess?.();
      }
    }, 1000);
  };

  // 邮箱登录
  const handleEmailLogin = (e: React.FormEvent) => {
    e.preventDefault();
    if (!emailForm.email || !emailForm.code) {
      toast.error('请输入邮箱和验证码');
      return;
    }

    setLoading(true);
    // 模拟检测多账号
    setTimeout(() => {
      setLoading(false);
      
      // 模拟：该邮箱绑定了多个账号
      const mockAccounts: Account[] = [
        { id: '1', name: '主账号 - 李四', lastLogin: '2024-12-04 14:30' },
        { id: '2', name: '子账号 - 李四（运营部）', lastLogin: '2024-12-03 11:20' },
      ];
      
      // 如果有多个账号，显示选择界面
      if (mockAccounts.length > 1) {
        setAvailableAccounts(mockAccounts);
        setShowAccountSelector(true);
      } else {
        toast.success('登录成功！');
        onLoginSuccess?.();
      }
    }, 1000);
  };

  // 选择账号登录
  const handleSelectAccount = (accountId: string) => {
    const selectedAccount = availableAccounts.find(acc => acc.id === accountId);
    if (selectedAccount) {
      toast.success(`已选择：${selectedAccount.name}`);
      setShowAccountSelector(false);
      onLoginSuccess?.();
    }
  };

  // 三方登录
  const handleThirdPartyLogin = (platform: string) => {
    toast.success(`正在跳转到${platform}授权页面...`);
  };

  return (
    <div className="w-full min-h-screen bg-gradient-to-br from-blue-50 via-indigo-50 to-purple-50 dark:from-gray-950 dark:via-blue-950 dark:to-gray-950 relative overflow-hidden">
      {/* 背景装饰 */}
      <div className="absolute inset-0 overflow-hidden pointer-events-none">
        <div className="absolute -top-40 -right-40 w-80 h-80 bg-blue-400/20 rounded-full blur-3xl animate-pulse"></div>
        <div className="absolute top-1/2 -left-40 w-80 h-80 bg-purple-400/20 rounded-full blur-3xl animate-pulse" style={{ animationDelay: '1s' }}></div>
        <div className="absolute -bottom-40 right-1/3 w-80 h-80 bg-indigo-400/20 rounded-full blur-3xl animate-pulse" style={{ animationDelay: '2s' }}></div>
      </div>

      {/* 语言切换按钮 */}
      <div className="absolute top-6 right-6 z-20">
        <button
          onClick={() => setLanguage(language === 'zh' ? 'en' : 'zh')}
          className="flex items-center gap-2 px-4 py-2 rounded-lg bg-white/80 dark:bg-gray-800/80 backdrop-blur-sm border border-gray-200/50 dark:border-gray-700/50 hover:border-blue-300 dark:hover:border-blue-600 transition-all duration-300 shadow-lg"
        >
          <Languages className="w-4 h-4 text-gray-600 dark:text-gray-400" />
          <span className="text-sm text-gray-700 dark:text-gray-300">
            {language === 'zh' ? 'EN' : '中文'}
          </span>
        </button>
      </div>

      <div className="relative z-10 min-h-screen flex">
        {/* 左侧品牌区 */}
        <div className="lg:flex lg:w-1/2 flex-col justify-between p-8 xl:p-16 2xl:p-20">
          {/* Logo和标题 */}
          <div>
            <div className="mb-12 xl:mb-16">
              <AngusGMLogo className="h-12 mb-6" />
              <h1 className="text-4xl xl:text-5xl 2xl:text-6xl mb-6 bg-gradient-to-r from-blue-600 via-indigo-600 to-purple-600 dark:from-blue-400 dark:via-indigo-400 dark:to-purple-400 bg-clip-text text-transparent leading-tight">
                {t('login.title')}
              </h1>
              <p className="text-lg xl:text-xl text-gray-600 dark:text-gray-400">
                {t('login.subtitle')}
              </p>
            </div>

            {/* 特性卡片 */}
            <div className="grid gap-4">
              <div className="group flex items-start gap-4 p-4 rounded-2xl bg-white/50 dark:bg-gray-800/50 backdrop-blur-sm border border-gray-200/50 dark:border-gray-700/50 hover:border-blue-300 dark:hover:border-blue-600 transition-all duration-300 hover:shadow-lg hover:shadow-blue-500/10">
                <div className="w-12 h-12 rounded-xl bg-gradient-to-br from-blue-500 to-blue-600 flex items-center justify-center flex-shrink-0 group-hover:scale-110 transition-transform duration-300">
                  <Shield className="w-6 h-6 text-white" />
                </div>
                <div>
                  <h3 className="text-base mb-1 text-gray-900 dark:text-white">{t('login.feature1Title')}</h3>
                  <p className="text-sm text-gray-600 dark:text-gray-400">{t('login.feature1Desc')}</p>
                </div>
              </div>

              <div className="group flex items-start gap-4 p-4 rounded-2xl bg-white/50 dark:bg-gray-800/50 backdrop-blur-sm border border-gray-200/50 dark:border-gray-700/50 hover:border-purple-300 dark:hover:border-purple-600 transition-all duration-300 hover:shadow-lg hover:shadow-purple-500/10">
                <div className="w-12 h-12 rounded-xl bg-gradient-to-br from-purple-500 to-purple-600 flex items-center justify-center flex-shrink-0 group-hover:scale-110 transition-transform duration-300">
                  <Zap className="w-6 h-6 text-white" />
                </div>
                <div>
                  <h3 className="text-base mb-1 text-gray-900 dark:text-white">{t('login.feature2Title')}</h3>
                  <p className="text-sm text-gray-600 dark:text-gray-400">{t('login.feature2Desc')}</p>
                </div>
              </div>

              <div className="group flex items-start gap-4 p-4 rounded-2xl bg-white/50 dark:bg-gray-800/50 backdrop-blur-sm border border-gray-200/50 dark:border-gray-700/50 hover:border-indigo-300 dark:hover:border-indigo-600 transition-all duration-300 hover:shadow-lg hover:shadow-indigo-500/10">
                <div className="w-12 h-12 rounded-xl bg-gradient-to-br from-indigo-500 to-indigo-600 flex items-center justify-center flex-shrink-0 group-hover:scale-110 transition-transform duration-300">
                  <Globe className="w-6 h-6 text-white" />
                </div>
                <div>
                  <h3 className="text-base mb-1 text-gray-900 dark:text-white">{t('login.feature3Title')}</h3>
                  <p className="text-sm text-gray-600 dark:text-gray-400">{t('login.feature3Desc')}</p>
                </div>
              </div>
            </div>
          </div>

          {/* 底部信息 */}
          <div className="mt-auto pt-8">
            <p className="text-sm text-gray-500 dark:text-gray-500">
              {t('common.copyright')}
            </p>
          </div>
        </div>

        {/* 右侧登录表单区 */}
        <div className="w-full lg:w-1/2 flex items-center justify-center p-6 lg:p-12 xl:p-16">
          <div className="w-full max-w-lg">
            {/* 移动端Logo */}
            <div className="hidden mb-8">
              <AngusGMLogo className="h-10 mb-4" />
              <h2 className="text-2xl mb-2 bg-gradient-to-r from-blue-600 to-indigo-600 dark:from-blue-400 dark:to-indigo-400 bg-clip-text text-transparent">
                {t('login.title')}
              </h2>
            </div>

            <Card className="border-0 shadow-2xl shadow-blue-500/10 dark:shadow-blue-500/5 bg-white/80 dark:bg-gray-800/80 backdrop-blur-xl">
              <div className="p-6 sm:p-8 lg:p-10">
                {/* 标题 */}
                <div className="text-center mb-8">
                  <h2 className="text-2xl mb-2 text-gray-900 dark:text-white">{t('login.cardTitle')}</h2>
                  <p className="text-sm text-gray-600 dark:text-gray-400">{t('login.cardSubtitle')}</p>
                </div>

                {/* 登录方式标签 */}
                <Tabs value={loginType} onValueChange={(v) => setLoginType(v as any)} className="mb-6">
                  <TabsList className="grid w-full grid-cols-3 bg-gray-100 dark:bg-gray-700/50 p-1 rounded-xl">
                    <TabsTrigger value="account" className="rounded-lg data-[state=active]:bg-white dark:data-[state=active]:bg-gray-600 data-[state=active]:shadow-sm">
                      {t('login.tabAccount')}
                    </TabsTrigger>
                    <TabsTrigger value="phone" className="rounded-lg data-[state=active]:bg-white dark:data-[state=active]:bg-gray-600 data-[state=active]:shadow-sm">
                      {t('login.tabPhone')}
                    </TabsTrigger>
                    <TabsTrigger value="email" className="rounded-lg data-[state=active]:bg-white dark:data-[state=active]:bg-gray-600 data-[state=active]:shadow-sm">
                      {t('login.tabEmail')}
                    </TabsTrigger>
                  </TabsList>

                  {/* 账号登录 */}
                  <TabsContent value="account" className="mt-6">
                    <form onSubmit={handleAccountLogin} className="space-y-5">
                      <div>
                        <label className="block text-sm mb-2 text-gray-700 dark:text-gray-300">
                          {t('login.account')}
                        </label>
                        <Input
                          type="text"
                          placeholder={t('login.accountPlaceholder')}
                          value={accountForm.account}
                          onChange={(e) => setAccountForm({ ...accountForm, account: e.target.value })}
                          className="h-12 bg-gray-50 dark:bg-gray-700/50 border-gray-200 dark:border-gray-600 focus:border-blue-500 dark:focus:border-blue-500 rounded-xl"
                        />
                      </div>

                      <div>
                        <label className="block text-sm mb-2 text-gray-700 dark:text-gray-300">
                          {t('login.password')}
                        </label>
                        <div className="relative">
                          <Input
                            type={showPassword ? 'text' : 'password'}
                            placeholder={t('login.passwordPlaceholder')}
                            value={accountForm.password}
                            onChange={(e) => setAccountForm({ ...accountForm, password: e.target.value })}
                            className="h-12 bg-gray-50 dark:bg-gray-700/50 border-gray-200 dark:border-gray-600 focus:border-blue-500 dark:focus:border-blue-500 rounded-xl pr-12"
                          />
                          <button
                            type="button"
                            onClick={() => setShowPassword(!showPassword)}
                            className="absolute right-4 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors"
                          >
                            {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                          </button>
                        </div>
                      </div>

                      <div className="flex items-center justify-between">
                        <label className="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400 cursor-pointer group">
                          <input
                            type="checkbox"
                            checked={accountForm.remember}
                            onChange={(e) => setAccountForm({ ...accountForm, remember: e.target.checked })}
                            className="w-4 h-4 rounded border-gray-300 text-blue-600 focus:ring-blue-500 focus:ring-offset-0 transition-colors"
                          />
                          <span className="group-hover:text-gray-900 dark:group-hover:text-gray-200 transition-colors">{t('login.remember')}</span>
                        </label>
                        <button
                          type="button"
                          onClick={onNavigateToForgotPassword}
                          className="text-sm text-blue-600 hover:text-blue-700 dark:text-blue-400 dark:hover:text-blue-300 transition-colors"
                        >
                          {t('login.forgot')}
                        </button>
                      </div>

                      <Button
                        type="submit"
                        className="w-full h-12 bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 text-white rounded-xl shadow-lg shadow-blue-500/30 hover:shadow-xl hover:shadow-blue-500/40 transition-all duration-300"
                        disabled={loading}
                      >
                        {loading ? (
                          <span className="flex items-center gap-2">
                            <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
                            {t('login.loading')}
                          </span>
                        ) : (
                          t('login.button')
                        )}
                      </Button>
                    </form>
                  </TabsContent>

                  {/* 短信登录 */}
                  <TabsContent value="phone" className="mt-6">
                    <form onSubmit={handlePhoneLogin} className="space-y-5">
                      <div>
                        <label className="block text-sm mb-2 text-gray-700 dark:text-gray-300">
                          {t('login.phone')}
                        </label>
                        <Input
                          type="tel"
                          placeholder={t('login.phonePlaceholder')}
                          value={phoneForm.phone}
                          onChange={(e) => setPhoneForm({ ...phoneForm, phone: e.target.value })}
                          className="h-12 bg-gray-50 dark:bg-gray-700/50 border-gray-200 dark:border-gray-600 focus:border-blue-500 dark:focus:border-blue-500 rounded-xl"
                        />
                      </div>

                      <div>
                        <label className="block text-sm mb-2 text-gray-700 dark:text-gray-300">
                          {t('login.code')}
                        </label>
                        <div className="flex gap-3">
                          <Input
                            type="text"
                            placeholder={t('login.codePlaceholder')}
                            value={phoneForm.code}
                            onChange={(e) => setPhoneForm({ ...phoneForm, code: e.target.value })}
                            className="flex-1 h-12 bg-gray-50 dark:bg-gray-700/50 border-gray-200 dark:border-gray-600 focus:border-blue-500 dark:focus:border-blue-500 rounded-xl"
                          />
                          <Button
                            type="button"
                            variant="outline"
                            onClick={() => handleSendCode('phone')}
                            disabled={countdown > 0}
                            className="h-12 px-6 rounded-xl border-gray-200 dark:border-gray-600 hover:border-blue-500 dark:hover:border-blue-500 transition-colors"
                          >
                            {countdown > 0 ? `${countdown}s` : t('login.sendCode')}
                          </Button>
                        </div>
                      </div>

                      <Button
                        type="submit"
                        className="w-full h-12 bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 text-white rounded-xl shadow-lg shadow-blue-500/30 hover:shadow-xl hover:shadow-blue-500/40 transition-all duration-300"
                        disabled={loading}
                      >
                        {loading ? (
                          <span className="flex items-center gap-2">
                            <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
                            {t('login.loading')}
                          </span>
                        ) : (
                          t('login.button')
                        )}
                      </Button>
                      
                      {/* 找回密码链接 */}
                      <div className="text-center">
                        <button
                          type="button"
                          onClick={onNavigateToForgotPassword}
                          className="text-sm text-blue-600 hover:text-blue-700 dark:text-blue-400 dark:hover:text-blue-300 transition-colors"
                        >
                          {t('login.forgot')}
                        </button>
                      </div>
                    </form>
                  </TabsContent>

                  {/* 邮箱登录 */}
                  <TabsContent value="email" className="mt-6">
                    <form onSubmit={handleEmailLogin} className="space-y-5">
                      <div>
                        <label className="block text-sm mb-2 text-gray-700 dark:text-gray-300">
                          {t('login.email')}
                        </label>
                        <Input
                          type="email"
                          placeholder={t('login.emailPlaceholder')}
                          value={emailForm.email}
                          onChange={(e) => setEmailForm({ ...emailForm, email: e.target.value })}
                          className="h-12 bg-gray-50 dark:bg-gray-700/50 border-gray-200 dark:border-gray-600 focus:border-blue-500 dark:focus:border-blue-500 rounded-xl"
                        />
                      </div>

                      <div>
                        <label className="block text-sm mb-2 text-gray-700 dark:text-gray-300">
                          {t('login.code')}
                        </label>
                        <div className="flex gap-3">
                          <Input
                            type="text"
                            placeholder={t('login.codePlaceholder')}
                            value={emailForm.code}
                            onChange={(e) => setEmailForm({ ...emailForm, code: e.target.value })}
                            className="flex-1 h-12 bg-gray-50 dark:bg-gray-700/50 border-gray-200 dark:border-gray-600 focus:border-blue-500 dark:focus:border-blue-500 rounded-xl"
                          />
                          <Button
                            type="button"
                            variant="outline"
                            onClick={() => handleSendCode('email')}
                            disabled={countdown > 0}
                            className="h-12 px-6 rounded-xl border-gray-200 dark:border-gray-600 hover:border-blue-500 dark:hover:border-blue-500 transition-colors"
                          >
                            {countdown > 0 ? `${countdown}s` : t('login.sendCode')}
                          </Button>
                        </div>
                      </div>

                      <Button
                        type="submit"
                        className="w-full h-12 bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 text-white rounded-xl shadow-lg shadow-blue-500/30 hover:shadow-xl hover:shadow-blue-500/40 transition-all duration-300"
                        disabled={loading}
                      >
                        {loading ? (
                          <span className="flex items-center gap-2">
                            <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
                            {t('login.loading')}
                          </span>
                        ) : (
                          t('login.button')
                        )}
                      </Button>
                      
                      {/* 找回密码链接 */}
                      <div className="text-center">
                        <button
                          type="button"
                          onClick={onNavigateToForgotPassword}
                          className="text-sm text-blue-600 hover:text-blue-700 dark:text-blue-400 dark:hover:text-blue-300 transition-colors"
                        >
                          {t('login.forgot')}
                        </button>
                      </div>
                    </form>
                  </TabsContent>
                </Tabs>

                {/* 分割线 */}
                <div className="relative my-8">
                  <Separator className="bg-gray-200 dark:bg-gray-700" />
                  <span className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-white dark:bg-gray-800 px-4 text-xs text-gray-500 dark:text-gray-400">
                    {t('common.or')}
                  </span>
                </div>

                {/* 第三方登录 */}
                <div className="grid grid-cols-3 gap-3 mb-6">
                  <button
                    onClick={() => handleThirdPartyLogin(t('common.wechat'))}
                    className="h-12 flex items-center justify-center rounded-xl border-2 border-gray-200 dark:border-gray-600 hover:border-green-500 dark:hover:border-green-500 hover:bg-green-50 dark:hover:bg-green-950/20 transition-all duration-300 group"
                  >
                    <svg className="w-6 h-6 text-gray-600 dark:text-gray-400 group-hover:text-green-600 dark:group-hover:text-green-500 transition-colors" viewBox="0 0 24 24" fill="currentColor">
                      <path d="M18.348 8.595c.367 0 .724.033 1.073.082-1.128-5.259-6.876-8.677-12.012-6.866C3.065 3.281.315 7.69 1.164 12.624c.193 1.124.542 2.151 1.002 3.064l-1.04 3.113 3.241-1.646c1.589.795 3.398 1.253 5.324 1.253 6.627 0 11.99-5.373 11.99-12s-5.363-11.813-11.99-11.813c-.367 0-.724.016-1.073.048 5.635.382 10.07 5.042 10.07 10.765 0 .367-.017.724-.049 1.073.367-.016.734-.049 1.11-.049 5.635 0 10.07 4.435 10.07 10.07s-4.435 10.07-10.07 10.07z"/>
                    </svg>
                  </button>
                  <button
                    onClick={() => handleThirdPartyLogin(t('common.github'))}
                    className="h-12 flex items-center justify-center rounded-xl border-2 border-gray-200 dark:border-gray-600 hover:border-gray-800 dark:hover:border-gray-400 hover:bg-gray-50 dark:hover:bg-gray-700/20 transition-all duration-300 group"
                  >
                    <Github className="w-6 h-6 text-gray-600 dark:text-gray-400 group-hover:text-gray-800 dark:group-hover:text-gray-300 transition-colors" />
                  </button>
                  <button
                    onClick={() => handleThirdPartyLogin(t('common.google'))}
                    className="h-12 flex items-center justify-center rounded-xl border-2 border-gray-200 dark:border-gray-600 hover:border-blue-500 dark:hover:border-blue-500 hover:bg-blue-50 dark:hover:bg-blue-950/20 transition-all duration-300 group"
                  >
                    <svg className="w-6 h-6" viewBox="0 0 24 24">
                      <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" fill="#4285F4"/>
                      <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" fill="#34A853"/>
                      <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z" fill="#FBBC05"/>
                      <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" fill="#EA4335"/>
                    </svg>
                  </button>
                </div>

                {/* 注册链接 */}
                <div className="text-center">
                  <span className="text-sm text-gray-600 dark:text-gray-400">{t('login.noAccount')}</span>
                  <button
                    onClick={onNavigateToRegister}
                    className="text-sm text-blue-600 hover:text-blue-700 dark:text-blue-400 dark:hover:text-blue-300 ml-2 font-medium transition-colors"
                  >
                    {t('login.register')}
                  </button>
                </div>
              </div>
            </Card>

            {/* 移动端底部信息 */}
            <div className="lg:hidden text-center mt-8">
              <p className="text-sm text-gray-500 dark:text-gray-500">
                {t('common.copyright')}
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* 多账号选择模态框 */}
      {showAccountSelector && (
        <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center p-6 z-50 animate-in fade-in duration-200">
          <Card className="w-full max-w-md border-0 shadow-2xl bg-white/95 dark:bg-gray-800/95 backdrop-blur-xl">
            <div className="p-8">
              {/* 标题 */}
              <div className="text-center mb-6">
                <div className="inline-flex items-center justify-center w-14 h-14 rounded-2xl bg-gradient-to-br from-blue-500 to-indigo-600 mb-4">
                  <User className="w-7 h-7 text-white" />
                </div>
                <h3 className="text-xl mb-2 text-gray-900 dark:text-white">选择登录账号</h3>
                <p className="text-sm text-gray-600 dark:text-gray-400">
                  检测到该{loginType === 'phone' ? '手机号' : '邮箱'}绑定了多个账号，请选择要登录的账号
                </p>
              </div>

              {/* 账号列表 */}
              <div className="space-y-3 mb-6">
                {availableAccounts.map((account) => (
                  <button
                    key={account.id}
                    onClick={() => handleSelectAccount(account.id)}
                    className="w-full p-4 rounded-xl border-2 border-gray-200 dark:border-gray-700 hover:border-blue-500 dark:hover:border-blue-500 hover:bg-blue-50 dark:hover:bg-blue-950/20 transition-all duration-300 text-left group"
                  >
                    <div className="flex items-center gap-4">
                      <div className="w-12 h-12 rounded-full bg-gradient-to-br from-blue-500 to-indigo-600 flex items-center justify-center text-white flex-shrink-0">
                        {account.name.charAt(account.name.indexOf('-') + 2)}
                      </div>
                      <div className="flex-1 min-w-0">
                        <div className="flex items-center gap-2 mb-1">
                          <p className="text-gray-900 dark:text-white group-hover:text-blue-600 dark:group-hover:text-blue-400 transition-colors truncate">
                            {account.name}
                          </p>
                          {account.id === '1' && (
                            <span className="px-2 py-0.5 rounded text-xs bg-gradient-to-r from-blue-500 to-indigo-600 text-white flex-shrink-0">
                              主账号
                            </span>
                          )}
                        </div>
                        {account.lastLogin && (
                          <p className="text-xs text-gray-500 dark:text-gray-400">
                            上次登录：{account.lastLogin}
                          </p>
                        )}
                      </div>
                      <div className="flex-shrink-0">
                        <svg className="w-5 h-5 text-gray-400 group-hover:text-blue-600 dark:group-hover:text-blue-400 transition-colors" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                        </svg>
                      </div>
                    </div>
                  </button>
                ))}
              </div>

              {/* 取消按钮 */}
              <Button
                variant="outline"
                onClick={() => setShowAccountSelector(false)}
                className="w-full h-12 rounded-xl border-gray-200 dark:border-gray-600"
              >
                取消
              </Button>
            </div>
          </Card>
        </div>
      )}
    </div>
  );
}