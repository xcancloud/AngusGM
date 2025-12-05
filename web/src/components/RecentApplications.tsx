import { MessageSquare, FileText, Database, ExternalLink, Edit, Clock, BarChart, Settings } from 'lucide-react';
import { Card } from './ui/card';
import { Button } from './ui/button';
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from './ui/dialog';
import { Skeleton } from './ui/skeleton';
import { useState } from 'react';

interface Tag {
  label: string;
  color: string;
}

interface Application {
  id: string;
  icon: any;
  name: string;
  description: string;
  fullDescription: string;
  tags: Tag[];
  usage: string;
  iconBg: string;
  createdAt: string;
  lastUsed: string;
  totalCalls: string;
  avgResponseTime: string;
}

function ApplicationSkeleton() {
  return (
    <Card className="p-5 dark:bg-gray-800">
      <div className="flex items-start justify-between mb-3">
        <div className="flex items-center gap-3">
          <Skeleton className="w-12 h-12 rounded-xl dark:bg-gray-700" />
          <Skeleton className="h-6 w-32 dark:bg-gray-700" />
        </div>
        <div className="flex gap-2">
          <Skeleton className="w-8 h-8 rounded-lg dark:bg-gray-700" />
          <Skeleton className="w-8 h-8 rounded-lg dark:bg-gray-700" />
        </div>
      </div>
      
      <Skeleton className="h-4 w-full mb-2 dark:bg-gray-700" />
      <Skeleton className="h-4 w-3/4 mb-2 dark:bg-gray-700" />
      
      <div className="flex gap-2 mb-2">
        <Skeleton className="h-6 w-16 rounded-md dark:bg-gray-700" />
        <Skeleton className="h-6 w-16 rounded-md dark:bg-gray-700" />
      </div>
      
      <Skeleton className="h-3 w-24 dark:bg-gray-700" />
    </Card>
  );
}

