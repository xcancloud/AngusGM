import { Construction, ArrowLeft } from 'lucide-react';
import { Button } from '@/components/ui/button';

interface UnderDevelopmentProps {
  title: string;
  description?: string;
}

export function UnderDevelopment({ title, description }: UnderDevelopmentProps) {
  return (
    <div className="flex items-center justify-center min-h-[60vh]">
      <div className="text-center max-w-md">
        <div className="inline-flex items-center justify-center w-20 h-20 bg-green-100 dark:bg-green-900/20 rounded-full mb-6">
          <Construction className="w-10 h-10 text-green-600 dark:text-green-400" />
        </div>
        <h2 className="text-2xl dark:text-white mb-3">{title}</h2>
        <p className="text-gray-600 dark:text-gray-400 mb-6">
          {description || '此功能正在开发中，敬请期待...'}
        </p>
      </div>
    </div>
  );
}