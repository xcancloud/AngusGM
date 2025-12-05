import { createContext, useContext, useState, ReactNode } from 'react';

type Language = 'zh' | 'en';

interface LanguageContextType {
  language: Language;
  setLanguage: (lang: Language) => void;
  t: (key: string) => string;
}

const translations = {
  zh: {
    // 公共
    'common.or': '或使用第三方登录',
    'common.wechat': '微信',
    'common.github': 'Github',
    'common.google': 'Google',
    'common.copyright': '© 2024 AngusGM. All rights reserved.',
    
    // 登录页
    'login.title': '欢迎回来',
    'login.subtitle': '登录 AngusGM 全局管理平台，开启高效管理之旅',
    'login.cardTitle': '账号登录',
    'login.cardSubtitle': '选择您喜欢的登录方式',
    'login.tabAccount': '账号',
    'login.tabPhone': '短信',
    'login.tabEmail': '邮箱',
    'login.account': '账号',
    'login.accountPlaceholder': '手机号 / 邮箱 / 用户名',
    'login.password': '密码',
    'login.passwordPlaceholder': '请输入密码',
    'login.remember': '记住我',
    'login.forgot': '忘记密码？',
    'login.button': '登录',
    'login.loading': '登录中...',
    'login.phone': '手机号',
    'login.phonePlaceholder': '请输入手机号',
    'login.email': '邮箱地址',
    'login.emailPlaceholder': '请输入邮箱地址',
    'login.code': '验证码',
    'login.codePlaceholder': '请输入验证码',
    'login.sendCode': '发送',
    'login.noAccount': '还没有账号？',
    'login.register': '立即注册',
    'login.feature1Title': '企业级安全',
    'login.feature1Desc': '多重身份验证，保障数据安全',
    'login.feature2Title': '高效便捷',
    'login.feature2Desc': '一站式管理，提升工作效率',
    'login.feature3Title': '全球部署',
    'login.feature3Desc': '支持多地域，稳定可靠',
    
    // 注册页
    'register.title': '开启管理新篇章',
    'register.subtitle': '注册 AngusGM 账号，体验专业的企业级管理平台',
    'register.cardTitle': '创建账号',
    'register.cardSubtitle': '选择您的注册方式',
    'register.tabPhone': '手机注册',
    'register.tabEmail': '邮箱注册',
    'register.phone': '手机号',
    'register.phonePlaceholder': '请输入手机号',
    'register.email': '邮箱地址',
    'register.emailPlaceholder': '请输入邮箱地址',
    'register.code': '验证码',
    'register.codePlaceholder': '请输入验证码',
    'register.sendCode': '发送',
    'register.password': '密码',
    'register.passwordPlaceholder': '至少8位，包含字母和数字',
    'register.confirmPassword': '确认密码',
    'register.confirmPasswordPlaceholder': '请再次输入密码',
    'register.inviteCode': '邀请码（可选）',
    'register.inviteCodePlaceholder': '如有邀请码请输入',
    'register.agree': '我已阅读并同意',
    'register.userAgreement': '《用户协议》',
    'register.privacyPolicy': '《隐私政策》',
    'register.and': '和',
    'register.button': '立即注册',
    'register.loading': '注册中...',
    'register.hasAccount': '已有账号？',
    'register.login': '立即登录',
    'register.required': '*',
    'register.feature1Title': '快速部署',
    'register.feature1Desc': '5分钟即可完成注册并开始使用',
    'register.feature2Title': '安全保障',
    'register.feature2Desc': '企业级加密，数据安全无忧',
    'register.feature3Title': '专业支持',
    'register.feature3Desc': '7×24小时技术支持团队',
    'register.strengthWeak': '弱',
    'register.strengthMedium': '中',
    'register.strengthStrong': '强',
    'register.strengthVeryStrong': '很强',
    'register.strengthLabel': '密码强度',
    
    // 找回密码
    'forgot.title': '找回密码',
    'forgot.subtitle': '通过验证快速重置您的账号密码',
    'forgot.cardTitle1': '验证身份',
    'forgot.cardTitle2': '设置新密码',
    'forgot.cardSubtitle1': '请选择验证方式',
    'forgot.cardSubtitle2': '请设置您的新密码',
    'forgot.back': '返回登录',
    'forgot.tabPhone': '手机找回',
    'forgot.tabEmail': '邮箱找回',
    'forgot.next': '下一步',
    'forgot.prev': '上一步',
    'forgot.submit': '确认修改',
    'forgot.feature1Title': '安全验证',
    'forgot.feature1Desc': '多重身份验证，保障账号安全',
    'forgot.feature2Title': '快速重置',
    'forgot.feature2Desc': '两步即可完成密码重置',
    'forgot.feature3Title': '24小时服务',
    'forgot.feature3Desc': '随时为您提供找回服务',
  },
  en: {
    // Common
    'common.or': 'Or sign in with',
    'common.wechat': 'WeChat',
    'common.github': 'Github',
    'common.google': 'Google',
    'common.copyright': '© 2024 AngusGM. All rights reserved.',
    
    // Login
    'login.title': 'Welcome Back',
    'login.subtitle': 'Sign in to AngusGM Global Management Platform',
    'login.cardTitle': 'Sign In',
    'login.cardSubtitle': 'Choose your preferred sign in method',
    'login.tabAccount': 'Account',
    'login.tabPhone': 'SMS',
    'login.tabEmail': 'Email',
    'login.account': 'Account',
    'login.accountPlaceholder': 'Phone / Email / Username',
    'login.password': 'Password',
    'login.passwordPlaceholder': 'Enter your password',
    'login.remember': 'Remember me',
    'login.forgot': 'Forgot password?',
    'login.button': 'Sign In',
    'login.loading': 'Signing in...',
    'login.phone': 'Phone Number',
    'login.phonePlaceholder': 'Enter phone number',
    'login.email': 'Email Address',
    'login.emailPlaceholder': 'Enter email address',
    'login.code': 'Code',
    'login.codePlaceholder': 'Enter verification code',
    'login.sendCode': 'Send',
    'login.noAccount': "Don't have an account?",
    'login.register': 'Sign up now',
    'login.feature1Title': 'Enterprise Security',
    'login.feature1Desc': 'Multi-factor authentication for data security',
    'login.feature2Title': 'Efficient & Easy',
    'login.feature2Desc': 'One-stop management solution',
    'login.feature3Title': 'Global Deployment',
    'login.feature3Desc': 'Multi-region support, stable & reliable',
    
    // Register
    'register.title': 'Start Your Journey',
    'register.subtitle': 'Create your AngusGM account for professional management',
    'register.cardTitle': 'Create Account',
    'register.cardSubtitle': 'Choose your registration method',
    'register.tabPhone': 'Phone',
    'register.tabEmail': 'Email',
    'register.phone': 'Phone Number',
    'register.phonePlaceholder': 'Enter phone number',
    'register.email': 'Email Address',
    'register.emailPlaceholder': 'Enter email address',
    'register.code': 'Verification Code',
    'register.codePlaceholder': 'Enter verification code',
    'register.sendCode': 'Send',
    'register.password': 'Password',
    'register.passwordPlaceholder': 'At least 8 characters',
    'register.confirmPassword': 'Confirm Password',
    'register.confirmPasswordPlaceholder': 'Re-enter password',
    'register.inviteCode': 'Invite Code (Optional)',
    'register.inviteCodePlaceholder': 'Enter invite code if you have',
    'register.agree': 'I agree to the',
    'register.userAgreement': 'User Agreement',
    'register.privacyPolicy': 'Privacy Policy',
    'register.and': 'and',
    'register.button': 'Sign Up',
    'register.loading': 'Signing up...',
    'register.hasAccount': 'Already have an account?',
    'register.login': 'Sign in now',
    'register.required': '*',
    'register.feature1Title': 'Quick Setup',
    'register.feature1Desc': 'Get started in just 5 minutes',
    'register.feature2Title': 'Secure',
    'register.feature2Desc': 'Enterprise-grade encryption',
    'register.feature3Title': 'Support',
    'register.feature3Desc': '24/7 technical support team',
    'register.strengthWeak': 'Weak',
    'register.strengthMedium': 'Medium',
    'register.strengthStrong': 'Strong',
    'register.strengthVeryStrong': 'Very Strong',
    'register.strengthLabel': 'Password Strength',
    
    // Forgot Password
    'forgot.title': 'Reset Password',
    'forgot.subtitle': 'Reset your password securely with verification',
    'forgot.cardTitle1': 'Verify Identity',
    'forgot.cardTitle2': 'Set New Password',
    'forgot.cardSubtitle1': 'Choose verification method',
    'forgot.cardSubtitle2': 'Set your new password',
    'forgot.back': 'Back to login',
    'forgot.tabPhone': 'Phone',
    'forgot.tabEmail': 'Email',
    'forgot.next': 'Next',
    'forgot.prev': 'Previous',
    'forgot.submit': 'Confirm',
    'forgot.feature1Title': 'Secure Verification',
    'forgot.feature1Desc': 'Multi-factor identity verification',
    'forgot.feature2Title': 'Quick Reset',
    'forgot.feature2Desc': 'Reset in just two steps',
    'forgot.feature3Title': '24/7 Service',
    'forgot.feature3Desc': 'Always available for recovery',
  },
};

const LanguageContext = createContext<LanguageContextType | undefined>(undefined);

export function LanguageProvider({ children }: { children?: ReactNode }) {
  const [language, setLanguage] = useState<Language>('zh');

  const t = (key: string): string => {
    return translations[language][key] || key;
  };

  return (
    <LanguageContext.Provider value={{ language, setLanguage, t }}>
      {children}
    </LanguageContext.Provider>
  );
}

export function useLanguage() {
  const context = useContext(LanguageContext);
  if (!context) {
    throw new Error('useLanguage must be used within LanguageProvider');
  }
  return context;
}
