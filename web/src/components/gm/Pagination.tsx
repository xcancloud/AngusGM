import { ChevronLeft, ChevronRight } from 'lucide-react';
import { Button } from '@/components/ui/button';

interface PaginationProps {
  currentPage: number;
  totalPages: number;
  onPageChange: (page: number) => void;
  totalItems: number;
  pageSize: number;
}

export function Pagination({ currentPage, totalPages, onPageChange, totalItems, pageSize }: PaginationProps) {
  const startItem = (currentPage - 1) * pageSize + 1;
  const endItem = Math.min(currentPage * pageSize, totalItems);

  return (
    <div className="flex items-center justify-between px-6 py-4 border-t dark:border-gray-700">
      <div className="text-sm text-gray-600 dark:text-gray-400">
        显示 {startItem}-{endItem} 条，共 {totalItems} 条
      </div>
      <div className="flex items-center gap-2">
        <Button
          variant="outline"
          size="sm"
          onClick={() => onPageChange(currentPage - 1)}
          disabled={currentPage === 1}
          className="dark:bg-gray-900 dark:border-gray-700 dark:text-gray-300 disabled:opacity-50"
        >
          <ChevronLeft className="w-4 h-4" />
        </Button>
        <div className="text-sm text-gray-600 dark:text-gray-400">
          第 {currentPage} 页，共 {totalPages} 页
        </div>
        <Button
          variant="outline"
          size="sm"
          onClick={() => onPageChange(currentPage + 1)}
          disabled={currentPage === totalPages}
          className="dark:bg-gray-900 dark:border-gray-700 dark:text-gray-300 disabled:opacity-50"
        >
          <ChevronRight className="w-4 h-4" />
        </Button>
      </div>
    </div>
  );
}
