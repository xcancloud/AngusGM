import { useLocation, useNavigate } from 'react-router-dom';
import { AppMenuManagement } from './AppMenuManagement';

interface Application {
  id: string;
  code: string;
  name: string;
  displayName: string;
  type: '基础应用' | '业务应用' | '系统应用';
  version: string;
  versionType: '云服务版' | '本地版' | '混合版';
  isDefault: boolean;
  shopStatus: string;
  sortOrder: number;
  status: '启用' | '禁用';
  search: string;
  url: string;
  groupId?: string;
  tenantName?: string;
  creator: string;
  createTime: string;
  description: string;
  isInstalled: boolean;
}

export function AppMenuManagementWrapper() {
  const location = useLocation();
  const navigate = useNavigate();
  const app = location.state?.app as Application | undefined;

  if (!app) {
    // 如果没有 app 数据，重定向到应用管理列表
    navigate('/app-management', { replace: true });
    return null;
  }

  const handleBack = () => {
    navigate('/app-management');
  };

  return <AppMenuManagement app={app} onBack={handleBack} />;
}

