import { useNavigate } from 'react-router-dom';
import { Register } from './Register';

export function RegisterWrapper() {
  const navigate = useNavigate();

  const handleRegisterSuccess = () => {
    navigate('/dashboard', { replace: true });
  };

  const handleNavigateToLogin = () => {
    navigate('/login', { replace: true });
  };

  const handleNavigateToAgreement = () => {
    // 如果需要单独的路由，可以导航到 /agreement
    // 目前保持为空，因为 Agreement 可能在 Register 组件内部处理
  };

  const handleNavigateToPrivacy = () => {
    // 如果需要单独的路由，可以导航到 /privacy
    // 目前保持为空，因为 Privacy 可能在 Register 组件内部处理
  };

  return (
    <Register
      onRegisterSuccess={handleRegisterSuccess}
      onNavigateToLogin={handleNavigateToLogin}
      onNavigateToAgreement={handleNavigateToAgreement}
      onNavigateToPrivacy={handleNavigateToPrivacy}
    />
  );
}

