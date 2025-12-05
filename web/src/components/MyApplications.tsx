import { Plus, Filter, Grid3x3, List, MoreVertical, Star, Clock, MessageSquare, FileText, Database, Code, Zap, Globe, Bot, Sparkles, Search, X } from 'lucide-react';
import { Button } from './ui/button';
import { Badge } from './ui/badge';
import { Tabs, TabsList, TabsTrigger } from './ui/tabs';
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from './ui/dropdown-menu';
import { Input } from './ui/input';
import { useState } from 'react';
import { toast } from 'sonner';

type ViewMode = 'grid' | 'list';
type AppStatus = '进行中' | '已暂停' | '已完成' | '草稿';

interface Tag {
  label: string;
  color: string;
}

interface Application {
  id: number;
  name: string;
  description: string;
  icon: any;
  iconBgColor: string;
  status: AppStatus;
  isStarred: boolean;
  tags: Tag[];
  visits: string;
  category: string;
}

export function MyApplications({ onCreateNew }: { onCreateNew?: () => void }) {
  const [viewMode, setViewMode] = useState<ViewMode>('grid');
  const [activeTab, setActiveTab] = useState('all');
  const [searchQuery, setSearchQuery] = useState('');

  const applications: Application[] = [
    {
      id: 1,
      name: '智能助手',
      description: '智能客服对话系统输入，支持多轮对话和知识库问答',
      icon: MessageSquare,
      iconBgColor: 'bg-purple-500',
      status: '进行中',
      isStarred: false,
      tags: [
        { label: '智能助手', color: 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400' },
        { label: '对话式', color: 'bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-400' }
      ],
      visits: '1.2K 次调用',
      category: 'chatbot',
    },
    {
      id: 7,
      name: '营销文案助手',
      description: '',
      icon: FileText,
      iconBgColor: 'bg-gray-400',
      status: '草稿',
      isStarred: false,
      tags: [],
      visits: '',
      category: 'text-generation',
    },
    {
      id: 2,
      name: '内容生成器',
      description: 'AI 内容创作工具，自动生成文章、广告文案和社交媒体内容',
      icon: Sparkles,
      iconBgColor: 'bg-purple-600',
      status: '进行中',
      isStarred: false,
      tags: [
        { label: '文本生成', color: 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400' }
      ],
      visits: '856 次调用',
      category: 'text-generation',
    },
    {
      id: 3,
      name: '产品知识库',
      description: '基于产品文档的智能问答系统，支持多语言和文档管理',
      icon: Database,
      iconBgColor: 'bg-green-500',
      status: '进行中',
      isStarred: false,
      tags: [
        { label: '知识问答', color: 'bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-400' },
        { label: '数据分析', color: 'bg-cyan-100 text-cyan-700 dark:bg-cyan-900/30 dark:text-cyan-400' }
      ],
      visits: '暂无数',
      category: 'knowledge',
    },
    {
      id: 4,
      name: '智能客服代理',
      description: '多渠道客服管理系统，支持智能路由和情绪分析',
      icon: Bot,
      iconBgColor: 'bg-orange-500',
      status: '星标',
      isStarred: true,
      tags: [
        { label: '智能助手', color: 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400' },
        { label: 'Agent', color: 'bg-pink-100 text-pink-700 dark:bg-pink-900/30 dark:text-pink-400' }
      ],
      visits: '2.3K 次调用',
      category: 'chatbot',
    },
    {
      id: 5,
      name: '多语言翻译器',
      description: '支持50+语言的智能翻译服务，保留上下文语义和风格',
      icon: Globe,
      iconBgColor: 'bg-pink-500',
      status: '进行中',
      isStarred: false,
      tags: [
        { label: '文本生成', color: 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400' },
        { label: '多语言', color: 'bg-indigo-100 text-indigo-700 dark:bg-indigo-900/30 dark:text-indigo-400' }
      ],
      visits: '3.5K 次调用',
      category: 'text-generation',
    },
    {
      id: 6,
      name: '代码助手',
      description: 'AI辅助编写、支持代码生成、调试和文档生成',
      icon: Code,
      iconBgColor: 'bg-indigo-500',
      status: '已暂停',
      isStarred: false,
      tags: [
        { label: '代码生成', color: 'bg-violet-100 text-violet-700 dark:bg-violet-900/30 dark:text-violet-400' },
        { label: 'Gpt 开发工具', color: 'bg-teal-100 text-teal-700 dark:bg-teal-900/30 dark:text-teal-400' }
      ],
      visits: '',
      category: 'other',
    },
  ];

  const filteredApps = applications.filter((app) => {
    // 搜索过滤
    const matchesSearch = searchQuery === '' || 
      app.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      app.description.toLowerCase().includes(searchQuery.toLowerCase()) ||
      app.tags.some(tag => tag.label.toLowerCase().includes(searchQuery.toLowerCase()));
    
    if (!matchesSearch) return false;
    
    // 分类过滤
    if (activeTab === 'all') return true;
    if (activeTab === 'in-progress') return app.status === '进行中';
    if (activeTab === 'paused') return app.status === '已暂停';
    if (activeTab === 'draft') return app.status === '草稿';
    if (activeTab === 'starred') return app.isStarred;
    if (activeTab === 'chatbot') return app.category === 'chatbot';
    if (activeTab === 'text-generation') return app.category === 'text-generation';
    if (activeTab === 'knowledge') return app.category === 'knowledge';
    return true;
  });

  const getCategoryCount = (category: string) => {
    if (category === 'all') return applications.length;
    if (category === 'in-progress') return applications.filter(app => app.status === '进行中').length;
    if (category === 'paused') return applications.filter(app => app.status === '已暂停').length;
    if (category === 'draft') return applications.filter(app => app.status === '草稿').length;
    if (category === 'starred') return applications.filter(app => app.isStarred).length;
    if (category === 'chatbot') return applications.filter(app => app.category === 'chatbot').length;
    if (category === 'text-generation') return applications.filter(app => app.category === 'text-generation').length;
    if (category === 'knowledge') return applications.filter(app => app.category === 'knowledge').length;
    return 0;
  };

  const handleAppClick = (appName: string) => {
    toast.success(`打开应用: ${appName}`);
  };

  const handleStarToggle = (e: React.MouseEvent, appId: number) => {
    e.stopPropagation();
    toast.success('已更新星标状态');
  };

  const handleMoreAction = (action: string, appName: string) => {
    toast.success(`${action}: ${appName}`);
  };

  const getStatusBadgeColor = (status: AppStatus) => {
    switch (status) {
      case '进行中':
        return 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400';
      case '已暂停':
        return 'bg-gray-100 text-gray-700 dark:bg-gray-700 dark:text-gray-400';
      case '已完成':
        return 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400';
      case '草稿':
        return 'bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-400';
      default:
        return 'bg-gray-100 text-gray-700 dark:bg-gray-700 dark:text-gray-400';
    }
  };

  return (
    <div className="space-y-6">
      {/* Header Section - 美化的标题区域 */}
      <div className="bg-gradient-to-r from-blue-50 to-purple-50 dark:from-gray-800 dark:to-gray-800 rounded-xl p-6 border border-blue-100 dark:border-gray-700">
        <div className="flex items-center gap-3 mb-2">
          <div className="w-10 h-10 bg-blue-500 dark:bg-blue-600 rounded-lg flex items-center justify-center shrink-0">
            <FileText className="w-5 h-5 text-white" />
          </div>
          <h1 className="text-3xl font-extrabold text-gray-900 dark:text-white">我的应用</h1>
        </div>
        <p className="text-sm text-gray-600 dark:text-gray-400 ml-13">
          管理您创建的所有应用和工作流，支持快速搜索和分类查看
        </p>
      </div>

      {/* Tabs */}
      <div className="border-b border-gray-200 dark:border-gray-700">
        <div className="flex gap-4 overflow-x-auto">
          <button
            onClick={() => setActiveTab('all')}
            className={`pb-3 px-1 border-b-2 text-sm whitespace-nowrap transition-colors ${
              activeTab === 'all'
                ? 'border-blue-500 text-blue-600 dark:text-blue-400'
                : 'border-transparent text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-gray-300'
            }`}
          >
            全部应用 ({getCategoryCount('all')})
          </button>
          <button
            onClick={() => setActiveTab('in-progress')}
            className={`pb-3 px-1 border-b-2 text-sm whitespace-nowrap transition-colors ${
              activeTab === 'in-progress'
                ? 'border-blue-500 text-blue-600 dark:text-blue-400'
                : 'border-transparent text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-gray-300'
            }`}
          >
            进行中 ({getCategoryCount('in-progress')})
          </button>
          <button
            onClick={() => setActiveTab('paused')}
            className={`pb-3 px-1 border-b-2 text-sm whitespace-nowrap transition-colors ${
              activeTab === 'paused'
                ? 'border-blue-500 text-blue-600 dark:text-blue-400'
                : 'border-transparent text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-gray-300'
            }`}
          >
            已暂停 ({getCategoryCount('paused')})
          </button>
          <button
            onClick={() => setActiveTab('draft')}
            className={`pb-3 px-1 border-b-2 text-sm whitespace-nowrap transition-colors ${
              activeTab === 'draft'
                ? 'border-blue-500 text-blue-600 dark:text-blue-400'
                : 'border-transparent text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-gray-300'
            }`}
          >
            草稿 ({getCategoryCount('draft')})
          </button>
          <button
            onClick={() => setActiveTab('starred')}
            className={`pb-3 px-1 border-b-2 text-sm whitespace-nowrap transition-colors ${
              activeTab === 'starred'
                ? 'border-blue-500 text-blue-600 dark:text-blue-400'
                : 'border-transparent text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-gray-300'
            }`}
          >
            星标 ({getCategoryCount('starred')})
          </button>
          <button
            onClick={() => setActiveTab('chatbot')}
            className={`pb-3 px-1 border-b-2 text-sm whitespace-nowrap transition-colors ${
              activeTab === 'chatbot'
                ? 'border-blue-500 text-blue-600 dark:text-blue-400'
                : 'border-transparent text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-gray-300'
            }`}
          >
            聊天助手 ({getCategoryCount('chatbot')})
          </button>
          <button
            onClick={() => setActiveTab('text-generation')}
            className={`pb-3 px-1 border-b-2 text-sm whitespace-nowrap transition-colors ${
              activeTab === 'text-generation'
                ? 'border-blue-500 text-blue-600 dark:text-blue-400'
                : 'border-transparent text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-gray-300'
            }`}
          >
            文本生成 ({getCategoryCount('text-generation')})
          </button>
          <button
            onClick={() => setActiveTab('knowledge')}
            className={`pb-3 px-1 border-b-2 text-sm whitespace-nowrap transition-colors ${
              activeTab === 'knowledge'
                ? 'border-blue-500 text-blue-600 dark:text-blue-400'
                : 'border-transparent text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-gray-300'
            }`}
          >
            知识问答 ({getCategoryCount('knowledge')})
          </button>
        </div>
      </div>

      {/* Action Buttons and Search - 放在列表正上方 */}
      <div className="flex items-center justify-between gap-3">
        {/* Search Bar - 左侧390px */}
        <div className="relative w-[390px]">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400 dark:text-gray-500" />
          <Input
            type="text"
            placeholder="搜索应用名称、描述或标签..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="pl-10 pr-10 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-100 focus-visible:border-blue-500 focus-visible:ring-blue-500/50"
          />
          {searchQuery && (
            <button
              onClick={() => setSearchQuery('')}
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
              <DropdownMenuItem className="dark:text-gray-300">最近创建</DropdownMenuItem>
              <DropdownMenuItem className="dark:text-gray-300">最多访问</DropdownMenuItem>
              <DropdownMenuItem className="dark:text-gray-300">按名称排序</DropdownMenuItem>
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
              onClick={() => setViewMode('list')}
              className={`p-1.5 rounded ${
                viewMode === 'list'
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
            onClick={onCreateNew}
          >
            <Plus className="w-4 h-4 mr-2" />
            新建应用
          </Button>
        </div>
      </div>

      {/* Applications Grid - 添加10px顶部间距 */}
      <div className={viewMode === 'grid' ? 'grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 mt-2.5' : 'space-y-3 mt-2.5'}>
        {filteredApps.map((app) => {
          const Icon = app.icon;
          
          if (viewMode === 'list') {
            // List View - 紧凑的横向布局
            return (
              <div
                key={app.id}
                onClick={() => handleAppClick(app.name)}
                className="bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-lg p-3 hover:shadow-md dark:hover:shadow-gray-900/50 transition-shadow cursor-pointer group"
              >
                <div className="flex items-center gap-4">
                  {/* 左侧：图标和基本信息 */}
                  <div className={`${app.iconBgColor} w-10 h-10 rounded-lg flex items-center justify-center flex-shrink-0`}>
                    <Icon className="w-5 h-5 text-white" />
                  </div>
                  
                  <div className="flex-1 min-w-0">
                    <h3 className="dark:text-white group-hover:text-blue-600 dark:group-hover:text-blue-400 transition-colors">
                      {app.name}
                    </h3>
                    <p className="text-sm text-gray-600 dark:text-gray-400 line-clamp-1">
                      {app.description}
                    </p>
                  </div>

                  {/* 右侧：标签、调用次数、操作 */}
                  <div className="flex items-center gap-4">
                    {/* 标签 */}
                    <div className="flex flex-wrap gap-1.5">
                      {app.tags.slice(0, 2).map((tag, index) => (
                        <span
                          key={index}
                          className={`text-xs px-2 py-1 rounded-md ${tag.color} border-0`}
                        >
                          {tag.label}
                        </span>
                      ))}
                      {app.tags.length > 2 && (
                        <span className="text-xs px-2 py-1 rounded-md bg-gray-100 text-gray-600 dark:bg-gray-700 dark:text-gray-400 border-0">
                          +{app.tags.length - 2}
                        </span>
                      )}
                    </div>

                    {/* 调用次数 */}
                    <div className="flex items-center gap-1 text-sm text-gray-500 dark:text-gray-400 min-w-[80px]">
                      <Clock className="w-4 h-4" />
                      <span>{app.visits || '暂无数据'}</span>
                    </div>

                    {/* 状态标签 */}
                    <Badge className={`text-xs ${getStatusBadgeColor(app.status as AppStatus)}`}>
                      {app.status}
                    </Badge>

                    {/* 收藏和菜单 */}
                    <div className="flex items-center gap-1">
                      <button
                        onClick={(e) => handleStarToggle(e, app.id)}
                        className={`p-1 rounded transition-colors ${
                          app.isStarred
                            ? 'text-yellow-500 hover:text-yellow-600'
                            : 'text-gray-400 hover:text-yellow-500 dark:text-gray-500 dark:hover:text-yellow-500'
                        }`}
                      >
                        <Star className={`w-4 h-4 ${app.isStarred ? 'fill-current' : ''}`} />
                      </button>
                      <DropdownMenu>
                        <DropdownMenuTrigger asChild>
                          <button 
                            onClick={(e) => e.stopPropagation()}
                            className="p-1 hover:bg-gray-100 dark:hover:bg-gray-700 rounded transition-colors opacity-0 group-hover:opacity-100"
                          >
                            <MoreVertical className="w-4 h-4 text-gray-600 dark:text-gray-400" />
                          </button>
                        </DropdownMenuTrigger>
                        <DropdownMenuContent align="end" className="dark:bg-gray-800 dark:border-gray-700">
                          <DropdownMenuItem onClick={() => handleMoreAction('编辑', app.name)} className="dark:text-gray-300">
                            编辑
                          </DropdownMenuItem>
                          <DropdownMenuItem onClick={() => handleMoreAction('复制', app.name)} className="dark:text-gray-300">
                            复制
                          </DropdownMenuItem>
                          <DropdownMenuItem onClick={() => handleMoreAction('分享', app.name)} className="dark:text-gray-300">
                            分享
                          </DropdownMenuItem>
                          <DropdownMenuItem onClick={() => handleMoreAction('删除', app.name)} className="text-red-600 dark:text-red-400">
                            删除
                          </DropdownMenuItem>
                        </DropdownMenuContent>
                      </DropdownMenu>
                    </div>
                  </div>
                </div>
              </div>
            );
          }

          // Grid View - 原有的卡片布局
          return (
            <div
              key={app.id}
              onClick={() => handleAppClick(app.name)}
              className="bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-lg p-4 hover:shadow-md dark:hover:shadow-gray-900/50 transition-shadow cursor-pointer group"
            >
              {/* Header */}
              <div className="flex items-start justify-between mb-4">
                <div className="flex items-start gap-3">
                  <div className={`${app.iconBgColor} w-12 h-12 rounded-xl flex items-center justify-center flex-shrink-0`}>
                    <Icon className="w-6 h-6 text-white" />
                  </div>
                  <div className="flex-1 min-w-0">
                    <h3 className="dark:text-white mb-1 group-hover:text-blue-600 dark:group-hover:text-blue-400 transition-colors">
                      {app.name}
                    </h3>
                    <Badge className={`text-xs ${getStatusBadgeColor(app.status as AppStatus)}`}>
                      {app.status}
                    </Badge>
                  </div>
                </div>
                <DropdownMenu>
                  <DropdownMenuTrigger asChild>
                    <button 
                      onClick={(e) => e.stopPropagation()}
                      className="p-1 hover:bg-gray-100 dark:hover:bg-gray-700 rounded transition-colors opacity-0 group-hover:opacity-100"
                    >
                      <MoreVertical className="w-4 h-4 text-gray-600 dark:text-gray-400" />
                    </button>
                  </DropdownMenuTrigger>
                  <DropdownMenuContent align="end" className="dark:bg-gray-800 dark:border-gray-700">
                    <DropdownMenuItem onClick={() => handleMoreAction('编辑', app.name)} className="dark:text-gray-300">
                      编辑
                    </DropdownMenuItem>
                    <DropdownMenuItem onClick={() => handleMoreAction('复制', app.name)} className="dark:text-gray-300">
                      复制
                    </DropdownMenuItem>
                    <DropdownMenuItem onClick={() => handleMoreAction('分享', app.name)} className="dark:text-gray-300">
                      分享
                    </DropdownMenuItem>
                    <DropdownMenuItem onClick={() => handleMoreAction('删除', app.name)} className="text-red-600 dark:text-red-400">
                      删除
                    </DropdownMenuItem>
                  </DropdownMenuContent>
                </DropdownMenu>
              </div>

              {/* Description */}
              <p className="text-sm text-gray-600 dark:text-gray-400 mb-4 line-clamp-2">
                {app.description}
              </p>

              {/* Tags */}
              <div className="flex flex-wrap gap-2 mb-4">
                {app.tags.map((tag, index) => (
                  <span
                    key={index}
                    className={`text-xs px-2 py-1 rounded-md ${tag.color}`}
                  >
                    {tag.label}
                  </span>
                ))}
              </div>

              {/* Footer */}
              <div className="flex items-center justify-between pt-3 border-t border-gray-100 dark:border-gray-700">
                <div className="flex items-center gap-1 text-sm text-gray-500 dark:text-gray-400">
                  <Clock className="w-4 h-4" />
                  <span>{app.visits || '暂无数据'}</span>
                </div>
                <button
                  onClick={(e) => handleStarToggle(e, app.id)}
                  className={`p-1 rounded transition-colors ${
                    app.isStarred
                      ? 'text-yellow-500 hover:text-yellow-600'
                      : 'text-gray-400 hover:text-yellow-500 dark:text-gray-500 dark:hover:text-yellow-500'
                  }`}
                >
                  <Star className={`w-4 h-4 ${app.isStarred ? 'fill-current' : ''}`} />
                </button>
              </div>
            </div>
          );
        })}
      </div>

      {/* Empty State */}
      {filteredApps.length === 0 && (
        <div className="text-center py-12">
          <div className="w-16 h-16 bg-gray-100 dark:bg-gray-800 rounded-full flex items-center justify-center mx-auto mb-4">
            {searchQuery ? (
              <Search className="w-8 h-8 text-gray-400 dark:text-gray-600" />
            ) : (
              <Zap className="w-8 h-8 text-gray-400 dark:text-gray-600" />
            )}
          </div>
          <h3 className="text-gray-900 dark:text-white mb-2">
            {searchQuery ? '未找到相关应用' : '暂无应用'}
          </h3>
          <p className="text-sm text-gray-600 dark:text-gray-400 mb-4">
            {searchQuery ? `没有找到包含"${searchQuery}"的应用` : '开始创建您的第一个AI应用'}
          </p>
          {!searchQuery && (
            <Button 
              className="bg-blue-500 hover:bg-blue-600 text-white"
              onClick={onCreateNew}
            >
              <Plus className="w-4 h-4 mr-2" />
              创建应用
            </Button>
          )}
        </div>
      )}
    </div>
  );
}
