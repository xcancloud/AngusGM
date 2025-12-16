import { TrendingUp, Activity, CheckCircle, XCircle, Clock, AlertTriangle, Filter, Search, RefreshCw, BarChart3 } from 'lucide-react';
import { Card } from '@/components/ui/card';  
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { useState, useEffect } from 'react';
import { LineChart, Line, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts';

export function InterfaceMonitoring() {
  const [searchQuery, setSearchQuery] = useState('');
  const [timeRange, setTimeRange] = useState('1h');
  const [currentTime, setCurrentTime] = useState(new Date());
  const [autoRefresh, setAutoRefresh] = useState(true);

  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentTime(new Date());
    }, 1000);
    return () => clearInterval(timer);
  }, []);

  // Mock statistics data
  const stats = [
    {
      name: '总调用次数',
      value: '125,842',
      icon: Activity,
      color: 'blue',
      trend: 'up',
      change: '+12.5%',
    },
    {
      name: '平均响应时间',
      value: '245ms',
      icon: Clock,
      color: 'green',
      trend: 'down',
      change: '-8.2%',
    },
    {
      name: '成功率',
      value: '99.2%',
      icon: CheckCircle,
      color: 'emerald',
      trend: 'up',
      change: '+0.3%',
    },
    {
      name: '错误率',
      value: '0.8%',
      icon: AlertTriangle,
      color: 'red',
      trend: 'down',
      change: '-0.3%',
    },
  ];

  // Mock trend data
  const callTrendData = [
    { time: '00:00', calls: 1200, success: 1188, error: 12 },
    { time: '04:00', calls: 980, success: 970, error: 10 },
    { time: '08:00', calls: 2150, success: 2128, error: 22 },
    { time: '12:00', calls: 2850, success: 2822, error: 28 },
    { time: '16:00', calls: 2420, success: 2396, error: 24 },
    { time: '20:00', calls: 1680, success: 1664, error: 16 },
  ];

  const responseTimeData = [
    { time: '00:00', avg: 245, p95: 380, p99: 520 },
    { time: '04:00', avg: 220, p95: 350, p99: 480 },
    { time: '08:00', avg: 280, p95: 420, p99: 580 },
    { time: '12:00', avg: 310, p95: 460, p99: 650 },
    { time: '16:00', avg: 265, p95: 400, p99: 550 },
    { time: '20:00', avg: 235, p95: 370, p99: 510 },
  ];

  const statusDistribution = [
    { name: '2xx 成功', value: 98500, color: '#10B981' },
    { name: '4xx 客户端错误', value: 620, color: '#F59E0B' },
    { name: '5xx 服务器错误', value: 180, color: '#EF4444' },
  ];

  // Mock interface performance ranking
  const topInterfaces = [
    {
      id: 'API001',
      path: '/api/v1/users',
      method: 'GET',
      calls: 28500,
      avgTime: 85,
      successRate: 99.8,
      errorCount: 57,
    },
    {
      id: 'API002',
      path: '/api/v1/auth/login',
      method: 'POST',
      calls: 15200,
      avgTime: 320,
      successRate: 98.5,
      errorCount: 228,
    },
    {
      id: 'API003',
      path: '/api/v1/projects',
      method: 'GET',
      calls: 12800,
      avgTime: 156,
      successRate: 99.5,
      errorCount: 64,
    },
    {
      id: 'API004',
      path: '/api/v1/files/upload',
      method: 'POST',
      calls: 8600,
      avgTime: 1250,
      successRate: 97.2,
      errorCount: 241,
    },
    {
      id: 'API005',
      path: '/api/v1/search',
      method: 'POST',
      calls: 7200,
      avgTime: 420,
      successRate: 99.1,
      errorCount: 65,
    },
  ];

  // Mock real-time call logs
  const recentCalls = [
    {
      id: 'L001',
      time: '2024-12-10 15:30:25',
      path: '/api/v1/users/12345',
      method: 'GET',
      status: 200,
      responseTime: 85,
      ip: '192.168.1.100',
    },
    {
      id: 'L002',
      time: '2024-12-10 15:30:24',
      path: '/api/v1/auth/login',
      method: 'POST',
      status: 200,
      responseTime: 320,
      ip: '192.168.1.105',
    },
    {
      id: 'L003',
      time: '2024-12-10 15:30:23',
      path: '/api/v1/projects',
      method: 'GET',
      status: 200,
      responseTime: 142,
      ip: '192.168.1.108',
    },
    {
      id: 'L004',
      time: '2024-12-10 15:30:22',
      path: '/api/v1/files/upload',
      method: 'POST',
      status: 500,
      responseTime: 1580,
      ip: '192.168.1.112',
      error: '文件大小超出限制',
    },
    {
      id: 'L005',
      time: '2024-12-10 15:30:21',
      path: '/api/v1/search',
      method: 'POST',
      status: 200,
      responseTime: 385,
      ip: '192.168.1.115',
    },
    {
      id: 'L006',
      time: '2024-12-10 15:30:20',
      path: '/api/v1/users',
      method: 'GET',
      status: 200,
      responseTime: 76,
      ip: '192.168.1.120',
    },
    {
      id: 'L007',
      time: '2024-12-10 15:30:19',
      path: '/api/v1/notifications',
      method: 'GET',
      status: 404,
      responseTime: 45,
      ip: '192.168.1.125',
      error: '资源不存在',
    },
    {
      id: 'L008',
      time: '2024-12-10 15:30:18',
      path: '/api/v1/projects/5678',
      method: 'PUT',
      status: 200,
      responseTime: 245,
      ip: '192.168.1.130',
    },
  ];

  // Mock error interfaces
  const errorInterfaces = [
    {
      id: 'E001',
      path: '/api/v1/files/upload',
      method: 'POST',
      errorCount: 241,
      errorRate: '2.8%',
      lastError: '文件大小超出限制',
      lastTime: '2分钟前',
    },
    {
      id: 'E002',
      path: '/api/v1/auth/login',
      method: 'POST',
      errorCount: 228,
      errorRate: '1.5%',
      lastError: '用户名或密码错误',
      lastTime: '5分钟前',
    },
    {
      id: 'E003',
      path: '/api/v1/search',
      method: 'POST',
      errorCount: 65,
      errorRate: '0.9%',
      lastError: '搜索超时',
      lastTime: '8分钟前',
    },
  ];

  const getMethodColor = (method: string) => {
    switch (method) {
      case 'GET':
        return 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400';
      case 'POST':
        return 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400';
      case 'PUT':
        return 'bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-400';
      case 'DELETE':
        return 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400';
      default:
        return 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-400';
    }
  };

  const getStatusColor = (status: number) => {
    if (status >= 200 && status < 300) {
      return 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400';
    } else if (status >= 400 && status < 500) {
      return 'bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-400';
    } else if (status >= 500) {
      return 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400';
    }
    return 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-400';
  };

  const getResponseTimeColor = (time: number) => {
    if (time < 200) return 'text-green-600 dark:text-green-400';
    if (time < 500) return 'text-yellow-600 dark:text-yellow-400';
    return 'text-red-600 dark:text-red-400';
  };

  const filteredCalls = recentCalls.filter(call =>
    call.path.toLowerCase().includes(searchQuery.toLowerCase()) ||
    call.method.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="space-y-6">
      {/* 页面标题 */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl dark:text-white mb-2">接口监控</h2>
          <p className="text-sm text-gray-500 dark:text-gray-400">
            实时监控API接口调用情况和性能指标
          </p>
        </div>
        <div className="flex items-center gap-3">
          <div className="flex items-center gap-2 text-sm text-gray-500 dark:text-gray-400">
            <Clock className="w-4 h-4" />
            {currentTime.toLocaleString('zh-CN')}
          </div>
          <Button
            variant="outline"
            size="sm"
            onClick={() => setAutoRefresh(!autoRefresh)}
            className={autoRefresh ? 'border-blue-500 text-blue-600 dark:border-blue-400 dark:text-blue-400' : ''}
          >
            <RefreshCw className={`w-4 h-4 mr-2 ${autoRefresh ? 'animate-spin' : ''}`} />
            {autoRefresh ? '自动刷新' : '手动刷新'}
          </Button>
        </div>
      </div>

      {/* 统计卡片 */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {stats.map((stat) => (
          <Card key={stat.name} className="p-4 dark:bg-gray-800 dark:border-gray-700">
            <div className="flex items-start justify-between mb-3">
              <div className={`p-2 rounded-lg bg-${stat.color}-100 dark:bg-${stat.color}-900/30`}>
                <stat.icon className={`w-5 h-5 text-${stat.color}-600 dark:text-${stat.color}-400`} />
              </div>
              <div className="flex items-center gap-1 text-xs">
                {stat.trend === 'up' ? (
                  <TrendingUp className={`w-3 h-3 ${stat.name.includes('错误') || stat.name.includes('响应时间') ? 'text-red-500' : 'text-green-500'}`} />
                ) : (
                  <TrendingUp className={`w-3 h-3 rotate-180 ${stat.name.includes('错误') || stat.name.includes('响应时间') ? 'text-green-500' : 'text-red-500'}`} />
                )}
                <span className={stat.name.includes('错误') || stat.name.includes('响应时间') ? (stat.trend === 'up' ? 'text-red-500' : 'text-green-500') : (stat.trend === 'up' ? 'text-green-500' : 'text-red-500')}>
                  {stat.change}
                </span>
              </div>
            </div>
            <div>
              <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">{stat.name}</p>
              <span className="text-2xl dark:text-white">{stat.value}</span>
            </div>
          </Card>
        ))}
      </div>

      {/* 监控图表 */}
      <Tabs defaultValue="calls" className="space-y-4">
        <TabsList className="dark:bg-gray-800">
          <TabsTrigger value="calls">
            <BarChart3 className="w-4 h-4 mr-2" />
            调用趋势
          </TabsTrigger>
          <TabsTrigger value="response">
            <Clock className="w-4 h-4 mr-2" />
            响应时间
          </TabsTrigger>
          <TabsTrigger value="status">
            <Activity className="w-4 h-4 mr-2" />
            状态分布
          </TabsTrigger>
        </TabsList>

        <TabsContent value="calls">
          <Card className="p-6 dark:bg-gray-800 dark:border-gray-700">
            <div className="mb-4">
              <h3 className="text-base dark:text-white mb-1">接口调用趋势</h3>
              <p className="text-sm text-gray-500 dark:text-gray-400">最近24小时接口调用统计</p>
            </div>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={callTrendData}>
                <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
                <XAxis dataKey="time" stroke="#9CA3AF" style={{ fontSize: '12px' }} />
                <YAxis stroke="#9CA3AF" style={{ fontSize: '12px' }} />
                <Tooltip
                  contentStyle={{
                    backgroundColor: '#1F2937',
                    border: '1px solid #374151',
                    borderRadius: '8px',
                    color: '#fff',
                  }}
                />
                <Bar dataKey="success" fill="#10B981" name="成功" stackId="a" />
                <Bar dataKey="error" fill="#EF4444" name="失败" stackId="a" />
              </BarChart>
            </ResponsiveContainer>
          </Card>
        </TabsContent>

        <TabsContent value="response">
          <Card className="p-6 dark:bg-gray-800 dark:border-gray-700">
            <div className="mb-4">
              <h3 className="text-base dark:text-white mb-1">响应时间趋势</h3>
              <p className="text-sm text-gray-500 dark:text-gray-400">最近24小时平均响应时间和分位数</p>
            </div>
            <ResponsiveContainer width="100%" height={300}>
              <LineChart data={responseTimeData}>
                <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
                <XAxis dataKey="time" stroke="#9CA3AF" style={{ fontSize: '12px' }} />
                <YAxis stroke="#9CA3AF" style={{ fontSize: '12px' }} />
                <Tooltip
                  contentStyle={{
                    backgroundColor: '#1F2937',
                    border: '1px solid #374151',
                    borderRadius: '8px',
                    color: '#fff',
                  }}
                />
                <Line type="monotone" dataKey="avg" stroke="#3B82F6" strokeWidth={2} name="平均值" />
                <Line type="monotone" dataKey="p95" stroke="#F59E0B" strokeWidth={2} name="P95" />
                <Line type="monotone" dataKey="p99" stroke="#EF4444" strokeWidth={2} name="P99" />
              </LineChart>
            </ResponsiveContainer>
          </Card>
        </TabsContent>

        <TabsContent value="status">
          <Card className="p-6 dark:bg-gray-800 dark:border-gray-700">
            <div className="mb-4">
              <h3 className="text-base dark:text-white mb-1">HTTP状态码分布</h3>
              <p className="text-sm text-gray-500 dark:text-gray-400">各状态码的调用占比</p>
            </div>
            <div className="flex items-center justify-center">
              <ResponsiveContainer width="100%" height={300}>
                <PieChart>
                  <Pie
                    data={statusDistribution}
                    cx="50%"
                    cy="50%"
                    labelLine={false}
                    label={({ name, percent }) => `${name} ${(percent * 100).toFixed(1)}%`}
                    outerRadius={100}
                    fill="#8884d8"
                    dataKey="value"
                  >
                    {statusDistribution.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={entry.color} />
                    ))}
                  </Pie>
                  <Tooltip
                    contentStyle={{
                      backgroundColor: '#1F2937',
                      border: '1px solid #374151',
                      borderRadius: '8px',
                      color: '#fff',
                    }}
                  />
                </PieChart>
              </ResponsiveContainer>
            </div>
          </Card>
        </TabsContent>
      </Tabs>

      {/* 接口性能排行 */}
      <Card className="dark:bg-gray-800 dark:border-gray-700">
        <div className="p-6 border-b border-gray-200 dark:border-gray-700">
          <h3 className="text-base dark:text-white mb-1">接口性能排行</h3>
          <p className="text-sm text-gray-500 dark:text-gray-400">按调用次数排序的Top接口</p>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-gray-50 dark:bg-gray-900/50">
              <tr>
                <th className="px-6 py-3 text-left text-xs text-gray-500 dark:text-gray-400">接口路径</th>
                <th className="px-6 py-3 text-left text-xs text-gray-500 dark:text-gray-400">方法</th>
                <th className="px-6 py-3 text-left text-xs text-gray-500 dark:text-gray-400">调用次数</th>
                <th className="px-6 py-3 text-left text-xs text-gray-500 dark:text-gray-400">平均响应时间</th>
                <th className="px-6 py-3 text-left text-xs text-gray-500 dark:text-gray-400">成功率</th>
                <th className="px-6 py-3 text-left text-xs text-gray-500 dark:text-gray-400">错误数</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-200 dark:divide-gray-700">
              {topInterfaces.map((api) => (
                <tr key={api.id} className="hover:bg-gray-50 dark:hover:bg-gray-900/50">
                  <td className="px-6 py-4">
                    <span className="text-sm dark:text-white font-mono">{api.path}</span>
                  </td>
                  <td className="px-6 py-4">
                    <Badge className={`${getMethodColor(api.method)} border-0`}>
                      {api.method}
                    </Badge>
                  </td>
                  <td className="px-6 py-4">
                    <span className="text-sm dark:text-white">{api.calls.toLocaleString()}</span>
                  </td>
                  <td className="px-6 py-4">
                    <span className={`text-sm ${getResponseTimeColor(api.avgTime)}`}>
                      {api.avgTime}ms
                    </span>
                  </td>
                  <td className="px-6 py-4">
                    <span className={`text-sm ${api.successRate >= 99 ? 'text-green-600 dark:text-green-400' : api.successRate >= 95 ? 'text-yellow-600 dark:text-yellow-400' : 'text-red-600 dark:text-red-400'}`}>
                      {api.successRate}%
                    </span>
                  </td>
                  <td className="px-6 py-4">
                    <span className="text-sm text-gray-600 dark:text-gray-400">{api.errorCount}</span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </Card>

      {/* 异常接口 */}
      {errorInterfaces.length > 0 && (
        <Card className="dark:bg-gray-800 dark:border-gray-700">
          <div className="p-6 border-b border-gray-200 dark:border-gray-700">
            <div className="flex items-center gap-2">
              <AlertTriangle className="w-5 h-5 text-red-600 dark:text-red-400" />
              <h3 className="text-base dark:text-white">异常接口监控</h3>
              <Badge className="bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400 border-0">
                {errorInterfaces.length}
              </Badge>
            </div>
          </div>
          <div className="divide-y divide-gray-200 dark:divide-gray-700">
            {errorInterfaces.map((api) => (
              <div key={api.id} className="p-6 hover:bg-gray-50 dark:hover:bg-gray-900/50">
                <div className="flex items-start justify-between mb-2">
                  <div className="flex items-center gap-2">
                    <Badge className={`${getMethodColor(api.method)} border-0`}>
                      {api.method}
                    </Badge>
                    <span className="text-sm dark:text-white font-mono">{api.path}</span>
                  </div>
                  <Badge className="bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400 border-0">
                    错误率: {api.errorRate}
                  </Badge>
                </div>
                <div className="flex items-center gap-6 text-sm text-gray-600 dark:text-gray-400">
                  <span>错误次数: {api.errorCount}</span>
                  <span>最后错误: {api.lastError}</span>
                  <span>发生时间: {api.lastTime}</span>
                </div>
              </div>
            ))}
          </div>
        </Card>
      )}

      {/* 实时调用日志 */}
      <Card className="dark:bg-gray-800 dark:border-gray-700">
        <div className="p-6 border-b border-gray-200 dark:border-gray-700">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h3 className="text-base dark:text-white mb-1">实时调用日志</h3>
              <p className="text-sm text-gray-500 dark:text-gray-400">最新的接口调用记录</p>
            </div>
          </div>
          <div className="flex items-center gap-3">
            <div className="flex-1 relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
              <Input
                placeholder="搜索接口路径或方法..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="pl-10 dark:bg-gray-900 dark:border-gray-700"
              />
            </div>
            <Select value={timeRange} onValueChange={setTimeRange}>
              <SelectTrigger className="w-[180px] dark:bg-gray-900 dark:border-gray-700">
                <SelectValue />
              </SelectTrigger>
              <SelectContent className="dark:bg-gray-800 dark:border-gray-700">
                <SelectItem value="1h">最近1小时</SelectItem>
                <SelectItem value="6h">最近6小时</SelectItem>
                <SelectItem value="24h">最近24小时</SelectItem>
                <SelectItem value="7d">最近7天</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-gray-50 dark:bg-gray-900/50">
              <tr>
                <th className="px-6 py-3 text-left text-xs text-gray-500 dark:text-gray-400">时间</th>
                <th className="px-6 py-3 text-left text-xs text-gray-500 dark:text-gray-400">接口路径</th>
                <th className="px-6 py-3 text-left text-xs text-gray-500 dark:text-gray-400">方法</th>
                <th className="px-6 py-3 text-left text-xs text-gray-500 dark:text-gray-400">状态</th>
                <th className="px-6 py-3 text-left text-xs text-gray-500 dark:text-gray-400">响应时间</th>
                <th className="px-6 py-3 text-left text-xs text-gray-500 dark:text-gray-400">来源IP</th>
                <th className="px-6 py-3 text-left text-xs text-gray-500 dark:text-gray-400">错误信息</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-200 dark:divide-gray-700">
              {filteredCalls.map((call) => (
                <tr key={call.id} className="hover:bg-gray-50 dark:hover:bg-gray-900/50">
                  <td className="px-6 py-4">
                    <span className="text-sm text-gray-600 dark:text-gray-400">{call.time}</span>
                  </td>
                  <td className="px-6 py-4">
                    <span className="text-sm dark:text-white font-mono">{call.path}</span>
                  </td>
                  <td className="px-6 py-4">
                    <Badge className={`${getMethodColor(call.method)} border-0`}>
                      {call.method}
                    </Badge>
                  </td>
                  <td className="px-6 py-4">
                    <Badge className={`${getStatusColor(call.status)} border-0`}>
                      {call.status === 200 && <CheckCircle className="w-3 h-3 mr-1" />}
                      {call.status >= 400 && <XCircle className="w-3 h-3 mr-1" />}
                      {call.status}
                    </Badge>
                  </td>
                  <td className="px-6 py-4">
                    <span className={`text-sm ${getResponseTimeColor(call.responseTime)}`}>
                      {call.responseTime}ms
                    </span>
                  </td>
                  <td className="px-6 py-4">
                    <span className="text-sm text-gray-600 dark:text-gray-400">{call.ip}</span>
                  </td>
                  <td className="px-6 py-4">
                    {call.error ? (
                      <span className="text-sm text-red-600 dark:text-red-400">{call.error}</span>
                    ) : (
                      <span className="text-sm text-gray-400">-</span>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </Card>
    </div>
  );
}
