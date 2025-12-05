import { useNavigate } from 'react-router-dom';
import { Login } from './Login';

export function LoginWrapper() {
  const navigate = useNavigate();

  const handleLoginSuccess = () => {
    navigate('/dashboard', { replace: true });
  };

  const handleNavigateToRegister = () => {
    navigate('/register', { replace: true });
  };

  const handleNavigateToForgotPassword = () => {
    navigate('/forgot-password', { replace: true });
  };

  return (
    <Login
      onLoginSuccess={handleLoginSuccess}
      onNavigateToRegister={handleNavigateToRegister}
      onNavigateToForgotPassword={handleNavigateToForgotPassword}
    />
  );
}

