import { useState } from 'react';
import { Login } from './Login';
import { Register } from './Register';
import { ForgotPassword } from './ForgotPassword';
import { UserAgreement } from './UserAgreement';
import { PrivacyPolicy } from './PrivacyPolicy';
import { LanguageProvider } from './LanguageContext';

interface AuthContainerProps {
  onAuthSuccess?: () => void;
}

export function AuthContainer({ onAuthSuccess }: AuthContainerProps) {
  const [currentPage, setCurrentPage] = useState<'login' | 'register' | 'forgot' | 'agreement' | 'privacy'>('privacy');

  const handleAuthSuccess = () => {
    // 登录/注册成功后的处理
    onAuthSuccess?.();
  };

  return (
    <LanguageProvider>
      {currentPage === 'login' && (
        <Login
          onLoginSuccess={handleAuthSuccess}
          onNavigateToRegister={() => setCurrentPage('register')}
          onNavigateToForgotPassword={() => setCurrentPage('forgot')}
        />
      )}
      {currentPage === 'register' && (
        <Register
          onRegisterSuccess={handleAuthSuccess}
          onNavigateToLogin={() => setCurrentPage('login')}
          onNavigateToAgreement={() => setCurrentPage('agreement')}
          onNavigateToPrivacy={() => setCurrentPage('privacy')}
        />
      )}
      {currentPage === 'forgot' && (
        <ForgotPassword
          onResetSuccess={() => setCurrentPage('login')}
          onNavigateToLogin={() => setCurrentPage('login')}
        />
      )}
      {currentPage === 'agreement' && (
        <UserAgreement
          onBack={() => setCurrentPage('register')}
        />
      )}
      {currentPage === 'privacy' && (
        <PrivacyPolicy
          onBack={() => setCurrentPage('register')}
        />
      )}
    </LanguageProvider>
  );
}