export function RecentApplications() {
  const [applications] = useState<Application[]>([
    {
      id: '1',
      icon: MessageSquare,
      name: '智能助手',
      description: '智能聊答问答机器人，可实现多轮对话支持场景',
      fullDescription: '智能聊答问答机器人，可实现多轮对话支持场景。基于先进的自然语言处理技术，能够理解用户意图，提供精准的回答。支持上下文记忆，多轮对话流畅自然。适用于客服、咨询、教育等多种场景。',
      tags: [
        { label: '聊天助手', color: 'bg-blue-100 text-blue-700' },
        { label: '智能问答', color: 'bg-purple-100 text-purple-700' }
      ],
      usage: '已 1.2K 次调用',
      iconBg: 'bg-blue-500',
      createdAt: '2024-01-15',
      lastUsed: '2小时前',
      totalCalls: '1,248',
      avgResponseTime: '0.8s',
    },
    {
      id: '2',
      icon: FileText,
      name: '内容生成器',
      description: 'AI 内容创作工具，自动生成高质量文章，广泛支持多种写作场景',
      fullDescription: 'AI 内容创作工具，自动生成高质量文章。支持多种写作风格和场景，包括新闻稿、营销文案、技术文档等。采用GPT-4模型，生成内容流畅自然，符合SEO优化标准。提供多种模板和自定义选项。',
      tags: [
        { label: '文本生成', color: 'bg-green-100 text-green-700' }
      ],
      usage: '已 856 次调用',
      iconBg: 'bg-purple-500',
      createdAt: '2024-01-20',
      lastUsed: '1天前',
      totalCalls: '856',
      avgResponseTime: '2.3s',
    },
    {
      id: '3',
      icon: Database,
      name: '产品知识库',
      description: '基于企业知识库的智能问答，支持多来源数据上下文搜索',
      fullDescription: '基于企业知识库的智能问答系统，支持多来源数据整合和上下文搜索。可以导入文档、网页、数据库等多种数据源，自动构建知识图谱。提供语义搜索和精准匹配，快速找到相关信息。适用于企业内部知识管理和客户服务。',
      tags: [
        { label: '知识问答', color: 'bg-orange-100 text-orange-700' },
        { label: '智能检索', color: 'bg-pink-100 text-pink-700' }
      ],
      usage: '已 重运行',
      iconBg: 'bg-green-500',
      createdAt: '2024-02-01',
      lastUsed: '3天前',
      totalCalls: '542',
      avgResponseTime: '1.2s',
    },
  ]);

  const [selectedApp, setSelectedApp] = useState<Application | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const handleCardClick = (app: Application) => {
    setSelectedApp(app);
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-4">
        <div className="flex items-center gap-2">
          <div className="w-2 h-6 bg-blue-500 rounded-full"></div>
          <h2 className="text-lg dark:text-white">最近使用</h2>
        </div>
        <Button variant="link" className="text-blue-500 dark:text-blue-400">
          查看全部 →
        </Button>
      </div>
      
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
        {isLoading ? (
          <>
            <ApplicationSkeleton />
            <ApplicationSkeleton />
            <ApplicationSkeleton />
          </>
        ) : (
          applications.map((app) => (
            <Card key={app.id} className="p-5 hover:shadow-lg transition-all group dark:bg-gray-800">
              <div className="flex items-start justify-between mb-3">
                <div className="flex items-center gap-3">
                  <div className={`${app.iconBg} w-12 h-12 rounded-xl flex items-center justify-center group-hover:scale-110 transition-transform`}>
                    <app.icon className="w-6 h-6 text-white" />
                  </div>
                  <h3 className="dark:text-white">{app.name}</h3>
                </div>
                <div className="flex gap-2">
                  <button 
                    onClick={(e) => {
                      e.stopPropagation();
                    }}
                    className="p-2 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg opacity-0 group-hover:opacity-100 transition-opacity"
                  >
                    <Edit className="w-4 h-4 text-gray-600 dark:text-gray-300" />
                  </button>
                  <button 
                    onClick={(e) => {
                      e.stopPropagation();
                    }}
                    className="p-2 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg opacity-0 group-hover:opacity-100 transition-opacity"
                  >
                    <ExternalLink className="w-4 h-4 text-gray-600 dark:text-gray-300" />
                  </button>
                </div>
              </div>
              
              <div onClick={() => handleCardClick(app)} className="cursor-pointer">
                <p className="text-sm text-gray-500 dark:text-gray-400 mb-2 leading-relaxed line-clamp-2">
                  {app.description}
                </p>
                
                <div className="flex flex-wrap gap-2 mb-2">
                  {app.tags.map((tag, tagIndex) => (
                    <span key={tagIndex} className={`text-xs px-2 py-1 rounded-md ${tag.color}`}>
                      {tag.label}
                    </span>
                  ))}
                </div>
                
                <div className="flex items-center justify-between text-xs text-gray-400 dark:text-gray-500">
                  <span>{app.usage}</span>
                  <span>最后访问：{app.lastUsed}</span>
                </div>
              </div>
            </Card>
          ))
        )}
      </div>

      {/* Detail Dialog */}
      <Dialog open={!!selectedApp} onOpenChange={() => setSelectedApp(null)}>
        <DialogContent className="max-w-2xl">
          <DialogHeader>
            <div className="flex items-start gap-4 mb-4">
              {selectedApp && (
                <>
                  <div className={`${selectedApp.iconBg} w-16 h-16 rounded-xl flex items-center justify-center`}>
                    <selectedApp.icon className="w-8 h-8 text-white" />
                  </div>
                  <div className="flex-1">
                    <DialogTitle className="text-2xl mb-2">{selectedApp.name}</DialogTitle>
                    <div className="flex flex-wrap gap-2">
                      {selectedApp.tags.map((tag, index) => (
                        <span key={index} className={`text-xs px-2 py-1 rounded-md ${tag.color}`}>
                          {tag.label}
                        </span>
                      ))}
                    </div>
                  </div>
                </>
              )}
            </div>
          </DialogHeader>
          
          {selectedApp && (
            <div className="space-y-6">
              <div>
                <h4 className="text-sm text-gray-500 mb-2">应用描述</h4>
                <DialogDescription className="text-base leading-relaxed">
                  {selectedApp.fullDescription}
                </DialogDescription>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div className="flex items-center gap-3 p-3 bg-gray-50 rounded-lg">
                  <Clock className="w-5 h-5 text-blue-500" />
                  <div>
                    <div className="text-xs text-gray-500">最后使用</div>
                    <div className="text-sm">{selectedApp.lastUsed}</div>
                  </div>
                </div>
                <div className="flex items-center gap-3 p-3 bg-gray-50 rounded-lg">
                  <BarChart className="w-5 h-5 text-green-500" />
                  <div>
                    <div className="text-xs text-gray-500">总调用次数</div>
                    <div className="text-sm">{selectedApp.totalCalls}</div>
                  </div>
                </div>
                <div className="flex items-center gap-3 p-3 bg-gray-50 rounded-lg">
                  <Settings className="w-5 h-5 text-purple-500" />
                  <div>
                    <div className="text-xs text-gray-500">平均响应时间</div>
                    <div className="text-sm">{selectedApp.avgResponseTime}</div>
                  </div>
                </div>
                <div className="flex items-center gap-3 p-3 bg-gray-50 rounded-lg">
                  <Clock className="w-5 h-5 text-orange-500" />
                  <div>
                    <div className="text-xs text-gray-500">创建时间</div>
                    <div className="text-sm">{selectedApp.createdAt}</div>
                  </div>
                </div>
              </div>

              <div className="flex gap-3 pt-4 border-t">
                <Button className="flex-1 bg-blue-500 hover:bg-blue-600">
                  立即使用
                </Button>
                <Button variant="outline" className="flex-1">
                  编辑应用
                </Button>
              </div>
            </div>
          )}
        </DialogContent>
      </Dialog>
    </div>
  );
}
