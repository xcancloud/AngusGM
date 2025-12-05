import { useState } from 'react';
import { Database, HardDrive, Cpu, Users, FileText, Zap, TrendingUp, Edit, Save, RefreshCw, Activity, Server, Network, Package, AlertTriangle } from 'lucide-react';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Badge } from '@/components/ui/badge';
import { Progress } from '@/components/ui/progress';
import { toast } from 'sonner';

interface QuotaItem {
  id: string;
  code: string;
  name: string;
  category: '系统资源' | '用户资源' | '存储资源' | '网络资源';
  icon: any;
  iconColor: string;
  iconBg: string;
  limit: number;
  used: number;
  unit: string;
  description: string;
}

export function ResourceQuota() {
  const [isEditing, setIsEditing] = useState(false);
  const [quotas, setQuotas] = useState<QuotaItem[]>([
    {
      id: 'users',
      code: 'user_count',
      name: '用户数量',
      category: '用户资源',
      icon: Users,
      iconColor: 'text-blue-500',
      iconBg: 'bg-blue-500',
      limit: 1000,
      used: 678,
      unit: '个',
      description: '系统可创建的最大用户数量',
    },
    {
      id: 'storage',
      code: 'storage_space',
      name: '存储空间',
      category: '存储资源',
      icon: HardDrive,
      iconColor: 'text-purple-500',
      iconBg: 'bg-purple-500',
      limit: 1000,
      used: 547,
      unit: 'GB',
      description: '系统总存储空间配额',
    },
    {
      id: 'cpu',
      code: 'cpu_cores',
      name: 'CPU核心',
      category: '系统资源',
      icon: Cpu,
      iconColor: 'text-orange-500',
      iconBg: 'bg-orange-500',
      limit: 32,
      used: 24,
      unit: '核',
      description: '系统可使用的CPU核心数',
    },
    {
      id: 'memory',
      code: 'memory_total',
      name: '内存',
      category: '系统资源',
      icon: Server,
      iconColor: 'text-green-500',
      iconBg: 'bg-green-500',
      limit: 128,
      used: 89,
      unit: 'GB',
      description: '系统可使用的内存总量',
    },
    {
      id: 'apiCalls',
      code: 'api_calls',
      name: 'API调用',
      category: '网络资源',
      icon: Zap,
      iconColor: 'text-yellow-500',
      iconBg: 'bg-yellow-500',
      limit: 10000000,
      used: 7856234,
      unit: '次/月',
      description: '每月API调用次数限制',
    },
    {
      id: 'bandwidth',
      code: 'network_bandwidth',
      name: '网络带宽',
      category: '网络资源',
      icon: Network,
      iconColor: 'text-cyan-500',
      iconBg: 'bg-cyan-500',
      limit: 5000,
      used: 3456,
      unit: 'GB/月',
      description: '每月网络流量配额',
    },
    {
      id: 'projects',
      code: 'project_count',
      name: '项目数量',
      category: '用户资源',
      icon: FileText,
      iconColor: 'text-indigo-500',
      iconBg: 'bg-indigo-500',
      limit: 500,
      used: 342,
      unit: '个',
      description: '系统可创建的最大项目数',
    },
    {
      id: 'databases',
      code: 'database_count',
      name: '数据库',
      category: '存储资源',
      icon: Database,
      iconColor: 'text-pink-500',
      iconBg: 'bg-pink-500',
      limit: 50,
      used: 37,
      unit: '个',
      description: '可创建的数据库实例数量',
    },
    {
      id: 'concurrent',
      code: 'concurrent_connections',
      name: '并发连接',
      category: '系统资源',
      icon: Activity,
      iconColor: 'text-red-500',
      iconBg: 'bg-red-500',
      limit: 10000,
      used: 6543,
      unit: '个',
      description: '系统支持的最大并发连接数',
    },
  ]);

  const [editFormData, setEditFormData] = useState<{ [key: string]: number }>({});

  const getUsagePercentage = (used: number, limit: number) => {
    return limit > 0 ? (used / limit) * 100 : 0;
  };

  const getUsageColor = (percentage: number) => {
    if (percentage >= 90) return 'text-red-600 dark:text-red-400';
    if (percentage >= 75) return 'text-orange-600 dark:text-orange-400';
    return 'text-green-600 dark:text-green-400';
  };

  const getProgressColor = (percentage: number) => {
    if (percentage >= 90) return 'bg-red-500';
    if (percentage >= 75) return 'bg-orange-500';
    return 'bg-blue-500';
  };

  const formatNumber = (num: number) => {
    if (num >= 1000000) return `${(num / 1000000).toFixed(1)}M`;
    if (num >= 1000) return `${(num / 1000).toFixed(1)}K`;
    return num.toString();
  };

  const handleEditClick = () => {
    // 初始化编辑表单数据
    const formData: { [key: string]: number } = {};
    quotas.forEach(quota => {
      formData[quota.id] = quota.limit;
    });
    setEditFormData(formData);
    setIsEditing(true);
  };

  const handleSave = () => {
    // 更新配额
    setQuotas(quotas.map(quota => ({
      ...quota,
      limit: editFormData[quota.id] || quota.limit,
    })));
    setIsEditing(false);
    toast.success('资源配额已更新');
  };

  const handleCancel = () => {
    setIsEditing(false);
    setEditFormData({});
  };

  const updateFormData = (id: string, value: number) => {
    setEditFormData({
      ...editFormData,
      [id]: value,
    });
  };

  // 计算统计数据
  const stats = {
    totalResources: quotas.length,
    appliedQuotas: quotas.filter(q => q.limit > 0).length,
    insufficientQuotas: quotas.filter(q => getUsagePercentage(q.used, q.limit) >= 90).length,
  };

  return (
    <div className="space-y-6">
      {/* 页面标题 */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl dark:text-white mb-2">资源配额</h2>
          <p className="text-sm text-gray-500 dark:text-gray-400">
            管理系统资源配额和使用情况
          </p>
        </div>
        <div className="flex items-center gap-3">
          {isEditing ? (
            <>
              <Button
                variant="outline"
                size="sm"
                onClick={handleCancel}
                className="dark:border-gray-600 dark:hover:bg-gray-700 dark:text-gray-300"
              >
                取消
              </Button>
              <Button
                size="sm"
                onClick={handleSave}
                className="bg-blue-500 hover:bg-blue-600 text-white"
              >
                <Save className="w-4 h-4 mr-2" />
                保存配额
              </Button>
            </>
          ) : (
            <>
              <Button
                variant="outline"
                size="sm"
                onClick={() => toast.success('已刷新资源使用情况')}
                className="dark:border-gray-600 dark:hover:bg-gray-700 dark:text-gray-300"
              >
                <RefreshCw className="w-4 h-4 mr-2" />
                刷新
              </Button>
              <Button
                size="sm"
                onClick={handleEditClick}
                className="bg-blue-500 hover:bg-blue-600 text-white"
              >
                <Edit className="w-4 h-4 mr-2" />
                编辑配额
              </Button>
            </>
          )}
        </div>
      </div>

      {/* 统计卡片 */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {/* 配额资源 */}
        <Card className="relative overflow-hidden dark:bg-gray-800 dark:border-gray-700">
          <div className="absolute top-0 right-0 w-32 h-32 opacity-10">
            <div className="w-full h-full bg-blue-500 rounded-full blur-3xl transform translate-x-8 -translate-y-8"></div>
          </div>
          <div className="p-6 relative">
            <div className="flex items-center justify-between mb-4">
              <div className="p-3 rounded-xl bg-blue-500/10">
                <Package className="w-6 h-6 text-blue-500" />
              </div>
            </div>
            <div className="text-3xl mb-2 text-blue-600 dark:text-blue-400">
              {stats.totalResources}
            </div>
            <div className="text-sm text-gray-600 dark:text-gray-400 mb-1">
              配额资源
            </div>
            <div className="text-xs text-gray-500 dark:text-gray-500">
              系统配置的资源类型
            </div>
          </div>
        </Card>

        {/* 配额应用 */}
        <Card className="relative overflow-hidden dark:bg-gray-800 dark:border-gray-700">
          <div className="absolute top-0 right-0 w-32 h-32 opacity-10">
            <div className="w-full h-full bg-green-500 rounded-full blur-3xl transform translate-x-8 -translate-y-8"></div>
          </div>
          <div className="p-6 relative">
            <div className="flex items-center justify-between mb-4">
              <div className="p-3 rounded-xl bg-green-500/10">
                <Activity className="w-6 h-6 text-green-500" />
              </div>
            </div>
            <div className="text-3xl mb-2 text-green-600 dark:text-green-400">
              {stats.appliedQuotas}
            </div>
            <div className="text-sm text-gray-600 dark:text-gray-400 mb-1">
              配额应用
            </div>
            <div className="text-xs text-gray-500 dark:text-gray-500">
              已应用配额限制的资源
            </div>
          </div>
        </Card>

        {/* 配额不足 */}
        <Card className="relative overflow-hidden dark:bg-gray-800 dark:border-gray-700">
          <div className="absolute top-0 right-0 w-32 h-32 opacity-10">
            <div className="w-full h-full bg-red-500 rounded-full blur-3xl transform translate-x-8 -translate-y-8"></div>
          </div>
          <div className="p-6 relative">
            <div className="flex items-center justify-between mb-4">
              <div className="p-3 rounded-xl bg-red-500/10">
                <AlertTriangle className="w-6 h-6 text-red-500" />
              </div>
            </div>
            <div className="text-3xl mb-2 text-red-600 dark:text-red-400">
              {stats.insufficientQuotas}
            </div>
            <div className="text-sm text-gray-600 dark:text-gray-400 mb-1">
              配额不足
            </div>
            <div className="text-xs text-gray-500 dark:text-gray-500">
              使用率超过90%的资源
            </div>
          </div>
        </Card>
      </div>

      {/* 配额列表 */}
      <Card className="dark:bg-gray-800 dark:border-gray-700">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="border-b dark:border-gray-700">
              <tr>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">资源名称</th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">资源编码</th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">类别</th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">说明</th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">已用/限额</th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">使用率</th>
                <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400 w-[200px]">进度</th>
                {isEditing && (
                  <th className="text-left p-4 text-sm text-gray-600 dark:text-gray-400">配额设置</th>
                )}
              </tr>
            </thead>
            <tbody>
              {quotas.map((quota) => {
                const Icon = quota.icon;
                const percentage = getUsagePercentage(quota.used, quota.limit);
                const displayUsed = quota.id === 'apiCalls' ? formatNumber(quota.used) : quota.used;
                const displayLimit = quota.id === 'apiCalls' ? formatNumber(quota.limit) : quota.limit;

                return (
                  <tr 
                    key={quota.id} 
                    className="border-b dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-700/50"
                  >
                    <td className="p-4">
                      <div className="flex items-center gap-3">
                        <div className={`w-10 h-10 ${quota.iconBg}/10 rounded-lg flex items-center justify-center`}>
                          <Icon className={`w-5 h-5 ${quota.iconColor}`} />
                        </div>
                        <div>
                          <div className="dark:text-white">{quota.name}</div>
                        </div>
                      </div>
                    </td>
                    <td className="p-4">
                      <span className="text-sm text-gray-600 dark:text-gray-400">
                        {quota.code}
                      </span>
                    </td>
                    <td className="p-4">
                      <Badge variant="secondary" className="dark:bg-gray-700 dark:text-gray-300 border-0">
                        {quota.category}
                      </Badge>
                    </td>
                    <td className="p-4">
                      <span className="text-sm text-gray-600 dark:text-gray-400">
                        {quota.description}
                      </span>
                    </td>
                    <td className="p-4">
                      <span className={`text-sm ${getUsageColor(percentage)}`}>
                        {displayUsed} / {displayLimit} {quota.unit}
                      </span>
                    </td>
                    <td className="p-4">
                      <span className={`text-sm ${getUsageColor(percentage)}`}>
                        {percentage.toFixed(1)}%
                      </span>
                    </td>
                    <td className="p-4">
                      <div className="space-y-1">
                        <Progress 
                          value={percentage}
                          className={`h-2 ${getProgressColor(percentage)}`}
                        />
                        {percentage >= 90 && (
                          <div className="text-xs text-red-600 dark:text-red-400 flex items-center gap-1">
                            <AlertTriangle className="w-3 h-3" />
                            配额不足
                          </div>
                        )}
                        {percentage >= 75 && percentage < 90 && (
                          <div className="text-xs text-orange-600 dark:text-orange-400 flex items-center gap-1">
                            <TrendingUp className="w-3 h-3" />
                            使用率较高
                          </div>
                        )}
                      </div>
                    </td>
                    {isEditing && (
                      <td className="p-4">
                        <div className="flex items-center gap-2 max-w-[200px]">
                          <Input
                            type="number"
                            value={editFormData[quota.id] || quota.limit}
                            onChange={(e) => updateFormData(quota.id, parseInt(e.target.value) || 0)}
                            className="dark:bg-gray-900 dark:border-gray-700"
                          />
                          <span className="text-sm text-gray-500 dark:text-gray-400 whitespace-nowrap">
                            {quota.unit}
                          </span>
                        </div>
                      </td>
                    )}
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </Card>
    </div>
  );
}