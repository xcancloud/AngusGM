import { Activity, Cpu, HardDrive, Network, Server, AlertCircle, CheckCircle, Clock, RefreshCw, TrendingUp, TrendingDown } from 'lucide-react';
import { Card } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Progress } from '@/components/ui/progress';
import { useState, useEffect } from 'react';
import { LineChart, Line, AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';

export function SystemMonitoring() {
  const [currentTime, setCurrentTime] = useState(new Date());
  const [autoRefresh, setAutoRefresh] = useState(true);

  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentTime(new Date());
    }, 1000);
    return () => clearInterval(timer);
  }, []);

  // Mock real-time data
  const cpuData = [
    { time: '00:00', value: 45 },
    { time: '00:05', value: 52 },
    { time: '00:10', value: 48 },
    { time: '00:15', value: 65 },
    { time: '00:20', value: 58 },
    { time: '00:25', value: 72 },
    { time: '00:30', value: 68 },
  ];

  const memoryData = [
    { time: '00:00', value: 62 },
    { time: '00:05', value: 65 },
    { time: '00:10', value: 68 },
    { time: '00:15', value: 71 },
    { time: '00:20', value: 69 },
    { time: '00:25', value: 74 },
    { time: '00:30', value: 72 },
  ];

  const networkData = [
    { time: '00:00', in: 120, out: 85 },
    { time: '00:05', in: 145, out: 92 },
    { time: '00:10', in: 132, out: 88 },
    { time: '00:15', in: 168, out: 105 },
    { time: '00:20', in: 155, out: 98 },
    { time: '00:25', in: 178, out: 112 },
    { time: '00:30', in: 165, out: 102 },
  ];

  const systemStats = [
    {
      name: 'CPU使用率',
      value: 68,
      icon: Cpu,
      color: 'blue',
      status: 'normal',
      trend: 'up',
      change: '+5%',
    },
    {
      name: '内存使用',
      value: 72,
      icon: Activity,
      color: 'green',
      status: 'normal',
      trend: 'down',
      change: '-2%',
    },
    {
      name: '磁盘使用',
      value: 45,
      icon: HardDrive,
      color: 'purple',
      status: 'normal',
      trend: 'up',
      change: '+1%',
    },
    {
      name: '网络流量',
      value: 165,
      unit: 'MB/s',
      icon: Network,
      color: 'orange',
      status: 'normal',
      trend: 'up',
      change: '+8%',
    },
  ];

  const services = [
    {
      id: 'S001',
      name: 'Web服务器',
      type: 'Nginx',
      status: 'running',
      cpu: '12%',
      memory: '256MB',
      uptime: '15天 8小时',
      port: '80, 443',
    },
    {
      id: 'S002',
      name: '应用服务',
      type: 'Node.js',
      status: 'running',
      cpu: '28%',
      memory: '512MB',
      uptime: '15天 8小时',
      port: '3000',
    },
    {
      id: 'S003',
      name: '数据库',
      type: 'PostgreSQL',
      status: 'running',
      cpu: '18%',
      memory: '1.2GB',
      uptime: '30天 12小时',
      port: '5432',
    },
    {
      id: 'S004',
      name: '缓存服务',
      type: 'Redis',
      status: 'running',
      cpu: '5%',
      memory: '128MB',
      uptime: '30天 12小时',
      port: '6379',
    },
    {
      id: 'S005',
      name: '消息队列',
      type: 'RabbitMQ',
      status: 'running',
      cpu: '8%',
      memory: '256MB',
      uptime: '15天 8小时',
      port: '5672',
    },
    {
      id: 'S006',
      name: '搜索引擎',
      type: 'Elasticsearch',
      status: 'warning',
      cpu: '45%',
      memory: '2.1GB',
      uptime: '7天 3小时',
      port: '9200',
    },
  ];

  const alerts = [
    {
      id: 'A001',
      type: 'warning',
      service: 'Elasticsearch',
      message: 'CPU使用率持续偏高',
      time: '5分钟前',
    },
    {
      id: 'A002',
      type: 'info',
      service: '应用服务',
      message: '请求响应时间略有上升',
      time: '15分钟前',
    },
  ];

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'running':
        return 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400';
      case 'warning':
        return 'bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-400';
      case 'stopped':
        return 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400';
      default:
        return 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-400';
    }
  };

  const getStatusText = (status: string) => {
    switch (status) {
      case 'running':
        return '运行中';
      case 'warning':
        return '警告';
      case 'stopped':
        return '已停止';
      default:
        return '未知';
    }
  };

  const getProgressColor = (value: number) => {
    if (value >= 80) return 'bg-red-500';
    if (value >= 60) return 'bg-yellow-500';
    return 'bg-blue-500';
  };

  return (
    <div className="space-y-6">
      {/* 页面标题 */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl dark:text-white mb-2">系统监控</h2>
          <p className="text-sm text-gray-500 dark:text-gray-400">
            实时监控系统资源使用情况和服务运行状态
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

      {/* 系统资源统计卡片 */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {systemStats.map((stat) => (
          <Card key={stat.name} className="p-4 dark:bg-gray-800 dark:border-gray-700">
            <div className="flex items-start justify-between mb-3">
              <div className={`p-2 rounded-lg bg-${stat.color}-100 dark:bg-${stat.color}-900/30`}>
                <stat.icon className={`w-5 h-5 text-${stat.color}-600 dark:text-${stat.color}-400`} />
              </div>
              <div className="flex items-center gap-1 text-xs">
                {stat.trend === 'up' ? (
                  <TrendingUp className="w-3 h-3 text-red-500" />
                ) : (
                  <TrendingDown className="w-3 h-3 text-green-500" />
                )}
                <span className={stat.trend === 'up' ? 'text-red-500' : 'text-green-500'}>
                  {stat.change}
                </span>
              </div>
            </div>
            <div>
              <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">{stat.name}</p>
              <div className="flex items-baseline gap-2">
                <span className="text-2xl dark:text-white">{stat.value}</span>
                <span className="text-sm text-gray-500 dark:text-gray-400">{stat.unit || '%'}</span>
              </div>
            </div>
            {!stat.unit && (
              <div className="mt-3">
                <Progress value={stat.value} className="h-2" indicatorClassName={getProgressColor(stat.value)} />
              </div>
            )}
          </Card>
        ))}
      </div>

      {/* 告警信息 */}
      {alerts.length > 0 && (
        <Card className="p-4 dark:bg-gray-800 dark:border-gray-700">
          <div className="flex items-center gap-2 mb-3">
            <AlertCircle className="w-5 h-5 text-yellow-600 dark:text-yellow-400" />
            <h3 className="text-base dark:text-white">实时告警</h3>
            <Badge className="bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-400 border-0">
              {alerts.length}
            </Badge>
          </div>
          <div className="space-y-2">
            {alerts.map((alert) => (
              <div
                key={alert.id}
                className="flex items-start gap-3 p-3 rounded-lg bg-yellow-50 dark:bg-yellow-900/10 border border-yellow-200 dark:border-yellow-800"
              >
                <AlertCircle className="w-4 h-4 text-yellow-600 dark:text-yellow-400 mt-0.5" />
                <div className="flex-1">
                  <div className="flex items-center gap-2 mb-1">
                    <span className="text-sm dark:text-white">{alert.service}</span>
                    <span className="text-xs text-gray-500 dark:text-gray-400">{alert.time}</span>
                  </div>
                  <p className="text-sm text-gray-600 dark:text-gray-400">{alert.message}</p>
                </div>
              </div>
            ))}
          </div>
        </Card>
      )}

      {/* 实时监控图表 */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* CPU使用率 */}
        <Card className="p-6 dark:bg-gray-800 dark:border-gray-700">
          <div className="mb-4">
            <h3 className="text-base dark:text-white mb-1">CPU使用率</h3>
            <p className="text-sm text-gray-500 dark:text-gray-400">实时CPU负载监控</p>
          </div>
          <ResponsiveContainer width="100%" height={200}>
            <AreaChart data={cpuData}>
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
              <Area type="monotone" dataKey="value" stroke="#3B82F6" fill="#3B82F6" fillOpacity={0.3} />
            </AreaChart>
          </ResponsiveContainer>
        </Card>

        {/* 内存使用 */}
        <Card className="p-6 dark:bg-gray-800 dark:border-gray-700">
          <div className="mb-4">
            <h3 className="text-base dark:text-white mb-1">内存使用</h3>
            <p className="text-sm text-gray-500 dark:text-gray-400">实时内存占用监控</p>
          </div>
          <ResponsiveContainer width="100%" height={200}>
            <AreaChart data={memoryData}>
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
              <Area type="monotone" dataKey="value" stroke="#10B981" fill="#10B981" fillOpacity={0.3} />
            </AreaChart>
          </ResponsiveContainer>
        </Card>

        {/* 网络流量 */}
        <Card className="p-6 dark:bg-gray-800 dark:border-gray-700 lg:col-span-2">
          <div className="mb-4">
            <h3 className="text-base dark:text-white mb-1">网络流量</h3>
            <p className="text-sm text-gray-500 dark:text-gray-400">实时网络带宽使用情况</p>
          </div>
          <ResponsiveContainer width="100%" height={200}>
            <LineChart data={networkData}>
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
              <Line type="monotone" dataKey="in" stroke="#F59E0B" strokeWidth={2} name="入站" />
              <Line type="monotone" dataKey="out" stroke="#8B5CF6" strokeWidth={2} name="出站" />
            </LineChart>
          </ResponsiveContainer>
        </Card>
      </div>

      {/* 服务状态 */}
      <Card className="dark:bg-gray-800 dark:border-gray-700">
        <div className="p-6 border-b border-gray-200 dark:border-gray-700">
          <div className="flex items-center justify-between">
            <div>
              <h3 className="text-base dark:text-white mb-1">服务状态</h3>
              <p className="text-sm text-gray-500 dark:text-gray-400">当前系统运行的所有服务</p>
            </div>
            <div className="flex items-center gap-2">
              <Badge className="bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400 border-0">
                {services.filter(s => s.status === 'running').length} 正常运行
              </Badge>
              {services.some(s => s.status === 'warning') && (
                <Badge className="bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-400 border-0">
                  {services.filter(s => s.status === 'warning').length} 需要注意
                </Badge>
              )}
            </div>
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-gray-50 dark:bg-gray-900/50">
              <tr>
                <th className="px-6 py-3 text-left text-xs text-gray-500 dark:text-gray-400">服务名称</th>
                <th className="px-6 py-3 text-left text-xs text-gray-500 dark:text-gray-400">类型</th>
                <th className="px-6 py-3 text-left text-xs text-gray-500 dark:text-gray-400">状态</th>
                <th className="px-6 py-3 text-left text-xs text-gray-500 dark:text-gray-400">CPU</th>
                <th className="px-6 py-3 text-left text-xs text-gray-500 dark:text-gray-400">内存</th>
                <th className="px-6 py-3 text-left text-xs text-gray-500 dark:text-gray-400">运行时间</th>
                <th className="px-6 py-3 text-left text-xs text-gray-500 dark:text-gray-400">端口</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-200 dark:divide-gray-700">
              {services.map((service) => (
                <tr key={service.id} className="hover:bg-gray-50 dark:hover:bg-gray-900/50">
                  <td className="px-6 py-4">
                    <div className="flex items-center gap-2">
                      <Server className="w-4 h-4 text-gray-400" />
                      <span className="text-sm dark:text-white">{service.name}</span>
                    </div>
                  </td>
                  <td className="px-6 py-4">
                    <span className="text-sm text-gray-600 dark:text-gray-400">{service.type}</span>
                  </td>
                  <td className="px-6 py-4">
                    <Badge className={`${getStatusColor(service.status)} border-0`}>
                      {service.status === 'running' && <CheckCircle className="w-3 h-3 mr-1" />}
                      {service.status === 'warning' && <AlertCircle className="w-3 h-3 mr-1" />}
                      {getStatusText(service.status)}
                    </Badge>
                  </td>
                  <td className="px-6 py-4">
                    <span className="text-sm text-gray-600 dark:text-gray-400">{service.cpu}</span>
                  </td>
                  <td className="px-6 py-4">
                    <span className="text-sm text-gray-600 dark:text-gray-400">{service.memory}</span>
                  </td>
                  <td className="px-6 py-4">
                    <span className="text-sm text-gray-600 dark:text-gray-400">{service.uptime}</span>
                  </td>
                  <td className="px-6 py-4">
                    <span className="text-sm text-gray-600 dark:text-gray-400">{service.port}</span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </Card>

      {/* 系统信息 */}
      <Card className="p-6 dark:bg-gray-800 dark:border-gray-700">
        <h3 className="text-base dark:text-white mb-4">系统信息</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          <div>
            <p className="text-sm text-gray-500 dark:text-gray-400 mb-1">操作系统</p>
            <p className="text-sm dark:text-white">Ubuntu 22.04 LTS</p>
          </div>
          <div>
            <p className="text-sm text-gray-500 dark:text-gray-400 mb-1">系统版本</p>
            <p className="text-sm dark:text-white">AngusGM v2.5.1</p>
          </div>
          <div>
            <p className="text-sm text-gray-500 dark:text-gray-400 mb-1">运行时间</p>
            <p className="text-sm dark:text-white">30天 12小时 45分钟</p>
          </div>
          <div>
            <p className="text-sm text-gray-500 dark:text-gray-400 mb-1">服务器IP</p>
            <p className="text-sm dark:text-white">192.168.1.100</p>
          </div>
        </div>
      </Card>
    </div>
  );
}
