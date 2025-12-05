import { useState } from 'react';
import { Server, Search, RefreshCw, Activity, CheckCircle2, XCircle, AlertCircle, Clock, ChevronDown, ChevronRight, Eye, Settings, Save, TestTube2 } from 'lucide-react';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Switch } from '@/components/ui/switch';
import { toast } from 'sonner';

interface ServiceInstance {
  instanceId: string;
  hostName: string;
  ipAddr: string;
  port: number;
  securePort: number;
  status: 'UP' | 'DOWN' | 'OUT_OF_SERVICE' | 'UNKNOWN';
  healthCheckUrl: string;
  statusPageUrl: string;
  homePageUrl: string;
  lastHeartbeat: string;
  uptime: string;
  metadata: { [key: string]: string };
}

interface Service {
  serviceName: string;
  displayName: string;
  instances: ServiceInstance[];
  expanded?: boolean;
}

interface EurekaConfig {
  serviceUrl: string;
  enableAuth: boolean;
  username: string;
  password: string;
  syncInterval: number;
  enableSsl: boolean;
  connectTimeout: number;
  readTimeout: number;
}

export function ServiceManagement() {
  const [activeTab, setActiveTab] = useState<'services' | 'config'>('services');
  
  const [services, setServices] = useState<Service[]>([
    {
      serviceName: 'ANGUS-GM-SERVICE',
      displayName: 'AngusGM全局管理服务',
      expanded: true,
      instances: [
        {
          instanceId: 'angus-gm-service-001',
          hostName: 'gm-server-01',
          ipAddr: '192.168.1.101',
          port: 8080,
          securePort: 8443,
          status: 'UP',
          healthCheckUrl: 'http://192.168.1.101:8080/actuator/health',
          statusPageUrl: 'http://192.168.1.101:8080/actuator/info',
          homePageUrl: 'http://192.168.1.101:8080/',
          lastHeartbeat: '2024-12-03 14:32:15',
          uptime: '15天 6小时 23分钟',
          metadata: { version: '1.0.0', zone: 'beijing', profile: 'prod' },
        },
        {
          instanceId: 'angus-gm-service-002',
          hostName: 'gm-server-02',
          ipAddr: '192.168.1.102',
          port: 8080,
          securePort: 8443,
          status: 'UP',
          healthCheckUrl: 'http://192.168.1.102:8080/actuator/health',
          statusPageUrl: 'http://192.168.1.102:8080/actuator/info',
          homePageUrl: 'http://192.168.1.102:8080/',
          lastHeartbeat: '2024-12-03 14:32:18',
          uptime: '10天 3小时 45分钟',
          metadata: { version: '1.0.0', zone: 'beijing', profile: 'prod' },
        },
      ],
    },
    {
      serviceName: 'ANGUS-AUTH-SERVICE',
      displayName: 'AngusAI认证服务',
      expanded: false,
      instances: [
        {
          instanceId: 'angus-auth-service-001',
          hostName: 'auth-server-01',
          ipAddr: '192.168.1.201',
          port: 9090,
          securePort: 9443,
          status: 'UP',
          healthCheckUrl: 'http://192.168.1.201:9090/actuator/health',
          statusPageUrl: 'http://192.168.1.201:9090/actuator/info',
          homePageUrl: 'http://192.168.1.201:9090/',
          lastHeartbeat: '2024-12-03 14:32:10',
          uptime: '22天 12小时 8分钟',
          metadata: { version: '2.1.0', zone: 'beijing', profile: 'prod' },
        },
      ],
    },
    {
      serviceName: 'ANGUS-USER-SERVICE',
      displayName: '用户管理服务',
      expanded: false,
      instances: [
        {
          instanceId: 'angus-user-service-001',
          hostName: 'user-server-01',
          ipAddr: '192.168.1.301',
          port: 8081,
          securePort: 8444,
          status: 'UP',
          healthCheckUrl: 'http://192.168.1.301:8081/actuator/health',
          statusPageUrl: 'http://192.168.1.301:8081/actuator/info',
          homePageUrl: 'http://192.168.1.301:8081/',
          lastHeartbeat: '2024-12-03 14:32:12',
          uptime: '8天 18小时 35分钟',
          metadata: { version: '1.5.2', zone: 'beijing', profile: 'prod' },
        },
        {
          instanceId: 'angus-user-service-002',
          hostName: 'user-server-02',
          ipAddr: '192.168.1.302',
          port: 8081,
          securePort: 8444,
          status: 'DOWN',
          healthCheckUrl: 'http://192.168.1.302:8081/actuator/health',
          statusPageUrl: 'http://192.168.1.302:8081/actuator/info',
          homePageUrl: 'http://192.168.1.302:8081/',
          lastHeartbeat: '2024-12-03 14:28:45',
          uptime: '0天 0小时 0分钟',
          metadata: { version: '1.5.2', zone: 'beijing', profile: 'prod' },
        },
      ],
    },
    {
      serviceName: 'ANGUS-GATEWAY-SERVICE',
      displayName: 'API网关服务',
      expanded: false,
      instances: [
        {
          instanceId: 'angus-gateway-service-001',
          hostName: 'gateway-server-01',
          ipAddr: '192.168.1.10',
          port: 80,
          securePort: 443,
          status: 'UP',
          healthCheckUrl: 'http://192.168.1.10:80/actuator/health',
          statusPageUrl: 'http://192.168.1.10:80/actuator/info',
          homePageUrl: 'http://192.168.1.10:80/',
          lastHeartbeat: '2024-12-03 14:32:20',
          uptime: '30天 5小时 12分钟',
          metadata: { version: '3.0.1', zone: 'beijing', profile: 'prod' },
        },
      ],
    },
    {
      serviceName: 'ANGUS-NOTIFICATION-SERVICE',
      displayName: '消息通知服务',
      expanded: false,
      instances: [
        {
          instanceId: 'angus-notification-service-001',
          hostName: 'notification-server-01',
          ipAddr: '192.168.1.401',
          port: 8082,
          securePort: 8445,
          status: 'OUT_OF_SERVICE',
          healthCheckUrl: 'http://192.168.1.401:8082/actuator/health',
          statusPageUrl: 'http://192.168.1.401:8082/actuator/info',
          homePageUrl: 'http://192.168.1.401:8082/',
          lastHeartbeat: '2024-12-03 14:20:00',
          uptime: '0天 0小时 0分钟',
          metadata: { version: '1.2.0', zone: 'beijing', profile: 'prod' },
        },
      ],
    },
  ]);

  const [eurekaConfig, setEurekaConfig] = useState<EurekaConfig>({
    serviceUrl: 'http://eureka-server:8761/eureka/',
    enableAuth: true,
    username: 'admin',
    password: '********',
    syncInterval: 30,
    enableSsl: false,
    connectTimeout: 5000,
    readTimeout: 10000,
  });

  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState('all');
  const [selectedInstance, setSelectedInstance] = useState<ServiceInstance | null>(null);
  const [selectedServiceName, setSelectedServiceName] = useState<string>('');
  const [showDetailDialog, setShowDetailDialog] = useState(false);
  const [testingConnection, setTestingConnection] = useState(false);

  // 统计数据
  const totalServices = services.length;
  const totalInstances = services.reduce((sum, service) => sum + service.instances.length, 0);
  const upInstances = services.reduce(
    (sum, service) => sum + service.instances.filter((i) => i.status === 'UP').length,
    0
  );
  const downInstances = services.reduce(
    (sum, service) => sum + service.instances.filter((i) => i.status === 'DOWN').length,
    0
  );
  const outOfServiceInstances = services.reduce(
    (sum, service) => sum + service.instances.filter((i) => i.status === 'OUT_OF_SERVICE').length,
    0
  );

  // 获取状态标签
  const getStatusBadge = (status: string) => {
    const configs = {
      UP: {
        className: 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400',
        icon: <CheckCircle2 className="w-3 h-3" />,
        label: '运行中',
      },
      DOWN: {
        className: 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400',
        icon: <XCircle className="w-3 h-3" />,
        label: '已停止',
      },
      OUT_OF_SERVICE: {
        className: 'bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-400',
        icon: <AlertCircle className="w-3 h-3" />,
        label: '维护中',
      },
      UNKNOWN: {
        className: 'bg-gray-100 text-gray-700 dark:bg-gray-700 dark:text-gray-400',
        icon: <AlertCircle className="w-3 h-3" />,
        label: '未知',
      },
    };
    const config = configs[status as keyof typeof configs] || configs.UNKNOWN;
    return (
      <Badge className={`${config.className} border-0 flex items-center gap-1`}>
        {config.icon}
        {config.label}
      </Badge>
    );
  };

  // 切换服务展开/收起
  const toggleService = (serviceName: string) => {
    setServices(
      services.map((service) =>
        service.serviceName === serviceName
          ? { ...service, expanded: !service.expanded }
          : service
      )
    );
  };

  // 刷新服务列表
  const handleRefresh = () => {
    toast.success('服务列表已刷新');
  };

  // 查看实例详情
  const handleViewDetail = (instance: ServiceInstance, serviceName: string) => {
    setSelectedInstance(instance);
    setSelectedServiceName(serviceName);
    setShowDetailDialog(true);
  };

  // 保存Eureka配置
  const handleSaveConfig = () => {
    toast.success('Eureka配置已保存');
  };

  // 测试连接
  const handleTestConnection = async () => {
    setTestingConnection(true);
    setTimeout(() => {
      setTestingConnection(false);
      toast.success('连接测试成功');
    }, 2000);
  };

  // 过滤服务
  const filteredServices = services.filter((service) => {
    const matchSearch =
      service.serviceName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      service.displayName.toLowerCase().includes(searchQuery.toLowerCase());

    if (statusFilter === 'all') return matchSearch;

    const hasMatchingStatus = service.instances.some((instance) => {
      if (statusFilter === 'UP') return instance.status === 'UP';
      if (statusFilter === 'DOWN') return instance.status === 'DOWN';
      if (statusFilter === 'OUT_OF_SERVICE') return instance.status === 'OUT_OF_SERVICE';
      return false;
    });

    return matchSearch && hasMatchingStatus;
  });

  return (
    <div className="space-y-6">
      {/* 页面标题 */}
      <div className="flex items-center justify-between">
        <div>
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-gradient-to-br from-blue-500 to-blue-600 rounded-lg flex items-center justify-center">
              <Server className="w-6 h-6 text-white" />
            </div>
            <div>
              <h1 className="text-2xl dark:text-white">服务管理</h1>
              <p className="text-sm text-gray-500 dark:text-gray-400">Eureka注册中心实例信息</p>
            </div>
          </div>
        </div>
        {activeTab === 'services' && (
          <Button onClick={handleRefresh} className="bg-blue-600 hover:bg-blue-700">
            <RefreshCw className="w-4 h-4 mr-2" />
            刷新
          </Button>
        )}
      </div>

      {/* Tab切换 */}
      <Card className="dark:bg-gray-800 dark:border-gray-700">
        <div className="flex border-b dark:border-gray-700">
          <button
            onClick={() => setActiveTab('services')}
            className={`flex items-center gap-2 px-6 py-3 text-sm transition-colors ${
              activeTab === 'services'
                ? 'text-blue-600 dark:text-blue-400 border-b-2 border-blue-600 dark:border-blue-400'
                : 'text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-gray-200'
            }`}
          >
            <Server className="w-4 h-4" />
            服务列表
          </button>
          <button
            onClick={() => setActiveTab('config')}
            className={`flex items-center gap-2 px-6 py-3 text-sm transition-colors ${
              activeTab === 'config'
                ? 'text-blue-600 dark:text-blue-400 border-b-2 border-blue-600 dark:border-blue-400'
                : 'text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-gray-200'
            }`}
          >
            <Settings className="w-4 h-4" />
            Eureka配置
          </button>
        </div>
      </Card>

      {/* 服务列表Tab */}
      {activeTab === 'services' && (
        <>
          {/* 统计卡片 */}
          <div className="grid grid-cols-1 md:grid-cols-5 gap-4">
            <Card className="dark:bg-gray-800 dark:border-gray-700 p-4">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-500 dark:text-gray-400">总服务数</p>
                  <p className="text-2xl mt-2 dark:text-white">{totalServices}</p>
                </div>
                <div className="w-12 h-12 bg-blue-100 dark:bg-blue-900/30 rounded-lg flex items-center justify-center">
                  <Server className="w-6 h-6 text-blue-600 dark:text-blue-400" />
                </div>
              </div>
            </Card>

            <Card className="dark:bg-gray-800 dark:border-gray-700 p-4">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-500 dark:text-gray-400">总实例数</p>
                  <p className="text-2xl mt-2 dark:text-white">{totalInstances}</p>
                </div>
                <div className="w-12 h-12 bg-purple-100 dark:bg-purple-900/30 rounded-lg flex items-center justify-center">
                  <Activity className="w-6 h-6 text-purple-600 dark:text-purple-400" />
                </div>
              </div>
            </Card>

            <Card className="dark:bg-gray-800 dark:border-gray-700 p-4">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-500 dark:text-gray-400">运行中</p>
                  <p className="text-2xl mt-2 dark:text-white">{upInstances}</p>
                </div>
                <div className="w-12 h-12 bg-green-100 dark:bg-green-900/30 rounded-lg flex items-center justify-center">
                  <CheckCircle2 className="w-6 h-6 text-green-600 dark:text-green-400" />
                </div>
              </div>
            </Card>

            <Card className="dark:bg-gray-800 dark:border-gray-700 p-4">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-500 dark:text-gray-400">已停止</p>
                  <p className="text-2xl mt-2 dark:text-white">{downInstances}</p>
                </div>
                <div className="w-12 h-12 bg-red-100 dark:bg-red-900/30 rounded-lg flex items-center justify-center">
                  <XCircle className="w-6 h-6 text-red-600 dark:text-red-400" />
                </div>
              </div>
            </Card>

            <Card className="dark:bg-gray-800 dark:border-gray-700 p-4">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-500 dark:text-gray-400">维护中</p>
                  <p className="text-2xl mt-2 dark:text-white">{outOfServiceInstances}</p>
                </div>
                <div className="w-12 h-12 bg-orange-100 dark:bg-orange-900/30 rounded-lg flex items-center justify-center">
                  <AlertCircle className="w-6 h-6 text-orange-600 dark:text-orange-400" />
                </div>
              </div>
            </Card>
          </div>

          {/* 筛选工具栏 */}
          <Card className="dark:bg-gray-800 dark:border-gray-700 p-4">
            <div className="flex items-center gap-4">
              <div className="flex-1 relative">
                <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
                <Input
                  placeholder="搜索服务名称..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="pl-10 dark:bg-gray-900 dark:border-gray-700"
                />
              </div>
              <div className="flex items-center gap-2">
                <Button
                  variant={statusFilter === 'all' ? 'default' : 'outline'}
                  size="sm"
                  onClick={() => setStatusFilter('all')}
                  className={statusFilter === 'all' ? 'bg-blue-600' : 'dark:border-gray-600'}
                >
                  全部
                </Button>
                <Button
                  variant={statusFilter === 'UP' ? 'default' : 'outline'}
                  size="sm"
                  onClick={() => setStatusFilter('UP')}
                  className={statusFilter === 'UP' ? 'bg-green-600 hover:bg-green-700' : 'dark:border-gray-600'}
                >
                  运行中
                </Button>
                <Button
                  variant={statusFilter === 'DOWN' ? 'default' : 'outline'}
                  size="sm"
                  onClick={() => setStatusFilter('DOWN')}
                  className={statusFilter === 'DOWN' ? 'bg-red-600 hover:bg-red-700' : 'dark:border-gray-600'}
                >
                  已停止
                </Button>
                <Button
                  variant={statusFilter === 'OUT_OF_SERVICE' ? 'default' : 'outline'}
                  size="sm"
                  onClick={() => setStatusFilter('OUT_OF_SERVICE')}
                  className={statusFilter === 'OUT_OF_SERVICE' ? 'bg-orange-600 hover:bg-orange-700' : 'dark:border-gray-600'}
                >
                  维护中
                </Button>
              </div>
            </div>
          </Card>

          {/* 服务列表 */}
          <div className="space-y-3">
            {filteredServices.map((service) => (
              <Card key={service.serviceName} className="dark:bg-gray-800 dark:border-gray-700">
                {/* 服务头部 */}
                <div
                  className="p-4 flex items-center justify-between cursor-pointer hover:bg-gray-50 dark:hover:bg-gray-700/50"
                  onClick={() => toggleService(service.serviceName)}
                >
                  <div className="flex items-center gap-3 flex-1">
                    <div>
                      {service.expanded ? (
                        <ChevronDown className="w-5 h-5 text-gray-500" />
                      ) : (
                        <ChevronRight className="w-5 h-5 text-gray-500" />
                      )}
                    </div>
                    <Server className="w-5 h-5 text-blue-600 dark:text-blue-400" />
                    <div className="flex-1">
                      <div className="flex items-center gap-3">
                        <h3 className="dark:text-white">{service.displayName}</h3>
                        <code className="text-sm text-gray-500 dark:text-gray-400 bg-gray-100 dark:bg-gray-700 px-2 py-0.5 rounded">
                          {service.serviceName}
                        </code>
                      </div>
                      <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
                        {service.instances.length} 个实例
                      </p>
                    </div>
                  </div>
                  <div className="flex items-center gap-2">
                    {service.instances.filter((i) => i.status === 'UP').length > 0 && (
                      <Badge className="bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400 border-0">
                        {service.instances.filter((i) => i.status === 'UP').length} UP
                      </Badge>
                    )}
                    {service.instances.filter((i) => i.status === 'DOWN').length > 0 && (
                      <Badge className="bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400 border-0">
                        {service.instances.filter((i) => i.status === 'DOWN').length} DOWN
                      </Badge>
                    )}
                    {service.instances.filter((i) => i.status === 'OUT_OF_SERVICE').length > 0 && (
                      <Badge className="bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-400 border-0">
                        {service.instances.filter((i) => i.status === 'OUT_OF_SERVICE').length} 维护
                      </Badge>
                    )}
                  </div>
                </div>

                {/* 实例列表 */}
                {service.expanded && (
                  <div className="border-t dark:border-gray-700">
                    <div className="overflow-x-auto">
                      <table className="w-full">
                        <thead className="bg-gray-50 dark:bg-gray-900/50">
                          <tr>
                            <th className="text-left p-3 text-sm text-gray-600 dark:text-gray-400">实例ID</th>
                            <th className="text-left p-3 text-sm text-gray-600 dark:text-gray-400">主机名</th>
                            <th className="text-left p-3 text-sm text-gray-600 dark:text-gray-400">IP地址</th>
                            <th className="text-left p-3 text-sm text-gray-600 dark:text-gray-400">端口</th>
                            <th className="text-left p-3 text-sm text-gray-600 dark:text-gray-400">状态</th>
                            <th className="text-left p-3 text-sm text-gray-600 dark:text-gray-400">最后心跳</th>
                            <th className="text-left p-3 text-sm text-gray-600 dark:text-gray-400">运行时长</th>
                            <th className="text-left p-3 text-sm text-gray-600 dark:text-gray-400">版本</th>
                            <th className="text-left p-3 text-sm text-gray-600 dark:text-gray-400">操作</th>
                          </tr>
                        </thead>
                        <tbody>
                          {service.instances.map((instance) => (
                            <tr
                              key={instance.instanceId}
                              className="border-t dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-700/50"
                            >
                              <td className="p-3">
                                <code className="text-xs text-gray-600 dark:text-gray-400">
                                  {instance.instanceId}
                                </code>
                              </td>
                              <td className="p-3">
                                <span className="dark:text-white">{instance.hostName}</span>
                              </td>
                              <td className="p-3">
                                <code className="text-sm dark:text-gray-300">{instance.ipAddr}</code>
                              </td>
                              <td className="p-3">
                                <div className="text-sm dark:text-gray-300">
                                  <div>{instance.port}</div>
                                  {instance.securePort > 0 && (
                                    <div className="text-xs text-gray-500">SSL: {instance.securePort}</div>
                                  )}
                                </div>
                              </td>
                              <td className="p-3">{getStatusBadge(instance.status)}</td>
                              <td className="p-3">
                                <div className="flex items-center gap-1 text-sm dark:text-gray-300">
                                  <Clock className="w-3 h-3 text-gray-400" />
                                  {instance.lastHeartbeat}
                                </div>
                              </td>
                              <td className="p-3">
                                <span className="text-sm dark:text-gray-300">{instance.uptime}</span>
                              </td>
                              <td className="p-3">
                                <Badge className="bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400 border-0 text-xs">
                                  {instance.metadata.version}
                                </Badge>
                              </td>
                              <td className="p-3">
                                <Button
                                  variant="ghost"
                                  size="sm"
                                  onClick={() => handleViewDetail(instance, service.displayName)}
                                  className="dark:hover:bg-gray-600"
                                >
                                  <Eye className="w-4 h-4" />
                                </Button>
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  </div>
                )}
              </Card>
            ))}

            {filteredServices.length === 0 && (
              <Card className="dark:bg-gray-800 dark:border-gray-700 p-12">
                <div className="text-center text-gray-500 dark:text-gray-400">
                  <Server className="w-12 h-12 mx-auto mb-3 opacity-50" />
                  <p>未找到匹配的服务</p>
                </div>
              </Card>
            )}
          </div>
        </>
      )}

      {/* Eureka配置Tab */}
      {activeTab === 'config' && (
        <Card className="dark:bg-gray-800 dark:border-gray-700 p-6">
          <div className="space-y-6">
            <div>
              <h3 className="text-lg mb-4 dark:text-white">Eureka服务器配置</h3>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                {/* 服务地址 */}
                <div className="md:col-span-2">
                  <Label className="dark:text-gray-300">服务地址 *</Label>
                  <Input
                    value={eurekaConfig.serviceUrl}
                    onChange={(e) => setEurekaConfig({ ...eurekaConfig, serviceUrl: e.target.value })}
                    className="mt-2 dark:bg-gray-900 dark:border-gray-700"
                    placeholder="http://eureka-server:8761/eureka/"
                  />
                  <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">
                    Eureka服务器的完整URL地址
                  </p>
                </div>

                {/* 启用SSL */}
                <div className="flex items-center justify-between">
                  <div>
                    <Label className="dark:text-gray-300">启用SSL</Label>
                    <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">
                      使用HTTPS协议连接
                    </p>
                  </div>
                  <Switch
                    checked={eurekaConfig.enableSsl}
                    onCheckedChange={(checked) =>
                      setEurekaConfig({ ...eurekaConfig, enableSsl: checked })
                    }
                  />
                </div>

                {/* 同步间隔 */}
                <div>
                  <Label className="dark:text-gray-300">同步间隔（秒）*</Label>
                  <Select
                    value={eurekaConfig.syncInterval.toString()}
                    onValueChange={(value) =>
                      setEurekaConfig({ ...eurekaConfig, syncInterval: parseInt(value) })
                    }
                  >
                    <SelectTrigger className="mt-2 dark:bg-gray-900 dark:border-gray-700">
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="10">10秒</SelectItem>
                      <SelectItem value="30">30秒</SelectItem>
                      <SelectItem value="60">60秒</SelectItem>
                      <SelectItem value="120">120秒</SelectItem>
                      <SelectItem value="300">300秒</SelectItem>
                    </SelectContent>
                  </Select>
                  <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">
                    自动同步服务列表的时间间隔
                  </p>
                </div>
              </div>
            </div>

            {/* 认证配置 */}
            <div className="pt-6 border-t dark:border-gray-700">
              <div className="flex items-center justify-between mb-4">
                <h3 className="text-lg dark:text-white">认证配置</h3>
                <Switch
                  checked={eurekaConfig.enableAuth}
                  onCheckedChange={(checked) =>
                    setEurekaConfig({ ...eurekaConfig, enableAuth: checked })
                  }
                />
              </div>

              {eurekaConfig.enableAuth && (
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div>
                    <Label className="dark:text-gray-300">用户名 *</Label>
                    <Input
                      value={eurekaConfig.username}
                      onChange={(e) => setEurekaConfig({ ...eurekaConfig, username: e.target.value })}
                      className="mt-2 dark:bg-gray-900 dark:border-gray-700"
                      placeholder="admin"
                    />
                  </div>

                  <div>
                    <Label className="dark:text-gray-300">密码 *</Label>
                    <Input
                      type="password"
                      value={eurekaConfig.password}
                      onChange={(e) => setEurekaConfig({ ...eurekaConfig, password: e.target.value })}
                      className="mt-2 dark:bg-gray-900 dark:border-gray-700"
                      placeholder="••••••••"
                    />
                  </div>
                </div>
              )}
            </div>

            {/* 高级配置 */}
            <div className="pt-6 border-t dark:border-gray-700">
              <h3 className="text-lg mb-4 dark:text-white">高级配置</h3>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <Label className="dark:text-gray-300">连接超时（毫秒）</Label>
                  <Input
                    type="number"
                    value={eurekaConfig.connectTimeout}
                    onChange={(e) =>
                      setEurekaConfig({ ...eurekaConfig, connectTimeout: parseInt(e.target.value) })
                    }
                    className="mt-2 dark:bg-gray-900 dark:border-gray-700"
                    placeholder="5000"
                  />
                  <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">
                    建立连接的最大等待时间
                  </p>
                </div>

                <div>
                  <Label className="dark:text-gray-300">读取超时（毫秒）</Label>
                  <Input
                    type="number"
                    value={eurekaConfig.readTimeout}
                    onChange={(e) =>
                      setEurekaConfig({ ...eurekaConfig, readTimeout: parseInt(e.target.value) })
                    }
                    className="mt-2 dark:bg-gray-900 dark:border-gray-700"
                    placeholder="10000"
                  />
                  <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">
                    读取数据的最大等待时间
                  </p>
                </div>
              </div>
            </div>

            {/* 操作按钮 */}
            <div className="pt-6 border-t dark:border-gray-700 flex items-center gap-3">
              <Button
                onClick={handleTestConnection}
                variant="outline"
                disabled={testingConnection}
                className="dark:border-gray-600"
              >
                <TestTube2 className="w-4 h-4 mr-2" />
                {testingConnection ? '测试中...' : '测试连接'}
              </Button>
              <Button onClick={handleSaveConfig} className="bg-blue-600 hover:bg-blue-700">
                <Save className="w-4 h-4 mr-2" />
                保存配置
              </Button>
            </div>

            {/* 配置说明 */}
            <div className="pt-6 border-t dark:border-gray-700">
              <h4 className="text-sm mb-2 dark:text-gray-300">配置说明</h4>
              <ul className="text-sm text-gray-600 dark:text-gray-400 space-y-1">
                <li>• 服务地址：Eureka服务器的完整URL，支持多个地址用逗号分隔</li>
                <li>• 同步间隔：系统会按此间隔自动同步服务注册信息</li>
                <li>• 认证配置：如果Eureka服务器启用了安全认证，需要提供用户名和密码</li>
                <li>• 连接超时：建议设置为5000-10000毫秒</li>
                <li>• 读取超时：建议设置为10000-30000毫秒</li>
              </ul>
            </div>
          </div>
        </Card>
      )}

      {/* 实例详情对话框 */}
      <Dialog open={showDetailDialog} onOpenChange={setShowDetailDialog}>
        <DialogContent className="max-w-3xl dark:bg-gray-800 dark:border-gray-700" aria-describedby={undefined}>
          <DialogHeader>
            <DialogTitle className="dark:text-white">
              实例详情 - {selectedServiceName}
            </DialogTitle>
          </DialogHeader>

          {selectedInstance && (
            <div className="space-y-6 max-h-[70vh] overflow-y-auto">
              {/* 基本信息 */}
              <div>
                <h3 className="text-sm mb-3 dark:text-gray-300">基本信息</h3>
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <p className="text-sm text-gray-500 dark:text-gray-400">实例ID</p>
                    <code className="text-sm dark:text-white mt-1 block">
                      {selectedInstance.instanceId}
                    </code>
                  </div>
                  <div>
                    <p className="text-sm text-gray-500 dark:text-gray-400">主机名</p>
                    <p className="dark:text-white mt-1">{selectedInstance.hostName}</p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-500 dark:text-gray-400">IP地址</p>
                    <code className="dark:text-white mt-1 block">{selectedInstance.ipAddr}</code>
                  </div>
                  <div>
                    <p className="text-sm text-gray-500 dark:text-gray-400">端口</p>
                    <p className="dark:text-white mt-1">
                      {selectedInstance.port} / {selectedInstance.securePort} (SSL)
                    </p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-500 dark:text-gray-400">状态</p>
                    <div className="mt-1">{getStatusBadge(selectedInstance.status)}</div>
                  </div>
                  <div>
                    <p className="text-sm text-gray-500 dark:text-gray-400">运行时长</p>
                    <p className="dark:text-white mt-1">{selectedInstance.uptime}</p>
                  </div>
                </div>
              </div>

              {/* URL信息 */}
              <div>
                <h3 className="text-sm mb-3 dark:text-gray-300">URL信息</h3>
                <div className="space-y-3">
                  <div>
                    <p className="text-sm text-gray-500 dark:text-gray-400">健康检查URL</p>
                    <a
                      href={selectedInstance.healthCheckUrl}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="text-sm text-blue-600 dark:text-blue-400 hover:underline mt-1 block break-all"
                    >
                      {selectedInstance.healthCheckUrl}
                    </a>
                  </div>
                  <div>
                    <p className="text-sm text-gray-500 dark:text-gray-400">状态页面URL</p>
                    <a
                      href={selectedInstance.statusPageUrl}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="text-sm text-blue-600 dark:text-blue-400 hover:underline mt-1 block break-all"
                    >
                      {selectedInstance.statusPageUrl}
                    </a>
                  </div>
                  <div>
                    <p className="text-sm text-gray-500 dark:text-gray-400">主页URL</p>
                    <a
                      href={selectedInstance.homePageUrl}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="text-sm text-blue-600 dark:text-blue-400 hover:underline mt-1 block break-all"
                    >
                      {selectedInstance.homePageUrl}
                    </a>
                  </div>
                </div>
              </div>

              {/* 元数据 */}
              <div>
                <h3 className="text-sm mb-3 dark:text-gray-300">元数据</h3>
                <div className="bg-gray-50 dark:bg-gray-900 rounded-lg p-4">
                  <pre className="text-xs dark:text-gray-300">
                    {JSON.stringify(selectedInstance.metadata, null, 2)}
                  </pre>
                </div>
              </div>

              {/* 心跳信息 */}
              <div>
                <h3 className="text-sm mb-3 dark:text-gray-300">心跳信息</h3>
                <div className="flex items-center gap-2 text-sm dark:text-gray-300">
                  <Clock className="w-4 h-4 text-gray-400" />
                  最后心跳时间: {selectedInstance.lastHeartbeat}
                </div>
              </div>
            </div>
          )}
        </DialogContent>
      </Dialog>
    </div>
  );
}
