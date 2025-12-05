import { Users, Shield, Bell, Settings, TrendingUp, TrendingDown, Activity, AlertCircle, CheckCircle, Clock, Mail, MessageSquare, Smartphone, UserCheck } from 'lucide-react';
import { Card } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Progress } from '@/components/ui/progress';
import { Button } from '@/components/ui/button';
import { LineChart, Line, AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import { useState } from 'react';

export function Dashboard() {
  const [timeRange, setTimeRange] = useState<'7days' | '30days' | '90days' | '6months' | '1year'>('6months');

  const stats = [
    {
      label: '用户统计',
      value: '1,234',
      subValue: '活跃: 1,156 | 在线: 342',
      change: '+12.5%',
      trend: 'up',
      icon: Users,
      iconBg: 'bg-blue-500',
    },
    {
      label: '租户统计',
      value: '95',
      subValue: '活跃租户: 89',
      change: '+8.3%',
      trend: 'up',
      icon: Shield,
      iconBg: 'bg-green-500',
    },
    {
      label: '日志统计',
      value: '156',
      subValue: '异常: 12 | 系统: 23',
      change: '-15.2%',
      trend: 'down',
      icon: Bell,
      iconBg: 'bg-orange-500',
    },
    {
      label: '系统消息',
      value: '2,345',
      subValue: '站内: 1,234 | 邮件: 890 | 短信: 221',
      change: '+18.7%',
      trend: 'up',
      icon: MessageSquare,
      iconBg: 'bg-purple-500',
    },
  ];

  const recentActivities = [
    {
      id: 1,
      type: 'success',
      icon: CheckCircle,
      title: '用户审核通过',
      description: '用户"张三"已通过审核',
      time: '5分钟前',
    },
    {
      id: 2,
      type: 'warning',
      icon: AlertCircle,
      title: '资源配额预警',
      description: '租户"某某科技"存储空间达90%',
      time: '15分钟前',
    },
    {
      id: 3,
      type: 'info',
      icon: Clock,
      title: 'LDAP同步完成',
      description: '同步完成，新增32个用户',
      time: '1小时前',
    },
    {
      id: 4,
      type: 'success',
      icon: CheckCircle,
      title: '系统更新完成',
      description: '系统已更新至v2.1.0',
      time: '2小时前',
    },
  ];

  const systemResources = [
    { label: 'CPU使用率', value: 45, total: '16核', color: 'bg-blue-500' },
    { label: '内存使用率', value: 68, total: '64GB', color: 'bg-green-500' },
    { label: '磁盘使用率', value: 52, total: '2TB', color: 'bg-orange-500' },
    { label: '网络带宽', value: 35, total: '1Gbps', color: 'bg-purple-500' },
  ];

  // 用户增长趋势数据 - 根据时间段不同展示不同数据
  const getGrowthData = () => {
    switch (timeRange) {
      case '7days':
        return [
          { month: '11/27', users: 1198 },
          { month: '11/28', users: 1203 },
          { month: '11/29', users: 1210 },
          { month: '11/30', users: 1215 },
          { month: '12/01', users: 1223 },
          { month: '12/02', users: 1228 },
          { month: '12/03', users: 1234 },
        ];
      case '30days':
        return [
          { month: '11/04', users: 1156 },
          { month: '11/08', users: 1165 },
          { month: '11/12', users: 1173 },
          { month: '11/16', users: 1182 },
          { month: '11/20', users: 1195 },
          { month: '11/24', users: 1208 },
          { month: '11/28', users: 1220 },
          { month: '12/03', users: 1234 },
        ];
      case '90days':
        return [
          { month: '9/5', users: 1089 },
          { month: '9/18', users: 1098 },
          { month: '10/1', users: 1112 },
          { month: '10/14', users: 1128 },
          { month: '10/27', users: 1145 },
          { month: '11/9', users: 1168 },
          { month: '11/22', users: 1195 },
          { month: '12/3', users: 1234 },
        ];
      case '6months':
        return [
          { month: '7月', users: 1089 },
          { month: '8月', users: 1134 },
          { month: '9月', users: 1187 },
          { month: '10月', users: 1234 },
          { month: '11月', users: 1289 },
          { month: '12月', users: 1345 },
        ];
      case '1year':
        return [
          { month: '2024/1', users: 856 },
          { month: '2024/2', users: 878 },
          { month: '2024/3', users: 901 },
          { month: '2024/4', users: 923 },
          { month: '2024/5', users: 956 },
          { month: '2024/6', users: 1012 },
          { month: '2024/7', users: 1089 },
          { month: '2024/8', users: 1134 },
          { month: '2024/9', users: 1187 },
          { month: '2024/10', users: 1234 },
          { month: '2024/11', users: 1289 },
          { month: '2024/12', users: 1345 },
        ];
      default:
        return [];
    }
  };

  const growthData = getGrowthData();

  // 获取时间段描述
  const getTimeRangeLabel = () => {
    switch (timeRange) {
      case '7days':
        return '最近7天用户增长情况';
      case '30days':
        return '最近30天用户增长情况';
      case '90days':
        return '最近90天用户增长情况';
      case '6months':
        return '最近6个月用户增长情况';
      case '1year':
        return '最近1年用户增长情况';
      default:
        return '';
    }
  };

  const getActivityColor = (type: string) => {
    switch (type) {
      case 'success':
        return 'text-green-500';
      case 'warning':
        return 'text-orange-500';
      case 'info':
        return 'text-blue-500';
      default:
        return 'text-gray-500';
    }
  };

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div className="bg-gradient-to-r from-blue-50 to-indigo-50 dark:from-blue-900/20 dark:to-indigo-900/20 rounded-lg p-6 border border-blue-100 dark:border-blue-800/30">
        <h1 className="text-2xl dark:text-white mb-2">系统概览</h1>
        <p className="text-sm text-gray-600 dark:text-gray-400">
          实时监控系统运行状态、资源使用情况和最近活动
        </p>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {stats.map((stat, index) => {
          const Icon = stat.icon;
          const TrendIcon = stat.trend === 'up' ? TrendingUp : TrendingDown;
          return (
            <Card key={index} className="p-5 dark:bg-gray-800 dark:border-gray-700">
              <div className="flex items-start justify-between mb-3">
                <div className={`${stat.iconBg} w-12 h-12 rounded-lg flex items-center justify-center flex-shrink-0`}>
                  <Icon className="w-6 h-6 text-white" />
                </div>
                <div className={`flex items-center gap-1 text-xs ${
                  stat.trend === 'up' ? 'text-green-600 dark:text-green-400' : 'text-red-600 dark:text-red-400'
                }`}>
                  <TrendIcon className="w-3 h-3" />
                  {stat.change}
                </div>
              </div>
              <div className="flex items-baseline gap-4 mb-2">
                <h3 className="text-base dark:text-white">{stat.label}</h3>
                <span className="text-3xl dark:text-white">{stat.value}</span>
              </div>
              <div className="text-xs text-gray-400 dark:text-gray-500 text-right">{stat.subValue}</div>
            </Card>
          );
        })}
      </div>

      {/* User Growth Chart */}
      <Card className="dark:bg-gray-800 dark:border-gray-700">
        <div className="p-5 border-b dark:border-gray-700 flex items-center justify-between">
          <div>
            <h3 className="dark:text-white">用户增长趋势</h3>
          </div>
          <div className="flex items-center gap-2">
            <Button
              variant={timeRange === '7days' ? 'default' : 'outline'}
              size="sm"
              onClick={() => setTimeRange('7days')}
              className={timeRange === '7days' ? 'bg-blue-600 hover:bg-blue-700' : 'dark:border-gray-600 dark:text-gray-300'}
            >
              7天
            </Button>
            <Button
              variant={timeRange === '30days' ? 'default' : 'outline'}
              size="sm"
              onClick={() => setTimeRange('30days')}
              className={timeRange === '30days' ? 'bg-blue-600 hover:bg-blue-700' : 'dark:border-gray-600 dark:text-gray-300'}
            >
              30天
            </Button>
            <Button
              variant={timeRange === '90days' ? 'default' : 'outline'}
              size="sm"
              onClick={() => setTimeRange('90days')}
              className={timeRange === '90days' ? 'bg-blue-600 hover:bg-blue-700' : 'dark:border-gray-600 dark:text-gray-300'}
            >
              90天
            </Button>
            <Button
              variant={timeRange === '6months' ? 'default' : 'outline'}
              size="sm"
              onClick={() => setTimeRange('6months')}
              className={timeRange === '6months' ? 'bg-blue-600 hover:bg-blue-700' : 'dark:border-gray-600 dark:text-gray-300'}
            >
              6个月
            </Button>
            <Button
              variant={timeRange === '1year' ? 'default' : 'outline'}
              size="sm"
              onClick={() => setTimeRange('1year')}
              className={timeRange === '1year' ? 'bg-blue-600 hover:bg-blue-700' : 'dark:border-gray-600 dark:text-gray-300'}
            >
              1年
            </Button>
          </div>
        </div>
        <div className="p-5">
          <ResponsiveContainer width="100%" height={280}>
            <AreaChart data={growthData}>
              <defs>
                <linearGradient id="colorUsers" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#3b82f6" stopOpacity={0.3}/>
                  <stop offset="95%" stopColor="#3b82f6" stopOpacity={0}/>
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="3 3" stroke="#374151" opacity={0.3} />
              <XAxis 
                dataKey="month" 
                stroke="#9ca3af"
                style={{ fontSize: '12px' }}
              />
              <YAxis 
                stroke="#9ca3af"
                style={{ fontSize: '12px' }}
              />
              <Tooltip
                contentStyle={{
                  backgroundColor: '#1f2937',
                  border: '1px solid #374151',
                  borderRadius: '8px',
                  color: '#fff'
                }}
              />
              <Area 
                type="monotone" 
                dataKey="users" 
                stroke="#3b82f6" 
                strokeWidth={2}
                fill="url(#colorUsers)" 
              />
            </AreaChart>
          </ResponsiveContainer>
        </div>
      </Card>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* System Resources */}
        <Card className="dark:bg-gray-800 dark:border-gray-700">
          <div className="p-5 border-b dark:border-gray-700">
            <h3 className="dark:text-white">系统资源</h3>
            <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
              实时资源使用情况
            </p>
          </div>
          <div className="p-5">
            <div className="space-y-5">
              {systemResources.map((resource, index) => (
                <div key={index}>
                  <div className="flex items-center justify-between mb-2">
                    <div className="flex items-center gap-2">
                      <span className="text-sm text-gray-700 dark:text-gray-300">
                        {resource.label}
                      </span>
                      <span className="text-xs text-gray-400 dark:text-gray-500">
                        {resource.total}
                      </span>
                    </div>
                    <span className="text-sm dark:text-white">
                      {resource.value}%
                    </span>
                  </div>
                  <div className="relative h-2 bg-gray-200 dark:bg-gray-700 rounded-full overflow-hidden">
                    <div 
                      className={`h-full ${resource.color} transition-all duration-300`}
                      style={{ width: `${resource.value}%` }}
                    />
                  </div>
                </div>
              ))}
            </div>
          </div>
        </Card>

        {/* Recent Activities */}
        <Card className="dark:bg-gray-800 dark:border-gray-700">
          <div className="p-5 border-b dark:border-gray-700">
            <h3 className="dark:text-white">最近活动</h3>
            <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
              系统最新动态
            </p>
          </div>
          <div className="p-5">
            <div className="space-y-4">
              {recentActivities.map((activity) => {
                const Icon = activity.icon;
                return (
                  <div key={activity.id} className="flex gap-3">
                    <div className={`flex-shrink-0 ${getActivityColor(activity.type)}`}>
                      <Icon className="w-5 h-5" />
                    </div>
                    <div className="flex-1 min-w-0">
                      <div className="dark:text-white mb-1">{activity.title}</div>
                      <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">
                        {activity.description}
                      </p>
                      <span className="text-xs text-gray-500 dark:text-gray-500">
                        {activity.time}
                      </span>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        </Card>
      </div>
    </div>
  );
}