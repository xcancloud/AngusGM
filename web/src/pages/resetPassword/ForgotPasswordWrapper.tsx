import { useNavigate } from 'react-router-dom';
import { ForgotPassword } from './ForgotPassword';

export function ForgotPasswordWrapper() {
  const navigate = useNavigate();

  const handleResetSuccess = () => {
    navigate('/login', { replace: true });
  };

  const handleNavigateToLogin = () => {
    navigate('/login', { replace: true });
  };

  return (
    <ForgotPassword
      onResetSuccess={handleResetSuccess}
      onNavigateToLogin={handleNavigateToLogin}
    />
  );
}

