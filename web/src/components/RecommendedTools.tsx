import { MessageSquare, FileText, HelpCircle, User } from 'lucide-react';
import { Card } from './ui/card';

export function RecommendedTools() {
  const tools = [
    {
      icon: MessageSquare,
      name: '智能助手',
      description: '智能聊答问答机器人，可实现多轮对话支持场景',
      iconBg: 'bg-blue-500',
    },
    {
      icon: FileText,
      name: '文本生成',
      description: 'AI 内容创作工具，自动生成高质量文章',
      iconBg: 'bg-purple-500',
    },
    {
      icon: HelpCircle,
      name: '知识问答',
      description: '基于知识库的问答功能，快速找到答案',
      iconBg: 'bg-green-500',
    },
    {
      icon: User,
      name: 'Agent 代理',
      description: '智能任务执行代理，自动化处理复杂任务',
      iconBg: 'bg-orange-500',
    },
  ];

  return (
    <div>
      <div className="flex items-center gap-2 mb-4">
        <div className="w-2 h-6 bg-orange-500 rounded-full"></div>
        <h2 className="text-lg dark:text-white">快速操作</h2>
      </div>
      
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {tools.map((tool, index) => (
          <Card key={index} className="p-5 hover:shadow-lg transition-all cursor-pointer group hover:border-blue-500 dark:bg-gray-800 dark:hover:border-blue-400">
            <div className="flex items-center gap-3 mb-3">
              <div className={`${tool.iconBg} w-12 h-12 rounded-xl flex items-center justify-center group-hover:scale-110 transition-transform`}>
                <tool.icon className="w-6 h-6 text-white" />
              </div>
              <h3 className="dark:text-white">{tool.name}</h3>
            </div>
            <p className="text-sm text-gray-500 dark:text-gray-400 leading-relaxed">{tool.description}</p>
          </Card>
        ))}
      </div>
    </div>
  );
}
