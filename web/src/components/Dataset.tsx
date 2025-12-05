import { Database, Plus, MoreHorizontal, Eye, Trash2, Edit, FileText, Copy, Search, X, Filter, Grid3x3, List, Upload, File, Download } from 'lucide-react';
import { Button } from './ui/button';
import { Card } from './ui/card';
import { Badge } from './ui/badge';
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from './ui/dropdown-menu';
import { Input } from './ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './ui/select';
import { Pagination, PaginationContent, PaginationEllipsis, PaginationItem, PaginationLink, PaginationNext, PaginationPrevious } from './ui/pagination';
import { Progress } from './ui/progress';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription } from './ui/dialog';
import { Label } from './ui/label';
import { useState } from 'react';
import { toast } from 'sonner';
import { CreateDatasetDialog } from './CreateDatasetDialog';

interface DatasetItem {
  id: number;
  name: string;
  description: string;
  icon: string;
  iconBg: string;
  type: '文本' | '表格' | '图像' | '文档';
  dataCount: string;
  size: string;
  status: '已激活' | '已停用' | '正准备';
  statusColor: string;
  updateTime: string;
  createdTime: string;
  creator: string;
  tags?: string[];
}

interface DataFileItem {
  id: number;
  name: string;
  type: 'CSV' | 'JSON' | 'Excel' | 'Text';
  typeColor: string;
  typeIcon: string;
  size: string;
  status: '已处理' | '处理中' | '待处理';
  statusColor: string;
  uploadTime: string;
  recordCount: string;
}

