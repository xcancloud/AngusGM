import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { ThemeProvider } from '@/components/ThemeProvider';
import { Toaster } from '@/components/ui/sonner';
import { SidebarGM } from '@/components/SidebarGM';
import { HeaderGM } from '@/components/HeaderGM';

function Layout() {
    const navigate = useNavigate();
    const location = useLocation();
    
    // 从路径中提取当前页面，处理嵌套路由
    const getActivePage = () => {
        const path = location.pathname;
        if (path === '/' || path === '/dashboard') return 'dashboard';
        // 移除前导斜杠并获取第一段路径
        const segments = path.split('/').filter(Boolean);
        return segments[0] || 'dashboard';
    };
    
    const activePage = getActivePage();
    
    const onPageChange = (page) => {
        navigate(`/${page}`);
    };
    
    const handleOpenSettings = (tab) => {
        // TODO: 实现设置页面路由
        navigate(`/settings${tab ? `?tab=${tab}` : ''}`);
    };
    
    const handleNavigate = (page) => {
        navigate(`/${page}`);
    };
    
    const handleLogout = () => {
        navigate('/login', { replace: true });
    };
    
    return (
        <ThemeProvider>
            <div className="flex h-screen bg-gray-50 dark:bg-gray-900">
                <Toaster richColors position="top-right" />
                <SidebarGM activePage={activePage} onPageChange={onPageChange} />
                
                <div className="flex-1 flex flex-col overflow-hidden">
                    <HeaderGM 
                        onOpenSettings={handleOpenSettings}
                        onNavigate={handleNavigate}
                        onLogout={handleLogout}
                    />
                    
                    <main className="flex-1 overflow-y-auto hide-scrollbar">
                        <div className="px-7 py-6 space-y-6">
                            <Outlet />
                        </div>
                    </main>
                </div>
            </div>
        </ThemeProvider>
    );
}

export { Layout };