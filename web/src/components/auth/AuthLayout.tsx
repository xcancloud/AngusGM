import { Outlet } from 'react-router-dom';
import { ThemeProvider } from '@/components/ThemeProvider';
import { Toaster } from '@/components/ui/sonner';
import { LanguageProvider } from './LanguageContext';

export function AuthLayout() {
  return (
    <ThemeProvider>
      <LanguageProvider>
        <div className="flex h-screen bg-gray-50 dark:bg-gray-900">
          <Toaster richColors position="top-right" />
          <Outlet />
        </div>
      </LanguageProvider>
    </ThemeProvider>
  );
}