export function Dataset() {
  const [searchQuery, setSearchQuery] = useState('');
  const [viewMode, setViewMode] = useState<'grid' | 'table'>('table');
  const [sortBy, setSort] = useState('default');
  const [createDialogOpen, setCreateDialogOpen] = useState(false);
  const [selectedDataset, setSelectedDataset] = useState<number | null>(null);
  const [viewDialogOpen, setViewDialogOpen] = useState(false);
  const [viewingDataset, setViewingDataset] = useState<DatasetItem | null>(null);
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 6;

  // 标签颜色映射
  const getTagColor = (tag: string): string => {
    const colors = [
      'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400',
      'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400',
      'bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-400',
      'bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-400',
      'bg-pink-100 text-pink-700 dark:bg-pink-900/30 dark:text-pink-400',
      'bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-400',
      'bg-cyan-100 text-cyan-700 dark:bg-cyan-900/30 dark:text-cyan-400',
      'bg-indigo-100 text-indigo-700 dark:bg-indigo-900/30 dark:text-indigo-400',
    ];
    
    // 根据标签内容生成一个稳定的索引
    const index = tag.split('').reduce((acc, char) => acc + char.charCodeAt(0), 0) % colors.length;
    return colors[index];
  };

  // 统计数据
  const stats = [
    {
      label: '数据集数量',
      value: '10',
      subtext: '较上月增加 2个',
      icon: Database,
      iconBg: 'bg-blue-500',
      trend: '+25%',
      trendUp: true,
    },
    {
      label: '数据总量',
      value: '45.8K',
      subtext: '记录：不为多条',
      icon: FileText,
      iconBg: 'bg-green-500',
      trend: '+39%',
      trendUp: true,
    },
    {
      label: '已启用',
      value: '18',
      subtext: '本周调用数 1024 次',
      icon: Eye,
      iconBg: 'bg-orange-500',
      trend: '+5%',
      trendUp: true,
    },
    {
      label: '存储空间',
      value: '1.8GB / 10GB',
      subtext: '已使用 18%',
      icon: Database,
      iconBg: 'bg-purple-500',
      progress: 18,
      showProgress: true,
      trend: '+150.5MB',
      trendUp: true,
    },
  ];

  // 数据集列表 - 扩展到10条
  const datasets: DatasetItem[] = [
    {
      id: 1,
      name: '客户对话数据集',
      description: '基于客服场景的真实对话数据',
      icon: '📚',
      iconBg: 'bg-blue-50 dark:bg-blue-900/20',
      type: '文本',
      dataCount: '24',
      size: '12.5K 条',
      status: '已激活',
      statusColor: 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400',
      updateTime: '2023-10-12',
      createdTime: '2023-09-01 10:30',
      creator: '张三',
      tags: ['客服', '对话'],
    },
    {
      id: 2,
      name: '技术问答集',
      description: 'IT技术类常见问题及答案',
      icon: '📗',
      iconBg: 'bg-green-50 dark:bg-green-900/20',
      type: '表格',
      dataCount: '8',
      size: '5.2K 条',
      status: '已激活',
      statusColor: 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400',
      updateTime: '2023-10-10',
      createdTime: '2023-08-15 14:20',
      creator: '李四',
      tags: ['技术', 'IT', 'Q&A'],
    },
    {
      id: 3,
      name: '产品评价数据集',
      description: '电商平台产品评价及推荐相关',
      icon: '📙',
      iconBg: 'bg-yellow-50 dark:bg-yellow-900/20',
      type: '文本',
      dataCount: '42',
      size: '18.7K 条',
      status: '正准备',
      statusColor: 'bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-400',
      updateTime: '2023-10-18',
      createdTime: '2023-10-01 09:15',
      creator: '王五',
      tags: ['电商', '评价', '推荐'],
    },
    {
      id: 4,
      name: '搜索文档',
      description: '产品相关文档及其索引库',
      icon: '📕',
      iconBg: 'bg-red-50 dark:bg-red-900/20',
      type: '文档',
      dataCount: '15',
      size: '3.8K 条',
      status: '已停用',
      statusColor: 'bg-gray-100 text-gray-700 dark:bg-gray-700 dark:text-gray-400',
      updateTime: '2023-10-20',
      createdTime: '2023-07-20 16:45',
      creator: '赵六',
      tags: ['搜索', '产品'],
    },
    {
      id: 5,
      name: '新闻文档',
      description: '行业相关新闻报道及分析',
      icon: '📘',
      iconBg: 'bg-blue-50 dark:bg-blue-900/20',
      type: '文本',
      dataCount: '67',
      size: '22.3K 条',
      status: '已激活',
      statusColor: 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400',
      updateTime: '2023-10-05',
      createdTime: '2023-08-10 11:00',
      creator: '孙七',
      tags: ['新闻', '行业分析'],
    },
    {
      id: 6,
      name: '法律案例',
      description: '法律相关案例及法律文档',
      icon: '📗',
      iconBg: 'bg-green-50 dark:bg-green-900/20',
      type: '文档',
      dataCount: '12',
      size: '6.4K 条',
      status: '已激活',
      statusColor: 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400',
      updateTime: '2023-10-12',
      createdTime: '2023-09-05 13:30',
      creator: '周八',
      tags: ['法律', '案例'],
    },
    {
      id: 7,
      name: '医疗知识库',
      description: '医疗健康相关知识和问答',
      icon: '💊',
      iconBg: 'bg-red-50 dark:bg-red-900/20',
      type: '文本',
      dataCount: '35',
      size: '15.2K 条',
      status: '已激活',
      statusColor: 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400',
      updateTime: '2023-10-08',
      createdTime: '2023-08-25 15:20',
      creator: '吴九',
      tags: ['医疗', '健康', '知识库'],
    },
    {
      id: 8,
      name: '金融数据集',
      description: '股票、基金等金融数据',
      icon: '💰',
      iconBg: 'bg-yellow-50 dark:bg-yellow-900/20',
      type: '表格',
      dataCount: '89',
      size: '28.9K 条',
      status: '已激活',
      statusColor: 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400',
      updateTime: '2023-10-15',
      createdTime: '2023-09-10 10:00',
      creator: '郑十',
      tags: ['金融', '股票', '基金'],
    },
    {
      id: 9,
      name: '教育资源',
      description: '在线教育课程和学习资料',
      icon: '🎓',
      iconBg: 'bg-purple-50 dark:bg-purple-900/20',
      type: '文档',
      dataCount: '56',
      size: '21.5K 条',
      status: '正准备',
      statusColor: 'bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-400',
      updateTime: '2023-10-19',
      createdTime: '2023-10-05 14:30',
      creator: '钱十一',
      tags: ['教育', '课程', '学习'],
    },
    {
      id: 10,
      name: '社交媒体数据',
      description: '社交平台用户行为和内容分析',
      icon: '📱',
      iconBg: 'bg-pink-50 dark:bg-pink-900/20',
      type: '文本',
      dataCount: '120',
      size: '35.6K 条',
      status: '已激活',
      statusColor: 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400',
      updateTime: '2023-10-21',
      createdTime: '2023-09-20 09:45',
      creator: '孙十二',
      tags: ['社交', '媒体', '用户行为'],
    },
  ];

  // 数据文件列表
  const dataFiles: DataFileItem[] = [
    {
      id: 1,
      name: '客户对话记录_2023Q3.csv',
      type: 'CSV',
      typeColor: 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400',
      typeIcon: '📊',
      size: '3.2 MB',
      status: '已处理',
      statusColor: 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400',
      uploadTime: '2023-10-10 14:30',
      recordCount: '5,240',
    },
    {
      id: 2,
      name: '客户反馈数据.json',
      type: 'JSON',
      typeColor: 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400',
      typeIcon: '📝',
      size: '1.8 MB',
      status: '处理中',
      statusColor: 'bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-400',
      uploadTime: '2023-10-11 09:15',
      recordCount: '3,180',
    },
    {
      id: 3,
      name: '服务质量评分.xlsx',
      type: 'Excel',
      typeColor: 'bg-emerald-100 text-emerald-700 dark:bg-emerald-900/30 dark:text-emerald-400',
      typeIcon: '📈',
      size: '2.5 MB',
      status: '已处理',
      statusColor: 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400',
      uploadTime: '2023-10-09 16:20',
      recordCount: '4,320',
    },
    {
      id: 4,
      name: '问题分类标签.txt',
      type: 'Text',
      typeColor: 'bg-gray-100 text-gray-700 dark:bg-gray-700 dark:text-gray-300',
      typeIcon: '📄',
      size: '0.5 MB',
      status: '待处理',
      statusColor: 'bg-gray-100 text-gray-700 dark:bg-gray-700 dark:text-gray-400',
      uploadTime: '2023-10-12 11:45',
      recordCount: '1,500',
    },
  ];

  const handleAction = (action: string, name: string) => {
    toast.success(`${action}: ${name}`);
  };

  const handleView = (dataset: DatasetItem) => {
    setViewingDataset(dataset);
    setViewDialogOpen(true);
  };

  const handleUpload = () => {
    toast.success('上传数据文件功能');
  };

  // 过滤数据集
  const filteredDatasets = datasets.filter((dataset) => {
    const searchLower = searchQuery.toLowerCase();
    return (
      dataset.name.toLowerCase().includes(searchLower) ||
      dataset.description.toLowerCase().includes(searchLower) ||
      dataset.type.toLowerCase().includes(searchLower)
    );
  });

  // 分页逻辑
  const totalPages = Math.ceil(filteredDatasets.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const endIndex = startIndex + itemsPerPage;
  const currentDatasets = filteredDatasets.slice(startIndex, endIndex);
  const shouldShowPagination = filteredDatasets.length > itemsPerPage;

  // 获取选中的数据集
  const selectedDS = datasets.find(ds => ds.id === selectedDataset);

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl mb-1 dark:text-white">数据集</h1>
        <p className="text-sm text-gray-600 dark:text-gray-400">
          数据集作为数据管理工具，用于AI模型应用和知识补充
        </p>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {stats.map((stat, index) => {
          const Icon = stat.icon;
          return (
            <Card key={index} className="px-5 pt-5 pb-3 dark:bg-gray-800 dark:border-gray-700">
              <div className="flex items-start justify-between mb-1.5">
                <div className={`${stat.iconBg} w-10 h-10 rounded-lg flex items-center justify-center`}>
                  <Icon className="w-5 h-5 text-white" />
                </div>
                {stat.trend && (
                  <span className={`text-sm ${stat.trendUp ? 'text-green-600 dark:text-green-400' : 'text-red-600 dark:text-red-400'}`}>
                    {stat.trend}
                  </span>
                )}
              </div>
              <div className="text-base font-semibold text-gray-600 dark:text-gray-400 mb-0.5">{stat.label}</div>
              <div className="text-3xl dark:text-white mb-0.5">{stat.value}</div>
              {stat.showProgress ? (
                <div className="flex items-center gap-2">
                  <Progress value={stat.progress} className="h-1.5 flex-1" />
                  <div className="text-xs text-gray-500 dark:text-gray-400 whitespace-nowrap">{stat.subtext}</div>
                </div>
              ) : (
                <div className="text-xs text-gray-500 dark:text-gray-400">{stat.subtext}</div>
              )}
            </Card>
          );
        })}
      </div>

      {/* Dataset List */}
      <div>
        {/* Action Buttons and Search - 与应用列表一致 */}
        <div className="flex items-center justify-between gap-3 mb-4">
          {/* Search Bar - 左侧390px */}
          <div className="relative w-[390px]">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400 dark:text-gray-500" />
            <Input
              type="text"
              placeholder="搜索数据集名称、描述或类型..."
              value={searchQuery}
              onChange={(e) => {
                setSearchQuery(e.target.value);
                setCurrentPage(1); // 重置到第一页
              }}
              className="pl-10 pr-10 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-100 focus-visible:border-blue-500 focus-visible:ring-blue-500/50"
            />
            {searchQuery && (
              <button
                onClick={() => {
                  setSearchQuery('');
                  setCurrentPage(1);
                }}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors"
              >
                <X className="w-4 h-4" />
              </button>
            )}
          </div>

          {/* Action Buttons - 右侧 */}
          <div className="flex items-center gap-3">
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button variant="outline" size="sm" className="dark:bg-gray-800 dark:border-gray-700 dark:text-gray-300">
                  <Filter className="w-4 h-4 mr-2" />
                  筛选
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end" className="dark:bg-gray-800 dark:border-gray-700">
                <DropdownMenuItem className="dark:text-gray-300">默认排序</DropdownMenuItem>
                <DropdownMenuItem className="dark:text-gray-300">按名称排序</DropdownMenuItem>
                <DropdownMenuItem className="dark:text-gray-300">按时间排序</DropdownMenuItem>
                <DropdownMenuItem className="dark:text-gray-300">按大小排序</DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>

            <div className="flex items-center gap-1 border border-gray-200 dark:border-gray-700 rounded-lg p-1">
              <button
                onClick={() => setViewMode('grid')}
                className={`p-1.5 rounded ${
                  viewMode === 'grid'
                    ? 'bg-blue-50 text-blue-600 dark:bg-blue-900/30 dark:text-blue-400'
                    : 'text-gray-600 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-700'
                }`}
              >
                <Grid3x3 className="w-4 h-4" />
              </button>
              <button
                onClick={() => setViewMode('table')}
                className={`p-1.5 rounded ${
                  viewMode === 'table'
                    ? 'bg-blue-50 text-blue-600 dark:bg-blue-900/30 dark:text-blue-400'
                    : 'text-gray-600 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-700'
                }`}
              >
                <List className="w-4 h-4" />
              </button>
            </div>

            <Button 
              size="sm" 
              className="bg-blue-500 hover:bg-blue-600 text-white"
              onClick={() => setCreateDialogOpen(true)}
            >
              <Plus className="w-4 h-4 mr-2" />
              创建数据集
            </Button>
          </div>
        </div>

        {/* Empty State */}
        {filteredDatasets.length === 0 && (
          <div className="text-center py-12">
            <Database className="w-12 h-12 text-gray-400 dark:text-gray-600 mx-auto mb-3" />
            <p className="text-gray-600 dark:text-gray-400">未找到匹配的数据集</p>
            <p className="text-sm text-gray-500 dark:text-gray-500 mt-1">
              {searchQuery ? '尝试使用其他搜索词' : '暂无数据集'}
            </p>
          </div>
        )}

        {/* Table View */}
        {viewMode === 'table' && filteredDatasets.length > 0 && (
          <Card className="dark:bg-gray-800 dark:border-gray-700">
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="bg-gray-50 dark:bg-gray-900">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs text-gray-600 dark:text-gray-400">数据集名称</th>
                    <th className="px-6 py-3 text-left text-xs text-gray-600 dark:text-gray-400">类型</th>
                    <th className="px-6 py-3 text-left text-xs text-gray-600 dark:text-gray-400">文档数</th>
                    <th className="px-6 py-3 text-left text-xs text-gray-600 dark:text-gray-400">状态</th>
                    <th className="px-6 py-3 text-left text-xs text-gray-600 dark:text-gray-400">更新时间</th>
                    <th className="px-6 py-3 text-left text-xs text-gray-600 dark:text-gray-400">操作</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-200 dark:divide-gray-700">
                  {currentDatasets.map((dataset) => (
                    <tr 
                      key={dataset.id} 
                      className={`hover:bg-gray-50 dark:hover:bg-gray-900 cursor-pointer ${
                        selectedDataset === dataset.id ? 'bg-blue-50 dark:bg-blue-900/20' : ''
                      }`}
                      onClick={() => setSelectedDataset(dataset.id)}
                    >
                      <td className="px-6 py-4">
                        <div className="flex items-center gap-3">
                          <div className={`${dataset.iconBg} w-10 h-10 rounded-lg flex items-center justify-center text-xl`}>
                            {dataset.icon}
                          </div>
                          <div>
                            <div className="flex items-center gap-2 mb-1">
                              <span className="text-sm dark:text-white">{dataset.name}</span>
                              {dataset.tags && dataset.tags.length > 0 && (
                                <div className="flex gap-1">
                                  {dataset.tags.slice(0, 3).map((tag, index) => (
                                    <Badge
                                      key={index}
                                      className={`text-xs px-1.5 py-0 border-0 ${getTagColor(tag)}`}
                                    >
                                      {tag}
                                    </Badge>
                                  ))}
                                  {dataset.tags.length > 3 && (
                                    <Badge
                                      variant="secondary"
                                      className="text-xs px-1.5 py-0 bg-gray-200 text-gray-700 dark:bg-gray-700 dark:text-gray-300 border-0"
                                    >
                                      +{dataset.tags.length - 3}
                                    </Badge>
                                  )}
                                </div>
                              )}
                            </div>
                            <div className="text-xs text-gray-500 dark:text-gray-400">
                              {dataset.description}
                            </div>
                          </div>
                        </div>
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-600 dark:text-gray-400">{dataset.type}</td>
                      <td className="px-6 py-4 text-sm text-gray-600 dark:text-gray-400">{dataset.dataCount}</td>
                      <td className="px-6 py-4">
                        <Badge className={`text-xs ${dataset.statusColor} border-0`}>
                          {dataset.status}
                        </Badge>
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-600 dark:text-gray-400">{dataset.updateTime}</td>
                      <td className="px-6 py-4">
                        <div className="flex items-center gap-2" onClick={(e) => e.stopPropagation()}>
                          <button 
                            onClick={() => handleView(dataset)}
                            className="p-1 hover:bg-gray-100 dark:hover:bg-gray-700 rounded"
                          >
                            <Eye className="w-4 h-4 text-blue-500" />
                          </button>
                          <button 
                            onClick={() => handleAction('编辑', dataset.name)}
                            className="p-1 hover:bg-gray-100 dark:hover:bg-gray-700 rounded"
                          >
                            <Edit className="w-4 h-4 text-gray-600 dark:text-gray-400" />
                          </button>
                          <button 
                            onClick={() => handleAction('删除', dataset.name)}
                            className="p-1 hover:bg-gray-100 dark:hover:bg-gray-700 rounded"
                          >
                            <Trash2 className="w-4 h-4 text-red-500" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {/* Pagination - 只在数据超过6条时显示 */}
            {shouldShowPagination && (
              <div className="flex items-center justify-center px-6 py-4 border-t border-gray-200 dark:border-gray-700">
                <Pagination>
                  <PaginationContent>
                    <PaginationItem>
                      <PaginationPrevious 
                        onClick={() => setCurrentPage(prev => Math.max(1, prev - 1))}
                        className={currentPage === 1 ? 'pointer-events-none opacity-50' : 'cursor-pointer'}
                      >
                        上一页
                      </PaginationPrevious>
                    </PaginationItem>
                    {Array.from({ length: totalPages }, (_, i) => i + 1).map((page) => (
                      <PaginationItem key={page}>
                        <PaginationLink
                          onClick={() => setCurrentPage(page)}
                          isActive={currentPage === page}
                          className="cursor-pointer"
                        >
                          {page}
                        </PaginationLink>
                      </PaginationItem>
                    ))}
                    <PaginationItem>
                      <PaginationNext 
                        onClick={() => setCurrentPage(prev => Math.min(totalPages, prev + 1))}
                        className={currentPage === totalPages ? 'pointer-events-none opacity-50' : 'cursor-pointer'}
                      >
                        下一页
                      </PaginationNext>
                    </PaginationItem>
                  </PaginationContent>
                </Pagination>
              </div>
            )}
          </Card>
        )}

        {/* Grid View */}
        {viewMode === 'grid' && filteredDatasets.length > 0 && (
          <>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {currentDatasets.map((dataset) => (
                <Card 
                  key={dataset.id} 
                  className={`p-4 dark:bg-gray-800 dark:border-gray-700 hover:shadow-lg transition-shadow cursor-pointer ${
                    selectedDataset === dataset.id ? 'ring-2 ring-blue-500 dark:ring-blue-400' : ''
                  }`}
                  onClick={() => setSelectedDataset(dataset.id)}
                >
                  <div className="flex items-start justify-between gap-3 mb-3">
                    <div className="flex items-center gap-3 flex-1 min-w-0">
                      <div className={`${dataset.iconBg} w-10 h-10 rounded-lg flex items-center justify-center text-xl shrink-0`}>
                        {dataset.icon}
                      </div>
                      <h3 className="dark:text-white truncate flex-1">{dataset.name}</h3>
                    </div>
                    <DropdownMenu>
                      <DropdownMenuTrigger asChild onClick={(e) => e.stopPropagation()}>
                        <button className="p-1.5 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors shrink-0">
                          <MoreHorizontal className="w-4 h-4 text-gray-600 dark:text-gray-400" />
                        </button>
                      </DropdownMenuTrigger>
                      <DropdownMenuContent align="end" className="dark:bg-gray-800 dark:border-gray-700">
                        <DropdownMenuItem onClick={() => handleView(dataset)} className="dark:text-gray-300">
                          <Eye className="w-4 h-4 mr-2" />
                          查看
                        </DropdownMenuItem>
                        <DropdownMenuItem onClick={() => handleAction('编辑', dataset.name)} className="dark:text-gray-300">
                          <Edit className="w-4 h-4 mr-2" />
                          编辑
                        </DropdownMenuItem>
                        <DropdownMenuItem onClick={() => handleAction('复制', dataset.name)} className="dark:text-gray-300">
                          <Copy className="w-4 h-4 mr-2" />
                          复制
                        </DropdownMenuItem>
                        <DropdownMenuItem onClick={() => handleAction('删除', dataset.name)} className="text-red-600 dark:text-red-400">
                          <Trash2 className="w-4 h-4 mr-2" />
                          删除
                        </DropdownMenuItem>
                      </DropdownMenuContent>
                    </DropdownMenu>
                  </div>

                  <p className="text-sm text-gray-600 dark:text-gray-400 mb-3 line-clamp-2">
                    {dataset.description}
                  </p>

                  {dataset.tags && dataset.tags.length > 0 && (
                    <div className="flex flex-wrap gap-1 mb-3">
                      {dataset.tags.slice(0, 3).map((tag, index) => (
                        <Badge
                          key={index}
                          className={`text-xs px-1.5 py-0 border-0 ${getTagColor(tag)}`}
                        >
                          {tag}
                        </Badge>
                      ))}
                      {dataset.tags.length > 3 && (
                        <Badge
                          variant="secondary"
                          className="text-xs px-1.5 py-0 bg-gray-200 text-gray-700 dark:bg-gray-700 dark:text-gray-300 border-0"
                        >
                          +{dataset.tags.length - 3}
                        </Badge>
                      )}
                    </div>
                  )}

                  <div className="flex items-center gap-2 mb-2">
                    <Badge className={`text-xs ${dataset.statusColor} border-0`}>
                      {dataset.status}
                    </Badge>
                    <span className="text-xs text-gray-500 dark:text-gray-400">{dataset.type}</span>
                  </div>

                  <div className="flex items-center justify-between text-xs text-gray-500 dark:text-gray-400">
                    <span>{dataset.dataCount} 文档</span>
                    <span>{dataset.size}</span>
                  </div>
                </Card>
              ))}
            </div>

            {/* Grid View Pagination */}
            {shouldShowPagination && (
              <div className="flex items-center justify-center mt-4">
                <Pagination>
                  <PaginationContent>
                    <PaginationItem>
                      <PaginationPrevious 
                        onClick={() => setCurrentPage(prev => Math.max(1, prev - 1))}
                        className={currentPage === 1 ? 'pointer-events-none opacity-50' : 'cursor-pointer'}
                      >
                        上一页
                      </PaginationPrevious>
                    </PaginationItem>
                    {Array.from({ length: totalPages }, (_, i) => i + 1).map((page) => (
                      <PaginationItem key={page}>
                        <PaginationLink
                          onClick={() => setCurrentPage(page)}
                          isActive={currentPage === page}
                          className="cursor-pointer"
                        >
                          {page}
                        </PaginationLink>
                      </PaginationItem>
                    ))}
                    <PaginationItem>
                      <PaginationNext 
                        onClick={() => setCurrentPage(prev => Math.min(totalPages, prev + 1))}
                        className={currentPage === totalPages ? 'pointer-events-none opacity-50' : 'cursor-pointer'}
                      >
                        下一页
                      </PaginationNext>
                    </PaginationItem>
                  </PaginationContent>
                </Pagination>
              </div>
            )}
          </>
        )}
      </div>

      {/* Data Files Section - 选中数据集后显示 */}
      {selectedDS && (
        <div>
          <div className="mb-4">
            <h2 className="text-xl dark:text-white mb-1">数据文件管理</h2>
            <p className="text-sm text-gray-600 dark:text-gray-400">
              {selectedDS.name} - 上传和管理数据文件
            </p>
          </div>

          {/* Upload Area */}
          <Card className="p-6 mb-4 dark:bg-gray-800 dark:border-gray-700">
            <div className="border-2 border-dashed border-gray-300 dark:border-gray-600 rounded-lg p-8 text-center hover:border-blue-500 dark:hover:border-blue-400 transition-colors cursor-pointer">
              <Upload className="w-12 h-12 text-gray-400 dark:text-gray-500 mx-auto mb-4" />
              <Button 
                className="bg-blue-500 hover:bg-blue-600 text-white mb-4"
                onClick={handleUpload}
              >
                <Upload className="w-4 h-4 mr-2" />
                选择文件
              </Button>
              <p className="text-sm text-gray-600 dark:text-gray-400">
                拖拽文件到此处或点击选择文件 · 支持 CSV, JSON, Excel, TXT 格式，最大 100MB
              </p>
            </div>
          </Card>

          {/* Uploaded Files List */}
          <div className="mb-4">
            <h3 className="text-lg dark:text-white mb-3">已上传文件</h3>
          </div>

          <Card className="dark:bg-gray-800 dark:border-gray-700">
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="bg-gray-50 dark:bg-gray-900">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs text-gray-600 dark:text-gray-400">文件名称</th>
                    <th className="px-6 py-3 text-left text-xs text-gray-600 dark:text-gray-400">类型</th>
                    <th className="px-6 py-3 text-left text-xs text-gray-600 dark:text-gray-400">大小</th>
                    <th className="px-6 py-3 text-left text-xs text-gray-600 dark:text-gray-400">记录数</th>
                    <th className="px-6 py-3 text-left text-xs text-gray-600 dark:text-gray-400">状态</th>
                    <th className="px-6 py-3 text-left text-xs text-gray-600 dark:text-gray-400">上传时间</th>
                    <th className="px-6 py-3 text-left text-xs text-gray-600 dark:text-gray-400">操作</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-200 dark:divide-gray-700">
                  {dataFiles.map((file) => (
                    <tr key={file.id} className="hover:bg-gray-50 dark:hover:bg-gray-900">
                      <td className="px-6 py-4">
                        <div className="flex items-center gap-3">
                          <span className="text-xl">{file.typeIcon}</span>
                          <span className="text-sm dark:text-white">{file.name}</span>
                        </div>
                      </td>
                      <td className="px-6 py-4">
                        <Badge className={`text-xs ${file.typeColor} border-0`}>
                          {file.type}
                        </Badge>
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-600 dark:text-gray-400">{file.size}</td>
                      <td className="px-6 py-4 text-sm text-gray-600 dark:text-gray-400">{file.recordCount}</td>
                      <td className="px-6 py-4">
                        <Badge className={`text-xs ${file.statusColor} border-0`}>
                          {file.status}
                        </Badge>
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-600 dark:text-gray-400">{file.uploadTime}</td>
                      <td className="px-6 py-4">
                        <div className="flex items-center gap-2">
                          <button 
                            onClick={() => handleAction('下载', file.name)}
                            className="p-1 hover:bg-gray-100 dark:hover:bg-gray-700 rounded"
                          >
                            <Download className="w-4 h-4 text-blue-500" />
                          </button>
                          <button 
                            onClick={() => handleAction('删除', file.name)}
                            className="p-1 hover:bg-gray-100 dark:hover:bg-gray-700 rounded"
                          >
                            <Trash2 className="w-4 h-4 text-red-500" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </Card>
        </div>
      )}

      {/* View Dataset Dialog */}
      <Dialog open={viewDialogOpen} onOpenChange={setViewDialogOpen}>
        <DialogContent className="max-w-2xl dark:bg-gray-900 dark:border-gray-700">
          <DialogHeader>
            <DialogTitle className="text-xl dark:text-white">数据集详情</DialogTitle>
            <DialogDescription className="text-sm text-gray-500 dark:text-gray-400">
              查看数据集的详细信息
            </DialogDescription>
          </DialogHeader>

          {viewingDataset && (
            <div className="space-y-4 py-4">
              <div className="flex items-center gap-4 pb-4 border-b border-gray-200 dark:border-gray-700">
                <div className={`${viewingDataset.iconBg} w-16 h-16 rounded-lg flex items-center justify-center text-3xl`}>
                  {viewingDataset.icon}
                </div>
                <div className="flex-1">
                  <h3 className="text-lg dark:text-white mb-1">{viewingDataset.name}</h3>
                  <p className="text-sm text-gray-600 dark:text-gray-400">{viewingDataset.description}</p>
                </div>
                <Badge className={`text-xs ${viewingDataset.statusColor} border-0`}>
                  {viewingDataset.status}
                </Badge>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <Label className="text-sm text-gray-600 dark:text-gray-400">数据类型</Label>
                  <p className="text-sm dark:text-white mt-1">{viewingDataset.type}</p>
                </div>
                <div>
                  <Label className="text-sm text-gray-600 dark:text-gray-400">文档数量</Label>
                  <p className="text-sm dark:text-white mt-1">{viewingDataset.dataCount} 个文档</p>
                </div>
                <div>
                  <Label className="text-sm text-gray-600 dark:text-gray-400">数据量</Label>
                  <p className="text-sm dark:text-white mt-1">{viewingDataset.size}</p>
                </div>
                <div>
                  <Label className="text-sm text-gray-600 dark:text-gray-400">创建者</Label>
                  <p className="text-sm dark:text-white mt-1">{viewingDataset.creator}</p>
                </div>
                <div>
                  <Label className="text-sm text-gray-600 dark:text-gray-400">创建时间</Label>
                  <p className="text-sm dark:text-white mt-1">{viewingDataset.createdTime}</p>
                </div>
                <div>
                  <Label className="text-sm text-gray-600 dark:text-gray-400">最后更新</Label>
                  <p className="text-sm dark:text-white mt-1">{viewingDataset.updateTime}</p>
                </div>
              </div>

              {viewingDataset.tags && viewingDataset.tags.length > 0 && (
                <div>
                  <Label className="text-sm text-gray-600 dark:text-gray-400 mb-2 block">标签</Label>
                  <div className="flex flex-wrap gap-2">
                    {viewingDataset.tags.map((tag, index) => (
                      <Badge
                        key={index}
                        className={`text-xs px-2 py-1 border-0 ${getTagColor(tag)}`}
                      >
                        {tag}
                      </Badge>
                    ))}
                  </div>
                </div>
              )}
            </div>
          )}

          <div className="flex justify-end gap-3 pt-4 border-t border-gray-200 dark:border-gray-700">
            <Button 
              variant="outline" 
              onClick={() => setViewDialogOpen(false)}
              className="dark:bg-gray-800 dark:border-gray-700 dark:text-gray-300"
            >
              关闭
            </Button>
            <Button 
              className="bg-blue-500 hover:bg-blue-600 text-white"
              onClick={() => {
                setViewDialogOpen(false);
                viewingDataset && handleAction('编辑', viewingDataset.name);
              }}
            >
              编辑数据集
            </Button>
          </div>
        </DialogContent>
      </Dialog>

      {/* Create Dataset Dialog */}
      <CreateDatasetDialog 
        open={createDialogOpen} 
        onOpenChange={setCreateDialogOpen}
      />
    </div>
  );
}
